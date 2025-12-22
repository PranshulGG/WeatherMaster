// home.dart is a bitch for sure, tried to clean it up but gave up

// TODO: fix unwanted builds

// Dart core libraries
import 'dart:async';
import 'dart:convert';
import 'dart:math';

// Flutter packages
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

// Third-party packages
import 'package:custom_refresh_indicator/custom_refresh_indicator.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_color_utilities/material_color_utilities.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:animations/animations.dart';
import 'package:expressive_loading_indicator/expressive_loading_indicator.dart';

// App utilities
import '../utils/animation_map.dart';
import '../utils/froggy_map.dart';
import '../utils/geo_location.dart';
import '../utils/preferences_helper.dart';
import '../utils/app_storage.dart';
import '../utils/snack_util.dart';
import '../utils/theme.dart';
import '../utils/theme_controller.dart';
import '../utils/bottom_provider.dart';
import '../utils/is_online.dart';
import '../utils/visual_utils.dart';

// App models
import '../models/insights_gen.dart';
import '../models/layout_config.dart';
import '../models/saved_location.dart';

// App screens
import '../screens/locations.dart';
import '../screens/settings.dart';

// App controllers
import '../controllers/froggy_img.dart';
import '../controllers/gradient.dart';
import '../controllers/gradient_list.dart';
import '../controllers/home_f.dart';
import '../controllers/view_location.dart';

// App notifiers
import '../notifiers/layout_provider.dart';
import '../notifiers/unit_settings_notifier.dart';

// App services
import '../services/fetch_data.dart';

// App widgets
import '../widgets/current_conditions_card.dart';
import '../widgets/daily_card.dart';
import '../widgets/hourly_card.dart';
import '../widgets/pollen_card.dart';
import '../widgets/rain_block.dart';
import '../widgets/top_summary_block.dart';
import '../widgets/top_weather_card.dart';

// home widget
import '../widget_background.dart';

class WeatherHome extends StatefulWidget {
  final String cacheKey;
  final String cityName;
  final String countryName;
  final bool isHomeLocation;
  final double? lat;
  final double? lon;

  const WeatherHome({
    super.key,
    required this.cacheKey,
    required this.cityName,
    required this.countryName,
    required this.isHomeLocation,
    required this.lat,
    required this.lon,
  });

  @override
  State<WeatherHome> createState() => _WeatherHomeState();
}

class _WeatherHomeState extends State<WeatherHome> {
  // late Future<Map<String, dynamic>?>? weatherFuture;
  Future<Map<String, dynamic>?>? weatherFuture;

  Widget? weatherAnimationWidget;
  int? _cachedWeatherCode;
  int? _cachedIsDay;
  bool isViewLocation = false;
  final ValueNotifier<bool> _showHeaderNotifier = ValueNotifier(false);
  late bool isHomeLocation;
  final ScrollController _scrollController = ScrollController();

  bool _isAppFullyLoaded = false;
  bool themeCalled = false;
  late String cityName;
  late String countryName;
  late String cacheKey;
  int selectedGradientIndex = 2;
  int selectedSearchBgIndex = 2;
  int selectedContainerBgIndex = 2;
  bool _istriggeredFromLocations = false;
  bool showInsightsRandomly = false;
  int? lastWeatherCode;
  bool widgetsUpdated = false;
  bool? iscurrentDay = false;
  bool? _cachedIsShowFrog;
  bool? lastIsDay;

  bool? _cachedGradientIsLight;
  bool? _cachedGradientIsShowFrog;
  bool? _cachedGradientIsCurrentDay;
  List<LinearGradient>? _cachedGradients;
  List<LinearGradient>? _cachedGradientsScrolled;

  bool? _cachedCardColorsIsLight;
  bool? _cachedCardColorsIsShowFrog;
  bool? _cachedCardColorsIsCurrentDay;
  bool? _cachedCardColorsUseDarkerBackground;
  List<Color>? _cachedSearchBgColors;
  List<int>? _cachedWeatherContainerColors;

  String? _derivedWeatherDataKey;
  _WeatherDerivedData? _derivedWeatherData;

  String? _insightsCacheKey;
  List<Map<String, dynamic>>? _insightsHourlyData;
  List<Map<String, dynamic>>? _insightsDailyData;

  final WeatherFroggyManager _weatherManager = WeatherFroggyManager();

  String? _iconUrlFroggy;
  bool _isLoadingFroggy = true;
  int? _lastFroggyWeatherCode;
  bool? _lastFroggyIsDay;
  int? _lastFroggyIndex;
  bool _froggyLoadInFlight = false;

  bool isFirstAppBuild = true;
  bool onLoadForceCall = false;

  double? lat;
  double? lon;

  double? homeLat;
  double? homeLon;

  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      final layoutProvider = Provider.of<LayoutProvider>(context, listen: false);
      layoutProvider.loadLayout();

