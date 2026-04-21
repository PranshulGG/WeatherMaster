package com.pranshulgg.weathermaster.feature.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation

@Composable
fun BlocksHeader(text: String, icon: Int, color: Color) {
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(
//            5.dp,
//            alignment = Alignment.CenterHorizontally
//        ),
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
//    ) {
//        Symbol(
//            icon,
//            color = MaterialTheme.colorScheme.onSurface,
//            size = 20.dp,
//        )
    Text(
        text,
        style = MaterialTheme.typography.titleMedium.copy(
            shadow = Shadow(
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
                offset = Offset(1f, 2f),
                blurRadius = 1.5f
            )
        ),
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface

    )
//    }
}