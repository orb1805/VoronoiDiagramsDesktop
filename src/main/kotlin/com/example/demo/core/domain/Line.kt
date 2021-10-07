package com.example.demo.core.domain

data class Line(val k: Float?, val b: Float) {

    /*fun rotate(angle: Float) {
        val point1: Point
        val point2: Point
        if (k != null) {
            point1 = Point(0f, b)
            point2 = Point(10f, k!! * 10f + b)
        } else {
            point1 = Point(b, 0f)
            point2 = Point(b, 10f)
        }
        point1.rotate(angle)
        point2.rotate(angle)
        val newLine = getLine(point1, point2)
        this.k = newLine.k
        this.b = newLine.b
    }*/

    override fun toString(): String {
        return "k = $k, b = $b"
    }

    companion object {

        fun getLine(point1: Point, point2: Point): Line {
            return if (point1.x != point2.x)
                Line(
                    (point1.y - point2.y) / (point1.x - point2.x),
                    point1.y - (point1.y - point2.y) / (point1.x - point2.x) * point1.x
                )
            else
                Line(
                    null,
                    point1.x
                )
        }
    }
}