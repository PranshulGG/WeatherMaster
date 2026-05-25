package com.pranshulgg.weather_master_app.core.network.github

import java.net.UnknownHostException
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val api: GithubApi
) {
    suspend fun isNewVersionAvailable(currentTag: String): Boolean {
        try {

            val response = api.fetchGithubRepos()

            val body = response.body() ?: throw UnknownHostException()

            val firstTag = body.firstOrNull { !it.prerelease }?.tagName
                ?: return false

            val isNewAvailable = firstTag != currentTag

            return isNewAvailable

        } catch (e: Exception) {
            throw e
        }
    }
}