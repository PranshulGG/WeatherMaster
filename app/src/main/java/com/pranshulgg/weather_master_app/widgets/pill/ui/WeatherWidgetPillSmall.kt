package com.pranshulgg.weather_master_app.widgets.pill.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather


@Composable
fun WeatherWidgetPillSmall(state: WidgetWeather?) {
    val textColor = GlanceTheme.colors.primary

    if (state != null)
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.pill_shape),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    GlanceTheme.colors.widgetBackground
                ),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    state.currentTemp,
                    style = TextStyle(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 52.sp,
                    ),
                    modifier = GlanceModifier.padding(start = 22.dp)
                )

                Spacer(modifier = GlanceModifier.height(38.dp))
            }

            Image(
                provider = ImageProvider(state.currentIcon),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(120.dp)
                    .padding(top = 40.dp, end = 40.dp)
            )
        }
}

