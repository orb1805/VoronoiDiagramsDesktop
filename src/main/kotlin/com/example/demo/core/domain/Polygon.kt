package com.example.demo.core.domain

import useCases.Type
import kotlin.math.*

open class Polygon {

    private var _points: CircledList<Point> = circledListOf()
    private var _lines: CircledList<PolygonLine> = circledListOf()
    private var types: CircledList<Type> = circledListOf()

    open val points: CircledList<Point>?
        get() = if (_points.size < 3)
            null
        else
            _points.copy()

    open val lines: CircledList<PolygonLine>?
        get() = if (_lines.size < 3)
            null
        else
            _lines.copy()

    fun getTypes(): CircledList<Type>? {
        return if (types.size < 3)
            null
        else
            types
    }

    fun addNode(point: Point) {
        for (checkPoint in _points)
            if (checkPoint == point)
                return
        if (_points.size > 0) {
            var line = Line.getLine(point, _points.last())
            var isPlusRight = (_points.last().x != point.x && _points.last().x < point.x)
                    || (_points.last().x == point.x && _points.last().y < point.y)
            var isPlusLeft = !isPlusRight
            _lines[_lines.lastIndex] = PolygonLine(line.k, line.b, isPlusRight, isPlusLeft)
            //getCoefficient(point, _points.last())
            /*b[b.lastIndex] = if (k.last() != null)
                point.y - k.last()!! * point.x
            else
                null*/
            line = Line.getLine(point, _points.first())
            isPlusRight = (point.x != _points.first().x && point.x < _points.first().x)
                    || (point.x == _points.first().x && point.y < _points.first().y)
            isPlusLeft = !isPlusRight
            _lines += PolygonLine(line.k, line.b, isPlusRight, isPlusLeft)
            //k.add(getCoefficient(point, _points[0]))
            /*if (k.last() != null)
                b.add(point.y - (k.last()?.times(point.x) ?: 0f))
            else
                b.add(null)*/
        } else {
            _lines += PolygonLine(null, 0f, true, true)
            /*k.add(0f)
            b.add(0f)*/
        }
        types.add(Type.NONE)
        _points.add(point)
        if (_points.size >= 3)
            determineTypes()
    }

    /*private fun addNode(index: Int, point: Point) {
        if (index >= _points.size)
            addNode(point)
        else {
            if (index in _points.indices) {
                var line = Line.getLine(point, _points[index])
                var isPlusRight = (_points[index].x != point.x && _points[index].x < point.x)
                        || (_points[index].x == point.x && _points[index].y < point.y)
                _lines.add(index, PolygonLine(line.k, line.b, isPlus))
                *//*k.add(index, getCoefficient(point, _points[index]))
                if (k[index] != null)
                    b.add(index, point.y - (k[index]?.times(point.x) ?: 0f))
                else
                    b.add(null)*//*
                line = Line.getLine(point, _points[index - 1])
                isPlus = (point.x != _points[index - 1].x && point.x < _points[index - 1].x)
                        || (point.x == _points[index - 1].x && point.y < _points[index - 1].y)
                _lines[index - 1] = PolygonLine(line.k, line.b, isPlus)
                *//*k[index - 1] = getCoefficient(point, _points[index - 1])
                if (k[index] != null)
                    b[index - 1] = (point.y - (k[index - 1]?.times(point.x) ?: 0f))
                else
                    b[index - 1] = null*//*
                types.add(index, Type.NONE)
                _points.add(index, point)
                if (_points.size >= 3)
                    determineTypes()
            }
        }
    }*/

    /*fun reverseAddNode(point: Point) {
        addNode(0, point)
    }*/

    private fun determineTypes() {
        for (i in _points.indices)
            types[i] = determineType(_points[i], _points[i - 1], _points[i + 1])
    }

    private fun determineType(point: Point, prevPoint: Point, nextPoint: Point): Type {
        return when {
            prevPoint.y >= point.y && point.y >= nextPoint.y -> Type.LEFT
            prevPoint.y <= point.y && point.y <= nextPoint.y -> Type.RIGHT
            (point.y >= prevPoint.y && point.y >= nextPoint.y) -> {
                var count = 0
                for (i in _lines.indices/*k.indices*/) {
                    if (_lines[i].k != null/*k[i] != null && b[i] != null*/) {
                        if ((_lines[i].k!!/* k[i]!!*/ * point.x + _lines[i].b/*b[i]!!*/) > point.y &&
                            point.x <= max(_points[i].x, _points[if (i == _points.lastIndex) 0; else (i + 1)].x) &&
                            point.x > min(_points[i].x, _points[if (i == _points.lastIndex) 0; else (i + 1)].x)
                        ) {
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
                for (i in _lines.indices/*k.indices*/) {
                    if (/*k[i] != null && b[i] != null*/_lines[i].k != null) {
                        if ((_lines[i].k!! /*k[i]!!*/ * point.x + /*b[i]!!*/_lines[i].b) < point.y &&
                            point.x <= max(_points[i].x, _points[if (i == _points.lastIndex) 0; else (i + 1)].x) &&
                            point.x > min(_points[i].x, _points[if (i == _points.lastIndex) 0; else (i + 1)].x)
                        )
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
        for (i in _points.indices)
            if (checkPoint == _points[i])
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
        for (i in 1.._points.lastIndex)
            if (_points[i].y != _points[i - 1].y)
                return false
        return true
    }

    fun isInside(checkPoint: Point?): Boolean {
        checkPoint ?: return false
        var upperCount = 0
        var lowerCount = 0
        var contains = false
        var line: Line
        for (i in _points.indices) {
            line = Line.getLine(
                _points[i],
                _points[i + 1]
            )
            if (
                line.k != null
                && ((checkPoint.x <= _points[i].x && checkPoint.x >= _points[i + 1].x)
                        || (checkPoint.x >= _points[i].x && checkPoint.x <= _points[i + 1].x))
            ) {
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
        return when (_points.size) {
            4 -> Trapezoid(_points[0], _points[1], _points[2], _points[3], number)
            3 -> Trapezoid(_points[0], _points[1], _points[2], _points[2], number)
            else -> {
                if (_points.size > 4) {
                    var flag = true
                    val tmpoints = _points.toMutableList()
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
                        for (i in 1 until tmpoints.lastIndex) {
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

    class PolygonLine(
        override val k: Float?,
        override val b: Float,
        val isPlusRight: Boolean,
        val isPlusLeft: Boolean
        ): Line(k, b)
}