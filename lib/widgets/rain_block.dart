import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import '../helper/locale_helper.dart';

class RainBlock extends StatelessWidget {
  final List<String> hourlyTime;
  final List<double> hourlyPrecp;
  final List<dynamic> hourlyPrecpProb;
  final int selectedContainerBgIndex;
  final String timezone;
  final String utcOffsetSeconds;

  const RainBlock(
      {super.key,
      required this.hourlyTime,
      required this.hourlyPrecp,
      required this.selectedContainerBgIndex,
      required this.timezone,
      required this.utcOffsetSeconds,
      required this.hourlyPrecpProb});

  int get _currentIndex {
    int offsetSeconds = int.parse(utcOffsetSeconds);
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

    for (int i = 0; i < hourlyTime.length; i++) {
      final dt = DateTime.parse(hourlyTime[i]);
      if (!dt.isBefore(now)) return i;
    }
    return 0; // fallback if all are in the past
  }

  List<String> get next12Time =>
      hourlyTime.skip(_currentIndex).take(12).toList();
  List<double> get next12Precp =>
      hourlyPrecp.skip(_currentIndex).take(12).toList();

  List<String> get next12TimeProb =>
      hourlyTime.skip(_currentIndex).take(12).toList();
  List get next12PrecpProb =>
      hourlyPrecpProb.skip(_currentIndex).take(12).toList();

  double get maxRain {
    final r = next12Precp.reduce((a, b) => a > b ? a : b);
    return (r < 3) ? 3 : (r * 1.3).ceilToDouble();
  }

  (int?, int?) getRainPeriod() {
    int? bestStart;
    int? bestEnd;
    int longestLength = 0;

    int? currentStart;

    for (int i = 0; i < next12Precp.length; i++) {
      final double precp = next12Precp[i];
      final int prob = next12PrecpProb[i] ?? 0;
      if (precp > 0.2 && prob >= 40) {
        currentStart ??= i;
      } else {
        if (currentStart != null) {
          final length = i - currentStart;
          if (length >= 2 && length > longestLength) {
            bestStart = currentStart;
            bestEnd = i - 1;
            longestLength = length;
          }
          currentStart = null; // reset
        }
      }
    }

    if (currentStart != null) {
      final length = next12Precp.length - currentStart;
      if (length >= 2 && length > longestLength) {
        bestStart = currentStart;
        bestEnd = next12Precp.length - 1;
      }
    }

    return (bestStart, bestEnd);
  }

  String _generateTitle(int? start) {
    if (start == null) return "rain_card_no_rain_exp".tr();

    if (start == 0 && next12Precp[0] > 0.2) {
      return willRainStopSoon()
          ? "rain_will_stop_soon".tr()
          : "its_currently_raining".tr();
    }

    final hour = DateTime.parse(next12Time[start]).hour;

    if (hour >= 0 && hour <= 5) return "rain_expected_overnight".tr();
    if (hour >= 6 && hour < 12) return "rain_expected_this_morning".tr();
    if (hour >= 12 && hour < 17) return "rain_expected_this_afternoon".tr();
    if (hour >= 17 && hour <= 22) return "rain_expected_later_today".tr();

    return "rain_expected_later_today".tr();
  }

  bool willRainStopSoon() {
    if (next12Precp[0] <= 0.2 || (next12PrecpProb[0] ?? 0) < 30) return false;

    int dryCount = 0;
    for (int i = 1; i < next12Precp.length; i++) {
      final double precp = next12Precp[i];
      final int prob = next12PrecpProb[i] ?? 0;

      if (precp <= 0.2 || prob < 30) {
        dryCount++;
        if (dryCount >= 2) return true;
      } else {
        dryCount = 0;
      }
    }
    return false;
  }

  Color _barColor(double mm, context) {
    if (mm > 5) return Theme.of(context).colorScheme.error;
    if (mm > 2) return Theme.of(context).colorScheme.tertiary;
    if (mm > 0.2) return Theme.of(context).colorScheme.primary;
    return Theme.of(context).colorScheme.primary;
  }

