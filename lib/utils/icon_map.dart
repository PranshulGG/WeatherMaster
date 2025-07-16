class WeatherIconMapper {
  static const _defaultIcon = 'assets/weather-icons/not_available.svg';

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
    '45_1': 'assets/weather-icons/haze_fog_dust_smoke.svg',
    '45_0': 'assets/weather-icons/haze_fog_dust_smoke.svg',
    '48_1': 'assets/weather-icons/haze_fog_dust_smoke.svg',
    '48_0': 'assets/weather-icons/haze_fog_dust_smoke.svg',

    // 51,53,55: Drizzle
    '51_1': 'assets/weather-icons/drizzle.svg',
    '51_0': 'assets/weather-icons/drizzle.svg',
    '53_1': 'assets/weather-icons/drizzle.svg',
    '53_0': 'assets/weather-icons/drizzle.svg',
    '55_1': 'assets/weather-icons/drizzle.svg',
    '55_0': 'assets/weather-icons/drizzle.svg',

    // 56,57: Freezing Drizzle
    '56_1': 'assets/weather-icons/mixed_rain_hail_sleet.svg',
    '56_0': 'assets/weather-icons/mixed_rain_hail_sleet.svg',
    '57_1': 'assets/weather-icons/mixed_rain_hail_sleet.svg',
    '57_0': 'assets/weather-icons/mixed_rain_hail_sleet.svg',

    // 61,63,65: Rain
    '61_1': 'assets/weather-icons/scattered_showers_day.svg',
    '61_0': 'assets/weather-icons/scattered_showers_night.svg',
    '63_1': 'assets/weather-icons/showers_rain.svg',
    '63_0': 'assets/weather-icons/showers_rain.svg',
    '65_1': 'assets/weather-icons/heavy_rain.svg',
    '65_0': 'assets/weather-icons/heavy_rain.svg',

    // 66,67: Freezing Rain
    '66_1': 'assets/weather-icons/mixed_rain_snow.svg',
    '66_0': 'assets/weather-icons/mixed_rain_snow.svg',
    '67_1': 'assets/weather-icons/mixed_rain_snow.svg',
    '67_0': 'assets/weather-icons/mixed_rain_snow.svg',

    // 71,73,75: Snowfall
    '71_1': 'assets/weather-icons/scattered_snow_showers_day.svg',
    '71_0': 'assets/weather-icons/scattered_snow_showers_night.svg',
    '73_1': 'assets/weather-icons/showers_snow.svg',
    '73_0': 'assets/weather-icons/showers_snow.svg',
    '75_1': 'assets/weather-icons/heavy_snow.svg',
    '75_0': 'assets/weather-icons/heavy_snow.svg',

    // 77: Snow grains
    '77_1': 'assets/weather-icons/flurries.svg',
    '77_0': 'assets/weather-icons/flurries.svg',

    // 80,81,82: Rain showers
    '80_1': 'assets/weather-icons/scattered_showers_day.svg',
    '80_0': 'assets/weather-icons/scattered_showers_night.svg',
    '81_1': 'assets/weather-icons/showers_rain.svg',
    '81_0': 'assets/weather-icons/showers_rain.svg',
    '82_1': 'assets/weather-icons/heavy_rain.svg',
    '82_0': 'assets/weather-icons/heavy_rain.svg',

    // 85,86: Snow showers
    '85_1': 'assets/weather-icons/showers_snow.svg',
    '85_0': 'assets/weather-icons/showers_snow.svg',
    '86_1': 'assets/weather-icons/heavy_snow.svg',
    '86_0': 'assets/weather-icons/heavy_snow.svg',

    // 95: Thunderstorm (no hail)
    '95_1': 'assets/weather-icons/isolated_thunderstorms.svg',
    '95_0': 'assets/weather-icons/isolated_thunderstorms.svg',

    // 96,99: Thunderstorm with hail
    '96_1': 'assets/weather-icons/strong_thunderstorms.svg',
    '96_0': 'assets/weather-icons/strong_thunderstorms.svg',
    '99_1': 'assets/weather-icons/strong_thunderstorms.svg',
    '99_0': 'assets/weather-icons/strong_thunderstorms.svg',
  };

  static String getIcon(int? weatherCode, int? isDay) {
    if (weatherCode == null || isDay == null) {
      return _defaultIcon;
    }

    final key = '${weatherCode}_$isDay';
    return _iconMap[key] ?? _defaultIcon;
  }
}
