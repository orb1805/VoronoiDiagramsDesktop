package com.example.demo.core.useCases

import com.example.demo.core.domain.Trapezoid
import com.example.demo.core.domain.Point
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GeometricTest {

    //region Points for equality of sections
    private val point11 = Point(1f, 2f)
    private val point12 = Point(2f, 2f)
    private val point21 = Point(1f, 2f)
    private val point22 = Point(2f, 2f)
    //endregion

    //region Trapezoid1
    private val pointForTrapezoid11 = Point(0f, 0f)
    private val pointForTrapezoid12 = Point(1f, 2f)
    private val pointForTrapezoid13 = Point(4f, 2f)
    private val pointForTrapezoid14 = Point(6f, 0f)

    private val trapezoid1 = Trapezoid(
        pointForTrapezoid11,
        pointForTrapezoid12,
        pointForTrapezoid13,
        pointForTrapezoid14,
        1
    )
    //endregion

    //region Trapezoid2
    private val pointForTrapezoid21 = Point(1f, 1f)
    private val pointForTrapezoid22 = Point(0f, 4f)
    private val pointForTrapezoid23 = Point(4f, 1f)
    private val pointForTrapezoid24 = Point(4f, 1f)

    private val trapezoid2 = Trapezoid(
        pointForTrapezoid21,
        pointForTrapezoid22,
        pointForTrapezoid23,
        pointForTrapezoid24,
        1
    )
    //endregion

    @Test
    fun testIsSectionsEqual1() {
        assertEquals(true, Geometric.checkHorizontalSections(point11, point12, point21, point22))
    }

    @Test
    fun testIsSectionsEqual2() {
        assertEquals(true, Geometric.checkHorizontalSections(point12, point11, point22, point21))
    }

    @Test
    fun testIsSectionsEqual3() {
        assertEquals(true, Geometric.checkHorizontalSections(point11, point22, point12, point21))
    }

    @Test
    fun testIsSectionsEqual4() {
        assertEquals(false, Geometric.checkHorizontalSections(point11, point21, point12, point22))
    }

    @Test
    fun testUpperEdge1() {
        assertEquals(listOf(pointForTrapezoid12, pointForTrapezoid13), trapezoid1.getUpperEdge())
    }

    @Test
    fun testLowerEdge1() {
        assertEquals(listOf(pointForTrapezoid11, pointForTrapezoid14), trapezoid1.getLowerEdge())
    }

    @Test
    fun testUpperEdge2() {
        assertEquals(listOf(pointForTrapezoid22, pointForTrapezoid22), trapezoid2.getUpperEdge())
    }

    @Test
    fun testLowerEdge2() {
        assertEquals(listOf(pointForTrapezoid21, pointForTrapezoid23), trapezoid2.getLowerEdge())
    }

}