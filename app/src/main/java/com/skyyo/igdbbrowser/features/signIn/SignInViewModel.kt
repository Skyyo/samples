package com.skyyo.igdbbrowser.features.signIn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.network.calls.AuthCalls
import com.skyyo.igdbbrowser.application.persistance.DataStoreManager
import com.skyyo.igdbbrowser.extensions.tryOrNull
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
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
    private val dataStoreManager: DataStoreManager,
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
        viewModelScope.launch {
            //dataStoreManager.setAccessToken("awd")
            goHome()
        }
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

    fun goMap() = navigationDispatcher.emit {
        it.navigate(Screens.MapScreen.route)
    }

    fun goForceTheme() = navigationDispatcher.emit {
        it.navigate(Screens.ForceThemeScreen.route)
    }

    fun goBottomSheetDestination() = navigationDispatcher.emit {
        it.navigate(Screens.BottomSheetScreen.route)
    }

    fun goBottomSheetsContainer() = navigationDispatcher.emit {
        it.navigate(Screens.ModalBottomSheetScreen.route)
    }

    fun goBottomSheetsScaffold() = navigationDispatcher.emit {
        it.navigate(Screens.BottomSheetScaffoldScreen.route)
    }

    fun goViewPager() = navigationDispatcher.emit {
        it.navigate(Screens.ViewPagerScreen.route)
    }

    private fun goHome() = navigationDispatcher.emit {


//        it.popBackStack(Screens.DogFeedScreen.route,false)

//        it.graph.setStartDestination(Screens.DogFeedScreen.route)
        it.navigate(Screens.DogFeedScreen.route) {
//            popUpTo(Screens.AuthScreen.route) {
//                inclusive = true
//            }
        }


//        it.navigate(Screens.DogFeedScreen.route) {
//            // Pop up to the start destination of the graph to
//            // avoid building up a large stack of destinations
//            // on the back stack as users select items
//            popUpTo(it.graph.findStartDestination().id) {
//                saveState = true
//                inclusive = true
//            }
//            // Avoid multiple copies of the same destination when
//            // reselecting the same item
//            launchSingleTop = true
//            // Restore state when reselecting a previously selected item
//            restoreState = true
//        }
    }

}
