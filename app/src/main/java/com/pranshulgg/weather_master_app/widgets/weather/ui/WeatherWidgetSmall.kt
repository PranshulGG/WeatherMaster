package com.pranshulgg.weather_master_app.widgets.weather.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetMinMaxTemp


@Composable
fun WeatherWidgetSmall(state: WidgetWeather?) {
    val textColor = GlanceTheme.colors.onSurface


    if (state != null)
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        ) {

            Column(horizontalAlignment = Alignment.Start) {
                Image(
                    provider = ImageProvider(state.currentIcon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(36.dp)
                )
                Spacer(GlanceModifier.height(10.dp))
                Text(
                    state.currentCondition,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Spacer(GlanceModifier.defaultWeight())
            Text(
                state.currentTemp,
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            WidgetMinMaxTemp(state.daily.first().tempMin, state.daily.first().tempMax)
        }
}