import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/layout_config.dart';
import '../utils/app_storage.dart';

class LayoutProvider extends ChangeNotifier {
  List<LayoutBlockConfig> layoutConfig = LayoutBlockConfig.defaults();

  List<LayoutBlockConfig> _cloneLayout(Iterable<LayoutBlockConfig> source) {
    return source
        .map((b) => LayoutBlockConfig(type: b.type, isVisible: b.isVisible))
        .toList(growable: false);
  }

  bool _layoutEquals(List<LayoutBlockConfig> a, List<LayoutBlockConfig> b) {
    if (identical(a, b)) return true;
    if (a.length != b.length) return false;
    for (var i = 0; i < a.length; i++) {
      if (a[i].type != b[i].type) return false;
      if (a[i].isVisible != b[i].isVisible) return false;
    }
    return true;
  }

  Future<void> loadLayout() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonStringList = prefs.getStringList(PrefKeys.layoutConfig);

    final next = jsonStringList != null
        ? jsonStringList
            .map((json) => LayoutBlockConfig.fromJson(jsonDecode(json)))
            .toList(growable: false)
        : LayoutBlockConfig.defaults();

    if (_layoutEquals(layoutConfig, next)) return;
    layoutConfig = next;
    notifyListeners();
  }

  Future<void> saveLayout(List<LayoutBlockConfig> newConfig) async {
    final next = _cloneLayout(newConfig);
    if (_layoutEquals(layoutConfig, next)) return;
    layoutConfig = next;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList(
      PrefKeys.layoutConfig,
      layoutConfig.map((e) => jsonEncode(e.toJson())).toList(growable: false),
    );

    notifyListeners();
  }
}
