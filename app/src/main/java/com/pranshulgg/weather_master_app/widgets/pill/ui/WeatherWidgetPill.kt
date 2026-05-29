package com.pranshulgg.weather_master_app.widgets.pill.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
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
fun WeatherWidgetPill(state: WidgetWeather?) {
    val textColor = GlanceTheme.colors.primary
    val size = LocalSize.current

    val iconSize = (size.height * 0.15f + size.width * 0.15f).coerceIn(24.dp, 100.dp)
    val fontSize = (size.height * 0.15f + size.width * 0.15f).coerceIn(24.dp, 200.dp).value.sp

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
                        fontSize = fontSize,
                    ),
                    modifier = GlanceModifier.padding(start = 25.dp)
                )

                Spacer(modifier = GlanceModifier.height(iconSize / 1.2f))
            }
            Row(
                modifier = GlanceModifier.fillMaxWidth().fillMaxHeight()
                    .padding(top = iconSize),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(state.currentIcon),
                    contentDescription = null,
                    modifier = GlanceModifier
                        .size(iconSize)
                )
                Spacer(modifier = GlanceModifier.width(iconSize / 1.1f))
            }
        }

}


