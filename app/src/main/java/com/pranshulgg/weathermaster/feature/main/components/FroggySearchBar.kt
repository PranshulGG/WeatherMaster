package com.pranshulgg.weathermaster.feature.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.Tooltip

@Composable
fun FroggySearchBar(onSearchClick: () -> Unit, onSettingsClick: () -> Unit, hintText: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = CircleShape,
        modifier = Modifier.padding(
            top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
            start = 16.dp,
            end = 16.dp
        ),
        onClick = {

        }
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Tooltip("Search") {
                IconButton(onClick = onSearchClick) {
                    Symbol(
                        R.drawable.search_24px,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Gap(horizontal = 4.dp)
            Text(
                hintText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Gap(horizontal = 4.dp)
            Tooltip("Settings") {
                IconButton(onClick = onSettingsClick) {
                    Symbol(
                        R.drawable.settings_24px,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}