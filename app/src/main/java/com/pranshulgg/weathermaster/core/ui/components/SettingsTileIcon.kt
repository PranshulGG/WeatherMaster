package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SettingsTileIcon(icon: Int, dangerColor: Boolean = false) {
    Symbol(
        icon,
        color = if (dangerColor) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
    )
}