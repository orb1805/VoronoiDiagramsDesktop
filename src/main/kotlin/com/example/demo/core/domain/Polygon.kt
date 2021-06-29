package domain

import com.example.demo.core.useCases.Geometric
import useCases.Type
import kotlin.math.*

class Polygon {

    private var points: MutableList<Point>
    private var types: MutableList<Type>
    //private var yAverage = 0f
    private val k = mutableListOf<Float?>()
    private val b = mutableListOf<Float?>()

    constructor() {
        points = mutableListOf()
        types = mutableListOf()
    }

    constructor(points: MutableList<Point>, types: MutableList<Type>/*, yAverage: Float*/) {
        this.points = points
        this.types = types
        //this.yAverage = yAverage
    }

    fun getPoints(): MutableList<Point>? {
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
        //yAverage = (yAverage * (points.size - 1) + point.y) / points.size
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
                //yAverage = (yAverage * (points.size - 1) + point.y) / points.size
                if (points.size >= 3)
                    determineTypes()
            }
        }
    }

    /*fun addNode(index: Int, point: Point, type: Type) {
        if (index >= points.size)
            addNode(point)
        else {
            if (index in points.indices) {
                types.add(index, type)
                points.add(index, point)
                //yAverage = (yAverage * (points.size - 1) + point.y) / points.size
                if (points.size >= 3)
                    determineTypes()
            }
        }
    }*/

    fun reverseAddNode(point: Point) {
        addNode(0, point)
    }

    fun removeNode(index: Int) {
        if (index in points.indices) {
            k.removeAt(index)
            b.removeAt(index)
            if (index == 0) {
                k[k.lastIndex] = getCoefficient(points.last(), points[index])
                if (k.last() != null)
                    b[b.lastIndex] = points[index].y - (k.last()?.times(points[index].x) ?: 0f)
                else
                    b[b.lastIndex] = null
            } else {
                k[index - 1] = getCoefficient(points[index - 1], points[index])
                if (k[index - 1] != null)
                    b[index - 1] = points[index].y - (k.last()?.times(points[index].x) ?: 0f)
                else
                    b[index - 1] = null
            }
            points.removeAt(index)
            types.removeAt(index)
            if (points.size >= 3)
                determineTypes()
        }
    }

    fun removeNode(point: Point) {
        val ind = contains(point)
        if (ind != -1)
            removeNode(ind)
    }

    fun copy(): Polygon {
        return Polygon(points.toMutableList(), types.toMutableList())
    }

    private fun determineTypes() {
        if (types[0] != Type.MARK)
            types[0] = determineType(points[0], points.last(), points[1])
        for (i in 1 until points.lastIndex)
            if (types[i] != Type.MARK)
                types[i] = determineType(points[i], points[i - 1], points[i + 1])
        if (types.last() != Type.MARK)
            types[types.lastIndex] = determineType(points.last(), points[points.lastIndex - 1], points[0])
    }

    private fun determineType(point: Point, prevPoint: Point, nextPoint: Point): Type {
        /*val a = (point.x - prevPoint.x).pow(2) + (point.y - prevPoint.y).pow(2)
        val b = (point.x - nextPoint.x).pow(2) + (point.y - nextPoint.y).pow(2)
        val c = (nextPoint.x - prevPoint.x).pow(2) + (nextPoint.y - prevPoint.y).pow(2)
        val alpha = acos((c - a - b)) / (-2 * sqrt(a * b))*/
        /*return when {
            prevPoint.y >= point.y && point.y >= nextPoint.y -> Type.LEFT
            prevPoint.y <= point.y && point.y <= nextPoint.y -> Type.RIGHT
            (point.y >= prevPoint.y && point.y >= nextPoint.y && point.y <= yAverage)
                    || (point.y <= prevPoint.y && point.y <= nextPoint.y && point.y >= yAverage) -> Type.WIDE //problem with WIDE
            else -> Type.NONE
        }*/
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

    fun contains(checkPoint: Point): Int {
        for (i in points.indices)
            if (points[i].x == checkPoint.x && points[i].y == checkPoint.y)
                return i
        return -1
    }

    /*fun insert(point: Point): Int? {
        if (points.size >= 2) {
            var k1: Float
            var k2: Float
            for (i in 0 until points.lastIndex) {
                if (point.x == points[i].x && point.x == points[i + 1].x) {
                    addNode(i + 1, point, Type.MARK)
                    return i + 1
                }
                k1 = (point.y - points[i].y) / (point.x - points[i].x)
                k2 = (point.y - points[i + 1].y) / (point.x - points[i + 1].x)
                if (abs(k1 - k2) < 0.01 &&
                    abs(Geometric.length(point, points[i]) + Geometric.length(point, points[i + 1]) - Geometric.length(points[i], points[i + 1])) < 0.01) {
                    addNode(i + 1, point, Type.MARK)
                    return i + 1
                }
            }
            if (point.x == points.last().x && point.x == points[0].x) {
                addNode(0, point, Type.MARK)
                return 0
            }
            k1 = (point.y - points.last().y) / (point.x - points.last().x)
            k2 = (point.y - points[0].y) / (point.x - points[0].x)
            if (abs(k1 - k2) < 0.01 &&
                abs(Geometric.length(point, points.last()) + Geometric.length(point, points[0]) - Geometric.length(points.last(), points[0])) < 0.01) {
                addNode(0, point, Type.MARK)
                return 0
            }
        }
        return null
    }*/

    private fun getCoefficient(point1: Point, point2: Point): Float? {
        return if (point1.x != point2.x)
            (point1.y - point2.y) / (point1.x - point2.x)
        else
            null
    }
}