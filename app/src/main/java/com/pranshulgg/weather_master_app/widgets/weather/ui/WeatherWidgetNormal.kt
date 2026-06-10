package com.pranshulgg.weather_master_app.widgets.weather.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetHourlyItem
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetMinMaxTemp


@Composable
fun WeatherWidgetNormal(state: WidgetWeather?, size: DpSize) {

    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant
    val itemWidth = 60.dp
    val count = ((size.width - 10.dp) / itemWidth).toInt().coerceIn(1, state?.hourly?.size)


    if (state != null)
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(24.dp),
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Image(
                    provider = ImageProvider(state.currentIcon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(46.dp)
                )
                Spacer(GlanceModifier.defaultWeight())
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        state.locationName,
                        style = TextStyle(
                            color = textColorVariant,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        state.currentCondition,
                        style = TextStyle(
                            color = textColor,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Spacer(GlanceModifier.defaultWeight())
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Column() {
                    Text(
                        state.currentTemp,
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row() {
                        Text(
                            state.daily.first().tempMax,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            state.daily.first().tempMin,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                Spacer(GlanceModifier.defaultWeight())
                state.hourly.take(count).forEach {
                    WidgetHourlyItem(
                        it.time,
                        it.temp,
                        it.conditionIcon
                    )
                }
            }
        }
}
