package com.thangoghd.cakeotv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thangoghd.cakeotv.data.repository.PreferencesRepository
import com.thangoghd.cakeotv.ui.model.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _themeState = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.getDarkMode(),
                preferencesRepository.getSystemTheme()
            ) { isDarkMode, isSystemTheme ->
                ThemeState(isDarkMode, isSystemTheme)
            }.collect { state ->
                _themeState.value = state
            }
        }
    }

    fun setDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkMode(isDarkMode)
            preferencesRepository.setSystemTheme(false)
        }
    }

    fun setSystemTheme(useSystemTheme: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSystemTheme(useSystemTheme)
        }
    }
}
