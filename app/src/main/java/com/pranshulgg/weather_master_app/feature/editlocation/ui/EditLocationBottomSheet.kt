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
import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.sources.Capability
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.sources.getSourcesForCountry
import com.pranshulgg.weather_master_app.core.model.sources.getSourcesGlobal
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
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
        selectedSource: Source = Source.NONE,
        onSave: (Source) -> Unit,
        onDismiss: () -> Unit,
        countryCode: String?,
        onClickApiConfig: () -> Unit,
        apiKeys: List<ApiKey>,
    ) {
        if (show) {

            var currentSelectedSource by remember(
                show,
                selectedSource
            ) {
                mutableStateOf(selectedSource)
            }

            val globalSources = getSourcesGlobal()

            val sources = globalSources.filter {
                Capability.ALERTS in it.capabilities
            }

            var recommendedSources = getSourcesForCountry(countryCode?.uppercase())

            recommendedSources = recommendedSources.filter { Capability.ALERTS in it.capabilities }

            val isApiKeyAvailable: (Source) -> Boolean = { source ->
                if (source.requiresUserApiKey) {
                    apiKeys.isNotEmpty()
                            && apiKeys
                        .any { it.source == source && !it.apiKey.isNullOrBlank() }
                } else {
                    true
                }
            }

            val description: (Source) -> String? = {
                if (it.requiresUserApiKey &&
                    !isApiKeyAvailable(it)
                ) "Requires API key" else null
            }


            val handeSelection: (Source) -> Boolean = { source ->
                if (!source.requiresUserApiKey) {
                    currentSelectedSource = source
                    true
                } else if (!isApiKeyAvailable(source)) {
                    onClickApiConfig()
                    true
                } else {
                    currentSelectedSource = source
                    true
                }
            }
            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel),
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
                        if (recommendedSources.isNotEmpty()) {
                            SettingSection(
                                title = stringResource(R.string.recommended_sources),
                                tiles = recommendedSources.map { source ->
                                    val isSelected = currentSelectedSource == source

                                    val countryString = source.countryNameRes?.let {
                                        " (${stringResource(it)})"
                                    } ?: ""

                                    SettingTile.ActionTile(
                                        leading = {
                                            if (isSelected) Symbol(
                                                R.drawable.check_24px,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        },
                                        title = source.displayName + countryString,
                                        description = description(source),
                                        colorDesc = MaterialTheme.colorScheme.error,
                                        selected = isSelected,
                                        onClick = {
                                            if (handeSelection(source)) {
                                                onSave(currentSelectedSource)
                                                hide()
                                            }
                                        }
                                    )
                                }
                            )
                        }
                        Gap(8.dp)
                        SettingSection(
                            title = stringResource(R.string.global_sources),
                            tiles = sources.filter { it !in recommendedSources }
                                .map { source ->
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
                                        description = description(source),
                                        colorDesc = MaterialTheme.colorScheme.error,
                                        onClick = {
                                            if (handeSelection(source)) {
                                                onSave(currentSelectedSource)
                                                hide()
                                            }
                                        }
                                    )
                                }
                        )

                        Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AirQualitySourcesSheet(
        show: Boolean,
        sheetState: SheetState,
        selectedSource: Source = Source.OPEN_METEO,
        onSave: (Source) -> Unit,
        onDismiss: () -> Unit,
        countryCode: String?,
        onClickApiConfig: () -> Unit,
        apiKeys: List<ApiKey>
    ) {
        if (show) {

            var currentSelectedSource by remember(
                show,
                selectedSource
            ) {
                mutableStateOf(selectedSource)
            }

            val globalSources = getSourcesGlobal()

            val sources = globalSources.filter {
                Capability.AIR_QUALITY in it.capabilities
            }


            var recommendedSources = getSourcesForCountry(countryCode?.uppercase())

            recommendedSources =
                recommendedSources.filter { Capability.AIR_QUALITY in it.capabilities }

            val isApiKeyAvailable: (Source) -> Boolean = { source ->
                if (source.requiresUserApiKey) {
                    apiKeys.isNotEmpty()
                            && apiKeys
                        .any { it.source == source && !it.apiKey.isNullOrBlank() }
                } else {
                    true
                }
            }

            val description: (Source) -> String? = {
                if (it.requiresUserApiKey &&
                    !isApiKeyAvailable(it)
                ) "Requires API key" else null
            }


            val handeSelection: (Source) -> Boolean = { source ->
                if (!source.requiresUserApiKey) {
                    currentSelectedSource = source
                    true
                } else if (!isApiKeyAvailable(source)) {
                    onClickApiConfig()
                    true
                } else {
                    currentSelectedSource = source
                    true
                }
            }

            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { onSave(currentSelectedSource) },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel),
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
                        if (recommendedSources.isNotEmpty()) {

                            SettingSection(
                                title = stringResource(R.string.recommended_sources),
                                tiles = recommendedSources.map { source ->
                                    val isSelected = currentSelectedSource == source

                                    val countryString = source.countryNameRes?.let {
                                        " (${stringResource(it)})"
                                    } ?: ""

                                    SettingTile.ActionTile(
                                        leading = {
                                            if (isSelected) Symbol(
                                                R.drawable.check_24px,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        },
                                        title = source.displayName + countryString,
                                        description = description(source),
                                        colorDesc = MaterialTheme.colorScheme.error,
                                        selected = isSelected,
                                        onClick = {
                                            if (handeSelection(source)) {
                                                onSave(currentSelectedSource)
                                                hide()
                                            }
                                        }
                                    )
                                }
                            )
                        }
                        Gap(8.dp)
                        SettingSection(
                            title = stringResource(R.string.global_sources),
                            tiles = sources.filter { it !in recommendedSources }
                                .map { source ->
                                    val isSelected = currentSelectedSource == source


                                    SettingTile.ActionTile(
                                        leading = {
                                            if (isSelected) Symbol(
                                                R.drawable.check_24px,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        },
                                        title = source.displayName,
                                        description = description(source),
                                        colorDesc = MaterialTheme.colorScheme.error,
                                        selected = isSelected,
                                        onClick = {
                                            if (handeSelection(source)) {
                                                onSave(currentSelectedSource)
                                                hide()
                                            }
                                        }
                                    )

                                }
                        )

                        Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())

                    }
                }
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