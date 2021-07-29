package com.skyyo.composespacex.features.profile

import androidx.lifecycle.ViewModel
import com.skyyo.composespacex.application.EditProfileGraph
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {

    fun onBtnClick() {
        navigationDispatcher.emit { it.navigate(EditProfileGraph.route) }
    }

}
