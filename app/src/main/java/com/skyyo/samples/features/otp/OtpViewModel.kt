package com.skyyo.samples.features.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.R
import com.skyyo.samples.application.EditProfileGraph
import com.skyyo.samples.extensions.getStateFlow
import com.skyyo.samples.features.inputValidations.InputWrapper
import com.skyyo.samples.utils.eventDispatchers.NavigationDispatcher
import com.skyyo.samples.utils.eventDispatchers.UnauthorizedEventDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val unauthorizedEventDispatcher: UnauthorizedEventDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val otp1 = handle.getStateFlow(viewModelScope, "otp1", InputWrapper())
    val otp2 = handle.getStateFlow(viewModelScope, "otp2", InputWrapper())

    fun onOtp1Entered(input: String) {
        otp1.value = otp1.value.copy(value = input, errorId = input.validate())
    }

    fun onOtp2Entered(input: String) {
        otp2.value = otp2.value.copy(value = input, errorId = input.validate())
    }

    private fun String.validate(): Int? = when (length) {
        3 -> null
        else -> R.string.validation_error
    }

    fun onBtnClick() {
        navigationDispatcher.emit { it.navigate(EditProfileGraph.route) }
    }

    fun onSignOut() {
        viewModelScope.launch { unauthorizedEventDispatcher.requestDeauthorization() }
    }
}
