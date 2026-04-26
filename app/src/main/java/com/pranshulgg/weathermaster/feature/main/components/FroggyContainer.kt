package com.pranshulgg.weathermaster.feature.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toFroggy

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