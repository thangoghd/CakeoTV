package com.thangoghd.cakeotv.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.thangoghd.cakeotv.R


sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int
) {
    // Function that will be overridden by each screen to handle reselection
    open suspend fun onReselected() {}

    object Live : Screen(
        route = "live",
        icon = Icons.Default.LiveTv,
        labelResId = R.string.live
    ) {
        override suspend fun onReselected() {
            // Will be implemented by NavigationViewModel
        }
    }
    
    object Highlight : Screen(
        route = "highlight",
        icon = Icons.Default.OndemandVideo,
        labelResId = R.string.highlight
    ) {
        override suspend fun onReselected() {
            // Will be implemented by NavigationViewModel
        }
    }
    
    object Replay : Screen(
        route = "replay",
        icon = Icons.Default.VideoLibrary,
        labelResId = R.string.replay
    ) {
        override suspend fun onReselected() {
            // Will be implemented by NavigationViewModel
        }
    }
    
    object Settings : Screen(
        route = "settings",
        icon = Icons.Default.Settings,
        labelResId = R.string.settings
    ) {
        override suspend fun onReselected() {
            // Will be implemented by NavigationViewModel
        }
    }

    companion object {
        fun getScreens() = listOf(Live, Highlight, Replay, Settings)
    }
}

@Composable
fun Screen.getLabel(): String {
    return stringResource(id = labelResId)
}
