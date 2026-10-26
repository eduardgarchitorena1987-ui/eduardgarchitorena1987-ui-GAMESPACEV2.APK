package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.audio.GamingAudioFx
import com.example.model.GameApp
import com.example.ui.components.GlowButton
import com.example.ui.components.glassmorphic
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ToxicGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    gameApps: List<GameApp>,
    onLaunchGame: (GameApp) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleVault: (String) -> Unit,
    onUpdateGameConfig: (String, Int, Int) -> Unit,
    onAddCustomGame: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var isAddDialogOpen by remember { mutableStateOf(false) }

    val categories = listOf("All Items", "In Vault (Selected)", "Competitive Games", "Favorites", "Device Apps")

    val filteredApps = remember(gameApps, searchQuery, selectedCategoryIndex) {
        gameApps.filter { app ->
            val matchesQuery = app.name.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)
            val matchesCategory = when (selectedCategoryIndex) {
                1 -> app.isInVault
                2 -> app.isGame
                3 -> app.isFavorite
                4 -> !app.isGame
                else -> true
            }
            matchesQuery && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CEDSII SPACE • APP VAULT SELECTION",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${filteredApps.size} titles ready • Tap VAULT to select/unselect for Game Space",
                    color = ToxicGreen,
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            GlowButton(
                text = "ADD GAME",
                onClick = {
                    GamingAudioFx.playOrbClickSound()
                    isAddDialogOpen = true
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                },
                glowColor = MaterialTheme.colorScheme.primary,
                testTag = "add_game_button",
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_search_field"),
            placeholder = { Text("Search installed games or package names...", color = Color.Gray, fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0x3338BDF8),
                focusedContainerColor = Color(0x33141E33),
                unfocusedContainerColor = Color(0x22141E33),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
            divider = {}
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = {
                        GamingAudioFx.playOrbClickSound()
                        selectedCategoryIndex = index
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedCategoryIndex == index) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.5.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // App List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredApps, key = { it.packageName }) { app ->
                AppListItem(
                    app = app,
                    onLaunch = { onLaunchGame(app) },
                    onToggleFavorite = { onToggleFavorite(app.packageName) },
                    onToggleVault = { onToggleVault(app.packageName) },
                    onUpdateConfig = { fps, touch -> onUpdateGameConfig(app.packageName, fps, touch) }
                )
            }
        }
    }

    if (isAddDialogOpen) {
        AddGameDialog(
            onDismiss = { isAddDialogOpen = false },
            onAdd = { name, pkg, cat ->
                onAddCustomGame(name, pkg, cat)
                isAddDialogOpen = false
            }
        )
    }
}

@Composable
fun AppListItem(
    app: GameApp,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleVault: () -> Unit,
    onUpdateConfig: (Int, Int) -> Unit
) {
    var expandedConfig by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFF0B1222).copy(alpha = 0.90f),
                borderColor = if (app.isInVault) ToxicGreen.copy(alpha = 0.6f) else Color(0x3338BDF8)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game Icon Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (app.isInVault) listOf(ToxicGreen, NeonCyan) else listOf(Color.DarkGray, Color.Gray)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Game Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expandedConfig = !expandedConfig }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.name,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (app.packageName.contains("callofduty") || app.packageName.contains("codm")) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (app.packageName.contains("garena")) "GARENA VIP" else "VIP CODM",
                                color = Color.Black,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ToxicGreen)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = "${app.category} • ${app.targetFps}Hz • ${app.touchBoostHz}Hz Touch",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // IN VAULT Selection Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (app.isInVault) ToxicGreen.copy(alpha = 0.25f) else Color(0xFF1E293B))
                        .border(1.dp, if (app.isInVault) ToxicGreen else Color.Gray, RoundedCornerShape(8.dp))
                        .clickable {
                            GamingAudioFx.playToggleSound(!app.isInVault)
                            onToggleVault()
                        }
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (app.isInVault) Icons.Default.CheckCircle else Icons.Default.Add,
                            contentDescription = "Vault Selection",
                            tint = if (app.isInVault) ToxicGreen else Color.LightGray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (app.isInVault) "IN VAULT" else "+ SELECT",
                            color = if (app.isInVault) ToxicGreen else Color.LightGray,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Favorite Star
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (app.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (app.isFavorite) Color(0xFFFFB703) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Launch Glow Button
                GlowButton(
                    text = "LAUNCH",
                    onClick = onLaunch,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Launch",
                            tint = Color.Black,
                            modifier = Modifier.size(12.dp)
                        )
                    },
                    glowColor = ToxicGreen,
                    testTag = "launch_btn_${app.packageName}",
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            AnimatedVisibility(visible = expandedConfig) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "PER-GAME KERNEL OPTIMIZATION PROFILE",
                        color = Color.Gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Target Framerate:", color = Color.White, fontSize = 10.5.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(60, 90, 120, 144).forEach { fps ->
                                Text(
                                    text = "${fps}Hz",
                                    color = if (app.targetFps == fps) Color.Black else Color.White,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (app.targetFps == fps) ToxicGreen else Color(0xFF1E293B))
                                        .clickable { onUpdateConfig(fps, app.touchBoostHz) }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Touch Sampling Rate:", color = Color.White, fontSize = 10.5.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(240, 360, 480).forEach { hz ->
                                Text(
                                    text = "${hz}Hz",
                                    color = if (app.touchBoostHz == hz) Color.Black else Color.White,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (app.touchBoostHz == hz) MaterialTheme.colorScheme.primary else Color(0xFF1E293B))
                                        .clickable { onUpdateConfig(app.targetFps, hz) }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class GamePreset(val name: String, val pkg: String, val category: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var pkg by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Shooter / Action") }

    val presets = remember {
        listOf(
            GamePreset("Call of Duty: Mobile (Garena)", "com.garena.game.codm", "FPS / Garena SEA"),
            GamePreset("Call of Duty: Mobile", "com.activision.callofduty.shooter", "FPS / Competitive"),
            GamePreset("PUBG Mobile", "com.tencent.ig", "Battle Royale"),
            GamePreset("Free Fire MAX", "com.dts.freefiremax", "Battle Royale"),
            GamePreset("Mobile Legends: Bang Bang", "com.mobile.legends", "MOBA"),
            GamePreset("Genshin Impact", "com.miHoYo.GenshinImpact", "Action RPG"),
            GamePreset("Roblox", "com.roblox.client", "Sandbox / Action"),
            GamePreset("Warzone Mobile", "com.activision.callofduty.warzone", "FPS / Battle Royale")
        )
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphic(shape = RoundedCornerShape(20.dp), backgroundColor = Color(0xFF0F172A))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "LINK GAME TO CEDSII SPACE",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "QUICK 1-TAP PRESETS:",
                    color = ToxicGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // 1-Tap Presets
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    presets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .clickable {
                                    GamingAudioFx.playOrbClickSound()
                                    name = preset.name
                                    pkg = preset.pkg
                                    category = preset.category
                                }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = preset.name,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Game Name", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pkg,
                    onValueChange = { pkg = it },
                    label = { Text("Package Name (e.g. com.game.pkg)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                GlowButton(
                    text = "ADD TO VAULT",
                    onClick = {
                        if (name.isNotBlank()) {
                            val targetPkg = if (pkg.isNotBlank()) pkg else "custom.game.${System.currentTimeMillis()}"
                            onAdd(name, targetPkg, category)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "confirm_add_game"
                )
            }
        }
    }
}
