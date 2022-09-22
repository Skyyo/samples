package com.skyyo.samples.features.oneTap

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import com.skyyo.samples.extensions.toast
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun CreateUserScreen(viewModel: CreateUserViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val events = remember {
        viewModel.events.receiveAsFlow().flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is UserEvent.ShowToast -> context.toast(event.message)
            }
        }
    }

    val user by viewModel.user.collectAsState()
    Column(
        modifier = Modifier.systemBarsPadding(),
        verticalArrangement = remember { Arrangement.spacedBy(20.dp) }
    ) {
        LabeledTextField(label = "User name: ", text = user.name)
        LabeledTextField(label = "User surname: ", text = user.surname)
        LabeledTextField(
            label = "User phone: ",
            text = user.phone,
            onTextChanged = viewModel::setPhone
        )
        Button(onClick = viewModel::applyUpdate) {
            Text(text = "apply")
        }
    }
}

@Composable
private fun LabeledTextField(label: String, text: String, onTextChanged: (String) -> Unit = {}) {
    val labelStyle = remember {
        TextStyle(
            fontSize = 12.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.W300
        )
    }
    val textStyle = remember {
        TextStyle(
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.W600
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = labelStyle)
        TextField(
            modifier = Modifier.padding(start = 10.dp),
            value = text,
            textStyle = textStyle,
            onValueChange = onTextChanged
        )
    }
}
