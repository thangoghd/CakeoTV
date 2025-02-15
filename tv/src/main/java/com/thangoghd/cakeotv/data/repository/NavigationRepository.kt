package com.thangoghd.cakeotv.data.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepository @Inject constructor() {
    private val _refreshEvents = MutableSharedFlow<String>()
    val refreshEvents: SharedFlow<String> = _refreshEvents.asSharedFlow()

    suspend fun emitRefreshEvent(route: String) {
        _refreshEvents.emit(route)
    }
}
