package com.pranshulgg.weather_master_app.core.model.weather.alerts

import androidx.compose.ui.graphics.Color
import com.pranshulgg.weather_master_app.R

enum class AlertSeverity(
    val label: Int,
    val color: Color
) {
    CRITICAL(
        R.string.alert_severity_critical,
        Color(0xFFD32F2F)
    ),
    HIGH(
        R.string.alert_severity_high,
        Color(0xFFF57C00)
    ),
    MODERATE(
        R.string.alert_severity_medium,
        Color(0xFFFBC02D)
    ),
    LOW(
        R.string.alert_severity_low,
        Color(0xFF388E3C)
    ),
    UNKNOWN(
        R.string.alert_severity_unknown,
        Color(0xFF757575)
    )
}