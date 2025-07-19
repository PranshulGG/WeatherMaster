import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import '../utils/theme.dart';
import 'package:hive/hive.dart';
import '../screens/settings.dart';
import '../screens/locations.dart';
import '../widgets/top_weather_card.dart';
import '../widgets/hourly_card.dart';
import '../widgets/daily_card.dart';
import 'package:flutter/services.dart';
import 'dart:convert';
import '../utils/animation_map.dart';
import '../services/fetch_data.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:material_color_utilities/material_color_utilities.dart';
import '../utils/froggy_map.dart';
import '../utils/theme_controller.dart';
import 'package:provider/provider.dart';
import '../utils/preferences_helper.dart';
import '../widgets/top_summary_block.dart';
import '../widgets/current_conditions_card.dart';
import '../widgets/rain_block.dart';
import '../models/insights_gen.dart';
import 'dart:math';
import 'package:custom_refresh_indicator/custom_refresh_indicator.dart';
import '../models/loading_me.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../notifiers/layout_provider.dart';
import '../utils/geoLocation.dart'; 
import '../models/saved_location.dart';
import 'package:http/http.dart' as http;
import 'dart:async';
import '../widgets/pollen_card.dart';
import '../models/layout_config.dart';
import '../utils/snack_util.dart';

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

  List<LayoutBlockConfig> layoutConfig = [];

  // late Future<Map<String, dynamic>?>? weatherFuture;
  Future<Map<String, dynamic>?>? weatherFuture;

  Map<String, dynamic>? weatherData;

    Widget? weatherAnimationWidget;
    int? _cachedWeatherCode;
    int? _cachedIsDay;
bool _showHeader = false;
final ValueNotifier<bool> _showHeaderNotifier = ValueNotifier(false);
  late bool isHomeLocation;
  final ScrollController _scrollController = ScrollController();

  bool _isAppFullyLoaded = false;
  bool shouldSkipAppFullyLoaded = false;
  bool themeCalled = false;
  late String cityName;
  late String countryName;
  late String cacheKey;
  int selectedGradientIndex = 2;
  int selectedSearchBgIndex = 2;
  int selectedContainerBgIndex = 2;
  int selectedConditionColorIndex = 2;
  bool _istriggeredFromLocations = false;
  bool showInsightsRandomly = false;
  int? lastWeatherCode;

  bool? lastIsDay;

    final WeatherFroggyManager _weatherManager = WeatherFroggyManager();

  String? _iconUrlFroggy;
  bool _isLoadingFroggy = true;
  bool layoutCreated = false;

Map<int, int> dayGradients = {
  0: 2,   // Clear sky
  1: 0,   // Mainly clear
  2: 0,   // Partly cloudy
  3: 1,   // Overcast

  45: 4,  // Fog
  48: 4,  // Depositing rime fog

  51: 5,  // Drizzle: Light
  53: 5,  // Drizzle: Moderate
  55: 5,  // Drizzle: Dense
  56: 5,  // Freezing Drizzle: Light
  57: 5,  // Freezing Drizzle: Dense
  61: 5,  // Rain: Slight
  63: 5,  // Rain: Moderate
  65: 5,  // Rain: Heavy
  66: 5,  // Freezing Rain: Light
  67: 5,  // Freezing Rain: Heavy
  80: 5,  // Rain showers: Slight
  81: 5,  // Rain showers: Moderate
  82: 5,  // Rain showers: Violent

  95: 6,  // Thunderstorm
  96: 6,  // Thunderstorm with hail
  99: 6,  // Thunderstorm with heavy hail

  71: 7,  // Snow fall: Slight
  73: 7,  // Snow fall: Moderate
  75: 7,  // Snow fall: Heavy
  77: 7,  // Snow grains
  85: 7,  // Snow showers: Slight
  86: 7,  // Snow showers: Heavy
};


Map<int, int> nightGradients = {
  0: 3,   // Clear sky night
  1: 0,   // Mainly clear
  2: 0,   // Partly cloudy
  3: 1,   // Overcast

  45: 4,  // Fog
  48: 4,  // Depositing rime fog

  51: 5,  // Drizzle: Light
  53: 5,  // Drizzle: Moderate
  55: 5,  // Drizzle: Dense
  56: 5,  // Freezing Drizzle: Light
  57: 5,  // Freezing Drizzle: Dense
  61: 5,  // Rain: Slight
  63: 5,  // Rain: Moderate
  65: 5,  // Rain: Heavy
  66: 5,  // Freezing Rain: Light
  67: 5,  // Freezing Rain: Heavy
  80: 5,  // Rain showers: Slight
  81: 5,  // Rain showers: Moderate
  82: 5,  // Rain showers: Violent

  95: 6,  // Thunderstorm
  96: 6,  // Thunderstorm with hail
  99: 6,  // Thunderstorm with heavy hail

  71: 7,  // Snow fall: Slight
  73: 7,  // Snow fall: Moderate
  75: 7,  // Snow fall: Heavy
  77: 7,  // Snow grains
  85: 7,  // Snow showers: Slight
  86: 7,  // Snow showers: Heavy
};




  double? lat;
  double? lon;

  double? homeLat;
  double? homeLon;
  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      final layoutProvider = Provider.of<LayoutProvider>(context, listen: false);

      layoutProvider.addListener(() {
        loadLayoutConfig();
      });

      layoutProvider.loadLayout(); 
    });
      WidgetsBinding.instance.addPostFrameCallback((_) {
        _initAfterLoad();
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

  } else {
    _setLatLon();
  }
  }


