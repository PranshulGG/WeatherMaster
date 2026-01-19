import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/preferences_helper.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import 'dart:math';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:flutter_compass/flutter_compass.dart';
import 'dart:io' show Platform;
import 'package:flutter/foundation.dart';
import 'package:reorderable_grid_view/reorderable_grid_view.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../screens/extended_widgets.dart';
import '../helper/locale_helper.dart';
import '../utils/icon_map.dart';
import '../utils/visual_utils.dart';
import 'package:animations/animations.dart';
import 'package:moon_phase/moon_widget.dart';

const String _noDataAvailableMessageKey = 'no_data_available';
const String _timeFormat24Hr = '24 hr';
const String _notAvailableValue = 'N/A';

class ConditionsWidgets extends StatefulWidget {
  final int selectedContainerBgIndex;
  final int currentHumidity;
  final double currentDewPoint;
  final String currentSunrise;
  final String currentSunset;
  final double currentPressure;
  final double currentVisibility;
  final double currentWindSpeed;
  final int currentWindDirc;
  final String timezone;
  final String utcOffsetSeconds;
  final double currentUvIndex;
  final int currentAQIUSA;
  final int currentAQIEURO;
  final double currentTotalPrec;
  final double currentDayLength;
  final bool isFromHome;
  final String? moonrise;
  final String? moonset;
  final String? moonPhase;
  final String cloudCover;

  const ConditionsWidgets(
      {super.key,
      required this.selectedContainerBgIndex,
      required this.currentHumidity,
      required this.currentDewPoint,
      required this.currentSunrise,
      required this.currentSunset,
      required this.currentPressure,
      required this.currentVisibility,
      required this.currentWindSpeed,
      required this.currentWindDirc,
      required this.timezone,
      required this.utcOffsetSeconds,
      required this.currentUvIndex,
      required this.currentAQIUSA,
      required this.currentAQIEURO,
      required this.currentTotalPrec,
      required this.currentDayLength,
      required this.isFromHome,
      this.moonrise,
      this.moonset,
      this.moonPhase,
      required this.cloudCover});

  @override
  State<ConditionsWidgets> createState() => _ConditionsWidgetsState();
}

class _ConditionsWidgetsState extends State<ConditionsWidgets> {
  static const double _noData = 0.0000001;
  static const int _tileCount = 10;
  static const String orderPrefsKey = 'tile_order_new';
  static const Duration _openContainerDuration = Duration(milliseconds: 500);

  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();

  List<int> itemOrder = List.generate(_tileCount, (index) => index);

  @override
  void initState() {
    super.initState();
    _loadTileOrder();
  }

  List<int> _normalizeOrder(List<int> input) {
    final used = <int>{};
    final result = <int>[];

    for (final i in input) {
      if (i >= 0 && i < _tileCount && used.add(i)) {
        result.add(i);
      }
    }
    for (var i = 0; i < _tileCount; i++) {
      if (used.add(i)) result.add(i);
    }
    return result;
  }

  Future<void> _loadTileOrder() async {
    final prefs = await _prefs;
    final savedList = prefs.getStringList(orderPrefsKey);
    if (savedList == null || savedList.isEmpty) return;

    final parsed = savedList.map(int.tryParse).whereType<int>().toList();
    final normalized = _normalizeOrder(parsed);

    if (listEquals(normalized, itemOrder)) return;
    setState(() => itemOrder = normalized);
  }

  bool _isNoData(num value) => value == _noData;

