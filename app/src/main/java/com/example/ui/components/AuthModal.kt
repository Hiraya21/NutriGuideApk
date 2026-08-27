package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.AuthRepository
import com.example.domain.models.AppLanguage
import com.example.domain.models.UserAccount
import com.example.domain.models.UserRole
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import com.example.ui.theme.FarmBrownPrimary

@Composable
fun AuthModal(
    isVisible: Boolean,
    currentUser: UserAccount,
    currentLanguage: AppLanguage,
    onFarmerLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onFarmerRegister: (name: String, phone: String, rsbsa: String, prov: String, mun: String, area: Double, crop: String) -> Result<UserAccount>,
    onAdminLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onLogout: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    var selectedTab by remember { mutableIntStateOf(if (currentUser.isAdmin) 2 else 0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFFD7CCC8), RoundedCornerShape(24.dp))
                .testTag("modal_auth_container"),
            color = Color.White,
            contentColor = Color(0xFF2A1E17),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FarmGreenPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.AdminPanelSettings else Icons.Default.Agriculture,
                                contentDescription = null,
                                tint = if (selectedTab == 2) Color(0xFF1565C0) else FarmGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "NutriGuide Accounts",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = "DA-PhilRice Agriculture & Admin Portal",
                                fontSize = 11.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_auth_modal")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF757575))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF5F5F5),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = if (selectedTab == 2) Color(0xFF1565C0) else FarmGreenPrimary,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "🌾 Farmer",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) FarmGreenPrimary else Color(0xFF616161)
                            )
                        },
                        modifier = Modifier.testTag("tab_farmer_login")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "➕ Register",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) FarmGreenPrimary else Color(0xFF616161)
                            )
                        },
                        modifier = Modifier.testTag("tab_farmer_register")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "🛡️ DA Admin",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) Color(0xFF1565C0) else Color(0xFF616161)
                            )
                        },
                        modifier = Modifier.testTag("tab_admin_portal")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Logged In Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentUser.isAdmin) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (currentUser.isAdmin) Color(0xFF1565C0) else FarmGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentUser.isAdmin) Icons.Default.Shield else Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser.fullName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (currentUser.isAdmin) Color(0xFF1976D2) else Color(0xFF388E3C))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = currentUser.displayRoleLabel,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = if (currentUser.isAdmin) "Personnel ID: ${currentUser.badgeOrPersonnelId}" else "RSBSA: ${currentUser.rsbsaNumber} • ${currentUser.farmAreaHectares} Ha",
                                fontSize = 11.sp,
                                color = Color(0xFF616161)
                            )
                        }

                        if (currentUser.isAdmin) {
                            Button(
                                onClick = {
                                    onOpenAdminDashboard()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_enter_admin_dash_quick")
                            ) {
                                Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> FarmerLoginTab(
                        onLogin = { id, pass ->
                            val res = onFarmerLogin(id, pass)
                            if (res.isSuccess) {
                                onDismiss()
                            }
                            res
                        }
                    )
                    1 -> FarmerRegisterTab(
                        onRegister = { name, phone, rsbsa, prov, mun, area, crop ->
                            val res = onFarmerRegister(name, phone, rsbsa, prov, mun, area, crop)
                            if (res.isSuccess) {
                                onDismiss()
                            }
                            res
                        }
                    )
                    2 -> AdminPersonnelLoginTab(
                        currentUser = currentUser,
                        onAdminLogin = { id, pass ->
                            val res = onAdminLogin(id, pass)
                            if (res.isSuccess) {
                                onOpenAdminDashboard()
                                onDismiss()
                            }
                            res
                        },
                        onOpenAdminDashboard = {
                            onOpenAdminDashboard()
                            onDismiss()
                        },
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun modalFarmerInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF2A1E17),
    unfocusedTextColor = Color(0xFF2A1E17),
    disabledTextColor = Color(0xFF424242),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFFFFFFF),
    focusedLabelColor = Color(0xFF3E2723),
    unfocusedLabelColor = Color(0xFF4E342E),
    focusedPlaceholderColor = Color(0xFF757575),
    unfocusedPlaceholderColor = Color(0xFF757575),
    focusedBorderColor = FarmBrownPrimary,
    unfocusedBorderColor = Color(0xFF8D6E63),
    cursorColor = Color(0xFF3E2723),
    focusedLeadingIconColor = FarmBrownPrimary,
    unfocusedLeadingIconColor = Color(0xFF5D4037),
    focusedTrailingIconColor = FarmBrownPrimary,
    unfocusedTrailingIconColor = Color(0xFF5D4037)
)

@Composable
private fun modalAdminInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0D47A1),
    unfocusedTextColor = Color(0xFF1E211D),
    disabledTextColor = Color(0xFF424242),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFFFFFFF),
    focusedLabelColor = Color(0xFF1565C0),
    unfocusedLabelColor = Color(0xFF37474F),
    focusedPlaceholderColor = Color(0xFF757575),
    unfocusedPlaceholderColor = Color(0xFF757575),
    focusedBorderColor = Color(0xFF1565C0),
    unfocusedBorderColor = Color(0xFF90CAF9),
    cursorColor = Color(0xFF0D47A1),
    focusedLeadingIconColor = Color(0xFF1565C0),
    unfocusedLeadingIconColor = Color(0xFF455A64),
    focusedTrailingIconColor = Color(0xFF1565C0),
    unfocusedTrailingIconColor = Color(0xFF757575)
)

