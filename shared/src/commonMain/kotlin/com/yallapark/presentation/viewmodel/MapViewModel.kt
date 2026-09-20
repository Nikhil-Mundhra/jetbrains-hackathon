package com.yallapark.presentation.viewmodel

import com.yallapark.data.engine.PredictiveOccupancyEngine
import com.yallapark.domain.model.BayType
import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.ParkingLot
import com.yallapark.domain.model.PredictiveForecast
import com.yallapark.domain.repository.ParkingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class BayFilterCategory(val label: String) {
    ALL("All Bays"),
    POD("POD Accessible"),
    WOMEN_PINK("Women-Only"),
    DELIVERY("Delivery Rider"),
    EV("EV Charging")
}

class MapViewModel(
    private val parkingRepository: ParkingRepository,
    private val predictionEngine: PredictiveOccupancyEngine = PredictiveOccupancyEngine()
) : ViewModel() {

    private val _selectedZone = MutableStateFlow<DubaiZone?>(null)
    val selectedZone = _selectedZone.asStateFlow()

    private val _selectedCategory = MutableStateFlow(BayFilterCategory.ALL)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedLot = MutableStateFlow<ParkingLot?>(null)
    val selectedLot = _selectedLot.asStateFlow()

    private val _targetEtaMinutes = MutableStateFlow(20)
    val targetEtaMinutes = _targetEtaMinutes.asStateFlow()

    private val _predictiveForecast = MutableStateFlow<PredictiveForecast?>(null)
    val predictiveForecast = _predictiveForecast.asStateFlow()

    val filteredLots: StateFlow<List<ParkingLot>> = combine(
        parkingRepository.getAllLots(),
        _selectedZone,
        _selectedCategory,
        _searchQuery
    ) { allLots, zone, category, query ->
        allLots.filter { lot ->
            val matchesZone = zone == null || lot.zone == zone
            val matchesCategory = when (category) {
                BayFilterCategory.ALL -> true
                BayFilterCategory.POD -> lot.hasPodBays
                BayFilterCategory.WOMEN_PINK -> lot.hasWomenPinkBays
                BayFilterCategory.DELIVERY -> lot.hasDeliveryBays
                BayFilterCategory.EV -> lot.hasEvCharging
            }
            val matchesQuery = query.isBlank() ||
                lot.name.contains(query, ignoreCase = true) ||
                lot.zone.displayName.contains(query, ignoreCase = true) ||
                lot.address.contains(query, ignoreCase = true)

            matchesZone && matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectZone(zone: DubaiZone?) {
        _selectedZone.value = zone
    }

    fun selectCategory(category: BayFilterCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectLot(lot: ParkingLot?) {
        _selectedLot.value = lot
        lot?.let { recalculatePrediction(it, _targetEtaMinutes.value) }
    }

    fun setTargetEtaMinutes(minutes: Int) {
        _targetEtaMinutes.value = minutes
        _selectedLot.value?.let { recalculatePrediction(it, minutes) }
    }

    private fun recalculatePrediction(lot: ParkingLot, eta: Int) {
        viewModelScope.launch {
            val forecast = predictionEngine.calculateForecast(lot, eta)
            _predictiveForecast.value = forecast
        }
    }
}
