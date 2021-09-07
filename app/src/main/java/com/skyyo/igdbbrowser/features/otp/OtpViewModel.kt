package com.skyyo.igdbbrowser.features.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.application.EditProfileGraph
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import com.skyyo.igdbbrowser.utils.eventDispatchers.UnauthorizedEventDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val unauthorizedEventDispatcher: UnauthorizedEventDispatcher,
) : ViewModel() {

    fun onBtnClick() {
        navigationDispatcher.emit { it.navigate(EditProfileGraph.route) }
    }

    fun onSignOut() {
        viewModelScope.launch { unauthorizedEventDispatcher.requestDeauthorization() }
    }
}
