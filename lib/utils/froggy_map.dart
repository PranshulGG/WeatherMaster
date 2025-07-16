import 'dart:math';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;

class WeatherFroggyManager {
  List<String> sunnyFrog = [];
  List<String> mostlySunnyFrog = [];
  List<String> partlyCloudyFrog = [];
  List<String> overcastFrog = [];
  List<String> fogFrog = [];
  List<String> rainFrog = [];
  List<String> snowFrog = [];
  List<String> thunderStormFrog = [];
  List<String> clearNightFrog = [];
  List<String> partlyCloudyNightFrog = [];

Future<bool> isOnline() async {
  try {
    if (kIsWeb) return true; 

    final response = await http.get(Uri.parse('https://www.google.com'))
        .timeout(Duration(seconds: 5));
    return response.statusCode == 200;
  } catch (e) {
    return false;
  }


}

Future<void> initializeIcons() async {
  final online = await isOnline();
  if (online) {
    _setOnlineIcons();

  } else {
    _setOfflineIcons();
  }

  Connectivity().onConnectivityChanged.listen((event) async {
    if (await isOnline()) {
      _setOnlineIcons();
    } else {
      _setOfflineIcons();
    }
  });
}


  void _setOnlineIcons() {
    sunnyFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-creek-swimming.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-home-laundry_c.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-orchard-picking.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-home-laundry_f.png?ref_type=heads"
    ];

    mostlySunnyFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-citypark-picnic.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-beach-sunscreen.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-home-laundry.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-beach-sandcastle.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-rooftop-pinacolada.png?ref_type=heads"
    ];

    partlyCloudyFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-field-hiking_f.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-home-flowers.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-creek-feet.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-field-biking_c.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-citypark-ukelele.png?ref_type=heads"
    ];

    overcastFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/09-cloudy/09-cloudy-home-flowers.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/09-cloudy/09-cloudy-orchard-watching.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/04-mostly-cloudy-day/04-mostly-cloudy-day-home-flowers.png?ref_type=heads"
    ];

    fogFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-bridge.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-fruit-stand.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-mountain.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-pier.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-field-lantern.png?ref_type=heads"
    ];

    rainFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/11-rain/11-rain-creek-leaf.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/11-rain/11-rain-home-inside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/04-mostly-cloudy-day/04-mostly-cloudy-day-orchard-treeswing_f.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/12-heavy-rain/12-heavy-rain-busstop-umbrella.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/12-heavy-rain/12-heavy-rain-cafe-sitting-singing.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/11-rain/11-shower-rain-field-leaf.png?ref_type=heads"
    ];

    snowFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/15-snow-showers-snow/15-snow-showers-snow-citypark-snowman.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/15-snow-showers-snow/15-snow-showers-snow-home-shoveling.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/15-snow-showers-snow/15-snow-showers-snow-creek-iceskating.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/17-heavy-snow-blizzard/17-heavy-snow-blizzard-home-inside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/16-blowing-snow/16-blowing-snow-field-snowman.png?ref_type=heads"
    ];

    thunderStormFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/22-iso-thunderstorms/22-iso-thunderstorms-home-inside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/22-iso-thunderstorms/22-iso-thunderstorms-cafe-looking-outside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/22-iso-thunderstorms/22-iso-thunderstorms-busstop-newspaper.png?ref_type=heads"
    ];

    clearNightFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-creek-stars.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-field-lanterns.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-orchard-fireflies.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-home-lounging.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-hills-telescope.png?ref_type=heads"
    ];

    partlyCloudyNightFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-creek-fireflies.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-field-fireflies.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-hills-smores.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-orchard-eating.png?ref_type=heads"
    ];


  }

  void _setOfflineIcons() {
    sunnyFrog = ['assets/froggie/01d.png'];
    mostlySunnyFrog = ['assets/froggie/02d.png'];
    partlyCloudyFrog = ['assets/froggie/03d.png'];
    overcastFrog = ['assets/froggie/04d.png'];
    fogFrog = ['assets/froggie/50d.png'];
    rainFrog = ['assets/froggie/09d.png'];
    snowFrog = ['assets/froggie/13d.png'];
    thunderStormFrog = ['assets/froggie/11d.png'];
    clearNightFrog = ['assets/froggie/01n.png'];
    partlyCloudyNightFrog = ['assets/froggie/02n.png'];
  }

    final Random _random = Random();

  String getFroggieIcon(int iconCode, bool isDay) {
    if (isDay) {
      if (iconCode == 0) {
        return _randomIcon(sunnyFrog);
      } else if (iconCode == 1) {
        return _randomIcon(mostlySunnyFrog);
      } else if (iconCode == 2) {
        return _randomIcon(partlyCloudyFrog);
      } else if (iconCode == 3) {
        return _randomIcon(overcastFrog);
      } else if ([45, 48].contains(iconCode)) {
        return _randomIcon(fogFrog);
      } else if ([51, 53, 55, 56, 57, 61, 63, 65, 80, 81, 82].contains(iconCode)) {
        return _randomIcon(rainFrog);
      } else if ([66, 67, 71, 73, 75, 77, 85, 86].contains(iconCode)) {
        return _randomIcon(snowFrog);
      } else if ([95, 96, 99].contains(iconCode)) {
        return _randomIcon(thunderStormFrog);
      }
    } else {
      if (iconCode == 0) {
        return _randomIcon(clearNightFrog);
      } else if ([1, 2].contains(iconCode)) {
        return _randomIcon(partlyCloudyNightFrog);
      } else if (iconCode == 3){
        return _randomIcon(overcastFrog);
      } else if ([45, 48].contains(iconCode)) {
        return _randomIcon(fogFrog);
      } else if ([51, 53, 55, 56, 57, 61, 63, 65, 80, 81, 82].contains(iconCode)) {
        return _randomIcon(rainFrog);
      } else if ([66, 67, 71, 73, 75, 77, 85, 86].contains(iconCode)) {
        return _randomIcon(snowFrog);
      } else if ([95, 96, 99].contains(iconCode)) {
        return _randomIcon(thunderStormFrog);
      }
    }

    return 'assets/froggie/unknown.png';
  }

  String _randomIcon(List<String> list) {
    if (list.isEmpty) return 'assets/froggie/unknown.png';
    return list[_random.nextInt(list.length)];
  }
}
