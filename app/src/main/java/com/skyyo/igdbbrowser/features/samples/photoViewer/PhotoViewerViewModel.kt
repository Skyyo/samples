package com.skyyo.igdbbrowser.features.samples.photoViewer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoViewerViewModel @Inject constructor(
    private val handle: SavedStateHandle
) : ViewModel() {

    val photoUri = handle.getLiveData<Uri>("uri")
}