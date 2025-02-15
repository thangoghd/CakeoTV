package com.thangoghd.cakeotv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thangoghd.cakeotv.data.model.Match
import com.thangoghd.cakeotv.data.model.Result
import com.thangoghd.cakeotv.data.repository.MatchRepository
import com.thangoghd.cakeotv.data.repository.NavigationRepository
import com.thangoghd.cakeotv.ui.components.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveMatchUiState(
    val isLoading: Boolean = false,
    val matches: List<Match> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LiveMatchViewModel @Inject constructor(
    private val repository: MatchRepository,
    private val navigationRepository: NavigationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveMatchUiState())
    val uiState: StateFlow<LiveMatchUiState> = _uiState.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        fetchLiveMatches()
        observeRefreshEvents()
    }

    private fun observeRefreshEvents() {
        viewModelScope.launch {
            navigationRepository.refreshEvents
                .filter { it == Screen.Live.route }
                .collect {
                    fetchLiveMatches()
                }
        }
    }

    fun onLiveTabReselected() {
        fetchLiveMatches()
    }

    fun fetchLiveMatches() {
        viewModelScope.launch {
            repository.getLiveMatches()
                .onStart { 
                    _uiState.value = LiveMatchUiState(isLoading = true)
                    _isRefreshing.value = true
                }
                .collect { result ->
                    _isRefreshing.value = false
                    _uiState.value = when (result) {
                        is Result.Success -> LiveMatchUiState(matches = result.data)
                        is Result.Error -> LiveMatchUiState(error = result.message)
                        is Result.Loading -> LiveMatchUiState(isLoading = true)
                    }
                }
        }
    }
}
