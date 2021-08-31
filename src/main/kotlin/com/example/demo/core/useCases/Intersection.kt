package com.example.demo.core.useCases

import com.example.demo.core.domain.Point

data class Intersection(var indexOfVertex: Int, var indexesOfEdge: List<Int>, var pointOfEdge: Point) {
    override fun toString(): String {
        return "(${pointOfEdge.x}, ${pointOfEdge.y})"
    }
}