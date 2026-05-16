package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.domain.WeatherBlockType
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlock
import com.pranshulgg.weathermaster.core.model.domain.toDomain
import com.pranshulgg.weathermaster.data.local.dao.WeatherBlocksDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.entity.WeatherBlockEntity
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class WeatherDataRepository @Inject constructor(
    private val weatherDataDao: WeatherDataDao,
    private val weatherBlocksDao: WeatherBlocksDao
) {

    // TODO: change this shit naming
    fun getAllLocationsWeather(): Flow<List<Weather>> {
        return weatherDataDao.getAllLocationsCurrentWeather()
            .map { list -> list.map { it.toDomain() } }
    }

    suspend fun saveBlocks(blocks: List<WeatherBlock>, isDaily: Boolean) {

        val entities = blocks.mapIndexed { index, block ->
            WeatherBlockEntity(
                type = block.type,
                isHidden = block.isHidden,
                position = index,
                isDaily = block.isDaily
            )
        }

        if (isDaily) {
            weatherBlocksDao.clearDailyBlocks()
        } else {
            weatherBlocksDao.clearMainBlocks()
        }
        weatherBlocksDao.insertBlocks(entities)
    }

    suspend fun loadBlocks(isDaily: Boolean = false): List<WeatherBlock> {
        val blocks = weatherBlocksDao.getBlocks()
            .filter { it.isDaily == isDaily }.map { it.toDomain() }

        val defaultBlocks = if (isDaily) {
            WeatherBlock.getDefaultForDaily()
        } else {
            WeatherBlock.getDefault()
        }

        if (blocks.isEmpty()) {
            return defaultBlocks
        }

        val oldBlocks = blocks.map { it.type }.toSet()

        val missingBlocks = defaultBlocks.filter { it.type !in oldBlocks }

        return blocks + missingBlocks
    }
}