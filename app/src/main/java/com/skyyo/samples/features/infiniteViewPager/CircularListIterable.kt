package com.skyyo.samples.features.infiniteViewPager

interface CircularListIterable<T> {
    val pageCount: Int
    fun peekPrevious(): T
    fun peekNext(): T
    fun peekCurrent(): T
    fun moveNext()
    fun movePrevious()

    fun Int.newPageIndex(next: Boolean, count: Int = pageCount) = when {
        next -> (this + 1) % count
        else -> (count + this - 1) % count
    }
}
