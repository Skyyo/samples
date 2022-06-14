package com.skyyo.samples.features.customView

import androidx.lifecycle.ViewModel
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CustomViewVM @javax.inject.Inject constructor(
    private val navigationDispatcher: NavigationDispatcher
): ViewModel() {
    fun goBack() = navigationDispatcher.emit { it.popBackStack() }
}