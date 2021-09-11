package com.skyyo.samples.features.cameraX

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraXViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {

    val latestTakenPictureUri = handle.getLiveData<Uri>("latestTakenPictureUri")

    fun onPictureTaken(uri: Uri) {
        latestTakenPictureUri.postValue(uri)
    }
}