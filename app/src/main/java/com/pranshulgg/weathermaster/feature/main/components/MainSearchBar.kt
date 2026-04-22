package com.pranshulgg.weathermaster.feature.main.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.Tooltip
import com.pranshulgg.weathermaster.core.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSearchBar(
    isFroggyLayout: Boolean = true,
    paddingValues: PaddingValues,
    navController: NavController,
    drawerState: DrawerState,
    weather: Weather
) {
    val scope = rememberCoroutineScope()
    val showDrawer = {
        scope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    val locationText = buildString {
        append(weather.location.name)
        append(", ")
        if (weather.location.state.isNotBlank()) {
            append(weather.location.state)
            append(", ")
        }
        append(weather.location.country)
    }


    Surface(
        color = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.6f),
        shape = CircleShape,
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        onClick = {
            showDrawer()
        }
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Tooltip("Show menu", preferredPosition = TooltipAnchorPosition.Below) {
                IconButton(onClick = { showDrawer() }) {
                    Symbol(
                        R.drawable.menu_24px,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Gap(horizontal = 4.dp)
            Text(
                locationText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Gap(horizontal = 4.dp)
            Tooltip("Settings", preferredPosition = TooltipAnchorPosition.Below) {
                IconButton(onClick = {
                    navController.navigate(NavRoutes.SETTINGS)
                }) {
                    Symbol(
                        R.drawable.settings_24px,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

