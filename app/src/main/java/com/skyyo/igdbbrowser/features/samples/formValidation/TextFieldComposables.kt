package com.skyyo.igdbbrowser.features.samples.formValidation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.application.models.local.Input
import com.skyyo.igdbbrowser.theme.Shapes
import com.skyyo.igdbbrowser.utils.OnKeyActionDone
import com.skyyo.igdbbrowser.utils.OnKeyActionNext
import com.skyyo.igdbbrowser.utils.OnValueChange
import com.skyyo.igdbbrowser.utils.creditCardFilter


@Composable
fun NewTextField(input: Input, onValueChange: OnValueChange, onKeyActionNext: OnKeyActionNext) {
    TextField(
        value = input.value,
        onValueChange = { onValueChange(it) },
        label = { Text(if (input.errorId == null) "Name" else stringResource(id = input.errorId)) },
        isError = input.errorId != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onKeyActionNext() }),
        maxLines = 1,
        singleLine = true
    )
}

@Composable
fun CustomTextField(input: Input, onValueChange: OnValueChange, onKeyActionNext: OnKeyActionNext) {
    Box(
        Modifier
            .clip(Shapes.medium)
            .width(200.dp)
            .height(56.dp)
            .background(Color.Cyan)
    ) {
        BasicTextField(
            modifier = Modifier.align(Alignment.Center),
            value = input.value,
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
fun OldSchoolTextField(
    input: Input,
    onValueChange: OnValueChange,
    onKeyActionDone: OnKeyActionDone
) {
    Column {
        TextField(
            value = input.value,
            onValueChange = { onValueChange(it) },
            label = { Text(text = stringResource(R.string.credit_card_number)) },
            isError = input.errorId != null,
            visualTransformation = ::creditCardFilter,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onKeyActionDone() }),
            maxLines = 1,
            singleLine = true
        )
        if (input.errorId != null) {
            Text(
                text = stringResource(input.errorId),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
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