package com.pranshulgg.weather_master_app.feature.main.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel


@Composable
fun WeatherAutoRefreshForeground(
    weatherViewModel: WeatherViewModel,
    location: Location?
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, location) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    location?.let {
                        weatherViewModel.startAutoRefresh(
                            location = it,
                            source = it.source
                        )
                        // Cache-respecting; only forces a real refetch if the
                        // device location actually moved since last time.
                        weatherViewModel.getWeather(
                            location = it,
                            source = it.source
                        )
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    weatherViewModel.stopAutoRefresh()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}