package com.skyyo.samples.features.infiniteViewPager

interface CircularListIterable<T> {
    fun peekPrevious(): T
    fun peekNext(): T
    fun peekCurrent(): T
    fun moveNext()
    fun movePrevious()
}
