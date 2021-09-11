package com.skyyo.igdbbrowser.features.cameraX

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraXViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {

    val latestTakenPictureUri = handle.getLiveData<Uri>("latestTakenPictureUri")

    fun onPictureTaken(uri: Uri) {
        log("new uri $uri")
        latestTakenPictureUri.postValue(uri)
    }
}
//navigationDispatcher.emit {
//    it.navigateWithObject(Screens.PhotoViewer.route, arguments = bundleOf("uri" to uri))
//}