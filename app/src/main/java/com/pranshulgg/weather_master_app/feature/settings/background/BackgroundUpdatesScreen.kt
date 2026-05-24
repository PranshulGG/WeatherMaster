package com.pranshulgg.weather_master_app.feature.settings.background

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.worker.WeatherUpdateScheduler
import com.pranshulgg.weather_master_app.feature.settings.background.batteryoptimization.BatteryOptimizationHelper
import com.pranshulgg.weather_master_app.feature.settings.background.notification.isNotificationPermissionGranted
import com.pranshulgg.weather_master_app.feature.settings.background.notification.rememberNotificationPermissionLauncher


@Composable
fun BackgroundUpdatesScreen(navController: NavController) {
    val context = LocalContext.current

    var isNotificationPermissionGranted by remember { mutableStateOf(context.isNotificationPermissionGranted()) }
    val requestPermission = rememberNotificationPermissionLauncher(onGranted = {
        isNotificationPermissionGranted = true
    }, onDenied = {
        SnackbarManager.show(R.string.setting_notification_permission_req)
    })

    val viewModel: BackgroundUpdatesViewModel = hiltViewModel()


    val prefs = LocalAppPrefs.current
    val intervals = mapOf(
        60 to stringResource(R.string.time_hour, "1"),
        90 to stringResource(R.string.time_hours, "1.5"),
        120 to stringResource(R.string.time_hours, "2"),
        180 to stringResource(R.string.time_hours, "3"),
        240 to stringResource(R.string.time_hours, "4"),
        300 to stringResource(R.string.time_hours, "5"),
        360 to stringResource(R.string.time_hours, "6")
    )

    val intervalOptions = intervals.map { DialogOption(it.key.toString(), it.value) }

    val uriHandler = LocalUriHandler.current

    LargeTopBarScaffold(
        title = stringResource(R.string.setting_background_updates),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            AnimatedVisibility(visible = !isNotificationPermissionGranted) {
                SettingSection(
                    title = "Notification",
                    tiles = listOf(
                        SettingTile.ActionTile(
                            danger = true,
                            title = stringResource(R.string.setting_notification_permission_req),
                            description = stringResource(R.string.setting_notification_permission_secondary),
                            colorDesc = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = {
                                requestPermission()
                            }
                        )
                    )
                )
            }
            SettingSection(
                title = stringResource(R.string.setting_updates),
                primarySwitch = true,
                tiles = listOf(
                    SettingTile.SwitchTile(
                        enabled = isNotificationPermissionGranted,
                        title = stringResource(R.string.setting_background_updates),
                        checked = prefs.backgroundUpdatesEnabled,
                        onCheckedChange = {
                            prefs.setBackgroundUpdates(it)
                            if (it) {
                                viewModel.scheduleWeatherUpdates(prefs.backgroundUpdatesInterval)
                            } else {
                                viewModel.disableWeatherUpdates()
                            }
                        }
                    )
                )
            )

            SettingSection(
                title = stringResource(R.string.setting_additional),
                tiles = listOf(
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.update_24px) },
                        title = stringResource(R.string.setting_update_interval),
                        options = intervalOptions,
                        selectedOption = prefs.backgroundUpdatesInterval.toString(),
                        onOptionSelected = {
                            prefs.setBackgroundUpdatesInterval(it.toInt())
                        }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.sync_problem_24px) },
                        title = stringResource(R.string.setting_disable_battery_opt_title),
                        description = stringResource(R.string.setting_disable_battery_opt_secondary),
                        onClick = {
                            BatteryOptimizationHelper.requestDisableBatteryOptimization(context)
                        }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.help_24px) },
                        title = stringResource(R.string.setting_dontkillmyapp_title),
                        description = stringResource(R.string.setting_dontkillmyapp_secondary),
                        onClick = { uriHandler.openUri("https://dontkillmyapp.com/") }
                    )
                )
            )
        }


    }

}

