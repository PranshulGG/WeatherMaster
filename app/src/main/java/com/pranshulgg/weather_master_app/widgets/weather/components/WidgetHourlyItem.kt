package com.pranshulgg.weather_master_app.widgets.weather.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
fun WidgetHourlyItem(time: String, temp: String, icon: Int, isSmall: Boolean = false) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.width(48.dp).height(80.dp)
    ) {
        Text(
            temp,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = if (isSmall) 14.sp else 15.sp
            )
        )
        Spacer(GlanceModifier.height(3.dp))
        Image(
            provider = ImageProvider(icon),
            contentDescription = "",
            modifier = GlanceModifier.size(if (isSmall) 22.dp else 28.dp)
        )
        Spacer(GlanceModifier.height(3.dp))
        Text(
            time,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Medium, fontSize = if (isSmall) 14.sp else 15.sp
            )
        )
    }
}