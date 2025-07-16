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
import '../widgets/currentConditions_card.dart';
import '../widgets/rain_block.dart';


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


  late bool isHomeLocation;
  final ScrollController _scrollController = ScrollController();

  bool _isAppFullyLoaded = false;

  late String cityName;
  late String countryName;
  late String cacheKey;
  int selectedGradientIndex = 2;
  int selectedSearchBgIndex = 2;
  int selectedContainerBgIndex = 2;
  int selectedConditionColorIndex = 2;


  int? lastWeatherCode;
  int? _lastSetThemeIndex;

  bool? lastIsDay;

    final WeatherFroggyManager _weatherManager = WeatherFroggyManager();

    String? _iconUrlFroggy;
  bool _isLoadingFroggy = true;


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
  } else {
    _setLatLon();
  }

}


  Future<Map<String, dynamic>?> getWeatherFromCache() async {
    final box = await Hive.openBox('weatherMasterCache');
    final cached = box.get(cacheKey);
    if (cached == null) return null;
    return json.decode(cached);
  }





Future<void> _loadWeatherIconFroggy(int weatherCode, bool isDay) async {
  await _weatherManager.initializeIcons();
  final icon = _weatherManager.getFroggieIcon(weatherCode, isDay);
  if (mounted) {
    setState(() {
      _iconUrlFroggy = icon;
      _isLoadingFroggy = false;
    });
  }
}


Future<void> _refreshWeatherData() async {
  if (lat == null || lon == null) {
    print("Coordinates not loaded yet.");
    return;
  }

  final box = await Hive.openBox('weatherMasterCache');
  final raw = box.get(cacheKey);
  if (raw == null) {
    print("No cached data found.");
    return;
  }

  final cached = json.decode(raw);
  final lastUpdatedStr = cached['last_updated'];
  final lastUpdated = DateTime.tryParse(lastUpdatedStr ?? '');

  if (lastUpdated == null) {
    print("Invalid last_updated format. Proceeding with refresh.");
  } else {
    final now = DateTime.now();
    final difference = now.difference(lastUpdated);
    if (difference.inMinutes < 10) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Weather is already up to date.',)),
      );
      return;
    }
  }

  final weatherService = WeatherService();
  await weatherService.fetchWeather(
    lat!,
    lon!,
    locationName: cacheKey,
  );

  setState(() {
  });
}

        Future<void> _setLatLon() async {
  final prefs = await SharedPreferences.getInstance();
  final jsonString = prefs.getString('currentLocation');
  if (jsonString != null) {
    final jsonMap = json.decode(jsonString);
    setState(() {
      lat = jsonMap['latitude'];
      lon = jsonMap['longitude'];
    });
  }
}


        Future<void> _setLatLonNoState() async {
  final prefs = await SharedPreferences.getInstance();
  final jsonString = prefs.getString('currentLocation');
  if (jsonString != null) {
    final jsonMap = json.decode(jsonString);
      lat = jsonMap['latitude'];
      lon = jsonMap['longitude'];
  }
}





  @override
  Widget build(BuildContext context) {




    

      SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);

        SystemChrome.setSystemUIOverlayStyle(
         SystemUiOverlayStyle(
          statusBarColor: Colors.transparent,
          systemNavigationBarColor: Colors.transparent,
          statusBarIconBrightness: Theme.of(context).brightness == Brightness.light ? Brightness.dark : Brightness.light,
          systemNavigationBarIconBrightness: Theme.of(context).brightness == Brightness.light ? Brightness.dark : Brightness.light,
        ),
      );


  final List<LinearGradient> gradients = [ 

      // cloudy
   Theme.of(context).brightness == Brightness.light ? 
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
  Theme.of(context).brightness == Brightness.light ? 
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
       Theme.of(context).brightness == Brightness.light ? 
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
       Theme.of(context).brightness == Brightness.light ? 
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
      Theme.of(context).brightness == Brightness.light ? 
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
      Theme.of(context).brightness == Brightness.light ? 
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
  Theme.of(context).brightness == Brightness.light ? 
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

      Theme.of(context).brightness == Brightness.light ? 
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
         Theme.of(context).brightness == Brightness.light ? 
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
    Theme.of(context).brightness == Brightness.light ? 
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

           Theme.of(context).brightness == Brightness.light ? 
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
           Theme.of(context).brightness == Brightness.light ? 
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
          Theme.of(context).brightness == Brightness.light ? 
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
        Theme.of(context).brightness == Brightness.light ? 
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
      Theme.of(context).brightness == Brightness.light ? 
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

      Theme.of(context).brightness == Brightness.light ? 
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
      ),
       Scaffold(
        extendBodyBehindAppBar: true,
        backgroundColor: Colors.transparent,
        body: _buildMainBody(),
    ),

    if (!_isAppFullyLoaded)
      Positioned.fill(
        child: Container(
          color: Colors.black,
          child: const Center(
            child: CircularProgressIndicator(year2023: false,),
          ),
        ),
      ),
  ],
);
        



}

