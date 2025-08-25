import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:weather_master_app/utils/unit_converter.dart';
import 'dart:convert';
import '../models/saved_location.dart';
import 'searchlocations.dart';
import 'package:hive/hive.dart';
import 'package:restart_app/restart_app.dart';
import '../utils/preferences_helper.dart';
import '../utils/icon_map.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/condition_label_map.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:animations/animations.dart';
import '../widgets/dialog.dart';

class LocationsScreen extends StatefulWidget {
  const LocationsScreen({super.key});

  @override
  State<LocationsScreen> createState() => _LocationsScreenState();
}

final GlobalKey<ScaffoldMessengerState> _scaffoldMessengerKey =
    GlobalKey<ScaffoldMessengerState>();

class _LocationsScreenState extends State<LocationsScreen> {
  List<SavedLocation> savedLocations = [];
  bool _isFirstBuild = true;

  @override
  void initState() {
    super.initState();

    loadSavedLocations();
  }

  Future<void> loadSavedLocations() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString('saved_locations');
    if (jsonString != null) {
      final List<dynamic> jsonList = jsonDecode(jsonString);
      final locations =
          jsonList.map((json) => SavedLocation.fromJson(json)).toList();

      setState(() {
        savedLocations = locations;
      });

      if (locations.length == 1 && prefs.getString('homeLocation') == null) {
        final loc = locations.first;

        WidgetsBinding.instance.addPostFrameCallback((_) async {
          final prefs = await SharedPreferences.getInstance();
          final cacheKey =
              "${loc.city}_${loc.country}".toLowerCase().replaceAll(' ', '_');

          await prefs.setString(
              'homeLocation',
              jsonEncode({
                'city': loc.city,
                'country': loc.country,
                'cacheKey': cacheKey,
                'lat': loc.latitude,
                'lon': loc.longitude,
              }));

          final box = await Hive.openBox('weatherMasterCache');
          final rawJson = box.get(cacheKey);
          String? lastUpdated;

          if (rawJson != null) {
            final map = json.decode(rawJson);
            lastUpdated = map['last_updated'];
          }

          final locationData = {
            'city': loc.city,
            'country': loc.country,
            'cacheKey': cacheKey,
            'latitude': loc.latitude,
            'longitude': loc.longitude,
          };
          await prefs.setString('currentLocation', jsonEncode(locationData));

          Restart.restartApp();
        });
      }
    }
  }

  Future<Map<String, dynamic>?> getWeatherFromCache(cacheKey) async {
    final box = await Hive.openBox('weatherMasterCache');
    final cached = box.get(cacheKey);
    if (cached == null) return null;
    final raw = json.decode(cached);
    final weather = raw['data'];
    final current = weather['current'];

    return current;
  }

  Future<String?> getWeatherLastUpdatedFromCache(cacheKey) async {
    final box = await Hive.openBox('weatherMasterCache');
    final rawJson = box.get(cacheKey);

    if (rawJson != null) {
      final map = json.decode(rawJson);
      return map['last_updated'];
    }

    return null;
  }

  bool? isOnlyHomeLocation = false;
  bool isEditing = false;

  @override
  Widget build(BuildContext context) {
    final colorTheme = Theme.of(context).colorScheme;
    return ScaffoldMessenger(
        key: _scaffoldMessengerKey,
        child: Scaffold(
          appBar: AppBar(
            title: Text("locations".tr()),
            toolbarHeight: 65,
            scrolledUnderElevation: 1,
            automaticallyImplyLeading: false,
            actions: [
              IconButton(
                icon: Icon(isEditing ? Icons.check : Icons.edit),
                onPressed: () {
                  _isFirstBuild = false;
                  setState(() {
                    isEditing = !isEditing;
                  });
                },
              ),
              SizedBox(
                width: 5,
              )
            ],
          ),
          body: AnimatedSwitcher(
            duration:
                _isFirstBuild ? Duration.zero : Duration(milliseconds: 300),
            switchInCurve: Curves.easeIn,
            switchOutCurve: Curves.easeOut,
            transitionBuilder: (Widget child, Animation<double> animation) {
              return SlideTransition(
                position: Tween<Offset>(
                  begin: Offset(0.0, 0.1),
                  end: Offset.zero,
                ).animate(animation),
                child: FadeTransition(
                  opacity: animation,
                  child: child,
                ),
              );
            },
            child: isEditing
                ? buildReorderableListView(key: ValueKey('reorderable'))
                : buildDismissibleListView(key: ValueKey('dismissible')),
          ),
          floatingActionButtonLocation:
              FloatingActionButtonLocation.centerFloat,
          floatingActionButton: OpenContainer<bool>(
            transitionType: ContainerTransitionType.fadeThrough,
            openBuilder: (context, _) {
              return SearchLocationsScreen();
            },
            closedElevation: 1,
            openElevation: 0,
            transitionDuration: Duration(milliseconds: 500),
            openShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(50),
            ),
            closedShape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(50),
            ),
            closedColor: colorTheme.primaryContainer,
            openColor: colorTheme.surfaceContainerHigh,
            closedBuilder: (context, openContainer) {
              return FloatingActionButton.large(
                backgroundColor: Colors.transparent,
                onPressed: () {
                  PreferencesHelper.remove("selectedViewLocation");
                  openContainer();
                },
                elevation: 0,
                highlightElevation: 0,
                shape: const CircleBorder(),
                child: Icon(PreferencesHelper.getString("homeLocation") != null
                    ? Icons.search
                    : Icons.add),
              );
            },
            onClosed: (updated) async {
              if (updated == true) {
                await loadSavedLocations();
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('location_saved'.tr())),
                );
              } else if (updated == false &&
                  PreferencesHelper.getString("selectedViewLocation") != null) {
                Navigator.pop(context, {'viewLocaton': true});
              }
            },
          ),
        ));
  }

  Widget buildDismissibleListView({Key? key}) {
    final colorTheme = Theme.of(context).colorScheme;
    return savedLocations.isEmpty
        ? const Center(child: Text("No saved locations."))
        : ListView.builder(
            padding: const EdgeInsets.only(bottom: 150),
            key: key,
            itemCount: savedLocations.length + 1,
            itemBuilder: (context, index) {
              final onlyhomeLocation =
                  PreferencesHelper.getJson('homeLocation');

              bool isOnlyHomeLocation = false;

              if (onlyhomeLocation != null && savedLocations.length == 1) {
                final location = savedLocations[0];
                if (location.city == onlyhomeLocation['city'] &&
                    location.country == onlyhomeLocation['country']) {
                  isOnlyHomeLocation = true;
                }
              }

              if (index == 0) {
                final weatherFutureCurrentLocation = getWeatherFromCache(
                    PreferencesHelper.getJson('homeLocation')?['cacheKey']);

                return Column(
                  key: const ValueKey("list-title"),
                  children: [
                    Padding(
                        padding: EdgeInsets.only(left: 14, bottom: 10, top: 16),
                        child: Row(
                          spacing: 5,
                          children: [
                            Icon(
                              Symbols.home_pin,
                              color: Theme.of(context).colorScheme.primary,
                            ),
                            Text(
                              "default_location".tr(),
                              style: TextStyle(
                                  color: Theme.of(context).colorScheme.primary,
                                  fontWeight: FontWeight.w600,
                                  fontSize: 16),
                            )
                          ],
                        )),
                    Container(
                      padding: EdgeInsets.only(left: 14, right: 14),
                      margin: EdgeInsets.only(bottom: 8),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(50),
                        child: Material(
                          color:
                              Theme.of(context).colorScheme.surfaceContainerLow,
                          child: InkWell(
                            onTap: () async {
                              final cacheKey = PreferencesHelper.getJson(
                                  'homeLocation')?['cacheKey'];
                              final prefs =
                                  await SharedPreferences.getInstance();

                              final box =
                                  await Hive.openBox('weatherMasterCache');
                              final rawJson = box.get(cacheKey);
                              String? lastUpdated;

                              if (rawJson != null) {
                                final map = json.decode(rawJson);
                                lastUpdated = map['last_updated'];
                              }

                              final locationData = {
                                'city': PreferencesHelper.getJson(
                                    'homeLocation')?['city'],
                                'country': PreferencesHelper.getJson(
                                    'homeLocation')?['country'],
                                'cacheKey': PreferencesHelper.getJson(
                                    'homeLocation')?['cacheKey'],
                                'latitude': PreferencesHelper.getJson(
                                    'homeLocation')?['lat'],
                                'longitude': PreferencesHelper.getJson(
                                    'homeLocation')?['lon'],
                              };
                              await prefs.setString(
                                  'currentLocation', jsonEncode(locationData));

                              Navigator.pop(context, {
                                'cacheKey': cacheKey,
                                'city': PreferencesHelper.getJson(
                                    'homeLocation')?['city'],
                                'country': PreferencesHelper.getJson(
                                    'homeLocation')?['country'],
                                'last_updated': lastUpdated,
                                'latitude': PreferencesHelper.getJson(
                                    'homeLocation')?['lat'],
                                'longitude': PreferencesHelper.getJson(
                                    'homeLocation')?['lon'],
                              });
                            },
                            child: FutureBuilder<Map<String, dynamic>?>(
                                future: weatherFutureCurrentLocation,
                                builder: (context, snapshot) {
                                  final isLoading = snapshot.connectionState ==
                                      ConnectionState.waiting;
                                  final hasData =
                                      snapshot.hasData && snapshot.data != null;
                                  final weatherData = snapshot.data;

                                  Widget leadingWidgetCurrent;
                                  Widget trailingWidgetCurrent;
                                  Widget subWidgetCurrent;

                                  if (isLoading) {
                                    leadingWidgetCurrent =
                                        const SizedBox.shrink();
                                    subWidgetCurrent = const SizedBox.shrink();
                                    trailingWidgetCurrent = const SizedBox(
                                      width: 20,
                                      height: 20,
                                      child: CircularProgressIndicator(
                                          strokeWidth: 2),
                                    );
                                  } else if (hasData) {
                                    final codeCurrent =
                                        weatherData!['weather_code'];
                                    final isDayCurrent = weatherData['is_day'];
                                    final tempCCurrent =
                                        weatherData['temperature_2m'];
                                    final tempCurrent =
                                        PreferencesHelper.getString(
                                                    "selectedTempUnit") ==
                                                "Fahrenheit"
                                            ? UnitConverter.celsiusToFahrenheit(
                                                tempCCurrent)
                                            : tempCCurrent;

                                    leadingWidgetCurrent = SvgPicture.asset(
                                      WeatherIconMapper.getIcon(
                                          codeCurrent.toInt(), isDayCurrent),
                                      width: 26,
                                      height: 26,
                                    );

                                    trailingWidgetCurrent = Text(
                                      "${tempCurrent.round()}°",
                                      style: TextStyle(
                                        fontSize: 32,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                      ),
                                    );

                                    subWidgetCurrent = Text(
                                      WeatherConditionMapper.getConditionLabel(
                                              codeCurrent, isDayCurrent)
                                          .tr(),
                                      style: TextStyle(),
                                    );
                                  } else {
                                    leadingWidgetCurrent = Text(
                                      "",
                                      style: TextStyle(
                                        fontSize: 32,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                      ),
                                    );

                                    trailingWidgetCurrent = Text(
                                      "",
                                      style: TextStyle(
                                        fontSize: 32,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                      ),
                                    );

                                    subWidgetCurrent = Text(
                                      "",
                                    );
                                  }

                                  return FadeInListItem(
                                    child: ListTile(
                                      minTileHeight: 70,
                                      contentPadding: const EdgeInsets.only(
                                          left: 10, right: 16),
                                      splashColor: Colors.transparent,
                                      leading: CircleAvatar(
                                        backgroundColor: Theme.of(context)
                                            .colorScheme
                                            .surface,
                                        radius: 25,
                                        child: leadingWidgetCurrent,
                                      ),
                                      title: Text(
                                        "${PreferencesHelper.getJson('homeLocation')?['city']}, ${PreferencesHelper.getJson('homeLocation')?['country']}",
                                        style: TextStyle(),
                                        maxLines: 1,
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                      subtitle: subWidgetCurrent,
                                      trailing: trailingWidgetCurrent,
                                    ),
                                  );
                                }),
                          ),
                        ),
                      ),
                    ),
                    Padding(
                        padding: EdgeInsets.only(
                            left: 14, bottom: 5, top: 0, right: 14),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            isOnlyHomeLocation
                                ? Text("")
                                : Row(
                                    spacing: 5,
                                    children: [
                                      Icon(
                                        Symbols.star,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .primary,
                                      ),
                                      isOnlyHomeLocation
                                          ? Text("")
                                          : Text(
                                              "saved_locations".tr(),
                                              style: TextStyle(
                                                  color: Theme.of(context)
                                                      .colorScheme
                                                      .primary,
                                                  fontWeight: FontWeight.w600,
                                                  fontSize: 16),
                                            ),
                                    ],
                                  ),
                            IconButton(
                                onPressed: () {
                                  showMatDialog(
                                      context: context,
                                      title: "Info",
                                      content: SizedBox(
                                          height: 70 + 30,
                                          child: Column(
                                            children: [
                                              Text(
                                                "• Swipe from right to left to delete a location",
                                                style: TextStyle(fontSize: 15),
                                              ),
                                              Divider(
                                                height: 16,
                                              ),
                                              Text(
                                                  "• Hold a location to set it as default",
                                                  style:
                                                      TextStyle(fontSize: 15)),
                                            ],
                                          )));
                                },
                                icon: Icon(
                                  Symbols.info,
                                  weight: 600,
                                ))
                          ],
                        )),
                    isOnlyHomeLocation
                        ? Padding(
                            padding: EdgeInsets.only(top: 40),
                            child: SvgPicture.string(
                              '''
                        <?xml version="1.0" encoding="utf-8"?>
                        <svg height="147.0dip" width="186.0dip" viewBox="0 0 186.0 147.0"
                          xmlns:android="http://schemas.android.com/apk/res/android">
                            <path fill="#71787e" d="M123.83,1.07C125.36,-0.36 127.78,-0.36 129.31,1.07C130.18,1.88 131.4,2.26 132.6,2.1C134.7,1.81 136.67,3.19 137.03,5.2C137.24,6.36 137.99,7.35 139.07,7.9C140.95,8.86 141.69,11.09 140.76,12.93C140.23,13.98 140.23,15.21 140.76,16.26C141.69,18.1 140.95,20.33 139.07,21.29C137.99,21.84 137.24,22.83 137.03,23.99C136.67,26 134.7,27.38 132.6,27.09C131.4,26.93 130.18,27.31 129.31,28.12C127.78,29.55 125.36,29.55 123.83,28.12C122.95,27.31 121.74,26.93 120.54,27.09C118.43,27.38 116.47,26 116.1,23.99C115.89,22.83 115.14,21.84 114.07,21.29C112.19,20.33 111.44,18.1 112.37,16.26C112.91,15.21 112.91,13.98 112.37,12.93C111.44,11.09 112.19,8.86 114.07,7.9C115.14,7.35 115.89,6.36 116.1,5.2C116.47,3.19 118.43,1.81 120.54,2.1C121.74,2.26 122.95,1.88 123.83,1.07Z" />
                            <path fill="#00000000" d="M64.96,91.46C57.17,91.46 50.59,96.91 47.91,104.54C46.08,103.17 43.89,102.49 41.57,102.49C36.21,102.49 31.71,106.44 30.24,111.89H95.43" stroke="#80868b" stroke-width="2.0" android:strokeLineCap="square" />
                            <path fill="#00000000" d="M77.56,62.95H24.4C25.94,57.84 30.67,54.13 36.29,54.13C38.72,54.13 41.02,54.77 42.93,56.05C45.74,48.9 52.64,43.78 60.82,43.78C69.33,43.78 75.74,48.31 78.2,56.11" stroke="#80868b" stroke-width="2.0" />
                            <path fill="#00000000" d="M149.15,39.86C151.75,39.86 154.01,41.24 155.24,43.28C155.76,43.1 156.32,43.02 156.88,43.02C159.08,43.02 160.96,44.42 161.58,46.41L161.98,47.7H160.62H143.11H142.11V46.7C142.11,42.88 145.33,39.86 149.15,39.86Z" stroke="#80868b" stroke-width="2.0" />
                            <path fill="#00000000" d="M124.58,140.87C123.99,140.87 123.41,140.97 122.87,141.16C121.57,137.71 118.37,135.22 114.59,135.22C111,135.22 107.98,137.44 106.54,140.57C105.69,140.16 104.75,139.92 103.75,139.92C100.8,139.92 98.34,141.92 97.41,144.65L96.95,145.97H98.35H128.51H129.82L129.48,144.71C128.88,142.5 126.93,140.87 124.58,140.87Z" stroke="#80868b" stroke-width="2.0" />
                            <path fill="#c1c7ce" d="M71.32,19.59C70.78,19.59 70.25,19.67 69.75,19.84C68.5,16.75 65.48,14.57 61.97,14.57C58.64,14.57 55.8,16.52 54.42,19.31C53.63,18.96 52.76,18.75 51.83,18.75C49.07,18.75 46.74,20.53 45.84,23.01L45.36,24.35H46.78H75H76.32L75.96,23.08C75.39,21.04 73.51,19.59 71.32,19.59Z" stroke="#c1c7ce" stroke-width="2.0" />
                            <path fill="#c1c7ce" d="M37.68,84.37C36.83,84.37 36,84.54 35.24,84.86C33.64,80.21 29.44,76.84 24.48,76.84C19.75,76.84 15.78,79.87 13.98,84.09C12.83,83.48 11.53,83.11 10.14,83.11C6.38,83.11 3.25,85.68 2.05,89.19L1.6,90.51H3H42.89H44.19L43.86,89.26C43.11,86.44 40.64,84.37 37.68,84.37Z" stroke="#c1c7ce" stroke-width="2.0" />
                            <path fill="#00000000" d="M101.88,68.55C106.14,68.21 109,69.56 111.28,71.17C113.84,63.29 121.06,57.53 129.65,57.53C138.64,57.53 146.25,63.9 148.33,72.49C149.91,71.37 151.88,70.77 153.96,70.77C158.5,70.77 162.36,73.8 163.54,78.04H101.27" stroke="#80868b" stroke-width="2.0" />
                            <path fill="#00000000" d="M105.68,95.46C114.04,95.46 125.68,100.53 125.68,110.93C127.35,110.06 129.64,109.19 131.53,109.19C139.9,109.19 143.24,114.39 144.08,118.6L90.57,118.6" stroke="#80868b" stroke-width="2.0" android:strokeLineCap="square" />
                            <path fill="#c1c7ce" d="M155.59,68.08C161.77,68.08 167.03,71.75 169.53,76.99C170.91,76.3 172.46,75.96 174.05,75.96C178.75,75.96 182.68,79.09 183.96,83.39L184.34,84.68H183H141.16H140.16V83.68C140.16,75.05 147.14,68.08 155.59,68.08Z" stroke="#c1c7ce" stroke-width="2.0" />
                            <path fill="#71787e" d="M90.09,128.43C89.11,128.46 88.15,128.14 87.38,127.53C86.61,126.9 86.03,126.08 85.68,125.15C84.3,121.03 82.5,117.06 80.32,113.3C77.61,108.78 74.61,104.44 71.35,100.31C67.83,95.84 64.72,91.08 62.03,86.08C59.53,80.99 58.31,75.37 58.47,69.7C58.46,65.54 59.26,61.41 60.84,57.56C62.42,53.71 64.75,50.2 67.69,47.25C70.62,44.3 74.12,41.96 77.96,40.37C81.8,38.77 85.93,37.95 90.09,37.95C94.25,37.95 98.37,38.77 102.22,40.37C106.06,41.96 109.55,44.3 112.49,47.25C115.43,50.2 117.75,53.71 119.33,57.56C120.91,61.41 121.72,65.54 121.7,69.7C121.7,76.55 120.4,82.25 117.81,86.81C115.12,91.5 112.12,96.01 108.83,100.31C105.51,104.63 102.44,109.13 99.63,113.8C97.55,117.41 95.83,121.21 94.49,125.16C94.18,126.13 93.56,126.97 92.74,127.58C91.97,128.14 91.04,128.44 90.09,128.43ZM90.09,80.99C92.67,80.97 95.16,80.05 97.14,78.39C99.13,76.74 100.47,74.45 100.96,71.91C101.44,69.37 101.04,66.75 99.81,64.48C98.57,62.21 96.6,60.43 94.21,59.46C91.81,58.48 89.16,58.36 86.69,59.12C84.22,59.88 82.09,61.47 80.67,63.62C79.24,65.77 78.6,68.35 78.85,70.92C79.11,73.49 80.25,75.89 82.07,77.72C83.11,78.78 84.36,79.62 85.74,80.19C87.12,80.75 88.6,81.03 90.09,80.99Z" />
                            <path fill="#c1c7ce" d="M146.4,117.11C145.5,117.11 144.62,117.29 143.81,117.65C142.17,112.63 137.78,108.95 132.55,108.95C127.58,108.95 123.42,112.25 121.56,116.82C120.35,116.14 118.98,115.74 117.52,115.74C113.58,115.74 110.31,118.52 109.08,122.28L108.65,123.6H110.03H151.87H153.16L152.83,122.35C152.06,119.34 149.51,117.11 146.4,117.11Z" stroke="#c1c7ce" stroke-width="2.0" />
                            <path fill="#f8f9fa" d="M69.28,129.78C68.05,129.78 66.87,130.16 65.93,130.84C64.69,125.55 60.15,121.62 54.79,121.62C49.66,121.62 45.36,125.17 43.83,130.03C42.47,129.04 40.82,128.41 39.05,128.41C35.46,128.41 32.4,130.84 31.22,134.27H75C74.29,131.65 72,129.78 69.28,129.78Z" />
                        </svg>

                        ''',
                              width: 250,
                            ))
                        : SizedBox.shrink(),
                  ],
                );
              }

              final actualIndex = index - 1;
              final loc = savedLocations[actualIndex];

              final weatherFuture = getWeatherFromCache(
                "${loc.city}_${loc.country}".toLowerCase().replaceAll(' ', '_'),
              );

              final homeLocation = PreferencesHelper.getJson('homeLocation');
              final isHomeLocation = homeLocation != null &&
                  homeLocation['city'] == loc.city &&
                  homeLocation['country'] == loc.country;

              if (isHomeLocation) {
                return KeyedSubtree(
                  key: ValueKey(
                      '${loc.city}-${loc.country}-${loc.latitude}-${loc.longitude}'),
                  child: const SizedBox.shrink(),
                );
              }

              final isLastItem = index == savedLocations.length;

              Future<Map<String, dynamic>> _getCurrentHomeInfo() async {
                final prefs = await SharedPreferences.getInstance();
                final homeLocationJson = prefs.getString('homeLocation');
                if (homeLocationJson != null) {
                  final data = jsonDecode(homeLocationJson);
                  return {
                    'cacheKey': data['cacheKey'] ?? '',
                    'isGPS': data['isGPS'] ?? false,
                    'city': data['city'] ?? '',
                    'country': data['country'] ?? '',
                  };
                }
                return {'cacheKey': '', 'isGPS': false};
              }

              Future<void> setHomeLocation(
                  BuildContext context, SavedLocation loc) async {
                final prefs = await SharedPreferences.getInstance();
                final cacheKey = "${loc.city}_${loc.country}"
                    .toLowerCase()
                    .replaceAll(' ', '_');

                await prefs.setString(
                    'homeLocation',
                    jsonEncode({
                      'city': loc.city,
                      'country': loc.country,
                      'cacheKey': cacheKey,
                      'lat': loc.latitude,
                      'lon': loc.longitude,
                      'isGPS': false,
                    }));
              }

              return FadeInListItem(
                child: Dismissible(
                  key: ValueKey(
                      '${loc.city}-${loc.country}-${loc.latitude}-${loc.longitude}-${index}'),
                  direction: DismissDirection.endToStart,
                  confirmDismiss: (direction) async {
                    final actualIndex = index - 1;

                    if (savedLocations[actualIndex].city ==
                            PreferencesHelper.getJson(
                                'homeLocation')?['city'] &&
                        savedLocations[actualIndex].country ==
                            PreferencesHelper.getJson(
                                'homeLocation')?['country']) {
                      _scaffoldMessengerKey.currentState?.hideCurrentSnackBar();

                      _scaffoldMessengerKey.currentState?.showSnackBar(
                        SnackBar(
                          content:
                              Text("You can't delete the default location"),
                          duration: Duration(seconds: 2),
                        ),
                      );
                      return false;
                    }
                    return true;
                  },
                  background: Container(
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    margin: EdgeInsets.only(left: 14, right: 14),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(50),
                      color: Theme.of(context).colorScheme.errorContainer,
                    ),
                    child: Icon(Icons.delete,
                        color: Theme.of(context).colorScheme.onErrorContainer),
                  ),
                  onDismissed: (direction) async {
                    final actualIndex = index - 1;
                    final removed = savedLocations.removeAt(actualIndex);

                    setState(() {});

                    final prefs = await SharedPreferences.getInstance();
                    await prefs.setString(
                      'saved_locations',
                      jsonEncode(
                          savedLocations.map((e) => e.toJson()).toList()),
                    );

                    final cacheKey = "${removed.city}_${removed.country}"
                        .toLowerCase()
                        .replaceAll(' ', '_');
                    final box = await Hive.openBox('weatherMasterCache');
                    await box.delete(cacheKey);
                    _scaffoldMessengerKey.currentState?.showSnackBar(
                      SnackBar(
                        content:
                            Text('Deleted ${removed.city}, ${removed.country}'),
                      ),
                    );
                  },
                  child: Container(
                    padding: EdgeInsets.only(left: 14, right: 14),
                    margin: EdgeInsets.only(bottom: 8),
                    decoration: BoxDecoration(color: Colors.transparent),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(50),
                      child: Material(
                        color:
                            Theme.of(context).colorScheme.surfaceContainerLow,
                        child: InkWell(
                          onLongPress: () async {
                            ScaffoldMessenger.of(context).hideCurrentSnackBar();
                            showMatDialog(
                              context: context,
                              title: "${loc.city}, ${loc.country}",
                              confirmText: "Set as default",
                              cancelText: "Cancel",
                              onConfirm: () {
                                if (!context.mounted) return;
                                setHomeLocation(context, loc);

                                setState(() {});
                              },
                            );
                          },
                          onTap: () async {
                            final cacheKey = "${loc.city}_${loc.country}"
                                .toLowerCase()
                                .replaceAll(' ', '_');
                            final prefs = await SharedPreferences.getInstance();

                            final box =
                                await Hive.openBox('weatherMasterCache');
                            final rawJson = box.get(cacheKey);
                            String? lastUpdated;

                            if (rawJson != null) {
                              final map = json.decode(rawJson);
                              lastUpdated = map['last_updated'];
                            }

                            // Save to prefs if needed
                            final locationData = {
                              'city': loc.city,
                              'country': loc.country,
                              'cacheKey': cacheKey,
                              'latitude': loc.latitude,
                              'longitude': loc.longitude,
                            };
                            await prefs.setString(
                                'currentLocation', jsonEncode(locationData));

                            // Return data to previous screen
                            Navigator.pop(context, {
                              'cacheKey': cacheKey,
                              'city': loc.city,
                              'country': loc.country,
                              'last_updated': lastUpdated,
                              'lat': loc.latitude,
                              'lon': loc.longitude,
                            });
                          },
                          child: FutureBuilder<Map<String, dynamic>?>(
                            future: weatherFuture,
                            builder: (context, snapshot) {
                              final isLoading = snapshot.connectionState ==
                                  ConnectionState.waiting;
                              final hasData =
                                  snapshot.hasData && snapshot.data != null;
                              final weatherData = snapshot.data;

                              Widget leadingWidget;
                              Widget trailingWidget;
                              Widget subWidget;
                              if (isLoading) {
                                leadingWidget = const SizedBox.shrink();
                                subWidget = const SizedBox.shrink();
                                trailingWidget = const SizedBox(
                                  width: 20,
                                  height: 20,
                                  child:
                                      CircularProgressIndicator(strokeWidth: 2),
                                );
                              } else if (hasData) {
                                final code = weatherData!['weather_code'];
                                final isDay = weatherData['is_day'];
                                final tempC = weatherData['temperature_2m'];

                                final temp = PreferencesHelper.getString(
                                            "selectedTempUnit") ==
                                        "Fahrenheit"
                                    ? UnitConverter.celsiusToFahrenheit(tempC)
                                    : tempC;

                                leadingWidget = SvgPicture.asset(
                                  WeatherIconMapper.getIcon(code, isDay),
                                  width: 26,
                                  height: 26,
                                );

                                trailingWidget = Text(
                                  "${temp.round()}°",
                                  style: TextStyle(
                                    fontSize: 32,
                                    color:
                                        Theme.of(context).colorScheme.onSurface,
                                  ),
                                );

                                subWidget = Text(
                                  WeatherConditionMapper.getConditionLabel(
                                          code, isDay)
                                      .tr(),
                                  style: TextStyle(),
                                );
                              } else {
                                leadingWidget = Text(
                                  "",
                                  style: TextStyle(
                                    fontSize: 32,
                                    color:
                                        Theme.of(context).colorScheme.onSurface,
                                  ),
                                );

                                trailingWidget = Text(
                                  "",
                                  style: TextStyle(
                                    fontSize: 32,
                                    color:
                                        Theme.of(context).colorScheme.onSurface,
                                  ),
                                );

                                subWidget = Text(
                                  "1",
                                );
                              }

                              return ListTile(
                                minTileHeight: 70,
                                contentPadding:
                                    const EdgeInsets.only(left: 10, right: 16),
                                splashColor: Colors.transparent,
                                leading: CircleAvatar(
                                  backgroundColor:
                                      Theme.of(context).colorScheme.surface,
                                  radius: 25,
                                  child: leadingWidget,
                                ),
                                title: Text(
                                  "${loc.city}, ${loc.country}",
                                  style: TextStyle(),
                                  maxLines: 1,
                                  overflow: TextOverflow.ellipsis,
                                ),
                                subtitle: subWidget,
                                trailing: trailingWidget,
                              );
                            },
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              );
            },
          );
  }

  Widget buildReorderableListView({Key? key}) {
    return ReorderableListView.builder(
      key: key,
      itemCount: savedLocations.length,
      onReorder: (oldIndex, newIndex) async {
        if (newIndex > oldIndex) newIndex--;
        final item = savedLocations.removeAt(oldIndex);
        savedLocations.insert(newIndex, item);
        setState(() {});

        // SAVE NEW ORDER
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString(
          'saved_locations',
          jsonEncode(savedLocations.map((e) => e.toJson()).toList()),
        );
      },
      proxyDecorator: (child, index, animation) {
        return Material(
          type: MaterialType.transparency, // No elevation or color
          child: child,
        );
      },
      itemBuilder: (context, index) {
        final loc = savedLocations[index];
        final homeLocation = PreferencesHelper.getJson('homeLocation');
        final isHomeLocation = homeLocation != null &&
            homeLocation['city'] == loc.city &&
            homeLocation['country'] == loc.country;
        if (isHomeLocation) {
          return KeyedSubtree(
            key: ValueKey(
                '${loc.city}-${loc.country}-${loc.latitude}-${loc.longitude}'),
            child: const SizedBox.shrink(),
          );
        }
        return Container(
          key: ValueKey('${loc.city}-${loc.country}'),
          margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.surfaceContainerLow,
            borderRadius: BorderRadius.circular(50),
          ),
          child: ListTile(
            minTileHeight: 70,
            contentPadding: EdgeInsets.only(right: 16, left: 16),
            leading: Icon(
              Symbols.location_on,
              size: 26,
            ),
            tileColor: Colors.transparent,
            title: Text(
              loc.city,
              style: TextStyle(
                fontSize: 15,
              ),
            ),
            subtitle: Text(loc.country,
                style: TextStyle(
                  fontSize: 13,
                )),
            trailing: CircleAvatar(
              backgroundColor: Theme.of(context).colorScheme.surface,
              child: Icon(Symbols.drag_handle),
            ),
          ),
        );
      },
    );
  }
}

// animation for list

class FadeInListItem extends StatefulWidget {
  final Widget child;

  const FadeInListItem({required this.child, super.key});

  @override
  State<FadeInListItem> createState() => _FadeInListItemState();
}

class _FadeInListItemState extends State<FadeInListItem> {
  double opacity = 0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      setState(() => opacity = 1);
    });
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedOpacity(
      opacity: opacity,
      duration: const Duration(milliseconds: 700),
      curve: Curves.easeInOut,
      child: widget.child,
    );
  }
}
