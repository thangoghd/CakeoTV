package com.thangoghd.cakeotv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import com.thangoghd.cakeotv.R
import com.thangoghd.cakeotv.ui.navigation.NavigationItem
import com.thangoghd.cakeotv.ui.navigation.NavigationViewModel
import com.thangoghd.cakeotv.ui.navigation.navigationItems
import com.thangoghd.cakeotv.ui.navigation.settingsItem
import com.thangoghd.cakeotv.ui.theme.mainColor

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NavigationBar(
    viewModel: NavigationViewModel,
    modifier: Modifier = Modifier
) {
    val currentRoute by viewModel.currentRoute.collectAsState()
    val focusRequesters = remember { 
        List(navigationItems.size + 1) { FocusRequester() } 
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side navigation items
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            navigationItems.forEachIndexed { index, item ->
                var isFocused by remember { mutableStateOf(false) }
                val isSelected = currentRoute == item.route

                NavigationButton(
                    item = item,
                    isSelected = isSelected,
                    isFocused = isFocused,
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged { 
                            isFocused = it.isFocused
//                            if (it.isFocused) {
//                                viewModel.navigate(item.route)
//                            }
                        },
                    onClick = { viewModel.navigate(item.route) }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        // Right side with logo and settings
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Settings button
            var isSettingsFocused by remember { mutableStateOf(false) }
            val isSettingsSelected = currentRoute == settingsItem.route

            NavigationButton(
                item = settingsItem,
                isSelected = isSettingsSelected,
                isFocused = isSettingsFocused,
                modifier = Modifier
                    .focusRequester(focusRequesters.last())
                    .onFocusChanged { 
                        isSettingsFocused = it.isFocused
//                        if (it.isFocused) {
//                            viewModel.navigate(settingsItem.route)
//                        }
                    },
                onClick = { viewModel.navigate(settingsItem.route) }
            )
        }
    }

    // Set initial focus to the first item
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun NavigationButton(
    item: NavigationItem,
    isSelected: Boolean,
    isFocused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.colors(
            containerColor = when {
                isSelected -> mainColor
                isFocused -> mainColor.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected || isFocused) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
