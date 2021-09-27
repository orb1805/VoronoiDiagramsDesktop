package com.example.demo.core.domain

class CircleList<T>(override var size: Int) : MutableList<T> {

    private val list = mutableListOf<T>()

    override fun contains(element: T): Boolean =
        list.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        list.containsAll(elements)

    override fun get(index: Int): T {
        val newIndex =
            when {
                index > lastIndex -> index % lastIndex - 1
                index < 0 -> lastIndex + (index % lastIndex) + 1
                else -> index
            }
        println(index)
        println(newIndex)
        println()
        return list[newIndex]
    }

    override fun indexOf(element: T): Int =
        list.indexOf(element)

    override fun isEmpty(): Boolean =
        list.isEmpty()

    override fun iterator(): MutableIterator<T> =
        list.iterator()

    override fun lastIndexOf(element: T): Int =
        list.lastIndexOf(element)

    override fun add(element: T): Boolean = list
        .add(element)
        .also { this.size = list.size }

    override fun add(index: Int, element: T) = list
        .add(index, element)
        .also { this.size = list.size }

    override fun addAll(index: Int, elements: Collection<T>): Boolean = list
        .addAll(index, elements)
        .also { this.size = list.size }

    override fun addAll(elements: Collection<T>): Boolean = list
        .addAll(elements)
        .also { this.size = list.size }

    override fun clear() = list
        .clear()
        .also { this.size = list.size }

    override fun listIterator(): MutableListIterator<T> =
        list.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> =
        list.listIterator(index)

    override fun remove(element: T): Boolean = list
        .remove(element)
        .also { this.size = list.size }

    override fun removeAll(elements: Collection<T>): Boolean = list
        .removeAll(elements)
        .also { this.size = list.size }

    override fun removeAt(index: Int): T = list
        .removeAt(index)
        .also { this.size = list.size }

    override fun retainAll(elements: Collection<T>): Boolean =
        list.retainAll(elements)

    override fun set(index: Int, element: T): T = list
        .set(index, element)
        .also { this.size = list.size }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        list.subList(fromIndex, toIndex)
}

fun <T> mutableCircleListOf(): CircleList<T> = CircleList(0)