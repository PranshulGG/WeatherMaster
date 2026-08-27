package com.pranshulgg.weather_master_app.widgets.froggy

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
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
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlinx.serialization.json.Json


class FroggyWidget : GlanceAppWidget() {

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
            val widgetState =
                currentState<WeatherWidgetStateJson>()
            val widgetColors = WidgetColors()

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

            val textColor = widgetColors
                .getTextColor(config.widgetTextTheme, config.widgetTheme)
                ?: Pair(GlanceTheme.colors.onSurface, null)

            val textColorVariant = widgetColors
                .getTextVariantColor(config.widgetTextTheme, config.widgetTheme)
                ?: GlanceTheme.colors.onSurfaceVariant


            val mainIconSize = 42 * config.iconSize
            val textFontSize = 18 * config.fontSize
            val locationFontSize = 14 * config.fontSize
            val tempFontSize = 30 * config.fontSize
            if (state != null) {

                Box(
                    modifier
                        .clickable(actionStartActivity<MainActivity>())
                ) {
                    Column() {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.fillMaxWidth()
                                .padding(top = 18.dp, start = 18.dp, end = 18.dp)
                        ) {
                            Image(
                                provider = ImageProvider(state.currentIcon),
                                contentDescription = null,
                                modifier = GlanceModifier.size(mainIconSize.dp)
                            )
                            Spacer(GlanceModifier.width(8.dp))
                            Column(GlanceModifier.fillMaxWidth()) {
                                Row(
                                    GlanceModifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        state.currentTemp,
                                        style = TextStyle(
                                            color = textColor.first,
                                            fontSize = tempFontSize.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(GlanceModifier.defaultWeight())
                                    Text(
                                        state.currentCondition,
                                        style = TextStyle(
                                            color = textColor.first,
                                            fontSize = textFontSize.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                Row(
                                    GlanceModifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        state.locationName,
                                        style = TextStyle(
                                            color = textColorVariant,
                                            fontSize = locationFontSize.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(GlanceModifier.defaultWeight())
                                    Text(
                                        "H ${state.daily[0].tempMax} / L ${state.daily[0].tempMin}",
                                        style = TextStyle(
                                            color = textColor.first,
                                            fontSize = textFontSize.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(GlanceModifier.defaultWeight())
                        Box(
                            modifier = GlanceModifier.fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Image(
                                provider = ImageProvider(state.currentFrog),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,

                                modifier = GlanceModifier.fillMaxWidth()
                            )
                        }
                    }


                }
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

