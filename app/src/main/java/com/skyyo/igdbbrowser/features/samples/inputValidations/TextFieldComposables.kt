package com.skyyo.igdbbrowser.features.samples.inputValidations

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.skyyo.igdbbrowser.theme.Shapes
import com.skyyo.igdbbrowser.utils.OnKeyActionNext
import com.skyyo.igdbbrowser.utils.OnValueChange

@Composable
fun CustomDecoratedTextField(
    value: String,
    onValueChange: OnValueChange,
    onKeyActionNext: OnKeyActionNext
) {
    val color = remember { mutableStateOf(Yellow) }
    Box(
        Modifier
            .clip(Shapes.medium)
            .width(200.dp)
            .height(56.dp)
            .background(Color.LightGray)
            .onFocusEvent { color.value = (if (it.isFocused) White else Gray) },
    ) {
        BasicTextField(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.DarkGray),
            value = value,
            textStyle = TextStyle(color = color.value),
            onValueChange = { onValueChange(it) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { onKeyActionNext() }),
            maxLines = 1,
            singleLine = true
        )
    }
}

@Composable
fun TextValidationScreensTitle() {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Blue)) {
                append("Form")
            }
            append(" Validation")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red)) {
                append(" Sample")
            }
        }
    )
}

@Composable
fun CustomTextField(
    modifier: Modifier,
    inputWrapper: InputWrapper,
    @StringRes labelResId: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: OnValueChange,
    onImeKeyAction: OnImeKeyAction
) {
    val fieldValue = remember {
        mutableStateOf(TextFieldValue(inputWrapper.value, TextRange(inputWrapper.value.length)))
    }
    Column {
        TextField(
            modifier = modifier,
            value = fieldValue.value,
            onValueChange = {
                fieldValue.value = it
                onValueChange(it.text)
            },
            label = { Text(stringResource(labelResId)) },
            isError = inputWrapper.errorId != null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onAny = { onImeKeyAction() }),
        )
        if (inputWrapper.errorId != null) {
            Text(
                text = stringResource(inputWrapper.errorId),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun CustomTextField2(
    modifier: Modifier,
    value: String,
    errorId: Int?,
    @StringRes labelResId: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: OnValueChange,
    onImeKeyAction: OnImeKeyAction
) {
    val fieldValue = remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    Column {
        TextField(
            modifier = modifier,
            value = fieldValue.value,
            onValueChange = {
                fieldValue.value = it
                onValueChange(it.text)
            },
            label = { Text(stringResource(labelResId)) },
            isError = errorId != null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onAny = { onImeKeyAction() }),
        )
        if (errorId != null) {
            Text(
                text = stringResource(errorId),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}