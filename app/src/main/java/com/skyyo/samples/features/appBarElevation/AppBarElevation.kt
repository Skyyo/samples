package com.skyyo.samples.features.appBarElevation

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

const val VERTICAL_GRID_CELLS_COUNT = 3

@Composable
fun AppBarElevation() {
    val scrollState = rememberLazyGridState()
    val items = remember { (0..200).map { it } }
    val transition = updateTransition(targetState = scrollState, label = "elevation")
    val elevation by transition.animateDp(label = "elevation") { state ->
        if (state.firstVisibleItemScrollOffset != 0) 16.dp else 0.dp
    }

    // we need Scaffold only because we're using cards with elevation. For some reason not
    // following the elevation priority is considered intended behaviour as described in this issue
    // https://issuetracker.google.com/issues/198313901
    Scaffold(
        topBar = {
            TopAppBar(
                elevation = elevation,
                backgroundColor = Color.White,
            ) {
                Text(text = "TestAppBarElevation")
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            columns = GridCells.Fixed(VERTICAL_GRID_CELLS_COUNT),
            state = scrollState,
            horizontalArrangement = remember {
                Arrangement.spacedBy(16.dp)
            },
            verticalArrangement = remember {
                Arrangement.spacedBy(8.dp)
            }
        ) {
            items(items) {
                Text(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(10.dp)
                        .shadow(1.dp),
                    text = "column $it",
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
