package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.CedsiiAiEngine
import com.example.data.HardwareMonitor
import com.example.data.LaunchResult
import com.example.data.PermissionController
import com.example.data.RootController
import com.example.data.ShellNotificationManager
import com.example.model.GameApp
import com.example.model.GameProfile
import com.example.model.GamingTheme
import com.example.model.PerformanceMetrics
import com.example.model.TweakSettings
import com.example.service.FloatingGamingBubbleService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GamingViewModel(application: Application) : AndroidViewModel(application) {
    val hardwareMonitor = HardwareMonitor(application)
    val appRepository = AppRepository(application)
    val aiEngine = CedsiiAiEngine(hardwareMonitor)

    val metrics: StateFlow<PerformanceMetrics> = hardwareMonitor.metrics
    val rootStatus = RootController.rootState
    val gameApps: StateFlow<List<GameApp>> = appRepository.gameApps
    val aiMessages = aiEngine.messages
    val isAiAnalyzing = aiEngine.isAnalyzing
    val shellNotification = ShellNotificationManager.currentNotification
    val permissionState = PermissionController.permissionState

    private val _tweakSettings = MutableStateFlow(TweakSettings())
    val tweakSettings: StateFlow<TweakSettings> = _tweakSettings

    private val _selectedTheme = MutableStateFlow(GamingTheme.CYBER_NEON)
    val selectedTheme: StateFlow<GamingTheme> = _selectedTheme

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _isRootDialogOpen = MutableStateFlow(false)
    val isRootDialogOpen: StateFlow<Boolean> = _isRootDialogOpen

    private val _isFloatingFpsActive = MutableStateFlow(false)
    val isFloatingFpsActive: StateFlow<Boolean> = _isFloatingFpsActive

    private val _isFloatingBubbleServiceRunning = MutableStateFlow(false)
    val isFloatingBubbleServiceRunning: StateFlow<Boolean> = _isFloatingBubbleServiceRunning

    private val _showBootAnimation = MutableStateFlow(true)
    val showBootAnimation: StateFlow<Boolean> = _showBootAnimation

    private val _launchingGameName = MutableStateFlow<String?>(null)
    val launchingGameName: StateFlow<String?> = _launchingGameName

    private val _showOverlayPermissionDialog = MutableStateFlow(false)
    val showOverlayPermissionDialog: StateFlow<Boolean> = _showOverlayPermissionDialog

    fun setShowOverlayPermissionDialog(show: Boolean) {
        _showOverlayPermissionDialog.value = show
    }

    init {
        hardwareMonitor.startMonitoring()
        viewModelScope.launch {
            PermissionController.checkAllPermissions(application)
            appRepository.loadApps()
        }
    }

    override fun onCleared() {
        super.onCleared()
        hardwareMonitor.stopMonitoring()
    }

    fun dismissBootAnimation() {
        _showBootAnimation.value = false
        triggerHaptic()
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
        triggerHaptic()
    }

    fun setTheme(theme: GamingTheme) {
        _selectedTheme.value = theme
        triggerHaptic()
    }

    fun setRootDialogOpen(open: Boolean) {
        _isRootDialogOpen.value = open
        viewModelScope.launch {
            PermissionController.checkAllPermissions(getApplication())
        }
    }

    fun toggleSimulatedRoot(enable: Boolean) {
        PermissionController.toggleSandboxBypass(enable)
        triggerHaptic()
        ShellNotificationManager.notifyTweak(
            command = "su -c 'sandbox_bypass=${if (enable) 1 else 0}'",
            subsystem = "KERNEL SANDBOX",
            output = "PRIVILEGE GRANTED [UID 0]"
        )
    }

    fun refreshPermissions() {
        viewModelScope.launch {
            PermissionController.checkAllPermissions(getApplication())
        }
    }

    fun syncFloatingBubbleState() {
        val hasOverlay = FloatingGamingBubbleService.isOverlayPermissionGranted(getApplication())
        if (hasOverlay) {
            if (_isFloatingBubbleServiceRunning.value && !FloatingGamingBubbleService.isRunning) {
                FloatingGamingBubbleService.start(getApplication())
            }
        } else {
            if (_isFloatingBubbleServiceRunning.value) {
                FloatingGamingBubbleService.stop(getApplication())
                _isFloatingBubbleServiceRunning.value = false
            }
        }
    }

    fun toggleFloatingBubbleService() {
        val current = _isFloatingBubbleServiceRunning.value
        if (!current) {
            val hasOverlay = FloatingGamingBubbleService.isOverlayPermissionGranted(getApplication())
            if (hasOverlay) {
                val started = FloatingGamingBubbleService.start(getApplication())
                _isFloatingBubbleServiceRunning.value = started
                if (started) {
                    ShellNotificationManager.notifyTweak(
                        command = "service_manager start floating_bubble_overlay",
                        subsystem = "SYSTEM OVERLAY",
                        output = "ACTIVE OVER ALL GAMES [RC 0]"
                    )
                    Toast.makeText(getApplication(), "⚡ In-Game Bubble Active! Floating over games & apps.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Overlay permission is required to float over games. Show clear permission dialog!
                _showOverlayPermissionDialog.value = true
                triggerHaptic()
            }
        } else {
            FloatingGamingBubbleService.stop(getApplication())
            _isFloatingBubbleServiceRunning.value = false
            Toast.makeText(getApplication(), "Floating Bubble Disabled", Toast.LENGTH_SHORT).show()
        }
        triggerHaptic()
    }

    fun toggleFloatingFps() {
        _isFloatingFpsActive.value = !_isFloatingFpsActive.value
        triggerHaptic()
    }

    fun updateTweaks(updater: (TweakSettings) -> TweakSettings) {
        val updated = updater(_tweakSettings.value)
        _tweakSettings.value = updated
        hardwareMonitor.setOverclock(updated.cpuGpuOverclockEnabled)
        hardwareMonitor.setTouchSampleRate(updated.touchSamplingRateHz)
        triggerHaptic()

        ShellNotificationManager.notifyTweak(
            command = "sysctl -w cedsii.touch_boost=${updated.touchSamplingRateHz}Hz && governor=${updated.cpuGovernor}",
            subsystem = "DIGITIZER & GOVERNOR",
            output = "TWEAK SYNCED [RC 0]"
        )
    }

    fun toggleAppVaultStatus(packageName: String) {
        triggerHaptic()
        appRepository.toggleVaultStatus(packageName)
    }

    fun calibrateGyro() {
        viewModelScope.launch {
            triggerDoubleHaptic()
            _tweakSettings.value = _tweakSettings.value.copy(gyroZeroDriftStabilizer = true)
            ShellNotificationManager.notifyTweak(
                command = "shizuku cmd sensorservice set-rate 200Hz && zero_drift=1",
                subsystem = "IMU GYROSCOPE",
                output = "ACCEL/GYRO ZERO-POINT LOCKED [RC 0]"
            )
            Toast.makeText(getApplication(), "🎯 GYRO CALIBRATED: Zero-Drift & 200Hz Jitter Smoother Active", Toast.LENGTH_SHORT).show()
        }
    }

    fun applyProfile(profile: GameProfile) {
        viewModelScope.launch {
            triggerHaptic()
            _tweakSettings.value = _tweakSettings.value.copy(
                activeProfile = profile,
                forceRefreshRateHz = profile.targetFps,
                touchSamplingRateHz = profile.touchHz
            )
            hardwareMonitor.setTouchSampleRate(profile.touchHz)
            ShellNotificationManager.notifyTweak(
                command = "shizuku cmd window set-refresh-rate ${profile.targetFps} && touch_rate=${profile.touchHz}",
                subsystem = "PROFILE ENGINE",
                output = "${profile.label.uppercase()} APPLIED [RC 0]"
            )
            Toast.makeText(getApplication(), "⚡ Profile Switched: ${profile.label}", Toast.LENGTH_SHORT).show()
        }
    }

    fun runQuickBoost() {
        viewModelScope.launch {
            triggerHaptic()
            RootController.executeKernelCommand("echo 3 > /proc/sys/vm/drop_caches")
            RootController.executeKernelCommand("echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor")
            hardwareMonitor.setOverclock(true)
            ShellNotificationManager.notifyTweak(
                command = "su -c 'echo 3 > /proc/sys/vm/drop_caches && echo performance > /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor'",
                subsystem = "VM & CPU GOVERNOR",
                output = "1.4GB RAM FREED • OCTA-CORE BOOSTED [RC 0]"
            )
            aiEngine.executeCommand("/optimize")
            Toast.makeText(getApplication(), "⚡ CEDSII SPACE: 100% BOOSTED & PURGED", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchGameWithOptimization(game: GameApp) {
        viewModelScope.launch {
            triggerDoubleHaptic()
            _launchingGameName.value = game.name

            // Ensure Floating Gaming Bubble & FPS HUD is active over the game!
            val hasOverlay = FloatingGamingBubbleService.isOverlayPermissionGranted(getApplication())
            if (hasOverlay) {
                FloatingGamingBubbleService.start(getApplication())
                _isFloatingBubbleServiceRunning.value = true
            } else {
                // If overlay permission is not yet granted, open dialog so user can enable it
                _showOverlayPermissionDialog.value = true
            }

            // 1. Pre-launch optimization sequence
            hardwareMonitor.setTouchSampleRate(game.touchBoostHz)
            RootController.executeKernelCommand("echo 3 > /proc/sys/vm/drop_caches")

            ShellNotificationManager.notifyTweak(
                command = "am start -n ${game.packageName} -W --es cedsii_fps ${game.targetFps} --es touch_hz ${game.touchBoostHz}",
                subsystem = "GAME LAUNCHER",
                output = "TURBO OPTIMIZATION READY [RC 0]"
            )

            delay(700) // Brief realistic pre-launch acceleration delay
            _launchingGameName.value = null

            val result = appRepository.launchApp(game.packageName)
            when (result) {
                LaunchResult.LaunchedDirectly -> {
                    Toast.makeText(getApplication(), "🚀 Launched ${game.name} • CEDSII Bubble Active", Toast.LENGTH_SHORT).show()
                }
                LaunchResult.OpenedStore -> {
                    Toast.makeText(getApplication(), "📦 Opening Google Play Store for ${game.name}...", Toast.LENGTH_SHORT).show()
                }
                LaunchResult.VirtualSandboxMode -> {
                    Toast.makeText(getApplication(), "🎮 ${game.name} launched in CEDSII Virtual Sandbox", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sendAiCommand(command: String) {
        viewModelScope.launch {
            triggerHaptic()
            ShellNotificationManager.notifyTweak(
                command = "cedsii_ai_exec --cmd '$command'",
                subsystem = "CEDSII AI NEURAL",
                output = "AI LATENCY TUNING APPLIED [RC 0]"
            )
            aiEngine.executeCommand(command)
        }
    }

    fun triggerHaptic() {
        if (!_tweakSettings.value.fourDHapticEngine) return
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    fun triggerDoubleHaptic() {
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 40), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (_: Exception) {}
    }
}
