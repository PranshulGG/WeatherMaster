package com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.variants

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme

@Composable
fun WeatherWidgetHorizontal(
    state: WidgetWeather?,
    modifier: GlanceModifier = GlanceModifier, config: WidgetConfig
) {

    val size = 18 * config.fontSize
    val fontSizeLocation = 16 * config.fontSize
    val tempSize = 40 * config.fontSize

    val textColor = if (config.widgetTheme == WidgetTheme.TRANSPARENT)
        ColorProvider(R.color.white) else GlanceTheme.colors.onSurface

    val textColorSecondary = if (config.widgetTheme == WidgetTheme.TRANSPARENT)
        ColorProvider(R.color.white_secondary) else GlanceTheme.colors.onSurfaceVariant
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
            Column(GlanceModifier.defaultWeight()) {
                Text(
                    state.locationName,
                    style = TextStyle(
                        color = textColor,
                        fontSize = fontSizeLocation.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start
                    )
                )
                Text(
                    state.currentCondition,
                    style = TextStyle(
                        color = textColor,
                        fontSize = size.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    ),
                    maxLines = 1,
                    modifier = GlanceModifier.fillMaxWidth()
                )
            }
            Spacer(GlanceModifier.width(16.dp))
            Text(
                state.currentTemp,
                style = TextStyle(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = tempSize.sp
                )
            )
            Spacer(GlanceModifier.width(8.dp))
            Column() {
                Text(
                    state.daily.first().tempMax,
                    style = TextStyle(
                        fontSize = size.sp,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    state.daily.first().tempMin,
                    style = TextStyle(
                        fontSize = size.sp,
                        color = textColorSecondary,
                        fontWeight = FontWeight.Medium
                    ),
                )
            }
        }
}

