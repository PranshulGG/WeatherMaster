package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingScreenPlaceholder(
    fraction: Float = 1f,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Box(
        modifier = Modifier
            .background(containerColor)
            .zIndex(10000f)
            .fillMaxWidth()
            .fillMaxHeight(fraction = fraction),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(modifier = Modifier.size(60.dp))
    }
}