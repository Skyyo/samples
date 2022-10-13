package com.skyyo.samples.features.noticeableScrollableRow

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skyyo.samples.theme.Purple200
import com.skyyo.samples.theme.Teal200

@Composable
fun NoticeableScrollableRowScreen() {
    BoxWithConstraints(Modifier.statusBarsPadding()) {
        val oneItemWidth = maxWidth / 2.5f

        Column {
            NoticeableScrollableRow(countItems = 3) {
                Item(width = oneItemWidth)
            }
            Spacer(modifier = Modifier.height(10.dp))
            NoticeableScrollableRow(countItems = 3) {
                Item(width = oneItemWidth - 10.dp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            NoticeableScrollableRow(countItems = 3) {
                Item(width = oneItemWidth + 10.dp)
            }
        }
    }
}

@Composable
private fun Item(width: Dp) {
    Row(
        Modifier
            .width(width)
            .height(30.dp)
    ) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Teal200)
        )
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Purple200)
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Teal200)
        )
    }
}

// This composable expects:
// 1. At least two items
// 2. First item width + 1/2 * second item width to be less then screen width.
// It adds equal space between item composables to have as much item composables as possible
// to be shown at once on the screen, such as only half of last one to be shown.
@Composable
fun NoticeableScrollableRow(countItems: Int, item: @Composable (Int) -> Unit) {
    if (countItems < 2) return
    Layout(
        content = { (0 until countItems).map { item(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) { measurables, constraints ->
        val layoutWidth = constraints.minWidth
        val placeables = mutableListOf<Placeable>()
        var spaceBetweenItems = 0
        var accumulatedWidth = 0
        var spaceSet = false
        var measuredLayoutWidth = 0
        var measuredLayoutHeight = 0

        measurables.forEachIndexed { index, measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0))

            if (!spaceSet) {
                when {
                    // items perfectly fit into screen
                    accumulatedWidth + placeable.width / 2 == layoutWidth -> spaceSet = true
                    // half of item can't be placed fully inside layout,
                    // so calculate available space based on previous items
                    accumulatedWidth + placeable.width / 2 > layoutWidth -> {
                        spaceBetweenItems = (layoutWidth - accumulatedWidth + placeables.last().width / 2) / (index - 1)
                        spaceSet = true
                    }
                    // half of item can be inside layout, but whole item can't,
                    // so calculate available space, based on previous items + current item
                    accumulatedWidth + placeable.width / 2 < layoutWidth && accumulatedWidth + placeable.width > layoutWidth -> {
                        spaceBetweenItems = (layoutWidth - accumulatedWidth - placeable.width / 2) / index
                        spaceSet = true
                    }
                }
                accumulatedWidth += placeable.width
            }

            measuredLayoutHeight = maxOf(measuredLayoutHeight, placeable.height)
            placeables.add(placeable)
        }
        placeables.forEachIndexed { index, placeable ->
            if (index != 0) measuredLayoutWidth += spaceBetweenItems
            measuredLayoutWidth += placeable.width
        }

        layout(measuredLayoutWidth, measuredLayoutHeight) {
            var placeableOffset = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(placeableOffset, 0)
                placeableOffset += placeable.width + spaceBetweenItems
            }
        }
    }
}
