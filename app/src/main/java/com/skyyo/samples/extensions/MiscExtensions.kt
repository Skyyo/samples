package com.skyyo.samples.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.text.layoutDirection
import com.skyyo.samples.features.languagePicker.SUPPORTED_LANGUAGES_ARRAY

inline fun <T> tryOrNull(f: () -> T) =
    try {
        f()
    } catch (e: Exception) {
        // log("exception: $e")
        null
    }

fun log(message: String, tag: String = "vovk") {
    Log.d(tag, message)
}

fun Context.toast(messageId: Int) {
    Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.goAppPermissions() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:${applicationContext.packageName}")
    }
    startActivity(intent)
}

// https://issuetracker.google.com/issues/236538894
@Composable
fun FixInAppLanguageSwitchLayoutDirection(content: @Composable () -> Unit) {
    val appLocale = AppCompatDelegate.getApplicationLocales().getFirstMatch(SUPPORTED_LANGUAGES_ARRAY) ?: LocalConfiguration.current.locales[0]
    val appLocaleDirection = when (appLocale.layoutDirection) {
        View.LAYOUT_DIRECTION_RTL -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
    CompositionLocalProvider(LocalLayoutDirection provides appLocaleDirection) {
        content()
    }
}

fun Context.getEnclosingActivity(): Activity = when (val tempContext = this) {
    is Activity -> tempContext
    else -> {
        val contextWrapper = this as ContextWrapper
        var activityContext = contextWrapper.baseContext
        while (activityContext !is Activity) {
            activityContext = (activityContext as ContextWrapper).baseContext
        }
        activityContext
    }
}
