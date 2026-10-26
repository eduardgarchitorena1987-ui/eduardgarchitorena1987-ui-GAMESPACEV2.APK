package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

data class PermissionState(
    val hasOverlayPermission: Boolean = false,
    val isRootGranted: Boolean = false,
    val rootMethod: String = "Detecting...",
    val isShizukuInstalled: Boolean = false,
    val isShizukuRunning: Boolean = false,
    val isShizukuAuthorized: Boolean = false,
    val isBatteryOptimizedIgnored: Boolean = false,
    val isSandboxBypassActive: Boolean = true
)

object PermissionController {
    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState

    suspend fun checkAllPermissions(context: Context): PermissionState = withContext(Dispatchers.IO) {
        val pm = context.packageManager

        // 1. Overlay permission
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }

        // 2. Root detection
        val rootStatus = RootController.checkRootAccess()

        // 3. Shizuku check
        val isShizukuPkgInstalled = try {
            pm.getPackageInfo("moe.shizuku.privileged.api", 0)
            true
        } catch (_: Exception) {
            false
        }

        // 4. Battery Optimization
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isBatteryIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
        } else {
            true
        }

        val updated = PermissionState(
            hasOverlayPermission = hasOverlay,
            isRootGranted = rootStatus.isAccessGranted,
            rootMethod = rootStatus.rootMethod,
            isShizukuInstalled = isShizukuPkgInstalled,
            isShizukuRunning = isShizukuPkgInstalled || rootStatus.isSimulatedMode,
            isShizukuAuthorized = isShizukuPkgInstalled || rootStatus.isSimulatedMode,
            isBatteryOptimizedIgnored = isBatteryIgnored,
            isSandboxBypassActive = rootStatus.isSimulatedMode
        )

        _permissionState.value = updated
        updated
    }

    fun requestOverlayPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun requestBatteryOptimizationExemption(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun toggleSandboxBypass(enable: Boolean) {
        RootController.toggleSimulatedRoot(enable)
        val current = _permissionState.value
        _permissionState.value = current.copy(
            isSandboxBypassActive = enable,
            isRootGranted = current.isRootGranted || enable,
            isShizukuAuthorized = current.isShizukuAuthorized || enable
        )
    }
}
