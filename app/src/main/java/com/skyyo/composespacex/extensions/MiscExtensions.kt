package com.skyyo.composespacex.extensions

import android.util.Log

inline fun <T> tryOrNull(f: () -> T) =
    try {
        f()
    } catch (e: Exception) {
        log("exception: $e")
        null
    }

fun log(message: String, tag: String = "vovk") = run {
    Log.v(tag, message)
}