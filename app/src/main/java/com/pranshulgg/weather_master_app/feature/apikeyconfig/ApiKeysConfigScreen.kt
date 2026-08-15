package com.pranshulgg.weather_master_app.feature.apikeyconfig

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile


@Composable
fun ApiKeysConfigScreen(navController: NavController) {

    val sourcesRequireApiKey = WeatherSource.entries.filter { it.requiresUserApiKey }

    val viewModel: ApiKeysConfigScreenViewModel = hiltViewModel()

    val apiKeys = viewModel.apiKeys.groupBy { it.source }

    LargeTopBarScaffold(
        title = stringResource(R.string.settings_api_key_config),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->

        Column(
            Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Only sources that require an API key will be shown here. Add and save your key to use the sources",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Gap(12.dp)
            SettingSection(
                tiles = sourcesRequireApiKey.map {

                    SettingTile.DialogTextFieldTile(
                        title = it.displayName,
                        onTextSubmitted = { key ->
                            viewModel.saveKey(key.trim(), it)
                        },
                        placeholder = apiKeys[it]?.getOrNull(0)?.apiKey.takeIf { key -> !key.isNullOrBlank() }
                            ?: "API key",
                        placeholderTextField = "API key",
                        initialText = apiKeys[it]?.getOrNull(0)?.apiKey ?: "",
                        placeholderAsValue = true,
                    )
                }
            )

            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
        }
    }
}
