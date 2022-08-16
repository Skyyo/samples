package com.skyyo.samples.features.otp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.SolidColor
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyyo.samples.features.inputValidations.InputWrapper
import com.skyyo.samples.features.inputValidations.OnValueChange
import com.skyyo.samples.features.otp.composables.OtpCharTextField
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

@Suppress("LongMethod")
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
    var lastFocusedTextFieldIndex by remember { mutableStateOf(-1) }
    val borderColors = remember(lastFocusedTextFieldIndex, input.errorId) {
        (0 until otpLength).map { currentChar ->
            when {
                input.errorId != null -> Error
                currentChar == lastFocusedTextFieldIndex -> Teal200
                else -> DarkGray
            }
        }.toMutableList()
    }

    Column(modifier) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row {
                repeat(otpLength) { index ->
                    if (index != 0) Spacer(modifier = Modifier.width(12.dp))
                    val borderStroke =
                        remember(borderColors[index]) { BorderStroke(2.dp, borderColors[index]) }
                    val focusRequester = remember(focusRequesters, index) { focusRequesters[index] }
                    val previousFocusRequester = remember(focusRequesters, index) {
                        if (index != 0) focusRequesters[index - 1] else null
                    }
                    val nextFocusRequester = remember(focusRequesters, codeArray, index) {
                        val nextEmptyIndex =
                            codeArray.indices.firstOrNull { codeArray[it].value.text == "" }
                        val shiftCurrentSymbolToNextEmptyTextField =
                            nextEmptyIndex != null && nextEmptyIndex <= index && nextEmptyIndex + 1 < codeArray.size
                        when {
                            nextEmptyIndex == null -> null
                            shiftCurrentSymbolToNextEmptyTextField -> focusRequesters[nextEmptyIndex + 1]
                            else -> focusRequesters[nextEmptyIndex]
                        }
                    }

                    OtpCharTextField(
                        value = codeArray[index].value,
                        cursorBrushColor = remember { SolidColor(DarkGray) },
                        borderStroke = borderStroke,
                        onCharChanged = { currentChar ->
                            codeArray[index].value = codeArray[index].value.copy(text = currentChar)
                            val prevOrNextFocusRequester =
                                if (currentChar.isEmpty()) previousFocusRequester else nextFocusRequester
                            prevOrNextFocusRequester?.requestFocus()
                            onOtpValueChange(codeArray.joinToString(separator = "") { it.value.text })
                        },
                        keyboardOptions = remember {
                            KeyboardOptions(
                                autoCorrect = false,
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        },
                        keyboardActions = remember {
                            KeyboardActions(
                                onDone = {
                                    onImeKeyAction()
                                    keyboardController?.hide()
                                }
                            )
                        },
                        textStyle = remember {
                            TextStyle(
                                fontSize = 16.sp,
                                color = DarkGray,
                                textAlign = TextAlign.Center
                            )
                        },
                        focusRequester = focusRequester,
                        onFocused = { isFocused ->
                            if (isFocused) lastFocusedTextFieldIndex = index
                            borderColors[index] = getBorderColor(input, isFocused)
                        },
                        shape = remember { RoundedCornerShape(8.dp) },
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

private fun getBorderColor(
    input: InputWrapper,
    isFocused: Boolean
) = when {
    input.errorId != null -> Error
    isFocused -> Teal200
    else -> DarkGray
}
