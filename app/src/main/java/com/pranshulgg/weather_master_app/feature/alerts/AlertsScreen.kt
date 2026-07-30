package com.pranshulgg.weather_master_app.feature.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.feature.alerts.components.AlertCard
import com.pranshulgg.weather_master_app.feature.settings.SettingsScreen

data class AlertsScreenUiState(
    val alerts: List<Alert?> = emptyList(),
    val location: Location? = null
)

@Composable
fun AlertsScreen(navController: NavController, locationId: String) {

    val viewModel: AlertsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.value
    val prefs = LocalAppPrefs.current

    LaunchedEffect(Unit) {
        viewModel.getAlertsForLocation(locationId)
        viewModel.getLocation(locationId)
    }


    LargeTopBarScaffold(
        title = "Alerts",
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = paddingValues.calculateTopPadding(), start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            uiState.alerts.filterNotNull().forEachIndexed { index, it ->
                val isFirst = index == 0
                val isLast = index == uiState.alerts.lastIndex
                val isOnly = uiState.alerts.size == 1
                val shape = when {
                    isOnly -> RoundedCornerShape(ShapeRadius.Large)
                    isFirst -> RoundedCornerShape(
                        topStart = ShapeRadius.Large,
                        topEnd = ShapeRadius.Large,
                        bottomStart = ShapeRadius.ExtraSmall,
                        bottomEnd = ShapeRadius.ExtraSmall
                    )

                    isLast -> RoundedCornerShape(
                        topStart = ShapeRadius.ExtraSmall,
                        topEnd = ShapeRadius.ExtraSmall,
                        bottomStart = ShapeRadius.Large,
                        bottomEnd = ShapeRadius.Large
                    )

                    else -> RoundedCornerShape(ShapeRadius.ExtraSmall)
                }


                AlertCard(it, prefs, uiState.location!!.timezone, shape)
            }

            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }

    }
}