import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter/material.dart';

class PreferencesHelper {
  static SharedPreferences? _prefs;

  /// Initialize shared preferences
  static Future<void> init() async {
    _prefs ??= await SharedPreferences.getInstance();
  }

  // -------------------- String --------------------

  static Future<bool> setString(String key, String value) async {
    await init();
    return _prefs!.setString(key, value);
  }

  static String? getString(String key) {
    return _prefs?.getString(key);
  }

  // -------------------- Bool --------------------

  static Future<bool> setBool(String key, bool value) async {
    await init();
    return _prefs!.setBool(key, value);
  }

  static bool? getBool(String key) {
    return _prefs?.getBool(key);
  }

  // -------------------- Int --------------------

  static Future<bool> setInt(String key, int value) async {
    await init();
    return _prefs!.setInt(key, value);
  }

  static int? getInt(String key) {
    return _prefs?.getInt(key);
  }

  // -------------------- Double --------------------

  static Future<bool> setDouble(String key, double value) async {
    await init();
    return _prefs!.setDouble(key, value);
  }

  static double? getDouble(String key) {
    return _prefs?.getDouble(key);
  }

  // -------------------- List<String> --------------------

  static Future<bool> setStringList(String key, List<String> value) async {
    await init();
    return _prefs!.setStringList(key, value);
  }

  static List<String>? getStringList(String key) {
    return _prefs?.getStringList(key);
  }

  // -------------------- JSON (Map) --------------------

  static Future<bool> setJson(String key, Map<String, dynamic> value) async {
    final jsonString = jsonEncode(value);
    return setString(key, jsonString);
  }

  static Map<String, dynamic>? getJson(String key) {
    final jsonString = getString(key);
    if (jsonString == null) return null;

    try {
      return jsonDecode(jsonString) as Map<String, dynamic>;
    } catch (e) {
      return null;
    }
  }
  

  // -------------------- Color --------------------

static Future<bool> setColor(String key, Color color) async {
  return setInt(key, color.value);
}

static Color? getColor(String key) {
  final colorValue = getInt(key);
  if (colorValue == null) return null;
  return Color(colorValue);
}

  // -------------------- Utilities --------------------

  static Future<bool> remove(String key) async {
    await init();
    return _prefs!.remove(key);
  }

  static Future<bool> clear() async {
    await init();
    return _prefs!.clear();
  }

  /// Optional: Log all preferences for debugging
  static void logAllPrefs() {
    if (_prefs == null) {
      print('Preferences not initialized.');
      return;
    }

    print('---- Preferences Dump ----');
    for (final key in _prefs!.getKeys()) {
      print('$key: ${_prefs!.get(key)}');
    }
    print('--------------------------');
  }


  
}
