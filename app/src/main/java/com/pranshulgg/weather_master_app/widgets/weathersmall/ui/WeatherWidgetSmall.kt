package com.pranshulgg.weather_master_app.widgets.weathersmall.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.model.WidgetDailyItem
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import kotlin.time.Duration.Companion.seconds

@Composable
fun WeatherWidgetSmall(state: WidgetWeather?) {

    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant


    if (state != null)
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(24.dp),
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column() {
                    Text(
                        state.locationName,
                        style = TextStyle(
                            color = textColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        state.currentTemp,
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(GlanceModifier.defaultWeight())
                Image(
                    provider = ImageProvider(state.currentIcon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(56.dp)
                )
            }
            Spacer(GlanceModifier.defaultWeight())

            state.daily.take(3).forEach {
                DailyItem(it)
            }
        }
}


@Composable
private fun DailyItem(daily: WidgetDailyItem) {
    Row(GlanceModifier.fillMaxWidth().height(28.dp)) {
        Text(
            daily.time, style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
        )
        Spacer(GlanceModifier.defaultWeight())
        Image(
            provider = ImageProvider(daily.conditionIcon),
            contentDescription = null,
            modifier = GlanceModifier.size(24.dp)
        )
        Spacer(GlanceModifier.defaultWeight())
        Text(
            "${daily.tempMax}  ${daily.tempMin}", style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
        )
    }
}