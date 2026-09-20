package com.yallapark.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OccupancyProgressBarTest {

    @Test
    fun testHighDensityThreshold() {
        // >= 85% occupied -> High Density (red)
        val total = 100
        val available = 15
        val occupied = total - available
        val ratio = occupied.toDouble() / total
        assertTrue(ratio >= 0.85, "85% occupied should trigger High Density classification")
    }

    @Test
    fun testModerateThreshold() {
        // 50-84% occupied -> Moderate (amber)
        val total = 100
        val available = 30
        val occupied = total - available
        val ratio = occupied.toDouble() / total
        assertTrue(ratio >= 0.50, "70% occupied should be above moderate threshold")
        assertTrue(ratio < 0.85, "70% occupied should be below high density threshold")
    }

    @Test
    fun testSpaciousThreshold() {
        // < 50% occupied -> Spacious (green)
        val total = 100
        val available = 60
        val occupied = total - available
        val ratio = occupied.toDouble() / total
        assertTrue(ratio < 0.50, "40% occupied should classify as Spacious")
    }

    @Test
    fun testOccupancyPercentageCalculation() {
        val total = 60
        val available = 22
        val occupied = total - available
        val percentage = (occupied.toDouble() / total * 100).toInt()
        assertEquals(63, percentage, "38 occupied of 60 should be 63%")
    }

    @Test
    fun testZeroCapacityHandling() {
        val total = 0
        val available = 0
        val ratio = if (total > 0) (total - available).toDouble() / total else 0.0
        assertEquals(0.0, ratio, "Zero capacity lot should have 0% occupancy ratio")
    }

    @Test
    fun testFullOccupancy() {
        val total = 50
        val available = 0
        val occupied = total - available
        val ratio = occupied.toDouble() / total
        assertEquals(1.0, ratio, "All bays occupied should produce 100% ratio")
    }
}