      if (PreferencesHelper.getBool("showNewVerNotification") ?? true) {
        checkForUpdatesOnStart(context);
      }
    });

    cityName = widget.cityName;
    countryName = widget.countryName;
    cacheKey = widget.cacheKey;

    // _loadLatLon();
    //  _initAppLoad();
    isHomeLocation = widget.isHomeLocation;
    if (isHomeLocation) {
      lat = widget.lat;
      lon = widget.lon;
      homeLat = widget.lat;
      homeLon = widget.lon;

      setHomeasCurrent();

      weatherFuture = getWeatherFromCache();
    } else {
      _setLatLon();
    }
  }

  @override
  void dispose() {
    _weatherManager.dispose();
    _scrollController.dispose();
    _showHeaderNotifier.dispose();
    super.dispose();
  }

  Future<void> setHomeasCurrent() async {
    final prefs = await SharedPreferences.getInstance();
    final locationData = {
      'city': PreferencesHelper.getJson(PrefKeys.homeLocation)?['city'],
      'country': PreferencesHelper.getJson(PrefKeys.homeLocation)?['country'],
      'cacheKey': PreferencesHelper.getJson(PrefKeys.homeLocation)?['cacheKey'],
      'latitude': PreferencesHelper.getJson(PrefKeys.homeLocation)?['lat'],
      'longitude': PreferencesHelper.getJson(PrefKeys.homeLocation)?['lon'],
    };
    await prefs.setString(PrefKeys.currentLocation, jsonEncode(locationData));
  }

  _WeatherDerivedData _getOrComputeDerivedWeatherData({
    required dynamic weather,
    required String? lastUpdated,
  }) {
    final now = DateTime.now();
    final dayKey =
        '${now.year.toString().padLeft(4, '0')}-${now.month.toString().padLeft(2, '0')}-${now.day.toString().padLeft(2, '0')}';

    final int offsetSeconds =
        int.parse(weather['utc_offset_seconds'].toString());
    final utcNow = DateTime.now().toUtc();
    var nowPrecip = utcNow.add(Duration(seconds: offsetSeconds));
    nowPrecip = DateTime(
      nowPrecip.year,
      nowPrecip.month,
      nowPrecip.day,
      nowPrecip.hour,
      nowPrecip.minute,
      nowPrecip.second,
      nowPrecip.millisecond,
      nowPrecip.microsecond,
    );
    final nowPrecipKey =
        '${nowPrecip.year.toString().padLeft(4, '0')}-${nowPrecip.month.toString().padLeft(2, '0')}-${nowPrecip.day.toString().padLeft(2, '0')}-${nowPrecip.hour.toString().padLeft(2, '0')}-${nowPrecip.minute.toString().padLeft(2, '0')}';

    final key = '${lastUpdated ?? ''}|$offsetSeconds|$dayKey|$nowPrecipKey';

    final cached = _derivedWeatherData;
    if (cached != null && _derivedWeatherDataKey == key) {
      return cached;
    }

    final hourly = weather['hourly'] ?? {};

    final List<dynamic> hourlyTimeNoFilter = hourly['time'];
    final List<dynamic> hourlyTempsNoFilter = hourly['temperature_2m'];
    final List<dynamic> hourlyWeatherCodesNoFilter = hourly['weather_code'];
    final List<dynamic> hourlyPrecpProbNoFilter =
        hourly['precipitation_probability'];

    final todayMidnight = DateTime(now.year, now.month, now.day);
    final filteredIndices = <int>[];
    for (int i = 0; i < hourlyTimeNoFilter.length; i++) {
      final time = DateTime.parse(hourlyTimeNoFilter[i]);
      if (time.isAfter(todayMidnight) || time.isAtSameMomentAs(todayMidnight)) {
        filteredIndices.add(i);
      }
    }

    final hourlyTime =
        filteredIndices.map((i) => hourlyTimeNoFilter[i]).toList(growable: false);
    final List? visibilityList = hourly['visibility'] as List?;
    final hourlyVisibility = filteredIndices.map((i) {
      final dynamic rawVal =
          (visibilityList != null && i < visibilityList.length)
              ? visibilityList[i]
              : null;
      return (rawVal as num?)?.toDouble() ?? 0.0;
    }).toList(growable: false);
    final hourlyTemps =
        filteredIndices.map((i) => hourlyTempsNoFilter[i]).toList(growable: false);
    final hourlyWeatherCodes = filteredIndices
        .map((i) => hourlyWeatherCodesNoFilter[i])
        .toList(growable: false);
    final hourlyPrecpProb = filteredIndices
        .map((i) => hourlyPrecpProbNoFilter[i])
        .toList(growable: false);

    final daily = weather['daily'];
    final List<dynamic> dailyDates = daily['time'];
    final List<dynamic> sunriseTimes = daily['sunrise'];
    final List<dynamic> sunsetTimes = daily['sunset'];

    final Map<String, (DateTime, DateTime)> daylightMap = {
      for (int i = 0; i < dailyDates.length; i++)
        dailyDates[i]: (
          DateTime.parse(sunriseTimes[i]),
          DateTime.parse(sunsetTimes[i]),
        ),
    };

    const double rainThreshold = 0.5;
    const int probThreshold = 40;

    final List<String> allTimeStrings =
        (hourly['time'] as List?)?.cast<String>() ?? [];
    final List<double> allPrecip = (hourly['precipitation'] as List?)
            ?.map((e) => (e as num?)?.toDouble() ?? 0.0)
            .toList() ??
        [];
    final List<int> allPrecipProb = (hourly['precipitation_probability'] as List?)
            ?.map((e) => (e as num?)?.toInt() ?? 0)
            .toList() ??
        [];

    final List<String> timeNext12h = [];
    final List<double> precpNext12h = [];
    final List<int> precipProbNext12h = [];

    for (int i = 0; i < allTimeStrings.length; i++) {
      if (i >= allPrecip.length || i >= allPrecipProb.length) break;
      final time = DateTime.parse(allTimeStrings[i]);
      if (time.isAfter(nowPrecip) && time.isBefore(nowPrecip.add(Duration(hours: 12)))) {
        timeNext12h.add(allTimeStrings[i]);
        precpNext12h.add(allPrecip[i]);
        precipProbNext12h.add(allPrecipProb[i]);
      }
    }

    int? rainStart;
    int longestRainLength = 0;
    int? bestStart;
    int? bestEnd;

    for (int i = 0; i < precpNext12h.length; i++) {
      if (precpNext12h[i] >= rainThreshold && precipProbNext12h[i] >= probThreshold) {
        rainStart ??= i;
      } else {
        if (rainStart != null) {
          final length = i - rainStart;
          if (length >= 2 && length > longestRainLength) {
            longestRainLength = length;
            bestStart = rainStart;
            bestEnd = i - 1;
          }
          rainStart = null;
        }
      }
    }

    if (rainStart != null) {
      final length = precpNext12h.length - rainStart;
      if (length >= 2 && length > longestRainLength) {
        bestStart = rainStart;
        bestEnd = precpNext12h.length - 1;
      }
    }

    final startIndex = getStartIndex(
      weather['utc_offset_seconds'].toString(),
      hourlyTime,
    );

    final next = _WeatherDerivedData(
      hourlyTime: hourlyTime,
      hourlyTemps: hourlyTemps,
      hourlyWeatherCodes: hourlyWeatherCodes,
      hourlyPrecpProb: hourlyPrecpProb,
      hourlyVisibility: hourlyVisibility,
      daylightMap: daylightMap,
      shouldShowRainBlock: bestStart != null && bestEnd != null,
      startIndex: startIndex,
    );

    _derivedWeatherDataKey = key;
    _derivedWeatherData = next;

    return next;
  }

  List<Map<String, dynamic>> _getInsightsHourlyData(
      dynamic hourly, String cacheKey) {
    if (_insightsCacheKey != cacheKey) {
      _insightsCacheKey = cacheKey;
      _insightsHourlyData = null;
      _insightsDailyData = null;
    }
    final cached = _insightsHourlyData;
    if (cached != null) return cached;
    final next = convertToListOfMaps(Map<String, dynamic>.from(hourly as Map));
    _insightsHourlyData = next;
    return next;
  }

  List<Map<String, dynamic>> _getInsightsDailyData(dynamic daily, String cacheKey) {
    if (_insightsCacheKey != cacheKey) {
      _insightsCacheKey = cacheKey;
      _insightsHourlyData = null;
      _insightsDailyData = null;
    }
    final cached = _insightsDailyData;
    if (cached != null) return cached;
    final next = convertToListOfMaps(Map<String, dynamic>.from(daily as Map));
    _insightsDailyData = next;
    return next;
  }

  Future<Map<String, dynamic>?> getWeatherFromCache() async {
    final box = await HiveBoxes.openWeatherCache();
    var cached = box.get(cacheKey);
    final homePref = PreferencesHelper.getJson(PrefKeys.homeLocation);
    if (cached == null) {
      if (!mounted) return null;
      final weatherService = WeatherService();
      await weatherService.fetchWeather(homePref?['lat'], homePref?['lon'],
          locationName: cacheKey, context: context);

      cached = box.get(cacheKey); // read again after fetch
    }

    if (cached == null) return null; // still null, give up

    String? lastUpdated;

    if (cached != null) {
      final map = json.decode(cached);
      lastUpdated = map['last_updated'];
    }

    if (lat == homePref?['lat'] && lon == homePref?['lon']) {
      isHomeLocation = true;
    } else {
      isHomeLocation = false;
    }

    final hasInternet = await NativeNetwork.isOnline();

    if (!hasInternet) {
      if (!_istriggeredFromLocations) {
        _isAppFullyLoaded = true;
      } else {}

      if (isFirstAppBuild) {
        if (mounted) {
          SnackUtil.showSnackBar(
              context: context, message: "network_unavailable".tr());
        }
        isFirstAppBuild = false;
      }
    } else if (lastUpdated != null) {
      final lastUpdateTime = DateTime.tryParse(lastUpdated);
      final now = DateTime.now();
      if (lastUpdateTime != null &&
          now.difference(lastUpdateTime).inMinutes < 45) {
        if (!_istriggeredFromLocations) {
          _isAppFullyLoaded = true;
        }
        isFirstAppBuild = false;
      } else {
        checkAndUpdateHomeLocation();
        isFirstAppBuild = false;
      }
    }
    return json.decode(cached);
  }

  Future<void> _loadWeatherIconFroggy(
      int weatherCode, bool isDay, int newIndex) async {
    if (!_isLoadingFroggy) return;
    if (_froggyLoadInFlight &&
        _lastFroggyWeatherCode == weatherCode &&
        _lastFroggyIsDay == isDay &&
        _lastFroggyIndex == newIndex) {
      return;
    }

    _lastFroggyWeatherCode = weatherCode;
    _lastFroggyIsDay = isDay;
    _lastFroggyIndex = newIndex;
    _froggyLoadInFlight = true;

    try {
      await _weatherManager.initializeIcons();
      final icon = _weatherManager.getFroggieIcon(weatherCode, isDay);
      if (!mounted || _isLoadingFroggy != true) {
        return;
      }

      if ((PreferencesHelper.getBool("DynamicColors") ?? false) ||
          (PreferencesHelper.getBool("usingCustomSeed") ?? false)) {
        setState(() {
          _iconUrlFroggy = icon;
          _isLoadingFroggy = false;
          if (_istriggeredFromLocations) {
            _istriggeredFromLocations = false;
            _isAppFullyLoaded = true;
          }
        });
        return;
      }

      if (themeCalled == false) {
        setState(() {
          _iconUrlFroggy = icon;
          _isLoadingFroggy = false;
          if (_istriggeredFromLocations) {
            _istriggeredFromLocations = false;
            _isAppFullyLoaded = true;
          }
        });
        Provider.of<ThemeController>(context, listen: false)
            .setSeedColor(weatherConditionColors[newIndex]);
        themeCalled = true;
      }
    } finally {
      _froggyLoadInFlight = false;
    }
  }

  Future<void> _refreshWeatherData() async {
    final hasInternet = await NativeNetwork.isOnline();

    if (isViewLocation) {
      if (!mounted) return;
      SnackUtil.showSnackBar(
          context: context, message: "Location is not saved");
      return;
    }

    if (!hasInternet) {
      if (!mounted) return;
      SnackUtil.showSnackBar(
          context: context, message: 'network_unavailable.'.tr());
      return;
    }

    if (lat == null || lon == null) {
      return;
    }

    final box = await HiveBoxes.openWeatherCache();
    final raw = box.get(cacheKey);
    if (raw == null) {
      return;
    }

    final cached = json.decode(raw);
    final lastUpdatedStr = cached['last_updated'];
    final lastUpdated = DateTime.tryParse(lastUpdatedStr ?? '');

    if (lastUpdated == null) {
    } else {
      final now = DateTime.now();
      final difference = now.difference(lastUpdated);
      if (PreferencesHelper.getBool("ModelChanged") != true) {
        if (difference.inMinutes < 10) {
          if (!mounted) return;
          SnackUtil.showSnackBar(
            context: context,
            message: 'Please_wait_before_refreshing_again.'.tr(),
          );
          return;
        }
      }
    }

    if (!mounted) return;
    final weatherService = WeatherService();
    Map<String, dynamic>? result;
    try {
      result = await weatherService.fetchWeather(lat!, lon!,
          locationName: cacheKey, context: context);
    } catch (e) {
      if (mounted) {
        setState(() {
          _isAppFullyLoaded = true;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('data_fetch_error'.tr()),
            duration: Duration(seconds: 5),
          ),
        );
      }
      return;
    }

    if (result == null) {
      return;
    }

    if (isHomeLocation) {
      widgetsUpdated = false;
    } else {
      widgetsUpdated = true;
    }
    setState(() {
      _isAppFullyLoaded = false;
      _istriggeredFromLocations = true;
      _isLoadingFroggy = true;
      themeCalled = false;
      weatherFuture = getWeatherFromCache();
    });

    PreferencesHelper.setBool("ModelChanged", false);
  }

  Future<void> _setLatLon() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString(PrefKeys.currentLocation);
    if (jsonString != null) {
      final jsonMap = json.decode(jsonString);
      lat = jsonMap['latitude'];
      lon = jsonMap['longitude'];
    }
  }

  Future<void> checkAndUpdateHomeLocation() async {
    final prefs = await SharedPreferences.getInstance();
    final storedJson = prefs.getString(PrefKeys.homeLocation);
    final storedLocation = storedJson != null ? jsonDecode(storedJson) : null;

    if (storedLocation['isGPS'] ?? false) {
      if (!mounted) return;
      bool serviceAvailable =
          await LocationPermissionHelper.checkServicesAndPermission(context);

      if (!serviceAvailable) {
        _refreshWeatherData();
        return;
      }

      final currentPosition = await NativeLocation.getCurrentPosition();
      final currentGeo = await NativeLocation.reverseGeocode(
          currentPosition.latitude, currentPosition.longitude);

      final currentCacheKey = "${currentGeo['city']!}_${currentGeo['country']!}"
          .toLowerCase()
          .replaceAll(' ', '_');

      final currentLat = currentPosition.latitude;
      final currentLon = currentPosition.longitude;

      bool locationChanged = true;

      if (storedLocation != null && storedLocation['isGPS'] == true) {
        final storedLat = storedLocation['lat'];
        final storedLon = storedLocation['lon'];

        final latDiff = (storedLat - currentLat).abs();
        final lonDiff = (storedLon - currentLon).abs();

        locationChanged = latDiff > 0.001 || lonDiff > 0.001;
      }

      Future<void> saveLocation(SavedLocation newLocation) async {
        final prefs = await SharedPreferences.getInstance();
        final existing = prefs.getString(PrefKeys.savedLocations);
        List<SavedLocation> current = [];

        if (existing != null) {
          final decoded = jsonDecode(existing) as List;
          current = decoded.map((e) => SavedLocation.fromJson(e)).toList();
        }

        bool alreadyExists = current.any((loc) =>
            loc.city == newLocation.city && loc.country == newLocation.country);

        if (!alreadyExists) {
          current.add(newLocation);
          await prefs.setString(PrefKeys.savedLocations,
              jsonEncode(current.map((e) => e.toJson()).toList()));
        }
      }

      if (locationChanged) {
        prefs.remove(PrefKeys.homeLocation);

        prefs.setString(
            PrefKeys.homeLocation,
            jsonEncode({
              'city': currentGeo['city']!,
              'country': currentGeo['country']!,
              'cacheKey': currentCacheKey,
              'lat': currentLat,
              'lon': currentLon,
              'isGPS': true,
            }));

        final saved = SavedLocation(
          latitude: currentPosition.latitude,
          longitude: currentPosition.longitude,
          city: currentGeo['city']!,
          country: currentGeo['country']!,
        );

        cityName = saved.city;
        countryName = saved.country;
        cacheKey = currentCacheKey;
        _isAppFullyLoaded = false;
        _istriggeredFromLocations = true;
        _isLoadingFroggy = true;
        lat = saved.latitude;
        lon = saved.longitude;
        themeCalled = false;
        await saveLocation(saved);

        if (!mounted) return;
        final weatherService = WeatherService();
        try {
          await weatherService.fetchWeather(
            currentLat,
            currentLon,
            locationName: currentCacheKey,
            context: context,
          );
        } catch (e) {
          if (mounted) {
            setState(() {
              _isAppFullyLoaded = true;
            });
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text('data_fetch_error'.tr()),
                duration: Duration(seconds: 5),
              ),
            );
          }
          return;
        }
        setState(() {
          weatherFuture = getWeatherFromCache();
        });
      } else {
        _refreshWeatherData();
      }
    } else {
      _refreshWeatherData();
    }
    // } else {
    //   _refreshWeatherData();
    // }
  }

  late bool isLight;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    isLight = Theme.of(context).brightness == Brightness.light;
  }

  final List<Color> weatherConditionColors = [
    // cloudy
    Color.fromARGB(255, 3, 88, 216),

    // overcast
    Colors.blueAccent,

    // clear day
    Colors.blueAccent,

    // clear night
    const Color.fromARGB(255, 58, 66, 183),

    // fog
    Colors.orange,

    // rain
    Colors.blueAccent,

    // thunder
    Color.fromARGB(255, 180, 68, 255),

    // snow
    Colors.cyan,
  ];

  void _ensureGradientCache(bool isLight, bool isShowFrog, bool isCurrentDay) {
    if (_cachedGradients != null &&
        _cachedGradientsScrolled != null &&
        _cachedGradientIsLight == isLight &&
        _cachedGradientIsShowFrog == isShowFrog &&
        _cachedGradientIsCurrentDay == isCurrentDay) {
      return;
    }

    _cachedGradientIsLight = isLight;
    _cachedGradientIsShowFrog = isShowFrog;
    _cachedGradientIsCurrentDay = isCurrentDay;

    _cachedGradients = getGradients(isLight, isShowFrog, isCurrentDay);
    _cachedGradientsScrolled =
        getGradientsScrolled(isLight, isShowFrog, isCurrentDay);
  }

  void _ensureCardColorCache({
    required bool isLight,
    required bool isShowFrog,
    required bool isCurrentDay,
    required bool useDarkerBackground,
  }) {
    if (_cachedSearchBgColors != null &&
        _cachedWeatherContainerColors != null &&
        _cachedCardColorsIsLight == isLight &&
        _cachedCardColorsIsShowFrog == isShowFrog &&
        _cachedCardColorsIsCurrentDay == isCurrentDay &&
        _cachedCardColorsUseDarkerBackground == useDarkerBackground) {
      return;
    }

    _cachedCardColorsIsLight = isLight;
    _cachedCardColorsIsShowFrog = isShowFrog;
    _cachedCardColorsIsCurrentDay = isCurrentDay;
    _cachedCardColorsUseDarkerBackground = useDarkerBackground;

    _cachedSearchBgColors = [
      // cloudy
      isLight
          ? Color(paletteWeather.secondary.get(150))
          : Color(paletteWeather.secondary.get(10)),

      // overcast
      isLight ? Color(0xFFe8f2ff) : Color(paletteWeather.secondary.get(13)),

      // clear day
      isLight ? Color(0xFFe8f2ff) : Color(paletteWeather.primary.get(10)),

      // clear night
      isLight
          ? Color(paletteWeather.neutral.get(100))
          : Color(paletteWeather.primary.get(10)),

      // fog
      isLight
          ? Color(
              CorePalette.of(const Color.fromARGB(255, 255, 153, 0).toARGB32())
                  .secondary
                  .get(100))
          : Color(
              CorePalette.of(const Color.fromARGB(255, 255, 153, 0).toARGB32())
                  .secondary
                  .get(20)),
      // rain
      isLight ? Color(0xFFe8f2ff) : Color(paletteWeather.secondary.get(15)),

      // thunder
      isLight
          ? Color.fromARGB(255, 247, 232, 255)
          : Color(CorePalette.of(const Color(0xFFe4b7f3).toARGB32())
              .secondary
              .get(20)),

      // snow
      isLight
          ? Color.fromARGB(255, 232, 254, 255)
          : Color(
              CorePalette.of(const Color.fromARGB(255, 0, 13, 31).toARGB32())
                  .secondary
                  .get(18)),
    ];

    _cachedWeatherContainerColors = [
      // cloudy
      isLight
          ? paletteWeather.secondary.get(98)
          : paletteWeather.secondary.get(useDarkerBackground
              ? 2
              : !isShowFrog
                  ? isCurrentDay
                      ? 8
                      : 3
                  : 8),

      // overcast
      isLight
          ? 0xFFfcfcff
          : paletteWeather.secondary.get(useDarkerBackground ? 2 : 6),

      // clear day
      isLight
          ? 0xFFfcfcff
          : paletteWeather.primary.get(useDarkerBackground ? 2 : 8),

      // clear night
      isLight
          ? CorePalette.of(const Color.fromARGB(255, 58, 77, 141).toARGB32())
              .primary
              .get(98)
          : CorePalette.of(const Color.fromARGB(255, 58, 77, 141).toARGB32())
              .primary
              .get(useDarkerBackground ? 2 : 4),

      // fog
      isLight
          ? CorePalette.of(Color.fromARGB(255, 255, 213, 165).toARGB32())
              .secondary
              .get(98)
          : CorePalette.of(Color.fromARGB(255, 255, 213, 165).toARGB32())
              .secondary
              .get(useDarkerBackground ? 2 : 6),

      // rain
      isLight
          ? 0xFFfcfcff
          : CorePalette.of(Colors.blueAccent.toARGB32())
              .secondary
              .get(useDarkerBackground ? 2 : 8),

      // thunder
      isLight
          ? CorePalette.of(const Color(0xFFe4b7f3).toARGB32()).secondary.get(96)
          : CorePalette.of(const Color(0xFFe4b7f3).toARGB32())
              .secondary
              .get(useDarkerBackground ? 2 : 10),

      // snow
      isLight
          ? 0xFFfcfcff
          : CorePalette.of(const Color.fromARGB(255, 0, 13, 31).toARGB32())
              .secondary
              .get(1),
    ];
  }

  @override
  Widget build(BuildContext context) {
    SystemChrome.setSystemUIOverlayStyle(
      SystemUiOverlayStyle(
          statusBarColor: Color(0x01000000),
          statusBarIconBrightness: isLight ? Brightness.dark : Brightness.light,
          systemNavigationBarIconBrightness:
              isLight ? Brightness.dark : Brightness.light,
          systemNavigationBarColor:
              MediaQuery.of(context).systemGestureInsets.left > 0
                  ? Color(0x01000000)
                  : isLight
                      ? Color(0x01000000)
                      : Color.fromRGBO(0, 0, 0, 0.3)),
    );

    final isShowFrog =
        context.select<UnitSettingsNotifier, bool>((n) => n.showFrog);
    final colorTheme = Theme.of(context).colorScheme;
    final currentDay = iscurrentDay ?? false;

    SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);

    _ensureGradientCache(isLight, isShowFrog, currentDay);
    final gradients = _cachedGradients!;
    final gradientsScrolled = _cachedGradientsScrolled!;

    return Stack(
      children: [
        ScrollReactiveGradient(
          scrollController: _scrollController,
          baseGradient: gradients[selectedGradientIndex],
          scrolledGradient: gradientsScrolled[selectedGradientIndex],
          headerVisibilityNotifier: _showHeaderNotifier,
        ),
        Scaffold(
          extendBodyBehindAppBar: true,
          backgroundColor: Colors.transparent,
          body: _buildMainBody(),
        ),
        if (!_isAppFullyLoaded)
          Positioned.fill(
            child: Container(
              color: Theme.of(context).colorScheme.surface,
              child: Center(
                  child: ExpressiveLoadingIndicator(
                activeSize: 48,
                color: colorTheme.primary,
              )),
            ),
          ),
        ValueListenableBuilder<bool>(
          valueListenable: _showHeaderNotifier,
          builder: (context, show, child) {
            return Positioned(
              top: 0,
              left: 0,
              right: 0,
              child: AnimatedSlide(
                offset: show ? Offset.zero : const Offset(0, -1),
                duration: const Duration(milliseconds: 250),
                curve: Curves.easeOut,
                child: AnimatedOpacity(
                  duration: const Duration(milliseconds: 250),
                  opacity: show ? 1 : 0,
                  child: IgnorePointer(
                    ignoring: !show,
                    child: Container(
                      height: MediaQuery.of(context).padding.top,
                      color: isLight
                          ? Colors.white.withValues(alpha: 0.5)
                          : Colors.black.withValues(alpha: 0.5),
                    ),
                  ),
                ),
              ),
            );
          },
        ),
      ],
    );
  }

  String _formatLastUpdated(String isoTime, Locale locale) {
    final dt = DateTime.tryParse(isoTime)?.toLocal();
    if (dt == null) return 'Invalid time';

    final now = DateTime.now();
    final difference = now.difference(dt);

    final lang = locale.languageCode;
    final country = locale.countryCode;

    String formatRelativeTime(String unit, int value) {
      final ago = 'ago'.tr();
      final valueUnit = '$value ${unit.tr()}';

      final prependAgoLangs = {
        'ca': ['ES'],
        'es': ['ES'],
        'fr': ['FR'],
        'ro': ['RO'],
        'pt': ['PT', 'BR'],
      };

      final shouldPrependAgo =
          prependAgoLangs[lang]?.contains(country) ?? false;

      return shouldPrependAgo
          ? '${ago.tr()} $valueUnit'
          : '$valueUnit ${ago.tr()}';
    }

    if (difference.inMinutes < 1) return 'just_now'.tr();
    if (difference.inMinutes < 60) {
      return formatRelativeTime('min', difference.inMinutes);
    }
    if (difference.inHours < 24) {
      return formatRelativeTime('hr', difference.inHours);
    }

    return '${dt.month}/${dt.day} at ${dt.hour}:${dt.minute.toString().padLeft(2, '0')}';
  }

  Widget _buildMainBody() {
    final colorTheme = Theme.of(context).colorScheme;

    return CustomRefreshIndicator(
      onRefresh: _refreshWeatherData,
      builder: (context, child, controller) {
        return Stack(
          alignment: Alignment.topCenter,
          children: [
            child,
            AnimatedBuilder(
              animation: controller,
              builder: (_, __) {
                final val = controller.value.clamp(0.0, 1.0);
                final isVisible = val > 0.0;

                return isVisible
                    ? Positioned(
                        top: -30 + 120 * val,
                        child: Opacity(
                          opacity: val,
                          child: RepaintBoundary(
                            child: Container(
                              padding: const EdgeInsets.all(2),
                              decoration: BoxDecoration(
                                color: colorTheme.primaryContainer,
                                borderRadius: BorderRadius.circular(50),
                              ),
                              child: ExpressiveLoadingIndicator(
                                color: colorTheme.primary,
                                activeSize: 40,
                              ),
                            ),
                          ),
                        ),
                      )
                    : const SizedBox.shrink();
              },
            ),
          ],
        );
      },
      child: _buildWeatherContent(),
    );
  }

  Widget _buildWeatherContent() {
    final padding = MediaQuery.of(context).padding;
    final bool usAnimations = context.select<UnitSettingsNotifier, bool>(
        (n) => n.useCardBackgroundAnimations);
    final bool useDarkerBackground =
        context.select<UnitSettingsNotifier, bool>((n) => n.useDarkBackgroundCards);
    final isShowFrog =
        context.select<UnitSettingsNotifier, bool>((n) => n.showFrog);
    final useFullMaterialScheme =
        context.select<UnitSettingsNotifier, bool>((n) => n.useOnlyMaterialScheme);

    final currentDay = iscurrentDay ?? false;
    _ensureCardColorCache(
      isLight: isLight,
      isShowFrog: isShowFrog,
      isCurrentDay: currentDay,
      useDarkerBackground: useDarkerBackground,
    );

    final searchBgColors = _cachedSearchBgColors!;
    final weatherContainerColors = _cachedWeatherContainerColors!;

    return FutureBuilder<Map<String, dynamic>?>(
        future: weatherFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return ListView(
              controller: _scrollController,
              physics: const AlwaysScrollableScrollPhysics(),
              padding: EdgeInsets.zero,
              children: [
                Padding(padding: EdgeInsets.only(top: padding.top + 10)),
              ],
            );
          }

          if (!snapshot.hasData || snapshot.data == null) {
            return ListView(
              controller: _scrollController,
              physics: const AlwaysScrollableScrollPhysics(),
              padding: EdgeInsets.zero,
              children: [
                Padding(padding: EdgeInsets.only(top: padding.top + 10)),
                Center(
                  child: TextButton(
                    onPressed: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(builder: (_) => const LocationsScreen()),
                      );
                    },
                    child: const Text("Choose Location"),
                  ),
                ),
              ],
            );
          }

          final raw = snapshot.data!;
          final weather = raw['data'];
          final current = weather['current'];
          final String? lastUpdated = raw['last_updated'];

          final int weatherCodeFroggy = current['weather_code'] ?? 0;
          final bool isDayFroggy = current['is_day'] == 1;

          final hourly = weather['hourly'] ?? {};

          final derived = _getOrComputeDerivedWeatherData(
            weather: weather,
            lastUpdated: lastUpdated,
          );

          final hourlyTime = derived.hourlyTime;
          final hourlyVisibility = derived.hourlyVisibility;
          final hourlyTemps = derived.hourlyTemps;
          final hourlyWeatherCodes = derived.hourlyWeatherCodes;
          final hourlyPrecpProb = derived.hourlyPrecpProb;
          final bool shouldShowRainBlock = derived.shouldShowRainBlock;
          final int startIndex = derived.startIndex;
          final insightsKey = '$cacheKey|${lastUpdated ?? ''}';

          final daily = weather['daily'];
          final List<dynamic> dailyDates = daily['time'];
          final List<dynamic> dailyTempsMin = daily['temperature_2m_min'];
          final List<dynamic> dailyTempsMax = daily['temperature_2m_max'];
          final List<dynamic> dailyPrecProb =
              daily['precipitation_probability_max'];
          final List<dynamic> dailyWeatherCodes = daily['weather_code'];

          void maybeUpdateWeatherAnimation(Map<String, dynamic> current,
              {isForce = false}) {
            final int weatherCode = current['weather_code'];
            final int isDay = current['is_day'];

            if (isForce) {
              WidgetsBinding.instance.addPostFrameCallback((_) {
                setState(() {
                  weatherAnimationWidget =
                      WeatherConditionAnimationMapper.build(
                          weatherCode: weatherCode,
                          isDay: isDay,
                          context: context,
                          setFullDisplay: !isShowFrog);
                });
              });
            }

            if (_cachedWeatherCode != weatherCode || _cachedIsDay != isDay) {
              weatherAnimationWidget = WeatherConditionAnimationMapper.build(
                  weatherCode: weatherCode,
                  isDay: isDay,
                  context: context,
                  setFullDisplay: !isShowFrog);

              _cachedWeatherCode = weatherCode;
              _cachedIsDay = isDay;
              _cachedIsShowFrog = isShowFrog;
              onLoadForceCall = false;
            }
          }

          if (onLoadForceCall) {
            if (_cachedIsShowFrog != isShowFrog) {
              maybeUpdateWeatherAnimation(current, isForce: true);

              _cachedIsShowFrog = isShowFrog;
            }
          } else {
            onLoadForceCall = true;
          }

          final int weatherCode = current['weather_code'] ?? 0;
          final bool isDay = current['is_day'] == 1;

          String formattedTime = lastUpdated != null
              ? _formatLastUpdated(
                  lastUpdated,
                  Locale(
                      context.locale.languageCode, context.locale.countryCode))
              : 'Unknown';
          final int newIndex = isDay
              ? dayGradients[weatherCode] ?? 0
              : nightGradients[weatherCode] ?? 0;

          if (lastWeatherCode != current['weather_code'] ||
              lastIsDay != isDay) {
            lastWeatherCode = current['weather_code'];
            lastIsDay = isDay;

            WidgetsBinding.instance.addPostFrameCallback((_) {
              if (context.mounted) {
                setState(() {
                  iscurrentDay = isDay;
                  !useFullMaterialScheme
                      ? selectedGradientIndex = newIndex
                      : null;
                  !useFullMaterialScheme
                      ? selectedSearchBgIndex = newIndex
                      : null;
                  !useFullMaterialScheme
                      ? selectedContainerBgIndex = newIndex
                      : null;
                });
                _isLoadingFroggy = true;
                PreferencesHelper.setColor(
                    "weatherThemeColor", weatherConditionColors[newIndex]);
                _loadWeatherIconFroggy(
                    weatherCodeFroggy, isDayFroggy, newIndex);
                maybeUpdateWeatherAnimation(current);
                showInsightsRandomly = Random().nextInt(100) < 40;
              }
            });
          } else {
            if (_isLoadingFroggy) {
              _loadWeatherIconFroggy(weatherCodeFroggy, isDayFroggy, newIndex);
            }
          }

          final double? alderPollen =
              weather['air_quality']['current']['alder_pollen'];
          final double? birchPollen =
              weather['air_quality']['current']['birch_pollen'];
          final double? grassPollen =
              weather['air_quality']['current']['grass_pollen'];
          final double? mugwortPollen =
              weather['air_quality']['current']['mugwort_pollen'];
          final double? olivePollen =
              weather['air_quality']['current']['olive_pollen'];
          final double? ragweedPollen =
              weather['air_quality']['current']['ragweed_pollen'];
          final colorTheme = Theme.of(context).colorScheme;

          if (!widgetsUpdated) {
            WidgetsBinding.instance.addPostFrameCallback((_) {
              if (!mounted || widgetsUpdated) return;
              updateHomeWidget(weather,
                  updatedFromHome: true); // update once on start
              PreferencesHelper.setBool(PrefKeys.triggerFromWorker, false);
              PreferencesHelper.setString(PrefKeys.lastUpdatedFromHome,
                  DateTime.now().toIso8601String());
              widgetsUpdated = true;
            });
          }

          Widget buildLayoutBlock(LayoutBlockType type) {
            switch (type) {
              case LayoutBlockType.rain:
                return shouldShowRainBlock
                    ? RainBlock(
                        key: const ValueKey('RainBlock'),
                        hourlyTime: (hourly['time'] as List).cast<String>(),
                        hourlyPrecp:
                            (hourly['precipitation'] as List).cast<double>(),
                        hourlyPrecpProb: hourly['precipitation_probability'],
                        selectedContainerBgIndex: useFullMaterialScheme
                            ? Theme.of(context)
                                .colorScheme
                                .surfaceContainerLowest
                                .toARGB32()
                            : weatherContainerColors[selectedContainerBgIndex],
                        timezone: weather['timezone'].toString(),
                        utcOffsetSeconds:
                            weather['utc_offset_seconds'].toString(),
                      )
                    : const SizedBox.shrink();

              case LayoutBlockType.insights:
                return !shouldShowRainBlock && showInsightsRandomly
                    ? ShowInsights(
                        key: const ValueKey('ShowInsights'),
                        hourlyData: _getInsightsHourlyData(
                            weather['hourly'], insightsKey),
                        dailyData:
                            _getInsightsDailyData(weather['daily'], insightsKey),
                        currentData: [
                          Map<String, dynamic>.from(weather['current'])
                        ],
                        selectedContainerBgIndex: useFullMaterialScheme
                            ? Theme.of(context)
                                .colorScheme
                                .surfaceContainerLowest
                                .toARGB32()
                            : weatherContainerColors[selectedContainerBgIndex],
                      )
                    : const SizedBox.shrink();

              case LayoutBlockType.summary:
                return SummaryCard(
                    selectedContainerBgIndex: useFullMaterialScheme
                        ? Theme.of(context)
                            .colorScheme
                            .surfaceContainerLowest
                            .toARGB32()
                        : weatherContainerColors[selectedContainerBgIndex],
                    hourlyData: hourly,
                    dailyData: daily,
                    currentData: current,
                    airQualityData: weather['air_quality'],
                    utcOffsetSeconds: weather['utc_offset_seconds'].toString());
              // return
              case LayoutBlockType.hourly:
                return HourlyCard(
                  hourlyTime: hourlyTime,
                  hourlyTemps: hourlyTemps,
                  hourlyWeatherCodes: hourlyWeatherCodes,
                  isHourDuringDaylightOptimized:
                      derived.isHourDuringDaylightOptimized,
                  selectedContainerBgIndex: useFullMaterialScheme
                      ? Theme.of(context)
                          .colorScheme
                          .surfaceContainerLowest
                          .toARGB32()
                      : weatherContainerColors[selectedContainerBgIndex],
                  timezone: weather['timezone'].toString(),
                  utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                  hourlyPrecpProb: hourlyPrecpProb,
                  startIndexOverride: startIndex,
                );

              case LayoutBlockType.daily:
                return DailyCard(
                    dailyTime: dailyDates,
                    dailyTempsMin: dailyTempsMin,
                    dailyWeatherCodes: dailyWeatherCodes,
                    dailyTempsMax: dailyTempsMax,
                    dailyPrecProb: dailyPrecProb,
                    utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                    selectedContainerBgIndex: useFullMaterialScheme
                        ? Theme.of(context)
                            .colorScheme
                            .surfaceContainerLowest
                            .toARGB32()
                        : weatherContainerColors[selectedContainerBgIndex]);

              case LayoutBlockType.conditions:
                return SizedBox(
                  // width: 380,
                  child: ConditionsWidgets(
                      selectedContainerBgIndex: useFullMaterialScheme
                          ? Theme.of(context)
                              .colorScheme
                              .surfaceContainerLowest
                              .toARGB32()
                          : weatherContainerColors[selectedContainerBgIndex],
                      currentHumidity:
                          current['relative_humidity_2m'] ?? 0.0000001,
                      currentDewPoint:
                          hourly['dew_point_2m'][startIndex]
                                  .toDouble() ??
                              0.0000001,
                      currentSunrise: daily['sunrise'][1] ?? 0.0000001,
                      currentSunset: daily['sunset'][1] ?? 0.0000001,
                      currentPressure: current['pressure_msl'] ?? 0.0000001,
                      currentVisibility: hourlyVisibility[startIndex] ??
                          0.0000001,
                      currentWindSpeed: current['wind_speed_10m'] ?? 0.0000001,
                      currentWindDirc:
                          current['wind_direction_10m'] ?? 0.0000001,
                      timezone: weather['timezone'].toString(),
                      utcOffsetSeconds:
                          weather['utc_offset_seconds'].toString(),
                      currentUvIndex:
                          hourly['uv_index'][startIndex] ??
                          0.0000001,
                      currentAQIUSA: weather['air_quality']['current']['us_aqi'] ?? 0.0000001,
                      currentAQIEURO: weather['air_quality']['current']['european_aqi'] ?? 0.0000001,
                      currentTotalPrec: daily['precipitation_sum'][1] ?? 0.0000001,
                      currentDayLength: daily['daylight_duration'][1] ?? 0.0000001,
                      isFromHome: true,
                      moonrise: weather['astronomy']?['astronomy']?['astro']?['moonrise'] ?? '',
                      moonset: weather['astronomy']?['astronomy']?['astro']?['moonset'] ?? '',
                      moonPhase: weather['astronomy']?['astronomy']?['astro']?['moon_phase'] ?? '',
                      cloudCover: current['cloud_cover'].toString()),
                );

              case LayoutBlockType.pollen:
                return Column(
                  children: [
                    PollenCard(
                      pollenData: weather['air_quality']['current'],
                      selectedContainerBgIndex: useFullMaterialScheme
                          ? Theme.of(context)
                              .colorScheme
                              .surfaceContainerLowest
                              .toARGB32()
                          : weatherContainerColors[selectedContainerBgIndex],
                    ),
                  ],
                );
            }
          }

          if (!_isAppFullyLoaded) {
            return ListView(
              controller: _scrollController,
              physics: const AlwaysScrollableScrollPhysics(),
              padding: EdgeInsets.zero,
              children: [
                Padding(padding: EdgeInsets.only(top: padding.top + 10)),
              ],
            );
          }

          final header = Stack(
            clipBehavior: Clip.none,
            children: [
              if (weatherAnimationWidget != null)
                useFullMaterialScheme
                    ? const SizedBox.shrink()
                    : usAnimations
                        ? ValueListenableBuilder<bool>(
                            valueListenable: _showHeaderNotifier,
                            builder: (context, show, child) {
                              return !show
                                  ? weatherAnimationWidget!
                                  : const SizedBox.shrink();
                            },
                          )
                        : const SizedBox.shrink()
              else
                const SizedBox.shrink(),
              Container(
                  margin: EdgeInsets.only(
                      left: isShowFrog ? 14 : 0,
                      right: isShowFrog ? 14 : 0),
                  child: OpenContainer<Map<String, dynamic>?>(
                    transitionType: ContainerTransitionType.fadeThrough,
                    openBuilder: (context, _) => const LocationsScreen(),
                    closedElevation: 0,
                    openElevation: 0,
                    transitionDuration: Duration(milliseconds: 500),
                    closedShape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(50),
                    ),
                    middleColor: isShowFrog ? null : Colors.transparent,
                    closedColor: isShowFrog
                        ? !useFullMaterialScheme
                            ? searchBgColors[selectedSearchBgIndex]
                            : Color(Theme.of(context)
                                .colorScheme
                                .surfaceContainerHigh
                                .toARGB32())
                        : Colors.transparent,
                    openColor: colorTheme.surface,
                    closedBuilder: (context, openContainer) {
                      return Container(
                        width: double.infinity,
                        height: 56,
                        padding:
                            const EdgeInsetsDirectional.only(start: 7, end: 8),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(50),
                        ),
                        child: Row(
                          children: [
                            Expanded(
                              child: Row(
                                children: [
                                  isShowFrog
                                      ? IconButton(
                                          onPressed: () async {
                                            showAddBottomSheet(
                                              context,
                                              lat.toString(),
                                              lon.toString(),
                                              cityName,
                                              countryName,
                                            );
                                          },
                                          icon: const Icon(
                                              Icons.location_on_outlined),
                                        )
                                      : SizedBox(
                                          width: 10,
                                        ),
                                  Expanded(
                                    child: Text(
                                      "$cityName, $countryName",
                                      style: TextStyle(
                                        color: isShowFrog
                                            ? colorTheme.onSurfaceVariant
                                            : isLight
                                                ? Colors.black
                                                : Colors.white,
                                        fontSize: 18,
                                      ),
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis,
                                      textAlign: TextAlign.start,
                                      textHeightBehavior: TextHeightBehavior(
                                          applyHeightToFirstAscent: false,
                                          applyHeightToLastDescent: false),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            IconButton(
                              onPressed: () async {
                                Navigator.of(context).push(
                                  MaterialPageRoute(
                                      builder: (_) => const SettingsScreen()),
                                );
                              },
                              icon: const Icon(Icons.settings_outlined),
                              color:
                                  isShowFrog ? null : colorTheme.onSurface,
                            ),
                            if (isViewLocation)
                              FilledButton(
                                onPressed: () => handleSaveLocationView(
                                  context: context,
                                  updateUIState: () {
                                    setState(() {
                                      isViewLocation = false;
                                      _isAppFullyLoaded = false;
                                      _istriggeredFromLocations = true;
                                      themeCalled = false;
                                      _isLoadingFroggy = true;
                                    });
                                  },
                                ),
                                style: ButtonStyle(
                                    backgroundColor: WidgetStateProperty.all(
                                        Theme.of(context)
                                            .colorScheme
                                            .tertiary)),
                                child: Text(
                                  "Save",
                                  style: TextStyle(
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onTertiary,
                                      fontWeight: FontWeight.w600),
                                ),
                              )
                          ],
                        ),
                      );
                    },
                    onClosed: (result) async {
                      final messenger = ScaffoldMessenger.of(context);
                      await Future.delayed(Duration(milliseconds: 300));
                      if (!context.mounted) return;
                      if (result == null || result['viewLocaton'] != true) {
                        return;
                      }

                      SnackUtil.showSnackBar(
                        context: context,
                        message: 'Loading data',
                      );

                      final selectedViewLocation = PreferencesHelper.getJson(
                          PrefKeys.selectedViewLocation);
                      final newCity = selectedViewLocation?['city'];
                      final newCountry = selectedViewLocation?['country'];
                      final newCacheKey = selectedViewLocation?['cacheKey'];
                      final newLat =
                          (selectedViewLocation?['lat'] as num?)?.toDouble();
                      final newLon =
                          (selectedViewLocation?['lon'] as num?)?.toDouble();
                      final cacheKeyStr = newCacheKey?.toString();

                      if (!context.mounted) return;
                      if (newLat == null || newLon == null || cacheKeyStr == null) {
                        return;
                      }
                      Map<String, dynamic>? fetchedResult;
                      final weatherService = WeatherService();
                      try {
                        fetchedResult = await weatherService.fetchWeather(
                          newLat,
                          newLon,
                          locationName: cacheKeyStr,
                          context: context,
                        );
                      } catch (e) {
                        if (!context.mounted) return;
                        setState(() {
                          _isAppFullyLoaded = true;
                        });
                        messenger.showSnackBar(
                          SnackBar(
                            content: Text('data_fetch_error'.tr()),
                            duration: Duration(seconds: 5),
                          ),
                        );
                        return;
                      }

                      if (!context.mounted || fetchedResult == null) {
                        return;
                      }

                      setState(() {
                        cityName = newCity ?? cityName;
                        countryName = newCountry ?? countryName;
                        cacheKey = cacheKeyStr;
                        lat = newLat;
                        lon = newLon;
                        isViewLocation = true;
                        _isAppFullyLoaded = false;
                        _istriggeredFromLocations = true;
                        themeCalled = false;
                        _isLoadingFroggy = true;
                        weatherFuture = Future.value(fetchedResult);
                      });
                    },
                  ))
            ],
          );

          final foldableWidth = isFoldableLayout(context) ? 500.0 : null;
          Widget wrapFoldable(Widget child) {
            if (foldableWidth == null) return child;
            return Center(
              child: SizedBox(
                width: foldableWidth,
                child: child,
              ),
            );
          }

          final layoutConfig =
              context.select<LayoutProvider, List<LayoutBlockConfig>>(
            (p) => p.layoutConfig,
          );

          final visibleBlocks = layoutConfig.where((block) {
            if (!block.isVisible) return false;

            switch (block.type) {
              case LayoutBlockType.pollen:
                return isPollenDataAvailable([
                  alderPollen,
                  birchPollen,
                  olivePollen,
                  grassPollen,
                  mugwortPollen,
                  ragweedPollen,
                ]);

              case LayoutBlockType.rain:
                return shouldShowRainBlock;

              case LayoutBlockType.insights:
                return !shouldShowRainBlock && showInsightsRandomly;

              default:
                return true;
            }
          }).toList();

          final List<Object> blockEntries = <Object>[];
          for (int i = 0; i < visibleBlocks.length; i++) {
            final currentBlock = visibleBlocks[i];
            blockEntries.add(currentBlock.type);

            final isRainThenInsights = currentBlock.type == LayoutBlockType.rain &&
                i + 1 < visibleBlocks.length &&
                visibleBlocks[i + 1].type == LayoutBlockType.insights;
            if (!isRainThenInsights && i < visibleBlocks.length - 1) {
              blockEntries.add(const _HomeListSpacer());
            }
          }

          const baseCount = 6;
          final totalCount = baseCount + blockEntries.length + 1;

          return ListView.builder(
            controller: _scrollController,
            physics: const AlwaysScrollableScrollPhysics(),
            padding: EdgeInsets.zero,
            itemCount: totalCount,
            itemBuilder: (context, index) {
              if (index == 0) {
                return Padding(padding: EdgeInsets.only(top: padding.top + 10));
              }
              if (index == 1) {
                return _HomeKeepAlive(child: header);
              }
              if (index == 2) {
                return const SizedBox(height: 10);
              }
              if (index == 3) {
                return _HomeKeepAlive(
                  child: WeatherTopCard(
                    currentTemp: current['temperature_2m'].toDouble(),
                    currentFeelsLike:
                        current['apparent_temperature'].toDouble(),
                    currentMaxTemp:
                        weather['daily']?['temperature_2m_max']?[1]
                                ?.toDouble() ??
                            0,
                    currentMinTemp:
                        weather['daily']?['temperature_2m_min']?[1]
                                ?.toDouble() ??
                            0,
                    currentWeatherIconCode: current['weather_code'],
                    currentisDay: current['is_day'],
                    currentLastUpdated: formattedTime,
                  ),
                );
              }
              if (index == 4) {
                return _HomeKeepAlive(
                    child: WeatherFrogIconWidget(iconUrl: _iconUrlFroggy));
              }
              if (index == 5) {
                return const SizedBox(height: 14);
              }

              if (index == totalCount - 1) {
                return homeBottomBar(context, isLight);
              }

              final entryIndex = index - baseCount;
              final entry = blockEntries[entryIndex];

              if (entry is _HomeListSpacer) {
                return const SizedBox(height: 12);
              }

              return _HomeKeepAlive(
                child: wrapFoldable(
                  RepaintBoundary(
                    child: buildLayoutBlock(entry as LayoutBlockType),
                  ),
                ),
              );
            },
          );
        });
  }
}

