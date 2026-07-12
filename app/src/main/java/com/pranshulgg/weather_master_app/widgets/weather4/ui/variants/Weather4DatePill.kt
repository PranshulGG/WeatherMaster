package com.pranshulgg.weather_master_app.widgets.weather4.ui.variants

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetDate


@Composable
fun Weather4DatePill(
    config: WidgetConfig,
    widgetColors: WidgetColors,
    context: Context,
    state: WidgetWeather
) {

    val textColor = widgetColors
        .getTextColor(config.widgetTextTheme, config.widgetTheme)
        ?: Pair(GlanceTheme.colors.onSurface, null)


    val iconSize = 48 * config.iconSize
    val textSize = 16 * config.fontSize

    val temp =
        if (config.showDailyInsteadOfCurrent) state.daily[0].tempMax else state.currentTemp
    val condition =
        if (config.showDailyInsteadOfCurrent) state.daily[0].conditionName else state.currentCondition
    val conditionIcon =
        if (config.showDailyInsteadOfCurrent) state.daily[0].conditionIcon else state.currentIcon

    Row(verticalAlignment = Alignment.CenterVertically, modifier = GlanceModifier.fillMaxSize()) {
        Column(
            GlanceModifier.background(GlanceTheme.colors.widgetBackground).cornerRadius(100.dp)
                .padding(horizontal = 16.dp).defaultWeight().fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier.wrapContentSize()
            ) {
                WidgetDate(
                    "EEE, MMM d",
                    color = textColor.second,
                    context = context,
                    size = textSize,
                    hideShadow = true
                )
            }
            Text(
                "$temp $condition",
                style = TextStyle(
                    fontSize = textSize.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
        Spacer(GlanceModifier.width(8.dp))

        Box(
            modifier = GlanceModifier.fillMaxHeight().width((88 * config.iconSize).dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.shape_circle),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    GlanceTheme.colors.widgetBackground
                ),
                modifier = GlanceModifier.fillMaxSize()
            )
            Image(
                provider = ImageProvider(conditionIcon),
                contentDescription = null,
                modifier = GlanceModifier.size(iconSize.dp)
            )
        }
    }
}