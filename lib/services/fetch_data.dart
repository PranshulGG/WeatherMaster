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
  


  Future<Map<String, dynamic>?> fetchWeather(double lat, double lon, {String? locationName, BuildContext? context, bool isOnlyView = false, bool isBackground = false}) async {
    final timezone = tzmap.latLngToTimezoneString(lat, lon);
    final key = locationName ?? 'loc_${lat}_${lon}';
    final box = await _openBox();

    final selectedModel = PreferencesHelper.getString("selectedWeatherModel") ?? "best_match";
    
    final uri = Uri.parse('https://api.open-meteo.com/v1/forecast').replace(queryParameters: {
      'latitude': lat.toString(),
      'longitude': lon.toString(),
      'current': 'temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m',
      'hourly': 'wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index',
      'daily': 'weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max',
      'timezone': timezone,
      'forecast_days': '7',
      'models': selectedModel
    });
    final airQualityUri = Uri.parse('https://air-quality-api.open-meteo.com/v1/air-quality').replace(queryParameters: {
    'latitude': lat.toString(),
    'longitude': lon.toString(),
    'current': 'us_aqi,european_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,alder_pollen,birch_pollen,grass_pollen,mugwort_pollen,olive_pollen,ragweed_pollen',
    'timezone': timezone,
    'forecast_hours': '1',
  });



  
  bool showAlerts = PreferencesHelper.getBool("showAlerts") ?? true;

  // Prepare list of HTTP requests (always include weather and air quality)
  final requests = <Future<http.Response>>[
    http.get(uri),
    http.get(airQualityUri),
  ];

  // Conditionally add alerts request only if user wants them
  if (showAlerts && !isBackground) {
    final alertUri = Uri.parse('https://api.weatherapi.com/v1/alerts.json').replace(queryParameters: {
      'key': dotenv.env['API_KEY_WEATHERAPI']!.toString(),
      'q': '$lat,$lon',
    });
    requests.add(http.get(alertUri));
  } 

  // Execute all HTTP requests in parallel
  final responses = await Future.wait(requests);
  

  // Parse responses
  final weatherData = json.decode(responses[0].body) as Map<String, dynamic>;
  final airQualityData = json.decode(responses[1].body) as Map<String, dynamic>;

  // Conditionally decode alert data if it was fetched
  final alertData = showAlerts && responses.length > 2
      ? json.decode(responses[2].body) as Map<String, dynamic>
      : {};

  // Check if we need fallback data for missing fields
  Map<String, dynamic> finalWeatherData = weatherData;
  if (selectedModel != "best_match" && _hasIncompleteData(weatherData)) {
    finalWeatherData = await _fetchWithFallback(lat, lon, timezone, weatherData, selectedModel);
  }

  finalWeatherData['current'] = sanitizeCurrent(finalWeatherData['current']);
  finalWeatherData['hourly'] = sanitizeHourly(finalWeatherData['hourly']);
  finalWeatherData['daily'] = sanitizeDaily(finalWeatherData['daily']);

   if (finalWeatherData['error'] == true || airQualityData['error'] == true) {
    final reason = finalWeatherData['reason'] ?? airQualityData['reason'] ?? 'Unknown error';
    
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
    ...finalWeatherData,
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

    if (json.encode(cachedData['current']) == json.encode(finalWeatherData['current']) &&
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
    ...finalWeatherData,
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

  /// Check if weather data has missing or incomplete fields
  bool _hasIncompleteData(Map<String, dynamic> weatherData) {
    // Check current data completeness
    final current = weatherData['current'] as Map<String, dynamic>?;
    if (current == null) return true;
    
    // All fields requested in the API call that should be available
    final allCurrentFields = [
      'temperature_2m', 'is_day', 'apparent_temperature', 'pressure_msl', 
      'relative_humidity_2m', 'precipitation', 'weather_code', 'cloud_cover', 
      'wind_speed_10m', 'wind_direction_10m', 'wind_gusts_10m'
    ];
    
    // Check for missing current fields
    int missingCurrentFields = 0;
    for (final field in allCurrentFields) {
      if (current[field] == null) {
        missingCurrentFields++;
      }
    }
    
    // Check hourly data completeness
    final hourly = weatherData['hourly'] as Map<String, dynamic>?;
    if (hourly == null) return true;
    
    final allHourlyFields = [
      'wind_speed_10m', 'wind_direction_10m', 'relative_humidity_2m', 'pressure_msl', 
      'cloud_cover', 'temperature_2m', 'dew_point_2m', 'apparent_temperature', 
      'precipitation_probability', 'precipitation', 'weather_code', 'visibility', 'uv_index'
    ];
    
    // Check for missing hourly fields
    int missingHourlyFields = 0;
    for (final field in allHourlyFields) {
      final data = hourly[field] as List?;
      if (data == null || data.isEmpty || !data.any((value) => value != null)) {
        missingHourlyFields++;
      }
    }
    
    // Check daily data completeness
    final daily = weatherData['daily'] as Map<String, dynamic>?;
    if (daily == null) return true;
    
    final allDailyFields = [
      'weather_code', 'temperature_2m_max', 'temperature_2m_min', 'sunrise', 'sunset', 
      'daylight_duration', 'uv_index_max', 'precipitation_sum', 'precipitation_probability_max', 
      'precipitation_hours', 'wind_speed_10m_max', 'wind_gusts_10m_max'
    ];
    
    // Check for missing daily fields
    int missingDailyFields = 0;
    for (final field in allDailyFields) {
      final data = daily[field] as List?;
      if (data == null || data.isEmpty || !data.any((value) => value != null)) {
        missingDailyFields++;
      }
    }
    
    // Trigger fallback if any fields are missing
    final totalMissing = missingCurrentFields + missingHourlyFields + missingDailyFields;
    return totalMissing > 0;
  }

  /// Fetch weather data with fallback to best_match for missing fields
  Future<Map<String, dynamic>> _fetchWithFallback(
    double lat, 
    double lon, 
    String timezone, 
    Map<String, dynamic> primaryData, 
    String selectedModel
  ) async {
    try {
      // Fetch fallback data using best_match
      final fallbackUri = Uri.parse('https://api.open-meteo.com/v1/forecast').replace(queryParameters: {
        'latitude': lat.toString(),
        'longitude': lon.toString(),
        'current': 'temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m',
        'hourly': 'wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index',
        'daily': 'weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max',
        'timezone': timezone,
        'forecast_days': '7',
        'models': 'best_match'
      });

      final fallbackResponse = await http.get(fallbackUri);
      final fallbackData = json.decode(fallbackResponse.body) as Map<String, dynamic>;

      if (fallbackData['error'] == true) {
        return primaryData;
      }

      // Merge data: primary model takes precedence, fallback fills gaps
      return _mergeWeatherData(primaryData, fallbackData);
    } catch (e) {
      return primaryData;
    }
  }

  /// Merge primary weather data with fallback data, prioritizing primary data
  Map<String, dynamic> _mergeWeatherData(
    Map<String, dynamic> primary, 
    Map<String, dynamic> fallback
  ) {
    final merged = Map<String, dynamic>.from(primary);
    
    // Merge current data
    merged['current'] = _mergeSection(
      primary['current'] as Map<String, dynamic>?, 
      fallback['current'] as Map<String, dynamic>?
    );
    
    // Merge hourly data
    merged['hourly'] = _mergeSection(
      primary['hourly'] as Map<String, dynamic>?, 
      fallback['hourly'] as Map<String, dynamic>?
    );
    
    // Merge daily data
    merged['daily'] = _mergeSection(
      primary['daily'] as Map<String, dynamic>?, 
      fallback['daily'] as Map<String, dynamic>?
    );
    
    // Keep other fields from primary, fallback if missing
    for (final key in fallback.keys) {
      if (!merged.containsKey(key) || merged[key] == null) {
        merged[key] = fallback[key];
      }
    }
    
    return merged;
  }

  /// Merge a specific section (current/hourly/daily) with fallback data
  Map<String, dynamic> _mergeSection(
    Map<String, dynamic>? primary, 
    Map<String, dynamic>? fallback
  ) {
    if (primary == null && fallback == null) return {};
    if (primary == null) return Map<String, dynamic>.from(fallback!);
    if (fallback == null) return Map<String, dynamic>.from(primary);
    
    final merged = Map<String, dynamic>.from(primary);
    
    for (final key in fallback.keys) {
      if (!merged.containsKey(key) || merged[key] == null) {
        merged[key] = fallback[key];
      } else if (merged[key] is List) {
        // For array fields, use primary if it has valid data, otherwise use fallback
        final primaryList = merged[key] as List;
        final fallbackList = fallback[key] as List;
        
        // Check if primary list has any non-null values
        final primaryHasValidData = primaryList.isNotEmpty && primaryList.any((value) => value != null);
        final fallbackHasValidData = fallbackList.isNotEmpty && fallbackList.any((value) => value != null);
        
        // Use fallback if primary has no valid data but fallback does
        if (!primaryHasValidData && fallbackHasValidData) {
          merged[key] = fallbackList;
        }
      }
    }
    
    return merged;
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