import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/layout_config.dart';

class LayoutProvider extends ChangeNotifier {
  List<LayoutBlockConfig> layoutConfig = [];

  Future<void> loadLayout() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonStringList = prefs.getStringList('layout_config');

    if (jsonStringList != null) {
      layoutConfig = jsonStringList
          .map((json) => LayoutBlockConfig.fromJson(jsonDecode(json)))
          .toList();
    }

    notifyListeners(); 
  }

  Future<void> saveLayout(List<LayoutBlockConfig> newConfig) async {
    layoutConfig = newConfig;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList(
      'layout_config',
      layoutConfig.map((e) => jsonEncode(e.toJson())).toList(),
    );

    notifyListeners();
  }
}
