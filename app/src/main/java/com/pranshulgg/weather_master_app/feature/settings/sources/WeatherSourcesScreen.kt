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
            Gap(8.dp)
            Text(
                "Want a source to be added? You can request it here",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Gap(5.dp)
            Button(onClick = {
                uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster/issues/new?template=new_source.yaml")
            }, modifier = Modifier.padding(horizontal = 16.dp), shapes = ButtonDefaults.shapes()) {
                Text("Request")
            }
            Gap(12.dp)
            WeatherSource.entries.forEach {
                SettingSection(
                    title = it.displayName,
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
