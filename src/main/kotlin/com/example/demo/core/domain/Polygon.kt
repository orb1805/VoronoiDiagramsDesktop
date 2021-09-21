package domain

import com.example.demo.core.domain.Line
import com.example.demo.core.domain.Point
import com.example.demo.core.domain.Trapezoid
import useCases.Type
import kotlin.math.*

open class Polygon() {

    private var points: MutableList<Point>
    private var types: MutableList<Type>
    private val k = mutableListOf<Float?>()
    private val b = mutableListOf<Float?>()

    init {
        points = mutableListOf()
        types = mutableListOf()
    }

    open fun getPoints(): MutableList<Point>? {
        return if (points.size < 3)
            null
        else
            points.toMutableList()
    }

    fun getTypes(): MutableList<Type>? {
        return if (types.size < 3)
            null
        else
            types
    }

    fun addNode(point: Point) {
        for (checkPoint in points)
            if (checkPoint == point)
                return
        if (points.size > 0) {
            k[k.lastIndex] =
                getCoefficient(point, points.last())//(point.y - points.last().y) / (point.x - points.last().x)
            b[b.lastIndex] = if (k.last() != null)
                point.y - k.last()!! * point.x
            else
                null
            k.add(getCoefficient(point, points[0]))
            if (k.last() != null)
                b.add(point.y - (k.last()?.times(point.x) ?: 0f))
            else
                b.add(null)
        } else {
            k.add(0f)
            b.add(0f)
        }
        types.add(Type.NONE)
        points.add(point)
        if (points.size >= 3)
            determineTypes()
    }

    fun addNode(index: Int, point: Point) {
        if (index >= points.size)
            addNode(point)
        else {
            if (index in points.indices) {
                k.add(index, getCoefficient(point, points[index]))
                if (k[index] != null)
                    b.add(index, point.y - (k[index]?.times(point.x) ?: 0f))
                else
                    b.add(null)
                if (index == 0) {
                    k[k.lastIndex] = getCoefficient(point, points.last())
                    if (k.last() != null)
                        b[b.lastIndex] = (point.y - (k.last()?.times(point.x) ?: 0f))
                    else
                        b[b.lastIndex] = null
                } else {
                    k[index - 1] = getCoefficient(point, points[index - 1])
                    if (k[index] != null)
                        b[index - 1] = (point.y - (k[index - 1]?.times(point.x) ?: 0f))
                    else
                        b[index - 1] = null
                }
                types.add(index, Type.NONE)
                points.add(index, point)
                if (points.size >= 3)
                    determineTypes()
            }
        }
    }

    fun reverseAddNode(point: Point) {
        addNode(0, point)
    }

    private fun determineTypes() {
        types[0] = determineType(points[0], points.last(), points[1])
        for (i in 1 until points.lastIndex)
            types[i] = determineType(points[i], points[i - 1], points[i + 1])
        types[types.lastIndex] = determineType(points.last(), points[points.lastIndex - 1], points[0])
    }

    private fun determineType(point: Point, prevPoint: Point, nextPoint: Point): Type {
        return when {
            prevPoint.y >= point.y && point.y >= nextPoint.y -> Type.LEFT
            prevPoint.y <= point.y && point.y <= nextPoint.y -> Type.RIGHT
            (point.y >= prevPoint.y && point.y >= nextPoint.y) -> {
                var count = 0
                for (i in k.indices) {
                    if (k[i] != null && b[i] != null) {
                        if ((k[i]!! * point.x + b[i]!!) > point.y &&
                            point.x <= max(points[i].x, points[if (i == points.lastIndex) 0; else (i + 1)].x) &&
                            point.x > min(points[i].x, points[if (i == points.lastIndex) 0; else (i + 1)].x)) {
                            count++
                        }
                    }
                }
                if (count % 2 == 0)
                    Type.NONE
                else
                    Type.WIDE
            }
            (point.y <= prevPoint.y && point.y <= nextPoint.y) -> {
                var count = 0
                for (i in k.indices) {
                    if (k[i] != null && b[i] != null) {
                        if ((k[i]!! * point.x + b[i]!!) < point.y &&
                            point.x <= max(points[i].x, points[if (i == points.lastIndex) 0; else (i + 1)].x) &&
                            point.x > min(points[i].x, points[if (i == points.lastIndex) 0; else (i + 1)].x))
                            count++
                    }
                }
                if (count % 2 == 0)
                    Type.NONE
                else
                    Type.WIDE
            }
            else -> Type.NONE
        }
    }

