package com.skyyo.igdbbrowser.features.samples.formValidation.manual

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.application.models.local.Input
import com.skyyo.igdbbrowser.extensions.getStateFlow
import com.skyyo.igdbbrowser.features.samples.formValidation.FormValidation
import com.skyyo.igdbbrowser.features.samples.formValidation.realTime.FormValidationsRealTimeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private class InputErrors(
    val nameErrorId: Int?,
    val cardErrorId: Int?,
    val passwordErrorId: Int?
)

@HiltViewModel
class FormValidationManualViewModel @Inject constructor(handle: SavedStateHandle) : ViewModel() {

    val name = handle.getStateFlow(viewModelScope, "name", Input())
    val creditCardNumber = handle.getStateFlow(viewModelScope, "ccNumber", Input())
    val password = handle.getStateFlow(viewModelScope, "password", Input())

    private val _events = Channel<FormValidationsRealTimeEvent>()
    val events = _events.receiveAsFlow()

    fun onNameEntered(input: String) {
        name.tryEmit(name.value.copy(value = input, errorId = null))
    }

    fun onCardNumberEntered(input: String) {
        creditCardNumber.tryEmit(creditCardNumber.value.copy(value = input, errorId = null))
    }

    fun onPasswordEntered(input: String) {
        password.tryEmit(password.value.copy(value = input, errorId = null))
    }

    private fun getInputErrors(): InputErrors? {
        val nameErrorId = FormValidation.getNameErrorIdOrNull(name.value.value)
        val cardErrorId = FormValidation.getCardNumberErrorIdOrNull(creditCardNumber.value.value)
        val passwordErrorId = FormValidation.getPasswordErrorIdOrNull(password.value.value)
        return if (nameErrorId == null && cardErrorId == null && passwordErrorId == null) {
            null
        } else {
            InputErrors(nameErrorId, cardErrorId, passwordErrorId)
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch(Dispatchers.Default) {
            val inputErrors = getInputErrors()
            if (inputErrors == null) {
                _events.send(FormValidationsRealTimeEvent.ShowToast(R.string.success))
            } else {
                name.emit(name.value.copy(errorId = inputErrors.nameErrorId))
                creditCardNumber.emit(creditCardNumber.value.copy(errorId = inputErrors.cardErrorId))
                password.emit(password.value.copy(errorId = inputErrors.passwordErrorId))
            }
        }
    }
}

