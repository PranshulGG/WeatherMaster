package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
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


            val weatherSourceString = buildString {
                append(location!!.source.fullName)

                if (location.source.countryNameRes != null) {
                    append(" (${stringResource(location.source.countryNameRes)})")
                }

                if (location.source == Source.OPEN_METEO) {
                    append(" (${location.openMeteoModel.displayName})")
                }

            }

            val alertSourceString = buildString {
                append(location!!.alertSource.fullName)
                if (location.alertSource.countryNameRes != null) {
                    append(" (${stringResource(location.alertSource.countryNameRes)})")
                }
            }


            val airQualitySourceString = buildString {
                append(location!!.airQualitySource.fullName)
                if (location.airQualitySource.countryNameRes != null) {
                    append(" (${stringResource(location.airQualitySource.countryNameRes)})")
                }
            }

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
                            title = weatherSourceString,
                            description = location!!.source.displayLink,
                            onClick = {
                                uriHandler.openUri(location.source.displayLink)
                            },
                            trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                        )
                    )
                )
                if (location.airQualitySource != Source.NONE) {
                    Gap(8.dp)
                    SettingSection(
                        title = stringResource(R.string.weather_air_quality),
                        tiles = listOf(
                            SettingTile.ActionTile(
                                title = airQualitySourceString,
                                description = location.airQualitySource.displayLink,
                                trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) },
                                onClick = {
                                    uriHandler.openUri(location.airQualitySource.displayLink)
                                }
                            )
                        )
                    )
                }
                if (location.alertSource != Source.NONE) {
                    Gap(8.dp)
                    SettingSection(
                        title = "Alerts",
                        tiles = listOf(
                            SettingTile.ActionTile(
                                title = alertSourceString,
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
