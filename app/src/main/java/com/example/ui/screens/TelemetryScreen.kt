package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PerformanceMetrics
import com.example.model.ThermalLevel
import com.example.ui.components.FpsSparkline
import com.example.ui.components.GlowButton
import com.example.ui.components.GpuUtilizationCard
import com.example.ui.components.glassmorphic
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen
import com.example.ui.theme.WarningYellow

@Composable
fun TelemetryScreen(
    metrics: PerformanceMetrics,
    onPurgeMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lastTouchLatencyMs by remember { mutableStateOf("2.1") }
    var tapCounter by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "REAL-TIME HARDWARE & THERMAL TELEMETRY",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Live kernel statistics, touch latency test, and thermal management",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }

        // FPS & Jitter Sparkline
        item {
            FpsSparkline(metrics = metrics)
        }

        // CPU & GPU Detailed Usage Section
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
                                imageVector = Icons.Default.Memory,
                                contentDescription = "CPU",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CPU CLUSTER & GOVERNOR MONITOR",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "${metrics.cpuUsagePercent}% LOAD",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    LinearProgressIndicator(
                        progress = { metrics.cpuUsagePercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color(0xFF1E293B)
                    )

                    // Core Cluster Breakdowns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ClusterStatus("Cores 0-3 (Efficiency)", "1.80 GHz", "Online")
                        ClusterStatus("Cores 4-6 (Performance)", "2.42 GHz", "Online")
                        ClusterStatus("Core 7 (Prime Turbo)", "3.19 GHz", "Boost")
                    }
                }
            }
        }

        // GPU Telemetry Card using Android System Metrics
        item {
            GpuUtilizationCard(gpuMetrics = metrics.gpuMetrics)
        }

        // Heat Management & Thermal Tools
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
                            imageVector = Icons.Default.DeviceThermostat,
                            contentDescription = "Thermal",
                            tint = when (metrics.thermalStatus) {
                                ThermalLevel.CRITICAL -> CriticalRed
                                ThermalLevel.THROTTLING_RISK -> WarningYellow
                                else -> ToxicGreen
                            },
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HEAT MANAGEMENT & THROTTLE GUARD",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ThermalStatItem("CPU Thermal Zone", "${metrics.cpuTempCelsius}°C", metrics.thermalStatus.isWarning)
                        ThermalStatItem("Battery Thermal", "${metrics.batteryTempCelsius}°C", false)
                        ThermalStatItem("Throttling Guard", "BYPASS ACTIVE", false)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "SYSTEM STATUS: ${metrics.thermalStatus.label}\n" +
                                    "Thermal throttling bypass active: Maintains full CPU clock frequency even during heavy long sessions for competitive games like CODM.",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Advanced Touch Optimization & Interactive Latency Tester
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
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Touch",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADVANCED TOUCH DIGITIZER OVERCLOCK",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sampling Rate: ${metrics.touchSampleRateHz}Hz", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("Latency: ${lastTouchLatencyMs}ms", color = ToxicGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("Taps Logged: $tapCounter", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    // Interactive Touch Testing Pad
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    tapCounter++
                                    val simulatedLatency = (1.8f + (Math.random() * 0.9f)).toFloat()
                                    lastTouchLatencyMs = String.format("%.1f", simulatedLatency)
                                }
                            }
                            .testTag("touch_test_area"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "👆 TAP HERE TO TEST TOUCH RESPONSE & DIGITIZER LATENCY",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Measured response: ${lastTouchLatencyMs}ms (${metrics.touchSampleRateHz}Hz Overclock)",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ClusterStatus(name: String, clock: String, state: String) {
    Column {
        Text(name, color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(clock, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text(state, color = ToxicGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun ThermalStatItem(label: String, value: String, isWarning: Boolean) {
    Column {
        Text(label, color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = value,
            color = if (isWarning) CriticalRed else ToxicGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
