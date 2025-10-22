import 'package:flutter/material.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:weather_master_app/utils/preferences_helper.dart';
import '../utils/open_links.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../../notifiers/unit_settings_notifier.dart';
import 'package:provider/provider.dart';

class LanguagesScreen extends StatefulWidget {
  const LanguagesScreen({super.key});

  @override
  State<LanguagesScreen> createState() => _LanguagesScreenState();
}

class _LanguagesScreenState extends State<LanguagesScreen> {
  Locale? _selectedLocale;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _selectedLocale ??= context.locale;
  }

  @override
  Widget build(BuildContext context) {
    final locales =
        List<Locale>.from(EasyLocalization.of(context)!.supportedLocales)
          ..sort((a, b) {
            final nameA = getLanguageNames(a)['english']!;
            final nameB = getLanguageNames(b)['english']!;
            return nameA.compareTo(nameB);
          });

    final forceLTRLAYOUT = PreferencesHelper.getBool("ForceltrLayout") ?? true;

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            title: Text('app_language'.tr()),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,
            actions: [
              IconButton(
                  icon: Icon(Icons.search),
                  onPressed: () async {
                    final selectedLocale = await showSearch<Locale?>(
                      context: context,
                      delegate: LanguageSearchDelegate(locales: locales),
                    );

                    if (selectedLocale != null) {
                      await context.setLocale(selectedLocale);
                      setState(() {
                        _selectedLocale = selectedLocale;
                      });
                    }
                  }),
              SizedBox(
                width: 5,
              )
            ],
          ),
          SliverToBoxAdapter(
              child: Column(
            children: [
              Padding(
                padding: EdgeInsets.only(left: 12, right: 12, bottom: 10),
                child: ListTile(
                  contentPadding: EdgeInsets.only(left: 26, right: 24),
                  horizontalTitleGap: 14,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(50),
                  ),
                  tileColor: Theme.of(context).colorScheme.tertiaryContainer,
                  leading: Icon(Symbols.translate),
                  title: Text('translate_this_app'.tr()),
                  subtitle: Text('on_crowdin'.tr()),
                  trailing: Icon(
                    Symbols.open_in_new,
                    weight: 500,
                  ),
                  onTap: () {
                    openLink(
                        "https://crowdin.com/project/weathermaster/invite?h=448278a9b1370f3c10d4336a091dae792286917");
                  },
                ),
              ),
              SettingSection(PrimarySwitch: true, styleTile: true, tiles: [
                SettingSwitchTile(
                    title: Text("force_ltr_layout".tr()),
                    toggled: forceLTRLAYOUT,
                    onChanged: (value) {
                      context
                          .read<UnitSettingsNotifier>()
                          .updateForceLTRlayout(value);
                      setState(() {});
                    })
              ]),
              SizedBox(
                height: 16,
              )
            ],
          )),
          FutureBuilder<Map<String, int>>(
              future: TranslationProgressService(
                projectId: '741419',
                apiToken: dotenv.env['API_TOKEN']!.toString(),
              ).fetchTranslationProgress(),
              builder: (context, snapshot) {
                final progressMap = snapshot.data ?? {};

                return SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      final locale = locales[index];
                      final languageName = getLanguageNames(locale);
                      final languageCode = getLanguageCodeCrodwin(locale);
                      final progress =
                          (languageCode == 'en' || languageCode == 'en-US')
                              ? 100
                              : (progressMap[languageCode] ?? 0);
                      final isLast = index == locales.length - 1;
                      final isFirst = index == 0;

                      return Container(
                        padding: EdgeInsets.only(left: 12, right: 12),
                        margin: EdgeInsets.only(
                          bottom: isLast
                              ? MediaQuery.of(context).padding.bottom + 20
                              : 2,
                        ),
                        child: LanguageTile(
                          locale: locale,
                          selectedLocale: _selectedLocale,
                          languageName: languageName,
                          progress: progress,
                          isFirst: isFirst,
                          isLast: isLast,
                          onTap: () async {
                            await context.setLocale(locale);
                            setState(() {
                              _selectedLocale = locale;
                            });
                            PreferencesHelper.setString(
                                "currentLocale", locale.toString());
                          },
                        ),
                      );
                    },
                    childCount: locales.length,
                  ),
                );
              }),
        ],
      ),
    );
  }
}

class TranslationProgressService {
  final String projectId;
  final String apiToken;

  TranslationProgressService({required this.projectId, required this.apiToken});

