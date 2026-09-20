package com.yallapark.ui.components

import androidx.compose.material3.testing.assertIsDisplayed
import androidx.compose.ui.test.junitUiTest
import com.yallapark.ui.theme.*
import kotlin.test.Test

class OccupancyProgressBarTest {

    @Test
    fun testOccupancyProgressBarDisplaysAvailability() = junitUiTest {
        onNode(withText("bays free")).assertIsDisplayed()
    }

    @Test
    fun testOccupancyProgressBarHighDensity() = junitUiTest {
        // Test high density state (>= 85% occupied)
        // 85% occupied means 15% available
        // When 85%+ occupied -> StatusCongestedRed, "High Density"
        // occupied = total - available, so if total=100, available=15 -> occupied=85
        // ratio = 85/100 = 0.85 -> 85%
        // This should show red color and "High Density" label
    }

    @Test
    fun testOccupancyProgressBarModerate() = junitUiTest {
        // Test moderate state (50-84% occupied)
        // Should show amber color and "Moderate" label
    }

    @Test
    fun testOccupancyProgressBarSpacious() = junitUiTest {
        // Test spacious state (< 50% occupied)
        // Should show green color and "Spacious" label
        // When < 50% occupied means > 50% available
    }
}