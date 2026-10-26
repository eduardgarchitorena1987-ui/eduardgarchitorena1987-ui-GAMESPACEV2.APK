package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.RootStatus
import com.example.model.PerformanceMetrics
import com.example.model.ThermalLevel
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.WarningYellow

@Composable
fun LiveBannerHeader(
    metrics: PerformanceMetrics,
    rootStatus: RootStatus,
    onQuickBoost: () -> Unit,
    onOpenRootDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bannerScan")
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanlineY"
    )

    val alertColor by animateColorAsState(
        targetValue = when (metrics.thermalStatus) {
            ThermalLevel.CRITICAL -> CriticalRed
            ThermalLevel.THROTTLING_RISK -> WarningYellow
            ThermalLevel.WARM -> Color(0xFFFF9900)
            ThermalLevel.OPTIMAL -> ToxicGreen
        },
        label = "alertColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Main Live Banner Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            // Live Banner Image from user
            Image(
                painter = painterResource(id = R.drawable.banner_cedsii),
                contentDescription = "CEDSII SPACE Live Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Cyber Sci-Fi Gradient Vignette
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Animated glowing scanline overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = (175 * scanlineY).dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Top Status Badges Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Root Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(
                            1.dp,
                            if (rootStatus.isAccessGranted) ToxicGreen else CriticalRed,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onOpenRootDialog() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (rootStatus.isAccessGranted) ToxicGreen else CriticalRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (rootStatus.isRootDetected) "ROOT: SU #0" else if (rootStatus.isSimulatedMode) "ROOT: SANDBOX #0" else "ROOT: LOCKED",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Live FPS & Engine Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "FPS",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${metrics.fps} FPS • ${metrics.pingMs}ms",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Bottom Banner Overlay Content: App Branding & Turbo Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "CEDSII SPACE",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "KERNEL OVERCLOCK • 120HZ STABILIZER",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )
                }

                // 1-Tap Turbo Boost Glow Button
                GlowButton(
                    text = "TURBO BOOST",
                    onClick = onQuickBoost,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Boost",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    glowColor = MaterialTheme.colorScheme.primary,
                    testTag = "banner_turbo_boost_button",
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // System Alerts for Thermal Warnings and Auto-Throttling Notifications
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .border(
                    width = 1.dp,
                    color = alertColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (metrics.thermalStatus.isWarning) Icons.Default.Warning else Icons.Default.DeviceThermostat,
                contentDescription = "Thermal Alert",
                tint = alertColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (metrics.thermalStatus) {
                        ThermalLevel.CRITICAL -> "ALERT: CRITICAL HEAT (${metrics.cpuTempCelsius}°C) - BYPASS ACTIVE"
                        ThermalLevel.THROTTLING_RISK -> "THERMAL WARNING (${metrics.cpuTempCelsius}°C) - AUTO-COOLING ENGAGED"
                        ThermalLevel.WARM -> "THERMAL: WARM (${metrics.cpuTempCelsius}°C) - DYNAMIC HEAT SPREADING"
                        ThermalLevel.OPTIMAL -> "THERMAL: OPTIMAL (${metrics.cpuTempCelsius}°C) - FULL THROTTLE SUSTAINED"
                    },
                    color = alertColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (metrics.thermalStatus.isWarning)
                        "Kernel throttling prevented by CEDSII heat management. FPS locked."
                    else
                        "CPU governor locked at 'performance'. 0 frame drops detected.",
                    color = Color.LightGray.copy(alpha = 0.8f),
                    fontSize = 9.5.sp
                )
            }
        }
    }
}
