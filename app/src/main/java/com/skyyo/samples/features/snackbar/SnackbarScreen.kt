package com.skyyo.samples.features.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skyyo.samples.extensions.log
import kotlinx.coroutines.launch

@Composable
fun SnackbarScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Button(onClick = { coroutineScope.launch { showSnackbar(snackbarHostState) } }) {
            Text(text = "show snack")
        }
        SnackbarHost(
            // this allows to customize the snackbar appearance
            snackbar = {
                Box(
                    Modifier
                        .size(106.dp)
                        .background(Color.Yellow)
                ) {
                    Button(onClick = { snackbarHostState.currentSnackbarData?.performAction() }) {
                        Text(text = "dismiss")
                    }
                }
            },
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

suspend fun showSnackbar(snackbarHostState: SnackbarHostState) {
    val result = snackbarHostState.showSnackbar(
        message = "Snackbar ",
        actionLabel = "Action"
    )
    when (result) {
        SnackbarResult.ActionPerformed -> log("ActionPerformed")
        SnackbarResult.Dismissed -> log("Dismissed")
    }
}
