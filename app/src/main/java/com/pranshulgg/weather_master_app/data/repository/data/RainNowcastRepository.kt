package com.pranshulgg.weather_master_app.data.repository.data

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.nowcastCandidateSources
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.data.provider.SourceRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds the ~2h high-resolution rain nowcast for a location.
 *
 * Resolution is purely capability-driven: the candidate order comes from
 * [nowcastCandidateSources] (the location's own nowcast source, its weather
 * source when it declares the capability, the recommended sources for the
 * location's country, then the global ones) and every candidate's repository
 * provides its [com.pranshulgg.weather_master_app.data.repository.capability.NowcastCapability]
 * with its own coverage knowledge. No provider names or geographies are
 * hardcoded here.
 */
/**
 * Initial rain nowcast integration implemented by https://github.com/Hizoul
 */
@Singleton
class RainNowcastRepository @Inject constructor(
    private val sourceRepositoryProvider: SourceRepositoryProvider
) {

    companion object {
        private const val TAG = "RainNowcast"
    }

    // ---- per-location dedupe cache ---------------------------------------
    // Radar nowcast data is published in 5-minute cycles: within a cycle the
    // cached result IS the freshest data that exists, so refetching for the
    // same location in the same cycle is pure waste (and the app can trigger
    // up to 3 overlapping fetches for one user action: main screen, rain
    // detail page, widget refresh). Keyed by rounded location coordinates.
    private data class CacheEntry(
        val nowcast: RainNowcast?,
        val cycleStart: Instant
    )

    private val cache = java.util.concurrent.ConcurrentHashMap<String, CacheEntry>()
    private val inFlight = java.util.concurrent.ConcurrentHashMap<String, Deferred<RainNowcast?>>()

    // Application-owned IO scope of this singleton: it primes the cycle cache
    // and must outlive any single caller (main screen, rain detail page and
    // the widget refresh can all trigger a fetch for the same location).
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun locationKey(location: Location): String =
        "${Math.round(location.latitude * 100.0)}_${Math.round(location.longitude * 100.0)}"

    /**
     * Returns the nowcast for the location's capability-declared nowcast
     * source, or null when no candidate source produced data (callers should
     * then simply hide the section / fall back to hourly bars).
     */
    suspend fun getNowcast(location: Location): RainNowcast? {
        val now = Instant.now()
        val key = locationKey(location)

        // Fresh for the whole 5-min radar cycle in which it was fetched.
        val cycleStart = Instant.ofEpochSecond(now.epochSecond - now.epochSecond % 300)
        cache[key]?.let { entry ->
            if (entry.cycleStart == cycleStart && cycleStart.plusSeconds(300) > now) {
                return entry.nowcast
            }
        }

        // Atomically install exactly one deferred per location: concurrent
        // callers await the winner's result instead of hammering the radar
        // APIs. A caller cancelling its await must NOT discard the priming
        // fetch nor poison the cycle cache — hence the rethrow below.
        val deferred = synchronized(inFlight) {
            inFlight[key]?.takeIf { it.isActive }
                ?: scope.async { fetchNowcast(location, now) }
                    .also { inFlight[key] = it }
        }
        val result = try {
            deferred.await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "nowcast fetch failed: ${e.message}")
            null
        } finally {
            inFlight.remove(key, deferred)
        }
        cache[key] = CacheEntry(result, cycleStart)
        return result
    }

    /**
     * Asks the candidate sources in order until one returns data. Every
     * repository answers null itself when it has no coverage for the
     * location, failed or produced no usable points.
     */
    private suspend fun fetchNowcast(location: Location, now: Instant): RainNowcast? =
        location.nowcastCandidateSources().firstNotNullOfOrNull { source ->
            val repository = sourceRepositoryProvider.getNowcastRepository(source)
            val result = try {
                repository?.getNowcast(location, now)
            } catch (e: Exception) {
                Log.w(TAG, "${source.name} nowcast failed: ${e.message}")
                null
            }
            result
        }
}