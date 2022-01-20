package com.example.demo.core.domain

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

open class Line(open val k: Float?, open val b: Float) {

    fun intersection(line: Line): Point? =
        when {
            k != null && line.k != null ->
                if (abs(k!! - line.k!!) < 0.0001f)
                    null
                else {
                    val x = -(b - line.b) / (k!! - line.k!!)
                    Point(x, k!! * x + b)
                }
            k != null && line.k == null ->
                Point(line.b, k!! * line.b + b)
            k == null && line.k != null ->
                Point(b, line.k!! * b + line.b)
            else ->
                null
        }

    override fun toString(): String {
        return "k = $k, b = $b"
    }

    companion object {

        fun getLine(point1: Point, point2: Point): Line =
            if (point1.x != point2.x)
                Line(
                    (point1.y - point2.y) / (point1.x - point2.x),
                    point1.y - (point1.y - point2.y) / (point1.x - point2.x) * point1.x
                )
            else
                Line(null, point1.x)

        fun getPerpendicular(line: Line, point: Point): Line =
            if (line.k != null)
                if (line.k != 0f) {
                    Line(-1f / line.k!!, point.y + point.x / line.k!!)
                } else
                    Line(null, point.x)
            else
                Line(0f, point.y)

        fun getBisector(line1: Line, line2: Line, isPlusA: Boolean, isPlusB: Boolean): Line? {
            val point = line1.intersection(line2)
            return if (point != null) {
                val pointA: Point
                val pointB: Point
                when {
                    line1.k != null && line2.k != null -> {
                        pointA = if (isPlusA)
                            Point(point.x + 10f, line1.k!! * (point.x + 10f) + line1.b) - point
                        else
                            Point(point.x - 10f, line1.k!! * (point.x - 10f) + line1.b) - point
                        pointB = if (isPlusB)
                            Point(point.x + 10f, line2.k!! * (point.x + 10f) + line2.b) - point
                        else
                            Point(point.x - 10f, line2.k!! * (point.x - 10f) + line2.b) - point
                    }
                    line1.k != null && line2.k == null -> {
                        pointA = if (isPlusA)
                            Point(point.x + 10f, line1.k!! * (point.x + 10f) + line1.b) - point
                        else
                            Point(point.x - 10f, line1.k!! * (point.x - 10f) + line1.b) - point
                        pointB = if (isPlusB)
                            Point(line2.b, 10f)
                        else
                            Point(line2.b, -10f)
                    }
                    line1.k == null && line2.k != null -> {
                        pointB = if (isPlusB)
                            Point(point.x + 10f, line2.k!! * (point.x + 10f) + line2.b) - point
                        else
                            Point(point.x - 10f, line2.k!! * (point.x - 10f) + line2.b) - point
                        pointA = if (isPlusA)
                            Point(line1.b, 10f)
                        else
                            Point(line1.b, -10f)
                    }
                    else -> {
                        pointA = if (isPlusA)
                            Point(line1.b, 10f)
                        else
                            Point(line1.b, -10f)
                        pointB = if (isPlusB)
                            Point(line2.b, 10f)
                        else
                            Point(line2.b, -10f)
                    }
                }
                val lenA = sqrt(pointA.x.pow(2) + pointA.y.pow(2))
                val lenB = sqrt(pointB.x.pow(2) + pointB.y.pow(2))
                val ortA = pointA / lenA
                val ortB = pointB / lenB
                val finalPoint = ortA + ortB + point
                getLine(point, finalPoint)
            } else
                null
        }
    }
}