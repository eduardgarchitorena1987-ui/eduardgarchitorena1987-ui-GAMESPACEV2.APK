package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GamingTheme
import com.example.ui.GamingViewModel
import com.example.ui.components.FloatingFpsOverlay
import com.example.ui.components.FloatingGamingOrb
import com.example.ui.components.IntroBootAnimation
import com.example.ui.components.ShellNotificationHud
import com.example.ui.components.TacticalCrosshairOverlay
import com.example.ui.components.glassmorphic
import com.example.ui.screens.AppListScreen
import com.example.ui.screens.CedsiiAiScreen
import com.example.ui.screens.CodmSpecialScreen
import com.example.ui.screens.GamingFeaturesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RootGateDialog
import com.example.ui.screens.TelemetryScreen
import com.example.ui.theme.CosmicDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.ToxicGreen

import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.audio.GamingAudioFx
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit

class MainActivity : ComponentActivity() {
    private val viewModel: GamingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enable Immersive Sticky Full-Screen Mode for Gaming
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            val selectedTheme by viewModel.selectedTheme.collectAsState()

            MyApplicationTheme(gamingTheme = selectedTheme) {
                MainAppScreen(
                    viewModel = viewModel,
                    onToggleFullScreen = { enable ->
                        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                        if (enable) {
                            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            insetsController.hide(WindowInsetsCompat.Type.systemBars())
                        } else {
                            insetsController.show(WindowInsetsCompat.Type.systemBars())
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: GamingViewModel,
    onToggleFullScreen: (Boolean) -> Unit = {}
) {
    val metrics by viewModel.metrics.collectAsState()
    val rootStatus by viewModel.rootStatus.collectAsState()
    val permissionState by viewModel.permissionState.collectAsState()
    val gameApps by viewModel.gameApps.collectAsState()
    val tweaks by viewModel.tweakSettings.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val isRootDialogOpen by viewModel.isRootDialogOpen.collectAsState()
    val isFloatingFpsActive by viewModel.isFloatingFpsActive.collectAsState()
    val isFloatingServiceRunning by viewModel.isFloatingBubbleServiceRunning.collectAsState()
    val showBootAnimation by viewModel.showBootAnimation.collectAsState()
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isAiAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    val shellNotification by viewModel.shellNotification.collectAsState()
    val launchingGameName by viewModel.launchingGameName.collectAsState()

    var isThemeDialogOpen by remember { mutableStateOf(false) }
    var isFullScreenActive by remember { mutableStateOf(true) }

    // Cool opening animation every time the app opens
    if (showBootAnimation) {
        IntroBootAnimation(
            onAnimationFinished = {
                GamingAudioFx.playGameSpaceIntroSound()
                viewModel.dismissBootAnimation()
            }
        )
        return
    }

    // Handle system back button
    BackHandler(enabled = selectedTab != 0) {
        viewModel.setSelectedTab(0)
    }

    val navigationItems = listOf(
        Triple("Home", Icons.Default.Home, 0),
        Triple("Games", Icons.Default.SportsEsports, 1),
        Triple("20 Suites", Icons.Default.Tune, 2),
        Triple("CEDSII AI", Icons.Default.Bolt, 3),
        Triple("CODM", Icons.Default.Gamepad, 4),
        Triple("Telemetry", Icons.Default.Analytics, 5)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicDark)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                // Sleek Landscape Gamer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .background(SpaceDark.copy(alpha = 0.90f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.setRootDialogOpen(true) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Logo",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "CEDSII SPACE v2",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (rootStatus.isRootDetected) "ROOT #0" else if (permissionState.isShizukuAuthorized) "SHIZUKU" else "UNIVERSAL",
                                        color = ToxicGreen,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ToxicGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    text = "PROFILE: ${tweaks.activeProfile.label.uppercase()} • ${metrics.fps} FPS",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Right header quick buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // In-Game Floating Bubble Service Toggle Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isFloatingServiceRunning) ToxicGreen.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                    .border(
                                        1.dp,
                                        if (isFloatingServiceRunning) ToxicGreen else Color.DarkGray,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.toggleFloatingBubbleService() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = "Bubble Service",
                                        tint = if (isFloatingServiceRunning) ToxicGreen else Color.LightGray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isFloatingServiceRunning) "BUBBLE: ACTIVE" else "BUBBLE: OFF",
                                        color = if (isFloatingServiceRunning) ToxicGreen else Color.LightGray,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Quick Turbo Boost Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    .clickable { viewModel.runQuickBoost() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Boost",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "100% TURBO",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Root / Shizuku / Permission Manager
                            IconButton(
                                onClick = { viewModel.setRootDialogOpen(true) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("root_manager_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Kernel & Shizuku",
                                    tint = if (permissionState.isRootGranted || permissionState.isShizukuAuthorized) ToxicGreen else Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Theme Selector
                            IconButton(
                                onClick = { isThemeDialogOpen = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("theme_selector_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Themes",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Immersive Sticky Fullscreen Toggle
                            IconButton(
                                onClick = {
                                    isFullScreenActive = !isFullScreenActive
                                    onToggleFullScreen(isFullScreenActive)
                                    GamingAudioFx.playOrbClickSound()
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("fullscreen_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isFullScreenActive) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Full Screen",
                                    tint = if (isFullScreenActive) ToxicGreen else Color.LightGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Landscape Side Navigation Rail
                NavigationRail(
                    modifier = Modifier
                        .fillMaxHeight()
                        .testTag("side_nav_rail"),
                    containerColor = SpaceDark.copy(alpha = 0.95f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    navigationItems.forEach { (label, icon, tabIndex) ->
                        NavigationRailItem(
                            selected = selectedTab == tabIndex,
                            onClick = { viewModel.setSelectedTab(tabIndex) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = if (selectedTab == tabIndex) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }

                // Active Landscape Screen Pane
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            metrics = metrics,
                            rootStatus = rootStatus,
                            gameApps = gameApps,
                            isFloatingFpsActive = isFloatingFpsActive,
                            onQuickBoost = { viewModel.runQuickBoost() },
                            onOpenRootDialog = { viewModel.setRootDialogOpen(true) },
                            onToggleFloatingFps = { viewModel.toggleFloatingFps() },
                            onLaunchGame = { game -> viewModel.launchGameWithOptimization(game) },
                            onNavigateToApps = { viewModel.setSelectedTab(1) },
                            onNavigateToFeatures = { viewModel.setSelectedTab(2) },
                            onNavigateToAi = { viewModel.setSelectedTab(3) },
                            onCalibrateGyro = { viewModel.calibrateGyro() }
                        )
                        1 -> AppListScreen(
                            gameApps = gameApps,
                            onLaunchGame = { game -> viewModel.launchGameWithOptimization(game) },
                            onToggleFavorite = { pkg -> viewModel.appRepository.toggleFavorite(pkg) },
                            onToggleVault = { pkg -> viewModel.toggleAppVaultStatus(pkg) },
                            onUpdateGameConfig = { pkg, fps, touch -> viewModel.appRepository.updateGameConfig(pkg, fps, touch) },
                            onAddCustomGame = { name, pkg, cat -> viewModel.appRepository.addCustomGame(name, pkg, cat) }
                        )
                        2 -> GamingFeaturesScreen(
                            tweaks = tweaks,
                            onUpdateTweaks = { updater -> viewModel.updateTweaks(updater) },
                            onTestHaptic = { viewModel.triggerHaptic() },
                            onPurgeMemory = { viewModel.runQuickBoost() },
                            onCalibrateGyro = { viewModel.calibrateGyro() }
                        )
                        3 -> CedsiiAiScreen(
                            metrics = metrics,
                            messages = aiMessages,
                            isAnalyzing = isAiAnalyzing,
                            onSendCommand = { cmd -> viewModel.sendAiCommand(cmd) }
                        )
                        4 -> CodmSpecialScreen(
                            tweaks = tweaks,
                            onUpdateTweaks = { updater -> viewModel.updateTweaks(updater) },
                            onLaunchCodmGarena = {
                                val codmGarena = gameApps.firstOrNull { it.packageName == "com.garena.game.codm" }
                                    ?: gameApps.firstOrNull { it.packageName.contains("garena") }
                                    ?: gameApps.first()
                                viewModel.launchGameWithOptimization(codmGarena)
                            },
                            onLaunchCodmGlobal = {
                                val codmGlobal = gameApps.firstOrNull { it.packageName == "com.activision.callofduty.shooter" }
                                    ?: gameApps.firstOrNull { it.packageName.contains("callofduty") }
                                    ?: gameApps.first()
                                viewModel.launchGameWithOptimization(codmGlobal)
                            }
                        )
                        5 -> TelemetryScreen(
                            metrics = metrics,
                            onPurgeMemory = { viewModel.runQuickBoost() }
                        )
                    }

                    // Floating In-App Tactical Crosshair Overlay (visible when turned on via Orb or Features)
                    TacticalCrosshairOverlay(
                        isVisible = tweaks.crosshairOverlayEnabled,
                        style = tweaks.crosshairStyle,
                        colorHex = tweaks.crosshairColorHex,
                        sizeDp = tweaks.crosshairSizeDp
                    )

                    // Floating Gaming Orb with 14 In-Game Features and Left Dock Tab
                    FloatingGamingOrb(
                        tweaks = tweaks,
                        metrics = metrics,
                        onUpdateTweaks = { updater -> viewModel.updateTweaks(updater) },
                        onPurgeMemory = { viewModel.runQuickBoost() },
                        onCalibrateGyro = { viewModel.calibrateGyro() },
                        onAiPingBoost = { viewModel.sendAiCommand("boost network ping and gaming packets") },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                    )

                    // Optional Floating FPS HUD
                    FloatingFpsOverlay(
                        metrics = metrics,
                        isVisible = isFloatingFpsActive,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 20.dp)
                    )
                }
            }
        }

        // Real-Time Shell Execution Notification HUD (Slides in from top)
        ShellNotificationHud(
            notification = shellNotification,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )

        // Realistic Pre-Launch Game Booster Modal Animation
        AnimatedVisibility(
            visible = launchingGameName != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            launchingGameName?.let { gameTitle ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(380.dp)
                            .glassmorphic(shape = RoundedCornerShape(20.dp), borderColor = ToxicGreen)
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(ToxicGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = "Launching",
                                    tint = ToxicGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = "ACCELERATING $gameTitle",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(">> Flushing memory & dirty page caches...", color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(">> Forcing 120 FPS frame pacing & 480Hz digitizer...", color = ToxicGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(">> Gyro zero-drift calibration active...", color = NeonCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(">> Launching target package...", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }

                            CircularProgressIndicator(
                                color = ToxicGreen,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Root, Shizuku, and System Overlay Manager Dialog
    if (isRootDialogOpen) {
        RootGateDialog(
            rootStatus = rootStatus,
            permissionState = permissionState,
            isFloatingServiceRunning = isFloatingServiceRunning,
            onToggleFloatingService = { viewModel.toggleFloatingBubbleService() },
            onToggleSimulatedRoot = { enable -> viewModel.toggleSimulatedRoot(enable) },
            onDismiss = { viewModel.setRootDialogOpen(false) }
        )
    }

    // Theme Picker Dialog
    if (isThemeDialogOpen) {
        BasicAlertDialog(onDismissRequest = { isThemeDialogOpen = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .glassmorphic(shape = RoundedCornerShape(20.dp), backgroundColor = Color(0xFF0F172A))
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SELECT GAMING PALETTE",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )

                    GamingTheme.values().forEach { theme ->
                        val isSelected = selectedTheme == theme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF1E293B) else Color.Transparent)
                                .clickable {
                                    viewModel.setTheme(theme)
                                    isThemeDialogOpen = false
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(theme.accentHex))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = theme.displayName,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (isSelected) {
                                Text(
                                    text = "ACTIVE",
                                    color = ToxicGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
