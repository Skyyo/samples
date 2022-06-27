package com.skyyo.samples.features.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.R
import com.skyyo.samples.application.ProfileGraph
import com.skyyo.samples.features.inputValidations.InputWrapper
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val input = handle.getStateFlow("otp", InputWrapper())

    fun onOtpEntered(otp: String) {
        handle["otp"] = input.value.copy(value = otp, errorId = null)
    }

    fun onBtnClick() {
        if (input.value.value == "0".repeat(OTP_LENGTH)) {
            navigationDispatcher.emit { it.navigate(ProfileGraph.route) }
        } else {
            handle["otp"] = input.value.copy(errorId = R.string.error_otp_wrong)
        }
    }

    companion object {
        const val OTP_LENGTH = 6
    }
}
