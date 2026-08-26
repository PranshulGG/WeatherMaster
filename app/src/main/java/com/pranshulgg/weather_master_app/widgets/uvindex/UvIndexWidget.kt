package com.pranshulgg.weather_master_app.widgets.uvindex

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
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
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.uv.UvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.uvindex.ui.variants.UvIndexWidgetCompact
import com.pranshulgg.weather_master_app.widgets.uvindex.ui.variants.UvIndexWidgetLarge
import kotlinx.serialization.json.Json

class UvIndexWidget : GlanceAppWidget() {

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

            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }
            val config = widgetState.config
            if (state != null) {

                Box(
                    GlanceModifier.fillMaxSize()
                        .clickable(actionStartActivity<MainActivity>())
                ) {
                    when (config.variant) {
                        WidgetVariant.LARGE -> UvIndexWidgetLarge(
                            state,
                            GlanceModifier.appWidgetBackgroundShape(getUvIndex(state.uvIndex ?: 0))
                        )

                        else -> UvIndexWidgetCompact(
                            state,
                            GlanceModifier.appWidgetBackgroundShape(getUvIndex(state.uvIndex ?: 0))
                        )
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
    uvIndex: UvIndex
): GlanceModifier {


    val color = getUvIndexColorForWidget(uvIndex)


    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .background(color)
    } else {
        this
            .background(
                ImageProvider(R.drawable.weather_widget_background),
                colorFilter = ColorFilter.tint(color)
            )
    }
}

fun getUvIndexColorForWidget(uvIndex: UvIndex): ColorProvider {

    val color = when (uvIndex) {
        UvIndex.LOW -> Color(0xFF4CAF50)
        UvIndex.MODERATE -> Color(0xFFFFC107)
        UvIndex.HIGH -> Color(0xFFFF9800)
        UvIndex.VERY_HIGH -> Color(0xFFF44336)
        UvIndex.EXTREME -> Color(0xFF9C27B0)
    }

    return ColorProvider(color)
}

fun getUvIndexTextColorForWidget(uvIndex: UvIndex): ColorProvider {

    val color = when (uvIndex) {
        UvIndex.LOW -> Color(0xFF0F3D0F)
        UvIndex.MODERATE -> Color(0xFF5C4300)
        UvIndex.HIGH -> Color(0xFFFFF4E0)
        UvIndex.VERY_HIGH -> Color(0xFFFFEBEE)
        UvIndex.EXTREME -> Color(0xFFFFE4F1)
    }

    return ColorProvider(color)
}

fun getUvIndexSecondaryTextColorForWidget(uvIndex: UvIndex): ColorProvider {
    val color = when (uvIndex) {
        UvIndex.LOW -> Color(0xFF123B14)
        UvIndex.MODERATE -> Color(0xFF4A3900)
        UvIndex.HIGH -> Color(0xFF4A2600)
        UvIndex.VERY_HIGH -> Color(0xFFFFFFFF)
        UvIndex.EXTREME -> Color(0xFFFFFFFF)
    }

    return ColorProvider(color)
}