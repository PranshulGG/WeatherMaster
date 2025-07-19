import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';

Future<Position> getCurrentPosition() async {
  bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
  if (!serviceEnabled) {
    throw Exception("Location services are disabled.");
  }

  LocationPermission permission = await Geolocator.checkPermission();
  if (permission == LocationPermission.denied) {
    permission = await Geolocator.requestPermission();
    if (permission == LocationPermission.denied) {
      throw Exception("Location permission denied.");
    }
  }

  if (permission == LocationPermission.deniedForever) {
    throw Exception("Location permission permanently denied.");
  }

  return await Geolocator.getCurrentPosition(
      desiredAccuracy: LocationAccuracy.high);
}



Future<Map<String, String>> reverseGeocode(double latitude, double longitude) async {
  try {
    List<Placemark> placemarks = await placemarkFromCoordinates(latitude, longitude);

    if (placemarks.isNotEmpty) {
      final Placemark place = placemarks.first;
      String city = place.locality?.isNotEmpty == true
          ? place.locality!
          : place.subAdministrativeArea?.isNotEmpty == true
              ? place.subAdministrativeArea!
              : place.administrativeArea?.isNotEmpty == true
                  ? place.administrativeArea!
                  : place.name?.isNotEmpty == true
                      ? place.name!
                      : 'Current';

      String country = place.country ?? '';

      return {
        'city': city,
        'country': country,
      };
    } else {
      return {
        'city': '',
        'country': '',
      };
    }
  } catch (e) {
    print('Reverse geocoding failed: $e');
    return {
      'city': '',
      'country': '',
    };
  }
}

