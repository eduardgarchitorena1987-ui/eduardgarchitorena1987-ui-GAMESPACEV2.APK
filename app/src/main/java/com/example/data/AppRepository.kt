package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.example.model.GameApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    private val _gameApps = MutableStateFlow<List<GameApp>>(emptyList())
    val gameApps: StateFlow<List<GameApp>> = _gameApps

    suspend fun loadApps() = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installedApps = try {
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(mainIntent, 0)
            }
        } catch (_: Exception) {
            emptyList()
        }

        val deviceAppMap = installedApps.associate { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            val label = resolveInfo.loadLabel(pm).toString()
            pkg to label
        }

        // Default flagship games list including Call of Duty: Mobile Garena & Global as top priority
        val curatedGames = listOf(
            GameApp(
                packageName = "com.garena.game.codm",
                name = "Call of Duty: Mobile (Garena)",
                isGame = true,
                isFavorite = true,
                targetFps = 120,
                touchBoostHz = 480,
                category = "FPS / Garena SEA",
                isInstalled = deviceAppMap.containsKey("com.garena.game.codm"),
                playTimeMinutes = 540
            ),
            GameApp(
                packageName = "com.activision.callofduty.shooter",
                name = "Call of Duty: Mobile",
                isGame = true,
                isFavorite = true,
                targetFps = 120,
                touchBoostHz = 480,
                category = "FPS / Competitive",
                isInstalled = deviceAppMap.containsKey("com.activision.callofduty.shooter"),
                playTimeMinutes = 420
            ),
            GameApp(
                packageName = "com.tencent.ig",
                name = "PUBG Mobile",
                isGame = true,
                isFavorite = true,
                targetFps = 90,
                touchBoostHz = 360,
                category = "Battle Royale",
                isInstalled = deviceAppMap.containsKey("com.tencent.ig"),
                playTimeMinutes = 280
            ),
            GameApp(
                packageName = "com.miHoYo.GenshinImpact",
                name = "Genshin Impact",
                isGame = true,
                isFavorite = false,
                targetFps = 60,
                touchBoostHz = 240,
                category = "Action RPG",
                isInstalled = deviceAppMap.containsKey("com.miHoYo.GenshinImpact"),
                playTimeMinutes = 190
            ),
            GameApp(
                packageName = "com.dts.freefiremax",
                name = "Free Fire MAX",
                isGame = true,
                isFavorite = false,
                targetFps = 90,
                touchBoostHz = 360,
                category = "Battle Royale",
                isInstalled = deviceAppMap.containsKey("com.dts.freefiremax"),
                playTimeMinutes = 150
            ),
            GameApp(
                packageName = "com.mobile.legends",
                name = "Mobile Legends: Bang Bang",
                isGame = true,
                isFavorite = false,
                targetFps = 120,
                touchBoostHz = 360,
                category = "MOBA",
                isInstalled = deviceAppMap.containsKey("com.mobile.legends"),
                playTimeMinutes = 310
            ),
            GameApp(
                packageName = "com.supercell.brawlstars",
                name = "Brawl Stars",
                isGame = true,
                isFavorite = false,
                targetFps = 120,
                touchBoostHz = 360,
                category = "Multiplayer Action",
                isInstalled = deviceAppMap.containsKey("com.supercell.brawlstars"),
                playTimeMinutes = 95
            ),
            GameApp(
                packageName = "com.riotgames.league.wildrift",
                name = "League of Legends: Wild Rift",
                isGame = true,
                isFavorite = false,
                targetFps = 120,
                touchBoostHz = 360,
                category = "MOBA",
                isInstalled = deviceAppMap.containsKey("com.riotgames.league.wildrift"),
                playTimeMinutes = 120
            ),
            GameApp(
                packageName = "com.roblox.client",
                name = "Roblox",
                isGame = true,
                isFavorite = false,
                targetFps = 60,
                touchBoostHz = 240,
                category = "Sandbox / Action",
                isInstalled = deviceAppMap.containsKey("com.roblox.client"),
                playTimeMinutes = 80
            )
        )

        // Also add installed device apps that aren't already in curated
        val additionalApps = deviceAppMap.filterKeys { pkg ->
            curatedGames.none { it.packageName == pkg } && pkg != context.packageName
        }.map { (pkg, label) ->
            GameApp(
                packageName = pkg,
                name = label,
                isGame = false,
                isFavorite = false,
                isInVault = false,
                targetFps = 60,
                touchBoostHz = 240,
                category = "App / Utility",
                isInstalled = true,
                playTimeMinutes = 30
            )
        }

        _gameApps.value = curatedGames + additionalApps
    }

    fun toggleVaultStatus(packageName: String) {
        _gameApps.value = _gameApps.value.map { app ->
            if (app.packageName == packageName) app.copy(isInVault = !app.isInVault) else app
        }
    }

    fun toggleFavorite(packageName: String) {
        _gameApps.value = _gameApps.value.map { app ->
            if (app.packageName == packageName) app.copy(isFavorite = !app.isFavorite) else app
        }
    }

    fun updateGameConfig(packageName: String, targetFps: Int, touchBoostHz: Int) {
        _gameApps.value = _gameApps.value.map { app ->
            if (app.packageName == packageName) app.copy(targetFps = targetFps, touchBoostHz = touchBoostHz) else app
        }
    }

    fun addCustomGame(name: String, packageName: String, category: String) {
        val newApp = GameApp(
            packageName = packageName,
            name = name,
            isGame = true,
            isFavorite = true,
            isInVault = true,
            targetFps = 120,
            touchBoostHz = 360,
            category = category,
            isInstalled = true
        )
        _gameApps.value = listOf(newApp) + _gameApps.value
    }

    fun launchApp(packageName: String): LaunchResult {
        return try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                context.startActivity(launchIntent)
                LaunchResult.LaunchedDirectly
            } else {
                // If not installed, launch Play Store or browser install intent
                val playStoreIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(playStoreIntent)
                    LaunchResult.OpenedStore
                } catch (_: Exception) {
                    val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(webIntent)
                    LaunchResult.OpenedStore
                }
            }
        } catch (_: Exception) {
            LaunchResult.VirtualSandboxMode
        }
    }
}

enum class LaunchResult {
    LaunchedDirectly,
    OpenedStore,
    VirtualSandboxMode
}
