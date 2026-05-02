package com.pranshulgg.weathermaster.feature.settings.units

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WindSpeedUnits
import com.pranshulgg.weathermaster.core.model.toName
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.DialogOption
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.feature.settings.SettingsScreenViewModel

@Composable
fun UnitsScreen(navController: NavController) {

    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val units by viewModel.weatherUnits.collectAsStateWithLifecycle()

    val currentUnits = units



    LargeTopBarScaffold(
        title = stringResource(R.string.setting_units),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->

        if (currentUnits != null) {

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                SettingSection(
                    tiles = listOf(
                        SettingTile.DialogOptionTile(
                            title = stringResource(R.string.setting_temperature_unit),
                            options = listOf(
                                DialogOption(
                                    TemperatureUnits.CELSIUS.toString(),
                                    TemperatureUnits.CELSIUS.toName()
                                ),
                                DialogOption(
                                    TemperatureUnits.FAHRENHEIT.toString(),
                                    TemperatureUnits.FAHRENHEIT.toName()
                                )
                            ),
                            selectedOption = currentUnits.tempUnit.toString(),
                            onOptionSelected = {
                                viewModel.updateTemperatureUnit(
                                    TemperatureUnits.valueOf(it.uppercase())
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            title = stringResource(R.string.setting_wind_speed_unit),
                            options = listOf(
                                DialogOption(
                                    WindSpeedUnits.MPS.toString(),
                                    WindSpeedUnits.MPS.toName()
                                ),
                                DialogOption(
                                    WindSpeedUnits.MPH.toString(),
                                    WindSpeedUnits.MPH.toName()
                                ),
                                DialogOption(
                                    WindSpeedUnits.KPH.toString(),
                                    WindSpeedUnits.KPH.toName()
                                ),
                            ),
                            selectedOption = currentUnits.windUnit.toString(),
                            onOptionSelected = {
                                viewModel.updateWindSpeedUnit(
                                    WindSpeedUnits.valueOf(it)
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            title = stringResource(R.string.setting_pressure_unit),
                            options = listOf(
                                DialogOption(
                                    PressureUnits.HPA.toString(),
                                    PressureUnits.HPA.toName()
                                ),
                                DialogOption(
                                    PressureUnits.INHG.toString(),
                                    PressureUnits.INHG.toName()
                                )

                            ),
                            selectedOption = currentUnits.pressureUnit.toString(),
                            onOptionSelected = {
                                viewModel.updatePressureUnit(
                                    PressureUnits.valueOf(it)
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            title = stringResource(R.string.setting_distance_unit),
                            options = listOf(
                                DialogOption(
                                    DistanceUnits.KM.toString(),
                                    DistanceUnits.KM.toName()
                                ),
                                DialogOption(DistanceUnits.M.toString(), DistanceUnits.M.toName()),
                                DialogOption(DistanceUnits.MI.toString(), DistanceUnits.MI.toName())
                            ),
                            selectedOption = currentUnits.distanceUnit.toString(),
                            onOptionSelected = {
                                viewModel.updateDistanceUnit(
                                    DistanceUnits.valueOf(it)
                                )
                            }
                        ),
                        SettingTile.DialogOptionTile(
                            title = stringResource(R.string.setting_precipitation_unit),
                            options = listOf(
                                DialogOption(
                                    PrecipitationUnits.CM.toString(),
                                    PrecipitationUnits.CM.toName()
                                ),
                                DialogOption(
                                    PrecipitationUnits.INCH.toString(),
                                    PrecipitationUnits.INCH.toName()
                                ),
                                DialogOption(
                                    PrecipitationUnits.MM.toString(),
                                    PrecipitationUnits.MM.toName()
                                )
                            ),
                            selectedOption = currentUnits.precipitationUnit.toString(),
                            onOptionSelected = {
                                viewModel.updatePrecipitationUnit(
                                    PrecipitationUnits.valueOf(it)
                                )
                            }
                        )
                    )
                )
            }
        }
    }
}