package com.thangoghd.cakeotv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thangoghd.cakeotv.data.model.PlayUrl
import com.thangoghd.cakeotv.data.remote.CakeoApi
import com.thangoghd.cakeotv.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUrl: PlayUrl? = null,
    val availableQualities: List<PlayUrl> = emptyList(),
    val isBackgroundPlaybackEnabled: Boolean = false,
    val isPictureInPictureEnabled: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val cakeoApi: CakeoApi,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

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
        if (playUrls.isEmpty()) return null

        // Prioritize Full HD streams
        playUrls.find { it.name.contains("FullHD", ignoreCase = true) }?.let { return it }
        
        // Then look for HD streams
        playUrls.find { it.name.contains("HD", ignoreCase = true) }?.let { return it }
        
        // If no HD streams found, return the first available stream
        return playUrls.first()
    }
}