@Composable
private fun FarmerLoginTab(
    onLogin: (identifier: String, passcode: String) -> Result<UserAccount>
) {
    var identifier by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mag-login bilang Magsasaka (Farmer Account)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = FarmGreenDark
        )
        Text(
            text = "I-enter ang iyong Mobile Number o RSBSA ID upang ma-access ang iyong farm profile at records.",
            fontSize = 12.sp,
            color = Color(0xFF616161),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = {
                identifier = it
                errorMessage = null
            },
            label = { Text("Mobile Number / RSBSA ID", fontSize = 13.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
            placeholder = { Text("hal. 0917-123-4567 o 03-49-12-00421", fontSize = 12.sp, color = Color(0xFF8D6E63)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FarmGreenPrimary) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_farmer_identifier"),
            colors = modalFarmerInputColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = passcode,
            onValueChange = {
                passcode = it
                errorMessage = null
            },
            label = { Text("PIN / Passcode", fontSize = 13.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
            placeholder = { Text("hal. 123456", fontSize = 12.sp, color = Color(0xFF8D6E63)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FarmGreenPrimary) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = FarmGreenPrimary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_farmer_password"),
            colors = modalFarmerInputColors()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚠️ $errorMessage",
                color = Color(0xFFD32F2F),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val res = onLogin(identifier.ifBlank { "0917-123-4567" }, passcode.ifBlank { "farmer123" })
                if (res.isFailure) {
                    errorMessage = res.exceptionOrNull()?.message ?: "Login failed"
                } else {
                    successMessage = "Maligayang pagdating, ${res.getOrNull()?.fullName}!"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_submit_farmer_login"),
            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Login sa Farm Account", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Demo Accounts Chips
        Text(
            text = "⚡ Subukan ang mga Demo Registered Farmers:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF757575)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    identifier = "0917-123-4567"
                    passcode = "farmer123"
                    onLogin("0917-123-4567", "farmer123")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_quick_demo_juan"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👨‍🌾 Juan Dela Cruz", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenDark)
                    Text("2.4 Ha • Rice", fontSize = 9.sp, color = Color(0xFF616161))
                }
            }

            OutlinedButton(
                onClick = {
                    identifier = "0918-987-6543"
                    passcode = "farmer123"
                    onLogin("0918-987-6543", "farmer123")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_quick_demo_pedro"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌾 Pedro Mangahas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenDark)
                    Text("3.1 Ha • Hybrid", fontSize = 9.sp, color = Color(0xFF616161))
                }
            }
        }
    }
}

@Composable
private fun FarmerRegisterTab(
    onRegister: (name: String, phone: String, rsbsa: String, prov: String, mun: String, area: Double, crop: String) -> Result<UserAccount>
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var rsbsaNumber by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("Nueva Ecija") }
    var municipality by remember { mutableStateOf("Science City of Muñoz") }
    var farmAreaText by remember { mutableStateOf("1.5") }
    var cropVariety by remember { mutableStateOf("NSIC Rc 222 (Tubigan 21)") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mag-rehistro ng Bagong Magsasaka",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = FarmGreenDark
        )
        Text(
            text = "I-save ang iyong datos para sa opisyal na DA-PhilRice nutrient advisory at GPS land records.",
            fontSize = 12.sp,
            color = Color(0xFF616161)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; errorMessage = null },
            label = { Text("Pangalan ng Magsasaka (Full Name)", fontSize = 12.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
            placeholder = { Text("hal. Juanita Mendoza", fontSize = 12.sp, color = Color(0xFF8D6E63)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FarmGreenPrimary) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_name"),
            colors = modalFarmerInputColors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; errorMessage = null },
                label = { Text("Mobile Phone", fontSize = 12.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
                placeholder = { Text("0917-xxx-xxxx", fontSize = 11.sp, color = Color(0xFF8D6E63)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FarmGreenPrimary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_reg_phone"),
                colors = modalFarmerInputColors()
            )

            OutlinedTextField(
                value = rsbsaNumber,
                onValueChange = { rsbsaNumber = it },
                label = { Text("RSBSA ID (Kung Mayroon)", fontSize = 11.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
                placeholder = { Text("03-49-12-...", fontSize = 11.sp, color = Color(0xFF8D6E63)) },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = FarmGreenPrimary) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_reg_rsbsa"),
                colors = modalFarmerInputColors()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = municipality,
                onValueChange = { municipality = it },
                label = { Text("Bayan / Munisipyo", fontSize = 12.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = FarmGreenPrimary) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("input_reg_mun"),
                colors = modalFarmerInputColors()
            )

            OutlinedTextField(
                value = farmAreaText,
                onValueChange = { farmAreaText = it },
                label = { Text("Laki ng Bukid (Ha)", fontSize = 11.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .weight(0.8f)
                    .testTag("input_reg_area"),
                colors = modalFarmerInputColors()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cropVariety,
            onValueChange = { cropVariety = it },
            label = { Text("Uri ng Palay / Barayti", fontSize = 12.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.Medium) },
            leadingIcon = { Icon(Icons.Default.Agriculture, contentDescription = null, tint = FarmGreenPrimary) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = FarmTextDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_crop"),
            colors = modalFarmerInputColors()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text("⚠️ $errorMessage", color = Color(0xFFD32F2F), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val area = farmAreaText.toDoubleOrNull() ?: 1.0
                val res = onRegister(fullName, phone, rsbsaNumber, province, municipality, area, cropVariety)
                if (res.isFailure) {
                    errorMessage = res.exceptionOrNull()?.message ?: "Registration failed"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_submit_farmer_reg"),
            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("I-rehistro ang Aking Account", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AdminPersonnelLoginTab(
    currentUser: UserAccount,
    onAdminLogin: (identifier: String, passcode: String) -> Result<UserAccount>,
    onOpenAdminDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    var personnelIdOrEmail by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1565C0).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Authorized DA / PhilRice Personnel Only",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1)
            )
        }

        Text(
            text = "Restricted administrative portal. Only accredited Municipal Agricultural Officers (MAO), PhilRice Extension Specialists, and Regional Directors can access the Agronomy Dashboard.",
            fontSize = 11.sp,
            color = Color(0xFF455A64),
            lineHeight = 15.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (currentUser.isAdmin) {
            // Already logged in as Admin
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Authenticated Personnel Active",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF01579B)
                        )
                    }
                    Text(
                        text = "${currentUser.fullName} • ${currentUser.badgeOrPersonnelId}",
                        fontSize = 12.sp,
                        color = Color(0xFF0277BD),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = currentUser.agency,
                        fontSize = 11.sp,
                        color = Color(0xFF546E7A)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenAdminDashboard,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_go_to_admin_dash")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Dashboard", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.testTag("btn_admin_logout")
                        ) {
                            Text("Switch to Farmer", fontSize = 12.sp, color = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        } else {
            // Admin Credentials Input
            OutlinedTextField(
                value = personnelIdOrEmail,
                onValueChange = { personnelIdOrEmail = it; errorMessage = null },
                label = { Text("Personnel Badge ID o Gov Email", fontSize = 12.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Medium) },
                placeholder = { Text("DA-MAO-2026-09 o admin@da.gov.ph", fontSize = 11.sp, color = Color(0xFF78909C)) },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF1565C0)) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0D47A1), fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_admin_personnel_id"),
                colors = modalAdminInputColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passcode,
                onValueChange = { passcode = it; errorMessage = null },
                label = { Text("Security Access Passcode", fontSize = 12.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Medium) },
                placeholder = { Text("I-enter ang admin passcode", fontSize = 11.sp, color = Color(0xFF78909C)) },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF1565C0)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF1565C0)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0D47A1), fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_admin_password"),
                colors = modalAdminInputColors()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "⛔ $errorMessage",
                        color = Color(0xFFC62828),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    isVerifying = true
                    val res = onAdminLogin(personnelIdOrEmail, passcode)
                    isVerifying = false
                    if (res.isFailure) {
                        errorMessage = res.exceptionOrNull()?.message ?: "Access Denied"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_admin_login"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Verify & Enter Admin Dashboard", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Whitelist Credentials for Testing
            Text(
                text = "🔑 Specific Accredited Personnel Whitelist (Demo Test Keys):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF455A64)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PersonnelCredentialCard(
                    name = "Engr. Roberto Santos",
                    badge = "DA-MAO-2026-09",
                    designation = "Municipal Agricultural Officer",
                    email = "admin@da.gov.ph",
                    passcode = "admin123",
                    onSelect = {
                        personnelIdOrEmail = "DA-MAO-2026-09"
                        passcode = "admin123"
                        onAdminLogin("DA-MAO-2026-09", "admin123")
                    }
                )

                PersonnelCredentialCard(
                    name = "Dr. Maria Elena Corpuz",
                    badge = "PHILRICE-TECH-404",
                    designation = "PhilRice Senior Agronomist",
                    email = "corpuz.m@philrice.gov.ph",
                    passcode = "palay2026",
                    onSelect = {
                        personnelIdOrEmail = "PHILRICE-TECH-404"
                        passcode = "palay2026"
                        onAdminLogin("PHILRICE-TECH-404", "palay2026")
                    }
                )
            }
        }
    }
}

@Composable
private fun PersonnelCredentialCard(
    name: String,
    badge: String,
    designation: String,
    email: String,
    passcode: String,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFB0BEC5), RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(8.dp),
        color = Color(0xFFF8F9FA)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )
                Text(
                    text = "$designation • Badge: $badge",
                    fontSize = 10.sp,
                    color = Color(0xFF546E7A)
                )
                Text(
                    text = "Pass: $passcode",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1565C0)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1565C0))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Auto Fill & Enter", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
