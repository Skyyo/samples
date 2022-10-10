package com.skyyo.samples.features.navigationCores.bottomBar

import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

const val WITH_BOTTOM_BAR_KEY = "withBottomBar"
private const val WAIT_FOR_VERIFICATION_SERVER = 5000L

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    val navigationDispatcher: NavigationDispatcher,
    val catCalls: CatCalls
) : ViewModel() {
    suspend fun waitForVerificationServerToWakeUp() {
        do {
            delay(WAIT_FOR_VERIFICATION_SERVER)
            val response = catCalls.pingVerificationServer("https://abrupt-tabby-medusaceratops.glitch.me/ping")
        } while (response.string() != "{}")
    }
}
