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
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.skyyo.samples.R
import dev.chrisbanes.snapper.*

@Composable
fun SnapScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        SnapTo1LazyRow()

        var snapItemsCount by remember { mutableStateOf(5) }
        TextField(value = snapItemsCount.toString(), onValueChange = { snapItemsCount =
            if (it.isEmpty()) 5 else it.toInt() }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ))
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
        items(200, { it }) { SnapItem(it, 1) }
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
    LazyRow(
        modifier = Modifier.padding(top = 10.dp),
        state = listState,
        flingBehavior = rememberSnapperFlingBehavior(
            snapItemsCount = snapItemsCount,
            layoutInfo = rememberLazyListSnapperLayoutInfo(
                lazyListState = listState,
                snapItemsCount = snapItemsCount
            ),
        ),
    ) {
        items(50, { it }) { SnapItem(it, snapItemsCount) }
    }
}