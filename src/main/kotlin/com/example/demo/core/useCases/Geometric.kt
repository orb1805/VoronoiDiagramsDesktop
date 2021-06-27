package com.example.demo.core.useCases

import domain.Point
import kotlin.math.pow
import kotlin.math.sqrt

class Geometric {
    companion object {
        fun length(point1: Point, point2: Point): Float {
            return sqrt((point1.x - point2.x).pow(2) + (point1.y - point2.y).pow(2))
        }
    }
}