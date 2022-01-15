package com.skyyo.samples.features.signatureView

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignatureView(
    modifier: Modifier = Modifier,
    stroke: Stroke = Stroke(10f),
    paintColor: Color = Color.Black,
    signatureSnapshotName: String = "",
    events: Flow<SignatureViewEvent>,
    onBitmapSaved: (filePath: String?) -> Unit
) {
    val context = LocalContext.current
    val path = remember { Path() }
    val canvasUpdateTrigger = remember { mutableStateOf(0F) }
    val viewBounds = remember { mutableStateOf(Rect.Zero) }

    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            color = paintColor
            strokeWidth = stroke.width
        }
    }
    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
    val canvasForSnapshot: MutableState<Canvas?> = remember {
        mutableStateOf(null)
    }

    fun createSnapshotCanvas() {
        val bounds = viewBounds.value
        bitmap.value = Bitmap.createBitmap(
            bounds.width.roundToInt(), bounds.height.roundToInt(),
            Bitmap.Config.ARGB_8888
        )
        canvasForSnapshot.value = Canvas(bitmap.value!!.asImageBitmap())
        canvasForSnapshot.value?.drawPath(path, paint)
    }

    fun saveMediaToStorage(): Boolean {
        createSnapshotCanvas()
        val saved: Boolean
        val fos: OutputStream?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, signatureSnapshotName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
            }

            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ).toString() + File.separator
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, "$signatureSnapshotName.png")
            fos = FileOutputStream(image)
        }
        saved = bitmap.value?.compress(Bitmap.CompressFormat.PNG, 100, fos) == true
        fos?.flush()
        fos?.close()
        return saved
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                SignatureViewEvent.Reset -> {
                    path.reset()
                    canvasUpdateTrigger.value = 0F
                }
                SignatureViewEvent.Save -> {
                    val saved = saveMediaToStorage()
                    if (saved) {
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Can't Save", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .onGloballyPositioned { viewBounds.value = it.boundsInRoot() }
            .clipToBounds()
            .pointerInteropFilter {
                val x = it.x
                val y = it.y
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> path.moveTo(x, y)
                    MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
                }
                canvasUpdateTrigger.value = x
                true
            },
    ) {
        canvasUpdateTrigger.value.run {
            drawPath(path = path, color = paintColor, alpha = 1f, style = stroke)
        }
    }

}