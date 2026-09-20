package com.yallapark.data.repository

import com.yallapark.domain.model.*
import com.yallapark.domain.repository.AdminRepository
import com.yallapark.domain.repository.ParkingRepository
import com.yallapark.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class YallaParkRepositoryImpl : ParkingRepository, ReservationRepository, AdminRepository {

    private val _lots = MutableStateFlow<List<ParkingLot>>(createInitialDubaiLots())
    val lotsFlow: Flow<List<ParkingLot>> = _lots.asStateFlow()

    private val _activeReservation = MutableStateFlow<Reservation?>(null)
    val activeReservationFlow: Flow<Reservation?> = _activeReservation.asStateFlow()

    // MongoDB Atlas sync connection metadata
    var mongoDbUri: String = "mongodb+srv://psb8013_db_user:<db_password>@jetbrains.tzw6r2y.mongodb.net/?appName=Jetbrains"

    // --- ParkingRepository Implementations ---

    override fun getAllLots(): Flow<List<ParkingLot>> = lotsFlow

    override fun getLotsByZone(zone: DubaiZone): Flow<List<ParkingLot>> {
        return _lots.map { list -> list.filter { it.zone == zone } }
    }

    override fun getLotById(id: String): Flow<ParkingLot?> {
        return _lots.map { list -> list.find { it.id == id } }
    }

    override fun searchLots(query: String): Flow<List<ParkingLot>> {
        return _lots.map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.zone.displayName.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true)
            }
        }
    }

    // --- ReservationRepository Implementations ---

    override fun getActiveReservation(): Flow<Reservation?> = activeReservationFlow

    override suspend fun createReservation(
        lotId: String,
        bayId: String,
        vehiclePlate: String,
        durationMinutes: Int,
        paymentMethod: PaymentMethod
    ): Result<Reservation> {
        val currentList = _lots.value
        val lot = currentList.find { it.id == lotId }
            ?: return Result.failure(IllegalArgumentException("Parking lot not found"))

        val bay = lot.bays.find { it.id == bayId }
            ?: return Result.failure(IllegalArgumentException("Selected bay not found"))

        if (bay.status != BayStatus.AVAILABLE) {
            return Result.failure(IllegalStateException("Selected bay is currently ${bay.status}"))
        }

        val now = currentTimeMillis()
        val durationMs = durationMinutes.toLong() * 60_000L
        val lockHoldMs = 900_000L // 15-minute guaranteed arrival lock
        val totalCost = (durationMinutes / 60.0) * lot.hourlyRateAed

        val reservation = Reservation(
            id = "RES_${now.toString().takeLast(6)}",
            lotId = lot.id,
            lotName = lot.name,
            bayId = bay.id,
            bayNumber = bay.bayNumber,
            bayType = bay.type,
            vehiclePlateNumber = vehiclePlate,
            startTimeEpochMs = now,
            durationMinutes = durationMinutes,
            endTimeEpochMs = now + durationMs,
            lockExpiryEpochMs = now + lockHoldMs,
            hourlyRateAed = lot.hourlyRateAed,
            totalCostAed = (totalCost * 100).toInt() / 100.0,
            paymentMethod = paymentMethod,
            status = ReservationStatus.HOLD_LOCKED
        )

        // Mark bay as reserved and reduce available count
        val updatedBays = lot.bays.map {
            if (it.id == bayId) it.copy(status = BayStatus.RESERVED, reservedByUserId = "USER_DEFAULT")
            else it
        }
        val updatedLot = lot.copy(
            bays = updatedBays,
            availableBays = (lot.availableBays - 1).coerceAtLeast(0)
        )

        _lots.value = currentList.map { if (it.id == lotId) updatedLot else it }
        _activeReservation.value = reservation

        return Result.success(reservation)
    }

    override suspend fun extendReservation(
        reservationId: String,
        additionalMinutes: Int
    ): Result<Reservation> {
        val current = _activeReservation.value
            ?: return Result.failure(IllegalStateException("No active reservation found"))

        if (current.id != reservationId) {
            return Result.failure(IllegalArgumentException("Reservation ID mismatch"))
        }

        val addMs = additionalMinutes.toLong() * 60_000L
        val addCost = (additionalMinutes / 60.0) * current.hourlyRateAed

        val extended = current.copy(
            durationMinutes = current.durationMinutes + additionalMinutes,
            endTimeEpochMs = current.endTimeEpochMs + addMs,
            totalCostAed = ((current.totalCostAed + addCost) * 100).toInt() / 100.0,
            status = ReservationStatus.EXTENDED
        )

        _activeReservation.value = extended
        return Result.success(extended)
    }

    override suspend fun cancelReservation(reservationId: String): Result<Boolean> {
        val current = _activeReservation.value
            ?: return Result.failure(IllegalArgumentException("No active reservation found"))

        if (current.id != reservationId) {
            return Result.failure(IllegalArgumentException("Reservation ID mismatch"))
        }

        // Free the bay
        val lot = _lots.value.find { it.id == current.lotId }
        if (lot != null) {
            val updatedBays = lot.bays.map {
                if (it.id == current.bayId) it.copy(status = BayStatus.AVAILABLE, reservedByUserId = null)
                else it
            }
            val updatedLot = lot.copy(
                bays = updatedBays,
                availableBays = (lot.availableBays + 1).coerceAtMost(lot.totalCapacity)
            )
            _lots.value = _lots.value.map { if (it.id == lot.id) updatedLot else it }
        }
        _activeReservation.value = null
        return Result.success(true)
    }

    // --- AdminRepository Implementations (Way 2) ---

    override suspend fun addParkingLot(lot: ParkingLot): Result<ParkingLot> {
        val existing = _lots.value
        if (existing.any { it.id == lot.id }) {
            return Result.failure(IllegalArgumentException("Lot with ID ${lot.id} already exists"))
        }
        _lots.value = listOf(lot) + existing
        return Result.success(lot)
    }

    override suspend fun deleteParkingLot(lotId: String): Result<Boolean> {
        val current = _lots.value
        val filtered = current.filterNot { it.id == lotId }
        if (filtered.size == current.size) {
            return Result.failure(IllegalArgumentException("Lot not found"))
        }
        _lots.value = filtered
        return Result.success(true)
    }

    override suspend fun updateBayStatus(
        lotId: String,
        bayId: String,
        newStatus: BayStatus
    ): Result<Boolean> {
        val current = _lots.value
        val lot = current.find { it.id == lotId } ?: return Result.failure(IllegalArgumentException("Lot not found"))

        val bay = lot.bays.find { it.id == bayId } ?: return Result.failure(IllegalArgumentException("Bay not found"))
        val oldStatus = bay.status

        val updatedBays = lot.bays.map {
            if (it.id == bayId) it.copy(status = newStatus) else it
        }

        // Recalculate available count
        val delta = when {
            oldStatus != BayStatus.AVAILABLE && newStatus == BayStatus.AVAILABLE -> 1
            oldStatus == BayStatus.AVAILABLE && newStatus != BayStatus.AVAILABLE -> -1
            else -> 0
        }

        val updatedLot = lot.copy(
            bays = updatedBays,
            availableBays = (lot.availableBays + delta).coerceIn(0, lot.totalCapacity)
        )

        _lots.value = current.map { if (it.id == lotId) updatedLot else it }
        return Result.success(true)
    }

    override suspend fun updateBayType(lotId: String, bayId: String, newType: BayType): Result<Boolean> {
        val current = _lots.value
        val lot = current.find { it.id == lotId } ?: return Result.failure(IllegalArgumentException("Lot not found"))

        val updatedBays = lot.bays.map {
            if (it.id == bayId) it.copy(type = newType) else it
        }

        _lots.value = current.map { if (it.id == lotId) lot.copy(bays = updatedBays) else it }
        return Result.success(true)
    }

    override suspend fun updateHourlyRate(lotId: String, newRateAed: Double): Result<Boolean> {
        val current = _lots.value
        val lot = current.find { it.id == lotId } ?: return Result.failure(IllegalArgumentException("Lot not found"))

        _lots.value = current.map {
            if (it.id == lotId) it.copy(hourlyRateAed = newRateAed) else it
        }
        return Result.success(true)
    }

    override suspend fun simulateLiveSensorToggle(lotId: String, bayId: String): Result<BayStatus> {
        val current = _lots.value
        val lot = current.find { it.id == lotId } ?: return Result.failure(IllegalArgumentException("Lot not found"))
        val bay = lot.bays.find { it.id == bayId } ?: return Result.failure(IllegalArgumentException("Bay not found"))

        val nextStatus = when (bay.status) {
            BayStatus.AVAILABLE -> BayStatus.OCCUPIED
            BayStatus.OCCUPIED -> BayStatus.AVAILABLE
            BayStatus.RESERVED -> BayStatus.OCCUPIED
            BayStatus.MAINTENANCE -> BayStatus.AVAILABLE
        }

        updateBayStatus(lotId, bayId, nextStatus)
        return Result.success(nextStatus)
    }

    override fun observeLiveLotFeed(): Flow<List<ParkingLot>> = lotsFlow

    private fun currentTimeMillis(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }

    companion object {
        fun createInitialDubaiLots(): List<ParkingLot> {
            return listOf(
                // 1. Bur Dubai - Al Fahidi Historical Neighborhood
                ParkingLot(
                    id = "LOT_BUR_01",
                    name = "Al Fahidi Heritage Public Parking",
                    zone = DubaiZone.BUR_DUBAI,
                    facilityType = FacilityType.SURFACE_OPEN_LOT,
                    latitude = 25.2605,
                    longitude = 55.2985,
                    totalCapacity = 60,
                    availableBays = 22,
                    hourlyRateAed = 4.0,
                    operatorName = "RTA Dubai Public Parking",
                    isManagedByRta = true,
                    bays = generateSampleBays("BF", 60, 4, 6, 8, 4),
                    operatingHours = "24/7 (Paid 8AM - 10PM)",
                    address = "Al Fahidi St, Bur Dubai, Near Dubai Museum"
                ),
                // 2. Al Karama - Commercial Center
                ParkingLot(
                    id = "LOT_KAR_01",
                    name = "Karama Commercial Center Parking",
                    zone = DubaiZone.KARAMA,
                    facilityType = FacilityType.SURFACE_OPEN_LOT,
                    latitude = 25.2428,
                    longitude = 55.3025,
                    totalCapacity = 85,
                    availableBays = 18,
                    hourlyRateAed = 4.0,
                    operatorName = "RTA Zone B Parking",
                    isManagedByRta = true,
                    bays = generateSampleBays("KC", 85, 5, 8, 12, 6),
                    operatingHours = "8:00 AM - 10:00 PM",
                    address = "18B Street, Al Karama Commercial District"
                ),
                // 3. Deira - Gold & Spice Souq Hub
                ParkingLot(
                    id = "LOT_DEI_01",
                    name = "Deira Old Souq Automated Garage",
                    zone = DubaiZone.DEIRA,
                    facilityType = FacilityType.MULTI_STOREY_GARAGE,
                    latitude = 25.2690,
                    longitude = 55.3120,
                    totalCapacity = 120,
                    availableBays = 45,
                    hourlyRateAed = 5.0,
                    operatorName = "RTA Multi-Storey Parking",
                    isManagedByRta = true,
                    bays = generateSampleBays("DS", 120, 8, 12, 16, 10),
                    operatingHours = "24/7 Automated",
                    address = "Baniyas Rd, Deira, Near Gold Souq Metro"
                ),
                // 4. Downtown Dubai - Boulevard Smart Garage
                ParkingLot(
                    id = "LOT_DOW_01",
                    name = "Downtown Boulevard Smart Garage",
                    zone = DubaiZone.DOWNTOWN,
                    facilityType = FacilityType.UNDERGROUND_SMART_PARK,
                    latitude = 25.1965,
                    longitude = 55.2755,
                    totalCapacity = 150,
                    availableBays = 62,
                    hourlyRateAed = 10.0,
                    operatorName = "Emaar Mobility / Mawaqif",
                    isManagedByRta = false,
                    bays = generateSampleBays("DT", 150, 10, 15, 20, 25),
                    operatingHours = "24/7 Valet & Smart Gate",
                    address = "Sheikh Mohammed bin Rashid Blvd, Downtown Dubai"
                ),
                // 5. Karama - Post Office Street Hub
                ParkingLot(
                    id = "LOT_KAR_02",
                    name = "Karama Central Post Office Lot",
                    zone = DubaiZone.KARAMA,
                    facilityType = FacilityType.SURFACE_OPEN_LOT,
                    latitude = 25.2460,
                    longitude = 55.3080,
                    totalCapacity = 45,
                    availableBays = 7,
                    hourlyRateAed = 4.0,
                    operatorName = "RTA Zone A Parking",
                    isManagedByRta = true,
                    bays = generateSampleBays("KP", 45, 3, 4, 6, 2),
                    operatingHours = "8:00 AM - 10:00 PM",
                    address = "Za'abeel St, Near Karama Post Office"
                ),
                // 6. Bur Dubai - Meena Bazaar Corridor
                ParkingLot(
                    id = "LOT_BUR_02",
                    name = "Meena Bazaar Express Parking",
                    zone = DubaiZone.BUR_DUBAI,
                    facilityType = FacilityType.ON_STREET_METRIC,
                    latitude = 25.2580,
                    longitude = 55.2940,
                    totalCapacity = 50,
                    availableBays = 4,
                    hourlyRateAed = 4.0,
                    operatorName = "RTA Zone A Parking",
                    isManagedByRta = true,
                    bays = generateSampleBays("MB", 50, 3, 5, 10, 2),
                    operatingHours = "8:00 AM - 10:00 PM",
                    address = "Al Souq Al Kabeer, Meena Bazaar"
                )
            )
        }

        private fun generateSampleBays(
            prefix: String,
            total: Int,
            podCount: Int,
            pinkCount: Int,
            deliveryCount: Int,
            evCount: Int
        ): List<ParkingBay> {
            val bays = mutableListOf<ParkingBay>()
            var currentIdx = 1

            // POD bays
            repeat(podCount) {
                bays.add(
                    ParkingBay(
                        id = "${prefix}_POD_${currentIdx}",
                        bayNumber = "${prefix}-P${currentIdx}",
                        type = BayType.PEOPLE_OF_DETERMINATION,
                        status = if (currentIdx % 2 == 0) BayStatus.AVAILABLE else BayStatus.OCCUPIED,
                        floorLevel = "Ground (Priority Access)"
                    )
                )
                currentIdx++
            }

            // Women-Only Pink bays
            var pinkIdx = 1
            repeat(pinkCount) {
                bays.add(
                    ParkingBay(
                        id = "${prefix}_WPN_${pinkIdx}",
                        bayNumber = "${prefix}-W${pinkIdx}",
                        type = BayType.WOMEN_ONLY_PINK,
                        status = if (pinkIdx % 3 == 0) BayStatus.OCCUPIED else BayStatus.AVAILABLE,
                        floorLevel = "Ground (Well-lit Near Exit)"
                    )
                )
                pinkIdx++
                currentIdx++
            }

            // Delivery Rider short-stay bays
            var delIdx = 1
            repeat(deliveryCount) {
                bays.add(
                    ParkingBay(
                        id = "${prefix}_DEL_${delIdx}",
                        bayNumber = "${prefix}-D${delIdx}",
                        type = BayType.DELIVERY_RIDER,
                        status = if (delIdx % 2 == 1) BayStatus.AVAILABLE else BayStatus.OCCUPIED,
                        floorLevel = "Street Level (Quick Bay)"
                    )
                )
                delIdx++
                currentIdx++
            }

            // EV Charging bays
            var evIdx = 1
            repeat(evCount) {
                bays.add(
                    ParkingBay(
                        id = "${prefix}_EV_${evIdx}",
                        bayNumber = "${prefix}-E${evIdx}",
                        type = BayType.EV_CHARGING,
                        status = if (evIdx % 2 == 0) BayStatus.OCCUPIED else BayStatus.AVAILABLE,
                        floorLevel = "Level 1 (Fast Charger)"
                    )
                )
                evIdx++
                currentIdx++
            }

            // Standard bays for the remainder
            val remaining = (total - bays.size).coerceAtLeast(0)
            var stdIdx = 1
            repeat(remaining) {
                bays.add(
                    ParkingBay(
                        id = "${prefix}_STD_${stdIdx}",
                        bayNumber = "${prefix}-S${stdIdx}",
                        type = BayType.STANDARD,
                        status = if (stdIdx % 3 == 0 || stdIdx % 5 == 0) BayStatus.OCCUPIED else BayStatus.AVAILABLE,
                        floorLevel = if (stdIdx > 30) "Level 2" else "Level 1"
                    )
                )
                stdIdx++
                currentIdx++
            }

            return bays
        }
    }
}
