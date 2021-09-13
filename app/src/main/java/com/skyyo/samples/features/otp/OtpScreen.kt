package com.skyyo.samples.features.otp

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.textfield.TextInputLayout
import android.widget.LinearLayout
import androidx.compose.ui.res.stringResource
import com.google.android.material.textfield.TextInputEditText


@Composable
fun OtpScreen(otpViewModel: OtpViewModel = hiltViewModel()) {
    val context = LocalContext.current

    //TODO get native views from activity to not bound to this composeable scope
    val textInputLayout1 = remember {
        createTextInputLayout(context).apply {
            editText?.doAfterTextChanged { otpViewModel.onOtp1Entered(it.toString()) }
        }
    }
    val otp1Input by otpViewModel.otp1.collectAsState()
    textInputLayout1.error = otp1Input.errorId?.let { stringResource(it) }

    val textInputLayout2 = remember {
        createTextInputLayout(context).apply {
            editText?.doAfterTextChanged { otpViewModel.onOtp2Entered(it.toString()) }
        }
    }
    val otp2Input by otpViewModel.otp2.collectAsState()
    val otp2Error = otp2Input.errorId?.let { stringResource(it) }

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
        AndroidView(factory = { textInputLayout1 }, modifier = Modifier.fillMaxWidth())
        AndroidView(factory = { textInputLayout2 }, modifier = Modifier.fillMaxWidth(), update = {
            it.error = otp2Error
        })

        Button(onClick = { }) {
            Text(text = "validate")
        }
    }
}

private fun createTextInputLayout(context: Context) = TextInputLayout(context).apply {
    val editText = TextInputEditText(context)

    val textInputLayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    addView(editText, textInputLayoutParams)
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