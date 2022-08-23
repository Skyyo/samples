package com.skyyo.samples.features.dragAndDrop

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun DragAndDropScreen() {
    var data by remember { mutableStateOf(List(50) { it.toString() }) }

    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(
        data = data,
        lazyListState = listState,
        onDropped = { finalList, draggedItem ->
            data = finalList.map { if (it == draggedItem) "$draggedItem 2" else it }
        }
    )

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .dragContainer(dragDropState)
            .navigationBarsPadding()
            .padding(bottom = 20.dp),
        state = listState,
        contentPadding = remember { PaddingValues(16.dp) },
        verticalArrangement = remember { Arrangement.spacedBy(16.dp) }
    ) {
        dragItems(dragDropState = dragDropState, key = { it }) { item, modifier, isDragging ->
            val elevation by animateDpAsState(if (isDragging) 4.dp else 1.dp)
            Card(elevation = elevation, modifier = modifier) {
                Text(
                    text = "Item $item",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun <T> rememberDragDropState(
    data: List<T>,
    lazyListState: LazyListState,
    onDropped: (finalList: List<T>, draggedItem: T) -> Unit
): DragDropState<T> {
    val scope = rememberCoroutineScope()
    val state = remember(data, lazyListState) {
        DragDropState(
            data = data,
            state = lazyListState,
            scope = scope,
            onDropped = onDropped
        )
    }
    val localLifecycle = LocalLifecycleOwner.current.lifecycle
    val scrollEvents = remember(state) {
        state.scrollChannel.receiveAsFlow().flowWithLifecycle(localLifecycle, Lifecycle.State.RESUMED)
    }
    LaunchedEffect(scrollEvents) {
        scrollEvents.collect { lazyListState.scrollBy(it) }
    }
    return state
}

@OptIn(ExperimentalComposeUiApi::class)
fun <T> Modifier.dragContainer(dragDropState: DragDropState<T>): Modifier {
    return pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                dragDropState.onDrag(offset = offset)
            },
            onDragStart = { offset -> dragDropState.onDragStart(offset) },
            onDragEnd = { dragDropState.onDragFinished() },
            onDragCancel = { dragDropState.onDragFinished() }
        )
    }
}

fun <T> LazyListScope.dragItems(
    dragDropState: DragDropState<T>,
    key: (T) -> Any,
    content: @Composable (item: T, modifier: Modifier, isDragging: Boolean) -> Unit
) {
    item(key = null) { Spacer(modifier = Modifier) }
    itemsIndexed(dragDropState.lazyListItems, key = { _, item -> key(item) }) { index, item ->
        DraggableItem(
            dragDropState = dragDropState,
            index = index
        ) { modifier, isDragging -> content(item, modifier, isDragging) }
    }
}

class DragDropState<T> internal constructor(
    data: List<T>,
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onDropped: (finalList: List<T>, draggedItem: T) -> Unit
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    internal val scrollChannel = Channel<Float>()

    private var draggingItemDraggedDelta by mutableStateOf(0f)
    private var draggingItemInitialOffset by mutableStateOf(0)
    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
        } ?: 0f

    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == draggingItemIndex }

    internal var indexOfAnimatableItem by mutableStateOf<Int?>(null)
        private set
    internal var dropAnimatable = Animatable(0f)
        private set

    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..item.offset + item.size
            }?.also {
                draggingItemIndex = it.index
                draggingItemInitialOffset = it.offset
            }
    }

    internal fun onDragFinished() {
        val lastDragIndex = draggingItemIndex
        if (lastDragIndex != null) {
            indexOfAnimatableItem = lastDragIndex
            val startOffset = draggingItemOffset
            scope.launch {
                dropAnimatable.snapTo(startOffset)
                dropAnimatable.animateTo(
                    0f,
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = 1f
                    )
                )
                indexOfAnimatableItem = null
                onDropped(lazyListItems, lazyListItems[lastDragIndex - 1])
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemIndex = null
        draggingItemInitialOffset = 0
    }

    internal fun onDrag(offset: Offset) {
        draggingItemDraggedDelta += offset.y

        val draggingItem = draggingItemLayoutInfo ?: return
        val startOffset = draggingItem.offset + draggingItemOffset
        val endOffset = startOffset + draggingItem.size
        val middleOffset = startOffset + (endOffset - startOffset) / 2f

        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            middleOffset.toInt() in item.offset..item.offsetEnd &&
                draggingItem.index != item.index && item.index != 0
        }
        if (targetItem != null) {
            val scrollToIndex = if (targetItem.index == state.firstVisibleItemIndex) {
                draggingItem.index
            } else if (draggingItem.index == state.firstVisibleItemIndex) {
                targetItem.index
            } else {
                null
            }
            if (scrollToIndex != null) {
                scope.launch {
                    // this is needed to neutralize automatic keeping the first item first.
                    state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                    lazyListItems = lazyListItems.toMutableList().apply {
                        add(targetItem.index - 1, removeAt(draggingItem.index - 1))
                    }
                }
            } else {
                lazyListItems = lazyListItems.toMutableList().apply {
                    add(targetItem.index - 1, removeAt(draggingItem.index - 1))
                }
            }
            draggingItemIndex = targetItem.index
        } else {
            val overscroll = when {
                draggingItemDraggedDelta > 0 ->
                    (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)
                draggingItemDraggedDelta < 0 ->
                    (startOffset - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)
                else -> 0f
            }
            if (overscroll != 0f) {
                scrollChannel.trySend(overscroll)
            }
        }
    }

    private val LazyListItemInfo.offsetEnd: Int
        get() = this.offset + this.size

    var lazyListItems: List<T> by mutableStateOf(data)
        private set
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> LazyItemScope.DraggableItem(
    dragDropState: DragDropState<T>,
    index: Int,
    content: @Composable (modifier: Modifier, isDragging: Boolean) -> Unit
) {
    val draggableItemIndex = index + 1
    val dragging = draggableItemIndex == dragDropState.draggingItemIndex
    val draggingModifier = if (dragging) {
        Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationY = dragDropState.draggingItemOffset
            }
    } else if (draggableItemIndex == dragDropState.indexOfAnimatableItem) {
        Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationY = dragDropState.dropAnimatable.value
            }
    } else {
        Modifier.animateItemPlacement()
    }

    content(
        modifier = draggingModifier,
        isDragging = dragging
    )
}
