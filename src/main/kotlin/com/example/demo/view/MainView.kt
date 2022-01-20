package com.example.demo.view

import com.example.demo.app.AppController
import com.example.demo.core.domain.*
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.*
import tornadofx.lineTo
import useCases.Type
import kotlin.math.abs
import com.example.demo.core.domain.VertexLineImage.Perpendiculars as Perpendiculars

class MainView : View("Voronoi diagram") {

    private val appController: AppController by inject()
    private val views = mutableListOf<HBox>()
    override var root = testFullDiagram(appController)
    private var flag = true
    val button = Button("NEXT").apply {
        action {
            if (appController.snapshot != null) {
                if (views.size > 0)
                    views.removeAt(0)
                views += drawSnapshot(appController)
                root.clear()
                for (view in views)
                    root += view
                appController.nextStep()
                root += drawPreResult(appController)
            } else
                root += drawResult(appController) ?: return@action
        }
    }

    /*init {
        root += button
    }*/

    fun testFullDiagram(appController: AppController) : HBox =
        hbox {
            path {
                val result = appController.result ?: return@path
                val points = appController.polygon.points ?: return@path
                moveTo(points.first())
                for (i in 1 .. points.lastIndex)
                    lineTo(points[i])
                lineTo(points.first())
                result.forEach {
                    if (it is Point)
                        drawCross(it)
                    else
                        drawParabola(it as Parabola)
                }
            }
        }

    fun testPerpendiculars(appController: AppController): HBox =
        hbox {
            path {
                val points = appController.polygon.points ?: return@path
                moveTo(points.first())
                for (i in 1 .. points.lastIndex)
                    lineTo(points[i])
                lineTo(points.first())
                val images = appController.images
                for (i in points.indices) {
                    if (images[i] is Line) {
                        moveTo(points[i])
                        if ((images[i] as Line).k != null)
                            lineTo(
                                points[i].x + 30f,
                                -((images[i] as Line).k!! * (points[i].x + 30f) + (images[i] as Line).b)
                            )
                        else
                            lineTo(points[i].x, -points[i].y - 30f)
                    } else {
                        val perpendicular1 = (images[i] as Perpendiculars).line1
                        val perpendicular2 = (images[i] as Perpendiculars).line2
                        if (perpendicular1.k != null)
                            moveTo(points[i].x + 30f, -(perpendicular1.k!! * (points[i].x + 30f) + perpendicular1.b))
                        else
                            moveTo(points[i].x, -points[i].y - 30f)
                        lineTo(points[i])
                        if (perpendicular2.k != null)
                            lineTo(points[i].x + 30f, -(perpendicular2.k!! * (points[i].x + 30f) + perpendicular2.b))
                        else
                            lineTo(points[i].x, -points[i].y - 30f)
                    }
                }
            }
        }

    fun testParabola(appController: AppController): HBox =
        hbox {
            path {
                drawAxes()
                drawParabola(appController.parabola)
                val k = -1f
                val b = 160f
                moveTo(-100f, -(k * (-100f) + b))
                lineTo(100f, -(k * 100f + b))
                val intersections = appController.parabola.intersection(Line(k, b))
                intersections.forEach {
                    drawFlake(it)
                }
            }
        }

    private fun drawPreResult(appController: AppController): HBox =
        hbox {
            val points = appController.polygon.points ?: return@hbox
            val centers = appController.centers.subList(0, appController.centers.size - 2)
            path {
                moveTo(points[0].x, -points[0].y)
                for (i in 1..points.lastIndex)
                    lineTo(points[i])
                closepath()
                for (center in centers)
                    drawCross(center)
            }
        }

    private fun drawSnapshot(appController: AppController): HBox =
        hbox {
            val points = appController.snapshot!!.polygon.points ?: return@hbox
            var averagePoint = Point(0f, 0f)
            for (point in points)
                averagePoint += point
            averagePoint /= points.size
            val mainPoints = appController.snapshot!!.mainPolygon.points ?: return@hbox
            val centers = appController.snapshot!!.centers
            val center = appController.snapshot!!.center
            path {
                moveTo(points[0])
                for (i in 1..points.lastIndex)
                    lineTo(points[i])
                closepath()
                for (center in centers)
                    if (center != null)
                        drawCross(center)
                    else
                        continue
                drawBigFlake(center)
                for (i in 0 until points.lastIndex) {
                    centers[i] ?: continue
                    moveTo(points[i])
                    lineTo(centers[i]!!)
                    lineTo(points[i + 1])
                }
                if (centers.last() != null) {
                    moveTo(points[0])
                    lineTo(centers.last()!!)
                    lineTo(points.last())
                }

                for (i in 0 until mainPoints.lastIndex)
                    stretchedLine(mainPoints[i + 1], mainPoints[i])
                stretchedLine(mainPoints.first(), mainPoints.last())
            }
        }

    private fun drawResult(appController: AppController) =
        if (flag)
            hbox {
                flag = false
                val points = appController.polygon.points ?: return@hbox
                path {
                    moveTo(points.first())
                    for (i in 1..points.lastIndex)
                        lineTo(points[i])
                    closepath()
                    for (center in appController.centers) {
                        drawCross(center)
                        moveTo(center)
                        lineTo(center.parent1X, -center.parent1Y)
                        moveTo(center)
                        lineTo(center.parent2X, -center.parent2Y)
                    }
                }
            }
        else
            null

