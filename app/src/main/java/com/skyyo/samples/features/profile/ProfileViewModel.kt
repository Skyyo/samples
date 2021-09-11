package com.skyyo.samples.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.EditProfileGraph
import com.skyyo.samples.utils.eventDispatchers.NavigationDispatcher
import com.skyyo.samples.utils.eventDispatchers.UnauthorizedEventDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
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
