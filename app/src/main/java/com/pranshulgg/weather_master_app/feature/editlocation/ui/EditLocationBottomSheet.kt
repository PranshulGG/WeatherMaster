package com.pranshulgg.weather_master_app.feature.editlocation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesForCountry
import com.pranshulgg.weather_master_app.core.model.sources.getWeatherSourcesGlobal
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModelType
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.Symbol


object EditLocationBottomSheet {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditLocationNameSheet(
        show: Boolean,
        sheetState: SheetState,
        onDismiss: () -> Unit,
        onSave: (String) -> Unit,
        value: String
    ) {
        if (show) {

            var currentText by remember { mutableStateOf(value) }

            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { onSave(currentText) },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel)
            ) {
                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Name") },
                )
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AlertSourcesSheet(
        show: Boolean,
        sheetState: SheetState,
        selectedSource: AlertSource = AlertSource.NONE,
        onSave: (AlertSource) -> Unit,
        onDismiss: () -> Unit
    ) {
        if (show) {

            var currentSelectedSource by remember(
                show,
                selectedSource
            ) {
                mutableStateOf(selectedSource)
            }

            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { onSave(currentSelectedSource) },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel)
            ) {
                SettingSection(
                    title = stringResource(R.string.global_sources),
                    tiles = AlertSource.entries.map { source ->
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


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AirQualitySourcesSheet(
        show: Boolean,
        sheetState: SheetState,
        selectedSource: AirQualitySource = AirQualitySource.OPEN_METEO,
        onSave: (AirQualitySource) -> Unit,
        onDismiss: () -> Unit
    ) {
        if (show) {

            var currentSelectedSource by remember(
                show,
                selectedSource
            ) {
                mutableStateOf(selectedSource)
            }

            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { onSave(currentSelectedSource) },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel)
            ) {
                SettingSection(
                    title = stringResource(R.string.global_sources),
                    tiles = AirQualitySource.entries.map { source ->
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OpenMeteoModelsSheet(
        show: Boolean,
        sheetState: SheetState,
        selectedModel: OpenMeteoModel = OpenMeteoModel.BEST_MATCH,
        onSave: (OpenMeteoModel) -> Unit,
        onDismiss: () -> Unit
    ) {
        if (show) {

            var currentSelectedSource by remember(
                show,
                selectedModel
            ) {
                mutableStateOf(selectedModel)
            }

            val modelsByType = OpenMeteoModel.entries.groupBy { it.modelType }



            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { },
                showActions = false,
                removeBottomInset = true
            ) { hide ->
                Box(
                    modifier = Modifier.heightIn(max = 700.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "The default BEST MATCH provides the best forecast for any given location worldwide. SEAMLESS combines all models from a given provider into a seamless prediction.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        modelsByType.forEach { (type, models) ->
                            ModelSection(
                                currentSelectedSource,
                                hide,
                                onSave = {
                                    currentSelectedSource = it
                                    onSave(it)
                                },
                                models
                            )
                            Gap(10.dp)
                        }
                        Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelSection(
    currentSelectedSource: OpenMeteoModel,
    hide: () -> Unit,
    onSave: (OpenMeteoModel) -> Unit,
    models: List<OpenMeteoModel>
) {
    SettingSection(
        tiles = models.map { model ->
            val isSelected = currentSelectedSource == model

            SettingTile.ActionTile(
                leading = {
                    if (isSelected) Symbol(
                        R.drawable.check_24px,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                title = model.displayName,
                selected = isSelected,
                onClick = {
                    onSave(model)
                    hide()
                }
            )
        }
    )
}