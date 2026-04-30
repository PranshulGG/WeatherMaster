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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
import com.pranshulgg.weathermaster.feature.settings.SettingsScreenViewModel

@Composable
fun UnitsScreen(navController: NavController) {

    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val units by viewModel.weatherUnits.collectAsStateWithLifecycle()

    val currentUnits = units


    LargeTopBarScaffold(
        title = "Units",
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
                            title = "Temperature Unit",
                            options = listOf(
                                TemperatureUnits.CELSIUS.toName(),
                                TemperatureUnits.FAHRENHEIT.toName()
                            ),
                            selectedOption = currentUnits.tempUnit.toName(),
                            onOptionSelected = {
                                viewModel.updateTemperatureUnit(
                                    TemperatureUnits.valueOf(it.uppercase())
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            title = "Wind speed Unit",
                            options = listOf(
                                WindSpeedUnits.MPS.toName(),
                                WindSpeedUnits.MPH.toName(),
                                WindSpeedUnits.KPH.toName(),
                            ),
                            selectedOption = currentUnits.windUnit.toName(),
                            onOptionSelected = {
                                viewModel.updateWindSpeedUnit(
                                    when (it) {
                                        "Meters per second" -> WindSpeedUnits.MPS
                                        "Miles per hour" -> WindSpeedUnits.MPH
                                        else -> WindSpeedUnits.KPH
                                    }
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            title = "Pressure Unit",
                            options = listOf(
                                PressureUnits.HPA.toName(),
                                PressureUnits.INHG.toName()
                            ),
                            selectedOption = currentUnits.pressureUnit.toName(),
                            onOptionSelected = {
                                viewModel.updatePressureUnit(
                                    when (it) {
                                        "Hectopascals" -> PressureUnits.HPA
                                        else -> PressureUnits.INHG
                                    }
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            title = "Distance Unit",
                            options = listOf(
                                DistanceUnits.KM.toName(),
                                DistanceUnits.M.toName(),
                                DistanceUnits.MI.toName()
                            ),
                            selectedOption = currentUnits.distanceUnit.toName(),
                            onOptionSelected = {
                                viewModel.updateDistanceUnit(
                                    when (it) {
                                        "Miles" -> DistanceUnits.MI
                                        "Meters" -> DistanceUnits.M
                                        else -> DistanceUnits.KM
                                    }
                                )
                            }
                        ),

                        SettingTile.DialogOptionTile(
                            title = "Precipitation Unit",
                            options = listOf(
                                PrecipitationUnits.CM.toName(),
                                PrecipitationUnits.INCH.toName(),
                                PrecipitationUnits.MM.toName()
                            ),
                            selectedOption = currentUnits.precipitationUnit.toName(),
                            onOptionSelected = {
                                viewModel.updatePrecipitationUnit(
                                    when (it) {
                                        "Inches" -> PrecipitationUnits.INCH
                                        "Centimeters" -> PrecipitationUnits.CM
                                        else -> PrecipitationUnits.MM
                                    }
                                )
                            }
                        )
                    )
                )
            }
        }
    }
}