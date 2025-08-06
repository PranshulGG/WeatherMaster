import 'services/fetch_data.dart';
import 'utils/preferences_helper.dart';
import 'package:flutter/material.dart';
import 'package:home_widget/home_widget.dart';
import 'package:path_provider/path_provider.dart';
import 'package:hive/hive.dart';
import 'package:workmanager/workmanager.dart';
import 'package:shared_preferences/shared_preferences.dart';

@pragma('vm:entry-point')
Future<void> updateHomeWidget() async {
   try {
   print('[WidgetUpdate] Starting updateHomeWidget');

  await HomeWidget.setAppGroupId('com.pranshulgg.weather_master_app');
  print('[WidgetUpdate] Set AppGroupId');

  
  final weatherService = WeatherService();
  final homeLocation = PreferencesHelper.getJson('homeLocation');

  final result = await weatherService.fetchWeather(
    homeLocation?['lat']!,
    homeLocation?['lon']!,
    locationName: homeLocation?['cacheKey'],
    context: null,
    isBackground: true
  );

  final current = result!['data']['current'];
  final temp = current['temperature_2m'].toInt();
  final code = current['weather_code'];
  final daily = result!['data']['daily'];
  final maxTemp = daily['temperature_2m_max'][0];
  final minTemp = daily['temperature_2m_min'][0];
  final isDay = current['is_day'];


    // final temp = 200;
    // final code = 80;
    // final maxTemp = 33;
    // final minTemp = 40;
    // final isDay = 0;
     
  await HomeWidget.saveWidgetData<String>('temperatureCurrentPill', temp.round().toString());
  await HomeWidget.saveWidgetData<String>('weather_codeCurrentPill', code.toString());
  await HomeWidget.saveWidgetData<String>('todayMax', maxTemp.round().toString());
  await HomeWidget.saveWidgetData<String>('todayMin', minTemp.round().toString());

  await HomeWidget.saveWidgetData<String>('isDayWidget', isDay.toString());

  await HomeWidget.updateWidget(name: 'WeatherWidgetProvider', iOSName: null);
   print('[WidgetUpdate] Called updateWidget');
   } catch (e, stack) {
    print('[WidgetUpdate][ERROR] $e');
    print(stack);
  }
}

@pragma('vm:entry-point')
void callbackDispatcherBG() {
  Workmanager().executeTask((task, inputData) async {
    WidgetsFlutterBinding.ensureInitialized();

    final dir = await getApplicationDocumentsDirectory();
    Hive.init(dir.path);


    await PreferencesHelper.init(); 
    await updateHomeWidget();


    return Future.value(true);
  });
}

