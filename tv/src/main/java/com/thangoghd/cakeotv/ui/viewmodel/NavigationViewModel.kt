package com.thangoghd.cakeotv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thangoghd.cakeotv.data.repository.NavigationRepository
import com.thangoghd.cakeotv.ui.components.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationRepository: NavigationRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun onScreenReselected(screen: Screen) {
        viewModelScope.launch {
            _isRefreshing.value = true
            when (screen) {
                is Screen.Live -> navigationRepository.emitRefreshEvent(Screen.Live.route)
                is Screen.Highlight -> {
                    // TODO: Implement Highlight reselection
                    navigationRepository.emitRefreshEvent(Screen.Highlight.route)
                }
                is Screen.Replay -> {
                    // TODO: Implement Replay reselection
                    navigationRepository.emitRefreshEvent(Screen.Replay.route)
                }
                is Screen.Settings -> {
                    // TODO: Implement Settings reselection
                    navigationRepository.emitRefreshEvent(Screen.Settings.route)
                }
            }
            _isRefreshing.value = false
        }
    }
}
