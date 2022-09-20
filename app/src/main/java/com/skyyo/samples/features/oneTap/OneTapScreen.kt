package com.skyyo.samples.features.oneTap

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import com.google.android.gms.auth.api.identity.Identity
import com.skyyo.samples.extensions.toast
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await

@Composable
fun OneTapScreen(viewModel: OneTapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val events = remember {
        viewModel.events.receiveAsFlow().flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is OneTapEvent.ShowToast -> context.toast(event.message)
            }
        }
    }

    val signInClient = remember { Identity.getSignInClient(context) }
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        when (it.resultCode) {
            Activity.RESULT_OK -> viewModel.signInAccepted(signInClient, it.data)
            Activity.RESULT_CANCELED -> viewModel.signInRejected()
        }
    }
    val signUpLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        when (it.resultCode) {
            Activity.RESULT_OK -> viewModel.signUpAccepted(signInClient, it.data)
            Activity.RESULT_CANCELED -> viewModel.signUpRejected()
        }
    }
    val isOneTapUiRejected by viewModel.isOneTapUiRejected.collectAsState()

    LaunchedEffect(isOneTapUiRejected) {
        if (!isOneTapUiRejected) {
            try {
                val signInResult = signInClient.beginSignIn(viewModel.signInRequest).await()
                signInLauncher.launch(
                    IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
                )
            } catch (e: Exception) {
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                try {
                    val signUpResult = signInClient.beginSignIn(viewModel.signUpRequest).await()
                    signUpLauncher.launch(
                        IntentSenderRequest.Builder(signUpResult.pendingIntent.intentSender).build()
                    )
                } catch (e: Exception) {
                    // No Google Accounts found. Just continue presenting the signed-out UI.
                }
                // Log.d(TAG, e.localizedMessage)
            }
        }
    }

    Column(Modifier.fillMaxSize().systemBarsPadding()) {
        Text(text = "Unauthorised content")
    }
}
