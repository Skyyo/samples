package com.skyyo.igdbbrowser.features.samples.cameraX

import android.net.Uri
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.extensions.navigateWithObject
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraXViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {

    fun goPhoto(uri: Uri) = navigationDispatcher.emit {
        it.navigateWithObject(Screens.PhotoViewer.route, arguments = bundleOf("uri" to uri))
    }
}