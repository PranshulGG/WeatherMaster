package com.pranshulgg.weather_master_app.feature.settings.sources

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes


@Composable
fun WeatherSourcesScreen(navController: NavController) {
    val uriHandler = LocalUriHandler.current


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
            Gap(12.dp)
            SettingSection(
                title = "Open Meteo (GLOBAL)",
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = WeatherSource.OPEN_METEO.displayName,
                        description = WeatherSource.OPEN_METEO.displayLink,
                        onClick = { uriHandler.openUri(WeatherSource.OPEN_METEO.displayLink) },
                        trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                    )
                )
            )
            Gap(10.dp)
            SettingSection(
                title = "Met Norway (GLOBAL)",
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = WeatherSource.MET_NORWAY.displayName,
                        description = WeatherSource.MET_NORWAY.displayLink,
                        onClick = { uriHandler.openUri(WeatherSource.MET_NORWAY.displayLink) },
                        trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                    )
                )
            )
            Gap(10.dp)
            SettingSection(
                title = "NWS (United states)",
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = WeatherSource.NWS.displayName,
                        description = WeatherSource.NWS.displayLink,
                        onClick = { uriHandler.openUri(WeatherSource.NWS.displayLink) },
                        trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                    )
                )
            )

            Gap(10.dp)
            SettingSection(
                title = "SMHI (Sweden)",
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = WeatherSource.SMHI.displayName,
                        description = WeatherSource.SMHI.displayLink,
                        onClick = { uriHandler.openUri(WeatherSource.SMHI.displayLink) },
                        trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                    )
                )
            )
            Gap(10.dp)
            SettingSection(
                title = "DWD (Germany)",
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = WeatherSource.DWD.displayName,
                        description = WeatherSource.DWD.displayLink,
                        onClick = { uriHandler.openUri(WeatherSource.DWD.displayLink) },
                        trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                    )
                )
            )
            Gap(10.dp)
            SettingSection(
                title = WeatherSource.METEO_FRANCE.displayName,
                tiles = listOf(
                    SettingTile.ActionTile(
                        title = WeatherSource.METEO_FRANCE.displayName,
                        description = WeatherSource.METEO_FRANCE.displayLink,
                        onClick = { uriHandler.openUri(WeatherSource.METEO_FRANCE.displayLink) },
                        trailing = { SettingsTileIcon(R.drawable.open_in_new_24px) }
                    )
                )
            )
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }
    }
}
