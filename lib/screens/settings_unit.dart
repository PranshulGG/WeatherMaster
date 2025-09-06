import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../utils/preferences_helper.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../helper/locale_helper.dart';

class AppUnitsPage extends StatefulWidget {
  const AppUnitsPage({super.key});

  @override
  State<AppUnitsPage> createState() => _AppUnitsPageState();
}

class _AppUnitsPageState extends State<AppUnitsPage> {
  @override
  Widget build(BuildContext context) {
    final currentAqiMode =
        PreferencesHelper.getString("selectedAQIUnit") ?? "United States";
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
        PreferencesHelper.getString("selectedTimeUnit") ?? "12 hr";

    final optionsAQI = {
      "United States": "united_states_aqi".tr(),
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
      "12 hr": localizeTimeFormat("12 hr", context.locale),
      "24 hr": localizeTimeFormat("24 hr", context.locale)
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
                      value: SettingTileValue(optionsTemp[
                          currentTempMode == "Fahrenheit"
                              ? "Fahrenheit"
                              : "Celsius"]!),
                      dialogTitle: 'temperature_unit'.tr(),
                      options: optionsTemp.values.toList(),
                      initialOption: optionsTemp[currentTempMode == "Fahrenheit"
                          ? "Fahrenheit"
                          : "Celsius"],
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
                      value:
                          SettingTileValue(optionsWind[currentWindMode == "Mph"
                              ? "Mph"
                              : currentWindMode == "M/s"
                                  ? "M/s"
                                  : currentWindMode == "Bft"
                                      ? "Bft"
                                      : currentWindMode == "Kt"
                                          ? "Kt"
                                          : "Km/h"]!),
                      dialogTitle: 'wind_unit'.tr(),
                      options: optionsWind.values.toList(),
                      initialOption: optionsWind[currentWindMode == "Mph"
                          ? "Mph"
                          : currentWindMode == "M/s"
                              ? "M/s"
                              : currentWindMode == "Bft"
                                  ? "Bft"
                                  : currentWindMode == "Kt"
                                      ? "Kt"
                                      : "Km/h"],
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
                      value: SettingTileValue(optionsVisibility[
                          currentVisibilityMode == "Mile" ? "Mile" : "Km"]!),
                      dialogTitle: 'visibility_unit'.tr(),
                      options: optionsVisibility.values.toList(),
                      initialOption: optionsVisibility[
                          currentVisibilityMode == "Mile" ? "Mile" : "Km"],
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
                      value: SettingTileValue(
                          optionsPrecip[currentPrecipMode == "cm"
                              ? "cm"
                              : currentPrecipMode == "in"
                                  ? "in"
                                  : "mm"]!),
                      dialogTitle: 'precipitation_unit'.tr(),
                      options: optionsPrecip.values.toList(),
                      initialOption: optionsPrecip[currentPrecipMode == "cm"
                          ? "cm"
                          : currentPrecipMode == "in"
                              ? "in"
                              : "mm"],
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
                          optionsPressure[currentPressureMode == "inHg"
                              ? "inHg"
                              : currentPressureMode == "mmHg"
                                  ? "mmHg"
                                  : "hPa"]!),
                      dialogTitle: 'pressure_unit'.tr(),
                      options: optionsPressure.values.toList(),
                      initialOption:
                          optionsPressure[currentPressureMode == "inHg"
                              ? "inHg"
                              : currentPressureMode == "mmHg"
                                  ? "mmHg"
                                  : "hPa"],
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
                      value: SettingTileValue(optionsTimeFormat[
                          currentTimeFormat == "24 hr" ? "24 hr" : "12 hr"]!),
                      dialogTitle: 'time_format'.tr(),
                      options: optionsTimeFormat.values.toList(),
                      initialOption: optionsTimeFormat[
                          currentTimeFormat == "24 hr" ? "24 hr" : "12 hr"],
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
                      value: SettingTileValue(optionsAQI[
                          currentAqiMode == "European"
                              ? "European"
                              : "United States"]!),
                      dialogTitle: 'aqi_type'.tr(),
                      options: optionsAQI.values.toList(),
                      initialOption: optionsAQI[currentAqiMode == "European"
                          ? "European"
                          : "United States"],
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
