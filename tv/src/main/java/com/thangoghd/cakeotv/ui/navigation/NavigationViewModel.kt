package com.thangoghd.cakeotv.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _currentRoute = MutableStateFlow(Screen.Live.route)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    fun navigate(route: String) {
        _currentRoute.value = route
    }
}
