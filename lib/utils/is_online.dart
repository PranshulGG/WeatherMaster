import 'package:flutter/services.dart';

class NativeNetwork {
  static const _channel =
      MethodChannel('com.pranshulgg.weather_master_app/service');

  static Future<bool> isOnline() async {
    final result = await _channel.invokeMethod<bool>('isOnline');
    return result ?? false;
  }
}
