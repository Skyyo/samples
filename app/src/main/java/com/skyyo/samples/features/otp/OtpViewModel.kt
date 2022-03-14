package com.skyyo.samples.features.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.ProfileGraph
import com.skyyo.samples.extensions.getStateFlow
import com.skyyo.samples.features.inputValidations.InputWrapper
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.skyyo.samples.R

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    handle: SavedStateHandle,
) : ViewModel() {

    val input = handle.getStateFlow(viewModelScope, "otp", InputWrapper())

    fun onOtpEntered(otp: String) {
        input.value = input.value.copy(value = otp, errorId = null)
    }

    fun onBtnClick() {
        if (input.value.value == "0".repeat(OTP_LENGTH)) {
            navigationDispatcher.emit { it.navigate(ProfileGraph.route) }
        } else {
            input.value = input.value.copy(errorId = R.string.error_otp_wrong)
        }
    }

    companion object {
        const val OTP_LENGTH = 6
    }
}
