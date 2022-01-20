package com.example.demo.core.domain

import tornadofx.lineTo
import tornadofx.moveTo
import kotlin.math.*

fun javafx.scene.shape.Path.lineTo(point: Point): javafx.scene.shape.Path {
    return this.lineTo(point.x, -point.y)
}

fun javafx.scene.shape.Path.moveTo(point: Point): javafx.scene.shape.Path {
    return this.moveTo(point.x, -point.y)
}

fun javafx.scene.shape.Path.drawCross(point: Point): javafx.scene.shape.Path {
    this.moveTo(point.x + 2.5f, -point.y - 2.5f)
    this.lineTo(point.x - 2.5f, -point.y + 2.5f)
    this.moveTo(point.x - 2.5f, -point.y - 2.5f)
    return this.lineTo(point.x + 2.5f, -point.y + 2.5f)
}

fun javafx.scene.shape.Path.drawBigCross(point: Point): javafx.scene.shape.Path {
    this.moveTo(point.x + 5f, -point.y - 5f)
    this.lineTo(point.x - 5f, -point.y + 5f)
    this.moveTo(point.x - 5f, -point.y - 5f)
    return this.lineTo(point.x + 5f, -point.y + 5f)
}

fun javafx.scene.shape.Path.drawFlake(point: Point): javafx.scene.shape.Path {
    this.moveTo(point.x + 2.5f, -point.y - 2.5f)
    this.lineTo(point.x - 2.5f, -point.y + 2.5f)
    this.moveTo(point.x - 2.5f, -point.y - 2.5f)
    this.lineTo(point.x + 2.5f, -point.y + 2.5f)
    this.moveTo(point.x - 2.5f, -point.y)
    this.lineTo(point.x + 2.5f, -point.y)
    this.moveTo(point.x, -point.y - 2.5f)
    return this.lineTo(point.x, -point.y + 2.5f)
}

fun javafx.scene.shape.Path.drawBigFlake(point: Point): javafx.scene.shape.Path {
    this.moveTo(point.x + 5f, -point.y - 5f)
    this.lineTo(point.x - 5f, -point.y + 5f)
    this.moveTo(point.x - 5f, -point.y - 5f)
    this.lineTo(point.x + 5f, -point.y + 5f)
    this.moveTo(point.x - 5f, -point.y)
    this.lineTo(point.x + 5f, -point.y)
    this.moveTo(point.x, -point.y - 5f)
    return this.lineTo(point.x, -point.y + 5f)
}

fun javafx.scene.shape.Path.stretchedLine(point1: Point, point2: Point): javafx.scene.shape.Path {
    val step = (point2 - point1) / 6f
    var currentPoint = point1
    var count = 0
    while (count < 3) {
        moveTo(currentPoint)
        lineTo(currentPoint + step)
        currentPoint += step * 2f
        count++
    }
    moveTo(currentPoint.x, -currentPoint.y)
    return lineTo(point2)
}

fun javafx.scene.shape.Path.drawAxes(): javafx.scene.shape.Path {
    moveTo(0f, -100f)
    lineTo(0f, 0f)
    return lineTo(100f, 0f)
}

fun javafx.scene.shape.Path.drawParabola(parabola: Parabola): javafx.scene.shape.Path {
    var leftPoint1 = Point(parabola.startX, 10f)
    var leftPoint2 = Point(parabola.startX, -10f)
    var rightPoint1 = Point(parabola.endX, 10f)
    var rightPoint2 = Point(parabola.endX, -10f)
    val center = parabola.center
    leftPoint1 -= center
    leftPoint2 -= center
    rightPoint1 -= center
    rightPoint2 -= center
    leftPoint1.rotate(-atan(parabola.directrix.k ?: (PI.toFloat() / 2f)))
    leftPoint2.rotate(-atan(parabola.directrix.k ?: (PI.toFloat() / 2f)))
    rightPoint1.rotate(-atan(parabola.directrix.k ?: (PI.toFloat() / 2f)))
    rightPoint2.rotate(-atan(parabola.directrix.k ?: (PI.toFloat() / 2f)))
    val p = parabola.p
    val leftLine = Line.getLine(leftPoint1, leftPoint2)
    val rightLine = Line.getLine(rightPoint1, rightPoint2)
    val startX: Float?
    val endX: Float?
    var d: Float
    if (leftLine.k != null) {
        d = leftLine.k!!.pow(2) + 4 * leftLine.b / p
        startX = when {
            d == 0f ->
                p * leftLine.k!! / 2f
            d < 0f ->
                null
            else -> {
                val x1 = p * (leftLine.k!! + sqrt(d)) / 2f
                val x2 = p * (leftLine.k!! - sqrt(d)) / 2f
                if (parabola.p >= 0f) {
                    if (x1 < x2)
                        x1
                    else
                        x2
                } else {
                    if (x1 > x2)
                        x1
                    else
                        x2
                }
            }
        }
    } else
        startX = leftLine.b
    if (rightLine.k != null) {
        d = rightLine.k!!.pow(2) + 4 * rightLine.b / p
        endX = when {
            d == 0f ->
                p * rightLine.k!! / 2f
            d < 0f ->
                null
            else -> {
                val x1 = p * (rightLine.k!! + sqrt(d)) / 2f
                val x2 = p * (rightLine.k!! - sqrt(d)) / 2f
                if (parabola.p >= 0f) {
                    if (x1 < x2)
                        x1
                    else
                        x2
                } else {
                    if (x1 > x2)
                        x1
                    else
                        x2
                }
            }
        }
    } else
        endX = rightLine.b
    var newPoint = Point(startX!!, startX.pow(2) / p)
    var x = startX
    newPoint.rotate(atan(parabola.directrix.k ?: (PI.toFloat() / 2f)))
    newPoint += center
    //drawCross(center)
    moveTo(newPoint)
    while (x < endX!!) {
        x += 1f
        newPoint = Point(x, x.pow(2) / p)
        newPoint.rotate(atan(parabola.directrix.k ?: (PI.toFloat() / 2f)))
        newPoint += center
        lineTo(newPoint)
    }
    return lineTo(newPoint)
}