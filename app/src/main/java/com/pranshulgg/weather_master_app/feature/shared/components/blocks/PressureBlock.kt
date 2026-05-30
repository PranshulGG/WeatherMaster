package com.pranshulgg.weather_master_app.feature.shared.components.blocks

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import kotlin.math.roundToInt

@Composable
fun PressureBlock(
    weather: Weather,
    units: WeatherUnits,
    context: Context,
    onClickBlock: () -> Unit
) {
    val pressure = weather.current.pressureMsl

    val pressureConverted = PressureUnit.HPA.convert(pressure!!, units.pressureUnit)
    val pressureHpa = weather.current.pressureMsl.roundToInt()

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
        shadowElevation = ShadowElevation.level2,
        onClick = onClickBlock
    ) {
        Box(
            Modifier
                .fillMaxSize()
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
                if (units.pressureUnit != PressureUnit.HPA) "%.2f".format(pressureConverted) else "$pressureHpa",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 10.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )


            Text(
                units.pressureUnit.toName(true, context),
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
            stringResource(R.string.weather_pressure),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}
