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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FormValidationViewModel @Inject constructor(handle: SavedStateHandle) : ViewModel() {

    val name = handle.getStateFlow(viewModelScope, "name", Input())
    val creditCardNumber = handle.getStateFlow(viewModelScope, "ccNumber", Input())
    val password = handle.getStateFlow(viewModelScope, "password", Input())
    val areInputsValid = combine(name, creditCardNumber, password) { name, cardNumber, password ->
        val nameErrorId = FormValidation.getNameErrorIdOrNull(name.value)
        val cardErrorId = FormValidation.getCardNumberErrorIdOrNull(cardNumber.value)
        val passwordErrorId = FormValidation.getCardNumberErrorIdOrNull(cardNumber.value)
        nameErrorId == null && cardErrorId == null && passwordErrorId == null
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _events = Channel<FormValidationsRealTimeEvent>()
    val events = _events.receiveAsFlow()

    fun onNameEntered(input: String) {
        val errorId = FormValidation.getNameErrorIdOrNull(input)
        name.tryEmit(name.value.copy(value = input, errorId = errorId))
    }

    fun onCardNumberEntered(input: String) {
        val errorId = if (input.isEmpty()) R.string.validation_error else null
        creditCardNumber.tryEmit(creditCardNumber.value.copy(value = input, errorId = errorId))
    }

    fun onPasswordEntered(input: String) {
        val errorId = if (input.length < 8) R.string.validation_error else null
        password.tryEmit(password.value.copy(value = input, errorId = errorId))
    }

    fun onSignUpClick() {
        viewModelScope.launch(Dispatchers.Default) {
            val stringId = when (name.value.errorId) {
                null -> R.string.success
                else -> R.string.validation_error
            }
            _events.send(FormValidationsRealTimeEvent.ShowToast(stringId))
        }
    }
}
