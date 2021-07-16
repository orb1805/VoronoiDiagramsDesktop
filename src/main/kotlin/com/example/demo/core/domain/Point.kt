package domain

import kotlin.math.abs

data class Point(var x: Float, var y: Float) {

    override fun equals(other: Any?): Boolean {
        val point = other as Point
        return abs(x - point.x) < 0.0001f && abs(y - point.y) < 0.0001f
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

}