package com.pranshulgg.weather_master_app.widgets.pill.ui

import android.os.Build
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
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetMinMaxTemp


@Composable
fun WeatherWidgetPillNormal(state: WidgetWeather?) {
    val textColor = GlanceTheme.colors.onSurface
    val textColorVariant = GlanceTheme.colors.onSurfaceVariant

    if (state != null)

        Row(
            modifier = GlanceModifier.fillMaxSize()
                .padding(horizontal = 16.dp).appWidgetBackgroundCircleShape(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                provider = ImageProvider(state.currentIcon),
                contentDescription = null,
                modifier = GlanceModifier.size(100.dp)
            )
            Spacer(GlanceModifier.width(12.dp))
            Column() {
                Text(
                    state.currentTemp,
                    style = TextStyle(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 68.sp
                    )
                )
                WidgetMinMaxTemp(state.daily.first().tempMin, state.daily.first().tempMax)
            }
        }

}


@Composable
private fun GlanceModifier.appWidgetBackgroundCircleShape(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .cornerRadius(999.dp)
            .background(GlanceTheme.colors.widgetBackground)
    } else {
        this
            .background(ImageProvider(R.drawable.shape_circle))
    }
}