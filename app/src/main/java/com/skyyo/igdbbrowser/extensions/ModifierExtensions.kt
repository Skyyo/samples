package com.skyyo.igdbbrowser.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast

inline fun <T> tryOrNull(f: () -> T) =
    try {
        f()
    } catch (e: Exception) {
        log("exception: $e")
        null
    }

fun log(message: String, tag: String = "vovk") {
    Log.d(tag, message)
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
