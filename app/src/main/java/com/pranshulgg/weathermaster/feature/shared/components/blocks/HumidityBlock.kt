package com.pranshulgg.weathermaster.feature.shared.components.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.ui.components.AvatarMonogram
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation
import com.pranshulgg.weathermaster.core.utils.UnitConverter

@Composable
fun HumidityBlock(weather: Weather, units: AppWeatherUnits) {
    val color = MaterialTheme.colorScheme.inversePrimary

    val humidity = weather.current.humidity.toInt()
    val dewPoint = UnitConverter.convertTemp(
        weather.current.dewPoint ?: 0.0,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    ).toInt()

    val humidityDrawable = when (humidity) {
        in 0..30 -> R.drawable.humidity_seven_percent
        in 30..50 -> R.drawable.humidity_thirty_precent
        in 50..70 -> R.drawable.humidity_fifty_percent
        in 70..90 -> R.drawable.humidity_seventy_percent
        else -> R.drawable.humidity_ninety_percent
    }


    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Box(Modifier.size(160.dp)) {

            Image(
                painter = painterResource(id = humidityDrawable),
                contentDescription = "Humidity",
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(color)
            )

            Box(Modifier.align(Alignment.TopStart)) {
                Header()
            }

            Text(
                "${humidity}%",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 12.dp),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(Modifier.align(Alignment.BottomStart)) {
                DewPointRow(dewPoint)
            }
        }
    }
}


@Composable
private fun Header() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            5.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp, start = 12.dp, end = 12.dp)
    ) {
        Symbol(
            R.drawable.humidity_percentage_24px,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
        Text(
            "Humidity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}


@Composable
private fun DewPointRow(dewPoint: Int) {


    Row(
        Modifier.padding(bottom = 12.dp, start = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(36.dp)

            ) {
                Text(
                    text = "${dewPoint}°",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

            }
        }
        Gap(horizontal = 5.dp)
        Text(
            "Dew point",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}