package com.skyyo.samples.features.oneTap

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.google.android.gms.auth.api.identity.Identity
import com.skyyo.samples.extensions.toast
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
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

    val user = viewModel.user
    val client = remember { Identity.getSignInClient(context) }
    Column(
        modifier = Modifier.systemBarsPadding(),
        verticalArrangement = remember { Arrangement.spacedBy(20.dp) }
    ) {
        LabeledText(label = "User name: ", text = user.name)
        LabeledText(label = "User surname: ", text = user.surname)
        LabeledText(label = "User phone: ", text = user.phone)
        Button(onClick = { viewModel.signOut(client) }) {
            Text(text = "sign out")
        }
        Button(onClick = { viewModel.signOut(client = client, deleteUser = true) }) {
            Text(text = "sign out with backend cleaning up")
        }
    }
}

@Composable
private fun LabeledText(label: String, text: String) {
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
        Text(modifier = Modifier.padding(start = 10.dp), text = text, style = textStyle)
    }
}
