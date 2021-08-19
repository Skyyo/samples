package com.skyyo.igdbbrowser.features.samples.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.igdbbrowser.extensions.addVerticalScrollbar
import com.skyyo.igdbbrowser.theme.DarkGray
import com.skyyo.igdbbrowser.theme.Teal200

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListsScreen() {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .addVerticalScrollbar(listState),
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = true,
            applyBottom = false,
        )
    ) {
        stickyHeader {
            Card(backgroundColor = Teal200) {
                Text(
                    "sticky header 1", modifier = Modifier.statusBarsPadding()
                )
            }
        }
        items(10) {
            Card(backgroundColor = DarkGray) {
                Text("items")
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
        stickyHeader {
            Card(backgroundColor = Teal200) {
                Text("sticky header 2", modifier = Modifier.statusBarsPadding())
            }
        }
        items(10) {
            Card(backgroundColor = DarkGray) {
                Text("items")
                Spacer(modifier = Modifier.height(156.dp))
            }
        }
    }

}

