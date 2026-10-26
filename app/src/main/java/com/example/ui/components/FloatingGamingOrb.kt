package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.DoNotTouch
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GamingAudioFx
import com.example.model.GameProfile
import com.example.model.PerformanceMetrics
import com.example.model.TweakSettings
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.WarningYellow
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Realistic 3D Glassmorphic Floating Gaming Orb with:
 * 1. Left-Side Hide / Unhide Dock Tab (collapses into sleek edge handle)
 * 2. Real-Time FPS monitor badge & rotating reactor core
 * 3. 5 Best Gaming Features hero section
 * 4. Realistic glass reflections and specular highlights
 * 5. Interactive sound effects and tactical audio
 */
@Composable
fun FloatingGamingOrb(
    tweaks: TweakSettings,
    onUpdateTweaks: ((TweakSettings) -> TweakSettings) -> Unit,
    onPurgeMemory: () -> Unit,
    onCalibrateGyro: () -> Unit,
    metrics: PerformanceMetrics = PerformanceMetrics(),
    onAiPingBoost: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isDockedLeft by remember { mutableStateOf(false) }
    var isMiniHudMode by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableIntStateOf(0) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "orbAnimations")

    // Breathing glow animation
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    // Continuous 360 degree reactor ring rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reactorRotation"
    )

    // Docked tab pulse indicator
    val tabPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tabPulse"
    )

    val fpsColor = when {
        metrics.fps >= 90 -> ToxicGreen
        metrics.fps >= 55 -> NeonCyan
        metrics.fps >= 40 -> WarningYellow
        else -> CriticalRed
    }

    Box(
        modifier = modifier
            .offset { IntOffset(if (isDockedLeft) 0 else offsetX.roundToInt(), offsetY.roundToInt()) }
    ) {
        if (isMiniHudMode) {
            // ================= MINI HUD DRAGGABLE PILL =================
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.88f))
                    .border(1.2.dp, ToxicGreen, RoundedCornerShape(8.dp))
                    .testTag("in_game_mini_hud")
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .clickable {
                        GamingAudioFx.playOrbClickSound()
                        isMiniHudMode = false
                        isExpanded = true
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ToxicGreen))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${metrics.fps} FPS",
                        color = ToxicGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GPU ${metrics.gpuMetrics.utilizationPercent}%",
                        color = NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${metrics.frameTimeMs}ms",
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else if (isDockedLeft) {
            // ================= 1. DOCKED TO LEFT SIDE: MINIMAL HIDE/UNHIDE TAB =================
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(72.dp)
                    .testTag("bubble_left_dock_handle")
                    .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF0F172A).copy(alpha = 0.95f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = tabPulse),
                        RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                    )
                    .clickable {
                        GamingAudioFx.playOrbClickSound()
                        isDockedLeft = false
                        isExpanded = true
                    }
                    .padding(vertical = 8.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pulsing vertical neon strip
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(ToxicGreen.copy(alpha = tabPulse))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Unhide Bubble",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        } else if (!isExpanded) {
            // ================= 2. REALISTIC 3D GLASSMORPHIC GAMING BUBBLE =================
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .testTag("floating_gaming_orb_button")
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            // Auto-dock when dragged towards the left edge
                            if (offsetX < -240f) {
                                isDockedLeft = true
                                offsetX = 0f
                            }
                        }
                    }
                    .clickable {
                        GamingAudioFx.playOrbClickSound()
                        isExpanded = true
                    },
                contentAlignment = Alignment.Center
            ) {
                // Outer Holographic Aura
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .neonGlow(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 16.dp,
                            alpha = pulseGlow,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF020617)
                                )
                            )
                        )
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape)
                )

                val orbPrimaryColor = MaterialTheme.colorScheme.primary

                // Canvas: Rotating segmented reactor ring & 3D Glass Specular Reflection
                Canvas(modifier = Modifier.size(58.dp)) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Draw 8 Rotating Reactor Ticks
                    val tickCount = 8
                    for (i in 0 until tickCount) {
                        val angleDeg = rotationAngle + (i * (360f / tickCount))
                        val angleRad = Math.toRadians(angleDeg.toDouble())
                        val startR = radius * 0.78f
                        val endR = radius * 0.94f

                        val startX = center.x + (startR * cos(angleRad)).toFloat()
                        val startY = center.y + (startR * sin(angleRad)).toFloat()
                        val endX = center.x + (endR * cos(angleRad)).toFloat()
                        val endY = center.y + (endR * sin(angleRad)).toFloat()

                        drawLine(
                            color = if (i % 2 == 0) NeonCyan.copy(alpha = 0.8f) else ToxicGreen.copy(alpha = 0.6f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // 3D Glass Specular Highlight Arc (Top-Left glare)
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.55f),
                                Color.Transparent
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(size.width * 0.5f, size.height * 0.5f)
                        ),
                        startAngle = 190f,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = Offset(radius * 0.18f, radius * 0.18f),
                        size = Size(radius * 1.64f, radius * 1.64f),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Bottom-right refraction rim
                    drawArc(
                        color = orbPrimaryColor.copy(alpha = 0.35f),
                        startAngle = 30f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(radius * 0.15f, radius * 0.15f),
                        size = Size(radius * 1.7f, radius * 1.7f),
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Center Real-Time FPS HUD Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${metrics.fps}",
                        color = fpsColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "FPS",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            // ================= 3. EXPANDED: IN-GAME FLOATING GAMING CONSOLE =================
            Box(
                modifier = Modifier
                    .width(380.dp)
                    .testTag("in_game_floating_panel")
                    .glassmorphic(
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color(0xFF080D1A).copy(alpha = 0.94f),
                        borderColor = MaterialTheme.colorScheme.primary
                    )
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Header Bar with Drag handle, FPS Telemetry, Dock Left & Close
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(fpsColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "FPS",
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "CEDSII GAME BOOSTER",
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "REAL-TIME: ${metrics.fps} FPS (${metrics.frameTimeMs}ms) • GPU ${metrics.gpuMetrics.utilizationPercent}% • ${metrics.cpuTempCelsius}°C",
                                    color = fpsColor,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Mini HUD Toggle Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ToxicGreen.copy(alpha = 0.2f))
                                    .border(1.dp, ToxicGreen.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        GamingAudioFx.playOrbClickSound()
                                        isExpanded = false
                                        isMiniHudMode = true
                                    }
                                    .padding(horizontal = 5.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = "Mini HUD",
                                        tint = ToxicGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "MINI HUD",
                                        color = ToxicGreen,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            // Dock to Left Side Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable {
                                        GamingAudioFx.playOrbClickSound()
                                        isExpanded = false
                                        isDockedLeft = true
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Dock Left",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "HIDE LEFT",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    GamingAudioFx.playOrbClickSound()
                                    isExpanded = false
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }

                    // Category Selector Tabs: 5 BEST FEATURES vs PERF & GPU vs COMBAT vs VAULT vs SYSTEM
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val tabs = listOf("⭐ 5 BEST", "⚡ PERF & GPU", "⚔️ COMBAT", "🎮 VAULT", "🛡️ SYSTEM")
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedCategory == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        GamingAudioFx.playOrbClickSound()
                                        selectedCategory = index
                                    }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Feature List Scroll Container
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 230.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        when (selectedCategory) {
                            0 -> {
                                // ================= THE 5 BEST FEATURES FOR BUBBLE =================
                                Text(
                                    text = "⭐ TOP 5 KILLER BUBBLE SHORTCUTS",
                                    color = ToxicGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )

                                // BEST FEATURE 1: RAM Turbo Rocket / Purge
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF131D33))
                                        .border(1.dp, Color(0xFFC084FC).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFC084FC).copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CleaningServices,
                                                contentDescription = "RAM Purge",
                                                tint = Color(0xFFC084FC),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "1. RAM Turbo Flush",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "Reclaim 1.4GB • Drop Kernel Caches",
                                                color = Color(0xFFC084FC),
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }

                                    GlowButton(
                                        text = "BOOST",
                                        onClick = {
                                            GamingAudioFx.playTurboBoostSound()
                                            onPurgeMemory()
                                        },
                                        glowColor = Color(0xFFC084FC),
                                        testTag = "bubble_best_ram_purge",
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                // BEST FEATURE 2: Tactical Crosshair HUD Overlay
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF131D33))
                                        .border(1.dp, ToxicGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(ToxicGreen.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FilterCenterFocus,
                                                contentDescription = "Crosshair",
                                                tint = ToxicGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "2. Tactical Crosshair HUD",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "Style: ${tweaks.crosshairStyle} (Tap to cycle)",
                                                color = ToxicGreen,
                                                fontSize = 8.5.sp,
                                                modifier = Modifier.clickable {
                                                    GamingAudioFx.playOrbClickSound()
                                                    val styles = listOf("Tactical Cross", "Tactical Dot", "Circle Dot", "Chevron")
                                                    val nextIdx = (styles.indexOf(tweaks.crosshairStyle) + 1) % styles.size
                                                    onUpdateTweaks { it.copy(crosshairStyle = styles[nextIdx]) }
                                                }
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = tweaks.crosshairOverlayEnabled,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks { it.copy(crosshairOverlayEnabled = v) }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = ToxicGreen),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                // BEST FEATURE 3: 480Hz Touch Sampling Overclock
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF131D33))
                                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(NeonCyan.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.TouchApp,
                                                contentDescription = "Touch",
                                                tint = NeonCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "3. 480Hz Touch Digitizer",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "Zero Touch Delay • Precision Aim",
                                                color = NeonCyan,
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = tweaks.touchResponseOverclock,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks {
                                                it.copy(
                                                    touchResponseOverclock = v,
                                                    touchSamplingRateHz = if (v) 480 else 240
                                                )
                                            }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                // BEST FEATURE 4: Black Equalizer (Night Sight Gamma)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF131D33))
                                        .border(1.dp, Color(0xFFFACC15).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFACC15).copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = "Black Equalizer",
                                                tint = Color(0xFFFACC15),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "4. Black Equalizer Gamma",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "See Enemies In Dark Corners (1.35x)",
                                                color = Color(0xFFFACC15),
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = tweaks.blackEqualizerGammaBooster,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks { it.copy(blackEqualizerGammaBooster = v) }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFACC15)),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                // BEST FEATURE 5: Anti-Mistouch & DND Shield
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF131D33))
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.NotificationsOff,
                                                contentDescription = "Shield",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "5. DND & Anti-Mistouch",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "Block Notification Banners & Edge Taps",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = tweaks.dndGameFocusShield,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks {
                                                it.copy(
                                                    dndGameFocusShield = v,
                                                    blockHeadsUp = v,
                                                    screenEdgeMistouchFilter = v
                                                )
                                            }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            1 -> {
                                // ================= ⚡ PERF & GPU =================
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.55f))
                                        .border(1.dp, ToxicGreen.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.DeveloperBoard,
                                                contentDescription = "GPU",
                                                tint = ToxicGreen,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "GPU ACCELERATOR",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Text(
                                            text = "${metrics.gpuMetrics.utilizationPercent}% LOAD",
                                            color = ToxicGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // GPU Utilization Bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFF1E293B))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fraction = (metrics.gpuMetrics.utilizationPercent.coerceIn(0, 100) / 100f))
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(NeonCyan, ToxicGreen, Color(0xFFFACC15))
                                                    )
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Clock: ${metrics.gpuMetrics.frequencyMhz} MHz • VRAM: ${metrics.gpuMetrics.memoryUsedMb} MB",
                                            color = Color.LightGray,
                                            fontSize = 8.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "Frame: ${metrics.frameTimeMs}ms",
                                            color = NeonCyan,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                // Refresh Rate Selector
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Hz", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Force Display Refresh", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        listOf(60, 90, 120, 144).forEach { hz ->
                                            val isSel = tweaks.forceRefreshRateHz == hz
                                            Text(
                                                text = "$hz",
                                                color = if (isSel) Color.Black else Color.White,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color(0xFF1E293B))
                                                    .clickable {
                                                        GamingAudioFx.playOrbClickSound()
                                                        onUpdateTweaks { it.copy(forceRefreshRateHz = hz) }
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                // Anti-Stutter Dynamic VSync Lock
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Tune, contentDescription = "VSync", tint = ToxicGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Dynamic VSync Lock", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("Zero Tear • Cadence Align", color = Color.Gray, fontSize = 8.sp)
                                        }
                                    }
                                    Switch(
                                        checked = tweaks.antiStutterVSyncLock,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks { it.copy(antiStutterVSyncLock = v) }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = ToxicGreen),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                // Battery Bypass Power
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Power, contentDescription = "Bypass", tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Battery Bypass Power", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("Direct Wall Power • 0% Heat", color = Color.Gray, fontSize = 8.sp)
                                        }
                                    }
                                    Switch(
                                        checked = tweaks.batteryBypassDirectPower,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks { it.copy(batteryBypassDirectPower = v) }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8)),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            2 -> {
                                // ================= ⚔️ COMBAT SUITE =================
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "KERNEL PROFILE: ${tweaks.activeProfile.label}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        GameProfile.values().forEach { profile ->
                                            val isSelected = tweaks.activeProfile == profile
                                            Text(
                                                text = profile.name.split("_").first(),
                                                color = if (isSelected) Color.Black else Color.White,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isSelected) ToxicGreen else Color(0xFF1E293B))
                                                    .clickable {
                                                        GamingAudioFx.playOrbClickSound()
                                                        onUpdateTweaks {
                                                            it.copy(
                                                                activeProfile = profile,
                                                                forceRefreshRateHz = profile.targetFps,
                                                                touchSamplingRateHz = profile.touchHz
                                                            )
                                                        }
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                // Gyro Zero Drift
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ScreenRotation,
                                            contentDescription = "Gyro",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Gyro Zero-Drift Fix", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("200Hz Jitter Smoother", color = ToxicGreen, fontSize = 8.sp)
                                        }
                                    }

                                    GlowButton(
                                        text = "CALIBRATE",
                                        onClick = {
                                            GamingAudioFx.playOrbClickSound()
                                            onCalibrateGyro()
                                        },
                                        glowColor = NeonCyan,
                                        testTag = "bubble_calibrate_gyro",
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                // Rapid Fire
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Whatshot,
                                            contentDescription = "Rapid Fire",
                                            tint = Color(0xFFFF5722),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Rapid-Fire Trigger Macro", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("14 CPS Auto Burst", color = Color.Gray, fontSize = 8.sp)
                                        }
                                    }

                                    Switch(
                                        checked = tweaks.rapidFireAssist,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks { it.copy(rapidFireAssist = v) }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF5722)),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            3 -> {
                                // ================= 🎮 QUICK VAULT LAUNCHER =================
                                val popularVaultGames = listOf(
                                    Pair("Call of Duty: Mobile (Garena)", "com.garena.game.codm"),
                                    Pair("Call of Duty: Mobile (Global)", "com.activision.callofduty.shooter"),
                                    Pair("PUBG Mobile", "com.tencent.ig"),
                                    Pair("Mobile Legends: Bang Bang", "com.mobile.legends"),
                                    Pair("Free Fire MAX", "com.dts.freefiremax"),
                                    Pair("Genshin Impact", "com.miHoYo.GenshinImpact")
                                )

                                Text(
                                    text = "QUICK VAULT LAUNCHER",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )

                                popularVaultGames.forEach { (name, pkg) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.45f))
                                            .border(0.8.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Icon(
                                                imageVector = Icons.Default.SportsEsports,
                                                contentDescription = name,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = name,
                                                    color = Color.White,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = pkg,
                                                    color = Color.Gray,
                                                    fontSize = 7.5.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        GlowButton(
                                            text = "LAUNCH",
                                            onClick = {
                                                GamingAudioFx.playGameSpaceIntroSound()
                                                try {
                                                    val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                                                    if (intent != null) {
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        context.startActivity(intent)
                                                    } else {
                                                        val playStore = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$pkg"))
                                                        playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        context.startActivity(playStore)
                                                    }
                                                } catch (_: Exception) {}
                                                isExpanded = false
                                            },
                                            glowColor = ToxicGreen,
                                            testTag = "bubble_launch_$pkg",
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            4 -> {
                                // ================= 🛡️ SYSTEM & NETWORK =================
                                // AI Ping Engine
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.NetworkCheck,
                                            contentDescription = "AI Ping",
                                            tint = ToxicGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("CEDSII AI Ping Engine", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("Traffic Priority • ${metrics.pingMs}ms", color = ToxicGreen, fontSize = 8.sp)
                                        }
                                    }

                                    GlowButton(
                                        text = "BOOST",
                                        onClick = {
                                            GamingAudioFx.playTurboBoostSound()
                                            onAiPingBoost?.invoke()
                                        },
                                        glowColor = ToxicGreen,
                                        testTag = "bubble_ai_ping_boost",
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                // 4D Haptic
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Haptics",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("4D Haptic Feedback", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("Weapon Recoil Simulation", color = Color.Gray, fontSize = 8.sp)
                                        }
                                    }

                                    Switch(
                                        checked = tweaks.fourDHapticEngine,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks { it.copy(fourDHapticEngine = v) }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                // Edge Mistouch
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DoNotTouch,
                                            contentDescription = "Anti-Mistouch",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Edge Mistouch Deadzone", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("12px Palm Rejection Filter", color = Color.Gray, fontSize = 8.sp)
                                        }
                                    }

                                    Switch(
                                        checked = tweaks.screenEdgeMistouchFilter,
                                        onCheckedChange = { v ->
                                            GamingAudioFx.playToggleSound(v)
                                            onUpdateTweaks { it.copy(screenEdgeMistouchFilter = v) }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
