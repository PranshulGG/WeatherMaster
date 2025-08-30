import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'dart:math';
import '../notifiers/unit_settings_notifier.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../helper/locale_helper.dart';

class _BulletCandidate {
  final int priority;
  final String text;
  _BulletCandidate(this.priority, this.text);
}

class _HeadlineCandidate {
  final int priority;
  final List<String> options;
  _HeadlineCandidate(this.priority, this.options);
}

class SummaryCard extends StatefulWidget {
  final int selectedContainerBgIndex;
  final Map<String, dynamic> hourlyData;
  final Map<String, dynamic> dailyData;
  final Map<String, dynamic> currentData;
  final Map<String, dynamic> airQualityData;
  final String utcOffsetSeconds;

  const SummaryCard(
      {super.key,
      required this.selectedContainerBgIndex,
      required this.hourlyData,
      required this.dailyData,
      required this.currentData,
      required this.airQualityData,
      required this.utcOffsetSeconds});

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
    final humidity =
        (hourly['relative_humidity_2m'] as List<dynamic>).take(24).toList();
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
    TimeOfDayPeriod period, {
    int? airQuality,
  }) {
    final candidates = <_HeadlineCandidate>[];

    if (isStormy(weatherCode)) {
      candidates.add(_HeadlineCandidate(90, [
        "summary_headlines_1".tr(),
        "summary_headlines_2".tr(),
        "summary_headlines_3".tr(),
      ]));
    } else if (isRainy(weatherCode)) {
      candidates.add(_HeadlineCandidate(70, [
        "summary_headlines_4".tr(),
        "summary_headlines_5".tr(),
        "summary_headlines_6".tr(),
      ]));
    }

    if (isCloudy(cloudCover)) {
      int cloudPriority = 40;
      switch (period) {
        case TimeOfDayPeriod.morning:
          candidates.add(_HeadlineCandidate(cloudPriority, [
            "summary_headlines_9".tr(),
            "summary_headlines_8".tr(),
            "summary_headlines_7".tr(),
          ]));
          break;
        case TimeOfDayPeriod.afternoon:
          candidates.add(_HeadlineCandidate(cloudPriority, [
            "summary_headlines_28".tr(),
            "summary_headlines_29".tr(),
            "summary_headlines_30".tr(),
          ]));
          break;
        case TimeOfDayPeriod.evening:
          candidates.add(_HeadlineCandidate(cloudPriority, [
            "summary_headlines_31".tr(),
            "summary_headlines_32".tr(),
            "summary_headlines_33".tr(),
          ]));
          break;
        case TimeOfDayPeriod.night:
          candidates.add(_HeadlineCandidate(cloudPriority, [
            "summary_headlines_34".tr(),
            "summary_headlines_35".tr(),
            "summary_headlines_36".tr(),
          ]));
          break;
      }
    }

    if ((period == TimeOfDayPeriod.morning ||
            period == TimeOfDayPeriod.afternoon) &&
        uv > 7 &&
        temp > 23 &&
        isClear(cloudCover)) {
      candidates.add(_HeadlineCandidate(60, [
        "summary_headlines_10".tr(),
        "summary_headlines_11".tr(),
        "summary_headlines_12".tr(),
      ]));
    }

    if (humidity > 75) {
      candidates.add(_HeadlineCandidate(50, [
        "summary_headlines_16".tr(),
        "summary_headlines_17".tr(),
        "summary_headlines_18".tr(),
      ]));
    }

    if (wind > 15) {
      candidates.add(_HeadlineCandidate(55, [
        "summary_headlines_19".tr(),
        "summary_headlines_20".tr(),
        "summary_headlines_21".tr(),
      ]));
    }

    if (temp < 15) {
      candidates.add(_HeadlineCandidate(45, [
        "summary_headlines_22".tr(),
        "summary_headlines_23".tr(),
        "summary_headlines_24".tr(),
      ]));
    }

    if (candidates.isEmpty) {
      candidates.add(_HeadlineCandidate(10, [
        "summary_headlines_25".tr(),
        "summary_headlines_26".tr(),
        "summary_headlines_27".tr(),
      ]));
    }

    candidates.sort((a, b) => b.priority.compareTo(a.priority));
    final base = _random(candidates.first.options);

    final suffix = getHeadlineSuffix(
      uv: uv,
      wind: wind,
      airQuality: airQuality,
      temp: temp,
      humidity: humidity,
      period: period,
    );

    return "$base$suffix";
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
    final bullets = <_BulletCandidate>[];

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
    bullets.add(_BulletCandidate(10, _random(tempOptions)));

    if (uvIndex > 2) {
      final uvTime = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr'
          ? "$uvHour:00"
          : formatHour(uvHour);
      final uvOptions = [
        "bulletsUVOptions_1".tr(namedArgs: {
          'uvTime': uvTime,
          'uvIndex': uvIndex.toStringAsFixed(0),
        }),
        "bulletsUVOptions_2".tr(namedArgs: {'uvTime': uvTime}),
        "bulletsUVOptions_3".tr(namedArgs: {'uvTime': uvTime}),
      ];
      bullets.add(_BulletCandidate(40, _random(uvOptions)));
    }

    if (humidity > 60) {
      final time = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr'
          ? "$dewHour:00"
          : formatHour(dewHour);
      final humidityOptions = [
        "bulletsHUMIDITYOptions_1".tr(namedArgs: {
          'humidity': humidity.toStringAsFixed(0),
          'dewpoint': isFahrenheit
              ? UnitConverter.celsiusToFahrenheit(dewPoint).round().toString()
              : dewPoint.toStringAsFixed(0),
          'time': time
        }),
        "bulletsHUMIDITYOptions_2".tr(namedArgs: {
          'dewpoint': isFahrenheit
              ? UnitConverter.celsiusToFahrenheit(dewPoint).round().toString()
              : dewPoint.toStringAsFixed(0),
          'time': time
        }),
        "bulletsHUMIDITYOptions_3".tr(namedArgs: {
          'time': time,
          'humidity': humidity.toStringAsFixed(0),
        }),
      ];
      bullets.add(_BulletCandidate(35, _random(humidityOptions)));
    }

    if (windSpeed > 19) {
      final convertedWind = windUnit == 'Mph'
          ? UnitConverter.kmhToMph(windSpeed).round()
          : windUnit == 'M/s'
              ? UnitConverter.kmhToMs(windSpeed).toStringAsFixed(2)
              : windUnit == 'Bft'
                  ? UnitConverter.kmhToBeaufort(windSpeed).round()
                  : windSpeed.toStringAsFixed(0);
      final windOptions = [
        "bulletsWINDOptions_1".tr(namedArgs: {
          'windSpeed': convertedWind.toString(),
          'windUnit': localizeWindUnit(windUnit.toString(), context.locale),
        }),
        "bulletsWINDOptions_2".tr(namedArgs: {
          'windSpeed': convertedWind.toString(),
          'windUnit': localizeWindUnit(windUnit.toString(), context.locale),
        }),
      ];
      bullets.add(_BulletCandidate(60, _random(windOptions)));
    }

    if (airQuality != null) {
      if (airQuality > 100) {
        bullets.add(_BulletCandidate(80, "bulletsAQIOptions_1".tr()));
      } else {
        final airOptions = [
          "bulletsAQIOptions_2".tr(),
          "bulletsAQIOptions_3".tr(),
          "bulletsAQIOptions_4".tr(),
        ];
        bullets.add(_BulletCandidate(30, _random(airOptions)));
      }
    }

    bullets.sort((a, b) => b.priority.compareTo(a.priority));
    return bullets.map((b) => b.text).toList();
  }

  String getHeadlineSuffix(
      {required double uv,
      required double wind,
      required int? airQuality,
      required double temp,
      required double humidity,
      required TimeOfDayPeriod period}) {
    final suffixes = <String>[];

    if ((period == TimeOfDayPeriod.morning ||
            period == TimeOfDayPeriod.afternoon) &&
        uv >= 7) {
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
        "${dayLengthDuration.inHours} ${"hrs_sub_text".tr()} ${dayLengthDuration.inMinutes.remainder(60)} ${"mins_sub_text".tr()}";

    int offsetSeconds = int.parse(widget.utcOffsetSeconds);
    DateTime utcNow = DateTime.now().toUtc();
    DateTime now = utcNow.add(Duration(seconds: offsetSeconds));

    now = DateTime(
      now.year,
      now.month,
      now.day,
      now.hour,
      now.minute,
      now.second,
      now.millisecond,
      now.microsecond,
    );

    final period = getTimeOfDayPeriod(now);

    setState(() {
      _headline = generateHeadline(
        currentTemp,
        peakUv,
        windSpeed,
        humidity,
        cloudCoverNow,
        weatherCodeNow,
        period,
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
      padding: EdgeInsets.only(left: 20, right: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(width: 8),
          Text(
            _headline ?? '',
            style: TextStyle(
                color: Theme.of(context).colorScheme.onSurface, fontSize: 14.5),
            textAlign: TextAlign.left,
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
                            padding: const EdgeInsets.only(
                                bottom: 8, left: 5, right: 5),
                            child: Text(
                              '• $b',
                              style: TextStyle(
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant,
                              ),
                            ),
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.only(
                              bottom: 8, left: 5, right: 5),
                          child: Text(
                            '• ${"day_length".tr()}: $_readableDayLengthTime',
                            style: TextStyle(
                              color: Theme.of(context)
                                  .colorScheme
                                  .onSurfaceVariant,
                            ),
                          ),
                        ),
                      ])
                : const SizedBox.shrink(
                    key: ValueKey(false),
                  ),
          ),
        ],
      ),
    );
  }

  String formatHour(int hour) {
    final suffix = hour >= 12 ? 'PM' : 'AM';
    final formatted = hour > 12
        ? hour - 12
        : hour == 0
            ? 12
            : hour;
    return '$formatted $suffix';
  }

  bool isExpanded = false;

  @override
  Widget build(BuildContext context) {
    if (!isSummaryLoaded) {
      computeWeatherSummary();
      isSummaryLoaded = true;
    }

    return Container(
      decoration: BoxDecoration(
        color: Color(widget.selectedContainerBgIndex),
        borderRadius: BorderRadius.circular(20),
      ),
      padding: EdgeInsets.only(top: 15, bottom: 10),
      margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          GestureDetector(
            onTap: () {
              setState(() {
                isExpanded = !isExpanded;
              });
            },
            child: Row(
                crossAxisAlignment: CrossAxisAlignment.end,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Container(
                      padding: EdgeInsets.only(left: 20),
                      child: Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Icon(
                              Symbols.analytics,
                              weight: 500,
                              color: Theme.of(context).colorScheme.secondary,
                              size: 21,
                              fill: 1,
                            ),
                            SizedBox(
                              width: 5,
                            ),
                            Text("quick_summary".tr(),
                                style: TextStyle(
                                    color:
                                        Theme.of(context).colorScheme.secondary,
                                    fontWeight: FontWeight.w600,
                                    fontSize: 16)),
                          ])),
                  Container(
                    padding: EdgeInsets.only(right: 20),
                    child: Icon(
                        isExpanded ? Icons.expand_less : Icons.expand_more),
                  ),
                ]),
          ),
          Divider(
            height: 14,
            color: Colors.transparent,
          ),
          GestureDetector(
            onTap: () {
              setState(() {
                isExpanded = !isExpanded;
              });
            },
            child: buildWeatherSummaryWidget(context, isExpanded),
          ),
        ],
      ),
    );
  }
}

enum TimeOfDayPeriod { morning, afternoon, evening, night }

TimeOfDayPeriod getTimeOfDayPeriod(DateTime now) {
  final hour = now.hour;
  if (hour >= 5 && hour < 11) {
    return TimeOfDayPeriod.morning;
  } else if (hour >= 11 && hour < 17) {
    return TimeOfDayPeriod.afternoon;
  } else if (hour >= 17 && hour < 21) {
    return TimeOfDayPeriod.evening;
  } else {
    return TimeOfDayPeriod.night;
  }
}
