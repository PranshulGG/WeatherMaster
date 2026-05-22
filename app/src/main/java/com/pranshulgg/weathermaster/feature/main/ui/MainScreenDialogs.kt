package com.pranshulgg.weathermaster.feature.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.core.ui.components.DialogBasic
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.feature.main.MainScreenUiState
import com.pranshulgg.weathermaster.feature.main.MainScreenViewModel

object MainScreenDialogs {

    @Composable
    fun ActiveLocationWeatherSourcesInfoDialog(
        viewModel: MainScreenViewModel,
        location: Location?
    ) {

        val uiState = viewModel.uiState.value
        val uriHandler = LocalUriHandler.current

        DialogBasic(
            title = stringResource(R.string.weather_sources),
            onConfirm = {},
            onDismiss = viewModel::hideActiveLocationWeatherSourcesInfoDialog,
            show = uiState.isActiveLocationWeatherSourcesInfoDialogOpen,
            dismissText = stringResource(R.string.action_ok),
            showOnlyDismissAction = true
        ) {
            Column(Modifier.padding(horizontal = 8.dp)) {
                DialogTitle(stringResource(R.string.source))
                SourceListItem(
                    location!!.source.displayName,
                    location.source.displayLink,
                    uriHandler
                )

                Gap(12.dp)
                DialogTitle(stringResource(R.string.weather_air_quality))
                SourceListItem(
                    WeatherSource.OPEN_METEO.displayName,
                    "https://open-meteo.com/en/docs/air-quality-api",
                    uriHandler
                )
            }
        }
    }
}

@Composable
private fun DialogTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.W700
    )
}

@Composable
private fun SourceListItem(
    headline: String,
    description: String,
    uriHandler: UriHandler,
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(headline) },
        supportingContent = { Text(description, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        trailingContent = { Symbol(R.drawable.open_in_new_24px) },
        modifier = Modifier.clickable(onClick = { uriHandler.openUri(description) })
    )
}