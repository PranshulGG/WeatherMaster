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
                ChangelogText("WMO Severe Weather added as alert source")
                ChangelogText("FOSS Public Alert Server added as alert source #1004")
                ChangelogText("You can now pick between 54 models for Open-Meteo source")
                ChangelogText("New sources: Gismeteo (Russia) #1005, MetOffice (United Kingdom), IMD (India) #935, AEMET (Spain) by @reveler-hub")
                ChangelogText("New Widgets: Hourly #1010,  UV index widget")
            }
            ChangelogBox {
                ChangelogHeader("Fixed")
                ChangelogText("Fixed Wrong temperature summary")
                ChangelogText("Fixed crashes on Wind, Pressure, Humidity, Visibility screens")
                ChangelogText("Fixed rain/snow block overflow issues #1008")
                ChangelogText("Fixed date cutting off in German #1007")
                ChangelogText("Fixed hourly forecast cutoff when font size is large")
                ChangelogText("Fixed unwanted calls to alert APIs")
                ChangelogText("Fixed wrong pressure and humidity for Meteo AM and BMKG source")
                ChangelogText("Fix Pill widget resizing issues")
                ChangelogText("Fix FPAS alerts to respect app language by @reveler-hub")
                ChangelogText("Fix search-result casing and block-screen transition timing by @reveler-hub")
                ChangelogText("Fixed weather block glitches during arrangement")
            }
            ChangelogBox {
                ChangelogHeader("Improvements")
                ChangelogText("Improved location list UI when there are active alerts")
                ChangelogText("Keep past hour in the hourly forecast")
                ChangelogText("Weather 3 widget now has option to display either the hourly or daily forecast")
                ChangelogText("Units are now localized")
                ChangelogText("Widget \"Weather 1\" now can display precipitation probability #1010")
                ChangelogText("Added missing strings")
                ChangelogText("Changed \"Light rain\" icon to be closer to \"Light\"")
                ChangelogText("Past hours from the hourly forecast now will not be removed even if the source doesn't provide any")
                ChangelogText("Add option to configure \"Weather 4\" widget date format #1018")
                ChangelogText("Date format is now based on the app locale #744 @reveler-hub")
                ChangelogText("Weather now auto-refreshes while the app is in the foreground #1026")
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