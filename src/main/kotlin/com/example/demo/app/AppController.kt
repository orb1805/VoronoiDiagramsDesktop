package com.example.demo.app

import com.example.demo.core.domain.*
import com.example.demo.core.useCases.*
import com.example.demo.view.MainView
import com.google.gson.Gson
import rx.Observable
import tornadofx.*
import java.io.File
import kotlin.math.PI

class AppController : Controller() {

    private val mainView: MainView by inject()

    val polygon = Polygon()
    lateinit var trapezoids: MutableList<Trapezoid>
    val medialAxes = mutableListOf<MutableList<Point>>()
    var centers = mutableListOf<Point>()
    var snapshot: CalculationSnapshot? = null
    var images: List<VertexLineImage> = listOf()
    var result: CircledList<MedialAxesPart>? = null
    /*val resultObservable: Observable<CircledList<MedialAxesPart>> = Observable.create { emmiter ->

    }*/

    val parabola = Parabola(Point(80f, 140f), Line (PI.toFloat() / 6f, -20f), 30f, 75f)

    fun testPerpendiculars() {
        val polygonPoints = Gson().fromJson(File("Test8.json").readText(), FileFormat::class.java)
        val scale = 1f
        for (i in 0..polygonPoints.x.lastIndex)
            polygon += Point(polygonPoints.x[i], polygonPoints.y[i]) * scale
        val polygonLines = circledListOf<Line>()
        val points = polygon.points ?: return
        for (i in points.indices)
            polygonLines += Line.getLine(points[i], points[i + 1])
        images = Geometric.findImages(polygonLines, points)!!
    }

    fun nextStep() {
        mainView.root += mainView.button
        snapshot = Geometric.findSimpleVoronoiDiagram(
            snapshot!!.tmpPolygon,
            polygon
        )
        if (snapshot != null)
            centers += snapshot!!.center
    }

    /*fun fill() {
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
    }*/

    fun testDiagram() {
        val polygonPoints = Gson().fromJson(File("Test1.json").readText(), FileFormat::class.java)
        val scale = 1f
        for (i in 0..polygonPoints.x.lastIndex)
            polygon += Point(polygonPoints.x[i], polygonPoints.y[i]) * scale
        snapshot = Geometric.findSimpleVoronoiDiagram(polygon, polygon)
        centers += snapshot!!.center
    }

    fun testFullDiagram() {
        val polygonPoints = Gson().fromJson(File("Test9.json").readText(), FileFormat::class.java)
        val scale = 1f
        for (i in 0..polygonPoints.x.lastIndex)
            polygon += Point(polygonPoints.x[i], polygonPoints.y[i]) * scale
        result = Geometric.findVoronoiDiagram(polygon)
    }
}