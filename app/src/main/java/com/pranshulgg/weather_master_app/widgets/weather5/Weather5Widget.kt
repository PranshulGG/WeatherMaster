package com.pranshulgg.weather_master_app.widgets.weather5

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
import com.pranshulgg.weather_master_app.widgets.params.getWidgetParams
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlinx.serialization.json.Json


class Weather5Widget : GlanceAppWidget() {

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

            val config = widgetState.config

            val modifier = when (config.widgetTheme) {
                WidgetTheme.TRANSPARENT -> GlanceModifier.fillMaxSize()
                else -> GlanceModifier.fillMaxSize()
                    .appWidgetBackgroundShape(config.widgetTheme, widgetColors)
            }

            if (state != null) {


                val textColor = widgetColors
                    .getTextColor(config.widgetTextTheme, config.widgetTheme)
                    ?: Pair(GlanceTheme.colors.onSurface, null)

                val textColorVariant = widgetColors
                    .getTextVariantColor(config.widgetTextTheme, config.widgetTheme)
                    ?: GlanceTheme.colors.onSurfaceVariant

                val mainTempFontSize = 42 * config.fontSize
                val conditionFontSize = 18 * config.fontSize
                val mainIconSize = 52 * config.iconSize
                val dailyFontSize = 16 * config.fontSize
                val dailyIconSize = 24 * config.iconSize

                val dailyItem: @Composable (String, String, Int) -> Unit = { weekday, temps, icon ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.fillMaxWidth()
                    ) {
                        Text(
                            weekday, style = TextStyle(
                                color = textColor.first,
                                fontSize = dailyFontSize.sp
                            )
                        )
                        Spacer(GlanceModifier.defaultWeight())
                        Image(
                            provider = ImageProvider(icon),
                            contentDescription = null,
                            modifier = GlanceModifier.size(dailyIconSize.dp)
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            temps, style = TextStyle(
                                color = textColor.first,
                                fontSize = dailyFontSize.sp
                            )
                        )
                    }
                }

                Box(
                    modifier.clickable(actionStartActivity<MainActivity>())
                ) {
                    Column(modifier = GlanceModifier.fillMaxSize().padding(16.dp)) {
                        Row(GlanceModifier.fillMaxWidth()) {
                            Column() {
                                Text(
                                    state.currentCondition,
                                    style = TextStyle(
                                        color = textColorVariant,
                                        fontSize = conditionFontSize.sp
                                    )
                                )
                                Text(
                                    state.currentTemp,
                                    style = TextStyle(
                                        color = textColor.first,
                                        fontSize = mainTempFontSize.sp
                                    )
                                )
                            }
                            Spacer(GlanceModifier.defaultWeight())
                            Image(
                                provider = ImageProvider(state.currentIcon),
                                contentDescription = null,
                                modifier = GlanceModifier.size(mainIconSize.dp)
                            )
                        }
                        Spacer(GlanceModifier.defaultWeight())
                        state.daily.take(3).forEach {
                            dailyItem(it.time, it.tempMax + "/" + it.tempMin, it.conditionIcon)
                            Spacer(GlanceModifier.height(5.dp))
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
