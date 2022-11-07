package com.skyyo.samples.features.infiniteViewPager

import androidx.compose.runtime.Immutable

@Immutable
class CircularList<T>(items: List<T>) : AbstractList<T>() {
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
        size = items.size
        firstNode = Node(current = items[0])
        var node = firstNode
        for (i in 1 until items.size) {
            val newNode = Node(previous = node, current = items[i])
            node.next = newNode
            node = newNode
        }
        node.next = firstNode
        firstNode.previous = node
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

    fun circularListIterator(): CircularListIterator<T> = object : CircularListIterator<T> {
        private var currentNode: Node<T> = firstNode

        override fun peekPrevious(): T = currentNode.previous!!.current
        override fun peekNext(): T = currentNode.next!!.current
        override fun peekCurrent(): T = currentNode.current

        override fun moveNext() {
            currentNode = currentNode.next!!
        }

        override fun movePrevious() {
            currentNode = currentNode.previous!!
        }
    }

    private class Node<T>(var previous: Node<T>? = null, val current: T, var next: Node<T>? = null)
}
