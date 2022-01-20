package com.example.demo.core.domain

fun main() {
    val parabola1 = Parabola(Point(0f, 1f), Line(0f, -1f))
    val parabola2 = Parabola(Point(3f, 1f), Line(0f, -1f))
    solve(parabola1, parabola2)
}

fun solve(parabola1: Parabola, parabola2: Parabola) {
    println(parabola1.intersection(parabola2, Point(0f, 0f)))
}