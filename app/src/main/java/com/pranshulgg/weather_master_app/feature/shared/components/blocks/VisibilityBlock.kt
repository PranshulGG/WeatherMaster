package com.pranshulgg.weather_master_app.feature.shared.components.blocks

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.utils.formatters.formatLocalizedNumber
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import java.util.Locale
import kotlin.math.roundToInt

@Composable

fun VisibilityBlock(
    weather: Weather,
    units: WeatherUnits,
    context: Context,
    isDaily: Boolean,
    dailyIndex: Int,
    onClickBlock: () -> Unit
) {

    val formatter: (Double) -> Double? = {
        DistanceUnit.M.convert(
            it,
            units.distanceUnit
        )
    }

    val visibility =
        if (isDaily) weather.daily[dailyIndex].visibility?.toDouble() else weather.current.visibility?.toDouble()

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
                painter = painterResource(id = R.drawable.visibility_block),
                contentDescription = "",
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Gap(28.dp)
                Header()

                Row(
                ) {
                    Text(
                        formatLocalizedNumber(
                            number = formatter(visibility!!)!!,
                            decimalPlaces = 0,
                            locale = getCurrentAppLocale()
                        ),
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.alignByBaseline(),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Gap(horizontal = 2.dp)
                    Text(
                        units.distanceUnit.toName(true, context),
                        modifier = Modifier.alignByBaseline(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!isDaily) {
                    Text(
                        stringResource(
                            visibilityLabels(
                                DistanceUnit.M.convert(
                                    visibility,
                                    DistanceUnit.KM
                                )?.roundToInt()!!
                            )
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                } else {
                    Text(
                        stringResource(R.string.weather_minimum),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
//                Text(
//                    formatLocalizedNumber(
//                        number = visibility!!,
//                        decimalPlaces = 0,
//                        locale = getCurrentAppLocale()
//                    ),
//                    style = MaterialTheme.typography.displaySmall,
//                    color = MaterialTheme.colorScheme.onSurface,
//                )
//
//
//                Text(
//                    units.distanceUnit.toName(context = context, inShort = true),
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
                Gap(28.dp)
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
        modifier = Modifier.padding(horizontal = 16.dp)

    ) {
        Symbol(
            R.drawable.visibility_24px,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
        Text(
            stringResource(R.string.weather_visibility),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}

private fun visibilityLabels(valueKm: Int): Int {
    return when {
        valueKm >= 20 -> R.string.text_very_good
        valueKm >= 10 -> R.string.text_good
        valueKm >= 4 -> R.string.text_moderate
        valueKm >= 1 -> R.string.text_poor
        else -> R.string.text_very_poor
    }
}