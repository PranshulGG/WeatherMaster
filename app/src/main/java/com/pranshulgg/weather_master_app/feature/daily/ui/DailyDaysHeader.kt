package com.pranshulgg.weather_master_app.feature.daily.ui

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.utils.formatters.toWeekdayString
import com.pranshulgg.weather_master_app.feature.daily.components.SelectableDayItem
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
    val scrollState = rememberLazyListState()


    LaunchedEffect(weather) {
        scrollState.animateScrollToItem(selectedIndex, -16)
    }



    LazyRow(
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(
            items = weatherDaily,
            key = { _, weatherItem -> weatherItem.time }) { index, it ->

            val weekDay = toWeekdayString(it.time, weather.location.timezone)
            val maxTemp =
                TemperatureUnit.CELSIUS.convert(it.temperatureMax, units.tempUnit)?.roundToInt()
                    ?: "-"
            val minTemp =
                TemperatureUnit.CELSIUS.convert(it.temperatureMin, units.tempUnit)?.roundToInt()
                    ?: "-"


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