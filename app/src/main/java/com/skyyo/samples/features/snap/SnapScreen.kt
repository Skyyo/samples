package com.skyyo.samples.features.snap

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.skyyo.samples.R
import dev.chrisbanes.snapper.*
import kotlin.math.max
import kotlin.math.min

private const val FAST_FLING_THRESHOLD = 9
private const val MAX_FLING_DISTANCE = 15

@Suppress("MagicNumber")
@Composable
fun SnapScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        SnapTo1LazyRow()

        var snapItemsCount by remember { mutableStateOf(5) }
        TextField(
            value = snapItemsCount.toString(), onValueChange = {
                snapItemsCount =
                    if (it.isEmpty()) 5 else it.toInt()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
        SnapToNLazyRow(snapItemsCount)
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SnapTo1LazyRow() {
    val listState = rememberLazyListState()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        state = listState,
        flingBehavior = rememberSnapperFlingBehavior(
            lazyListState = listState,
            snapOffsetForItem = SnapOffsets.Start
        )
    ) {
        items(
            count = 50,
            { it }
        ) { SnapItem(it, snapItemsCount = 1) }
    }
}

@Composable
fun SnapItem(position: Int, snapItemsCount: Int) {
    Box(Modifier.border(1.dp, color = if (position % snapItemsCount == 0) Color.Blue else Color.Red)) {
        Icon(
            painter = painterResource(id = R.drawable.common_full_open_on_phone),
            contentDescription = "",
            modifier = Modifier.size(150.dp, 100.dp),
            tint = if (position % snapItemsCount == 0) Color.Blue else Color.Red
        )

        Text("$position", Modifier.align(Alignment.Center))
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SnapToNLazyRow(snapItemsCount: Int) {
    val listState = rememberLazyListState()
    val contentPadding = PaddingValues(10.dp)

    LazyRow(
        modifier = Modifier.padding(top = 10.dp),
        state = listState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = rememberSnapperFlingBehavior(
            lazyListState = listState,
            endContentPadding = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
            snapOffsetForItem = SnapOffsets.Start,
            snapIndex = { layout, startIndex, targetIndex ->
                run {
                    val totalItemsCount = layout.totalItemsCount
                    var indexOfFirstItemInGroup = targetIndex.firstItemInGroup(totalItemsCount, snapItemsCount)
                    if (startIndex - targetIndex > MAX_FLING_DISTANCE) {
                        indexOfFirstItemInGroup = (startIndex - MAX_FLING_DISTANCE) / snapItemsCount * snapItemsCount
                    } else if (targetIndex - startIndex > MAX_FLING_DISTANCE) {
                        indexOfFirstItemInGroup = (startIndex + MAX_FLING_DISTANCE) / snapItemsCount * snapItemsCount
                    }
                    val flingForward = targetIndex > startIndex
                    if (flingForward && targetIndex - startIndex > FAST_FLING_THRESHOLD) {
                        return@run indexOfFirstItemInGroup
                    }
                    if (!flingForward && startIndex - targetIndex > FAST_FLING_THRESHOLD) {
                        return@run min(indexOfFirstItemInGroup + snapItemsCount, totalItemsCount - 1)
                    }

                    val indexOfFirstItemOfNextGroup = indexOfFirstItemInGroup + snapItemsCount

                    when {
                        targetIndex - indexOfFirstItemInGroup < indexOfFirstItemOfNextGroup - targetIndex -> indexOfFirstItemInGroup
                        else -> min(indexOfFirstItemOfNextGroup, totalItemsCount - 1)
                    }
                }
            },
        ),
    ) {
        items(count = 50, { it }) { SnapItem(it, snapItemsCount) }
    }
}

private fun Int.firstItemInGroup(totalItemsCount: Int, itemsInGroup: Int): Int {
    return min(max(this, 0) / itemsInGroup, totalItemsCount / itemsInGroup) * itemsInGroup
}
