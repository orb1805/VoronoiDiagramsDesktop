package com.example.demo.core.domain

import domain.Point

data class Line(val k: Float?, val b: Float) {
    override fun toString(): String {
        return "k = $k, b = $b"
    }
}