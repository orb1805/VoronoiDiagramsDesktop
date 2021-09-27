package com.example.demo.app

import com.example.demo.core.domain.CalculationSnapshot
import com.example.demo.core.domain.Line
import com.example.demo.core.domain.Trapezoid
import com.example.demo.core.useCases.*
import com.example.demo.view.MainView
import com.google.gson.Gson
import com.example.demo.core.domain.Point
import com.example.demo.core.domain.Polygon
import tornadofx.*
import java.io.File

class AppController : Controller() {

    private val mainView: MainView by inject()

    val polygon = Polygon()
    lateinit var trapezoids: MutableList<Trapezoid>
    val medialAxes = mutableListOf<MutableList<Point>>()
    var centers = mutableListOf<Point>()
    var snapshot: CalculationSnapshot? = CalculationSnapshot(Polygon(), mutableListOf(), Point(0f, 0f), Polygon(), Polygon())

    val testPolygon = Polygon()

    fun nextStep() {
        mainView.root += mainView.button
        snapshot = Geometric.findSimpleVoronoiDiagram(
            snapshot!!.tmpPolygon,
            polygon
        )
        if (snapshot != null)
            centers += snapshot!!.center
    }

    fun testIntersection() {
        testPolygon.addNode(Point(0f, 0f))
        testPolygon.addNode(Point(100f, 0f))
        testPolygon.addNode(Point(100f, 100f))
        testPolygon.addNode(Point(0f, 100f))
        val points = testPolygon.getPoints() ?: return
        val line1 = Line.getLine(Point(100f, 100f), Point(50f, -50f))
        val line2 = Line.getLine(Point(0f, 100f), Point(50f, -50f))
        var count = 0
        for (i in 0 until points.lastIndex)
            if (Geometric.areIntersected(Point(100f, 100f), Point(50f, -50f), points[i], points[i + 1])
                || Geometric.areIntersected(Point(0f, 100f), Point(50f, -50f), points[i], points[i + 1]))
                    count++
        println(count)
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
        val gson = Gson()
        val polygonPoints = gson.fromJson(File("Test6.json").readText(), FileFormat::class.java)
        val scale = 1f
        for (i in 0..polygonPoints.x.lastIndex)
            polygon.addNode(
                Point(
                    scale * polygonPoints.x[i],
                    scale * polygonPoints.y[i]
                )
            )
        //centers = Geometric.findSimpleVoronoiDiagram(polygon)
        snapshot = Geometric.findSimpleVoronoiDiagram(polygon, polygon)
        centers += snapshot!!.center
    }
}