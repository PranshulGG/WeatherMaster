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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetHourlyItem
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetMinMaxTemp

@Composable
fun WeatherWidgetMedium(state: WidgetWeather?) {
    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant

    if (state != null)
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        state.locationName,
                        style = TextStyle(
                            color = textColorVariant,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        state.currentTemp,
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                }
                Spacer(GlanceModifier.defaultWeight())
                Column(horizontalAlignment = Alignment.End) {
                    Image(
                        provider = ImageProvider(state.currentIcon),
                        contentDescription = null,
                        modifier = GlanceModifier.size(32.dp)
                    )
                    Spacer(GlanceModifier.height(3.dp))
                    Text(
                        state.currentCondition,
                        style = TextStyle(
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(GlanceModifier.height(3.dp))
                    WidgetMinMaxTemp(state.daily.first().tempMin, state.daily.first().tempMax, true)

                }

            }
            Spacer(GlanceModifier.defaultWeight())
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.End,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                state.hourly.take(3).forEach {
                    WidgetHourlyItem(
                        it.time,
                        it.temp,
                        it.conditionIcon
                    )
                }
            }
        }
//    if (state != null)
//        Column(
//            modifier = GlanceModifier.fillMaxSize().cornerRadius(16.dp)
//                .background(GlanceTheme.colors.widgetBackground).padding(16.dp),
//        ) {
//
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = GlanceModifier.fillMaxWidth()
//            ) {
//                Image(
//                    provider = ImageProvider(state.currentIcon),
//                    contentDescription = null,
//                    modifier = GlanceModifier.size(36.dp)
//                )
//                Spacer(GlanceModifier.defaultWeight())
//                Column(horizontalAlignment = Alignment.End) {
//                    Text(
//                        state.locationName,
//                        style = TextStyle(
//                            color = textColorVariant,
//                            fontSize = 17.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    )
//                    Text(
//                        state.currentCondition,
//                        style = TextStyle(
//                            color = textColor,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    )
//                }
//            }
//            Spacer(GlanceModifier.defaultWeight())
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = GlanceModifier.fillMaxWidth()
//            ) {
//                Column(horizontalAlignment = Alignment.Start) {
//                    Text(
//                        state.currentTemp,
//                        style = TextStyle(
//                            color = GlanceTheme.colors.primary,
//                            fontSize = 52.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    )
//                    WidgetMinMaxTemp(state.daily.first().tempMin, state.daily.first().tempMax)
//                }
//                Spacer(GlanceModifier.defaultWeight())
//                state.hourly.take(2).forEach {
//                    WidgetHourlyItem(
//                        it.time,
//                        it.temp,
//                        it.conditionIcon,
//                        true
//                    )
//                }
//
//            }
//        }
}

