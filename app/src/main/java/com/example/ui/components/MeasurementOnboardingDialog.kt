package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MeasurementOnboardingDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 2

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FarmGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Help Guide",
                        tint = FarmGreenHeader,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    val dialogTitle = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Measurement Guide"
                        AppLanguage.TAGALOG -> "Gabay sa Pagsukat"
                        AppLanguage.TAGLISH -> "Measurement Help & Guide"
                        AppLanguage.ILOCANO -> "Giya ti Panagrukod"
                        AppLanguage.CEBUANO -> "Giya sa Pagsukod"
                    }
                    Text(
                        text = dialogTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text(
                        text = "Step ${step + 1} of $totalSteps",
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Step Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    repeat(totalSteps) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (step == index) 24.dp else 8.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (step == index) FarmGreenPrimary else FarmBorder)
                        )
                    }
                }

                // Step Content
                AnimatedContent(
                    targetState = step,
                    transitionSpec = { fadeIn() with fadeOut() },
                    label = "stepAnimation"
                ) { targetStep ->
                    when (targetStep) {
                        0 -> OnboardingStepCard(
                            icon = Icons.Default.Map,
                            title = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "GPS Boundary Tool"
                                AppLanguage.TAGALOG -> "Pagsukat gamit ang GPS"
                                AppLanguage.TAGLISH -> "GPS Boundary Measurement"
                                AppLanguage.ILOCANO -> "Panagrukod babaen ti GPS"
                                AppLanguage.CEBUANO -> "Pagsukod gamit ang GPS"
                            },
                            items = when (currentLanguage) {
                                AppLanguage.ENGLISH -> listOf(
                                    "Tap 'Start' to turn on high-accuracy GPS tracking.",
                                    "Walk along the edge or boundary of your rice field.",
                                    "Click 'Mark Point' at every corner of your plot.",
                                    "You can also tap directly on the map to add or refine points."
                                )
                                AppLanguage.TAGALOG -> listOf(
                                    "Pindutin ang 'Start' para simulan ang GPS tracking.",
                                    "Lakaran ang gilid o hangganan ng iyong palayan.",
                                    "Pindutin ang 'Mark Point' sa bawat sulok ng bukid.",
                                    "Pwede ring i-tap ang mapa para magdagdag ng punto."
                                )
                                AppLanguage.TAGLISH -> listOf(
                                    "Tap 'Start' to activate GPS tracking.",
                                    "Walk along the field boundary edges.",
                                    "Press 'Mark Point' at each farm corner.",
                                    "You can tap the interactive map directly to adjust points."
                                )
                                AppLanguage.ILOCANO -> listOf(
                                    "Pinduten ti 'Start' tapno magun-od ti GPS tracking.",
                                    "Magnaka iti igid ti talon.",
                                    "Pinduten ti 'Mark Point' iti tunggal patingga ti talon.",
                                    "Mabalin pay a i-tap ti mapa para iti tuldek."
                                )
                                AppLanguage.CEBUANO -> listOf(
                                    "Pindota ang 'Start' aron masugdan ang GPS tracking.",
                                    "Lakaw subay sa kilid o utlanan sa imong humayan.",
                                    "Pindota ang 'Mark Point' sa matag sulok sa yuta.",
                                    "Makahimo ka usab sa pag-tap direkta sa mapa."
                                )
                            }
                        )
                        else -> OnboardingStepCard(
                            icon = Icons.Default.CameraAlt,
                            title = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Camera Land Survey View"
                                AppLanguage.TAGALOG -> "Pagsusuri gamit ang Camera"
                                AppLanguage.TAGLISH -> "Camera Survey Interface"
                                AppLanguage.ILOCANO -> "Panagrukod babaen ti Kamera"
                                AppLanguage.CEBUANO -> "Pagsusi gamit ang Kamera"
                            },
                            items = when (currentLanguage) {
                                AppLanguage.ENGLISH -> listOf(
                                    "Hold your phone up to see the live camera viewfinder.",
                                    "Visually align land corners and observe boundary lines.",
                                    "Check the point count badge in the top right as you walk.",
                                    "When complete, click 'Finish' to save field area in hectares."
                                )
                                AppLanguage.TAGALOG -> listOf(
                                    "Hawakan ang telepono upang makita ang live camera view.",
                                    "Gamitin ang camera para sa tamang pagtingin sa sulok ng bukid.",
                                    "Tignan ang bilang ng punto sa itaas habang naglalakad.",
                                    "Pag natapos, pindutin ang 'Finish' para mai-save ang resulta."
                                )
                                AppLanguage.TAGLISH -> listOf(
                                    "Hold phone up to use the camera overlay.",
                                    "Visually inspect corners while taking boundary measurements.",
                                    "Monitor point count badge in the upper right.",
                                    "Click 'Finish' to save calculated hectares to farm history."
                                )
                                AppLanguage.ILOCANO -> listOf(
                                    "Iseggaay ti telepono para iti camera view.",
                                    "Kitaen ti patingga ti talon babaen ti kamera.",
                                    "Kitaen ti bilang ti tuldek iti ngato.",
                                    "Pinduten ti 'Finish' no nalpasen."
                                )
                                AppLanguage.CEBUANO -> listOf(
                                    "Gawaya ang telepono aron makita ang live camera view.",
                                    "Gamita ang kamera sa pagtan-aw sa mga sulok sa yuta.",
                                    "Bantayi ang ihap sa puntos sa ibabaw samtang naglakaw.",
                                    "Inag-human, pindota ang 'Finish' aron ma-save ang rekord."
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    TextButton(
                        onClick = { step-- },
                        modifier = Modifier.testTag("btn_onboarding_prev")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.ENGLISH -> "Back"
                                    AppLanguage.TAGALOG -> "Bumalik"
                                    AppLanguage.TAGLISH -> "Back"
                                    AppLanguage.ILOCANO -> "Agsubli"
                                    AppLanguage.CEBUANO -> "Balik"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (step < totalSteps - 1) {
                            step++
                        } else {
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FarmGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_onboarding_next")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val btnText = if (step < totalSteps - 1) {
                            when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Next"
                                AppLanguage.TAGALOG -> "Susunod"
                                AppLanguage.TAGLISH -> "Next"
                                AppLanguage.ILOCANO -> "Sumaruno"
                                AppLanguage.CEBUANO -> "Sunod"
                            }
                        } else {
                            when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Got it!"
                                AppLanguage.TAGALOG -> "Naintindihan!"
                                AppLanguage.TAGLISH -> "Got it! Start"
                                AppLanguage.ILOCANO -> "Ammokon!"
                                AppLanguage.CEBUANO -> "Nasabtan!"
                            }
                        }
                        Text(text = btnText, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (step < totalSteps - 1) Icons.Default.ChevronRight else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_onboarding_skip")
            ) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Skip"
                        AppLanguage.TAGALOG -> "Laktawan"
                        AppLanguage.TAGLISH -> "Skip"
                        AppLanguage.ILOCANO -> "Laktawan"
                        AppLanguage.CEBUANO -> "Laktawan"
                    },
                    color = FarmTextSecondary
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    )
}

@Composable
private fun OnboardingStepCard(
    icon: ImageVector,
    title: String,
    items: List<String>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FarmGreenContainer.copy(alpha = 0.5f))
            .border(1.dp, FarmBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenHeader
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            items.forEach { bullet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp, end = 8.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(FarmGreenPrimary)
                    )
                    Text(
                        text = bullet,
                        fontSize = 13.sp,
                        color = FarmTextDark,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
