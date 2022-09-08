package com.skyyo.samples.features.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private const val BOTTOM_SHEET_HEIGHT_FRACTION = .96f

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
        sheetShape = remember(cornerRadius) {
            RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(BOTTOM_SHEET_HEIGHT_FRACTION)
                    .padding(top = 18.dp)
                    .background(MaterialTheme.colors.primary)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            if (scaffoldState.bottomSheetState.isCollapsed) {
                                scaffoldState.bottomSheetState.expand()
                            } else {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                ) {
                    Text(text = "Expand/Collapse Bottom Sheet")
                }
                val list = arrayListOf<Int>()
                repeat(times = 200) { list += it }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(alpha = currentFraction),
                    contentPadding = WindowInsets.navigationBars.asPaddingValues()
                ) {
                    item {
                        Text("fraction = $currentFraction")
                        Text("target = $targetValue")
                        Text(
                            modifier = Modifier.padding(bottom = 20.dp),
                            text = "current = $currentValue"
                        )
                    }
                    itemsIndexed(list) { index, _ ->
                        Text(text = "Bottom Sheet Screen $index")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        },
        sheetPeekHeight = 120.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        )
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
