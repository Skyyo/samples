package com.skyyo.samples.features.pdfViewer

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor() : ViewModel() {
    val uri = MutableStateFlow<Uri?>(null)
}