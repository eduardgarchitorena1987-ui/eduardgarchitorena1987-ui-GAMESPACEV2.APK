package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurface

fun Modifier.neonGlow(
    color: Color,
    radius: Dp = 8.dp,
    alpha: Float = 0.45f,
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier = this.drawBehind {
    val transparentColor = color.copy(alpha = 0f).toArgb()
    val shadowColor = color.copy(alpha = alpha).toArgb()
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            radius.toPx(),
            0f,
            0f,
            shadowColor
        )
        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            12.dp.toPx(),
            12.dp.toPx(),
            paint
        )
    }
}

fun Modifier.glassmorphic(
    shape: Shape = RoundedCornerShape(16.dp),
    borderColor: Color = GlassBorderSubtle,
    backgroundColor: Color = GlassSurface,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.55f),
                backgroundColor.copy(alpha = 0.25f)
            )
        ),
        shape = shape
    )
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                borderColor.copy(alpha = 0.6f),
                borderColor.copy(alpha = 0.15f),
                borderColor.copy(alpha = 0.45f)
            )
        ),
        shape = shape
    )

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    testTag: String = "glow_button",
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val shape = RoundedCornerShape(12.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 48.dp)
            .neonGlow(color = if (enabled) glowColor else Color.Transparent, radius = 10.dp, alpha = pulseAlpha)
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) listOf(
                        glowColor.copy(alpha = 0.28f),
                        glowColor.copy(alpha = 0.14f)
                    ) else listOf(Color.DarkGray.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.3f))
                ),
                shape = shape
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = if (enabled) listOf(
                        glowColor.copy(alpha = 0.9f),
                        glowColor.copy(alpha = 0.4f)
                    ) else listOf(Color.Gray.copy(alpha = 0.3f), Color.DarkGray.copy(alpha = 0.2f))
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = glowColor),
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.8.sp
            )
        }
    }
}
