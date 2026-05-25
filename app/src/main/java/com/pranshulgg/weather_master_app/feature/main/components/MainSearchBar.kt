package com.pranshulgg.weather_master_app.feature.main.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.Tooltip
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.ui.theme.isThemeDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSearchBar(
    isFroggyLayout: Boolean = true,
    paddingValues: PaddingValues,
    navController: NavController,
    drawerState: DrawerState,
    activeLocation: Location?,
    isTabletLike: Boolean,
    onEditLocation: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val showDrawer = {
        scope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    val locationText = activeLocation?.let {
        buildString {
            append(activeLocation.name)
            if (activeLocation.country.isNotBlank()) {
                append(", ")
            }
            if (activeLocation.state.isNotBlank()) {
                append(activeLocation.state)
                append(", ")
            }
            append(activeLocation.country)
        }
    } ?: "••••"


    Surface(
        color = if (isFroggyLayout) getSearchBarColor(isDark = isThemeDark()) else Color.Transparent,
        shape = CircleShape,
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding() + 8.dp,
                start = if (isFroggyLayout) 16.dp else 0.dp,
                end = if (isFroggyLayout) 16.dp else 0.dp,
            )
            .clickable(
                enabled = isFroggyLayout,
                onClick = {
                    if (!isTabletLike) {
                        showDrawer()
                    }
                },
            ),

        shadowElevation = if (isFroggyLayout) ShadowElevation.level1 else 0.dp
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Tooltip("Show menu", preferredPosition = TooltipAnchorPosition.Below) {
                IconButton(onClick = {
                    if (!isTabletLike) {
                        showDrawer()
                    }
                }) {
                    Symbol(
                        if (isTabletLike) R.drawable.location_on_24px else R.drawable.menu_24px,
                        color = if (isFroggyLayout) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Gap(horizontal = 4.dp)
            Text(
                locationText,
                color = if (isFroggyLayout) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Gap(horizontal = 4.dp)
            Tooltip("Edit location", preferredPosition = TooltipAnchorPosition.Below) {
                IconButton(onClick = onEditLocation) {
                    Symbol(
                        R.drawable.edit_24px,
                        color = if (isFroggyLayout) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Tooltip("Settings", preferredPosition = TooltipAnchorPosition.Below) {
                IconButton(onClick = {
                    navController.navigate(NavRoutes.SETTINGS)
                }) {
                    Symbol(
                        R.drawable.settings_24px,
                        color = if (isFroggyLayout) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Composable
private fun getSearchBarColor(isDark: Boolean): Color {

    return when (isDark) {
        true -> MaterialTheme.colorScheme.surface.copy(0.4f)
        false -> MaterialTheme.colorScheme.surfaceContainerLowest.copy(0.6f)
    }

}