  Future<Map<String, int>> fetchTranslationProgress() async {
    final prefs = await SharedPreferences.getInstance();
    final cachedData = prefs.getString('translationProgress');
    final now = DateTime.now();

    if (cachedData != null) {
      final parsed = jsonDecode(cachedData);
      final timestamp =
          DateTime.fromMillisecondsSinceEpoch(parsed['timestamp']);

      if (now.difference(timestamp).inHours < 24) {
        return Map<String, int>.from(parsed['progressData']);
      }
    }

    final response = await http.get(
      Uri.parse(
          'https://api.crowdin.com/api/v2/projects/$projectId/languages/progress?limit=32&offset=0'),
      headers: {
        'Authorization': 'Bearer $apiToken',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final Map<String, int> progressData = {};

      for (final item in data['data']) {
        final lang = item['data']['languageId'];
        final progress = item['data']['translationProgress'];
        progressData[lang] = progress;
      }

      await prefs.setString(
          'translationProgress',
          jsonEncode({
            'timestamp': now.millisecondsSinceEpoch,
            'progressData': progressData,
          }));

      return progressData;
    } else {
      print('Failed to fetch data: ${response.statusCode}');
      return {};
    }
  }
}

class LanguageTile extends StatefulWidget {
  final Locale locale;
  final Locale? selectedLocale;
  final Map<String, String> languageName;
  final int progress;
  final bool isFirst;
  final bool isLast;
  final VoidCallback onTap;

  const LanguageTile({
    super.key,
    required this.locale,
    required this.selectedLocale,
    required this.languageName,
    required this.progress,
    required this.isFirst,
    required this.isLast,
    required this.onTap,
  });

  @override
  State<LanguageTile> createState() => _LanguageTileState();
}

class _LanguageTileState extends State<LanguageTile> {
  late bool _isSelected;

  @override
  void didUpdateWidget(covariant LanguageTile oldWidget) {
    super.didUpdateWidget(oldWidget);
    _isSelected = widget.locale == widget.selectedLocale;
  }

  @override
  Widget build(BuildContext context) {
    _isSelected = widget.locale == widget.selectedLocale;

    final radiusEnd = _isSelected
        ? BorderRadius.circular(50)
        : widget.isFirst
            ? BorderRadius.only(
                bottomLeft: Radius.circular(2.7),
                bottomRight: Radius.circular(2.7),
                topLeft: Radius.circular(18),
                topRight: Radius.circular(18))
            : widget.isLast
                ? BorderRadius.only(
                    bottomLeft: Radius.circular(18),
                    bottomRight: Radius.circular(18),
                    topLeft: Radius.circular(2.7),
                    topRight: Radius.circular(2.7))
                : BorderRadius.circular(2.7);

    return TweenAnimationBuilder<BorderRadius>(
      tween: Tween<BorderRadius>(
        begin: BorderRadius.circular(0),
        end: radiusEnd,
      ),
      duration: const Duration(milliseconds: 400),
      curve: Curves.easeInCubic,
      builder: (context, radius, child) {
        return ClipRRect(
          borderRadius: radius,
          child: child,
        );
      },
      child: Material(
        color: _isSelected
            ? Theme.of(context).colorScheme.primaryContainer
            : Theme.of(context).colorScheme.surfaceContainerLowest,
        child: ListTile(
            minTileHeight: 66,
            splashColor: Colors.transparent,
            contentPadding: EdgeInsetsDirectional.only(start: 8, end: 14),
            leading: Stack(alignment: Alignment.center, children: [
              CircleAvatar(
                  backgroundColor: _isSelected
                      ? Theme.of(context).colorScheme.primary
                      : null,
                  foregroundColor: _isSelected
                      ? Theme.of(context).colorScheme.onPrimary
                      : null,
                  child: _isSelected
                      ? Icon(
                          Symbols.check,
                          size: 30,
                        )
                      : Text(getLanguageCode(widget.locale))),
              SizedBox(
                  width: 56,
                  height: 60,
                  child: CircularProgressIndicator(
                    value: widget.progress / 100,
                    year2023: false,
                    backgroundColor: Colors.red,
                  ))
            ]),
            title: Text(
              widget.languageName['native']!,
              style: TextStyle(
                  fontFamily: widget.languageName['native'] == "English"
                      ? 'FlexFontEn'
                      : 'DefaultFont',
                  color: _isSelected
                      ? Theme.of(context).colorScheme.onPrimaryContainer
                      : Theme.of(context).colorScheme.onSurface),
            ),
            subtitle: Text(widget.languageName['english']!),
            trailing: Container(
              padding: EdgeInsetsDirectional.only(
                  start: 6, end: 6, top: 2, bottom: 2),
              decoration: BoxDecoration(
                  color: _isSelected
                      ? Theme.of(context).colorScheme.primary
                      : Theme.of(context).colorScheme.primaryContainer,
                  borderRadius: BorderRadius.circular(50)),
              child: Text(
                "${widget.progress}%",
                style: TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w700,
                    color: _isSelected
                        ? Theme.of(context).colorScheme.onPrimary
                        : null),
              ),
            ),
            onTap: widget.onTap),
      ),
    );
  }
}

