package com.yallapark.domain.repository

import com.yallapark.domain.model.BayStatus
import com.yallapark.domain.model.BayType
import com.yallapark.domain.model.ParkingLot
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    suspend fun addParkingLot(lot: ParkingLot): Result<ParkingLot>
    suspend fun deleteParkingLot(lotId: String): Result<Boolean>
    suspend fun updateBayStatus(lotId: String, bayId: String, newStatus: BayStatus): Result<Boolean>
    suspend fun updateBayType(lotId: String, bayId: String, newType: BayType): Result<Boolean>
    suspend fun updateHourlyRate(lotId: String, newRateAed: Double): Result<Boolean>
    suspend fun simulateLiveSensorToggle(lotId: String, bayId: String): Result<BayStatus>
    fun observeLiveLotFeed(): Flow<List<ParkingLot>>
}
