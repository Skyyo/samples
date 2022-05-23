package com.skyyo.samples.features.autoComplete

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.window.PopupPositionProvider
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.skyyo.samples.R
import kotlinx.coroutines.coroutineScope
import java.util.*
import kotlin.math.roundToInt

private const val InTransitionDuration = 120
private const val OutTransitionDuration = 75
private val MenuElevation = 8.dp
private val DropdownMenuVerticalPadding = 8.dp

@Composable
fun CustomDropdownMenuContent(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    expandedStates: MutableTransitionState<Boolean>,
    transformOriginState: MutableState<TransformOrigin>,
    content: LazyListScope.() -> Unit
) {
    val transition = updateTransition(expandedStates, "DropDownMenu")
    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis = InTransitionDuration,
                    easing = LinearOutSlowInEasing
                )
            } else {
                tween(
                    durationMillis = 1,
                    delayMillis = OutTransitionDuration - 1
                )
            }
        },
        label = "FloatAnimation",
        targetValueByState = { if (it) 1f else 0.8f }
    )

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(durationMillis = 30)
            } else {
                tween(durationMillis = OutTransitionDuration)
            }
        },
        label = "FloatAnimation",
        targetValueByState = { if (it) 1f else 0f }
    )

    Card(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
            transformOrigin = transformOriginState.value
        },
        elevation = MenuElevation
    ) {
        LazyColumn(
            modifier = modifier.padding(vertical = DropdownMenuVerticalPadding),
            state = scrollState,
            content = content,
        )
    }
}

interface CustomExposedDropdownMenuBoxScope {
    fun Modifier.exposedDropdownSize(
        matchTextFieldWidth: Boolean = true
    ): Modifier

    fun scrollState(): LazyListState

    @Composable
    fun CustomExposedDropdownMenu(
        modifier: Modifier = Modifier,
        scrollState: LazyListState = rememberLazyListState(),
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        content: LazyListScope.() -> Unit
    ) {
        val expandedStates = remember { MutableTransitionState(false) }
        expandedStates.targetState = expanded

        if (expandedStates.currentState || expandedStates.targetState) {
            val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
            val density = LocalDensity.current
            val popupPositionProvider = CustomDropdownMenuPositionProvider(
                DpOffset.Zero,
                density
            ) { parentBounds, menuBounds ->
                transformOriginState.value = calculateTransformOrigin(parentBounds, menuBounds)
            }

            CustomExposedDropdownMenuPopup(
                onDismissRequest = onDismissRequest,
                popupPositionProvider = popupPositionProvider
            ) {
                CustomDropdownMenuContent(
                    modifier = modifier.exposedDropdownSize(),
                    scrollState = scrollState(),
                    expandedStates = expandedStates,
                    transformOriginState = transformOriginState,
                    content = content
                )
            }
        }
    }
}

@Immutable
internal data class CustomDropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // The content offset specified using the dropdown offset parameter.
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        // Compute horizontal position.
        val toRight = anchorBounds.left + contentOffsetX
        val toLeft = anchorBounds.right - contentOffsetX - popupContentSize.width
        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(
                toRight,
                toLeft,
                // If the anchor gets outside of the window on the left, we want to position
                // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                if (anchorBounds.left >= 0) toDisplayRight else toDisplayLeft
            )
        } else {
            sequenceOf(
                toLeft,
                toRight,
                // If the anchor gets outside of the window on the right, we want to position
                // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                if (anchorBounds.right <= windowSize.width) toDisplayLeft else toDisplayRight
            )
        }.firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        // Compute vertical position.
        val toBottom = maxOf(anchorBounds.bottom + contentOffsetY)
        val toTop = anchorBounds.top - contentOffsetY - popupContentSize.height
        val toCenter = anchorBounds.top - popupContentSize.height / 2
        val toDisplayBottom = windowSize.height - popupContentSize.height
        val y = sequenceOf(toBottom, toTop, toCenter, toDisplayBottom).firstOrNull {
                    it + popupContentSize.height <= windowSize.height
        } ?: toTop

        onPositionCalculated(
            anchorBounds,
            IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height)
        )
        return IntOffset(x, y)
    }
}

