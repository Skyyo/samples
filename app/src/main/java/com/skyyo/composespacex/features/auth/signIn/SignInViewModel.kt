package com.skyyo.composespacex.features.auth.signIn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    //    val events = Channel<SignInEvent>(Channel.UNLIMITED)
//    var email = handle.get<String>("email")
//        set(value) {
//            field = value
//            handle.set("email", field)
//        }

    fun onBtnSignInClick() {
        goHome()
    }

    private fun goHome() = navigationDispatcher.emit { it.navigate(Screens.DogFeedScreen.route) }

}
