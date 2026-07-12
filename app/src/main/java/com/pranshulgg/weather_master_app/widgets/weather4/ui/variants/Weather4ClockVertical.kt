package com.pranshulgg.weather_master_app.widgets.weather4.ui.variants

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetClock
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetDate


@Composable
fun Weather4ClockVerticalDate(
    config: WidgetConfig,
    widgetColors: WidgetColors,
    context: Context,
    state: WidgetWeather
) {

    val textColor = widgetColors
        .getTextColor(config.widgetTextTheme, config.widgetTheme)
        ?: Pair(GlanceTheme.colors.onSurface, null)

    val iconSize = 20 * config.iconSize
    val clockSize = 64 * config.fontSize
    val textSize = 20 * config.fontSize
    val secondaryTextSize = 16 * config.fontSize


    val temp =
        if (config.showDailyInsteadOfCurrent) "${state.daily[0].tempMax} / ${state.daily[0].tempMin}" else state.currentTemp
    val condition =
        if (config.showDailyInsteadOfCurrent) state.daily[0].conditionName else state.currentCondition
    val conditionIcon =
        if (config.showDailyInsteadOfCurrent) state.daily[0].conditionIcon else state.currentIcon


    Column(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.fillMaxSize()
    ) {
        Box(
            modifier = GlanceModifier.wrapContentSize()
        ) {
            WidgetClock(
                clockSize,
                context,
                textColor.second,
                true,
                format = "hh",
                format24Hr = "HH"
            )
        }
        Box(
            modifier = GlanceModifier.wrapContentSize()
        ) {
            WidgetClock(
                clockSize,
                context,
                textColor.second,
                true,
                format = "mm",
                format24Hr = "mm"
            )
        }
        Box(
            modifier = GlanceModifier.wrapContentSize()
        ) {
            WidgetDate(
                "EEEE, MMM d",
                color = textColor.second,
                context = context,
                size = textSize,
                hideShadow = true
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                provider = ImageProvider(conditionIcon),
                contentDescription = null,
                modifier = GlanceModifier.size(iconSize.dp)
            )
            Spacer(GlanceModifier.width(5.dp))
            Text(
                condition,
                style = TextStyle(
                    fontSize = secondaryTextSize.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.first
                )
            )
            Spacer(GlanceModifier.width(5.dp))
            Text(
                " • $temp",
                style = TextStyle(
                    fontSize = secondaryTextSize.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.first
                )
            )
        }
    }
}