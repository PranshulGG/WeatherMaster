package com.pranshulgg.weather_master_app.data.worker

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVisibility @Inject constructor() : DefaultLifecycleObserver {

    var isForeground = false
        private set

    override fun onStart(owner: LifecycleOwner) {
        isForeground = true
    }

    override fun onStop(owner: LifecycleOwner) {
        isForeground = false
    }
}