package com.skyyo.composespacex.extensions

import android.util.Log

fun log(message: String, tag: String = "vovk") = run {
    Log.v(tag, message)
}