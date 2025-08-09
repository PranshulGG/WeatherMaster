import 'package:flutter/services.dart';
import '../widget_background.dart';
import '../utils/preferences_helper.dart';

const MethodChannel _serviceChannel =
    MethodChannel('com.pranshulgg.weather_master_app/service');

const MethodChannel _bgChannel =
    MethodChannel("com.pranshulgg.weather_master_app/bg");

// const MethodChannel _debugChannel =
//     MethodChannel("com.pranshulgg.weather_master_app/debug");

Future<void> startWeatherService() async {
  final int selectedInterval = PreferencesHelper.getInt("savedRefreshInterval") ?? 90;;
  await _serviceChannel.invokeMethod('startService', {
  'intervalMinutes': selectedInterval,
});
}

Future<void> stopWeatherService() async {
  await _serviceChannel.invokeMethod('stopService');
}

void listenForServiceEvents() {
  // Listen for service telling us to update
  _bgChannel.setMethodCallHandler((call) async {
    if (call.method == "updateWidget") {
      await updateHomeWidget(null, updatedFromHome: false);
    }
  });

}
