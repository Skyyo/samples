package com.skyyo.samples.features.bottomSheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetScreen() {
    val list = arrayListOf<Int>()
    repeat(times = 200) { list += it }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        contentPadding = WindowInsets.systemBars.asPaddingValues()
    ) {
        itemsIndexed(list) { index, _ ->
            Text(text = "Bottom Sheet Screen $index")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