  void _showNoDataSnack(BuildContext context) {
    ScaffoldMessenger.of(context).hideCurrentSnackBar();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          spacing: 10,
          children: [
            Icon(
              Symbols.error,
              color: Theme.of(context).colorScheme.onInverseSurface,
            ),
            Text(_noDataAvailableMessageKey.tr()),
          ],
        ),
      ),
    );
  }

  PageRouteBuilder<void> _fadeRoute(Widget page) {
    return PageRouteBuilder(
      opaque: true,
      fullscreenDialog: true,
      reverseTransitionDuration: const Duration(milliseconds: 200),
      pageBuilder: (context, animation, secondaryAnimation) => page,
      transitionsBuilder: (context, animation, secondaryAnimation, child) {
        return FadeTransition(opacity: animation, child: child);
      },
    );
  }

  VoidCallback? _tapHandler(
    BuildContext context, {
    required String widgetKey,
    required VoidCallback openContainer,
    required bool useAnimation,
    required bool enabled,
    bool showSnackIfDisabled = true,
  }) {
    if (!widget.isFromHome) return null;

    return () {
      if (!enabled) {
        if (showSnackIfDisabled) _showNoDataSnack(context);
        return;
      }
      if (useAnimation) {
        openContainer();
      } else {
        Navigator.of(context).push(_fadeRoute(ExtendWidget(widgetKey)));
      }
    };
  }

  DateTime? _parseTime(String? timeString) {
    if (timeString == null || timeString.isEmpty) return null;
    final lower = timeString.toLowerCase();
    if (lower.contains("no")) return null;

    try {
      final cleaned = timeString.split("at").first.trim();
      return DateFormat.jm().parse(cleaned);
    } catch (e) {
      debugPrint("Failed to parse time: $timeString ($e)");
      return null;
    }
  }

  double _convertPressure(double hPa, String unit) {
    switch (unit) {
      case 'inHg':
        return UnitConverter.hPaToInHg(hPa);
      case 'mmHg':
        return UnitConverter.hPaToMmHg(hPa);
      default:
        return hPa;
    }
  }

  double _convertPrecip(double mm, String unit) {
    switch (unit) {
      case 'cm':
        return UnitConverter.mmToCm(mm);
      case 'in':
        return UnitConverter.mmToIn(mm);
      default:
        return mm;
    }
  }

  num _convertWind(double kmh, String unit) {
    switch (unit) {
      case 'Mph':
        return UnitConverter.kmhToMph(kmh);
      case 'M/s':
        return UnitConverter.kmhToMs(kmh);
      case 'Bft':
        return UnitConverter.kmhToBeaufort(kmh);
      case 'Kt':
        return UnitConverter.kmhToKt(kmh);
      default:
        return kmh;
    }
  }

  Widget _openContainerTile({
    required Color closedColor,
    Color? middleColor,
    required Color openColor,
    required ShapeBorder closedShape,
    required ShapeBorder openShape,
    double closedElevation = 1,
    double openElevation = 0,
    required Widget Function(BuildContext, VoidCallback) closedBuilder,
    required Widget Function(BuildContext, VoidCallback) openBuilder,
  }) {
    return OpenContainer(
      transitionType: ContainerTransitionType.fadeThrough,
      closedElevation: closedElevation,
      closedShape: closedShape,
      openShape: openShape,
      openElevation: openElevation,
      transitionDuration: _openContainerDuration,
      closedColor: closedColor,
      middleColor: middleColor,
      openColor: openColor,
      openBuilder: (context, _) => openBuilder(context, () {}),
      closedBuilder: (context, openContainer) => closedBuilder(context, openContainer),
    );
  }

  @override
  Widget build(BuildContext context) {
    final settings = context.watch<UnitSettingsNotifier>();

    final theme = Theme.of(context);
    final colorTheme = theme.colorScheme;
    final isDark = theme.brightness == Brightness.dark;
    final headerFg = isDark ? Colors.white : Colors.black;
    final size = MediaQuery.sizeOf(context);
    final isFoldable = isFoldableLayout(context);

    final useAnimation = PreferencesHelper.getBool("UseopenContainerAnimation") ?? true;

    final dewpointConverted = settings.tempUnit == 'Fahrenheit'
        ? UnitConverter.celsiusToFahrenheit(widget.currentDewPoint).round()
        : widget.currentDewPoint.round();

    final offsetSeconds = int.tryParse(widget.utcOffsetSeconds) ?? 0;
    final now = DateTime.now().toUtc().add(Duration(seconds: offsetSeconds));
    final nowLocal = DateTime(now.year, now.month, now.day, now.hour, now.minute, now.second);

    final sunrise = DateTime.parse(widget.currentSunrise);
    var sunset = DateTime.parse(widget.currentSunset);

    final timeFormatter =
        settings.timeUnit == _timeFormat24Hr ? DateFormat.Hm() : DateFormat.jm();
    String formatTime(DateTime value) => timeFormatter.format(value);

    final sunriseFormat = formatTime(sunrise);
    final sunsetFormat = formatTime(sunset);

    final moonriseRaw = widget.moonrise;
    final moonsetRaw = widget.moonset;

    final moonriseTime = _parseTime(moonriseRaw);
    final moonsetTime = _parseTime(moonsetRaw);

    final moonriseFormat = moonriseTime != null ? formatTime(moonriseTime) : _notAvailableValue;
    final moonsetFormat = moonsetTime != null ? formatTime(moonsetTime) : _notAvailableValue;

    final convertedPressure = _convertPressure(widget.currentPressure, settings.pressureUnit);
    final convertedPrecip = _convertPrecip(widget.currentTotalPrec, settings.precipitationUnit);

    final convertedVisibility = settings.visibilityUnit == 'Mile'
        ? UnitConverter.mToMiles(widget.currentVisibility)
        : UnitConverter.mToKm(widget.currentVisibility);

    final formattedWindSpeed = _convertWind(widget.currentWindSpeed, settings.windUnit);

    if (sunset.isBefore(sunrise)) {
      sunset = sunset.add(const Duration(days: 1));
    }

    final dayTotalSeconds = sunset.difference(sunrise).inSeconds;
    final dayPassedSeconds = nowLocal.difference(sunrise).inSeconds;
    final percent = dayTotalSeconds <= 0 ? 0.0 : (dayPassedSeconds / dayTotalSeconds).clamp(0.0, 1.0);

    final aqiFormat = settings.aqiUnit == 'European' ? widget.currentAQIEURO : widget.currentAQIUSA;

    double moonPercent = 0.0;
    if (moonriseTime != null && moonsetTime != null) {
      var moonrise = DateTime(nowLocal.year, nowLocal.month, nowLocal.day, moonriseTime.hour, moonriseTime.minute);
      var moonset = DateTime(nowLocal.year, nowLocal.month, nowLocal.day, moonsetTime.hour, moonsetTime.minute);

      if (moonset.isBefore(moonrise)) {
        moonset = moonset.add(const Duration(days: 1));
      }

      final totalSeconds = moonset.difference(moonrise).inSeconds;
      if (totalSeconds > 0) {
        final secondsSinceMoonrise = nowLocal.difference(moonrise).inSeconds;
        moonPercent = (secondsSinceMoonrise / totalSeconds).clamp(0.0, 1.0);
      }
    }

    Widget buildTile(int i) {
      switch (i) {
        case 0:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('humidity_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'humidity_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !_isNoData(widget.currentPressure),
                ),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(20),
                  child: Container(
                    decoration: BoxDecoration(
                      color: Color(widget.selectedContainerBgIndex),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Stack(
                      children: [
                        AspectRatio(
                          aspectRatio: 1,
                          child: SvgPicture.string(
                            buildHumidity(colorTheme.tertiaryContainer, widget.currentHumidity),
                            fit: BoxFit.contain,
                          ),
                        ),
                        ListTile(
                          leading: Icon(
                            Symbols.humidity_high,
                            fill: 1,
                            color: headerFg,
                          ),
                          horizontalTitleGap: 5,
                          contentPadding: const EdgeInsetsDirectional.only(start: 10, bottom: 0),
                          title: Text(
                            "humidity".tr(),
                            style: TextStyle(color: headerFg),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.only(left: 10),
                          child: Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              "${_isNoData(widget.currentHumidity) ? '--' : widget.currentHumidity}%",
                              style: TextStyle(
                                fontFamily: "FlexFontEn",
                                color: colorTheme.onTertiaryContainer,
                                fontSize: isFoldable ? 70 : size.width * 0.13,
                              ),
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.bottomLeft,
                          child: Padding(
                            padding: const EdgeInsetsDirectional.only(start: 10, bottom: 10),
                            child: Row(
                              spacing: 10,
                              children: [
                                Container(
                                  width: 40,
                                  height: 40,
                                  decoration: BoxDecoration(
                                    color: colorTheme.tertiary,
                                    borderRadius: BorderRadius.circular(50),
                                  ),
                                  child: Center(
                                    child: Text(
                                      _isNoData(widget.currentDewPoint) ? '--' : "$dewpointConverted°",
                                      style: TextStyle(
                                        fontFamily: "FlexFontEn",
                                        color: colorTheme.onTertiary,
                                        fontSize: 16,
                                      ),
                                    ),
                                  ),
                                ),
                                Flexible(
                                  child: Text(
                                    "dew_point".tr(),
                                    style: TextStyle(color: colorTheme.onSurface),
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          );

        case 1:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('sun_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'sun_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: true,
                ),
                child: Container(
                  clipBehavior: Clip.hardEdge,
                  decoration: BoxDecoration(
                    color: Color(widget.selectedContainerBgIndex),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Stack(
                    children: [
                      ListTile(
                        leading: Icon(
                          Symbols.wb_twilight,
                          weight: 500,
                          fill: 1,
                          color: headerFg,
                        ),
                        horizontalTitleGap: 5,
                        contentPadding: const EdgeInsetsDirectional.only(start: 10, bottom: 0),
                        title: Text(
                          "sun_tile_page".tr(),
                          style: TextStyle(color: headerFg),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      Positioned(
                        bottom: -1,
                        left: 0,
                        right: 0,
                        child: SizedBox(
                          height: isFoldable ? 120 : size.height * 0.12,
                          child: SvgPicture.string(
                            clipBehavior: Clip.none,
                            buildSunPathWithIcon(
                              pathColor: colorTheme.primary,
                              percent: percent,
                              outLineColor: colorTheme.onSurface,
                            ),
                            allowDrawingOutsideViewBox: true,
                            fit: BoxFit.fill,
                          ),
                        ),
                      ),
                      Positioned(
                        bottom: 0,
                        right: 0,
                        left: -1,
                        child: Container(
                          height: 65,
                          decoration: BoxDecoration(
                            border: Border(
                              top: BorderSide(
                                color: colorTheme.outline,
                                width: 1.5,
                              ),
                            ),
                            color: const Color.fromRGBO(0, 0, 0, 0.5),
                          ),
                        ),
                      ),
                      Align(
                        alignment: Alignment.bottomCenter,
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              spacing: 5,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                const Icon(
                                  Symbols.vertical_align_top,
                                  weight: 500,
                                  size: 17,
                                  color: Colors.white,
                                ),
                                Text(
                                  sunriseFormat,
                                  style: const TextStyle(
                                    color: Colors.white,
                                    fontFamily: "FlexFontEn",
                                    fontSize: 14,
                                  ),
                                ),
                              ],
                            ),
                            Row(
                              spacing: 5,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                const Icon(
                                  Symbols.vertical_align_bottom,
                                  weight: 500,
                                  size: 17,
                                  color: Colors.white,
                                ),
                                Text(
                                  sunsetFormat,
                                  style: const TextStyle(
                                    fontFamily: "FlexFontEn",
                                    color: Colors.white,
                                    fontSize: 14,
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 10),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          );

        case 2:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: const CircleBorder(),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('pressure_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'pressure_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !_isNoData(widget.currentPressure),
                ),
                child: Stack(
                  children: [
                    AspectRatio(
                      aspectRatio: 1,
                      child: SvgPicture.string(
                        buildPressueSvg(
                          colorTheme.primary,
                          colorTheme.surfaceContainerHighest,
                          Color(widget.selectedContainerBgIndex),
                          widget.currentPressure.round(),
                        ),
                        fit: BoxFit.contain,
                      ),
                    ),
                    HeaderWidgetConditions(
                      headerText: "pressure".tr(),
                      headerIcon: Symbols.compress,
                    ),
                    Align(
                      alignment: Alignment.center,
                      child: Text(
                        _isNoData(widget.currentPressure) ? '--' : "${convertedPressure.round()}",
                        style: TextStyle(
                          fontFamily: "FlexFontEn",
                          color: colorTheme.onSurface,
                          fontSize: isFoldable ? 60 : size.width * 0.1,
                        ),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.only(bottom: 30),
                      child: Align(
                        alignment: Alignment.bottomCenter,
                        child: Text(
                          localizePressureUnit(settings.pressureUnit, context.locale),
                          style: TextStyle(
                            color: colorTheme.onSurfaceVariant,
                            fontSize: 18,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              );
            },
          );

        case 3:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: const CircleBorder(),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('visibility_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'visibility_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !_isNoData(widget.currentVisibility),
                ),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: Container(
                    height: 160,
                    decoration: BoxDecoration(
                      color: Color(widget.selectedContainerBgIndex),
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Stack(
                      children: [
                        AspectRatio(
                          aspectRatio: 1,
                          child: SvgPicture.string(
                            buildVisibilitySvg(colorTheme.tertiaryContainer),
                            fit: BoxFit.contain,
                          ),
                        ),
                        HeaderWidgetConditions(
                          headerText: "visibility".tr(),
                          headerIcon: Symbols.visibility,
                        ),
                        Align(
                          alignment: Alignment.center,
                          child: Text(
                            _isNoData(widget.currentVisibility) ? '--' : "${convertedVisibility.round()}",
                            style: TextStyle(
                              fontFamily: "FlexFontEn",
                              color: colorTheme.onTertiaryContainer,
                              fontSize: isFoldable ? 60 : size.width * 0.1,
                            ),
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.only(bottom: 30),
                          child: Align(
                            alignment: Alignment.bottomCenter,
                            child: Text(
                              localizeVisibilityUnit(settings.visibilityUnit, context.locale),
                              style: TextStyle(
                                color: colorTheme.onSurface,
                                fontSize: 18,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          );

        case 4:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: const CircleBorder(),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('winddirc_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'winddirc_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !_isNoData(widget.currentWindSpeed),
                ),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: Container(
                    height: 160,
                    decoration: BoxDecoration(
                      color: Color(widget.selectedContainerBgIndex),
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Stack(
                      children: [
                        WindCompassWidget(
                          currentWindDirc: widget.currentWindDirc,
                          backgroundColor: Color(widget.selectedContainerBgIndex),
                        ),
                        HeaderWidgetConditions(
                          headerText: "wind".tr(),
                          headerIcon: Symbols.air,
                        ),
                        Align(
                          alignment: Alignment.center,
                          child: Text(
                            _isNoData(widget.currentWindDirc) ? '--' : getCompassDirection(widget.currentWindDirc),
                            style: TextStyle(
                              color: colorTheme.onSurface,
                              fontSize: isFoldable
                                  ? 60
                                  : context.locale.languageCode == 'bg'
                                      ? 24
                                      : size.width * 0.1,
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.bottomCenter,
                          child: Container(
                            margin: const EdgeInsets.only(left: 20, right: 20),
                            height: 55,
                            child: Text(
                              _isNoData(widget.currentWindSpeed)
                                  ? '--'
                                  : '${settings.windUnit == 'M/s' ? formattedWindSpeed.toStringAsFixed(1) : formattedWindSpeed.round()} ${localizeWindUnit(settings.windUnit, context.locale)}',
                              style: TextStyle(
                                color: colorTheme.onSurfaceVariant,
                                fontSize: 18,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              textAlign: TextAlign.center,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          );

        case 5:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 0,
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Colors.transparent,
            middleColor: Colors.transparent,
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('uv_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'uv_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !_isNoData(widget.currentUvIndex),
                ),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: Container(
                    height: 160,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Stack(
                      children: [
                        AspectRatio(
                          aspectRatio: 1,
                          child: SvgPicture.string(
                            buildUVSvg(
                              Color(widget.selectedContainerBgIndex),
                              widget.currentUvIndex.round(),
                            ),
                            fit: BoxFit.contain,
                          ),
                        ),
                        HeaderWidgetConditions(
                          headerText: "uv_index".tr(),
                          headerIcon: Symbols.light_mode,
                        ),
                        Align(
                          alignment: Alignment.center,
                          child: Text(
                            _isNoData(widget.currentUvIndex) ? '--' : "${widget.currentUvIndex.round()}",
                            style: TextStyle(
                              color: colorTheme.onSurface,
                              fontSize: isFoldable ? 60 : size.width * 0.1,
                            ),
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.only(bottom: 40),
                          child: Align(
                            alignment: Alignment.bottomCenter,
                            child: Text(
                              _isNoData(widget.currentUvIndex) ? '--' : getUvIndexType(widget.currentUvIndex.round()),
                              style: TextStyle(
                                fontFamily: "FlexFontEn",
                                color: colorTheme.onSurfaceVariant,
                                fontSize: 15,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          );

        case 6:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('aqi_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'aqi_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !_isNoData(aqiFormat),
                ),
                child: Container(
                  decoration: BoxDecoration(
                    color: Color(widget.selectedContainerBgIndex),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Stack(
                    children: [
                      ListTile(
                        leading: Icon(
                          Symbols.airwave,
                          weight: 500,
                          fill: 1,
                          color: headerFg,
                        ),
                        horizontalTitleGap: 5,
                        contentPadding: const EdgeInsetsDirectional.only(start: 10, bottom: 0),
                        title: Text(
                          "AQI",
                          style: TextStyle(color: headerFg),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      Align(
                        alignment: Alignment.centerRight,
                        child: Padding(
                          padding: const EdgeInsets.only(right: 10, bottom: 12),
                          child: Text(
                            _isNoData(aqiFormat) ? '--' : aqiFormat.toString(),
                            style: TextStyle(
                              fontFamily: "FlexFontEn",
                              fontSize: isFoldable ? 60 : size.width * 0.11,
                              color: colorTheme.onSurface,
                            ),
                          ),
                        ),
                      ),
                      AQISliderBar(
                        aqi: widget.currentAQIUSA,
                        width: 360,
                      ),
                      Align(
                        alignment: Alignment.bottomRight,
                        child: Padding(
                          padding: const EdgeInsets.only(right: 10, bottom: 25),
                          child: Text(
                            _isNoData(aqiFormat)
                                ? '--'
                                : getAQIIndexType(aqiFormat, settings.aqiUnit == 'European'),
                            style: TextStyle(
                              fontSize: 17,
                              color: colorTheme.onSurfaceVariant,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          );

        case 7:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('precip_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'precip_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !_isNoData(widget.currentTotalPrec),
                ),
                child: Container(
                  decoration: BoxDecoration(
                    color: Color(widget.selectedContainerBgIndex),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Stack(
                    children: [
                      ListTile(
                        leading: Icon(
                          Symbols.rainy_heavy,
                          weight: 500,
                          fill: 1,
                          color: headerFg,
                        ),
                        horizontalTitleGap: 5,
                        contentPadding: const EdgeInsetsDirectional.only(start: 10, bottom: 0),
                        title: Text(
                          "precipitation".tr(),
                          style: TextStyle(color: headerFg),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      Align(
                        alignment: Alignment.centerLeft,
                        child: Padding(
                          padding: const EdgeInsetsDirectional.only(start: 10, bottom: 12),
                          child: Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            spacing: 2,
                            children: [
                              Text(
                                _isNoData(widget.currentTotalPrec)
                                    ? '--'
                                    : "${double.parse(convertedPrecip.toStringAsFixed(2))}",
                                style: TextStyle(
                                  fontFamily: "FlexFontEn",
                                  fontSize: isFoldable ? 60 : size.width * 0.10 + 0.5,
                                  color: colorTheme.onSurface,
                                ),
                              ),
                              Padding(
                                padding: const EdgeInsets.only(top: 15),
                                child: Text(
                                  localizePrecipUnit(settings.precipitationUnit, context.locale),
                                  style: TextStyle(
                                    fontSize: 20,
                                    color: colorTheme.secondary,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      Align(
                        alignment: Alignment.bottomLeft,
                        child: Padding(
                          padding: const EdgeInsets.only(left: 10, right: 10, bottom: 10),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              SizedBox(
                                width: 100,
                                child: Text(
                                  "total_precip_sub".tr(),
                                  style: TextStyle(
                                    height: 1.2,
                                    color: colorTheme.onSurfaceVariant,
                                  ),
                                  maxLines: 2,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                              SvgPicture.asset(
                                WeatherIconMapper.getIcon(63, 1),
                                width: 30,
                                height: 30,
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          );

        case 8:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => ExtendWidget('moon_widget'),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: _tapHandler(
                  context,
                  widgetKey: 'moon_widget',
                  openContainer: openContainer,
                  useAnimation: useAnimation,
                  enabled: !(moonriseTime == null && moonsetTime == null),
                  showSnackIfDisabled: false,
                ),
                child: Container(
                  clipBehavior: Clip.hardEdge,
                  decoration: BoxDecoration(
                    color: Color(widget.selectedContainerBgIndex),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Stack(
                    children: [
                      ListTile(
                        leading: MoonWidget(
                          date: nowLocal,
                          pixelSize: 128,
                          size: 22,
                          moonColor: Colors.amber,
                          earthshineColor: Colors.blueGrey,
                        ),
                        horizontalTitleGap: 5,
                        contentPadding: const EdgeInsetsDirectional.only(start: 10, bottom: 0),
                        title: Text(
                          "moon".tr(),
                          style: TextStyle(color: headerFg),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      Positioned(
                        bottom: -1,
                        left: 0,
                        right: 0,
                        child: SizedBox(
                          height: isFoldable ? 120 : size.height * 0.12,
                          child: SvgPicture.string(
                            clipBehavior: Clip.none,
                            buildMoonPathWithIcon(
                              pathColor: colorTheme.primary,
                              percent: moonPercent,
                              outLineColor: colorTheme.outline,
                            ),
                            allowDrawingOutsideViewBox: true,
                            fit: BoxFit.fill,
                          ),
                        ),
                      ),
                      Positioned(
                        bottom: 0,
                        right: 0,
                        left: -1,
                        child: Container(
                          height: 65,
                          decoration: BoxDecoration(
                            border: Border(
                              top: BorderSide(
                                color: colorTheme.outline,
                                width: 1.5,
                              ),
                            ),
                            color: const Color.fromRGBO(0, 0, 0, 0.5),
                          ),
                        ),
                      ),
                      Align(
                        alignment: Alignment.bottomCenter,
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              spacing: 5,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                const Icon(
                                  Symbols.vertical_align_top,
                                  weight: 500,
                                  size: 17,
                                  color: Colors.white,
                                ),
                                Text(
                                  moonriseFormat,
                                  style: const TextStyle(
                                    color: Colors.white,
                                    fontFamily: "FlexFontEn",
                                    fontSize: 14,
                                  ),
                                ),
                              ],
                            ),
                            Row(
                              spacing: 5,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                const Icon(
                                  Symbols.vertical_align_bottom,
                                  weight: 500,
                                  size: 17,
                                  color: Colors.white,
                                ),
                                Text(
                                  moonsetFormat,
                                  style: const TextStyle(
                                    fontFamily: "FlexFontEn",
                                    color: Colors.white,
                                    fontSize: 14,
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 10),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          );

        case 9:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 1,
            closedShape: const CircleBorder(),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: const Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) => const SizedBox.shrink(),
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                onTap: () {},
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: Container(
                    height: 160,
                    decoration: BoxDecoration(
                      color: Color(widget.selectedContainerBgIndex),
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Stack(
                      children: [
                        AspectRatio(
                          aspectRatio: 1,
                          child: SvgPicture.string(
                            buildCloudCoverSvg(colorTheme.tertiaryContainer),
                            fit: BoxFit.contain,
                          ),
                        ),
                        HeaderWidgetConditions(
                          headerText: "cloudiness".tr(),
                          headerIcon: Symbols.cloud,
                        ),
                        Align(
                          alignment: Alignment.center,
                          child: Text(
                            _isNoData(widget.currentVisibility) ? '--' : widget.cloudCover,
                            style: TextStyle(
                              color: colorTheme.onTertiaryContainer,
                              fontFamily: "FlexFontEn",
                              fontSize: isFoldable ? 60 : size.width * 0.1,
                            ),
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.only(bottom: 30),
                          child: Align(
                            alignment: Alignment.bottomCenter,
                            child: Text(
                              "%",
                              style: TextStyle(
                                color: colorTheme.onSurface,
                                fontSize: 20,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          );

        default:
          return const SizedBox();
      }
    }

// 123216

    return Container(
      margin: const EdgeInsets.fromLTRB(12.7, 0, 12.7, 0),
      child: Column(
        children: [
          ReorderableGridView.builder(
            itemCount: itemOrder.length,
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
              childAspectRatio: 1,
            ),
            itemBuilder: (context, index) {
              final item = itemOrder[index];
              return Container(
                key: ValueKey(item),
                child: buildTile(item),
              );
            },
            dragWidgetBuilder: (index, child) {
              return Material(
                type: MaterialType.transparency,
                child: child,
              );
            },
            onReorder: (oldIndex, newIndex) async {
              setState(() {
                final item = itemOrder.removeAt(oldIndex);
                itemOrder.insert(newIndex, item);
              });

              final prefs = await _prefs;
              prefs.setStringList(
                orderPrefsKey,
                itemOrder.map((e) => e.toString()).toList(),
              );
            },
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            padding: const EdgeInsets.only(top: 0),
          ),
        ],
      ),
    );
  }
}

// svgs

class HeaderWidgetConditions extends StatelessWidget {
  final String headerText;
  final IconData headerIcon;

  const HeaderWidgetConditions(
      {super.key, required this.headerText, required this.headerIcon});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final headerFg = isDark ? Colors.white : Colors.black;

    return Positioned(
      left: 0,
      right: 0,
      top: 35,
      child: Center(
        child: Padding(
          padding: const EdgeInsets.only(left: 16, right: 16),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                headerIcon,
                weight: 500,
                fill: 1,
                color: headerFg,
                size: 18,
              ),
              const SizedBox(width: 3),
              Flexible(
                child: Text(
                  headerText,
                  style: TextStyle(
                    color: headerFg,
                    fontSize: 14,
                  ),
                  textAlign: TextAlign.center,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// wind dirc

class WindCompassWidget extends StatefulWidget {
  final int currentWindDirc;
  final Color backgroundColor;

  const WindCompassWidget({
    required this.currentWindDirc,
    required this.backgroundColor,
    super.key,
  });

  @override
  State<WindCompassWidget> createState() => _WindCompassWidgetState();
}

class _WindCompassWidgetState extends State<WindCompassWidget> {
  double _previousRotation = 0;
  double? _lastHeading;

  double lowPassFilter(double newValue, double? oldValue, double alpha) {
    if (oldValue == null) return newValue;
    return oldValue + alpha * (newValue - oldValue);
  }

  double _normalizeAngle(double angle) => atan2(sin(angle), cos(angle));

  @override
  Widget build(BuildContext context) {
    final useDeviceCompass =
        context.select<UnitSettingsNotifier, bool>((n) => n.useDeviceCompass);

    final svg = AspectRatio(
      aspectRatio: 1,
      child: SvgPicture.string(
        buildWindSvg(
          Theme.of(context).colorScheme.primaryContainer,
          widget.backgroundColor,
        ),
        fit: BoxFit.contain,
      ),
    );

    if (useDeviceCompass && !kIsWeb && (Platform.isAndroid || Platform.isIOS)) {
      return StreamBuilder<CompassEvent>(
        stream: FlutterCompass.events,
        builder: (context, snapshot) {
          final rawHeading = snapshot.data?.heading;
          if (rawHeading == null) return const SizedBox();

          _lastHeading = lowPassFilter(rawHeading, _lastHeading, 0.2);
          final smoothedHeading = _lastHeading!;

          final targetRotation = (widget.currentWindDirc - smoothedHeading) * (pi / 180);
          final delta = _normalizeAngle(targetRotation - _previousRotation);
          final newRotation = _previousRotation + delta;

          final begin = _previousRotation;
          _previousRotation = newRotation;

          return TweenAnimationBuilder<double>(
            tween: Tween<double>(begin: begin, end: newRotation),
            duration: const Duration(milliseconds: 300),
            builder: (context, angle, child) {
              return Transform.rotate(
                angle: angle,
                child: child,
              );
            },
            child: svg,
          );
        },
      );
    }

    return Transform.rotate(
      angle: widget.currentWindDirc * (pi / 180),
      child: svg,
    );
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
      bottom: 55,
      left: 0,
      right: 0,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          Container(
            height: height,
            width: width,
            margin: const EdgeInsets.only(left: 10, right: 10),
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
            top: -2.5,
            child: Column(
              children: [
                Container(
                  width: 10,
                  height: 10,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.surface,
                    shape: BoxShape.circle,
                    border: Border.all(
                      color: Theme.of(context).colorScheme.outline,
                    ),
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
