package com.example.model

data class GameApp(
    val packageName: String,
    val name: String,
    val isGame: Boolean = true,
    val isFavorite: Boolean = false,
    val isInVault: Boolean = true,
    val targetFps: Int = 120,
    val touchBoostHz: Int = 480,
    val category: String = "Shooter / Action",
    val isInstalled: Boolean = false,
    val playTimeMinutes: Int = 140
)

data class GpuMetrics(
    val utilizationPercent: Int = 48,
    val frequencyMhz: Int = 680,
    val maxFrequencyMhz: Int = 900,
    val rendererName: String = "Adreno 740 / Vulkan 1.3",
    val temperatureCelsius: Float = 37.8f,
    val loadLevel: GpuLoadLevel = GpuLoadLevel.BALANCED,
    val memoryUsedMb: Long = 1840L,
    val history: List<Int> = listOf(42, 45, 48, 52, 47, 50, 55, 49, 46, 52, 48, 51, 49, 53, 48)
)

enum class GpuLoadLevel(val label: String, val colorHex: Long) {
    IDLE("POWERSAVE • IDLE", 0xFF38BDF8),
    BALANCED("OPTIMAL 3D LOAD", 0xFF00FF88),
    HEAVY("HEAVY SHADING LOAD", 0xFFFFB703),
    EXTREME("PEAK ULTRA CLOCK", 0xFFFF0055)
}

data class PerformanceMetrics(
    val fps: Int = 120,
    val targetFps: Int = 120,
    val onePercentLow: Int = 114,
    val frameTimeMs: Float = 8.3f,
    val cpuUsagePercent: Int = 38,
    val gpuUsagePercent: Int = 48,
    val gpuMetrics: GpuMetrics = GpuMetrics(),
    val cpuTempCelsius: Float = 36.4f,
    val batteryTempCelsius: Float = 34.8f,
    val ramUsedMb: Long = 4200L,
    val ramTotalMb: Long = 8192L,
    val pingMs: Int = 21,
    val jitterMs: Int = 2,
    val packetLossPercent: Float = 0.0f,
    val thermalStatus: ThermalLevel = ThermalLevel.OPTIMAL,
    val recentFpsHistory: List<Int> = listOf(120, 119, 120, 120, 118, 120, 120, 119, 120, 120, 120, 117, 120, 120, 120),
    val isOverclockActive: Boolean = true,
    val activeGovernor: String = "performance",
    val touchSampleRateHz: Int = 480,
    val activeProfile: GameProfile = GameProfile.CODM_TURBO,
    val gyroCalibrated: Boolean = true
)

enum class GameProfile(val label: String, val targetFps: Int, val touchHz: Int) {
    CODM_TURBO("CODM Extreme 120FPS", 120, 480),
    ESPORTS_COMPETITIVE("Competitive Esports", 120, 360),
    BALANCED_PERF("Balanced Gaming", 90, 240),
    BATTERY_SAVER("Battery Saver", 60, 120)
}

enum class ThermalLevel(val label: String, val isWarning: Boolean) {
    OPTIMAL("OPTIMAL - UNTHROTTLED", false),
    WARM("WARM - ELEVATED LOAD", false),
    THROTTLING_RISK("HIGH TEMP WARNING - ACTIVE COOLING", true),
    CRITICAL("CRITICAL - AUTO-THROTTLING OVERRIDE ACTIVE", true)
}

enum class GamingTheme(val displayName: String, val accentHex: Long, val secondaryHex: Long) {
    CYBER_NEON("Cyber Neon", 0xFF00F0FF, 0xFF0066FF),
    OBSIDIAN_CRIMSON("Obsidian Crimson", 0xFFFF0055, 0xFFFF7700),
    TOXIC_EMERALD("Toxic Emerald", 0xFF00FF88, 0xFFA6FF00),
    VAPOR_PURPLE("Vapor Purple", 0xFFB026FF, 0xFF00F0FF)
}

data class AiMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val commandExecuted: String? = null,
    val latencyReducedMs: Int? = null
)

