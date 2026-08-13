package com.pranshulgg.weather_master_app.widgets.uvindex.ui.variants

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toLabel
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
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
                stringResource(R.string.weather_current),
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
                stringResource(
                    R.string.widget_weather_today_max_uv,
                    "${state.daily[0].maxUvIndex}"
                ),
                style = TextStyle(
                    color = textColorVariant,
                    fontSize = 20.sp,
                ),
            )
            Text(
                stringResource(
                    R.string.widget_weather_today_max_uv_at,
                    "${state.daily[0].maxUvIndexAt}"
                ),
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
