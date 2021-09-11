package com.skyyo.igdbbrowser.features.otp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OtpScreen(otpViewModel: OtpViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Otp view",
            modifier = Modifier.padding(16.dp)
        )
        //TODO optimize the otp since current implementation is bad
        // add viewModel + PD restoration. add customization options to
        // cover 90% of cases
        Otp(Modifier.align(Alignment.CenterHorizontally))
        Button(onClick = { }) {
            Text(text = "validate")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Otp(modifier: Modifier = Modifier) {
    val passcodeLength = 5
    val focusRequesters = remember {
        (0 until passcodeLength).map { FocusRequester() }
    }
    val digits = remember {
        mutableStateListOf(
            *((0 until passcodeLength).map { "" }.toTypedArray())
        )
    }
    Row(modifier = modifier) {
        for (i in 0 until passcodeLength) {
            TextField(
                value = digits[i],
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        digits[i] = it.lastOrNull()?.toString() ?: ""
                        if (digits[i].isBlank() && i > 0) {
                            focusRequesters[i - 1].requestFocus()
                        } else if (i < passcodeLength - 1) {
                            focusRequesters[i + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .padding(2.dp)
                    .width(50.dp)
                    .focusOrder(focusRequesters[i])
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyUp
                            && event.key == Key.Backspace
                            && digits[i].isEmpty()
                            && i > 0
                        ) {
                            focusRequesters[i - 1].requestFocus()
                            digits[i - 1] = ""
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                textStyle = TextStyle(fontSize = 24.sp)
            )
        }
    }
}