package com.skyyo.samples.features.customView

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.application.activity.MainActivity
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.math.roundToInt


@Composable
fun CustomViewScreen(viewModel: CustomViewVM = hiltViewModel()) {
    val sliderValue by remember { mutableStateOf(0.5f) }
    var doubleBackPressWithCustomView by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
        Triangle(sliderValue)
        CustomProgressCircle(sliderValue)
        CustomProgressCircle2(sliderValue)

        Switch(checked = doubleBackPressWithCustomView, onCheckedChange = {doubleBackPressWithCustomView = it})

        if (doubleBackPressWithCustomView) {
            AndroidView(factory = { context ->
                DoubleBackPressView(context).apply {
                    setDoubleBackPressAction(viewModel::goBack)
                }
            })
        } else {
            DoubleBackPressComposable(viewModel::goBack)
        }
    }
}

@Composable
fun CustomProgressCircle2(sliderValue: Float) {
    Canvas(
        modifier = Modifier
            .width(250.dp)
            .height(250.dp)
            .padding(16.dp)
    ) {
        drawArc(
            brush = SolidColor(Color.LightGray),
            startAngle = 120f,
            sweepAngle = 300f,
            useCenter = false,
            style = Stroke(35f, cap = StrokeCap.Round)
        )

        val convertedValue = sliderValue * 300

        drawArc(
            brush = SolidColor(Color.Cyan),
            startAngle = 120f,
            sweepAngle = convertedValue,
            useCenter = false,
            style = Stroke(35f, cap = StrokeCap.Round)
        )

        drawIntoCanvas {
            val paint = Paint().asFrameworkPaint()
            paint.apply {
                isAntiAlias = true
                textSize = 55f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            //TODO this should be replaced by one of the compose API's in future
            it.nativeCanvas.drawText(
                "${(sliderValue * 100).roundToInt().toInt()}%",
                size.width / 2,
                size.height / 2,
                paint
            )
        }
    }
}

@Composable
fun CustomProgressCircle(sliderValue: Float) {
    Canvas(
        modifier = Modifier
            .size(250.dp)
            .padding(16.dp)
    ) {
        drawCircle(
            SolidColor(Color.LightGray),
            size.width / 2,
            style = Stroke(35f)
        )
        val convertedValue = sliderValue * 360
        drawArc(
            brush = SolidColor(Color.Black),
            startAngle = -90f,
            sweepAngle = convertedValue,
            useCenter = false,
            style = Stroke(35f)
        )
    }
}

@Composable
fun Triangle(sliderValue: Float) {
    Canvas(
        modifier = Modifier
            .width(300.dp)
            .height(150.dp)
            .padding(16.dp)
    ) {
        val path = Path()
        path.moveTo(size.width, 0f)
        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)

        clipPath(clipOp = ClipOp.Intersect, path = path) {
            drawPath(
                path = path,
                brush = SolidColor(Color.LightGray)
            )

            drawRect(
                SolidColor(Color.Green),
                size = Size(
                    sliderValue * size.width,
                    size.height
                )
            )
        }

    }
}

@Composable
private fun DoubleBackPressComposable(doubleBackPressAction: () -> Unit) {
    var previousBackPressTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current
    var isToastVisible by remember { mutableStateOf(false) }
    val toast: Toast = remember {
        Toast.makeText(context, "Press back one more time to continue", Toast.LENGTH_LONG).apply {
            addCallback(object : Toast.Callback() {
                override fun onToastHidden() {
                    super.onToastHidden()
                    isToastVisible = false
                }

                override fun onToastShown() {
                    super.onToastShown()
                    isToastVisible = true
                }
            })
        }
    }
    BackHandler {
        val newBackPressTime = System.currentTimeMillis()
        previousBackPressTime = if (previousBackPressTime != 0L && newBackPressTime - previousBackPressTime < 5000L) {
            doubleBackPressAction()
            toast.cancel()
            0L
        } else {
            if (!isToastVisible) toast.show()
            newBackPressTime
        }
    }
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .background(Color.Yellow))
}

private class BackInvokedWrapper(val callback: Any)

class DoubleBackPressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var doubleBackPressAction: () -> Unit = {}
    private var previousBackPressTime = 0L
    private var doublePressActionShouldBeInvoked = false
    private var isToastVisible = false
    private var isCallbackRegistered = false
    private var backInvokedWrapper = BackInvokedWrapper(
        callback = when {
            Build.VERSION.SDK_INT < 33 -> {
                {
                    prepareToInterceptOnBackCalls()
                    onBackPressed()
                    true
                }
            }
            else -> {
                createOnBackInvokedCallback {
                    prepareToInterceptOnBackCalls()
                    onBackPressed()
                }
            }
        }
    )

    private fun createOnBackInvokedCallback(action: () -> Unit): Any {
        val callbackClass = Class.forName("android.window.OnBackInvokedCallback")
        return Proxy.newProxyInstance(
            callbackClass.classLoader,
            arrayOf<Class<*>>(callbackClass)
        ) { _, method, _ ->
            if (method.name.equals("onBackInvoked")) {
                action()
            }
            Unit
        }
    }

    private val toast: Toast = Toast.makeText(context, "Press back one more time to continue", Toast.LENGTH_LONG).apply {
        addCallback(object: Toast.Callback() {
            override fun onToastHidden() {
                super.onToastHidden()
                isToastVisible = false
            }

            override fun onToastShown() {
                super.onToastShown()
                isToastVisible = true
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.Cyan.toArgb())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            prepareToInterceptOnBackCalls()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun prepareToInterceptOnBackCalls() {
        val newBackPressTime = System.currentTimeMillis()
        if (previousBackPressTime != 0L && newBackPressTime - previousBackPressTime < 5000L) {
            doublePressActionShouldBeInvoked = true
            previousBackPressTime = 0L
        } else {
            doublePressActionShouldBeInvoked = false
            previousBackPressTime = newBackPressTime
        }
        if (!isToastVisible) toast.show()
    }

    fun setDoubleBackPressAction(doubleBackPressAction: () -> Unit) {
        this.doubleBackPressAction = doubleBackPressAction
    }

    private fun onBackPressed() {
        if (doublePressActionShouldBeInvoked) doubleBackPressAction()
    }

    private fun syncBackInvokeState(shouldBeRegistered: Boolean) {
        if (shouldBeRegistered && !isCallbackRegistered) {
            (context as Activity).registerOnBackInvokedCallback()
            isCallbackRegistered = true
        } else if (!shouldBeRegistered && isCallbackRegistered) {
            (context as Activity).unregisterOnBackInvokedCallback()
            isCallbackRegistered = false
        }
    }

    private fun Activity.onBackInvokedDispatcher(): Any? {
        return try {
            val backInvokeDispatcherField = javaClass.getDeclaredField("onBackInvokedDispatcher")
            backInvokeDispatcherField.isAccessible = true
            backInvokeDispatcherField.get(this)
        } catch (e: NoSuchFieldException) {
            null
        }
    }

    private fun Activity.registerOnBackInvokedCallback() {
        val dispatcher = onBackInvokedDispatcher()
        if (dispatcher != null) {
            val registerMethod = dispatcher.javaClass.getDeclaredMethod(
                "registerOnBackInvokedCallback",
                Int::class.java,
                Class.forName("android.window.OnBackInvokedCallback")
            )
            registerMethod.isAccessible = true
            val priorityField = dispatcher.javaClass.getDeclaredField("PRIORITY_OVERLAY")
            priorityField.isAccessible = true
            registerMethod.invoke(
                dispatcher,
                priorityField.get(dispatcher),
                backInvokedWrapper.callback
            )
        } else {
            (this as MainActivity).backInvokedDispatcher.registerOnBackInvokedCallback(
                callback = backInvokedWrapper.callback as () -> Boolean
            )
        }
    }

    private fun Activity.unregisterOnBackInvokedCallback() {
        val dispatcher = onBackInvokedDispatcher()
        if (dispatcher != null) {
            val unRegisterMethod = dispatcher.javaClass.getDeclaredMethod(
                "unregisterOnBackInvokedCallback",
                Class.forName("android.window.OnBackInvokedCallback")
            )
            unRegisterMethod.isAccessible = true
            unRegisterMethod.invoke(dispatcher, backInvokedWrapper.callback)
        } else {
            (this as MainActivity).backInvokedDispatcher.unregisterOnBackInvokedCallback(backInvokedWrapper.callback as () -> Boolean)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        syncBackInvokeState(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        toast.cancel()
        syncBackInvokeState(false)
    }
}