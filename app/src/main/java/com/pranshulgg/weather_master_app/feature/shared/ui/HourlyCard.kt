package com.pranshulgg.weather_master_app.feature.shared.ui

import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherHourlyDomainSafe
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingDaily
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun HourlyCard(
    weather: Weather,
    units: WeatherUnits,
    currentMilli: Long = weather.current.time
) {

    if (!isWeatherHourlyDomainSafe(weather)) return


    val lazyListState = rememberLazyListState()
    val filteredHourly =
        findMatchingHourly(
            weather.hourly,
            currentMilli,
            weather.location.source,

            )


    val prefs = LocalAppPrefs.current

    val is24hr = prefs.is24HrTimeFormat

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {

            CardsHeader(stringResource(R.string.weather_hourly_forecast), R.drawable.schedule_48px)

            if (filteredHourly.isEmpty()) {
                NoHourlyDataAvailable()
                return@Column
            }
            LazyRow(state = lazyListState) {
                items(
                    filteredHourly.size,
                    key = { "${filteredHourly[it].time}_$it" }) { index ->
                    val time = if (is24hr) to24HourTimeString(
                        filteredHourly[index].time,
                        weather.location.timezone
                    ) else to12HourTimeString(
                        filteredHourly[index].time,
                        weather.location.timezone
                    )


                    val item = filteredHourly[index]


                    val temperature =
                        TemperatureUnit.CELSIUS.convert(item.temperature, units.tempUnit)

                    val matchingDaily = findMatchingDaily(
                        item.time,
                        weather.daily,
                        weather.location.timezone
                    )


                    if (index == 0) Gap(horizontal = 10.dp)

                    HourlyItem(
                        time = time,
                        precipitationProbability = item.precipitationProbability ?: 0,
                        temperature = temperature,
                        isNow = index == 0,
                        icon = item.weatherCondition.toIcon(matchingDaily, item.time)
                    )

                    if (index == filteredHourly.size - 1) Gap(horizontal = 10.dp)

                }
            }
        }
    }

}

@Composable
private fun HourlyItem(
    time: String,
    precipitationProbability: Int,
    temperature: Double?,
    isNow: Boolean,
    icon: Int
) {

    Column(
        modifier = Modifier
            .height(120.dp)
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Gap(5.dp)
        TempWithShape(temperature, isNow)
        Gap(2.dp)
        Text(
            "${precipitationProbability}%",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(bottom = 3.dp)
                .alpha(if (precipitationProbability > 0) 1f else 0f)
        )
        WeatherIconBox(icon, size = 28.dp)
        Gap(3.dp)
        Text(
            time,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TempWithShape(temperature: Double?, isNow: Boolean = false) {
    Surface(
        shape = MaterialShapes.Cookie4Sided.toShape(),
        modifier = Modifier
            .size(36.dp),
        color = if (isNow) MaterialTheme.colorScheme.primary else Color.Transparent
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "${temperature?.roundToInt() ?: "-"}°",
                color = if (isNow) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}