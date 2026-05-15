package com.pranshulgg.weathermaster.core.model.domain

import com.pranshulgg.weathermaster.data.local.entity.WeatherBlockEntity

enum class WeatherBlockType {
    HUMIDITY_BLOCK,
    VISIBILITY_BLOCK,
    UV_INDEX_BLOCK,
    PRESSURE_BLOCK,
    SUN_BLOCK,
    MOON_BLOCK,
    AIR_QUALITY_BLOCK,

    RAIN_BLOCK

}

data class WeatherBlock(
    val id: Long,
    val isDaily: Boolean,
    val type: WeatherBlockType,
    val isHidden: Boolean,
    val position: Int
) {
    companion object {
        fun getDefault() = listOf(
            WeatherBlock(
                type = WeatherBlockType.SUN_BLOCK,
                isHidden = false,
                position = 0,
                isDaily = false,
                id = 0
            ),
            WeatherBlock(
                type = WeatherBlockType.MOON_BLOCK,
                isHidden = false,
                position = 1,
                isDaily = false,
                id = 1
            ),
            WeatherBlock(
                type = WeatherBlockType.HUMIDITY_BLOCK,
                isHidden = false,
                position = 2,
                isDaily = false,
                id = 2
            ),
            WeatherBlock(
                type = WeatherBlockType.VISIBILITY_BLOCK,
                isHidden = false,
                position = 3,
                isDaily = false,
                id = 3
            ),
            WeatherBlock(
                type = WeatherBlockType.PRESSURE_BLOCK,
                isHidden = false,
                position = 4,
                isDaily = false,
                id = 4
            ),
            WeatherBlock(
                type = WeatherBlockType.UV_INDEX_BLOCK,
                isHidden = false,
                position = 5,
                isDaily = false,
                id = 5
            ),
            WeatherBlock(
                type = WeatherBlockType.AIR_QUALITY_BLOCK,
                isHidden = false,
                position = 6,
                isDaily = false,
                id = 6
            ),
            WeatherBlock(
                type = WeatherBlockType.RAIN_BLOCK,
                isHidden = false,
                position = 7,
                isDaily = false,
                id = 7
            ),
        )

        fun getDefaultForDaily() = listOf(
            WeatherBlock(
                type = WeatherBlockType.SUN_BLOCK,
                isHidden = false,
                position = 0,
                isDaily = true,
                id = (999L..2000L).random() // Being safe 🫠
            ),
            WeatherBlock(
                type = WeatherBlockType.MOON_BLOCK,
                isHidden = false,
                position = 1,
                isDaily = true,
                id = (999L..2000L).random()
            ),
            WeatherBlock(
                type = WeatherBlockType.RAIN_BLOCK,
                isHidden = false,
                position = 2,
                isDaily = true,
                id = (999L..2000L).random()
            ),
            WeatherBlock(
                type = WeatherBlockType.UV_INDEX_BLOCK,
                isHidden = false,
                position = 3,
                isDaily = true,
                id = (999L..2000L).random()
            ),
        )
    }
}


fun WeatherBlockEntity.toDomain(): WeatherBlock {
    return WeatherBlock(
        type = type,
        isHidden = isHidden,
        position = position,
        isDaily = isDaily,
        id = id
    )
}


