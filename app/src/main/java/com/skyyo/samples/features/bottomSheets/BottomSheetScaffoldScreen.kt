package com.skyyo.samples.features.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

/**
 * can be used both as modal & persistent sheet,
 * just not fit for destinations
 */
@ExperimentalMaterialApi
@Composable
fun BottomSheetScaffoldScreen() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()

    val currentFraction = scaffoldState.currentFraction
    val cornerRadius = (30 * currentFraction).dp
    val targetValue = scaffoldState.bottomSheetState.targetValue
    val currentValue = scaffoldState.bottomSheetState.currentValue

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.8f)
                    .statusBarsPadding()
                    .background(MaterialTheme.colors.primary)
                    .graphicsLayer(alpha = currentFraction),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("fraction = $currentFraction")
                Text("target = $targetValue")
                Text("current = $currentValue")
                Button(onClick = {
                    coroutineScope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                }) {
                    Text(text = "Expand/Collapse Bottom Sheet")
                }
            }
        },
        sheetPeekHeight = 86.dp
    ) {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Gray))
    }
}

@OptIn(ExperimentalMaterialApi::class)
val BottomSheetScaffoldState.currentFraction: Float
    get() {
        val fraction = bottomSheetState.progress.fraction
        val targetValue = bottomSheetState.targetValue
        val currentValue = bottomSheetState.currentValue

        return when {
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed -> 0f
            currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded -> 1f
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Expanded -> fraction
            else -> 1f - fraction
        }
    }
