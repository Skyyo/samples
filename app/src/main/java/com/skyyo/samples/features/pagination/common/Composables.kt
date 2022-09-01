package com.skyyo.samples.features.pagination.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.Purple500
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.theme.White
import com.skyyo.samples.utils.OnClick

@Composable
fun CustomCard(catId: String) {
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
            Image(
                painter = rememberAsyncImagePainter("https://placekitten.com/g/200/300"),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .padding(horizontal = 8.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth
            )
            Text(catId, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

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
