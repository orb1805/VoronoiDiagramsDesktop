package com.example.demo.core.domain

import tornadofx.lineTo
import tornadofx.moveTo

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

fun <T> List<T>.preLast(): T {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    if (size == 1)
        throw java.util.NoSuchElementException("List's size is 1")
    return this[lastIndex - 1]
}

fun <T> List<T>.second(): T {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    if (size == 1)
        throw java.util.NoSuchElementException("List's size is 1")
    return this[1]
}