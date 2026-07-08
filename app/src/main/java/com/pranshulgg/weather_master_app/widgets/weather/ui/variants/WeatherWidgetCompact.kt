package com.pranshulgg.weather_master_app.widgets.weather.ui.variants

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
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors


@Composable
fun WeatherWidgetCompact(
    state: WidgetWeather?,
    widgetColors: WidgetColors,
    config: WidgetConfig
) {


    val textColor = widgetColors
        .getTextColor(config.widgetTextTheme, config.widgetTheme)
        ?: Pair(GlanceTheme.colors.onSurface, null)

    val textColorVariant = widgetColors
        .getTextVariantColor(config.widgetTextTheme, config.widgetTheme)
        ?: GlanceTheme.colors.onSurfaceVariant

    if (state != null) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(18.dp),
        ) {


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Image(
                    provider = ImageProvider(state.currentIcon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(28.dp)
                )
                Text(
                    state.currentCondition,
                    style = TextStyle(
                        color = textColor.first,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                    maxLines = 1
                )
            }
            Spacer(GlanceModifier.defaultWeight())

            Text(
                state.currentTemp,
                style = TextStyle(
                    color = textColor.first,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                )
            )

            Row() {
                Text(
                    state.daily.first().tempMax,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = textColor.first,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(GlanceModifier.width(8.dp))
                Text(
                    state.daily.first().tempMin,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = textColorVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    } else {
        ReloadButton()
    }
}
