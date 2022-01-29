package com.skyyo.samples.features.pdfViewer

import android.Manifest
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.skyyo.samples.R
import com.skyyo.samples.extensions.goAppPermissions
import com.skyyo.samples.features.zoomable.Zoomable
import com.skyyo.samples.utils.OnClick
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PdfViewerScreen(viewModel: PdfViewerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val storagePermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    val uri by viewModel.uri.collectAsState()

    ProvideWindowInsets {
        PermissionRequired(
            permissionState = storagePermissionState,
            permissionNotGrantedContent = {
                StoragePermissionNotGrantedContent(
                    R.string.please_grant_storage_permission,
                    storagePermissionState::launchPermissionRequest
                )
            },
            permissionNotAvailableContent = {
                StoragePermissionNotGrantedContent(
                    R.string.please_grant_storage_permission_from_settings,
                    context::goAppPermissions
                )
            },
            content = {
                if (uri != null) {
                    val pdfRender = remember(uri) {
                        val inputStream: InputStream? = context.contentResolver.openInputStream(uri!!)
                        val file = File(context.filesDir, "pdf")
                        file.writeBytes(inputStream!!.readBytes())
                        inputStream.close()
                        PdfRenderer(
                            ParcelFileDescriptor.open(
                                file,
                                ParcelFileDescriptor.MODE_READ_ONLY
                            )
                        )
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            pdfRender.close()
                        }
                    }
                    Zoomable() {
                        LazyColumn(
                            Modifier.statusBarsPadding()
                        ) {
                            items(pdfRender.pageCount) {
                                val bitmap = remember(it) { generatePageBitmap(pdfRender, it, PdfQuality.NORMAL) }
                                PdfPageItem(bitmap = bitmap)
                            }
                        }
                    }
                } else {
                    ChoosePdfButton(viewModel)
                }
            }
        )
    }
}

@Composable
fun ChoosePdfButton(viewModel: PdfViewerViewModel) {
    val chooseFileRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            viewModel.uri.value = it
        })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { chooseFileRequester.launch("application/pdf") }) {
            Text(
                text = "Choose file",
                color = Color.White,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StoragePermissionNotGrantedContent(@StringRes explanationStringId: Int, onClick: OnClick) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(explanationStringId),
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onClick) {
            Text(
                text = "Grant permission",
                color = Color.White,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PdfPageItem(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White),
        contentScale = ContentScale.Crop
    )
}


fun generatePageBitmap(renderer: PdfRenderer, page: Int, pdfQuality: PdfQuality): Bitmap {
    val openedPage = renderer.openPage(page)
    val bitmapNew = Bitmap.createBitmap(
        openedPage.width * pdfQuality.ratio,
        openedPage.height  * pdfQuality.ratio,
        Bitmap.Config.ARGB_8888
    )
    openedPage.render(
        bitmapNew,
        null,
        null,
        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
    )
    openedPage.close()
    return bitmapNew
}
