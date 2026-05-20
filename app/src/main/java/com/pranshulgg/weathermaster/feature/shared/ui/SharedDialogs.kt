package com.pranshulgg.weathermaster.feature.shared.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.core.model.sources.getWeatherSourcesForCountry
import com.pranshulgg.weathermaster.core.model.sources.getWeatherSourcesGlobal
import com.pranshulgg.weathermaster.core.ui.components.DialogBasic
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.RadioRow

object SharedDialogs {
    @Composable
    fun WeatherProvidersForLocationDialog(
        countryCode: String?,
        show: Boolean,
        selectedSource: WeatherSource = WeatherSource.OPEN_METEO,
        isEditing: Boolean = false,
        onSave: (WeatherSource) -> Unit,
        onCancel: () -> Unit
    ) {

        val recommendedSources = getWeatherSourcesForCountry(countryCode)
        val globalSources = getWeatherSourcesGlobal()
        var currentSelectedSource by remember(
            show,
            selectedSource
        ) {
            mutableStateOf(if (recommendedSources.isNotEmpty() && !isEditing) recommendedSources[0] else selectedSource)
        }

        DialogBasic(
            title = stringResource(R.string.weather_sources),
            onConfirm = {
                onSave(currentSelectedSource)
            },
            onDismiss = onCancel,
            show = show,
            confirmText = stringResource(R.string.action_save),
            dismissText = stringResource(R.string.action_cancel)
        ) {
            Column {

                if (recommendedSources.isNotEmpty()) {
                    DialogTitle(stringResource(R.string.recommended_sources))
                    recommendedSources.forEach { source ->
                        RadioRow(
                            label = source.displayName,
                            onClick = { currentSelectedSource = WeatherSource.valueOf(it) },
                            selected = currentSelectedSource == source,
                            value = source.toString()
                        )
                    }
                    Gap(5.dp)
                }

                DialogTitle(stringResource(R.string.global_sources))
                globalSources.forEach { source ->
                    RadioRow(
                        label = source.displayName,
                        onClick = { currentSelectedSource = WeatherSource.valueOf(it) },
                        selected = currentSelectedSource == source,
                        value = source.toString()
                    )
                }
            }
        }

    }

}

@Composable
private fun DialogTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 5.dp, top = 5.dp, start = 16.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.W700
    )
}