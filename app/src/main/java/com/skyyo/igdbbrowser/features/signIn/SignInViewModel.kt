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


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val authCalls: AuthCalls,
    private val dataStoreManager: DataStoreManager,
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val _events = Channel<SignInEvent>()
    val events = _events.receiveAsFlow()

    fun signIn() = viewModelScope.launch(Dispatchers.IO) {
        _events.send(SignInEvent.UpdateLoadingIndicator(isLoading = true))
        val response = tryOrNull { authCalls.signIn() }
        if (response == null) {
            _events.send(SignInEvent.NetworkError)
            _events.send(SignInEvent.UpdateLoadingIndicator(isLoading = false))
        } else {
            dataStoreManager.setAccessToken(response.accessToken)
            goHome()
        }
    }

    private fun goHome() = navigationDispatcher.emit {
        it.navigate(Screens.DogFeedScreen.route) {
            popUpTo(Screens.AuthScreen.route) {
                inclusive = true
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

    fun goNavWithResultSample() = navigationDispatcher.emit {
        it.navigate(Screens.DogFeedScreen.route)
    }

    fun goStickyHeaders() = navigationDispatcher.emit {
        it.navigate(Screens.ListsScreen.route)
    }

}