class LanguageSearchDelegate extends SearchDelegate<Locale?> {
  final List<Locale> locales;

  LanguageSearchDelegate({required this.locales});

  @override
  List<Widget> buildActions(BuildContext context) {
    return [
      IconButton(
        icon: Icon(Symbols.close, weight: 500),
        onPressed: () {
          query = '';
        },
      ),
      SizedBox(
        width: 5,
      )
    ];
  }

  @override
  Widget buildLeading(BuildContext context) {
    return IconButton(
      icon: Icon(Symbols.arrow_back, weight: 500),
      onPressed: () {
        close(context, null);
      },
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    return SizedBox.shrink();
  }

  @override
  ThemeData appBarTheme(BuildContext context) {
    final baseTheme = Theme.of(context);
    return baseTheme.copyWith(
      appBarTheme:
          AppBarTheme(scrolledUnderElevation: 0, titleSpacing: 0, elevation: 1),
      inputDecorationTheme: InputDecorationTheme(
        border: OutlineInputBorder(
          borderSide: BorderSide.none,
        ),
        hintStyle: TextStyle(
            fontSize: 16,
            color: Theme.of(context).colorScheme.onSurfaceVariant),
      ),
    );
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    final queryLower = query.toLowerCase();
    final suggestions = query.isEmpty
        ? locales
        : locales.where((locale) {
            final languageNames = getLanguageNames(locale);
            return languageNames['native']!
                    .toLowerCase()
                    .contains(queryLower) ||
                languageNames['english']!.toLowerCase().contains(queryLower);
          }).toList();

    return ListView.builder(
      itemCount: suggestions.length,
      itemBuilder: (context, index) {
        final locale = suggestions[index];
        final languageNames = getLanguageNames(locale);
        return ListTile(
          title: Text(languageNames['native']!),
          subtitle: Text(languageNames['english']!),
          onTap: () {
            close(context, locale);
          },
        );
      },
    );
  }
}

Map<String, String> getLanguageNames(Locale locale) {
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
  if (lang == 'ca' && country == 'ES') {
    return {'native': 'Català', 'english': 'Catalan'};
  }
  if (lang == 'bg' && country == 'BG') {
    return {'native': 'Български', 'english': 'Bulgarian'};
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
      'english': 'Portuguese (Portugal)'
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
  if (lang == 'sr' && country == 'SP') {
    return {'native': 'Српски (Ћирилица)', 'english': 'Serbian (Cyrillic)'};
  }
  if (lang == 'sr' && country == 'CS') {
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

  return {
    'native': '$lang${country != null ? "_$country" : ""}',
    'english': 'Unknown'
  };
}

String getLanguageCode(Locale locale) {
  return locale.languageCode.toUpperCase();
}

String getLanguageCodeCrodwin(Locale locale) {
  final lang = locale.languageCode.toLowerCase();
  final country = locale.countryCode?.toUpperCase();

  if (lang == 'sr' && country == 'CS') return 'sr-CS';
  if (lang == 'sr') return 'sr';
  if (lang == 'pt' && country == 'BR') return 'pt-BR';
  if (lang == 'pt' && country == 'PT') return 'pt-PT';
  if (lang == 'es' && country == 'ES') return 'es-ES';
  if (lang == 'zh' && country == 'CN') return 'zh-CN';
  if (lang == 'zh' && country == 'TW') return 'zh-TW';
  if (lang == 'ar' && country == 'SA') return 'ar-SA';
  if (lang == 'sv' && country == 'SE') return 'sv-SE';

  if ([
    'az', // Azerbaijani
    'cs', // Czech
    'de', // German
    'el', // Greek
    'fa', // Persian
    'fi', // Finnish
    'fil', // Filipino
    'fr', // French
    'hu', // Hungarian
    'id', // Indonesian
    'it', // Italian
    'ja', // Japanese
    'ko', // Korean
    'nl', // Dutch
    'pl', // Polish
    'ro', // Romanian
    'ru', // Russian
    'sl', // Slovenian
    'sr', // Serbian
    'tr', // Turkish
    'uk', // Ukrainian
    'vi', // Vietnamese
    'ca',
    'bg'
  ].contains(lang)) {
    return lang;
  }

  return (country != null && country.isNotEmpty) ? '$lang-$country' : lang;
}
