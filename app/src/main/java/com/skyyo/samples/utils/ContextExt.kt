package com.skyyo.samples.utils

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.internal.managers.ViewComponentManager

fun Context.getLifecycle() = when(this) {
    is ViewComponentManager.FragmentContextWrapper -> {
        val wrapperInstance = this
        val fragmentMethod = ViewComponentManager.FragmentContextWrapper::class.java.getDeclaredMethod("getFragment")
        fragmentMethod.isAccessible = true
        (fragmentMethod.invoke(wrapperInstance) as Fragment).lifecycle
    }
    else -> (this as ComponentActivity).lifecycle
}