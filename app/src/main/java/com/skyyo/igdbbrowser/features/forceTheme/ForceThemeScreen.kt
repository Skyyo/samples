package com.skyyo.igdbbrowser.features.forceTheme

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.skyyo.igdbbrowser.application.activity.MainActivity
import com.skyyo.igdbbrowser.features.sampleContainer.THEME_AUTO
import com.skyyo.igdbbrowser.features.sampleContainer.THEME_DARK
import com.skyyo.igdbbrowser.features.sampleContainer.THEME_LIGHT
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * if we can't afford recreating activity, we can observe the theme
 * in the Theme composable, so it recomposes when user changes the value.
 * We should always use auto mode, manual control is an edge case.
 */
@Composable
fun ForceThemeScreen(viewModel: ForceThemeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    LaunchedEffect(Unit) {
        launch {
            events.collect { (context as MainActivity).recreate() }
        }

    }

    Column {
        Text(text = "Force Theme")
        Button(onClick = { viewModel.setAppTheme(THEME_LIGHT) }) {
            Text(text = "light")
        }
        Button(onClick = { viewModel.setAppTheme(THEME_DARK) }) {
            Text(text = "dark")
        }
        Button(onClick = { viewModel.setAppTheme(THEME_AUTO) }) {
            Text(text = "auto ")
        }
    }
}


