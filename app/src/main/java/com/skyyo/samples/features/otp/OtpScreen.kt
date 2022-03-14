package com.skyyo.samples.features.otp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyyo.samples.features.inputValidations.InputWrapper
import com.skyyo.samples.features.inputValidations.OnValueChange
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.Error
import com.skyyo.samples.theme.Teal200
import com.skyyo.samples.utils.OnKeyActionDone

@Composable
fun OtpScreen(viewModel: OtpViewModel = hiltViewModel()) {
    val input by viewModel.input.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Otp view",
            modifier = Modifier.padding(16.dp)
        )
        Otp(
            Modifier.align(Alignment.CenterHorizontally),
            input = input,
            onOtpValueChange = viewModel::onOtpEntered,
            onImeKeyAction = viewModel::onBtnClick
        )
        Button(onClick = viewModel::onBtnClick) {
            Text(text = "validate")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Otp(
    modifier: Modifier = Modifier,
    otpLength: Int = OtpViewModel.OTP_LENGTH,
    input: InputWrapper,
    onOtpValueChange: OnValueChange,
    onImeKeyAction: OnKeyActionDone
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesters = remember {
        (0 until otpLength).map { FocusRequester() }
    }
    val codeArray = remember(input) {
        Array(otpLength) {
            val text = if (it < input.value.length) input.value[it].toString() else ""
            mutableStateOf(TextFieldValue(text = text, selection = TextRange(text.length)))
        }
    }

    Column(modifier) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row {
                repeat(otpLength) { index ->
                    if (index != 0) Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = codeArray[index].value,
                        onValueChange = { newTextFieldValue ->
                            val newText = newTextFieldValue.text
                            if (newText.isDigitsOnly()) {
                                codeArray[index].value = codeArray[index].value.copy(text = if (newText.isNotEmpty()) newText.last().toString() else newText)
                                if (codeArray[index].value.text != "") {
                                    val nextEmptyIndex = codeArray.indices.firstOrNull { codeArray[it].value.text == "" }
                                    val shiftCurrentSymbolToStart = nextEmptyIndex != null && nextEmptyIndex < index
                                    if (nextEmptyIndex != null) {
                                        val nextSymbolRequester = if (shiftCurrentSymbolToStart) {
                                            focusRequesters[nextEmptyIndex + 1]
                                        } else {
                                            focusRequesters[nextEmptyIndex]
                                        }
                                        nextSymbolRequester.requestFocus()
                                    }
                                }
                                onOtpValueChange(codeArray.joinToString(separator = "") { it.value.text })
                            }
                        },
                        modifier = Modifier
                            .size(48.dp, 56.dp)
                            .focusRequester(focusRequesters[index])
                            .onPreviewKeyEvent {
                                if (it.type == KeyEventType.KeyDown && it.key == Key.Backspace) {
                                    if (codeArray[index].value.text.isNotEmpty()) {
                                        codeArray[index].value =
                                            codeArray[index].value.copy(text = "")
                                        onOtpValueChange(codeArray.joinToString(separator = "") { it.value.text })
                                    }
                                    if (index != 0) focusRequesters[index - 1].requestFocus()
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
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onImeKeyAction()
                                keyboardController?.hide()
                            }
                        ),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = DarkGray,
                            textAlign = TextAlign.Center
                        ),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Teal200,
                            textColor = DarkGray,
                            cursorColor = DarkGray,
                            errorBorderColor = Error
                        ),
                        isError = input.errorId != null
                    )
                }
            }
        }
        if (input.errorId != null) {
            Text(
                text = stringResource(id = input.errorId),
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                color = Error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}