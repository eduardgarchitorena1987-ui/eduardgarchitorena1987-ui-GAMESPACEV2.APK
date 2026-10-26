package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun TacticalCrosshairOverlay(
    isVisible: Boolean,
    style: String = "Tactical Cross",
    colorHex: Long = 0xFF00FF88,
    sizeDp: Float = 18f,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val crosshairColor = Color(colorHex)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((sizeDp * 2.2f).dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val lineLen = sizeDp * 1.1f
            val gap = sizeDp * 0.35f
            val strokeW = 2.2f

            when (style) {
                "Tactical Dot" -> {
                    drawCircle(color = crosshairColor, radius = 4f, center = center)
                    drawCircle(color = Color.Black, radius = 5.5f, center = center, style = Stroke(1.2f))
                }
                "Circle Dot" -> {
                    drawCircle(color = crosshairColor, radius = 3.5f, center = center)
                    drawCircle(color = crosshairColor, radius = lineLen * 0.7f, center = center, style = Stroke(strokeW))
                    drawCircle(color = Color.Black, radius = lineLen * 0.7f + 1f, center = center, style = Stroke(1f))
                }
                "Chevron" -> {
                    // Top inverted chevron
                    val topY = center.y - gap
                    drawLine(crosshairColor, Offset(center.x - lineLen * 0.6f, topY - lineLen * 0.6f), Offset(center.x, topY), strokeW)
                    drawLine(crosshairColor, Offset(center.x + lineLen * 0.6f, topY - lineLen * 0.6f), Offset(center.x, topY), strokeW)
                    drawCircle(color = crosshairColor, radius = 2.5f, center = center)
                }
                else -> {
                    // Tactical Cross with center gap & outline
                    // Top line
                    drawLine(crosshairColor, Offset(center.x, center.y - gap - lineLen), Offset(center.x, center.y - gap), strokeW)
                    // Bottom line
                    drawLine(crosshairColor, Offset(center.x, center.y + gap), Offset(center.x, center.y + gap + lineLen), strokeW)
                    // Left line
                    drawLine(crosshairColor, Offset(center.x - gap - lineLen, center.y), Offset(center.x - gap, center.y), strokeW)
                    // Right line
                    drawLine(crosshairColor, Offset(center.x + gap, center.y), Offset(center.x + gap + lineLen, center.y), strokeW)
                    // Center dot
                    drawCircle(color = crosshairColor, radius = 2.5f, center = center)
                }
            }
        }
    }
}
