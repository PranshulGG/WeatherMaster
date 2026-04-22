package com.pranshulgg.weathermaster.feature.shared.components.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toLabel
import com.pranshulgg.weathermaster.core.model.toName
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius
import com.pranshulgg.weathermaster.core.utils.UnitConverter

@Composable
fun PressureBlock(weather: Weather, units: AppWeatherUnits) {
    val pressure = weather.current.pressureMsl

    val pressureInhg =
        UnitConverter.convertPressure(pressure, PressureUnits.HPA, PressureUnits.INHG)
    val pressureHpa = weather.current.pressureMsl.toInt()

    val progressDrawable = when {
        pressureHpa < 980 -> R.drawable.pressure_progress_low
        pressureHpa in 980..1005 -> R.drawable.pressure_progress_medium
        pressureHpa in 1005..1020 -> R.drawable.pressure_progress_low_medium
        pressureHpa in 1020..1035 -> R.drawable.pressure_progress_high
        else -> R.drawable.pressure_progress_very_high
    }


    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(ShapeRadius.Full),
        shadowElevation = ShadowElevation.level2
    ) {
        Box(
            Modifier
                .size(160.dp)
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pressure_progress_container),
                contentDescription = "",
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
            Image(
                painter = painterResource(id = progressDrawable),
                contentDescription = "",
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Box(Modifier.align(Alignment.TopCenter)) {
                Header()
            }

            Text(
                if (units.pressureUnit == PressureUnits.INHG) "%.2f".format(pressureInhg) else "$pressureHpa",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 10.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )


            Text(
                units.pressureUnit.toName(true),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-24).dp),
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
            .padding(top = 38.dp, start = 12.dp, end = 12.dp)

    ) {
        Symbol(
            R.drawable.compress_24px,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
        Text(
            "Pressure",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}
