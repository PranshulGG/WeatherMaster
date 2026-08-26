package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavSpacing(extra: Dp = 0.dp) {
    Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding().plus(extra))
}

