package com.pranshulgg.weather_master_app.data.worker.gadgetbridge

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager


fun isGadgetbridgeInstalled(context: Context): Boolean {
    return context.packageManager
        .queryBroadcastReceivers(Intent("nodomain.freeyourgadget.gadgetbridge.ACTION_GENERIC_WEATHER"), PackageManager.GET_RESOLVED_FILTER)
        .isNotEmpty()
}
