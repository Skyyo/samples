package com.skyyo.samples.features.pagination.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.skyyo.samples.theme.Purple500
import com.skyyo.samples.theme.Shapes

@OptIn(ExperimentalCoilApi::class)
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
                painter = rememberImagePainter(
                    data = "https://placekitten.com/g/200/300",
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .padding(horizontal = 8.dp)
            )
            Text(catId, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}
