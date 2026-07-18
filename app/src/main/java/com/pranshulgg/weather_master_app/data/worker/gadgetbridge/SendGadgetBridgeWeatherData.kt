package com.pranshulgg.weather_master_app.data.worker.gadgetbridge

import android.content.Context
import android.content.Intent
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt


fun sendGadgetBridgeWeatherData(context: Context, weather: Weather) {
    try {
        val rootWeatherJson = JSONObject().apply {
            put("timestamp", weather.current.time / 1000L)
            put("location", weather.location.name)
            put("currentTemp", weather.current.temperature?.inKelvin())
            put("todayMinTemp", weather.daily[0].temperatureMin?.inKelvin())
            put("todayMaxTemp", weather.daily[0].temperatureMax?.inKelvin())
            put("currentCondition", weather.current.weatherCondition.toLabel(context))
            put("currentConditionCode", getOpenWeatherMapCode(weather.current.weatherCondition))
            put("currentHumidity", weather.current.humidity)
            put("windSpeed", weather.current.windSpeed)
            put("windDirection", WindDirection.toDegrees(weather.current.windDirection))
            put("uvIndex", weather.current.uvIndex)
            put("visibility", weather.current.visibility?.toDouble())

            val dailyArray = JSONArray()
            for (i in 0..4) {
                val dailyObj = JSONObject().apply {
                    put("condition", weather.daily[i].weatherCondition.toLabel(context))
                    put("humidity", weather.daily[i].humidity)
                    put("minTemp", weather.daily[i].temperatureMin?.inKelvin())
                    put("maxTemp", weather.daily[i].temperatureMax?.inKelvin())
                }
                dailyArray.put(dailyObj)
            }
            put("forecasts", dailyArray)

            val hourlyArray = JSONArray()
            for (i in 0..11) {
                val hourlyObj = JSONObject().apply {
                    put("timestamp", weather.hourly[i].time / 1000L)
                    put("temperature", weather.hourly[i].temperature?.inKelvin())
                    put("conditionCode", getOpenWeatherMapCode(weather.hourly[i].weatherCondition))
                    put("humidity", weather.hourly[i].humidity)
                    put("windSpeed", weather.hourly[i].windSpeed)
                }
                hourlyArray.put(hourlyObj)
            }
            put("hourly", hourlyArray)
        }

        val intent = Intent().apply {
            action = "nodomain.freeyourgadget.gadgetbridge.ACTION_GENERIC_WEATHER"
            setClassName(
                "nodomain.freeyourgadget.gadgetbridge",
                "nodomain.freeyourgadget.gadgetbridge.externalevents.GenericWeatherReceiver"
            )
            putExtra("WeatherJson", rootWeatherJson.toString())
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        }

        context.sendBroadcast(intent)


    } catch (e: Exception) {
    }
}

private fun Double.inKelvin(): Int {
    return (this + 273.15).roundToInt()
}

private fun getOpenWeatherMapCode(condition: WeatherCondition): Int {
    return when (condition) {
        WeatherCondition.THUNDERSTORM -> 201
        WeatherCondition.OVERCAST -> 804
        WeatherCondition.PARTLY_CLOUDY -> 802
        WeatherCondition.MOSTLY_CLEAR -> 801
        WeatherCondition.CLEAR_SKY -> 800
        WeatherCondition.LIGHT_RAIN -> 500
        WeatherCondition.RAIN -> 501
        WeatherCondition.HEAVY_RAIN -> 502
        WeatherCondition.SNOW -> 601
        WeatherCondition.LIGHT_SNOW -> 600
        WeatherCondition.HEAVY_SNOW -> 602
        WeatherCondition.FOG_HAZE -> 721
        else -> 800
    }
}