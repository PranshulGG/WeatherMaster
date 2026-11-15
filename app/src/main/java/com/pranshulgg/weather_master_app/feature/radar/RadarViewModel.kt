package com.pranshulgg.weather_master_app.feature.radar

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.radar.RadarFrame
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.repository.radar.RadarRepository
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class RadarUiState(
    /** Recent worldwide radar frames, oldest first; the last one is shown */
    val frames: List<RadarFrame>? = null,
    val error: AppException? = null,
    /** The last hour of frames for the time lapse while it plays; it plays as it is until stopped */
    val loop: List<RadarFrame>? = null
) {
    val latest: RadarFrame? get() = frames?.lastOrNull()
    val isLoopOn: Boolean get() = loop != null
}

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val repository: RadarRepository,
    weatherStore: WeatherStore
) : ViewModel() {

    var uiState by mutableStateOf(RadarUiState())
        private set

    private var lastFetchMilli = 0L // SystemClock.elapsedRealtime()

    // Only someone who uses the radar has it fetched (and the page prepared) in the background,
    // so the free radar server isn't asked for anything on behalf of those who never open it
    var hasOpenedRadar = PreferencesHelper.getBool(KEY_HAS_OPENED_RADAR) ?: false
        private set
    private var fetchJob: Job? = null

    /** Credit for the radar source, shown on the map */
    val attribution: Int get() = repository.attribution

    init {
        // Once the radar has been opened, fetch it together with the weather, whether or not the
        // page is open: whenever new weather arrives (location set, pull to refresh, auto refresh,
        // new source)
        weatherStore.data
            .map { it.weather?.current?.lastUpdatedInMilli }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { if (hasOpenedRadar) refreshNow() }
            .launchIn(viewModelScope)
    }

    /** The radar page was opened: from now on the radar is also fetched with the weather */
    fun onPageOpened() {
        if (hasOpenedRadar) return
        hasOpenedRadar = true
        PreferencesHelper.setBool(KEY_HAS_OPENED_RADAR, true)
    }

    /**
     * Keeps the frames up to date while called (the radar page runs it while visible and the app
     * is started). Doesn't fetch again within the refresh interval of the last fetch
     */
    suspend fun keepUpdated() {
        while (true) {
            if (fetchedWithin(REFRESH_INTERVAL)) {
                delay(REFRESH_INTERVAL.inWholeMilliseconds - millisSince(lastFetchMilli))
                continue // re-check: refreshNow may have fetched meanwhile
            }
            // Wait for a fetch already running rather than start a second one
            (fetchJob?.takeIf { it.isActive } ?: startFetch()).join()
        }
    }

    /**
     * Fetches the frames now, with the weather and for Retry. Skipped if fetched in the last
     * [MIN_REFETCH_GAP] (e.g. repeated pull to refresh)
     */
    fun refreshNow() {
        // (or one is already on its way)
        if (uiState.error == null && (fetchedWithin(MIN_REFETCH_GAP) || fetchJob?.isActive == true)) return
        startFetch()
    }

    /** Starts the time lapse with the last hour of frames, or stops it */
    fun toggleLoop() {
        if (uiState.isLoopOn) {
            stopLoop()
            return
        }
        val frames = uiState.frames ?: return
        val since = frames.last().timeMilli - LOOP_PERIOD.inWholeMilliseconds
        uiState = uiState.copy(loop = frames.filter { it.timeMilli >= since })
    }

    fun stopLoop() {
        uiState = uiState.copy(loop = null)
    }

    /** Draws [frame]'s radar tiles for [view] (tiles are cached by the repository) */
    suspend fun renderEchoes(frame: RadarFrame, view: MercatorView, scale: Float = 1f): RenderedEchoes =
        RadarRenderer.renderEchoes(view, repository.tileSizePx, repository.maxZoom, scale) { zoom, x, y ->
            repository.getTile(frame, zoom, x, y)
        }

    private fun startFetch(): Job {
        fetchJob?.cancel()
        return viewModelScope.launch { fetch() }.also { fetchJob = it }
    }

    private suspend fun fetch() {
        uiState = try {
            val frames = repository.getFrames()
            // Keep the same list when the latest frame hasn't changed, so nothing is drawn again
            val current = uiState.frames
            val unchanged = current != null && current.last().timeMilli == frames.last().timeMilli
            uiState.copy(frames = if (unchanged) current else frames, error = null)
        } catch (e: Exception) {
            // Keep showing the last frames if a refresh fails
            uiState.copy(error = e.toAppException())
        }
        lastFetchMilli = SystemClock.elapsedRealtime()
    }

    private fun millisSince(elapsedRealtimeMilli: Long) = SystemClock.elapsedRealtime() - elapsedRealtimeMilli

    private fun fetchedWithin(duration: Duration) =
        lastFetchMilli != 0L && millisSince(lastFetchMilli) < duration.inWholeMilliseconds

    private companion object {
        const val KEY_HAS_OPENED_RADAR = "hasOpenedRadar"
        val REFRESH_INTERVAL = 3.minutes
        val MIN_REFETCH_GAP = 30.seconds
        val LOOP_PERIOD = 1.hours
    }
}
