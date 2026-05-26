package com.pranshulgg.weather_master_app.widgets.weather.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R

@Composable
fun WidgetMinMaxTemp(min: String, max: String, isSmall: Boolean = false) {
    Row() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                provider = ImageProvider(R.drawable.arrow_downward_24px),
                contentDescription = null,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                modifier = GlanceModifier.size(if (isSmall) 18.dp else 24.dp)
            )
            Spacer(GlanceModifier.width(3.dp))
            Text(
                min,
                style = TextStyle(
                    fontSize = if (isSmall) 16.sp else 18.sp,
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(GlanceModifier.width(if (isSmall) 6.dp else 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                provider = ImageProvider(R.drawable.arrow_upward_24px),
                contentDescription = null,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                modifier = GlanceModifier.size(if (isSmall) 18.dp else 24.dp)
            )
            Spacer(GlanceModifier.width(3.dp))
            Text(
                max,
                style = TextStyle(
                    fontSize = if (isSmall) 16.sp else 18.sp,
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}