    fun drawPartialMedialAxes(appController: AppController): VBox {
        return vbox {
            hbox {
                path {
                    val points = appController.polygon.points
                    if (points != null) {
                        moveTo(points[0].x, -points[0].y)
                        for (i in 1..points.lastIndex)
                            lineTo(points[i].x, -points[i].y)
                    }
                    closepath()
                }
                path {
                    val points = appController.polygon.points
                    if (points != null) {
                        moveTo(points[0].x, -points[0].y)
                        for (i in 1..points.lastIndex)
                            lineTo(points[i].x, -points[i].y)
                    }
                    closepath()
                    val types = appController.polygon.getTypes()
                    if (types != null && points != null) {
                        for (i in 0..types.lastIndex) {
                            when (types[i]) {
                                Type.LEFT -> {
                                    moveTo(points[i].x, -points[i].y)
                                    lineTo(points[i].x - 20f, -points[i].y)
                                }
                                Type.RIGHT -> {
                                    moveTo(points[i].x, -points[i].y)
                                    lineTo(points[i].x + 20f, -points[i].y)
                                }
                                Type.WIDE -> {
                                    moveTo(points[i].x - 20f, -points[i].y)
                                    lineTo(points[i].x + 20f, -points[i].y)
                                }
                            }
                            closepath()
                        }
                    }
                }
            }
            hbox {
                val trapezoids = appController.trapezoids
                val medialAxes = appController.medialAxes
                for (trapezoidInd in trapezoids.indices) {
                    val points = trapezoids[trapezoidInd].points
                    val isReal = trapezoids[trapezoidInd].isReal
                    if (points != null) {
                        path {
                            moveTo(points[0].x, -points[0].y)
                            for (i in 1..points.lastIndex)
                                if (isReal[i - 1])
                                    lineTo(points[i].x, -points[i].y)
                                else {
                                    var stepX = abs(points[i].x - points[i - 1].x) / 10f
                                    var stepY = abs(points[i].y - points[i - 1].y) / 10f
                                    if (points[i].x < points[i - 1].x)
                                        stepX *= -1f
                                    if (points[i].y < points[i - 1].y)
                                        stepY *= -1f
                                    var tmpX = points[i - 1].x
                                    var tmpY = points[i - 1].y
                                    for (j in 1..5) {
                                        tmpX += stepX
                                        tmpY += stepY
                                        lineTo(tmpX, -tmpY)
                                        tmpX += stepX
                                        tmpY += stepY
                                        moveTo(tmpX, -tmpY)
                                    }
                                }
                            if (isReal.last())
                                lineTo(points[0].x, -points[0].y)
                            else {
                                var stepX = abs(points[0].x - points.last().x) / 10f
                                var stepY = abs(points[0].y - points.last().y) / 10f
                                if (points[0].x < points.last().x)
                                    stepX *= -1f
                                if (points[0].y < points.last().y)
                                    stepY *= -1f
                                var tmpX = points.last().x
                                var tmpY = points.last().y
                                for (j in 1..5) {
                                    tmpX += stepX
                                    tmpY += stepY
                                    lineTo(tmpX, -tmpY)
                                    tmpX += stepX
                                    tmpY += stepY
                                    moveTo(tmpX, -tmpY)
                                }
                            }
                            closepath()
                            for (axes in medialAxes[trapezoidInd]) {
                                moveTo(axes.x + 2.5f, -axes.y - 2.5f)
                                lineTo(axes.x - 2.5f, -axes.y + 2.5f)
                                moveTo(axes.x - 2.5f, -axes.y - 2.5f)
                                lineTo(axes.x + 2.5f, -axes.y + 2.5f)
                                closepath()
                            }
                            if (medialAxes[trapezoidInd].size == 2) {
                                moveTo(medialAxes[trapezoidInd][0].x, -medialAxes[trapezoidInd][0].y)
                                lineTo(medialAxes[trapezoidInd][1].x, -medialAxes[trapezoidInd][1].y)
                                closepath()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun drawSimpleDiagram(appController: AppController): HBox {
        return hbox {
            val points = appController.polygon.points
            val centers = appController.centers
            if (points != null) {
                path {
                    moveTo(points[0].x, -points[0].y)
                    for (i in 1..points.lastIndex)
                        lineTo(points[i].x, -points[i].y)
                    lineTo(points[0].x, -points[0].y)
                    for (center in centers) {
                        moveTo(center.x + 2.5f, -center.y - 2.5f)
                        lineTo(center.x - 2.5f, -center.y + 2.5f)
                        moveTo(center.x - 2.5f, -center.y - 2.5f)
                        lineTo(center.x + 2.5f, -center.y + 2.5f)
                    }
                }
            }
        }
    }

    fun lineIntoSection(line: Line, point: Point) =
        Section(
            point,
            Point(
                point.x + 10f,
                if (line.k != null)
                    (point.x + 10f) * line.k!! + line.b
                else
                    point.y + 10f
            )
        )
}