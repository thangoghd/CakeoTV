package com.thangoghd.cakeotv.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.thangoghd.cakeotv.R

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int
) {
    object Live : Screen(
        route = "live",
        icon = Icons.Default.LiveTv,
        labelResId = R.string.live
    )
    
    object Highlight : Screen(
        route = "highlight",
        icon = Icons.Default.Star,
        labelResId = R.string.highlight
    )
    
    object Replay : Screen(
        route = "replay",
        icon = Icons.Default.Replay,
        labelResId = R.string.replay
    )
    
    object Settings : Screen(
        route = "settings",
        icon = Icons.Default.Settings,
        labelResId = R.string.settings
    )

    companion object {
        fun getScreens() = listOf(Live, Highlight, Replay, Settings)
    }
}


@Composable
fun Screen.getLabel(): String {
    return stringResource(id = labelResId)
}
