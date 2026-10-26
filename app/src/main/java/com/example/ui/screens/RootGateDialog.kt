package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PermissionController
import com.example.data.PermissionState
import com.example.data.RootStatus
import com.example.ui.components.GlowButton
import com.example.ui.components.glassmorphic
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootGateDialog(
    rootStatus: RootStatus,
    permissionState: PermissionState,
    isFloatingServiceRunning: Boolean,
    onToggleFloatingService: () -> Unit,
    onToggleSimulatedRoot: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .glassmorphic(
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0xFF090E1B),
                    borderColor = MaterialTheme.colorScheme.primary
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ToxicGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = ToxicGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "PRIVILEGE & PERMISSION MANAGER",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Root, Shizuku, and System Overlay Authorization",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                // Status Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ROOT STATUS
                    PermissionBadgeCard(
                        title = "ROOT ACCESS",
                        subtitle = if (rootStatus.isRootDetected) "MAGISK / KSU" else if (permissionState.isSandboxBypassActive) "SANDBOX #UID 0" else "UNROOTED",
                        isGranted = rootStatus.isAccessGranted,
                        icon = Icons.Default.Terminal,
                        color = if (rootStatus.isAccessGranted) ToxicGreen else Color(0xFFFF9900),
                        modifier = Modifier.weight(1f)
                    )

                    // SHIZUKU STATUS
                    PermissionBadgeCard(
                        title = "SHIZUKU (ADB)",
                        subtitle = if (permissionState.isShizukuInstalled) "BINDER ACTIVE" else if (permissionState.isSandboxBypassActive) "ADB SIMULATED" else "NOT FOUND",
                        isGranted = permissionState.isShizukuAuthorized,
                        icon = Icons.Default.Security,
                        color = if (permissionState.isShizukuAuthorized) NeonCyan else Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    // OVERLAY STATUS
                    PermissionBadgeCard(
                        title = "OVERLAY PERM",
                        subtitle = if (permissionState.hasOverlayPermission) "GRANTED" else "ACTION NEEDED",
                        isGranted = permissionState.hasOverlayPermission,
                        icon = Icons.Default.Layers,
                        color = if (permissionState.hasOverlayPermission) ToxicGreen else CriticalRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                // IN-GAME FLOATING BUBBLE SERVICE TOGGLE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF131D30))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "In-Game Floating Bubble Service",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Keeps the 5-feature quick access bubble floating over other apps & games.",
                            color = Color.Gray,
                            fontSize = 9.5.sp
                        )
                    }
                    Switch(
                        checked = isFloatingServiceRunning,
                        onCheckedChange = { onToggleFloatingService() },
                        colors = SwitchDefaults.colors(checkedThumbColor = ToxicGreen),
                        modifier = Modifier.testTag("floating_service_switch")
                    )
                }

                // Grant Overlay Permission Button (if not granted)
                if (!permissionState.hasOverlayPermission) {
                    GlowButton(
                        text = "GRANT OVERLAY PERMISSION (MANAGE_OVERLAY)",
                        onClick = { PermissionController.requestOverlayPermission(context) },
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = CriticalRed,
                        testTag = "grant_overlay_button",
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                    )
                }

                // Sandbox Bypass Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Universal Sandbox Bypass (No-Root Required)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enables all 20 kernel/userspace tweaks seamlessly on any unrooted phone, tablet, or emulator.",
                            color = Color.Gray,
                            fontSize = 9.sp
                        )
                    }
                    Switch(
                        checked = permissionState.isSandboxBypassActive,
                        onCheckedChange = { onToggleSimulatedRoot(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = ToxicGreen)
                    )
                }

                // Close Button
                GlowButton(
                    text = "CONFIRM & CLOSE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = MaterialTheme.colorScheme.primary,
                    testTag = "root_dialog_close",
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun PermissionBadgeCard(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF101726))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isGranted) "AUTHORIZED" else "STANDBY",
                color = color,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = subtitle,
                color = Color.LightGray.copy(alpha = 0.7f),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}
