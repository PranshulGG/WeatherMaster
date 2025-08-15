import 'package:flutter/services.dart';

class Position {
  final double latitude;
  final double longitude;

  Position({
    required this.latitude,
    required this.longitude,
  });
}

class NativeLocation {
  static const platform = MethodChannel('native_location');

  // Get current position from native Kotlin
  static Future<Position> getCurrentPosition() async {
    try {
      final result = await platform.invokeMethod('getCurrentPosition');

      final latitude = double.parse(result['latitude'] ?? '0');
      final longitude = double.parse(result['longitude'] ?? '0');

      print(latitude + longitude);

      return Position(latitude: latitude, longitude: longitude);
    } on PlatformException catch (e) {
      throw Exception(e.message ?? "Unknown error");
    }
  }

  // Reverse geocode
  static Future<Map<String, String>> reverseGeocode(
      double lat, double lon) async {
    try {
      final result = await platform.invokeMethod('reverseGeocode', {
        'latitude': lat,
        'longitude': lon,
      });
      return {
        'city': result['city'] ?? '',
        'country': result['country'] ?? '',
      };
    } on PlatformException catch (e) {
      // Return empty strings on failure
      return {'city': '', 'country': ''};
    }
  }
}
