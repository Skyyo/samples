package com.skyyo.samples.features.lazyColumnWithTextFields

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.skyyo.samples.common.composables.ImeAwareLazyColumn

@Composable
fun ImeAwareLazyColumnScreen() {
    val texts = remember { Array(30) { it.toString() }.toList().toMutableStateList() }

    ImeAwareLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.asPaddingValues()
    ) {
        items(texts.size * 2) { index ->
            if (index % 2 == 1) {
                Text(text = "simple text")
            } else {
                TextField(value = texts[index / 2], onValueChange = { texts[index / 2] = it })
            }
        }
    }
}
