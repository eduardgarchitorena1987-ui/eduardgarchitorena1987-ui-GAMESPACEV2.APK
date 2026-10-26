package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GamingAudioFx
import com.example.data.RootStatus
import com.example.model.GameApp
import com.example.model.PerformanceMetrics
import com.example.model.ThermalLevel
import com.example.ui.components.FpsSparkline
import com.example.ui.components.GlowButton
import com.example.ui.components.GpuUtilizationCard
import com.example.ui.components.LiveBannerHeader
import com.example.ui.components.glassmorphic
import com.example.ui.components.neonGlow
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.WarningYellow
import kotlin.math.cos
import kotlin.math.sin

/**
 * Realistic Cyberpunk Game Space with:
 * 1. Perspective Cyber Grid & Warp Particle background
 * 2. Realistic Cockpit Circular HUD Telemetry Dial for Real-Time FPS
 * 3. 5 Quick In-Game Boost Shortcuts
 * 4. Realistic 3D Glass Game Vault Launcher with sound synthesis triggers
 */
@Composable
fun HomeScreen(
    metrics: PerformanceMetrics,
    rootStatus: RootStatus,
    gameApps: List<GameApp>,
    isFloatingFpsActive: Boolean,
    onQuickBoost: () -> Unit,
    onOpenRootDialog: () -> Unit,
    onToggleFloatingFps: () -> Unit,
    onLaunchGame: (GameApp) -> Unit,
    onNavigateToApps: () -> Unit,
    onNavigateToFeatures: () -> Unit,
    onNavigateToAi: () -> Unit,
    onCalibrateGyro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gameSpaceMotion")

    val radarSweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarSweep"
    )

    val gridPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gridPulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Perspective Background Grid Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Horizontal perspective horizon lines
            for (i in 1..8) {
                val y = h * (0.35f + (i * i) / 80f)
                drawLine(
                    color = Color(0xFF0284C7).copy(alpha = (gridPulse * (i / 8f)).coerceIn(0.05f, 0.4f)),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Vanishing perspective vertical rays
            val vanishingPoint = Offset(w * 0.5f, h * 0.25f)
            val rayCount = 12
            for (i in 0..rayCount) {
                val bottomX = (w / rayCount) * i
                drawLine(
                    color = Color(0xFF0284C7).copy(alpha = gridPulse * 0.35f),
                    start = vanishingPoint,
                    end = Offset(bottomX, h),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ================= LEFT COLUMN: Cockpit Real-Time Telemetry & Top Actions =================
            LazyColumn(
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Live Gaming Banner
                item {
                    LiveBannerHeader(
                        metrics = metrics,
                        rootStatus = rootStatus,
                        onQuickBoost = {
                            GamingAudioFx.playTurboBoostSound()
                            onQuickBoost()
                        },
                        onOpenRootDialog = onOpenRootDialog
                    )
                }

                // Realistic Cockpit Gauge Row: Real-Time FPS Dial + Hardware Core Telemetry
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // REAL-TIME FPS COCKPIT DIAL
                        RealisticFpsCockpitDial(
                            metrics = metrics,
                            modifier = Modifier.weight(1f)
                        )

                        // LIVE THERMAL & MEMORY MATRIX
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TelemetryMiniCard(
                                title = "CPU / GOVERNOR",
                                value = "${metrics.cpuUsagePercent}%",
                                subtitle = "Gov: ${metrics.activeGovernor}",
                                color = MaterialTheme.colorScheme.primary,
                                icon = Icons.Default.Memory
                            )
                            TelemetryMiniCard(
                                title = "SOC THERMALS",
                                value = "${metrics.cpuTempCelsius}°C",
                                subtitle = metrics.thermalStatus.label,
                                color = when (metrics.thermalStatus) {
                                    ThermalLevel.CRITICAL -> CriticalRed
                                    ThermalLevel.THROTTLING_RISK -> WarningYellow
                                    else -> ToxicGreen
                                },
                                icon = Icons.Default.DeviceThermostat
                            )
                        }
                    }
                }

                // Android System Metrics GPU Utilization Card
                item {
                    GpuUtilizationCard(gpuMetrics = metrics.gpuMetrics)
                }

                // Real-Time Frame Pacing Sparkline Chart
                item {
                    FpsSparkline(metrics = metrics)
                }

                // Quick Action Bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlowButton(
                            text = "RAM FLUSH",
                            onClick = {
                                GamingAudioFx.playTurboBoostSound()
                                onQuickBoost()
                            },
                            modifier = Modifier.weight(1f),
                            glowColor = Color(0xFFC084FC),
                            testTag = "home_flush_ram_button",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = "Flush",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 9.dp)
                        )

                        GlowButton(
                            text = "FIX GYRO",
                            onClick = {
                                GamingAudioFx.playOrbClickSound()
                                onCalibrateGyro()
                            },
                            modifier = Modifier.weight(1f),
                            glowColor = NeonCyan,
                            testTag = "home_fix_gyro_button",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ScreenRotation,
                                    contentDescription = "Gyro",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 9.dp)
                        )

                        GlowButton(
                            text = "CEDSII AI",
                            onClick = {
                                GamingAudioFx.playOrbClickSound()
                                onNavigateToAi()
                            },
                            modifier = Modifier.weight(1f),
                            glowColor = MaterialTheme.colorScheme.primary,
                            testTag = "home_open_ai_button",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 9.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // ================= RIGHT COLUMN: Realistic Game Vault & Hardware Radar =================
            LazyColumn(
                modifier = Modifier
                    .weight(1.05f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Featured Game Vault Header & Carousel
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(ToxicGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GAME VAULT",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }

                            Text(
                                text = "EXPAND ALL >",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        GamingAudioFx.playOrbClickSound()
                                        onNavigateToApps()
                                    }
                                    .padding(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // High-Fidelity 3D Game Cards Carousel
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val pinned = gameApps.filter { it.isGame && it.isInVault }.ifEmpty { gameApps.filter { it.isGame } }.take(8)
                            items(pinned) { game ->
                                RealisticGameCard(
                                    game = game,
                                    onLaunch = {
                                        GamingAudioFx.playGameSpaceIntroSound()
                                        onLaunchGame(game)
                                    }
                                )
                            }
                        }
                    }
                }

                // Low-Latency Network Radar & Hardware Matrix
                item {
                    Column {
                        Text(
                            text = "REAL-TIME TELEMETRY MATRIX",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TelemetryCard(
                                title = "DIGITIZER",
                                value = "${metrics.touchSampleRateHz}Hz",
                                subtitle = "Hyper Response",
                                icon = Icons.Default.TouchApp,
                                color = NeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            TelemetryCard(
                                title = "PING / QOS",
                                value = "${metrics.pingMs}ms",
                                subtitle = "±${metrics.jitterMs}ms Jitter",
                                icon = Icons.Default.NetworkCheck,
                                color = ToxicGreen,
                                modifier = Modifier.weight(1f)
                            )
                            TelemetryCard(
                                title = "FREE RAM",
                                value = "${metrics.ramTotalMb - metrics.ramUsedMb}MB",
                                subtitle = "Kernel Cached",
                                icon = Icons.Default.CleaningServices,
                                color = Color(0xFFC084FC),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

/**
 * High-Tech Realistic Semi-Circular Cockpit Gauge for Real-Time FPS.
 */
@Composable
fun RealisticFpsCockpitDial(
    metrics: PerformanceMetrics,
    modifier: Modifier = Modifier
) {
    val fps = metrics.fps
    val maxFps = metrics.targetFps.coerceAtLeast(120)
    val sweepFraction = (fps.toFloat() / maxFps).coerceIn(0.2f, 1.0f)

    val dialColor = when {
        fps >= 90 -> ToxicGreen
        fps >= 55 -> NeonCyan
        fps >= 40 -> WarningYellow
        else -> CriticalRed
    }

    Box(
        modifier = modifier
            .glassmorphic(shape = RoundedCornerShape(16.dp), borderColor = dialColor.copy(alpha = 0.4f))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    val strokeW = 7.dp.toPx()

                    // Background Track Arc (240 degrees)
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = 150f,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = Offset(strokeW / 2, strokeW / 2),
                        size = Size(size.width - strokeW, size.height - strokeW),
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    // Active FPS Gradient Arc
                    val activeSweep = 240f * sweepFraction
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                NeonCyan,
                                ToxicGreen,
                                dialColor
                            )
                        ),
                        startAngle = 150f,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        topLeft = Offset(strokeW / 2, strokeW / 2),
                        size = Size(size.width - strokeW, size.height - strokeW),
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    // Dial Needle Indicator
                    val needleAngleDeg = 150f + activeSweep
                    val needleAngleRad = Math.toRadians(needleAngleDeg.toDouble())
                    val tipR = radius * 0.88f
                    val needleTip = Offset(
                        center.x + (tipR * cos(needleAngleRad)).toFloat(),
                        center.y + (tipR * sin(needleAngleRad)).toFloat()
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = needleTip
                    )
                }

                // Numeric readout inside dial
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$fps",
                        color = dialColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "REAL FPS",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "FRAME TIME: ${metrics.frameTimeMs}ms (1% Low: ${metrics.onePercentLow})",
                color = Color.Gray,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun TelemetryMiniCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .glassmorphic(shape = RoundedCornerShape(12.dp), borderColor = color.copy(alpha = 0.3f))
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, color = Color.Gray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                Text(text = subtitle, color = Color.LightGray.copy(alpha = 0.7f), fontSize = 7.5.sp, fontFamily = FontFamily.Monospace)
            }
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(14.dp))
            }
        }
    }
}

