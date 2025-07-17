import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'dart:math';
import '../notifiers/unit_settings_notifier.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';


class SummaryCard extends StatefulWidget {
  final int selectedContainerBgIndex;
  final Map<String, dynamic> hourlyData;
  final Map<String, dynamic> dailyData;
  final Map<String, dynamic> currentData;
  final Map<String, dynamic> airQualityData;

  const SummaryCard({super.key, 
  required this.selectedContainerBgIndex,
  required this.hourlyData,
  required this.dailyData,
  required this.currentData,
  required this.airQualityData
  });


  @override
  State<SummaryCard> createState() => _SummaryCardState();
}


class _SummaryCardState extends State<SummaryCard> {

  String? _headline;
  List<String>? _bullets;
  String? _readableDayLengthTime;

  bool isSummaryLoaded = false;

@override
void initState() {
  super.initState();
 isSummaryLoaded = false;
}

  Map<String, dynamic> findPeakUv(Map<String, dynamic> hourly) {
  final times = hourly['time'] as List<dynamic>;
  final uvs = hourly['uv_index'] as List<dynamic>;
  int peakIndex = 0;
  double peakValue = 0;

  for (int i = 0; i < times.length; i++) {
    final hour = DateTime.parse(times[i]).hour;
    if (hour >= 9 && hour <= 15) {
      final uv = uvs[i]?.toDouble() ?? 0;
      if (uv > peakValue) {
        peakValue = uv;
        peakIndex = i;
      }
    }
  }

  return {
    'value': peakValue,
    'time': times[peakIndex],
  };
}

bool isStormy(int code) => [95, 96, 99].contains(code);
bool isRainy(int code) => [61, 63, 65, 80, 81, 82].contains(code);
bool isCloudy(double cloud) => cloud > 70;
bool isClear(double cloud) => cloud < 30;

Map<String, dynamic> getEveningHumidityAndDew(Map<String, dynamic> hourly) {
  final times = (hourly['time'] as List<dynamic>).take(24).toList();
  final humidity = (hourly['relative_humidity_2m'] as List<dynamic>).take(24).toList();
  final dew = (hourly['dew_point_2m'] as List<dynamic>).take(24).toList();


  for (int i = times.length - 1; i >= 0; i--) {
    final hour = DateTime.parse(times[i]).hour;
    if (hour >= 18) {
      return {
        'humidity': humidity[i]?.toDouble() ?? 0,
        'dewPoint': dew[i]?.toDouble() ?? 0,
        'time': times[i],
      };
    }
  }

  return {
    'humidity': humidity.last?.toDouble() ?? 0,
    'dewPoint': dew.last?.toDouble() ?? 0,
    'time': times.last,
  };
}


String generateHeadline(
  double temp,
  double uv,
  double wind,
  double humidity,
  double cloudCover,
  int weatherCode,
  {int? airQuality}
) {
  String base;

  if (isStormy(weatherCode)) {
    base = _random([
      "summary_headlines_1".tr(),
      "summary_headlines_2".tr(),
      "summary_headlines_3".tr(),
    ]);
  } else if (isRainy(weatherCode)) {
    base = _random([
      "summary_headlines_4".tr(),
      "summary_headlines_5".tr(),
      "summary_headlines_6".tr(),
    ]);
  } else if (isCloudy(cloudCover)) {
    base = _random([
      "summary_headlines_7".tr(),
      "summary_headlines_8".tr(),
      "summary_headlines_9".tr(),
    ]);
  } else if (uv > 7 && temp > 23 && isClear(cloudCover)) {
    base = _random([
      "summary_headlines_10".tr(),
      "summary_headlines_11".tr(),
      "summary_headlines_12".tr(),
    ]);
  } else if (uv > 7 && isClear(cloudCover)) {
    base = _random([
      "summary_headlines_13".tr(),
      "summary_headlines_14".tr(),
      "summary_headlines_15".tr(),
    ]);
  } else if (humidity > 75) {
    base = _random([
      "summary_headlines_16".tr(),
      "summary_headlines_17".tr(),
      "summary_headlines_18".tr(),
    ]);
  } else if (wind > 15) {
    base = _random([
      "summary_headlines_19".tr(),
      "summary_headlines_20".tr(),
      "summary_headlines_21".tr(),
    ]);
  } else if (temp < 15) {
    base = _random([
      "summary_headlines_22".tr(),
      "summary_headlines_23".tr(),
      "summary_headlines_24".tr(),
    ]);
  } else {
    base = _random([
      "summary_headlines_25".tr(),
      "summary_headlines_26".tr(),
      "summary_headlines_27".tr(),
    ]);
  }

  final suffix = getHeadlineSuffix(
    uv: uv,
    wind: wind,
    airQuality: airQuality,
    temp: temp,
    humidity: humidity,
  );

  return "$base$suffix";
}


String getHeadlineSuffix({
  required double uv,
  required double wind,
  required int? airQuality,
  required double temp,
  required double humidity,
}) {
  final suffixes = <String>[];

  if (uv >= 7) {
    suffixes.add(_random([
      "summary_suffixes_1".tr(),
      "summary_suffixes_2".tr(),
      "summary_suffixes_3".tr(),
      "summary_suffixes_4".tr(),
    ]));
  }

  if (wind >= 15) {
    suffixes.add(_random([
      "summary_suffixes_5".tr(),
      "summary_suffixes_6".tr(),
      "summary_suffixes_7".tr(),
    ]));
  }

  if ((airQuality ?? 0) > 100) {
    suffixes.add(_random([
      "summary_suffixes_8".tr(),
      "summary_suffixes_9".tr(),
      "summary_suffixes_10".tr(),
    ]));
  }

  if (humidity > 70 && temp > 23) {
    suffixes.add(_random([
      "summary_suffixes_11".tr(),
      "summary_suffixes_12".tr(),
      "summary_suffixes_13".tr(),
    ]));
  }

  if (suffixes.isEmpty) return ".";

  final selected = suffixes.take(2).join(" ${"summary_suffixes_and".tr()} ");
  return " — $selected.";
}

String _random(List<String> options) {
  return options[Random().nextInt(options.length)];
}


void computeWeatherSummary() {
  final currentTemp = widget.currentData['temperature_2m']?.toDouble() ?? 0;
  final windSpeed = widget.currentData['wind_speed_10m']?.toDouble() ?? 0;
  final airQuality = widget.airQualityData['current']['us_aqi']?.toInt();
  final tempMin = widget.dailyData['temperature_2m_min']?[0]?.toDouble() ?? 0;
  final tempMax = widget.dailyData['temperature_2m_max']?[0]?.toDouble() ?? 0;

  final uvData = findPeakUv(widget.hourlyData);
  final peakUv = uvData['value'] as double;
  final uvHour = DateTime.parse(uvData['time']).hour;

  final evening = getEveningHumidityAndDew(widget.hourlyData);
  final humidity = evening['humidity'] as double;
  final dewPoint = evening['dewPoint'] as double;
  final dewHour = DateTime.parse(evening['time']).hour;

  final weatherCodeNow = widget.currentData['weather_code']?.toInt() ?? 0;
  final cloudCoverNow = widget.currentData['cloud_cover']?.toDouble() ?? 100;

  final currentDayLength = widget.dailyData['daylight_duration'][0];
  final secondsDayLength = currentDayLength.toInt();
  final dayLengthDuration = Duration(seconds: secondsDayLength);
  final readableDayLengthTime =
      "${dayLengthDuration.inHours} hrs ${dayLengthDuration.inMinutes.remainder(60)} min";

  setState(() {
    _headline = generateHeadline(
      currentTemp,
      peakUv,
      windSpeed,
      humidity,
      cloudCoverNow,
      weatherCodeNow,
      airQuality: airQuality,
    );

    _bullets = generateBulletPoints(
      tempMin: tempMin,
      tempMax: tempMax,
      uvIndex: peakUv,
      uvHour: uvHour,
      humidity: humidity,
      dewPoint: dewPoint,
      dewHour: dewHour,
      windSpeed: windSpeed,
      airQuality: airQuality,
    );

    _readableDayLengthTime = readableDayLengthTime;
  });
}

Widget buildWeatherSummaryWidget(BuildContext context, bool isExpanded) {

  return Padding(
   padding: EdgeInsets.only(left: 16, right: 12), 
  
   child: Column(
    crossAxisAlignment: CrossAxisAlignment.stretch,
    children: [
      SizedBox(width: 8),
      Text(
        _headline ?? '',
        style: TextStyle(
          fontWeight: FontWeight.w500,
          color: Theme.of(context).colorScheme.onSurface,
        ),
      ),
      // ),
      SizedBox(height: 8),
    AnimatedSwitcher(
  duration: Duration(milliseconds: 300),
  switchInCurve: Curves.easeIn,
  switchOutCurve: Curves.easeOut,
  transitionBuilder: (Widget child, Animation<double> animation) {
    return FadeTransition(
      opacity: animation,
      child: child,
    );
  },
  child: isExpanded
      ? Column(
          key: ValueKey(true), 
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
          ...?_bullets?.map(
            (b) => Padding(
              padding: const EdgeInsets.only(bottom: 8, left: 5, right: 5),
              child: Text(
                '• $b',
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
              ),
            ),
          ),

          Padding(
            padding: const EdgeInsets.only(bottom: 8, left: 5, right: 5),
            child: Text(
              '• ${"day_length".tr()}: $_readableDayLengthTime',
              style: TextStyle(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
          ),
          ]
        )
      : const SizedBox.shrink(
          key: ValueKey(false),
        ),
    ),

    ],
  ),
  );
}

List<String> generateBulletPoints({
  required double tempMin,
  required double tempMax,
  required double uvIndex,
  required int uvHour,
  required double humidity,
  required double dewPoint,
  required int dewHour,
  required double windSpeed,
  required int? airQuality,
}) {
  final rand = Random();
  final bullets = <String>[];


final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;
final windUnit = context.watch<UnitSettingsNotifier>().windUnit;

final isFahrenheit = tempUnit == 'Fahrenheit';

final tempOptions = [
  "bulletstempOptions_1".tr(namedArgs: {
    'min': isFahrenheit
        ? UnitConverter.celsiusToFahrenheit(tempMin).round().toString()
        : tempMin.toStringAsFixed(0),
    'max': isFahrenheit
        ? UnitConverter.celsiusToFahrenheit(tempMax).round().toString()
        : tempMax.toStringAsFixed(0),
  }),

    "bulletstempOptions_2".tr(namedArgs: {
    'min': isFahrenheit
        ? UnitConverter.celsiusToFahrenheit(tempMin).round().toString()
        : tempMin.toStringAsFixed(0),
    'max': isFahrenheit
        ? UnitConverter.celsiusToFahrenheit(tempMax).round().toString()
        : tempMax.toStringAsFixed(0),
  }),


    "bulletstempOptions_3".tr(namedArgs: {
    'max': isFahrenheit
        ? UnitConverter.celsiusToFahrenheit(tempMax).round().toString()
        : tempMax.toStringAsFixed(0),
  }),

];

  bullets.add(tempOptions[rand.nextInt(tempOptions.length)]);

  if (uvIndex > 2) {
    final uvTime = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr' ? "$uvHour:00" : formatHour(uvHour);
      final uvOptions = [
      "bulletsUVOptions_1".tr(namedArgs: {
      'uvTime': uvTime.toString(),
      'uvIndex': uvIndex.toStringAsFixed(0),
    }),

      "bulletsUVOptions_2".tr(namedArgs: {
        'uvTime': uvTime.toString(),
    }),
      "bulletsUVOptions_3".tr(namedArgs: {
      'uvTime': uvTime.toString(),
    }),
  ];
    bullets.add(uvOptions[rand.nextInt(uvOptions.length)]);
  }

  if (humidity > 60) {
    final time = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr' ? "$dewHour:00" : formatHour(dewHour);
    final humidityOptions = [
      "bulletsHUMIDITYOptions_1".tr(namedArgs: {
        'humidity': humidity.toStringAsFixed(0),
        'dewpoint': tempUnit == 'Fahrenheit'
            ? UnitConverter.celsiusToFahrenheit(dewPoint).round().toString() : dewPoint.toStringAsFixed(0),
        'time': time.toString()
      }),

        "bulletsHUMIDITYOptions_2".tr(namedArgs: {
        'dewpoint': tempUnit == 'Fahrenheit'
            ? UnitConverter.celsiusToFahrenheit(dewPoint).round().toString() : dewPoint.toStringAsFixed(0),
        'time': time.toString()
      }),


        "bulletsHUMIDITYOptions_3".tr(namedArgs: {
        'time': time.toString(),
        'humidity': humidity.toStringAsFixed(0),
      }),
    ];
    bullets.add(humidityOptions[rand.nextInt(humidityOptions.length)]);
  }

  if (windSpeed > 19) {
        final convertedwindSpeed = windUnit == 'Mph'
        ? UnitConverter.kmhToMph(windSpeed)
        : windUnit == 'M/s'
            ? UnitConverter.kmhToMs(windSpeed) : windUnit == 'Bft' ? UnitConverter.kmhToBeaufort(windSpeed)
            : windSpeed.toStringAsFixed(0);
    final windOptions = [
      "bulletsWINDOptions_1".tr(namedArgs: {
        'windSpeed': convertedwindSpeed.toString(),
        'windUnit': windUnit.toString(),
      }),
      "bulletsWINDOptions_2".tr(namedArgs: {
        'windSpeed': convertedwindSpeed.toString(),
        'windUnit': windUnit.toString(),
      }),
    ];
    bullets.add(windOptions[rand.nextInt(windOptions.length)]);
  }

  if (airQuality != null) {
    if (airQuality > 100) {
      bullets.add("bulletsAQIOptions_1".tr());
    } else {
      final airOptions = [
        "bulletsAQIOptions_2".tr(),
        "bulletsAQIOptions_3".tr(),
        "bulletsAQIOptions_4".tr(),
      ];
      bullets.add(airOptions[rand.nextInt(airOptions.length)]);
    }
  }

  return bullets;
}

String formatHour(int hour) {
  final suffix = hour >= 12 ? 'PM' : 'AM';
  final formatted = hour > 12 ? hour - 12 : hour == 0 ? 12 : hour;
  return '$formatted $suffix';
}

  


bool isExpanded = false;

  @override
  Widget build(BuildContext context) {

  if(!isSummaryLoaded){
    computeWeatherSummary();
    isSummaryLoaded = true;
  }


    final windUnit = context.watch<UnitSettingsNotifier>().windUnit;

    final formattedWindGust = windUnit == 'Mph'
        ? UnitConverter.kmhToMph(widget.currentData['wind_gusts_10m'])
        : windUnit == 'M/s'
            ? UnitConverter.kmhToMs(widget.currentData['wind_gusts_10m']) : windUnit == 'Bft' ? UnitConverter.kmhToBeaufort(widget.currentData['wind_gusts_10m'])
            : widget.currentData['wind_gusts_10m'];


  final formattedWindSpeed =  windUnit == 'Mph'
        ? UnitConverter.kmhToMph(widget.currentData['wind_speed_10m'])
        : windUnit == 'M/s'
            ? UnitConverter.kmhToMs(widget.currentData['wind_speed_10m']) : windUnit == 'Bft' ? UnitConverter.kmhToBeaufort(widget.currentData['wind_speed_10m'])
            : widget.currentData['wind_speed_10m'];

    return Container(
        decoration: BoxDecoration(
          color: Color(widget.selectedContainerBgIndex),
          borderRadius: BorderRadius.circular(18),
        ),
        padding: EdgeInsets.only(top: 12, bottom: 10),
        margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
        child: Column(
        children: [
      GestureDetector(
        onTap: () {
          setState(() {
            isExpanded = !isExpanded;
          });
        },
       child:  Row(
          crossAxisAlignment: CrossAxisAlignment.end,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Container(
              padding: EdgeInsets.only(left: 20),
          child: Row(
            children: [
            Icon(Symbols.analytics, weight: 500, color: Theme.of(context).colorScheme.secondary, size: 20,),
            SizedBox(width: 5,),
            Text("quick_summary".tr(), style: TextStyle(fontWeight: FontWeight.w500, color: Theme.of(context).colorScheme.secondary, fontSize: 15.5)),
            ]
          )
        ),  
            Container(
            padding: EdgeInsets.only(right: 20), 
           child: Icon(isExpanded
          ? Icons.expand_less
          : Icons.expand_more),   
        ),
              
          ]
        ),
    ),

        Divider(height: 20, color: Theme.of(context).colorScheme.outlineVariant,),
           GestureDetector(
        onTap: () {
          setState(() {
            isExpanded = !isExpanded;
          });
        },
      child:  buildWeatherSummaryWidget(context, isExpanded),
    ),
    Padding(
      padding: EdgeInsets.only(left: 10, right: 10, top: 8, bottom: 8),
     child: Row(
        children: [
        Expanded(
         child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            spacing: 4,
            children: [
              Icon(Symbols.air, weight: 500, color: Theme.of(context).colorScheme.onSurface, size: 19,),
              Text('${windUnit == 'M/s' ? formattedWindSpeed.toStringAsFixed(1) : formattedWindSpeed.round()} $windUnit', style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 15))
            ],
          ),),

          
          Expanded(
         child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            spacing: 6,
            children: [
              Icon(Symbols.wind_power, weight: 500, color: Theme.of(context).colorScheme.onSurface, size: 19),
              Text("${windUnit == 'M/s' ? formattedWindGust.toStringAsFixed(1) : formattedWindGust.round()} $windUnit", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 15))
            ],
          ),),
          Expanded(
         child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            spacing: 6,
            children: [
              Icon(Symbols.cloud, weight: 500, color: Theme.of(context).colorScheme.onSurface, size: 19),
              Text("${widget.currentData['cloud_cover']}%", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 15))
            ],
          ),),         
        ],
      )
      )
      ],
      ),
    );
  }
}



// widget.currentData['wind_speed_10m']