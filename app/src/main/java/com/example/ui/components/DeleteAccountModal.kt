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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun DeleteAccountModal(
    isVisible: Boolean,
    currentLanguage: AppLanguage,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning Red Icon Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FarmRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Account Warning",
                        tint = FarmRed,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                val titleText = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Delete Your Account?"
                    AppLanguage.TAGALOG -> "Burahin ang Iyong Account?"
                    AppLanguage.TAGLISH -> "Gusto mo bang i-Delete ang Account mo?"
                    AppLanguage.ILOCANO -> "Buraen ti Account mo?"
                    AppLanguage.CEBUANO -> "I-delete ang Imong Account?"
                }

                Text(
                    text = titleText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val bodyText = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Are you sure you want to delete your account? This action is permanent and will completely erase all your saved farm records, measurement history, fertilizer plans, and settings."
                    AppLanguage.TAGALOG -> "Sigurado ka bang gusto mong burahin ang iyong account? Ang aksyong ito ay permanente at ganap na magbubura ng lahat ng iyong na-save na bukid, sukat, plano sa pataba, at setting."
                    AppLanguage.TAGLISH -> "Are you sure na gusto mong i-delete ang account mo? Permanently mabubura ang lahat ng saved farm records, measurement history, at preferences mo."
                    AppLanguage.ILOCANO -> "Pasingkedan a buraen ti account mo? Maintudloy a mabura ti ammin a datos ti talon ken sukat."
                    AppLanguage.CEBUANO -> "Sigurado ka ba nga gusto nimong i-delete ang imong account? Permanente kini ug papasit tanang rekord sa imong yuta, sukat, ug setting."
                }

                Text(
                    text = bodyText,
                    fontSize = 14.sp,
                    color = FarmTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Irreversible Warning Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FarmRedLight.copy(alpha = 0.6f))
                        .border(1.dp, FarmRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = FarmRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val warningNote = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Warning: This action cannot be undone."
                            AppLanguage.TAGALOG -> "Babala: Hindi na ito mababawi kailanman."
                            AppLanguage.TAGLISH -> "Warning: This action cannot be undone."
                            AppLanguage.ILOCANO -> "Pakaammo: Saan a mabawi daytoy."
                            AppLanguage.CEBUANO -> "Pahidaan: Dili na kini mabawi."
                        }
                        Text(
                            text = warningNote,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FarmRed
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FarmRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("button_confirm_delete_account")
            ) {
                val confirmText = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Yes, Delete Account"
                    AppLanguage.TAGALOG -> "Oo, Burahin ang Account"
                    AppLanguage.TAGLISH -> "Yes, Delete Account"
                    AppLanguage.ILOCANO -> "Wen, Buraen ti Account"
                    AppLanguage.CEBUANO -> "Oo, I-delete ang Account"
                }
                Text(
                    text = confirmText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("button_cancel_delete_account")
            ) {
                val cancelText = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Cancel, Keep Account"
                    AppLanguage.TAGALOG -> "Kanselahin, Panatilihin"
                    AppLanguage.TAGLISH -> "Cancel, Huwag Muna"
                    AppLanguage.ILOCANO -> "Saan, Ibagkat"
                    AppLanguage.CEBUANO -> "Kanselahin, Ipanalipod"
                }
                Text(
                    text = cancelText,
                    fontSize = 15.sp,
                    color = FarmTextDark,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    )
}
