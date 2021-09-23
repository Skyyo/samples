package com.skyyo.samples.features.scanQR

import androidx.lifecycle.ViewModel
import com.skyyo.samples.extensions.log
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {

    var isProcessingAllowed = true

    fun onQrCodeRecognized(qrCode: String) {
        //handle the multiple events here by using a boolean, closing the scanner etc
        // remember to set isProcessingAllowed to true if needed, eg. in onResume()
        if (!isProcessingAllowed) return
        isProcessingAllowed = false
        log("qrCode:$qrCode")
    }
}
