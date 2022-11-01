package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAll
import com.skyyo.samples.utils.OnValueChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val WAITING_TIME_BETWEEN_DISMISS_REQUEST_AND_TAP_EVENT = 500L

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AutoCompleteDropdown(
    modifier: Modifier,
    scrollInteractionSource: InteractionSource,
    items: List<String>,
    query: String,
    isDropdownVisible: Boolean,
    onQueryChanged: OnValueChange,
    onItemSelected: OnValueChange,
) {
    var isDropdownTemporaryHidden by rememberSaveable { mutableStateOf(false) }
    val queryInteractionSource = remember { MutableInteractionSource() }
    val isFocused by queryInteractionSource.collectIsFocusedAsState()
    val isAutoCompleteDropdownVisible = remember(isDropdownVisible, isDropdownTemporaryHidden) {
        isDropdownVisible && !isDropdownTemporaryHidden
    }
    val coroutineScope = rememberCoroutineScope()
    val dismissDropdownJob = remember { Ref<Job>() }
    val textInputModifier: Modifier = remember {
        Modifier
            .fillMaxWidth()
            .whenTapped {
                // toggle dropdown visibility when user tapped on text field
                dismissDropdownJob.value?.cancel().also { dismissDropdownJob.value = null }
                isDropdownTemporaryHidden = !isDropdownTemporaryHidden
            }
    }
    val focusManager = LocalFocusManager.current
    val queryKeyboardActions = remember {
        KeyboardActions(onAny = { focusManager.moveFocus(FocusDirection.Next) })
    }

    if (isFocused) {
        LaunchedEffect(Unit) {
            scrollInteractionSource.interactions.collect {
                // hide dropdown when it's focused and scroll started
                if (it is DragInteraction.Start) {
                    isDropdownTemporaryHidden = true
                }
            }
        }
    }

    Dropdown(
        modifier = modifier,
        expanded = isAutoCompleteDropdownVisible,
        onDismissRequest = {
            dismissDropdownJob.value = coroutineScope.launch(Dispatchers.IO) {
                delay(WAITING_TIME_BETWEEN_DISMISS_REQUEST_AND_TAP_EVENT)
                isDropdownTemporaryHidden = true
            }
        },
        anchor = {
            OutlinedTextFieldWithValueSwitch(
                value = query,
                modifier = textInputModifier,
                interactionSource = queryInteractionSource,
                onSelectionChange = {
                    dismissDropdownJob.value?.cancel().also { dismissDropdownJob.value = null }
                },
                onValueChange = {
                    dismissDropdownJob.value?.cancel().also { dismissDropdownJob.value = null }
                    isDropdownTemporaryHidden = false
                    onQueryChanged(it)
                },
                label = { Text("Label") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(isAutoCompleteDropdownVisible)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, autoCorrect = false),
                keyboardActions = queryKeyboardActions
            )
        },
        content = { contentModifier ->
            Items(
                modifier = contentModifier,
                items = items,
                onNewItemSelected = { newSelectedItem ->
                    onItemSelected(newSelectedItem)
                    if (isFocused) focusManager.moveFocus(FocusDirection.Next)
                }
            )
        }
    )
}

@Composable
private fun Items(modifier: Modifier, items: List<String>, onNewItemSelected: OnValueChange) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        this.items(items = items, key = { item -> item }) { item ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .fillMaxWidth()
                    .clickable { onNewItemSelected(item) }
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = item)
            }
        }
    }
}

private fun Modifier.whenTapped(action: () -> Unit) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            var event: PointerEvent
            val touchSlop = viewConfiguration.touchSlop
            var scrolledDistance = 0f
            var isTapAction = true
            do {
                event = awaitPointerEvent(PointerEventPass.Initial)
                // if more than one pointer on screen, then it's not tap action
                if (event.changes.size > 1) isTapAction = false
                scrolledDistance += event.changes.firstOrNull()?.positionChange()?.getDistance()
                    ?: 0f
                // if scrolled more than touch slop, then it's not tap action
                if (abs(scrolledDistance) > touchSlop) isTapAction = false
            } while (!event.changes.fastAll { it.changedToUp() })
            if (isTapAction) action()
        }
    }
}
