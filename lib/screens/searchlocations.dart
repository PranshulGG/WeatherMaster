import 'package:flutter/material.dart';
import 'package:easy_localization/easy_localization.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/saved_location.dart';
import '../services/fetch_data.dart';
import 'package:expressive_loading_indicator/expressive_loading_indicator.dart';

enum GeoProvider { nominatim, geonames, openMeteo }

class SearchLocationsScreen extends StatefulWidget {
  const SearchLocationsScreen({super.key});

  @override
  State<SearchLocationsScreen> createState() => _SearchLocationsScreenState();
}

class _SearchLocationsScreenState extends State<SearchLocationsScreen> {
  List<SavedLocation> savedLocations = [];

  Future<void> saveLocation(SavedLocation newLocation) async {
    final prefs = await SharedPreferences.getInstance();
    final existing = prefs.getString('saved_locations');
    List<SavedLocation> current = [];

    if (existing != null) {
      final decoded = jsonDecode(existing) as List;
      current = decoded.map((e) => SavedLocation.fromJson(e)).toList();
    }

    // Optionally avoid duplicates
    bool alreadyExists = current.any((loc) =>
        loc.city == newLocation.city && loc.country == newLocation.country);

    if (!alreadyExists) {
      current.add(newLocation);
      await prefs.setString('saved_locations',
          jsonEncode(current.map((e) => e.toJson()).toList()));
    }
  }

