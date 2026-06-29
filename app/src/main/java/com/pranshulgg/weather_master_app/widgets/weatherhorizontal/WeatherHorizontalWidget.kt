package com.pranshulgg.weather_master_app.widgets.weatherhorizontal


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
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.variants.WeatherWidgetHorizontal
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.variants.WeatherWidgetHorizontalCompact
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.variants.WeatherWidgetHorizontalSmall
import kotlinx.serialization.json.Json


class WeatherHorizontalWidget : GlanceAppWidget() {

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
                val modifier = when (config.widgetTheme) {
                    WidgetTheme.TRANSPARENT -> GlanceModifier.fillMaxSize()
                    else -> GlanceModifier.fillMaxSize()
                        .appWidgetBackgroundShape(config.widgetTheme, widgetColors)
                }
                Box(
                    modifier.clickable(actionStartActivity<MainActivity>())
                ) {
                    when (config.variant) {
                        WidgetVariant.LARGE -> WeatherWidgetHorizontal(state, config = config)
                        WidgetVariant.COMPACT -> WeatherWidgetHorizontalCompact(
                            state,
                            config = config
                        )

                        else -> WeatherWidgetHorizontalSmall(state, config = config)
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


    val color = widgetColors.getBackgroundColor(theme)


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



