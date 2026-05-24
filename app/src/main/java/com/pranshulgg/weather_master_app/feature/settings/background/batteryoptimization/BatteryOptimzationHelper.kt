package com.pranshulgg.weather_master_app.feature.settings.background.batteryoptimization

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager

object BatteryOptimizationHelper {

    fun requestDisableBatteryOptimization(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        if (powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            SnackbarManager.show(R.string.info_already_ignoring_battery_opt)
            return
        }


        val fallback = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(fallback)
    }
}