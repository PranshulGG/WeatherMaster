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

class LocationServiceDisabledException implements Exception {
  final String message;
  LocationServiceDisabledException(
      [this.message = "Location services are disabled."]);

  @override
  String toString() => "LocationServiceDisabledException: $message";
}

class LocationPermissionDeniedException implements Exception {
  final String message;
  LocationPermissionDeniedException(
      [this.message = "Location permission denied."]);

  @override
  String toString() => "LocationPermissionDeniedException: $message";
}

class LocationPermissionPermanentlyDeniedException implements Exception {
  final String message;
  LocationPermissionPermanentlyDeniedException(
      [this.message =
          "Location permission permanently denied. Open settings to enable."]);

  @override
  String toString() => "LocationPermissionPermanentlyDeniedException: $message";
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

class LocationPermissionHelper {
  /// Shows SnackBars if either check fails
  static Future<bool> checkServicesAndPermission(BuildContext context) async {
    final serviceStatus = await Permission.location.serviceStatus;
    if (serviceStatus != ServiceStatus.enabled) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please enable location services')),
      );
      return false;
    }

    var status = await Permission.location.status;
    if (!status.isGranted) {
      status = await Permission.location.request();
      if (!status.isGranted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Location permission denied')),
        );
        return false;
      }
    }

    // All good
    return true;
  }
}
