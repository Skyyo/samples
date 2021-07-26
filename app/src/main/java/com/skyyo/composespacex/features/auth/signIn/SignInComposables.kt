package com.skyyo.composespacex.features.auth.signIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun AuthScreen(viewModel: SignInViewModel = hiltViewModel()) {
    Column {
        Text(text = "Auth Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "press to imitate sign in")
        Button(onClick = viewModel::onBtnSignInClick) { Text(text = "Sign In") }
    }

}