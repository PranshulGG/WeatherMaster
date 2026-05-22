package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.blocks.WeatherBlockEntity
import javax.inject.Inject

class WeatherBlocksRepository @Inject constructor(
    private val weatherBlocksDao: WeatherBlocksDao
) {
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
