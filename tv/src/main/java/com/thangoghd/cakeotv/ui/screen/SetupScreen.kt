package com.thangoghd.cakeotv.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thangoghd.cakeotv.ui.model.UIMode
import com.thangoghd.cakeotv.ui.viewmodel.MainViewModel

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isFirstLaunch) {
        if (!uiState.isFirstLaunch) {
            onSetupComplete()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Welcome to CakeoTV",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Please select your preferred interface:",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.setUIMode(UIMode.TV) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Android TV")
                    }

                    Button(
                        onClick = { viewModel.setUIMode(UIMode.MOBILE) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mobile Phone")
                    }
                }

                // Error message
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = androidx.compose.ui.graphics.Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
