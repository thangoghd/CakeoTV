package com.thangoghd.cakeotv.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Live : Screen(
        route = "live",
        icon = Icons.Default.LiveTv,
        label = "Live"
    )
    
    object Highlight : Screen(
        route = "highlight",
        icon = Icons.Default.Star,
        label = "Highlight"
    )
    
    object Replay : Screen(
        route = "replay",
        icon = Icons.Default.Replay,
        label = "Replay"
    )
    
    object Settings : Screen(
        route = "settings",
        icon = Icons.Default.Settings,
        label = "Settings"
    )

    companion object {
        fun getScreens() = listOf(Live, Highlight, Replay, Settings)
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
)

val navigationItems = listOf(
    NavigationItem(
        title = "Live",
        route = Screen.Live.route,
    ),
    NavigationItem(
        title = "Highlight",
        route = Screen.Highlight.route,
    ),
    NavigationItem(
        title = "Replay",
        route = Screen.Replay.route,
    )
)

val settingsItem = NavigationItem(
    title = "Settings",
    route = Screen.Settings.route,
)
