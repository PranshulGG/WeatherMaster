package com.pranshulgg.weather_master_app.widgets.uvindex.ui.variants

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.uv.UvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toLabel
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import com.pranshulgg.weather_master_app.widgets.uvindex.getUvIndexColorForWidget
import com.pranshulgg.weather_master_app.widgets.uvindex.getUvIndexSecondaryTextColorForWidget
import com.pranshulgg.weather_master_app.widgets.uvindex.getUvIndexTextColorForWidget


@Composable
fun UvIndexWidgetLarge(
    state: WidgetWeather?,
    glanceModifier: GlanceModifier
) {
    val context = LocalContext.current

    if (state != null) {


        val textColor = getUvIndexTextColorForWidget(getUvIndex(state.uvIndex ?: 0))
        val textColorVariant = getUvIndexSecondaryTextColorForWidget(getUvIndex(state.uvIndex ?: 0))

        Column(modifier = glanceModifier.fillMaxSize().padding(16.dp)) {
            Text(
                "Current",
                style = TextStyle(
                    color = textColorVariant,
                    fontSize = 18.sp,
                ),
            )
            Text(
                state.uvIndex?.let {
                    getUvIndex(it).toLabel(context)
                } ?: "No data",
                style = TextStyle(
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                ),
            )
            Text(
                "${state.uvIndex ?: "No data"}",
                style = TextStyle(
                    color = textColor,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
            )
            Spacer(GlanceModifier.defaultWeight())
            Text(
                "Today's max: ${state.daily[0].maxUvIndex}",
                style = TextStyle(
                    color = textColorVariant,
                    fontSize = 20.sp,
                ),
            )
            Text(
                "At around ${state.daily[0].maxUvIndexAt}",
                style = TextStyle(
                    color = textColorVariant,
                    fontSize = 18.sp,
                ),
            )
        }

    } else {
        ReloadButton()
    }

}
