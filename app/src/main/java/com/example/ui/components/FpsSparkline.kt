package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PerformanceMetrics
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen

@Composable
fun FpsSparkline(
    metrics: PerformanceMetrics,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .glassmorphic()
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REAL-TIME FPS & FRAME TIME JITTER",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Hardware Choreographer Sync • 120Hz Target",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${metrics.fps}",
                        color = lineColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " FPS",
                        color = lineColor.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-time Canvas Graph
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                val data = metrics.recentFpsHistory
                if (data.size < 2) return@Canvas

                val minVal = 60f
                val maxVal = 144f
                val stepX = size.width / (data.size - 1)

                val points = data.mapIndexed { index, fps ->
                    val normalized = (fps - minVal) / (maxVal - minVal)
                    val y = size.height - (normalized.coerceIn(0f, 1f) * size.height)
                    Offset(index * stepX, y)
                }

                // Fill gradient path
                val fillPath = Path().apply {
                    moveTo(points.first().x, size.height)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, size.height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )

                // Stroke line
                val strokePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }

                drawPath(
                    path = strokePath,
                    color = lineColor,
                    style = Stroke(width = 2.5.dp.toPx())
                )

                // Draw current head dot
                points.lastOrNull()?.let { last ->
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = last
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 6.5.dp.toPx(),
                        center = last,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stat pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricChip("1% LOW", "${metrics.onePercentLow} FPS", ToxicGreen)
                MetricChip("FRAME TIME", "${metrics.frameTimeMs} ms", NeonCyan)
                MetricChip("JITTER", "±${metrics.jitterMs} ms", MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            color = Color.Gray,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
