package com.thangoghd.cakeotv.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.thangoghd.cakeotv.data.model.Match
import com.thangoghd.cakeotv.ui.components.MatchCard
import com.thangoghd.cakeotv.ui.components.ErrorMessage
import com.thangoghd.cakeotv.ui.components.LoadingIndicator
import com.thangoghd.cakeotv.ui.model.UIMode
import com.thangoghd.cakeotv.ui.viewmodel.LiveMatchViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LiveScreen(
    viewModel: LiveMatchViewModel = hiltViewModel(),
    uiMode: UIMode = UIMode.TV,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val columns = when (uiMode) {
        UIMode.TV -> GridCells.Fixed(3)
        UIMode.MOBILE -> GridCells.Fixed(1)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }
            uiState.error != null -> {
                ErrorMessage(message = uiState.error!!)
            }
            else -> {
                val sortedMatches = uiState.matches.sortedWith(
                    compareByDescending<Match> { it.isFeatured }
                        .thenByDescending { it.status == 1 }
                )

                LazyVerticalGrid(
                    columns = columns,
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedMatches) { match ->
                        MatchCard(
                            match = match,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}
