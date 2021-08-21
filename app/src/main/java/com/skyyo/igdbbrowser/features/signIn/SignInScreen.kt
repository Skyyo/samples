package com.skyyo.igdbbrowser.features.signIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.google.accompanist.insets.systemBarsPadding
import com.skyyo.igdbbrowser.extensions.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(viewModel: SignInViewModel = hiltViewModel()) {
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

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
    ) {
        Text(text = "IGDB app")
        Button(onClick = viewModel::signIn) { Text(text = "Sign In") }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = viewModel::goMap) { Text(text = "google map") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = viewModel::goForceTheme) { Text(text = "force theme") }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Bottom sheets")
        Button(onClick = viewModel::goBottomSheetDestination) { Text(text = "destination (accompanist)") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = viewModel::goBottomSheetsContainer) { Text(text = "modal sheet (accompanist)") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = viewModel::goBottomSheetsScaffold) { Text(text = " modal & persistent (scaffold)") }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = viewModel::goViewPager) { Text(text = "view pager (accompanist)") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = viewModel::goNavWithResultSample) { Text(text = "navigate to/back with results") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = viewModel::goStickyHeaders) { Text(text = "sticky headers") }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Form validation")
        Button(onClick = viewModel::goInputRealTimeValidation) { Text(text = "real time validation") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = viewModel::goInputRealTimeValidation) { Text(text = "manual validation") }
        Spacer(modifier = Modifier.height(8.dp))
    }

}

const val THEME_LIGHT = "light"
const val THEME_DARK = "dark"
const val THEME_AUTO = "auto"