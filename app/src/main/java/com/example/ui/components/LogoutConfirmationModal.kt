package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmRed
import com.example.ui.theme.FarmRedLight
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary

@Composable
fun LogoutConfirmationModal(
    isVisible: Boolean,
    currentLanguage: AppLanguage,
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val title = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Log Out of Account?"
        AppLanguage.TAGALOG -> "Mag-logout sa Account?"
        AppLanguage.TAGLISH -> "Mag-log Out sa Account?"
        AppLanguage.ILOCANO -> "Rummuar iti Account?"
        AppLanguage.CEBUANO -> "Mo-logout sa Account?"
    }

    val message = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Are you sure you want to log out? You will need to sign in again to access your saved farm boundaries and nutrient recommendations."
        AppLanguage.TAGALOG -> "Sigurado ka bang nais mong mag-logout? Kakailanganin mong mag-sign in muli upang ma-access ang iyong mga rekord ng bukid at gabay sa pataba."
        AppLanguage.TAGLISH -> "Sure ka bang gusto mong mag-log out? Kailangan mong mag-login muli para ma-access ang farm records at fertilizer recommendations mo."
        AppLanguage.ILOCANO -> "Sigurado kadi a kayatmo ti rummuar? Masapulmo ti ag-sign in manen tapno ma-access dagiti rekord ti talon ken rekomendasyon iti abono."
        AppLanguage.CEBUANO -> "Sigurado ba ka nga gusto nimo mo-logout? Kinahanglan ka mo-sign in og balik aron ma-access ang mga rekord sa uma ug rekomendasyon sa abono."
    }

    val confirmText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Yes, Log Out"
        AppLanguage.TAGALOG -> "Oo, Mag-logout"
        AppLanguage.TAGLISH -> "Yes, Log Out"
        AppLanguage.ILOCANO -> "Wen, Rummuar"
        AppLanguage.CEBUANO -> "Oo, Mo-logout"
    }

    val cancelText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Cancel"
        AppLanguage.TAGALOG -> "Kanselahin"
        AppLanguage.TAGLISH -> "Cancel"
        AppLanguage.ILOCANO -> "Kanselaen"
        AppLanguage.CEBUANO -> "Kanselahon"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logout Icon Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FarmRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Log Out",
                        tint = FarmRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    fontSize = 13.5.sp,
                    color = FarmTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FarmBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_logout_modal_cancel"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FarmTextDark
                    )
                ) {
                    Text(
                        text = cancelText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        onDismiss()
                        onConfirmLogout()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_logout_modal_confirm")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = confirmText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = null,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        modifier = modifier
            .border(1.dp, FarmBorder, RoundedCornerShape(20.dp))
            .testTag("dialog_logout_confirmation")
    )
}
