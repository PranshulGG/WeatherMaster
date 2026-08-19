package com.pranshulgg.weather_master_app.feature.main.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
    // Read via rememberUpdatedState instead of keying the effect on `location` itself: the
    // device-location branch below updates activeLocation mid-fetch (after detecting a move),
    // which used to change the `location` parameter and re-key this DisposableEffect - Compose
    // then replays a synthetic ON_START on resubscribe, firing a second getWeather() call whose
    // weatherJob?.cancel() killed the first call's in-flight fetch before it could write the
    // real data, leaving the location name/coordinates updated but the weather itself stale.
    val currentLocation by rememberUpdatedState(location)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    currentLocation?.let {
                        weatherViewModel.startAutoRefresh(
                            location = it,
                            source = it.source
                        )
                        // isInitialized guard avoids duplicating setActiveLocation()'s
                        // cold-start fetch.
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