import 'package:flutter/material.dart';
import 'package:animations/animations.dart';
import 'package:path_provider/path_provider.dart';
import 'package:hive/hive.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'screens/home.dart';
import 'screens/locations.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '../utils/theme_controller.dart'; 
import '../utils/geoLocation.dart'; 
import '../services/fetch_data.dart';
import '../models/saved_location.dart';
import '../utils/preferences_helper.dart';
import 'notifiers/unit_settings_notifier.dart';
import 'notifiers/layout_provider.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_color_utilities/material_color_utilities.dart';
import 'package:flutter_svg/flutter_svg.dart';

final CorePalette paletteStartScreen = CorePalette.of(const Color.fromARGB(255, 255, 196, 0).toARGB32());

const easySupportedLocales = [
  Locale('ar', 'SA'),
  Locale('az', 'AZ'),
  Locale('cs', 'CZ'),
  Locale('de', 'DE'),
  Locale('el', 'GR'),
  Locale('en'),
  Locale('es', 'ES'),
  Locale('fa', 'IR'),
  Locale('fi', 'FI'),
  Locale('fil', 'PH'),
  Locale('fr', 'FR'),
  Locale('hu', 'HU'),
  Locale('id', 'ID'),
  Locale('it', 'IT'),
  Locale('ja', 'JP'),
  Locale('ko', 'KR'),
  Locale('nl', 'NL'),
  Locale('pl', 'PL'),
  Locale('pt', 'BR'),
  Locale('pt', 'PT'),
  Locale('ro', 'RO'),
  Locale('ru', 'RU'),
  Locale('sl', 'SI'),
  Locale('sr', 'CS'), 
  Locale('sr', 'SP'), 
  Locale('sv', 'SE'),
  Locale('tr', 'TR'),
  Locale('uk', 'UA'),
  Locale('vi', 'VN'),
  Locale('zh', 'CN'),
  Locale('zh', 'TW'),
];


final flutterSupportedLocales = easySupportedLocales
    .map((l) => Locale(l.languageCode))
    .toSet()
    .toList();

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await EasyLocalization.ensureInitialized();

  await SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  final dir = await getApplicationDocumentsDirectory();
  Hive.init(dir.path);
 await PreferencesHelper.init();

  final themeController = ThemeController();
  await themeController.initialize();
  await themeController.checkDynamicColorSupport();

  PaintingBinding.instance.imageCache.maximumSize = 1000;
  PaintingBinding.instance.imageCache.maximumSizeBytes = 200 << 20;

  final prefs = await SharedPreferences.getInstance();
  final homeLocationJson = prefs.getString('homeLocation');
  final currentLocationJson = prefs.getString('currentLocation');

  String? cityName;
  String? countryName;
  String? cacheKey;
  double? lat;
  double? lon;

  if (homeLocationJson != null) {
    final locationData = jsonDecode(homeLocationJson);
    cityName = locationData['city'];
    countryName = locationData['country'];
    cacheKey = locationData['cacheKey'];
    lat = locationData['lat'];
    lon = locationData['lon'];
  } else if (currentLocationJson != null) {
    final locationData = jsonDecode(currentLocationJson);
    cityName = locationData['city'];
    countryName = locationData['country'];
    cacheKey = locationData['cacheKey'];
    lat = locationData['lat'];
    lon = locationData['lon'];
  }

runApp(
    EasyLocalization(
      supportedLocales: easySupportedLocales,
      path: 'assets/translations',
      fallbackLocale: Locale('en'),
      saveLocale: true,

      child: MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => themeController),
          ChangeNotifierProvider(create: (_) => UnitSettingsNotifier()),
          ChangeNotifierProvider(create: (_) => LayoutProvider()),
        ],
        child: MyApp(
          cacheKey: cacheKey,
          cityName: cityName,
          countryName: countryName,
          hasHomeLocation: homeLocationJson != null,
          lat: lat,
          lon: lon,
        ),
      ),
    ),
  );
}
class MyApp extends StatelessWidget {
  final String? cacheKey;
  final String? cityName;
  final String? countryName;
  final bool hasHomeLocation;
  final double? lat;
  final double? lon;

  const MyApp({
    super.key,
    this.cacheKey,
    this.cityName,
    this.countryName,
    required this.hasHomeLocation,
    this.lat,
    this.lon,
  });

