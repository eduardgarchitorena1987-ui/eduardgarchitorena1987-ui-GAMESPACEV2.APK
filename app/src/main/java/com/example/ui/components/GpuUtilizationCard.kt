package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GpuLoadLevel
import com.example.model.GpuMetrics
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen

/**
 * Clean, high-tech Compose card displaying Android System Metrics GPU Utilization.
 */
@Composable
fun GpuUtilizationCard(
    gpuMetrics: GpuMetrics,
    modifier: Modifier = Modifier
) {
    val animatedPercent by animateFloatAsState(
        targetValue = gpuMetrics.utilizationPercent.toFloat(),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "gpuPercent"
    )

    val gaugeColor = when (gpuMetrics.loadLevel) {
        GpuLoadLevel.EXTREME -> Color(0xFFFF0055)
        GpuLoadLevel.HEAVY -> Color(0xFFFFB703)
        GpuLoadLevel.BALANCED -> ToxicGreen
        GpuLoadLevel.IDLE -> NeonCyan
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .glassmorphic(
                shape = RoundedCornerShape(18.dp),
                backgroundColor = Color(0xFF0A1020).copy(alpha = 0.90f),
                borderColor = gaugeColor.copy(alpha = 0.45f)
            )
            .padding(14.dp)
            .testTag("gpu_utilization_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row: Title, Icon & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(gaugeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperBoard,
                            contentDescription = "GPU",
                            tint = gaugeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "GPU HARDWARE ENGINE",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = gpuMetrics.rendererName,
                            color = Color.Gray,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(gaugeColor.copy(alpha = 0.2f))
                        .border(1.dp, gaugeColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = gpuMetrics.loadLevel.label,
                        color = gaugeColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Main Telemetry Row: Circular Gauge + Clock & Telemetry Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Progress Dial
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(72.dp)) {
                        val strokeWidth = 7.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                        // Background track
                        drawArc(
                            color = Color(0x331E293B),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress arc
                        val sweep = (animatedPercent / 100f) * 270f
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(NeonCyan, gaugeColor)
                            ),
                            startAngle = 135f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${gpuMetrics.utilizationPercent}%",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "LOAD",
                            color = gaugeColor,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Telemetry Stats Columns
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill(
                            label = "GPU CLOCK",
                            value = "${gpuMetrics.frequencyMhz} MHz",
                            accent = gaugeColor
                        )
                        StatPill(
                            label = "MAX CLOCK",
                            value = "${gpuMetrics.maxFrequencyMhz} MHz",
                            accent = Color.LightGray
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill(
                            label = "GPU TEMP",
                            value = "${gpuMetrics.temperatureCelsius}°C",
                            accent = if (gpuMetrics.temperatureCelsius >= 42f) Color(0xFFFFB703) else ToxicGreen
                        )
                        StatPill(
                            label = "VRAM USAGE",
                            value = "${gpuMetrics.memoryUsedMb} MB",
                            accent = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Real-Time Mini Sparkline Chart
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "REAL-TIME SYSTEM SHADER ACTIVITY",
                        color = Color.Gray,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "15-SAMPLE HISTOGRAM",
                        color = gaugeColor,
                        fontSize = 7.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF030712))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val points = gpuMetrics.history
                        if (points.size >= 2) {
                            val w = size.width
                            val h = size.height
                            val stepX = w / (points.size - 1)
                            val path = Path()

                            points.forEachIndexed { i, p ->
                                val x = i * stepX
                                val y = h - ((p / 100f) * (h - 4f)) - 2f
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }

                            drawPath(
                                path = path,
                                color = gaugeColor,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    accent: Color
) {
    Column {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 7.5.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