class _HomeListSpacer {
  const _HomeListSpacer();
}

class _HomeKeepAlive extends StatefulWidget {
  final Widget child;
  const _HomeKeepAlive({required this.child});

  @override
  State<_HomeKeepAlive> createState() => _HomeKeepAliveState();
}

class _HomeKeepAliveState extends State<_HomeKeepAlive>
    with AutomaticKeepAliveClientMixin {
  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return widget.child;
  }
}

class _WeatherDerivedData {
  final List<dynamic> hourlyTime;
  final List<dynamic> hourlyTemps;
  final List<dynamic> hourlyWeatherCodes;
  final List<dynamic> hourlyPrecpProb;
  final List<dynamic> hourlyVisibility;
  final Map<String, (DateTime, DateTime)> daylightMap;
  final bool shouldShowRainBlock;
  final int startIndex;

  const _WeatherDerivedData({
    required this.hourlyTime,
    required this.hourlyTemps,
    required this.hourlyWeatherCodes,
    required this.hourlyPrecpProb,
    required this.hourlyVisibility,
    required this.daylightMap,
    required this.shouldShowRainBlock,
    required this.startIndex,
  });

  bool isHourDuringDaylightOptimized(DateTime hourTime) {
    final key =
        "${hourTime.year.toString().padLeft(4, '0')}-${hourTime.month.toString().padLeft(2, '0')}-${hourTime.day.toString().padLeft(2, '0')}";
    final times = daylightMap[key];
    if (times != null) {
      return hourTime.isAfter(times.$1) && hourTime.isBefore(times.$2);
    }
    return true;
  }
}

// Workaround for flickering
// OverlayEntry? _loaderOverlay;

// void showLoader(BuildContext context) {
//   if (_loaderOverlay != null) return;
//   _loaderOverlay = OverlayEntry(
//     builder: (context) => Positioned.fill(
//       child: Container(
//         color: Theme.of(context).colorScheme.surface,
//         child: const Center(
//           child: LoaderWidget(
//             size: 60,
//             isContained: false,
//           ),
//         ),
//       ),
//     ),
//   );

//   Overlay.of(context)?.insert(_loaderOverlay!);
// }

// void hideLoader() {
//   _loaderOverlay?.remove();
//   _loaderOverlay = null;
// }

bool checkLocaleChange(String localeCurrent) {
  if (PreferencesHelper.getString("currentLocale") != null) {
    if (PreferencesHelper.getString("currentLocale") == localeCurrent) {
      return false;
    } else {
      return true;
    }
  } else {
    PreferencesHelper.setString("currentLocale", localeCurrent);
    return false;
  }
}
