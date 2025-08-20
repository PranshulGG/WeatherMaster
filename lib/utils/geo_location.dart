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

      final latitudeStr = result['latitude'] as String?;
      final longitudeStr = result['longitude'] as String?;
      
      if (latitudeStr == null || longitudeStr == null) {
        throw Exception("Invalid location data received");
      }

      final latitude = double.tryParse(latitudeStr);
      final longitude = double.tryParse(longitudeStr);
      
      if (latitude == null || longitude == null) {
        throw Exception("Could not parse location coordinates");
      }
      
      // Validate coordinates are reasonable
      if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
        throw Exception("Invalid location coordinates received");
      }

      return Position(latitude: latitude, longitude: longitude);
    } on PlatformException catch (e) {
      throw Exception(e.message ?? "Unknown location error");
    } catch (e) {
      throw Exception("Failed to get location: ${e.toString()}");
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
      
      final city = result['city'] as String? ?? '';
      final country = result['country'] as String? ?? '';
      
      return {
        'city': city.isEmpty ? 'Unknown Location' : city,
        'country': country.isEmpty ? 'Unknown Country' : country,
      };
    } on PlatformException catch (e) {
      // Return fallback values on failure
      return {
        'city': 'Unknown Location', 
        'country': 'Unknown Country'
      };
    }
  }
}

class LocationPermissionHelper {
  /// Shows SnackBars if either check fails
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

    var status = await Permission.location.status;
    if (!status.isGranted) {
      status = await Permission.location.request();
      if (!status.isGranted) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Location permission denied')),
          );
        }
        return false;
      }
    }

    // All good
    return true;
  }
}
