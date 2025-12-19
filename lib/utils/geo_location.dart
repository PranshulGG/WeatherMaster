import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

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

      debugPrint('Location: ${latitude.toString()}, ${longitude.toString()}');

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
      debugPrint('Coordinates: $lat, $lon');
      return {
        'city': result['city'] ?? '',
        'country': result['country'] ?? '',
      };
    } on PlatformException {
      // Return empty strings on failure
      return {'city': '', 'country': ''};
    }
  }
}

class LocationPermissionHelper {
  static Future<bool> checkServicesAndPermission(BuildContext context) async {
    final serviceStatus = await Permission.location.serviceStatus;
    if (serviceStatus != ServiceStatus.enabled) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Please enable location services')),
        );
      }
      return false;
    }

    var status = await Permission.locationWhenInUse.status;

    if (!status.isGranted && !status.isLimited) {
      if (context.mounted) {
        status = await Permission.locationWhenInUse.request();
      }
    }

    if ((status.isGranted || status.isLimited) && context.mounted) {
      return true;
    }

    if (context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Location permission denied')),
      );
    }
    return false;
  }
}
