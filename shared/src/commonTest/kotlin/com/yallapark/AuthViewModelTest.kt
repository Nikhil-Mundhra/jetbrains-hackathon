package com.yallapark

import com.yallapark.domain.model.AccountType
import com.yallapark.domain.model.UserRole
import com.yallapark.presentation.viewmodel.AuthViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthViewModelTest {

    @Test
    fun testInitialAuthState() {
        val viewModel = AuthViewModel()
        assertNull(viewModel.currentUser.value)
        assertEquals(AccountType.MOTORIST_DRIVER, viewModel.selectedAccountType.value)
    }

    @Test
    fun testDriverLoginFlow() {
        val viewModel = AuthViewModel()
        viewModel.setDriverPhone("+971 50 123 4567")
        viewModel.setDriverPlate("dxb a 48291") // will uppercase
        viewModel.loginAsDriver("Ahmed Al-Mansoor")

        val user = viewModel.currentUser.value
        assertNotNull(user)
        assertEquals("Ahmed Al-Mansoor", user.name)
        assertEquals(AccountType.MOTORIST_DRIVER, user.accountType)
        assertEquals(UserRole.DRIVER, user.role)
        assertEquals("DXB A 48291", user.vehiclePlateNumber)
        assertEquals(340, user.ecoPoints)
    }

    @Test
    fun testProviderLoginFlow() {
        val viewModel = AuthViewModel()
        viewModel.setAccountType(AccountType.PARKING_PROVIDER)
        viewModel.setProviderOrg("Roads and Transport Authority (RTA)")
        viewModel.setProviderGovId("RTA-GOV-9824")
        viewModel.setProviderRole(UserRole.RTA_AUTHORITY)
        viewModel.loginAsProvider(
            name = "Eng. Fatima Al-Hashimi",
            role = UserRole.RTA_AUTHORITY,
            org = "Roads and Transport Authority (RTA)"
        )

        val user = viewModel.currentUser.value
        assertNotNull(user)
        assertEquals("Eng. Fatima Al-Hashimi", user.name)
        assertEquals(AccountType.PARKING_PROVIDER, user.accountType)
        assertEquals(UserRole.RTA_AUTHORITY, user.role)
        assertEquals("Roads and Transport Authority (RTA)", user.organizationName)
        assertEquals("RTA-GOV-9824", user.badgeNumberOrGovId)
    }

    @Test
    fun testLogoutFlow() {
        val viewModel = AuthViewModel()
        viewModel.loginAsDriver("Sara Al-Qasimi")
        assertNotNull(viewModel.currentUser.value)

        viewModel.logout()
        assertNull(viewModel.currentUser.value)
    }
}
