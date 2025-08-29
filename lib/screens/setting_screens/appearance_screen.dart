import 'package:flutter/material.dart';
import 'package:weather_master_app/utils/preferences_helper.dart';
import '../home_location.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../settings_unit.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../../notifiers/unit_settings_notifier.dart';
import 'package:provider/provider.dart';
import '../../utils/theme_controller.dart';
import '../languages_page.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:restart_app/restart_app.dart';
import '../../utils/snack_util.dart';
import '../about_page.dart';
import '../meteo_models.dart';
import '../edit_layout_page.dart';
import '../../services/data_backup_service.dart';
import '../../screens/background_updates.dart';

class AppearanceScreen extends StatefulWidget {
  const AppearanceScreen({super.key});

  @override
  State<AppearanceScreen> createState() => _AppearanceScreenState();
}

class _AppearanceScreenState extends State<AppearanceScreen> {
  bool _showTile = PreferencesHelper.getBool("usingCustomSeed") ?? false;
  bool _useCustomTile =
      PreferencesHelper.getBool("DynamicColors") == true ? false : true;
  @override
  Widget build(BuildContext context) {
    final themeController = Provider.of<ThemeController>(context);
    final currentMode = themeController.themeMode;

    final isSupported = themeController.isDynamicColorSupported;

    final optionsTheme = {
      "Auto": "theme_auto".tr(),
      "Dark": "theme_dark".tr(),
      "Light": "theme_light".tr(),
    };
    return Scaffold(
        backgroundColor: Theme.of(context).colorScheme.surface,
        body: CustomScrollView(slivers: [
          SliverAppBar.large(
            title: Text('Appearance'),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,
          ),
          SliverToBoxAdapter(
              child: Column(children: [
            SettingSection(
                styleTile: true,
                title: SettingSectionTitle("app_looks".tr(), noPadding: true),
                tiles: [
                  SettingSingleOptionTile(
                    icon: Icon(Symbols.routine),
                    title: Text('app_theme_dark_light'.tr()),
                    dialogTitle: 'app_theme_dark_light'.tr(),
                    value: SettingTileValue(
                      optionsTheme[currentMode == ThemeMode.light
                          ? "Light"
                          : currentMode == ThemeMode.system
                              ? "Auto"
                              : "Dark"]!,
                    ),
                    options: optionsTheme.values.toList(),
                    initialOption: optionsTheme[currentMode == ThemeMode.light
                        ? "Light"
                        : currentMode == ThemeMode.system
                            ? "Auto"
                            : "Dark"]!,
                    onSubmitted: (value) {
                      setState(() {
                        final selectedKey = optionsTheme.entries
                            .firstWhere((e) => e.value == value)
                            .key;
                        PreferencesHelper.setString("AppTheme", selectedKey);
                        themeController.setThemeMode(
                          selectedKey == "Dark"
                              ? ThemeMode.dark
                              : selectedKey == "Auto"
                                  ? ThemeMode.system
                                  : ThemeMode.light,
                        );
                      });
                    },
                  ),
                  SettingSwitchTile(
                    enabled: _useCustomTile,
                    icon: Icon(Symbols.colorize, fill: 1, weight: 500),
                    title: Text("use_custom_color".tr()),
                    toggled:
                        PreferencesHelper.getBool("usingCustomSeed") ?? false,
                    onChanged: (value) {
                      setState(() {
                        PreferencesHelper.setBool("usingCustomSeed", value);
                        if (value == true) {
                          Provider.of<ThemeController>(
                            context,
                            listen: false,
                          ).setSeedColor(
                            PreferencesHelper.getColor(
                                  "CustomMaterialColor",
                                ) ??
                                Colors.blue,
                          );
                        } else {
                          Provider.of<ThemeController>(
                            context,
                            listen: false,
                          ).setSeedColor(
                            PreferencesHelper.getColor("weatherThemeColor") ??
                                Colors.blue,
                          );
                        }
                      });
                      _showTile = value;
                    },
                  ),
                  SettingColorTile(
                    enabled: _showTile,
                    icon: Icon(Symbols.colors, fill: 1, weight: 500),
                    title: Text('primary_color'.tr()),
                    description: Text('primary_color_sub'.tr()),
                    dialogTitle: 'Color',
                    initialColor:
                        PreferencesHelper.getColor("CustomMaterialColor") ??
                            Colors.blue,
                    colorPickers: [ColorPickerType.primary],
                    onSubmitted: (value) {
                      setState(() {
                        PreferencesHelper.setColor(
                          "CustomMaterialColor",
                          value,
                        );
                        Provider.of<ThemeController>(
                          context,
                          listen: false,
                        ).setSeedColor(value);
                      });
                    },
                  ),
                  SettingSwitchTile(
                    enabled: isSupported
                        ? _showTile
                            ? false
                            : true
                        : false,
                    icon: Icon(Symbols.wallpaper, fill: 1, weight: 500),
                    title: Text("dynamic_colors".tr()),
                    description: Text(
                      "${"dynamic_colors_sub".tr()} ${isSupported ? "" : "(Android 12+)"}",
                    ),
                    toggled:
                        PreferencesHelper.getBool("DynamicColors") ?? false,
                    onChanged: (value) async {
                      final themeController = context.read<ThemeController>();

                      PreferencesHelper.setBool("DynamicColors", value);

                      if (value) {
                        await themeController.loadDynamicColors();
                      } else {
                        Provider.of<ThemeController>(
                          context,
                          listen: false,
                        ).setSeedColor(
                          PreferencesHelper.getColor("weatherThemeColor") ??
                              Colors.blue,
                        );
                      }
                      setState(() {
                        if (value) {
                          _useCustomTile = false;
                        } else {
                          _useCustomTile = true;
                        }
                      });
                    },
                  ),
                  SettingSwitchTile(
                    icon: Icon(Symbols.palette, fill: 1, weight: 500),
                    title: Text("material_scheme_only".tr()),
                    description: Text('material_scheme_only_sub'.tr()),
                    toggled: PreferencesHelper.getBool("OnlyMaterialScheme") ??
                        false,
                    onChanged: (value) {
                      PreferencesHelper.setBool("OnlyMaterialScheme", value);
                      setState(() {
                        SnackUtil.showSnackBar(
                          context: context,
                          message: "Restarting app",
                        );
                        Future.delayed(Duration(seconds: 2), () {
                          Restart.restartApp();
                        });
                      });
                    },
                  ),
                  SettingSwitchTile(
                    icon: Icon(Symbols.cards, fill: 1, weight: 500),
                    title: Text('make_the_card_background_darker'.tr()),
                    toggled: PreferencesHelper.getBool(
                          "useDarkerCardBackground",
                        ) ??
                        false,
                    onChanged: (value) {
                      context
                          .read<UnitSettingsNotifier>()
                          .updateUseDarkerBackground(value);
                      setState(() {});
                    },
                  ),
                ]),
            SizedBox(
              height: 10,
            ),
            SettingSection(
              styleTile: true,
              title: SettingSectionTitle("Animations", noPadding: true),
              tiles: [
                SettingSwitchTile(
                  icon: Icon(Symbols.thermometer_add, fill: 1, weight: 500),
                  title: Text("temperature_animation".tr()),
                  description: Text("temp_animation_sub_pref".tr()),
                  toggled:
                      PreferencesHelper.getBool("useTempAnimation") ?? true,
                  onChanged: (value) {
                    setState(() {
                      PreferencesHelper.setBool("useTempAnimation", value);
                    });
                  },
                ),
                SettingSwitchTile(
                  icon: Icon(
                    Symbols.assistant_navigation,
                    fill: 1,
                    weight: 500,
                  ),
                  title: Text("use_device_compass".tr()),
                  description: Text("use_device_compass_sub".tr()),
                  toggled:
                      PreferencesHelper.getBool("useDeviceCompass") ?? false,
                  onChanged: (value) {
                    context
                        .read<UnitSettingsNotifier>()
                        .updateUseDeviceCompass(value);
                    setState(() {});
                  },
                ),
                SettingSwitchTile(
                  icon: Icon(Symbols.animation, fill: 1, weight: 500),
                  title: Text("background_card_animation".tr()),
                  toggled: PreferencesHelper.getBool(
                        "CardBackgroundAnimations",
                      ) ??
                      true,
                  onChanged: (value) {
                    context
                        .read<UnitSettingsNotifier>()
                        .updateuseCardBackgroundAnimations(value);
                    setState(() {});
                  },
                ),
              ],
            ),
            SizedBox(
              height: 10,
            ),
            SettingSection(
                styleTile: true,
                title: SettingSectionTitle("Layout", noPadding: true),
                tiles: [
                  SettingSwitchTile(
                    icon: Icon(
                      Symbols.responsive_layout,
                      fill: 1,
                      weight: 500,
                    ),
                    title: Text("froggy_layout".tr()),
                    toggled: PreferencesHelper.getBool("showFroggy") ?? true,
                    onChanged: (value) {
                      context.read<UnitSettingsNotifier>().updateShowFroggy(
                            value,
                          );
                      setState(() {});
                    },
                  ),
                  SettingSwitchTile(
                    icon: Icon(Symbols.taunt, fill: 1, weight: 500),
                    title: Text('froggy_insights'.tr()),
                    description: Text('froggy_insights_sub'.tr()),
                    toggled:
                        PreferencesHelper.getBool("useFroggyInsights") ?? false,
                    onChanged: (value) {
                      setState(() {
                        PreferencesHelper.setBool("useFroggyInsights", value);
                      });
                    },
                  ),
                  SettingActionTile(
                    icon: Icon(Symbols.view_agenda, fill: 1, weight: 500),
                    title: Text('edit_layout'.tr()),
                    trailing: Icon(Icons.chevron_right),
                    onTap: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(
                          builder: (_) => EditLayoutPage(),
                          fullscreenDialog: true,
                        ),
                      );
                    },
                  ),
                ]),
            SizedBox(height: 200),
          ]))
        ]));
  }
}