    open fun contains(checkPoint: Point): Int {
        for (i in points.indices)
            if (points[i].x == checkPoint.x && points[i].y == checkPoint.y)
                return i
        return -1
    }

    private fun getCoefficient(point1: Point, point2: Point): Float? {
        return if (point1.x != point2.x)
            (point1.y - point2.y) / (point1.x - point2.x)
        else
            null
    }

    open fun isDegenerate(): Boolean {
        for (i in 1 .. points.lastIndex)
            if (points[i].y != points[i - 1].y)
                return false
        return true
    }

    fun isInside(checkPoint: Point?): Boolean {
        checkPoint ?: return false
        var upperCount = 0
        var lowerCount = 0
        var contains = false
        var line: Line
        for (i in 0 until points.lastIndex) {
            line = Line.getLine(
                points[i],
                points[i + 1]
            )
            if (
                line.k != null
                && ((checkPoint.x <= points[i].x && checkPoint.x >= points[i + 1].x)
                                || (checkPoint.x >= points[i].x && checkPoint.x <= points[i + 1].x))) {
                when {
                    checkPoint.y > line.k!! * checkPoint.x + line.b ->
                        upperCount++
                    checkPoint.y < line.k!! * checkPoint.x + line.b ->
                        lowerCount++
                    else ->
                        contains = true
                }
            } else {
                if (checkPoint.x == line.b)
                    contains = true
            }
        }
        line = Line.getLine(
            points[0],
            points.last()
        )
        if ((checkPoint.x <= points[0].x && checkPoint.x >= points.last().x)
                || (checkPoint.x >= points[0].x && checkPoint.x <= points.last().x)) {
            if (line.k != null) {
                when {
                    checkPoint.y > line.k!! * checkPoint.x + line.b ->
                        upperCount++
                    checkPoint.y < line.k!! * checkPoint.x + line.b ->
                        lowerCount++
                    else ->
                        contains = true
                }
            } else {
                if (checkPoint.x == line.b)
                    contains = true
            }
        }
        return (upperCount % 2 == 1 && lowerCount % 2 == 1) || contains
    }

    fun toTrapezoid(number: Int): Trapezoid? {
        return when (points.size) {
            4 -> Trapezoid(points[0], points[1], points[2], points[3], number)
            3 -> Trapezoid(points[0], points[1], points[2], points[2], number)
            else -> {
                if (points.size > 4) {
                    var flag = true
                    val tmpoints = points.toMutableList()
                    var k1: Float?
                    var k2: Float?
                    var b1: Float?
                    var b2: Float?
                    var listToDelete: MutableList<Int>
                    while (tmpoints.size > 4 && flag) {
                        listToDelete = mutableListOf()
                        k1 = getCoefficient(tmpoints.last(), tmpoints[0])
                        k2 = getCoefficient(tmpoints[0], tmpoints[1])
                        b1 = if (k1 != null)
                            tmpoints[0].y - k1 * tmpoints[0].y
                        else
                            null
                        b2 = if (k2 != null)
                            tmpoints[0].y - k2 * tmpoints[0].y
                        else
                            null
                        if (k1 == k2 && b1 == b2)
                            listToDelete.add(0)
                        for (i in 1 until  tmpoints.lastIndex) {
                            k1 = k2
                            k2 = getCoefficient(tmpoints[i], tmpoints[i + 1])
                            b1 = b2
                            b2 = if (k2 != null)
                                tmpoints[i].y - k2 * tmpoints[i].y
                            else
                                null
                            if (k1 == k2 && b1 == b2)
                                listToDelete.add(i)
                        }
                        k1 = k2
                        k2 = getCoefficient(tmpoints.last(), tmpoints[0])
                        b1 = b2
                        b2 = if (k2 != null)
                            tmpoints.last().y - k2 * tmpoints.last().y
                        else
                            null
                        if (k1 == k2 && b1 == b2)
                            listToDelete.add(tmpoints.lastIndex)
                        if (listToDelete.isEmpty())
                            flag = false
                        else
                            for (i in listToDelete.lastIndex downTo 0) {
                                tmpoints.removeAt(i)
                            }
                    }
                    if (flag)
                        Trapezoid(tmpoints[0], tmpoints[1], tmpoints[2], tmpoints[3], number)
                    else
                        null
                } else
                    null
            }
        }
    }

    operator fun plusAssign(point: Point) {
        this.addNode(point)
    }
}