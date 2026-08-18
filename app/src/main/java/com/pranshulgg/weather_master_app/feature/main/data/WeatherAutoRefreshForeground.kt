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
                        // Guarded on isInitialized so this only fires on a genuine
                        // resume-from-background, not the initial cold-start load
                        // (setActiveLocation() already covers that one, and this
                        // DisposableEffect re-subscribes - and Android replays a
                        // synthetic ON_START on subscribe when the lifecycle is
                        // already started - the moment `location` first becomes
                        // non-null, which would otherwise double-fire here too).
                        // Cache-respecting; only forces a real refetch if the
                        // device location actually moved since last time.
                        if (weatherViewModel.uiState.value.isInitialized) {
                            weatherViewModel.getWeather(
                                location = it,
                                source = it.source
                            )
                        }
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