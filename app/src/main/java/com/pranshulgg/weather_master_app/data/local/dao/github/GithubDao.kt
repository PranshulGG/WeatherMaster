package com.pranshulgg.weather_master_app.data.local.dao.github

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weather_master_app.data.local.entity.github.GithubEntity


@Dao
interface GithubDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GithubEntity)
    suspend fun insertGithubEntity(currentTag: String, lastFetchedTag: String, time: Long) {
        insert(
            GithubEntity(
                id = 1,
                currentTag = currentTag,
                lastFetchedTag = lastFetchedTag,
                lastFetchedTime = time
            )
        )
    }

    @Query("SELECT * FROM github WHERE id = 1 LIMIT 1")
    suspend fun getGithubEntity(): GithubEntity?
}