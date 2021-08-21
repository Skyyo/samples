package com.skyyo.igdbbrowser.features.samples.formValidation

import com.skyyo.igdbbrowser.R

object FormValidation {

    fun getNameErrorIdOrNull(input: String): Int? {
        return when {
            input.isEmpty() -> R.string.validation_error
            //...
            else -> null
        }
    }

    fun getCardNumberErrorIdOrNull(input: String): Int? {
        return when {
            input.isEmpty() -> R.string.validation_error
            //...
            else -> null
        }
    }

    fun getPasswordErrorIdOrNull(input: String): Int? {
        return when {
            input.isEmpty() -> R.string.validation_error
            input.length < 8 -> R.string.validation_error
            //...
            else -> null
        }
    }
}
