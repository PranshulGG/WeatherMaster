package com.pranshulgg.weather_master_app.widgets.hourly

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import com.pranshulgg.weather_master_app.widgets.weather.components.WidgetHourlyItem
import kotlinx.serialization.json.Json


class WidgetHourly : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact
    override val stateDefinition =
        WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {


        provideContent {
            val widgetState =
                currentState<WeatherWidgetStateJson>()
            val widgetColors = WidgetColors()

            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }
            val config = widgetState.config

            if (state != null) {
                val modifier = when (config.widgetTheme) {
                    WidgetTheme.TRANSPARENT -> GlanceModifier.fillMaxSize()
                    else -> GlanceModifier.fillMaxSize()
                        .appWidgetBackgroundShape(
                            config.widgetTheme,
                            widgetColors
                        )
                }

                val textColor = widgetColors
                    .getTextColor(config.widgetTextTheme, config.widgetTheme)
                    ?: Pair(GlanceTheme.colors.onSurface, null)

                val textColorVariant = widgetColors
                    .getTextVariantColor(config.widgetTextTheme, config.widgetTheme)
                    ?: GlanceTheme.colors.onSurfaceVariant
                Box(
                    modifier.clickable(actionStartActivity<MainActivity>())
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxWidth().fillMaxHeight()
                    ) {
                        val hourlyIconSize = 22 * config.iconSize
                        val hourlyTextSize = 14 * config.fontSize
                        state.hourly.take(config.hourlyCount).forEach {
                            WidgetHourlyItem(
                                it.time,
                                it.temp,
                                it.conditionIcon,
                                hourlyTextSize,
                                hourlyIconSize,
                                textColor.first,
                                textColorVariant,
                                verticalPadding = 10.dp,
                                precipitationProbability = if (config.showPrecipitationProbability)
                                    it.precipitationProbability else null
                            )
                        }
                    }

                }
            } else {
                ReloadButton()
            }
        }
    }

}


@Composable
private fun GlanceModifier.appWidgetBackgroundShape(
    theme: WidgetTheme,
    widgetColors: WidgetColors
): GlanceModifier {


    val color = widgetColors.getBackgroundColor(
        theme
    )


    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .background(color ?: GlanceTheme.colors.widgetBackground)
    } else {
        this
            .background(
                ImageProvider(R.drawable.weather_widget_background),
                colorFilter = ColorFilter.tint(color ?: GlanceTheme.colors.widgetBackground)
            )
    }
}
