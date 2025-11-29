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
      MapEntry(_comfortIndexInsight, Symbols.spa),
      MapEntry(_clothingInsight, Symbols.checkroom),
      MapEntry(_visibilityInsight, Symbols.visibility),
      MapEntry(_recordInsight, Symbols.emoji_events),
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
        return "It’s getting colder by the weekend… I’m grabbing a big leaf to hide under, ribbit";
      } else if (diff > 3) {
        return "Things are warming up… not my vibe, but hey, maybe you like toasty air";
      } else {
        return "Looks like the weather’s staying steady… calm days make a calm frog";
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
        return "Might get a real good rain on ${_weekdayName(peakDay)}… I’m already doing my happy splash dance";
      } else if (rainChances.any((r) => r > 40)) {
        return "Some days look kinda splashy… maybe keep your stuff dry while I enjoy the puddles";
      }
      return "Dry skies all week… guess I’ll just sit and stare dramatically into the distance";
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
        return "Too many hot days coming… I’m sinking into the pond until further notice";
      } else if (hotDays >= 1) {
        return "There’s at least one toasty day ahead… terrible for hopping, great for melting";
      }
      return "No major heat this week… perfect temperature for a lazy sun sit";
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
        return "Humidity’s climbing… great for my skin, not sure about yours";
      } else if (start > end + 10) {
        return "Air’s drying out… feels weird on my frog body but whatever";
      }
      return "Humidity’s pretty normal… nothing screaming for attention";
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
        return "Way too windy… almost got blown off my log last time. Not again";
      } else if (maxWind > 20) {
        return "Kinda breezy… the reeds are doing that wiggly dance I like";
      }
      return 'Pretty calm today… perfect for staying still and pretending I’m a moss rock';
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
        return "$sunnyDays nice days ahead… I’m claiming the warmest rock before anyone else";
      }
      return "Some good days, some meh days… that’s life, ribbit.";
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
        return "Big temperature jumps today… I’d bring a leaf AND a tiny blanket if I had one";
      }
      return "Temps look steady… peaceful hopping conditions";
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
        return "Sun’s way too spicy today… I’m hiding in the shade like a wise frog";
      } else if (peakUV >= 4) {
        return "Sun’s a little strong… I’ll be near the cattails in case I need a quick escape";
      }
      return "Low sun today… perfect lighting for dramatic posing";
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
        return "The wind’s going wild today… hold onto your hat, if you’re the kinda creature that wears one";
      } else if (diff > 15) {
        return "Wind’s acting strange… up, down, sideways… chaotic frog energy";
      }
      return "Air’s calm today… I’m sticking to my log, literally";
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
        return "Humidity’s bouncing all over… weird day for a frog with sensitive vibes";
      }
      return "Humidity’s steady… feels pretty comfy actually";
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
        return "Sky’s all covered… makes the world feel soft and cozy";
      } else if (avgCloud < 30) {
        return "Almost no clouds… everything’s shiny and kinda blinding";
      }
      return "Bit of clouds, bit of sun… balanced, like a well-hydrated frog";
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
        return "Might get real wet later… excellent news for me personally";
      } else if (peak > 40) {
        return "Some light rain might wander through… keep stuff dry while I enjoy the sprinkles";
      }
      return "Looks pretty dry today… guess I’ll vibe on a crisp leaf";
    }

    if (peak > 70) {
      return "insights_sub_35".tr();
    } else if (peak > 40) {
      return "insights_sub_36".tr();
    }
    return "insights_sub_37".tr();
  }

  String _comfortIndexInsight() {
    final dewPoints = widget.hourlyData
        .map((d) => d['dew_point_2m'] ?? 0)
        .cast<num>()
        .toList();
    final avgDew = dewPoints.reduce((a, b) => a + b) / dewPoints.length;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (avgDew >= 20) {
        return "Sticky air incoming… feels like home, kind of swampy, kind of gross";
      } else if (avgDew >= 13) {
        return "Air feels comfy… great for slow, lazy sitting";
      }
      return "Dry air today… crisp hopping weather";
    }

    if (avgDew >= 20) return "insights_sub_38".tr();
    if (avgDew >= 13) return "insights_sub_39".tr();
    return "insights_sub_40".tr();
  }

  String _clothingInsight() {
    final temps = widget.dailyData
        .map((d) => d['temperature_2m_max'] ?? 0)
        .cast<num>()
        .toList();
    final avgTemp = temps.reduce((a, b) => a + b) / temps.length;

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (avgTemp >= 30) {
        return "Super hot week… if I could, I’d just chill in the pond all day";
      } else if (avgTemp >= 20) {
        return "Warm days ahead… you might wear shorts, I’m sticking with my classic no-clothes look";
      } else if (avgTemp >= 10) {
        return "Cooler air coming… you may want a jacket; I’m fine because I’m slimy";
      }
      return "Cold week… bundle up or find a warm rock to sit on";
    }

    if (avgTemp >= 30) return "insights_sub_41".tr();
    if (avgTemp >= 20) return "insights_sub_42".tr();
    if (avgTemp >= 10) return "insights_sub_43".tr();
    return "insights_sub_44".tr();
  }

  String _visibilityInsight() {
    final visibilities = widget.hourlyData
        .map((d) => d['visibility'] ?? 10000) // meters
        .cast<num>()
        .toList();
    final minVis = visibilities.reduce(min);

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (minVis < 500) {
        return "Foggy morning ahead… tough to spot bugs, I gotta rely on my ears";
      } else if (minVis < 2000) {
        return "A little misty… makes the pond look mystical, I like it";
      }
      return "Clear visibility… I can see all the way across the reeds";
    }

    if (minVis < 500) return "insights_sub_45".tr();
    if (minVis < 2000) return "insights_sub_46".tr();
    return "insights_sub_47".tr();
  }

  String _recordInsight() {
    final temps = widget.dailyData
        .map((d) => d['temperature_2m_max'] ?? 0)
        .cast<num>()
        .toList();
    final maxTemp = temps.reduce(max);

    if (PreferencesHelper.getBool('useFroggyInsights') == true) {
      if (maxTemp >= 40) {
        return "Whoa… might hit record heat. I’m diving deep until it’s over";
      } else if (maxTemp <= -10) {
        return "Brr… maybe record cold. My toes are shaking just thinking about it";
      }
      return "Nothing record-breaking… just normal weather stuff";
    }

    if (maxTemp >= 40) return "insights_sub_48".tr();
    if (maxTemp <= -10) return "insights_sub_49".tr();
    return "insights_sub_50".tr();
  }

  @override
  Widget build(BuildContext context) {
    final colorTheme = Theme.of(context).colorScheme;

    return Padding(
        padding: const EdgeInsets.symmetric(horizontal: 12.7),
        child: Material(
            elevation: 1,
            borderRadius: BorderRadius.circular(20),
            color: Color(widget.selectedContainerBgIndex),
            child: Container(
                padding: EdgeInsets.only(top: 15, bottom: 10 + 5),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            SizedBox(
                              width: 20,
                            ),
                            Icon(
                              iconData,
                              weight: 500,
                              color: colorTheme.secondary,
                              size: 21,
                              fill: 1,
                            ),
                            SizedBox(
                              width: 5,
                            ),
                            Text("insights".tr(),
                                style: TextStyle(
                                    color: colorTheme.secondary,
                                    fontSize: 16,
                                    fontWeight: FontWeight.w600)),
                          ]),
                      Divider(
                        height: 10,
                        color: Colors.transparent,
                      ),
                      Padding(
                          padding: EdgeInsets.only(left: 20, right: 20),
                          child: Text(_insight,
                              style: TextStyle(
                                fontSize: 14.5,
                                color: Theme.of(context).colorScheme.onSurface,
                              )))
                    ]))));
  }
}
