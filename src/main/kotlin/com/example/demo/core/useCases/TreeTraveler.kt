package com.example.demo.core.useCases

class TreeTraveler<T>(private val parent: Node<T>) {

    private lateinit var tmpMap: MutableMap<Int, MutableList<T>>

    fun breadthTravel(): List<T> {
        tmpMap = mutableMapOf()
        breadthTravel(parent, 0)
        val tmp = tmpMap.toSortedMap()
        val result = mutableListOf<T>()
        for ((_, value) in tmp)
            for (node in value)
                result.add(node)
        return result
    }

    private fun breadthTravel(parent: Node<T>, level: Int) {
        if (tmpMap.containsKey(level))
            tmpMap[level]!!.add(parent.data)
        else
            tmpMap[level] = mutableListOf(parent.data)
        val size = parent.getNumberOfChildren()
        for (i in 0 until size) {
            breadthTravel(parent.getChild(i)!!, level + 1)
        }
    }

    fun fromLeavesToRootTravel(): List<T> {
        val result = breadthTravel()
        return result.reversed()
    }

}