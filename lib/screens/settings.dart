import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:weather_master_app/utils/open_links.dart';
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
                      icon: Container(
                        width: 40,
                        height: 40,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(50),
                          color: Color(0xff5865f2),
                        ),
                        child: Center(
                          child: Transform.scale(
                            scale: 0.6,
                            child: SvgPicture.string(
                              """ <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"  viewBox="0 -28.5 256 256" >
    <g>
        <path d="M216.856339,16.5966031 C200.285002,8.84328665 182.566144,3.2084988 164.041564,0 C161.766523,4.11318106 159.108624,9.64549908 157.276099,14.0464379 C137.583995,11.0849896 118.072967,11.0849896 98.7430163,14.0464379 C96.9108417,9.64549908 94.1925838,4.11318106 91.8971895,0 C73.3526068,3.2084988 55.6133949,8.86399117 39.0420583,16.6376612 C5.61752293,67.146514 -3.4433191,116.400813 1.08711069,164.955721 C23.2560196,181.510915 44.7403634,191.567697 65.8621325,198.148576 C71.0772151,190.971126 75.7283628,183.341335 79.7352139,175.300261 C72.104019,172.400575 64.7949724,168.822202 57.8887866,164.667963 C59.7209612,163.310589 61.5131304,161.891452 63.2445898,160.431257 C105.36741,180.133187 151.134928,180.133187 192.754523,160.431257 C194.506336,161.891452 196.298154,163.310589 198.110326,164.667963 C191.183787,168.842556 183.854737,172.420929 176.223542,175.320965 C180.230393,183.341335 184.861538,190.991831 190.096624,198.16893 C211.238746,191.588051 232.743023,181.531619 254.911949,164.955721 C260.227747,108.668201 245.831087,59.8662432 216.856339,16.5966031 Z M85.4738752,135.09489 C72.8290281,135.09489 62.4592217,123.290155 62.4592217,108.914901 C62.4592217,94.5396472 72.607595,82.7145587 85.4738752,82.7145587 C98.3405064,82.7145587 108.709962,94.5189427 108.488529,108.914901 C108.508531,123.290155 98.3405064,135.09489 85.4738752,135.09489 Z M170.525237,135.09489 C157.88039,135.09489 147.510584,123.290155 147.510584,108.914901 C147.510584,94.5396472 157.658606,82.7145587 170.525237,82.7145587 C183.391518,82.7145587 193.761324,94.5189427 193.539891,108.914901 C193.539891,123.290155 183.391518,135.09489 170.525237,135.09489 Z" fill="#ffffff" fill-rule="nonzero">

</path>
    </g>
</svg> """,
                            ),
                          ),
                        ),
                      ),
                      title: Text("join_discord".tr()),
                      onTap: () async {
                        openLink("https://discord.gg/sSW2E4nqmn");
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
