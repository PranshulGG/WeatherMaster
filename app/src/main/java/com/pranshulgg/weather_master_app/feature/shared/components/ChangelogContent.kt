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
                ChangelogText("App is now available on Google Play Store")
                ChangelogText("Weather Notifications:")
                ChangelogSubText("Scheduled notifications #1021")
                ChangelogSubText("On-going notification")
                ChangelogText("New sources:")
                ChangelogSubText("CWA (Taiwan) by @reveler-hub")
                ChangelogSubText("Pirate Weather by @altendorfme")
                ChangelogSubText("JMA (Japan) by @reveler-hub")
                ChangelogSubText("IMET (Brazil) by @altendorfme")
                ChangelogSubText("OpenWeather (Global)")
                ChangelogText("New layout for tablets")
                ChangelogText("Added AMOLED theme option by @reveler-hub")
                ChangelogText("Added Backup & Restore by @reveler-hub")
                ChangelogText("Auto-select a self-paired alert source to match the weather source by @reveler-hub")
                ChangelogText("Widget \"Weather 4\" now has more data formats")
                ChangelogText("Widget \"Glance\" added option to hide the weather")
                ChangelogText("New widget \"Weather 5\" #945")
                ChangelogText("Widget \"Pill\" is now configurable #1053")
                ChangelogText("New widget \"Froggy\"")

            }
            ChangelogBox {
                ChangelogHeader("Fixed")
                ChangelogText("Fixed recommended source selection")
                ChangelogText("Fix crashes on location lookup #968")
                ChangelogText("Fix app not being able to find current location #938 @reveler-hub")
                ChangelogText("Fix Open-Meteo errors on some models #1039")
                ChangelogText("Fix pressure screen using wrong units #1040")
                ChangelogText("Fix current location not being updated when moving places #1035 by @reveler-hub")
                ChangelogText("Fix current location sometimes not being able to get the country code making it not displaying the recommended sources correctly")
                ChangelogText("Fix stutter/flash on every weather refresh by @reveler-hub")
                ChangelogText("Fix BMKG forecast errors by @reveler-hub ")
                ChangelogText("Fix device location's weather staying pinned to the old city after moving by @reveler-hub")
                ChangelogText("Fix past-hour cache could grow indefinitely. Only keep previous 24 hrs")
                ChangelogText("Fix Met Norway wind direction")
                ChangelogText("Fix sun progress calculation")
                ChangelogText("Fix mis-paired moonrise/moonset timings #1077")
                ChangelogText("Fix overflowing caused by large font scaling")
                ChangelogText("Fix UV index widget not working")
            }
            ChangelogBox {
                ChangelogHeader("Improvements")
                ChangelogText("Improved error handling")
                ChangelogText("Improved daily weather conditions. Now correctly picks the secondary condition for the day")
                ChangelogText("Improved weather sources architecture")
                ChangelogText("Humidity/Dew point will be hidden if not provided by the source")
                ChangelogText("Sun/moon block should now only be visible if valid")
                ChangelogText("Added missing widget previews")
                ChangelogText("Improved cache handling")
                ChangelogText("Updated all translations")

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
private fun ChangelogSubText(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(start = 12.dp)
    ) {
        Text(
            "•",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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