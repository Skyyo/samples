package com.skyyo.samples.features.otp

import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.ProfileGraph
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {

    fun onBtnClick() {
        navigationDispatcher.emit { it.navigate(ProfileGraph.route) }
    }

}
