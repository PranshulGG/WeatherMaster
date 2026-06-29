package com.pranshulgg.weather_master_app.widgets.weather.ui.variants

import androidx.compose.runtime.Composable
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
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetHourlyItem

@Composable
fun WeatherWidgetLarge(state: WidgetWeather?, count: Int, config: WidgetConfig) {

    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant

    val mainIconSize = 32 * config.iconSize
    val textFontSize = 18 * config.fontSize
    val locationFontSize = 16 * config.fontSize
    val tempFontSize = 42 * config.fontSize


    if (state != null) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(18.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth()
            ) {

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.defaultWeight()
                    ) {
                        Image(
                            provider = ImageProvider(state.currentIcon),
                            contentDescription = null,
                            modifier = GlanceModifier.size(mainIconSize.dp)
                        )
                        Spacer(GlanceModifier.width(6.dp))
                        Text(
                            state.currentCondition,
                            style = TextStyle(
                                color = textColor,
                                fontSize = textFontSize.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            modifier = GlanceModifier.fillMaxWidth(),
                            maxLines = 1
                        )
                    }
                    Spacer(GlanceModifier.height(6.dp))
                    Row() {
                        Text(
                            state.daily.first().tempMax,
                            style = TextStyle(
                                fontSize = textFontSize.sp,
                                color = GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            state.daily.first().tempMin,
                            style = TextStyle(
                                fontSize = textFontSize.sp,
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(GlanceModifier.width(5.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        state.locationName,
                        style = TextStyle(
                            color = textColorVariant,
                            fontSize = locationFontSize.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        state.currentTemp,
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = tempFontSize.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )

                }
            }
            Spacer(GlanceModifier.defaultWeight())
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth().background(GlanceTheme.colors.background)
                    .cornerRadius(16.dp)
            ) {
                val hourlyIconSize = 22 * config.iconSize
                val hourlyTextSize = 14 * config.fontSize
                state.hourly.take(count).forEach {
                    WidgetHourlyItem(
                        it.time,
                        it.temp,
                        it.conditionIcon,
                        hourlyIconSize,
                        hourlyTextSize
                    )
                }
            }
        }
    } else {
        ReloadButton()
    }
}
