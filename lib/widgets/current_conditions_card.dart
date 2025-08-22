import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:weather_master_app/utils/condition_label_map.dart';
import '../notifiers/unit_settings_notifier.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import 'dart:math';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:flutter_compass/flutter_compass.dart';
import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:reorderable_grid_view/reorderable_grid_view.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:async';
import '../screens/extended_widgets.dart';
import '../helper/locale_helper.dart';
import '../utils/visual_utils.dart';
import 'package:animations/animations.dart';

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
      required this.isFromHome});

  @override
  State<ConditionsWidgets> createState() => _ConditionsWidgetsState();
}

class _ConditionsWidgetsState extends State<ConditionsWidgets> {
  List<int> itemOrder = [];

  final String orderPrefsKey = 'tile_order';

  @override
  void initState() {
    super.initState();
    _loadTileOrder();
  }

  Future<void> _loadTileOrder() async {
    final prefs = await SharedPreferences.getInstance();
    final savedList = prefs.getStringList(orderPrefsKey);

    if (savedList != null && savedList.isNotEmpty) {
      setState(() {
        itemOrder = savedList.map(int.parse).toList();
      });
    } else {
      // Default order
      setState(() {
        itemOrder = List.generate(8, (index) => index);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
//     return const Placeholder();
//   }
// }

// class ConditionsWidgets extends StatelessWidget {

    // @override
    // Widget build(BuildContext context) {
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;

    final dewpointConverted = tempUnit == 'Fahrenheit'
        ? UnitConverter.celsiusToFahrenheit(widget.currentDewPoint.toDouble())
            .round()
        : widget.currentDewPoint.toDouble().round();
    // DateTime now = DateTime.now();
    int offsetSeconds = int.parse(widget.utcOffsetSeconds);
    DateTime utcNow = DateTime.now().toUtc();
    DateTime now = utcNow.add(Duration(seconds: offsetSeconds));
    DateTime sunrise = DateTime.parse(widget.currentSunrise);
    DateTime sunset = DateTime.parse(widget.currentSunset);

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

    final timeUnit = context.watch<UnitSettingsNotifier>().timeUnit;
    final isShowFrog = context.read<UnitSettingsNotifier>().showFrog;

    final sunriseFormat = timeUnit == '24 hr'
        ? DateFormat.Hm().format(sunrise)
        : DateFormat.jm().format(sunrise);
    final sunsetFormat = timeUnit == '24 hr'
        ? DateFormat.Hm().format(sunset)
        : DateFormat.jm().format(sunset);

    final pressureUnit = context.watch<UnitSettingsNotifier>().pressureUnit;
    final precipitationUnit =
        context.watch<UnitSettingsNotifier>().precipitationUnit;
    final visibilityUnit = context.watch<UnitSettingsNotifier>().visibilityUnit;
    final aqiUnit = context.watch<UnitSettingsNotifier>().aqiUnit;

    final convertedPressure = pressureUnit == 'inHg'
        ? UnitConverter.hPaToInHg(widget.currentPressure)
        : pressureUnit == 'mmHg'
            ? UnitConverter.hPaToMmHg(widget.currentPressure)
            : widget.currentPressure;

    final convertedPrecip = precipitationUnit == 'cm'
        ? UnitConverter.mmToCm(widget.currentTotalPrec)
        : precipitationUnit == 'in'
            ? UnitConverter.mmToIn(widget.currentTotalPrec)
            : widget.currentTotalPrec;

    final convertedVisibility = visibilityUnit == 'Mile'
        ? UnitConverter.mToMiles(widget.currentVisibility.toDouble())
        : UnitConverter.mToKm(widget.currentVisibility.toDouble());

    if (sunset.isBefore(sunrise)) {
      sunset = sunset.add(Duration(days: 1));
    }

    double percent = ((now.difference(sunrise).inSeconds) /
            (sunset.difference(sunrise).inSeconds))
        .clamp(0, 1);

    final aqiFormat =
        aqiUnit == 'European' ? widget.currentAQIEURO : widget.currentAQIUSA;

    final colorTheme = Theme.of(context).colorScheme;

    List<Widget> gridItems = itemOrder.map((i) {
      switch (i) {
        case 0:
          return OpenContainer(
              transitionType: ContainerTransitionType.fadeThrough,
              closedElevation: 0,
              closedShape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20),
              ),
              openShape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20),
              ),
              openElevation: 0,
              transitionDuration: Duration(milliseconds: 500),
              closedColor: Color(widget.selectedContainerBgIndex),
              openColor: colorTheme.surface,
              openBuilder: (context, _) {
                return ExtendWidget('humidity_widget');
              },
              closedBuilder: (context, openContainer) {
                return GestureDetector(
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
                              buildHumidity(
                                  Theme.of(context)
                                      .colorScheme
                                      .tertiaryContainer,
                                  widget.currentHumidity),
                              fit: BoxFit.contain,
                            ),
                          ),
                          ListTile(
                            leading: Icon(
                              Symbols.humidity_high,
                              fill: 1,
                              color: Theme.of(context).brightness ==
                                      Brightness.dark
                                  ? Colors.white
                                  : Colors.black,
                            ),
                            horizontalTitleGap: 5,
                            contentPadding:
                                EdgeInsets.only(left: 10, bottom: 0),
                            title: Text(
                              "humidity".tr(),
                              style: TextStyle(
                                  color: Theme.of(context).brightness ==
                                          Brightness.dark
                                      ? Colors.white
                                      : Colors.black),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                          Container(
                              padding: EdgeInsets.only(left: 10),
                              child: Align(
                                alignment: Alignment.centerLeft,
                                child: Text(
                                  "${widget.currentHumidity == 0.0000001 ? '--' : widget.currentHumidity}%",
                                  style: TextStyle(
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onTertiaryContainer,
                                    fontSize:
                                        MediaQuery.of(context).size.width *
                                            0.13,
                                    fontVariations: [
                                      FontVariation('wght', 500),
                                      FontVariation('ROND', 100),
                                    ],
                                  ),
                                ),
                              )),
                          Align(
                              alignment: Alignment.bottomLeft,
                              child: Padding(
                                padding: EdgeInsets.only(left: 10, bottom: 10),
                                child: Row(
                                  spacing: 10,
                                  children: [
                                    Container(
                                      width: 40,
                                      height: 40,
                                      decoration: BoxDecoration(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .tertiary,
                                        borderRadius: BorderRadius.circular(50),
                                      ),
                                      child: Center(
                                          child: Text(
                                              widget.currentDewPoint ==
                                                      0.0000001
                                                  ? '--'
                                                  : "$dewpointConverted°",
                                              style: TextStyle(
                                                color: Theme.of(context)
                                                    .colorScheme
                                                    .onTertiary,
                                                fontSize: 16,
                                                fontVariations: [
                                                  FontVariation('wght', 450),
                                                  FontVariation('ROND', 0),
                                                ],
                                              ))),
                                    ),
                                    Flexible(
                                        child: Text(
                                      "dew_point".tr(),
                                      style: TextStyle(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                        fontVariations: [
                                          FontVariation('wght', 450),
                                          FontVariation('ROND', 0),
                                        ],
                                      ),
                                      overflow: TextOverflow.ellipsis,
                                    ))
                                  ],
                                ),
                              )),
                        ],
                      ),
                    ),
                  ),
                  onTap: () {
                    if (widget.isFromHome) {
                      if (widget.currentPressure == 0.0000001) {
                        ScaffoldMessenger.of(context).hideCurrentSnackBar();
                        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                            content: Row(
                          spacing: 10,
                          children: [
                            Icon(
                              Symbols.error,
                              color: Theme.of(context)
                                  .colorScheme
                                  .onInverseSurface,
                            ),
                            Text("No data available")
                          ],
                        )));
                      } else {
                        openContainer();
                      }
                    }
                  },
                );
              });
        case 1:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 0,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) {
              return ExtendWidget('sun_widget');
            },
            closedBuilder: (context, openContainer) {
              return GestureDetector(
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
                          color: Theme.of(context).brightness == Brightness.dark
                              ? Colors.white
                              : Colors.black,
                        ),
                        horizontalTitleGap: 5,
                        contentPadding: EdgeInsets.only(left: 10, bottom: 0),
                        title: Text("sun_tile_page".tr(),
                            style: TextStyle(
                                color: Theme.of(context).brightness ==
                                        Brightness.dark
                                    ? Colors.white
                                    : Colors.black),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis),
                      ),
                      Positioned(
                        bottom: -1,
                        left: 0,
                        right: 0,
                        child: SizedBox(
                          height: MediaQuery.of(context).size.height * 0.12,
                          child: SvgPicture.string(
                            clipBehavior: Clip.none,
                            buildSunPathWithIcon(
                              pathColor: Theme.of(context).colorScheme.primary,
                              percent: percent,
                              outLineColor:
                                  Theme.of(context).colorScheme.onSurface,
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
                                color: Theme.of(context).colorScheme.outline,
                                width: 1.5,
                              ),
                            ),
                            color: const Color.fromRGBO(0, 0, 0, 0.5),
                            // borderRadius: BorderRadius.only(bottomLeft: Radius.circular(20), bottomRight: Radius.circular(20))
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
                                  Icon(
                                    Symbols.vertical_align_top,
                                    weight: 500,
                                    size: 17,
                                    color: Colors.white,
                                  ),
                                  Text(sunriseFormat,
                                      style: TextStyle(
                                        color: Colors.white,
                                        fontVariations: [
                                          FontVariation('wght', 500),
                                          FontVariation('ROND', 100),
                                        ],
                                        fontSize: 13,
                                      ))
                                ],
                              ),
                              Row(
                                spacing: 5,
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  Icon(
                                    Symbols.vertical_align_bottom,
                                    weight: 500,
                                    size: 17,
                                    color: Colors.white,
                                  ),
                                  Text(sunsetFormat,
                                      style: TextStyle(
                                          color: Colors.white,
                                          fontVariations: [
                                            FontVariation('wght', 500),
                                            FontVariation('ROND', 100),
                                          ],
                                          fontSize: 13)),
                                ],
                              ),
                              SizedBox(
                                height: 10,
                              )
                            ],
                          )),
                    ],
                  ),
                ),
                onTap: () {
                  widget.isFromHome ? openContainer() : null;
                },
              );
            },
          );
        case 2:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 0,
            closedShape: const CircleBorder(),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) {
              return ExtendWidget('pressure_widget');
            },
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                child: Stack(children: [
                  AspectRatio(
                    aspectRatio: 1,
                    child: SvgPicture.string(
                        buildPressueSvg(
                            Theme.of(context).colorScheme.primary,
                            Theme.of(context)
                                .colorScheme
                                .surfaceContainerHighest,
                            Color(widget.selectedContainerBgIndex),
                            widget.currentPressure.round()),
                        fit: BoxFit.contain),
                  ),
                  headerWidgetConditions(
                    headerText: "pressure".tr(),
                    headerIcon: Symbols.compress,
                  ),
                  Align(
                    alignment: Alignment.center,
                    child: Text(
                      widget.currentPressure == 0.0000001
                          ? '--'
                          : "${convertedPressure.round()}",
                      style: TextStyle(
                        color: Theme.of(context).colorScheme.onSurface,
                        fontSize: MediaQuery.of(context).size.width * 0.1,
                        fontVariations: [
                          FontVariation('wght', 500),
                          FontVariation('ROND', 100),
                        ],
                      ),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.only(bottom: 30),
                    child: Align(
                      alignment: Alignment.bottomCenter,
                      child: Text(
                        localizePressureUnit(pressureUnit, context.locale),
                        style: TextStyle(
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                          fontSize: 18,
                          fontVariations: [
                            FontVariation('wght', 450),
                            FontVariation('ROND', 0),
                          ],
                        ),
                      ),
                    ),
                  ),
                ]),
                onTap: () {
                  if (widget.isFromHome) {
                    if (widget.currentPressure == 0.0000001) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                          content: Row(
                        spacing: 10,
                        children: [
                          Icon(
                            Symbols.error,
                            color:
                                Theme.of(context).colorScheme.onInverseSurface,
                          ),
                          Text("No data available")
                        ],
                      )));
                    } else {
                      openContainer();
                    }
                  }
                },
              );
            },
          );
        case 3:
          return OpenContainer(
              transitionType: ContainerTransitionType.fadeThrough,
              closedElevation: 0,
              closedShape: const CircleBorder(),
              openShape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20),
              ),
              openElevation: 0,
              transitionDuration: Duration(milliseconds: 500),
              closedColor: Color(widget.selectedContainerBgIndex),
              openColor: colorTheme.surface,
              openBuilder: (context, _) {
                return ExtendWidget('visibility_widget');
              },
              closedBuilder: (context, openContainer) {
                return GestureDetector(
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
                                buildVisibilitySvg(Theme.of(context)
                                    .colorScheme
                                    .tertiaryContainer),
                                fit: BoxFit.contain,
                              ),
                            ),
                            headerWidgetConditions(
                              headerText: "visibility".tr(),
                              headerIcon: Symbols.visibility,
                            ),
                            Align(
                              alignment: Alignment.center,
                              child: Text(
                                widget.currentVisibility == 0.0000001
                                    ? '--'
                                    : "${convertedVisibility.round()}",
                                style: TextStyle(
                                  color: Theme.of(context)
                                      .colorScheme
                                      .onTertiaryContainer,
                                  fontSize:
                                      MediaQuery.of(context).size.width * 0.1,
                                  fontVariations: [
                                    FontVariation('wght', 500),
                                    FontVariation('ROND', 100),
                                  ],
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.only(bottom: 30),
                              child: Align(
                                alignment: Alignment.bottomCenter,
                                child: Text(
                                  localizeVisibilityUnit(
                                      visibilityUnit, context.locale),
                                  style: TextStyle(
                                    color:
                                        Theme.of(context).colorScheme.onSurface,
                                    fontSize: 18,
                                    fontVariations: [
                                      FontVariation('wght', 450),
                                      FontVariation('ROND', 0),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    onTap: () {
                      if (widget.isFromHome) {
                        if (widget.currentVisibility == 0.0000001) {
                          ScaffoldMessenger.of(context).hideCurrentSnackBar();
                          ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                              content: Row(
                            spacing: 10,
                            children: [
                              Icon(
                                Symbols.error,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onInverseSurface,
                              ),
                              Text("No data available")
                            ],
                          )));
                        } else {
                          openContainer();
                        }
                      }
                    });
              });

        case 4:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 0,
            closedShape: const CircleBorder(),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) {
              return ExtendWidget('winddirc_widget');
            },
            closedBuilder: (context, openContainer) {
              return GestureDetector(
                child: ClipRRect(
                    borderRadius: BorderRadius.circular(999),
                    child: Container(
                        height: 160,
                        decoration: BoxDecoration(
                          color: Color(widget.selectedContainerBgIndex),
                          borderRadius: BorderRadius.circular(999),
                        ),
                        child: Stack(children: [
                          WindCompassWidget(
                              currentWindDirc: widget.currentWindDirc,
                              backgroundColor:
                                  Color(widget.selectedContainerBgIndex)),
                          headerWidgetConditions(
                            headerText: "direction".tr(),
                            headerIcon: Symbols.explore,
                          ),
                          Align(
                            alignment: Alignment.center,
                            child: Text(
                              widget.currentWindDirc == 0.0000001
                                  ? '--'
                                  : getCompassDirection(widget.currentWindDirc),
                              style: TextStyle(
                                color: Theme.of(context).colorScheme.onSurface,
                                fontSize:
                                    MediaQuery.of(context).size.width * 0.1,
                                fontVariations: [
                                  FontVariation('wght', 500),
                                  FontVariation('ROND', 100),
                                ],
                              ),
                            ),
                          ),
                          Align(
                            alignment: Alignment.bottomCenter,
                            child: Container(
                              margin: EdgeInsets.only(left: 20, right: 20),
                              height: 55,
                              child: Text(
                                widget.currentWindSpeed == 0.0000001
                                    ? '--'
                                    : getWindSpeedType(widget.currentWindSpeed),
                                style: TextStyle(
                                  color: Theme.of(context)
                                      .colorScheme
                                      .onSurfaceVariant,
                                  fontSize: 16,
                                  fontVariations: [
                                    FontVariation('wght', 450),
                                    FontVariation('ROND', 0),
                                  ],
                                ),
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ]))),
                onTap: () {
                  if (widget.isFromHome) {
                    if (widget.currentWindSpeed == 0.0000001) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                          content: Row(
                        spacing: 10,
                        children: [
                          Icon(
                            Symbols.error,
                            color:
                                Theme.of(context).colorScheme.onInverseSurface,
                          ),
                          Text("No data available")
                        ],
                      )));
                    } else {
                      openContainer();
                    }
                  }
                },
              );
            },
          );
        case 5:
          return OpenContainer(
              transitionType: ContainerTransitionType.fadeThrough,
              closedElevation: 0,
              // closedShape: const CircleBorder(),
              openShape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20),
              ),
              openElevation: 0,
              transitionDuration: Duration(milliseconds: 500),
              closedColor: Colors.transparent,
              middleColor: Colors.transparent,
              openColor: colorTheme.surface,
              openBuilder: (context, _) {
                return ExtendWidget('uv_widget');
              },
              closedBuilder: (context, openContainer) {
                return GestureDetector(
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
                                    widget.currentUvIndex.round()),
                                fit: BoxFit.contain,
                              ),
                            ),
                            headerWidgetConditions(
                              headerText: "uv_index".tr(),
                              headerIcon: Symbols.light_mode,
                            ),
                            Align(
                              alignment: Alignment.center,
                              child: Text(
                                "${widget.currentUvIndex == 0.0000001 ? '--' : widget.currentUvIndex.round()}",
                                style: TextStyle(
                                  color:
                                      Theme.of(context).colorScheme.onSurface,
                                  fontSize:
                                      MediaQuery.of(context).size.width * 0.1,
                                  fontVariations: [
                                    FontVariation('wght', 500),
                                    FontVariation('ROND', 100),
                                  ],
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.only(bottom: 40),
                              child: Align(
                                alignment: Alignment.bottomCenter,
                                child: Text(
                                  widget.currentUvIndex == 0.0000001
                                      ? '--'
                                      : getUvIndexType(
                                          widget.currentUvIndex.round()),
                                  style: TextStyle(
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                    fontSize: 15,
                                    fontVariations: [
                                      FontVariation('wght', 450),
                                      FontVariation('ROND', 0),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    onTap: () {
                      if (widget.isFromHome) {
                        if (widget.currentUvIndex == 0.0000001) {
                          ScaffoldMessenger.of(context).hideCurrentSnackBar();
                          ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                              content: Row(
                            spacing: 10,
                            children: [
                              Icon(
                                Symbols.error,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onInverseSurface,
                              ),
                              Text("No data available")
                            ],
                          )));
                        } else {
                          openContainer();
                        }
                      }
                    });
              });

        case 6:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 0,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) {
              return ExtendWidget('aqi_widget');
            },
            closedBuilder: (context, openContainer) {
              return GestureDetector(
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
                          color: Theme.of(context).brightness == Brightness.dark
                              ? Colors.white
                              : Colors.black,
                        ),
                        horizontalTitleGap: 5,
                        contentPadding: EdgeInsets.only(left: 10, bottom: 0),
                        title: Text("AQI",
                            style: TextStyle(
                                color: Theme.of(context).brightness ==
                                        Brightness.dark
                                    ? Colors.white
                                    : Colors.black),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis),
                      ),
                      Align(
                        alignment: Alignment.centerRight,
                        child: Padding(
                          padding: EdgeInsets.only(right: 10, bottom: 12),
                          child: Text(
                            aqiFormat == 0.0000001
                                ? '--'
                                : aqiFormat.toString(),
                            style: TextStyle(
                                fontSize:
                                    MediaQuery.of(context).size.width * 0.11,
                                fontVariations: [
                                  FontVariation('wght', 500),
                                  FontVariation('ROND', 100),
                                ],
                                color: Theme.of(context).colorScheme.onSurface),
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
                          padding: EdgeInsets.only(right: 10, bottom: 25),
                          child: Text(
                            aqiFormat == 0.0000001
                                ? '--'
                                : getAQIIndexType(aqiFormat,
                                    aqiUnit == 'European' ? true : false),
                            style: TextStyle(
                                fontSize: 17,
                                fontVariations: [
                                  FontVariation('wght', 450),
                                  FontVariation('ROND', 0),
                                ],
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                onTap: () {
                  if (widget.isFromHome) {
                    if (aqiFormat == 0.0000001) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                          content: Row(
                        spacing: 10,
                        children: [
                          Icon(
                            Symbols.error,
                            color:
                                Theme.of(context).colorScheme.onInverseSurface,
                          ),
                          Text("No data available")
                        ],
                      )));
                    } else {
                      openContainer();
                    }
                  }
                },
              );
            },
          );
        case 7:
          return OpenContainer(
            transitionType: ContainerTransitionType.fadeThrough,
            closedElevation: 0,
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            openElevation: 0,
            transitionDuration: Duration(milliseconds: 500),
            closedColor: Color(widget.selectedContainerBgIndex),
            openColor: colorTheme.surface,
            openBuilder: (context, _) {
              return ExtendWidget('precip_widget');
            },
            closedBuilder: (context, openContainer) {
              return GestureDetector(
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
                          color: Theme.of(context).brightness == Brightness.dark
                              ? Colors.white
                              : Colors.black,
                        ),
                        horizontalTitleGap: 5,
                        contentPadding: EdgeInsets.only(left: 10, bottom: 0),
                        title: Text("precipitation".tr(),
                            style: TextStyle(
                                color: Theme.of(context).brightness ==
                                        Brightness.dark
                                    ? Colors.white
                                    : Colors.black),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis),
                      ),
                      Align(
                        alignment: Alignment.centerLeft,
                        child: Padding(
                          padding: EdgeInsets.only(left: 10, bottom: 12),
                          child: Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Text(
                                widget.currentTotalPrec == 0.0000001
                                    ? '--'
                                    : "${double.parse(convertedPrecip.toStringAsFixed(2))}",
                                style: TextStyle(
                                    fontSize:
                                        MediaQuery.of(context).size.width *
                                                0.10 +
                                            0.5,
                                    fontVariations: [
                                      FontVariation('wght', 500),
                                      FontVariation('ROND', 100),
                                    ],
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurface),
                              ),
                              Padding(
                                padding: EdgeInsets.only(top: 15),
                                child: Text(
                                  localizePrecipUnit(
                                      precipitationUnit, context.locale),
                                  style: TextStyle(
                                      fontSize: 20,
                                      fontVariations:
                                          FontVariationsRegularNoRound,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .secondary),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      Align(
                          alignment: Alignment.bottomLeft,
                          child: Padding(
                              padding: EdgeInsets.only(
                                  left: 10, right: 10, bottom: 10),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  SizedBox(
                                      width: 100,
                                      child: Text(
                                        "total_precip_sub".tr(),
                                        style: TextStyle(
                                            height: 1.2,
                                            fontVariations: [
                                              FontVariation('wght', 450),
                                              FontVariation('ROND', 0),
                                            ],
                                            color: Theme.of(context)
                                                .colorScheme
                                                .onSurfaceVariant),
                                        maxLines: 2,
                                        overflow: TextOverflow.ellipsis,
                                      )),
                                  SvgPicture.asset(
                                    'assets/weather-icons/showers_rain.svg',
                                    width: 30,
                                    height: 30,
                                  )
                                ],
                              ))),
                    ],
                  ),
                ),
                onTap: () {
                  if (widget.isFromHome) {
                    if (widget.currentTotalPrec == 0.0000001) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                          content: Row(
                        spacing: 10,
                        children: [
                          Icon(
                            Symbols.error,
                            color:
                                Theme.of(context).colorScheme.onInverseSurface,
                          ),
                          Text("No data available")
                        ],
                      )));
                    } else {
                      openContainer();
                    }
                  }
                },
              );
            },
          );
        default:
          return const SizedBox();
      }
    }).toList();

