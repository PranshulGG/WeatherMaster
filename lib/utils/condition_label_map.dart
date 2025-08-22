import 'package:flutter/material.dart';

class WeatherConditionMapper {
  static const _defaultLabel = 'Not found';

  static const Map<String, String> _labelMap = {
    // 0: Clear sky
    '0_1': 'clear_sky',
    '0_0': 'clear_sky',

    // 1,2,3: Mainly clear, partly cloudy, overcast
    '1_1': 'mostly_clear',
    '1_0': 'mostly_clear',
    '2_1': 'partly_cloudy',
    '2_0': 'partly_cloudy',
    '3_1': 'overcast',
    '3_0': 'overcast',

    // 45,48: Fog
    '45_1': 'fog',
    '45_0': 'fog',
    '48_1': 'fog',
    '48_0': 'fog',

    // 51,53,55: Drizzle
    '51_1': 'drizzle',
    '51_0': 'drizzle',
    '53_1': 'drizzle',
    '53_0': 'drizzle',
    '55_1': 'drizzle',
    '55_0': 'drizzle',

    // 56,57: Freezing Drizzle
    '56_1': 'freezing_drizzle',
    '56_0': 'freezing_drizzle',
    '57_1': 'freezing_drizzle',
    '57_0': 'freezing_drizzle',

    // 61,63,65: Rain
    '61_1': 'moderate_rain',
    '61_0': 'moderate_rain',
    '63_1': 'moderate_rain',
    '63_0': 'moderate_rain',
    '65_1': 'heavy_intensity_rain',
    '65_0': 'heavy_intensity_rain',

    // 66,67: Freezing Rain
    '66_1': 'freezing_rain',
    '66_0': 'freezing_rain',
    '67_1': 'freezing_rain',
    '67_0': 'freezing_rain',

    // 71,73,75: Snowfall
    '71_1': 'slight_snow',
    '71_0': 'slight_snow',
    '73_1': 'moderate_snow',
    '73_0': 'moderate_snow',
    '75_1': 'heavy_intensity_snow',
    '75_0': 'heavy_intensity_snow',

    // 77: Snow grains
    '77_1': 'snow_grains',
    '77_0': 'snow_grains',

    // 80,81,82: Rain showers
    '80_1': 'rain_showers',
    '80_0': 'rain_showers',
    '81_1': 'rain_showers',
    '81_0': 'rain_showers',
    '82_1': 'heavy_rain_showers',
    '82_0': 'heavy_rain_showers',

    // 85,86: Snow showers
    '85_1': 'slight_snow_showers',
    '85_0': 'slight_snow_showers',
    '86_1': 'heavy_snow_showers',
    '86_0': 'heavy_snow_showers',

    // 95: Thunderstorm (no hail)
    '95_1': 'thunderstorm',
    '95_0': 'thunderstorm',

    // 96,99: Thunderstorm with hail
    '96_1': 'strong_thunderstorm',
    '96_0': 'strong_thunderstorm',
    '99_1': 'strong_thunderstorm',
    '99_0': 'strong_thunderstorm',
  };

  static String getConditionLabel(int? weatherCode, int? isDay) {
    if (weatherCode == null || isDay == null) {
      return _defaultLabel;
    }

    final key = '${weatherCode}_$isDay';
    return _labelMap[key] ?? _defaultLabel;
  }
}

const FontVariationsRegularNoRound = [
  FontVariation('wght', 460),
  FontVariation('ROND', 0),
];

const FontVariationsMedium = [
  FontVariation('wght', 500),
  FontVariation('ROND', 100),
];

const FontVariationsBold = [
  FontVariation('wght', 600),
  FontVariation('ROND', 100),
];

const FontVariationsFullBold = [
  FontVariation('wght', 800),
  FontVariation('ROND', 100),
];

const FontVariationsSemiBold = [
  FontVariation('wght', 700),
  FontVariation('ROND', 100),
];

const FontVariationsExtraBold = [
  FontVariation('wght', 900),
  FontVariation('ROND', 100),
];
