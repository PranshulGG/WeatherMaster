package com.pranshulgg.weather_master_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class BatteryOptimizationHandler(private val context: Context) : MethodChannel.MethodCallHandler {

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "isIgnoringBatteryOptimizations" -> {
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val isIgnoring = pm.isIgnoringBatteryOptimizations(context.packageName)
                result.success(isIgnoring)
            }

            "requestIgnoreBatteryOptimizations" -> {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:${context.packageName}")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                result.success(null)
            }

            else -> result.notImplemented()
        }
    }
}
