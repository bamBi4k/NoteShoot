package com.sentinel.noteshoot.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.noteshoot.ui.theme.ThemeCatalog
import com.sentinel.noteshoot.ui.theme.ThemeManager
import com.sentinel.noteshoot.ui.theme.cornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
    val theme = ThemeManager.active
    var showUnlockDialog by remember { mutableStateOf(false) }
    var pulse by remember { mutableStateOf(false) }

    val pulseScale by animateFloatAsState(
        targetValue = if (pulse) 1.15f else 1f,
        animationSpec = tween(120),
        label = "version-pulse"
    )

    LaunchedEffect(pulse) {
        if (pulse) {
            kotlinx.coroutines.delay(130)
            pulse = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ABOUT",
                        color = theme.fontsHeadings,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.fontsPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.bgMantle)
            )
        },
        containerColor = theme.bgBase
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "NoteShoot",
                color = theme.fontsHeadings,
                fontSize = 26.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(6.dp))

            // === EASTER EGG CLICK TARGET ===
            Text(
                text = "version 1.0",
                color = theme.fontsSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .scale(pulseScale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        pulse = true
                        val unlocked = ThemeManager.registerEasterEggHit()
                        if (unlocked) showUnlockDialog = true
                    }
            )

            Spacer(Modifier.height(36.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = theme.bgSurface,
                shape = theme.cornerShape(),
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "LICENSE",
                        color = theme.fontsHeadings,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "MIT License\n\nPermission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files.",
                        color = theme.fontsPrimary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "CREDITS",
                        color = theme.fontsHeadings,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Made by bambi4k",
                        color = theme.fontsPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showUnlockDialog) {
        val vgui = ThemeCatalog.vgui
        AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            containerColor = theme.bgSurface,
            titleContentColor = theme.fontsHeadings,
            textContentColor = theme.fontsPrimary,
            title = {
                Text(
                    "VGUI unlocked",
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            },
            text = {
                Text(
                    "You found the secret theme. Apply it now?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    ThemeManager.setTheme(vgui)
                    showUnlockDialog = false
                }) {
                    Text(
                        "Apply",
                        color = theme.buttonPrimaryBg,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlockDialog = false }) {
                    Text("Later", color = theme.fontsSecondary)
                }
            }
        )
    }
}