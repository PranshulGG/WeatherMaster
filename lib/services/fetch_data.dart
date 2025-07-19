import 'dart:convert';
import 'dart:developer';
import 'package:http/http.dart' as http;
import 'package:hive/hive.dart';
import 'package:lat_lng_to_timezone/lat_lng_to_timezone.dart' as tzmap;
import '../utils/preferences_helper.dart';
import 'package:flutter/material.dart';
import '../screens/meteo_models.dart';

class WeatherService {
  static const String _boxName = 'weatherMasterCache';

  Future<Box> _openBox() async {
    if (!Hive.isBoxOpen(_boxName)) {
      return await Hive.openBox(_boxName);
    }
    return Hive.box(_boxName);
  }
  


  Future<Map<String, dynamic>?> fetchWeather(double lat, double lon, {String? locationName, BuildContext? context,}) async {
    final timezone = tzmap.latLngToTimezoneString(lat, lon);
    final key = locationName ?? 'loc_${lat}_${lon}';
    final box = await _openBox();

    final uri = Uri.parse('https://api.open-meteo.com/v1/forecast').replace(queryParameters: {
      'latitude': lat.toString(),
      'longitude': lon.toString(),
      'current': 'temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m',
      'hourly': 'wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index',
      'daily': 'weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max',
      'timezone': timezone,
      'forecast_days': '14',
      'models': PreferencesHelper.getString("selectedWeatherModel") ?? "best_match"
    });
    final airQualityUri = Uri.parse('https://air-quality-api.open-meteo.com/v1/air-quality').replace(queryParameters: {
    'latitude': lat.toString(),
    'longitude': lon.toString(),
    'current': 'us_aqi,european_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,alder_pollen,birch_pollen,grass_pollen,mugwort_pollen,olive_pollen,ragweed_pollen',
    'timezone': timezone,
    'forecast_hours': '1',
  });

    // final response = await http.get(uri);
    // final data = json.decode(response.body) as Map<String, dynamic>;

  final responses = await Future.wait([
    http.get(uri),
    http.get(airQualityUri),
  ]);

  final weatherData = json.decode(responses[0].body) as Map<String, dynamic>;
  final airQualityData = json.decode(responses[1].body) as Map<String, dynamic>;

   if (weatherData['error'] == true || airQualityData['error'] == true) {
    final reason = weatherData['reason'] ?? airQualityData['reason'] ?? 'Unknown error';
    
    if (context != null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          duration: Duration(seconds: 10),
          content: Text("$reason. Please change your model"),
          
          action: SnackBarAction(
            label: 'Change model',
            onPressed: () {
              Navigator.of(context).push(
                  MaterialPageRoute(builder: (_) => const MeteoModelsPage()),
                );
            },
          ),
        ),
      );
    }


    return null; 
  }

  final existingJson = box.get(key);
  if (existingJson != null) {
    final cachedMap = json.decode(existingJson);
    final cachedData = cachedMap['data'];
    final lastUpdated = cachedMap['last_updated'];

    if (json.encode(cachedData['current']) == json.encode(weatherData['current']) &&
        json.encode(cachedData['air_quality'] ?? {}) == json.encode(airQualityData)) {
      log("Hive: No update needed for $key");
      return {
        'data': cachedData,
        'last_updated': lastUpdated,
        'from_cache': true,
      };
    }
  }

  final now = DateTime.now().toIso8601String();
  final combinedData = {
    ...weatherData,
    'air_quality': airQualityData,
  };

  final wrappedData = {
    'data': combinedData,
    'last_updated': now,
  };

  await box.put(key, json.encode(wrappedData));
  log("Hive: Updated cache for $key at $now");

  return {
    'data': combinedData,
    'last_updated': now,
    'from_cache': false,
  };
  }
}
