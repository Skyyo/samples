package com.skyyo.composespacex.features.friends

import androidx.lifecycle.ViewModel
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {

    fun goFriendDetails() {
        navigationDispatcher.emit { it.navigate(Screens.FriendDetails.route) }
    }

}