void _initAfterLoad() async {
  weatherFuture = getWeatherFromCache();
}

Future<void> loadLayoutConfig() async {
  final prefs = await SharedPreferences.getInstance();
  final jsonStringList = prefs.getStringList('layout_config');

  if (jsonStringList != null) {
    layoutConfig = jsonStringList
        .map((json) => LayoutBlockConfig.fromJson(jsonDecode(json)))
        .toList();
  } else {
    layoutConfig = [
      LayoutBlockConfig(type: LayoutBlockType.rain),
      LayoutBlockConfig(type: LayoutBlockType.insights),
      LayoutBlockConfig(type: LayoutBlockType.summary),
      LayoutBlockConfig(type: LayoutBlockType.hourly),
      LayoutBlockConfig(type: LayoutBlockType.daily),
      LayoutBlockConfig(type: LayoutBlockType.conditions),
      LayoutBlockConfig(type: LayoutBlockType.pollen),
    ];
  }


    if(layoutCreated){
    setState(() {
    });
  }
    layoutCreated = true;
}

Future<void> saveLayoutConfig() async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.setStringList(
    'layout_config',
    layoutConfig.map((e) => jsonEncode(e.toJson())).toList(),
  );
}




 Future<void> setHomeasCurrent() async{
      final prefs = await SharedPreferences.getInstance();
      final locationData = {
      'city': PreferencesHelper.getJson(
          'homeLocation')?['city'],
      'country': PreferencesHelper.getJson(
          'homeLocation')?['country'],
      'cacheKey': PreferencesHelper.getJson(
          'homeLocation')?['cacheKey'],
      'latitude': PreferencesHelper.getJson(
          'homeLocation')?['lat'],
      'longitude': PreferencesHelper.getJson(
          'homeLocation')?['lon'],
    };
    await prefs.setString('currentLocation',
        jsonEncode(locationData));
}

  Future<Map<String, dynamic>?> getWeatherFromCache() async {
    final box = await Hive.openBox('weatherMasterCache');
    final cached = box.get(cacheKey);
    if (cached == null) return null;


                          
    final rawJson = box.get(cacheKey);

    String? lastUpdated;

    if (rawJson != null) {
      final map = json.decode(rawJson);
      lastUpdated = map['last_updated'];
    }

  if(lat == PreferencesHelper.getJson('homeLocation')?['lat'] && lon == PreferencesHelper.getJson('homeLocation')?['lon']){
    isHomeLocation = true;
  } else{
    isHomeLocation = false;
  }

  final hasInternet = await hasRealInternet();


  if (!hasInternet) {
    _isAppFullyLoaded = true;
  } else if (isHomeLocation && lastUpdated != null) {
    final lastUpdateTime = DateTime.tryParse(lastUpdated);
    final now = DateTime.now();
    if (lastUpdateTime != null && now.difference(lastUpdateTime).inMinutes < 45) {
      _isAppFullyLoaded = true; 
    } else{
    checkAndUpdateHomeLocation();

    }
  }


    return json.decode(cached);
  }

Future<bool> hasRealInternet() async {
  try {
    final response = await http.get(Uri.parse('https://www.google.com'))
        .timeout(Duration(seconds: 5));
    return response.statusCode == 200;
  }  catch (e) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('network_unavailable'.tr()),
          duration: Duration(seconds: 3),
        ),
      );
    }
    return false;
  }
}


 

Future<void> _loadWeatherIconFroggy(int weatherCode, bool isDay, newindex) async {
  await _weatherManager.initializeIcons();
  final icon = _weatherManager.getFroggieIcon(weatherCode, isDay);
  if (mounted && _isLoadingFroggy == true) {

    if ((PreferencesHelper.getBool("DynamicColors") ?? false) || (PreferencesHelper.getBool("usingCustomSeed") ?? false)) {
        setState(() {
          _iconUrlFroggy = icon;
          _isLoadingFroggy = false;
          if(_istriggeredFromLocations){
            _istriggeredFromLocations = false;
            _isAppFullyLoaded = true;

        }
        });
      }

      if (!(PreferencesHelper.getBool("DynamicColors") ?? false) && !(PreferencesHelper.getBool("usingCustomSeed") ?? false)) {

        if(themeCalled == false){

          themeCalled = true;
          _iconUrlFroggy = icon;
          _isLoadingFroggy = false;
        if(_istriggeredFromLocations){
            _istriggeredFromLocations = false;
            _isAppFullyLoaded = true;
        }
          Provider.of<ThemeController>(context, listen: false)
            .setSeedColor(weatherConditionColors[newindex]);



      }
    
  }

  

  }
}


