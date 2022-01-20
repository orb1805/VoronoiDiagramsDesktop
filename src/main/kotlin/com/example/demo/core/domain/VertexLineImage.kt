package com.example.demo.core.domain

import java.lang.Exception

interface VertexLineImage {

    val point: Point

    class Bisector(
        override val k: Float?,
        override val b: Float,
        val baseLine1: Line,
        val baseLine2: Line,
        override val point: Point
    ) : VertexLineImage, Line(k, b) {

        constructor(
            line: Line,
            baseLine1: Line,
            baseLine2: Line,
            point: Point
        ) : this(line.k, line.b, baseLine1, baseLine2, point)

    }

    class Perpendiculars(val line1: Line, val line2: Line, override val point: Point) : VertexLineImage {

        //var countOfUsed = 0
        private var _line1IsUsed = false
        private var _line2IsUsed = false

        val line1IsUsed: Boolean
            get() = _line1IsUsed
        val line2IsUsed: Boolean
            get() = _line2IsUsed

        fun line1Used() {
            if (_line1IsUsed)
                throw Exception("line1 can not be used twice")
            else
                _line1IsUsed = true
        }

        fun line2Used() {
            if (_line2IsUsed)
                throw Exception("line1 can not be used twice")
            else
                _line2IsUsed = true
        }
    }

    class ParabolaImage(
        override val focus: Point,
        override val directrix: Line,
        override var startX: Float = -20f,
        override var endX: Float = 20f,
        override val point: Point
    ) : Parabola(focus, directrix, startX, endX), VertexLineImage {

        constructor(parabola: Parabola, point: Point) : this(
            parabola.focus,
            parabola.directrix,
            parabola.startX,
            parabola.endX,
            point
        )
    }
}