  @override
  Widget build(BuildContext context) {
  final themeController = Provider.of<ThemeController>(context);


  
    return MaterialApp(
      title: 'WeatherMaster',
      debugShowCheckedModeBanner: false,
      locale: context.locale,
      supportedLocales: flutterSupportedLocales, 
      localizationsDelegates: context.localizationDelegates,
      localeResolutionCallback: (locale, supportedLocales) {
        return context.locale; 
      },

  theme: ThemeData.from(
      colorScheme: ColorScheme.fromSeed(
        seedColor: themeController.seedColor,
        brightness: Brightness.light,
      ),
      useMaterial3: true,
    ).copyWith(
      textTheme: ThemeData.light().textTheme.apply(fontFamily: 'OpenSans'),
      highlightColor: Colors.transparent,
      
      pageTransitionsTheme: const PageTransitionsTheme(
        builders: {
          TargetPlatform.android: SharedAxisPageTransitionsBuilder(
            transitionType: SharedAxisTransitionType.horizontal,
          ),
          TargetPlatform.iOS: SharedAxisPageTransitionsBuilder(
            transitionType: SharedAxisTransitionType.horizontal,
          ),
        },
      ),
    ),

    darkTheme: ThemeData.from(
        colorScheme: ColorScheme.fromSeed(
          seedColor: themeController.seedColor,
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ).copyWith(
        textTheme: ThemeData.dark().textTheme.apply(fontFamily: 'OpenSans'),
        highlightColor: Colors.transparent,
        pageTransitionsTheme: const PageTransitionsTheme(
        builders: {
          TargetPlatform.android: SharedAxisPageTransitionsBuilder(
            transitionType: SharedAxisTransitionType.horizontal,
          ),
          TargetPlatform.iOS: SharedAxisPageTransitionsBuilder(
            transitionType: SharedAxisTransitionType.horizontal,
          ),
        },
      ),
      ),

      themeMode: themeController.themeMode,
      home: (hasHomeLocation && cacheKey != null && cityName != null && countryName != null)
            ? WeatherHome(
                cacheKey: cacheKey!,
                cityName: cityName!,
                countryName: countryName!,
                isHomeLocation: true,
                lat: lat!,
                lon: lon!,
              )
            : const LocationPromptScreen(),
      );
    }
  }


class LocationPromptScreen extends StatelessWidget {
  const LocationPromptScreen({super.key});


