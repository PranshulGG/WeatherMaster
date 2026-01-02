import 'dart:convert';
import '../utils/app_storage.dart';

class WeatherCacheRenderer {
  static Future<Map<String, dynamic>?> renderData(String locationName) async {
    final box = await HiveBoxes.openWeatherCache();
    final jsonStr = box.get(locationName);

    if (jsonStr != null) {
      return json.decode(jsonStr);
    }
    return null;
  }
}
