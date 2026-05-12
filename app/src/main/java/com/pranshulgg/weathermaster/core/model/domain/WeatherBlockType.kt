package com.pranshulgg.weathermaster.core.model.domain

import com.pranshulgg.weathermaster.data.local.entity.WeatherBlockEntity

enum class WeatherBlockType {
    HUMIDITY_BLOCK,
    VISIBILITY_BLOCK,
    UV_INDEX_BLOCK,
    PRESSURE_BLOCK,
    SUN_BLOCK,
    MOON_BLOCK,
    AIR_QUALITY

}

data class WeatherBlock(
    val type: WeatherBlockType,
    val isHidden: Boolean,
    val position: Int
) {
    companion object {
        fun getDefault() = listOf(
            WeatherBlock(
                type = WeatherBlockType.SUN_BLOCK,
                isHidden = false,
                position = 0
            ),
            WeatherBlock(
                type = WeatherBlockType.MOON_BLOCK,
                isHidden = false,
                position = 1
            ),
            WeatherBlock(
                type = WeatherBlockType.HUMIDITY_BLOCK,
                isHidden = false,
                position = 2
            ),
            WeatherBlock(
                type = WeatherBlockType.VISIBILITY_BLOCK,
                isHidden = false,
                position = 3
            ),
            WeatherBlock(
                type = WeatherBlockType.PRESSURE_BLOCK,
                isHidden = false,
                position = 4
            ),
            WeatherBlock(
                type = WeatherBlockType.UV_INDEX_BLOCK,
                isHidden = false,
                position = 5
            ),
            WeatherBlock(
                type = WeatherBlockType.AIR_QUALITY,
                isHidden = false,
                position = 6
            )
        )
    }
}


fun WeatherBlockEntity.toDomain(): WeatherBlock {
    return WeatherBlock(
        type = type,
        isHidden = isHidden,
        position = position
    )
}


