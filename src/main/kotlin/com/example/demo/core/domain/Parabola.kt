package com.example.demo.core.domain

import java.lang.Exception

data class Parabola(
    val focus: Point,
    val directrix: Line,
    val startX: Float = -20f,
    val endX: Float = 20f
) {

    init {
        if (startX > endX)
            throw Exception("startX must be smaller then endX")
    }
}