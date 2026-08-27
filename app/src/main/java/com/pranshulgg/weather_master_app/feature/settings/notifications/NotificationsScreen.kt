package com.pranshulgg.weather_master_app.feature.settings.notifications

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.core.ui.components.BasicTimePicker
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.feature.notifications.NotificationConfig
import com.pranshulgg.weather_master_app.feature.notifications.isNotificationPermissionGranted
import com.pranshulgg.weather_master_app.feature.notifications.ongoing.OnGoingNotification
import com.pranshulgg.weather_master_app.feature.notifications.rememberNotificationPermissionLauncher
import com.pranshulgg.weather_master_app.feature.notifications.scheduled.NotificationScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

data class NotificationsScreenUiState(
    val weather: Weather? = null,
    val units: WeatherUnits? = null,
    val defaultLocation: Location? = null
)

@Composable
fun NotificationsScreen(navController: NavController) {

    val viewModel: NotificationScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.value

    LaunchedEffect(uiState.defaultLocation) {
        viewModel.getDefaultLocation()
        viewModel.getUnitsOnce()
        viewModel.getWeather(uiState.defaultLocation?.id)
    }

    var isTimePickerOpen by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()


    val defaultChosenTimeForToday = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 30)
    }.timeInMillis

    val defaultChosenTimeForNextDay = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 19)
        set(Calendar.MINUTE, 30)
    }.timeInMillis

    val savedChosenTimeForToday = PreferencesHelper.getLong("today_forecast_notification_time")
    val savedChosenTimeForNextDay = PreferencesHelper.getLong("next_day_forecast_notification_time")

    var isOnGoingNotificationEnabled by remember {
        mutableStateOf(
            PreferencesHelper.getBool(
                "isOnGoingNotificationEnabled"
            ) ?: false
        )
    }

    val context = LocalContext.current

    val prefs = LocalAppPrefs.current

    val isTodayForecastNotificationEnabled = prefs.isTodayForecastNotificationEnabled
    val isNextDayForecastNotificationEnabled = prefs.isNextDayForecastNotificationEnabled

    var type by remember { mutableStateOf<String?>(null) }

    var chosenTime by remember {
        mutableLongStateOf(
            savedChosenTimeForToday ?: defaultChosenTimeForToday
        )
    }
    var chosenTimeNextDay by remember {
        mutableLongStateOf(
            savedChosenTimeForNextDay ?: defaultChosenTimeForNextDay
        )
    }

    val initialTime = when (type) {
        NotificationConfig.TODAY_FORECAST -> chosenTime
        NotificationConfig.NEXT_DAY_FORECAST -> chosenTimeNextDay
        else -> chosenTime
    }


    val pattern = if (prefs.is24HrTimeFormat) "HH:mm" else "hh:mm a"

    val handleDisableAction: (String) -> Unit = {
        NotificationScheduler.cancelScheduledNotification(
            it,
            context
        )
        if (it == NotificationConfig.TODAY_FORECAST) {
            chosenTime = defaultChosenTimeForToday
            PreferencesHelper.setLong("today_forecast_notification_time", defaultChosenTimeForToday)
        } else {
            chosenTimeNextDay = defaultChosenTimeForNextDay
            PreferencesHelper.setLong(
                "next_day_forecast_notification_time",
                defaultChosenTimeForNextDay
            )
        }
    }

    val handleScheduleAction: (Long, String) -> Unit = { time, type ->
        NotificationScheduler.scheduleNotification(context, time, type)
    }

    var isNotificationPermissionGranted by remember { mutableStateOf(context.isNotificationPermissionGranted()) }
    val requestPermission = rememberNotificationPermissionLauncher(onGranted = {
        isNotificationPermissionGranted = true
    }, onDenied = {
        SnackbarManager.show(R.string.setting_notification_permission_req)
    })


    LargeTopBarScaffold(
        title = stringResource(R.string.settings_notifications),
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

            if (!isNotificationPermissionGranted) {
                SettingSection(
                    tiles = listOf(
                        SettingTile.ActionTile(
                            danger = true,
                            title = stringResource(R.string.setting_notification_permission_req),
                            onClick = {
                                requestPermission()
                            }
                        )
                    )
                )
            }

            SettingSection(
                tiles = listOf(
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.notifications_unread_24px) },
                        title = stringResource(R.string.setting_on_going_notification),
                        enabled = isNotificationPermissionGranted,
                        checked = isOnGoingNotificationEnabled,
                        onCheckedChange = {
                            isOnGoingNotificationEnabled = it
                            PreferencesHelper.setBool("isOnGoingNotificationEnabled", it)

                            if (it && uiState.weather != null && uiState.units != null) {
                                OnGoingNotification.update(uiState.weather, context, uiState.units)
                            } else {
                                OnGoingNotification.remove(context)
                            }
                        },
                    ),
                )
            )
            SettingSection(
                tiles = listOf(
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.today_24px) },
                        title = stringResource(R.string.setting_today_forecast_notification),
                        checked = isTodayForecastNotificationEnabled,
                        enabled = isNotificationPermissionGranted,
                        onCheckedChange = {
                            prefs.setTodayForecastNotificationEnabled(it)

                            if (!it) {
                                handleDisableAction(NotificationConfig.TODAY_FORECAST)
                            } else {
                                handleScheduleAction(
                                    defaultChosenTimeForToday,
                                    NotificationConfig.TODAY_FORECAST
                                )
                            }
                        },
                    ),
                    SettingTile.ActionTile(
                        title = stringResource(R.string.setting_today_forecast_notification_set_time),
                        leading = { SettingsTileIcon(R.drawable.schedule_48px) },
                        description = if (isTodayForecastNotificationEnabled) {
                            SimpleDateFormat(pattern, getCurrentAppLocale()).format(
                                Date(chosenTime)
                            )
                        } else stringResource(R.string.label_disabled),
                        colorDesc = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            type = NotificationConfig.TODAY_FORECAST

                            if (isTodayForecastNotificationEnabled) {
                                isTimePickerOpen = true
                            }
                        }
                    ),
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.today_24px) },
                        title = stringResource(R.string.setting_next_day_forecast_notification),
                        checked = isNextDayForecastNotificationEnabled,
                        enabled = isNotificationPermissionGranted,
                        onCheckedChange = {
                            prefs.setNextDayForecastNotificationEnabled(it)

                            if (!it) {
                                handleDisableAction(NotificationConfig.NEXT_DAY_FORECAST)
                            } else {
                                handleScheduleAction(
                                    defaultChosenTimeForNextDay,
                                    NotificationConfig.NEXT_DAY_FORECAST
                                )
                            }

                        },
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.schedule_48px) },
                        title = stringResource(R.string.setting_today_forecast_notification_set_time),
                        description = if (isNextDayForecastNotificationEnabled) {
                            SimpleDateFormat(pattern, getCurrentAppLocale()).format(
                                Date(chosenTimeNextDay)
                            )
                        } else stringResource(R.string.label_disabled),
                        colorDesc = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            type = NotificationConfig.NEXT_DAY_FORECAST

                            if (isNextDayForecastNotificationEnabled) {
                                isTimePickerOpen = true
                            }
                        }
                    ),
                )
            )
        }
    }



    BasicTimePicker(
        show = isTimePickerOpen,
        onDismiss = { isTimePickerOpen = false },
        initialTime = initialTime,
        onConfirm = {
            isTimePickerOpen = false
            if (type == NotificationConfig.TODAY_FORECAST) {
                chosenTime = it
                PreferencesHelper.setLong("today_forecast_notification_time", it)
            } else {
                chosenTimeNextDay = it
                PreferencesHelper.setLong("next_day_forecast_notification_time", it)
            }

            handleScheduleAction(it, type ?: NotificationConfig.TODAY_FORECAST)
        },
        is24Hour = prefs.is24HrTimeFormat
    )
}
