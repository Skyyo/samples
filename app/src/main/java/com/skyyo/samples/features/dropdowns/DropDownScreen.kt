package com.skyyo.samples.features.dropdowns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun DropdownScreen() {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("A", "B", "C", "D", "E", "F")
    val selectedIndex = remember { mutableStateOf(0) }
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { expanded = true }) {
                Text(items[selectedIndex.value])
            }
            //effect of Modifier.wrapContentSize(Alignment.TopStart) is unclear for this scenario
            Box {
                Button(onClick = { expanded = true }) {
                    Text(items[selectedIndex.value])
                }
                DropdownMenu(
                    properties = PopupProperties(dismissOnBackPress = true),
                    offset = DpOffset(x = 8.dp, y = 8.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.LightGray)
                ) {
                    items.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            selectedIndex.value = index
                            expanded = false
                        }) {
                            Text(text = s)
                        }
                    }
                }
            }
            Button(onClick = { expanded = true }) {
                Text(items[selectedIndex.value])
            }

        }
    }

}