package com.skyyo.composespacex.features.signIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.skyyo.composespacex.extensions.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@Composable
fun AuthScreen(viewModel: SignInViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }

    //TODO try the old way + remember the channel?
//    val events2 = remember(viewModel.events, lifecycleOwner) {
//        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//
//        }
//    }

    LaunchedEffect(Unit) {
        launch {
            events.collect { event ->
                when (event) {
                    is SignInEvent.NetworkError -> context.toast("network error")
                    is SignInEvent.UpdateLoadingIndicator -> {
                    }
                }
            }
        }
    }

    Column {
        Text(text = "Auth Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "press to imitate sign in")
        Button(onClick = viewModel::onBtnSignInClick) { Text(text = "Sign In") }
    }

}