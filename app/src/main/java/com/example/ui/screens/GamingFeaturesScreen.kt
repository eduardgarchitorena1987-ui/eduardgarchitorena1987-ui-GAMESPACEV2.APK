package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TweakSettings
import com.example.ui.components.GlowButton
import com.example.ui.components.glassmorphic
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen

@Composable
fun GamingFeaturesScreen(
    tweaks: TweakSettings,
    onUpdateTweaks: ((TweakSettings) -> TweakSettings) -> Unit,
    onTestHaptic: () -> Unit,
    onPurgeMemory: () -> Unit,
    onCalibrateGyro: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "20 ADVANCED GAMING & OVERCLOCK SUITES",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "High-precision kernel & userspace acceleration engine for competitive gaming",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }

        // 1. CPU/GPU Turbo Overclock
        item {
            FeatureSection(
                number = "01",
                title = "CPU/GPU TURBO OVERCLOCK",
                icon = Icons.Default.Bolt,
                color = MaterialTheme.colorScheme.primary,
                checked = tweaks.cpuGpuOverclockEnabled,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(cpuGpuOverclockEnabled = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Scaling Governor (Octa-Core Cluster):", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("performance", "schedutil", "powersave").forEach { gov ->
                            ChipButton(gov.uppercase(), tweaks.cpuGovernor == gov, MaterialTheme.colorScheme.primary) {
                                onUpdateTweaks { it.copy(cpuGovernor = gov) }
                            }
                        }
                    }
                    SubToggle("GPU Adreno/Mali Clock Max MHz Lock", tweaks.gpuBoostEnabled) { v -> onUpdateTweaks { it.copy(gpuBoostEnabled = v) } }
                    SubToggle("Core Unparking (Force All 8 Cores Online)", tweaks.coreUnparkingAllOnline) { v -> onUpdateTweaks { it.copy(coreUnparkingAllOnline = v) } }
                }
            }
        }

        // 2. RAM & Cache Purge
        item {
            FeatureSection(
                number = "02",
                title = "RAM & CACHE PURGE (DROP CACHES)",
                icon = Icons.Default.CleaningServices,
                color = Color(0xFFC084FC),
                checked = tweaks.ramAutoPurge,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(ramAutoPurge = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Flushes dirty page cache, dentries, and memory buffer.", color = Color.LightGray, fontSize = 11.sp)
                    SubToggle("Auto-drop caches on game boot", tweaks.dropCachesOnLaunch) { v -> onUpdateTweaks { it.copy(dropCachesOnLaunch = v) } }
                    GlowButton("EXECUTE DROP CACHES NOW", onPurgeMemory, Modifier.fillMaxWidth(), glowColor = Color(0xFFC084FC), testTag = "features_purge_memory_button")
                }
            }
        }

        // 3. Touch Response & Sampling Rate
        item {
            FeatureSection(
                number = "03",
                title = "TOUCH SAMPLING RATE OVERCLOCK",
                icon = Icons.Default.TouchApp,
                color = NeonCyan,
                checked = tweaks.touchResponseOverclock,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(touchResponseOverclock = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Digitizer Polling Frequency:", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(120, 240, 360, 480).forEach { hz ->
                            ChipButton("${hz}Hz", tweaks.touchSamplingRateHz == hz, NeonCyan) {
                                onUpdateTweaks { it.copy(touchSamplingRateHz = hz) }
                            }
                        }
                    }
                    Text("Sensitivity Multiplier: ${String.format("%.2f", tweaks.touchSensitivityMultiplier)}x", color = Color.LightGray, fontSize = 11.sp)
                    Slider(
                        value = tweaks.touchSensitivityMultiplier,
                        onValueChange = { v -> onUpdateTweaks { it.copy(touchSensitivityMultiplier = v) } },
                        valueRange = 1.0f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )
                    SubToggle("Zero Input Lag Buffer Mode", tweaks.zeroInputLagBuffer) { v -> onUpdateTweaks { it.copy(zeroInputLagBuffer = v) } }
                }
            }
        }

        // 4. Heat Management & Throttling Bypass
        item {
            FeatureSection(
                number = "04",
                title = "HEAT MANAGEMENT & THROTTLING BYPASS",
                icon = Icons.Default.DeviceThermostat,
                color = ToxicGreen,
                checked = tweaks.heatManagementActive,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(heatManagementActive = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cooling Profile:", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Extreme Chill", "Balanced", "Aggressive").forEach { profile ->
                            ChipButton(profile, tweaks.coolingProfile == profile, ToxicGreen) {
                                onUpdateTweaks { it.copy(coolingProfile = profile) }
                            }
                        }
                    }
                    SubToggle("Bypass Kernel Thermal Throttling Drops", tweaks.autoThrottlingBypass) { v -> onUpdateTweaks { it.copy(autoThrottlingBypass = v) } }
                }
            }
        }

        // 5. CEDSII AI Latency Optimizer
        item {
            FeatureSection(
                number = "05",
                title = "CEDSII AI REAL-TIME LATENCY ENGINE",
                icon = Icons.Default.NetworkCheck,
                color = MaterialTheme.colorScheme.primary,
                checked = tweaks.cedsiiAiLatencyEngine,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(cedsiiAiLatencyEngine = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Gaming Low-Latency DNS Route:", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Cloudflare 1.1.1.1 Gaming", "Google 8.8.8.8").forEach { dns ->
                            ChipButton(dns.split(" ").first(), tweaks.gameDnsMode.startsWith(dns.split(" ").first()), MaterialTheme.colorScheme.primary) {
                                onUpdateTweaks { it.copy(gameDnsMode = dns) }
                            }
                        }
                    }
                    SubToggle("QoS Packet Priority (DSCP 0x2E EF)", tweaks.packetPriorityBoost) { v -> onUpdateTweaks { it.copy(packetPriorityBoost = v) } }
                }
            }
        }

        // 6. DND & Focus Shield
        item {
            FeatureSection(
                number = "06",
                title = "DND & GAME FOCUS SHIELD",
                icon = Icons.Default.NotificationsOff,
                color = Color(0xFFFF0055),
                checked = tweaks.dndGameFocusShield,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(dndGameFocusShield = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubToggle("Block Heads-Up Notification Popups", tweaks.blockHeadsUp) { v -> onUpdateTweaks { it.copy(blockHeadsUp = v) } }
                    SubToggle("Lock Screen Gestures (Prevent Swipe-to-Exit)", tweaks.lockScreenGestures) { v -> onUpdateTweaks { it.copy(lockScreenGestures = v) } }
                    SubToggle("Lock Display Brightness (Stop Auto-Dimming)", tweaks.lockBrightness) { v -> onUpdateTweaks { it.copy(lockBrightness = v) } }
                }
            }
        }

        // 7. Display Refresh Rate Forcer
        item {
            FeatureSection(
                number = "07",
                title = "DISPLAY REFRESH RATE & PACING FORCER",
                icon = Icons.Default.Speed,
                color = NeonCyan,
                checked = tweaks.framePacingStabilizer,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(framePacingStabilizer = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Display Mode Target:", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(60, 90, 120, 144).forEach { hz ->
                            ChipButton("${hz}Hz", tweaks.forceRefreshRateHz == hz, NeonCyan) {
                                onUpdateTweaks { it.copy(forceRefreshRateHz = hz) }
                            }
                        }
                    }
                }
            }
        }

        // 8. Tactical Crosshair HUD Overlay
        item {
            FeatureSection(
                number = "08",
                title = "TACTICAL CROSSHAIR HUD OVERLAY",
                icon = Icons.Default.FilterCenterFocus,
                color = ToxicGreen,
                checked = tweaks.crosshairOverlayEnabled,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(crosshairOverlayEnabled = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Reticle Pattern for Hipfire & Snipers:", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Tactical Cross", "Tactical Dot", "Circle Dot", "Chevron").forEach { style ->
                            ChipButton(style, tweaks.crosshairStyle == style, ToxicGreen) {
                                onUpdateTweaks { it.copy(crosshairStyle = style) }
                            }
                        }
                    }
                }
            }
        }

        // 9. 3D Spatial Audio Booster
        item {
            FeatureSection(
                number = "09",
                title = "3D SPATIAL AUDIO & FOOTSTEP BOOSTER",
                icon = Icons.Default.GraphicEq,
                color = Color(0xFFFFB703),
                checked = tweaks.spatialAudioFootstepBooster,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(spatialAudioFootstepBooster = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Acoustic Footstep Frequency Preset:", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("CODM Footsteps (2.8kHz)", "Gunshot Clarity").forEach { eq ->
                            ChipButton(eq.split(" ").first(), tweaks.footstepEqPreset.startsWith(eq.split(" ").first()), Color(0xFFFFB703)) {
                                onUpdateTweaks { it.copy(footstepEqPreset = eq) }
                            }
                        }
                    }
                }
            }
        }

        // 10. 4D Haptic Feedback
        item {
            FeatureSection(
                number = "10",
                title = "4D HAPTIC ENGINE & TACTILE PULSE",
                icon = Icons.Default.Vibration,
                color = MaterialTheme.colorScheme.primary,
                checked = tweaks.fourDHapticEngine,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(fourDHapticEngine = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Haptic Intensity: ${(tweaks.hapticIntensity * 100).toInt()}%", color = Color.LightGray, fontSize = 11.sp)
                    Slider(
                        value = tweaks.hapticIntensity,
                        onValueChange = { v -> onUpdateTweaks { it.copy(hapticIntensity = v) } },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                    GlowButton("TEST HAPTIC BURST", onTestHaptic, Modifier.fillMaxWidth(), glowColor = MaterialTheme.colorScheme.primary, testTag = "test_haptic_button")
                }
            }
        }

        // --- NEW V2 FEATURES 11-20 ---

        // 11. Gyroscope Zero-Drift & Sensor Calibration
        item {
            FeatureSection(
                number = "11",
                title = "GYROSCOPE ZERO-DRIFT & CALIBRATION",
                icon = Icons.Default.ScreenRotation,
                color = NeonCyan,
                checked = tweaks.gyroZeroDriftStabilizer,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(gyroZeroDriftStabilizer = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Calibrates IMU sensor offsets and removes aim drift in CODM/PUBG.", color = Color.LightGray, fontSize = 11.sp)
                    Text("Gyro Multiplier: ${String.format("%.1f", tweaks.gyroSensitivityMultiplier)}x", color = Color.LightGray, fontSize = 11.sp)
                    Slider(
                        value = tweaks.gyroSensitivityMultiplier,
                        onValueChange = { v -> onUpdateTweaks { it.copy(gyroSensitivityMultiplier = v) } },
                        valueRange = 1.0f..3.0f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )
                    GlowButton("CALIBRATE SENSOR ZERO-POINT", onCalibrateGyro, Modifier.fillMaxWidth(), glowColor = NeonCyan, testTag = "feature_gyro_calibrate_button")
                }
            }
        }

        // 12. Anti-Stutter Dynamic VSync Lock
        item {
            FeatureSection(
                number = "12",
                title = "ANTI-STUTTER DYNAMIC VSYNC LOCK",
                icon = Icons.Default.Speed,
                color = ToxicGreen,
                checked = tweaks.antiStutterVSyncLock,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(antiStutterVSyncLock = v) } }
            ) {
                Text("Aligns Android SurfaceView buffers with display VSync pulses to eliminate micro-stutters and screen tearing during rapid turns.", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        // 13. Battery Bypass & Direct Power Simulation
        item {
            FeatureSection(
                number = "13",
                title = "BATTERY BYPASS & DIRECT POWER MODE",
                icon = Icons.Default.BatteryChargingFull,
                color = Color(0xFFFFB703),
                checked = tweaks.batteryBypassDirectPower,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(batteryBypassDirectPower = v) } }
            ) {
                Text("Simulates bypass charging when plugged into power: powers CPU/GPU directly, preventing battery overheating during marathon gaming.", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        // 14. Custom Game Resolution Scaling
        item {
            FeatureSection(
                number = "14",
                title = "GAME RESOLUTION & GRAPHICS SCALER",
                icon = Icons.Default.Visibility,
                color = MaterialTheme.colorScheme.secondary,
                checked = true,
                onCheckedChange = {}
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Render Target Resolution:", color = Color.LightGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("720p Turbo", "900p Balance", "1080p Ultra", "1440p Max").forEach { res ->
                            ChipButton(res, tweaks.gameResolutionScale.startsWith(res.split(" ").first()), MaterialTheme.colorScheme.secondary) {
                                onUpdateTweaks { it.copy(gameResolutionScale = res) }
                            }
                        }
                    }
                }
            }
        }

        // 15. Screen Edge Mistouch Filter (Claw Grip Deadzone)
        item {
            FeatureSection(
                number = "15",
                title = "PALM & CLAW-GRIP MISTOUCH FILTER",
                icon = Icons.Default.TouchApp,
                color = NeonCyan,
                checked = tweaks.screenEdgeMistouchFilter,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(screenEdgeMistouchFilter = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Curved Screen Edge Deadzone: ${tweaks.mistouchDeadzonePx}px", color = Color.LightGray, fontSize = 11.sp)
                    Slider(
                        value = tweaks.mistouchDeadzonePx.toFloat(),
                        onValueChange = { v -> onUpdateTweaks { it.copy(mistouchDeadzonePx = v.toInt()) } },
                        valueRange = 8f..48f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )
                }
            }
        }

        // 16. Voice Comms Background Noise Canceler
        item {
            FeatureSection(
                number = "16",
                title = "VOICE COMMS NOISE CANCELER",
                icon = Icons.Default.Mic,
                color = Color(0xFFC084FC),
                checked = tweaks.voiceCommsNoiseCanceler,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(voiceCommsNoiseCanceler = v) } }
            ) {
                Text("Filters out keyboard clicks, heavy breathing, and ambient fan noise from in-game microphone party chat.", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        // 17. Instant Highlight Replay Buffer
        item {
            FeatureSection(
                number = "17",
                title = "INSTANT HIGHLIGHT REPLAY BUFFER",
                icon = Icons.Default.Videocam,
                color = Color(0xFFFF0055),
                checked = tweaks.instantHighlightReplayBuffer,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(instantHighlightReplayBuffer = v) } }
            ) {
                Text("Keeps a continuous rolling 30-second low-overhead memory buffer to save clutches and aces without draining battery.", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        // 18. Dual-Channel Network Boost (Wi-Fi + Mobile Multipath)
        item {
            FeatureSection(
                number = "18",
                title = "DUAL-CHANNEL MULTIPATH BOOST",
                icon = Icons.Default.Wifi,
                color = ToxicGreen,
                checked = tweaks.dualChannelNetworkBoost,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(dualChannelNetworkBoost = v) } }
            ) {
                Text("Binds Wi-Fi and 5G cellular simultaneously: zero packet loss failover prevents ping spikes when someone uses Wi-Fi.", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        // 19. Tactical Black Equalizer / Dark Area Gamma Booster
        item {
            FeatureSection(
                number = "19",
                title = "BLACK EQUALIZER (DARK AREA VISION)",
                icon = Icons.Default.Visibility,
                color = Color(0xFFFFB703),
                checked = tweaks.blackEqualizerGammaBooster,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(blackEqualizerGammaBooster = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Brightens shadows in dark building corners without overexposing sunny areas. Spot campers instantly.", color = Color.LightGray, fontSize = 11.sp)
                    Text("Shadow Boost: ${String.format("%.2f", tweaks.blackEqualizerIntensity)}x", color = Color.LightGray, fontSize = 11.sp)
                    Slider(
                        value = tweaks.blackEqualizerIntensity,
                        onValueChange = { v -> onUpdateTweaks { it.copy(blackEqualizerIntensity = v) } },
                        valueRange = 1.0f..1.8f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFFFB703), activeTrackColor = Color(0xFFFFB703))
                    )
                }
            }
        }

        // 20. Rapid Fire Auto-Tap / Trigger Assist
        item {
            FeatureSection(
                number = "20",
                title = "RAPID-FIRE BURST TRIGGER ASSIST",
                icon = Icons.Default.Speed,
                color = MaterialTheme.colorScheme.primary,
                checked = tweaks.rapidFireAssist,
                onCheckedChange = { v -> onUpdateTweaks { it.copy(rapidFireAssist = v) } }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Digital trigger burst rate: ${tweaks.rapidFireCps} Clicks Per Second (CPS)", color = Color.LightGray, fontSize = 11.sp)
                    Slider(
                        value = tweaks.rapidFireCps.toFloat(),
                        onValueChange = { v -> onUpdateTweaks { it.copy(rapidFireCps = v.toInt()) } },
                        valueRange = 6f..24f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun FeatureSection(
    number: String,
    title: String,
    icon: ImageVector,
    color: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(shape = RoundedCornerShape(16.dp), borderColor = if (checked) color.copy(alpha = 0.5f) else Color.DarkGray.copy(alpha = 0.3f))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#$number ",
                                color = color,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = title,
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = if (checked) "ACTIVE & TUNED" else "DISABLED",
                            color = if (checked) ToxicGreen else Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = color,
                        checkedTrackColor = color.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("switch_feature_$number")
                )
            }

            if (checked) {
                Spacer(modifier = Modifier.height(10.dp))
                content()
            }
        }
    }
}

@Composable
fun SubToggle(
    label: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChecked(!checked) }
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White, fontSize = 11.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ToxicGreen,
                checkedTrackColor = ToxicGreen.copy(alpha = 0.3f)
            ),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun ChipButton(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Text(
        text = label,
        color = if (selected) Color.Black else Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) color else Color(0xFF1E293B))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    )
}

