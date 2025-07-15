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
import 'package:easy_localization/easy_localization.dart';

const easySupportedLocales = [
  Locale('en'),
  Locale('zh'),
  Locale('nl'),
  Locale('fil'),
  Locale('fr'),
  Locale('de'),
  Locale('el'),
  Locale('it'),
  Locale('ja'),
  Locale('fa'),
  Locale('pl'),
  Locale('pt'),
  Locale('ro'),
  Locale('ru'),
  Locale('es'),
  Locale('tr'),
  Locale('uk'),
  Locale('vi'),
  Locale('sr'),
  Locale('az'),
  Locale('sl'),
  Locale('fi'),
  Locale('hu'),
  Locale('cs'),
  Locale('ar', 'SA'),
  Locale('zh', 'TW'),
  Locale('sr', 'CY'),
  Locale('ko'),
  Locale('pt', 'BR'),
  Locale('id'),
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
        final easyLocale = context.locale;
        return Locale(easyLocale.languageCode);
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

      // This controls which theme is used
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


    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
  ElevatedButton.icon(
  icon: const Icon(Icons.my_location),
  label: const Text("Use Current Location"),
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
),
SizedBox(height: 20,),
  ElevatedButton.icon(
  icon: const Icon(Icons.search),
  label: const Text("Search location"),
  onPressed: () async {
      Navigator.of(context).push(
          MaterialPageRoute(builder: (_) => const LocationsScreen()),
        );
  }
  )

          ],
        ),
      ),
    );
  }
}
