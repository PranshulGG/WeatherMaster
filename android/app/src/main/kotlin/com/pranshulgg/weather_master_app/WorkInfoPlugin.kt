package com.pranshulgg.weather_master_app

import android.content.Context
import androidx.work.WorkManager
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.text.SimpleDateFormat
import java.util.*

class WorkInfoPlugin(private val context: Context) {

    private val CHANNEL = "com.pranshulgg.workinfo/channel"

    fun registerWith(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getWorkInfoSummary") {
                val uniqueWorkName = call.argument<String>("uniqueWorkName") ?: ""
                val intervalMinutes = (call.argument<Number>("intervalMinutes")?.toLong()) ?: 15L

                try {
                    // Call the member function here
                    val info = getWorkInfoSummary(uniqueWorkName, intervalMinutes)
                    result.success(info)
                } catch (e: Exception) {
                    result.error("ERROR", e.message, null)
                }
            } else {
                result.notImplemented()
            }
        }
    }

private fun getWorkInfoSummary(uniqueWorkName: String, intervalMinutes: Long): Map<String, Any> {
    val workInfos = WorkManager.getInstance(context).getWorkInfosForUniqueWork(uniqueWorkName).get()
    if (workInfos.isEmpty()) return mapOf("error" to "No work info found.")

    val info = workInfos[0]

    val id = info.id.toString()
    val tags = info.tags.toList()
    val state = info.state.name
    val attempt = info.runAttemptCount

    val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    val lastRunMillis = prefs.getLong("last_weather_update", System.currentTimeMillis())

    val selectedTimeUnit = prefs.getString("selectedTimeUnit", "12 hr") ?: "12 hr"


    val nextRunMillis = lastRunMillis + intervalMinutes * 60 * 1000

    // Select time format based on preference
    val timePattern = if (selectedTimeUnit == "24 hr") "yyyy-MM-dd HH:mm" else "yyyy-MM-dd hh:mm a"

    val sdf = SimpleDateFormat(timePattern, Locale.getDefault())
    val nextRunFormatted = sdf.format(Date(nextRunMillis))


    return mapOf(
        "Enqueued" to true,
        "ID" to id,
        "Work Tags" to tags,
        "Current State" to state,
        "Next Scheduled Run" to nextRunFormatted,
        "Attempt Number" to attempt
    )
}
}