// 123216

    return Container(
      margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
      child: Column(
        children: [
          Container(
              child: ReorderableGridView.builder(
            itemCount: gridItems.length,
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              crossAxisSpacing: 10,
              mainAxisSpacing: 10,
              childAspectRatio: 1,
            ),
            itemBuilder: (context, index) {
              final item = itemOrder[index];

              return Container(
                key: ValueKey(item),
                child: gridItems[index],
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

              final prefs = await SharedPreferences.getInstance();
              prefs.setStringList(
                  orderPrefsKey, itemOrder.map((e) => e.toString()).toList());
            },
            shrinkWrap: true,
            physics: NeverScrollableScrollPhysics(),
            padding: const EdgeInsets.only(top: 0),
          )),
        ],
      ),
    );
  }
}

// svgs

class headerWidgetConditions extends StatelessWidget {
  final String headerText;
  final IconData headerIcon;

  const headerWidgetConditions(
      {super.key, required this.headerText, required this.headerIcon});

  @override
  Widget build(BuildContext context) {
    return Positioned(
      left: 0,
      right: 0,
      top: 35,
      child: Center(
        child: Padding(
          padding: EdgeInsets.only(left: 16, right: 16),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                headerIcon,
                weight: 500,
                fill: 1,
                color: Theme.of(context).brightness == Brightness.dark
                    ? Colors.white
                    : Colors.black,
                size: 18,
              ),
              SizedBox(width: 3),
              Flexible(
                child: Text(headerText,
                    style: TextStyle(
                        color: Theme.of(context).brightness == Brightness.dark
                            ? Colors.white
                            : Colors.black,
                        fontSize: 14,
                        fontWeight: FontWeight.w500),
                    textAlign: TextAlign.center,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis),
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

  @override
  Widget build(BuildContext context) {
    final useDeviceCompass =
        context.watch<UnitSettingsNotifier>().useDeviceCompass;

    if (useDeviceCompass && !kIsWeb && (Platform.isAndroid || Platform.isIOS)) {
      return StreamBuilder<CompassEvent>(
        stream: FlutterCompass.events,
        builder: (context, snapshot) {
          final rawHeading = snapshot.data?.heading;
          if (rawHeading == null) return const SizedBox();

          double normalizeAngle(double angle) {
            while (angle > pi) angle -= 2 * pi;
            while (angle < -pi) angle += 2 * pi;
            return angle;
          }

          _lastHeading = lowPassFilter(rawHeading, _lastHeading, 0.2);
          final smoothedHeading = _lastHeading!;

          final targetRotation =
              (widget.currentWindDirc - smoothedHeading) * (pi / 180);
          double delta = normalizeAngle(targetRotation - _previousRotation);
          final newRotation = _previousRotation + delta;

          final animatedRotation = _previousRotation;
          _previousRotation = newRotation;

          return TweenAnimationBuilder<double>(
            tween: Tween<double>(
              begin: animatedRotation,
              end: newRotation,
            ),
            duration: Duration(milliseconds: 300),
            builder: (context, angle, child) {
              return Transform.rotate(
                angle: angle,
                child: child,
              );
            },
            child: AspectRatio(
              aspectRatio: 1,
              child: SvgPicture.string(
                buildWindSvg(
                  Theme.of(context).colorScheme.primaryContainer,
                  widget.backgroundColor,
                ),
                fit: BoxFit.contain,
              ),
            ),
          );
        },
      );
    } else {
      return Transform.rotate(
        angle: widget.currentWindDirc * (pi / 180),
        child: AspectRatio(
          aspectRatio: 1,
          child: SvgPicture.string(
            buildWindSvg(
              Theme.of(context).colorScheme.primaryContainer,
              widget.backgroundColor,
            ),
            fit: BoxFit.contain,
          ),
        ),
      );
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
      bottom: 55,
      left: 0,
      right: 0,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          Container(
            height: height,
            width: width,
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
                        color: Theme.of(context).colorScheme.outline),
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
