package com.skyyo.samples.features.pagination.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.White
import com.skyyo.samples.utils.OnClick

@Composable
fun FadingFab(
    modifier: Modifier = Modifier,
    isListScrolled: Boolean,
    onclick: OnClick
) {
    AnimatedVisibility(
        enter = fadeIn(),
        exit = fadeOut(),
        visible = isListScrolled,
        modifier = modifier

    ) {
        FloatingActionButton(
            onClick = onclick,
            modifier = Modifier.size(48.dp),
            backgroundColor = DarkGray
        ) {
            Icon(Icons.Filled.ArrowUpward, contentDescription = null, tint = White)
        }
    }
}
