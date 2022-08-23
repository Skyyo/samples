package com.skyyo.samples.features.scrollAnimation1.composables

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyyo.samples.features.scrollAnimation1.EXPANDED_TOOLBAR_HEIGHT
import com.skyyo.samples.features.scrollAnimation1.PADDING
import com.skyyo.samples.theme.Shapes

@Composable
fun ScrollingContent(
    navigationBarHeight: Dp,
    scrollState: ScrollState,
    historyTextList: MutableList<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(
                top = EXPANDED_TOOLBAR_HEIGHT.dp + PADDING.dp,
                start = PADDING.dp,
                end = PADDING.dp,
                bottom = navigationBarHeight + PADDING.dp
            )
            .background(color = Color.White, shape = Shapes.medium)
    ) {
        historyTextList.forEachIndexed { index, description ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING.dp),
                lineHeight = 24.sp,
                text = description,
                color = Color.Black,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp
            )
            if (index == historyTextList.size - 1) return@forEachIndexed
            Divider(
                modifier = Modifier
                    .width(160.dp)
                    .padding(vertical = PADDING.dp)
                    .background(color = Color.Black)
                    .align(Alignment.CenterHorizontally),
                thickness = 4.dp
            )
        }
    }
}
