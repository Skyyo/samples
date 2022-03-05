package com.skyyo.samples.features.pdfViewer

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.extensions.getStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {
    val uri = handle.getStateFlow<Uri?>(viewModelScope, "uri", null)
    val pdfRenderer = MutableStateFlow<PdfRenderer?>(null)
}