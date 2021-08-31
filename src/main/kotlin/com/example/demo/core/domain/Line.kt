package com.example.demo.core.domain

data class Line(val k: Float?, val b: Float) {
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
        }    }
}