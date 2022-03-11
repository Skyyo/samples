package com.skyyo.samples.features.bringIntoViewRequester

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun BringIntoViewRequesterScreen(viewModel: BringIntoViewRequesterViewModel = hiltViewModel()) {
    val name by viewModel.name.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val scrollableState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollableState)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(6) { PlaceholderCard() }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .bringIntoViewRequester(bringIntoViewRequester),
            elevation = 8.dp
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Please fill something in"
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .onFocusEvent { focusState ->
                            if (focusState.isFocused) {
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        },
                    placeholder = { Text("Some input") },
                    value = name,
                    singleLine = true,
                    onValueChange = viewModel::onNameEntered,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = { }
                ) {
                    Text("Click")
                }
            }
        }
        repeat(10) { PlaceholderCard() }
    }
}

@Composable
private fun PlaceholderCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        elevation = 8.dp
    ) {
        Text(modifier = Modifier.padding(32.dp), text = "Placeholder")
    }
}
