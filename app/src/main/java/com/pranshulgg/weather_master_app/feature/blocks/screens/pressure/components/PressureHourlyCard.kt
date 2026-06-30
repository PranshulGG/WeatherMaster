package com.pranshulgg.weather_master_app.feature.blocks.screens.pressure.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.glance.LocalContext
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.formatLocalizedNumber
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import kotlin.collections.average
import kotlin.collections.map
import kotlin.collections.mapIndexed
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun PressureHourlyCard(
    data: List<WeatherHourly>,
    zoneId: String,
    unit: PressureUnit,
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

    val formatter: (Double) -> Double? = {
        PressureUnit.HPA.convert(it, unit)
    }

    val pressure = data.map { it.pressureMsl }

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
                        true,
                        context
                    )
                })", R.drawable.schedule_48px
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


                    val chartMin = 980.0
                    val chartMax = 1040.0

                    val percentage =
                        ((item.pressureMsl!!.roundToInt() - chartMin) / (chartMax - chartMin))
                            .coerceIn(0.0, 1.0)

                    val barHeight = max((percentage * 140).roundToInt(), 48)


                    val barColor = when {
                        item.pressureMsl < 980 -> Color(0xFF4A148C)
                        item.pressureMsl < 995 -> Color(0xFF1565C0)
                        item.pressureMsl < 1010 -> Color(0xFF43A047)
                        item.pressureMsl < 1025 -> Color(0xFFF9A825)
                        else -> Color(0xFFC62828)
                    }

                    val previousPressure = if (index > 0) pressure[index - 1] else item.pressureMsl
                    val pressureDifference = item.pressureMsl - previousPressure!!

                    val pressureIcon =
                        when {
                            pressureDifference > 0.5 -> R.drawable.trending_up_24px
                            pressureDifference < -0.5 -> R.drawable.trending_down_24px
                            else -> R.drawable.trending_flat_24px
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
                                    Symbol(pressureIcon, color = MaterialTheme.colorScheme.surface)
                                }
                            }
                        }
                        Gap(5.dp)
                        Text(
                            item.pressureMsl.roundToInt().toString(),
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