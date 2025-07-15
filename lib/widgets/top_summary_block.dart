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
    base = "Stormy conditions expected";
  } else if (isRainy(weatherCode)) {
    base = "Cloudy with a chance of rain";
  } else if (isCloudy(cloudCover)) {
    base = "Overcast skies with muted sunlight";
  } else if (uv > 7 && temp > 23 && isClear(cloudCover)) {
    base = "Hot and sunny with strong UV";
  } else if (uv > 7 && isClear(cloudCover)) {
    base = "Bright and sunny with strong UV";
  } else if (humidity > 75) {
    base = "Warm start to a sticky day";
  } else if (wind > 15) {
    base = "A brisk and breezy morning";
  } else if (temp < 15) {
    base = "Cool and calm start to the day";
  } else {
    base = "Pleasant start to the day";
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

  if (uv >= 7) suffixes.add("high UV midday");
  if (wind >= 15) suffixes.add("breezy afternoon ahead");
  if ((airQuality ?? 0) > 100) suffixes.add("poor air quality");
  if (humidity > 70 && temp > 23) suffixes.add("humid conditions later");

  if (suffixes.isEmpty) return ".";

  final selected = suffixes.take(2).join(" and ");
  return " — $selected.";
}


Widget buildWeatherSummaryWidget(BuildContext context, bool isExpanded) {
  final currentTemp =  widget.currentData['temperature_2m']?.toDouble() ?? 0;
  final windSpeed = widget.currentData['wind_speed_10m']?.toDouble() ?? 0;
  final airQuality = widget.airQualityData['current']['us_aqi']?.toInt();


  final tempMin = widget.dailyData['temperature_2m_min']?[0]?.toDouble() ?? 0;
  final tempMax = widget.dailyData['temperature_2m_max']?[0]?.toDouble() ?? 0;



  // Analyze hourly data
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

      int secondsDayLength = currentDayLength.toInt();
    Duration dayLengthduration = Duration(seconds: secondsDayLength);

  String readableDayLengthTime = "${dayLengthduration.inHours} hrs ${dayLengthduration.inMinutes.remainder(60)} min";


final headline = generateHeadline(
  currentTemp.toDouble(),
  peakUv.toDouble(),
  windSpeed.toDouble(),
  humidity.toDouble(),
  cloudCoverNow.toDouble(),
  weatherCodeNow,
  airQuality: airQuality
);

  final bullets = generateBulletPoints(
    tempMin: tempMin,
    tempMax: tempMax,
    uvIndex: peakUv,
    uvHour: uvHour,
    humidity: humidity,
    dewPoint: dewPoint,
    dewHour: dewHour,
    windSpeed: windSpeed.toDouble(),
    airQuality: airQuality,
  );


  return Padding(
   padding: EdgeInsets.only(left: 16, right: 12), 
  
   child: Column(
    crossAxisAlignment: CrossAxisAlignment.stretch,
    children: [
      SizedBox(width: 8),
      Text(
        headline,
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
          key: ValueKey(true), // Important for AnimatedSwitcher
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
          ...bullets.map(
            (b) => Padding(
              padding: const EdgeInsets.only(bottom: 8, left: 10),
              child: Text(
                '• $b',
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
              ),
            ),
          ),

          Padding(
            padding: const EdgeInsets.only(bottom: 8, left: 10),
            child: Text(
              '• ${"day_length".tr()}: $readableDayLengthTime',
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
  "Temperatures ranging between ${isFahrenheit ? UnitConverter.celsiusToFahrenheit(tempMin).round() : tempMin.toStringAsFixed(0)}° and ${isFahrenheit ? UnitConverter.celsiusToFahrenheit(tempMax).round() : tempMax.toStringAsFixed(0)}°.",
  "Expect a daytime range from ${isFahrenheit ? UnitConverter.celsiusToFahrenheit(tempMin).round() : tempMin.toStringAsFixed(0)}° to ${isFahrenheit ? UnitConverter.celsiusToFahrenheit(tempMax).round() : tempMax.toStringAsFixed(0)}°.",
  "Today's high will reach around ${isFahrenheit ? UnitConverter.celsiusToFahrenheit(tempMax).round() : tempMax.toStringAsFixed(0)}° after a cooler start.",
];

  bullets.add(tempOptions[rand.nextInt(tempOptions.length)]);

  if (uvIndex > 2) {
    final uvTime = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr' ? "$uvHour:00" : formatHour(uvHour);
    final uvOptions = [
      "UV index peaks around $uvTime at ${uvIndex.toStringAsFixed(0)}.",
      "Strong UV expected near $uvTime — sun protection recommended.",
      "High UV levels around $uvTime could pose a risk outdoors.",
    ];
    bullets.add(uvOptions[rand.nextInt(uvOptions.length)]);
  }

  if (humidity > 60) {
    final time = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr' ? "$dewHour:00" : formatHour(dewHour);
    final humidityOptions = [
      "${humidity.toStringAsFixed(0)}% humidity with a dew point of ${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(dewPoint).round() : dewPoint.toStringAsFixed(0)}° by $time.",
      "Feels humid later — dew point near ${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(dewPoint).round() : dewPoint.toStringAsFixed(0)}° at $time.",
      "Sticky conditions expected by $time with ${humidity.toStringAsFixed(0)}% humidity.",
    ];
    bullets.add(humidityOptions[rand.nextInt(humidityOptions.length)]);
  }

  if (windSpeed > 19) {
        final convertedwindSpeed = windUnit == 'Mph'
        ? UnitConverter.kmhToMph(windSpeed)
        : windUnit == 'M/s'
            ? UnitConverter.kmhToMs(windSpeed)
            : windSpeed.toStringAsFixed(0);
    final windOptions = [
      "Breezy conditions with winds up to $convertedwindSpeed $windUnit.",
      "Wind picks up during the day, reaching $convertedwindSpeed $windUnit.",
    ];
    bullets.add(windOptions[rand.nextInt(windOptions.length)]);
  }

  if (airQuality != null) {
    if (airQuality > 100) {
      bullets.add("Air quality is poor right now — consider limiting time outside.");
    } else {
      final airOptions = [
        "Good air quality at the moment.",
        "No air quality concerns currently.",
        "Breathable conditions outdoors today.",
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

    final windUnit = context.watch<UnitSettingsNotifier>().windUnit;

    final formattedWindGust = windUnit == 'Mph'
        ? UnitConverter.kmhToMph(widget.currentData['wind_gusts_10m'])
        : windUnit == 'M/s'
            ? UnitConverter.kmhToMs(widget.currentData['wind_gusts_10m'])
            : widget.currentData['wind_gusts_10m'];


  final formattedWindSpeed =  windUnit == 'Mph'
        ? UnitConverter.kmhToMph(widget.currentData['wind_speed_10m'])
        : windUnit == 'M/s'
            ? UnitConverter.kmhToMs(widget.currentData['wind_speed_10m'])
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