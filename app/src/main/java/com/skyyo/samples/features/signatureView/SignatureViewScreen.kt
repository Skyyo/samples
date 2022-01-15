package com.skyyo.samples.features.signatureView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

enum class SignatureViewEvent { Save, Reset }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignatureViewScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val signatureEvents = remember { Channel<SignatureViewEvent>(Channel.UNLIMITED) }
    val signatureEventsFlow = remember(signatureEvents, lifecycleOwner) {
        signatureEvents
            .receiveAsFlow()
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center
    ) {
        SignatureView(
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
                .align(CenterHorizontally)
                .background(Color.White),
            events = signatureEventsFlow,
            onBitmapSaved = {

            }
        )
        Row(
            Modifier
                .fillMaxWidth()
                .align(CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { signatureEvents.trySend(SignatureViewEvent.Save) }) {
                Text(text = "Save")
            }
            Button(onClick = { signatureEvents.trySend(SignatureViewEvent.Reset) }) {
                Text(text = "Reset")
            }
        }
    }
}


