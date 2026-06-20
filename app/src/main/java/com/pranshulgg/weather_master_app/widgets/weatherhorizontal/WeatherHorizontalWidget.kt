package com.pranshulgg.weather_master_app.widgets.weatherhorizontal


import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
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
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSize
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
import com.pranshulgg.weather_master_app.widgets.params.getWidgetParams
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.WeatherWidgetHorizontal
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.WeatherWidgetHorizontalCompact
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.WeatherWidgetHorizontalMini
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
            val widgetParams = getWidgetParams(size)
            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            Box(
                GlanceModifier.fillMaxSize().appWidgetBackgroundShape()
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                when (widgetParams.size) {
                    WidgetSize.LARGE -> WeatherWidgetHorizontal(state)
                    WidgetSize.SMALL, WidgetSize.TINY -> WeatherWidgetHorizontalMini(state)
                    else -> WeatherWidgetHorizontalCompact(state)
                }
            }
        }
    }

}


@Composable
private fun GlanceModifier.appWidgetBackgroundShape(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .background(GlanceTheme.colors.widgetBackground)
    } else {
        this
            .background(ImageProvider(R.drawable.weather_widget_background))
    }
}



