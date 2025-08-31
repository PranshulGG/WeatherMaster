import 'dart:math';
import 'package:flutter/material.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import '../utils/preferences_helper.dart';
import 'package:easy_localization/easy_localization.dart';

class ShowInsights extends StatefulWidget {
  final List<Map<String, dynamic>> hourlyData;
  final List<Map<String, dynamic>> dailyData;
  final List<Map<String, dynamic>> currentData;
  final int selectedContainerBgIndex;

  const ShowInsights(
      {super.key,
      required this.hourlyData,
      required this.dailyData,
      required this.currentData,
      required this.selectedContainerBgIndex});

  @override
  State<ShowInsights> createState() => _ShowInsightsState();
}

class _ShowInsightsState extends State<ShowInsights> {
  String _insight = '';
  late IconData iconData;

  @override
  void initState() {
    super.initState();
  }

  bool _hasGeneratedInsight = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    if (!_hasGeneratedInsight) {
      _generateSmartInsight();
      _hasGeneratedInsight = true;
    }
  }

  void _generateSmartInsight() {
    final insightGenerators = <MapEntry<String Function(), IconData>>[
      MapEntry(_coldTrendInsight, Symbols.ac_unit),
      MapEntry(_rainProbabilityInsight, Symbols.umbrella),
      MapEntry(_heatwaveInsight, Symbols.heat),
      MapEntry(_humidityTrendInsight, Symbols.humidity_mid),
      MapEntry(_windInsight, Symbols.air),
      MapEntry(_pleasantWeatherInsight, Symbols.mood),
      MapEntry(_temperatureSwingInsight, Symbols.thermostat_auto),
      MapEntry(_uvIndexInsight, Symbols.flare),
      MapEntry(_windFluctuationInsight, Symbols.energy),
      MapEntry(_hourlyHumidityInsight, Symbols.humidity_mid),
      MapEntry(_cloudPatternInsight, Symbols.cloud),
      MapEntry(_precipitationWindowInsight, Symbols.rainy),
      MapEntry(_randomFunFact, Symbols.fact_check),
    ];

    final random = Random();
    final selected =
        insightGenerators[random.nextInt(insightGenerators.length)];

    setState(() {
      _insight = selected.key();
      iconData = selected.value;
    });
  }

  String _coldTrendInsight() {
    final temps =
        widget.dailyData.map((d) => d['temperature_2m_max'] as double).toList();

    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;
    final start = temps.first;
    final end = temps.last;

    final diff = (end - start).round();

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (diff < -3) {
        return "It’s gonna get colder by the weekend... bring a leaf or a scarf or something ~ froggy :3";
      } else if (diff > 3) {
        return "Things are heating up... not my favorite, but you might like it ~ froggy :3";
      } else {
        return "Weather’s staying the same mostly... no surprises, i like that ~ froggy :3";
      }
    }

    if (diff < -3) {
      return "insights_sub_1".tr(namedArgs: {
        'tempDiff':
            "${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(diff.abs().toDouble()) : diff.abs()}"
      });
    } else if (diff > 3) {
      return "insights_sub_2".tr(namedArgs: {
        'tempDiff':
            "${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(diff.toDouble()) : "$diff"}"
      });
    }

    return "insights_sub_3".tr();
  }

  String _rainProbabilityInsight() {
    final rainChances = widget.dailyData
        .map((d) => d['precipitation_probability_max'] ?? 0)
        .cast<num>()
        .toList();
    final peakDayIndex = rainChances.indexOf(rainChances.reduce(max));
    final peakDay =
        DateTime.parse(widget.dailyData[peakDayIndex]['time']).weekday;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (rainChances[peakDayIndex] > 60) {
        return "Might get a real good rain on ${_weekdayName(peakDay)}... i’m excited ~ froggy :3";
      } else if (rainChances.any((r) => r > 40)) {
        return "Some days might be splashy... bring something waterproof maybe ~ froggy :3";
      }
      return "Dry skies all week... i guess i’ll just sit and blink a lot ~ froggy :3";
    }

    if (rainChances[peakDayIndex] > 60) {
      return "insights_sub_4".tr(namedArgs: {'peakDay': _weekdayName(peakDay)});
    } else if (rainChances.any((r) => r > 40)) {
      return "insights_sub_5".tr();
    }
    return "insights_sub_6".tr();
  }

  String _heatwaveInsight() {
    final rawTemps =
        widget.dailyData.map((d) => d['temperature_2m_max']).toList();

    final maxTemps = rawTemps
        .where((t) => t != null && t is num)
        .map((t) => (t as num).toDouble())
        .toList();
    final hotDays = maxTemps.where((t) => t >= 35).length;
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (hotDays >= 3) {
        return "Too many hot days coming... i’ll just sink into the pond and wait it out ~ froggy :3";
      } else if (hotDays >= 1) {
        return "There’s at least one toasty day... not great for hopping around ~ froggy :3";
      }
      return "No major heat... cool enough for a lazy sit ~ froggy :3";
    }

    if (hotDays >= 3) {
      return "insights_sub_7".tr(namedArgs: {
        'hotDays': "$hotDays",
        'heatTemp':
            "${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(35) : "35"}"
      });
    } else if (hotDays >= 1) {
      return "insights_sub_8".tr(namedArgs: {
        'heatTemp':
            "${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(35) : "35"}"
      });
    }
    return "insights_sub_9".tr();
  }

  String _humidityTrendInsight() {
    final humidities = widget.dailyData
        .map((d) => d['relative_humidity_max'] ?? 0)
        .cast<num>()
        .toList();
    final start = humidities.first;
    final end = humidities.last;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (end > start + 10) {
        return "Humidity's going up... good for my skin i guess ~ froggy :3";
      } else if (start > end + 10) {
        return "Air’s drying out... not as comfy, but fine for hopping ~ froggy :3";
      }
      return "Humidity looks normal... nothing too weird ~ froggy :3";
    }

    if (end > start + 10) {
      return "insights_sub_10".tr();
    } else if (start > end + 10) {
      return "insights_sub_11".tr();
    }
    return "insights_sub_12".tr();
  }

  String _windInsight() {
    final winds = widget.dailyData
        .map((d) => d['wind_speed_max'] ?? 0)
        .cast<num>()
        .toList();
    final maxWind = winds.reduce(max);

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (maxWind > 30) {
        return "Too windy... almost got blown off my log last time ~ froggy :3";
      } else if (maxWind > 20) {
        return "Bit breezy... makes the reeds wiggle ~ froggy :3";
      }
      return "Calm air... everything’s still and nice ~ froggy :3";
    }

    if (maxWind > 30) {
      return "insights_sub_13".tr(
          namedArgs: {'weekdays': _weekdayName(winds.indexOf(maxWind) + 1)});
    } else if (maxWind > 20) {
      return "insights_sub_14".tr();
    }
    return "insights_sub_15".tr();
  }

  String _pleasantWeatherInsight() {
    final sunnyDays = widget.dailyData
        .where((d) =>
            (d['precipitation_probability_max'] ?? 0) < 20 &&
            (d['temperature_2m_max'] ?? 25) >= 22 &&
            (d['temperature_2m_max'] ?? 25) <= 28)
        .length;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (sunnyDays >= 4) {
        return "$sunnyDays nice days ahead... good for long sits on warm stones ~ froggy :3";
      }
      return "Some good days, some not... sounds like life ~ froggy :3";
    }

    if (sunnyDays >= 4) {
      return "insights_sub_16".tr(namedArgs: {'sunnyDays': "$sunnyDays"});
    }
    return "insights_sub_17".tr();
  }

  String _randomFunFact() {
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;

    final facts = [
      "insights_sub_18".tr(),
      "insights_sub_19".tr(namedArgs: {
        'temp':
            "${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(10) : "10"}",
        'feelliketemp':
            "${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(5) : "5"}"
      }),
      "insights_sub_20".tr(),
      "insights_sub_21".tr(),
    ];

    return facts[Random().nextInt(facts.length)];
  }

  String _weekdayName(int weekdayNumber) {
    final weekdays = [
      "weekdays_1".tr(),
      "weekdays_2".tr(),
      "weekdays_3".tr(),
      "weekdays_4".tr(),
      "weekdays_5".tr(),
      "weekdays_6".tr(),
      "weekdays_7".tr()
    ];
    return weekdays[(weekdayNumber - 1) % 7];
  }

  String _temperatureSwingInsight() {
    final temps = widget.hourlyData
        .map((d) => d['temperature_2m'] ?? 0)
        .cast<num>()
        .toList();
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;

    final minTemp = temps.reduce(min);
    final maxTemp = temps.reduce(max);
    final diff = (maxTemp - minTemp).round();

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (diff >= 10) {
        return "Big temperature jumps today... bring a leaf AND a blanket maybe ~ froggy :3";
      }
      return "Temps look steady... i like that ~ froggy :3";
    }

    if (diff >= 10) {
      return "insights_sub_22".tr(namedArgs: {
        'tempdiff':
            "Δ ${tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(diff.toDouble()) : "$diff"}"
      });
    } else {
      return "insights_sub_23".tr();
    }
  }

  String _uvIndexInsight() {
    final uv =
        widget.hourlyData.map((d) => d['uv_index'] ?? 0).cast<num>().toList();
    final peakUV = uv.reduce(max);

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (peakUV >= 7) {
        return "Sun’s too spicy today... maybe find shade ~ froggy :3";
      } else if (peakUV >= 4) {
        return "Sun’s a little strong... i’m staying near the cattails ~ froggy :3";
      }
      return "Low sun today... good for sitting anywhere ~ froggy :3";
    }

    if (peakUV >= 7) {
      return "insights_sub_24".tr();
    } else if (peakUV >= 4) {
      return "insights_sub_25".tr();
    }
    return "insights_sub_26".tr();
  }

  String _windFluctuationInsight() {
    final winds = widget.hourlyData
        .map((d) => d['wind_speed_10m'] ?? 0)
        .cast<num>()
        .toList();
    final maxWind = winds.reduce(max);
    final minWind = winds.reduce(min);
    final diff = maxWind - minWind;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (maxWind > 30) {
        return "The wind’s acting wild today... hold onto your hat, if you have one ~ froggy :3";
      } else if (diff > 15) {
        return "Wind’s being weird... up and down and all around ~ froggy :3";
      }
      return "Air’s calm today... i’m staying on my log ~ froggy :3";
    }

    if (maxWind > 30) {
      return "insights_sub_27".tr();
    } else if (diff > 15) {
      return "insights_sub_28".tr();
    }
    return "insights_sub_29".tr();
  }

  String _hourlyHumidityInsight() {
    final humidities = widget.hourlyData
        .map((d) => d['relative_humidity_2m'] ?? 0)
        .cast<num>()
        .toList();
    final maxH = humidities.reduce(max);
    final minH = humidities.reduce(min);

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if ((maxH - minH) > 20) {
        return "Humidity’s bouncing all over... weird frog day ~ froggy :3";
      }
      return "Humidity looks steady... kinda comfy actually ~ froggy :3";
    }

    if ((maxH - minH) > 20) {
      return "insights_sub_30".tr();
    }
    return "insights_sub_31".tr();
  }

  String _cloudPatternInsight() {
    final clouds = widget.hourlyData
        .map((d) => d['cloud_cover'] ?? 0)
        .cast<num>()
        .toList();
    final avgCloud = clouds.reduce((a, b) => a + b) / clouds.length;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (avgCloud > 70) {
        return "Sky’s all covered up today... kinda cozy ~ froggy :3";
      } else if (avgCloud < 30) {
        return "Not many clouds... sun's out and everything’s shiny ~ froggy :3";
      }
      return "Some clouds, some sun... looks balanced to me ~ froggy :3";
    }

    if (avgCloud > 70) {
      return "insights_sub_32".tr();
    } else if (avgCloud < 30) {
      return "insights_sub_33".tr();
    }
    return "insights_sub_34".tr();
  }

  String _precipitationWindowInsight() {
    final precipChances = widget.hourlyData
        .map((d) => d['precipitation_probability'] ?? 0)
        .cast<num>()
        .toList();
    final peak = precipChances.reduce(max);

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (peak > 70) {
        return "Might get real wet later... i’m into it ~ froggy :3";
      } else if (peak > 40) {
        return "Some little rain might show up... keep your things dry ~ froggy :3";
      }
      return "Not much rain in the air today... dry leaf kind of vibe ~ froggy :3";
    }

    if (peak > 70) {
      return "insights_sub_35".tr();
    } else if (peak > 40) {
      return "insights_sub_36".tr();
    }
    return "insights_sub_37".tr();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      margin: const EdgeInsets.fromLTRB(12.7, 0, 12.7, 0),
      decoration: BoxDecoration(
        color: Color(widget.selectedContainerBgIndex),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.15),
            spreadRadius: 1,
            blurRadius: 1,
            offset: Offset(0, 1),
          ),
        ],
      ),
      constraints: BoxConstraints(minHeight: 65),
      child: IntrinsicHeight(
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              width: 46,
              height: 46,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(50),
              ),
              child: Center(
                child: Icon(
                  iconData,
                  color: Theme.of(context).colorScheme.onPrimaryContainer,
                ),
              ),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Text(
                _insight,
                style: TextStyle(
                  fontSize: 14.5,
                  color: Theme.of(context).colorScheme.onSurface,
                  fontVariations: [
                    FontVariation('wght', 450),
                    FontVariation('ROND', 100),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
