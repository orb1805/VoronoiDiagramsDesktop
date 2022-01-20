package com.example.demo.core.domain

interface MedialAxesPart {

    class ParabolaPart(
        override val focus: Point,
        override val directrix: Line,
        override var startX: Float = -20f,
        override var endX: Float = 20f
    ) : Parabola(focus, directrix, startX, endX), MedialAxesPart {

        constructor(parabola: Parabola): this(
            parabola.focus,
            parabola.directrix,
            parabola.startX,
            parabola.endX
        )

    }

    class PointPart(point: Point) : Point(point.x, point.y), MedialAxesPart
}