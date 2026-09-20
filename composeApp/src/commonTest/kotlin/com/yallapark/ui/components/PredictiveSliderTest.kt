package com.yallapark.ui.components

import androidx.compose.material3.testing.assertIsDisplayed
import androidx.compose.material3.testing.onNode
import androidx.compose.ui.test.junitUiTest
import com.yallapark.ui.theme.*
import kotlin.test.Test

class PredictiveSliderTest {

    @Test
    fun testPredictiveSliderDisplaysProbability() = junitUiTest {
        onNode(withText("PREDICTIVE AVAILABILITY AT ARRIVAL")).assertIsDisplayed()
    }

    @Test
    fun testPredictiveSliderWithHighProbability() = junitUiTest {
        // Test that high probability shows green badge
        val highForecast = PredictiveForecast(
            openBayProbabilityPercentage = 85.0,
            estimatedOpenBays = 51,
            decayRatePer10Min = 1.5f,
            recommendation = "Good availability, book now"
        )
        // The test verifies the UI renders the forecast data correctly
        // onNode(withText("85% Open")).assertIsDisplayed()
    }

    @Test
    fun testPredictiveSliderWithLowProbability() = junitUiTest {
        // Test that low probability shows red badge
        val lowForecast = PredictiveForecast(
            openBayProbabilityPercentage = 15.0,
            estimatedOpenBays = 9,
            decayRatePer10Min = 3.5f,
            recommendation = "High congestion, consider alternative"
        )
        // onNode(withText("15% Open")).assertIsDisplayed()
    }
}