package com.pranshulgg.weathermaster.feature.daily.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.utils.formatters.toWeekdayString
import com.pranshulgg.weathermaster.core.utils.weather.UnitConverter
import com.pranshulgg.weathermaster.feature.daily.components.SelectableDayItem
import kotlin.math.roundToInt

@Composable
fun DailyDaysHeader(
    weather: Weather,
    units: WeatherUnits,
    onSelect: (Int) -> Unit,
    selectedIndex: Int
) {


    val weatherDaily = weather.daily
    val motionScheme = MotionScheme.expressive()

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        weatherDaily.forEachIndexed { index, it ->
            val weekDay = toWeekdayString(it.time, weather.location.timezone)
            val maxTemp = UnitConverter.convertTemp(
                it.temperatureMax,
                TemperatureUnits.CELSIUS,
                units.tempUnit
            )?.roundToInt() ?: '-'

            val minTemp = UnitConverter.convertTemp(
                it.temperatureMin,
                TemperatureUnits.CELSIUS,
                units.tempUnit
            )?.roundToInt() ?: "-"

            if (index == 0) Gap(horizontal = 16.dp)
            SelectableDayItem(
                weekDay,
                minTemp.toString(),
                maxTemp.toString(),
                it.weatherCondition,
                onSelect = { onSelect(index) },
                isSelected = index == selectedIndex,
                motionScheme
            )
            if (index == weatherDaily.size - 1) Gap(horizontal = 16.dp)
        }

    }

}