String _formatLastUpdated(String isoTime) {
  final dt = DateTime.tryParse(isoTime)?.toLocal();
  if (dt == null) return 'Invalid time';

  final now = DateTime.now();
  final difference = now.difference(dt);

  if (difference.inMinutes < 1) return 'just now';
  if (difference.inMinutes < 60) return '${difference.inMinutes} min. ago';
  if (difference.inHours < 24) return '${difference.inHours} hr. ago';
  return '${dt.month}/${dt.day} at ${dt.hour}:${dt.minute.toString().padLeft(2, '0')}';
}


Widget _buildMainBody() {
  final padding = MediaQuery.of(context).padding;

  return RefreshIndicator(
    onRefresh: () async {
      if (lat != null && lon != null) {
        await _refreshWeatherData();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Location not ready to refresh.")),
        );
      }
    },
    edgeOffset: padding.top + 10,
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

  
      final List<Color> searchBgColors = [
        // cloudy
           Theme.of(context).brightness == Brightness.light ? Color(paletteWeather.secondary.get(150)) : Color(paletteWeather.secondary.get(11)),

          // overcast
         Theme.of(context).brightness == Brightness.light ? Color(0xFFe8f2ff) : Color(paletteWeather.secondary.get(13)),

          // clear day
        Theme.of(context).brightness == Brightness.light ? Color(0xFFe8f2ff) :  Color(paletteWeather.primary.get(10)),

          // clear night
        Theme.of(context).brightness == Brightness.light ? Color(paletteWeather.neutral.get(100)) :  Color(paletteWeather.primary.get(10)),

         // fog  
         Theme.of(context).brightness == Brightness.light ? Color(CorePalette.of(const Color.fromARGB(255, 255, 153, 0).toARGB32()).secondary.get(100)) : Color(CorePalette.of(const Color.fromARGB(255, 255, 153, 0).toARGB32()).secondary.get(20)),
         // rain
         Theme.of(context).brightness == Brightness.light ? Color(0xFFe8f2ff) : Color(paletteWeather.secondary.get(15)),

          // thunder
          Theme.of(context).brightness == Brightness.light ? Color.fromARGB(255, 247, 232, 255) : Color(CorePalette.of(const Color(0xFFe4b7f3).toARGB32()).secondary.get(20)),


          // snow
         Theme.of(context).brightness == Brightness.light ? Color.fromARGB(255, 232, 254, 255) : Color(CorePalette.of(const Color.fromARGB(255, 0, 13, 31).toARGB32()).secondary.get(18)),

      ];


      final List<int> weatherContainerColors = [
        // cloudy
        Theme.of(context).brightness == Brightness.light ? paletteWeather.secondary.get(98) : paletteWeather.secondary.get(12),

          // overcast
         Theme.of(context).brightness == Brightness.light ? 0xFFfcfcff : paletteWeather.secondary.get(6),

          // clear day
         Theme.of(context).brightness == Brightness.light ? 0xFFfcfcff : paletteWeather.primary.get(7),

          // clear night
         Theme.of(context).brightness == Brightness.light ? CorePalette.of(const Color.fromARGB(255, 58, 77, 141).toARGB32()).primary.get(98) : CorePalette.of(const Color.fromARGB(255, 58, 77, 141).toARGB32()).primary.get(5),

         // fog  
         Theme.of(context).brightness == Brightness.light ? CorePalette.of(Color.fromARGB(255, 255, 213, 165).toARGB32()).secondary.get(98) : CorePalette.of(Color.fromARGB(255, 255, 213, 165).toARGB32()).secondary.get(6),

         // rain
         Theme.of(context).brightness == Brightness.light ? 0xFFfcfcff : CorePalette.of(Colors.blueAccent.toARGB32()).secondary.get(8),

          // thunder
         Theme.of(context).brightness == Brightness.light ? CorePalette.of(const Color(0xFFe4b7f3).toARGB32()).secondary.get(96) : CorePalette.of(const Color(0xFFe4b7f3).toARGB32()).secondary.get(10),


          // snow
        Theme.of(context).brightness == Brightness.light ? 0xFFfcfcff : CorePalette.of(const Color.fromARGB(255, 0, 13, 31).toARGB32()).secondary.get(1),

      ];

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



  return  FutureBuilder<Map<String, dynamic>?>(
      future: getWeatherFromCache(),
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

      if (_iconUrlFroggy == null && _isLoadingFroggy) {
  WidgetsBinding.instance.addPostFrameCallback((_) {
    _loadWeatherIconFroggy(weatherCodeFroggy, isDayFroggy);
  });
}

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


_setLatLonNoState();

  if(lat == PreferencesHelper.getJson('homeLocation')?['lat'] && lon == PreferencesHelper.getJson('homeLocation')?['lon']){
    isHomeLocation = true;
  } else{
    isHomeLocation = false;
  }


    // if (isHomeLocation && lastUpdated != null) {
    //   final lastUpdateTime = DateTime.tryParse(lastUpdated);
    //   final now = DateTime.now();

    //   if (lastUpdateTime != null && now.difference(lastUpdateTime).inMinutes > 40) {
    //     WidgetsBinding.instance.addPostFrameCallback((_) {
    //       _refreshWeatherData(); 
    //     });
    //   }
    //   }  else{

    //   }


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



      final int newIndex = isDay
          ? dayGradients[weatherCode] ?? 0
          : nightGradients[weatherCode] ?? 0;

      if (lastWeatherCode != weatherCode || lastIsDay != isDay) {
        lastWeatherCode = weatherCode;
        lastIsDay = isDay;

        WidgetsBinding.instance.addPostFrameCallback((_) {
          if (mounted) {
            setState(() {
              selectedGradientIndex = newIndex;
              selectedSearchBgIndex = newIndex;
              selectedContainerBgIndex = newIndex;
              selectedConditionColorIndex = newIndex;
              _isLoadingFroggy = true;
            });
             _loadWeatherIconFroggy(weatherCode, isDay);
          }
        });
      }

    String formattedTime = lastUpdated != null
        ? _formatLastUpdated(lastUpdated)
        : 'Unknown';

              if (!_isAppFullyLoaded && !_isLoadingFroggy) {
        WidgetsBinding.instance.addPostFrameCallback((_) {
          if (mounted) {
            setState(() {
              _isAppFullyLoaded = true;
            });

          }
        });
      }


// if (!_hasSetThemeColor) {
//   WidgetsBinding.instance.addPostFrameCallback((_) {
//     if (mounted) {
//       Provider.of<ThemeController>(context, listen: false).setSeedColor(weatherConditionColors[selectedConditionColorIndex]);
//       _hasSetThemeColor = true;
//     }
//   });
// }

if (_lastSetThemeIndex != selectedConditionColorIndex) {
  WidgetsBinding.instance.addPostFrameCallback((_) {
    if (mounted) {
if (!(PreferencesHelper.getBool("usingCustomSeed") ?? false)) {
      Provider.of<ThemeController>(context, listen: false)
          .setSeedColor(weatherConditionColors[selectedConditionColorIndex]);
      _lastSetThemeIndex = selectedConditionColorIndex;
    }
    }
    PreferencesHelper.setColor("weatherThemeColor", weatherConditionColors[selectedConditionColorIndex]);
  });
}




const double rainThreshold = 0.5;

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



final List<String> allTimeStrings = (hourly['time'] as List).cast<String>();
final List<double> allPrecip = (hourly['precipitation'] as List).cast<double>();

final List<String> timeNext12h = [];
final List<double> precpNext12h = [];

for (int i = 0; i < allTimeStrings.length; i++) {
  final time = DateTime.parse(allTimeStrings[i]);
  if (time.isAfter(nowPrecip) && time.isBefore(nowPrecip.add(Duration(hours: 12)))) {
    timeNext12h.add(allTimeStrings[i]);
    precpNext12h.add(allPrecip[i]);
  }
}

final List<double> next2hPrecip = [];

for (int i = 0; i < timeNext12h.length; i++) {
  final time = DateTime.parse(timeNext12h[i]);
  if (time.isBefore(nowPrecip.add(Duration(hours: 2)))) {
    next2hPrecip.add(precpNext12h[i]);
  }
}

final bool shouldShowRainBlock = next2hPrecip.any((p) => p >= rainThreshold);



    return Column(
      children: [
            Stack(
            clipBehavior: Clip.none,
            children: [
               WeatherConditionAnimationMapper.build(
              weatherCode: current['weather_code'], 
              isDay: current['is_day'],
              context: context,
            ),
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
              setState(() {
                cityName = result['city'];
                countryName = result['country'];
                cacheKey = result['cacheKey'];
                lat = result['lat']; 
                lon = result['lon'];
              });
            }
          },
            child:  Container(
                width: double.infinity,
                margin: const EdgeInsets.only(left: 16, right: 16),
                height: 56,
                padding: const EdgeInsets.only(left: 10, right: 10),
                decoration: BoxDecoration(
                  color: searchBgColors[selectedSearchBgIndex],
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
            Container(
              child:  _isLoadingFroggy
            ? const Text("Loading...")
            : _iconUrlFroggy != null
                ? _iconUrlFroggy!.startsWith('http')
                    ? Image.network(_iconUrlFroggy!)
                    : Image.asset(_iconUrlFroggy!)
                : const Text("No icon available"),
            ),
            // TextButton(onPressed:() {
            //   Provider.of<ThemeController>(context, listen: false).setSeedColor(const Color(0xFFe4b7f3));
            // } , child: Text("${current['weather_code']}")),


         const SizedBox(height: 10),

        if (shouldShowRainBlock)
          RainBlock(
            hourlyTime: (hourly['time'] as List).cast<String>(),
            hourlyPrecp: (hourly['precipitation'] as List).cast<double>(),
            selectedContainerBgIndex: weatherContainerColors[selectedContainerBgIndex],
            timezone: weather['timezone'].toString(),
            utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
          ),
          SummaryCard(
            selectedContainerBgIndex: weatherContainerColors[selectedContainerBgIndex],
            hourlyData: hourly,
            dailyData: daily,
            currentData: current,
            airQualityData: weather['air_quality']
            ),
         const SizedBox(height: 8.5),
          HourlyCard(hourlyTime: hourlyTime,
            hourlyTemps: hourlyTemps,
            hourlyWeatherCodes: hourlyWeatherCodes,
            isHourDuringDaylightOptimized: isHourDuringDaylightOptimized,
            selectedContainerBgIndex: weatherContainerColors[selectedContainerBgIndex],
            timezone: weather['timezone'].toString(),
            utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
            hourlyPrecpProb: hourlyPrecpProb,
            ),
         const SizedBox(height: 8.5),
          DailyCard(dailyTime: dailyDates, 
            dailyTempsMin: dailyTempsMin, 
            dailyWeatherCodes: dailyWeatherCodes, 
            dailyTempsMax: dailyTempsMax, 
            dailyPrecProb: dailyPrecProb, 
            selectedContainerBgIndex: weatherContainerColors[selectedContainerBgIndex]),
          SizedBox(
              height: 800,
              width: 380,
              child: ConditionsWidgets(
                selectedContainerBgIndex: weatherContainerColors[selectedContainerBgIndex],
                currentHumidity: current['relative_humidity_2m'],
                currentDewPoint: hourly['dew_point_2m'][0].toDouble(),
                currentSunrise: daily['sunrise'][0],
                currentSunset: daily['sunset'][0],
                currentPressure: current['pressure_msl'],
                currentVisibility: hourly['visibility'][0],
                currentWindSpeed: current['wind_speed_10m'],
                currentWindDirc: current['wind_direction_10m'],
                timezone: weather['timezone'].toString(),
                utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                currentUvIndex: hourly['uv_index'][0],
                currentAQIUSA: weather['air_quality']['current']['us_aqi'],
                currentAQIEURO: weather['air_quality']['current']['european_aqi'],
                currentTotalPrec: daily['precipitation_sum'][0],
              ),
            ),


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

  const ScrollReactiveGradient({
    required this.scrollController,
    required this.baseGradient,
    required this.scrolledGradient,
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
    }
  }

  @override
  void dispose() {
    widget.scrollController.removeListener(_onScroll);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        AnimatedOpacity(
          duration: const Duration(milliseconds: 0),
          opacity: _isScrolled ? 0 : 1,
          child: Container(
            decoration: BoxDecoration(
              gradient: widget.baseGradient,
            ),
          ),
        ),
        AnimatedOpacity(
          duration: const Duration(milliseconds: 0),
          opacity: _isScrolled ? 1 : 0,
          child: Container(
            decoration: BoxDecoration(
              gradient: widget.scrolledGradient,
            ),
          ),
        ),
      ],
    );
  }
}


