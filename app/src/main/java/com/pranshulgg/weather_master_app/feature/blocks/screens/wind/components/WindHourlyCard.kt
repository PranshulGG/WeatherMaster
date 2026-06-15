package com.pranshulgg.weather_master_app.feature.blocks.screens.wind.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun WindHourlyCard(
    data: List<WeatherHourly>,
    zoneId: String,
    unit: WindSpeedUnit,
    context: Context
) {

    val prefs = LocalAppPrefs.current

    val timeFormatter: (Long) -> String = {
        if (prefs.is24HrTimeFormat) to24HourTimeString(
            it,
            zoneId
        ) else to12HourTimeString(
            it,
            zoneId
        )
    }


    val max = data.maxOf { it.windSpeed!! }
    val min = data.minOf { it.windSpeed!! }
    val formatter: (Double) -> Double? = {
        WindSpeedUnit.KPH.convert(it, unit)
    }
    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            CardsHeader(
                "${stringResource(R.string.weather_hourly_forecast)} (${
                    unit.toName(
                        context,
                        true
                    )
                })",
                R.drawable.schedule_48px
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.height(200.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                items(
                    data.size,
                    key = { "${data[it].time}_$it" }) { index ->

                    val item = data[index]

                    val percentage = ((item.windSpeed!!.minus(min)).div((max - min))).times(100)

                    val barHeight = max((percentage.div(100)).times(140).roundToInt(), 48)

                    val barColor = when {
                        item.windSpeed < 10 -> Color(0xFF42A5F5)
                        item.windSpeed < 20 -> Color(0xFF66BB6A)
                        item.windSpeed < 40 -> Color(0xFFFDD835)
                        item.windSpeed < 60 -> Color(0xFFFB8C00)
                        else -> Color(0xFFC62828)
                    }

                    if (index == 0) Gap(horizontal = 16.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(contentAlignment = Alignment.BottomCenter) {
                            Surface(
                                Modifier
                                    .width(18.dp)
                                    .height(140.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            ) {

                            }
                            Surface(
                                Modifier
                                    .width(38.dp)
                                    .height(barHeight.dp),
                                color = barColor,
                                shape = CircleShape
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(top = 6.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    if (item.windDirection != null) {
                                        Image(
                                            painter = painterResource(id = R.drawable.weather_wind_arrow_dominant),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .rotate(
                                                    WindDirection.toDegrees(item.windDirection)
                                                        ?.toFloat() ?: 0f
                                                ),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface)
                                        )
                                    }
                                }
                            }
                        }
                        Gap(5.dp)
                        Text(
                            "${formatter(item.windSpeed)?.roundToInt()}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            timeFormatter(item.time),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    if (index == data.size - 1) Gap(horizontal = 16.dp)
                }
            }
        }
    }
}