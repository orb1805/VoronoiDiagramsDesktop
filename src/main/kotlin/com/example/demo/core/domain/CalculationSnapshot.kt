package com.example.demo.core.domain

data class CalculationSnapshot (
    val polygon: Polygon,
    val centers: MutableList<Point?>,
    val center: Point,
    val tmpPolygon: Polygon,
    val mainPolygon: Polygon
    )