internal fun calculateTransformOrigin(
    parentBounds: IntRect,
    menuBounds: IntRect
): TransformOrigin {
    val pivotX = when {
        menuBounds.left >= parentBounds.right -> 0f
        menuBounds.right <= parentBounds.left -> 1f
        menuBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter = (kotlin.math.max(
                parentBounds.left,
                menuBounds.left
            ) + kotlin.math.min(parentBounds.right, menuBounds.right)) / 2
            (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
        }
    }
    val pivotY = when {
        menuBounds.top >= parentBounds.bottom -> 0f
        menuBounds.bottom <= parentBounds.top -> 1f
        menuBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter = (kotlin.math.max(
                parentBounds.top,
                menuBounds.top
            ) + kotlin.math.min(parentBounds.bottom, menuBounds.bottom)) / 2
            (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}

@ExperimentalMaterialApi
@Composable
fun CustomExposedDropdownMenuBox(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onClick: () -> Unit,
    content: @Composable CustomExposedDropdownMenuBoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    val view = LocalView.current
    var width by remember { mutableStateOf(0) }
    var menuHeight by remember { mutableStateOf(0) }
    val coordinates = remember { Ref<LayoutCoordinates>() }
    var hasFocus by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    val scope = remember(density, menuHeight, width) {
        object : CustomExposedDropdownMenuBoxScope {
            override fun Modifier.exposedDropdownSize(matchTextFieldWidth: Boolean): Modifier {
                return with(density) {
                    heightIn(max = menuHeight.toDp()).let {
                        if (matchTextFieldWidth) {
                            it.width(width.toDp())
                        } else it
                    }
                }
            }
            override fun scrollState(): LazyListState = scrollState
        }
    }

    Box(
        modifier
            .onGloballyPositioned {
                width = it.size.width
                coordinates.value = it
                updateHeight(
                    view.rootView,
                    coordinates.value
                ) { newHeight ->
                    menuHeight = newHeight
                }
            }
            .expandable(onClick = onClick)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                hasFocus = focusState.isFocused
            }
    ) {
        scope.content()
    }

    SideEffect {
        if (expanded) focusRequester.requestFocus()
    }

    LaunchedEffect(hasFocus) {
        if (!hasFocus) scrollState.scrollToItem(0)
    }

    DisposableEffect(view) {
        val listener = OnGlobalLayoutListener(view) {
            // We want to recalculate the menu height on relayout - e.g. when keyboard shows up.
            updateHeight(
                view.rootView,
                coordinates.value
            ) { newHeight ->
                menuHeight = newHeight
            }
        }
        onDispose { listener.dispose() }
    }
}

private fun updateHeight(
    view: View,
    coordinates: LayoutCoordinates?,
    onHeightUpdate: (Int) -> Unit
) {

    coordinates ?: return
    val visibleWindowBounds = Rect().let {
        view.getWindowVisibleDisplayFrame(it)
        it
    }

    val heightAbove = coordinates.boundsInWindow().top - visibleWindowBounds.top
    val heightBelow = visibleWindowBounds.bottom - visibleWindowBounds.top - coordinates.boundsInWindow().bottom

    onHeightUpdate(kotlin.math.max(heightAbove, heightBelow).toInt())
}

private fun Modifier.expandable(
    onClick: () -> Unit
) = pointerInput(Unit) {
    forEachGesture {
        coroutineScope {
            awaitPointerEventScope {
                var event: PointerEvent
                do {
                    event = awaitPointerEvent(PointerEventPass.Initial)
                } while (
                    !event.changes.fastAll { it.changedToUp() }
                )
                onClick.invoke()
            }
        }
    }
}.semantics {
    onClick {
        onClick.invoke()
        true
    }
}

private class OnGlobalLayoutListener(
    private val view: View,
    private val onGlobalLayoutCallback: () -> Unit
) : View.OnAttachStateChangeListener, ViewTreeObserver.OnGlobalLayoutListener {
    private var isListeningToGlobalLayout = false

    init {
        view.addOnAttachStateChangeListener(this)
        registerOnGlobalLayoutListener()
    }

    override fun onViewAttachedToWindow(p0: View?) = registerOnGlobalLayoutListener()

    override fun onViewDetachedFromWindow(p0: View?) = unregisterOnGlobalLayoutListener()

    override fun onGlobalLayout() = onGlobalLayoutCallback()

    private fun registerOnGlobalLayoutListener() {
        if (isListeningToGlobalLayout || !view.isAttachedToWindow) return
        view.viewTreeObserver.addOnGlobalLayoutListener(this)
        isListeningToGlobalLayout = true
    }

    private fun unregisterOnGlobalLayoutListener() {
        if (!isListeningToGlobalLayout) return
        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
        isListeningToGlobalLayout = false
    }

    fun dispose() {
        unregisterOnGlobalLayoutListener()
        view.removeOnAttachStateChangeListener(this)
    }
}

internal val LocalPopupTestTag = compositionLocalOf { "DEFAULT_TEST_TAG" }

@Suppress("NOTHING_TO_INLINE")
@Composable
private inline fun SimpleStack(modifier: Modifier, noinline content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        when (measurables.size) {
            0 -> layout(0, 0) {}
            1 -> {
                val p = measurables[0].measure(constraints)
                layout(p.width, p.height) {
                    p.placeRelative(0, 0)
                }
            }
            else -> {
                val placeables = measurables.fastMap { it.measure(constraints) }
                var width = 0
                var height = 0
                for (i in 0..placeables.lastIndex) {
                    val p = placeables[i]
                    width = maxOf(width, p.width)
                    height = maxOf(height, p.height)
                }
                layout(width, height) {
                    for (i in 0..placeables.lastIndex) {
                        val p = placeables[i]
                        p.placeRelative(0, 0)
                    }
                }
            }
        }
    }
}

