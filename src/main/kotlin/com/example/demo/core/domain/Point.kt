package com.example.demo.core.domain

import kotlin.math.*

open class Point(var x: Float = 0f, var y: Float = 0f) {

    var imageX = x
    var imageY = y

    var parent1X = x
    var parent1Y = y
    var parent2X = x
    var parent2Y = y

    val module: Float
        get() = sqrt(x.pow(2) + y.pow(2))

    fun rotate(angle: Float) {
        val x = this.x
        val y = this.y
        this.x = x * cos(angle) - y * sin(angle)
        this.y = x * sin(angle) + y * cos(angle)
    }

    fun normalize() {
        x /= module
        y /= module
    }

    fun lengthToPoint(point: Point): Float =
        sqrt((x - point.x).pow(2) + (y - point.y).pow(2))

    fun copy(): Point = Point(x, y)

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

    operator fun div(a: Int): Point {
        return Point(x / a, y / a)
    }

    operator fun times(a: Float): Point =
        Point(x * a, y * a)

    override fun toString(): String = "($x, $y)"

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + imageX.hashCode()
        result = 31 * result + imageY.hashCode()
        result = 31 * result + parent1X.hashCode()
        result = 31 * result + parent1Y.hashCode()
        result = 31 * result + parent2X.hashCode()
        result = 31 * result + parent2Y.hashCode()
        return result
    }
}