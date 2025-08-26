import 'package:flutter/material.dart';
import 'package:weather_master_app/utils/preferences_helper.dart';
import 'home_location.dart';
import 'package:settings_tiles/settings_tiles.dart';
import 'settings_unit.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../notifiers/unit_settings_notifier.dart';
import 'package:provider/provider.dart';
import '../utils/theme_controller.dart';
import 'languages_page.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:restart_app/restart_app.dart';
import '../utils/snack_util.dart';
import 'about_page.dart';
import 'meteo_models.dart';
import 'edit_layout_page.dart';
import '../services/data_backup_service.dart';
import '../screens/background_updates.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool _showTile = PreferencesHelper.getBool("usingCustomSeed") ?? false;
  bool _useCustomTile =
      PreferencesHelper.getBool("DynamicColors") == true ? false : true;
  String? city;
  String? country;

  @override
  void initState() {
    super.initState();
    loadLocation();
  }

  void loadLocation() {
    final location = PreferencesHelper.getJson('homeLocation');
    setState(() {
      city = location?['city'];
      country = location?['country'];
    });
  }

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
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            title: Text('settings'.tr()),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,
          ),
          SliverToBoxAdapter(
            child: Column(
              children: [
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
                      toggled:
                          PreferencesHelper.getBool("OnlyMaterialScheme") ??
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
                  ],
                ),
                SizedBox(height: 10),
                SettingSection(
                  styleTile: true,
                  title: SettingSectionTitle('weather'.tr(), noPadding: true),
                  tiles: [
                    SettingActionTile(
                      icon: Icon(Symbols.home_pin, fill: 1, weight: 500),
                      title: Text('home_location'.tr()),
                      description: Text(
                        "${city ?? 'Unknown City'}, ${country ?? 'Unknown Country'}",
                        style: TextStyle(
                          color: Theme.of(context).colorScheme.secondary,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () async {
                        final updated = await Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const HomeLocationScreen(),
                          ),
                        );

                        if (updated == true) {
                          loadLocation();
                        }
                      },
                    ),
                    SettingActionTile(
                      icon: Icon(Symbols.page_info, fill: 1, weight: 500),
                      title: Text('app_units'.tr()),
                      description: Text(
                        'temperature_wind_pressure_visibility_precipitation_time_aqi'
                            .tr(),
                      ),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const AppUnitsPage(),
                          ),
                        );
                      },
                    ),
                    SettingActionTile(
                      icon: Icon(Symbols.update, fill: 1, weight: 500),
                      title: Text('background_updates'.tr()),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => BackgroundUpdatesPage(),
                          ),
                        );
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
                      toggled: PreferencesHelper.getBool("useDeviceCompass") ??
                          false,
                      onChanged: (value) {
                        context
                            .read<UnitSettingsNotifier>()
                            .updateUseDeviceCompass(value);
                        setState(() {});
                      },
                    ),
                    SettingSwitchTile(
                      icon: Icon(Symbols.taunt, fill: 1, weight: 500),
                      title: Text('froggy_insights'.tr()),
                      description: Text('froggy_insights_sub'.tr()),
                      toggled: PreferencesHelper.getBool("useFroggyInsights") ??
                          false,
                      onChanged: (value) {
                        setState(() {
                          PreferencesHelper.setBool("useFroggyInsights", value);
                        });
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
                    SettingActionTile(
                      icon: Icon(
                        Symbols.nest_farsight_weather,
                        fill: 1,
                        weight: 500,
                      ),
                      title: Text('weather_models'.tr()),
                      description: Text('openmeteo_weather_models'.tr()),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const MeteoModelsPage(),
                          ),
                        );
                      },
                    ),
                  ],
                ),
                SizedBox(height: 10),
                SettingSection(
                  styleTile: true,
                  title: SettingSectionTitle(
                    'additional'.tr(),
                    noPadding: true,
                  ),
                  tiles: [
                    SettingActionTile(
                      icon: Icon(Symbols.language, fill: 1, weight: 500),
                      title: Text('app_language'.tr()),
                      description: Text(
                        getLanguageNamesSettingsView(
                          context.locale,
                        )['english']
                            .toString(),
                      ),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const LanguagesScreen(),
                          ),
                        );
                      },
                    ),
                    SettingActionTile(
                      icon: Icon(Symbols.upload, fill: 1, weight: 500),
                      title: Text("export_data".tr()),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () async {
                        await DataBackupService.exportData();
                      },
                    ),
                    SettingActionTile(
                      icon: Icon(Symbols.download, fill: 1, weight: 500),
                      title: Text("import_data".tr()),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () async {
                        await DataBackupService.importAndReplaceAllData(
                          context,
                        );
                      },
                    ),
                    SettingActionTile(
                      icon: Icon(Symbols.info, fill: 1, weight: 500),
                      title: Text('${"about".tr()} WeatherMaster'),
                      description: Text('learn_about_app'.tr()),
                      trailing: Icon(Icons.chevron_right),
                      onTap: () {
                        Navigator.of(
                          context,
                        ).push(MaterialPageRoute(builder: (_) => AboutPage()));
                      },
                    ),
                  ],
                ),
                SizedBox(height: 200),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

