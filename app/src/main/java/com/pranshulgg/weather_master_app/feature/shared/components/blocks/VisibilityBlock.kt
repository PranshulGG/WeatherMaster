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
import com.pranshulgg.weather_master_app.core.utils.formatters.formatLocalizedNumber
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale

@Composable
fun VisibilityBlock(weather: Weather, units: WeatherUnits, context: Context) {


    val visibility =
        DistanceUnit.M.convert(weather.current.visibility?.toDouble(), units.distanceUnit)

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
                formatLocalizedNumber(
                    number = visibility!!,
                    decimalPlaces = 0,
                    locale = getCurrentAppLocale()
                ),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )


            Text(
                units.distanceUnit.toName(context = context),
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
            stringResource(R.string.weather_visibility),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}
