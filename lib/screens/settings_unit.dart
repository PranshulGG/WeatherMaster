import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../utils/preferences_helper.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';

class AppUnitsPage extends StatefulWidget {
  const AppUnitsPage({super.key});

  @override
  State<AppUnitsPage> createState() => _AppUnitsPageState();
}

class _AppUnitsPageState extends State<AppUnitsPage> {


  @override
  Widget build(BuildContext context) {
     
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
                    value: SettingTileValue(PreferencesHelper.getString("selectedTempUnit") ?? "Celsius"),
                    dialogTitle: 'temperature_unit'.tr(),
                    options: const ['Celsius', 'Fahrenheit'],
                    initialOption: PreferencesHelper.getString("selectedTempUnit") ?? "Celsius",
                    onSubmitted: (value) {
                      context.read<UnitSettingsNotifier>().updateTempUnit(value);
                      setState(() {
                      });
                    },
                    
                  ),
                SettingSingleOptionTile(
                    icon: Icon(Icons.air),
                    title: Text('wind_unit'.tr()),
                    value: SettingTileValue(PreferencesHelper.getString("selectedWindUnit") ?? "Km/h"),
                    dialogTitle: 'wind_unit'.tr(),
                    options: const ['Km/h', 'Mph', 'M/s', 'Bft'],
                    initialOption: PreferencesHelper.getString("selectedWindUnit") ?? "Km/h",
                    onSubmitted: (value) {
                      context.read<UnitSettingsNotifier>().updateWindUnit(value);
                      setState(() {
                      });
                    },
                  ),
                SettingSingleOptionTile(
                    icon: Icon(Icons.visibility),
                    title: Text('visibility_unit'.tr()),
                    value: SettingTileValue(PreferencesHelper.getString("selectedVisibilityUnit") ?? "Km"),
                    dialogTitle: 'visibility_unit'.tr(),
                    options: const ['Km', 'Mile'],
                    initialOption: PreferencesHelper.getString("selectedVisibilityUnit") ?? "Km",
                    onSubmitted: (value) {
                      context.read<UnitSettingsNotifier>().updateVisibilityUnit(value);
                      setState(() {
                      });
                    },
                  ),
                  SettingSingleOptionTile(
                    icon: Icon(Icons.water_drop),
                    title: Text('precipitation_unit'.tr()),
                    value: SettingTileValue(PreferencesHelper.getString("selectedPrecipitationUnit") ?? "mm"),
                    dialogTitle: 'precipitation_unit'.tr(),
                    options: const ['mm', 'cm', 'in'],
                    initialOption: PreferencesHelper.getString("selectedPrecipitationUnit") ?? "mm",
                    onSubmitted: (value) {
                      context.read<UnitSettingsNotifier>().updatePrecipitationUnit(value);
                      setState(() {
                      });
                    },
                  ),
                  SettingSingleOptionTile(
                    icon: Icon(Icons.av_timer),
                    title: Text('pressure_unit'.tr()),
                    value: SettingTileValue(PreferencesHelper.getString("selectedPressureUnit") ?? "hPa"),
                    dialogTitle: 'pressure_unit'.tr(),
                    options: const ['hPa', 'inHg', 'mmHg'],
                    initialOption: PreferencesHelper.getString("selectedPressureUnit") ?? "hPa",
                    onSubmitted: (value) {
                      context.read<UnitSettingsNotifier>().updatePressureUnit(value);
                      setState(() {
                      });
                    },
                  ),
                  SettingSingleOptionTile(
                    icon: Icon(Icons.schedule),
                    title: Text('time_format'.tr()),
                    value: SettingTileValue(PreferencesHelper.getString("selectedTimeUnit") ?? "12 hr"),
                    dialogTitle: 'time_format'.tr(),
                    options: const ['12 hr', '24 hr'],
                    initialOption: PreferencesHelper.getString("selectedTimeUnit") ?? "12 hr",
                    onSubmitted: (value) {
                      context.read<UnitSettingsNotifier>().updateTimeUnit(value);
                      setState(() {
                      });
                    },
                  ),
                  SettingSingleOptionTile(
                    icon: Icon(Icons.waves),
                    title: Text('aqi_type'.tr()),
                    value: SettingTileValue(PreferencesHelper.getString("selectedAQIUnit") ?? "United States"),
                    dialogTitle: 'aqi_type'.tr(),
                    options: const ['United States', 'European'],
                    initialOption: PreferencesHelper.getString("selectedAQIUnit") ?? "United States",
                    onSubmitted: (value) {
                      context.read<UnitSettingsNotifier>().updateAQIUnit(value);
                      setState(() {
                      });
                    },
                  ),
                ],
             ),
            ],
            ),
          ),
        ],
      ),
    );
  }
}