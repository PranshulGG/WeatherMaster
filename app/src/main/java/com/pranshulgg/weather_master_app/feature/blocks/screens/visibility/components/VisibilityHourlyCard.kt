package com.pranshulgg.weather_master_app.feature.blocks.screens.visibility.components

import android.content.Context
import android.util.Log
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
import androidx.glance.LocalContext
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun VisibilityHourlyCard(
    data: List<WeatherHourly>,
    zoneId: String,
    unit: DistanceUnit,
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


    val formatter: (Int) -> Double? = {
        DistanceUnit.M.convert(it.toDouble(), unit)
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

                    val visibilityMin = 0.0
                    val visibilityMax = 50000.0
                    val percentage = ((item.visibility!!.minus(visibilityMin))
                        .div((visibilityMax - visibilityMin))).times(100.0)

                    val barHeight = max((percentage.div(100.0)).times(140.0), 5.0)


                    val visibilityKm =
                        DistanceUnit.M.convert(item.visibility.toDouble(), DistanceUnit.KM)
                            ?.roundToInt()!!

                    val barColor = when {
                        visibilityKm < 1 -> Color(0xFFC62828)
                        visibilityKm < 5 -> Color(0xFFFB8C00)
                        visibilityKm < 10 -> Color(0xFFFDD835)
                        visibilityKm < 20 -> Color(0xFF43A047)
                        else -> Color(0xFF1565C0)
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

                            }
                        }
                        Gap(5.dp)
                        Text(
                            "${formatter(item.visibility)?.roundToInt()}",
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