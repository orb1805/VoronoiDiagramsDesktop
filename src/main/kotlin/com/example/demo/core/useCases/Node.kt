package com.example.demo.core.useCases

class Node<T> {

    private var parent: Node<T>?
    private var children: MutableList<Node<T>>
    var data: T

    constructor(data: T) {
        this.data = data
        parent = null
        children = mutableListOf()
    }

    constructor(data: T, parent: Node<T>) {
        this.data = data
        this.parent = parent
        children = mutableListOf()
    }

    constructor(data: T, children: MutableList<Node<T>>) {
        this.data = data
        this.parent = null
        this.children = children
    }

    constructor(data: T, parent: Node<T>, children: MutableList<Node<T>>) {
        this.data = data
        this.parent = parent
        this.children = children
    }

    fun getParent(): Node<T>? {
        return parent
    }

    fun getChild(): Node<T>? {
        return if (children.size > 0)
            children[0]
        else
            null
    }

    fun getChild(index: Int): Node<T>? {
        return if (index >= 0 && index < children.size)
            children[index]
        else
            null
    }

    fun getNumberOfChildren(): Int {
        return children.size
    }

    fun addChild(node: Node<T>) {
        children.add(node)
    }
}