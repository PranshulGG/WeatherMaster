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
  bool _useExpressiveVariant = false;
  bool _forceltrLayout = true;

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
  bool get useExpressiveVariant => _useExpressiveVariant;
  bool get forceltrLayout => _forceltrLayout;

  UnitSettingsNotifier() {
    _loadAllUnits();
  }

  void _loadAllUnits() {
    _tempUnit = PreferencesHelper.getString("selectedTempUnit") ?? _tempUnit;
    _windUnit = PreferencesHelper.getString("selectedWindUnit") ?? _windUnit;
    _visibilityUnit =
        PreferencesHelper.getString("selectedVisibilityUnit") ??
            _visibilityUnit;
    _precipitationUnit =
        PreferencesHelper.getString("selectedPrecipitationUnit") ??
            _precipitationUnit;
    _pressureUnit =
        PreferencesHelper.getString("selectedPressureUnit") ?? _pressureUnit;
    _timeUnit = PreferencesHelper.getString("selectedTimeUnit") ?? _timeUnit;
    _aqiUnit = PreferencesHelper.getString("selectedAQIUnit") ?? _aqiUnit;
    _useDeviceCompass =
        PreferencesHelper.getBool("useDeviceCompass") ?? _useDeviceCompass;
    _useCardBackgroundAnimations =
        PreferencesHelper.getBool("CardBackgroundAnimations") ??
            _useCardBackgroundAnimations;
    _useOnlyMaterialScheme =
        PreferencesHelper.getBool("OnlyMaterialScheme") ??
            _useOnlyMaterialScheme;
    _showFrog = PreferencesHelper.getBool("showFroggy") ?? _showFrog;
    _useDarkBackgroundCards =
        PreferencesHelper.getBool("useDarkerCardBackground") ??
            _useDarkBackgroundCards;
    _useExpressiveVariant =
        PreferencesHelper.getBool("useExpressiveVariant") ??
            _useExpressiveVariant;
    _forceltrLayout =
        PreferencesHelper.getBool("ForceltrLayout") ?? _forceltrLayout;
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

  void updateColorVariant(bool value) {
    _useExpressiveVariant = value;
    PreferencesHelper.setBool("useExpressiveVariant", value);
    notifyListeners();
  }

  void updateForceLTRlayout(bool value) {
    _forceltrLayout = value;
    PreferencesHelper.setBool("ForceltrLayout", value);
    notifyListeners();
  }
}
