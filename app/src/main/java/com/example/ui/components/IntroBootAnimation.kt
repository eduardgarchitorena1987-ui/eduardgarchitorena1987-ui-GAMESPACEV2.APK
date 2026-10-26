package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen
import kotlinx.coroutines.delay

@Composable
fun IntroBootAnimation(
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.7f) }
    val progress = remember { Animatable(0f) }
    var logStep by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    val logs = listOf(
        ">> INITIALIZING CEDSII SPACE KERNEL v2.0...",
        ">> HARDWARE DETECTED: 8-CORE OCTA-BURST ENGINE",
        ">> DIGITIZER OVERCLOCK: 480HZ ZERO INPUT LAG",
        ">> CEDSII AI LATENCY OPTIMIZER: SYNCED (18ms)",
        ">> FLOATING GAMING ORB: ACTIVE & READY",
        ">> CODM WARFIGHTER 120 FPS ENGINE: ENGAGED"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(1.05f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        scale.animateTo(1.0f, animationSpec = tween(300))
    }

    LaunchedEffect(Unit) {
        for (i in 0..5) {
            logStep = i
            com.example.audio.GamingAudioFx.playOrbClickSound()
            delay(380)
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(2400, easing = LinearEasing))
        delay(300)
        onAnimationFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050811))
            .testTag("intro_boot_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Cyber Grid & Scanline Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Animated laser scanline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.TopCenter)
                .padding(top = (400 * scanY).dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary,
                            ToxicGreen,
                            Color.Transparent
                        )
                    )
                )
        )

        // Center Cyber Orb & Logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale.value)
        ) {
            // Glowing circular avatar with banner graphic
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .neonGlow(MaterialTheme.colorScheme.primary, radius = 22.dp, shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cedsii_icon),
                    contentDescription = "CEDSII Boot Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CEDSII SPACE v2.0",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )

            Text(
                text = "UNIVERSAL GAMING SPACE • REAL-TIME PERFORMANCE ENGINE",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Diagnostic Terminal Ticker
            Box(
                modifier = Modifier
                    .width(420.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SYSTEM BOOT TELEMETRY",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${(progress.value * 100).toInt()}%",
                            color = ToxicGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = logs[logStep.coerceIn(0, logs.size - 1)],
                        color = ToxicGreen,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glowing Progress Line
            LinearProgressIndicator(
                progress = { progress.value },
                modifier = Modifier
                    .width(360.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFF1E293B)
            )
        }

        // Top-Right Skip Button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable { onAnimationFinished() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SKIP",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Skip",
                    tint = Color.LightGray,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
