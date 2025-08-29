import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/icon_map.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../utils/visual_utils.dart';

class HourlyCard extends StatelessWidget {
  final List<dynamic> hourlyTime;
  final List<dynamic> hourlyTemps;
  final List<dynamic> hourlyWeatherCodes;
  final bool Function(DateTime) isHourDuringDaylightOptimized;
  final int selectedContainerBgIndex;
  final String timezone;
  final String utcOffsetSeconds;
  final List<dynamic> hourlyPrecpProb;

  const HourlyCard(
      {super.key,
      required this.hourlyTime,
      required this.hourlyTemps,
      required this.hourlyWeatherCodes,
      required this.isHourDuringDaylightOptimized,
      required this.selectedContainerBgIndex,
      required this.timezone,
      required this.utcOffsetSeconds,
      required this.hourlyPrecpProb});

  @override
  Widget build(BuildContext context) {
    final offset = Duration(seconds: int.parse(utcOffsetSeconds));
    final nowUtc = DateTime.now().toUtc();
    final nowLocal = nowUtc.add(offset);
    final colorTheme = Theme.of(context).colorScheme;

    final timeUnit = context.watch<UnitSettingsNotifier>().timeUnit;
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;

    final roundedNow =
        DateTime(nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

    int startIndex = hourlyTime.indexWhere((timeStr) {
      final forecastLocal = DateTime.parse(timeStr);
      return !forecastLocal.isBefore(roundedNow);
    });

    if (startIndex == -1) startIndex = 0;
    final scale = MediaQuery.of(context).textScaler.scale(1.0);
    final extraHeight = (scale - 1.0) * 30;

    return Container(
      decoration: BoxDecoration(
        color: Color(selectedContainerBgIndex),
        borderRadius: BorderRadius.circular(20),
      ),
      padding: EdgeInsets.only(top: 15, bottom: 0),
      margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
      child: Column(
        children: [
          Row(crossAxisAlignment: CrossAxisAlignment.center, children: [
            SizedBox(
              width: 20,
            ),
            Icon(
              Symbols.schedule,
              weight: 500,
              color: colorTheme.secondary,
              size: 21,
              fill: 1,
            ),
            SizedBox(
              width: 5,
            ),
            Text("hourly_forecast".tr(),
                style: TextStyle(
                    color: colorTheme.secondary,
                    fontSize: 16,
                    fontWeight: FontWeight.w600)),
          ]),
          Divider(
            height: 6,
            color: Colors.transparent,
          ),
          SizedBox(
            height: 98 + extraHeight + 30,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              physics: BouncingScrollPhysics(),
              itemCount:
                  startIndex != null ? (48 - startIndex).clamp(0, 48) : 0,
              itemBuilder: (context, index) {
                // final time = DateTime.parse(hourlyTime[index]);
                final dataIndex = startIndex + index;
                final itemCount =
                    startIndex != null ? (48 - startIndex).clamp(0, 48) : 0;

                final isFirst = index == 0;
                final isLast = index == itemCount - 1;

                if (dataIndex >= hourlyTime.length) return const SizedBox();

                final forecastLocal = DateTime.parse(hourlyTime[dataIndex]);

                final roundedDisplayTime = DateTime(
                  forecastLocal.year,
                  forecastLocal.month,
                  forecastLocal.day,
                  forecastLocal.hour,
                );

                final hour = timeUnit == '24 hr'
                    ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00"
                    : UnitConverter.formatTo12Hour(roundedDisplayTime);
                final temp = tempUnit == 'Fahrenheit'
                    ? UnitConverter.celsiusToFahrenheit(
                            hourlyTemps[dataIndex].toDouble())
                        .round()
                    : hourlyTemps[dataIndex].toDouble().round();
                final code = hourlyWeatherCodes[dataIndex];
                final precipProb = hourlyPrecpProb[dataIndex] ?? 0.1111111;

                final isDay = isHourDuringDaylightOptimized(roundedDisplayTime);

                return Container(
                  clipBehavior: Clip.none,
                  width: 56,
                  margin: EdgeInsets.only(
                      right: isLast ? 10 : 0, left: isFirst ? 10 : 0),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      const SizedBox(height: 3),
                      Stack(
                        clipBehavior: Clip.none,
                        alignment: Alignment.center,
                        children: [
                          Positioned(

                              // bottom: -10,
                              child: SvgPicture.string(
                            buildNowHourSvg(isFirst
                                ? colorTheme.tertiary
                                : Color(selectedContainerBgIndex)),
                            width: 42,
                            height: 42,
                          )),
                          Transform(
                            transform: Matrix4.translationValues(
                                0, isFirst ? 0 : 0, 0),
                            child: Text(
                              "${temp}°",
                              style: TextStyle(
                                fontSize: 16,
                                color: isFirst
                                    ? colorTheme.onTertiary
                                    : colorTheme.onSurface,
                              ),
                              textHeightBehavior: TextHeightBehavior(
                                  applyHeightToFirstAscent: false,
                                  applyHeightToLastDescent: false),
                            ),
                          ),
                        ],
                      ),
                      Text(
                          precipProb == 0.1111111
                              ? '--%'
                              : precipProb > 10
                                  ? "${precipProb.round()}%"
                                  : "‎",
                          style: TextStyle(
                            fontWeight: FontWeight.w700,
                            fontSize: 12.5,
                            color: colorTheme.primary,
                          )),
                      SvgPicture.asset(
                        WeatherIconMapper.getIcon(code, isDay ? 1 : 0),
                        width: 26,
                      ),
                      SizedBox(
                        height: 5,
                      ),
                      Text(hour,
                          style: TextStyle(
                            fontSize: 14,
                            color: colorTheme.onSurfaceVariant,
                          )),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
