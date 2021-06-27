package com.example.demo.core.useCases

import domain.Point

data class Intersection(val indexOfVertex: Int, val indexesOfEdge: List<Int>, val pointOfEdge: Point) {
    override fun toString(): String {
        return "(${pointOfEdge.x}, ${pointOfEdge.y})"
    }
}