import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'dart:convert';
import '../utils/preferences_helper.dart';
import 'package:hive/hive.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/unit_converter.dart';
import 'package:solar_calculator/solar_calculator.dart';
import 'dart:math' as math;
import '../helper/locale_helper.dart';
import '../utils/visual_utils.dart';

class ExtendWidget extends StatefulWidget {
  final String widgetType;
  const ExtendWidget(this.widgetType, {super.key});

  @override
  State<ExtendWidget> createState() => _ExtendWidgetState();
}

class _ExtendWidgetState extends State<ExtendWidget> {
  late final Widget child;
  late final String extendedTitle;
  late final IconData? iconData;

  Future<Map<String, dynamic>?> getWeatherWidgets() async {
    final cacheKey = PreferencesHelper.getJson('currentLocation')?['cacheKey'];

    final box = await Hive.openBox('weatherMasterCache');
    final cached = box.get(cacheKey);
    if (cached == null) return null;

    return json.decode(cached);
  }

  @override
  void initState() {
    super.initState();
    if (widget.widgetType == 'humidity_widget') {
      child = buildHumidityExtended();
      extendedTitle = 'humidity'.tr();
      iconData = Symbols.humidity_mid;
    } else if (widget.widgetType == 'sun_widget') {
      child = buildSunExtended();
      extendedTitle = 'sun_tile_page'.tr();
      iconData = Symbols.wb_twilight;
    } else if (widget.widgetType == 'pressure_widget') {
      child = buildPressureExtended();
      extendedTitle = 'pressure'.tr();
      iconData = Symbols.compress;
    } else if (widget.widgetType == 'visibility_widget') {
      child = buildVisibilityExtended();
      extendedTitle = 'visibility'.tr();
      iconData = Symbols.visibility;
    } else if (widget.widgetType == 'winddirc_widget') {
      child = buildWindExtended();
      extendedTitle = 'wind'.tr();
      iconData = Symbols.air;
    } else if (widget.widgetType == 'uv_widget') {
      child = buildUVExtended();
      extendedTitle = 'uv_index'.tr();
      iconData = Symbols.flare;
    } else if (widget.widgetType == 'aqi_widget') {
      child = buildAQIExtended();
      extendedTitle = 'air_quality'.tr();
      iconData = Symbols.airwave;
    } else if (widget.widgetType == 'precip_widget') {
      child = buildPrecipExtended();
      extendedTitle = 'precipitation'.tr();
      iconData = Symbols.rainy_heavy;
    } else if (widget.widgetType == 'moon_widget') {
      child = buildMoonExtended();
      extendedTitle = 'moon'.tr();
      iconData = Symbols.nightlight;
    } else {
      child = const Center(child: Text('Unknown widget type'));
      extendedTitle = 'Error';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: CustomScrollView(slivers: [
      SliverAppBar.large(
        titleSpacing: 0,
        automaticallyImplyLeading: false,
        flexibleSpace: FlexibleSpaceBar(
          title: Row(
            spacing: 5,
            children: [Icon(iconData), Text(extendedTitle)],
          ),
          expandedTitleScale: 1.3,
          titlePadding: EdgeInsets.all(16),
        ),
        backgroundColor: Theme.of(context).colorScheme.surface,
        expandedHeight: 100,
        scrolledUnderElevation: 1,
        actions: [
          IconButton(
              onPressed: () {
                Navigator.pop(context);
              },
              icon: Icon(
                Symbols.close,
                weight: 600,
              )),
          SizedBox(width: 5)
        ],
      ),
      SliverToBoxAdapter(
        child: child,
      )
    ]));
  }

  Widget buildHumidityExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
      future: getWeatherWidgets(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        } else if (!snapshot.hasData || snapshot.data == null) {
          return Center(child: Text('no_data_available'.tr()));
        }

        final data = snapshot.data!;
        final weather = data['data'];

        final hourly = weather['hourly'];
        final List<dynamic> hourlyTime = hourly['time'];
        final List<dynamic> hourlyhumidity = hourly['relative_humidity_2m'];

        final offset = Duration(
            seconds: int.parse(weather['utc_offset_seconds'].toString()));
        final nowUtc = DateTime.now().toUtc();
        final nowLocal = nowUtc.add(offset);

        final timeUnit =
            PreferencesHelper.getString("selectedTimeUnit") ?? "12 hr";

        final roundedNow = DateTime(
            nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

        int startIndex = hourlyTime.indexWhere((timeStr) {
          final forecastLocal = DateTime.parse(timeStr);
          return !forecastLocal.isBefore(roundedNow);
        });

        if (startIndex == -1) startIndex = 0;

        List<int> todayHumidities = [];

        for (int i = 1; i <= 23 && i < hourlyhumidity.length; i++) {
          final humidity = hourlyhumidity[i];
          if (humidity is int) {
            todayHumidities.add(humidity);
          }
        }

        final int avgHumidity = todayHumidities.isNotEmpty
            ? (todayHumidities.reduce((a, b) => a + b) ~/
                todayHumidities.length)
            : 0;

        return Column(children: [
          Container(
            height: 360,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surfaceContainer,
              borderRadius: BorderRadius.circular(18),
            ),
            padding: EdgeInsets.only(top: 12, bottom: 0),
            margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
            child: Column(
              children: [
                Container(
                  padding: EdgeInsets.fromLTRB(20, 10, 20, 0),
                  alignment: Alignment.centerLeft,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text("todays_avg".tr(),
                          style: TextStyle(
                              fontSize: 20,
                              color: Theme.of(context).colorScheme.onSurface,
                              fontWeight: FontWeight.w500)),
                      Text(
                        "$avgHumidity%",
                        style: TextStyle(
                          fontSize: 50,
                          color: Theme.of(context).colorScheme.secondary,
                        ),
                      )
                    ],
                  ),
                ),
                SizedBox(
                  height: 225.2,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    physics: BouncingScrollPhysics(),
                    itemCount: 24 + 24 - startIndex,
                    itemBuilder: (context, index) {
                      final dataIndex = startIndex + index;

                      if (dataIndex >= hourlyTime.length)
                        return const SizedBox();
                      final forecastLocal =
                          DateTime.parse(hourlyTime[dataIndex]);

                      final roundedDisplayTime = DateTime(
                        forecastLocal.year,
                        forecastLocal.month,
                        forecastLocal.day,
                        forecastLocal.hour,
                      );
                      final hour = timeUnit == '24 hr'
                          ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00"
                          : UnitConverter.formatTo12Hour(roundedDisplayTime);
                      final humidityPercentage = hourlyhumidity[dataIndex];
                      ;

                      EdgeInsets itemMargin = EdgeInsets.only(
                        left: index == 0 ? 10 : 0,
                        right: index == 24 + 24 - startIndex - 1 ? 10 : 0,
                      );

                      return Container(
                        width: 53,
                        margin: itemMargin,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            Stack(
                                alignment: Alignment.bottomCenter,
                                clipBehavior: Clip.none,
                                children: [
                                  Container(
                                    width: 20,
                                    height: 160,
                                    decoration: BoxDecoration(
                                      color:
                                          Theme.of(context).colorScheme.surface,
                                      borderRadius: BorderRadius.circular(50),
                                    ),
                                  ),
                                  TweenAnimationBuilder<double>(
                                    tween: Tween<double>(
                                      begin: 0,
                                      end: math.max(
                                          (humidityPercentage / 100) * 160, 45),
                                    ),
                                    duration: const Duration(milliseconds: 500),
                                    curve: Curves.easeOutBack,
                                    builder: (context, value, child) {
                                      return Container(
                                        width: 43,
                                        height: value,
                                        decoration: BoxDecoration(
                                          color: Theme.of(context)
                                              .colorScheme
                                              .primary,
                                          borderRadius:
                                              BorderRadius.circular(50),
                                        ),
                                        child: child,
                                      );
                                    },
                                    child: Align(
                                      alignment: Alignment.topCenter,
                                      child: SizedBox(
                                        width: 60,
                                        height: 60,
                                        child: Stack(
                                            alignment: Alignment.center,
                                            children: [
                                              Positioned(
                                                top: 0,
                                                child: SvgPicture.string(
                                                  '''<svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20.3091 8.60363C20.4924 8.454 20.584 8.37919 20.6677 8.31603C22.6389 6.82799 25.3611 6.82799 27.3323 8.31603C27.416 8.37919 27.5076 8.454 27.6909 8.60363C27.7727 8.67042 27.8136 8.70381 27.8541 8.7356C28.7818 9.46445 29.9191 9.87748 31.0993 9.91409C31.1508 9.91569 31.2037 9.91634 31.3094 9.91765C31.5462 9.92059 31.6646 9.92206 31.7694 9.92733C34.2381 10.0516 36.3234 11.7974 36.8747 14.2015C36.8982 14.3036 36.9202 14.4197 36.9642 14.6518C36.9838 14.7555 36.9937 14.8073 37.0042 14.8576C37.2452 16.0109 37.8504 17.0567 38.7309 17.8416C38.7693 17.8759 38.8094 17.9103 38.8895 17.9791C39.069 18.1332 39.1588 18.2102 39.2357 18.2815C41.0467 19.96 41.5194 22.6347 40.393 24.8299C40.3451 24.9231 40.2872 25.0262 40.1714 25.2322C40.1196 25.3242 40.0938 25.3702 40.0694 25.4155C39.5111 26.4536 39.3009 27.6429 39.4697 28.8088C39.4771 28.8597 39.4856 28.9117 39.5027 29.0158C39.5409 29.249 39.56 29.3656 39.573 29.4695C39.879 31.9168 38.5179 34.2689 36.2407 35.2281C36.1441 35.2688 36.0333 35.3106 35.8118 35.3942C35.7129 35.4315 35.6635 35.4501 35.6156 35.4692C34.5192 35.9063 33.592 36.6826 32.9701 37.684C32.943 37.7277 32.916 37.7731 32.862 37.8637C32.741 38.0669 32.6806 38.1685 32.6236 38.2564C31.2814 40.3273 28.7233 41.2563 26.3609 40.5306C26.2606 40.4998 26.1489 40.4608 25.9253 40.3827C25.8256 40.3479 25.7757 40.3305 25.7268 40.3144C24.6052 39.9461 23.3948 39.9461 22.2732 40.3144C22.2243 40.3305 22.1744 40.3479 22.0747 40.3827C21.8511 40.4608 21.7394 40.4998 21.6391 40.5306C19.2767 41.2563 16.7186 40.3273 15.3764 38.2564C15.3194 38.1685 15.259 38.0669 15.138 37.8637C15.084 37.7731 15.057 37.7277 15.0299 37.684C14.408 36.6826 13.4808 35.9063 12.3844 35.4692C12.3365 35.4501 12.2871 35.4315 12.1882 35.3942C11.9667 35.3106 11.8559 35.2688 11.7593 35.2281C9.48205 34.2689 8.12097 31.9168 8.42698 29.4695C8.43997 29.3656 8.45908 29.249 8.4973 29.0158C8.51436 28.9117 8.52289 28.8597 8.53026 28.8088C8.69906 27.6429 8.48889 26.4536 7.93056 25.4155C7.90621 25.3702 7.88035 25.3242 7.82863 25.2322C7.71278 25.0262 7.65485 24.9231 7.60704 24.8299C6.48057 22.6347 6.95327 19.96 8.76433 18.2815C8.8412 18.2102 8.93096 18.1332 9.11047 17.9791C9.19061 17.9103 9.23068 17.8759 9.26908 17.8416C10.1496 17.0567 10.7548 16.0109 10.9958 14.8576C11.0063 14.8073 11.0162 14.7555 11.0358 14.6518C11.0798 14.4197 11.1019 14.3036 11.1253 14.2015C11.6766 11.7974 13.7619 10.0516 16.2306 9.92733C16.3354 9.92206 16.4538 9.92059 16.6906 9.91765C16.7963 9.91634 16.8492 9.91569 16.9007 9.91409C18.0809 9.87748 19.2182 9.46445 20.1459 8.7356C20.1864 8.70381 20.2273 8.67042 20.3091 8.60363Z" fill="#${Theme.of(context).colorScheme.primaryContainer.value.toRadixString(16).padLeft(8, '0').substring(2)}"/>
                            </svg> ''',
                                                ),
                                              ),
                                              Positioned(
                                                top: 0,
                                                child: SizedBox(
                                                    height: 48,
                                                    child: Center(
                                                      child: Text(
                                                          "$humidityPercentage",
                                                          style: TextStyle(
                                                            fontSize: 16,
                                                            color: Theme.of(
                                                                    context)
                                                                .colorScheme
                                                                .primary,
                                                          )),
                                                    )),
                                              )
                                            ]),
                                      ),
                                    ),
                                  ),
                                ]),
                            const SizedBox(height: 10),
                            Text(hour,
                                style: TextStyle(
                                    fontSize: 15,
                                    color:
                                        Theme.of(context).colorScheme.onSurface,
                                    fontWeight: FontWeight.w500)),
                          ],
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
          Container(
              margin: EdgeInsets.fromLTRB(
                  12, 20, 12, MediaQuery.of(context).padding.bottom + 26),
              padding: EdgeInsets.all(20),
              decoration: BoxDecoration(
                border: Border.all(
                  width: 1,
                  color: Theme.of(context).colorScheme.outlineVariant,
                ),
                borderRadius: BorderRadius.circular(18),
              ),
              child: Column(
                spacing: 20,
                children: [
                  Text("humidity_info".tr(),
                      style: TextStyle(
                        fontSize: 17,
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                      )),
                  Text("humidity_info_2".tr(),
                      style: TextStyle(
                        fontSize: 17,
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                      )),
                ],
              ))
        ]);
      },
    );
  }

