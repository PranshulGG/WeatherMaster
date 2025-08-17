import 'dart:convert';
import 'services/fetch_data.dart';
import 'utils/preferences_helper.dart';
import 'package:flutter/material.dart';
import 'package:home_widget/home_widget.dart';
import 'package:path_provider/path_provider.dart';
import 'package:hive/hive.dart';
import 'utils/unit_converter.dart';
import 'utils/condition_label_map.dart';
import 'package:flutter/services.dart' show rootBundle;

@pragma('vm:entry-point')
Future<void> updateHomeWidget(weather, {bool updatedFromHome = false}) async {
  try {
    WidgetsFlutterBinding.ensureInitialized();

    final dir = await getApplicationDocumentsDirectory();
    Hive.init(dir.path);

    await PreferencesHelper.init();
    await HomeWidget.setAppGroupId('com.pranshulgg.weather_master_app');

    double _safeToDouble(dynamic value, [double fallback = 0.0]) {
      if (value == null) return fallback;
      if (value is num) return value.toDouble();
      return double.tryParse(value.toString()) ?? fallback;
    }

    final dynamic temp;
    final dynamic code;
    final dynamic maxTemp;
    final dynamic minTemp;
    final dynamic isDay;
    final List<dynamic> hourlyTime;
    final List<dynamic> hourlyTemps;
    final List<dynamic> hourlyWeatherCodes;
    final String utcOffsetSeconds;

    final timeUnit = PreferencesHelper.getString("selectedTimeUnit") ?? "12 hr";
    final tempUnit =
        PreferencesHelper.getString("selectedTempUnit") ?? "Celsius";

    final triggerFromWorker =
        PreferencesHelper.getBool("triggerfromWorker") ?? false;
    String? lastUpdatedString =
        PreferencesHelper.getString('lastUpdatedFromHome');

    if (weather == null && updatedFromHome == false) {
      if (triggerFromWorker == false) {
        PreferencesHelper.setBool('triggerfromWorker', true);
        return;
      }

      if (lastUpdatedString != null) {
        DateTime lastUpdated = DateTime.parse(lastUpdatedString);

        Duration difference = DateTime.now().difference(lastUpdated);

        if (difference.inMinutes < 45) {
          return;
        }
      }

      final weatherService = WeatherService();
      final homeLocation = PreferencesHelper.getJson('homeLocation');

      if (homeLocation == null) {
        return;
      }

      final result = await weatherService.fetchWeather(
          homeLocation?['lat']!, homeLocation?['lon']!,
          locationName: homeLocation?['cacheKey'],
          context: null,
          isBackground: true);

      final current = result!['data']['current'];
      temp = _safeToDouble(current['temperature_2m'].toDouble());
      code = current['weather_code'];
      final dailyData = result!['data']['daily'];
      final hourly = result!['data']['hourly'] ?? {};

      hourlyTime = hourly['time'];
      hourlyTemps = hourly['temperature_2m'];
      hourlyWeatherCodes = hourly['weather_code'];

      maxTemp = _safeToDouble(dailyData['temperature_2m_max'][0]);
      minTemp = _safeToDouble(dailyData['temperature_2m_min'][0]);
      isDay = current['is_day'];

      utcOffsetSeconds = result!['data']['utc_offset_seconds'].toString();
    } else {
      final currentData = weather['current'];

      temp = currentData['temperature_2m'].toDouble();
      code = currentData['weather_code'];
      final dailyData = weather['daily'];
      final hourly = weather['hourly'] ?? {};

      maxTemp = dailyData['temperature_2m_max'][0];
      minTemp = dailyData['temperature_2m_min'][0];
      isDay = currentData['is_day'];

      hourlyTime = hourly['time'];
      hourlyTemps = hourly['temperature_2m'];
      hourlyWeatherCodes = hourly['weather_code'];
      utcOffsetSeconds = weather['utc_offset_seconds'].toString();
    }

    int offsetSeconds = int.parse(utcOffsetSeconds);
    DateTime utcNow = DateTime.now().toUtc();
    DateTime now = utcNow.add(Duration(seconds: offsetSeconds));

    now = DateTime(now.year, now.month, now.day, now.hour);

    // Find the first index in hourlyTime that is >= now
    int startIndex = hourlyTime.indexWhere((time) {
      DateTime hourlyTimeItem = DateTime.parse(time);
      return !hourlyTimeItem.isBefore(now); // same as >= now
    });

    // If for some reason it's not found, default to 0 (safe fallback)
    if (startIndex == -1) startIndex = 0;

    for (int i = 0; i < 4; i++) {
      int currentIndex = startIndex + i;

      // Prevent out-of-bounds error
      if (currentIndex >= hourlyTemps.length ||
          currentIndex >= hourlyTime.length ||
          currentIndex >= hourlyWeatherCodes.length) break;

      // Temp conversion
      final tempValue = hourlyTemps[currentIndex].toDouble();
      final formattedTemp = tempUnit == 'Fahrenheit'
          ? UnitConverter.celsiusToFahrenheit(tempValue).round().toString()
          : tempValue.round().toString();

      // Time formatting
      final rawTimeString = hourlyTime[currentIndex];
      final roundedDisplayTime = DateTime.parse(rawTimeString);

      final formattedTime = timeUnit == '24 hr'
          ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00"
          : UnitConverter.formatTo12Hour(roundedDisplayTime);

      await HomeWidget.saveWidgetData<String>('hourly_temp_$i', formattedTemp);
      await HomeWidget.saveWidgetData<String>('hourly_time_$i', formattedTime);
      await HomeWidget.saveWidgetData<String>(
          'hourly_code_$i', hourlyWeatherCodes[currentIndex].toString());
    }

    final convertedTemp = tempUnit == 'Fahrenheit'
        ? UnitConverter.celsiusToFahrenheit(temp).round()
        : temp.round();

    final convertedMaxTemp = tempUnit == 'Fahrenheit'
        ? UnitConverter.celsiusToFahrenheit(maxTemp).round()
        : maxTemp.round();

    final convertedMinTemp = tempUnit == 'Fahrenheit'
        ? UnitConverter.celsiusToFahrenheit(minTemp).round()
        : minTemp.round();

    final currentTempFormatted = "${convertedTemp.round()}";
    final maxTempFormatted = "${convertedMaxTemp.round()}";
    final minTempFormatted = "${convertedMinTemp.round()}";

    final conditionKey = WeatherConditionMapper.getConditionLabel(code, isDay);
    final localeString = PreferencesHelper.getString('locale') ?? 'en';

    Locale locale;

    if (localeString.contains('-')) {
      var parts = localeString.split('-');
      locale = Locale(parts[0], parts[1]);
    } else if (localeString.contains('_')) {
      var parts = localeString.split('_');
      locale = Locale(parts[0], parts[1]);
    } else {
      locale = Locale(localeString);
    }

    String translationFileName = locale.languageCode;
    if (locale.countryCode != null && locale.countryCode!.isNotEmpty) {
      translationFileName += '-${locale.countryCode}';
    }

    Map<String, dynamic> translations;

    try {
      final String data = await rootBundle
          .loadString('assets/translations/$translationFileName.json');
      translations = jsonDecode(data);
    } catch (e) {
      print(
          '[Translation] Could not load $translationFileName.json, falling back to en.json');
      final String data =
          await rootBundle.loadString('assets/translations/en.json');
      translations = jsonDecode(data);
    }

    final conditionName = translations[conditionKey] ?? conditionKey;

    await HomeWidget.saveWidgetData<String>(
        'temperatureCurrentPill', currentTempFormatted);
    await HomeWidget.saveWidgetData<String>(
        'weather_codeCurrentPill', code.toString());
    await HomeWidget.saveWidgetData<String>('todayMax', maxTempFormatted);
    await HomeWidget.saveWidgetData<String>('todayMin', minTempFormatted);
    await HomeWidget.saveWidgetData<String>('locationNameWidget',
        "${PreferencesHelper.getJson('homeLocation')?['city']}, ${PreferencesHelper.getJson('homeLocation')?['country']}");
    await HomeWidget.saveWidgetData<String>(
        'locationCurrentConditon', conditionName);

    await HomeWidget.saveWidgetData<String>('isDayWidget', isDay.toString());

    await HomeWidget.updateWidget(name: 'WeatherWidgetProvider', iOSName: null);
    await HomeWidget.updateWidget(
        name: 'WeatherWidgetCastProvider', iOSName: null);
    await HomeWidget.updateWidget(name: 'PillWidgetProvider', iOSName: null);
    await HomeWidget.updateWidget(name: 'GlanceWidgetProvider', iOSName: null);
    await HomeWidget.updateWidget(
        name: 'DateCurrentWidgetProvider', iOSName: null);

    print('[WidgetUpdate] Called updateWidget');
  } catch (e, stack) {
    print('[WidgetUpdate][ERROR] $e');
    print(stack);
  }
}
