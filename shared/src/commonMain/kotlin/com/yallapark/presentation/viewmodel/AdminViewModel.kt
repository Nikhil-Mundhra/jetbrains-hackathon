package com.yallapark.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yallapark.domain.model.*
import com.yallapark.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    // Current multi-tenant role
    private val _currentRole = MutableStateFlow(UserRole.RTA_AUTHORITY)
    val currentRole = _currentRole.asStateFlow()

    // Observable live feed of all facilities
    val liveLots: StateFlow<List<ParkingLot>> = adminRepository.observeLiveLotFeed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected lot for admin inspection & bay matrix editing
    private val _selectedLotForAdmin = MutableStateFlow<ParkingLot?>(null)
    val selectedLotForAdmin = _selectedLotForAdmin.asStateFlow()

    private val _adminMessage = MutableStateFlow<String?>(null)
    val adminMessage = _adminMessage.asStateFlow()

    fun switchRole(newRole: UserRole) {
        _currentRole.value = newRole
        _adminMessage.value = "Switched to ${newRole.title}"
    }

    fun selectLotForAdmin(lot: ParkingLot?) {
        _selectedLotForAdmin.value = lot
    }

    fun addNewParkingLot(
        name: String,
        zone: DubaiZone,
        facilityType: FacilityType,
        totalCapacity: Int,
        hourlyRateAed: Double,
        podCount: Int,
        pinkCount: Int,
        deliveryCount: Int,
        address: String
    ) {
        val lotId = "LOT_ADMIN_${currentTimeMillis().toString().takeLast(5)}"
        val bays = mutableListOf<ParkingBay>()
        var idx = 1

        repeat(podCount) {
            bays.add(
                ParkingBay(
                    id = "${lotId}_POD_$idx",
                    bayNumber = "P-$idx",
                    type = BayType.PEOPLE_OF_DETERMINATION,
                    status = BayStatus.AVAILABLE,
                    floorLevel = "Ground"
                )
            )
            idx++
        }
        var pIdx = 1
        repeat(pinkCount) {
            bays.add(
                ParkingBay(
                    id = "${lotId}_WPN_$pIdx",
                    bayNumber = "W-$pIdx",
                    type = BayType.WOMEN_ONLY_PINK,
                    status = BayStatus.AVAILABLE,
                    floorLevel = "Ground"
                )
            )
            pIdx++
            idx++
        }
        var dIdx = 1
        repeat(deliveryCount) {
            bays.add(
                ParkingBay(
                    id = "${lotId}_DEL_$dIdx",
                    bayNumber = "D-$dIdx",
                    type = BayType.DELIVERY_RIDER,
                    status = BayStatus.AVAILABLE,
                    floorLevel = "Street Level"
                )
            )
            dIdx++
            idx++
        }
        val remaining = (totalCapacity - bays.size).coerceAtLeast(0)
        var sIdx = 1
        repeat(remaining) {
            bays.add(
                ParkingBay(
                    id = "${lotId}_STD_$sIdx",
                    bayNumber = "S-$sIdx",
                    type = BayType.STANDARD,
                    status = BayStatus.AVAILABLE,
                    floorLevel = "Level 1"
                )
            )
            sIdx++
            idx++
        }

        val newLot = ParkingLot(
            id = lotId,
            name = name,
            zone = zone,
            facilityType = facilityType,
            latitude = zone.lat + (kotlin.random.Random.nextDouble(-0.005, 0.005)),
            longitude = zone.lon + (kotlin.random.Random.nextDouble(-0.005, 0.005)),
            totalCapacity = totalCapacity,
            availableBays = totalCapacity,
            hourlyRateAed = hourlyRateAed,
            operatorName = when (_currentRole.value) {
                UserRole.RTA_AUTHORITY -> "RTA Municipal Parking"
                UserRole.MAWAQIF_OPERATOR -> "Mawaqif Zone Enforcement"
                UserRole.PRIVATE_OPERATOR -> "Private Operator Facility"
                UserRole.DRIVER -> "Public Parking"
            },
            isManagedByRta = _currentRole.value == UserRole.RTA_AUTHORITY,
            bays = bays,
            hasPodBays = podCount > 0,
            hasWomenPinkBays = pinkCount > 0,
            hasDeliveryBays = deliveryCount > 0,
            address = address
        )

        viewModelScope.launch {
            adminRepository.addParkingLot(newLot)
            _adminMessage.value = "Successfully added facility '${newLot.name}' (${newLot.zone.displayName})"
        }
    }

    fun deleteParkingLot(lotId: String) {
        viewModelScope.launch {
            adminRepository.deleteParkingLot(lotId)
            if (_selectedLotForAdmin.value?.id == lotId) {
                _selectedLotForAdmin.value = null
            }
            _adminMessage.value = "Facility removed successfully"
        }
    }

    fun updateBayStatus(lotId: String, bayId: String, newStatus: BayStatus) {
        viewModelScope.launch {
            adminRepository.updateBayStatus(lotId, bayId, newStatus)
            // Refresh selected lot
            val updated = liveLots.value.find { it.id == lotId }
            _selectedLotForAdmin.value = updated
        }
    }

    fun simulateSensorTrigger(lotId: String, bayId: String) {
        viewModelScope.launch {
            val result = adminRepository.simulateLiveSensorToggle(lotId, bayId)
            result.onSuccess { next ->
                _adminMessage.value = "Sensor Event: Bay toggled to $next"
                val updated = liveLots.value.find { it.id == lotId }
                _selectedLotForAdmin.value = updated
            }
        }
    }

    fun updateHourlyRate(lotId: String, newRate: Double) {
        viewModelScope.launch {
            adminRepository.updateHourlyRate(lotId, newRate)
            _adminMessage.value = "Updated tariff to AED $newRate/hr"
        }
    }

    fun clearMessage() {
        _adminMessage.value = null
    }

    private fun currentTimeMillis(): Long {
        return kotlin.time.TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds + 1726830000000L
    }
}
