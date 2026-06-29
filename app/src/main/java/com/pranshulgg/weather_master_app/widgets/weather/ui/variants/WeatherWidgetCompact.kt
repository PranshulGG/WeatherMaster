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
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton


@Composable
fun WeatherWidgetCompact(state: WidgetWeather?) {

    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant


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
                        color = textColor,
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
                    color = GlanceTheme.colors.primary,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
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
    } else {
        ReloadButton()
    }
}
