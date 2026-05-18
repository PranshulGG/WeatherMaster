package com.pranshulgg.weathermaster.feature.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.core.model.sources.toName
import com.pranshulgg.weathermaster.core.model.sources.weatherProviderGetForCountry
import com.pranshulgg.weathermaster.core.ui.components.DialogBasic
import com.pranshulgg.weathermaster.core.ui.components.RadioRow

object SharedDialogs {


    @Composable
    fun WeatherProvidersForLocationDialog(
        countryCode: String,
        show: Boolean,
        selectedSource: WeatherSource = WeatherSource.OPEN_METEO,
        onSave: () -> Unit,
        onCancel: () -> Unit
    ) {

        val supportedProviders = weatherProviderGetForCountry(countryCode)
        var selectedProvider by remember(
            show,
            selectedSource
        ) {
            mutableStateOf(selectedSource)
        }

        DialogBasic(
            title = "Sources",
            onConfirm = {
                onSave()
            },
            onDismiss = onCancel,
            show = show,
            confirmText = stringResource(R.string.action_save),
            dismissText = stringResource(R.string.action_cancel)
        ) {
            supportedProviders.forEach { provider ->
                RadioRow(
                    label = provider.toName(),
                    onClick = { selectedProvider = WeatherSource.valueOf(it) },
                    selected = selectedProvider == provider,
                    value = provider.toString()
                )
            }
        }

    }

}