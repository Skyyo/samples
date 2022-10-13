package com.skyyo.samples.features.inputValidations.manual

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.R
import com.skyyo.samples.features.inputValidations.FocusedTextFieldKey
import com.skyyo.samples.features.inputValidations.InputErrors
import com.skyyo.samples.features.inputValidations.InputValidator
import com.skyyo.samples.features.inputValidations.ScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputValidationManualViewModel @Inject constructor(
    private val handle: SavedStateHandle
) : ViewModel() {

    val name = handle.getStateFlow("name", "")
    val nameErrorId = handle.getStateFlow<Int?>("nameErrorId", null)
    val creditCardNumber = handle.getStateFlow("ccNumber", "")
    val creditCardNumberErrorId = handle.getStateFlow<Int?>("ccNumberError", null)
    private var focusedTextField = handle["focusedTextField"] ?: FocusedTextFieldKey.NAME
        set(value) {
            field = value
            handle["focusedTextField"] = value
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (focusedTextField != FocusedTextFieldKey.NONE) focusOnLastSelectedTextField()
    }

    fun onNameEntered(input: String) {
        handle["name"] = input
        handle["nameErrorId"] = null
    }

    fun onCardNumberEntered(input: String) {
        handle["ccNumber"] = input
        handle["ccNumberError"] = null
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
        handle["nameErrorId"] = inputErrors.nameErrorId
        handle["ccNumberError"] = inputErrors.cardErrorId
    }

    private suspend fun clearFocusAndHideKeyboard() {
        _events.send(ScreenEvent.ClearFocus)
        _events.send(ScreenEvent.UpdateKeyboard(false))
        focusedTextField = FocusedTextFieldKey.NONE
    }

    private fun focusOnLastSelectedTextField() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(ScreenEvent.RequestFocus(focusedTextField))
            delay(timeMillis = 250)
            _events.send(ScreenEvent.UpdateKeyboard(true))
        }
    }
}
