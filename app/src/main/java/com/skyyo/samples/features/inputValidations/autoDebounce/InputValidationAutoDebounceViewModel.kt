package com.skyyo.samples.features.inputValidations.autoDebounce

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.R
import com.skyyo.samples.features.inputValidations.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(FlowPreview::class)
@HiltViewModel
class InputValidationAutoDebounceViewModel @Inject constructor(
    private val handle: SavedStateHandle
) : ViewModel() {

    val name = handle.getStateFlow("name", InputWrapper())
    val creditCardNumber = handle.getStateFlow("creditCardNumber", InputWrapper())
    val areInputsValid = combine(name, creditCardNumber) { name, cardNumber ->
        name.value.isNotEmpty() && name.errorId == null &&
                cardNumber.value.isNotEmpty() && cardNumber.errorId == null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)
    private var focusedTextField = handle["focusedTextField"] ?: FocusedTextFieldKey.NAME
        set(value) {
            field = value
            handle["focusedTextField"] = value
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()
    private val inputEvents = Channel<UserInputEvent>(Channel.CONFLATED)

    init {
        observeUserInputEvents()
        if (focusedTextField != FocusedTextFieldKey.NONE) focusOnLastSelectedTextField()
    }

    private fun observeUserInputEvents() {
        viewModelScope.launch(Dispatchers.Default) {
            inputEvents.receiveAsFlow()
                .onEach { event ->
                    when (event) {
                        is UserInputEvent.Name -> {
                            when (InputValidator.getNameErrorIdOrNull(event.input)) {
                                null -> {
                                    handle["name"] =
                                        name.value.copy(value = event.input, errorId = null)
                                }
                                else -> handle["name"] = name.value.copy(value = event.input)
                            }
                        }
                        is UserInputEvent.CreditCard -> {
                            when (InputValidator.getCardNumberErrorIdOrNull(event.input)) {
                                null -> {
                                    handle["creditCardNumber"] = creditCardNumber.value.copy(
                                        value = event.input,
                                        errorId = null
                                    )
                                }
                                else -> {
                                    handle["creditCardNumber"] =
                                        creditCardNumber.value.copy(value = event.input)
                                }
                            }
                        }
                    }
                }
                .debounce(350)
                .collect { event ->
                    when (event) {
                        is UserInputEvent.Name -> {
                            val errorId = InputValidator.getNameErrorIdOrNull(event.input)
                            handle["name"] = name.value.copy(errorId = errorId)
                        }
                        is UserInputEvent.CreditCard -> {
                            val errorId = InputValidator.getCardNumberErrorIdOrNull(event.input)
                            handle["creditCardNumber"] =
                                creditCardNumber.value.copy(errorId = errorId)
                        }
                    }
                }
        }
    }

    fun onNameEntered(input: String) {
        viewModelScope.launch(Dispatchers.Default) {
            inputEvents.send(UserInputEvent.Name(input))
        }
    }

    fun onCardNumberEntered(input: String) {
        viewModelScope.launch(Dispatchers.Default) {
            inputEvents.send(UserInputEvent.CreditCard(input))
        }
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
        val nameErrorId = InputValidator.getNameErrorIdOrNull(name.value.value)
        val cardErrorId = InputValidator.getCardNumberErrorIdOrNull(creditCardNumber.value.value)
        return if (nameErrorId == null && cardErrorId == null) {
            null
        } else {
            InputErrors(nameErrorId, cardErrorId)
        }
    }

    private fun displayInputErrors(inputErrors: InputErrors) {
        handle["name"] = name.value.copy(errorId = inputErrors.nameErrorId)
        handle["creditCardNumber"] = creditCardNumber.value.copy(errorId = inputErrors.cardErrorId)
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

