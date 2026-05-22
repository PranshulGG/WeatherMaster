package com.pranshulgg.weather_master_app.feature.main.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.Symbol


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreditsBottomSection(weather: Weather?, onClick: () -> Unit) {

    val source = weather?.location?.source?.displayName
        ?: WeatherSource.OPEN_METEO.displayName

    Text(
        "$source and more",
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline
    )


}