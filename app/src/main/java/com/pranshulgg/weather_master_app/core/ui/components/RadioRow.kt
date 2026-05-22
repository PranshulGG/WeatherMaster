package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun RadioRow(
    label: String,
    value: String,
    selected: Boolean,
    hasPadding: Boolean = true,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick(value) }
            .fillMaxWidth()
            .padding(horizontal = if (hasPadding) 16.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onClick(value)
            }
        )
        Gap(horizontal = 8.dp)
        Text(
            label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}