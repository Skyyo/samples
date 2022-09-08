package com.skyyo.samples.features.stickyHeaders

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.skyyo.samples.extensions.addVerticalScrollbar
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.Teal200

private const val SIZE_TRANSFORM_DURATION = 1300
private const val KEY_FRAME = 1500

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ListsScreen() {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .addVerticalScrollbar(listState),
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Vertical).asPaddingValues()
    ) {
        stickyHeader {
            Card(backgroundColor = Teal200) {
                Text(
                    "sticky header 1", modifier = Modifier.statusBarsPadding()
                )
            }
        }
        items(count = 50) {
            val transitionState = remember {
                MutableTransitionState(true).apply {
                    targetState = false
                }
            }
            val transition = updateTransition(transitionState, label = "transition")
            val offsetX by transition.animateDp({
                tween(durationMillis = 500)
            }, label = "offsetXTransition") {
            if (it) (-88).dp else 24.dp
        }
            Card(
                backgroundColor = DarkGray,
                modifier = Modifier.absoluteOffset(x = offsetX)
            ) {
                Text("items")
            }
            Spacer(modifier = Modifier.height(56.dp))
        }
        stickyHeader {
            Card(backgroundColor = Teal200) {
                Text("sticky header 2", modifier = Modifier.statusBarsPadding())
            }
        }
        items(count = 50) {
            var initialBoolean by remember { mutableStateOf(false) }
            SideEffect {
                initialBoolean = true
            }
            AnimatedContent(targetState = initialBoolean, transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = 500)) with
                    fadeOut(animationSpec = tween(durationMillis = 500)) using
                    SizeTransform { initialSize, targetSize ->
                        if (targetState) {
                            keyframes {
                                // Expand horizontally first.
                                IntSize(targetSize.width, initialSize.height) at KEY_FRAME
                                durationMillis = SIZE_TRANSFORM_DURATION
                            }
                        } else {
                            keyframes {
                                // Shrink vertically first.
                                IntSize(initialSize.width, targetSize.height) at KEY_FRAME
                                durationMillis = SIZE_TRANSFORM_DURATION
                            }
                        }
                    }
            }) { targetExpanded ->
                if (targetExpanded) {
                    Card(backgroundColor = DarkGray) {
                        Text("content 1")
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                } else {
                    Card(backgroundColor = DarkGray) {
                        Text("content 2")
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
            }
        }
    }
}
