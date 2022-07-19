package com.skyyo.samples.features.bottomSheets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun BottomSheetScreen() {
    val list = arrayListOf<Int>()
    repeat(times = 200) { list += it }

    LazyColumn(Modifier.systemBarsPadding()) {
        itemsIndexed(list) { index, _ ->
            Text(text = "Bottom Sheet Screen $index")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
