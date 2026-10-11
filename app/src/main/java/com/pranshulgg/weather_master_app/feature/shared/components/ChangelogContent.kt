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
                ChangelogHeader("Fixed")
                ChangelogText("Fix crashes when location services are turned off")
                ChangelogText("Fixed wrong moonrise/moonset time #1105")
                ChangelogText("Fixed scrolling issues #1062")
                ChangelogText("Fix \"None\" showing as source in the weather sources screen #1109 by @reveler-hub")
                ChangelogText("Fix unrounded pressure values on hourly detail card by @reveler-hub")
                ChangelogText("Fixed wrong translation for air quality @reveler-hub #1147")
                ChangelogText("Fix alerts-only country-recommended sources leaking into weather picker @reveler-hub")
                ChangelogText("Use real-time instead of outdated saved timestamp #1088")
                ChangelogText("Fixed app not launching when clicking on the notifications")
                ChangelogText("Fixed country code error for locations in China")
                ChangelogText("Fixed Froggy widget cropping issue")
            }
            ChangelogBox {
                ChangelogHeader("Improvements")
                ChangelogText("Add option to disable tablet layout  #1111")
                ChangelogText("Color adjustments for hourly forecasts")
                ChangelogText("Widgets and Notifications should work without the internet if cached data is available")
                ChangelogText("Refactored app architecture")
                ChangelogText("Updated all translations")
                ChangelogText("Add support for third-party gadgetbridge receivers by @FlammeGamer")
                ChangelogText("Remove unused background location permission")
                ChangelogText("Widget clock or date now opens the calendar/clock app if available")
                ChangelogText("Add missing comma in \"EEE d MMMM\" by @VadosG")
                ChangelogText("Use rounded chips in the legend  by @RakshithBhat03")
                ChangelogText("Option to change font size for Glance widget")
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