/**
 * Realistic 3D Game Card with Holographic Trim & Start Engine Button.
 */
@Composable
fun RealisticGameCard(
    game: GameApp,
    onLaunch: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(142.dp)
            .glassmorphic(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFF0F172A).copy(alpha = 0.85f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            )
            .padding(10.dp)
    ) {
        Column {
            // Icon & FPS Lock Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Game",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (game.packageName.contains("garena")) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE11D48).copy(alpha = 0.25f))
                            .border(1.dp, Color(0xFFE11D48), RoundedCornerShape(6.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "GARENA",
                            color = Color(0xFFFB7185),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ToxicGreen.copy(alpha = 0.2f))
                            .border(1.dp, ToxicGreen.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${game.targetFps} FPS",
                            color = ToxicGreen,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = game.name,
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )

            Text(
                text = "${game.category} • ${game.touchBoostHz}Hz",
                color = Color.Gray,
                fontSize = 8.5.sp,
                maxLines = 1,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Start Engine Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .clickable { onLaunch() }
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.Black,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "LAUNCH",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .glassmorphic(shape = RoundedCornerShape(12.dp), borderColor = color.copy(alpha = 0.35f))
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.Gray,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                color = Color.LightGray.copy(alpha = 0.7f),
                fontSize = 7.5.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}

