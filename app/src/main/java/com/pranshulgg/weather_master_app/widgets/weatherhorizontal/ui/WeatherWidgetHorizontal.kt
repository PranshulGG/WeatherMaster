package com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetMinMaxTemp

@Composable
fun WeatherWidgetHorizontal(
    state: WidgetWeather?,
    modifier: GlanceModifier = GlanceModifier
) {
    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant

    if (state != null)

        Row(
            modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                provider = ImageProvider(state.currentIcon),
                contentDescription = null,
                modifier = GlanceModifier.size(48.dp)
            )
            Spacer(GlanceModifier.width(12.dp))
            Column() {
                Text(
                    state.locationName,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start
                    )
                )
                Text(
                    state.currentCondition,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                )
            }
            Spacer(GlanceModifier.defaultWeight())
            Text(
                state.currentTemp,
                style = TextStyle(color = textColor, fontWeight = FontWeight.Bold, fontSize = 48.sp)
            )
            Spacer(GlanceModifier.width(8.dp))
            Column() {
                Text(
                    state.daily.first().tempMax,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    state.daily.first().tempMin,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
//            Spacer(GlanceModifier.width(6.dp))
//
//            Spacer(GlanceModifier.defaultWeight())
//            WidgetMinMaxTemp(state.daily.first().tempMin, state.daily.first().tempMax)

        }
}

