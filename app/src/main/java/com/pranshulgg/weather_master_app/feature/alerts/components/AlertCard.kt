package com.pranshulgg.weather_master_app.feature.alerts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import java.time.Instant
import java.time.format.DateTimeFormatter


@Composable
fun AlertCard(alert: Alert, prefs: AppPrefsState, zoneId: String, shape: Shape) {
    val pattern = if (prefs.is24HrTimeFormat) "MMM dd, HH:mm" else "MMM dd, hh:mm a"

    val formatter: (Long?) -> String? = {
        it?.let {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val instant = Instant.ofEpochMilli(it)
            val dateTime = instant.atZone(safeZoneId(zoneId)).toLocalDateTime()
            formatter.format(dateTime)
        } ?: "Unknown"
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = shape,
        shadowElevation = ShadowElevation.level2
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    5.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Column() {
                    Text(
                        alert.event,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Gap(6.dp)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(
                            5.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {
                        Chip(
                            stringResource(alert.severity?.label ?: AlertSeverity.UNKNOWN.label),
                            alert.severity?.color ?: AlertSeverity.UNKNOWN.color,
                            alert.severity?.contentColor ?: AlertSeverity.UNKNOWN.contentColor
                        )
                        Chip(
                            "Effective ${formatter(alert.effective)}",
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Chip(
                            "Expires ${formatter(alert.expires)}",
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Gap(12.dp)
            HorizontalDivider()
            Gap(12.dp)
            Text(
                alert.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Gap(12.dp)
            HorizontalDivider()
            Gap(12.dp)
            Text(
                "Source ${alert.source}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun Chip(text: String, color: Color, textColor: Color) {
    Surface(
        color = color,
        shape = CircleShape,
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelLarge,
            color = textColor
        )
    }
}