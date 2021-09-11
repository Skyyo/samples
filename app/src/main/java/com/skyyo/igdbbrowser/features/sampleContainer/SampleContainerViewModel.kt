package com.skyyo.igdbbrowser.features.sampleContainer

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
class SampleContainerViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val authCalls: AuthCalls,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {

    private val _events = Channel<SampleContainerScreenEvent>()
    val events = _events.receiveAsFlow()

    fun signIn() = viewModelScope.launch(Dispatchers.IO) {
        _events.send(SampleContainerScreenEvent.UpdateLoadingIndicator(isLoading = true))
        val response = tryOrNull { authCalls.signIn() }
        if (response == null) {
            _events.send(SampleContainerScreenEvent.NetworkError)
            _events.send(SampleContainerScreenEvent.UpdateLoadingIndicator(isLoading = false))
        } else {
            dataStoreManager.setAccessToken(response.accessToken)
            goHome()
        }
    }

    private fun goHome() = navigationDispatcher.emit {
        it.navigate(Screens.DogFeed.route) {
            popUpTo(Screens.SampleContainer.route) {
                inclusive = true
            }
        }
    }

    fun goMap() = navigationDispatcher.emit {
        it.navigate(Screens.Map.route)
    }

    fun goForceTheme() = navigationDispatcher.emit {
        it.navigate(Screens.ForceTheme.route)
    }

    fun goCameraX() = navigationDispatcher.emit {
        it.navigate(Screens.CameraX.route)
    }

    fun goBottomSheetDestination() = navigationDispatcher.emit {
        it.navigate(Screens.BottomSheet.route)
    }

    fun goBottomSheetsContainer() = navigationDispatcher.emit {
        it.navigate(Screens.ModalBottomSheet.route)
    }

    fun goBottomSheetsScaffold() = navigationDispatcher.emit {
        it.navigate(Screens.BottomSheetScaffold.route)
    }

    fun goViewPager() = navigationDispatcher.emit {
        it.navigate(Screens.ViewPager.route)
    }

    fun goNavWithResultSample() = navigationDispatcher.emit {
        it.navigate(Screens.DogFeed.route)
    }

    fun goStickyHeaders() = navigationDispatcher.emit {
        it.navigate(Screens.Lists.route)
    }

    fun goInputAutoValidation() = navigationDispatcher.emit {
        it.navigate(Screens.InputValidationAuto.route)
    }

    fun goInputDebounceValidation() = navigationDispatcher.emit {
        it.navigate(Screens.InputValidationDebounce.route)
    }

    fun goInputManualValidation() = navigationDispatcher.emit {
        it.navigate(Screens.InputValidationManual.route)
    }

    fun goAnimations() = navigationDispatcher.emit {
        it.navigate(Screens.Animations.route)
    }

    fun goOtp() = navigationDispatcher.emit {
        it.navigate(Screens.Otp.route)
    }

    fun goNestedHorizontalLists() = navigationDispatcher.emit {
        it.navigate(Screens.NestedHorizontalLists.route)
    }
    fun goAutoScroll() = navigationDispatcher.emit {
        it.navigate(Screens.AutoScroll.route)
    }
}
