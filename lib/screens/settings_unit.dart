import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../utils/preferences_helper.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../helper/locale_helper.dart';

const String _aqiUnitedStates = 'United States';
const String _timeFormat12Hr = '12 hr';
const String _timeFormat24Hr = '24 hr';

class AppUnitsPage extends StatefulWidget {
  const AppUnitsPage({super.key});

  @override
  State<AppUnitsPage> createState() => _AppUnitsPageState();
}

class _AppUnitsPageState extends State<AppUnitsPage> {
  @override
  Widget build(BuildContext context) {
    final currentAqiMode =
        PreferencesHelper.getString("selectedAQIUnit") ?? _aqiUnitedStates;
    final currentTempMode =
        PreferencesHelper.getString("selectedTempUnit") ?? "Celsius";
    final currentWindMode =
        PreferencesHelper.getString("selectedWindUnit") ?? "Km/h";
    final currentVisibilityMode =
        PreferencesHelper.getString("selectedVisibilityUnit") ?? "Km";
    final currentPrecipMode =
        PreferencesHelper.getString("selectedPrecipitationUnit") ?? "mm";
    final currentPressureMode =
        PreferencesHelper.getString("selectedPressureUnit") ?? "hPa";
    final currentTimeFormat =
        PreferencesHelper.getString("selectedTimeUnit") ?? _timeFormat12Hr;

    final String selectedTempKey;
    switch (currentTempMode) {
      case 'Fahrenheit':
        selectedTempKey = 'Fahrenheit';
        break;
      default:
        selectedTempKey = 'Celsius';
        break;
    }

    final String selectedWindKey;
    switch (currentWindMode) {
      case 'Mph':
        selectedWindKey = 'Mph';
        break;
      case 'M/s':
        selectedWindKey = 'M/s';
        break;
      case 'Bft':
        selectedWindKey = 'Bft';
        break;
      case 'Kt':
        selectedWindKey = 'Kt';
        break;
      default:
        selectedWindKey = 'Km/h';
        break;
    }

    final String selectedVisibilityKey;
    switch (currentVisibilityMode) {
      case 'Mile':
        selectedVisibilityKey = 'Mile';
        break;
      default:
        selectedVisibilityKey = 'Km';
        break;
    }

    final String selectedPrecipKey;
    switch (currentPrecipMode) {
      case 'cm':
        selectedPrecipKey = 'cm';
        break;
      case 'in':
        selectedPrecipKey = 'in';
        break;
      default:
        selectedPrecipKey = 'mm';
        break;
    }

    final String selectedPressureKey;
    switch (currentPressureMode) {
      case 'inHg':
        selectedPressureKey = 'inHg';
        break;
      case 'mmHg':
        selectedPressureKey = 'mmHg';
        break;
      default:
        selectedPressureKey = 'hPa';
        break;
    }

    final String selectedTimeFormatKey;
    switch (currentTimeFormat) {
      case _timeFormat24Hr:
        selectedTimeFormatKey = _timeFormat24Hr;
        break;
      default:
        selectedTimeFormatKey = _timeFormat12Hr;
        break;
    }

    final String selectedAqiKey;
    switch (currentAqiMode) {
      case 'European':
        selectedAqiKey = 'European';
        break;
      default:
        selectedAqiKey = _aqiUnitedStates;
        break;
    }

    final optionsAQI = {
      _aqiUnitedStates: "united_states_aqi".tr(),
      "European": "european_aqi".tr()
    };

    final optionsTemp = {
      "Celsius": localizeTempUnit("Celsius", context.locale),
      "Fahrenheit": localizeTempUnit("Fahrenheit", context.locale)
    };

    final optionsWind = {
      "Km/h": localizeWindUnit("Km/h", context.locale),
      "Mph": localizeWindUnit("Mph", context.locale),
      "M/s": localizeWindUnit("M/s", context.locale),
      "Bft": localizeWindUnit("Bft", context.locale),
      "Kt": localizeWindUnit("Kt", context.locale),
    };
    final optionsVisibility = {
      "Km": localizeVisibilityUnit("Km", context.locale),
      "Mile": localizeVisibilityUnit("Mile", context.locale),
    };

    final optionsPrecip = {
      "mm": localizePrecipUnit("mm", context.locale),
      "cm": localizePrecipUnit("cm", context.locale),
      "in": localizePrecipUnit("in", context.locale),
    };

    final optionsPressure = {
      "hPa": localizePressureUnit("hPa", context.locale),
      "inHg": localizePrecipUnit("inHg", context.locale),
      "mmHg": localizePrecipUnit("mmHg", context.locale)
    };

    final optionsTimeFormat = {
      _timeFormat12Hr: localizeTimeFormat(_timeFormat12Hr, context.locale),
      _timeFormat24Hr: localizeTimeFormat(_timeFormat24Hr, context.locale)
    };

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            title: Text('app_units'.tr()),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,
          ),
          SliverToBoxAdapter(
            child: Column(
              children: [
                SettingSection(
                  styleTile: true,
                  tiles: [
                    SettingSingleOptionTile(
                      icon: Icon(Icons.device_thermostat),
                      title: Text('temperature_unit'.tr()),
                      value: SettingTileValue(optionsTemp[selectedTempKey]!),
                      dialogTitle: 'temperature_unit'.tr(),
                      options: optionsTemp.values.toList(),
                      initialOption: optionsTemp[selectedTempKey],
                      onSubmitted: (value) {
                        final selectedKey = optionsTemp.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        context
                            .read<UnitSettingsNotifier>()
                            .updateTempUnit(selectedKey);
                        setState(() {});
                      },
                    ),
                    SettingSingleOptionTile(
                      icon: Icon(Icons.air),
                      title: Text('wind_unit'.tr()),
                      value: SettingTileValue(optionsWind[selectedWindKey]!),
                      dialogTitle: 'wind_unit'.tr(),
                      options: optionsWind.values.toList(),
                      initialOption: optionsWind[selectedWindKey],
                      onSubmitted: (value) {
                        final selectedKey = optionsWind.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        context
                            .read<UnitSettingsNotifier>()
                            .updateWindUnit(selectedKey);
                        setState(() {});
                      },
                    ),
                    SettingSingleOptionTile(
                      icon: Icon(Icons.visibility),
                      title: Text('visibility_unit'.tr()),
                      value: SettingTileValue(
                          optionsVisibility[selectedVisibilityKey]!),
                      dialogTitle: 'visibility_unit'.tr(),
                      options: optionsVisibility.values.toList(),
                      initialOption: optionsVisibility[selectedVisibilityKey],
                      onSubmitted: (value) {
                        final selectedKey = optionsVisibility.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        context
                            .read<UnitSettingsNotifier>()
                            .updateVisibilityUnit(selectedKey);
                        setState(() {});
                      },
                    ),
                    SettingSingleOptionTile(
                      icon: Icon(Icons.water_drop),
                      title: Text('precipitation_unit'.tr()),
                      value: SettingTileValue(optionsPrecip[selectedPrecipKey]!),
                      dialogTitle: 'precipitation_unit'.tr(),
                      options: optionsPrecip.values.toList(),
                      initialOption: optionsPrecip[selectedPrecipKey],
                      onSubmitted: (value) {
                        final selectedKey = optionsPrecip.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        context
                            .read<UnitSettingsNotifier>()
                            .updatePrecipitationUnit(selectedKey);
                        setState(() {});
                      },
                    ),
                    SettingSingleOptionTile(
                      icon: Icon(Icons.av_timer),
                      title: Text('pressure_unit'.tr()),
                      value: SettingTileValue(
                          optionsPressure[selectedPressureKey]!),
                      dialogTitle: 'pressure_unit'.tr(),
                      options: optionsPressure.values.toList(),
                      initialOption: optionsPressure[selectedPressureKey],
                      onSubmitted: (value) {
                        final selectedKey = optionsPressure.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        context
                            .read<UnitSettingsNotifier>()
                            .updatePressureUnit(selectedKey);
                        setState(() {});
                      },
                    ),
                    SettingSingleOptionTile(
                      icon: Icon(Icons.schedule),
                      title: Text('time_format'.tr()),
                      value: SettingTileValue(
                          optionsTimeFormat[selectedTimeFormatKey]!),
                      dialogTitle: 'time_format'.tr(),
                      options: optionsTimeFormat.values.toList(),
                      initialOption: optionsTimeFormat[selectedTimeFormatKey],
                      onSubmitted: (value) {
                        final selectedKey = optionsTimeFormat.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        context
                            .read<UnitSettingsNotifier>()
                            .updateTimeUnit(selectedKey);
                        setState(() {});
                      },
                    ),
                    SettingSingleOptionTile(
                      icon: Icon(Icons.waves),
                      title: Text('aqi_type'.tr()),
                      value: SettingTileValue(optionsAQI[selectedAqiKey]!),
                      dialogTitle: 'aqi_type'.tr(),
                      options: optionsAQI.values.toList(),
                      initialOption: optionsAQI[selectedAqiKey],
                      onSubmitted: (value) {
                        final selectedKey = optionsAQI.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        context
                            .read<UnitSettingsNotifier>()
                            .updateAQIUnit(selectedKey);
                        setState(() {});
                      },
                    ),
                  ],
                ),
                SizedBox(
                  height: MediaQuery.of(context).padding.bottom + 30,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
