package com.pranshulgg.weather_master_app.widgets.uvindex.ui.variants

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
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
fun UvIndexWidgetCompact(
    state: WidgetWeather,
    glanceModifier: GlanceModifier
) {
    val context = LocalContext.current


    val textColor = getUvIndexTextColorForWidget(getUvIndex(state.uvIndex ?: 0))
    val textColorVariant = getUvIndexSecondaryTextColorForWidget(getUvIndex(state.uvIndex ?: 0))



    Column(
        modifier = glanceModifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            context.getString(R.string.weather_current),
            style = TextStyle(
                color = textColorVariant,
                fontSize = 18.sp,
            ),
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "${state.uvIndex ?: "--"}",
                style = TextStyle(
                    color = textColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
            )
            Spacer(GlanceModifier.width(5.dp))
            Text(
                state.uvIndex?.let {
                    getUvIndex(it).toLabel(context)
                } ?: "--",
                style = TextStyle(
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
            )
        }
    }
}