package com.pranshulgg.weather_master_app.feature.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.Symbol


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