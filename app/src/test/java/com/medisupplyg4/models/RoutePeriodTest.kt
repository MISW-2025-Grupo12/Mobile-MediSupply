package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class RoutePeriodTest {

    @Test
    fun `RoutePeriod enum should have all expected values`() {
        // When
        val periods = RoutePeriod.values()

        // Then
        assertEquals(3, periods.size)
        assertTrue(periods.contains(RoutePeriod.DAY))
        assertTrue(periods.contains(RoutePeriod.WEEK))
        assertTrue(periods.contains(RoutePeriod.MONTH))
    }

    @Test
    fun `RoutePeriod values should be unique`() {
        // When
        val periods = RoutePeriod.values()
        val uniquePeriods = periods.distinct()

        // Then
        assertEquals(periods.size, uniquePeriods.size)
    }
}

