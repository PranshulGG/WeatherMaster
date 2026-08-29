package com.pranshulgg.weather_master_app.feature.blocks.screens.airquality.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.core.model.weather.airquality.Pollutant
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.weather.airquality.AirQualityColors
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import kotlin.math.max

@Composable
fun AirQualityHourlyCard(data: List<AirQualityHourly>, zoneId: String, airQuality: AirQuality) {

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

    val aqiList = data.map {
        airQuality.getAqiFromValues(
            it.carbonMonoxide,
            it.pm25,
            it.nitrogenDioxide,
            it.ozone,
            it.pm10,
            it.sulphurDioxide
        )
    }

    val sharedScrollState = rememberScrollState()

    val max = aqiList.max().toDouble()

    val min = 0.0
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
            CardsHeader(stringResource(R.string.weather_air_quality_index))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .heightIn(200.dp)
                    .horizontalScroll(sharedScrollState),
                verticalAlignment = Alignment.Bottom,
            ) {

                data.forEachIndexed { index, hourly ->


                    val aqi = airQuality.getAqiFromValues(
                        hourly.carbonMonoxide,
                        hourly.pm25,
                        hourly.nitrogenDioxide,
                        hourly.ozone,
                        hourly.pm10,
                        hourly.sulphurDioxide
                    )

                    val percentage = ((aqi.toDouble().minus(min)).div((max - min))).times(
                        100
                    )

                    val barHeight = max((percentage.div(100.0)).times(140), 5.0)



                    if (index == 0) Gap(horizontal = 16.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            timeFormatter(hourly.time.secondsToMilliseconds()),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Gap(5.dp)
                        Box(contentAlignment = Alignment.BottomCenter) {
                            Surface(
                                Modifier
                                    .width(18.dp)
                                    .height(140.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ) {

                            }
                            Surface(
                                Modifier
                                    .width(38.dp)
                                    .height(barHeight.dp),
                                color = AirQualityColors.getColors(airQuality.getAqiLevel(aqi)),
                                shape = CircleShape
                            ) {

                            }
                        }
                        Gap(5.dp)
                        Text(
                            "$aqi",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (index == data.size - 1) Gap(horizontal = 16.dp)
                }
            }

            PollutantHourlyCard(data, zoneId, Pollutant.PM25, sharedScrollState)
            PollutantHourlyCard(data, zoneId, Pollutant.PM10, sharedScrollState)
            PollutantHourlyCard(data, zoneId, Pollutant.O3, sharedScrollState)
            PollutantHourlyCard(data, zoneId, Pollutant.NO2, sharedScrollState)
            PollutantHourlyCard(data, zoneId, Pollutant.CO, sharedScrollState)
            PollutantHourlyCard(data, zoneId, Pollutant.SO2, sharedScrollState)

        }
    }


}

