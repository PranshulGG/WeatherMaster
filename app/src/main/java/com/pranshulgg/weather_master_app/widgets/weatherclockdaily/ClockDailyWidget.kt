package com.pranshulgg.weather_master_app.widgets.weatherclockdaily

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import android.util.Log
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
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.action.actionStartActivity
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
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetColors
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetClock
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetDate
import kotlinx.serialization.json.Json
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt


class ClockDailyWidget : GlanceAppWidget() {

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
            if (state != null) {

                val textColor = widgetColors
                    .getTextColor(config.widgetTextTheme, config.widgetTheme)
                    ?: Pair(GlanceTheme.colors.onSurface, null)


                val clockFontSize = 50 * config.fontSize
                val dateConditionFontSize = 18 * config.fontSize
                val mainIconSize = 52 * config.iconSize


                Box(
                    modifier.clickable(actionStartActivity<MainActivity>())
                ) {
                    Column(
                        GlanceModifier.fillMaxSize()
                            .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 12.dp)
                    ) {
                        Row(
                            GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = GlanceModifier.wrapContentSize()
                                    .clickable(onClick = openAvailableClockApp())
                            ) {
                                WidgetClock(clockFontSize, context, color = textColor.second)
                            }

                            Spacer(GlanceModifier.defaultWeight())
                            Image(
                                provider = ImageProvider(state.currentIcon),
                                contentDescription = null,
                                modifier = GlanceModifier.size(mainIconSize.dp)
                            )
                        }
                        Row(GlanceModifier.fillMaxWidth()) {
                            Box(
                                modifier = GlanceModifier.wrapContentSize()
                                    .clickable(openAvailableCalendarApp())
                            ) {
                                WidgetDate(
                                    config.dateFormat,
                                    context,
                                    color = textColor.second,
                                    dateConditionFontSize
                                )
                            }
                            Spacer(GlanceModifier.width(16.dp))
                            Text(
                                "${state.currentTemp} ${state.currentCondition}",
                                style = TextStyle(
                                    color = textColor.first,
                                    fontSize = dateConditionFontSize.sp,
                                    textAlign = TextAlign.End
                                ),
                                modifier = GlanceModifier.defaultWeight(),
                                maxLines = 1
                            )
                        }
                        Spacer(GlanceModifier.defaultWeight())

                        Row(
                            GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            state.daily.take(config.dailyCount).forEach {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = GlanceModifier.defaultWeight()
                                        .padding(vertical = 5.dp)
                                ) {
                                    DailyItem(
                                        day = it.time,
                                        icon = it.conditionIcon,
                                        temps = "${it.tempMax}/${it.tempMin}",
                                        textColor = textColor,
                                        config
                                    )
                                }
                            }
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

private fun openAvailableClockApp(): Action =
    actionStartActivity(
        Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )

private fun openAvailableCalendarApp(): Action {
    return actionStartActivity(
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_CALENDAR)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

@Composable
private fun DailyItem(
    day: String,
    icon: Int,
    temps: String,
    textColor: Pair<ColorProvider, Int?>,
    config: WidgetConfig
) {
    val fontSize = 15 * config.fontSize

    val iconSize = 28 * config.iconSize

    Text(
        day,
        style = TextStyle(
            color = textColor.first,
            fontWeight = FontWeight.Medium,
            fontSize = fontSize.sp
        )
    )
    Spacer(GlanceModifier.height(3.dp))
    Image(
        provider = ImageProvider(icon),
        contentDescription = "",
        modifier = GlanceModifier.size(iconSize.dp)
    )
    Spacer(GlanceModifier.height(3.dp))
    Text(
        temps,
        style = TextStyle(
            color = textColor.first,
            fontWeight = FontWeight.Medium, fontSize = fontSize.sp
        )
    )

}