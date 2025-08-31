import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import '../utils/icon_map.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../screens/daily_forecast.dart';
import '../controllers/home_f.dart';

class DailyCard extends StatelessWidget {
  final List<dynamic> dailyTime;
  final List<dynamic> dailyTempsMin;
  final List<dynamic> dailyWeatherCodes;
  final List<dynamic> dailyTempsMax;
  final List<dynamic> dailyPrecProb;
  final int selectedContainerBgIndex;
  final String utcOffsetSeconds;

  DailyCard(
      {super.key,
      required this.dailyTime,
      required this.dailyTempsMin,
      required this.dailyWeatherCodes,
      required this.dailyTempsMax,
      required this.dailyPrecProb,
      required this.utcOffsetSeconds,
      required this.selectedContainerBgIndex});

  @override
  Widget build(BuildContext context) {
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;
    final colorTheme = Theme.of(context).colorScheme;

    final isShowFrog = context.read<UnitSettingsNotifier>().showFrog;

    num convert(num celsius) => tempUnit == "Fahrenheit"
        ? UnitConverter.celsiusToFahrenheit(celsius.toDouble()).round()
        : celsius.round();

    final List<Map<String, dynamic>> validDailyData = [];

    for (int i = 0; i < dailyTime.length; i++) {
      if (i < dailyTempsMin.length &&
          i < dailyTempsMax.length &&
          i < dailyWeatherCodes.length &&
          i < dailyPrecProb.length &&
          dailyTime[i] != null &&
          dailyTempsMin[i] != null &&
          dailyTempsMax[i] != null &&
          dailyWeatherCodes[i] != null) {
        validDailyData.add({
          "time": dailyTime[i],
          "tempMin": dailyTempsMin[i],
          "tempMax": dailyTempsMax[i],
          "weatherCode": dailyWeatherCodes[i],
          "precipProb": (dailyPrecProb[i] as num?)?.toDouble() ?? 0.0000001,
        });
      }
    }

    return Container(
      decoration: BoxDecoration(
        color: Color(selectedContainerBgIndex),
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
      padding: EdgeInsets.only(
        top: 15,
        bottom: 0,
      ),
      margin: EdgeInsets.fromLTRB(13, 0, 13, 0),
      child: Column(
        children: [
          Row(crossAxisAlignment: CrossAxisAlignment.center, children: [
            SizedBox(
              width: 20,
            ),
            Icon(
              Symbols.calendar_month,
              weight: 500,
              color: colorTheme.secondary,
              size: 21,
              fill: 1,
            ),
            SizedBox(
              width: 5,
            ),
            Text("daily_forecast".tr(),
                style: TextStyle(
                    color: colorTheme.secondary,
                    fontSize: 16,
                    fontWeight: FontWeight.w600)),
          ]),
          Divider(
            height: 14,
            color: Colors.transparent,
          ),
          SizedBox(
            height: 213,
            child: ListView.separated(
              scrollDirection: Axis.horizontal,
              physics: BouncingScrollPhysics(),
              itemCount: validDailyData.length,
              separatorBuilder: (context, index) => const SizedBox(width: 5),
              itemBuilder: (context, index) {
                final item = validDailyData[index];
                final time = DateTime.parse(item["time"]);
                final tempMax = convert(item["tempMax"]);
                final tempMin = convert(item["tempMin"]);
                final code = item["weatherCode"];
                final precipProb = item["precipProb"];

                EdgeInsets itemMargin = EdgeInsets.only(
                  left: index == 0 ? 15 : 0,
                  right: index == dailyTime.length - 1 ? 15 : 0,
                );

                return RepaintBoundary(
                  child: GestureDetector(
                    onTap: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(
                          builder: (_) => DailyForecastPage(
                            initialSelectedDate: time,
                          ),
                        ),
                      );
                    },
                    child: Container(
                      width: 68,
                      margin: itemMargin,
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(99),
                          color:
                              Theme.of(context).brightness == Brightness.light
                                  ? colorTheme.surfaceContainer
                                  : !isShowFrog
                                      ? colorTheme.surfaceContainerLow
                                          .withValues(alpha: 0.6)
                                      : Color.fromRGBO(0, 0, 0, 0.247)),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          SizedBox(
                            height: 10,
                          ),
                          Column(
                            children: [
                              Text("${tempMax.round()}°",
                                  style: TextStyle(
                                    fontSize: 16,
                                    color: colorTheme.onSurface,
                                  )),
                              Text("${tempMin.round()}°",
                                  style: TextStyle(
                                    fontSize: 16,
                                    color: colorTheme.onSurfaceVariant,
                                  )),
                            ],
                          ),
                          SizedBox(
                            height: 10,
                          ),
                          SvgPicture.asset(
                            WeatherIconMapper.getIcon(code, 1),
                            width: 35,
                          ),
                          SizedBox(
                            height: 5,
                          ),
                          Column(children: [
                            Text(
                                precipProb == 0.0000001
                                    ? '--'
                                    : "${precipProb.round()}%",
                                style: TextStyle(
                                  fontSize: 14,
                                  color: colorTheme.primary,
                                  fontWeight: FontWeight.w600,
                                )),
                            SizedBox(
                              height: 3,
                            ),
                            Text(
                              getDayLabel(time, index, utcOffsetSeconds)
                                  .toLowerCase()
                                  .tr(),
                              style: const TextStyle(
                                fontSize: 14,
                              ),
                              textAlign: TextAlign.center,
                            ),
                            Text(
                              getLocalizedDateFormat(
                                  time, Localizations.localeOf(context)),
                              style: TextStyle(
                                fontSize: 13,
                                color: colorTheme.onSurfaceVariant,
                              ),
                              textAlign: TextAlign.center,
                            ),
                          ]),
                          SizedBox(
                            height: 10,
                          ),
                        ],
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
          SizedBox(
            height: 14,
          ),
        ],
      ),
    );
  }
}

String getLocalizedDateFormat(DateTime time, Locale locale) {
  final lang = locale.languageCode;
  final country = locale.countryCode;

  if (lang == 'en' && country == 'US') {
    return DateFormat('MM/dd').format(time);
  } else if (lang == 'ja') {
    return DateFormat('MM月dd日', 'ja').format(time);
  } else if (lang == 'zh' && country == 'CN' || country == "TW") {
    return DateFormat('MM月dd日', 'zh_CN').format(time);
  } else if (lang == 'fa') {
    return DateFormat('yyyy/MM/dd', 'fa').format(time);
  } else if (lang == 'de') {
    return DateFormat('dd.MM').format(time);
  }

  return DateFormat('dd/MM').format(time);
}
