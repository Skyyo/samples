package com.skyyo.samples.features.autofill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutoFillScreen() {
    val email = remember { mutableStateOf("") }
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val creditCard = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.autofill(
                value = email.value,
                onFill = { email.value = it },
                AutofillType.EmailAddress
            ),
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
        )

        OutlinedTextField(
            modifier = Modifier.autofill(
                value = firstName.value,
                onFill = { firstName.value = it },
                AutofillType.PersonFirstName,
            ),
            value = firstName.value,
            onValueChange = { firstName.value = it },
            label = { Text(text = "First name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),

            )
        OutlinedTextField(
            modifier = Modifier.autofill(
                value = lastName.value,
                onFill = { lastName.value = it },
                AutofillType.PersonLastName,
            ),
            value = lastName.value,
            onValueChange = { lastName.value = it },
            label = { Text(text = "Last name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
        )
        OutlinedTextField(
            modifier = Modifier.autofill(
                value = phoneNumber.value,
                onFill = { phoneNumber.value = it },
                AutofillType.PhoneNumber,
            ),
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            label = { Text(text = "Phone number") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone
            ),
        )
        OutlinedTextField(
            modifier = Modifier.autofill(
                value = creditCard.value,
                onFill = { creditCard.value = it },
                AutofillType.CreditCardNumber,
            ),
            value = creditCard.value,
            onValueChange = { creditCard.value = it },
            label = { Text(text = "Credit card") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
    }
}
