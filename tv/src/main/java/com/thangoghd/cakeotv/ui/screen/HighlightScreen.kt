package com.thangoghd.cakeotv.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thangoghd.cakeotv.ui.model.UIMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightScreen(
    uiMode: UIMode = UIMode.TV,
    modifier: Modifier = Modifier
) {
    val columns = when (uiMode) {
        UIMode.TV -> GridCells.Fixed(2)
        UIMode.MOBILE -> GridCells.Fixed(1)
    }

    LazyVerticalGrid(
        columns = columns,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // TODO: Add items from HighlightViewModel
        items(10) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                onClick = { /* TODO: Navigate to highlight detail */ }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Highlight ${index + 1}")
                }
            }
        }
    }
}
