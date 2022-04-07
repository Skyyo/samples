package com.skyyo.samples.features.videopager.composables

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import com.skyyo.samples.features.videopager.model.PagerState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PageData
}

data class PagerScope(val state: PagerState, val page: Int)

@Composable
fun VideoPager(
    modifier: Modifier = Modifier,
    state: PagerState,
    orientation: Orientation = Orientation.Horizontal,
    offscreenLimit: Int = 2,
    content: @Composable PagerScope.() -> Unit
) {
    var pageSize by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Layout(
        content = {
            val minPage = (state.currentPage - offscreenLimit).coerceAtLeast(state.minPage)
            val maxPage = (state.currentPage + offscreenLimit).coerceAtMost(state.maxPage)

            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)
                key(pageData) {
                    Column(modifier = pageData) {
                        scope.content()
                    }
                }
            }
        },
        modifier = modifier.draggable(
            orientation = orientation,
            onDragStarted = {
                state.selectionState = PagerState.SelectionState.Undecided
            },
            onDragStopped = { velocity ->
                coroutineScope.launch {
                    // Velocity is in pixels per second, but we deal in percentage offsets, so we
                    // need to scale the velocity to match
                    state.fling(velocity / pageSize)
                }
            },
            state = rememberDraggableState { dy ->
                coroutineScope.launch {
                    with(state) {
                        val pos = pageSize * currentPageOffset
                        val max = if (currentPage == minPage) 0 else pageSize * offscreenLimit
                        val min = if (currentPage == maxPage) 0 else -pageSize * offscreenLimit
                        val newPos = (pos + dy).coerceIn(min.toFloat(), max.toFloat())
                        snapToOffset(newPos / pageSize)
                    }
                }
            },
        )
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentPage = state.currentPage
            val offset = state.currentPageOffset
            val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            measurables
                .map { it.measure(childConstraints) to it.page }
                .forEach { (placeable, page) ->
                    // TODO: current this centers each page. We should investigate reading
                    //  gravity modifiers on the child, or maybe as a param to Pager.
                    val xCenterOffset = (constraints.maxWidth - placeable.width) / 3
                    val yCenterOffset = (constraints.maxHeight - placeable.height) / 3

                    if (currentPage == page) {
                        pageSize = if (orientation == Orientation.Horizontal) {
                            placeable.width
                        } else {
                            placeable.height
                        }
                    }
                    if (orientation == Orientation.Horizontal) {
                        placeable.place(
                            x = xCenterOffset + ((page - (currentPage - offset)) * placeable.width).roundToInt(),
                            y = yCenterOffset
                        )
                    } else {
                        placeable.place(
                            x = xCenterOffset,
                            y = yCenterOffset + ((page - (currentPage - offset)) * placeable.height).roundToInt()
                        )
                    }
                }
        }
    }
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("No PageData for measurable $this")

