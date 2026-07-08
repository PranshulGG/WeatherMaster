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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors


@Composable

fun WeatherWidgetSmall(
    state: WidgetWeather?, config: WidgetConfig,
    widgetColors: WidgetColors
) {
    val textColor = widgetColors
        .getTextColor(config.widgetTextTheme, config.widgetTheme)
        ?: Pair(GlanceTheme.colors.onSurface, null)

    val fontSize = 54 * config.fontSize
    val iconSize = 58 * config.iconSize
    if (state != null) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                provider = ImageProvider(state.currentIcon),
                contentDescription = null,
                modifier = GlanceModifier.size(iconSize.dp)
            )
            Text(
                state.currentTemp,
                style = TextStyle(
                    color = textColor.first,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                )
            )


        }
    } else {
        ReloadButton()
    }
}
