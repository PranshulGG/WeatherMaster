package com.pranshulgg.weather_master_app.feature.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.toFroggy

@Composable
fun FroggyContainer(weather: Weather) {

    Image(
        painter = painterResource(
            weather.current.weatherCondition.toFroggy(
                targetTimeSecs = weather.current.time,
                daily = weather.daily.firstOrNull()
            )
        ),
        contentDescription = "",
        modifier = Modifier.padding(top = 8.dp)
    )
}