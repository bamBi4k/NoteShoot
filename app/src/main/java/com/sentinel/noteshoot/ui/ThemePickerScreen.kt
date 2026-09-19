package com.sentinel.noteshoot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.noteshoot.ui.theme.ThemeCatalog
import com.sentinel.noteshoot.ui.theme.ThemeManager
import com.sentinel.noteshoot.ui.theme.ThemeSpec
import com.sentinel.noteshoot.ui.theme.cornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(onNavigateBack: () -> Unit) {
    val activeTheme = ThemeManager.active
    val unlocked = ThemeManager.unlockedIds
    val currentPt = ThemeManager.basePt

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "THEMES",
                        color = activeTheme.fontsHeadings,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = activeTheme.fontsPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = activeTheme.bgMantle)
            )
        },
        containerColor = activeTheme.bgBase
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { FontSizeCard(currentPt) }

            item {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "THEME",
                    color = activeTheme.fontsSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            items(ThemeCatalog.all, key = { it.id }) { spec ->
                val isUnlocked = spec.id in unlocked
                val isActive = spec.id == activeTheme.id
                ThemeCard(
                    spec = spec,
                    isUnlocked = isUnlocked,
                    isActive = isActive,
                    onClick = { if (isUnlocked && !isActive) ThemeManager.setTheme(spec) }
                )
            }

            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Tip: find the easter egg in About to unlock a secret theme.",
                    color = activeTheme.fontsSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun FontSizeCard(currentPt: Float) {
    val theme = ThemeManager.active
    val shape = theme.cornerShape()

    var textValue by remember(currentPt) {
        mutableStateOf(formatPt(currentPt))
    }
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = theme.bgSurface,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "FONT SIZE",
                    color = theme.fontsHeadings,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { new ->
                        val filtered = new.filter { it.isDigit() || it == '.' }.take(5)
                        textValue = filtered

                        val parsed = filtered.toFloatOrNull()
                        if (parsed != null && parsed in ThemeManager.BASE_PT_MIN..ThemeManager.BASE_PT_MAX) {
                            ThemeManager.updateBasePt(parsed)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .width(96.dp)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                            if (!focusState.isFocused) {
                                textValue = formatPt(ThemeManager.basePt)
                            }
                        },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp,
                        color = theme.fontsPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    ),
                    trailingIcon = {
                        Text(
                            "pt",
                            color = theme.fontsSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.buttonPrimaryBg,
                        unfocusedBorderColor = theme.bevelBorder,
                        focusedTextColor = theme.fontsPrimary,
                        unfocusedTextColor = theme.fontsPrimary,
                        cursorColor = theme.buttonPrimaryBg,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Aa The quick brown fox 👋",
                color = theme.fontsPrimary,
                fontSize = currentPt.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("A", color = theme.fontsSecondary, fontSize = 10.sp)
                Slider(
                    value = currentPt,
                    onValueChange = {
                        ThemeManager.updateBasePt(it)
                        if (!isFocused) textValue = formatPt(it)
                    },
                    valueRange = ThemeManager.BASE_PT_MIN..ThemeManager.BASE_PT_MAX,
                    steps = 6,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = theme.buttonPrimaryBg,
                        activeTrackColor = theme.buttonPrimaryBg,
                        inactiveTrackColor = theme.bevelBorder
                    )
                )
                Text("A", color = theme.fontsSecondary, fontSize = 16.sp)
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = "Range: ${ThemeManager.BASE_PT_MIN.toInt()}–${ThemeManager.BASE_PT_MAX.toInt()} pt",
                color = theme.fontsSecondary,
                fontSize = 10.sp
            )
        }
    }
}

private fun formatPt(pt: Float): String =
    if (pt % 1f == 0f) pt.toInt().toString() else String.format("%.1f", pt)

@Composable
private fun ThemeCard(
    spec: ThemeSpec,
    isUnlocked: Boolean,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val outer = ThemeManager.active
    val shape = outer.cornerShape()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked && !isActive, onClick = onClick),
        color = outer.bgSurface,
        shape = shape,
        tonalElevation = if (isActive) 4.dp else 0.dp,
        border = if (isActive) {
            androidx.compose.foundation.BorderStroke(1.dp, outer.buttonPrimaryBg)
        } else null
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeSwatch(spec = spec, isUnlocked = isUnlocked)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isUnlocked) spec.displayName else "???",
                    color = outer.fontsHeadings,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = when {
                        !isUnlocked -> "Locked — find the easter egg"
                        isActive -> "Active"
                        else -> spec.mode.name.lowercase().replaceFirstChar { it.uppercase() } + " theme"
                    },
                    color = outer.fontsSecondary,
                    fontSize = 11.sp
                )
            }
            when {
                !isUnlocked -> Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = outer.fontsSecondary,
                    modifier = Modifier.size(18.dp)
                )
                isActive -> Icon(
                    Icons.Default.Check,
                    contentDescription = "Active",
                    tint = outer.buttonPrimaryBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ThemeSwatch(spec: ThemeSpec, isUnlocked: Boolean) {
    val bg = if (isUnlocked) spec.bgBase else androidx.compose.ui.graphics.Color(0xFF1A1A1A)
    val surface = if (isUnlocked) spec.bgSurface else androidx.compose.ui.graphics.Color(0xFF2A2A2A)
    val textPrimary = if (isUnlocked) spec.fontsPrimary else androidx.compose.ui.graphics.Color(0xFF555555)
    val accent = if (isUnlocked) spec.buttonPrimaryBg else androidx.compose.ui.graphics.Color(0xFF444444)

    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 56.dp)
            .clip(RoundedCornerShape(if (spec.isBeveled && isUnlocked) 0.dp else 8.dp))
            .background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(surface)
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(4.dp)
                    .background(textPrimary.copy(alpha = 0.7f))
            )
            Spacer(Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(4.dp)
                    .background(textPrimary.copy(alpha = 0.4f))
            )
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(accent)
            )
        }
    }
}