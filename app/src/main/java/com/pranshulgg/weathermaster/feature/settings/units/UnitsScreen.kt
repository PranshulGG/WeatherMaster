package com.pranshulgg.weathermaster.feature.settings.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.weather.DistanceUnits
import com.pranshulgg.weathermaster.core.model.weather.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.weather.PressureUnits
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.weather.WindSpeedUnits
import com.pranshulgg.weathermaster.core.model.weather.toName
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.ui.components.SettingsTileIcon
import com.pranshulgg.weathermaster.core.ui.components.tiles.DialogOption
import com.pranshulgg.weathermaster.feature.settings.SettingsScreenViewModel

@Composable
fun UnitsScreen(navController: NavController) {

    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val units by viewModel.weatherUnits.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
                            leading = { SettingsTileIcon(R.drawable.device_thermostat_24px) },
                            title = stringResource(R.string.setting_temperature_unit),
                            options = listOf(
                                DialogOption(
                                    TemperatureUnits.CELSIUS.toString(),
                                    TemperatureUnits.CELSIUS.toName(context)
                                ),
                                DialogOption(
                                    TemperatureUnits.FAHRENHEIT.toString(),
                                    TemperatureUnits.FAHRENHEIT.toName(context)
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
                            leading = { SettingsTileIcon(R.drawable.air_24px) },
                            title = stringResource(R.string.setting_wind_speed_unit),
                            options = listOf(
                                DialogOption(
                                    WindSpeedUnits.MPS.toString(),
                                    WindSpeedUnits.MPS.toName(context)
                                ),
                                DialogOption(
                                    WindSpeedUnits.MPH.toString(),
                                    WindSpeedUnits.MPH.toName(context)
                                ),
                                DialogOption(
                                    WindSpeedUnits.KPH.toString(),
                                    WindSpeedUnits.KPH.toName(context)
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
                            leading = { SettingsTileIcon(R.drawable.compress_24px) },
                            title = stringResource(R.string.setting_pressure_unit),
                            options = listOf(
                                DialogOption(
                                    PressureUnits.HPA.toString(),
                                    PressureUnits.HPA.toName(context = context)
                                ),
                                DialogOption(
                                    PressureUnits.INHG.toString(),
                                    PressureUnits.INHG.toName(context = context)
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
                            leading = { SettingsTileIcon(R.drawable.visibility_24px) },
                            title = stringResource(R.string.setting_distance_unit),
                            options = listOf(
                                DialogOption(
                                    DistanceUnits.KM.toString(),
                                    DistanceUnits.KM.toName(context = context)
                                ),
                                DialogOption(
                                    DistanceUnits.M.toString(),
                                    DistanceUnits.M.toName(context = context)
                                ),
                                DialogOption(
                                    DistanceUnits.MI.toString(),
                                    DistanceUnits.MI.toName(context = context)
                                )
                            ),
                            selectedOption = currentUnits.distanceUnit.toString(),
                            onOptionSelected = {
                                viewModel.updateDistanceUnit(
                                    DistanceUnits.valueOf(it)
                                )
                            }
                        ),
                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.water_drop_24px) },
                            title = stringResource(R.string.setting_precipitation_unit),
                            options = listOf(
                                DialogOption(
                                    PrecipitationUnits.CM.toString(),
                                    PrecipitationUnits.CM.toName(context)
                                ),
                                DialogOption(
                                    PrecipitationUnits.INCH.toString(),
                                    PrecipitationUnits.INCH.toName(context)
                                ),
                                DialogOption(
                                    PrecipitationUnits.MM.toString(),
                                    PrecipitationUnits.MM.toName(context)
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