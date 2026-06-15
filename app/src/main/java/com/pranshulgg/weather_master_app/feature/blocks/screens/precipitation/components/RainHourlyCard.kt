package com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toColor
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.formatLocalizedNumber
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun RainHourlyCard(
    data: List<WeatherHourly>,
    zoneId: String,
    unit: PrecipitationUnit,
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

    val max = data.maxOf { it.rain }
    val min = data.minOf { it.rain }
    val formatter: (Double) -> Double? = {
        PrecipitationUnit.MM.convert(it, unit)
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
                })", R.drawable.schedule_48px
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.height(230.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                items(
                    data.size,
                    key = { "${data[it].time}_$it" }) { index ->

                    val item = data[index]

                    val percentage = ((item.rain.minus(min)).div((max - min))).times(100)

                    val barHeight = if (!percentage.isNaN()) max(
                        (percentage.div(100)).times(140).roundToInt(),
                        5
                    ) else 5

                    val barColor = when {
                        item.rain <= 0.0 -> Color(0xFFE0E0E0)

                        item.rain < 1.0 -> Color(0xFFB3E5FC)
                        item.rain < 2.5 -> Color(0xFF4FC3F7)
                        item.rain < 7.5 -> Color(0xFF1E88E5)
                        item.rain < 20.0 -> Color(0xFF1565C0)
                        item.rain < 50.0 -> Color(0xFF6A1B9A)
                        else -> Color(0xFFD50000)
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
                            ) {}
                            Surface(
                                Modifier
                                    .width(38.dp)
                                    .height(barHeight.dp),
                                color = barColor,
                                shape = CircleShape
                            ) {}
                        }
                        Gap(5.dp)
                        Text(
                            formatLocalizedNumber(
                                getCurrentAppLocale(),
                                formatter(item.rain) ?: 0.0,
                                1
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${item.precipitationProbability}%",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall,
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