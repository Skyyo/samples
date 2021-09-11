package com.skyyo.igdbbrowser.features.samples.nestedHorizontalList

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.*
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

@Composable
fun AppBarElevation() {
    val scrollState = rememberLazyListState()
    val items = remember { mutableListOf<Int>().apply { repeat(100) { add(it) } } }
    val transition = updateTransition(targetState = scrollState, label = "elevation")
    val elevation by transition.animateDp(label = "elevation") { state ->
        if (state.firstVisibleItemScrollOffset != 0) 16.dp else 0.dp
    }
    Column {
        TopAppBar(
            elevation = elevation,
            backgroundColor = Color.White,
        ) {
            Text(text = "TestAppBarElevation")
        }
        LazyColumnLazyRow(items, scrollState)
    }
}

@Composable
private fun LazyColumnLazyRow(items: List<Int>, state: LazyListState) {
    LazyColumn(state = state, content = {
        itemsIndexed(items) { _, num ->
            LazyRowItems(row = num, items)
        }
    })
}

@Composable
private fun LazyRowItems(row: Int, items: List<Int>) {
    LazyRow(content = {
        itemsIndexed(items) { _, item ->
            TestItem(row = row, column = item)
        }
    })
}

@Composable
private fun TestItem(row: Int, column: Int) {
    Text(
        text = "row $row column $column",
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .padding(10.dp)
            .shadow(1.dp),
    )
}