  @override
  Widget build(BuildContext context) {
    final timeUnit = context.watch<UnitSettingsNotifier>().timeUnit;

    String? generateSummary(int? start, int? end) {
      if (start == null || end == null) return null;

      final segment = next12Precp.sublist(start, end + 1);
      final max = segment.reduce((a, b) => a > b ? a : b);

      String label = switch (max) {
        > 5 => "heavy_rain".tr(),
        > 2 => "moderate_rain".tr(),
        _ => "light_rain".tr()
      };

      final startStr = timeUnit == '24 hr'
          ? DateFormat.Hm().format(DateTime.parse(next12Time[start]))
          : DateFormat.jm().format(DateTime.parse(next12Time[start]));
      final endStr = timeUnit == '24 hr'
          ? DateFormat.Hm().format(DateTime.parse(next12Time[end]))
          : DateFormat.jm().format(DateTime.parse(next12Time[end]));

      return "$label ${'from_text'.tr()} $startStr ${'to_text'.tr()} $endStr";
    }

    final (start, end) = getRainPeriod();
    final title = _generateTitle(start);
    final subtitle = generateSummary(start, end);
    final rain = next12Precp;
    final unitSettings =
        Provider.of<UnitSettingsNotifier>(context, listen: false);
    final precipitationUnit = unitSettings.precipitationUnit;

    return Container(
      margin: const EdgeInsets.only(bottom: 0, left: 12, right: 12, top: 0),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Color(selectedContainerBgIndex),
        borderRadius: BorderRadius.circular(18),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title,
              style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurface,
                  fontSize: 16,
                  fontWeight: FontWeight.w500)),
          if (subtitle != null)
            Padding(
              padding: const EdgeInsets.only(top: 2),
              child: Text(subtitle,
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.onSurfaceVariant,
                      fontSize: 13)),
            ),
          const SizedBox(height: 16),

          SizedBox(
            height: 90,
            child: BarChart(
              BarChartData(
                alignment: BarChartAlignment.start,
                maxY: maxRain,
                barTouchData: BarTouchData(
                  enabled: true,
                  touchTooltipData: BarTouchTooltipData(
                    tooltipBorderRadius: BorderRadius.circular(50),
                    getTooltipColor: (group) =>
                        Theme.of(context).colorScheme.primaryContainer,
                    tooltipPadding: EdgeInsets.only(left: 5, right: 5),
                    getTooltipItem: (group, groupIndex, rod, rodIndex) {
                      final convertedPrecip = precipitationUnit == 'cm'
                          ? UnitConverter.mmToCm(rod.toY)
                          : precipitationUnit == 'in'
                              ? UnitConverter.mmToIn(rod.toY)
                              : rod.toY;
                      return BarTooltipItem(
                        '${convertedPrecip.toStringAsFixed(1)} $precipitationUnit',
                        TextStyle(
                            color: Theme.of(context)
                                .colorScheme
                                .onPrimaryContainer,
                            fontWeight: FontWeight.w500),
                      );
                    },
                  ),
                ),
                borderData: FlBorderData(
                  show: true,
                  border: Border(
                    bottom: BorderSide(
                      color: Theme.of(context).colorScheme.outlineVariant,
                      width: 1,
                    ),
                  ),
                ),
                gridData: FlGridData(
                  show: true,
                  drawHorizontalLine: true,
                  drawVerticalLine: false,
                  horizontalInterval: maxRain / 3,
                  getDrawingHorizontalLine: (_) => FlLine(
                    color: Theme.of(context).colorScheme.outlineVariant,
                    strokeWidth: 1,
                  ),
                ),
                titlesData: FlTitlesData(
                  topTitles:
                      AxisTitles(sideTitles: SideTitles(showTitles: false)),
                  rightTitles:
                      AxisTitles(sideTitles: SideTitles(showTitles: false)),
                  leftTitles: AxisTitles(
                    sideTitles: SideTitles(
                      showTitles: true,
                      reservedSize: 50,
                      getTitlesWidget: (value, _) {
                        final roundedMax = maxRain;
                        final step = roundedMax / 2;
                        final convertedPrecip = precipitationUnit == 'cm'
                            ? UnitConverter.mmToCm(value)
                            : precipitationUnit == 'in'
                                ? UnitConverter.mmToIn(value)
                                : value;

                        if (value == 0 ||
                            value == step ||
                            value == roundedMax) {
                          return Text(
                            '${double.parse(convertedPrecip.toStringAsFixed(1))} ${localizePrecipUnit(precipitationUnit, context.locale)}',
                            style: TextStyle(
                                fontSize: 10,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant),
                          );
                        }
                        return const SizedBox.shrink();
                      },
                      interval: maxRain / 2,
                    ),
                  ),
                  bottomTitles: AxisTitles(
                    sideTitles: SideTitles(
                      showTitles: true,
                      reservedSize: 18,
                      getTitlesWidget: (value, _) {
                        final idx = value.toInt();
                        if (idx % 3 != 0 || idx >= next12Time.length)
                          return const SizedBox.shrink();
                        final dt = DateTime.parse(next12Time[idx]);
                        return Padding(
                            padding: EdgeInsets.only(left: 8),
                            child: Text(
                              timeUnit == '24 hr'
                                  ? DateFormat.Hm().format(dt)
                                  : DateFormat.jm().format(dt),
                              style: TextStyle(
                                  color: Theme.of(context)
                                      .colorScheme
                                      .onSurfaceVariant,
                                  fontSize: 9),
                            ));
                      },
                    ),
                  ),
                ),
                barGroups: List.generate(rain.length, (i) {
                  return BarChartGroupData(
                    x: i,
                    barRods: [
                      BarChartRodData(
                        toY: rain[i],
                        width: 15,
                        color: _barColor(rain[i], context),
                        borderRadius: BorderRadius.only(
                            topLeft: Radius.circular(50),
                            topRight: Radius.circular(50)),
                      ),
                    ],
                  );
                }),
              ),
            ),
          ),

          // ),
        ],
      ),
    );
  }
}