@Composable
internal fun CustomExposedDropdownMenuPopup(
    modifier: Modifier = Modifier,
    onDismissRequest: (() -> Unit)? = null,
    popupPositionProvider: PopupPositionProvider,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val testTag = LocalPopupTestTag.current
    val layoutDirection = LocalLayoutDirection.current
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val popupId = rememberSaveable { UUID.randomUUID() }
    val popupLayout = remember {
        PopupLayout(
            onDismissRequest = onDismissRequest,
            testTag = testTag,
            composeView = view,
            density = density,
            initialPositionProvider = popupPositionProvider,
            popupId = popupId
        ).apply {
            setContent(parentComposition) {
                SimpleStack(
                    modifier
                        .semantics { this.popup() }
                        // Get the size of the content
                        .onSizeChanged {
                            popupContentSize = it
                            updatePosition()
                        }
                        // Hide the popup while we can't position it correctly
                        .alpha(if (canCalculatePosition) 1f else 0f)
                ) {
                    currentContent()
                }
            }
        }
    }

    DisposableEffect(popupLayout) {
        popupLayout.show()
        popupLayout.updateParameters(
            onDismissRequest = onDismissRequest,
            testTag = testTag,
            layoutDirection = layoutDirection
        )
        onDispose {
            popupLayout.disposeComposition()
            // Remove the window
            popupLayout.dismiss()
        }
    }

    SideEffect {
        popupLayout.updateParameters(
            onDismissRequest = onDismissRequest,
            testTag = testTag,
            layoutDirection = layoutDirection
        )
    }

    DisposableEffect(popupPositionProvider) {
        popupLayout.positionProvider = popupPositionProvider
        popupLayout.updatePosition()
        onDispose {}
    }

    //  Look at module arrangement so that Box can be
    //  used instead of this custom Layout
    // Get the parent's position, size and layout direction
    Layout(
        content = {},
        modifier = Modifier.onGloballyPositioned { childCoordinates ->
            val coordinates = childCoordinates.parentLayoutCoordinates!!
            val layoutSize = coordinates.size
            val position = coordinates.positionInWindow()
            val layoutPosition = IntOffset(position.x.roundToInt(), position.y.roundToInt())

            popupLayout.parentBounds = IntRect(layoutPosition, layoutSize)
            // Update the popup's position
            popupLayout.updatePosition()
        }
    ) { _, _ ->
        popupLayout.parentLayoutDirection = layoutDirection
        layout(0, 0) {}
    }
}

