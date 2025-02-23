package com.thangoghd.cakeotv.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thangoghd.cakeotv.ui.viewmodel.NavigationViewModel
import androidx.compose.animation.core.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val screens = Screen.getScreens()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        modifier = modifier
    ) {
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == screen.route
            } == true
            NavigationBarItem(
                label = {
                    Text(text = screen.getLabel())
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.getLabel()
                    )
                },
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        navigationViewModel.onScreenReselected(screen)
                    } else {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationRail(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val screens = Screen.getScreens()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination
    val isRefreshing by navigationViewModel.isRefreshing.collectAsState()

    // Animation for icon when refreshing
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.snapTo(0f)
        }
    }

    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == screen.route
            } == true

            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(screen.labelResId),
                        modifier = if (isRefreshing) {
                            Modifier.graphicsLayer { rotationZ = rotation.value }
                        } else Modifier
                    )
                },
                label = { Text(stringResource(screen.labelResId)) },
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        navigationViewModel.onScreenReselected(screen)
                    } else {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
