package com.skyyo.samples.features.lists

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.extensions.addVerticalScrollbar
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.Teal200

const val EXPAND_ANIMATION_DURATION = 300 * 2
const val COLLAPSE_ANIMATION_DURATION = 300 * 2
const val FADE_IN_ANIMATION_DURATION = 350
const val FADE_OUT_ANIMATION_DURATION = 300


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
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
        items(50) {
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
        items(50) {
            var initialBoolean by remember { mutableStateOf(false) }
            SideEffect {
                initialBoolean = true
            }
            AnimatedContent(targetState = initialBoolean, transitionSpec = {
                fadeIn(animationSpec = tween(500, 500)) with
                        fadeOut(animationSpec = tween(500)) using
                        SizeTransform { initialSize, targetSize ->
                            if (targetState) {
                                keyframes {
                                    // Expand horizontally first.
                                    IntSize(targetSize.width, initialSize.height) at 1500
                                    durationMillis = 1300
                                }
                            } else {
                                keyframes {
                                    // Shrink vertically first.
                                    IntSize(initialSize.width, targetSize.height) at 1500
                                    durationMillis = 1300
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

