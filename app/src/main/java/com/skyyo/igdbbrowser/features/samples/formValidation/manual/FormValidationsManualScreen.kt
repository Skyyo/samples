package com.skyyo.igdbbrowser.features.samples.formValidation.manual

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.skyyo.igdbbrowser.extensions.toast
import com.skyyo.igdbbrowser.features.samples.formValidation.CustomTextField
import com.skyyo.igdbbrowser.features.samples.formValidation.NewTextField
import com.skyyo.igdbbrowser.features.samples.formValidation.OldSchoolTextField
import com.skyyo.igdbbrowser.features.samples.formValidation.TextValidationScreensTitle
import com.skyyo.igdbbrowser.features.samples.formValidation.realTime.FormValidationsRealTimeEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FormValidationsManualScreen(viewModel: FormValidationManualViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }

    val name by viewModel.name.collectAsState()
    val password by viewModel.password.collectAsState()
    val creditCardNumber by viewModel.creditCardNumber.collectAsState()

    fun moveFocusDown() = focusManager.moveFocus(FocusDirection.Down)

    LaunchedEffect(Unit) {
        launch {
            events.collect { event ->
                when (event) {
                    is FormValidationsRealTimeEvent.ShowToast -> context.toast(event.messageId)
                    is FormValidationsRealTimeEvent.UpdateKeyboard -> {
                        if (event.show) focusRequester.requestFocus() else keyboardController?.hide()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextValidationScreensTitle()
        Spacer(Modifier.height(16.dp))
        NewTextField(
            modifier = Modifier.focusRequester(focusRequester),
            input = name,
            onValueChange = viewModel::onNameEntered,
            onKeyActionNext = ::moveFocusDown
        )
        Spacer(Modifier.height(16.dp))
        CustomTextField(
            input = password,
            onValueChange = viewModel::onPasswordEntered,
            onKeyActionNext = ::moveFocusDown
        )
        Spacer(Modifier.height(16.dp))
        OldSchoolTextField(
            input = creditCardNumber,
            onValueChange = viewModel::onCardNumberEntered,
            onKeyActionDone = viewModel::onSignUpClick
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = viewModel::onSignUpClick) {
            Text(text = "Continue")
        }
    }
}