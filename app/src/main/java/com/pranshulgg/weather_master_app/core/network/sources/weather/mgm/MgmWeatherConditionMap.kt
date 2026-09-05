package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

// Turkish condition codes ("hadise"/"hadiseKodu") cross-checked against breezy-weather's
// MGM source (github.com/breezy-weather/breezy-weather), which documents the Turkish terms.
object MgmWeatherConditionMap {
    fun getCondition(code: String?): WeatherCondition {
        return when (code) {
            "A" -> WeatherCondition.CLEAR_SKY // Açık
            "AB" -> WeatherCondition.MOSTLY_CLEAR // Az Bulutlu
            "PB" -> WeatherCondition.PARTLY_CLOUDY // Parçalı Bulutlu
            "CB" -> WeatherCondition.OVERCAST // Çok Bulutlu
            "HY" -> WeatherCondition.LIGHT_RAIN // Hafif Yağmurlu
            "Y" -> WeatherCondition.RAIN // Yağmurlu
            "KY" -> WeatherCondition.HEAVY_RAIN // Kuvvetli Yağmurlu
            "KKY" -> WeatherCondition.SLEET // Karla Karışık Yağmurlu
            "HKY" -> WeatherCondition.LIGHT_SNOW // Hafif Kar Yağışlı
            "K" -> WeatherCondition.SNOW // Kar Yağışlı
            "KYK", "YKY" -> WeatherCondition.HEAVY_SNOW // Yoğun Kar Yağışlı
            "HSY" -> WeatherCondition.LIGHT_RAIN // Hafif Sağanak Yağışlı
            "SY" -> WeatherCondition.RAIN // Sağanak Yağışlı
            "KSY" -> WeatherCondition.HEAVY_RAIN // Kuvvetli Sağanak Yağışlı
            "MSY" -> WeatherCondition.RAIN // Mevzi Sağanak Yağışlı
            "DY" -> WeatherCondition.HAIL // Dolu
            "GSY" -> WeatherCondition.THUNDERSTORM // Gökgürültülü Sağanak Yağışlı
            "KGY" -> WeatherCondition.THUNDERSTORM // Kuvvetli Gökgürültülü Sağanak Yağışlı
            "SIS" -> WeatherCondition.FOG_HAZE // Sisli
            "PUS" -> WeatherCondition.FOG_HAZE // Puslu
            "DNM" -> WeatherCondition.FOG_HAZE // Dumanlı
            "KF" -> WeatherCondition.FOG_HAZE // Toz veya Kum Fırtınası
            "HHY" -> WeatherCondition.RAIN // Yağışlı
            // Wind-only descriptors (R/GKR/KKR) and temperature-only ones (SCK/SGK - hot/cold)
            // don't correspond to a sky condition in this app's model.
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