@SuppressLint("ViewConstructor")
private class PopupLayout(
    private var onDismissRequest: (() -> Unit)?,
    var testTag: String,
    private val composeView: View,
    density: Density,
    initialPositionProvider: PopupPositionProvider,
    popupId: UUID
) : AbstractComposeView(composeView.context),
    ViewRootForInspector,
    ViewTreeObserver.OnGlobalLayoutListener {
    private val windowManager =
        composeView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val params = createLayoutParams()

    /** The logic of positioning the popup relative to its parent. */
    var positionProvider = initialPositionProvider

    // Position params
    var parentLayoutDirection: LayoutDirection = LayoutDirection.Ltr
    var parentBounds: IntRect? by mutableStateOf(null)
    var popupContentSize: IntSize? by mutableStateOf(null)

    // Track parent bounds and content size; only show popup once we have both
    val canCalculatePosition by derivedStateOf { parentBounds != null && popupContentSize != null }

    private val maxSupportedElevation = 30.dp

    // The window visible frame used for the last popup position calculation.
    private val previousWindowVisibleFrame = Rect()
    private val tmpWindowVisibleFrame = Rect()

    override val subCompositionView: AbstractComposeView get() = this

    // Specific to exposed dropdown menus.
    private val dismissOnOutsideClick = { offset: Offset?, bounds: IntRect ->
        if (offset == null) false
        else {
            offset.x < bounds.left || offset.x > bounds.right || offset.y < bounds.top || offset.y > bounds.bottom
        }
    }

    init {
        id = android.R.id.content
        ViewTreeLifecycleOwner.set(this, ViewTreeLifecycleOwner.get(composeView))
        ViewTreeViewModelStoreOwner.set(this, ViewTreeViewModelStoreOwner.get(composeView))
        ViewTreeSavedStateRegistryOwner.set(this, ViewTreeSavedStateRegistryOwner.get(composeView))
        composeView.viewTreeObserver.addOnGlobalLayoutListener(this)
        // Set unique id for AbstractComposeView. This allows state restoration for the state
        // defined inside the Popup via rememberSaveable()
        setTag(R.id.compose_view_saveable_id_tag, "Popup:$popupId")

        // Enable children to draw their shadow by not clipping them
        clipChildren = false
        // Allocate space for elevation
        with(density) { elevation = maxSupportedElevation.toPx() }
        // Simple outline to force window manager to allocate space for shadow.
        // Note that the outline affects clickable area for the dismiss listener. In case of shapes
        // like circle the area for dismiss might be to small (rectangular outline consuming clicks
        // outside of the circle).
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, result: Outline) {
                result.setRect(0, 0, view.width, view.height)
                // We set alpha to 0 to hide the view's shadow and let the composable to draw its
                // own shadow. This still enables us to get the extra space needed in the surface.
                result.alpha = 0f
            }
        }
    }

    private var content: @Composable () -> Unit by mutableStateOf({})

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun show() {
        windowManager.addView(this, params)
    }

    fun setContent(parent: CompositionContext, content: @Composable () -> Unit) {
        setParentCompositionContext(parent)
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
    }

    @Composable
    override fun Content() {
        content()
    }

    /**
     * Taken from PopupWindow
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyDispatcherState == null) {
                return super.dispatchKeyEvent(event)
            }
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                val state = keyDispatcherState
                state?.startTracking(event, this)
                return true
            } else if (event.action == KeyEvent.ACTION_UP) {
                val state = keyDispatcherState
                if (state != null && state.isTracking(event) && !event.isCanceled) {
                    onDismissRequest?.invoke()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun updateParameters(
        onDismissRequest: (() -> Unit)?,
        testTag: String,
        layoutDirection: LayoutDirection
    ) {
        this.onDismissRequest = onDismissRequest
        this.testTag = testTag
        superSetLayoutDirection(layoutDirection)
    }

    /**
     * Updates the position of the popup based on current position properties.
     */
    fun updatePosition() {
        val parentBounds = parentBounds ?: return
        val popupContentSize = popupContentSize ?: return

        val windowSize = previousWindowVisibleFrame.let {
            composeView.getWindowVisibleDisplayFrame(it)
            val bounds = it.toIntBounds()
            IntSize(width = bounds.width, height = bounds.height)
        }

        val popupPosition = positionProvider.calculatePosition(
            parentBounds,
            windowSize,
            parentLayoutDirection,
            popupContentSize
        )

        params.width = parentBounds.width
        params.x = popupPosition.x
        params.y = popupPosition.y

        windowManager.updateViewLayout(this, params)
    }

    /**
     * Remove the view from the [WindowManager].
     */
    fun dismiss() {
        ViewTreeLifecycleOwner.set(this, null)
        composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        windowManager.removeViewImmediate(this)
    }

    /**
     * Handles touch screen motion events and calls [onDismissRequest] when the
     * users clicks outside the popup.
     */

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        // Note that this implementation is taken from PopupWindow. It actually does not seem to
        // matter whether we return true or false as some upper layer decides on whether the
        // event is propagated to other windows or not. So for focusable the event is consumed but
        // for not focusable it is propagated to other windows.
        if (((event.action == MotionEvent.ACTION_DOWN) &&
                    (
                            (event.x < 0) ||
                                    (event.x >= width) ||
                                    (event.y < 0) ||
                                    (event.y >= height)
                            )
                    ) ||
            event.action == MotionEvent.ACTION_OUTSIDE
        ) {
            val parentBounds = parentBounds
            val shouldDismiss = parentBounds == null || dismissOnOutsideClick(
                if (event.x != 0f || event.y != 0f) {
                    Offset(
                        params.x + event.x,
                        params.y + event.y
                    )
                } else null,
                parentBounds
            )
            if (shouldDismiss) {
                onDismissRequest?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun setLayoutDirection(layoutDirection: Int) {
        // Do nothing. ViewRootImpl will call this method attempting to set the layout direction
        // from the context's locale, but we have one already from the parent composition.
    }

    // Sets the "real" layout direction for our content that we obtain from the parent composition.
    private fun superSetLayoutDirection(layoutDirection: LayoutDirection) {
        val direction = when (layoutDirection) {
            LayoutDirection.Ltr -> android.util.LayoutDirection.LTR
            LayoutDirection.Rtl -> android.util.LayoutDirection.RTL
        }
        super.setLayoutDirection(direction)
    }

    /**
     * Initialize the LayoutParams specific to [android.widget.PopupWindow].
     */
    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().apply {
            // Start to position the popup in the top left corner, a new position will be calculated
            gravity = Gravity.START or Gravity.TOP

            // Flags specific to exposed dropdown menu.
            flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED

            type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL

            // Get the Window token from the parent view
            token = composeView.applicationWindowToken

            // Wrap the frame layout which contains composable content
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT

            format = PixelFormat.TRANSLUCENT

            // accessibilityTitle is not exposed as a public API therefore we set popup window
            // title which is used as a fallback by a11y services
            title = "title"
        }
    }

    private fun Rect.toIntBounds() = IntRect(
        left = left,
        top = top,
        right = right,
        bottom = bottom
    )

    override fun onGlobalLayout() {
        // Update the position of the popup, in case getWindowVisibleDisplayFrame has changed.
        composeView.getWindowVisibleDisplayFrame(tmpWindowVisibleFrame)
        if (tmpWindowVisibleFrame != previousWindowVisibleFrame) {
            updatePosition()
        }
    }
}