data class TweakSettings(
    // --- ORIGINAL 10 FEATURES ---
    // 1. CPU/GPU Overclock
    val cpuGpuOverclockEnabled: Boolean = true,
    val cpuGovernor: String = "performance",
    val gpuBoostEnabled: Boolean = true,
    val coreUnparkingAllOnline: Boolean = true,

    // 2. RAM & Cache Purge
    val ramAutoPurge: Boolean = true,
    val dropCachesOnLaunch: Boolean = true,

    // 3. Advanced Touch Optimization
    val touchResponseOverclock: Boolean = true,
    val touchSamplingRateHz: Int = 480,
    val touchSensitivityMultiplier: Float = 1.45f,
    val zeroInputLagBuffer: Boolean = true,

    // 4. Heat Management & Throttling
    val heatManagementActive: Boolean = true,
    val autoThrottlingBypass: Boolean = true,
    val maxTempThreshold: Int = 44,
    val coolingProfile: String = "Extreme Chill",

    // 5. CEDSII AI Latency Optimizer
    val cedsiiAiLatencyEngine: Boolean = true,
    val gameDnsMode: String = "Cloudflare 1.1.1.1 Gaming",
    val packetPriorityBoost: Boolean = true,
    val tcpCongestionTuner: String = "BBR Fast Response",

    // 6. DND & Focus Shield
    val dndGameFocusShield: Boolean = true,
    val blockHeadsUp: Boolean = true,
    val lockScreenGestures: Boolean = true,
    val lockBrightness: Boolean = true,

    // 7. Display Refresh Rate Forcer
    val forceRefreshRateHz: Int = 120,
    val framePacingStabilizer: Boolean = true,

    // 8. Tactical Crosshair HUD Overlay
    val crosshairOverlayEnabled: Boolean = false,
    val crosshairStyle: String = "Tactical Cross",
    val crosshairColorHex: Long = 0xFF00FF88,
    val crosshairSizeDp: Float = 18f,

    // 9. 3D Spatial Audio Booster
    val spatialAudioFootstepBooster: Boolean = true,
    val footstepEqPreset: String = "CODM Tactical Footsteps (2.8kHz)",

    // 10. 4D Haptic Feedback
    val fourDHapticEngine: Boolean = true,
    val hapticIntensity: Float = 0.85f,

    // --- 10 NEW V2 FEATURES ---
    // 11. Gyroscope Zero-Drift & Sensor Calibration
    val gyroZeroDriftStabilizer: Boolean = true,
    val gyroSensitivityMultiplier: Float = 2.0f,

    // 12. Anti-Stutter Dynamic VSync Lock
    val antiStutterVSyncLock: Boolean = true,

    // 13. Battery Bypass & Direct Power Simulation
    val batteryBypassDirectPower: Boolean = true,

    // 14. Custom Game Resolution Scaling
    val gameResolutionScale: String = "1080p Ultra (Native)",

    // 15. Screen Edge Mistouch Filter (Claw Grip Deadzone)
    val screenEdgeMistouchFilter: Boolean = true,
    val mistouchDeadzonePx: Int = 24,

    // 16. Voice Comms Background Noise Canceler
    val voiceCommsNoiseCanceler: Boolean = true,

    // 17. Instant Highlight Replay Buffer
    val instantHighlightReplayBuffer: Boolean = false,

    // 18. Dual-Channel Network Boost (Wi-Fi + Mobile Multipath)
    val dualChannelNetworkBoost: Boolean = true,

    // 19. Tactical Black Equalizer / Dark Area Gamma Booster
    val blackEqualizerGammaBooster: Boolean = true,
    val blackEqualizerIntensity: Float = 1.30f,

    // 20. Rapid Fire Auto-Tap / Trigger Macro Assist
    val rapidFireAssist: Boolean = false,
    val rapidFireCps: Int = 14,

    // Floating Gaming Orb / Mini Circle Controls
    val floatingGamingOrbEnabled: Boolean = true,
    val activeProfile: GameProfile = GameProfile.CODM_TURBO,

    // CODM Specific Warfighter Suite
    val codmUltra120Fps: Boolean = true,
    val codmGyroSampleMultiplier: Boolean = true,
    val codmRecoilStabilizer: Boolean = true,
    val windowsEmulationCompatibility: Boolean = true
)
