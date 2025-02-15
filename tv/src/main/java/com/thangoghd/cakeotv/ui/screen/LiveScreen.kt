package com.thangoghd.cakeotv.ui.screen

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.thangoghd.cakeotv.data.model.Match
import com.thangoghd.cakeotv.ui.components.MatchCard
import com.thangoghd.cakeotv.ui.components.ErrorMessage
import com.thangoghd.cakeotv.ui.components.LoadingIndicator
import com.thangoghd.cakeotv.ui.model.UIMode
import com.thangoghd.cakeotv.PlayerActivity
import com.thangoghd.cakeotv.ui.viewmodel.LiveMatchViewModel

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LiveScreen(
    viewModel: LiveMatchViewModel = hiltViewModel(),
    uiMode: UIMode = UIMode.TV,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val refreshing by viewModel.isRefreshing.collectAsState()
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(refreshing, { viewModel.fetchLiveMatches() })

    val columns = when (uiMode) {
        UIMode.TV -> GridCells.Fixed(3)
        UIMode.MOBILE -> GridCells.Fixed(1)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .run {
                if (uiMode == UIMode.MOBILE) {
                    pullRefresh(pullRefreshState)
                } else {
                    this
                }
            }
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
                        .thenByDescending { it.timestamp }
                )

                LazyVerticalGrid(
                    columns = columns,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedMatches) { match ->
                        MatchCard(
                            match = match,
                            onClick = {
                                context.startActivity(
                                    PlayerActivity.createIntent(context as Activity, match.id)
                                )
                            }
                        )
                    }
                }
            }
        }
        
        if (uiMode == UIMode.MOBILE) {
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
