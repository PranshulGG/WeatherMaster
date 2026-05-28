package com.pranshulgg.weather_master_app.widgets.pill

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
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
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizeType
import com.pranshulgg.weather_master_app.widgets.params.getWidgetParams
import com.pranshulgg.weather_master_app.widgets.pill.ui.WeatherWidgetPillMedium
import com.pranshulgg.weather_master_app.widgets.pill.ui.WeatherWidgetPillNormal
import com.pranshulgg.weather_master_app.widgets.pill.ui.WeatherWidgetPillSmall
import com.pranshulgg.weather_master_app.widgets.pill.ui.WeatherWidgetPillTiny
import com.pranshulgg.weather_master_app.widgets.weather.ui.horizontal.WeatherWidgetHorizontalLarge
import com.pranshulgg.weather_master_app.widgets.weather.ui.horizontal.WeatherWidgetHorizontalMedium
import com.pranshulgg.weather_master_app.widgets.weather.ui.horizontal.WeatherWidgetHorizontalSmall
import com.pranshulgg.weather_master_app.widgets.weather.ui.horizontal.WeatherWidgetHorizontalTiny
import kotlinx.serialization.json.Json


class WidgetPill : GlanceAppWidget() {

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

            val json = widgetState.json
            val widgetParams = getWidgetParams(size)
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            Box(
                GlanceModifier.fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>())
            ) {

                if (widgetParams.type == WidgetSizeType.HORIZONTAL) {
                    when (widgetParams.size) {
                        WidgetSize.TINY -> WeatherWidgetHorizontalTiny(
                            state,
                            GlanceModifier.appWidgetBackgroundCircleShape()
                        )

                        WidgetSize.SMALL -> WeatherWidgetHorizontalSmall(
                            state,
                            GlanceModifier.appWidgetBackgroundCircleShape()
                        )

                        WidgetSize.MEDIUM -> WeatherWidgetHorizontalMedium(
                            state,
                            GlanceModifier.appWidgetBackgroundCircleShape()
                        )

                        WidgetSize.LARGE -> WeatherWidgetHorizontalLarge(
                            state,
                            GlanceModifier.appWidgetBackgroundCircleShape()
                        )
                    }
                } else {
                    when (widgetParams.size) {
                        WidgetSize.TINY -> WeatherWidgetPillTiny(state, size)
                        WidgetSize.SMALL -> WeatherWidgetPillSmall(state, size)
                        WidgetSize.MEDIUM -> WeatherWidgetPillMedium(state, size)
                        WidgetSize.LARGE -> WeatherWidgetPillNormal(state)
                    }
                }
            }
        }
    }

}


@Composable
private fun GlanceModifier.appWidgetBackgroundCircleShape(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .cornerRadius(999.dp)
            .background(GlanceTheme.colors.widgetBackground)
    } else {
        this
            .background(ImageProvider(R.drawable.shape_circle))
    }
}