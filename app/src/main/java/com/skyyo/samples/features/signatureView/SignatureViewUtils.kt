package com.skyyo.samples.features.signatureView

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.ColorInt
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.roundToInt

object SignatureViewUtils {

    @Throws(Exception::class)
    fun saveMediaToStorage(
        context: Context,
        bitmap: Bitmap,
        imageName: String = "bitmap",
        mimeType: String = "png"
    ) {
        try {
            val fos: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver: ContentResolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                ).toString() + File.separator
                val file = File(imagesDir)
                if (!file.exists()) file.mkdir()
                val image = File(imagesDir, "$imageName.$mimeType")
                fos = FileOutputStream(image)
            }
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos)
            fos?.flush()
            fos?.close()
        } catch (e: Exception) {
            throw e
        }
    }

    fun getBitmap(bounds: Rect, @ColorInt canvasColor: Int, path: Path, paint: Paint): Bitmap {
        val bitmap = Bitmap.createBitmap(
            bounds.width.roundToInt(), bounds.height.roundToInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvasForSnapshot = Canvas(bitmap.asImageBitmap()).apply {
            nativeCanvas.drawColor(canvasColor)
        }
        canvasForSnapshot.drawPath(path, paint)
        return bitmap
    }
}