Future<void> _refreshWeatherData() async {

  final hasInternet = await hasRealInternet();

  if (!hasInternet) {
    if (!mounted) return;
      SnackUtil.showSnackBar(context: context, message: 'network_unavailable.'.tr());
    return;
  }
  
  if (lat == null || lon == null) {
    return;
  }

  final box = await Hive.openBox('weatherMasterCache');
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
    if (difference.inMinutes < 10) {
      if (!mounted) return;
    SnackUtil.showSnackBar(
      context: context,
      message: 'Please_wait_before_refreshing_again.'.tr(),
    );
      return;
    }
  }

  final weatherService = WeatherService();
  await weatherService.fetchWeather(
    lat!,
    lon!,
    locationName: cacheKey,
    context: context
  );




  setState(() {
  _isAppFullyLoaded = false;
  _istriggeredFromLocations = true;
  _isLoadingFroggy = true;
    themeCalled = false;
  });
 weatherFuture = getWeatherFromCache();
}

        Future<void> _setLatLon() async {
  final prefs = await SharedPreferences.getInstance();
  final jsonString = prefs.getString('currentLocation');
  if (jsonString != null) {
    final jsonMap = json.decode(jsonString);
      lat = jsonMap['latitude'];
      lon = jsonMap['longitude'];
  }
}





