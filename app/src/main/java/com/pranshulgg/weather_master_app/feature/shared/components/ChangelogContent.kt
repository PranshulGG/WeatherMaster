package com.pranshulgg.weather_master_app.feature.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap


@Composable
fun ChangelogContent(hideSheet: () -> Unit) {
    Column(Modifier.heightIn(max = 700.dp)) {
        Text(
            "Changelog ${BuildConfig.APP_VERSION}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider()

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            Gap(5.dp)
            ChangelogBox {
                ChangelogHeader("New stuff")
                ChangelogText("Edit location names")
                ChangelogText("Option to choose Air quality and Alert sources")
                ChangelogText("New source: WeatherApi.com, IPMA (Portugal) #983")
                ChangelogText("Changelog should appear on first-time install of every new release")
                ChangelogText("AccuWeather now available as a search source")
                ChangelogText("Widget \"Weather3\" now has options to change the background color to follow current conditions and to display hourly data instead of daily")
                ChangelogText("Add dates to the daily forecast on the main screen #975")
                ChangelogText("Widget \"Weather 2\" now has options to change text and background color #973")
                ChangelogText("Added dew point scale #990")
            }
            ChangelogBox {
                ChangelogHeader("Fixed")
                ChangelogText("AccuWeather redundant calls for location keys")
                ChangelogText("Fixed null value crashes for NWS")
                ChangelogText("Search results without time zone should not show")
                ChangelogText("Main hourly forecast should also include next day hours #974")
                ChangelogText("Fixed wrong summary for night-time rain")
                ChangelogText("Fixed visibility graph overflow @arpitagarwal1301")
                ChangelogText("Fixed wrong weather conditions for MeteoFrance @hydrielax")
                ChangelogText("Fixed translation crashes")

            }
            ChangelogBox {
                ChangelogHeader("Improvements")
                ChangelogText("Summary now only shows upcoming events and hides past ones")
                ChangelogText("Improved source picker")
                ChangelogText("Updated all translations")
                ChangelogText("Improved font sizes")
                ChangelogText("Few UI tweaks")
                ChangelogText("Improved landscape/tablet layout")

            }
        }

        Button(
            onClick = hideSheet, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shapes = ButtonDefaults.shapes()
        ) {
            Text(stringResource(R.string.action_ok))
        }
    }
}

@Composable
private fun ChangelogBox(content: @Composable () -> Unit) {
    Column(Modifier.padding(vertical = 5.dp, horizontal = 16.dp)) {
        content()
    }
}

@Composable
private fun ChangelogText(text: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            "•",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ChangelogHeader(text: String) {
    Text(
        "# $text",
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 3.dp)
    )
}