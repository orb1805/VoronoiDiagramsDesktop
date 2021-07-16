package com.example.demo.core.useCases

import com.example.demo.core.domain.Line
import com.example.demo.core.domain.Trapezoid
import domain.Point
import domain.Polygon
import tornadofx.closepath
import tornadofx.lineTo
import tornadofx.moveTo
import useCases.Type
import java.util.function.BinaryOperator
import kotlin.math.*

class Geometric {
    companion object {

        fun trapezoidateToList(polygon: Polygon) : MutableList<Trapezoid> {
            val trapezoids = mutableListOf<Polygon>()
            var points = polygon.getPoints()
            val types = polygon.getTypes()
            if (points != null && types != null) {
                if (points.size > 3) {
                    /**construct edge list**/
                    val edgeList = constructEdgeList(points)
                    /**build intersections**/
                    val intersections = mutableMapOf<Int, MutableList<Intersection?>>()
                    var coefs: List<Float>?
                    for (i in 0..types.lastIndex)
                        if (types[i] != Type.NONE) {
                            for (edge in edgeList)
                                if (isBetweenByY(points[i], points[edge[0]], points[edge[1]]) && i != edge[0] && i != edge[1]) {
                                    coefs = getCoefficients(points[edge[0]], points[edge[1]])
                                    val x = if (coefs != null)
                                        (points[i].y - coefs[1]) / coefs[0]
                                    else
                                        points[edge[0]].x
                                    when (types[i]) {
                                        Type.RIGHT -> {
                                            if (x > points[i].x) {
                                                if (intersections.containsKey(i)) {
                                                    if (intersections[i]!![0]!!.pointOfEdge.x > x)
                                                        intersections[i] = mutableListOf(
                                                            Intersection(
                                                                i,
                                                                listOf(edge[0], edge[1]),
                                                                Point(x, points[i].y)
                                                            )
                                                        )
                                                } else {
                                                    intersections[i] = mutableListOf(
                                                        Intersection(
                                                            i,
                                                            listOf(edge[0], edge[1]),
                                                            Point(x, points[i].y)
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        Type.LEFT -> {
                                            if (x < points[i].x) {
                                                if (intersections.containsKey(i)) {
                                                    if (intersections[i]!![0]!!.pointOfEdge.x < x)
                                                        intersections[i] = mutableListOf(
                                                            Intersection(
                                                                i,
                                                                listOf(edge[0], edge[1]),
                                                                Point(x, points[i].y)
                                                            )
                                                        )
                                                } else {
                                                    intersections[i] = mutableListOf(
                                                        Intersection(
                                                            i,
                                                            listOf(edge[0], edge[1]),
                                                            Point(x, points[i].y)
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        Type.WIDE -> {
                                            if (intersections.containsKey(i)) {
                                                if (x < points[i].x) {
                                                    if (intersections[i]!![0] != null) {
                                                        if (x > intersections[i]!![0]!!.pointOfEdge.x)
                                                            intersections[i]!![0] =
                                                                Intersection(
                                                                    i,
                                                                    listOf(edge[0], edge[1]),
                                                                    Point(x, points[i].y)
                                                                )

                                                    } else {
                                                        intersections[i]!![0] =
                                                            Intersection(i, listOf(edge[0], edge[1]), Point(x, points[i].y))
                                                    }
                                                } else {
                                                    if (x != points[i].x) {
                                                        if (intersections[i]!![1] != null) {
                                                            if (x < intersections[i]!![1]!!.pointOfEdge.x)
                                                                intersections[i]!![1] = Intersection(
                                                                    i,
                                                                    listOf(edge[0], edge[1]),
                                                                    Point(x, points[i].y)
                                                                )

                                                        } else {
                                                            intersections[i]!![1] =
                                                                Intersection(
                                                                    i,
                                                                    listOf(edge[0], edge[1]),
                                                                    Point(x, points[i].y)
                                                                )
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (x < points[i].x) {
                                                    intersections[i] = mutableListOf(
                                                        Intersection(
                                                            i,
                                                            listOf(edge[0], edge[1]),
                                                            Point(x, points[i].y)
                                                        ), null
                                                    )
                                                } else {
                                                    intersections[i] = mutableListOf(
                                                        null, Intersection(
                                                            i,
                                                            listOf(edge[0], edge[1]),
                                                            Point(x, points[i].y)
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                    /**intersections from map to list**/
                    var length1: Int
                    var length2: Int
                    var polygon: Polygon
                    var count: Int
                    var setOfIndexes: Set<Int>
                    var a: Int
                    var c: Int
                    val inters = mutableListOf<Intersection>()
                    for (i in intersections) {
                        for (j in i.value) {
                            inters.add(j!!)
                        }
                    }
                    /**delete same intersections**/
                    var listToDelete = mutableListOf<Int>()
                    for (i in 0 until inters.lastIndex) {
                        for (j in i + 1 .. inters.lastIndex) {
                            if (areSectionsEqual(
                                    inters[i].pointOfEdge,
                                    points[inters[i].indexOfVertex],
                                    inters[j].pointOfEdge,
                                    points[inters[j].indexOfVertex]
                                )
                            )
                                listToDelete.add(i)
                        }
                    }
                    for (i in listToDelete.lastIndex downTo 0)
                        inters.removeAt(listToDelete[i])
                    /**delete intersections thar are equal to sections of polygon**/
                    listToDelete = mutableListOf()
                    for (i in inters.indices) {
                        for (j in 0 until points.lastIndex)
                            if (areSectionsEqual(
                                    inters[i].pointOfEdge,
                                    points[inters[i].indexOfVertex],
                                    points[j],
                                    points[j + 1]
                                )
                            )
                                listToDelete.add(i)
                        if (areSectionsEqual(
                                inters[i].pointOfEdge,
                                points[inters[i].indexOfVertex],
                                points.last(),
                                points[0]
                            )
                        )
                            listToDelete.add(i)
                    }
                    for (i in listToDelete.lastIndex downTo 0)
                        inters.removeAt(listToDelete[i])
                    /**sort intersections by y**/
                    var tmpInter: Intersection
                    for (i in 0 until inters.lastIndex)
                        for (j in i + 1..inters.lastIndex)
                            if (inters[i].pointOfEdge.y < inters[j].pointOfEdge.y) {
                                tmpInter = inters[i]
                                inters[i] = inters[j]
                                inters[j] = tmpInter
                            }
                    /**build trapezoids**/
                    val deletionController = mutableListOf<Int>()
                    for (i in 0 until types.lastIndex)
                        if (types[i] == Type.WIDE) {
                            if (points[i + 1].y > points[i].y)
                                deletionController.add(1)
                            else
                                deletionController.add(2)
                        }
                        else
                            deletionController.add(0)
                    val setOfDeleted = mutableSetOf<Int>()
                    for (i in inters.indices) {
                        polygon = Polygon()
                        if (inters[i].indexOfVertex < inters[i].indexesOfEdge[0]) {
                            a = inters[i].indexOfVertex
                            c = inters[i].indexesOfEdge[0]
                            length1 = c - a
                            setOfIndexes = (a..c).toSet()
                        } else {
                            length1 = if (inters[i].indexOfVertex == points.lastIndex)
                                1
                            else
                                points.lastIndex - inters[i].indexOfVertex
                            length1 += inters[i].indexesOfEdge[0]
                            setOfIndexes = inters[i].indexOfVertex..points.lastIndex union 0..inters[i].indexesOfEdge[0]
                        }
                        count = 0
                        for (j in setOfDeleted)
                            if (j in setOfIndexes)
                                count++
                        length1 -= count
                        length2 = points.size - setOfDeleted.size - length1 - 1
                        if (length1 == length2) {
                            if (points[inters[i].indexesOfEdge[0]].y > points[inters[i].indexOfVertex].y)
                                length2++
                            else
                                length1++
                        }
                        if (length1 < length2) {
                            if (inters[i].indexOfVertex > inters[i].indexesOfEdge[0]) {
                                count = 0
                                polygon.addNode(points[inters[i].indexOfVertex])
                                if (types[inters[i].indexOfVertex] == Type.WIDE) {
                                    if (deletionController[inters[i].indexOfVertex] > 0)
                                        deletionController[inters[i].indexOfVertex]--
                                    else
                                        setOfDeleted.add(inters[i].indexOfVertex)
                                }
                                for (j in inters[i].indexOfVertex + 1..points.lastIndex) {
                                    if (!setOfDeleted.contains(j)) {
                                        polygon.addNode(points[j])
                                        setOfDeleted.add(j)
                                        count++
                                    }
                                }
                                for (j in 0..inters[i].indexesOfEdge[0]) {
                                    if (!setOfDeleted.contains(j)) {
                                        polygon.addNode(points[j])
                                        setOfDeleted.add(j)
                                        count++
                                    }
                                }
                                polygon.addNode(inters[i].pointOfEdge)
                                points[setOfDeleted.last()] = inters[i].pointOfEdge
                                setOfDeleted.remove(setOfDeleted.last())
                            } else {
                                count = 0
                                polygon.addNode(points[inters[i].indexOfVertex])
                                if (types[inters[i].indexOfVertex] == Type.WIDE) {
                                    if (deletionController[inters[i].indexOfVertex] > 0)
                                        deletionController[inters[i].indexOfVertex]--
                                    else
                                        setOfDeleted.add(inters[i].indexOfVertex)
                                }
                                for (j in inters[i].indexOfVertex + 1..inters[i].indexesOfEdge[0]) {
                                    if (!setOfDeleted.contains(j)) {
                                        polygon.addNode(points[j])
                                        setOfDeleted.add(j)
                                        count++
                                    }
                                }
                                polygon.addNode(inters[i].pointOfEdge)
                                points[setOfDeleted.last()] = inters[i].pointOfEdge
                                setOfDeleted.remove(setOfDeleted.last())
                            }
                        } else {
                            if (inters[i].indexOfVertex > inters[i].indexesOfEdge[1]) {
                                count = 0
                                polygon.reverseAddNode(points[inters[i].indexOfVertex])
                                if (types[inters[i].indexOfVertex] == Type.WIDE) {
                                    if (deletionController[inters[i].indexOfVertex] > 0)
                                        deletionController[inters[i].indexOfVertex]--
                                    else
                                        setOfDeleted.add(inters[i].indexOfVertex)
                                }
                                for (j in inters[i].indexOfVertex - 1 downTo inters[i].indexesOfEdge[1]) {
                                    if (!setOfDeleted.contains(j)) {
                                        polygon.reverseAddNode(points[j])
                                        setOfDeleted.add(j)
                                        count++
                                    }
                                }
                                polygon.reverseAddNode(inters[i].pointOfEdge)
                                points[setOfDeleted.last()] = inters[i].pointOfEdge
                                setOfDeleted.remove(setOfDeleted.last())
                            } else {
                                count = 0
                                polygon.reverseAddNode(points[inters[i].indexOfVertex])
                                if (types[inters[i].indexOfVertex] == Type.WIDE) {
                                    if (deletionController[inters[i].indexOfVertex] > 0)
                                        deletionController[inters[i].indexOfVertex]--
                                    else
                                        setOfDeleted.add(inters[i].indexOfVertex)
                                }
                                for (j in inters[i].indexOfVertex - 1 downTo 0) {
                                    if (!setOfDeleted.contains(j)) {
                                        polygon.reverseAddNode(points[j])
                                        setOfDeleted.add(j)
                                        count++
                                    }
                                }
                                for (j in points.lastIndex downTo inters[i].indexesOfEdge[1]) {
                                    if (!setOfDeleted.contains(j)) {
                                        polygon.reverseAddNode(points[j])
                                        setOfDeleted.add(j)
                                        count++
                                    }
                                }
                                polygon.reverseAddNode(inters[i].pointOfEdge)
                                points[setOfDeleted.last()] = inters[i].pointOfEdge
                                setOfDeleted.remove(setOfDeleted.last())
                            }
                        }
                        trapezoids.add(polygon)
                    }
                    polygon = Polygon()
                    for (i in points.indices) {
                        if (!setOfDeleted.contains(i))
                            polygon.addNode(points[i])
                    }
                    trapezoids.add(polygon)
                    listToDelete = mutableListOf()
                    for (i in trapezoids.indices)
                        if (trapezoids[i].isDegenerate())
                            listToDelete.add(i)
                    for (i in listToDelete.lastIndex downTo 0)
                        trapezoids.removeAt(listToDelete[i])
                }
            }
            val result = mutableListOf<Trapezoid>()
            var tmp: Trapezoid?
            points = polygon.getPoints()
            if (points != null) {
                for (i in trapezoids.indices) {
                    tmp = trapezoids[i].toTrapezoid(i)
                    if (tmp != null) {
                        //region Check if edge of trapezoid is real
                        val isReal = mutableListOf<Boolean>()
                        val tmpoints = tmp.getPoints()
                        for (j in 0 until tmpoints!!.lastIndex) {
                            isReal.add(false)
                            for (k in 0 until points.lastIndex)
                                if (checkIntersection(
                                        tmpoints[j], tmpoints[j + 1],
                                        points[k], points[k + 1]
                                ))
                                    isReal[j] = true
                            if (checkIntersection(
                                    tmpoints[j], tmpoints[j + 1],
                                    points.last(), points[0]
                                ))
                                isReal[j] = true
                        }
                        isReal.add(false)
                        for (k in 0 until points.lastIndex)
                            if (checkIntersection(
                                    tmpoints.last(), tmpoints[0],
                                    points[k], points[k + 1]
                                ))
                                isReal[isReal.lastIndex] = true
                        if (checkIntersection(
                                tmpoints.last(), tmpoints[0],
                                points.last(), points[0]
                            ))
                            isReal[isReal.lastIndex] = true
                        tmp.isReal = isReal
                        //endregion
                        result.add(tmp)
                    }
                }
            }
            return result
        }

        private fun constructEdgeList(points: MutableList<Point>) : MutableList<MutableList<Int>>{
            val tmp = mutableListOf<Float>()
            for (i in 0..points.lastIndex)
                tmp.add(points[i].y)
            val sortedByY = mutableListOf<Int>()
            for (i in 0..tmp.lastIndex)
                sortedByY.add(i)
            for (i in 0 until tmp.lastIndex)
                for (j in i + 1..tmp.lastIndex)
                    if (tmp[i] < tmp[j]) {
                        tmp[i] += tmp[j]
                        tmp[j] = tmp[i] - tmp[j]
                        tmp[i] -= tmp[j]
                        sortedByY[i] += sortedByY[j]
                        sortedByY[j] = sortedByY[i] - sortedByY[j]
                        sortedByY[i] -= sortedByY[j]
                    }
            val edgeList = mutableListOf<MutableList<Int>>()
            edgeList.add(mutableListOf(sortedByY[0], sortedByY[0] + 1))
            for (i in 1..sortedByY.lastIndex) {
                if (edgeList.last()[1] == sortedByY[i])
                    edgeList.removeLast()
                if (sortedByY[i] == sortedByY.lastIndex)
                    edgeList.add(mutableListOf(sortedByY[i], 0))
                else
                    edgeList.add(mutableListOf(sortedByY[i], sortedByY[i] + 1))
            }
            return edgeList
        }

        fun getCoefficients(point1: Point, point2: Point): List<Float>? {
            return if (point1.x != point2.x)
                listOf(
                    (point1.y - point2.y) / (point1.x - point2.x),
                    point1.y - (point1.y - point2.y) / (point1.x - point2.x) * point1.x
                )
            else
                null
        }

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

        private fun isBetweenByY(point: Point, point1: Point, point2: Point): Boolean {
            return point1.y >= point.y && point2.y <= point.y || point1.y <= point.y && point2.y >= point.y
        }

        fun trapezoidateToTree(trapezoidList1: MutableList<Trapezoid>) : Node<Trapezoid>? {
            val trapezoidList = trapezoidList1.toMutableList()//trapezoidateToList(polygon)
            if (trapezoidList.isNotEmpty()) {
                val parent = Node(trapezoidList[0])
                trapezoidList.removeAt(0)
                val listToDelete = mutableListOf<Int>()
                for (i in trapezoidList.indices)
                    if (checkHorizontalSections(
                            parent.data.getUpperEdge()[0],
                            parent.data.getUpperEdge()[1],
                            trapezoidList[i].getLowerEdge()[0],
                            trapezoidList[i].getLowerEdge()[1]
                        ) ||
                        checkHorizontalSections(
                            parent.data.getLowerEdge()[0],
                            parent.data.getLowerEdge()[1],
                            trapezoidList[i].getUpperEdge()[0],
                            trapezoidList[i].getUpperEdge()[1]
                        )
                    ) {
                        parent.addChild(Node(trapezoidList[i]))
                        listToDelete.add(i)
                    }
                for (i in listToDelete.lastIndex downTo 0)
                    trapezoidList.removeAt(listToDelete[i])
                val n = parent.getNumberOfChildren()
                for (i in 0 until n)
                    trapezoidateToTree(parent.getChild(i)!!, trapezoidList)
                return parent
            }
            return null
        }

        fun trapezoidateToTree(parent: Node<Trapezoid>, trapezoidList: MutableList<Trapezoid>){
            if (trapezoidList.isNotEmpty()) {
                val listToDelete = mutableListOf<Int>()
                for (i in trapezoidList.indices)
                    if (checkHorizontalSections(
                            parent.data.getUpperEdge()[0],
                            parent.data.getUpperEdge()[1],
                            trapezoidList[i].getLowerEdge()[0],
                            trapezoidList[i].getLowerEdge()[1]
                        ) ||
                        checkHorizontalSections(
                            parent.data.getLowerEdge()[0],
                            parent.data.getLowerEdge()[1],
                            trapezoidList[i].getUpperEdge()[0],
                            trapezoidList[i].getUpperEdge()[1]
                        )
                    ) {
                        parent.addChild(Node(trapezoidList[i]))
                        listToDelete.add(i)
                    }
                for (i in listToDelete.lastIndex downTo 0)
                    trapezoidList.removeAt(listToDelete[i])
                val n = parent.getNumberOfChildren()
                for (i in 0 until n)
                    trapezoidateToTree(parent.getChild(i)!!, trapezoidList)
            }
        }

        fun checkHorizontalSections(point1: Point, point2: Point, point3: Point, point4: Point): Boolean {
            val a1 = min(point1.x, point2.x)
            val b1 = max(point1.x, point2.x)
            val a2 = min(point3.x, point4.x)
            val b2 = max(point3.x, point4.x)
            return point1.y == point2.y &&
                    point2.y == point3.y &&
                    point3.y == point4.y &&
                    (a1 <= a2 && b1 >= b2 || a1 >= a2 && b1 <= b2)
        }

        fun areSectionsEqual(point1: Point, point2: Point, point3: Point, point4: Point) : Boolean {
            return (point1 == point3 && point2 == point4) || (point1 == point4 && point2 == point3)
        }

        fun checkIntersection(point1: Point, point2: Point, point3: Point, point4: Point): Boolean {
            val ax1 = min(point1.x, point2.x)
            val bx1 = max(point1.x, point2.x)
            val ax2 = min(point3.x, point4.x)
            val bx2 = max(point3.x, point4.x)
            val ay1 = min(point1.y, point2.y)
            val by1 = max(point1.y, point2.y)
            val ay2 = min(point3.y, point4.y)
            val by2 = max(point3.y, point4.y)
            return if ((ax1 <= ax2 && bx1 >= bx2 || ax1 >= ax2 && bx1 <= bx2) &&
                (ay1 <= ay2 && by1 >= by2 || ay1 >= ay2 && by1 <= by2)) {
                val coefficients1 = getCoefficients(point1, point2)
                val coefficients2 = getCoefficients(point3, point4)
                when {
                    coefficients1 == null && coefficients2 == null -> true
                    (coefficients1 == null && coefficients2 != null) ||
                            (coefficients1 != null && coefficients2 == null) -> false
                    abs(coefficients1!![0] - coefficients2!![0]) < 0.001f &&
                            abs(coefficients1[1] - coefficients2[1]) < 0.001f -> true
                    else -> false
                }
            } else
                false
        }

        fun getPerpendicular(point1: Point, point2: Point): List<Float>? {
            val coefs = getCoefficients(point1, point2)
            return if (coefs != null) {
                if (coefs[0] != 0f) {
                    listOf(-1f / coefs[0], point1.y + point1.x / coefs[0])
                }
                else
                    null
            }
            else
                listOf(0f, point1.y)
        }

        fun getPerpendicular(line: Line, point: Point): Line {
            return if (line.k != null) {
                if (line.k != 0f) {
                    Line(
                        -1f / line.k,
                        point.y + point.x / line.k
                    )
                }
                else
                    Line(
                        null,
                        point.x
                    )
            }
            else
                Line(
                    0f,
                    point.y
                )
        }

        fun getBisector(point1: Point, point2: Point, point3: Point): List<Float>? {
            val pointA = point1 - point2
            val pointB = point3 - point2
            val lenA = sqrt(pointA.x.pow(2) + pointA.y.pow(2))
            val lenB = sqrt(pointB.x.pow(2) + pointB.y.pow(2))
            val ortA = pointA / lenA
            val ortB = pointB / lenB
            val finalPoint = ortA + ortB + point2
            return getCoefficients(point2, finalPoint)
        }

        fun getBisector(line1: Line, line2: Line, isPlusA: Boolean, isPlusB: Boolean): Line? {
            println("$isPlusA $isPlusB")
            val point = getIntersection(line1, line2)
            return if (point != null) {
                val pointA: Point// = point1 - point2
                val pointB: Point// = point3 - point2
                when {
                    line1.k != null && line2.k != null -> {
                        pointA = if (isPlusA)
                            Point(point.x + 10f, line1.k * (point.x + 10f) + line1.b) - point
                        else
                            Point(point.x - 10f, line1.k * (point.x - 10f) + line1.b) - point
                        pointB = if (isPlusB)
                            Point(point.x + 10f, line2.k * (point.x + 10f) + line2.b) - point
                        else
                            Point(point.x - 10f, line2.k * (point.x - 10f) + line2.b) - point
                    }
                    line1.k != null && line2.k == null -> {
                        pointA = if (isPlusA)
                            Point(point.x + 10f, line1.k * (point.x + 10f) + line1.b) - point
                        else
                            Point(point.x - 10f, line1.k * (point.x - 10f) + line1.b) - point
                        pointB = if (isPlusB)
                            Point(line2.b, 10f)
                        else
                            Point(line2.b, -10f)
                    }
                    line1.k == null && line2.k != null -> {
                        pointB = if (isPlusB)
                            Point(point.x + 10f, line2.k * (point.x + 10f) + line2.b) - point
                        else
                            Point(point.x - 10f, line2.k * (point.x - 10f) + line2.b) - point
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

        fun getIntersection(line1: Line, line2: Line): Point? {
            return when {
                line1.k != null && line2.k != null -> {
                    if (abs(line1.k - line2.k) < 0.0001f)
                        null
                    else {
                        val x = -(line1.b - line2.b) / (line1.k - line2.k)
                        Point(
                            x,
                            line1.k * x + line1.b
                        )
                    }
                }
                line1.k != null && line2.k == null -> {
                    Point(
                        line2.b,
                        line1.k * line2.b + line1.b
                    )
                }
                line1.k == null && line2.k != null -> {
                    Point(
                        line1.b,
                        line2.k * line1.b + line2.b
                    )
                }
                else -> null
            }
        }

        fun buildBisectors(trapezoid: Trapezoid) {
            val points = trapezoid.getPoints()
            val isReal = trapezoid.isReal
            if (points != null) {
                val setOfDone = mutableSetOf<Int>()
                if (trapezoid.isTraingle) {
                    if (!isReal[0]) {
                        setOfDone.add(0)
                        var coefs = getPerpendicular(points[0], points[2])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[0] = coefs[0]
                            trapezoid.bOfBisectors[0] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[0] = null
                            trapezoid.bOfBisectors[0] = null
                        }
                        coefs = getPerpendicular(points[1], points[2])
                        setOfDone.add(1)
                        if (coefs != null) {
                            trapezoid.kOfBisectors[1] = coefs[0]
                            trapezoid.bOfBisectors[1] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[1] = null
                            trapezoid.bOfBisectors[1] = null
                        }
                    }
                    if (!isReal[1]) {
                        setOfDone.add(1)
                        var coefs = getPerpendicular(points[1], points[0])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[1] = coefs[0]
                            trapezoid.bOfBisectors[1] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[1] = null
                            trapezoid.bOfBisectors[1] = null
                        }
                        coefs = getPerpendicular(points[2], points[0])
                        setOfDone.add(2)
                        if (coefs != null) {
                            trapezoid.kOfBisectors[2] = coefs[0]
                            trapezoid.bOfBisectors[2] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[2] = null
                            trapezoid.bOfBisectors[2] = null
                        }
                    }
                    if (!isReal[2]) {
                        setOfDone.add(2)
                        var coefs = getPerpendicular(points[2], points[1])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[2] = coefs[0]
                            trapezoid.bOfBisectors[2] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[2] = null
                            trapezoid.bOfBisectors[2] = null
                        }
                        coefs = getPerpendicular(points[0], points[1])
                        setOfDone.add(0)
                        if (coefs != null) {
                            trapezoid.kOfBisectors[0] = coefs[0]
                            trapezoid.bOfBisectors[0] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[0] = null
                            trapezoid.bOfBisectors[0] = null
                        }
                    }
                    if (!setOfDone.contains(0)) {
                        val bisector = when {
                            points[0].x <= points[2].x && points[0].x <= points[1].x -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[1], points[0]),
                                true,
                                true
                                )
                            points[0].x >= points[2].x && points[0].x <= points[1].x -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[1], points[0]),
                                false,
                                true
                            )
                            points[0].x <= points[2].x && points[0].x >= points[1].x -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[1], points[0]),
                                true,
                                false
                            )
                            else -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[1], points[0]),
                                false,
                                false
                            )
                        }
                        val coefs = if (bisector!!.k != null)
                            listOf(bisector.k, bisector.b)
                        else
                            null
                        //val coefs = getBisector(points[2], points[0], points[1])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[0] = coefs[0]
                            trapezoid.bOfBisectors[0] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[0] = null
                            trapezoid.bOfBisectors[0] = null
                        }
                    }
                    if (!setOfDone.contains(1)) {
                        val bisector = when {
                            points[1].x <= points[0].x && points[1].x <= points[2].x -> getBisector(
                                getLine(points[1], points[0]),
                                getLine(points[1], points[2]),
                                true,
                                true
                            )
                            points[1].x >= points[0].x && points[1].x <= points[2].x -> getBisector(
                                getLine(points[1], points[0]),
                                getLine(points[1], points[2]),
                                false,
                                true
                            )
                            points[1].x <= points[0].x && points[1].x >= points[2].x -> getBisector(
                                getLine(points[1], points[0]),
                                getLine(points[1], points[2]),
                                true,
                                false
                            )
                            else -> getBisector(
                                getLine(points[1], points[0]),
                                getLine(points[1], points[2]),
                                false,
                                false
                            )
                        }
                        val coefs = if (bisector!!.k != null)
                            listOf(bisector.k, bisector.b)
                        else
                            null
                        //val coefs = getBisector(points[0], points[1], points[2])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[1] = coefs[0]
                            trapezoid.bOfBisectors[1] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[1] = null
                            trapezoid.bOfBisectors[1] = null
                        }
                    }
                    if (!setOfDone.contains(2)) {
                        val bisector = when {
                            points[2].x <= points[0].x && points[2].x <= points[1].x -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[2], points[1]),
                                true,
                                true
                            )
                            points[2].x >= points[0].x && points[2].x <= points[1].x -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[2], points[1]),
                                false,
                                true
                            )
                            points[2].x <= points[0].x && points[2].x >= points[1].x -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[2], points[1]),
                                true,
                                false
                            )
                            else -> getBisector(
                                getLine(points[2], points[0]),
                                getLine(points[2], points[1]),
                                false,
                                false
                            )
                        }
                        val coefs = if (bisector!!.k != null)
                            listOf(bisector.k, bisector.b)
                        else
                            null
                        //val coefs = getBisector(points[1], points[2], points[0])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[2] = coefs[0]
                            trapezoid.bOfBisectors[2] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[2] = null
                            trapezoid.bOfBisectors[2] = null
                        }
                    }
                } else {
                    if (!isReal[0]) {
                        setOfDone.add(0)
                        var coefs = getPerpendicular(points[0], points[3])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[0] = coefs[0]
                            trapezoid.bOfBisectors[0] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[0] = null
                            trapezoid.bOfBisectors[0] = null
                        }
                        coefs = getPerpendicular(points[1], points[2])
                        setOfDone.add(1)
                        if (coefs != null) {
                            trapezoid.kOfBisectors[1] = coefs[0]
                            trapezoid.bOfBisectors[1] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[1] = null
                            trapezoid.bOfBisectors[1] = null
                        }
                    }
                    if (!isReal[1]) {
                        setOfDone.add(1)
                        var coefs = getPerpendicular(points[1], points[0])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[1] = coefs[0]
                            trapezoid.bOfBisectors[1] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[1] = null
                            trapezoid.bOfBisectors[1] = null
                        }
                        coefs = getPerpendicular(points[2], points[3])
                        setOfDone.add(2)
                        if (coefs != null) {
                            trapezoid.kOfBisectors[2] = coefs[0]
                            trapezoid.bOfBisectors[2] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[2] = null
                            trapezoid.bOfBisectors[2] = null
                        }
                    }
                    if (!isReal[2]) {
                        setOfDone.add(2)
                        var coefs = getPerpendicular(points[2], points[1])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[2] = coefs[0]
                            trapezoid.bOfBisectors[2] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[2] = null
                            trapezoid.bOfBisectors[2] = null
                        }
                        coefs = getPerpendicular(points[3], points[0])
                        setOfDone.add(3)
                        if (coefs != null) {
                            trapezoid.kOfBisectors[3] = coefs[0]
                            trapezoid.bOfBisectors[3] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[3] = null
                            trapezoid.bOfBisectors[3] = null
                        }
                    }
                    if (!isReal[3]) {
                        setOfDone.add(3)
                        var coefs = getPerpendicular(points[3], points[2])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[3] = coefs[0]
                            trapezoid.bOfBisectors[3] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[3] = null
                            trapezoid.bOfBisectors[3] = null
                        }
                        coefs = getPerpendicular(points[0], points[1])
                        setOfDone.add(0)
                        if (coefs != null) {
                            trapezoid.kOfBisectors[0] = coefs[0]
                            trapezoid.bOfBisectors[0] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[0] = null
                            trapezoid.bOfBisectors[0] = null
                        }
                    }
                    if (!setOfDone.contains(0)) {
                        val coefs = getBisector(points[3], points[0], points[1])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[0] = coefs[0]
                            trapezoid.bOfBisectors[0] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[0] = null
                            trapezoid.bOfBisectors[0] = null
                        }
                    }
                    if (!setOfDone.contains(1)) {
                        val coefs = getBisector(points[0], points[1], points[2])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[1] = coefs[0]
                            trapezoid.bOfBisectors[1] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[1] = null
                            trapezoid.bOfBisectors[1] = null
                        }
                    }
                    if (!setOfDone.contains(2)) {
                        val coefs = getBisector(points[1], points[2], points[3])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[2] = coefs[0]
                            trapezoid.bOfBisectors[2] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[2] = null
                            trapezoid.bOfBisectors[2] = null
                        }
                    }
                    if (!setOfDone.contains(3)) {
                        val coefs = getBisector(points[2], points[3], points[0])
                        if (coefs != null) {
                            trapezoid.kOfBisectors[3] = coefs[0]
                            trapezoid.bOfBisectors[3] = coefs[1]
                        } else {
                            trapezoid.kOfBisectors[3] = null
                            trapezoid.bOfBisectors[3] = null
                        }
                    }
                }
            }
        }

        fun buildMedialAxes(trapezoid: Trapezoid): MutableList<Point> {
            return if (trapezoid.isTraingle)
                findMedialAxesInTriangle(trapezoid)
            else
                findMedialAxesInTrapezoid(trapezoid)
        }

        fun findMedialAxesInTriangle(trapezoid: Trapezoid): MutableList<Point> {
            val perpendicular1: Line
            val perpendicular2: Line
            val bisector: Line?
            val points = trapezoid.getPoints()
            val intersection1: Point?
            val intersection2: Point?
            val len1: Float
            val len2: Float
            return if (points != null) {
                when {
                    !trapezoid.isReal[0] -> {
                        perpendicular1 = getPerpendicular(
                            getLine(points[2], points[0]),
                            points[0]
                            )
                        perpendicular2 = getPerpendicular(
                            getLine(points[2], points[1]),
                            points[1]
                        )
                        bisector = when {
                            points[2].x < points[1].x && points[2].x < points[0].x -> getBisector(
                                getLine(points[2], points[1]),
                                getLine(points[2], points[0]),
                                true,
                                true
                            )
                            points[2].x >= points[1].x && points[2].x <= points[0].x -> getBisector(
                                getLine(points[2], points[1]),
                                getLine(points[2], points[0]),
                                false,
                                true
                            )
                            points[2].x <= points[1].x && points[2].x >= points[0].x -> getBisector(
                                getLine(points[2], points[1]),
                                getLine(points[2], points[0]),
                                true,
                                false
                            )
                            else -> getBisector(
                                getLine(points[2], points[1]),
                                getLine(points[2], points[0]),
                                false,
                                false
                            )
                        }
                        if (bisector == null)
                            mutableListOf()
                        else {
                            intersection1 = getIntersection(bisector, perpendicular1)
                            intersection2 = getIntersection(bisector, perpendicular2)
                            println("$intersection1 $intersection2")
                            len1 = if (intersection1 != null)
                                length(points[1], intersection1)
                            else
                                -1f
                            len2 = if (intersection2 != null)
                                length(points[1], intersection2)
                            else
                                -1f
                            when {
                                len1 > 0f && len2 > 0f -> {
                                    if (len1 < len2)
                                        mutableListOf(intersection1!!)
                                    else
                                        mutableListOf(intersection2!!)
                                }
                                len1 < 0f && len2 > 0f ->
                                    mutableListOf(intersection2!!)
                                len1 > 0f && len2 < 0f ->
                                    mutableListOf(intersection1!!)
                                else ->
                                    mutableListOf()
                            }
                        }
                    }
                    !trapezoid.isReal[1] -> {
                        perpendicular1 = getPerpendicular(
                            getLine(points[0], points[1]),
                            points[1]
                        )
                        perpendicular2 = getPerpendicular(
                            getLine(points[0], points[2]),
                            points[2]
                        )
                        bisector = when {
                            points[0].x < points[2].x && points[0].x < points[1].x -> getBisector(
                                getLine(points[0], points[2]),
                                getLine(points[0], points[1]),
                                true,
                                true
                            )
                            points[0].x >= points[2].x && points[0].x <= points[1].x -> getBisector(
                                getLine(points[0], points[2]),
                                getLine(points[0], points[1]),
                                false,
                                true
                            )
                            points[0].x <= points[2].x && points[0].x >= points[1].x -> getBisector(
                                getLine(points[2], points[1]),
                                getLine(points[2], points[0]),
                                true,
                                false
                            )
                            else -> getBisector(
                                getLine(points[0], points[2]),
                                getLine(points[0], points[1]),
                                false,
                                false
                            )
                        }
                        if (bisector == null)
                            mutableListOf()
                        else {
                            intersection1 = getIntersection(bisector, perpendicular1)
                            intersection2 = getIntersection(bisector, perpendicular2)
                            println("$intersection1 $intersection2")
                            len1 = if (intersection1 != null)
                                length(points[0], intersection1)
                            else
                                -1f
                            len2 = if (intersection2 != null)
                                length(points[0], intersection2)
                            else
                                -1f
                            when {
                                len1 > 0f && len2 > 0f -> {
                                    if (len1 < len2)
                                        mutableListOf(intersection1!!)
                                    else
                                        mutableListOf(intersection2!!)
                                }
                                len1 < 0f && len2 > 0f ->
                                    mutableListOf(intersection2!!)
                                len1 > 0f && len2 < 0f ->
                                    mutableListOf(intersection1!!)
                                else ->
                                    mutableListOf()
                            }
                        }
                    }
                    !trapezoid.isReal[2] -> {
                        perpendicular1 = getPerpendicular(
                            getLine(points[1], points[2]),
                            points[2]
                        )
                        perpendicular2 = getPerpendicular(
                            getLine(points[1], points[0]),
                            points[0]
                        )
                        bisector = when {
                            points[1].x < points[2].x && points[1].x < points[0].x -> getBisector(
                                getLine(points[1], points[2]),
                                getLine(points[1], points[0]),
                                true,
                                true
                            )
                            points[1].x >= points[2].x && points[1].x <= points[0].x -> getBisector(
                                getLine(points[1], points[2]),
                                getLine(points[1], points[0]),
                                false,
                                true
                            )
                            points[1].x <= points[2].x && points[1].x >= points[0].x -> getBisector(
                                getLine(points[1], points[2]),
                                getLine(points[1], points[0]),
                                true,
                                false
                            )
                            else -> getBisector(
                                getLine(points[1], points[2]),
                                getLine(points[1], points[0]),
                                false,
                                false
                            )
                        }
                        println(perpendicular1)
                        println(perpendicular2)
                        println(bisector)
                        println("--")
                        if (bisector == null)
                            mutableListOf()
                        else {
                            intersection1 = getIntersection(bisector, perpendicular1)
                            intersection2 = getIntersection(bisector, perpendicular2)
                            println("$intersection1 $intersection2")
                            len1 = if (intersection1 != null)
                                length(points[1], intersection1)
                            else
                                -1f
                            len2 = if (intersection2 != null)
                                length(points[1], intersection2)
                            else
                                -1f
                            when {
                                len1 > 0f && len2 > 0f -> {
                                    if (len1 < len2)
                                        mutableListOf(intersection1!!)
                                    else
                                        mutableListOf(intersection2!!)
                                }
                                len1 < 0f && len2 > 0f ->
                                    mutableListOf(intersection2!!)
                                len1 > 0f && len2 < 0f ->
                                    mutableListOf(intersection1!!)
                                else ->
                                    mutableListOf()
                            }
                        }
                    }
                    else -> mutableListOf()
                }
            } else
                mutableListOf()
        }

        fun findMedialAxesInTrapezoid(trapezoid: Trapezoid): MutableList<Point> {
            val points = trapezoid.getPoints()
            return if (points != null) {
                if (countOfReal(trapezoid) == 2) {
                    val perpendicular1: Line
                    val perpendicular2: Line
                    val perpendicular3: Line
                    val perpendicular4: Line
                    val phantomPerpendicular1: Line
                    val phantomPerpendicular2: Line
                    val intersection1: Point?
                    val intersection2: Point?
                    val intersection3: Point?
                    val intersection4: Point?
                    val tmp1: Point
                    val tmp2: Point
                    val len1: Float
                    val len2: Float
                    val len3: Float
                    val len4: Float
                    when {
                        !trapezoid.isReal[0] -> {
                            perpendicular1 = getPerpendicular(
                                getLine(points[0], points[3]),
                                points[0]
                                )
                            perpendicular2 = getPerpendicular(
                                getLine(points[1], points[2]),
                                points[1]
                            )
                            perpendicular3 = getPerpendicular(
                                getLine(points[2], points[1]),
                                points[2]
                            )
                            perpendicular4 = getPerpendicular(
                                getLine(points[3], points[0]),
                                points[3]
                            )
                            tmp1 = Point(
                                (points[0].x + points[1].x) / 2f,
                                points[0].y
                            )
                            tmp2 = Point(
                                (points[3].x + points[2].x) / 2f,
                                points[3].y
                            )
                            phantomPerpendicular1 = getPerpendicular(
                                getLine(points[0], tmp1),
                                tmp1
                            )
                            phantomPerpendicular2 = getPerpendicular(
                                getLine(points[3], tmp2),
                                tmp2
                            )
                            intersection1 = getIntersection(perpendicular1, phantomPerpendicular1)
                            intersection2 = getIntersection(perpendicular2, phantomPerpendicular1)
                            intersection3 = getIntersection(perpendicular3, phantomPerpendicular2)
                            intersection4 = getIntersection(perpendicular4, phantomPerpendicular2)
                            val res = mutableListOf<Point>()
                            when {
                                intersection1 != null && intersection2 != null -> {
                                    len1 = length(intersection1, tmp1)
                                    len2 = length(intersection2, tmp1)
                                    if (len1 < len2)
                                        res.add(intersection1)
                                    else
                                        res.add(intersection2)
                                }
                                intersection1 != null && intersection2 == null -> res.add(intersection1)
                                intersection1 == null && intersection2 != null -> res.add(intersection2)
                            }
                            when {
                                intersection3 != null && intersection4 != null -> {
                                    len3 = length(intersection3, tmp2)
                                    len4 = length(intersection4, tmp2)
                                    if (len3 < len4)
                                        res.add(intersection3)
                                    else
                                        res.add(intersection4)
                                }
                                intersection3 != null && intersection4 == null -> res.add(intersection3)
                                intersection3 == null && intersection4 != null -> res.add(intersection4)
                            }
                            res
                        }
                        !trapezoid.isReal[1] -> {
                            perpendicular1 = getPerpendicular(
                                getLine(points[1], points[0]),
                                points[1]
                            )
                            perpendicular2 = getPerpendicular(
                                getLine(points[2], points[3]),
                                points[2]
                            )
                            perpendicular3 = getPerpendicular(
                                getLine(points[3], points[2]),
                                points[3]
                            )
                            perpendicular4 = getPerpendicular(
                                getLine(points[0], points[1]),
                                points[0]
                            )
                            tmp1 = Point(
                                (points[1].x + points[2].x) / 2f,
                                points[1].y
                            )
                            tmp2 = Point(
                                (points[0].x + points[3].x) / 2f,
                                points[0].y
                            )
                            phantomPerpendicular1 = getPerpendicular(
                                getLine(points[1], tmp1),
                                tmp1
                            )
                            phantomPerpendicular2 = getPerpendicular(
                                getLine(points[3], tmp2),
                                tmp2
                            )
                            intersection1 = getIntersection(perpendicular1, phantomPerpendicular1)
                            intersection2 = getIntersection(perpendicular2, phantomPerpendicular1)
                            intersection3 = getIntersection(perpendicular3, phantomPerpendicular2)
                            intersection4 = getIntersection(perpendicular4, phantomPerpendicular2)
                            val res = mutableListOf<Point>()
                            when {
                                intersection1 != null && intersection2 != null -> {
                                    len1 = length(intersection1, tmp1)
                                    len2 = length(intersection2, tmp1)
                                    if (len1 < len2)
                                        res.add(intersection1)
                                    else
                                        res.add(intersection2)
                                }
                                intersection1 != null && intersection2 == null -> res.add(intersection1)
                                intersection1 == null && intersection2 != null -> res.add(intersection2)
                            }
                            when {
                                intersection3 != null && intersection4 != null -> {
                                    len3 = length(intersection3, tmp2)
                                    len4 = length(intersection4, tmp2)
                                    if (len3 < len4)
                                        res.add(intersection3)
                                    else
                                        res.add(intersection4)
                                }
                                intersection3 != null && intersection4 == null -> res.add(intersection3)
                                intersection3 == null && intersection4 != null -> res.add(intersection4)
                            }
                            res
                        }
                        else ->
                            mutableListOf()
                    }
                } else {
                    val perpendicular1: Line
                    val perpendicular2: Line
                    val bisector1: Line?
                    val bisector2: Line?
                    val phantomPerpendicular: Line
                    val phantomBisector: Line?
                    val intersection1: Point?
                    val intersection2: Point?
                    val intersection3: Point?
                    val intersection4: Point?
                    val tmpIntersection: Point?
                    val tmp: Point
                    val len1: Float
                    val len2: Float
                    val len3: Float
                    when {
                        !trapezoid.isReal[0] -> {
                            perpendicular1 = getPerpendicular(
                                getLine(points[0], points[3]),
                                points[0]
                            )
                            perpendicular2 = getPerpendicular(
                                getLine(points[1], points[2]),
                                points[1]
                            )
                            bisector1 = when {
                                points[2].x <= points[1].x && points[2].x <= points[3].x ->
                                    getBisector(
                                        getLine(points[2], points[1]),
                                        getLine(points[2], points[3]),
                                        true,
                                        true
                                    )
                                points[2].x >= points[1].x && points[2].x <= points[3].x ->
                                    getBisector(
                                        getLine(points[2], points[1]),
                                        getLine(points[2], points[3]),
                                        false,
                                        true
                                    )
                                points[2].x <= points[1].x && points[2].x >= points[3].x ->
                                    getBisector(
                                        getLine(points[2], points[1]),
                                        getLine(points[2], points[3]),
                                        true,
                                        false
                                    )
                                else ->
                                    getBisector(
                                        getLine(points[2], points[1]),
                                        getLine(points[2], points[3]),
                                        false,
                                        false
                                    )
                            }
                            bisector2 = when {
                                points[3].x <= points[2].x && points[3].x <= points[0].x ->
                                    getBisector(
                                        getLine(points[3], points[2]),
                                        getLine(points[3], points[0]),
                                        true,
                                        true
                                    )
                                points[3].x >= points[2].x && points[3].x <= points[0].x ->
                                    getBisector(
                                        getLine(points[3], points[2]),
                                        getLine(points[3], points[0]),
                                        false,
                                        true
                                    )
                                points[3].x <= points[2].x && points[3].x >= points[0].x ->
                                    getBisector(
                                        getLine(points[3], points[2]),
                                        getLine(points[3], points[0]),
                                        true,
                                        false
                                    )
                                else ->
                                    getBisector(
                                        getLine(points[3], points[2]),
                                        getLine(points[3], points[0]),
                                        false,
                                        false
                                    )
                            }
                            tmp = Point(
                                (points[1].x + points[0].x) / 2f,
                                points[1].y
                            )
                            phantomPerpendicular = getPerpendicular(
                                getLine(points[1], tmp),
                                tmp
                            )
                            val res = mutableListOf<Point>()
                            if (bisector1 != null && bisector2 != null) {
                                intersection1 = getIntersection(
                                    bisector1,
                                    bisector2
                                )
                                if (intersection1 != null) {
                                    res.add(intersection1)
                                    tmpIntersection = getIntersection(
                                        getLine(points[1], points[2]),
                                        getLine(points[0], points[3])
                                    )
                                    if (tmpIntersection != null) {
                                        phantomBisector = when {
                                            tmpIntersection.x <= points[1].x && tmpIntersection.x <= points[0].x ->
                                                getBisector(
                                                    getLine(tmpIntersection, points[1]),
                                                    getLine(tmpIntersection, points[0]),
                                                    true,
                                                    true
                                                )
                                            tmpIntersection.x >= points[1].x && tmpIntersection.x <= points[0].x ->
                                                getBisector(
                                                    getLine(tmpIntersection, points[1]),
                                                    getLine(tmpIntersection, points[0]),
                                                    false,
                                                    true
                                                )
                                            tmpIntersection.x <= points[1].x && tmpIntersection.x >= points[0].x ->
                                                getBisector(
                                                    getLine(tmpIntersection, points[1]),
                                                    getLine(tmpIntersection, points[0]),
                                                    true,
                                                    false
                                                )
                                            else ->
                                                getBisector(
                                                    getLine(tmpIntersection, points[1]),
                                                    getLine(tmpIntersection, points[0]),
                                                    false,
                                                    false
                                                )
                                        }
                                        if (phantomBisector != null) {
                                            intersection2 = getIntersection(
                                                phantomBisector,
                                                perpendicular1
                                            )
                                            intersection3 = getIntersection(
                                                phantomBisector,
                                                perpendicular2
                                            )
                                            intersection4 = getIntersection(
                                                phantomBisector,
                                                phantomPerpendicular
                                            )
                                            len1 = if (intersection2 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection2, tmp)
                                            len2 = if (intersection3 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection3, tmp)
                                            len3 = if (intersection4 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection4, tmp)
                                            when {
                                                len1 <= len2 && len1 <= len3 && len1 != Float.MAX_VALUE-> res.add(intersection2!!)
                                                len2 <= len1 && len2 <= len3 && len2 != Float.MAX_VALUE -> res.add(intersection3!!)
                                                len3 <= len2 && len3 <= len2 && len3 != Float.MAX_VALUE -> res.add(intersection3!!)
                                            }
                                        }
                                    }
                                }
                            }
                            res
                        }
                        !trapezoid.isReal[2] -> {
                            perpendicular1 = getPerpendicular(
                                getLine(points[1], points[2]),
                                points[2]
                            )
                            perpendicular2 = getPerpendicular(
                                getLine(points[0], points[3]),
                                points[3]
                            )
                            var isPlusA = points[0].x <= points[3].x
                            var isPlusB = points[0].x <= points[1].x
                            bisector1 = getBisector(
                                getLine(points[0], points[3]),
                                getLine(points[0], points[1]),
                                isPlusA,
                                isPlusB
                            )
                            isPlusA = points[1].x <= points[0].x
                            isPlusB = points[1].x <= points[2].x
                            bisector2 = getBisector(
                                getLine(points[1], points[0]),
                                getLine(points[1], points[2]),
                                isPlusA,
                                isPlusB
                            )
                            tmp = Point(
                                (points[3].x + points[2].x) / 2f,
                                points[3].y
                            )
                            phantomPerpendicular = getPerpendicular(
                                getLine(points[2], tmp),
                                tmp
                            )
                            val res = mutableListOf<Point>()
                            if (bisector1 != null && bisector2 != null) {
                                intersection1 = getIntersection(
                                    bisector1,
                                    bisector2
                                )
                                if (intersection1 != null) {
                                    res.add(intersection1)
                                    tmpIntersection = getIntersection(
                                        getLine(points[0], points[3]),
                                        getLine(points[1], points[2])
                                    )
                                    if (tmpIntersection != null) {
                                        isPlusA = tmpIntersection.x <= points[3].x
                                        isPlusB = tmpIntersection.x <= points[2].x
                                        phantomBisector = getBisector(
                                            getLine(tmpIntersection, points[3]),
                                            getLine(tmpIntersection, points[2]),
                                            isPlusA,
                                            isPlusB
                                        )
                                        if (phantomBisector != null) {
                                            intersection2 = getIntersection(
                                                phantomBisector,
                                                perpendicular1
                                            )
                                            intersection3 = getIntersection(
                                                phantomBisector,
                                                perpendicular2
                                            )
                                            intersection4 = getIntersection(
                                                phantomBisector,
                                                phantomPerpendicular
                                            )
                                            len1 = if (intersection2 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection2, tmp)
                                            len2 = if (intersection3 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection3, tmp)
                                            len3 = if (intersection4 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection4, tmp)
                                            when {
                                                len1 <= len2 && len1 <= len3 && len1 != Float.MAX_VALUE -> res.add(intersection2!!)
                                                len2 <= len1 && len2 <= len3 && len2 != Float.MAX_VALUE -> res.add(intersection3!!)
                                                len3 <= len2 && len3 <= len2 && len3 != Float.MAX_VALUE -> res.add(intersection3!!)
                                            }
                                        }
                                    }
                                }
                            }
                            res
                        }
                        !trapezoid.isReal[3] -> {
                            perpendicular1 = getPerpendicular(
                                getLine(points[3], points[2]),
                                points[3]
                            )
                            perpendicular2 = getPerpendicular(
                                getLine(points[0], points[1]),
                                points[0]
                            )
                            var isPlusA = points[1].x <= points[0].x
                            var isPlusB = points[1].x <= points[2].x
                            bisector1 = getBisector(
                                getLine(points[1], points[0]),
                                getLine(points[1], points[2]),
                                isPlusA,
                                isPlusB
                            )
                            isPlusA = points[2].x <= points[1].x
                            isPlusB = points[2].x <= points[3].x
                            bisector2 = getBisector(
                                getLine(points[2], points[1]),
                                getLine(points[2], points[3]),
                                isPlusA,
                                isPlusB
                            )
                            tmp = Point(
                                (points[3].x + points[0].x) / 2f,
                                points[0].y
                            )
                            phantomPerpendicular = getPerpendicular(
                                getLine(points[0], tmp),
                                tmp
                            )
                            val res = mutableListOf<Point>()
                            if (bisector1 != null && bisector2 != null) {
                                intersection1 = getIntersection(
                                    bisector1,
                                    bisector2
                                )
                                if (intersection1 != null) {
                                    res.add(intersection1)
                                    tmpIntersection = getIntersection(
                                        getLine(points[0], points[1]),
                                        getLine(points[3], points[2])
                                    )
                                    if (tmpIntersection != null) {
                                        isPlusA = tmpIntersection.x <= points[0].x
                                        isPlusB = tmpIntersection.x <= points[3].x
                                        phantomBisector = getBisector(
                                            getLine(tmpIntersection, points[0]),
                                            getLine(tmpIntersection, points[3]),
                                            isPlusA,
                                            isPlusB
                                        )
                                        if (phantomBisector != null) {
                                            intersection2 = getIntersection(
                                                phantomBisector,
                                                perpendicular1
                                            )
                                            intersection3 = getIntersection(
                                                phantomBisector,
                                                perpendicular2
                                            )
                                            intersection4 = getIntersection(
                                                phantomBisector,
                                                phantomPerpendicular
                                            )
                                            len1 = if (intersection2 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection2, tmp)
                                            len2 = if (intersection3 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection3, tmp)
                                            len3 = if (intersection4 == null)
                                                Float.MAX_VALUE
                                            else
                                                length(intersection4, tmp)
                                            when {
                                                len1 <= len2 && len1 <= len3 && len1 != Float.MAX_VALUE -> res.add(intersection2!!)
                                                len2 <= len1 && len2 <= len3 && len2 != Float.MAX_VALUE -> res.add(intersection3!!)
                                                len3 <= len2 && len3 <= len2 && len3 != Float.MAX_VALUE -> res.add(intersection3!!)
                                            }
                                        }
                                    }
                                }
                            }
                            res
                        }
                        else -> mutableListOf()
                    }
                }
            } else
                mutableListOf()
        }

        fun length(point1: Point, point2: Point): Float =
            sqrt((point1.x - point2.x).pow(2) + (point1.y - point2.y).pow(2))

        fun countOfReal(trapezoid: Trapezoid): Int {
            var count = 0
            val isReal = trapezoid.isReal
            for (i in isReal)
                if (i)
                    count++
            return count
        }
    }
}