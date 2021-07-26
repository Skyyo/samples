package com.skyyo.composespacex.features.friends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    fun onGoFriendContactClick() {
        goFriendContact()
    }

    private fun goFriendContact() =
        navigationDispatcher.emit { it.navigate(Screens.FriendContacts.route) }

}
