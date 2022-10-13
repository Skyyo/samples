package com.skyyo.samples.features.viewPager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.utils.OnClick

@Composable
fun PagerCard(
    pageOffset: Float,
    page: Int,
    backgroundColor: Color,
    offset: Float,
    onClick: OnClick
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                // Calculate the absolute offset for the current page from the
                // scroll position. We use the absolute value which allows us to mirror
                // any effects for both directions
                // We animate the scaleX + scaleY, between 85% and 100%
                lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }
                // We animate the alpha, between 50% and 100%
                alpha = lerp(start = 0.5f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                // offset for snapping
                translationX = offset
            }
            .background(backgroundColor, shape = Shapes.large)
            .fillMaxSize(fraction = 0.3f)
            .height(256.dp)
    ) {
        Button(onClick = { onClick() }) {
            Text("scroll to page 0 ")
        }
        Text(text = "Page: $page")
    }
}
