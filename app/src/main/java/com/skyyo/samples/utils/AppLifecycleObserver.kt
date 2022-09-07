package com.skyyo.samples.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.skyyo.samples.application.activity.MILLIS_IN_SECOND
import com.skyyo.samples.application.activity.SESSION_EXTRA_TIME_SECONDS
import com.skyyo.samples.application.activity.SESSION_MAIN_TIME_SECONDS
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant

typealias OnMaximumIdlingTimeInBackgroundReached = () -> Unit

@OptIn(DelicateCoroutinesApi::class)
class AppLifecycleObserver(
    private val onMaximumIdlingTimeInBackgroundReached: OnMaximumIdlingTimeInBackgroundReached
) : DefaultLifecycleObserver {

    var lastGoToBackgroundTime: Long? = null

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        @Suppress("GlobalCoroutineUsage")
        GlobalScope.launch(Dispatchers.IO) {
            val currentTimeInMilliseconds = currentTimeInMilliseconds()
            val goToBackgroundTime = lastGoToBackgroundTime ?: currentTimeInMilliseconds
            val timeInBackground = currentTimeInMilliseconds - goToBackgroundTime
            val sessionTotalTime = SESSION_MAIN_TIME_SECONDS + SESSION_EXTRA_TIME_SECONDS
            if (timeInBackground > sessionTotalTime * MILLIS_IN_SECOND) {
                onMaximumIdlingTimeInBackgroundReached()
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        lastGoToBackgroundTime = currentTimeInMilliseconds()
    }

    private fun currentTimeInMilliseconds() = Instant.now().toEpochMilli()
}
