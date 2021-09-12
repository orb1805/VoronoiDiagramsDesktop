package com.example.demo.core.domain

import kotlin.math.abs

data class Point(var x: Float, var y: Float) {

    var imageX = x
    var imageY = y

    var parent1X = x
    var parent1Y = y
    var parent2X = x
    var parent2Y = y

    override fun equals(other: Any?): Boolean {
        val point = other as Point
        return abs(x - point.x) < 0.01f && abs(y - point.y) < 0.01f
    }

    operator fun minus(a: Point): Point {
        return Point(x - a.x, y - a.y)
    }

    operator fun plus(a: Point): Point {
        return Point(x + a.x, y + a.y)
    }

    operator fun div(a: Float): Point {
        return Point(x / a, y / a)
    }

    operator fun times(a: Float): Point =
        Point(x * a, y * a)
}