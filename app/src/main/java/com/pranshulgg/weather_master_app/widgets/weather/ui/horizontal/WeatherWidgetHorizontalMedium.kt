package com.pranshulgg.weather_master_app.widgets.weather.ui.horizontal

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
fun WeatherWidgetHorizontalMedium(
    state: WidgetWeather?,
    modifier: GlanceModifier = GlanceModifier
) {
    val textColor = GlanceTheme.colors.onSurface

    if (state != null)

        Row(
            modifier = modifier.fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                provider = ImageProvider(state.currentIcon),
                contentDescription = null,
                modifier = GlanceModifier.size(34.dp)
            )
            Spacer(GlanceModifier.width(8.dp))
            Text(
                state.currentTemp,
                style = TextStyle(color = textColor, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            )
            Spacer(GlanceModifier.defaultWeight())
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    state.currentCondition,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                )
                Spacer(GlanceModifier.height(3.dp))
                WidgetMinMaxTemp(state.daily.first().tempMin, state.daily.first().tempMax, true)
            }

        }

}

