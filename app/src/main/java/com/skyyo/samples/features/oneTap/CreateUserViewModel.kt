package com.skyyo.samples.features.oneTap

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.CODE_200
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.OneTapUser
import com.skyyo.samples.application.network.calls.OneTapCalls
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val CREATE_USER_KEY = "user"

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val oneTapCalls: OneTapCalls,
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {
    val events = Channel<UserEvent>(Channel.UNLIMITED)
    val user = handle.getStateFlow(CREATE_USER_KEY, OneTapUser.empty)

    fun setPhone(phone: String) {
        handle[CREATE_USER_KEY] = user.value.copy(phone = phone)
    }

    fun applyUpdate() = viewModelScope.launch(Dispatchers.IO) {
        val response = tryOrNull { oneTapCalls.updateUser(user.value) }
        when {
            response?.code() == CODE_200 -> {
                navigationDispatcher.emit {
                    it.popBackStack()
                    it.navigateWithObject(
                        route = Destination.OneTapAuthorised.route,
                        arguments = bundleOf(USER_KEY to user.value)
                    )
                }
            }
            else -> {
                val message = when (response) {
                    null -> "No internet"
                    else -> "Update failed with code: ${response.code()}"
                }
                events.send(UserEvent.ShowToast(message))
            }
        }
    }
}
