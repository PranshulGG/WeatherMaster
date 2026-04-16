package com.pranshulgg.weathermaster.feature.main.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.feature.main.components.FroggySearchBar
import com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations.RainCanvas

@Composable
fun FroggyCurrentWeatherCard() {

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(236.dp)
            .padding(top = 8.dp)
    ) {
//        RainCanvas(canvasHeight = 236.dp, rainDropCount = 20)

        FroggySearchBar(
            onSearchClick = {},
            onSettingsClick = {},
            hintText = "location name hereee"
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.padding(top = 16.dp)) {
                Text("Now", color = colorScheme.secondary, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "30°",
                        color = colorScheme.primary,
                        fontSize = 62.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Image(
                        painter = painterResource(R.drawable.weather_mostly_clear_day),
                        contentDescription = "",
                        Modifier.size(42.dp)
                    )
                }

                Text(
                    "High: 32° Low: 28°",
                    color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(Modifier.weight(1f))
            Column() {
                Text(
                    "Mostly clear",
                    color = colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Feels like: 31°",
                    color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }

}