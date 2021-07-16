package com.example.demo.view

import com.example.demo.app.AppController
import com.example.demo.core.useCases.Geometric
import tornadofx.*
import useCases.Type
import kotlin.math.abs

class MainView : View("Voronoi diagram") {
    private val appController: AppController by inject()
    override val root = vbox {
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
                                for (j in 1 .. 5) {
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
                        for (i in points.indices) {
                            moveTo(points[i].x, -points[i].y)
                            if (trapezoids[trapezoidInd].kOfBisectors[i] != null)
                                lineTo(points[i].x + 10f, -trapezoids[trapezoidInd].kOfBisectors[i]!! * (points[i].x + 10f) - trapezoids[trapezoidInd].bOfBisectors[i]!!)
                            else
                                lineTo(points[i].x, -points[i].y - 10f)
                            closepath()
                        }
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