package com.skyyo.samples.features.oneTap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.skyyo.samples.application.models.OneTapUser
import com.skyyo.samples.application.network.calls.OneTapCalls
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val USER_KEY = "user"

@HiltViewModel
class UserViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher,
    private val oneTapCalls: OneTapCalls
) : ViewModel() {
    val events = Channel<UserEvent>(Channel.UNLIMITED)
    val user: OneTapUser = handle[USER_KEY]!!

    fun signOut(client: SignInClient, deleteUser: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        if (deleteUser) oneTapCalls.deleteUser(user.id)
        try {
            client.signOut().await()
        } catch (e: Exception) {
            events.send(UserEvent.ShowToast("sign out failed"))
        }
        navigationDispatcher.emit { it.popBackStack() }
    }
}
