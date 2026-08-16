package com.pranshulgg.weather_master_app.feature.editlocation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.feature.editlocation.ui.EditLocationBottomSheet
import com.pranshulgg.weather_master_app.feature.editlocation.ui.EditLocationScreenDialogs
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedBottomSheet
import com.pranshulgg.weather_master_app.feature.shared.ui.SharedDialogs


data class EditLocationScreenUiState(
    val location: Location? = null,
    val isWeatherSourcesForLocationSheetOpen: Boolean = false,
    val selectedWeatherSource: WeatherSource? = null,
    val selectedAlertSource: AlertSource? = null,
    val selectedAirQualitySource: AirQualitySource? = null,
    val isAlertSourcesSheetOpen: Boolean = false,
    val isAirQualitySourcesSheetOpen: Boolean = false,
    val isEditLocationNameSheetOpen: Boolean = false,
    val isConfirmationDialogOpen: Boolean = false,
    val isOpenMeteoModelsSheetOpen: Boolean = false,
    val selectedOpenMeteoModel: OpenMeteoModel? = null,
    val apiKeys: List<ApiKey> = emptyList()
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditLocationScreen(
    navController: NavController, id: String,
    weatherViewModel: WeatherViewModel
) {

    val viewModel: EditLocationViewModel = hiltViewModel()
    val uiState = viewModel.uiState.value

    LaunchedEffect(Unit) {
        viewModel.getLocationForId(id)
    }

    if (uiState.location == null) return


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

    val selectedWeatherSourceString = buildString {
        if (uiState.selectedWeatherSource != null) {
            append(uiState.selectedWeatherSource.displayName)
        } else {
            append(uiState.location.source.displayName)
        }

        if (uiState.selectedWeatherSource?.countryNameRes != null) {
            append(" (${stringResource(uiState.selectedWeatherSource.countryNameRes)})")
        } else if (uiState.location.source.countryNameRes != null && uiState.selectedWeatherSource == null) {
            append(" (${stringResource(uiState.location.source.countryNameRes)})")
        }

        if (uiState.selectedWeatherSource == WeatherSource.OPEN_METEO) {
            append(" (${uiState.selectedOpenMeteoModel?.displayName})")
        } else if (uiState.location.source == WeatherSource.OPEN_METEO && uiState.selectedWeatherSource == null) {
            append(" (${uiState.location.openMeteoModel.displayName})")
        }

    }


    LargeTopBarScaffold(
        title = stringResource(R.string.location_edit),
        navigationIcon = { NavigateUpBtn(navController) },
        floatingActionButtonPosition = FabPosition.Center,
        actions = {
            IconButton(
                onClick = { navController.navigate(NavRoutes.API_KEYS_CONFIG) },
                shapes = IconButtonDefaults.shapes()
            ) {
                Symbol(R.drawable.key_24px)
            }
        }
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
                        title = stringResource(R.string.location_name),
                        description = currentLocationName,
                        onClick = {
                            viewModel.showEditLocationNameSheet()
                        },
                        trailing = {
                            IconButton(onClick = { currentLocationName = locationText }) {
                                Symbol(R.drawable.refresh_24px)
                            }
                        },
                        colorDesc = colorDesc,
                        overline = { Text(stringResource(R.string.action_requires_restart)) }
                    )
                )
            )


            Gap(10.dp)
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = stringResource(R.string.weather_source),
                        description = selectedWeatherSourceString,
                        colorDesc = colorDesc,
                        onClick = {
                            viewModel.showWeatherSourcesForLocationSheet()
                        },
                        trailing = {
                            val showButton = if (uiState.selectedWeatherSource != null)
                                uiState.selectedWeatherSource == WeatherSource.OPEN_METEO else
                                uiState.location.source == WeatherSource.OPEN_METEO

                            if (showButton) {
                                IconButton(
                                    onClick = viewModel::showOpenMeteoModelsSheet,
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Symbol(R.drawable.settings_24px)
                                }
                            }

                        }
                    ),
                    SettingTile.ActionTile(
                        title = stringResource(R.string.weather_alert_source),
                        description = uiState.selectedAlertSource?.displayName
                            ?: uiState.location.alertSource.displayName,
                        colorDesc = colorDesc,
                        onClick = {
                            viewModel.showAlertSourcesSheet()
                        }
                    ),
                    SettingTile.ActionTile(
                        title = stringResource(R.string.weather_airquality_source),
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
                "${stringResource(R.string.location_latitude)}: ${uiState.location.latitude}, ${
                    stringResource(
                        R.string.location_longitude
                    )
                }: ${uiState.location.longitude}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)
            )
            Gap(26.dp)
            ButtonWithIcon(
                onClick = {
                    viewModel.saveLocationName(
                        if (currentLocationName.trim() == locationText.trim()) null else currentLocationName.trim(),
                        uiState.location.id
                    )

                    navController.popBackStack()
                    weatherViewModel.handleSourceChangeForWeather(
                        uiState.location,
                        uiState.selectedWeatherSource ?: uiState.location.source,
                        uiState.selectedAirQualitySource ?: uiState.location.airQualitySource,
                        uiState.selectedAlertSource ?: uiState.location.alertSource,
                        uiState.selectedOpenMeteoModel ?: uiState.location.openMeteoModel
                    )
                },
                text = stringResource(R.string.action_save_changes),
                icon = R.drawable.check_24px
            )
            Gap(8.dp)
            ButtonWithIcon(
                onClick = {
                    viewModel.updateDefaultLocation(uiState.location.id)
                    navController.popBackStack()
                },
                text = stringResource(R.string.action_set_default),
                icon = R.drawable.home_pin_24px,
            )
            Gap(8.dp)
            ButtonWithIcon(
                onClick = {
                    if (uiState.location.isDefault) {
                        SnackbarManager.show(R.string.error_delete_default_location)
                        return@ButtonWithIcon
                    }
                    viewModel.showConfirmationDialog()
                },
                text = stringResource(R.string.action_delete),
                icon = R.drawable.delete_24px,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )

            // WEATHER SOURCES SHEET
            SharedBottomSheet.WeatherSourcesForLocationSheet(
                countryCode = uiState.location.countryCode,
                show = uiState.isWeatherSourcesForLocationSheetOpen,
                isEditing = true,
                selectedSource = uiState.selectedWeatherSource ?: uiState.location.source,
                onSave = {
                    viewModel.updateSelectedWeatherSource(it)
                    if (it == WeatherSource.OPEN_METEO) {
                        viewModel.updateSelectedOpenMeteoModel(
                            uiState.selectedOpenMeteoModel ?: uiState.location.openMeteoModel
                        )
                    }
                },
                onDismiss = viewModel::hideWeatherSourcesForLocationSheet,
                sheetState = sheetState,
                onClickApiConfig = {
                    navController.navigate(NavRoutes.API_KEYS_CONFIG)
                    viewModel.hideWeatherSourcesForLocationSheet()
                },
                apiKeys = uiState.apiKeys
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

            // CONFIRMATION DIALOG
            EditLocationScreenDialogs.EditLocationScreenConfirmationDialog(
                viewModel,
                onConfirm = {
                    weatherViewModel.deleteLocation(uiState.location.id)
                    viewModel.hideConfirmationDialog()
                    navController.popBackStack()
                }
            )

            // OPEN METEO MODELS SHEET
            EditLocationBottomSheet.OpenMeteoModelsSheet(
                show = uiState.isOpenMeteoModelsSheetOpen,
                selectedModel = uiState.selectedOpenMeteoModel
                    ?: uiState.location.openMeteoModel,
                sheetState = sheetState,
                onDismiss = {
                    viewModel.hideOpenMeteoModelsSheet()
                },
                onSave = {
                    viewModel.updateSelectedOpenMeteoModel(it)
                },
            )

        }

        Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ButtonWithIcon(
    onClick: () -> Unit,
    text: String,
    icon: Int,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true
) {

    val btnSize = ButtonDefaults.MediumContainerHeight

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        modifier = Modifier
            .heightIn(btnSize)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(),
        contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
    ) {
        Symbol(
            icon,
            color = contentColor,
            size = ButtonDefaults.iconSizeFor(btnSize)
        )
        Gap(horizontal = ButtonDefaults.iconSpacingFor(btnSize))
        Text(
            text,
            style = ButtonDefaults.textStyleFor(btnSize),
            color = contentColor
        )
    }
}