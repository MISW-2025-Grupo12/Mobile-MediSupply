package com.medisupplyg4.models

import org.junit.Test
import kotlin.test.assertEquals

class RoutePeriodTest {

    @Test
    fun `DAY enum should have correct name`() {
        assertEquals("DAY", RoutePeriod.DAY.name)
    }

    @Test
    fun `WEEK enum should have correct name`() {
        assertEquals("WEEK", RoutePeriod.WEEK.name)
    }

    @Test
    fun `MONTH enum should have correct name`() {
        assertEquals("MONTH", RoutePeriod.MONTH.name)
    }

    @Test
    fun `DAY enum should have correct ordinal`() {
        assertEquals(0, RoutePeriod.DAY.ordinal)
    }

    @Test
    fun `WEEK enum should have correct ordinal`() {
        assertEquals(1, RoutePeriod.WEEK.ordinal)
    }

    @Test
    fun `MONTH enum should have correct ordinal`() {
        assertEquals(2, RoutePeriod.MONTH.ordinal)
    }

    @Test
    fun `values should return all enum values`() {
        val values = RoutePeriod.values()
        assertEquals(3, values.size)
        assertEquals(RoutePeriod.DAY, values[0])
        assertEquals(RoutePeriod.WEEK, values[1])
        assertEquals(RoutePeriod.MONTH, values[2])
    }

    @Test
    fun `valueOf should return correct enum for DAY`() {
        val result = RoutePeriod.valueOf("DAY")
        assertEquals(RoutePeriod.DAY, result)
    }

    @Test
    fun `valueOf should return correct enum for WEEK`() {
        val result = RoutePeriod.valueOf("WEEK")
        assertEquals(RoutePeriod.WEEK, result)
    }

    @Test
    fun `valueOf should return correct enum for MONTH`() {
        val result = RoutePeriod.valueOf("MONTH")
        assertEquals(RoutePeriod.MONTH, result)
    }
}