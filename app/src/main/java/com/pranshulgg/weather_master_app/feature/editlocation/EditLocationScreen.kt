package com.pranshulgg.weather_master_app.feature.editlocation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesForCountry
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesGlobal
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.feature.editlocation.ui.EditLocationBottomSheet
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedBottomSheet


data class EditLocationScreenUiState(
    val location: Location? = null,
    val isWeatherSourcesForLocationSheetOpen: Boolean = false,
    val selectedWeatherSource: WeatherSource? = null,
    val selectedAlertSource: AlertSource? = null,
    val selectedAirQualitySource: AirQualitySource? = null,
    val isAlertSourcesSheetOpen: Boolean = false,
    val isAirQualitySourcesSheetOpen: Boolean = false,
    val isEditLocationNameSheetOpen: Boolean = false
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditLocationScreen(
    navController: NavController, id: String
) {

    val viewModel: EditLocationViewModel = hiltViewModel()
    val uiState = viewModel.uiState.value

    LaunchedEffect(Unit) {
        viewModel.getLocationForId(id)
    }

    if (uiState.location == null) return

    val btnSize = ButtonDefaults.MediumContainerHeight

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Expanded, SheetValue.Hidden)
    )

    val colorDesc = MaterialTheme.colorScheme.tertiary


    val locationText = buildString {
        append(uiState.location.name)
        if (uiState.location.country.isNotBlank()) {
            append(", ")
        }
        if (uiState.location.state.isNotBlank()) {
            append(uiState.location.state)
            append(", ")
        }
        append(uiState.location.country)
    }

    val locationName = if (!uiState.location.customName.isNullOrBlank()) uiState.location.customName
    else locationText

    var currentLocationName by remember { mutableStateOf(locationName) }


    LargeTopBarScaffold(
        title = stringResource(R.string.location_edit),
        navigationIcon = { NavigateUpBtn(navController) },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = paddingValues.calculateTopPadding())
        ) {
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = "Location name",
                        description = currentLocationName,
                        onClick = {
                            viewModel.showEditLocationNameSheet()
                        },
                        trailing = {
                            IconButton(onClick = { currentLocationName = locationName }) {
                                Symbol(R.drawable.refresh_24px)
                            }
                        },
                        colorDesc = colorDesc
                    )
                )
            )


            Gap(10.dp)
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = "Weather source",
                        description = uiState.selectedWeatherSource?.displayName
                            ?: uiState.location.source.displayName,
                        colorDesc = colorDesc,
                        onClick = {
                            viewModel.showWeatherSourcesForLocationSheet()
                        }
                    ),
                    SettingTile.ActionTile(
                        title = "Alert source",
                        description = uiState.selectedAlertSource?.displayName
                            ?: uiState.location.alertSource.displayName,
                        colorDesc = colorDesc,
                        onClick = {
                            viewModel.showAlertSourcesSheet()
                        }
                    ),
                    SettingTile.ActionTile(
                        title = "Air quality source",
                        description = uiState.selectedAirQualitySource?.displayName
                            ?: uiState.location.airQualitySource.displayName,
                        colorDesc = colorDesc,
                        onClick = {
                            viewModel.showAirQualitySourcesSheet()
                        }
                    )
                )
            )
            Text(
                "Latitude: ${uiState.location.latitude}, Longitude: ${uiState.location.longitude}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)
            )
            Gap(26.dp)
            Button(
                modifier = Modifier
                    .heightIn(btnSize)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = { /* Handle click */ },
                shapes = ButtonDefaults.shapes(),
                contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
            ) {
                Symbol(
                    R.drawable.check_24px,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    size = ButtonDefaults.iconSizeFor(btnSize)
                )
                Gap(horizontal = ButtonDefaults.iconSpacingFor(btnSize))
                Text(
                    stringResource(R.string.action_save_changes),
                    style = ButtonDefaults.textStyleFor(btnSize),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // WEATHER SOURCES SHEET
            SharedBottomSheet.WeatherSourcesForLocationSheet(
                countryCode = uiState.location.countryCode,
                show = uiState.isWeatherSourcesForLocationSheetOpen,
                isEditing = true,
                selectedSource = uiState.selectedWeatherSource ?: uiState.location.source,
                onSave = {
                    viewModel.updateSelectedWeatherSource(it)
                },
                onDismiss = viewModel::hideWeatherSourcesForLocationSheet,
                sheetState = sheetState
            )


            // ALERT SOURCES SHEET
            EditLocationBottomSheet.AlertSourcesSheet(
                show = uiState.isAlertSourcesSheetOpen,
                sheetState = sheetState,
                selectedSource = uiState.selectedAlertSource ?: uiState.location.alertSource,
                onSave = {
                    viewModel.updateSelectedAlertSource(it)
                },
                onDismiss = viewModel::hideAlertSourcesSheet
            )

            // AIR QUALITY SOURCES SHEET
            EditLocationBottomSheet.AirQualitySourcesSheet(
                show = uiState.isAirQualitySourcesSheetOpen,
                sheetState = sheetState,
                selectedSource = uiState.selectedAirQualitySource
                    ?: uiState.location.airQualitySource,
                onSave = {
                    viewModel.updateSelectedAirQualitySource(it)
                },
                onDismiss = viewModel::hideAirQualitySourcesSheet
            )

            // EDIT LOCATION NAME SHEET
            EditLocationBottomSheet.EditLocationNameSheet(
                show = uiState.isEditLocationNameSheetOpen,
                sheetState = sheetState,
                onDismiss = {
                    viewModel.hideEditLocationNameSheet()
                },
                onSave = {
                    if (it.isNotBlank()) {
                        currentLocationName = it
                    }
                },
                value = currentLocationName,
            )

            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
        }
    }
}


