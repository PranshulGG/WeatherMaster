package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmptyContainerPlaceholder(
    icon: Int,
    text: String,
    description: String = "",
    fraction: Float = 0.8f,
    size: Float = 1f
) {

    val containerSize = 160.dp * size
    val iconSize = 76.dp * size
    val titleSize = 24.sp * size
    val descSize = 16.sp * size
    val spacingLarge = 16.dp * size
    val spacingSmall = 5.dp * size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = fraction),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = MaterialShapes.Pill.toShape(),
            modifier = Modifier
                .height(containerSize)
                .width(containerSize),
            color = MaterialTheme.colorScheme.surfaceBright
        ) {

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                Symbol(icon, size = iconSize, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(spacingLarge))
        Text(
            text,
            fontSize = titleSize,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Black
        )
        if (description != "") {
            Spacer(Modifier.height(spacingSmall))
            Text(
                description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = descSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

}