package com.skyyo.samples.features.marqueeText

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MarqueeTextScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center
    ) {
        MarqueeText(
            text = "Brush twenty oz of ground beef in a handfull pounds of mint sauce.",
            textEndsSpacing = SPACING_NONE
        )
        MarqueeText(
            text = "How scurvy. You command like an ale. Aw, yer not vandalizing me without a courage!",
            textEndsSpacing = SPACING_MEDIUM,
            scrollSpeed = 3000,
            scrollDirection = ScrollDirection.Backward
        )
        MarqueeText(
            text = "How scurvy. You command like an ale. Aw, yer not vandalizing me without a courage!",
            scrollDirection = ScrollDirection.Forward
        )
    }
}
