package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AlertsSection(
    alerts: List<Alert>,
    prefs: AppPrefsState,
    zoneId: String,
    onAlertClick: () -> Unit
) {

    val pattern = if (prefs.is24HrTimeFormat) "MMM dd, HH:mm" else "MMM dd, hh:mm a"


    val formatter: (Long) -> String = {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val instant = Instant.ofEpochMilli(it)
        val dateTime = instant.atZone(safeZoneId(zoneId)).toLocalDateTime()
        formatter.format(dateTime)
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            alerts.forEach {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable(onClick = onAlertClick),
                    leadingContent = {
                        Symbol(
                            R.drawable.warning_24px,
                            color = it.severity?.color ?: AlertSeverity.UNKNOWN.color,
                            size = 32.dp
                        )
                    },
                    headlineContent = { Text(it.event) },
                    supportingContent = {
                        if (it.effective != null && it.expires != null) {
                            Text("${formatter(it.effective)} • ${formatter(it.expires)}")
                        }
                    }
                )
            }
        }

    }
}