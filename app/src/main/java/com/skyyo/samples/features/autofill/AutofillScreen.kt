package com.skyyo.samples.features.autofill

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Suppress("LongMethod")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutofillScreen() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val email = remember { mutableStateOf("") }
    val creditCard = remember { mutableStateOf("") }
    val firstName = remember { mutableStateOf("") }

    val email2 = remember { mutableStateOf(TextFieldValue()) }
    val creditCard2 = remember { mutableStateOf(TextFieldValue()) }
    val firstName2 = remember { mutableStateOf(TextFieldValue()) }

    val email2InteractionSource = remember { MutableInteractionSource() }
    val creditCard2InteractionSource = remember { MutableInteractionSource() }
    val firstName2InteractionSource = remember { MutableInteractionSource() }

    val numberKeyboardOptions = remember {
        KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number
        )
    }
    val textKeyboardOptions = remember {
        KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text
        )
    }
    val creditCardAutofillTypes = remember {
        listOf(
            AutofillType.CreditCardNumber,
            AutofillType.CreditCardExpirationYear,
            AutofillType.CreditCardExpirationMonth,
            AutofillType.CreditCardExpirationDay,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "OutlinedTextField",
            textAlign = TextAlign.Center
        )
        AutoFilledTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email.value,
            onValueChange = {
                email.value = it
            },
            onFill = {
                email.value = it
            },
            autofillTypes = remember { listOf(AutofillType.EmailAddress) },
            keyboardOptions = textKeyboardOptions,
            label = { Text(text = "Email") }
        )
        AutoFilledTextField(
            modifier = Modifier.fillMaxWidth(),
            value = creditCard.value,
            onValueChange = {
                creditCard.value = it
            },
            onFill = {
                creditCard.value = it
            },
            autofillTypes = creditCardAutofillTypes,
            keyboardOptions = numberKeyboardOptions,
            label = { Text(text = "Credit Card") }
        )
        AutoFilledTextField(
            modifier = Modifier.fillMaxWidth(),
            value = firstName.value,
            onValueChange = {
                firstName.value = it
            },
            onFill = {
                firstName.value = it
            },
            autofillTypes = remember { listOf(AutofillType.PersonFirstName) },
            keyboardOptions = textKeyboardOptions,
            label = { Text(text = "First name") }
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(modifier = Modifier.fillMaxWidth(), text = "Modifier", textAlign = TextAlign.Center)
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .autofill(
                    value = email2.value.text,
                    onFill = {
                        email2.value = TextFieldValue(text = it, selection = TextRange(it.length))
                    },
                    autofillTypes = remember { listOf(AutofillType.EmailAddress) },
                    interactionSource = email2InteractionSource
                ),
            value = email2.value,
            onValueChange = { email2.value = it },
            label = { Text(text = "Email") },
            keyboardOptions = textKeyboardOptions,
            interactionSource = email2InteractionSource
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .autofill(
                    value = creditCard2.value.text,
                    onFill = {
                        creditCard2.value = TextFieldValue(
                            text = it,
                            selection = TextRange(it.length)
                        )
                    },
                    autofillTypes = creditCardAutofillTypes,
                    interactionSource = creditCard2InteractionSource
                ),
            value = creditCard2.value,
            onValueChange = { creditCard2.value = it },
            label = { Text(text = "Credit card") },
            keyboardOptions = numberKeyboardOptions,
            interactionSource = creditCard2InteractionSource
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .autofill(
                    value = firstName2.value.text,
                    onFill = {
                        firstName2.value = TextFieldValue(
                            text = it,
                            selection = TextRange(it.length)
                        )
                    },
                    autofillTypes = remember { listOf(AutofillType.PersonFirstName) },
                    interactionSource = firstName2InteractionSource
                ),
            value = firstName2.value,
            onValueChange = { firstName2.value = it },
            label = { Text(text = "First name") },
            keyboardOptions = remember {
                KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                )
            },
            keyboardActions = remember {
                KeyboardActions(onDone = { keyboardController?.hide() })
            },
            interactionSource = firstName2InteractionSource
        )
    }
}
