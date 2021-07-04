package com.example.demo.view

import com.example.demo.app.AppController
import tornadofx.*
import useCases.Type

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
            /*path {
                val points = appController.polygon.getPoints()
                if (points != null) {
                    moveTo(points[0].x, -points[0].y)
                    for (i in 1..points.lastIndex)
                        lineTo(points[i].x, -points[i].y)
                }
                closepath()
                val addition = appController.trapezoidAddition
                for (edge in addition) {
                    moveTo(edge[0].x, -edge[0].y)
                    lineTo(edge[1].x, -edge[1].y)
                    closepath()
                }
            }*/
        val trapezoids = appController.trapezoids
            for (trapezoid in trapezoids) {
                val points = trapezoid.getPoints()
                if (points != null) {
                    path {
                        moveTo(points[0].x, -points[0].y)
                        for (i in 1..points.lastIndex)
                            lineTo(points[i].x, -points[i].y)
                        closepath()
                    }
                }
            }
        }
    }
}