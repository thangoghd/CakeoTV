package com.thangoghd.cakeotv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.thangoghd.cakeotv.data.model.PlayUrl
import com.thangoghd.cakeotv.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoPlayerState())
    val uiState: StateFlow<VideoPlayerState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.getBackgroundPlayback().collect { enabled ->
                _uiState.update { it.copy(isBackgroundPlaybackEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.getPictureInPicture().collect { enabled ->
                _uiState.update { it.copy(isPictureInPictureEnabled = enabled) }
            }
        }
    }

    fun setPlayUrls(playUrls: List<PlayUrl>) {
        val fullHDUrl = playUrls.find { it.name.equals("FullHD", ignoreCase = true) }
        val hdUrl = playUrls.find { it.name.contains("HD", ignoreCase = true) }
        val defaultUrl = playUrls.firstOrNull()

        val selectedUrl = fullHDUrl ?: hdUrl ?: defaultUrl

        _uiState.update {
            it.copy(
                playUrls = playUrls,
                currentPlayUrl = selectedUrl,
                availableQualities = playUrls.map { url -> url.name }
            )
        }
    }

    fun onQualitySelected(quality: String) {
        val newUrl = uiState.value.playUrls.find { it.name == quality }
        newUrl?.let { url ->
            _uiState.update { it.copy(currentPlayUrl = url) }
        }
    }

    fun updatePlaybackState(isPlaying: Boolean) {
        _uiState.update { it.copy(isPlaying = isPlaying) }
    }

    fun updateProgress(progress: Long, duration: Long) {
        _uiState.update {
            it.copy(
                currentProgress = progress,
                duration = duration
            )
        }
    }
}

data class VideoPlayerState(
    val playUrls: List<PlayUrl> = emptyList(),
    val currentPlayUrl: PlayUrl? = null,
    val isPlaying: Boolean = false,
    val currentProgress: Long = 0L,
    val duration: Long = 0L,
    val isBackgroundPlaybackEnabled: Boolean = true,
    val isPictureInPictureEnabled: Boolean = true,
    val availableQualities: List<String> = emptyList(),
    val error: String? = null
)