package com.yallapark.ui.screens

import androidx.compose.material3.testing.assertIsDisplayed
import androidx.compose.ui.test.junitUiTest
import com.yallapark.ui.theme.*
import kotlin.test.Test

class LandingHomeScreenTest {

    @Test
    fun testLandingHomeScreenDisplaysTitle() = junitUiTest {
        onNode(withText("Smart Mobility & Predictive Parking")).assertIsDisplayed()
    }

    @Test
    fun testLandingHomeScreenDisplaysDubaiZones() = junitUiTest {
        onNode(withText("Bur Dubai")).assertIsDisplayed()
        onNode(withText("Al Karama")).assertIsDisplayed()
        onNode(withText("Deira")).assertIsDisplayed()
        onNode(withText("Downtown Dubai")).assertIsDisplayed()
    }

    @Test
    fun testLandingHomeScreenBayFilters() = junitUiTest {
        onNode(withText("POD Accessible")).assertIsDisplayed()
        onNode(withText("Women-Only Pink")).assertIsDisplayed()
        onNode(withText("Delivery Rider")).assertIsDisplayed()
        onNode(withText("EV Charging")).assertIsDisplayed()
    }
}