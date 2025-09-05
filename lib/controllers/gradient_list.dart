import 'package:flutter/material.dart';
import '../utils/theme.dart';
import 'package:material_color_utilities/material_color_utilities.dart';

List<LinearGradient> getGradients(
    bool isLight, bool isFroggyLayout, iscurrentDay) {
  return [
    // cloudy
    isLight
        ? LinearGradient(
            colors: [Color(0xFFc6d3e4), Color(0xFFd5e4f7)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : !isFroggyLayout
            ? iscurrentDay
                ? LinearGradient(
                    colors: [
                      Color(CorePalette.of(
                              const Color.fromARGB(255, 31, 25, 0).toARGB32())
                          .secondary
                          .get(30)),
                      Color(paletteWeather.secondary.get(18))
                    ],
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0, 0.5])
                : LinearGradient(
                    colors: [
                      Color(paletteWeather.primary.get(4)),
                      Color(paletteWeather.secondary.get(10))
                    ],
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0, 0.5])
            : iscurrentDay
                ? LinearGradient(
                    colors: [
                      Color(CorePalette.of(
                              const Color.fromARGB(255, 31, 25, 0).toARGB32())
                          .secondary
                          .get(30)),
                      Color(paletteWeather.secondary.get(18))
                    ],
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0, 0.5])
                : LinearGradient(
                    colors: [
                      Color(paletteWeather.secondary.get(30)),
                      Color(paletteWeather.secondary.get(18))
                    ],
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0, 0.5]),

    // overcast
    isLight
        ? LinearGradient(
            colors: [Color(0xFFc9d3e0), Color(0xFFcfdef1)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : !isFroggyLayout
            ? LinearGradient(
                colors: [
                  Color(paletteWeather.neutral.get(0)),
                  Color(paletteWeather.secondary.get(20))
                ],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                stops: [0, 0.5])
            : LinearGradient(
                colors: [
                  Color(paletteWeather.neutral.get(20)),
                  Color(paletteWeather.secondary.get(15))
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                stops: [0, 0.5]),

    // clear day
    isLight
        ? LinearGradient(
            colors: [Color(0xFF9dceff), Color(0xFFcee5ff)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : LinearGradient(
            colors: [
              const Color.fromARGB(255, 5, 47, 110),
              Color.fromARGB(255, 0, 51, 92)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5]),

    // clear night
    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 227, 232, 255),
              Color.fromARGB(255, 189, 197, 236)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : LinearGradient(
            colors: [Color.fromARGB(255, 0, 0, 0), Color(0xFF162155)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5]),

    // fog
    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 245, 237, 219),
              Color.fromARGB(255, 255, 236, 192)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : const LinearGradient(
            colors: [Color(0xFF191209), Color(0xFF352603)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5]),

    // rain
    isLight
        ? LinearGradient(
            colors: [Color(0xFFaab8ca), Color(0xFFc4d3e5)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : LinearGradient(
            colors: [Color(0xFF050709), Color(0xFF1e2c3a)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            stops: [0, 0.5]),

    // thunder
    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 231, 201, 243),
              Color.fromARGB(255, 223, 196, 229)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : const LinearGradient(
            colors: [Color(0xFF15021a), Color.fromARGB(255, 76, 40, 88)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            stops: [0, 0.5]),

    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 170, 200, 202),
              Color.fromARGB(255, 196, 225, 229)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : const LinearGradient(
            colors: [
              Color.fromARGB(255, 0, 0, 0),
              Color.fromARGB(255, 17, 23, 29)
            ],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            stops: [0, 0.5]),
  ];
}

List<LinearGradient> getGradientsScrolled(
    bool isLight, bool isFroggyLayout, iscurrentDay) {
  return [
    // cloudy
    isLight
        ? LinearGradient(
            colors: [Color(0xFFd5e4f7), Color(0xFFd5e4f7)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : !isFroggyLayout
            ? iscurrentDay
                ? LinearGradient(
                    colors: [
                      Color(paletteWeather.secondary.get(18)),
                      Color(paletteWeather.secondary.get(18))
                    ],
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0, 0.5])
                : LinearGradient(
                    colors: [
                      Color(paletteWeather.primary.get(10)),
                      Color(paletteWeather.secondary.get(10))
                    ],
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0, 0.5])
            : LinearGradient(
                colors: [
                  Color(paletteWeather.secondary.get(18)),
                  Color(paletteWeather.secondary.get(18))
                ],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                stops: [0, 0.5]),

    // overcast
    isLight
        ? LinearGradient(
            colors: [Color(0xFFcfdef1), Color(0xFFcfdef1)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : !isFroggyLayout
            ? LinearGradient(
                colors: [
                  Color(paletteWeather.neutral.get(20)),
                  Color(paletteWeather.secondary.get(20))
                ],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                stops: [0, 0.5])
            : LinearGradient(
                colors: [
                  Color(paletteWeather.secondary.get(15)),
                  Color(paletteWeather.secondary.get(15))
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                stops: [0, 0.5]),

    // clear day

    isLight
        ? LinearGradient(
            colors: [Color(0xFFcee5ff), Color(0xFFcee5ff)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : LinearGradient(
            colors: [
              Color.fromARGB(255, 0, 51, 92),
              Color.fromARGB(255, 0, 51, 92)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5]),

    // clear night
    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 189, 197, 236),
              Color.fromARGB(255, 189, 197, 236)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : LinearGradient(
            colors: [Color(0xFF162155), Color(0xFF162155)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5]),

    // fog
    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 255, 236, 192),
              Color.fromARGB(255, 255, 236, 192)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : const LinearGradient(
            colors: [Color(0xFF352603), Color(0xFF352603)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5]),

    // rain
    isLight
        ? LinearGradient(
            colors: [Color(0xFFc4d3e5), Color(0xFFc4d3e5)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : LinearGradient(
            colors: [Color(0xFF1e2c3a), Color(0xFF1e2c3a)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            stops: [0, 0.5]),

    // thunder
    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 171, 145, 180),
              Color.fromARGB(255, 223, 196, 229)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : const LinearGradient(
            colors: [
              Color.fromARGB(255, 76, 40, 88),
              Color.fromARGB(255, 76, 40, 88)
            ],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            stops: [0, 0.5]),

    isLight
        ? LinearGradient(
            colors: [
              Color.fromARGB(255, 196, 225, 229),
              Color.fromARGB(255, 196, 225, 229)
            ],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            stops: [0, 0.5],
          )
        : const LinearGradient(
            colors: [
              Color.fromARGB(255, 17, 23, 29),
              Color.fromARGB(255, 17, 23, 29)
            ],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            stops: [0, 0.5]),
  ];
}

Map<int, int> dayGradients = {
  0: 2, // Clear sky
  1: 0, // Mainly clear
  2: 0, // Partly cloudy
  3: 1, // Overcast

  45: 4, // Fog
  48: 4, // Depositing rime fog

  51: 5, // Drizzle: Light
  53: 5, // Drizzle: Moderate
  55: 5, // Drizzle: Dense
  56: 5, // Freezing Drizzle: Light
  57: 5, // Freezing Drizzle: Dense
  61: 5, // Rain: Slight
  63: 5, // Rain: Moderate
  65: 5, // Rain: Heavy
  66: 5, // Freezing Rain: Light
  67: 5, // Freezing Rain: Heavy
  80: 5, // Rain showers: Slight
  81: 5, // Rain showers: Moderate
  82: 5, // Rain showers: Violent

  95: 6, // Thunderstorm
  96: 6, // Thunderstorm with hail
  99: 6, // Thunderstorm with heavy hail

  71: 7, // Snow fall: Slight
  73: 7, // Snow fall: Moderate
  75: 7, // Snow fall: Heavy
  77: 7, // Snow grains
  85: 7, // Snow showers: Slight
  86: 7, // Snow showers: Heavy
};

Map<int, int> nightGradients = {
  0: 3, // Clear sky night
  1: 0, // Mainly clear
  2: 0, // Partly cloudy
  3: 1, // Overcast

  45: 4, // Fog
  48: 4, // Depositing rime fog

  51: 5, // Drizzle: Light
  53: 5, // Drizzle: Moderate
  55: 5, // Drizzle: Dense
  56: 5, // Freezing Drizzle: Light
  57: 5, // Freezing Drizzle: Dense
  61: 5, // Rain: Slight
  63: 5, // Rain: Moderate
  65: 5, // Rain: Heavy
  66: 5, // Freezing Rain: Light
  67: 5, // Freezing Rain: Heavy
  80: 5, // Rain showers: Slight
  81: 5, // Rain showers: Moderate
  82: 5, // Rain showers: Violent

  95: 6, // Thunderstorm
  96: 6, // Thunderstorm with hail
  99: 6, // Thunderstorm with heavy hail

  71: 7, // Snow fall: Slight
  73: 7, // Snow fall: Moderate
  75: 7, // Snow fall: Heavy
  77: 7, // Snow grains
  85: 7, // Snow showers: Slight
  86: 7, // Snow showers: Heavy
};
