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

class LocationsScreen extends StatefulWidget {
  const LocationsScreen({super.key});

  @override
  State<LocationsScreen> createState() => _LocationsScreenState();
}

final GlobalKey<ScaffoldMessengerState> _scaffoldMessengerKey =
    GlobalKey<ScaffoldMessengerState>();

class _LocationsScreenState extends State<LocationsScreen> {
  List<SavedLocation> savedLocations = [];

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
    return 
     ScaffoldMessenger(
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
                setState(() {
                  isEditing = !isEditing;
                });
              },
            ),
            SizedBox(width: 5,)
          ],
          ),
            body: isEditing
        ? buildReorderableListView()
        : buildDismissibleListView(),

                  floatingActionButton: FloatingActionButton.large(
            onPressed: () async {
              final updated = await Navigator.of(context).push<bool>(
                MaterialPageRoute(
                    builder: (_) => const SearchLocationsScreen()),
              );

              if (updated == true) {
                await loadSavedLocations(); // <- this refreshes your saved locations
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Location saved')),
                );
              }
            },
            shape: const CircleBorder(),
            child: const Icon(Icons.add),
          ),
          floatingActionButtonLocation:
              FloatingActionButtonLocation.centerFloat,
        
)


    );

    
}
Widget buildDismissibleListView() {
          return savedLocations.isEmpty
              ? const Center(child: Text("No saved locations."))
              : ListView.builder(
                  itemCount: savedLocations.length + 1,
                  itemBuilder: (context, index) {

                final onlyhomeLocation = PreferencesHelper.getJson('homeLocation');

                bool isOnlyHomeLocation = false;

                if (onlyhomeLocation != null &&
                    savedLocations.length == 1) {
                  final location = savedLocations[0];
                  if (location.city == onlyhomeLocation['city'] &&
                      location.country == onlyhomeLocation['country']) {
                    isOnlyHomeLocation = true;
                  }
                }


              

                    if (index == 0) {
                      final weatherFutureCurrentLocation = getWeatherFromCache(
                          PreferencesHelper.getJson(
                              'homeLocation')?['cacheKey']);


                
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
                                    color:
                                        Theme.of(context).colorScheme.primary,
                                  ),
                                  Text(
                                    "current_location".tr(),
                                    style: TextStyle(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .primary,
                                        fontWeight: FontWeight.w500,
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
                                color: Theme.of(context)
                                    .colorScheme
                                    .surfaceContainerLow,
                                child: InkWell(
                                  onTap: () async {
                                    final cacheKey = PreferencesHelper.getJson(
                                        'homeLocation')?['cacheKey'];
                                    final prefs =
                                        await SharedPreferences.getInstance();

                                    final box = await Hive.openBox(
                                        'weatherMasterCache');
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
                                    await prefs.setString('currentLocation',
                                        jsonEncode(locationData));

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
                                        final isLoading =
                                            snapshot.connectionState ==
                                                ConnectionState.waiting;
                                        final hasData = snapshot.hasData &&
                                            snapshot.data != null;
                                        final weatherData = snapshot.data;

                                        Widget leadingWidgetCurrent;
                                        Widget trailingWidgetCurrent;
                                        Widget subWidgetCurrent;

                                        if (isLoading) {
                                          leadingWidgetCurrent =
                                              const SizedBox.shrink();
                                          subWidgetCurrent =
                                              const SizedBox.shrink();
                                          trailingWidgetCurrent =
                                              const SizedBox(
                                            width: 20,
                                            height: 20,
                                            child: CircularProgressIndicator(
                                                strokeWidth: 2),
                                          );
                                        } else if (hasData) {
                                          final codeCurrent =
                                              weatherData!['weather_code'];
                                          final isDayCurrent =
                                              weatherData['is_day'];
                                          final tempCCurrent =
                                              weatherData['temperature_2m'];
                                          final tempCurrent =
                                              PreferencesHelper.getString(
                                                          "selectedTempUnit") ==
                                                      "Fahrenheit"
                                                  ? UnitConverter
                                                      .celsiusToFahrenheit(
                                                          tempCCurrent)
                                                  : tempCCurrent;

                                          leadingWidgetCurrent =
                                              SvgPicture.asset(
                                            WeatherIconMapper.getIcon(
                                                codeCurrent, isDayCurrent),
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
                                            WeatherConditionMapper
                                                .getConditionLabel(
                                                    codeCurrent, isDayCurrent).tr(),
                                          );
                                        } else {
                                          leadingWidgetCurrent = Text(
                                            "N/A",
                                            style: TextStyle(
                                              fontSize: 32,
                                              color: Theme.of(context)
                                                  .colorScheme
                                                  .onSurface,
                                            ),
                                          );

                                          trailingWidgetCurrent = Text(
                                            "N/A",
                                            style: TextStyle(
                                              fontSize: 32,
                                              color: Theme.of(context)
                                                  .colorScheme
                                                  .onSurface,
                                            ),
                                          );

                                          subWidgetCurrent = Text(
                                            "N/A",
                                          );
                                        }

                                        return ListTile(
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
                                            maxLines: 1,
                                            overflow: TextOverflow.ellipsis,
                                          ),
                                          subtitle: subWidgetCurrent,
                                          trailing: trailingWidgetCurrent,
                                          
                                          
                                        );
                                      }),
                                ),
                              ),
                            ),
                          ),
                          Padding(
                              padding: EdgeInsets.only(
                                  left: 14, bottom: 10, top: 12),
                              child: Row(
                                spacing: 5,
                                children: [
                                  isOnlyHomeLocation ? Text("") :
                                  Icon(
                                    Symbols.star,
                                    color:
                                        Theme.of(context).colorScheme.primary,
                                  ),
                                  isOnlyHomeLocation ? Text("") :
                                  Text(
                                    "saved_locations".tr(),
                                    style: TextStyle(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .primary,
                                        fontWeight: FontWeight.w500,
                                        fontSize: 16),
                                  )
                                ],
                              )),
                        ],
                      );
                    }

                    final actualIndex = index - 1;
                    final loc = savedLocations[actualIndex];

                    final weatherFuture = getWeatherFromCache(
                      "${loc.city}_${loc.country}"
                          .toLowerCase()
                          .replaceAll(' ', '_'),
                    );

                    final homeLocation =
                        PreferencesHelper.getJson('homeLocation');
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




                    return Dismissible(
                      key: ValueKey(
                          '${loc.city}-${loc.country}-${loc.latitude}-${loc.longitude}'),
                      direction: DismissDirection.endToStart,
                      confirmDismiss: (direction) async {
                        final actualIndex = index - 1;

                        if (savedLocations[actualIndex].city ==
                                PreferencesHelper.getJson(
                                    'homeLocation')?['city'] &&
                            savedLocations[actualIndex].country ==
                                PreferencesHelper.getJson(
                                    'homeLocation')?['country']) {
                          _scaffoldMessengerKey.currentState
                              ?.hideCurrentSnackBar();

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
                            color:
                                Theme.of(context).colorScheme.onErrorContainer),
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
                            content: Text(
                                'Deleted ${removed.city}, ${removed.country}'),

                          ),
                        );
                      },
                      child: Container(
                        padding: EdgeInsets.only(left: 14, right: 14),
                        margin: EdgeInsets.only(bottom: 8),
                        decoration: BoxDecoration(
                          color: Colors.transparent
                        ),
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(50),
                          child: Material(
                            color: Theme.of(context)
                                .colorScheme
                                .surfaceContainerLow,
                            child: InkWell(
                              onTap: () async {
                                final cacheKey = "${loc.city}_${loc.country}"
                                    .toLowerCase()
                                    .replaceAll(' ', '_');
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

                                // Save to prefs if needed
                                final locationData = {
                                  'city': loc.city,
                                  'country': loc.country,
                                  'cacheKey': cacheKey,
                                  'latitude': loc.latitude,
                                  'longitude': loc.longitude,
                                };
                                await prefs.setString('currentLocation',
                                    jsonEncode(locationData));

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
                                      child: CircularProgressIndicator(
                                          strokeWidth: 2),
                                    );
                                  } else if (hasData) {
                                    final code = weatherData!['weather_code'];
                                    final isDay = weatherData['is_day'];
                                    final tempC = weatherData['temperature_2m'];

                                    final temp = PreferencesHelper.getString(
                                                "selectedTempUnit") ==
                                            "Fahrenheit"
                                        ? UnitConverter.celsiusToFahrenheit(
                                            tempC)
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
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                      ),
                                    );

                                    subWidget = Text(
                                      WeatherConditionMapper.getConditionLabel(
                                          code, isDay).tr(),
                                    );
                                  } else {
                                    leadingWidget = Text(
                                      "N/A",
                                      style: TextStyle(
                                        fontSize: 32,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                      ),
                                    );

                                    trailingWidget = Text(
                                      "N/A",
                                      style: TextStyle(
                                        fontSize: 32,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                      ),
                                    );

                                    subWidget = Text(
                                      "N/A",
                                    );
                                  }

                                  return ListTile(
                                    minTileHeight: 70,
                                    contentPadding: const EdgeInsets.only(
                                        left: 10, right: 16),
                                    splashColor: Colors.transparent,
                                    leading: CircleAvatar(
                                      backgroundColor:
                                          Theme.of(context).colorScheme.surface,
                                      radius: 25,
                                      child: leadingWidget,
                                    ),
                                    title: Text(
                                      "${loc.city}, ${loc.country}",
                  
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
                    );
                  },
                
              );
            

  

  }
  Widget buildReorderableListView() {
  return ReorderableListView.builder(
    itemCount: savedLocations.length,
    
    onReorder: (oldIndex, newIndex) {
      if (newIndex > oldIndex) newIndex--;
      final item = savedLocations.removeAt(oldIndex);
      savedLocations.insert(newIndex, item);
      setState(() {}); 
    },
proxyDecorator: (child, index, animation) {
  return Material(
    type: MaterialType.transparency, // No elevation or color
    child: child,
  );
},
    itemBuilder: (context, index) {
      final loc = savedLocations[index];
      final homeLocation =
      PreferencesHelper.getJson('homeLocation');
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
        minTileHeight: 65,
        contentPadding: EdgeInsets.only(right: 10, left: 16),
        leading: Icon(Symbols.location_on),
        tileColor: Colors.transparent,
        title: Text(loc.city, style: TextStyle(fontSize: 15),),
        subtitle: Text(loc.country, style: TextStyle(fontSize: 13)),
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

