import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/saved_location.dart';
import '../utils/preferences_helper.dart';
import '../utils/app_storage.dart';

Future<void> handleSaveLocationView({
  required BuildContext context,
  required VoidCallback updateUIState,
}) async {
  final selected = PreferencesHelper.getJson(PrefKeys.selectedViewLocation);
  if (selected == null) {
    return;
  }

  final lat = (selected['lat'] as num?)?.toDouble();
  final lon = (selected['lon'] as num?)?.toDouble();
  if (lat == null || lon == null) {
    return;
  }

  final saved = SavedLocation(
    latitude: lat,
    longitude: lon,
    city: selected['city']?.toString() ?? '',
    country: selected['country']?.toString() ?? '',
  );

  await _saveLocationView(saved);

  updateUIState();
}


Future<void> _saveLocationView(SavedLocation newLocation) async {
      final prefs = await SharedPreferences.getInstance();
      final existing = prefs.getString(PrefKeys.savedLocations);
      List<SavedLocation> current = [];

      if (existing != null) {
        final decoded = jsonDecode(existing) as List;
        current = decoded.map((e) => SavedLocation.fromJson(e)).toList();
      }

      bool alreadyExists = current.any((loc) =>
          loc.city == newLocation.city && loc.country == newLocation.country);

      if (!alreadyExists) {
        current.add(newLocation);
        await prefs.setString(
            PrefKeys.savedLocations,
            jsonEncode(current.map((e) => e.toJson()).toList()));
      }
}
