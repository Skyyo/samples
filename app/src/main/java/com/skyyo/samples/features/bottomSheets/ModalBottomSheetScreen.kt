package com.skyyo.samples.features.bottomSheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ModalBottomSheetScreen() {
    val modalSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val list = arrayListOf<Int>()
    repeat(times = 200) { list += it }
    ModalBottomSheetLayout(
        sheetContent = {
            LazyColumn(Modifier.statusBarsPadding()) {
                itemsIndexed(list) { index, _ ->
                    Text(text = "Bottom Sheet Content $index")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        sheetState = modalSheetState,
    ) {
        Column(Modifier.statusBarsPadding()) {
            Text(text = "modal bottom sheet screen")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { coroutineScope.launch { modalSheetState.show() } }) { Text(text = "Modal sheet ") }
        }
    }
}
