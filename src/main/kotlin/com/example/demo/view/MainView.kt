package com.example.demo.view

import com.example.demo.app.AppController
import com.example.demo.core.domain.Line
import com.example.demo.core.domain.Section
import com.example.demo.core.domain.Point
import com.example.demo.core.domain.lineToPoint
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.*
import useCases.Type
import kotlin.math.abs

class MainView : View("Voronoi diagram") {

    private val appController: AppController by inject()
    private val views = mutableListOf<HBox>()
    override var root = hbox { }
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
            } else {
                root += drawResult(appController) ?: return@action
            }
        }
    }

    init {
        root += button
    }

    fun drawPreResult(appController: AppController): HBox {
        return hbox {
            val points = appController.polygon.getPoints() ?: return@hbox
            val centers = appController.centers.subList(0, appController.centers.size - 2)
            path {
                moveTo(points[0].x, -points[0].y)
                for (i in 1..points.lastIndex)
                    lineToPoint(this, points[i])
                closepath()
                for (center in centers) {
                    center ?: continue
                    moveTo(center.x + 2.5f, -center.y - 2.5f)
                    lineTo(center.x - 2.5f, -center.y + 2.5f)
                    moveTo(center.x - 2.5f, -center.y - 2.5f)
                    lineTo(center.x + 2.5f, -center.y + 2.5f)
                    moveTo(center.x - 2.5f, -center.y)
                    lineTo(center.x + 2.5f, -center.y)
                    moveTo(center.x, -center.y - 2.5f)
                    lineTo(center.x, -center.y + 2.5f)
                    /*moveTo(center.x, -center.y)
                    lineTo(center.parentX, -center.parentY)*/
                }
            }
        }
    }

    private fun drawSnapshot(appController: AppController): HBox {
        return hbox {
            val points = appController.snapshot!!.polygon.getPoints() ?: return@hbox
            val mainPoints = appController.snapshot!!.mainPolygon.getPoints() ?: return@hbox
            val centers = appController.snapshot!!.centers
            val center = appController.snapshot!!.center
            path {
                moveTo(points[0].x, -points[0].y)
                for (i in 1..points.lastIndex)
                    lineToPoint(this, points[i])
                closepath()
                for (center in centers) {
                    center ?: continue
                    moveTo(center.x + 2.5f, -center.y - 2.5f)
                    lineTo(center.x - 2.5f, -center.y + 2.5f)
                    moveTo(center.x - 2.5f, -center.y - 2.5f)
                    lineTo(center.x + 2.5f, -center.y + 2.5f)
                    moveTo(center.x - 2.5f, -center.y)
                    lineTo(center.x + 2.5f, -center.y)
                    moveTo(center.x, -center.y - 2.5f)
                    lineTo(center.x, -center.y + 2.5f)
                }
                moveTo(center.x + 5f, -center.y - 5f)
                lineTo(center.x - 5f, -center.y + 5f)
                moveTo(center.x - 5f, -center.y - 5f)
                lineTo(center.x + 5f, -center.y + 5f)
                moveTo(center.x - 5f, -center.y)
                lineTo(center.x + 5f, -center.y)
                moveTo(center.x, -center.y - 2.5f)
                lineTo(center.x, -center.y + 2.5f)
                for (i in 1..points.lastIndex) {
                    centers[i] ?: continue
                    moveTo(points[i].x, -points[i].y)
                    lineTo(centers[i]!!.x, -centers[i]!!.y)
                    lineTo(points[i - 1].x, -points[i - 1].y)
                }
                if (centers[0] != null) {
                    moveTo(points[0].x, -points[0].y)
                    lineTo(centers[0]!!.x, -centers[0]!!.y)
                    lineTo(points.last().x, -points.last().y)
                }

               var step: Point
               var currentPoint: Point
               var count: Int
                for (i in 0 until mainPoints.lastIndex) {
                    step = (mainPoints[i + 1] - mainPoints[i]) / 6f
                    currentPoint = mainPoints[i]
                    count = 0
                    while (count < 3) {
                        moveTo(currentPoint.x, -currentPoint.y)
                        lineToPoint(this, currentPoint + step)
                        currentPoint += step * 2f
                        count++
                    }
                }
                step = (mainPoints.first() - mainPoints.last()) / 6f
                currentPoint = mainPoints.last()
                count = 0
                while (count < 3) {
                    moveTo(currentPoint.x, -currentPoint.y)
                    lineToPoint(this, currentPoint + step)
                    currentPoint += step * 2f
                    count++
                }
            }
        }
    }

    fun drawResult(appController: AppController) =
        if (flag)
            hbox {
                flag = false
                val points = appController.polygon.getPoints() ?: return@hbox
                path {
                    moveTo(points.first().x, -points.first().y)
                    for (i in 1..points.lastIndex)
                        lineTo(points[i].x, -points[i].y)
                    closepath()
                    for (center in appController.centers) {
                        moveTo(center.x + 2.5f, -center.y - 2.5f)
                        lineTo(center.x - 2.5f, -center.y + 2.5f)
                        moveTo(center.x - 2.5f, -center.y - 2.5f)
                        lineTo(center.x + 2.5f, -center.y + 2.5f)
                        moveTo(center.x - 2.5f, -center.y)
                        moveTo(center.x + 2.5f, -center.y)
                        moveTo(center.x, -center.y)
                        lineTo(center.parent1X, -center.parent1Y)
                        moveTo(center.x, -center.y)
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
                    val points = appController.polygon.getPoints()
                    if (points != null) {
                        moveTo(points[0].x, -points[0].y)
                        for (i in 1..points.lastIndex)
                            lineTo(points[i].x, -points[i].y)
                    }
                    closepath()
                }
                path {
                    val points = appController.polygon.getPoints()
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
                    val points = trapezoids[trapezoidInd].getPoints()
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
            val points = appController.polygon.getPoints()
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
                    var i = 0
                    /*while (i < centers.size) {
                        moveTo(centers[i + 1].x, -centers[i + 1].y)
                        lineTo(centers[i].x, -centers[i].y)
                        lineTo(centers[i + 2].x, -centers[i + 2].y)
                        i += 3
                    }*/
                    //closepath()
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
                    (point.x + 10f) * line.k + line.b
                else
                    point.y + 10f
            )
        )
}