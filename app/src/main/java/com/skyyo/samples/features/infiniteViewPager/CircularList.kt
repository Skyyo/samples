package com.skyyo.samples.features.infiniteViewPager

import androidx.compose.runtime.Immutable
import java.util.LinkedList

@Immutable
class CircularList<T>(items: List<T>, currentIndex: Int = 0) : CircularListIterable<T> {
    private var iterationIndex = 0
    private val dataHolders: LinkedList<DataHolder<T>>

    init {
        require(items.size > 1)
        require(currentIndex in items.indices)
        val zeroBasedItems = items.subList(currentIndex, items.size) + items.subList(0, currentIndex)
        dataHolders = LinkedList(
            zeroBasedItems.mapIndexed { index, item ->
                DataHolder(
                    previous = items[index.newPageIndex(next = false, count = items.size)],
                    current = item,
                    next = items[index.newPageIndex(next = true, count = items.size)]
                )
            }
        )
    }

    override val pageCount: Int = dataHolders.size
    override fun peekPrevious(): T = dataHolders[iterationIndex].previous
    override fun peekNext(): T = dataHolders[iterationIndex].next
    override fun peekCurrent(): T = dataHolders[iterationIndex].current

    override fun moveNext() {
        iterationIndex = iterationIndex.newPageIndex(next = true)
    }

    override fun movePrevious() {
        iterationIndex = iterationIndex.newPageIndex(next = false)
    }

    private class DataHolder<T>(val previous: T, val current: T, val next: T)
}
