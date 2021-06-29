package com.example.demo.app

import com.example.demo.core.useCases.FileFormat
import com.example.demo.core.useCases.Intersection
import com.google.gson.Gson
import domain.Point
import domain.Polygon
import tornadofx.*
import useCases.Type
import java.io.File
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.min

class AppController : Controller() {

    val polygon = Polygon()
    private val sortedByY = mutableListOf<Int>()
    val trapezoidAddition = mutableListOf<List<Point>>()
    val trapezoids = mutableListOf<Polygon>()

    fun fill() {
        val gson = Gson()
        val polygonPoints = gson.fromJson(File("Test1.json").readText(), FileFormat::class.java)
        for (i in 0..polygonPoints.x.lastIndex)
            polygon.addNode(
                Point(
                    polygonPoints.x[i],
                    polygonPoints.y[i]
                )
            )
        var points = polygon.getPoints()
        var types = polygon.getTypes()
        if (points != null && types != null) {
            val tmp = mutableListOf<Float>()
            for (i in 0..points.lastIndex)
                tmp.add(points[i].y)
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
            val intersections =
                mutableMapOf<Int, MutableList<Intersection?>>()//mutableMapOf<Int, MutableList<List<Int>>>()
            var k: Float
            var b: Float
            for (i in 0..types.lastIndex)
                if (types[i] != Type.NONE) {
                    for (edge in edgeList)
                        if (
                            ((points[edge[0]].y >= points[i].y && points[edge[1]].y <= points[i].y) || (points[edge[0]].y <= points[i].y && points[edge[1]].y >= points[i].y))
                            && i != edge[0] && i != edge[1]
                        ) {
                            var x: Float
                            if (points[edge[0]].x != points[edge[1]].x) {
                                k = (points[edge[0]].y - points[edge[1]].y) / (points[edge[0]].x - points[edge[1]].x)
                                b = points[edge[0]].y - k * points[edge[0]].x
                                x = (points[i].y - b) / k
                            } else {
                                x = points[edge[0]].x
                            }
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
                                                        Intersection(i, listOf(edge[0], edge[1]), Point(x, points[i].y))

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
                                                        Intersection(i, listOf(edge[0], edge[1]), Point(x, points[i].y))
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
            println(intersections)
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
            val inters = mutableListOf<Intersection>()
            for (i in intersections) {
                for (j in i.value) {
                    inters.add(j!!)
                }
            }
            var tmpInter: Intersection
            for (i in 0 until inters.lastIndex)
                for (j in i + 1 .. inters.lastIndex)
                    if (inters[i].pointOfEdge.y < inters[j].pointOfEdge.y) {
                        tmpInter = inters[i]
                        inters[i] = inters[j]
                        inters[j] = tmpInter
                    }
            println(inters.size)
            val deletionController = mutableListOf<Boolean>()
            for (type in types)
                if (type == Type.WIDE)
                    deletionController.add(true)
                else
                    deletionController.add(false)
            val setOfDeleted = mutableSetOf<Int>()
            for (i in inters.indices) {
                polygon = Polygon()
                val a = min(inters[i].indexOfVertex, inters[i].indexesOfEdge[0])
                val c = max(inters[i].indexOfVertex, inters[i].indexesOfEdge[0])
                length1 = c - a//abs(inters[i].indexOfVertex - inters[i].indexesOfEdge[0])
                count = 0
                for (j in setOfDeleted)
                    if (j in a..c)
                        count++
                length1 -= count
                /*if (inters[i].indexOfVertex > inters[i].indexesOfEdge[0])
                    length1--*/
                length2 = points.size - setOfDeleted.size - length1 - 1
                println("len1: $length1  len2: $length2")
                if (length1 == length2) {
                    if (points[inters[i].indexesOfEdge[0]].y > points[inters[i].indexOfVertex].y)
                        length2++
                    else
                        length1++
                }
                if (length1 < length2) {
                    println("I choose len1")
                    if (inters[i].indexOfVertex > inters[i].indexesOfEdge[0]) {
                        //from indexOfVertex downTo indexesOfEdge[0]
                        count = 0
                        polygon.reverseAddNode(points[inters[i].indexOfVertex])
                        if (types[inters[i].indexOfVertex] == Type.WIDE) {
                            if (deletionController[inters[i].indexOfVertex])
                                deletionController[inters[i].indexOfVertex] = false
                            else
                                setOfDeleted.add(i)
                        }
                        for (j in inters[i].indexOfVertex - 1 downTo inters[i].indexesOfEdge[0]) {
                            if (!setOfDeleted.contains(j)) {
                                polygon.reverseAddNode(points[j])
                                //mainPolygon.removeNode(points[j])
                                setOfDeleted.add(j)
                                count++
                            }
                        }
                        polygon.reverseAddNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    } else {
                        //from indexOfVertex to indexesOfEdge[0]
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
                                //mainPolygon.removeNode(points[j])
                                setOfDeleted.add(j)
                                count++
                            }
                        }
                        polygon.addNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    }
                } else {
                    println("I choose len2")
                    if (inters[i].indexOfVertex > inters[i].indexesOfEdge[1]) {
                        //from indexOfVertex downTo indexesOfEdge[1]
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
                                //mainPolygon.removeNode(points[j])
                                setOfDeleted.add(j)
                                count++
                            }
                        }
                        polygon.reverseAddNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    } else {
                        //from indexOfVertex to indexesOfEdge[1]
                        count = 0
                        polygon.addNode(points[inters[i].indexOfVertex])
                        if (types[inters[i].indexOfVertex] == Type.WIDE) {
                            if (deletionController[inters[i].indexOfVertex])
                                deletionController[inters[i].indexOfVertex] = false
                            else
                                setOfDeleted.add(i)
                        }
                        for (j in inters[i].indexOfVertex + 1..inters[i].indexesOfEdge[1]) {
                            if (!setOfDeleted.contains(j)) {
                                polygon.addNode(points[j])
                                //mainPolygon.removeNode(points[j])
                                setOfDeleted.add(j)
                                count++
                            }
                        }
                        polygon.addNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    }
                }
                println(setOfDeleted)
                trapezoids.add(polygon)
            }
            /*for (i in inters.indices) {
                polygon = Polygon()
                if (points[if (inters[i].indexOfVertex + 1 != points.size) inters[i].indexOfVertex + 1; else 0].y > points[inters[i].indexOfVertex].y &&
                    points[if (inters[i].indexOfVertex - 1 != -1) inters[i].indexOfVertex - 1; else points.lastIndex].y > points[inters[i].indexOfVertex].y) {

                }
                if (points[if (inters[i].indexOfVertex + 1 != points.size) inters[i].indexOfVertex + 1; else 0].y > points[inters[i].indexOfVertex].y) {
                    val maxInd = if (points[inters[i].indexesOfEdge[0]].y > points[inters[i].indexesOfEdge[1]].y)
                        inters[i].indexesOfEdge[0]
                    else
                        inters[i].indexesOfEdge[1]
                    if (inters[i].indexOfVertex < maxInd) {
                        polygon.addNode(points[inters[i].indexOfVertex])
                        for (j in inters[i].indexOfVertex + 1..maxInd) {
                            polygon.addNode(points[j])
                            setOfDeleted.add(j)
                        }
                        polygon.addNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    } else {
                        polygon.addNode(points[inters[i].indexOfVertex])
                        for (j in inters[i].indexOfVertex + 1..points.lastIndex) {
                            polygon.addNode(points[j])
                            setOfDeleted.add(j)
                        }
                        for (j in 0..maxInd) {
                            polygon.addNode(points[j])
                            setOfDeleted.add(j)
                        }
                        polygon.addNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    }
                } else {
                    val minInd = if (points[inters[i].indexesOfEdge[0]].y > points[inters[i].indexesOfEdge[1]].y)
                        inters[i].indexesOfEdge[0]
                    else
                        inters[i].indexesOfEdge[1]
                    if (inters[i].indexOfVertex > minInd) {
                        polygon.reverseAddNode(points[inters[i].indexOfVertex])
                        for (j in inters[i].indexOfVertex - 1 downTo minInd) {
                            polygon.reverseAddNode(points[j])
                            setOfDeleted.add(j)
                        }
                        polygon.reverseAddNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    } else {
                        polygon.reverseAddNode(points[inters[i].indexOfVertex])
                        for (j in inters[i].indexOfVertex - 1 downTo 0) {
                            polygon.reverseAddNode(points[j])
                            setOfDeleted.add(j)
                        }
                        for (j in points.lastIndex .. minInd) {
                            polygon.reverseAddNode(points[j])
                            setOfDeleted.add(j)
                        }
                        polygon.reverseAddNode(inters[i].pointOfEdge)
                        points[setOfDeleted.last()] = inters[i].pointOfEdge
                        setOfDeleted.remove(setOfDeleted.last())
                    }
                }
                println(setOfDeleted)
                trapezoids.add(polygon)
            }*/
        }
    }
}