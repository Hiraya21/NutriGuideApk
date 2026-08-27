package com.example.domain.models

enum class UserRole {
    FARMER,
    AUTHORIZED_ADMIN,
    AGRICULTURAL_TECHNOLOGIST
}

data class UserAccount(
    val id: String,
    val fullName: String,
    val usernameOrEmail: String,
    val role: UserRole,
    val rsbsaNumber: String = "",
    val badgeOrPersonnelId: String = "",
    val agency: String = "DA-PhilRice",
    val province: String = "Nueva Ecija",
    val municipality: String = "Muñoz",
    val farmAreaHectares: Double = 1.5,
    val primaryCrop: String = "Inbred Rice (NSIC Rc 222)",
    val phoneNumber: String = "0917-123-4567",
    val profilePhotoUrl: String = "",
    val dateRegistered: String = "Aug 2026"
) {
    val isAdmin: Boolean get() = role == UserRole.AUTHORIZED_ADMIN || role == UserRole.AGRICULTURAL_TECHNOLOGIST
    val displayRoleLabel: String get() = when (role) {
        UserRole.FARMER -> "Registered Farmer"
        UserRole.AUTHORIZED_ADMIN -> "Authorized DA Admin"
        UserRole.AGRICULTURAL_TECHNOLOGIST -> "PhilRice Extension Specialist"
    }
}

data class AdminAuditLog(
    val id: String,
    val personnelName: String,
    val personnelId: String,
    val action: String,
    val timestampFormatted: String,
    val ipOrDevice: String = "Local Secure Terminal"
)

data class FarmerRegistryItem(
    val id: String,
    val fullName: String,
    val rsbsaId: String,
    val barangay: String,
    val municipality: String,
    val farmSizeHa: Double,
    val cropVariety: String,
    val soilType: String,
    val lastActiveDate: String,
    val subsidyStatus: String = "Approved (DA-RCEF)"
)
