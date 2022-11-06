package com.skyyo.samples.features.infiniteViewPager

import androidx.compose.runtime.Immutable

@Immutable
class CircularList<T>(
    items: List<T>,
    currentIndex: Int = 0
) : CircularListIterable<T>, Iterable<T>, AbstractList<T>() {
    private var currentNode: Node<T>
    private var firstNode: Node<T>

    override val size: Int

    override fun get(index: Int): T {
        var node = firstNode
        repeat(index) {
            node = node.next!!
        }
        return node.previous!!.current
    }

    init {
        require(items.size > 1)
        require(currentIndex in items.indices)
        val zeroBasedItems = items.subList(currentIndex, items.size) + items.subList(0, currentIndex)
        size = zeroBasedItems.size
        firstNode = Node(current = zeroBasedItems[0])
        currentNode = firstNode
        var node = firstNode
        for (i in 1 until zeroBasedItems.size) {
            val newNode = Node(previous = node, current = zeroBasedItems[i])
            node.next = newNode
            node = newNode
        }
        node.next = firstNode
        firstNode.previous = node
    }

    override fun peekPrevious(): T = currentNode.previous!!.current
    override fun peekNext(): T = currentNode.next!!.current
    override fun peekCurrent(): T = currentNode.current

    override fun moveNext() {
        currentNode = currentNode.next!!
    }

    override fun movePrevious() {
        currentNode = currentNode.previous!!
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var index = 0
        private var node = firstNode

        override fun hasNext(): Boolean = index < size

        override fun next(): T {
            index++
            if (index == size + 1) throw NoSuchElementException()
            node = node.next!!
            return node.current
        }
    }

    private class Node<T>(var previous: Node<T>? = null, val current: T, var next: Node<T>? = null)
}
