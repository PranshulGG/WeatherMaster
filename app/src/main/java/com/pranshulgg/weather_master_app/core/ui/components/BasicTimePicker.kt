package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun BasicTimePicker(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    is24Hour: Boolean
) {

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
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
            TimePicker(
                state = timePickerState,
            )
        }
    }

}