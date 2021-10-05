package com.skyyo.samples.features.scrollAnimation1

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.skyyo.samples.features.scrollAnimation1.composables.ExpandableToolbar
import com.skyyo.samples.features.scrollAnimation1.composables.ScrollingContent

const val TOOLBAR_EXPANDED = 0
const val TOOLBAR_COLLAPSED = 1
const val EXPANDED_TOOLBAR_HEIGHT = 400
const val COLLAPSED_TOOLBAR_HEIGHT = 64
const val PADDING = 16

@Composable
fun ScrollAnimation1Screen(viewModel: ScrollAnimation1ViewModel = hiltViewModel()) {
    val insets = LocalWindowInsets.current
    val navigationBarHeight = with(LocalDensity.current) { insets.navigationBars.bottom.toDp() }
    val toolbarState = remember { mutableStateOf(TOOLBAR_EXPANDED) }
    val toolbarHeightPx = with(LocalDensity.current) {
        EXPANDED_TOOLBAR_HEIGHT.dp.roundToPx().toFloat()
    }
    val collapsedToolbarHeightPx = with(LocalDensity.current) {
        COLLAPSED_TOOLBAR_HEIGHT.dp.roundToPx().toFloat()
    }
    val scrollState = rememberScrollState()
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val finalTopOffset =
                    toolbarHeightPx - collapsedToolbarHeightPx - insets.statusBars.top
                val newOffset = toolbarOffsetHeightPx.value + available.y
                val delta = newOffset.coerceIn(-finalTopOffset, 0f)
                when {
                    scrollState.value.toFloat() > finalTopOffset -> {
                        toolbarState.value = TOOLBAR_COLLAPSED
                        toolbarOffsetHeightPx.value = -finalTopOffset
                    }
                    else -> {
                        toolbarState.value = TOOLBAR_EXPANDED
                        toolbarOffsetHeightPx.value = delta
                    }
                }
                return Offset.Zero
            }
        }
    }

    val text by viewModel.text.observeAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        val maxWidth = maxWidth.value
        ScrollingContent(
            navigationBarHeight = navigationBarHeight,
            scrollState = scrollState,
            historyTextList = text!!
        )
        ExpandableToolbar(
            toolbarState = toolbarState.value,
            maxWidth = maxWidth,
            toolbarOffsetHeightPx = toolbarOffsetHeightPx.value
        )
    }

}

