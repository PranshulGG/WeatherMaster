package com.pranshulgg.weather_master_app.widgets.weather4

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
import com.pranshulgg.weather_master_app.widgets.params.getWidgetParams
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import com.pranshulgg.weather_master_app.widgets.weather4.ui.variants.Weather4ClockDate
import com.pranshulgg.weather_master_app.widgets.weather4.ui.variants.Weather4ClockHorizontal
import com.pranshulgg.weather_master_app.widgets.weather4.ui.variants.Weather4ClockVerticalDate
import com.pranshulgg.weather_master_app.widgets.weather4.ui.variants.Weather4Date
import com.pranshulgg.weather_master_app.widgets.weather4.ui.variants.Weather4DatePill
import kotlinx.serialization.json.Json


class Weather4Widget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        sizes = WidgetSizePoints.SIZES

    )
    override val stateDefinition =
        WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {


        provideContent {
            val size = LocalSize.current
            val widgetState =
                currentState<WeatherWidgetStateJson>()
            val widgetColors = WidgetColors()

            val widgetParams = getWidgetParams(size)
            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }
            if (state != null) {
                val config = widgetState.config
                Box(
                    GlanceModifier.fillMaxSize().clickable(actionStartActivity<MainActivity>())
                ) {
                    when (config.variant) {
                        WidgetVariant.DATE -> Weather4Date(config, widgetColors, context, state)
                        WidgetVariant.DATE_PILL -> Weather4DatePill(
                            config,
                            widgetColors,
                            context,
                            state
                        )

                        WidgetVariant.CLOCK_DATE -> Weather4ClockDate(
                            config,
                            widgetColors,
                            context,
                            state
                        )

                        WidgetVariant.CLOCK_VERTICAL -> Weather4ClockVerticalDate(
                            config,
                            widgetColors,
                            context,
                            state
                        )

                        WidgetVariant.CLOCK_HORIZONTAL -> Weather4ClockHorizontal(
                            config,
                            widgetColors,
                            context,
                            state
                        )

                        else -> Weather4Date(config, widgetColors, context, state)
                    }
                }
            } else {
                ReloadButton()
            }
        }
    }
}

