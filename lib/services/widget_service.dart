import 'package:flutter/services.dart';
import '../widget_background.dart';

const MethodChannel _serviceChannel =
    MethodChannel('com.pranshulgg.weather_master_app/service');

const MethodChannel _bgChannel =
    MethodChannel("com.pranshulgg.weather_master_app/bg");

const MethodChannel _debugChannel =
    MethodChannel("com.pranshulgg.weather_master_app/debug");

Future<void> startWeatherService() async {
  await _serviceChannel.invokeMethod('startService');
}

Future<void> stopWeatherService() async {
  await _serviceChannel.invokeMethod('stopService');
}

void listenForServiceEvents() {
  // Listen for service telling us to update
  _bgChannel.setMethodCallHandler((call) async {
    if (call.method == "updateWidget") {
      print("[SERVICE DEBUG] updateWidget triggered");
      await updateHomeWidget(null, updatedFromHome: false);
    }
  });

  // Listen for debug messages from service
  _debugChannel.setMethodCallHandler((call) async {
    if (call.method == "debugLog") {
      print("[SERVICE DEBUG] ${call.arguments}");
    }
  });
}
