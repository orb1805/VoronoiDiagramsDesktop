package com.example.demo.core.useCases

import com.example.demo.core.domain.Trapezoid
import domain.Point
import domain.Polygon
import useCases.Type
import kotlin.math.min
import kotlin.math.max

class Geometric {
    companion object {

        fun trapezoidateToList(polygon: Polygon) : MutableList<Trapezoid> {
            val trapezoids = mutableListOf<Polygon>()
            val trapezoidAddition = mutableListOf<List<Point>>()
            val points = polygon.getPoints()
            val types = polygon.getTypes()
            if (points != null && types != null) {
                if (points.size > 3) {
                    val edgeList = constructEdgeList(points)
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
                    for ((_, value) in intersections) {
                        for (intersection in value) {
                            if (intersection != null)
                                trapezoidAddition.add(
                                    listOf(
                                        points[intersection.indexOfVertex],
                                        intersection.pointOfEdge
                                    )
                                )
                        }
                    }
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
                    var tmpInter: Intersection
                    for (i in 0 until inters.lastIndex)
                        for (j in i + 1..inters.lastIndex)
                            if (inters[i].pointOfEdge.y < inters[j].pointOfEdge.y) {
                                tmpInter = inters[i]
                                inters[i] = inters[j]
                                inters[j] = tmpInter
                            }
                    val deletionController = mutableListOf<Boolean>()
                    for (type in types)
                        if (type == Type.WIDE)
                            deletionController.add(true)
                        else
                            deletionController.add(false)
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
                                    if (deletionController[inters[i].indexOfVertex])
                                        deletionController[inters[i].indexOfVertex] = false
                                    else
                                        setOfDeleted.add(i)
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
                                    if (deletionController[inters[i].indexOfVertex])
                                        deletionController[inters[i].indexOfVertex] = false
                                    else
                                        setOfDeleted.add(i)
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
                                    if (deletionController[inters[i].indexOfVertex])
                                        deletionController[inters[i].indexOfVertex] = false
                                    else
                                        setOfDeleted.add(i)
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
                                    if (deletionController[inters[i].indexOfVertex])
                                        deletionController[inters[i].indexOfVertex] = false
                                    else
                                        setOfDeleted.add(i)
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
                    val listToDelete = mutableListOf<Int>()
                    for (i in trapezoids.indices)
                        if (trapezoids[i].isDegenerate())
                            listToDelete.add(i)
                    for (i in listToDelete.lastIndex downTo 0)
                        trapezoids.removeAt(listToDelete[i])
                }
            }
            val result = mutableListOf<Trapezoid>()
            var tmp: Trapezoid?
            for (i in trapezoids.indices) {
                tmp = trapezoids[i].toTrapezoid(i)
                if (tmp != null)
                    result.add(tmp)
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

        private fun getCoefficients(point1: Point, point2: Point): List<Float>? {
            return if (point1.x != point2.x)
                listOf(
                    (point1.y - point2.y) / (point1.x - point2.x),
                    point1.y - (point1.y - point2.y) / (point1.x - point2.x) * point1.x
                )
            else
                null
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
    }
}