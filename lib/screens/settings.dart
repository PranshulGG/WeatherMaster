import 'package:flutter/material.dart';
import 'package:weather_master_app/utils/preferences_helper.dart';
import 'home_location.dart';
import 'package:settings_tiles/settings_tiles.dart';
import 'settings_unit.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'languages_page.dart';
import 'package:easy_localization/easy_localization.dart';
import 'about_page.dart';
import 'meteo_models.dart';
import '../services/data_backup_service.dart';
import '../screens/background_updates.dart';
import 'setting_screens/appearance_screen.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
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
    final isLight = Theme.of(context).brightness == Brightness.light;

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
                  tiles: [
                    SettingActionTile(
                        icon: iconContainer(
                            Symbols.format_paint,
                            isLight ? Color(0xfff8e287) : Color(0xff534600),
                            isLight ? Color(0xff534600) : Color(0xfff8e287)),
                        title: Text("appearance".tr()),
                        description: Text("appearance_sub".tr()),
                        onTap: () async {
                          Navigator.of(context).push(
                            MaterialPageRoute(
                              builder: (_) => const AppearanceScreen(),
                            ),
                          );
                        })
                  ],
                ),
                SizedBox(height: 16),
                SettingSection(
                  styleTile: true,
                  tiles: [
                    SettingActionTile(
                      // icon: Icon(Symbols.home_pin, fill: 1, weight: 500),
                      icon: iconContainer(
                          Symbols.home_pin,
                          isLight ? Color(0xffcdeda3) : Color(0xff354e16),
                          isLight ? Color(0xff354e16) : Color(0xffcdeda3)),
                      title: Text('home_location'.tr()),
                      description: Text(
                        "${city ?? 'Unknown City'}, ${country ?? 'Unknown Country'}",
                        style: TextStyle(
                          color: Theme.of(context).colorScheme.secondary,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
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
                      icon: iconContainer(
                          Symbols.page_info,
                          isLight ? Color(0xffd6e3ff) : Color(0xff284777),
                          isLight ? Color(0xff284777) : Color(0xffd6e3ff)),
                      title: Text('app_units'.tr()),
                      description: Text(
                        'temperature_wind_pressure_visibility_precipitation_time_aqi'
                            .tr(),
                      ),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const AppUnitsPage(),
                          ),
                        );
                      },
                    ),
                    SettingActionTile(
                      icon: iconContainer(
                          Symbols.update,
                          isLight ? Color(0xffffdbd1) : Color(0xff723523),
                          isLight ? Color(0xff723523) : Color(0xffffdbd1)),
                      title: Text('background_updates'.tr()),
                      description: Text("background_updates_sub".tr()),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => BackgroundUpdatesPage(),
                          ),
                        );
                      },
                    ),
                    SettingActionTile(
                      // icon: Icon(
                      //   Symbols.nest_farsight_weather,
                      //   fill: 1,
                      //   weight: 500,
                      // ),
                      icon: iconContainer(
                          Symbols.nest_farsight_weather,
                          isLight ? Color(0xff9df0f8) : Color(0xff004f54),
                          isLight ? Color(0xff004f54) : Color(0xff9df0f8)),
                      title: Text('weather_models'.tr()),
                      description: Text('openmeteo_weather_models'.tr()),
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
                SizedBox(height: 16),
                SettingSection(
                  styleTile: true,
                  tiles: [
                    SettingActionTile(
                      // icon: Icon(Symbols.language, fill: 1, weight: 500),
                      icon: iconContainer(
                          Symbols.language,
                          isLight ? Color(0xffffd6f9) : Color(0xff633664),
                          isLight ? Color(0xff633664) : Color(0xffffd6f9)),
                      title: Text('app_language'.tr()),
                      description: Text(
                        getLanguageNamesSettingsView(
                          context.locale,
                        )['native']
                            .toString(),
                      ),
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const LanguagesScreen(),
                          ),
                        );
                      },
                    ),
                    SettingActionTile(
                      // icon: Icon(Symbols.upload, fill: 1, weight: 500),
                      icon: iconContainer(
                          Symbols.upload,
                          isLight ? Color(0xffffdcc5) : Color(0xff6d390b),
                          isLight ? Color(0xff6d390b) : Color(0xffffdcc5)),
                      title: Text("export_data".tr()),
                      onTap: () async {
                        await DataBackupService.exportData();
                      },
                    ),
                    SettingActionTile(
                      // icon: Icon(Symbols.download, fill: 1, weight: 500),
                      icon: iconContainer(
                          Symbols.download,
                          isLight ? Color(0xffffdcc5) : Color(0xff6d390b),
                          isLight ? Color(0xff6d390b) : Color(0xffffdcc5)),
                      title: Text("import_data".tr()),
                      onTap: () async {
                        await DataBackupService.importAndReplaceAllData(
                          context,
                        );
                      },
                    ),
                    SettingActionTile(
                      // icon: Icon(Symbols.info, fill: 1, weight: 500),
                      icon: iconContainer(
                          Symbols.info,
                          isLight ? Color(0xffe6deff) : Color(0xff493e76),
                          isLight ? Color(0xff493e76) : Color(0xffe6deff)),
                      title: Text('${"about".tr()} WeatherMaster'),
                      description: Text('about_weathermaster_sub'.tr()),
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
    return {'native': 'English (US)', 'english': 'English (US)'};
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
  if (lang == 'bg' && country == 'BG') {
    return {'native': 'Български', 'english': 'Bulgarian'};
  }
  return {
    'native': '$lang${country != null ? "_$country" : ""}',
    'english': 'Unknown',
  };
}

Widget iconContainer(IconData icon, Color color, Color onColor) {
  return Container(
    width: 40,
    height: 40,
    decoration: BoxDecoration(
      borderRadius: BorderRadius.circular(50),
      color: color,
    ),
    child: Icon(
      icon,
      fill: 1,
      weight: 500,
      color: onColor,
    ),
  );
}
