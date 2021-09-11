package com.skyyo.igdbbrowser.features.pagination.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.skyyo.igdbbrowser.theme.DarkGray
import com.skyyo.igdbbrowser.theme.Purple500
import com.skyyo.igdbbrowser.theme.Shapes
import com.skyyo.igdbbrowser.theme.White
import com.skyyo.igdbbrowser.utils.OnClick

@Composable
fun CustomCard(gameName: String) {
    Card(
        backgroundColor = Purple500,
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .padding(vertical = 8.dp),
        shape = Shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        ) {
            Image(painter = rememberImagePainter(
                data = "https://placekitten.com/g/200/300",
                builder = {
                    transformations(CircleCropTransformation())
                }
            ),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .padding(horizontal = 8.dp))
            Text(gameName, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
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