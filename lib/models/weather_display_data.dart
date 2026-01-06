import 'dart:convert';

class WeatherDisplayData {
  final Map<String, dynamic> raw;

  // Hourly Data (Filtered)
  final List<dynamic> hourlyTime;
  final List<dynamic> hourlyTemps;
  final List<dynamic> hourlyWeatherCodes;
  final List<dynamic> hourlyPrecpProb;
  final List<dynamic> hourlyDewPoint;
  final List<dynamic> hourlyVisibility;
  final List<dynamic> hourlyUvIndex;

  // Daily Data
  final List<dynamic> dailyDates;
  final List<dynamic> dailyTempsMin;
  final List<dynamic> dailyTempsMax;
  final List<dynamic> dailyPrecProb;
  final List<dynamic> dailyPrecipSum;
  final List<dynamic> dailyDaylightDuration;
  final List<dynamic> dailyWeatherCodes;
  final List<dynamic> sunriseTimes;
  final List<dynamic> sunsetTimes;

  // Processed Data
  final Map<String, (DateTime, DateTime)> daylightMap;
  final bool shouldShowRainBlock;
  final int? rainStart;
  final int? bestStart;
  final int? bestEnd;
  final List<String> timeNext12h;
  final List<double> precpNext12h;
  final List<int> precipProbNext12h;

  WeatherDisplayData({
    required this.raw,
    required this.hourlyTime,
    required this.hourlyTemps,
    required this.hourlyWeatherCodes,
    required this.hourlyPrecpProb,
    required this.hourlyDewPoint,
    required this.hourlyVisibility,
    required this.hourlyUvIndex,
    required this.dailyDates,
    required this.dailyTempsMin,
    required this.dailyTempsMax,
    required this.dailyPrecProb,
    required this.dailyPrecipSum,
    required this.dailyDaylightDuration,
    required this.dailyWeatherCodes,
    required this.sunriseTimes,
    required this.sunsetTimes,
    required this.daylightMap,
    required this.shouldShowRainBlock,
    this.rainStart,
    this.bestStart,
    this.bestEnd,
    required this.timeNext12h,
    required this.precpNext12h,
    required this.precipProbNext12h,
  });
}

