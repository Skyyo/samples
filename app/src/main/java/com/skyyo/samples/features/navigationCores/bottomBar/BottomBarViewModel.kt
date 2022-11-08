package com.skyyo.samples.features.navigationCores.bottomBar

import androidx.lifecycle.ViewModel
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val WITH_BOTTOM_BAR_KEY = "withBottomBar"

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    val navigationDispatcher: NavigationDispatcher
) : ViewModel()
