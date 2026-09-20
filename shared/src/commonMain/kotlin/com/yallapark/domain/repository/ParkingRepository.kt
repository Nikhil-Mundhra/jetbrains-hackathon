package com.yallapark.domain.repository

import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.ParkingLot
import kotlinx.coroutines.flow.Flow

interface ParkingRepository {
    fun getAllLots(): Flow<List<ParkingLot>>
    fun getLotsByZone(zone: DubaiZone): Flow<List<ParkingLot>>
    fun getLotById(id: String): Flow<ParkingLot?>
    fun searchLots(query: String): Flow<List<ParkingLot>>
}
