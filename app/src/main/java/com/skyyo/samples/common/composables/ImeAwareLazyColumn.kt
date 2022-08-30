package com.skyyo.samples.common.composables

import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onFocusedBoundsChanged
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TEXT_FIELD_BOTTOM_UNSET = -1f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImeAwareLazyColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = remember { PaddingValues(0.dp) },
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    var keyboardHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val windowInsetsConsumer = LocalView.current
    var lazyColumnCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val keyboardCallback = remember {
        KeyboardCallback(scope, state, windowInsetsConsumer) { keyboardHeight = density.run { it.toDp() } }
    }
    DisposableEffect(Unit) {
        ViewCompat.setWindowInsetsAnimationCallback(windowInsetsConsumer, keyboardCallback)
        onDispose {
            ViewCompat.setWindowInsetsAnimationCallback(windowInsetsConsumer, null)
        }
    }

    BoxWithConstraints(
        modifier = modifier.onGloballyPositioned {
            lazyColumnCoordinates = it
        }
    ) {
        keyboardCallback.lazyColumnHeight = density.run { maxHeight.toPx() }
        LazyColumn(
            state = state,
            contentPadding = contentPadding,
            modifier = modifier
                .fillMaxHeight()
                .onFocusedBoundsChanged {
                    val tempLazyColumnCoordinates = lazyColumnCoordinates
                    if (it != null && tempLazyColumnCoordinates != null) {
                        keyboardCallback.focusedTextFieldBottom =
                            tempLazyColumnCoordinates.localPositionOf(
                                sourceCoordinates = it,
                                relativeToSource = Offset(0f, it.size.height.toFloat())
                            ).y
                    }
                }
        ) {
            content()
            item { Spacer(modifier = Modifier.height(keyboardHeight)) }
        }
    }
}

private class KeyboardCallback(
    private val scope: CoroutineScope,
    private val state: LazyListState,
    private val targetView: View,
    private val onKeyboardHeightChanged: (Float) -> Unit
) : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
    var lazyColumnHeight = 0f
        set(value) {
            if (field == 0f) field = value
        }
    var focusedTextFieldBottom = TEXT_FIELD_BOTTOM_UNSET
        set(value) {
            if (!isKeyboardAnimating) field = value
        }
    private var isKeyboardAnimating = false
    private var keyboardHeight = 0f
    private var consumedFraction = 0f
    private var isKeyboardVisible = false

    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
        isKeyboardAnimating = animation.typeMask and WindowInsetsCompat.Type.ime() == WindowInsetsCompat.Type.ime()
        super.onPrepare(animation)
    }

    override fun onStart(
        animation: WindowInsetsAnimationCompat,
        bounds: WindowInsetsAnimationCompat.BoundsCompat
    ): WindowInsetsAnimationCompat.BoundsCompat {
        if (isKeyboardAnimating) keyboardHeight = bounds.upperBound.bottom.toFloat()
        return super.onStart(animation, bounds)
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: MutableList<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        if (isKeyboardAnimating) {
            val imeAnimation = runningAnimations.first { it.typeMask and WindowInsetsCompat.Type.ime() == WindowInsetsCompat.Type.ime() }
            val newConsumedFraction = imeAnimation.interpolatedFraction
            var newKeyboardBottom = keyboardHeight * newConsumedFraction
            var consumedBottom = keyboardHeight * consumedFraction
            if (isKeyboardVisible) {
                newKeyboardBottom = keyboardHeight - newKeyboardBottom
                consumedBottom = keyboardHeight - consumedBottom
            }
            scope.launch {
                if (focusedTextFieldBottom == TEXT_FIELD_BOTTOM_UNSET || lazyColumnHeight - focusedTextFieldBottom < newKeyboardBottom) {
                    state.scrollBy(newKeyboardBottom - consumedBottom)
                }
            }
            onKeyboardHeightChanged(newKeyboardBottom)
            consumedFraction = newConsumedFraction
        }
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        super.onEnd(animation)
        val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(
            targetView.rootWindowInsets,
            targetView
        )
        consumedFraction = 0f
        isKeyboardVisible = windowInsetsCompat.isVisible(WindowInsetsCompat.Type.ime())
        isKeyboardAnimating = false
    }
}
