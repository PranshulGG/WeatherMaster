class WeatherIconMapper {
  static const _defaultIcon = 'assets/weather-icons/not_available.svg';

  static const _hazeFogDustSmoke =
      'assets/weather-icons/haze_fog_dust_smoke.svg';
  static const _drizzle = 'assets/weather-icons/drizzle.svg';
  static const _mixedRainHailSleet =
      'assets/weather-icons/mixed_rain_hail_sleet.svg';
  static const _showersRain = 'assets/weather-icons/showers_rain.svg';
  static const _heavyRain = 'assets/weather-icons/heavy_rain.svg';
  static const _mixedRainSnow = 'assets/weather-icons/mixed_rain_snow.svg';
  static const _showersSnow = 'assets/weather-icons/showers_snow.svg';
  static const _heavySnow = 'assets/weather-icons/heavy_snow.svg';
  static const _strongThunderstorms =
      'assets/weather-icons/strong_thunderstorms.svg';

  static const Map<String, String> _iconMap = {
    // 0: Clear sky
    '0_1': 'assets/weather-icons/clear_day.svg',
    '0_0': 'assets/weather-icons/clear_night.svg',

    // 1,2,3: Mainly clear, partly cloudy, overcast
    '1_1': 'assets/weather-icons/mostly_clear_day.svg',
    '1_0': 'assets/weather-icons/mostly_clear_night.svg',
    '2_1': 'assets/weather-icons/partly_cloudy_day.svg',
    '2_0': 'assets/weather-icons/partly_cloudy_night.svg',
    '3_1': 'assets/weather-icons/cloudy.svg',
    '3_0': 'assets/weather-icons/cloudy.svg',

    // 45,48: Fog
    '45_1': _hazeFogDustSmoke,
    '45_0': _hazeFogDustSmoke,
    '48_1': _hazeFogDustSmoke,
    '48_0': _hazeFogDustSmoke,

    // 51,53,55: Drizzle
    '51_1': _drizzle,
    '51_0': _drizzle,
    '53_1': _drizzle,
    '53_0': _drizzle,
    '55_1': _drizzle,
    '55_0': _drizzle,

    // 56,57: Freezing Drizzle
    '56_1': _mixedRainHailSleet,
    '56_0': _mixedRainHailSleet,
    '57_1': _mixedRainHailSleet,
    '57_0': _mixedRainHailSleet,

    // 61,63,65: Rain
    '61_1': 'assets/weather-icons/scattered_showers_day.svg',
    '61_0': 'assets/weather-icons/scattered_showers_night.svg',
    '63_1': _showersRain,
    '63_0': _showersRain,
    '65_1': _heavyRain,
    '65_0': _heavyRain,

    // 66,67: Freezing Rain
    '66_1': _mixedRainSnow,
    '66_0': _mixedRainSnow,
    '67_1': _mixedRainSnow,
    '67_0': _mixedRainSnow,

    // 71,73,75: Snowfall
    '71_1': 'assets/weather-icons/scattered_snow_showers_day.svg',
    '71_0': 'assets/weather-icons/scattered_snow_showers_night.svg',
    '73_1': _showersSnow,
    '73_0': _showersSnow,
    '75_1': _heavySnow,
    '75_0': _heavySnow,

    // 77: Snow grains
    '77_1': 'assets/weather-icons/flurries.svg',
    '77_0': 'assets/weather-icons/flurries.svg',

    // 80,81,82: Rain showers
    '80_1': 'assets/weather-icons/scattered_showers_day.svg',
    '80_0': 'assets/weather-icons/scattered_showers_night.svg',
    '81_1': _showersRain,
    '81_0': _showersRain,
    '82_1': _heavyRain,
    '82_0': _heavyRain,

    // 85,86: Snow showers
    '85_1': _showersSnow,
    '85_0': _showersSnow,
    '86_1': _heavySnow,
    '86_0': _heavySnow,

    // 95: Thunderstorm (no hail)
    '95_1': 'assets/weather-icons/isolated_thunderstorms.svg',
    '95_0': 'assets/weather-icons/isolated_thunderstorms.svg',

    // 96,99: Thunderstorm with hail
    '96_1': _strongThunderstorms,
    '96_0': _strongThunderstorms,
    '99_1': _strongThunderstorms,
    '99_0': _strongThunderstorms,
  };

  static String getIcon(int? weatherCode, int? isDay) {
    if (weatherCode == null || isDay == null) {
      return _defaultIcon;
    }

    final key = '${weatherCode}_$isDay';
    return _iconMap[key] ?? _defaultIcon;
  }
}
