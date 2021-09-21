package com.skyyo.samples.features.pagination.common

import com.skyyo.samples.R

sealed class PagingException(open val stringRes: Int) : Throwable() {
    object NetworkError : PagingException(R.string.network_error)
    class Error(stringRes: Int) : PagingException(stringRes)
}