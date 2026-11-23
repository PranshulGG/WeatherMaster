package com.pranshulgg.weather_master_app.core.network.github

import com.pranshulgg.weather_master_app.data.local.dao.github.GithubDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val api: GithubApi,
    private val dao: GithubDao
) {
    suspend fun isNewVersionAvailable(currentTag: String): Boolean = withContext(Dispatchers.IO) {

        val getSavedVersionInfo = dao.getGithubEntity()
        if (getSavedVersionInfo != null && getSavedVersionInfo.currentTag == currentTag) {

            val ageMillis = System.currentTimeMillis() - getSavedVersionInfo.lastFetchedTime
            val maxAgeMillis = TimeUnit.HOURS.toMillis(2)

            if (ageMillis < maxAgeMillis) {
                return@withContext isNewerVersion(getSavedVersionInfo.lastFetchedTag, currentTag)
            }
        }
        try {

            val response = api.fetchGithubRepos()

            val body = response.body() ?: throw UnknownHostException()

            val firstTag = body.firstOrNull { !it.prerelease }?.tagName
                ?: return@withContext false

            val isNewAvailable = isNewerVersion(firstTag, currentTag)

            dao.insertGithubEntity(currentTag, firstTag, System.currentTimeMillis())

            return@withContext isNewAvailable

        } catch (e: Exception) {
            throw e
        }
    }
}

/**
 * Only true when the release is newer than the installed version, so builds newer than the
 * latest release (e.g. built from main) aren't told to "update" to an older version
 *
 * Tags look like "v3.10.6" or "v3.0.0-beta3" (a "V" prefix and "+build" metadata are ignored).
 * If a tag can't be parsed, falls back to comparing the tags as text
 */
internal fun isNewerVersion(latestTag: String, currentTag: String): Boolean {
    fun parse(tag: String): Pair<List<Int>, Boolean>? {
        val version = tag.trim().removePrefix("v").removePrefix("V").substringBefore("+")
        val numbers = version.substringBefore("-").split(".").map { it.toIntOrNull() ?: return null }
        return numbers to version.contains("-")
    }

    val (latest, latestIsPreRelease) = parse(latestTag) ?: return latestTag != currentTag
    val (current, currentIsPreRelease) = parse(currentTag) ?: return latestTag != currentTag

    for (i in 0 until maxOf(latest.size, current.size)) {
        val comparison = latest.getOrElse(i) { 0 }.compareTo(current.getOrElse(i) { 0 })
        if (comparison != 0) return comparison > 0
    }

    // Same numbers: a release is newer than a pre-release of the same version
    return currentIsPreRelease && !latestIsPreRelease
}