package com.yallapark.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.yallapark.domain.model.AccountType
import com.yallapark.domain.model.UserRole
import com.yallapark.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _selectedAccountType = MutableStateFlow(AccountType.MOTORIST_DRIVER)
    val selectedAccountType = _selectedAccountType.asStateFlow()

    // Driver login fields
    private val _driverPhone = MutableStateFlow("+971 50 123 4567")
    val driverPhone = _driverPhone.asStateFlow()

    private val _driverPlate = MutableStateFlow("DXB A 48291")
    val driverPlate = _driverPlate.asStateFlow()

    // Provider login fields
    private val _providerOrg = MutableStateFlow("Roads and Transport Authority (RTA)")
    val providerOrg = _providerOrg.asStateFlow()

    private val _providerGovId = MutableStateFlow("RTA-GOV-9824")
    val providerGovId = _providerGovId.asStateFlow()

    private val _providerRole = MutableStateFlow(UserRole.RTA_AUTHORITY)
    val providerRole = _providerRole.asStateFlow()

    fun setAccountType(type: AccountType) {
        _selectedAccountType.value = type
    }

    fun setDriverPhone(phone: String) {
        _driverPhone.value = phone
    }

    fun setDriverPlate(plate: String) {
        _driverPlate.value = plate.uppercase()
    }

    fun setProviderOrg(org: String) {
        _providerOrg.value = org
    }

    fun setProviderGovId(id: String) {
        _providerGovId.value = id
    }

    fun setProviderRole(role: UserRole) {
        _providerRole.value = role
    }

    fun loginAsDriver(name: String = "Ahmed Al-Mansoor") {
        _currentUser.value = UserSession(
            id = "USR_DRV_01",
            name = name,
            emailOrPhone = _driverPhone.value,
            accountType = AccountType.MOTORIST_DRIVER,
            role = UserRole.DRIVER,
            vehiclePlateNumber = _driverPlate.value,
            ecoPoints = 340
        )
    }

    fun loginAsProvider(
        name: String = "Eng. Fatima Al-Hashimi",
        role: UserRole = _providerRole.value,
        org: String = _providerOrg.value
    ) {
        _currentUser.value = UserSession(
            id = "USR_PRV_01",
            name = name,
            emailOrPhone = "f.alhashimi@rta.ae",
            accountType = AccountType.PARKING_PROVIDER,
            role = role,
            organizationName = org,
            badgeNumberOrGovId = _providerGovId.value
        )
    }

    fun logout() {
        _currentUser.value = null
    }
}
