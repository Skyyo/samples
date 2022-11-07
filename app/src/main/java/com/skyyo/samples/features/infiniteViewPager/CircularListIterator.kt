package com.skyyo.samples.features.infiniteViewPager

interface CircularListIterator<T> {
    fun peekPrevious(): T
    fun peekNext(): T
    fun peekCurrent(): T
    fun moveNext()
    fun movePrevious()
}
