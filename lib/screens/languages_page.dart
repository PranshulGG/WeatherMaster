import 'package:flutter/material.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/open_links.dart'; 
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

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
    final locales = EasyLocalization.of(context)!.supportedLocales;
    

    return Scaffold(
     backgroundColor: Theme.of(context).colorScheme.surface,
      body: 
       CustomScrollView(
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
              }
            ),
            SizedBox(width: 5,)
          ],
          ),
           SliverToBoxAdapter(
      child: Padding(
        padding: EdgeInsets.only(left: 10, right: 10, bottom: 20),
          child: ListTile(
          contentPadding: EdgeInsets.only(left: 26, right: 24),
          
          horizontalTitleGap: 14,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(50), 
          ),
          tileColor: Theme.of(context).colorScheme.tertiaryContainer, 
          leading: Icon(Symbols.translate),
          title: Text('translate_this_app'.tr()),
          subtitle: Text('On Crowdin'),
          trailing: Icon(Symbols.open_in_new, weight: 500,),
          onTap: () {
            openLink("https://crowdin.com/project/weathermaster/invite?h=448278a9b1370f3c10d4336a091dae792286917");
          },
        ),
      ),
    ),
          FutureBuilder<Map<String, int>>(
        future: TranslationProgressService(
          projectId: '741419',
          apiToken: '22439bfa72878c3ecb9a1b17ea8d2f9379f800b616856c4647219af7da462632fb325abd32815ace',
        ).fetchTranslationProgress(),
        builder: (context, snapshot) {
          final progressMap = snapshot.data ?? {};

      return  SliverList(
            delegate: SliverChildBuilderDelegate(
              (context, index) {

                final locale = locales[index];
                final languageName = getLanguageNames(locale);
                final languageCode = getLanguageCodeCrodwin(locale);
                final progress = (languageCode == 'en' || languageCode == 'en-US') ? 100 : (progressMap[languageCode] ?? 0);
                final isLast = index == locales.length - 1;
                final isFirst = index == 0;


              return 
              
               Container(
              padding: EdgeInsets.only(left: 10, right: 10),
              margin: EdgeInsets.only(bottom: isLast ? MediaQuery.of(context).padding.bottom + 20 : 5,),
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
                },
              ),
            );
                 
              },
              childCount: locales.length,
              
            ),
          );
          }
          ),
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
      final timestamp = DateTime.fromMillisecondsSinceEpoch(parsed['timestamp']);

      if (now.difference(timestamp).inHours < 24) {
        print("Using cached data. ${parsed}" );
        return Map<String, int>.from(parsed['progressData']);
      }
    }

    final response = await http.get(
      Uri.parse('https://api.crowdin.com/api/v2/projects/$projectId/languages/progress?limit=30&offset=0'),
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

      await prefs.setString('translationProgress', jsonEncode({
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

    final radiusEnd = _isSelected ? BorderRadius.circular(50) : widget.isFirst ? BorderRadius.only(bottomLeft: Radius.circular(10), bottomRight: Radius.circular(10), topLeft: Radius.circular(20), topRight: Radius.circular(20)) : widget.isLast ? BorderRadius.only(bottomLeft: Radius.circular(20), bottomRight: Radius.circular(20), topLeft: Radius.circular(10), topRight: Radius.circular(10)) : BorderRadius.circular(10);

    return TweenAnimationBuilder<BorderRadius>(
      tween: Tween<BorderRadius>(
        begin: BorderRadius.circular(10),
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
        child:Material(   
            color: _isSelected ? Theme.of(context).colorScheme.primaryContainer : Theme.of(context).colorScheme.surfaceContainerLow,
          child:  ListTile( 
            minTileHeight: 66,
            splashColor: Colors.transparent,
            contentPadding: EdgeInsets.only(left: 8, right: 14),
            leading: Stack(
              alignment: Alignment.center,
              children: [
              CircleAvatar(backgroundColor:  _isSelected ? Theme.of(context).colorScheme.primary : null, foregroundColor: _isSelected ? Theme.of(context).colorScheme.onPrimary : null, child: _isSelected ? Icon(Symbols.check, size: 30,) : Text(getLanguageCode(widget.locale))),
              SizedBox(
              width: 56,
              height: 60,
              child: CircularProgressIndicator(value: widget.progress / 100, year2023: false, backgroundColor: Colors.red,))
            ]
            ),
            title: Text(widget.languageName['native']!, style: TextStyle(color: _isSelected ? Theme.of(context).colorScheme.onPrimaryContainer : Theme.of(context).colorScheme.onSurface),),
            subtitle: Text(widget.languageName['english']!),
            trailing: Container(
              padding: EdgeInsets.only(left: 6, right: 6, top: 2, bottom: 2),
              decoration: BoxDecoration(
                color: _isSelected ? Theme.of(context).colorScheme.primary : Theme.of(context).colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(50)
              ),
              child: Text("${widget.progress}%", style: TextStyle(fontSize: 13, fontWeight: FontWeight.w700, color: _isSelected ? Theme.of(context).colorScheme.onPrimary : null),),
            ),
            onTap: widget.onTap
          ),
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
      SizedBox(width: 5,)
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
    appBarTheme: AppBarTheme(
      scrolledUnderElevation: 0,
      titleSpacing: 0,
      elevation: 1      
    ),
    inputDecorationTheme: InputDecorationTheme(
       border: OutlineInputBorder(
        borderSide: BorderSide.none,
      ),
      hintStyle: TextStyle(fontSize: 16, color: Theme.of(context).colorScheme.onSurfaceVariant),
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
          return languageNames['native']!.toLowerCase().contains(queryLower) ||
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
          close(context, locale); // ✅ return the selected Locale
        },
      );
    },
  );
}

}


Map<String, String> getLanguageNames(Locale locale) {
  final lang = locale.languageCode;
  final country = locale.countryCode;

  if (lang == 'pt' && country == 'BR') return {'native': 'Português (Brasil)', 'english': 'Portuguese (Brazil)'};
  if (lang == 'pt') return {'native': 'Português', 'english': 'Portuguese'};
  if (lang == 'en') return {'native': 'English', 'english': 'English'};
  if (lang == 'zh' && country == 'TW') return {'native': '繁體中文', 'english': 'Traditional Chinese'};
  if (lang == 'zh') return {'native': '中文', 'english': 'Chinese'};
  if (lang == 'nl') return {'native': 'Nederlands', 'english': 'Dutch'};
  if (lang == 'fil') return {'native': 'Filipino', 'english': 'Filipino'};
  if (lang == 'fr') return {'native': 'Français', 'english': 'French'};
  if (lang == 'de') return {'native': 'Deutsch', 'english': 'German'};
  if (lang == 'el') return {'native': 'Ελληνικά', 'english': 'Greek'};
  if (lang == 'it') return {'native': 'Italiano', 'english': 'Italian'};
  if (lang == 'ja') return {'native': '日本語', 'english': 'Japanese'};
  if (lang == 'fa') return {'native': 'فارسی', 'english': 'Persian'};
  if (lang == 'pl') return {'native': 'Polski', 'english': 'Polish'};
  if (lang == 'ro') return {'native': 'Română', 'english': 'Romanian'};
  if (lang == 'ru') return {'native': 'Русский', 'english': 'Russian'};
  if (lang == 'es') return {'native': 'Español', 'english': 'Spanish'};
  if (lang == 'tr') return {'native': 'Türkçe', 'english': 'Turkish'};
  if (lang == 'uk') return {'native': 'Українська', 'english': 'Ukrainian'};
  if (lang == 'vi') return {'native': 'Tiếng Việt', 'english': 'Vietnamese'};
  if (lang == 'sr' && country == 'CY') return {'native': 'Српски (Ћирилица)', 'english': 'Serbian (Cyrillic)'};
  if (lang == 'sr') return {'native': 'Српски', 'english': 'Serbian'};
  if (lang == 'az') return {'native': 'Azərbaycanca', 'english': 'Azerbaijani'};
  if (lang == 'sl') return {'native': 'Slovenščina', 'english': 'Slovenian'};
  if (lang == 'fi') return {'native': 'Suomi', 'english': 'Finnish'};
  if (lang == 'hu') return {'native': 'Magyar', 'english': 'Hungarian'};
  if (lang == 'cs') return {'native': 'Čeština', 'english': 'Czech'};
  if (lang == 'ar' && country == 'SA') return {'native': 'العربية (السعودية)', 'english': 'Arabic, Saudi Arabia'};
  if (lang == 'ko') return {'native': '한국어', 'english': 'Korean'};
  if (lang == 'id') return {'native': 'Bahasa Indonesia', 'english': 'Indonesian'};

  return {'native': '$lang${country != null ? "_$country" : ""}', 'english': 'Unknown'};
}




String getLanguageCode(Locale locale) {
  return locale.languageCode.toUpperCase();
}

String getLanguageCodeCrodwin(Locale locale) {
  final lang = locale.languageCode.toLowerCase();
  final country = locale.countryCode?.toUpperCase();

  // Normalize edge cases manually
  if (lang == 'sr' && country == 'CY') return 'sr-CS'; // or 'sr' depending on Crowdin config
  if (lang == 'pt') return 'pt-PT';
  if (lang == 'pt' && country == 'BR') return 'pt-BR';
  if (lang == 'es') return 'es-ES';
  if (lang == 'zh') return 'zh-CN';
  if (lang == 'zh' && country == 'TW') return 'zh-TW';
  if (lang == 'ar' && country == 'SA') return 'ar-SA';
  if (lang == 'sv' && country == 'SE') return 'sv-SE';

  // Default behavior
  return (country != null && country.isNotEmpty) ? '$lang-$country' : lang;
}

