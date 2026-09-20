package com.yallapark.data.engine

import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.ParkingLot
import com.yallapark.domain.model.PredictiveForecast
import kotlin.math.exp
import kotlin.math.roundToInt

class PredictiveOccupancyEngine {

    /**
     * Calculates slot availability probability P(avail | ETA) using exponential decay
     * parameterized by zone turnover velocity and current occupancy baseline.
     */
    fun calculateForecast(
        lot: ParkingLot,
        etaMinutes: Int,
        hourOfDay: Int = 14 // default afternoon peak in UAE
    ): PredictiveForecast {
        val clampedEta = etaMinutes.coerceIn(5, 120)
        val currentOccupancyRatio = lot.occupancyRate
        
        // Zone-specific turnover rate (Karama & Bur Dubai have highest demand turnover)
        val zoneDecayFactor = when (lot.zone) {
            DubaiZone.KARAMA -> 0.035
            DubaiZone.BUR_DUBAI -> 0.032
            DubaiZone.DEIRA -> 0.028
            DubaiZone.DOWNTOWN -> 0.024
        }
        
        // Peak hour adjustment (Dubai commercial peak: 11AM-2PM and 6PM-9PM)
        val isPeakHour = (hourOfDay in 11..14) || (hourOfDay in 18..21)
        val peakMultiplier = if (isPeakHour) 1.35 else 0.85
        
        // Effective decay rate per 10 minutes
        val effectiveDecay = zoneDecayFactor * peakMultiplier
        
        // Projected open bays calculation
        val currentOpen = lot.availableBays.toDouble()
        // Exponential slot decay model: Open(t) = Open(0) * e^(-lambda * t)
        val projectedOpen = (currentOpen * exp(-effectiveDecay * 0.5 * clampedEta)).coerceAtLeast(0.0)
        
        // Probability calculation
        val probability = if (lot.totalCapacity > 0) {
            val ratio = projectedOpen / (lot.totalCapacity * 0.25).coerceAtLeast(1.0)
            (ratio * 100.0).coerceIn(5.0, 98.0).roundToInt()
        } else {
            10
        }
        
        val congestion = when {
            probability >= 70 -> "High Availability (Low Risk)"
            probability >= 40 -> "Moderate Turnover (Medium Risk)"
            else -> "High Demand (Pre-booking Recommended)"
        }
        
        val recommendation = when {
            probability >= 70 -> "Optimal slot availability expected at your arrival."
            probability >= 40 -> "Spaces are turning over quickly. Lock your space now to guarantee entry."
            else -> "Critical parking density in ${lot.zone.displayName}. Guaranteed pre-booking is strongly advised."
        }
        
        return PredictiveForecast(
            lotId = lot.id,
            targetEtaMinutes = clampedEta,
            estimatedOpenBays = projectedOpen.roundToInt().coerceAtLeast(if (probability > 10) 1 else 0),
            openBayProbabilityPercentage = probability,
            decayRatePer10Min = effectiveDecay,
            congestionLevel = congestion,
            recommendation = recommendation
        )
    }
}
