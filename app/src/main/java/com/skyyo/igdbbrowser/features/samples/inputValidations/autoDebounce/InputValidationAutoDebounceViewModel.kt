package com.skyyo.igdbbrowser.features.samples.inputValidations.autoDebounce

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.extensions.getStateFlow
import com.skyyo.igdbbrowser.features.samples.inputValidations.*
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

    val name = handle.getStateFlow(viewModelScope, "name", InputWrapper())
    val creditCardNumber = handle.getStateFlow(viewModelScope, "creditCardNumber", InputWrapper())
    val areInputsValid = combine(name, creditCardNumber) { name, cardNumber ->
        name.value.isNotEmpty() && name.errorId == null &&
                cardNumber.value.isNotEmpty() && cardNumber.errorId == null
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)
    private var focusedTextField = handle.get("focusedTextField") ?: FocusedTextFieldKey.NAME
        set(value) {
            field = value
            handle.set("focusedTextField", value)
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
                                    name.emit(name.value.copy(value = event.input, errorId = null))
                                }
                                else -> name.emit(name.value.copy(value = event.input))
                            }
                        }
                        is UserInputEvent.CreditCard -> {
                            when (InputValidator.getCardNumberErrorIdOrNull(event.input)) {
                                null -> {
                                    creditCardNumber.emit(
                                        creditCardNumber.value.copy(
                                            value = event.input,
                                            errorId = null
                                        )
                                    )
                                }
                                else -> creditCardNumber.emit(creditCardNumber.value.copy(value = event.input))
                            }
                        }
                    }
                }
                .debounce(350)
                .collect { event ->
                    when (event) {
                        is UserInputEvent.Name -> {
                            val errorId = InputValidator.getNameErrorIdOrNull(event.input)
                            name.emit(name.value.copy(errorId = errorId))
                        }
                        is UserInputEvent.CreditCard -> {
                            val errorId = InputValidator.getCardNumberErrorIdOrNull(event.input)
                            creditCardNumber.emit(creditCardNumber.value.copy(errorId = errorId))
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

    private suspend fun displayInputErrors(inputErrors: InputErrors) {
        name.emit(name.value.copy(errorId = inputErrors.nameErrorId))
        creditCardNumber.emit(creditCardNumber.value.copy(errorId = inputErrors.cardErrorId))
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