Future<void> checkAndUpdateHomeLocation() async {
  final prefs = await SharedPreferences.getInstance();
  final storedJson = prefs.getString('homeLocation');
  final storedLocation = storedJson != null ? jsonDecode(storedJson) : null;



if(storedLocation['isGPS'] ?? false){
  final currentPosition = await getCurrentPosition();
  final currentGeo = await reverseGeocode(currentPosition.latitude, currentPosition.longitude); 

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


  if (locationChanged) {
    prefs.remove('homeLocation');

    prefs.setString('homeLocation', jsonEncode({
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

   await saveLocation(saved);


      final weatherService = WeatherService();
      await weatherService.fetchWeather(
        currentLat,
        currentLon,
        locationName: currentCacheKey,
        context: context
      );


  weatherFuture = getWeatherFromCache();

  _isAppFullyLoaded = true;

  } else{
  _refreshWeatherData();
  }
  } else{

      _refreshWeatherData();
  }
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




  @override
  Widget build(BuildContext context) {

        SystemChrome.setSystemUIOverlayStyle(
         SystemUiOverlayStyle(
          statusBarColor: Color(0x01000000),
          statusBarIconBrightness: isLight ? Brightness.dark : Brightness.light,
          systemNavigationBarIconBrightness: isLight ? Brightness.dark : Brightness.light,
          systemNavigationBarColor: MediaQuery.of(context).systemGestureInsets.left > 0 ? Color(0x01000000) : isLight ? Color(0x01000000) : Color.fromRGBO(0, 0, 0, 0.3)
          
        ),
      );

      SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
      


  final List<LinearGradient> gradients = [ 

      // cloudy
   isLight ? 
   LinearGradient(
    colors: [
      Color(0xFFc6d3e4),
      Color(0xFFd5e4f7)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   : LinearGradient(
      colors: [Color(paletteWeather.neutral.get(10)), Color(paletteWeather.secondary.get(25))],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),


    // overcast
  isLight ? 
    LinearGradient(
    colors: [
      Color(0xFFc9d3e0),
      Color(0xFFcfdef1)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [Color(paletteWeather.neutral.get(20)), Color(paletteWeather.secondary.get(15))],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),
    
    // clear day
       isLight ? 
   LinearGradient(
    colors: [
      Color(0xFF9dceff),
      Color(0xFFcee5ff)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [const Color.fromARGB(255, 4, 36, 83), Color(0xFF004a76)],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),

    // clear night
       isLight ? 
   LinearGradient(
    colors: [
    Color.fromARGB(255, 227, 232, 255), Color.fromARGB(255, 189, 197, 236)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [Color.fromARGB(255, 0, 0, 0), Color(0xFF162155)],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),

    // fog
      isLight ? 
   LinearGradient(
    colors: [
    Color.fromARGB(255, 245, 237, 219), Color.fromARGB(255, 255, 236, 192)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
    const LinearGradient(
      colors: [Color(0xFF191209), Color(0xFF352603)],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),


    // rain
      isLight ? 
    LinearGradient(
    colors: [
      Color(0xFFaab8ca),
      Color(0xFFc4d3e5)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [Color(0xFF050709), Color(0xFF1e2c3a)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),

    // thunder
  isLight ? 
    LinearGradient(
    colors: [
  Color.fromARGB(255, 231, 201, 243), Color.fromARGB(255, 223, 196, 229)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
    const LinearGradient(
      colors: [Color(0xFF15021a), Color.fromARGB(255, 76, 40, 88)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),

      isLight ? 
    LinearGradient(
    colors: [
      Color.fromARGB(255, 170, 200, 202),
      Color.fromARGB(255, 196, 225, 229)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
    const LinearGradient(
      colors: [Color.fromARGB(255, 0, 0, 0), Color.fromARGB(255, 17, 23, 29)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),

  ];

  final List<LinearGradient> gradientsScrolled = [ 

      // cloudy
         isLight ? 
   LinearGradient(
    colors: [
      Color(0xFFd5e4f7),
      Color(0xFFd5e4f7)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   : LinearGradient(
      colors: [Color(paletteWeather.secondary.get(25)), Color(paletteWeather.secondary.get(25))],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),

    // overcast
    isLight ? 
    LinearGradient(
    colors: [
      Color(0xFFcfdef1),
      Color(0xFFcfdef1)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [Color(paletteWeather.secondary.get(15)), Color(paletteWeather.secondary.get(15))],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),
    
    // clear day

           isLight ? 
   LinearGradient(
    colors: [
      Color(0xFFcee5ff),
      Color(0xFFcee5ff)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [Color(0xFF004a76), Color(0xFF004a76)],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),

    // clear night
           isLight ? 
   LinearGradient(
    colors: [
    Color.fromARGB(255, 189, 197, 236), Color.fromARGB(255, 189, 197, 236)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [Color(0xFF162155), Color(0xFF162155)],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),

    // fog
          isLight ? 
   LinearGradient(
    colors: [
    Color.fromARGB(255, 255, 236, 192), Color.fromARGB(255, 255, 236, 192)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
    const LinearGradient(
      colors: [Color(0xFF352603), Color(0xFF352603)],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      stops: [0, 0.5]
    ),

    // rain
        isLight ? 
    LinearGradient(
    colors: [
      Color(0xFFc4d3e5),
      Color(0xFFc4d3e5)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
     LinearGradient(
      colors: [Color(0xFF1e2c3a), Color(0xFF1e2c3a)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),

    // thunder
      isLight ? 
    LinearGradient(
    colors: [
  Color.fromARGB(255, 171, 145, 180), Color.fromARGB(255, 223, 196, 229)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
    const LinearGradient(
      colors: [Color.fromARGB(255, 76, 40, 88), Color.fromARGB(255, 76, 40, 88)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),

      isLight ? 
    LinearGradient(
    colors: [
      Color.fromARGB(255, 196, 225, 229),
      Color.fromARGB(255, 196, 225, 229)
    ],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0, 0.5],
    )
   :
    const LinearGradient(
      colors: [Color.fromARGB(255, 17, 23, 29), Color.fromARGB(255, 17, 23, 29)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      stops: [0, 0.5]
    ),

  ];


    return Stack(
  children: [
    // Container(
    //   decoration: BoxDecoration(
    //     gradient: gradients[selectedGradientIndex],
    //   ),
    
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
          child: const Center(
            child: LoaderWidget(size: 60, isContained: false,),
          ),
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
              color: isLight ? Colors.white.withValues(alpha: 0.5) : Colors.black.withValues(alpha: 0.5),
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

String _formatLastUpdated(String isoTime) {
  final dt = DateTime.tryParse(isoTime)?.toLocal();
  if (dt == null) return 'Invalid time';

  final now = DateTime.now();
  final difference = now.difference(dt);

  if (difference.inMinutes < 1) return 'just_now'.tr();
  if (difference.inMinutes < 60) return '${difference.inMinutes} ${'min'.tr()} ${'ago'.tr()}';
  if (difference.inHours < 24) return '${difference.inHours} ${'hr'.tr()} ${'ago'.tr()}';
  return '${dt.month}/${dt.day} at ${dt.hour}:${dt.minute.toString().padLeft(2, '0')}';
}


Widget _buildMainBody() {
  
  final padding = MediaQuery.of(context).padding;
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
            return Positioned(
              top: -30 + 120 * val,
              child: Opacity(
                opacity: val,
                child: LoaderWidget(size: 50, isContained: true,), 
              ),
            );
          },
        ),
      ],
    );
  },
    child: SingleChildScrollView(
      controller: _scrollController,
      physics: const AlwaysScrollableScrollPhysics(),
      child: Column(
        children: [
          Padding(padding: EdgeInsets.only(top: padding.top + 10)),
          _buildWeatherContent(), 
        ],
      ),
    ),
  );
}

Widget _buildWeatherContent() {
  final bool usAnimations = context.watch<UnitSettingsNotifier>().useCardBackgroundAnimations;

  
      final List<Color> searchBgColors = [
        // cloudy
           isLight ? Color(paletteWeather.secondary.get(150)) : Color(paletteWeather.secondary.get(11)),

          // overcast
         isLight ? Color(0xFFe8f2ff) : Color(paletteWeather.secondary.get(13)),

          // clear day
        isLight ? Color(0xFFe8f2ff) :  Color(paletteWeather.primary.get(10)),

          // clear night
        isLight ? Color(paletteWeather.neutral.get(100)) :  Color(paletteWeather.primary.get(10)),

         // fog  
         isLight ? Color(CorePalette.of(const Color.fromARGB(255, 255, 153, 0).toARGB32()).secondary.get(100)) : Color(CorePalette.of(const Color.fromARGB(255, 255, 153, 0).toARGB32()).secondary.get(20)),
         // rain
         isLight ? Color(0xFFe8f2ff) : Color(paletteWeather.secondary.get(15)),

          // thunder
          isLight ? Color.fromARGB(255, 247, 232, 255) : Color(CorePalette.of(const Color(0xFFe4b7f3).toARGB32()).secondary.get(20)),


          // snow
         isLight ? Color.fromARGB(255, 232, 254, 255) : Color(CorePalette.of(const Color.fromARGB(255, 0, 13, 31).toARGB32()).secondary.get(18)),

      ];


      final List<int> weatherContainerColors = [
        // cloudy
        isLight ? paletteWeather.secondary.get(98) : 0xff0e1d2a,

          // overcast
         isLight ? 0xFFfcfcff : paletteWeather.secondary.get(6),

          // clear day
         isLight ? 0xFFfcfcff : paletteWeather.primary.get(8),

          // clear night
         isLight ? CorePalette.of(const Color.fromARGB(255, 58, 77, 141).toARGB32()).primary.get(98) : CorePalette.of(const Color.fromARGB(255, 58, 77, 141).toARGB32()).primary.get(5),

         // fog  
         isLight ? CorePalette.of(Color.fromARGB(255, 255, 213, 165).toARGB32()).secondary.get(98) : CorePalette.of(Color.fromARGB(255, 255, 213, 165).toARGB32()).secondary.get(6),

         // rain
         isLight ? 0xFFfcfcff : CorePalette.of(Colors.blueAccent.toARGB32()).secondary.get(8),

          // thunder
         isLight ? CorePalette.of(const Color(0xFFe4b7f3).toARGB32()).secondary.get(96) : CorePalette.of(const Color(0xFFe4b7f3).toARGB32()).secondary.get(10),


          // snow
        isLight ? 0xFFfcfcff : CorePalette.of(const Color.fromARGB(255, 0, 13, 31).toARGB32()).secondary.get(1),

      ];



  return  FutureBuilder<Map<String, dynamic>?>(
      future: weatherFuture,
      builder: (context, snapshot) {
    if (snapshot.connectionState == ConnectionState.waiting) {
      return const SizedBox.shrink();
    }

    if (!snapshot.hasData || snapshot.data == null) {
      return  Center(child: TextButton(
          onPressed: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (_) => const LocationsScreen()),
            );
          },
          child: const Text("Choose Location"),
        ),
        );
    }


    final raw = snapshot.data!;
    final weather = raw['data'];
    final current = weather['current'];
    final String? lastUpdated = raw['last_updated'];

  final int weatherCodeFroggy = current['weather_code'] ?? 0;
final bool isDayFroggy = current['is_day'] == 1;


    final hourly = weather['hourly'];
    final List<dynamic> hourlyTime = hourly['time'];
    final List<dynamic> hourlyTemps = hourly['temperature_2m'];
    final List<dynamic> hourlyWeatherCodes = hourly['weather_code'];
    final List<dynamic> hourlyPrecpProb = hourly['precipitation_probability'];

    final daily = weather['daily'];
    final List<dynamic> dailyDates = daily['time'];       
    final List<dynamic> sunriseTimes = daily['sunrise'];   
    final List<dynamic> sunsetTimes = daily['sunset'];    
    final List<dynamic> dailyTempsMin = daily['temperature_2m_min'];
    final List<dynamic> dailyTempsMax = daily['temperature_2m_max'];
    final List<dynamic> dailyPrecProb = daily['precipitation_probability_max'];
    final List<dynamic> dailyWeatherCodes = daily['weather_code'];

void maybeUpdateWeatherAnimation(Map<String, dynamic> current) {
  final int weatherCode = current['weather_code'];
  final int isDay = current['is_day'];

  if (_cachedWeatherCode != weatherCode || _cachedIsDay != isDay) {
    weatherAnimationWidget = WeatherConditionAnimationMapper.build(
      weatherCode: weatherCode,
      isDay: isDay,
      context: context,
    );

    _cachedWeatherCode = weatherCode;
    _cachedIsDay = isDay;

  }

  
}




      final Map<String, (DateTime, DateTime)> daylightMap = {
        for (int i = 0; i < dailyDates.length; i++)
          dailyDates[i]: (DateTime.parse(sunriseTimes[i]), DateTime.parse(sunsetTimes[i])),
      };

      bool isHourDuringDaylightOptimized(DateTime hourTime) {
        final key = "${hourTime.year.toString().padLeft(4, '0')}-${hourTime.month.toString().padLeft(2, '0')}-${hourTime.day.toString().padLeft(2, '0')}";
        final times = daylightMap[key];
        if (times != null) {
          return hourTime.isAfter(times.$1) && hourTime.isBefore(times.$2);
        }
        return true; 
      }




      final int weatherCode = current['weather_code'] ?? 0;
      final bool isDay = current['is_day'] == 1;

    final useFullMaterialScheme = PreferencesHelper.getBool("OnlyMaterialScheme") ?? false;





    String formattedTime = lastUpdated != null
        ? _formatLastUpdated(lastUpdated)
        : 'Unknown';
        final int newIndex = isDay
          ? dayGradients[weatherCode] ?? 0
          : nightGradients[weatherCode] ?? 0;

if (lastWeatherCode != weatherCode || lastIsDay != isDay) {


        lastWeatherCode = weatherCode;
        lastIsDay = isDay;

        WidgetsBinding.instance.addPostFrameCallback((_) {
          if (mounted) {
            // setState(() {
             !useFullMaterialScheme ? selectedGradientIndex = newIndex : null;
             !useFullMaterialScheme ? selectedSearchBgIndex = newIndex : null;
             !useFullMaterialScheme ? selectedContainerBgIndex = newIndex : null;
             !useFullMaterialScheme ? selectedConditionColorIndex = newIndex : null;
            // });
              _isLoadingFroggy = true;
              showInsightsRandomly = Random().nextInt(100) < 40;
              PreferencesHelper.setColor("weatherThemeColor", weatherConditionColors[newIndex]);
               maybeUpdateWeatherAnimation(current);
            _loadWeatherIconFroggy(weatherCodeFroggy, isDayFroggy, newIndex);

          }
        });
} else{

  _isLoadingFroggy == true;
  _loadWeatherIconFroggy(weatherCodeFroggy, isDayFroggy, newIndex);

}




    final double? alderPollen = weather['air_quality']['current']['alder_pollen'];
    final double? birchPollen = weather['air_quality']['current']['birch_pollen'];
    final double? grassPollen = weather['air_quality']['current']['grass_pollen'];
    final double? mugwortPollen = weather['air_quality']['current']['mugwort_pollen'];
    final double? olivePollen = weather['air_quality']['current']['olive_pollen'];
    final double? ragweedPollen = weather['air_quality']['current']['ragweed_pollen'];

const double rainThreshold = 0.5;
const int probThreshold = 40;
    int offsetSeconds = int.parse(weather['utc_offset_seconds'].toString());
    DateTime utcNow = DateTime.now().toUtc();
    DateTime nowPrecip = utcNow.add(Duration(seconds: offsetSeconds));


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



final List<String> allTimeStrings = (hourly['time'] as List?)?.cast<String>() ?? [];
final List<double> allPrecip = (hourly['precipitation'] as List?)?.map((e) => (e as num?)?.toDouble() ?? 0.0).toList() ?? [];
final List<int> allPrecipProb = (hourly['precipitation_probability'] as List?)?.map((e) => (e as num?)?.toInt() ?? 0).toList() ?? [];

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

final List<double> next2hPrecip = [];

for (int i = 0; i < timeNext12h.length; i++) {
  final time = DateTime.parse(timeNext12h[i]);
  if (time.isBefore(nowPrecip.add(Duration(hours: 2)))) {
    next2hPrecip.add(precpNext12h[i]);
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

final bool shouldShowRainBlock = bestStart != null && bestEnd != null;


Widget buildLayoutBlock(LayoutBlockType type) {
  switch (type) {
    case LayoutBlockType.rain:
      return shouldShowRainBlock ?
          RainBlock(
            key: const ValueKey('RainBlock'),
            hourlyTime: (hourly['time'] as List).cast<String>(),
            hourlyPrecp: (hourly['precipitation'] as List).cast<double>(),
            hourlyPrecpProb: hourly['precipitation_probability'],
            selectedContainerBgIndex: useFullMaterialScheme ? Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32() : weatherContainerColors[selectedContainerBgIndex],
            timezone: weather['timezone'].toString(),
            utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
          ) : const SizedBox.shrink();

    case LayoutBlockType.insights:
      return !shouldShowRainBlock &&
           showInsightsRandomly ?
            ShowInsights(
               key: const ValueKey('ShowInsights'),
              hourlyData: convertToListOfMaps(weather['hourly']),
              dailyData: convertToListOfMaps(weather['daily']),
              currentData: [Map<String, dynamic>.from(weather['current'])],
              selectedContainerBgIndex: useFullMaterialScheme ? Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32() : weatherContainerColors[selectedContainerBgIndex],
            )
          : const SizedBox.shrink();

    case LayoutBlockType.summary:
      return SummaryCard(
            selectedContainerBgIndex: useFullMaterialScheme ? Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32() : weatherContainerColors[selectedContainerBgIndex],
            hourlyData: hourly,
            dailyData: daily,
            currentData: current,
            airQualityData: weather['air_quality'],
            
            );

    case LayoutBlockType.hourly:
      return HourlyCard(hourlyTime: hourlyTime,
            hourlyTemps: hourlyTemps,
            hourlyWeatherCodes: hourlyWeatherCodes,
            isHourDuringDaylightOptimized: isHourDuringDaylightOptimized,
            selectedContainerBgIndex: useFullMaterialScheme ? Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32() : weatherContainerColors[selectedContainerBgIndex],
            timezone: weather['timezone'].toString(),
            utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
            hourlyPrecpProb: hourlyPrecpProb,
          );

    case LayoutBlockType.daily:
      return DailyCard(dailyTime: dailyDates, 
            dailyTempsMin: dailyTempsMin, 
            dailyWeatherCodes: dailyWeatherCodes, 
            dailyTempsMax: dailyTempsMax, 
            dailyPrecProb: dailyPrecProb, 
            selectedContainerBgIndex: useFullMaterialScheme ?  Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32() : weatherContainerColors[selectedContainerBgIndex]);
         
    case LayoutBlockType.conditions:
    return SizedBox(
              // width: 380,
              child: ConditionsWidgets(
                selectedContainerBgIndex: useFullMaterialScheme ? Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32() : weatherContainerColors[selectedContainerBgIndex],
                currentHumidity: current['relative_humidity_2m'] ?? 0.0000001,
                currentDewPoint: hourly['dew_point_2m'][getStartIndex(weather['utc_offset_seconds'].toString(), hourlyTime)].toDouble() ?? 0.0000001,
                currentSunrise: daily['sunrise'][0] ?? 0.0000001,
                currentSunset: daily['sunset'][0] ?? 0.0000001,
                currentPressure: current['pressure_msl'] ?? 0.0000001,
                currentVisibility: hourly['visibility'][getStartIndex(weather['utc_offset_seconds'].toString(), hourlyTime)] ?? 0.0000001,
                currentWindSpeed: current['wind_speed_10m'] ?? 0.0000001,
                currentWindDirc: current['wind_direction_10m'] ?? 0.0000001,
                timezone: weather['timezone'].toString(),
                utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                currentUvIndex: hourly['uv_index'][getStartIndex(weather['utc_offset_seconds'].toString(), hourlyTime)] ?? 0.0000001,
                currentAQIUSA: weather['air_quality']['current']['us_aqi'] ?? 0.0000001,
                currentAQIEURO: weather['air_quality']['current']['european_aqi'] ?? 0.0000001,
                currentTotalPrec: daily['precipitation_sum'][0] ?? 0.0000001,
                currentDayLength: daily['daylight_duration'][0] ?? 0.0000001,
                isFromHome: true,
              ),
          );

    case LayoutBlockType.pollen:
      return      _isPollenDataAvailable([
          alderPollen,
          birchPollen,
          olivePollen,
          grassPollen,
          mugwortPollen,
          ragweedPollen,
        ])
            ? Column(
                children: [
                  PollenCard(
                    pollenData: weather['air_quality']['current'],
                    selectedContainerBgIndex: useFullMaterialScheme
                        ? Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32()
                        : weatherContainerColors[selectedContainerBgIndex],
                  ),
                  const SizedBox(height: 8.5),
                ],
              )
            : const SizedBox.shrink();
  }
}




    return Column(
      children: [
            Stack(
            clipBehavior: Clip.none,
            children: [
           
        if (weatherAnimationWidget != null)
         useFullMaterialScheme ? const SizedBox.shrink() : usAnimations ? weatherAnimationWidget! : const SizedBox.shrink()
        else
          const SizedBox.shrink(),

      GestureDetector(
          onTap: () async {
            final result =
                await Navigator.of(context).push<Map<String, dynamic>>(
              PageRouteBuilder(
                opaque: true,
                reverseTransitionDuration: Duration(milliseconds: 200),
                pageBuilder: (context, animation, secondaryAnimation) {
              return const LocationsScreen();
            },
            transitionsBuilder: (context, animation, secondaryAnimation, child) {
              return FadeTransition(
                opacity: animation,
                child: child,
                
              );
            }, 
        )
            );

            if (result != null) {
            final newCity = result['city'];
            final newCountry = result['country'];
            final newCacheKey = result['cacheKey'];
            final newLat = result['latitude'] ?? result['lat'];
            final newLon = result['longitude'] ?? result['lon'];



            final isDifferent = cityName != newCity ||
                                countryName != newCountry ||
                                cacheKey != newCacheKey ||
                                lat != newLat ||
                                lon != newLon;

   
            if (isDifferent) {
              setState(() {
                cityName = newCity;
                countryName = newCountry;
                cacheKey = newCacheKey;
                lat = newLat;
                lon = newLon;
                _isAppFullyLoaded = false;
                _istriggeredFromLocations = true;
                themeCalled = false;
                _isLoadingFroggy = true;

              });

              weatherFuture = getWeatherFromCache();
            }
          }

          },
            child:  Container(
                width: double.infinity,
                margin: const EdgeInsets.only(left: 16, right: 16),
                height: 56,
                padding: const EdgeInsets.only(left: 10, right: 10),
                decoration: BoxDecoration(
                  color: !useFullMaterialScheme ? searchBgColors[selectedSearchBgIndex] : Color(Theme.of(context).colorScheme.surfaceContainerHigh.toARGB32()),
                  borderRadius: BorderRadius.circular(50),
                ),
                child: Row(
                  children: [
                    Expanded( 
                      child: Row(
                        children: [
                          CircleAvatar(
                            radius: 20,
                            backgroundColor: Colors.transparent,
                            child: Icon(
                              Icons.location_on_outlined,
                              color: Theme.of(context).colorScheme.onSurfaceVariant,
                            ),
                          ),
                          // const SizedBox(width: 8),
                          Expanded( 
                            child: Text(
                              "$cityName, $countryName",
                              style: TextStyle(
                                color: Theme.of(context).colorScheme.onSurfaceVariant,
                                fontSize: 18,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                        ],
                      ),
                    ),
                    IconButton(
                      onPressed: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(builder: (_) => const SettingsScreen()),
                        );
                      },
                      icon: const Icon(Icons.settings_outlined),
                    ),

                  ],
                ),
              )
              )
            ],
            
            ),

            
         const SizedBox(height: 10,),
               WeatherTopCard(
                  currentTemp: current['temperature_2m'].toDouble(),
                  currentFeelsLike: current['apparent_temperature'].toDouble(),
                  currentMaxTemp: weather['daily']?['temperature_2m_max']?[0]?.toDouble() ?? 0,
                  currentMinTemp: weather['daily']?['temperature_2m_min']?[0]?.toDouble() ?? 0,
                  currentWeatherIconCode: current['weather_code'],
                  currentisDay: current['is_day'],
                  currentLastUpdated: formattedTime,
                ),
        WeatherFrogIconWidget(iconUrl: _iconUrlFroggy),
         const SizedBox(height: 12),
      Column(
        children: () {
          final visibleBlocks = layoutConfig.where((block) => block.isVisible).toList();

          final List<Widget> children = [];
          for (int i = 0; i < visibleBlocks.length; i++) {
            final currentBlock = visibleBlocks[i];
            children.add(buildLayoutBlock(currentBlock.type));

            final isRainThenInsights = currentBlock.type == LayoutBlockType.rain &&
                i + 1 < visibleBlocks.length &&
                visibleBlocks[i + 1].type == LayoutBlockType.insights;

            if (!isRainThenInsights && i < visibleBlocks.length - 1) {
              children.add(const SizedBox(height: 8.5));
            }
          }
          return children;
        }(),
      ),


      

            Container(
              width: double.infinity,
              padding: EdgeInsets.only(bottom: MediaQuery.of(context).padding.bottom + 5, top: 16),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.only(topRight: Radius.circular(28), topLeft: Radius.circular(28)),
                color:  Color(useFullMaterialScheme ? Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32() : weatherContainerColors[selectedContainerBgIndex]),
              ),
              child: Center(
                child: Text("Data provided by Open-Meteo"),
              ),
            )

            ]
          );  
        }

      );

    
  }
}


class ScrollReactiveGradient extends StatefulWidget {
  final ScrollController scrollController;
  final LinearGradient baseGradient;
  final LinearGradient scrolledGradient;
  final ValueNotifier<bool>? headerVisibilityNotifier;

  const ScrollReactiveGradient({
    required this.scrollController,
    required this.baseGradient,
    required this.scrolledGradient,
    this.headerVisibilityNotifier,
    super.key,
  });

  @override
  State<ScrollReactiveGradient> createState() => _ScrollReactiveGradientState();
}

class _ScrollReactiveGradientState extends State<ScrollReactiveGradient> {
  bool _isScrolled = false;

  @override
  void initState() {
    super.initState();
    widget.scrollController.addListener(_onScroll);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _checkScroll();
    });
  }

  @override
  void didUpdateWidget(covariant ScrollReactiveGradient oldWidget) {
    super.didUpdateWidget(oldWidget);
    _checkScroll(); 
  }

  void _onScroll() {
    _checkScroll();
  }

  void _checkScroll() {
    final isNowScrolled = widget.scrollController.offset > 300;
    if (_isScrolled != isNowScrolled) {
      setState(() {
        _isScrolled = isNowScrolled;

      });

if (widget.headerVisibilityNotifier != null &&
        widget.headerVisibilityNotifier!.value != isNowScrolled) {
      widget.headerVisibilityNotifier!.value = isNowScrolled;
    }
    }

  }

  @override
  void dispose() {
    widget.scrollController.removeListener(_onScroll);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {

    final useFullMaterialScheme = PreferencesHelper.getBool("OnlyMaterialScheme") ?? false;

    return Stack(
      children: [
        AnimatedOpacity(
          duration: const Duration(milliseconds: 0),
          opacity: _isScrolled ? 0 : 1,
          child: Container(
            decoration:!useFullMaterialScheme ? BoxDecoration(
              gradient: widget.baseGradient,
            ) : BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainerLow
            )
          ),
        ),
        AnimatedOpacity(
          duration: const Duration(milliseconds: 0),
          opacity: _isScrolled ? 1 : 0,
          child: Container(
            decoration: !useFullMaterialScheme ? BoxDecoration(
              gradient: widget.scrolledGradient,
            ) : BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainerLow
            )
          ),
        ),
      ],
    );
  }
}


List<Map<String, dynamic>> convertToListOfMaps(Map<String, dynamic> data) {
  final length = (data.values.first as List).length;
  return List.generate(length, (index) {
    return data.map((key, valueList) => MapEntry(key, valueList[index]));
  });
}



class WeatherFrogIconWidget extends StatelessWidget {
  final String? iconUrl;

  const WeatherFrogIconWidget({super.key, required this.iconUrl});

  @override
  Widget build(BuildContext context) {
    if (iconUrl == null) {
      return const Text("");
    }
    
    final isShowFrog = context.read<UnitSettingsNotifier>().showFrog;

    return isShowFrog ? iconUrl!.startsWith('http')
        ? Image.network(
            iconUrl!,
            loadingBuilder: (context, child, loadingProgress) {
              if (loadingProgress == null) return child;
              return const Text("Loading...");
            },
          )
        : Image.asset(iconUrl!) : SizedBox.shrink() ;
  }
}

int getStartIndex(utc_offset_seconds, hourlyTime) {
    final offset = Duration(seconds: int.parse(utc_offset_seconds));
    final nowUtc = DateTime.now().toUtc();
    final nowLocal = nowUtc.add(offset);

    final timeUnit = PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';


    final roundedNow = DateTime(nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

    int startIndex = hourlyTime.indexWhere((timeStr) {
      final forecastLocal = DateTime.parse(timeStr); 
      return !forecastLocal.isBefore(roundedNow);
    });

    if (startIndex == -1) startIndex = 0;

    return startIndex;
}

  bool _isPollenDataAvailable(List<double?> values) {
    return values.every((value) => value != null);
  }
