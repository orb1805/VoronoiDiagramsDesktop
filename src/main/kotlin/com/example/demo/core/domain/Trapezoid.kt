package com.example.demo.core.domain

open class Trapezoid
    (
    private var point1: Point,
    private var point2: Point,
    private var point3: Point,
    private var point4: Point,
    open val number: Int
) : Polygon() {

    private val upperEdge: List<Point>
    private val lowerEdge: List<Point>
    val isTraingle: Boolean
    var isReal: CircledList<Boolean>

    init {
        when {
            point1 == point2 -> {
                isTraingle = true
                val tmp = point2
                point2 = point4
                point4 = tmp
            }
            point1 == point3 -> {
                isTraingle = true
                val tmp = point3
                point3 = point4
                point4 = tmp
            }
            point1 == point4 -> {
                isTraingle = true
            }
            point2 == point3 -> {
                isTraingle = true
                val tmp = point3
                point3 = point4
                point4 = tmp
            }
            point2 == point4 -> {
                isTraingle = true
            }
            point3 == point4 -> {
                isTraingle = true
            }
            else -> isTraingle = false
        }
        if (isTraingle) {
            isReal = circledListOf(mutableListOf(true, true, true))
            when {
                point1.y == point2.y -> {
                    if (point1.y > point3.y) {
                        upperEdge = listOf(point1, point2)
                        lowerEdge = listOf(point3, point3)
                    } else {
                        lowerEdge = listOf(point1, point2)
                        upperEdge = listOf(point3, point3)
                    }
                }
                point1.y == point3.y -> {
                    if (point1.y > point2.y) {
                        upperEdge = listOf(point1, point3)
                        lowerEdge = listOf(point2, point2)
                    } else {
                        lowerEdge = listOf(point1, point3)
                        upperEdge = listOf(point2, point2)
                    }
                }
                else -> {
                    if (point2.y > point1.y) {
                        upperEdge = listOf(point2, point3)
                        lowerEdge = listOf(point1, point1)
                    } else {
                        lowerEdge = listOf(point2, point3)
                        upperEdge = listOf(point1, point1)
                    }
                }
            }
        }
        else {
            isReal = circledListOf(mutableListOf(true, true, true, true))
            when (point1.y) {
                point2.y -> {
                    if (point1.y > point3.y) {
                        upperEdge = listOf(point1, point2)
                        lowerEdge = listOf(point4, point3)
                    } else {
                        lowerEdge = listOf(point2, point1)
                        upperEdge = listOf(point3, point4)
                    }
                }
                point4.y -> {
                    if (point1.y > point2.y) {
                        upperEdge = listOf(point4, point1)
                        lowerEdge = listOf(point3, point2)
                    } else {
                        upperEdge = listOf(point2, point3)
                        lowerEdge = listOf(point1, point4)
                    }
                }
                else -> { //2 == 3
                    if (point2.y > point1.y) {
                        upperEdge = listOf(point2, point3)
                        lowerEdge = listOf(point1, point4)
                    } else {
                        upperEdge = listOf(point1, point4)
                        lowerEdge = listOf(point2, point3)
                    }
                }
            }
        }
    }

    override val points: CircledList<Point>?
    get() = if (isTraingle)
        circledListOf(mutableListOf(point1, point2, point3))
    else
        circledListOf(mutableListOf(point1, point2, point3, point4))


    override fun contains(checkPoint: Point): Int {
        return when(checkPoint) {
            point1 -> 0
            point2 -> 1
            point3 -> 2
            point4 -> 3
            else -> -1
        }
    }

    override fun isDegenerate(): Boolean {
        return point1.x != point2.x ||
                point1.x != point3.x ||
                point1 != point4 ||
                point2 != point3 ||
                point2 != point4 ||
                point3 != point4
    }

    fun getUpperEdge() : List<Point> {
        return upperEdge
    }

    fun getLowerEdge() : List<Point> {
        return lowerEdge
    }

    operator fun get(index: Int): Point? {
        return when (index) {
            0 -> point1
            1 -> point2
            2 -> point3
            3 -> point4
            else -> null
        }
    }

    override fun toString(): String {
        return number.toString()
    }
}