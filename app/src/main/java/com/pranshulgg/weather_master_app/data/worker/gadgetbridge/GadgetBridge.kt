package com.pranshulgg.weather_master_app.data.worker.gadgetbridge

import android.content.Context
import android.content.pm.PackageManager


fun isGadgetbridgeInstalled(context: Context): Boolean {
    val pm = context.packageManager
    val standardPackage = "nodomain.freeyourgadget.gadgetbridge"
    val nightlyPackage = "nodomain.freeyourgadget.gadgetbridge.nightly"

    return try {
        pm.getPackageInfo(standardPackage, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        try {
            pm.getPackageInfo(nightlyPackage, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
