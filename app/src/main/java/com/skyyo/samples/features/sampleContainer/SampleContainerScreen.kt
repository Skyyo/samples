package com.skyyo.samples.features.sampleContainer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.systemBarsPadding


@Composable
fun SampleContainerScreen(viewModel: SampleContainerViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "pagination")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goPaginationSimple
        ) { Text(text = "simple") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goPaginationRoom
        ) { Text(text = "room") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goPaginationPaging
        ) { Text(text = "paging") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goPaginationPagingRoom
        ) { Text(text = "paging with room") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Bottom sheets")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goBottomSheetDestination
        ) { Text(text = "as destination") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goBottomSheetsContainer
        ) { Text(text = "modal ") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goBottomSheetsScaffold
        ) { Text(text = "persistent") }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Input validations")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goInputManualValidation
        ) { Text(text = "manual") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goInputAutoValidation
        ) { Text(text = "auto") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goInputDebounceValidation
        ) { Text(text = "debounce") }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Integrations")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goMap
        ) { Text(text = "google map") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goCameraX
        ) { Text(text = "camera x") }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goForceTheme
        ) { Text(text = "force theme") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goNestedHorizontalLists
        ) { Text(text = "app bar auto-elevation animation") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goViewPager
        ) { Text(text = "view pager") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goNavWithResultSample
        ) { Text(text = "navigate to/back with results") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goStickyHeaders
        ) { Text(text = "sticky headers") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goAnimations
        ) { Text(text = "animations") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goOtp
        ) { Text(text = "otp view") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goAutoScroll
        ) { Text(text = "auto scroll") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::goTable
        ) { Text(text = "table ") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = viewModel::goParallaxEffect) {
            Text(text = "parallax effect ")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = viewModel::goCustomView) {
            Text(
                text = "custom view", style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = Color.Red,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

}

const val THEME_LIGHT = "light"
const val THEME_DARK = "dark"
const val THEME_AUTO = "auto"