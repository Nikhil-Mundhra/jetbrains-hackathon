package com.yallapark.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AccountType {
    MOTORIST_DRIVER,
    PARKING_PROVIDER
}

@Serializable
data class UserSession(
    val id: String,
    val name: String,
    val emailOrPhone: String,
    val accountType: AccountType,
    val role: UserRole = UserRole.DRIVER,
    val organizationName: String? = null,
    val vehiclePlateNumber: String? = null,
    val badgeNumberOrGovId: String? = null,
    val ecoPoints: Int = 340
)
