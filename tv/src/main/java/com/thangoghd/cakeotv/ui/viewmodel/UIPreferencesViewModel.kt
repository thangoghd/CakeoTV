package com.thangoghd.cakeotv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thangoghd.cakeotv.data.repository.PreferencesRepository
import com.thangoghd.cakeotv.ui.model.UIMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UIPreferencesState(
    val uiMode: UIMode = UIMode.TV,
    val isFirstLaunch: Boolean = true
)

@HiltViewModel
class UIPreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIPreferencesState())
    val uiState: StateFlow<UIPreferencesState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.getUIMode().collect { mode ->
                _uiState.value = _uiState.value.copy(
                    uiMode = mode,
                    isFirstLaunch = preferencesRepository.isFirstLaunch()
                )
            }
        }
    }

    fun setUIMode(mode: UIMode) {
        viewModelScope.launch {
            preferencesRepository.setUIMode(mode)
            preferencesRepository.setFirstLaunch(false)
            _uiState.value = _uiState.value.copy(
                uiMode = mode,
                isFirstLaunch = false
            )
        }
    }
}
