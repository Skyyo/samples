package com.skyyo.samples.features.autoscroll

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.delay

private const val DELAY_BETWEEN_SCROLL_MS = 8L
private const val SCROLL_DX = 1f

@Composable
fun AutoScrollScreen() {
    val lazyRowState = rememberLazyListState()
    val list = remember { (0..10).toList() }

    LaunchedEffect(Unit) {
        autoScroll(lazyRowState)
    }

    LazyRow(
        state = lazyRowState,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        infinityItems(list.size) { index ->
            Card(
                Modifier
                    .padding(8.dp)
                    .size(56.dp)
            ) { Text("Item $index") }
        }
    }
}

fun LazyListScope.infinityItems(
    count: Int,
    itemContent: @Composable LazyItemScope.(index: Int) -> Unit
) {
    items(Int.MAX_VALUE) { index ->
        itemContent(index % count)
    }
}

private tailrec suspend fun autoScroll(lazyListState: LazyListState) {
    // TODO add tracking if user touches the list, and always relaunch autoScroll when he's moving
    // the finger away
    lazyListState.scroll(MutatePriority.PreventUserInput) {
        scrollBy(SCROLL_DX)
    }
    delay(DELAY_BETWEEN_SCROLL_MS)

    autoScroll(lazyListState)
}
