package com.yallapark.presentation.viewmodel

import com.yallapark.domain.model.ParkingBay
import com.yallapark.domain.model.ParkingLot
import com.yallapark.domain.model.PaymentMethod
import com.yallapark.domain.model.Reservation
import com.yallapark.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class BookingViewModel(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    val activeReservation: StateFlow<Reservation?> = reservationRepository.getActiveReservation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedBay = MutableStateFlow<ParkingBay?>(null)
    val selectedBay = _selectedBay.asStateFlow()

    private val _durationMinutes = MutableStateFlow(60) // default 1 hour
    val durationMinutes = _durationMinutes.asStateFlow()

    private val _vehiclePlate = MutableStateFlow("DXB A 48291")
    val vehiclePlate = _vehiclePlate.asStateFlow()

    private val _paymentMethod = MutableStateFlow(PaymentMethod.APPLE_PAY)
    val paymentMethod = _paymentMethod.asStateFlow()

    private val _bookingError = MutableStateFlow<String?>(null)
    val bookingError = _bookingError.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    fun selectBay(bay: ParkingBay?) {
        _selectedBay.value = bay
    }

    fun setDurationMinutes(minutes: Int) {
        _durationMinutes.value = minutes
    }

    fun setVehiclePlate(plate: String) {
        _vehiclePlate.value = plate
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _paymentMethod.value = method
    }

    fun createReservation(
        lot: ParkingLot,
        onSuccess: (Reservation) -> Unit
    ) {
        val bay = _selectedBay.value ?: run {
            _bookingError.value = "Please select a bay first"
            return
        }

        _isProcessing.value = true
        _bookingError.value = null

        viewModelScope.launch {
            val result = reservationRepository.createReservation(
                lotId = lot.id,
                bayId = bay.id,
                vehiclePlate = _vehiclePlate.value,
                durationMinutes = _durationMinutes.value,
                paymentMethod = _paymentMethod.value
            )
            _isProcessing.value = false
            result.onSuccess { reservation ->
                onSuccess(reservation)
            }.onFailure { err ->
                _bookingError.value = err.message ?: "Booking failed"
            }
        }
    }

    fun extendActiveSession(additionalMinutes: Int) {
        val current = activeReservation.value ?: return
        viewModelScope.launch {
            reservationRepository.extendReservation(current.id, additionalMinutes)
        }
    }

    fun cancelActiveSession() {
        val current = activeReservation.value ?: return
        viewModelScope.launch {
            reservationRepository.cancelReservation(current.id)
        }
    }
}
