package com.skyyo.samples.features.navigationCores.bottomBar

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skyyo.samples.application.Destination

private const val EXPAND_ANIMATION_DURATION = 300
private const val COLLAPSE_ANIMATION_DURATION = 300
private const val FADE_IN_ANIMATION_DURATION = 350
private const val FADE_OUT_ANIMATION_DURATION = 300
val BOTTOM_BAR_HEIGHT = 56.dp

@Composable
fun AnimatedBottomBar(
    modifier: Modifier = Modifier,
    items: List<Destination>,
    selectedIndex: Int,
    isBottomBarVisible: Boolean,
    onTabClick: (index: Int, route: String) -> Unit
) {
    val density = LocalDensity.current
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = FADE_IN_ANIMATION_DURATION,
                easing = FastOutLinearInEasing
            )
        )
    }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = FADE_OUT_ANIMATION_DURATION,
                easing = LinearOutSlowInEasing
            )
        )
    }
    val enterSlide = remember {
        slideIn(
            animationSpec = tween(EXPAND_ANIMATION_DURATION),
            initialOffset = { with(density) { IntOffset(0, BOTTOM_BAR_HEIGHT.roundToPx()) } }
        )
    }
    val exitSlide = remember {
        slideOut(
            animationSpec = tween(COLLAPSE_ANIMATION_DURATION),
            targetOffset = { with(density) { IntOffset(0, BOTTOM_BAR_HEIGHT.roundToPx()) } }
        )
    }

    return AnimatedVisibility(
        modifier = modifier,
        visible = isBottomBarVisible,
        enter = enterSlide + enterFadeIn,
        exit = exitSlide + exitFadeOut,
    ) {
        BottomNavigation(
            backgroundColor = DarkGray,
            modifier = Modifier.windowInsetsBottomHeight(
                WindowInsets.navigationBars.add(WindowInsets(bottom = BOTTOM_BAR_HEIGHT))
            )
        ) {
            items.forEachIndexed { index, screen ->
                BottomNavigationItem(
                    modifier = Modifier.navigationBarsPadding(),
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text("Tab $index") },
                    selected = index == selectedIndex,
                    onClick = { onTabClick(index, screen.route) }
                )
            }
        }
    }
}
