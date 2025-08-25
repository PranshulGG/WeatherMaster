import 'package:flutter/material.dart';
import '../utils/preferences_helper.dart';

class UnitSettingsNotifier extends ChangeNotifier {
  // Default values
  String _tempUnit = "Celsius";
  String _windUnit = "Km/h";
  String _visibilityUnit = "Km";
  String _precipitationUnit = "mm";
  String _pressureUnit = "hPa";
  String _timeUnit = "12 hr";
  String _aqiUnit = "United States";
  bool _useDeviceCompass = false;
  bool _useCardBackgroundAnimations = true;
  bool _useOnlyMaterialScheme = false;
  bool _showFrog = true;
  bool _useDarkBackgroundCards = false;
  String _appFont = "Open sans";

  // Getters
  String get tempUnit => _tempUnit;
  String get windUnit => _windUnit;
  String get visibilityUnit => _visibilityUnit;
  String get precipitationUnit => _precipitationUnit;
  String get pressureUnit => _pressureUnit;
  String get timeUnit => _timeUnit;
  String get aqiUnit => _aqiUnit;
  bool get useDeviceCompass => _useDeviceCompass;
  bool get useCardBackgroundAnimations => _useCardBackgroundAnimations;
  bool get useOnlyMaterialScheme => _useOnlyMaterialScheme;
  bool get showFrog => _showFrog;
  bool get useDarkBackgroundCards => _useDarkBackgroundCards;
  String get appFont => _appFont;

  UnitSettingsNotifier() {
    _loadAllUnits();
  }

  Future<void> _loadAllUnits() async {
    _tempUnit =
        await PreferencesHelper.getString("selectedTempUnit") ?? _tempUnit;
    _windUnit =
        await PreferencesHelper.getString("selectedWindUnit") ?? _windUnit;
    _visibilityUnit =
        await PreferencesHelper.getString("selectedVisibilityUnit") ??
            _visibilityUnit;
    _precipitationUnit =
        await PreferencesHelper.getString("selectedPrecipitationUnit") ??
            _precipitationUnit;
    _pressureUnit = await PreferencesHelper.getString("selectedPressureUnit") ??
        _pressureUnit;
    _timeUnit =
        await PreferencesHelper.getString("selectedTimeUnit") ?? _timeUnit;
    _aqiUnit = await PreferencesHelper.getString("selectedAQIUnit") ?? _aqiUnit;
    _useDeviceCompass = await PreferencesHelper.getBool("useDeviceCompass") ??
        _useDeviceCompass;
    _useCardBackgroundAnimations =
        await PreferencesHelper.getBool("CardBackgroundAnimations") ??
            _useCardBackgroundAnimations;
    _useOnlyMaterialScheme =
        await PreferencesHelper.getBool("OnlyMaterialScheme") ??
            _useOnlyMaterialScheme;
    _showFrog = await PreferencesHelper.getBool("showFroggy") ?? _showFrog;
    _useDarkBackgroundCards =
        await PreferencesHelper.getBool("useDarkerCardBackground") ??
            _useDarkBackgroundCards;
    _appFont = await PreferencesHelper.getString("appFont") ?? _appFont;

    notifyListeners();
  }

  // Setters with notification
  void updateTempUnit(String value) {
    if (_tempUnit != value) {
      _tempUnit = value;
      PreferencesHelper.setString("selectedTempUnit", value);
      notifyListeners();
    }
  }

  void updateWindUnit(String value) {
    if (_windUnit != value) {
      _windUnit = value;
      PreferencesHelper.setString("selectedWindUnit", value);
      notifyListeners();
    }
  }

  void updateVisibilityUnit(String value) {
    if (_visibilityUnit != value) {
      _visibilityUnit = value;
      PreferencesHelper.setString("selectedVisibilityUnit", value);
      notifyListeners();
    }
  }

  void updatePrecipitationUnit(String value) {
    if (_precipitationUnit != value) {
      _precipitationUnit = value;
      PreferencesHelper.setString("selectedPrecipitationUnit", value);
      notifyListeners();
    }
  }

  void updatePressureUnit(String value) {
    if (_pressureUnit != value) {
      _pressureUnit = value;
      PreferencesHelper.setString("selectedPressureUnit", value);
      notifyListeners();
    }
  }

  void updateTimeUnit(String value) {
    if (_timeUnit != value) {
      _timeUnit = value;
      PreferencesHelper.setString("selectedTimeUnit", value);
      notifyListeners();
    }
  }

  void updateAQIUnit(String value) {
    if (_aqiUnit != value) {
      _aqiUnit = value;
      PreferencesHelper.setString("selectedAQIUnit", value);
      notifyListeners();
    }
  }

  void updateUseDeviceCompass(bool value) {
    _useDeviceCompass = value;
    PreferencesHelper.setBool("useDeviceCompass", value);
    notifyListeners();
  }

  void updateuseCardBackgroundAnimations(bool value) {
    _useCardBackgroundAnimations = value;
    PreferencesHelper.setBool("CardBackgroundAnimations", value);
    notifyListeners();
  }

  void updateuseOnlyMaterialScheme(bool value) {
    _useOnlyMaterialScheme = value;
    PreferencesHelper.setBool("OnlyMaterialScheme", value);
    notifyListeners();
  }

  void updateShowFroggy(bool value) {
    _showFrog = value;
    PreferencesHelper.setBool("showFroggy", value);
    notifyListeners();
  }

  void updateUseDarkerBackground(bool value) {
    _useDarkBackgroundCards = value;
    PreferencesHelper.setBool("useDarkerCardBackground", value);
    notifyListeners();
  }

  void updateAppFont(String value) {
    _appFont = value;
    PreferencesHelper.setString("appFont", value);
    notifyListeners();
  }
}
