package com.pranshulgg.weathermaster.feature.shared.components.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toName
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius
import com.pranshulgg.weathermaster.core.utils.UnitConverter
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@Composable
fun VisibilityBlock(weather: Weather, units: AppWeatherUnits) {

    val visibility = UnitConverter.convertDistance(
        weather.current.visibility?.toDouble() ?: 0.0,
        DistanceUnits.M,
        units.distanceUnit
    )

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
            Image(
                painter = painterResource(id = R.drawable.visibility_block),
                contentDescription = "",
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary)
            )

            Box(Modifier.align(Alignment.TopCenter)) {
                Header()
            }

            Text(
                WeatherUtils.formatNumbers(
                    number = visibility,
                    decimalPlaces = 0
                ),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )


            Text(
                units.distanceUnit.toName(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-30).dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            R.drawable.visibility_24px,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
        Text(
            "Visibility",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}
