package com.pranshulgg.weather_master_app.core.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BasicTimePicker(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    is24Hour: Boolean,
    initialTime: Long? = null,
) {

    val time = Instant.ofEpochMilli(initialTime ?: System.currentTimeMillis())
        .atZone(ZoneId.systemDefault()).toLocalTime()


    key(initialTime, is24Hour) {
        val timePickerState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = is24Hour,
        )

        val calendar = Calendar.getInstance()


        DialogBasic(
            show = show,
            onConfirm = {
                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onConfirm(calendar.timeInMillis)
            },
            onDismiss = onDismiss,
            title = "Select time",
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TimePicker(state = timePickerState)
            }
        }
    }
}