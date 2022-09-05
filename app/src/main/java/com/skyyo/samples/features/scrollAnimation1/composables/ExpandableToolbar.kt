package com.skyyo.samples.features.scrollAnimation1.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skyyo.samples.features.scrollAnimation1.EXPANDED_TOOLBAR_HEIGHT
import com.skyyo.samples.features.scrollAnimation1.PADDING
import com.skyyo.samples.features.scrollAnimation1.TOOLBAR_EXPANDED
import kotlin.math.roundToInt

@Composable
fun ExpandableToolbar(
    toolbarState: Int,
    maxWidth: Float,
    toolbarOffsetHeightPx: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(EXPANDED_TOOLBAR_HEIGHT.dp)
            .offset {
                IntOffset(
                    x = 0,
                    y = toolbarOffsetHeightPx.roundToInt()
                )
            }
    ) {
        ToolbarBackground(toolbarState)
        ToolbarTitle(
            modifier = Modifier
                .padding(
                    start = PADDING.dp,
                    end = PADDING.dp,
                    bottom = PADDING.dp
                ),
            state = toolbarState,
            maxWidth = maxWidth
        )
    }
}

@Composable
private fun ToolbarBackground(state: Int) {
    Crossfade(targetState = state) { toolbarState ->
        when (toolbarState) {
            TOOLBAR_EXPANDED -> {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = "https://cataas.com/cat",
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(elevation = 8.dp)
                        .background(color = Color.Cyan)
                )
            }
        }
    }
}
