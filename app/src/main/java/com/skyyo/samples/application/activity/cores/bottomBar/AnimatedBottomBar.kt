package com.skyyo.samples.application.activity.cores.bottomBar

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.skyyo.samples.application.Screens
import com.skyyo.samples.theme.DarkGray

private const val EXPAND_ANIMATION_DURATION = 300
private const val COLLAPSE_ANIMATION_DURATION = 300
private const val FADE_IN_ANIMATION_DURATION = 350
private const val FADE_OUT_ANIMATION_DURATION = 300

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedBottomBar(
    items: List<Screens>,
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
            initialOffset = { with(density) { IntOffset(0, 56.dp.roundToPx()) } })
    }
    val exitSlide = remember {
        slideOut(
            animationSpec = tween(COLLAPSE_ANIMATION_DURATION),
            targetOffset = { with(density) { IntOffset(0, 56.dp.roundToPx()) } })
    }

    return AnimatedVisibility(
        visible = isBottomBarVisible,
        enter = enterSlide + enterFadeIn,
        exit = exitSlide + exitFadeOut,
    ) {
        BottomNavigation(
            backgroundColor = DarkGray,
            modifier = Modifier.navigationBarsHeight(56.dp)
        ) {
            items.forEachIndexed { index, screen ->
                BottomNavigationItem(
                    modifier = Modifier.navigationBarsPadding(),
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text(stringResource(screen.resourceId)) },
                    selected = index == selectedIndex,
                    onClick = { onTabClick(index, screen.route) }
                )
            }
        }
    }
}