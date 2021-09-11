package com.skyyo.igdbbrowser.features.inputValidations.manual

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.extensions.getStateFlow
import com.skyyo.igdbbrowser.features.inputValidations.FocusedTextFieldKey
import com.skyyo.igdbbrowser.features.inputValidations.InputErrors
import com.skyyo.igdbbrowser.features.inputValidations.InputValidator
import com.skyyo.igdbbrowser.features.inputValidations.ScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FormValidationManualViewModel @Inject constructor(private val handle: SavedStateHandle) :
    ViewModel() {

    val name = handle.getStateFlow(viewModelScope, "name", "")
    val nameErrorId = handle.getStateFlow<Int?>(viewModelScope, "nameErrorId", null)
    val creditCardNumber = handle.getStateFlow(viewModelScope, "ccNumber", "")
    val creditCardNumberErrorId = handle.getStateFlow<Int?>(viewModelScope, "ccNumberError", null)
    private var focusedTextField = handle.get("focusedTextField") ?: FocusedTextFieldKey.NAME
        set(value) {
            field = value
            handle.set("focusedTextField", value)
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (focusedTextField != FocusedTextFieldKey.NONE) focusOnLastSelectedTextField()
    }

    fun onNameEntered(input: String) {
        name.value = input
        nameErrorId.value = null
    }

    fun onCardNumberEntered(input: String) {
        creditCardNumber.value = input
        creditCardNumberErrorId.value = null
    }

    fun onTextFieldFocusChanged(key: FocusedTextFieldKey, isFocused: Boolean) {
        focusedTextField = if (isFocused) key else FocusedTextFieldKey.NONE
    }

    fun onNameImeActionClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(ScreenEvent.MoveFocus())
        }
    }

    fun onContinueClick() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val inputErrors = getInputErrorsOrNull()) {
                null -> {
                    clearFocusAndHideKeyboard()
                    _events.send(ScreenEvent.ShowToast(R.string.success))
                }
                else -> displayInputErrors(inputErrors)
            }
        }
    }

    private fun getInputErrorsOrNull(): InputErrors? {
        val nameErrorId = InputValidator.getNameErrorIdOrNull(name.value)
        val cardErrorId = InputValidator.getCardNumberErrorIdOrNull(creditCardNumber.value)
        return if (nameErrorId == null && cardErrorId == null) {
            null
        } else {
            InputErrors(nameErrorId, cardErrorId)
        }
    }

    private fun displayInputErrors(inputErrors: InputErrors) {
        nameErrorId.value = inputErrors.nameErrorId
        creditCardNumberErrorId.value = inputErrors.cardErrorId
    }

    private suspend fun clearFocusAndHideKeyboard() {
        _events.send(ScreenEvent.ClearFocus)
        _events.send(ScreenEvent.UpdateKeyboard(false))
        focusedTextField = FocusedTextFieldKey.NONE
    }

    private fun focusOnLastSelectedTextField() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(ScreenEvent.RequestFocus(focusedTextField))
            delay(250)
            _events.send(ScreenEvent.UpdateKeyboard(true))
        }
    }
}

