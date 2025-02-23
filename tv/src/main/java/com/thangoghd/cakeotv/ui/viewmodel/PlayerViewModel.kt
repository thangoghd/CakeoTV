package com.thangoghd.cakeotv.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.thangoghd.cakeotv.data.model.PlayUrl
import com.thangoghd.cakeotv.data.remote.CakeoApi
import com.thangoghd.cakeotv.data.repository.PreferencesRepository
import com.thangoghd.cakeotv.service.MediaPlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import androidx.core.os.HandlerCompat
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Executor
import javax.inject.Inject
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

data class PlayerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUrl: PlayUrl? = null,
    val availableQualities: List<PlayUrl> = emptyList(),
    val isBackgroundPlaybackEnabled: Boolean = false,
    val isPictureInPictureEnabled: Boolean = false,
    val player: Player? = null,
    val currentPosition: Long = 0,
    val isPlaying: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val cakeoApi: CakeoApi,
    private val preferencesRepository: PreferencesRepository,

    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var retryCount = 0
    private val maxRetries = 3
    private val retryDelayMs = 1000L // 1 second delay between retries
    private val TAG = "MediaPlaybackPlayerViewModel"

    private var mediaController: MediaController? = null

    init {
        // Observe app lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                // App went to background
                if (!uiState.value.isBackgroundPlaybackEnabled) {
                    // If background playback is not enabled, stop playback
                    stopAndReleasePlayer()
                }
            }
        })

        viewModelScope.launch {
            preferencesRepository.getBackgroundPlayback().collect { enabled ->
                _uiState.update { it.copy(isBackgroundPlaybackEnabled = enabled) }
                if (enabled) {
                    initializeMediaSessionWithDelay(context)
                }
            }
        }
        viewModelScope.launch {
            preferencesRepository.getPictureInPicture().collect { enabled ->
                _uiState.update { it.copy(isPictureInPictureEnabled = enabled) }
            }
        }
    }

    fun initializeMediaSessionWithDelay(context: Context) {
        viewModelScope.launch {
            // Đợi một chút để service khởi động
            delay(500)
            initializeMediaSession(context)
        }
    }

    private fun initializeMediaSession(context: Context) {
        Log.d(TAG, "Initializing media session (attempt ${retryCount + 1}/$maxRetries)")
        try {
            // Release existing controller if any
            mediaController?.release()
            mediaController = null

            val sessionToken = SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
            val controllerFuture = MediaController.Builder(context, sessionToken)
                .setApplicationLooper(Looper.getMainLooper())
                .buildAsync()

            controllerFuture.addListener({
                try {
                    val controller = controllerFuture.get()
                    mediaController = controller
                    _uiState.update { it.copy(player = controller) }
                    Log.d(TAG, "Media controller connected successfully")
                    // Reset retry count on success
                    retryCount = 0
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get media controller", e)
                    if (retryCount < maxRetries) {
                        retryCount++
                        // Schedule retry after delay
                        viewModelScope.launch {
                            delay(retryDelayMs)
                            initializeMediaSession(context)
                        }
                    } else {
                        Log.e(TAG, "Failed to initialize media session after $maxRetries attempts")
                        _uiState.update { it.copy(error = "Failed to initialize media playback. Please try again.") }
                        retryCount = 0
                    }
                }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize media session", e)
            _uiState.update { it.copy(error = "Failed to initialize media playback: ${e.message}") }
        }
    }

    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val response = cakeoApi.getMatchMeta(matchId)
                if (response.status == 1 && response.data.fansites.isNotEmpty()) {
                    // Filter out FLV streams and get all available streams
                    val playUrls = response.data.fansites[0].playUrls.filter {
                        !it.url.endsWith(".flv", ignoreCase = true)
                    }
                    
                    if (playUrls.isNotEmpty()) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                availableQualities = playUrls,
                                selectedUrl = selectBestQualityUrl(playUrls)
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = "No compatible streams available"
                            )
                        }
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "No streams available"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun selectQuality(playUrl: PlayUrl) {
        _uiState.update { it.copy(selectedUrl = playUrl) }
    }

    private fun selectBestQualityUrl(playUrls: List<PlayUrl>): PlayUrl? {
        val fullHDQualityString: String = "FullHD"
        val hdQualityString: String = "HD"
        if (playUrls.isEmpty()) return null
        playUrls.find { it.name.contains(fullHDQualityString, ignoreCase = true) }?.let { return it }
        playUrls.find { it.name.contains(hdQualityString, ignoreCase = true) }?.let { return it }

        // If no HD streams found, return the first available stream
        return playUrls.first()
    }

    fun updatePlaybackState(position: Long, isPlaying: Boolean) {
        _uiState.update { 
            it.copy(
                currentPosition = position,
                isPlaying = isPlaying
            )
        }
    }

    fun stopAndReleasePlayer() {
        viewModelScope.launch {
            try {
                mediaController?.release()
                mediaController = null
                _uiState.update { it.copy(player = null) }
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping player", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaController?.release()
        mediaController = null
        stopAndReleasePlayer()
    }
}
