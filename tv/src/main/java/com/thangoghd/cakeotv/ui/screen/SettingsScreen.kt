package com.thangoghd.cakeotv.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thangoghd.cakeotv.R
import com.thangoghd.cakeotv.ui.model.UIMode
import com.thangoghd.cakeotv.ui.viewmodel.MainViewModel
import com.thangoghd.cakeotv.ui.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiMode: UIMode = UIMode.TV,
    viewModel: MainViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeState by themeViewModel.themeState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Interface Mode Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.interface_mode),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setUIMode(UIMode.TV) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.uiMode == UIMode.TV) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (uiState.uiMode == UIMode.TV)
                                    MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(text = stringResource(R.string.android_tv))
                        }

                        Button(
                            onClick = { viewModel.setUIMode(UIMode.MOBILE) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.uiMode == UIMode.MOBILE) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (uiState.uiMode == UIMode.MOBILE)
                                    MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(text = stringResource(R.string.phone))
                        }
                    }
                }
            }
        }

        // Theme Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.theme),
                        style = MaterialTheme.typography.titleLarge
                    )

                    // System Theme Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(R.string.use_system_theme))
                        Switch(
                            checked = themeState.isSystemTheme,
                            onCheckedChange = { themeViewModel.setSystemTheme(it) }
                        )
                    }

                    // Dark Mode Switch (only visible if not using system theme)
                    if (!themeState.isSystemTheme) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dark_mode),
                            )
                            Switch(
                                checked = themeState.isDarkMode,
                                onCheckedChange = { themeViewModel.setDarkMode(it) }
                            )
                        }
                    }
                }
            }
        }

        // App Information Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.interface_mode),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.version),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stringResource(R.string.build),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
