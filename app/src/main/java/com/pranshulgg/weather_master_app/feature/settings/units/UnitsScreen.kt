package com.pranshulgg.weather_master_app.feature.settings.units

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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.feature.settings.SettingsScreenViewModel

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
                                    TemperatureUnit.CELSIUS.toString(),
                                    TemperatureUnit.CELSIUS.toName(context)
                                ),
                                DialogOption(
                                    TemperatureUnit.FAHRENHEIT.toString(),
                                    TemperatureUnit.FAHRENHEIT.toName(context)
                                )
                            ),
                            selectedOption = currentUnits.tempUnit.toString(),
                            onOptionSelected = {
                                viewModel.updateTemperatureUnit(
                                    TemperatureUnit.valueOf(it.uppercase())
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.air_24px) },
                            title = stringResource(R.string.setting_wind_speed_unit),
                            options = listOf(
                                DialogOption(
                                    WindSpeedUnit.MPS.toString(),
                                    WindSpeedUnit.MPS.toName(context)
                                ),
                                DialogOption(
                                    WindSpeedUnit.MPH.toString(),
                                    WindSpeedUnit.MPH.toName(context)
                                ),
                                DialogOption(
                                    WindSpeedUnit.KPH.toString(),
                                    WindSpeedUnit.KPH.toName(context)
                                ),
                                DialogOption(
                                    WindSpeedUnit.BFT.toString(),
                                    WindSpeedUnit.BFT.toName(context)
                                ),
                                DialogOption(
                                    WindSpeedUnit.KT.toString(),
                                    WindSpeedUnit.KT.toName(context)
                                ),
                            ),
                            selectedOption = currentUnits.windUnit.toString(),
                            onOptionSelected = {
                                viewModel.updateWindSpeedUnit(
                                    WindSpeedUnit.valueOf(it)
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.compress_24px) },
                            title = stringResource(R.string.setting_pressure_unit),
                            options = listOf(
                                DialogOption(
                                    PressureUnit.HPA.toString(),
                                    PressureUnit.HPA.toName(context = context),
                                ),
                                DialogOption(
                                    PressureUnit.INHG.toString(),
                                    PressureUnit.INHG.toName(context = context)
                                ),
                                DialogOption(
                                    PressureUnit.MMHG.toString(),
                                    PressureUnit.MMHG.toName(context = context)
                                )

                            ),
                            selectedOption = currentUnits.pressureUnit.toString(),
                            onOptionSelected = {
                                viewModel.updatePressureUnit(
                                    PressureUnit.valueOf(it)
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.visibility_24px) },
                            title = stringResource(R.string.setting_distance_unit),
                            options = listOf(
                                DialogOption(
                                    DistanceUnit.KM.toString(),
                                    DistanceUnit.KM.toName(context = context)
                                ),
                                DialogOption(
                                    DistanceUnit.M.toString(),
                                    DistanceUnit.M.toName(context = context)
                                ),
                                DialogOption(
                                    DistanceUnit.MI.toString(),
                                    DistanceUnit.MI.toName(context = context)
                                )
                            ),
                            selectedOption = currentUnits.distanceUnit.toString(),
                            onOptionSelected = {
                                viewModel.updateDistanceUnit(
                                    DistanceUnit.valueOf(it)
                                )
                            }
                        ),
                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.water_drop_24px) },
                            title = stringResource(R.string.setting_precipitation_unit),
                            options = listOf(
                                DialogOption(
                                    PrecipitationUnit.CM.toString(),
                                    PrecipitationUnit.CM.toName(context)
                                ),
                                DialogOption(
                                    PrecipitationUnit.INCH.toString(),
                                    PrecipitationUnit.INCH.toName(context)
                                ),
                                DialogOption(
                                    PrecipitationUnit.MM.toString(),
                                    PrecipitationUnit.MM.toName(context)
                                )
                            ),
                            selectedOption = currentUnits.precipitationUnit.toString(),
                            onOptionSelected = {
                                viewModel.updatePrecipitationUnit(
                                    PrecipitationUnit.valueOf(it)
                                )
                            }
                        )
                    )
                )
            }
        }
    }
}