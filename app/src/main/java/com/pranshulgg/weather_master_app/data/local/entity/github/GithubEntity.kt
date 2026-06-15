package com.pranshulgg.weather_master_app.data.local.entity.github

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github")
data class GithubEntity(
    @PrimaryKey
    val id: Long = 1,
    val currentTag: String,
    val lastFetchedTag: String,
    val lastFetchedTime: Long
)