  Future<void> loadSavedLocations() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString('saved_locations');
    if (jsonString != null) {
      final List<dynamic> jsonList = jsonDecode(jsonString);
      setState(() {
        savedLocations =
            jsonList.map((json) => SavedLocation.fromJson(json)).toList();
      });
    } else {
      savedLocations = [];
    }
  }

  GeoProvider selectedProvider = GeoProvider.nominatim;
  bool isLoading = false;
  List<Map<String, String>> results = [];
  String query = '';

  final providerLabels = {
    GeoProvider.nominatim: "OpenStreetMap",
    GeoProvider.geonames: "GeoNames",
    GeoProvider.openMeteo: "Open-Meteo"
  };

  @override
  void initState() {
    super.initState();
    loadSavedProvider();
    loadSavedLocations();
  }

  Future<void> loadSavedProvider() async {
    final prefs = await SharedPreferences.getInstance();
    final index = prefs.getInt('geo_provider_index') ?? 1;
    setState(() {
      selectedProvider = GeoProvider.values[index];
    });
  }

  Future<void> saveProvider(GeoProvider provider) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt('geo_provider_index', provider.index);
  }

  Future<void> searchLocation(String input) async {
    input = input.trim();
    if (input.isEmpty) return;

    setState(() {
      isLoading = true;
      results.clear();
    });

    final encodedQuery = Uri.encodeComponent(input);
    final username = 'pranshulgg'; // GeoNames username
    String url = switch (selectedProvider) {
      GeoProvider.nominatim =>
        'https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&addressdetails=1&limit=10',
      GeoProvider.geonames =>
        'https://secure.geonames.org/searchJSON?name_startsWith=$encodedQuery&maxRows=10&username=$username',
      GeoProvider.openMeteo =>
        'https://geocoding-api.open-meteo.com/v1/search?name=$encodedQuery&count=8',
    };

    try {
      final res = await http.get(Uri.parse(url));
      if (res.statusCode != 200) {
        throw Exception("Failed to load data: ${res.statusCode}");
      }

      final contentType = res.headers['content-type'];
      if (contentType == null || !contentType.contains('application/json')) {
        throw Exception("Invalid response format. Expected JSON.");
      }

      final data = json.decode(res.body);

      final locations = switch (selectedProvider) {
        GeoProvider.nominatim => (data as List).map<Map<String, String>>((e) {
            final address = e['address'] ?? {};
            return {
              'city': e['name'] ?? e['display_name'] ?? '',
              'country':
                  ((address['state'] != null ? "${address['state']}, " : "") +
                      (address['country'] ?? '')),
              'country_code': address['country_code'] ?? '',
              'lat': e['lat'] ?? '',
              'lon': e['lon'] ?? '',
            };
          }).toList(),
        GeoProvider.geonames =>
          (data['geonames'] as List).map<Map<String, String>>((e) {
            return {
              'city': e['name'],
              'country':
                  ((e['adminName1'] != null ? "${e['adminName1']}, " : "") +
                      (e['countryName'] ?? '')),
              'country_code': e['countryCode'] ?? '',
              'lat': e['lat']?.toString() ?? '',
              'lon': e['lng']?.toString() ?? '',
            };
          }).toList(),
        GeoProvider.openMeteo =>
          (data['results'] as List).map<Map<String, String>>((e) {
            return {
              'city': e['name'],
              'country': ((e['admin1'] != null ? "${e['admin1']}, " : "") +
                  (e['country'] ?? '')),
              'country_code': e['country_code'] ?? '',
              'lat': e['latitude']?.toString() ?? '',
              'lon': e['longitude']?.toString() ?? '',
            };
          }).toList(),
      };

      // Remove duplicates
      final unique = {
        for (var loc in locations) "${loc['city']},${loc['country']}": loc
      }.values.toList();

      setState(() => results = unique);
    } catch (e) {
      setState(() => results = []);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("data_fetch_error".tr())),
      );
    }

    setState(() => isLoading = false);
  }

  void openProviderSheet() async {
    GeoProvider tempProvider = selectedProvider;
    final colorTheme = Theme.of(context).colorScheme;

    final confirmed = await showModalBottomSheet<bool>(
      context: context,
      showDragHandle: true,
      isScrollControlled: true,
      backgroundColor: colorTheme.surfaceContainerLow,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      builder: (_) {
        return Padding(
          padding: EdgeInsets.only(
              bottom: MediaQuery.of(context).padding.bottom + 10),
          child: StatefulBuilder(
            builder: (context, setState) {
              return Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 18),
                    child: Text(
                      "search_provider".tr(),
                      style: TextStyle(fontSize: 22),
                    ),
                  ),

                  SizedBox(height: 15),
                  ...GeoProvider.values.asMap().entries.map((entry) {
                    final index = entry.key;
                    final provider = entry.value;
                    final isFirst = index == 0;
                    final isLast = index == GeoProvider.values.length - 1;
                    final isSelected = tempProvider == provider;
                    return Padding(
                      padding:
                          const EdgeInsets.only(left: 18, right: 18, bottom: 2),
                      child: ListTile(
                        shape: RoundedRectangleBorder(
                            borderRadius: isSelected
                                ? BorderRadiusGeometry.circular(50)
                                : BorderRadius.only(
                                    topRight:
                                        Radius.circular(isFirst ? 18 : 2.6),
                                    topLeft:
                                        Radius.circular(isFirst ? 18 : 2.6),
                                    bottomLeft:
                                        Radius.circular(isLast ? 18 : 2.6),
                                    bottomRight:
                                        Radius.circular(isLast ? 18 : 2.6))),
                        tileColor: colorTheme.surfaceContainerLowest,
                        dense: true,
                        selected: isSelected,
                        selectedTileColor: colorTheme.primaryContainer,
                        title: Text(
                          providerLabels[provider]!,
                          style: TextStyle(
                            fontSize: 15,
                            color: isSelected
                                ? colorTheme.onPrimaryContainer
                                : null,
                          ),
                        ),
                        onTap: () => setState(() => tempProvider = provider),
                      ),
                    );
                  }).toList(),
                  SizedBox(height: 13),
                  // Divider(),
                  // SizedBox(height: 4),
                  Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 18),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          OutlinedButton(
                              onPressed: () => Navigator.pop(context, false),
                              child: Text(
                                'cancel'.tr(),
                                style: TextStyle(
                                    fontSize: 16, fontWeight: FontWeight.w600),
                              )),
                          FilledButton(
                              onPressed: () => Navigator.pop(context, true),
                              child: Text(
                                'save'.tr(),
                                style: TextStyle(
                                    fontSize: 16, fontWeight: FontWeight.w600),
                              )),
                        ],
                      ))
                ],
              );
            },
          ),
        );
      },
    );

    if (confirmed == true && tempProvider != selectedProvider) {
      setState(() => selectedProvider = tempProvider);
      saveProvider(tempProvider);
      if (query.isNotEmpty) searchLocation(query); // refresh search
    }
  }

  Future<int> getLocationCount() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString('saved_locations');
    if (jsonString != null) {
      final List<dynamic> jsonList = jsonDecode(jsonString);
      final locations =
          jsonList.map((json) => SavedLocation.fromJson(json)).toList();
      return locations.length;
    }
    return 0;
  }

  @override
  Widget build(BuildContext context) {
    final colorTheme = Theme.of(context).colorScheme;

    return Scaffold(
      backgroundColor: colorTheme.surfaceContainerHigh,
      appBar: AppBar(
          elevation: 0,
          title: TextField(
            style: TextStyle(fontSize: 16, color: colorTheme.onSurface),
            onChanged: (value) => query = value,
            onSubmitted: (value) => searchLocation(value.trim()),
            decoration: InputDecoration(
              border: InputBorder.none,
              focusedBorder: InputBorder.none,
              enabledBorder: InputBorder.none,
              disabledBorder: InputBorder.none,
              hintText: "${"search".tr()}...",
              hintStyle: TextStyle(),
            ),
          ),
          titleSpacing: 0,
          toolbarHeight: 65 + 10,
          scrolledUnderElevation: 0,
          backgroundColor: colorTheme.surfaceContainerHigh,
          shape: Border(
              bottom: BorderSide(
                  color: Theme.of(context).colorScheme.outline.withOpacity(0.8),
                  width: 2))),
      body: isLoading
          ? Center(
              child: ExpressiveLoadingIndicator(
              activeSize: 48,
              color: colorTheme.primary,
            ))
          : results.isEmpty
              ? const Center(child: Text(""))
              : ListView.builder(
                  itemCount: results.length,
                  itemBuilder: (context, index) {
                    final city = results[index]['city'] ?? '';
                    final country = results[index]['country'] ?? '';
                    final code = results[index]['country_code'] ?? '';

                    final isSaved = savedLocations.any((loc) =>
                        loc.latitude.toDouble().toString() ==
                            results[index]['lat'] &&
                        loc.longitude.toDouble().toString() ==
                            results[index]['lon']);

                    return ListTile(
                        enabled: !isSaved,
                        contentPadding: EdgeInsets.only(left: 16, right: 16),
                        leading: code.isNotEmpty
                            ? CircleAvatar(
                                radius: 18,
                                backgroundColor: Colors.transparent,
                                backgroundImage: NetworkImage(
                                    "https://flagcdn.com/w80/${code.toLowerCase()}.png"),
                              )
                            : const Icon(Icons.location_on),
                        title: Text(
                          city,
                          style: TextStyle(),
                        ),
                        subtitle: country.isNotEmpty
                            ? Text(
                                country,
                                style: TextStyle(),
                              )
                            : null,
                        trailing: FutureBuilder<int>(
                            future: getLocationCount(),
                            builder: (context, snapshot) {
                              if (snapshot.connectionState ==
                                  ConnectionState.waiting) {
                                return const SizedBox(
                                  height: 24,
                                  width: 24,
                                  child:
                                      CircularProgressIndicator(strokeWidth: 2),
                                );
                              }

                              if (snapshot.hasData && snapshot.data! >= 1) {
                                return IconButton.filled(
                                    onPressed: () async {
                                      showDialog(
                                        context: context,
                                        barrierDismissible: false,
                                        barrierColor: Theme.of(context)
                                            .colorScheme
                                            .surface,
                                        builder: (context) => Center(
                                            child: ExpressiveLoadingIndicator(
                                          activeSize: 48,
                                          color: colorTheme.primary,
                                        )),
                                      );
                                      final location = results[index];
                                      final lat = double.tryParse(
                                              location['lat'] ?? '') ??
                                          0.0;
                                      final lon = double.tryParse(
                                              location['lon'] ?? '') ??
                                          0.0;

                                      final saved = SavedLocation(
                                        latitude: lat,
                                        longitude: lon,
                                        city: location['city'] ?? '',
                                        country: location['country'] ?? '',
                                      );
                                      final cacheKey =
                                          "${saved.city}_${saved.country}"
                                              .toLowerCase()
                                              .replaceAll(' ', '_');
                                      final weatherService = WeatherService();
                                      try {
                                        await weatherService.fetchWeather(
                                            lat, lon,
                                            locationName: cacheKey,
                                            context: context);
                                      } catch (e) {
                                        Navigator.pop(context);

                                        if (context != null) {
                                          ScaffoldMessenger.of(context)
                                              .showSnackBar(
                                            SnackBar(
                                              content:
                                                  Text('data_fetch_error'.tr()),
                                              duration: Duration(seconds: 5),
                                            ),
                                          );
                                        }
                                        return;
                                      }

                                      saveLocation(saved);

                                      final prefs =
                                          await SharedPreferences.getInstance();
                                      final jsonString =
                                          prefs.getString('saved_locations');
                                      if (jsonString != null) {
                                        final List<dynamic> jsonList =
                                            jsonDecode(jsonString);
                                        final locations = jsonList
                                            .map((json) =>
                                                SavedLocation.fromJson(json))
                                            .toList();
                                        if (locations.length == 1) {
                                          final loc = locations.first;

                                          WidgetsBinding.instance
                                              .addPostFrameCallback((_) async {
                                            final prefs =
                                                await SharedPreferences
                                                    .getInstance();
                                            final cacheKey =
                                                "${loc.city}_${loc.country}"
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
                                                }));
                                          });
                                        }

                                        Navigator.pop(context);

                                        Navigator.pop(context, true);
                                      }
                                    },
                                    icon: Icon(
                                      Icons.add,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .onTertiaryContainer,
                                    ),
                                    constraints: BoxConstraints(
                                      minWidth: 53,
                                      minHeight: 40,
                                    ),
                                    style: ButtonStyle(
                                        backgroundColor:
                                            WidgetStateProperty.all(
                                                Theme.of(context)
                                                    .colorScheme
                                                    .tertiaryContainer)));
                              }
                              return const SizedBox.shrink();
                            }),
                        onTap: () async {
                          final location = results[index];
                          final lat =
                              double.tryParse(location['lat'] ?? '') ?? 0.0;
                          final lon =
                              double.tryParse(location['lon'] ?? '') ?? 0.0;

                          final saved = SavedLocation(
                            latitude: lat,
                            longitude: lon,
                            city: location['city'] ?? '',
                            country: location['country'] ?? '',
                          );

                          final count = await getLocationCount();

                          if (count == 0) {
                            showDialog(
                              context: context,
                              barrierDismissible: false,
                              barrierColor:
                                  Theme.of(context).colorScheme.surface,
                              builder: (context) => Center(
                                  child: ExpressiveLoadingIndicator(
                                activeSize: 48,
                                color: colorTheme.primary,
                              )),
                            );

                            final cacheKey = "${saved.city}_${saved.country}"
                                .toLowerCase()
                                .replaceAll(' ', '_');
                            final weatherService = WeatherService();
                            try {
                              await weatherService.fetchWeather(lat, lon,
                                  locationName: cacheKey, context: context);
                            } catch (e) {
                              if (context != null) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(
                                    content: Text('data_fetch_error'.tr()),
                                    duration: Duration(seconds: 5),
                                  ),
                                );
                              }
                              return;
                            }

                            saveLocation(saved);

                            // final weatherService = WeatherService();
                            // await weatherService.fetchWeather(lat, lon,
                            //     locationName: cacheKey, context: context);

                            final prefs = await SharedPreferences.getInstance();
                            final jsonString =
                                prefs.getString('saved_locations');
                            if (jsonString != null) {
                              final List<dynamic> jsonList =
                                  jsonDecode(jsonString);
                              final locations = jsonList
                                  .map((json) => SavedLocation.fromJson(json))
                                  .toList();
                              if (locations.length == 1) {
                                final loc = locations.first;

                                WidgetsBinding.instance
                                    .addPostFrameCallback((_) async {
                                  final prefs =
                                      await SharedPreferences.getInstance();
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
                                      }));
                                });
                              }

                              Navigator.pop(context);

                              Navigator.pop(context, true);
                            }
                          } else {
                            final cacheKey = "${saved.city}_${saved.country}"
                                .toLowerCase()
                                .replaceAll(' ', '_');

                            final prefs = await SharedPreferences.getInstance();
                            await prefs.setString(
                                'selectedViewLocation',
                                jsonEncode({
                                  'city': saved.city,
                                  'country': saved.country,
                                  'cacheKey': cacheKey,
                                  'lat': lat,
                                  'lon': lon,
                                  'isGPS': false,
                                }));
                            Navigator.pop(context, false);
                          }
                        });
                  },
                ),
      bottomNavigationBar: BottomAppBar(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "${"search_provider".tr()}:",
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.onSurface,
                      fontSize: 18,
                      fontWeight: FontWeight.w500),
                ),
                Text("${providerLabels[selectedProvider]}",
                    style: TextStyle(
                        color: Theme.of(context).colorScheme.secondary,
                        fontSize: 14)),
              ],
            ),
            FloatingActionButton(
              onPressed: openProviderSheet,
              child: Icon(Icons.tune),
            )
          ],
        ),
      ),
    );
  }
}
