package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.DialogBasic
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.feature.main.MainScreenViewModel

object MainScreenBottomSheets {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WeatherSourcesInfoForLocationSheet(
        viewModel: MainScreenViewModel,
        location: Location?,
        sheetState: SheetState,
    ) {

        val uiState = viewModel.uiState.value
        val uriHandler = LocalUriHandler.current



        if (uiState.isWeatherSourcesInfoForLocationSheetOpen) {

            val titleSource =
                if (location!!.source == WeatherSource.OPEN_METEO) "${location.source.fullName} (${location.openMeteoModel.displayName})"
                else location.source.fullName

            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = viewModel::hideWeatherSourcesInfoForLocationSheet,
                onConfirm = { },
                hideConfirmBtn = true,
                cancelText = stringResource(R.string.action_ok)
            ) {
                SettingSection(
                    title = stringResource(R.string.source),
                    tiles = listOf(

                        SettingTile.ActionTile(
                            title = titleSource,
                            description = location.source.displayLink,
                            onClick = {
                                uriHandler.openUri(location.source.displayLink)
                            },
                            trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                        )
                    )
                )
                if (location.airQualitySource != AirQualitySource.NONE) {
                    Gap(8.dp)
                    SettingSection(
                        title = stringResource(R.string.weather_air_quality),
                        tiles = listOf(
                            SettingTile.ActionTile(
                                title = location.airQualitySource.fullName,
                                description = location.airQualitySource.displayLink,
                                trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) },
                                onClick = {
                                    uriHandler.openUri(location.airQualitySource.displayLink)
                                }
                            )
                        )
                    )
                }
                if (location.alertSource != AlertSource.NONE) {
                    Gap(8.dp)
                    SettingSection(
                        title = "Alerts",
                        tiles = listOf(
                            SettingTile.ActionTile(
                                title = location.alertSource.fullName,
                                description = location.alertSource.displayLink,
                                trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) },
                                onClick = {
                                    uriHandler.openUri(location.alertSource.displayLink)
                                }
                            )
                        )
                    )
                }
            }
        }
    }
}
