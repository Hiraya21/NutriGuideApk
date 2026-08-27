package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.models.AdminAuditLog
import com.example.domain.models.FarmerRegistryItem
import com.example.domain.models.UserAccount
import com.example.domain.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nutriguide_auth_prefs", Context.MODE_PRIVATE)

    // Authorized Personnel Whitelist
    data class AuthorizedPersonnelCredential(
        val personnelId: String,
        val emailOrUsername: String,
        val passcode: String,
        val fullName: String,
        val role: UserRole,
        val agency: String,
        val designation: String
    )

    private val authorizedPersonnelList = listOf(
        AuthorizedPersonnelCredential(
            personnelId = "DA-MAO-2026-09",
            emailOrUsername = "admin@da.gov.ph",
            passcode = "admin123",
            fullName = "Engr. Roberto Santos",
            role = UserRole.AUTHORIZED_ADMIN,
            agency = "Department of Agriculture - Region III",
            designation = "Municipal Agricultural Officer (MAO)"
        ),
        AuthorizedPersonnelCredential(
            personnelId = "PHILRICE-TECH-404",
            emailOrUsername = "corpuz.m@philrice.gov.ph",
            passcode = "palay2026",
            fullName = "Dr. Maria Elena Corpuz",
            role = UserRole.AGRICULTURAL_TECHNOLOGIST,
            agency = "DA-PhilRice Central Experiment Station",
            designation = "Senior Agronomist & Extension Specialist"
        ),
        AuthorizedPersonnelCredential(
            personnelId = "DA-RFO3-EX88",
            emailOrUsername = "rfo3.supervisor@da.gov.ph",
            passcode = "da2026ph",
            fullName = "Dir. Arthur Del Rosario",
            role = UserRole.AUTHORIZED_ADMIN,
            agency = "DA Regional Field Office III",
            designation = "Regional Executive Director"
        )
    )

    // Default Demo Farmer
    private val defaultFarmer = UserAccount(
        id = "farmer_001",
        fullName = "Juan Dela Cruz",
        usernameOrEmail = "0917-123-4567",
        role = UserRole.FARMER,
        rsbsaNumber = "03-49-12-00421",
        agency = "DA-RSBSA Registry",
        province = "Nueva Ecija",
        municipality = "Science City of Muñoz",
        farmAreaHectares = 2.4,
        primaryCrop = "Lowland Irrigated Rice (NSIC Rc 222)",
        phoneNumber = "0917-123-4567",
        dateRegistered = "Jul 2026"
    )

    private val _currentUser = MutableStateFlow<UserAccount>(loadPersistedUser())
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AdminAuditLog>>(loadDefaultAuditLogs())
    val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

    private val _farmerRegistry = MutableStateFlow<List<FarmerRegistryItem>>(loadDefaultFarmerRegistry())
    val farmerRegistry: StateFlow<List<FarmerRegistryItem>> = _farmerRegistry.asStateFlow()

    fun getAuthorizedPersonnelList(): List<AuthorizedPersonnelCredential> = authorizedPersonnelList

    private fun loadPersistedUser(): UserAccount {
        val id = prefs.getString("user_id", null) ?: return defaultFarmer
        val name = prefs.getString("user_name", "Juan Dela Cruz") ?: "Juan Dela Cruz"
        val username = prefs.getString("user_username", "farmer_juan") ?: "farmer_juan"
        val roleStr = prefs.getString("user_role", UserRole.FARMER.name) ?: UserRole.FARMER.name
        val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.FARMER }
        val rsbsa = prefs.getString("user_rsbsa", "03-49-12-00421") ?: ""
        val badge = prefs.getString("user_badge", "") ?: ""
        val agency = prefs.getString("user_agency", "DA-PhilRice") ?: "DA-PhilRice"
        val prov = prefs.getString("user_prov", "Nueva Ecija") ?: "Nueva Ecija"
        val mun = prefs.getString("user_mun", "Muñoz") ?: "Muñoz"
        val area = prefs.getFloat("user_area", 2.4f).toDouble()
        val crop = prefs.getString("user_crop", "Inbred Rice") ?: "Inbred Rice"
        val phone = prefs.getString("user_phone", "0917-123-4567") ?: "0917-123-4567"

        return UserAccount(
            id = id,
            fullName = name,
            usernameOrEmail = username,
            role = role,
            rsbsaNumber = rsbsa,
            badgeOrPersonnelId = badge,
            agency = agency,
            province = prov,
            municipality = mun,
            farmAreaHectares = area,
            primaryCrop = crop,
            phoneNumber = phone
        )
    }

    private fun savePersistedUser(user: UserAccount) {
        prefs.edit()
            .putString("user_id", user.id)
            .putString("user_name", user.fullName)
            .putString("user_username", user.usernameOrEmail)
            .putString("user_role", user.role.name)
            .putString("user_rsbsa", user.rsbsaNumber)
            .putString("user_badge", user.badgeOrPersonnelId)
            .putString("user_agency", user.agency)
            .putString("user_prov", user.province)
            .putString("user_mun", user.municipality)
            .putFloat("user_area", user.farmAreaHectares.toFloat())
            .putString("user_crop", user.primaryCrop)
            .putString("user_phone", user.phoneNumber)
            .apply()
    }

    // Farmer Login
    fun loginFarmer(identifier: String, passcode: String): Result<UserAccount> {
        val trimmed = identifier.trim()
        if (trimmed.isBlank()) {
            return Result.failure(Exception("Please enter your Phone number or RSBSA Number"))
        }

        // Search matching registered farmer or create active session
        val farmer = UserAccount(
            id = "farmer_${System.currentTimeMillis() % 10000}",
            fullName = if (trimmed.contains("pedro", ignoreCase = true)) "Pedro Mangahas" else if (trimmed.contains("maria", ignoreCase = true)) "Maria Clara Santos" else "Juan Dela Cruz",
            usernameOrEmail = trimmed,
            role = UserRole.FARMER,
            rsbsaNumber = if (trimmed.contains("-")) trimmed else "03-49-12-00421",
            province = "Nueva Ecija",
            municipality = "Science City of Muñoz",
            farmAreaHectares = 2.4,
            primaryCrop = "Lowland Irrigated Palay (NSIC Rc 222)",
            phoneNumber = if (trimmed.startsWith("09")) trimmed else "0917-123-4567"
        )

        _currentUser.value = farmer
        savePersistedUser(farmer)
        return Result.success(farmer)
    }

    // Farmer Registration
    fun registerFarmer(
        fullName: String,
        phone: String,
        rsbsa: String,
        province: String,
        municipality: String,
        farmAreaHa: Double,
        crop: String
    ): Result<UserAccount> {
        if (fullName.isBlank() || phone.isBlank()) {
            return Result.failure(Exception("Full Name and Phone Number are required."))
        }

        val newFarmer = UserAccount(
            id = "farmer_${UUID.randomUUID().toString().take(8)}",
            fullName = fullName.trim(),
            usernameOrEmail = phone.trim(),
            role = UserRole.FARMER,
            rsbsaNumber = if (rsbsa.isBlank()) "03-49-12-${(10000..99999).random()}" else rsbsa.trim(),
            province = if (province.isBlank()) "Nueva Ecija" else province,
            municipality = if (municipality.isBlank()) "Muñoz" else municipality,
            farmAreaHectares = if (farmAreaHa <= 0.0) 1.0 else farmAreaHa,
            primaryCrop = if (crop.isBlank()) "Inbred Rice" else crop,
            phoneNumber = phone.trim(),
            dateRegistered = SimpleDateFormat("MMM yyyy", Locale.US).format(Date())
        )

        // Add to registry
        val newItem = FarmerRegistryItem(
            id = newFarmer.id,
            fullName = newFarmer.fullName,
            rsbsaId = newFarmer.rsbsaNumber,
            barangay = "Brgy. Maligaya",
            municipality = newFarmer.municipality,
            farmSizeHa = newFarmer.farmAreaHectares,
            cropVariety = newFarmer.primaryCrop,
            soilType = "Clay Loam (Maligaya Clay)",
            lastActiveDate = "Today, " + SimpleDateFormat("h:mm a", Locale.US).format(Date()),
            subsidyStatus = "Pending Verification"
        )
        _farmerRegistry.value = listOf(newItem) + _farmerRegistry.value

        _currentUser.value = newFarmer
        savePersistedUser(newFarmer)
        return Result.success(newFarmer)
    }

    // Admin Personnel Login with specific verification
    fun loginAuthorizedPersonnel(
        identifier: String, // email or badge ID
        passcode: String
    ): Result<UserAccount> {
        val trimmedId = identifier.trim()
        val trimmedPass = passcode.trim()

        if (trimmedId.isBlank() || trimmedPass.isBlank()) {
            return Result.failure(Exception("Personnel ID/Email and Security Passcode are required."))
        }

        // Find match in authorized personnel whitelist
        val match = authorizedPersonnelList.find {
            (it.emailOrUsername.equals(trimmedId, ignoreCase = true) ||
             it.personnelId.equals(trimmedId, ignoreCase = true)) &&
            it.passcode == trimmedPass
        }

        if (match == null) {
            return Result.failure(
                Exception("Access Denied: Unrecognized Personnel ID or invalid Security Passcode. Only authorized DA / PhilRice agricultural personnel may enter the Admin Dashboard.")
            )
        }

        val adminUser = UserAccount(
            id = "admin_${match.personnelId}",
            fullName = match.fullName,
            usernameOrEmail = match.emailOrUsername,
            role = match.role,
            badgeOrPersonnelId = match.personnelId,
            agency = match.agency,
            province = "Regional Field Office III",
            municipality = "Central Experiment Station",
            farmAreaHectares = 0.0,
            primaryCrop = "Regional Agronomy Database"
        )

        // Record Audit Log
        val log = AdminAuditLog(
            id = UUID.randomUUID().toString().take(6),
            personnelName = match.fullName,
            personnelId = match.personnelId,
            action = "Authorized Login to Admin Portal (${match.designation})",
            timestampFormatted = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.US).format(Date()),
            ipOrDevice = "DA Regional Secure Node"
        )
        _auditLogs.value = listOf(log) + _auditLogs.value

        _currentUser.value = adminUser
        savePersistedUser(adminUser)
        return Result.success(adminUser)
    }

    fun logout() {
        _currentUser.value = defaultFarmer
        savePersistedUser(defaultFarmer)
    }

    fun switchRoleToFarmer() {
        _currentUser.value = defaultFarmer
        savePersistedUser(defaultFarmer)
    }

    private fun loadDefaultAuditLogs(): List<AdminAuditLog> {
        return listOf(
            AdminAuditLog(
                id = "log_101",
                personnelName = "Engr. Roberto Santos",
                personnelId = "DA-MAO-2026-09",
                action = "Generated Regional Fertilizer Allocation Vouchers for Muñoz",
                timestampFormatted = "Aug 24, 2026 • 2:15 PM"
            ),
            AdminAuditLog(
                id = "log_102",
                personnelName = "Dr. Maria Elena Corpuz",
                personnelId = "PHILRICE-TECH-404",
                action = "Broadcasted Agrometeorology Advisory for Monsoon Season (Central Luzon)",
                timestampFormatted = "Aug 24, 2026 • 11:30 AM"
            ),
            AdminAuditLog(
                id = "log_103",
                personnelName = "Dir. Arthur Del Rosario",
                personnelId = "DA-RFO3-EX88",
                action = "Approved RSBSA Batch #2026-CL3 Soil Chemistry Matrix",
                timestampFormatted = "Aug 23, 2026 • 4:45 PM"
            )
        )
    }

    private fun loadDefaultFarmerRegistry(): List<FarmerRegistryItem> {
        return listOf(
            FarmerRegistryItem(
                id = "reg_1",
                fullName = "Juan Dela Cruz",
                rsbsaId = "03-49-12-00421",
                barangay = "Brgy. Maligaya",
                municipality = "Science City of Muñoz",
                farmSizeHa = 2.4,
                cropVariety = "NSIC Rc 222 (Tubigan 21)",
                soilType = "Maligaya Clay Loam (pH 6.2)",
                lastActiveDate = "Today, 10:45 AM",
                subsidyStatus = "Approved (DA-RCEF)"
            ),
            FarmerRegistryItem(
                id = "reg_2",
                fullName = "Pedro Mangahas",
                rsbsaId = "03-49-12-00853",
                barangay = "Brgy. Bantug",
                municipality = "Science City of Muñoz",
                farmSizeHa = 3.1,
                cropVariety = "NSIC Rc 160 (Tubigan 14)",
                soilType = "San Manuel Silt Loam (pH 5.9)",
                lastActiveDate = "Yesterday, 3:20 PM",
                subsidyStatus = "Approved (DA-RCEF)"
            ),
            FarmerRegistryItem(
                id = "reg_3",
                fullName = "Maria Clara Santos",
                rsbsaId = "03-49-12-01102",
                barangay = "Brgy. Maragol",
                municipality = "Science City of Muñoz",
                farmSizeHa = 1.8,
                cropVariety = "NSIC Rc 480 (Green Super Rice)",
                soilType = "Quingua Clay Loam (pH 6.5)",
                lastActiveDate = "Aug 22, 2026",
                subsidyStatus = "Voucher Distributed"
            ),
            FarmerRegistryItem(
                id = "reg_4",
                fullName = "Ernesto Pineda",
                rsbsaId = "03-49-12-01499",
                barangay = "Brgy. Villa Santos",
                municipality = "Science City of Muñoz",
                farmSizeHa = 4.5,
                cropVariety = "Mestiso 20 (Hybrid Rice)",
                soilType = "Bantog Clay (pH 5.8)",
                lastActiveDate = "Aug 21, 2026",
                subsidyStatus = "Approved (DA-RCEF)"
            ),
            FarmerRegistryItem(
                id = "reg_5",
                fullName = "Teresa Macapagal",
                rsbsaId = "03-49-12-01932",
                barangay = "Brgy. Curva",
                municipality = "Science City of Muñoz",
                farmSizeHa = 2.0,
                cropVariety = "NSIC Rc 216",
                soilType = "Maligaya Clay (pH 6.1)",
                lastActiveDate = "Aug 20, 2026",
                subsidyStatus = "Under Review"
            )
        )
    }
}
