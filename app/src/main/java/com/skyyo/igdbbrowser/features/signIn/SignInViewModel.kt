package com.skyyo.igdbbrowser.features.signIn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.network.calls.AuthCalls
import com.skyyo.igdbbrowser.extensions.tryOrNull
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TWITCH_OAUTH_URL = "https://id.twitch.tv/oauth2/token"
private const val TWITCH_CLIENT_ID = "s9miuod1ccn3x5vwf28n3k2up2mtxy"
private const val TWITCH_CLIENT_SECRET = "uir0le1mwpw1wswqw5sd4mx5l6lm4c"
private const val TWITCH_GRANT_TYPE = "client_credentials"

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val authCalls: AuthCalls,
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val _events = Channel<SignInEvent>(Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    //TODO move to fake sign up inputs handling sample
//    var email = handle.get<String>("email")
//        set(value) {
//            field = value
//            handle.set("email", field)
//        }

    fun onBtnSignInClick() {
        // is valid etc
        // signIn()
        goHome()
    }

    private fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            _events.send(SignInEvent.UpdateLoadingIndicator(isLoading = true))
            val response = tryOrNull {
                authCalls.signIn(
                    TWITCH_OAUTH_URL, TWITCH_CLIENT_ID,
                    TWITCH_CLIENT_SECRET,
                    TWITCH_GRANT_TYPE
                )
            }
            if (response == null) {
                _events.send(SignInEvent.NetworkError)
                _events.send(SignInEvent.UpdateLoadingIndicator(isLoading = false))
            } else {
                //save access token in data store
                goHome()
            }
        }
    }

    private fun goHome() = navigationDispatcher.emit { it.navigate(Screens.DogFeedScreen.route) }

}
