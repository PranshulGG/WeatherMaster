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
                ChangelogText("New sources:")
                ChangelogSubText("MGM (Turky) Alerts/Weather #1079")
                ChangelogSubText("KMI (Belgium) Alerts/Weather #997, #381, #1122")
                ChangelogSubText("MOENV (Taiwan) Air Quality source by @reveler-hub")
                ChangelogSubText("Meteofor (Ukraine) Weather by @reveler-hub")
                ChangelogText("Point Met Office's API key link to the right DataHub product by @reveler-hub")
                ChangelogText("Option added to receive notifications for weather alerts. Can be turned on in Settings → Notifications #316")
                ChangelogText("Custom date format #1142")
                ChangelogText("Add an option to disable the “New version available” message (Only Github versions)")
            }
            ChangelogBox {
                ChangelogHeader("Fixed")
                ChangelogText("Correctly map met office temperature values by @fardog")
                ChangelogText("Handle null temperature values in widget @fardog")
                ChangelogText("Use the day precipitation probability for Met Office's daily max by @reveler-hub")
                ChangelogText("Fix infinite loading when exiting Edit Location without changing anything")
                ChangelogText("Fix air-quality API key lookup using the wrong source by @reveler-hub")
                ChangelogText("Fix alert API key lookup using the wrong source")
                ChangelogText("Fix SMHI 404 for coordinates with more than 6 decimals by @nikiosna")
                ChangelogText("Fix Gismeteo location lookup sending wrong query parameter by @reveler-hub")
                ChangelogText("Fix widget dates not opening calendar apps when clicked")
                ChangelogText("Fix issues where errors would not display if the API key is missing or not set")
                ChangelogText("NWS fix: hourly forecast shows sun icons after sunset until midnight after an evening refresh by @GaijinSystems #1199")
                ChangelogText("ECCC: fix forecast dates around new year by @GaijinSystems")

            }
            ChangelogBox {
                ChangelogHeader("Improvements")
                ChangelogText("Remove max width/height for \"Weather 1\" widget by @fardog")
                ChangelogText("Updated all translations")
                ChangelogText("Add OneCall 4.0 API support to OpenWeather source #1119 @reveler-hub")
                ChangelogText("ECCC: keep today in daily forecast after its daytime period ends by @GaijinSystems")
                ChangelogText("Removed notification sounds for background updates and scheduled notifications #1211")
                ChangelogText("Added missing strings")

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
            text.trim(),
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
            text.trim(),
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