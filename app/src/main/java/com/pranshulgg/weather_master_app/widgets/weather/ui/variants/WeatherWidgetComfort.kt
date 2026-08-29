package com.pranshulgg.weather_master_app.widgets.weather.ui.variants

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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors

@Composable
fun WeatherWidgetComfort(
    state: WidgetWeather?, config: WidgetConfig,
    widgetColors: WidgetColors
) {
    val textColor = widgetColors
        .getTextColor(config.widgetTextTheme, config.widgetTheme)
        ?: Pair(GlanceTheme.colors.onSurface, null)

    val textColorVariant = widgetColors
        .getTextVariantColor(config.widgetTextTheme, config.widgetTheme)
        ?: GlanceTheme.colors.onSurfaceVariant

    if (state != null) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(18.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(state.currentIcon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(28.dp)
                )
                Spacer(GlanceModifier.defaultWeight())
                Text(
                    state.locationName,
                    style = TextStyle(
                        color = textColor.first,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
            }

            Spacer(GlanceModifier.height(8.dp))

            Text(
                state.currentTemp,
                style = TextStyle(
                    color = textColor.first,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(GlanceModifier.defaultWeight())

            val daily = state.daily.firstOrNull()
            if (daily != null) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        daily.tempMax,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = textColor.first,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(GlanceModifier.width(8.dp))
                    Text(
                        daily.tempMin,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = textColorVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    } else {
        ReloadButton()
    }
}