Map<String, String> getLanguageNamesSettingsView(Locale locale) {
  final lang = locale.languageCode;
  final country = locale.countryCode;

  if (lang == 'ar' && country == 'SA') {
    return {'native': 'العربية (السعودية)', 'english': 'Arabic (Saudi Arabia)'};
  }
  if (lang == 'az' && country == 'AZ') {
    return {'native': 'Azərbaycanca', 'english': 'Azerbaijani'};
  }
  if (lang == 'cs' && country == 'CZ') {
    return {'native': 'Čeština', 'english': 'Czech'};
  }
  if (lang == 'de' && country == 'DE') {
    return {'native': 'Deutsch', 'english': 'German'};
  }
  if (lang == 'el' && country == 'GR') {
    return {'native': 'Ελληνικά', 'english': 'Greek'};
  }
  if (lang == 'en') {
    return {'native': 'English', 'english': 'English (US)'};
  }
  if (lang == 'es' && country == 'ES') {
    return {'native': 'Español', 'english': 'Spanish'};
  }
  if (lang == 'fa' && country == 'IR') {
    return {'native': 'فارسی', 'english': 'Persian'};
  }
  if (lang == 'fi' && country == 'FI') {
    return {'native': 'Suomi', 'english': 'Finnish'};
  }
  if (lang == 'fil' && country == 'PH') {
    return {'native': 'Filipino', 'english': 'Filipino'};
  }
  if (lang == 'fr' && country == 'FR') {
    return {'native': 'Français', 'english': 'French'};
  }
  if (lang == 'hu' && country == 'HU') {
    return {'native': 'Magyar', 'english': 'Hungarian'};
  }
  if (lang == 'id' && country == 'ID') {
    return {'native': 'Bahasa Indonesia', 'english': 'Indonesian'};
  }
  if (lang == 'it' && country == 'IT') {
    return {'native': 'Italiano', 'english': 'Italian'};
  }
  if (lang == 'ja' && country == 'JP') {
    return {'native': '日本語', 'english': 'Japanese'};
  }
  if (lang == 'ko' && country == 'KR') {
    return {'native': '한국어', 'english': 'Korean'};
  }
  if (lang == 'nl' && country == 'NL') {
    return {'native': 'Nederlands', 'english': 'Dutch'};
  }
  if (lang == 'pl' && country == 'PL') {
    return {'native': 'Polski', 'english': 'Polish'};
  }
  if (lang == 'pt' && country == 'BR') {
    return {'native': 'Português (Brasil)', 'english': 'Portuguese (Brazil)'};
  }
  if (lang == 'pt' && country == 'PT') {
    return {
      'native': 'Português (Portugal)',
      'english': 'Portuguese (Portugal)',
    };
  }
  if (lang == 'ro' && country == 'RO') {
    return {'native': 'Română', 'english': 'Romanian'};
  }
  if (lang == 'ru' && country == 'RU') {
    return {'native': 'Русский', 'english': 'Russian'};
  }
  if (lang == 'sl' && country == 'SI') {
    return {'native': 'Slovenščina', 'english': 'Slovenian'};
  }
  if (lang == 'sr' && country == 'CS') {
    return {'native': 'Српски (Ћирилица)', 'english': 'Serbian (Cyrillic)'};
  }
  if (lang == 'sr' && country == 'SP') {
    return {'native': 'Српски', 'english': 'Serbian'};
  }
  if (lang == 'sv' && country == 'SE') {
    return {'native': 'Svenska', 'english': 'Swedish'};
  }
  if (lang == 'tr' && country == 'TR') {
    return {'native': 'Türkçe', 'english': 'Turkish'};
  }
  if (lang == 'uk' && country == 'UA') {
    return {'native': 'Українська', 'english': 'Ukrainian'};
  }
  if (lang == 'vi' && country == 'VN') {
    return {'native': 'Tiếng Việt', 'english': 'Vietnamese'};
  }
  if (lang == 'zh' && country == 'CN') {
    return {'native': '简体中文', 'english': 'Simplified Chinese'};
  }
  if (lang == 'zh' && country == 'TW') {
    return {'native': '繁體中文', 'english': 'Traditional Chinese'};
  }
  if (lang == 'ca' && country == 'ES') {
    return {'native': 'Català', 'english': 'Catalan'};
  }

  return {
    'native': '$lang${country != null ? "_$country" : ""}',
    'english': 'Unknown',
  };
}
