package com.thangoghd.cakeotv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.thangoghd.cakeotv.ui.viewmodel.LiveMatchViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContentGrid(
    viewModel: LiveMatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.error != null -> ErrorMessage(message = uiState.error!!)
        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(15.dp),
                horizontalArrangement = Arrangement.spacedBy(50.dp),
                verticalArrangement = Arrangement.spacedBy(50.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.matches) { match ->
                    MatchCard(
                        match = match,
                        onClick = { /* TODO: Handle match click */ }
                    )
                }
            }
        }
    }
}
