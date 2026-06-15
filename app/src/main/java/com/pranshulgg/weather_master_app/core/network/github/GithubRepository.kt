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
            val maxAgeMillis = TimeUnit.HOURS.toMillis(24)

            if (ageMillis < maxAgeMillis) {
                return@withContext getSavedVersionInfo.lastFetchedTag != currentTag
            }
        }
        try {

            val response = api.fetchGithubRepos()

            val body = response.body() ?: throw UnknownHostException()

            val firstTag = body.firstOrNull { !it.prerelease }?.tagName
                ?: return@withContext false

            val isNewAvailable = firstTag != currentTag

            dao.insertGithubEntity(currentTag, firstTag, System.currentTimeMillis())

            return@withContext isNewAvailable

        } catch (e: Exception) {
            throw e
        }
    }
}