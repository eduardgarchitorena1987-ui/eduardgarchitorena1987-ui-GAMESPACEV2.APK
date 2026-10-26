package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameApp
import com.example.model.TweakSettings
import com.example.ui.components.GlowButton
import com.example.ui.components.glassmorphic
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen

@Composable
fun CodmSpecialScreen(
    tweaks: TweakSettings,
    onUpdateTweaks: ((TweakSettings) -> TweakSettings) -> Unit,
    onLaunchCodmGarena: () -> Unit,
    onLaunchCodmGlobal: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, ToxicGreen.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_cedsii),
                    contentDescription = "CODM Warfighter",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "CALL OF DUTY: MOBILE WARFIGHTER",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "120 FPS ULTRA • 480HZ DIGITIZER • GARENA & GLOBAL READY",
                        color = ToxicGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 120 FPS Ultra Framerate Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(shape = RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "FPS",
                                tint = ToxicGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "120 FPS ULTRA FRAMERATE UNLOCK",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        SubToggle(
                            label = "",
                            checked = tweaks.codmUltra120Fps,
                            onChecked = { v -> onUpdateTweaks { it.copy(codmUltra120Fps = v) } }
                        )
                    }
                    Text(
                        text = "Overrides manufacturer device model profile to unlock \"Ultra 120 FPS\" graphics preset in CODM MP and Battle Royale.",
                        color = Color.LightGray,
                        fontSize = 10.5.sp
                    )
                }
            }
        }

        // Gyro & Touch Sampling Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(shape = RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ScreenRotation,
                            contentDescription = "Gyro",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GYRO & TOUCH SAMPLING OVERCLOCK",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    SubToggle(
                        label = "2x Gyroscope Polling Rate (Instant 1:1 Aim)",
                        checked = tweaks.codmGyroSampleMultiplier,
                        onChecked = { v -> onUpdateTweaks { it.copy(codmGyroSampleMultiplier = v) } }
                    )

                    SubToggle(
                        label = "Recoil Micro-Jitter Suppression Filter",
                        checked = tweaks.codmRecoilStabilizer,
                        onChecked = { v -> onUpdateTweaks { it.copy(codmRecoilStabilizer = v) } }
                    )
                }
            }
        }

        // Windows & Mobile Cross-Platform Seamless Support Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(shape = RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = "Windows",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WINDOWS & PC EMULATOR COMPATIBILITY",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        SubToggle(
                            label = "",
                            checked = tweaks.windowsEmulationCompatibility,
                            onChecked = { v -> onUpdateTweaks { it.copy(windowsEmulationCompatibility = v) } }
                        )
                    }
                    Text(
                        text = "Optimizes for Windows Subsystem for Android (WSA), Gameloop, and PC emulators: enables raw mouse input polling, eliminates cursor deadzones, and stabilizes frame pacing across PC monitors.",
                        color = Color.LightGray,
                        fontSize = 10.5.sp
                    )
                }
            }
        }

        // Deploy & Launch Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlowButton(
                    text = "DEPLOY & LAUNCH GARENA",
                    onClick = onLaunchCodmGarena,
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Launch Garena",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    glowColor = Color(0xFFFB7185),
                    testTag = "deploy_codm_garena_button",
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
                )
                GlowButton(
                    text = "DEPLOY & LAUNCH GLOBAL",
                    onClick = onLaunchCodmGlobal,
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Gamepad,
                            contentDescription = "Launch Global",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    glowColor = ToxicGreen,
                    testTag = "deploy_codm_global_button",
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