  Widget buildSunExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];

          final daily = weather['daily'];

          final currentSunrise = daily['sunrise'][0];
          final currentSunset = daily['sunset'][0];

          int offsetSeconds =
              int.parse(weather['utc_offset_seconds'].toString());
          DateTime utcNow = DateTime.now().toUtc();
          DateTime now = utcNow.add(Duration(seconds: offsetSeconds));
          DateTime sunrise = DateTime.parse(currentSunrise);
          DateTime sunset = DateTime.parse(currentSunset);

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

          final timeUnit =
              PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';

          final sunriseFormat = timeUnit == '24 hr'
              ? DateFormat.Hm().format(sunrise)
              : DateFormat.jm().format(sunrise);
          final sunsetFormat = timeUnit == '24 hr'
              ? DateFormat.Hm().format(sunset)
              : DateFormat.jm().format(sunset);

          if (sunset.isBefore(sunrise)) {
            sunset = sunset.add(Duration(days: 1));
          }

          final timeZoneOffset = offsetSeconds / 3600.0;
          final instant = Instant(
            year: now.year,
            month: now.month,
            day: now.day,
            hour: now.hour,
            minute: now.minute,
            second: now.second,
            timeZoneOffset: timeZoneOffset,
          );

          String formatInstantToLocalTime(Instant instant,
              {String timeUnit = '24 hr'}) {
            final dateTime = DateTime(
              instant.year,
              instant.month,
              instant.day,
              instant.hour,
              instant.minute,
              instant.second,
            ).toLocal();

            final formatter =
                timeUnit == '24 hr' ? DateFormat.Hm() : DateFormat.jm();

            return formatter.format(dateTime);
          }

          final calc = SolarCalculator(
              instant, weather['latitude'], weather['longitude']);

          final timeFormatDUSKDAWN =
              timeUnit == '24 hr' ? DateFormat.Hm() : DateFormat.jm();

          final dawn = calc.morningCivilTwilight.begining;
          final dusk = calc.eveningCivilTwilight.ending;

          final dawnFormatted = formatInstantToLocalTime(dawn,
              timeUnit: timeFormatDUSKDAWN.toString());
          final duskFormatted = formatInstantToLocalTime(dusk,
              timeUnit: timeFormatDUSKDAWN.toString());

          double percent = ((now.difference(sunrise).inSeconds) /
                  (sunset.difference(sunrise).inSeconds))
              .clamp(0, 1);

          final isSmallScreen = MediaQuery.of(context).size.width < 400;

          final dawnWidget = Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text("dawn".tr(),
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.primary,
                      fontSize: 15)),
              Text(dawnFormatted,
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.primary,
                      fontSize: 18)),
            ],
          );

          final sunriseWidget = Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text("sunrise".tr(),
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.primary,
                      fontSize: 15)),
              Text(sunriseFormat,
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.primary,
                      fontSize: 18)),
            ],
          );

          final duskWidget = Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text("dusk".tr(),
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.secondary,
                      fontSize: 15)),
              Text(duskFormatted,
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.secondary,
                      fontSize: 18)),
            ],
          );

          final sunsetWidget = Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text("sunset".tr(),
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.secondary,
                      fontSize: 15)),
              Text(sunsetFormat,
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.secondary,
                      fontSize: 18)),
            ],
          );
          return Column(children: [
            Container(
              height: 250,
              margin: EdgeInsets.all(10),
              clipBehavior: Clip.hardEdge,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(28),
                  color: Theme.of(context).colorScheme.surfaceContainerLow),
              child: Stack(children: [
                Positioned(
                  bottom: -30,
                  left: 0,
                  right: 0,
                  child: AspectRatio(
                    aspectRatio: 1.6,
                    child: SvgPicture.string(
                      clipBehavior: Clip.none,
                      buildSunPathWithIcon(
                        pathColor: Theme.of(context).colorScheme.primary,
                        percent: percent,
                        outLineColor: Theme.of(context).colorScheme.onSurface,
                      ),
                      allowDrawingOutsideViewBox: true,
                      fit: BoxFit.fill,
                    ),
                  ),
                ),
                Positioned(
                  left: 0,
                  bottom: -1,
                  right: -1,
                  child: Container(
                    height: 100,
                    decoration: BoxDecoration(
                        border: Border(
                          top: BorderSide(
                            color: Theme.of(context).colorScheme.outline,
                            width: 1.5,
                          ),
                        ),
                        color: const Color.fromRGBO(0, 0, 0, 0.5),
                        borderRadius: BorderRadius.only(
                            bottomLeft: Radius.circular(20),
                            bottomRight: Radius.circular(20))),
                  ),
                ),
              ]),
            ),
            SizedBox(
              height: 10,
            ),
            isSmallScreen
                ? Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [dawnWidget, sunriseWidget],
                      ),
                      SizedBox(height: 10),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [duskWidget, sunsetWidget],
                      ),
                    ],
                  )
                : Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      dawnWidget,
                      sunriseWidget,
                      duskWidget,
                      sunsetWidget
                    ],
                  ),
            Container(
                margin: EdgeInsets.fromLTRB(
                    12, 20, 12, MediaQuery.of(context).padding.bottom + 26),
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(
                    width: 1,
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(18),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Text("sunset_rise_info".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("sunset_rise_info_2".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("sunset_rise_info_3".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("sunset_rise_info_4".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                  ],
                ))
          ]);
        });
  }

  Widget buildPressureExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];
          final pressureCurrent = weather['current'];
          final pressureHourly = weather['hourly']['pressure_msl'];

          final pressureUnit =
              PreferencesHelper.getString("selectedPressureUnit") ?? 'hPa';

          final convertedPressure = pressureUnit == 'inHg'
              ? UnitConverter.hPaToInHg(pressureCurrent['pressure_msl'])
              : pressureUnit == 'mmHg'
                  ? UnitConverter.hPaToMmHg(pressureCurrent['pressure_msl'])
                  : pressureCurrent['pressure_msl'];

          final hourly = weather['hourly'];
          final List<dynamic> hourlyTime = hourly['time'];
          final List<double> hourlyPressure =
              (hourly['pressure_msl'] as List<dynamic>)
                  .map((e) => (e as num).toDouble())
                  .toList();

          final offset = Duration(
              seconds: int.parse(weather['utc_offset_seconds'].toString()));
          final nowUtc = DateTime.now().toUtc();
          final nowLocal = nowUtc.add(offset);

          final timeUnit =
              PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';

          final roundedNow = DateTime(
              nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

          int startIndex = hourlyTime.indexWhere((timeStr) {
            final forecastLocal = DateTime.parse(timeStr);
            return !forecastLocal.isBefore(roundedNow);
          });

          if (startIndex == -1) startIndex = 0;

          final double minPressure =
              hourlyPressure.reduce((a, b) => a < b ? a : b);
          final double maxPressure =
              hourlyPressure.reduce((a, b) => a > b ? a : b);

          return Column(children: [
            Container(
              height: 360,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainer,
                borderRadius: BorderRadius.circular(18),
              ),
              padding: EdgeInsets.only(top: 12, bottom: 0),
              margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
              child: Column(
                children: [
                  Container(
                    padding: EdgeInsets.fromLTRB(20, 10, 20, 0),
                    alignment: Alignment.centerLeft,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text("current_conditions".tr(),
                            style: TextStyle(
                                fontSize: 20,
                                color: Theme.of(context).colorScheme.onSurface,
                                fontWeight: FontWeight.w500)),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              "${convertedPressure.round()}",
                              style: TextStyle(
                                fontSize: 50,
                                color: Theme.of(context).colorScheme.secondary,
                              ),
                            ),
                            Padding(
                                padding: EdgeInsets.only(bottom: 11, left: 8),
                                child: Text(
                                  localizePressureUnit(
                                      PreferencesHelper.getString(
                                              "selectedPressureUnit") ??
                                          'hPa',
                                      context.locale),
                                  style: TextStyle(
                                    fontSize: 20,
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                  ),
                                ))
                          ],
                        )
                      ],
                    ),
                  ),
                  SizedBox(
                    height: 225.2,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      physics: BouncingScrollPhysics(),
                      itemCount: 24 + 24 - startIndex,
                      itemBuilder: (context, index) {
                        final dataIndex = startIndex + index;

                        if (dataIndex >= hourlyTime.length)
                          return const SizedBox();
                        final forecastLocal =
                            DateTime.parse(hourlyTime[dataIndex]);

                        final roundedDisplayTime = DateTime(
                          forecastLocal.year,
                          forecastLocal.month,
                          forecastLocal.day,
                          forecastLocal.hour,
                        );
                        final hour = timeUnit == '24 hr'
                            ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00"
                            : UnitConverter.formatTo12Hour(roundedDisplayTime);
                        final currentPressure = hourlyPressure[dataIndex];
                        final pressurePercentage =
                            ((currentPressure - minPressure) /
                                    (maxPressure - minPressure)) *
                                100;

                        Widget? pressureTrendIcon;
                        Color? pressureprimary;

                        if (dataIndex > 0) {
                          final previousPressure =
                              hourlyPressure[dataIndex - 1];
                          final pressureDifference =
                              currentPressure - previousPressure;

                          if (pressureDifference > 0.5) {
                            pressureTrendIcon = Icon(Symbols.arrow_upward,
                                size: 18,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onPrimaryContainer,
                                weight: 600);
                            pressureprimary =
                                Theme.of(context).colorScheme.primaryContainer;
                          } else if (pressureDifference < -0.5) {
                            pressureTrendIcon = Icon(Symbols.arrow_downward,
                                size: 18,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onErrorContainer,
                                weight: 600);
                            pressureprimary =
                                Theme.of(context).colorScheme.errorContainer;
                          } else {
                            pressureTrendIcon = Icon(Symbols.trending_flat,
                                size: 18,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant,
                                weight: 600);
                            pressureprimary = Theme.of(context)
                                .colorScheme
                                .surfaceContainerLowest;
                          }
                        }

                        EdgeInsets itemMargin = EdgeInsets.only(
                          left: index == 0 ? 10 : 0,
                          right: index == 24 + 24 - startIndex - 1 ? 10 : 0,
                        );

                        return Container(
                          width: 53,
                          margin: itemMargin,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              Stack(
                                  alignment: Alignment.bottomCenter,
                                  clipBehavior: Clip.none,
                                  children: [
                                    Container(
                                      width: 20,
                                      height: 160,
                                      decoration: BoxDecoration(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .surface,
                                        borderRadius: BorderRadius.circular(50),
                                      ),
                                    ),
                                    TweenAnimationBuilder<double>(
                                      tween: Tween<double>(
                                        begin: 0,
                                        end: math.max(
                                            (pressurePercentage / 100) * 160,
                                            48),
                                      ),
                                      duration:
                                          const Duration(milliseconds: 500),
                                      curve: Curves.easeOutBack,
                                      builder: (context, value, child) {
                                        return Container(
                                          width: 43,
                                          height: value,
                                          decoration: BoxDecoration(
                                            color: Theme.of(context)
                                                .colorScheme
                                                .primary,
                                            borderRadius:
                                                BorderRadius.circular(50),
                                          ),
                                          child: child,
                                        );
                                      },
                                      child: Align(
                                        alignment: Alignment.topCenter,
                                        child: SizedBox(
                                          width: 60,
                                          height: 60,
                                          child: Stack(
                                              alignment: Alignment.center,
                                              children: [
                                                Positioned(
                                                  top: 0,
                                                  child: SvgPicture.string(
                                                    '''<svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20.3091 8.60363C20.4924 8.454 20.584 8.37919 20.6677 8.31603C22.6389 6.82799 25.3611 6.82799 27.3323 8.31603C27.416 8.37919 27.5076 8.454 27.6909 8.60363C27.7727 8.67042 27.8136 8.70381 27.8541 8.7356C28.7818 9.46445 29.9191 9.87748 31.0993 9.91409C31.1508 9.91569 31.2037 9.91634 31.3094 9.91765C31.5462 9.92059 31.6646 9.92206 31.7694 9.92733C34.2381 10.0516 36.3234 11.7974 36.8747 14.2015C36.8982 14.3036 36.9202 14.4197 36.9642 14.6518C36.9838 14.7555 36.9937 14.8073 37.0042 14.8576C37.2452 16.0109 37.8504 17.0567 38.7309 17.8416C38.7693 17.8759 38.8094 17.9103 38.8895 17.9791C39.069 18.1332 39.1588 18.2102 39.2357 18.2815C41.0467 19.96 41.5194 22.6347 40.393 24.8299C40.3451 24.9231 40.2872 25.0262 40.1714 25.2322C40.1196 25.3242 40.0938 25.3702 40.0694 25.4155C39.5111 26.4536 39.3009 27.6429 39.4697 28.8088C39.4771 28.8597 39.4856 28.9117 39.5027 29.0158C39.5409 29.249 39.56 29.3656 39.573 29.4695C39.879 31.9168 38.5179 34.2689 36.2407 35.2281C36.1441 35.2688 36.0333 35.3106 35.8118 35.3942C35.7129 35.4315 35.6635 35.4501 35.6156 35.4692C34.5192 35.9063 33.592 36.6826 32.9701 37.684C32.943 37.7277 32.916 37.7731 32.862 37.8637C32.741 38.0669 32.6806 38.1685 32.6236 38.2564C31.2814 40.3273 28.7233 41.2563 26.3609 40.5306C26.2606 40.4998 26.1489 40.4608 25.9253 40.3827C25.8256 40.3479 25.7757 40.3305 25.7268 40.3144C24.6052 39.9461 23.3948 39.9461 22.2732 40.3144C22.2243 40.3305 22.1744 40.3479 22.0747 40.3827C21.8511 40.4608 21.7394 40.4998 21.6391 40.5306C19.2767 41.2563 16.7186 40.3273 15.3764 38.2564C15.3194 38.1685 15.259 38.0669 15.138 37.8637C15.084 37.7731 15.057 37.7277 15.0299 37.684C14.408 36.6826 13.4808 35.9063 12.3844 35.4692C12.3365 35.4501 12.2871 35.4315 12.1882 35.3942C11.9667 35.3106 11.8559 35.2688 11.7593 35.2281C9.48205 34.2689 8.12097 31.9168 8.42698 29.4695C8.43997 29.3656 8.45908 29.249 8.4973 29.0158C8.51436 28.9117 8.52289 28.8597 8.53026 28.8088C8.69906 27.6429 8.48889 26.4536 7.93056 25.4155C7.90621 25.3702 7.88035 25.3242 7.82863 25.2322C7.71278 25.0262 7.65485 24.9231 7.60704 24.8299C6.48057 22.6347 6.95327 19.96 8.76433 18.2815C8.8412 18.2102 8.93096 18.1332 9.11047 17.9791C9.19061 17.9103 9.23068 17.8759 9.26908 17.8416C10.1496 17.0567 10.7548 16.0109 10.9958 14.8576C11.0063 14.8073 11.0162 14.7555 11.0358 14.6518C11.0798 14.4197 11.1019 14.3036 11.1253 14.2015C11.6766 11.7974 13.7619 10.0516 16.2306 9.92733C16.3354 9.92206 16.4538 9.92059 16.6906 9.91765C16.7963 9.91634 16.8492 9.91569 16.9007 9.91409C18.0809 9.87748 19.2182 9.46445 20.1459 8.7356C20.1864 8.70381 20.2273 8.67042 20.3091 8.60363Z" fill="#${pressureprimary?.value.toRadixString(16).padLeft(8, '0').substring(2)}"/>
                            </svg> ''',
                                                  ),
                                                ),
                                                Positioned(
                                                    top: 0,
                                                    child: SizedBox(
                                                      height: 48,
                                                      child: pressureTrendIcon,
                                                    ))
                                              ]),
                                        ),
                                      ),
                                    ),
                                  ]),
                              const SizedBox(height: 10),
                              Text("${pressureHourly[dataIndex].round()}",
                                  style: TextStyle(
                                    fontSize: 14,
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                  )),
                              Text(hour,
                                  style: TextStyle(
                                      fontSize: 15,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurface,
                                      fontWeight: FontWeight.w500)),
                            ],
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            ),
            Container(
                margin: EdgeInsets.fromLTRB(
                    12, 20, 12, MediaQuery.of(context).padding.bottom + 26),
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(
                    width: 1,
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(18),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Text("pressure_info".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("pressure_info_2".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                  ],
                ))
          ]);
        });
  }

  Widget buildVisibilityExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];
          final hourly = weather['hourly'];
          final List<dynamic> hourlyTime = hourly['time'];

          final currentVisibility = hourly['visibility'][getStartIndex(
                  weather['utc_offset_seconds'].toString(), hourlyTime)] ??
              0.0;
          final visibilityUnit =
              PreferencesHelper.getString("selectedVisibilityUnit") ?? "Km";

          final convertedVisibility = visibilityUnit == 'Mile'
              ? UnitConverter.mToMiles(currentVisibility.toDouble())
              : UnitConverter.mToKm(currentVisibility.toDouble());

          return Column(children: [
            Container(
              height: 130,
              clipBehavior: Clip.hardEdge,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainer,
                borderRadius: BorderRadius.circular(18),
              ),
              margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
              child: Stack(
                children: [
                  Positioned(
                    left: 0,
                    top: 0,
                    child: SvgPicture.string(
                      '''
                <svg height="112.0dip" width="364.0dip" viewBox="0 0 364.0 112.0" xmlns="http://www.w3.org/2000/svg">
                    <g>
                        <clip-path d="M20,0L344,0A20,20 0,0 1,364 20L364,92A20,20 0,0 1,344 112L20,112A20,20 0,0 1,0 92L0,20A20,20 0,0 1,20 0z" />
                        <path fill="#${Theme.of(context).colorScheme.tertiaryContainer.value.toRadixString(16).padLeft(8, '0').substring(2)}" d="M6.2,238.86C15.3,248.66 30.8,248.66 39.9,238.86V238.86C46.5,231.76 56.86,229.55 65.78,233.36V233.36C78.08,238.61 92.24,232.3 96.57,219.65V219.65C99.71,210.47 108.27,204.25 117.97,204.1V204.1C131.34,203.89 141.72,192.37 140.53,179.05V179.05C139.66,169.39 144.96,160.23 153.76,156.14V156.14C165.88,150.51 170.67,135.77 164.17,124.09V124.09C159.45,115.61 160.56,105.08 166.94,97.77V97.77C175.73,87.7 174.11,72.28 163.41,64.25V64.25C155.65,58.43 152.38,48.36 155.24,39.09V39.09C159.17,26.31 151.42,12.89 138.39,9.9V9.9C128.93,7.74 121.85,-0.13 120.68,-9.76V-9.76C119.08,-23.03 106.53,-32.15 93.41,-29.57V-29.57C83.89,-27.7 74.23,-32.01 69.24,-40.33V-40.33C62.38,-51.8 47.21,-55.03 36.28,-47.34V-47.34C28.34,-41.76 17.76,-41.76 9.82,-47.34V-47.34C-1.12,-55.03 -16.28,-51.8 -23.15,-40.33V-40.33C-28.13,-32.01 -37.8,-27.7 -47.32,-29.57V-29.57C-60.44,-32.15 -72.98,-23.03 -74.59,-9.76V-9.76C-75.75,-0.13 -82.83,7.74 -92.29,9.9V9.9C-105.32,12.89 -113.07,26.31 -109.14,39.09V39.09C-106.29,48.36 -109.56,58.43 -117.32,64.25V64.25C-128.01,72.28 -129.63,87.7 -120.84,97.77V97.77C-114.46,105.08 -113.36,115.61 -118.07,124.09V124.09C-124.58,135.77 -119.79,150.51 -107.66,156.14V156.14C-98.86,160.23 -93.57,169.39 -94.43,179.05V179.05C-95.62,192.37 -85.25,203.89 -71.88,204.1V204.1C-62.18,204.25 -53.62,210.47 -50.47,219.65V219.65C-46.14,232.3 -31.98,238.61 -19.68,233.36V233.36C-10.76,229.55 -0.41,231.76 6.2,238.86V238.86Z" />
                        <path fill="#${Theme.of(context).colorScheme.tertiaryContainer.value.toRadixString(16).padLeft(8, '0').substring(2)}"  d="M72.91,239.34C82.51,248.65 97.99,247.83 106.57,237.58V237.58C112.79,230.13 123.01,227.39 132.12,230.73V230.73C144.68,235.32 158.49,228.29 162.15,215.43V215.43C164.81,206.1 173.03,199.44 182.71,198.78V198.78C196.05,197.87 205.81,185.82 203.92,172.58V172.58C202.56,162.98 207.36,153.55 215.93,149.01V149.01C227.75,142.76 231.76,127.78 224.66,116.45V116.45C219.5,108.24 220.06,97.67 226.04,90.03V90.03C234.29,79.51 231.87,64.2 220.77,56.74V56.74C212.72,51.33 208.93,41.45 211.29,32.04V32.04C214.55,19.08 206.1,6.07 192.93,3.78V3.78C183.38,2.11 175.89,-5.37 174.22,-14.93V-14.93C171.93,-28.1 158.92,-36.55 145.96,-33.29V-33.29C136.55,-30.92 126.67,-34.72 121.26,-42.77V-42.77C113.8,-53.87 98.49,-56.29 87.97,-48.04V-48.04C80.33,-42.06 69.76,-41.5 61.55,-46.66V-46.66C50.22,-53.76 35.25,-49.75 28.99,-37.93V-37.93C24.45,-29.36 15.02,-24.56 5.42,-25.92V-25.92C-7.82,-27.81 -19.87,-18.05 -20.78,-4.71V-4.71C-21.44,4.97 -28.1,13.19 -37.43,15.85V15.85C-50.29,19.51 -57.32,33.32 -52.73,45.88V45.88C-49.39,54.99 -52.13,65.21 -59.58,71.44V71.44C-69.83,80.01 -70.64,95.49 -61.34,105.09V105.09C-54.59,112.06 -52.93,122.51 -57.2,131.23V131.23C-63.08,143.23 -57.53,157.71 -45.12,162.69V162.69C-36.12,166.31 -30.36,175.18 -30.71,184.88V184.88C-31.2,198.24 -20.24,209.2 -6.88,208.71V208.71C2.81,208.36 11.69,214.12 15.31,223.12V223.12C20.29,235.53 34.77,241.08 46.77,235.2V235.2C55.49,230.93 65.94,232.59 72.91,239.34V239.34Z" stroke-opacity="0.5" fill-opacity="0.5" />
                        <path fill="#${Theme.of(context).colorScheme.tertiaryContainer.value.toRadixString(16).padLeft(8, '0').substring(2)}"  d="M52.51,239.89C62.58,248.68 78,247.06 86.03,236.37V236.37C91.85,228.61 101.92,225.34 111.19,228.19V228.19C123.97,232.12 137.4,224.37 140.38,211.34V211.34C142.55,201.88 150.41,194.8 160.04,193.63V193.63C173.32,192.03 182.43,179.49 179.85,166.37V166.37C177.99,156.85 182.29,147.18 190.62,142.2V142.2C202.09,135.33 205.31,120.17 197.62,109.23V109.23C192.04,101.29 192.04,90.71 197.62,82.77V82.77C205.31,71.83 202.09,56.67 190.62,49.8V49.8C182.29,44.82 177.99,35.15 179.85,25.63V25.63C182.43,12.51 173.32,-0.03 160.04,-1.63V-1.63C150.41,-2.8 142.55,-9.88 140.38,-19.34V-19.34C137.4,-32.37 123.97,-40.12 111.19,-36.19V-36.19C101.92,-33.34 91.85,-36.61 86.03,-44.37V-44.37C78,-55.06 62.58,-56.68 52.51,-47.89V-47.89C45.2,-41.51 34.67,-40.4 26.2,-45.12V-45.12C14.52,-51.63 -0.23,-46.84 -5.86,-34.71V-34.71C-9.94,-25.91 -19.11,-20.62 -28.77,-21.48V-21.48C-42.09,-22.67 -53.61,-12.29 -53.82,1.07V1.07C-53.97,10.77 -60.19,19.34 -69.37,22.48V22.48C-82.02,26.81 -88.32,40.97 -83.08,53.27V53.27C-79.27,62.19 -81.47,72.54 -88.58,79.15V79.15C-98.37,88.25 -98.37,103.75 -88.58,112.85V112.85C-81.47,119.46 -79.27,129.81 -83.08,138.73V138.73C-88.32,151.03 -82.02,165.19 -69.37,169.52V169.52C-60.19,172.66 -53.97,181.23 -53.82,190.93V190.93C-53.61,204.29 -42.09,214.67 -28.77,213.48V213.48C-19.11,212.62 -9.94,217.91 -5.86,226.71V226.71C-0.23,238.84 14.52,243.63 26.2,237.12V237.12C34.67,232.4 45.2,233.51 52.51,239.89V239.89Z" stroke-opacity="0.5" fill-opacity="0.5" />
                    </g>
                </svg>
             ''',
                      height: 130,
                    ),
                  ),
                  Container(
                    padding: EdgeInsets.fromLTRB(20, 10 + 5, 20, 0),
                    alignment: Alignment.centerLeft,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text("current_conditions".tr(),
                            style: TextStyle(
                                fontSize: 20,
                                color: Theme.of(context).colorScheme.onSurface,
                                fontWeight: FontWeight.w500)),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              "${convertedVisibility.round()}",
                              style: TextStyle(
                                fontSize: 50,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onTertiaryContainer,
                              ),
                            ),
                            Padding(
                                padding: EdgeInsets.only(bottom: 11, left: 8),
                                child: Text(
                                  localizeVisibilityUnit(
                                      visibilityUnit, context.locale),
                                  style: TextStyle(
                                    fontSize: 20,
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onTertiaryContainer,
                                  ),
                                ))
                          ],
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            Container(
                margin: EdgeInsets.fromLTRB(
                    12, 20, 12, MediaQuery.of(context).padding.bottom + 26),
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(
                    width: 1,
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(18),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Text("visibility_info".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                  ],
                ))
          ]);
        });
  }

  Widget buildWindExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];

          final hourly = weather['hourly'];
          final List<dynamic> hourlyTime = hourly['time'];
          final List<double> windSpeeds =
              (hourly['wind_speed_10m'] as List<dynamic>)
                  .map((e) => (e as num).toDouble())
                  .toList();
          final List<dynamic> windDirections = hourly['wind_direction_10m'];

          final offset = Duration(
              seconds: int.parse(weather['utc_offset_seconds'].toString()));
          final nowUtc = DateTime.now().toUtc();
          final nowLocal = nowUtc.add(offset);

          final timeUnit =
              PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';

          final roundedNow = DateTime(
              nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

          int startIndex = hourlyTime.indexWhere((timeStr) {
            final forecastLocal = DateTime.parse(timeStr);
            return !forecastLocal.isBefore(roundedNow);
          });

          if (startIndex == -1) startIndex = 0;

          final double minWind = windSpeeds.reduce((a, b) => a < b ? a : b);
          final double maxWind = windSpeeds.reduce((a, b) => a > b ? a : b);

          List<double> todayWindSpeeds = [];

          for (int i = 8; i <= 23 && i < windSpeeds.length; i++) {
            final speed = windSpeeds[i];
            if (speed is num) {
              todayWindSpeeds.add(speed.toDouble());
            }
          }

          final double avgWind = todayWindSpeeds.isNotEmpty
              ? todayWindSpeeds.reduce((a, b) => a + b) / todayWindSpeeds.length
              : 0;
          final windUnit =
              PreferencesHelper.getString("selectedWindUnit") ?? 'Km/h';

          final formattedAvgWindSpeed = windUnit == 'Mph'
              ? UnitConverter.kmhToMph(avgWind)
              : windUnit == 'M/s'
                  ? UnitConverter.kmhToMs(avgWind)
                  : windUnit == 'Bft'
                      ? UnitConverter.kmhToBeaufort(avgWind)
                      : windUnit == 'Kt'
                          ? UnitConverter.kmhToKt(avgWind)
                          : avgWind;

          return Column(children: [
            Container(
              height: 360,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainer,
                borderRadius: BorderRadius.circular(18),
              ),
              padding: EdgeInsets.only(top: 12, bottom: 0),
              margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
              child: Column(
                children: [
                  Container(
                    padding: EdgeInsets.fromLTRB(20, 10, 20, 0),
                    alignment: Alignment.centerLeft,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text("todays_avg".tr(),
                            style: TextStyle(
                                fontSize: 20,
                                color: Theme.of(context).colorScheme.onSurface,
                                fontWeight: FontWeight.w500)),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              "${PreferencesHelper.getString("selectedWindUnit") == 'M/s' ? formattedAvgWindSpeed.toStringAsFixed(1) : formattedAvgWindSpeed.round()}",
                              style: TextStyle(
                                fontSize: 50,
                                color: Theme.of(context).colorScheme.secondary,
                              ),
                            ),
                            Padding(
                                padding: EdgeInsets.only(bottom: 11, left: 8),
                                child: Text(
                                  localizeWindUnit(windUnit, context.locale),
                                  style: TextStyle(
                                    fontSize: 20,
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                  ),
                                ))
                          ],
                        )
                      ],
                    ),
                  ),
                  SizedBox(
                    height: 225.2,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      physics: BouncingScrollPhysics(),
                      itemCount: 24 + 24 - startIndex,
                      itemBuilder: (context, index) {
                        final dataIndex = startIndex + index;

                        if (dataIndex >= hourlyTime.length)
                          return const SizedBox();
                        final forecastLocal =
                            DateTime.parse(hourlyTime[dataIndex]);

                        final roundedDisplayTime = DateTime(
                          forecastLocal.year,
                          forecastLocal.month,
                          forecastLocal.day,
                          forecastLocal.hour,
                        );
                        final hour = timeUnit == '24 hr'
                            ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00"
                            : UnitConverter.formatTo12Hour(roundedDisplayTime);
                        final windSpeed = windSpeeds[dataIndex];
                        final windDirection = windDirections[dataIndex];

                        final windPercentage =
                            ((windSpeed - minWind) / (maxWind - minWind)) * 100;

                        final windUnit =
                            PreferencesHelper.getString("selectedWindUnit") ??
                                'Km/h';

                        final formattedWindSpeed = windUnit == 'Mph'
                            ? UnitConverter.kmhToMph(windSpeed)
                                .toStringAsFixed(1)
                            : windUnit == 'M/s'
                                ? UnitConverter.kmhToMs(windSpeed)
                                    .toStringAsFixed(1)
                                : windUnit == 'Bft'
                                    ? UnitConverter.kmhToBeaufort(windSpeed)
                                        .round()
                                    : windUnit == 'Kt'
                                        ? UnitConverter.kmhToKt(windSpeed)
                                            .toStringAsFixed(1)
                                        : windSpeed.toStringAsFixed(1);

                        Widget windArrow = Transform.rotate(
                          angle: -(windDirection * (3.1415926535 / 180)),
                          child: Icon(
                            Icons.navigation,
                            size: 20,
                            color: Theme.of(context)
                                .colorScheme
                                .onPrimaryContainer,
                          ),
                        );

                        EdgeInsets itemMargin = EdgeInsets.only(
                          left: index == 0 ? 10 : 0,
                          right: index == 24 + 24 - startIndex - 1 ? 10 : 0,
                        );

                        return Container(
                          width: 53,
                          margin: itemMargin,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              Stack(
                                  alignment: Alignment.bottomCenter,
                                  clipBehavior: Clip.none,
                                  children: [
                                    Container(
                                      width: 20,
                                      height: 160,
                                      decoration: BoxDecoration(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .surface,
                                        borderRadius: BorderRadius.circular(50),
                                      ),
                                    ),
                                    TweenAnimationBuilder<double>(
                                      tween: Tween<double>(
                                        begin: 0,
                                        end: math.max(
                                            (windPercentage / 100) * 160, 48),
                                      ),
                                      duration:
                                          const Duration(milliseconds: 500),
                                      curve: Curves.easeOutBack,
                                      builder: (context, value, child) {
                                        return Container(
                                          width: 43,
                                          height: value,
                                          decoration: BoxDecoration(
                                            color: Theme.of(context)
                                                .colorScheme
                                                .primary,
                                            borderRadius:
                                                BorderRadius.circular(50),
                                          ),
                                          child: child,
                                        );
                                      },
                                      child: Align(
                                        alignment: Alignment.topCenter,
                                        child: SizedBox(
                                          width: 60,
                                          height: 60,
                                          child: Stack(
                                              alignment: Alignment.center,
                                              children: [
                                                Positioned(
                                                  top: 0,
                                                  child: SvgPicture.string(
                                                    '''<svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20.3091 8.60363C20.4924 8.454 20.584 8.37919 20.6677 8.31603C22.6389 6.82799 25.3611 6.82799 27.3323 8.31603C27.416 8.37919 27.5076 8.454 27.6909 8.60363C27.7727 8.67042 27.8136 8.70381 27.8541 8.7356C28.7818 9.46445 29.9191 9.87748 31.0993 9.91409C31.1508 9.91569 31.2037 9.91634 31.3094 9.91765C31.5462 9.92059 31.6646 9.92206 31.7694 9.92733C34.2381 10.0516 36.3234 11.7974 36.8747 14.2015C36.8982 14.3036 36.9202 14.4197 36.9642 14.6518C36.9838 14.7555 36.9937 14.8073 37.0042 14.8576C37.2452 16.0109 37.8504 17.0567 38.7309 17.8416C38.7693 17.8759 38.8094 17.9103 38.8895 17.9791C39.069 18.1332 39.1588 18.2102 39.2357 18.2815C41.0467 19.96 41.5194 22.6347 40.393 24.8299C40.3451 24.9231 40.2872 25.0262 40.1714 25.2322C40.1196 25.3242 40.0938 25.3702 40.0694 25.4155C39.5111 26.4536 39.3009 27.6429 39.4697 28.8088C39.4771 28.8597 39.4856 28.9117 39.5027 29.0158C39.5409 29.249 39.56 29.3656 39.573 29.4695C39.879 31.9168 38.5179 34.2689 36.2407 35.2281C36.1441 35.2688 36.0333 35.3106 35.8118 35.3942C35.7129 35.4315 35.6635 35.4501 35.6156 35.4692C34.5192 35.9063 33.592 36.6826 32.9701 37.684C32.943 37.7277 32.916 37.7731 32.862 37.8637C32.741 38.0669 32.6806 38.1685 32.6236 38.2564C31.2814 40.3273 28.7233 41.2563 26.3609 40.5306C26.2606 40.4998 26.1489 40.4608 25.9253 40.3827C25.8256 40.3479 25.7757 40.3305 25.7268 40.3144C24.6052 39.9461 23.3948 39.9461 22.2732 40.3144C22.2243 40.3305 22.1744 40.3479 22.0747 40.3827C21.8511 40.4608 21.7394 40.4998 21.6391 40.5306C19.2767 41.2563 16.7186 40.3273 15.3764 38.2564C15.3194 38.1685 15.259 38.0669 15.138 37.8637C15.084 37.7731 15.057 37.7277 15.0299 37.684C14.408 36.6826 13.4808 35.9063 12.3844 35.4692C12.3365 35.4501 12.2871 35.4315 12.1882 35.3942C11.9667 35.3106 11.8559 35.2688 11.7593 35.2281C9.48205 34.2689 8.12097 31.9168 8.42698 29.4695C8.43997 29.3656 8.45908 29.249 8.4973 29.0158C8.51436 28.9117 8.52289 28.8597 8.53026 28.8088C8.69906 27.6429 8.48889 26.4536 7.93056 25.4155C7.90621 25.3702 7.88035 25.3242 7.82863 25.2322C7.71278 25.0262 7.65485 24.9231 7.60704 24.8299C6.48057 22.6347 6.95327 19.96 8.76433 18.2815C8.8412 18.2102 8.93096 18.1332 9.11047 17.9791C9.19061 17.9103 9.23068 17.8759 9.26908 17.8416C10.1496 17.0567 10.7548 16.0109 10.9958 14.8576C11.0063 14.8073 11.0162 14.7555 11.0358 14.6518C11.0798 14.4197 11.1019 14.3036 11.1253 14.2015C11.6766 11.7974 13.7619 10.0516 16.2306 9.92733C16.3354 9.92206 16.4538 9.92059 16.6906 9.91765C16.7963 9.91634 16.8492 9.91569 16.9007 9.91409C18.0809 9.87748 19.2182 9.46445 20.1459 8.7356C20.1864 8.70381 20.2273 8.67042 20.3091 8.60363Z" fill="#${Theme.of(context).colorScheme.primaryContainer.value.toRadixString(16).padLeft(8, '0').substring(2)}"/>
                            </svg> ''',
                                                  ),
                                                ),
                                                Positioned(
                                                    top: 0,
                                                    child: SizedBox(
                                                      height: 48,
                                                      child: windArrow,
                                                    ))
                                              ]),
                                        ),
                                      ),
                                    ),
                                  ]),
                              const SizedBox(height: 10),
                              Text("$formattedWindSpeed",
                                  style: TextStyle(
                                    fontSize: 14,
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                  )),
                              Text(hour,
                                  style: TextStyle(
                                      fontSize: 15,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurface,
                                      fontWeight: FontWeight.w500)),
                            ],
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            ),
            Container(
                margin: EdgeInsets.fromLTRB(
                    12, 20, 12, MediaQuery.of(context).padding.bottom + 26),
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(
                    width: 1,
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(18),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Text("wind_info".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                  ],
                ))
          ]);
        });
  }

  Widget buildUVExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];

          final hourly = weather['hourly'];
          final List<dynamic> hourlyTime = hourly['time'];
          final List<dynamic> uvIndexes = hourly['uv_index'];

          final offset = Duration(
              seconds: int.parse(weather['utc_offset_seconds'].toString()));
          final nowUtc = DateTime.now().toUtc();
          final nowLocal = nowUtc.add(offset);

          final timeUnit =
              PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';

          final roundedNow = DateTime(
              nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

          int startIndex = hourlyTime.indexWhere((timeStr) {
            final forecastLocal = DateTime.parse(timeStr);
            return !forecastLocal.isBefore(roundedNow);
          });

          if (startIndex == -1) startIndex = 0;

          final nonNullUvIndexes = uvIndexes.whereType<num>().toList();
          final double minUv = nonNullUvIndexes.isNotEmpty
              ? nonNullUvIndexes.reduce((a, b) => a < b ? a : b).toDouble()
              : 0;
          final double maxUv = nonNullUvIndexes.isNotEmpty
              ? nonNullUvIndexes.reduce((a, b) => a > b ? a : b).toDouble()
              : 1;

          List<double> todayUvIndexes = [];

          for (int i = 8; i <= 18 && i < uvIndexes.length; i++) {
            final uv = uvIndexes[i];
            if (uv is num) {
              todayUvIndexes.add(uv.toDouble());
            }
          }
          final double avgUv = todayUvIndexes.isNotEmpty
              ? todayUvIndexes.reduce((a, b) => a + b) / todayUvIndexes.length
              : 0;

          return Column(children: [
            Container(
              height: 360,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainer,
                borderRadius: BorderRadius.circular(18),
              ),
              padding: EdgeInsets.only(top: 12, bottom: 0),
              margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
              child: Column(
                children: [
                  Container(
                    padding: EdgeInsets.fromLTRB(20, 10, 20, 0),
                    alignment: Alignment.centerLeft,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text("todays_avg".tr(),
                            style: TextStyle(
                                fontSize: 20,
                                color: Theme.of(context).colorScheme.onSurface,
                                fontWeight: FontWeight.w500)),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              avgUv != null ? "${avgUv.round()}" : "--",
                              style: TextStyle(
                                fontSize: 50,
                                color: Theme.of(context).colorScheme.secondary,
                              ),
                            ),
                            Padding(
                                padding: EdgeInsets.only(bottom: 11, left: 8),
                                child: Text(
                                  avgUv != null
                                      ? getUvIndexType(avgUv.round())
                                      : "Not available",
                                  style: TextStyle(
                                      fontSize: 20,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurfaceVariant,
                                      fontWeight: FontWeight.w500),
                                ))
                          ],
                        )
                      ],
                    ),
                  ),
                  SizedBox(
                    height: 216,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      physics: BouncingScrollPhysics(),
                      itemCount: 24 + 24 - startIndex,
                      itemBuilder: (context, index) {
                        final dataIndex = startIndex + index;

                        if (dataIndex >= hourlyTime.length)
                          return const SizedBox();
                        final forecastLocal =
                            DateTime.parse(hourlyTime[dataIndex]);

                        final roundedDisplayTime = DateTime(
                          forecastLocal.year,
                          forecastLocal.month,
                          forecastLocal.day,
                          forecastLocal.hour,
                        );
                        final hour = timeUnit == '24 hr'
                            ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00"
                            : UnitConverter.formatTo12Hour(roundedDisplayTime);

                        final uv = uvIndexes[dataIndex];
                        final bool isValidUv = uv is num;
                        final double? uvValue =
                            isValidUv ? uv.toDouble() : null;
                        final double uvPercentage =
                            uvValue != null && maxUv != minUv
                                ? ((uvValue - minUv) / (maxUv - minUv)) * 100
                                : 0;

                        EdgeInsets itemMargin = EdgeInsets.only(
                          left: index == 0 ? 10 : 0,
                          right: index == 24 + 24 - startIndex - 1 ? 10 : 0,
                        );

                        Color getUvColor(double uv, BuildContext context) {
                          if (uv <= 2) return Color(0xFFa5d395);
                          if (uv <= 5) return Color(0xFFdbc66e);
                          if (uv <= 7) return Color(0xFFfeb877);
                          if (uv <= 10) return Color(0xFFffb4ab);
                          return Color(0xFFe9b5ee);
                        }

                        String getOnUvColor(double uv, BuildContext context) {
                          if (uv <= 2) return "11380b";
                          if (uv <= 5) return "3a3000";
                          if (uv <= 7) return "4b2800";
                          if (uv <= 10) return "690005";
                          return "48214f";
                        }

                        return Container(
                          width: 53,
                          margin: itemMargin,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              Stack(
                                  alignment: Alignment.bottomCenter,
                                  clipBehavior: Clip.none,
                                  children: [
                                    Container(
                                      width: 20,
                                      height: 160,
                                      decoration: BoxDecoration(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .surface,
                                        borderRadius: BorderRadius.circular(50),
                                      ),
                                    ),
                                    TweenAnimationBuilder<double>(
                                      tween: Tween<double>(
                                        begin: 0,
                                        end: math.max(
                                            (uvPercentage / 100) * 160, 48),
                                      ),
                                      duration:
                                          const Duration(milliseconds: 500),
                                      curve: Curves.easeOutBack,
                                      builder: (context, value, child) {
                                        return Container(
                                          width: 43,
                                          height: value,
                                          decoration: BoxDecoration(
                                            color: getUvColor(
                                                uvValue ?? 0, context),
                                            borderRadius:
                                                BorderRadius.circular(50),
                                          ),
                                          child: child,
                                        );
                                      },
                                      child: Align(
                                        alignment: Alignment.topCenter,
                                        child: SizedBox(
                                          width: 60,
                                          height: 60,
                                          child: Stack(
                                              alignment: Alignment.center,
                                              children: [
                                                Positioned(
                                                  top: 0,
                                                  child: SvgPicture.string(
                                                    '''<svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20.3091 8.60363C20.4924 8.454 20.584 8.37919 20.6677 8.31603C22.6389 6.82799 25.3611 6.82799 27.3323 8.31603C27.416 8.37919 27.5076 8.454 27.6909 8.60363C27.7727 8.67042 27.8136 8.70381 27.8541 8.7356C28.7818 9.46445 29.9191 9.87748 31.0993 9.91409C31.1508 9.91569 31.2037 9.91634 31.3094 9.91765C31.5462 9.92059 31.6646 9.92206 31.7694 9.92733C34.2381 10.0516 36.3234 11.7974 36.8747 14.2015C36.8982 14.3036 36.9202 14.4197 36.9642 14.6518C36.9838 14.7555 36.9937 14.8073 37.0042 14.8576C37.2452 16.0109 37.8504 17.0567 38.7309 17.8416C38.7693 17.8759 38.8094 17.9103 38.8895 17.9791C39.069 18.1332 39.1588 18.2102 39.2357 18.2815C41.0467 19.96 41.5194 22.6347 40.393 24.8299C40.3451 24.9231 40.2872 25.0262 40.1714 25.2322C40.1196 25.3242 40.0938 25.3702 40.0694 25.4155C39.5111 26.4536 39.3009 27.6429 39.4697 28.8088C39.4771 28.8597 39.4856 28.9117 39.5027 29.0158C39.5409 29.249 39.56 29.3656 39.573 29.4695C39.879 31.9168 38.5179 34.2689 36.2407 35.2281C36.1441 35.2688 36.0333 35.3106 35.8118 35.3942C35.7129 35.4315 35.6635 35.4501 35.6156 35.4692C34.5192 35.9063 33.592 36.6826 32.9701 37.684C32.943 37.7277 32.916 37.7731 32.862 37.8637C32.741 38.0669 32.6806 38.1685 32.6236 38.2564C31.2814 40.3273 28.7233 41.2563 26.3609 40.5306C26.2606 40.4998 26.1489 40.4608 25.9253 40.3827C25.8256 40.3479 25.7757 40.3305 25.7268 40.3144C24.6052 39.9461 23.3948 39.9461 22.2732 40.3144C22.2243 40.3305 22.1744 40.3479 22.0747 40.3827C21.8511 40.4608 21.7394 40.4998 21.6391 40.5306C19.2767 41.2563 16.7186 40.3273 15.3764 38.2564C15.3194 38.1685 15.259 38.0669 15.138 37.8637C15.084 37.7731 15.057 37.7277 15.0299 37.684C14.408 36.6826 13.4808 35.9063 12.3844 35.4692C12.3365 35.4501 12.2871 35.4315 12.1882 35.3942C11.9667 35.3106 11.8559 35.2688 11.7593 35.2281C9.48205 34.2689 8.12097 31.9168 8.42698 29.4695C8.43997 29.3656 8.45908 29.249 8.4973 29.0158C8.51436 28.9117 8.52289 28.8597 8.53026 28.8088C8.69906 27.6429 8.48889 26.4536 7.93056 25.4155C7.90621 25.3702 7.88035 25.3242 7.82863 25.2322C7.71278 25.0262 7.65485 24.9231 7.60704 24.8299C6.48057 22.6347 6.95327 19.96 8.76433 18.2815C8.8412 18.2102 8.93096 18.1332 9.11047 17.9791C9.19061 17.9103 9.23068 17.8759 9.26908 17.8416C10.1496 17.0567 10.7548 16.0109 10.9958 14.8576C11.0063 14.8073 11.0162 14.7555 11.0358 14.6518C11.0798 14.4197 11.1019 14.3036 11.1253 14.2015C11.6766 11.7974 13.7619 10.0516 16.2306 9.92733C16.3354 9.92206 16.4538 9.92059 16.6906 9.91765C16.7963 9.91634 16.8492 9.91569 16.9007 9.91409C18.0809 9.87748 19.2182 9.46445 20.1459 8.7356C20.1864 8.70381 20.2273 8.67042 20.3091 8.60363Z" fill="#${getOnUvColor(uv, context)}"/>
                            </svg> ''',
                                                  ),
                                                ),
                                                Positioned(
                                                    top: 0,
                                                    child: SizedBox(
                                                      height: 48,
                                                      child: Center(
                                                          child: Text(
                                                              uvValue != null
                                                                  ? "${uvValue.round()}"
                                                                  : "--",
                                                              style: TextStyle(
                                                                fontSize: 16,
                                                                color: getUvColor(
                                                                    uvValue ??
                                                                        0,
                                                                    context),
                                                              ))),
                                                    ))
                                              ]),
                                        ),
                                      ),
                                    ),
                                  ]),
                              const SizedBox(height: 10),
                              Text(hour,
                                  style: TextStyle(
                                      fontSize: 15,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurface,
                                      fontWeight: FontWeight.w500)),
                            ],
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            ),
            Container(
                margin: EdgeInsets.fromLTRB(
                    12, 20, 12, MediaQuery.of(context).padding.bottom + 26),
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(
                    width: 1,
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(18),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Text("uv_index_info".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("uv_index_info_2".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("uv_index_info_3".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("uv_index_info_4".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("uv_index_info_5".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                  ],
                ))
          ]);
        });
  }

  Widget buildAQIExtended() {
    int selectedIndex = 0;

    final aqiUnit =
        PreferencesHelper.getString("selectedAQIUnit") ?? "United States";

    final ValueNotifier<int> tabIndexNotifier =
        ValueNotifier<int>(aqiUnit == 'European' ? 1 : 0);

    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];
          final airQualityData = weather['air_quality']['current'];

          final aqiFormat = aqiUnit == 'European'
              ? airQualityData['european_aqi']
              : airQualityData['us_aqi'];

          return Column(
            children: [
              Container(
                clipBehavior: Clip.hardEdge,
                height: 305,
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surfaceContainer,
                  borderRadius: BorderRadius.circular(18),
                ),
                padding: EdgeInsets.only(top: 12, bottom: 0),
                margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
                child: Column(
                  children: [
                    Container(
                      padding: EdgeInsets.fromLTRB(20, 10, 20, 0),
                      alignment: Alignment.centerLeft,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text("current_conditions".tr(),
                              style: TextStyle(
                                  fontSize: 20,
                                  color:
                                      Theme.of(context).colorScheme.onSurface,
                                  fontWeight: FontWeight.w500)),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              Text(
                                "$aqiFormat",
                                style: TextStyle(
                                  fontSize: 50,
                                  color:
                                      Theme.of(context).colorScheme.secondary,
                                ),
                              ),
                              Padding(
                                  padding: EdgeInsets.only(bottom: 11, left: 8),
                                  child: Text(
                                    getAQIIndexType(aqiFormat,
                                        aqiUnit == 'European' ? true : false),
                                    style: TextStyle(
                                      fontSize: 20,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurfaceVariant,
                                    ),
                                  ))
                            ],
                          ),
                        ],
                      ),
                    ),
                    Container(
                      height: 20,
                      clipBehavior: Clip.none,
                      padding: EdgeInsets.fromLTRB(12, 0, 12, 0),
                      margin: EdgeInsets.only(top: 10),
                      child: Stack(
                        children: [
                          AQISliderBar(
                            aqi: airQualityData['us_aqi'],
                            width: 380,
                            height: 20,
                          ),
                        ],
                      ),
                    ),
                    Container(
                        padding: EdgeInsets.fromLTRB(20, 20, 20, 0),
                        child: Column(
                          spacing: 10,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  "${"united_states_aqi".tr()}:",
                                  style: TextStyle(
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurface,
                                      fontSize: 15),
                                ),
                                Container(
                                  padding: EdgeInsets.only(left: 10, right: 10),
                                  decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(50),
                                      color: Theme.of(context)
                                          .colorScheme
                                          .tertiary),
                                  child: Text("${airQualityData['us_aqi']}",
                                      style: TextStyle(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onTertiary,
                                        fontSize: 16,
                                      )),
                                )
                              ],
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text("${"european_aqi".tr()}:",
                                    style: TextStyle(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                        fontSize: 15)),
                                Container(
                                  padding: EdgeInsets.only(left: 10, right: 10),
                                  decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(50),
                                      color: Theme.of(context)
                                          .colorScheme
                                          .tertiary),
                                  child:
                                      Text("${airQualityData['european_aqi']}",
                                          style: TextStyle(
                                            color: Theme.of(context)
                                                .colorScheme
                                                .onTertiary,
                                            fontSize: 16,
                                          )),
                                )
                              ],
                            )
                          ],
                        )),
                    SizedBox(
                      height: 20,
                    ),
                    ValueListenableBuilder<int>(
                      valueListenable: tabIndexNotifier,
                      builder: (context, selectedIndex, _) {
                        return Container(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          child: Row(
                            children: [
                              _buildTabButton(context, "united_states_aqi".tr(),
                                  0, selectedIndex, tabIndexNotifier),
                              _buildTabButton(context, "european_aqi".tr(), 1,
                                  selectedIndex, tabIndexNotifier),
                            ],
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),
              SizedBox(
                height: 20,
              ),
              ValueListenableBuilder<int>(
                valueListenable: tabIndexNotifier,
                builder: (context, selectedIndex, _) {
                  return AnimatedSwitcher(
                      duration: const Duration(milliseconds: 250),
                      child: selectedIndex == 0
                          ? _buildAQIDetailsCard(
                              context,
                              "aqi_info_us".tr(),
                              [
                                "0–50: ${"air_quality_satisfactory".tr()}",
                                "51–100: ${"air_quality_acceptable".tr()}",
                                "101–150: ${"sensitive_groups_health_effects".tr()}",
                                "151–200: ${"health_effects_public".tr()}",
                                "201–300: ${"emergency_conditions".tr()}",
                              ],
                              key: const ValueKey('us'),
                            )
                          : _buildAQIDetailsCard(
                              context,
                              "aqi_info_eu".tr(),
                              [
                                "0–25: ${"air_quality_satisfactory".tr()}",
                                "26–50: ${"air_quality_acceptable".tr()}",
                                "51–75: ${"sensitive_groups_health_effects".tr()}",
                                "76–100: ${"health_effects_public".tr()}",
                                "100+: ${"emergency_conditions".tr()}",
                              ],
                              key: const ValueKey('eu'),
                            ));
                },
              ),
            ],
          );
        });
  }

  Widget _buildTabButton(
    BuildContext context,
    String label,
    int index,
    int selectedIndex,
    ValueNotifier<int> tabIndexNotifier,
  ) {
    final isSelected = selectedIndex == index;
    final isFirst = index == 0;
    final isLast = index == 1;

    BorderRadius borderRadius;

    if (isSelected) {
      borderRadius = BorderRadius.circular(50);
    } else if (isFirst) {
      borderRadius = const BorderRadius.only(
          topLeft: Radius.circular(50),
          bottomLeft: Radius.circular(50),
          bottomRight: Radius.circular(10),
          topRight: Radius.circular(10));
    } else if (isLast) {
      borderRadius = const BorderRadius.only(
          topRight: Radius.circular(50),
          bottomRight: Radius.circular(50),
          bottomLeft: Radius.circular(10),
          topLeft: Radius.circular(10));
    } else {
      borderRadius = BorderRadius.zero;
    }

    return Expanded(
      child: GestureDetector(
        onTap: () => tabIndexNotifier.value = index,
        child: TweenAnimationBuilder<BorderRadius>(
          duration: const Duration(milliseconds: 1000),
          curve: Curves.elasticOut,
          tween:
              Tween<BorderRadius>(begin: BorderRadius.zero, end: borderRadius),
          builder: (context, radius, child) {
            return Container(
              alignment: Alignment.center,
              padding: const EdgeInsets.symmetric(vertical: 12),
              decoration: BoxDecoration(
                color: isSelected
                    ? Theme.of(context).colorScheme.primary
                    : Theme.of(context).colorScheme.primaryContainer,
                borderRadius: radius,
              ),
              child: Text(
                label,
                style: TextStyle(
                  color: isSelected
                      ? Theme.of(context).colorScheme.onPrimary
                      : Theme.of(context).colorScheme.onPrimaryContainer,
                  fontWeight: FontWeight.w500,
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  Widget _buildAQIDetailsCard(
    BuildContext context,
    String title,
    List<String> lines, {
    required Key key,
  }) {
    return Container(
      key: key,
      margin: EdgeInsets.fromLTRB(
          12, 0, 12, MediaQuery.of(context).padding.bottom + 26),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        border: Border.all(color: Theme.of(context).colorScheme.outlineVariant),
        borderRadius: BorderRadius.circular(18),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title,
              style: TextStyle(
                fontSize: 17,
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              )),
          const SizedBox(height: 10),
          for (var line in lines)
            Padding(
              padding: const EdgeInsets.only(bottom: 6),
              child: Text(
                line,
                style: TextStyle(
                  fontSize: 16,
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget buildPrecipExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];

          final hourly = weather['hourly'];
          final daily = weather['daily'];
          final List<dynamic> hourlyTime = hourly['time'];
          final List<dynamic> precipProb = hourly['precipitation_probability'];
          final List<double> precipAmount =
              (hourly['precipitation'] as List<dynamic>)
                  .map((e) => (e as num).toDouble())
                  .toList();
          final double precipHours = daily['precipitation_hours'][0];
          final double todaysAMOUNT = daily['precipitation_sum'][0];

          final offset = Duration(
              seconds: int.parse(weather['utc_offset_seconds'].toString()));
          final nowUtc = DateTime.now().toUtc();
          final nowLocal = nowUtc.add(offset);

          final timeUnit =
              PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';

          final precipitationUnit =
              PreferencesHelper.getString("selectedPrecipitationUnit") ?? 'mm';

          final convertedPrecip = precipitationUnit == 'cm'
              ? UnitConverter.mmToCm(todaysAMOUNT)
              : precipitationUnit == 'in'
                  ? UnitConverter.mmToIn(todaysAMOUNT)
                  : todaysAMOUNT;

          final roundedNow = DateTime(
              nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

          int startIndex = hourlyTime.indexWhere((timeStr) {
            final forecastLocal = DateTime.parse(timeStr);
            return !forecastLocal.isBefore(roundedNow);
          });

          if (startIndex == -1) startIndex = 0;

          final double minprecipAmount =
              precipAmount.reduce((a, b) => a < b ? a : b);
          final double maxprecipAmount =
              precipAmount.reduce((a, b) => a > b ? a : b);

          return Column(children: [
            Container(
              height: 360,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainer,
                borderRadius: BorderRadius.circular(18),
              ),
              padding: EdgeInsets.only(top: 12, bottom: 0),
              margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
              child: Column(
                children: [
                  Container(
                    padding: EdgeInsets.fromLTRB(20, 10, 20, 0),
                    alignment: Alignment.centerLeft,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text("today's_amount".tr(),
                            style: TextStyle(
                                fontSize: 20,
                                color: Theme.of(context).colorScheme.onSurface,
                                fontWeight: FontWeight.w500)),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              "${double.parse(convertedPrecip.toStringAsFixed(2))}",
                              style: TextStyle(
                                fontSize: 50,
                                color: Theme.of(context).colorScheme.secondary,
                              ),
                            ),
                            Padding(
                                padding: EdgeInsets.only(bottom: 11, left: 8),
                                child: Text(
                                  '${localizePrecipUnit(precipitationUnit, context.locale)} • ${precipHours.round()} ${'hrs_sub_text'.tr()}',
                                  style: TextStyle(
                                    fontSize: 20,
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                  ),
                                ))
                          ],
                        )
                      ],
                    ),
                  ),
                  SizedBox(
                    height: 225.2,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      physics: BouncingScrollPhysics(),
                      itemCount: 24 + 24 - startIndex,
                      itemBuilder: (context, index) {
                        final dataIndex = startIndex + index;

                        if (dataIndex >= hourlyTime.length)
                          return const SizedBox();
                        final forecastLocal =
                            DateTime.parse(hourlyTime[dataIndex]);

                        final roundedDisplayTime = DateTime(
                          forecastLocal.year,
                          forecastLocal.month,
                          forecastLocal.day,
                          forecastLocal.hour,
                        );
                        final hour = timeUnit == '24 hr'
                            ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00"
                            : UnitConverter.formatTo12Hour(roundedDisplayTime);

                        final precipAmountMain = precipAmount[dataIndex];
                        final precipProbMain = precipProb[dataIndex];

                        final double rainValue = (precipAmountMain is num)
                            ? precipAmountMain.toDouble()
                            : 0;
                        final double rainPercentage =
                            ((rainValue - minprecipAmount) /
                                    (maxprecipAmount - minprecipAmount)) *
                                100;

                        final convertedPrecipAmount = precipitationUnit == 'cm'
                            ? UnitConverter.mmToCm(rainValue)
                            : precipitationUnit == 'in'
                                ? UnitConverter.mmToIn(rainValue)
                                : rainValue;

                        EdgeInsets itemMargin = EdgeInsets.only(
                          left: index == 0 ? 10 : 0,
                          right: index == 24 + 24 - startIndex - 1 ? 10 : 0,
                        );

                        return Container(
                          width: 53,
                          margin: itemMargin,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              Stack(
                                  alignment: Alignment.bottomCenter,
                                  clipBehavior: Clip.none,
                                  children: [
                                    Container(
                                      width: 20,
                                      height: 160,
                                      decoration: BoxDecoration(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .surface,
                                        borderRadius: BorderRadius.circular(50),
                                      ),
                                    ),
                                    TweenAnimationBuilder<double>(
                                      tween: Tween<double>(
                                        begin: 0,
                                        end: math.max(
                                            (rainPercentage / 100) * 160, 45),
                                      ),
                                      duration:
                                          const Duration(milliseconds: 500),
                                      curve: Curves.easeOutBack,
                                      builder: (context, value, child) {
                                        return Container(
                                          width: 43,
                                          height: value,
                                          decoration: BoxDecoration(
                                            color: Theme.of(context)
                                                .colorScheme
                                                .primary,
                                            borderRadius:
                                                BorderRadius.circular(50),
                                          ),
                                          child: child,
                                        );
                                      },
                                      child: Align(
                                        alignment: Alignment.topCenter,
                                        child: SizedBox(
                                          width: 60,
                                          height: 60,
                                          child: Stack(
                                              alignment: Alignment.center,
                                              children: [
                                                Positioned(
                                                  top: -2,
                                                  child: SvgPicture.string(
                                                    '''<svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20.3091 8.60363C20.4924 8.454 20.584 8.37919 20.6677 8.31603C22.6389 6.82799 25.3611 6.82799 27.3323 8.31603C27.416 8.37919 27.5076 8.454 27.6909 8.60363C27.7727 8.67042 27.8136 8.70381 27.8541 8.7356C28.7818 9.46445 29.9191 9.87748 31.0993 9.91409C31.1508 9.91569 31.2037 9.91634 31.3094 9.91765C31.5462 9.92059 31.6646 9.92206 31.7694 9.92733C34.2381 10.0516 36.3234 11.7974 36.8747 14.2015C36.8982 14.3036 36.9202 14.4197 36.9642 14.6518C36.9838 14.7555 36.9937 14.8073 37.0042 14.8576C37.2452 16.0109 37.8504 17.0567 38.7309 17.8416C38.7693 17.8759 38.8094 17.9103 38.8895 17.9791C39.069 18.1332 39.1588 18.2102 39.2357 18.2815C41.0467 19.96 41.5194 22.6347 40.393 24.8299C40.3451 24.9231 40.2872 25.0262 40.1714 25.2322C40.1196 25.3242 40.0938 25.3702 40.0694 25.4155C39.5111 26.4536 39.3009 27.6429 39.4697 28.8088C39.4771 28.8597 39.4856 28.9117 39.5027 29.0158C39.5409 29.249 39.56 29.3656 39.573 29.4695C39.879 31.9168 38.5179 34.2689 36.2407 35.2281C36.1441 35.2688 36.0333 35.3106 35.8118 35.3942C35.7129 35.4315 35.6635 35.4501 35.6156 35.4692C34.5192 35.9063 33.592 36.6826 32.9701 37.684C32.943 37.7277 32.916 37.7731 32.862 37.8637C32.741 38.0669 32.6806 38.1685 32.6236 38.2564C31.2814 40.3273 28.7233 41.2563 26.3609 40.5306C26.2606 40.4998 26.1489 40.4608 25.9253 40.3827C25.8256 40.3479 25.7757 40.3305 25.7268 40.3144C24.6052 39.9461 23.3948 39.9461 22.2732 40.3144C22.2243 40.3305 22.1744 40.3479 22.0747 40.3827C21.8511 40.4608 21.7394 40.4998 21.6391 40.5306C19.2767 41.2563 16.7186 40.3273 15.3764 38.2564C15.3194 38.1685 15.259 38.0669 15.138 37.8637C15.084 37.7731 15.057 37.7277 15.0299 37.684C14.408 36.6826 13.4808 35.9063 12.3844 35.4692C12.3365 35.4501 12.2871 35.4315 12.1882 35.3942C11.9667 35.3106 11.8559 35.2688 11.7593 35.2281C9.48205 34.2689 8.12097 31.9168 8.42698 29.4695C8.43997 29.3656 8.45908 29.249 8.4973 29.0158C8.51436 28.9117 8.52289 28.8597 8.53026 28.8088C8.69906 27.6429 8.48889 26.4536 7.93056 25.4155C7.90621 25.3702 7.88035 25.3242 7.82863 25.2322C7.71278 25.0262 7.65485 24.9231 7.60704 24.8299C6.48057 22.6347 6.95327 19.96 8.76433 18.2815C8.8412 18.2102 8.93096 18.1332 9.11047 17.9791C9.19061 17.9103 9.23068 17.8759 9.26908 17.8416C10.1496 17.0567 10.7548 16.0109 10.9958 14.8576C11.0063 14.8073 11.0162 14.7555 11.0358 14.6518C11.0798 14.4197 11.1019 14.3036 11.1253 14.2015C11.6766 11.7974 13.7619 10.0516 16.2306 9.92733C16.3354 9.92206 16.4538 9.92059 16.6906 9.91765C16.7963 9.91634 16.8492 9.91569 16.9007 9.91409C18.0809 9.87748 19.2182 9.46445 20.1459 8.7356C20.1864 8.70381 20.2273 8.67042 20.3091 8.60363Z" fill="#${Theme.of(context).colorScheme.primaryContainer.value.toRadixString(16).padLeft(8, '0').substring(2)}"/>
                            </svg> ''',
                                                  ),
                                                ),
                                                Positioned(
                                                    top: -5,
                                                    child: SizedBox(
                                                      height: 48,
                                                      child: Center(
                                                          child: Text(
                                                              "$precipProbMain",
                                                              style: TextStyle(
                                                                fontSize: 15,
                                                                color: Theme.of(
                                                                        context)
                                                                    .colorScheme
                                                                    .primary,
                                                              ))),
                                                    )),
                                                Positioned(
                                                    top: 6,
                                                    child: SizedBox(
                                                      height: 48,
                                                      child: Center(
                                                          child: Text("%",
                                                              style: TextStyle(
                                                                fontSize: 10,
                                                                color: Theme.of(
                                                                        context)
                                                                    .colorScheme
                                                                    .primary,
                                                              ))),
                                                    ))
                                              ]),
                                        ),
                                      ),
                                    ),
                                  ]),
                              const SizedBox(height: 10),
                              Text(
                                  "${double.parse(convertedPrecipAmount.toStringAsFixed(2))}",
                                  style: TextStyle(
                                      fontSize: 14,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurfaceVariant,
                                      fontWeight: FontWeight.w500)),
                              Text(hour,
                                  style: TextStyle(
                                      fontSize: 15,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurface,
                                      fontWeight: FontWeight.w500)),
                            ],
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            ),
            Container(
                margin: EdgeInsets.fromLTRB(
                    12, 20, 12, MediaQuery.of(context).padding.bottom + 26),
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(
                    width: 1,
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(18),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Text("precip_info".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("precip_info_2".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("precip_info_3".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                    Text("precip_info_4".tr(),
                        style: TextStyle(
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        )),
                  ],
                ))
          ]);
        });
  }

  // MOON

  Widget buildMoonExtended() {
    return FutureBuilder<Map<String, dynamic>?>(
        future: getWeatherWidgets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data == null) {
            return Center(child: Text('no_data_available'.tr()));
          }

          final data = snapshot.data!;
          final weather = data['data'];

          return Column(
            children: [
              Container(
                clipBehavior: Clip.hardEdge,
                height: 305,
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surfaceContainer,
                  borderRadius: BorderRadius.circular(18),
                ),
                padding: EdgeInsets.only(top: 12, bottom: 0),
                margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
                child: Column(
                  children: [
                    Container(
                      padding: EdgeInsets.fromLTRB(20, 10, 20, 0),
                      alignment: Alignment.centerLeft,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text("current_conditions".tr(),
                              style: TextStyle(
                                  fontSize: 20,
                                  color:
                                      Theme.of(context).colorScheme.onSurface,
                                  fontWeight: FontWeight.w500)),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              Text(
                                "",
                                style: TextStyle(
                                  fontSize: 50,
                                  color:
                                      Theme.of(context).colorScheme.secondary,
                                ),
                              ),
                              Padding(
                                  padding: EdgeInsets.only(bottom: 11, left: 8),
                                  child: Text(
                                    "",
                                    style: TextStyle(
                                      fontSize: 20,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onSurfaceVariant,
                                    ),
                                  ))
                            ],
                          ),
                        ],
                      ),
                    ),
                    SizedBox(
                      height: 20,
                    ),
                  ],
                ),
              ),
              SizedBox(
                height: 20,
              ),
            ],
          );
        });
  }
}

Widget buildAirQualityBox(String label, double value, String type) {
  return Container(
    padding: EdgeInsets.all(12),
    margin: EdgeInsets.symmetric(vertical: 6),
    decoration: BoxDecoration(
      color: getColor(value, type),
      borderRadius: BorderRadius.circular(10),
    ),
    child: Text(
      "$label: $value",
      style: TextStyle(color: Colors.black, fontSize: 16),
    ),
  );
}

Color getColor(double value, String type) {
  switch (type) {
    case "CO":
      if (value <= 4.5) return Color(0xFF20FC03);
      if (value <= 9.5) return Colors.yellow;
      if (value <= 12.5) return Colors.orange;
      if (value <= 15.5) return Color(0xFFFC606D);
      if (value <= 30.5) return Color(0xFF9000FF);
      if (value <= 50.5) return Colors.brown;
      return Colors.brown;

    case "NO2":
      if (value <= 40) return Color(0xFF20FC03);
      if (value <= 100) return Colors.yellow;
      if (value <= 200) return Colors.orange;
      if (value <= 300) return Color(0xFFFC606D);
      if (value <= 500) return Color(0xFF9000FF);
      return Colors.brown;

    case "SO2":
      if (value <= 50) return Color(0xFF20FC03);
      if (value <= 150) return Colors.yellow;
      if (value <= 250) return Colors.orange;
      if (value <= 500) return Color(0xFFFC606D);
      if (value <= 1000) return Color(0xFF9000FF);
      return Colors.brown;

    case "O3":
      if (value <= 100) return Color(0xFF20FC03);
      if (value <= 180) return Colors.yellow;
      if (value <= 300) return Colors.orange;
      if (value <= 400) return Color(0xFFFC606D);
      if (value <= 500) return Color(0xFF9000FF);
      return Colors.brown;

    case "PM2.5":
      if (value <= 12) return Color(0xFF20FC03);
      if (value <= 35) return Colors.yellow;
      if (value <= 55) return Colors.orange;
      if (value <= 150) return Color(0xFFFC606D);
      if (value <= 250) return Color(0xFF9000FF);
      return Colors.brown;

    case "PM10":
      if (value <= 20) return Color(0xFF20FC03);
      if (value <= 50) return Colors.yellow;
      if (value <= 100) return Colors.orange;
      if (value <= 150) return Color(0xFFFC606D);
      if (value <= 250) return Color(0xFF9000FF);
      return Colors.brown;

    default:
      return Colors.white;
  }
}

String getAQIIndexType(int aqiIndex, bool isEU) {
  if (!isEU) {
    if (aqiIndex <= 50) {
      return "good".tr();
    } else if (aqiIndex <= 100) {
      return "fair".tr();
    } else if (aqiIndex <= 150) {
      return "moderate".tr();
    } else if (aqiIndex <= 200) {
      return "poor".tr();
    } else {
      return "very_poor".tr();
    }
  } else {
    if (aqiIndex <= 25) {
      return "good".tr();
    } else if (aqiIndex <= 50) {
      return "fair".tr();
    } else if (aqiIndex <= 75) {
      return "moderate".tr();
    } else if (aqiIndex <= 100) {
      return "poor".tr();
    } else {
      return "very_poor".tr();
    }
  }
}
// AQI WIDGET

class AQISliderBar extends StatelessWidget {
  final double width;
  final double height;
  final int aqi;

  const AQISliderBar({
    super.key,
    required this.aqi,
    this.width = 50,
    this.height = 5,
  });

  @override
  Widget build(BuildContext context) {
    final double clampedAQI = aqi.clamp(0, 500).toDouble();
    final double thumbPosition = (clampedAQI / 500) * width;

    return Positioned(
      left: 0,
      right: 0,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          Container(
            height: height,
            margin: EdgeInsets.only(left: 10, right: 10),
            decoration: const BoxDecoration(
              borderRadius: BorderRadius.all(Radius.circular(999)),
              gradient: LinearGradient(
                begin: Alignment.centerLeft,
                end: Alignment.centerRight,
                colors: [
                  Color(0xFF00E400), // Green
                  Color(0xFFFFFF00), // Yellow
                  Color(0xFFFF7E00), // Orange
                  Color(0xFFFF0000), // Red
                  Color(0xFF8F3F97), // Purple
                  Color(0xFF7E0023), // Maroon
                ],
                stops: [
                  0.1,
                  0.2,
                  0.4,
                  0.6,
                  0.8,
                  1.0,
                ],
              ),
            ),
          ),
          Positioned(
            left: thumbPosition - 10,
            top: -5,
            child: Column(
              children: [
                Container(
                  width: 16,
                  height: 30,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.inversePrimary,
                    borderRadius: BorderRadius.circular(50),
                    border: Border.all(
                        color: Theme.of(context).colorScheme.primary),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
      // ],
    );
  }
}

int getStartIndex(utc_offset_seconds, hourlyTime) {
  final offset = Duration(seconds: int.parse(utc_offset_seconds));
  final nowUtc = DateTime.now().toUtc();
  final nowLocal = nowUtc.add(offset);

  final timeUnit = PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';

  final roundedNow =
      DateTime(nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

  int startIndex = hourlyTime.indexWhere((timeStr) {
    final forecastLocal = DateTime.parse(timeStr);
    return !forecastLocal.isBefore(roundedNow);
  });

  if (startIndex == -1) startIndex = 0;

  return startIndex;
}
