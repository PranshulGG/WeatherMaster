package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.sources.getSourcesForCountry
import com.pranshulgg.weather_master_app.core.model.sources.getSourcesGlobal
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.feature.shared.components.ChangelogContent

object SharedBottomSheet {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WeatherSourcesForLocationSheet(
        countryCode: String?,
        show: Boolean,
        sheetState: SheetState,
        selectedSource: Source = Source.OPEN_METEO,
        isEditing: Boolean = false,
        onSave: (Source) -> Unit,
        onDismiss: () -> Unit,
        onClickApiConfig: () -> Unit,
        apiKeys: List<ApiKey>,
    ) {
        if (show) {
            val recommendedSources = getSourcesForCountry(countryCode?.uppercase())
            val globalSources = getSourcesGlobal()

            val isApiKeyAvailable: (Source) -> Boolean = { source ->
                if (source.requiresUserApiKey) {
                    apiKeys.isNotEmpty()
                            && apiKeys
                        .any { it.source == source && !it.apiKey.isNullOrBlank() }
                } else {
                    true
                }
            }

            var currentSelectedSource by remember(
                show,
                selectedSource,
                isApiKeyAvailable
            ) {
                mutableStateOf(
                    if (recommendedSources.isNotEmpty() && !isEditing && isApiKeyAvailable(
                            recommendedSources[0]
                        )
                    ) recommendedSources[0] else selectedSource
                )
            }

            val handeSelection: (Source) -> Unit = {
                if (!it.requiresUserApiKey) {
                    currentSelectedSource = it
                } else if (!isApiKeyAvailable(it)) {
                    onClickApiConfig()
                } else {
                    currentSelectedSource = it
                }
            }

            val description: (Source) -> String? = {
                if (it.requiresUserApiKey &&
                    !isApiKeyAvailable(it)
                ) "Requires API key" else null
            }



            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { onSave(currentSelectedSource) },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel)
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
                                    handeSelection(source)
                                }
                            )
                        }
                    )
                }
                Gap(8.dp)
                SettingSection(
                    title = stringResource(R.string.global_sources),
                    tiles = globalSources.filter { !it.regionalButWorldwideSupport }.map { source ->
                        val isSelected = currentSelectedSource == source

                        val countryString =
                            source.countryNameRes?.let { " (${stringResource(it)})" } ?: ""

                        SettingTile.ActionTile(
                            leading = {
                                if (isSelected) Symbol(
                                    R.drawable.check_24px,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            title = source.displayName + countryString,
                            selected = isSelected,
                            description = description(source),
                            colorDesc = MaterialTheme.colorScheme.error,
                            onClick = {
                                handeSelection(source)
                            },
                        )
                    }
                )
                Gap(8.dp)
                SettingSection(
                    title = stringResource(R.string.source_regional_global),
                    tiles = globalSources.filter { it.regionalButWorldwideSupport && it !in recommendedSources }
                        .map { source ->
                            val isSelected = currentSelectedSource == source


                            val countryString =
                                if (source.countryNameRes != null) " (${stringResource(source.countryNameRes)})" else ""

                            SettingTile.ActionTile(
                                leading = {
                                    if (isSelected) Symbol(
                                        R.drawable.check_24px,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                },
                                title = source.displayName + countryString,
                                selected = isSelected,
                                description = description(source),
                                colorDesc = MaterialTheme.colorScheme.error,
                                onClick = {
                                    handeSelection(source)
                                },
                            )
                        }
                )
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChangelogBottomSheet(
        sheetState: SheetState,
        show: Boolean,
        onDismiss: () -> Unit
    ) {
        if (show) {
            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = { onDismiss() },
                onConfirm = { },
                showActions = false,
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel)
            ) { hide ->
                ChangelogContent(hide)
            }
        }
    }

}

