package com.example.demo.app

import com.example.demo.core.useCases.FileFormat
import com.example.demo.core.useCases.Intersection
import com.google.gson.Gson
import domain.Point
import domain.Polygon
import tornadofx.*
import tornadofx.osgi.impl.getBundleId
import useCases.Type
import java.io.File

class AppController : Controller() {

    val polygon = Polygon()
    private val sortedByY = mutableListOf<Int>()
    val trapezoidAddition = mutableListOf<List<Point>>()
    val trapezoids = mutableListOf<Polygon>()

    fun fill() {
        val gson = Gson()
        val polygonPoints = gson.fromJson(File("Test1.json").readText(), FileFormat::class.java)
        println(polygonPoints.x)
        println(polygonPoints.y)
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
            println("sorted by y: $sortedByY")
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
            println("sorted by y: $sortedByY")
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
            println("edgelist: $edgeList")
            val intersections = mutableMapOf<Int, MutableList<Intersection?>>()//mutableMapOf<Int, MutableList<List<Int>>>()
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
                                            intersections[i] = mutableListOf(Intersection(
                                                i,
                                                listOf(edge[0], edge[1]),
                                                Point(x, points[i].y)
                                            ), null)
                                        } else {
                                            intersections[i] = mutableListOf(null, Intersection(
                                                i,
                                                listOf(edge[0], edge[1]),
                                                Point(x, points[i].y)
                                            ))
                                        }
                                    }
                                }
                            }
                        }
                }
            println("intersections: $intersections")
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
            val mainPolygon = this.polygon.copy()
            /*var k: Float
            var b: Float
            for (intersection in intersections) {
                for (edge in intersection.value) {
                    if (points[edge[0]].x != points[edge[1]].x) {
                        k = (points[edge[0]].y - points[edge[1]].y) / (points[edge[0]].x - points[edge[1]].x)
                        b = points[edge[0]].y - k * points[edge[0]].x
                        val x = (points[intersection.key].y - b) / k
                        when (types[intersection.key]) {
                            Type.RIGHT -> {

                            }
                        }
                        /*trapezoidAddition.add(
                            listOf(
                                Point(points[intersection.key].x, points[intersection.key].y),
                                Point((points[intersection.key].y - b) / k, points[intersection.key].y)
                            )
                        )*/
                    } else {
                        trapezoidAddition.add(
                            listOf(
                                Point(points[intersection.key].x, points[intersection.key].y),
                                Point(points[edge[0]].x, points[intersection.key].y)
                            )
                        )
                    }
                    mainPolygon.insert(trapezoidAddition.last()[1])
                    println(trapezoidAddition.last()[1])
                }
            }*/
            println(trapezoidAddition)
            /*val trapezoidPoints = mutableMapOf<Float, MutableList<Int>>()
            points = mainPolygon.getPoints()
            types = mainPolygon.getTypes()
            if (points != null && types != null) {
                for (i in points.indices) {
                    if (types[i] == Type.MARK) {
                        if (trapezoidPoints.containsKey(points[i].y))
                            trapezoidPoints[points[i].y]?.add(i)
                        else
                            trapezoidPoints[points[i].y] = mutableListOf(i)
                    }
                }
                println(trapezoidPoints)
                //map can be unsorted
            }*/
            /*points = mainPolygon.getPoints()
            types = mainPolygon.getTypes()
            var polygons: MutableList<Polygon>
            var maxInd: Int
            if (points != null && types != null) {
                maxInd = 0
                for (i in points.indices) {
                    if (points[i].y > points[maxInd].y && types[i] == Type.MARK)
                        maxInd = i
                }
                
            }*/
        }
    }
}