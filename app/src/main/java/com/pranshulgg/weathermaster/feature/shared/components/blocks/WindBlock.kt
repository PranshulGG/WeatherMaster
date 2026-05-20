package com.pranshulgg.weathermaster.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.weather.WindSpeedUnits
import com.pranshulgg.weathermaster.core.model.weather.toName
import com.pranshulgg.weathermaster.core.model.weather.wind.WindDirection
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius
import com.pranshulgg.weathermaster.core.utils.weather.UnitConverter
import kotlin.math.roundToInt

@Composable
fun WindBlock(
    weather: Weather,
    context: Context,
    isDaily: Boolean,
    dailyIndex: Int,
    units: WeatherUnits
) {

    val windDirection = if (isDaily) {
        weather.daily[dailyIndex].windDirection
    } else {
        weather.current.windDirection
    }

    val windSpeed = if (isDaily) weather.daily[dailyIndex].windSpeed else weather.current.windSpeed

    val windSpeedFormatted = UnitConverter
        .convertWindSpeed(windSpeed!!, WindSpeedUnits.KPH, units.windUnit).roundToInt()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(ShapeRadius.Full),
        shadowElevation = ShadowElevation.level2
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            if (windDirection != null) {
                Image(
                    painter = painterResource(id = R.drawable.weather_wind_arrow_dominant),
                    contentDescription = "",
                    modifier = Modifier
                        .matchParentSize()
                        .rotate(WindDirection.toDegrees(windDirection).toFloat()),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary)
                )
            }

            Box(Modifier.align(Alignment.TopCenter)) {
                Header()
            }

            Row(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = 10.dp)
            ) {
                Text(
                    windSpeedFormatted.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.alignByBaseline(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Gap(horizontal = 2.dp)
                Text(
                    units.windUnit.toName(context, true),
                    modifier = Modifier.alignByBaseline(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }


            if (windDirection != null) {
                Text(
                    "From $windDirection",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
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
        modifier = Modifier
            .padding(top = 36.dp, start = 12.dp, end = 12.dp)

    ) {
        Symbol(
            R.drawable.air_24px,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
        Text(
            stringResource(R.string.weather_wind),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}
