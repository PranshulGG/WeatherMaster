package com.pranshulgg.weather_master_app.widgets.pill.ui

import android.os.Build
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
import androidx.glance.layout.Box
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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather


@Composable
fun WeatherWidgetPillMedium(state: WidgetWeather?) {
    val textColor = GlanceTheme.colors.primary
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant

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
                        fontSize = 68.sp,
                    ),
                    modifier = GlanceModifier.padding(start = 22.dp)
                )

                Spacer(modifier = GlanceModifier.height(48.dp))
            }

            Image(
                provider = ImageProvider(R.drawable.weather_clear_day),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(140.dp)
                    .padding(top = 60.dp, end = 60.dp)
            )
        }
}

