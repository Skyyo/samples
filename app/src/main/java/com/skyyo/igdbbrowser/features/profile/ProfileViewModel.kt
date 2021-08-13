package com.skyyo.igdbbrowser.features.profile

import androidx.lifecycle.ViewModel
import com.skyyo.igdbbrowser.application.EditProfileGraph
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
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
