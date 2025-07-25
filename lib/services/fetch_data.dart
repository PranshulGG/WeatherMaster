import 'dart:convert';
import 'dart:developer';
import 'package:http/http.dart' as http;
import 'package:hive/hive.dart';
import 'package:lat_lng_to_timezone/lat_lng_to_timezone.dart' as tzmap;
import '../utils/preferences_helper.dart';
import 'package:flutter/material.dart';
import '../screens/meteo_models.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class WeatherService {
  static const String _boxName = 'weatherMasterCache';

  Future<Box> _openBox() async {
    if (!Hive.isBoxOpen(_boxName)) {
      return await Hive.openBox(_boxName);
    }
    return Hive.box(_boxName);
  }
  


  Future<Map<String, dynamic>?> fetchWeather(double lat, double lon, {String? locationName, BuildContext? context, bool isOnlyView = false}) async {
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

    final alertUri = Uri.parse('https://api.weatherapi.com/v1/alerts.json').replace(queryParameters: {
    'key': dotenv.env['API_KEY_WEATHERAPI']!.toString(),
    'q': '$lat,$lon',
  });

    // final response = await http.get(uri);
    // final data = json.decode(response.body) as Map<String, dynamic>;
  
    

  final responses = await Future.wait([
    http.get(uri),
    http.get(airQualityUri),
    http.get(alertUri),
  ]);

  final weatherData = json.decode(responses[0].body) as Map<String, dynamic>;
  final airQualityData = json.decode(responses[1].body) as Map<String, dynamic>;
  final alertData = json.decode(responses[2].body) as Map<String, dynamic>;


    weatherData['current'] = sanitizeCurrent(weatherData['current']);
    weatherData['hourly'] = sanitizeHourly(weatherData['hourly']);
    weatherData['daily'] = sanitizeDaily(weatherData['daily']);

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


  final combinedDataForView = {
    ...weatherData,
    'air_quality': airQualityData,
    'alerts': alertData['alerts'] ?? [],
  };

  final nowForView = DateTime.now().toIso8601String();

 if (isOnlyView) {
    log("Only viewing data for $key. Not saving to cache.");
    return {
      'data': combinedDataForView,
      'last_updated': nowForView,
      'from_cache': false,
    };
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
     'alerts': alertData['alerts'] ?? [],
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
T nullSafeValue<T extends num>(dynamic value) {
  if (value == null) {
    if (T == int) return 0000 as T;
    if (T == double) return 0.0000001 as T;
    return 0 as T;
  }

  if (value is T) return value;

  if (value is num) {
    if (T == int) return value.toInt() as T;
    if (T == double) return value.toDouble() as T;
  }

  return T == int ? 0 as T : 0.0000001 as T;
}




  Map<String, dynamic> sanitizeCurrent(Map? current) {
    current ??= {};
    return {
      'temperature_2m': nullSafeValue<double>(current['temperature_2m']),
      'apparent_temperature': nullSafeValue<double>(current['apparent_temperature']),
      'pressure_msl': nullSafeValue<double>(current['pressure_msl']),
      'relative_humidity_2m': nullSafeValue(current['relative_humidity_2m']),
      'precipitation': nullSafeValue<double>(current['precipitation']),
      'weather_code': nullSafeValue<int>(current['weather_code']),
      'cloud_cover': nullSafeValue<int>(current['cloud_cover']),
      'wind_speed_10m': nullSafeValue<double>(current['wind_speed_10m']),
      'wind_direction_10m': nullSafeValue<int>(current['wind_direction_10m']),
      'wind_gusts_10m': nullSafeValue<double>(current['wind_gusts_10m']),
      'is_day': nullSafeValue<int>(current['is_day']),
    };
  }

  Map<String, dynamic> sanitizeHourly(Map? hourly) {
    hourly ??= {};
    return {
      'time': (hourly['time'] as List?) ?? [],
      'wind_speed_10m': (hourly['wind_speed_10m'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'wind_direction_10m': (hourly['wind_direction_10m'] as List?)?.map((v) => nullSafeValue<int>(v)).toList() ?? [],
      'relative_humidity_2m': (hourly['relative_humidity_2m'] as List?)?.map((v) => nullSafeValue<int>(v)).toList() ?? [],
      'pressure_msl': (hourly['pressure_msl'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'cloud_cover': (hourly['cloud_cover'] as List?)?.map((v) => nullSafeValue<int>(v)).toList() ?? [],
      'temperature_2m': (hourly['temperature_2m'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'dew_point_2m': (hourly['dew_point_2m'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'apparent_temperature': (hourly['apparent_temperature'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'precipitation_probability': (hourly['precipitation_probability'] as List?)?.map((v) => nullSafeValue<int>(v)).toList() ?? [],
      'precipitation': (hourly['precipitation'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'weather_code': (hourly['weather_code'] as List?)?.map((v) => nullSafeValue<int>(v)).toList() ?? [],
      'visibility': (hourly['visibility'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'uv_index': (hourly['uv_index'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
    };
  }

  Map<String, dynamic> sanitizeDaily(Map? daily) {
    daily ??= {};
    return {
      'time': (daily['time'] as List?) ?? [],
      'weather_code': (daily['weather_code'] as List?)?.map((v) => nullSafeValue<int>(v)).toList() ?? [],
      'temperature_2m_max': (daily['temperature_2m_max'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'temperature_2m_min': (daily['temperature_2m_min'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'sunrise': (daily['sunrise'] as List?) ?? [],
      'sunset': (daily['sunset'] as List?) ?? [],
      'daylight_duration': (daily['daylight_duration'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'uv_index_max': (daily['uv_index_max'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'precipitation_sum': (daily['precipitation_sum'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'precipitation_probability_max': (daily['precipitation_probability_max'] as List?)?.map((v) => nullSafeValue<int>(v)).toList() ?? [],
      'precipitation_hours': (daily['precipitation_hours'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'wind_speed_10m_max': (daily['wind_speed_10m_max'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
      'wind_gusts_10m_max': (daily['wind_gusts_10m_max'] as List?)?.map((v) => nullSafeValue<double>(v)).toList() ?? [],
    };
  }
}
