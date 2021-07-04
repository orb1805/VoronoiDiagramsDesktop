package com.example.demo.app

import com.example.demo.core.domain.Trapezoid
import com.example.demo.core.useCases.*
import com.google.gson.Gson
import domain.Point
import domain.Polygon
import tornadofx.*
import useCases.Type
import java.io.File

class AppController : Controller() {

    val polygon = Polygon()
    lateinit var trapezoids: MutableList<Trapezoid>

    fun fill() {
        val gson = Gson()
        val polygonPoints = gson.fromJson(File("Test2.json").readText(), FileFormat::class.java)
        for (i in 0..polygonPoints.x.lastIndex)
            polygon.addNode(
                Point(
                    polygonPoints.x[i],
                    polygonPoints.y[i]
                )
            )
        trapezoids = Geometric.trapezoidateToList(polygon)
        val trapezoidsTree = Geometric.trapezoidateToTree(trapezoids)
        if (trapezoidsTree != null) {
            val treeTraveler = TreeTraveler(trapezoidsTree)
            println(treeTraveler.fromLeavesToRootTravel())
        }
    }

    fun test() {
        val parent = Node(2)
        var node1 = Node(7)
        var node2 = Node(6)
        var node3 = Node(3)
        var node4 = Node(2)
        var node5 = Node(1)
        var node6 = Node(4)
        var node7 = Node(3)
        var node8 = Node(4)
        parent.addChild(node1)
        parent.addChild(node2)
        node1.addChild(node3)
        node2.addChild(node4)
        node2.addChild(node5)
        node2.addChild(node6)
        node3.addChild(node7)
        node3.addChild(node8)
        val treeTraveler = TreeTraveler(parent)
        println(treeTraveler.fromLeavesToRootTravel())
    }
}