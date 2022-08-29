package com.skyyo.samples.features.textSpans

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyyo.samples.features.textSpans.composables.*

private const val CORNER_RADIUS_VALUE = 20f

// done using https://saket.me/compose-custom-text-spans/ and https://github.com/saket/ExtendedSpans
@OptIn(ExperimentalTextApi::class)
@Composable
fun TextSpansScreen() {
    var isCorrect by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .systemBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = remember { Arrangement.spacedBy(20.dp) }
    ) {
        val text = remember {
            buildAnnotatedString {
                append("Where is the proud pirate? ")
                withAnnotation("squiggles", annotation = "ignored") {
                    withStyle(SpanStyle(color = Color.Magenta)) {
                        append("The rough shipmate fiery fights the dagger.")
                    }
                }
                append(" Belay! Pieces o' yellow fever are forever dead.")
            }
        }
        SquiggleUnderlineText(text = text, fontSize = 24.sp, lineHeight = 30.sp)
        AnimatedSquiggleUnderlineText(text = text, fontSize = 24.sp, lineHeight = 30.sp)
        AnimatedSquiggleWavelengthUnderlineText(
            text = text,
            isCorrect = isCorrect,
            fontSize = 24.sp,
            lineHeight = 30.sp
        )
        Button(onClick = { isCorrect = !isCorrect }) {
            Text(text = if (isCorrect) "Incorrect" else "Correct")
        }
        HighlightedText(
            text = text,
            cornerRadius = remember { CornerRadius(CORNER_RADIUS_VALUE, CORNER_RADIUS_VALUE) },
            highlightColor = Color.Cyan,
            highlightBorderColor = Color.DarkGray,
            padding = remember { SimplePaddingValues(horizontal = 2.dp, vertical = 2.dp) },
            fontSize = 24.sp,
            lineHeight = 30.sp
        )
        UnderlinedText(text = text, fontSize = 24.sp, lineHeight = 30.sp)
    }
}
