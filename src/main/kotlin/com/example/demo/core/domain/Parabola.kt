package com.example.demo.core.domain

import com.example.demo.core.useCases.Geometric
import java.lang.Exception
import kotlin.math.*

open class Parabola(
    open val focus: Point,
    open val directrix: Line,
    open var startX: Float = -20f,
    open var endX: Float = 20f
) {

    val p: Float
    val center: Point = Geometric.centerOfPerpendicular(directrix, focus)!!

    private val alpha: Float
    private val a: Float
    private val b: Float
    private val c: Float
    private val d: Float
    private val e: Float
    private val f: Float

    init {
        p = when {
            focus.y > center.y ->
                Geometric.lengthFromPointToLine(directrix, focus) / 2f
            focus.y < center.y ->
                -Geometric.lengthFromPointToLine(directrix, focus) / 2f
            else ->
                if (focus.x > center.x)
                    Geometric.lengthFromPointToLine(directrix, focus) / 2f
                else
                    -Geometric.lengthFromPointToLine(directrix, focus) / 2f
        }
        alpha = if (directrix.k != null)
            atan(directrix.k!!)
        else
            PI.toFloat() / 2f
        a = -cos(alpha).pow(2) / p
        b = -sin(alpha).pow(2) / p
        c = 2f * cos(alpha) * sin(alpha) / p
        d = sin(alpha) - 2f * center.y * sin(alpha) * cos(alpha) / p + 2f * center.x * cos(alpha).pow(2) / p
        e = cos(alpha) + 2f * center.y * sin(alpha).pow(2) / p - 2f * center.x * sin(alpha) * cos(alpha) / p
        f = -center.x * sin(alpha) - center.y * cos(alpha) - center.y.pow(2) * sin(alpha).pow(2) / p -
                center.x.pow(2) * cos(alpha).pow(2) / p + 2f * center.x * center.y * sin(alpha) * cos(alpha) / p
    }

    fun intersection(parabola: Parabola, approximationPoint: Point): Point? {
        var x = approximationPoint.x
        var y = approximationPoint.y
        var tmpX = x + 1f
        var tmpY = y + 1f
        while (sqrt((x - tmpX).pow(2) + (y - tmpY).pow(2)) > 0.001f) {
            tmpX = x
            tmpY = y
            val j11 = derX(tmpX, tmpY)
            val j12 = derY(tmpX, tmpY)
            val j21 = parabola.derX(tmpX, tmpY)
            val j22 = parabola.derY(tmpX, tmpY)
            val det = j11 * j22 - j12 * j21
            x = tmpX - (f(tmpX, tmpY) * j22 - parabola.f(tmpX, tmpY) * j12) / det
            y = tmpY - (f(tmpX, tmpY) * j21 - parabola.f(tmpX, tmpY) * j11) / det
        }
        return Point(x, y)
    }

    // TODO возможно придется брать точку, ближайшую к центру
    fun intersection(line: Line): List<Point> {
        val result = mutableListOf<Point>()
        var point1: Point
        var point2: Point
        if (line.k != null) {
            point1 = Point(0f, line.b)
            point2 = Point(10f, line.k!! * 10f + line.b)
        } else {
            point1 = Point(line.b, 0f)
            point2 = Point(line.b, 10f)
        }
        val center = Geometric.centerOfPerpendicular(directrix, focus)!!
        point1 -= center
        point2 -= center
        point1.rotate(-atan(directrix.k ?: (PI.toFloat() / 2f)))
        point2.rotate(-atan(directrix.k ?: (PI.toFloat() / 2f)))
        val newLine = Line.getLine(point1, point2)
        if (newLine.k != null) {
            val d = newLine.k!!.pow(2) + 4 * newLine.b / p
            when {
                d == 0f -> {
                    val x = p * newLine.k!! / 2f
                    result += Point(x, x.pow(2) / p)
                }
                d > 0f -> {
                    val x1 = p * (newLine.k!! + sqrt(d)) / 2f
                    val x2 = p * (newLine.k!! - sqrt(d)) / 2f
                    result += Point(x1, x1.pow(2) / p)
                    result += Point(x2, x2.pow(2) / p)
                }
            }
        } else
            result += Point(newLine.b, newLine.b.pow(2) / p)
        result.map { point ->
            point.rotate(atan(directrix.k ?: (PI.toFloat() / 2f)))
            point2.x += center.x
            point.y += center.y
        }
        return result
    }

    fun f(x: Float, y: Float): Float =
        a * x.pow(2) + b * y.pow(2) + c * x * y + d * x + e * y + f

    fun derX(x: Float, y: Float): Float =
        2f * a * x + b * y.pow(2) + c * y + d

    fun derY(x: Float, y: Float): Float =
        a * x.pow(2) + 2f * b * y + c * x + e

    companion object {

        const val MAX_RETRIES = 50
    }
}