package com.example.demo.app

import com.example.demo.core.domain.*
import com.example.demo.core.useCases.*
import com.example.demo.view.MainView
import com.google.gson.Gson
import tornadofx.*
import java.io.File
import kotlin.math.PI

class AppController : Controller() {

    private val mainView: MainView by inject()

    val polygon = Polygon()
    lateinit var trapezoids: MutableList<Trapezoid>
    val medialAxes = mutableListOf<MutableList<Point>>()
    var centers = mutableListOf<Point>()
    var snapshot: CalculationSnapshot? = CalculationSnapshot(
        Polygon(),
        mutableListOf(),
        Point(0f, 0f),
        Polygon(),
        Polygon()
    )

    fun nextStep() {
        mainView.root += mainView.button
        snapshot = Geometric.findSimpleVoronoiDiagram(
            snapshot!!.tmpPolygon,
            polygon
        )
        if (snapshot != null)
            centers += snapshot!!.center
    }

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
        trapezoids = Geometric.trapezoidateToList(polygon)
        val trapezoidsTree = Geometric.trapezoidateToTree(trapezoids)
        if (trapezoidsTree != null) {
            val treeTraveler = TreeTraveler(trapezoidsTree)
            println(treeTraveler.fromLeavesToRootTravel())
        }
        for (i in trapezoids) {
            medialAxes.add(Geometric.buildMedialAxes(i))
        }
        println(medialAxes)
    }

    fun testDiagram() {
        val polygonPoints = Gson().fromJson(File("Test6.json").readText(), FileFormat::class.java)
        val scale = 2f
        for (i in 0..polygonPoints.x.lastIndex)
            polygon += Point(polygonPoints.x[i], polygonPoints.y[i]) * scale
        snapshot = Geometric.findSimpleVoronoiDiagram(polygon, polygon)
        centers += snapshot!!.center
    }
}