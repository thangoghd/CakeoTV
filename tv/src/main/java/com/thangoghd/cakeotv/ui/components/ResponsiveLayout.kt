package com.thangoghd.cakeotv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thangoghd.cakeotv.ui.model.UIMode

@Composable
fun ResponsiveLayout(
    uiMode: UIMode,
    navigationRail: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    when (uiMode) {
        UIMode.TV -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Navigation rail for TV mode
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        // .width(200.dp)
                        .padding(vertical = 5.dp)
                ) {
                    navigationRail()
                }

                // Main content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical =  5.dp)
                ) {
                    content()
                }
            }
        }
        UIMode.MOBILE -> {
            // For mobile, just show the content (bottom navigation is handled by Scaffold)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}
