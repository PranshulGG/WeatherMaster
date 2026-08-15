package com.pranshulgg.weather_master_app.feature.settings.sources

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes


private data class WeatherSourceScreen(
    val displayName: String,
    val fullName: String,
    val displayLink: String,
    val countryNameRes: Int? = null
)

@Composable
fun WeatherSourcesScreen(navController: NavController) {
    val uriHandler = LocalUriHandler.current

    val alertSources =
        AlertSource.entries.filter { it.displayName !in WeatherSource.entries.map { source -> source.displayName } }
            .filter { it != AlertSource.NONE }
            .map {
                WeatherSourceScreen(
                    displayName = it.displayName,
                    fullName = it.fullName,
                    displayLink = it.displayLink,
                )
            }

    val weatherSources = WeatherSource.entries.map {
        WeatherSourceScreen(
            displayName = it.displayName,
            fullName = it.fullName,
            displayLink = it.displayLink,
            countryNameRes = it.countryNameRes
        )
    }

    val sources = weatherSources + alertSources

    LargeTopBarScaffold(
        title = stringResource(R.string.weather_sources),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.setting_weather_sources_info),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Gap(8.dp)
            Text(
                stringResource(R.string.settings_source_request_title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Gap(5.dp)
            Button(onClick = {
                uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster/issues/new?template=new_source.yaml")
            }, modifier = Modifier.padding(horizontal = 16.dp), shapes = ButtonDefaults.shapes()) {
                Text(stringResource(R.string.action_request))
            }
            Gap(5.dp)

            Button(onClick = {
                navController.navigate(NavRoutes.API_KEYS_CONFIG)
            }, modifier = Modifier.padding(horizontal = 16.dp), shapes = ButtonDefaults.shapes()) {
                Text(stringResource(R.string.settings_api_key_config))
            }
            Gap(12.dp)
            sources.forEach {
                val countryString =
                    if (it.countryNameRes != null) " (${stringResource(it.countryNameRes)})" else ""

                SettingSection(
                    title = it.displayName + countryString,
                    tiles = listOf(
                        SettingTile.ActionTile(
                            title = it.fullName,
                            description = it.displayLink,
                            onClick = { uriHandler.openUri(it.displayLink) },
                            trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                        )
                    )
                )
                Gap(10.dp)
            }
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        }
    }
}
