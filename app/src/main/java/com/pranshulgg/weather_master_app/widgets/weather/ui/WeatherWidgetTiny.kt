package com.pranshulgg.weather_master_app.widgets.weather.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather


@Composable
fun WeatherWidgetTiny(state: WidgetWeather?) {
    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant

    if (state != null)
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                provider = ImageProvider(state.currentIcon),
                contentDescription = null,
                modifier = GlanceModifier.size(30.dp)
            )
            Spacer(GlanceModifier.height(6.dp))
            Text(
                state.currentTemp,
                style = TextStyle(color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
        }
}