WeatherDisplayData processWeatherData(Map<String, dynamic> raw) {
  final weatherData = raw['data'] ?? raw;

  final hourly = weatherData['hourly'] ?? {};
  final daily = weatherData['daily'] ?? {};

  final List<dynamic> hourlyTimeNoFilter = hourly['time'] ?? [];
  final List<dynamic> hourlyTempsNoFilter = hourly['temperature_2m'] ?? [];
  final List<dynamic> hourlyWeatherCodesNoFilter = hourly['weather_code'] ?? [];
  final List<dynamic> hourlyPrecpProbNoFilter = hourly['precipitation_probability'] ?? [];
  final List<dynamic> hourlyDewPointNoFilter = hourly['dew_point_2m'] ?? [];
  final List<dynamic> hourlyVisibilityNoFilter = hourly['visibility'] ?? [];
  final List<dynamic> hourlyUvIndexNoFilter = hourly['uv_index'] ?? [];

  // Filter hourly data
  final now = DateTime.now();
  final todayMidnight = DateTime(now.year, now.month, now.day);

  final filteredIndices = <int>[];
  for (int i = 0; i < hourlyTimeNoFilter.length; i++) {
    final time = DateTime.parse(hourlyTimeNoFilter[i]);
    if (time.isAfter(todayMidnight) || time.isAtSameMomentAs(todayMidnight)) {
      filteredIndices.add(i);
    }
  }

  final hourlyTime = filteredIndices.map((i) => hourlyTimeNoFilter[i]).toList();
  final hourlyTemps = filteredIndices.map((i) => hourlyTempsNoFilter[i]).toList();
  final hourlyWeatherCodes = filteredIndices.map((i) => hourlyWeatherCodesNoFilter[i]).toList();
  final hourlyPrecpProb = filteredIndices.map((i) => hourlyPrecpProbNoFilter[i]).toList();

  // Safe mapping for optional fields
  final hourlyDewPoint = filteredIndices.map((i) => i < hourlyDewPointNoFilter.length ? hourlyDewPointNoFilter[i] : null).toList();
  final hourlyVisibility = filteredIndices.map((i) => i < hourlyVisibilityNoFilter.length ? hourlyVisibilityNoFilter[i] : null).toList();
  final hourlyUvIndex = filteredIndices.map((i) => i < hourlyUvIndexNoFilter.length ? hourlyUvIndexNoFilter[i] : null).toList();

  // Daily Data
  final List<dynamic> dailyDates = daily['time'] ?? [];
  final List<dynamic> sunriseTimes = daily['sunrise'] ?? [];
  final List<dynamic> sunsetTimes = daily['sunset'] ?? [];
  final List<dynamic> dailyTempsMin = daily['temperature_2m_min'] ?? [];
  final List<dynamic> dailyTempsMax = daily['temperature_2m_max'] ?? [];
  final List<dynamic> dailyPrecProb = daily['precipitation_probability_max'] ?? [];
  final List<dynamic> dailyPrecipSum = daily['precipitation_sum'] ?? [];
  final List<dynamic> dailyDaylightDuration = daily['daylight_duration'] ?? [];
  final List<dynamic> dailyWeatherCodes = daily['weather_code'] ?? [];

  // Daylight Map
  final Map<String, (DateTime, DateTime)> daylightMap = {};
  if (dailyDates.isNotEmpty && sunriseTimes.isNotEmpty && sunsetTimes.isNotEmpty) {
      for (int i = 0; i < dailyDates.length; i++) {
        if (i < sunriseTimes.length && i < sunsetTimes.length) {
            daylightMap[dailyDates[i]] = (
                DateTime.parse(sunriseTimes[i]),
                DateTime.parse(sunsetTimes[i])
            );
        }
      }
  }

  // Rain Logic
  const double rainThreshold = 0.5;
  const int probThreshold = 40;

  int offsetSeconds = 0;
  if (weatherData['utc_offset_seconds'] != null) {
      offsetSeconds = int.parse(weatherData['utc_offset_seconds'].toString());
  }

  DateTime utcNow = DateTime.now().toUtc();
  DateTime nowPrecip = utcNow.add(Duration(seconds: offsetSeconds));
  nowPrecip = DateTime(
    nowPrecip.year,
    nowPrecip.month,
    nowPrecip.day,
    nowPrecip.hour,
    nowPrecip.minute,
    nowPrecip.second,
    nowPrecip.millisecond,
    nowPrecip.microsecond,
  );

  final List<String> allTimeStrings = (hourly['time'] as List?)?.cast<String>() ?? [];
  final List<double> allPrecip = (hourly['precipitation'] as List?)
          ?.map((e) => (e as num?)?.toDouble() ?? 0.0)
          .toList() ??
      [];
  final List<int> allPrecipProb = (hourly['precipitation_probability'] as List?)
          ?.map((e) => (e as num?)?.toInt() ?? 0)
          .toList() ??
      [];

  final List<String> timeNext12h = [];
  final List<double> precpNext12h = [];
  final List<int> precipProbNext12h = [];

  for (int i = 0; i < allTimeStrings.length; i++) {
    if (i >= allPrecip.length || i >= allPrecipProb.length) break;

    final time = DateTime.parse(allTimeStrings[i]);
    if (time.isAfter(nowPrecip) &&
        time.isBefore(nowPrecip.add(Duration(hours: 12)))) {
      timeNext12h.add(allTimeStrings[i]);
      precpNext12h.add(allPrecip[i]);
      precipProbNext12h.add(allPrecipProb[i]);
    }
  }

  int? rainStart;
  int longestRainLength = 0;
  int? bestStart;
  int? bestEnd;

  for (int i = 0; i < precpNext12h.length; i++) {
    if (precpNext12h[i] >= rainThreshold &&
        precipProbNext12h[i] >= probThreshold) {
      rainStart ??= i;
    } else {
      if (rainStart != null) {
        final length = i - rainStart;
        if (length >= 2 && length > longestRainLength) {
          longestRainLength = length;
          bestStart = rainStart;
          bestEnd = i - 1;
        }
        rainStart = null;
      }
    }
  }

  if (rainStart != null) {
    final length = precpNext12h.length - rainStart;
    if (length >= 2 && length > longestRainLength) {
      bestStart = rainStart;
      bestEnd = precpNext12h.length - 1;
    }
  }

  final bool shouldShowRainBlock = bestStart != null && bestEnd != null;

  return WeatherDisplayData(
    raw: raw,
    hourlyTime: hourlyTime,
    hourlyTemps: hourlyTemps,
    hourlyWeatherCodes: hourlyWeatherCodes,
    hourlyPrecpProb: hourlyPrecpProb,
    hourlyDewPoint: hourlyDewPoint,
    hourlyVisibility: hourlyVisibility,
    hourlyUvIndex: hourlyUvIndex,
    dailyDates: dailyDates,
    dailyTempsMin: dailyTempsMin,
    dailyTempsMax: dailyTempsMax,
    dailyPrecProb: dailyPrecProb,
    dailyPrecipSum: dailyPrecipSum,
    dailyDaylightDuration: dailyDaylightDuration,
    dailyWeatherCodes: dailyWeatherCodes,
    sunriseTimes: sunriseTimes,
    sunsetTimes: sunsetTimes,
    daylightMap: daylightMap,
    shouldShowRainBlock: shouldShowRainBlock,
    rainStart: rainStart,
    bestStart: bestStart,
    bestEnd: bestEnd,
    timeNext12h: timeNext12h,
    precpNext12h: precpNext12h,
    precipProbNext12h: precipProbNext12h,
  );
}
