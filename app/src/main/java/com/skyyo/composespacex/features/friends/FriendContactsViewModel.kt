package com.skyyo.composespacex.features.friends

import androidx.lifecycle.ViewModel
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {

    fun popToFriendsList() {
        navigationDispatcher.emit { it.popBackStack(Screens.FriendDetails.route, true) }
    }

}
