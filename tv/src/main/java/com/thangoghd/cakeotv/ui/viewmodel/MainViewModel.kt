package com.thangoghd.cakeotv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thangoghd.cakeotv.data.repository.PreferencesRepository
import com.thangoghd.cakeotv.ui.model.UIMode
import com.thangoghd.cakeotv.ui.model.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                preferencesRepository.getUIMode().collect { mode ->
                    _uiState.update { 
                        it.copy(
                            uiMode = mode,
                            isFirstLaunch = preferencesRepository.isFirstLaunch(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun setUIMode(mode: UIMode) {
        viewModelScope.launch {
            try {
                preferencesRepository.setUIMode(mode)
                preferencesRepository.setFirstLaunch(false)
                _uiState.update { 
                    it.copy(
                        uiMode = mode,
                        isFirstLaunch = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setFirstLaunch(isFirst: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setFirstLaunch(isFirst)
                _uiState.update { 
                    it.copy(isFirstLaunch = isFirst)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
