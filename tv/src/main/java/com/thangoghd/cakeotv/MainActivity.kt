package com.thangoghd.cakeotv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thangoghd.cakeotv.ui.components.BottomBar
import com.thangoghd.cakeotv.ui.components.NavigationRail
import com.thangoghd.cakeotv.ui.components.ResponsiveLayout
import com.thangoghd.cakeotv.ui.model.UIMode
import com.thangoghd.cakeotv.ui.components.Screen
import com.thangoghd.cakeotv.ui.screen.*
import com.thangoghd.cakeotv.ui.theme.CakeoTVTheme
import com.thangoghd.cakeotv.ui.viewmodel.MainViewModel
import com.thangoghd.cakeotv.ui.viewmodel.SplashViewModel
import com.thangoghd.cakeotv.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.app.AlertDialog
import android.widget.Toast
import androidx.activity.OnBackPressedCallback

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Xác nhận thoát")
                    .setMessage("Bạn có muốn thoát ứng dụng không?")
                    .setPositiveButton("Có") { _, _ ->
                        finish()
                    }
                    .setNegativeButton("Không") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        })

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val splashViewModel: SplashViewModel = hiltViewModel()
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val themeState by themeViewModel.themeState.collectAsState()
            var showSplash by remember { mutableStateOf(true) }
            val navController = rememberNavController()

            val isDarkTheme = if (themeState.isSystemTheme) {
                isSystemInDarkTheme()
            } else {
                themeState.isDarkMode
            }

            CakeoTVTheme(
                darkTheme = isDarkTheme
            ) {
                if (showSplash) {
                    SplashScreen(
                        onSplashComplete = {
                            showSplash = false
                        }
                    )
                } else if (uiState.isFirstLaunch) {
                    SetupScreen(
                        onSetupComplete = {
                            viewModel.setFirstLaunch(false)
                        }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            if (uiState.uiMode == UIMode.MOBILE) {
                                BottomBar(navController = navController)
                            }
                        }
                    ) { paddingValues ->
                        ResponsiveLayout(
                            uiMode = uiState.uiMode,
                            navigationRail = {
                                if (uiState.uiMode == UIMode.TV) {
                                    NavigationRail(navController = navController)
                                }
                            }
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Live.route,
                                modifier = Modifier.padding(paddingValues)
                            ) {
                                composable(Screen.Live.route) {
                                    LiveScreen(uiMode = uiState.uiMode)
                                }
                                composable(Screen.Highlight.route) {
                                    HighlightScreen(uiMode = uiState.uiMode)
                                }
                                composable(Screen.Replay.route) {
                                    ReplayScreen(uiMode = uiState.uiMode)
                                }
                                composable(Screen.Settings.route) {
                                    SettingsScreen(uiMode = uiState.uiMode)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
