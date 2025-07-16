import 'package:flutter/material.dart';
import 'package:material_color_utilities/material_color_utilities.dart';
import '../utils/preferences_helper.dart';
import 'package:dynamic_color/dynamic_color.dart';

class ThemeController extends ChangeNotifier {
  Color? _seedColor; 
  CorePalette? _corePalette;

  ThemeMode _themeMode = PreferencesHelper.getString("AppTheme") == "Light"
      ? ThemeMode.light
      : ThemeMode.dark;

  bool isCustom = PreferencesHelper.getBool("usingCustomSeed") ?? false;
  bool _isUsingDynamicColor = false;

  bool _isDynamicColorSupported = false;

bool get isDynamicColorSupported => _isDynamicColorSupported;

Future<void> checkDynamicColorSupport() async {
  _isDynamicColorSupported = (await DynamicColorPlugin.getCorePalette()) != null;
}

  ThemeController();

  Future<void> initialize({Color fallbackColor = const Color(0xFF012E7C)}) async {
    bool useDynamicColors = PreferencesHelper.getBool("DynamicColors") ?? false;

    if (useDynamicColors) {
      await loadDynamicColors();
    } else if (isCustom) {
      setSeedColor(PreferencesHelper.getColor("CustomMaterialColor") ?? Colors.blue);
    } else {
      setSeedColor(fallbackColor);
    }
  }

  Color get seedColor => _seedColor!;
  CorePalette get corePalette => _corePalette!;
  ThemeMode get themeMode => _themeMode;
  bool get isUsingDynamicColor => _isUsingDynamicColor;

  void setSeedColor(Color newColor) {
    _seedColor = newColor;
    _corePalette = CorePalette.of(newColor.value);
    _isUsingDynamicColor = false;
    notifyListeners();
  }

void setSeedColorSilently(Color newColor) {
  _seedColor = newColor;
  _corePalette = CorePalette.of(newColor.value);
  _isUsingDynamicColor = false;
}


  void setThemeMode(ThemeMode mode) {
    _themeMode = mode;
    notifyListeners();
  }

  Brightness get currentBrightness {
    switch (_themeMode) {
      case ThemeMode.light:
        return Brightness.light;
      case ThemeMode.dark:
        return Brightness.dark;
      case ThemeMode.system:
        return WidgetsBinding.instance.window.platformBrightness;
    }
  }

  Future<void> loadDynamicColors() async {
    final corePalette = await DynamicColorPlugin.getCorePalette();
    if (corePalette != null) {
      _corePalette = corePalette;
      _seedColor = Color(corePalette.primary.get(40));
      _isUsingDynamicColor = true;
      notifyListeners();
    }
  }
}