  @override
  Widget build(BuildContext context) {
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);

    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        systemNavigationBarColor: Colors.transparent,
      ),
    );

  ColorScheme customDarkScheme = ColorScheme(
      brightness: Brightness.dark,
      primary: Color(paletteStartScreen.primary.get(80)),
      onPrimary: Color(paletteStartScreen.primary.get(20)),
      primaryContainer: Color(paletteStartScreen.primary.get(30)),
      onPrimaryContainer: Color(paletteStartScreen.primary.get(90)),
      secondary: Color(paletteStartScreen.secondary.get(80)),
      onSecondary: Color(paletteStartScreen.secondary.get(20)),
      secondaryContainer: Color(paletteStartScreen.secondary.get(30)),
      onSecondaryContainer: Color(paletteStartScreen.secondary.get(90)),
      tertiary: Color(paletteStartScreen.tertiary.get(80)),
      onTertiary: Color(paletteStartScreen.tertiary.get(20)),
      tertiaryContainer: Color(paletteStartScreen.tertiary.get(30)),
      onTertiaryContainer: Color(paletteStartScreen.tertiary.get(90)),
      surface: Color(paletteStartScreen.neutral.get(6)),
      onSurface: Color(paletteStartScreen.neutral.get(90)),
      onSurfaceVariant: Color(paletteStartScreen.neutralVariant.get(80)),
      error: Color(paletteStartScreen.error.get(80)),
      onError: Color(paletteStartScreen.error.get(20)),
      errorContainer: Color(paletteStartScreen.error.get(30)),
      onErrorContainer: Color(paletteStartScreen.error.get(90)),
      inversePrimary: Color(paletteStartScreen.primary.get(40)),
      inverseSurface: Color(paletteStartScreen.neutral.get(90)),
      outline: Color(paletteStartScreen.neutralVariant.get(60)),
      outlineVariant: Color(paletteStartScreen.neutralVariant.get(30)),
      shadow: Color(paletteStartScreen.neutral.get(0)),
      surfaceContainerHigh: Color(paletteStartScreen.neutral.get(17)),
      surfaceContainerLow: Color(paletteStartScreen.neutral.get(10)),
      surfaceContainer: Color(paletteStartScreen.neutral.get(12)),
      surfaceContainerHighest: Color(paletteStartScreen.neutral.get(22)),
      surfaceContainerLowest: Color(paletteStartScreen.neutral.get(4))
);


    Future<void> saveLocation(SavedLocation newLocation) async {
      final prefs = await SharedPreferences.getInstance();
      final existing = prefs.getString('saved_locations');
      List<SavedLocation> current = [];

      if (existing != null) {
        final decoded = jsonDecode(existing) as List;
        current = decoded.map((e) => SavedLocation.fromJson(e)).toList();
      }

      bool alreadyExists = current.any((loc) =>
          loc.city == newLocation.city && loc.country == newLocation.country);

      if (!alreadyExists) {
        current.add(newLocation);
        await prefs.setString(
            'saved_locations',
            jsonEncode(current.map((e) => e.toJson()).toList()));
      }
}


    return
    Scaffold(
      backgroundColor: customDarkScheme.surfaceContainerLow,
      appBar: AppBar(
        toolbarHeight: 130,
        backgroundColor: customDarkScheme.tertiaryContainer,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.only(bottomLeft: Radius.circular(28), bottomRight: Radius.circular(28))
        ),
        title: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            spacing: 0,
            children: [
              Text("Welcome!", style: TextStyle(fontFamily: 'fantasy', color: customDarkScheme.tertiary, fontSize: 44, height: 0),),
              Text("WeatherMaster", style: TextStyle(fontFamily: 'fantasy', color: customDarkScheme.onTertiaryContainer, fontSize: 30, height: 0),),
              

            ]
           ),
      ),
      body: SafeArea(
      child: 
       Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [

            Container(
              padding: EdgeInsets.only(top: 40),
              child: SvgPicture.string('''
          <svg height="300.0dip" width="412.0dip" viewBox="0 0 412.0 300.0"
            xmlns:android="http://schemas.android.com/apk/res/android">
              <g>
                  <clip-path d="M0,0h412v300h-412z" />
                  <path fill="transparent" d="M384.2,300H27.8C12.5,300 0,287.2 0,271.5V28.5C0,12.8 12.5,0 27.8,0H384.3C399.5,0 412,12.8 412,28.5V271.7C412,287.2 399.5,300 384.2,300Z" />
                  <path fill="#12b5cb" d="M299.04,77.05L303.56,95.56L309.3,94.16L304.78,75.64L324.41,80.66L326.16,75.37L306.53,70.35L321.64,56.85L317.65,52.96L302.54,66.46L298.02,47.94L292.27,49.34L296.8,67.86L277.17,62.84L275.42,68.13L295.05,73.15L279.94,86.65L283.93,90.54L299.04,77.05Z" fill-rule="evenodd" />
                  <path fill="#12b5cb" d="M299.04,77.05L299.52,76.93L299.33,76.12L298.7,76.67L299.04,77.05ZM303.56,95.56L303.07,95.68L303.19,96.17L303.68,96.05L303.56,95.56ZM309.3,94.16L309.42,94.65L309.91,94.53L309.79,94.04L309.3,94.16ZM304.78,75.64L304.91,75.16L304.1,74.95L304.3,75.76L304.78,75.64ZM324.41,80.66L324.28,81.15L324.74,81.26L324.88,80.82L324.41,80.66ZM326.16,75.37L326.63,75.53L326.8,75.02L326.28,74.88L326.16,75.37ZM306.53,70.35L306.2,69.98L305.5,70.6L306.41,70.83L306.53,70.35ZM321.64,56.85L321.97,57.22L322.37,56.87L321.99,56.49L321.64,56.85ZM317.65,52.96L317.99,52.6L317.66,52.28L317.31,52.59L317.65,52.96ZM302.54,66.46L302.05,66.58L302.25,67.39L302.87,66.83L302.54,66.46ZM298.02,47.94L298.51,47.82L298.39,47.34L297.9,47.45L298.02,47.94ZM292.27,49.34L292.16,48.86L291.67,48.97L291.79,49.46L292.27,49.34ZM296.8,67.86L296.67,68.34L297.48,68.55L297.28,67.74L296.8,67.86ZM277.17,62.84L277.29,62.36L276.84,62.24L276.69,62.68L277.17,62.84ZM275.42,68.13L274.94,67.98L274.77,68.49L275.29,68.62L275.42,68.13ZM295.05,73.15L295.38,73.53L296.08,72.9L295.17,72.67L295.05,73.15ZM279.94,86.65L279.61,86.28L279.21,86.64L279.59,87.01L279.94,86.65ZM283.93,90.54L283.58,90.9L283.92,91.23L284.27,90.92L283.93,90.54ZM298.55,77.16L303.07,95.68L304.04,95.44L299.52,76.93L298.55,77.16ZM303.68,96.05L309.42,94.65L309.18,93.68L303.44,95.08L303.68,96.05ZM309.79,94.04L305.27,75.53L304.3,75.76L308.82,94.28L309.79,94.04ZM304.66,76.13L324.28,81.15L324.53,80.18L304.91,75.16L304.66,76.13ZM324.88,80.82L326.63,75.53L325.68,75.21L323.93,80.51L324.88,80.82ZM326.28,74.88L306.66,69.86L306.41,70.83L326.04,75.85L326.28,74.88ZM321.31,56.48L306.2,69.98L306.87,70.72L321.97,57.22L321.31,56.48ZM317.3,53.32L321.29,57.21L321.99,56.49L317.99,52.6L317.3,53.32ZM302.87,66.83L317.98,53.33L317.31,52.59L302.21,66.08L302.87,66.83ZM303.02,66.34L298.51,47.82L297.53,48.06L302.05,66.58L303.02,66.34ZM297.9,47.45L292.16,48.86L292.39,49.83L298.14,48.43L297.9,47.45ZM291.79,49.46L296.31,67.98L297.28,67.74L292.76,49.22L291.79,49.46ZM296.92,67.37L277.29,62.36L277.05,63.32L296.67,68.34L296.92,67.37ZM276.69,62.68L274.94,67.98L275.89,68.29L277.64,63L276.69,62.68ZM275.29,68.62L294.92,73.64L295.17,72.67L275.54,67.65L275.29,68.62ZM280.27,87.02L295.38,73.53L294.71,72.78L279.61,86.28L280.27,87.02ZM284.28,90.19L280.29,86.29L279.59,87.01L283.58,90.9L284.28,90.19ZM298.7,76.67L283.6,90.17L284.27,90.92L299.37,77.42L298.7,76.67Z" />
                  <path fill="#669df6" d="M109.32,190.36L134.79,185.56C132.64,194.42 131.81,199.35 130.26,208.22C127.67,221.76 119.6,232.35 106.94,233.59C94.3,234.86 85.14,225.62 86.5,212.96C87.86,200.31 96.65,191.6 109.32,190.36Z" />
                  <path fill="#f9ab00" d="M194.46,92.88C200.06,87.66 208.94,87.66 214.54,92.88C217.75,95.86 222.2,97.26 226.61,96.66C234.33,95.61 241.52,100.64 242.86,108.04C243.63,112.27 246.39,115.92 250.32,117.93C257.2,121.45 259.95,129.6 256.53,136.35C254.57,140.21 254.57,144.72 256.53,148.58C259.95,155.32 257.2,163.47 250.32,166.99C246.39,169.01 243.63,172.66 242.86,176.89C241.52,184.28 234.33,189.32 226.61,188.27C222.2,187.66 217.75,189.06 214.54,192.05C208.94,197.27 200.06,197.27 194.46,192.05C191.25,189.06 186.8,187.66 182.39,188.27C174.67,189.32 167.48,184.28 166.14,176.89C165.37,172.66 162.61,169.01 158.68,166.99C151.79,163.47 149.05,155.32 152.47,148.58C154.43,144.72 154.43,140.21 152.47,136.35C149.05,129.6 151.79,121.45 158.68,117.93C162.61,115.92 165.37,112.27 166.14,108.04C167.48,100.64 174.67,95.61 182.39,96.66C186.8,97.26 191.25,95.86 194.46,92.88Z" />
                  <path fill="#1a73e8" d="M124.82,58.96C113.59,58.96 104.07,65.66 100.33,74.98C97.61,73.34 94.72,72.36 91.32,72.36C83.67,72.36 77.21,77.26 75,83.96L151,83.96C151.17,70.24 139.27,58.96 124.82,58.96Z" />
                  <path fill="#fdd663" d="M303.62,209.92C308.02,201.14 305.76,190.66 298.63,184.28C302.57,184.38 306.79,185.33 310.54,187.21C324.42,194.17 330.07,211.02 323.14,224.84C316.21,238.67 299.34,244.24 285.45,237.28C277.01,233.05 271.75,225.27 270.24,216.57C271.56,217.69 272.96,218.63 274.46,219.38C285.35,224.84 298.29,220.57 303.62,209.92Z" />
                  <path fill="#e8eaed" d="M157.41,228.51L157.21,228.32C155.51,226.51 155.62,223.82 157.41,222.12L163.62,216.21C165.41,214.51 168.12,214.62 169.82,216.41L170.01,216.62C171.71,218.41 171.62,221.12 169.82,222.82L163.62,228.71C161.82,230.32 159.01,230.32 157.41,228.51Z" />
                  <path fill="#e8eaed" d="M114.4,129.9C110.3,129.9 107,126.5 107,122.4C107,118.3 110.4,115 114.5,115C118.6,115 121.9,118.3 121.9,122.4C121.9,126.6 118.6,129.9 114.4,129.9C114.5,129.9 114.4,129.9 114.4,129.9ZM114.4,117.6C111.7,117.6 109.5,119.8 109.5,122.5C109.5,125.2 111.7,127.4 114.4,127.4C117.1,127.4 119.3,125.2 119.3,122.5C119.3,119.8 117.1,117.6 114.4,117.6Z" />
                  <path fill="#e8eaed" d="M174.02,53.97C175.1,55.51 175.85,57.34 175.98,59.35C175.96,60.43 176.41,61.7 177.13,62.55C177.69,63.54 178.68,64.28 179.74,64.62C183.38,66.26 185.67,69.6 186.06,73.57C186.04,74.64 186.49,75.92 187.21,76.76L183.36,79.48C182.27,77.94 181.52,76.11 181.35,74.19C181.37,73.11 180.93,71.84 180.2,71C179.64,70 178.65,69.26 177.6,68.93C173.95,67.28 171.67,63.94 171.27,59.98C171.29,58.9 170.85,57.63 170.12,56.78L174.02,53.97Z" />
                  <path fill="#e8eaed" d="M92.66,161.68C94.47,161.15 96.45,161.04 98.39,161.56C99.41,161.93 100.75,161.92 101.79,161.51C102.91,161.3 103.93,160.6 104.59,159.72C107.32,156.8 111.23,155.72 115.11,156.63C116.12,157 117.46,156.99 118.5,156.57L119.83,161.1C118.02,161.63 116.04,161.75 114.17,161.29C113.16,160.92 111.81,160.93 110.78,161.34C109.65,161.55 108.63,162.25 107.97,163.13C105.24,166.05 101.34,167.13 97.46,166.22C96.45,165.85 95.1,165.86 94.06,166.28L92.66,161.68Z" />
                  <path fill="#e8eaed" d="M288.57,131.38C290.35,130.76 292.31,130.54 294.28,130.96C295.31,131.28 296.65,131.2 297.67,130.74C298.78,130.47 299.77,129.72 300.38,128.8C302.96,125.75 306.8,124.47 310.72,125.18C311.75,125.5 313.09,125.42 314.11,124.95L315.66,129.4C313.88,130.03 311.91,130.24 310.02,129.88C308.99,129.57 307.65,129.64 306.64,130.11C305.53,130.37 304.54,131.12 303.93,132.04C301.35,135.1 297.51,136.37 293.59,135.66C292.56,135.35 291.21,135.43 290.2,135.89L288.57,131.38Z" />
                  <path fill="#e8eaed" d="M274.5,197L272,200.12L281.28,207.57L283.78,204.45L274.5,197Z" />
                  <path fill="#e8eaed" d="M256.79,73.6L253.75,71L246,80.03L249.04,82.64L256.79,73.6Z" />
                  <path fill="#e8eaed" d="M219.63,75L219.36,74.93C217.02,74.35 215.59,71.94 216.18,69.61L218.25,61.38C218.84,59.05 221.24,57.62 223.57,58.2L223.85,58.27C226.18,58.86 227.62,61.26 227.03,63.6L224.95,71.82C224.28,74.21 221.96,75.59 219.63,75Z" />
                  <path fill="#e8eaed" d="M243.4,229.9C239.3,229.9 236,226.5 236,222.4C236,218.3 239.4,215 243.5,215C247.6,215 250.9,218.3 250.9,222.4C250.9,226.7 247.6,229.9 243.4,229.9C243.5,229.9 243.4,229.9 243.4,229.9ZM243.4,217.7C240.7,217.7 238.5,219.9 238.5,222.6C238.5,225.3 240.7,227.5 243.4,227.5C246.1,227.5 248.3,225.3 248.3,222.6C248.3,219.9 246.1,217.7 243.4,217.7Z" />
                  <path fill="#e8eaed" d="M306.31,157.34C307.05,156.89 307.95,156.89 308.69,157.34L310.75,158.59L312.81,159.84C313.55,160.28 314,161.11 314,162L314,164.5L314,167C314,167.89 313.55,168.72 312.81,169.16L310.75,170.41L308.69,171.66C307.95,172.11 307.05,172.11 306.31,171.66L304.25,170.41L302.19,169.16C301.45,168.72 301,167.89 301,167L301.01,164.5L301,162C301,161.11 301.45,160.28 302.19,159.84L304.25,158.59L306.31,157.34Z" />
                  <path fill="#e8eaed" d="M203.31,230.34C204.05,229.89 204.95,229.89 205.69,230.34L207.75,231.59L209.81,232.84C210.55,233.28 211,234.11 211,235L211,237.5L211,240C211,240.89 210.55,241.72 209.81,242.16L207.75,243.41L205.69,244.66C204.95,245.11 204.05,245.11 203.31,244.66L201.25,243.41L199.19,242.16C198.45,241.72 198,240.89 198,240L198.01,237.5L198,235C198,234.11 198.45,233.28 199.19,232.84L201.25,231.59L203.31,230.34Z" />
              </g>
          </svg>
          '''),
      ),

          Align(
            alignment: Alignment.bottomCenter,
            child: Center(
              child: Text("Please search for a location or use your device's location to get the weather for your area.", textAlign: TextAlign.center, style: TextStyle(color: customDarkScheme.onSurface),),
            ),
          )
          ],
        ),
        
        ),
        bottomNavigationBar: 
        
      BottomAppBar(
        height: 160,
        clipBehavior: Clip.hardEdge,
        color: customDarkScheme.surfaceContainerLow,
        child: Column(
          spacing: 10,
          children: [
          FilledButton.icon(
            style: ButtonStyle(
              backgroundColor: WidgetStateProperty.all(customDarkScheme.primary),
              minimumSize: WidgetStateProperty.all(const Size(300, 60)), 
            ),
             onPressed: () async {
            try {

              final position = await getCurrentPosition();

              final geoData = await reverseGeocode(position.latitude, position.longitude);

              final saved = SavedLocation(
                latitude: position.latitude,
                longitude: position.longitude,
                city: geoData['city']!,
                country: geoData['country']!,
              );

              saveLocation(saved);

              final cacheKey =
                  "${saved.city}_${saved.country}".toLowerCase().replaceAll(' ', '_');

              final prefs = await SharedPreferences.getInstance();
              prefs.setString('homeLocation', jsonEncode({
                'city': saved.city,
                'country': saved.country,
                'cacheKey': cacheKey,
                'lat': saved.latitude,
                'lon': saved.longitude,
                'isGPS': true,

              }));

              final weatherService = WeatherService();
              await weatherService.fetchWeather(
                saved.latitude,
                saved.longitude,
                locationName: cacheKey,
              );

              Navigator.of(context).pushReplacement(
                MaterialPageRoute(
                  builder: (_) => WeatherHome(
                    cacheKey: cacheKey,
                    cityName: saved.city,
                    countryName: saved.country,
                    isHomeLocation: true,
                    lat: saved.latitude,
                    lon: saved.longitude,
                  ),
                ),
              );
            } catch (e) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Error: ${e.toString()}')),
              );
            }
          },
            icon: Icon(Icons.my_location, color: customDarkScheme.onPrimary, size: 25,),
            label: Text("Use Current Location", style: TextStyle(color: customDarkScheme.onPrimary, fontSize: 20, fontFamily: 'sans-serif', fontWeight: FontWeight.w400),),
            ),
          OutlinedButton.icon(
            style: ButtonStyle(              
              minimumSize: WidgetStateProperty.all(const Size(300, 60)), 
            side: WidgetStateProperty.all(
              BorderSide(color: customDarkScheme.outline, width: 2), 
            ),
            ),

           onPressed: () async {
                  Navigator.of(context).push(
                      MaterialPageRoute(builder: (_) => const LocationsScreen()),
                    );
              },
            icon: Icon(Icons.search, color: customDarkScheme.primary, size: 25,),
            label: Text("Search location", style: TextStyle(color: customDarkScheme.primary, fontSize: 20, fontFamily: 'sans-serif', fontWeight: FontWeight.w400),),
            ),
          ],
      ),
    ),
    );
  }
}
