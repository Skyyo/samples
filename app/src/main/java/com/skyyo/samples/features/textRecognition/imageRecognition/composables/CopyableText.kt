package com.skyyo.samples.features.textRecognition.imageRecognition.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyyo.samples.extensions.toast

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CopyableText(text: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val annotatedTextString = remember(text) { AnnotatedString(text) }
    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {},
                    onLongClick = {
                        clipboardManager.setText(annotatedTextString)
                        context.toast("Text copied to clipboard!")
                    }
                )
                .padding(horizontal = 10.dp),
            text = text,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Black
        )
    }
}
