package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesForCountry
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesGlobal
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.DialogBasic
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.RadioRow
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.Symbol

object SharedBottomSheet {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WeatherSourcesForLocationSheet(
        countryCode: String?,
        show: Boolean,
        sheetState: SheetState,
        selectedSource: WeatherSource = WeatherSource.OPEN_METEO,
        isEditing: Boolean = false,
        onSave: (WeatherSource) -> Unit,
        onDismiss: () -> Unit
    ) {
        if (show) {
            val recommendedSources = getWeatherSourcesForCountry(countryCode)
            val globalSources = getWeatherSourcesGlobal()
            var currentSelectedSource by remember(
                show,
                selectedSource
            ) {
                mutableStateOf(if (recommendedSources.isNotEmpty() && !isEditing) recommendedSources[0] else selectedSource)
            }


            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { onSave(currentSelectedSource) },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel)
            ) {
                if (recommendedSources.isNotEmpty()) {

                    SettingSection(
                        title = stringResource(R.string.recommended_sources),
                        tiles = recommendedSources.map { source ->
                            val isSelected = currentSelectedSource == source

                            SettingTile.ActionTile(
                                leading = {
                                    if (isSelected) Symbol(
                                        R.drawable.check_24px,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                },
                                title = source.displayName,
                                selected = isSelected,
                                onClick = {
                                    currentSelectedSource = source
                                }
                            )
                        }
                    )
                }
                Gap(8.dp)
                SettingSection(
                    title = stringResource(R.string.global_sources),
                    tiles = globalSources.map { source ->
                        val isSelected = currentSelectedSource == source

                        SettingTile.ActionTile(
                            leading = {
                                if (isSelected) Symbol(
                                    R.drawable.check_24px,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            title = source.displayName,
                            selected = isSelected,
                            onClick = {
                                currentSelectedSource = source
                            }
                        )
                    }
                )
            }
        }
    }
}

