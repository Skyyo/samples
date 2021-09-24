package com.skyyo.samples.features.scrollAnimation1.composables

import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyyo.samples.features.scrollAnimation1.EXPANDED_TOOLBAR_HEIGHT
import com.skyyo.samples.features.scrollAnimation1.PADDING
import com.skyyo.samples.features.scrollAnimation1.TOOLBAR_EXPANDED

@Composable
fun ToolbarTitle(
    modifier: Modifier,
    state: Int,
    maxWidth: Float
) {
    val expandedToolbarTitleHeight = 40.dp
    val collapsedToolbarTitleHeight = 32.dp
    val toolbarTitleWidth = 128.dp
    val toolbarSubTitleWidth = 296.dp
    val transitionDuration = 500
    val transition = updateTransition(targetState = state, label = "")
    val titleOffset by transition.animateOffset(
        transitionSpec = {
            tween(durationMillis = transitionDuration)
        },
        label = ""
    ) { toolbarState ->
        if (toolbarState == TOOLBAR_EXPANDED) {
            Offset(
                x = (maxWidth - toolbarTitleWidth.value) / 2 - PADDING,
                y = EXPANDED_TOOLBAR_HEIGHT - expandedToolbarTitleHeight.value - collapsedToolbarTitleHeight.value
            )
        } else {
            Offset(
                x = 0F,
                y = EXPANDED_TOOLBAR_HEIGHT - collapsedToolbarTitleHeight.value - PADDING
            )
        }
    }
    val subTitleOffset by transition.animateOffset(
        transitionSpec = {
            tween(durationMillis = transitionDuration)
        },
        label = ""
    ) { toolbarState ->
        val y = EXPANDED_TOOLBAR_HEIGHT - expandedToolbarTitleHeight.value -
            collapsedToolbarTitleHeight.value - PADDING
        if (toolbarState == TOOLBAR_EXPANDED) {
            Offset(x = (maxWidth - toolbarSubTitleWidth.value) / 2 - PADDING, y = y)
        } else {
            Offset(x = 0F, y = y)
        }
    }
    Column {
        Text(
            modifier = modifier
                .height(
                    provideToolbarTitleHeight(
                        state = state,
                        expandedToolbarTextHeight = expandedToolbarTitleHeight,
                        collapsedToolbarTextHeight = collapsedToolbarTitleHeight
                    )
                )
                .width(toolbarTitleWidth)
                .offset(titleOffset.x.dp, titleOffset.y.dp),
            text = provideToolbarTitle(state)
        )
        if (state == TOOLBAR_EXPANDED) {
            Text(
                modifier = modifier
                    .width(toolbarSubTitleWidth)
                    .offset(subTitleOffset.x.dp, subTitleOffset.y.dp),
                text = provideSubTitleText()
            )
        }
    }
}

private fun provideToolbarTitleHeight(
    state: Int,
    expandedToolbarTextHeight: Dp,
    collapsedToolbarTextHeight: Dp
): Dp {
    return if (state == TOOLBAR_EXPANDED) {
        expandedToolbarTextHeight
    } else {
        collapsedToolbarTextHeight
    }
}

@Composable
private fun provideToolbarTitle(state: Int): AnnotatedString {
    return if (state == TOOLBAR_EXPANDED) {
        provideExpandedTitleText()
    } else {
        provideCollapsedTitleText()
    }
}

@Composable
private fun provideExpandedTitleText() = buildAnnotatedString {
    withStyle(
        style = ParagraphStyle(
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )
    ) {
        withStyle(
            style = SpanStyle(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.W700
            )
        ) {
            append("Title")
        }
    }
}

@Composable
private fun provideSubTitleText() = buildAnnotatedString {
    withStyle(
        style = ParagraphStyle(
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )
    ) {
        withStyle(
            style = SpanStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )
        ) {
            append("Subtitle")
        }
    }
}

@Composable
private fun provideCollapsedTitleText() = buildAnnotatedString {
    withStyle(
        style = ParagraphStyle(
            lineHeight = 20.sp,
            textAlign = TextAlign.Start
        )
    ) {
        withStyle(
            style = SpanStyle(
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.W700
            )
        ) {
            append("Title")
        }
    }
}
