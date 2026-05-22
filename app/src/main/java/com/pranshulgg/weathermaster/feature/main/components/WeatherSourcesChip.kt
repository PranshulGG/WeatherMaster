package com.pranshulgg.weathermaster.feature.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherSourcesChip(weather: Weather?, onClick: () -> Unit) {

    val containerColor =
        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    val contentColor = MaterialTheme.colorScheme.onSurface

    Box(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = { },
                    colors = containerColor
                ) {

                    Text(
                        weather?.location?.source?.displayName
                            ?: WeatherSource.OPEN_METEO.displayName,
                        color = contentColor
                    )
                }
            },
            trailingButton = {

                SplitButtonDefaults.TrailingButton(
                    onClick = onClick,
                    colors = containerColor
                ) {
                    Symbol(R.drawable.add_24px, color = contentColor)
                }
            }
        )
    }

}