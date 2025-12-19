import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/layout_config.dart';
import '../utils/app_storage.dart';

class LayoutProvider extends ChangeNotifier {
  List<LayoutBlockConfig> layoutConfig = [];

  Future<void> loadLayout() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonStringList = prefs.getStringList(PrefKeys.layoutConfig);

    if (jsonStringList != null) {
      layoutConfig = jsonStringList
          .map((json) => LayoutBlockConfig.fromJson(jsonDecode(json)))
          .toList();
    } else {
      layoutConfig = LayoutBlockConfig.defaults();
    }

    notifyListeners(); 
  }

  Future<void> saveLayout(List<LayoutBlockConfig> newConfig) async {
    layoutConfig = newConfig;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList(
      PrefKeys.layoutConfig,
      layoutConfig.map((e) => jsonEncode(e.toJson())).toList(),
    );

    notifyListeners();
  }
}
