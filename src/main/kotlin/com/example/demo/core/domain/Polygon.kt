package domain

import com.example.demo.core.useCases.Geometric
import useCases.Type
import kotlin.math.abs

class Polygon {

    private var points: MutableList<Point>
    private var types: MutableList<Type>
    private var yAverage = 0f

    constructor() {
        points = mutableListOf()
        types = mutableListOf()
    }

    constructor(points: MutableList<Point>, types: MutableList<Type>, yAverage: Float) {
        this.points = points
        this.types = types
        this.yAverage = yAverage
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
        types.add(Type.NONE)
        points.add(point)
        yAverage = (yAverage * (points.size - 1) + point.y) / points.size
        if (points.size >= 3)
            determineTypes()
    }

    fun addNode(index: Int, point: Point) {
        if (index >= points.size)
            addNode(point)
        else {
            if (index in points.indices) {
                types.add(index, Type.NONE)
                points.add(index, point)
                yAverage = (yAverage * (points.size - 1) + point.y) / points.size
                if (points.size >= 3)
                    determineTypes()
            }
        }
    }

    fun addNode(index: Int, point: Point, type: Type) {
        if (index >= points.size)
            addNode(point)
        else {
            if (index in points.indices) {
                types.add(index, type)
                points.add(index, point)
                yAverage = (yAverage * (points.size - 1) + point.y) / points.size
                if (points.size >= 3)
                    determineTypes()
            }
        }
    }

    fun removeNode(index: Int) {
        if (index in points.indices) {
            yAverage = (yAverage * points.size - points[index].y) / (points.size - 1)
            points.removeAt(index)
            types.removeAt(index)
            if (points.size >= 3)
                determineTypes()
        }
    }

    fun copy(): Polygon {
        return Polygon(points.toMutableList(), types.toMutableList(), yAverage)
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
        return when {
            prevPoint.y >= point.y && point.y >= nextPoint.y -> Type.LEFT
            prevPoint.y <= point.y && point.y <= nextPoint.y -> Type.RIGHT
            (point.y >= prevPoint.y && point.y >= nextPoint.y && point.y <= yAverage)
                    || (point.y <= prevPoint.y && point.y <= nextPoint.y && point.y >= yAverage) -> Type.WIDE //problem with WIDE
            else -> Type.NONE
        }
    }

    fun contains(checkPoint: Point): Boolean {
        for (point in points)
            if (point == checkPoint)
                return true
        return false
    }

    fun insert(point: Point): Int? {
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
    }
}