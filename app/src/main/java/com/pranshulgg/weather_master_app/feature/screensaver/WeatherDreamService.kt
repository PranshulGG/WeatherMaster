package com.pranshulgg.weather_master_app.feature.screensaver

import android.service.dreams.DreamService
import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.feature.notifications.mapper.notificationWeatherMapper
import com.pranshulgg.weather_master_app.feature.notifications.model.NotificationWeatherModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.Date
import javax.inject.Inject

/**
 * PROTOTYPE for #690: shows the default location's cached weather as an Android screen saver
 * (Settings > Display > Screen saver). Reads only what's already stored; makes no network calls.
 */
@AndroidEntryPoint
class WeatherDreamService : DreamService(), LifecycleOwner, SavedStateRegistryOwner {

    @Inject
    lateinit var weatherContextRepository: WeatherContextRepository

    @Inject
    lateinit var weatherUnitsRepository: WeatherUnitsRepository

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        isScreenBright = false
        PreferencesHelper.init(this)

        // Opaque black from the first frame, so the lock screen isn't visible behind the dream
        // while the Compose content is still being set up.
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.BLACK))

        window.decorView.setViewTreeLifecycleOwner(this)
        window.decorView.setViewTreeSavedStateRegistryOwner(this)

        setContentView(ComposeView(this).apply {
            setContent {
                ScreensaverContent(loadWeather = ::loadWeather)
            }
        })
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onDreamingStopped() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onDreamingStopped()
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    private suspend fun loadWeather(): Pair<String, NotificationWeatherModel>? {
        val default = weatherContextRepository.getLocationsOnce().find { it.isDefault } ?: return null
        val units = weatherUnitsRepository.getUnitsOnce() ?: return null
        val weather = weatherContextRepository.getWeatherForLocation(default.id) ?: return null
        return default.name to notificationWeatherMapper(weather, this, units)
    }
}

@Composable
private fun ScreensaverContent(loadWeather: suspend () -> Pair<String, NotificationWeatherModel>?) {
    val context = LocalContext.current
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var data by remember { mutableStateOf<Pair<String, NotificationWeatherModel>?>(null) }

    // Clock tick
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1_000)
        }
    }
    // Re-read cached weather every 10 minutes
    LaunchedEffect(Unit) {
        while (true) {
            data = runCatching { loadWeather() }
                .onFailure { android.util.Log.e("WeatherDream", "Failed to load cached weather", it) }
                .onSuccess { if (it == null) android.util.Log.w("WeatherDream", "No default location, units or cached weather") }
                .getOrNull()
            delay(10 * 60_000L)
        }
    }

    // Small position shift each minute to avoid OLED burn-in
    val minute = now / 60_000
    val shiftX = (((minute % 5) - 2) * 6).toInt()
    val shiftY = ((((minute / 5) % 5) - 2) * 6).toInt()

    val is24 = PreferencesHelper.getBool("is24HrTimeFormat") ?: DateFormat.is24HourFormat(context)
    val timeText = DateFormat.format(if (is24) "HH:mm" else "h:mm", Date(now)).toString()
    val dateText = DateFormat.format("EEEE d MMMM", Date(now)).toString()

    val primary = Color(0xFFE6E1E5)
    val secondary = Color(0xFF9A96A0)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.offset(shiftX.dp, shiftY.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(timeText, color = primary, fontSize = 96.sp, fontWeight = FontWeight.Light)
            Text(dateText, color = secondary, fontSize = 20.sp)

            val weather = data
            if (weather != null) {
                val (place, model) = weather
                Spacer(Modifier.height(40.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(model.current.currentConditionIcon),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(model.current.temp ?: "--", color = primary, fontSize = 56.sp)
                }
                Text(model.current.currentCondition, color = primary, fontSize = 20.sp)
                model.daily.firstOrNull()?.let {
                    Text(
                        "${it.maxTemp ?: "--"} / ${it.minTemp ?: "--"}  ·  $place",
                        color = secondary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(Modifier.height(32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    model.hourly.take(6).forEach { hour ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(hour.time ?: "", color = secondary, fontSize = 13.sp)
                            Image(
                                painterResource(hour.conditionIcon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(vertical = 6.dp)
                                    .size(28.dp)
                            )
                            Text(hour.temp ?: "", color = primary, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
