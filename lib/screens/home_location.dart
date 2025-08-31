import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/saved_location.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/geo_location.dart';
import '../services/fetch_data.dart';
import 'package:easy_localization/easy_localization.dart';
import '../models/loading_me.dart';

class HomeLocationScreen extends StatelessWidget {
  const HomeLocationScreen({super.key});

  Future<List<SavedLocation>> loadSavedLocations() async {
    final prefs = await SharedPreferences.getInstance();
    final saved = prefs.getString('saved_locations');
    if (saved != null) {
      final decoded = jsonDecode(saved) as List;
      return decoded.map((e) => SavedLocation.fromJson(e)).toList();
    }
    return [];
  }

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

  Future<void> setHomeLocation(BuildContext context, SavedLocation loc) async {
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
          'isGPS': false,
        }));

    Navigator.pop(context, true);
  }

  @override
  Widget build(BuildContext context) {
    Future<void> saveLocation(SavedLocation newLocation) async {
      final prefs = await SharedPreferences.getInstance();
      final existing = prefs.getString('saved_locations');
      List<SavedLocation> current = [];

      if (existing != null) {
        final decoded = jsonDecode(existing) as List;
        current = decoded.map((e) => SavedLocation.fromJson(e)).toList();
      }

      bool alreadyExists = current.any((loc) =>
          loc.city == newLocation.city && loc.country == newLocation.country);

      if (!alreadyExists) {
        current.add(newLocation);
        await prefs.setString('saved_locations',
            jsonEncode(current.map((e) => e.toJson()).toList()));
      }
    }

    return Scaffold(
      body: FutureBuilder<List<SavedLocation>>(
        future: loadSavedLocations(),
        builder: (context, snapshot) {
          return FutureBuilder<Map<String, dynamic>>(
            future: _getCurrentHomeInfo(),
            builder: (context, homeSnapshot) {
              final homeData =
                  homeSnapshot.data ?? {'cacheKey': '', 'isGPS': false};
              final currentCacheKey = homeData['cacheKey'] ?? '';
              final isGPS = homeData['isGPS'] ?? false;

              return CustomScrollView(
                slivers: [
                  SliverAppBar.large(
                    title: Text('home_location'.tr()),
                    titleSpacing: 0,
                    backgroundColor: Theme.of(context).colorScheme.surface,
                    scrolledUnderElevation: 1,
                  ),
                  SliverList(
                    delegate: SliverChildListDelegate.fixed(
                      [
                        Opacity(
                          opacity: 0.7,
                          child: Padding(
                            padding: EdgeInsets.only(
                                bottom: 10, left: 16, right: 16),
                            child: Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Container(
                                  padding: EdgeInsets.only(top: 2),
                                  child: Icon(
                                    Icons.info_outline,
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                    size: 17,
                                  ),
                                ),
                                SizedBox(
                                  width: 5,
                                ),
                                Expanded(
                                  child: Text(
                                    "info_home_location_tip".tr(),
                                    style: TextStyle(
                                        color: Theme.of(context)
                                            .colorScheme
                                            .onSurfaceVariant),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  if (snapshot.connectionState == ConnectionState.waiting)
                    const SliverFillRemaining(
                      child: Center(child: CircularProgressIndicator()),
                    )
                  else if (!snapshot.hasData || snapshot.data!.isEmpty)
                    const SliverFillRemaining(
                      child:
                          Center(child: Text("No saved locations available.")),
                    )
                  else
                    SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, index) {
                          if (index == 0) {
                            // This is the device location tile
                            // final isSelected = (homeSnapshot.data == 'device_location');
                            final isSelected = isGPS;

                            return Padding(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 12.0, vertical: 2.0),
                              child: Container(
                                decoration: BoxDecoration(
                                  color: isSelected
                                      ? Theme.of(context)
                                          .colorScheme
                                          .primaryContainer
                                      : Theme.of(context)
                                          .colorScheme
                                          .surfaceContainerLowest,
                                  borderRadius: isSelected
                                      ? BorderRadius.circular(50)
                                      : BorderRadius.only(
                                          topLeft: Radius.circular(18),
                                          topRight: Radius.circular(18),
                                          bottomLeft: Radius.circular(0),
                                          bottomRight: Radius.circular(0)),
                                ),
                                child: ListTile(
                                    contentPadding:
                                        EdgeInsets.only(left: 5, right: 20),
                                    minTileHeight: 68,
                                    splashColor: Colors.transparent,
                                    leading: isSelected
                                        ? Stack(
                                            alignment: Alignment.center,
                                            children: [
                                              SvgPicture.string(buildSvg(
                                                  Theme.of(context)
                                                      .colorScheme
                                                      .primary)),
                                              Icon(Icons.check,
                                                  color: Theme.of(context)
                                                      .colorScheme
                                                      .onPrimary),
                                            ],
                                          )
                                        : Stack(
                                            alignment: Alignment.center,
                                            children: [
                                              SvgPicture.string(buildSvg(
                                                  Theme.of(context)
                                                      .colorScheme
                                                      .tertiary)),
                                              Icon(Icons.my_location,
                                                  color: Theme.of(context)
                                                      .colorScheme
                                                      .onTertiary),
                                            ],
                                          ),
                                    title: Text(
                                      "your_device_location".tr(),
                                      style: TextStyle(
                                        fontWeight: FontWeight.w500,
                                        color: isSelected
                                            ? Theme.of(context)
                                                .colorScheme
                                                .onPrimaryContainer
                                            : null,
                                      ),
                                    ),
                                    subtitle: isSelected
                                        ? Text(
                                            "${homeData['city']}, ${homeData['country']}")
                                        : Text(
                                            "current_location_tile_sub".tr()),
                                    onTap: () async {
                                      bool ready =
                                          await LocationPermissionHelper
                                              .checkServicesAndPermission(
                                                  context);
                                      if (!ready) return;
                                      showDialog(
                                        context: context,
                                        barrierDismissible: false,
                                        barrierColor: Theme.of(context)
                                            .colorScheme
                                            .surface,
                                        builder: (context) => const Center(
                                            child: LoaderWidget(
                                          size: 60,
                                          isContained: false,
                                        )),
                                      );
                                      try {
                                        final position = await NativeLocation
                                            .getCurrentPosition();

                                        final geoData =
                                            await NativeLocation.reverseGeocode(
                                                position.latitude,
                                                position.longitude);

                                        final saved = SavedLocation(
                                          latitude: position.latitude,
                                          longitude: position.longitude,
                                          city: geoData['city']!,
                                          country: geoData['country']!,
                                        );

                                        saveLocation(saved);

                                        final cacheKey =
                                            "${saved.city}_${saved.country}"
                                                .toLowerCase()
                                                .replaceAll(' ', '_');

                                        final prefs = await SharedPreferences
                                            .getInstance();
                                        prefs.setString(
                                            'homeLocation',
                                            jsonEncode({
                                              'city': saved.city,
                                              'country': saved.country,
                                              'cacheKey': cacheKey,
                                              'lat': saved.latitude,
                                              'lon': saved.longitude,
                                              'isGPS': true,
                                            }));

                                        final weatherService = WeatherService();
                                        await weatherService.fetchWeather(
                                            saved.latitude, saved.longitude,
                                            locationName: cacheKey,
                                            context: context);

                                        Navigator.pop(context);
                                        Navigator.pop(context, true);
                                      } catch (e) {
                                        ScaffoldMessenger.of(context)
                                            .showSnackBar(
                                          SnackBar(
                                              content: Text(
                                                  'Error: ${e.toString()}')),
                                        );
                                      }
                                    }),
                              ),
                            );
                          }

                          final loc = snapshot.data![index - 1];
                          final isLast = index == snapshot.data!.length;

                          final currentCacheKey =
                              homeSnapshot.data?['cacheKey'] ?? '';
                          final thisCacheKey = "${loc.city}_${loc.country}"
                              .toLowerCase()
                              .replaceAll(' ', '_');

                          final isSelected =
                              thisCacheKey == currentCacheKey && !isGPS;

                          return Container(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 12.0, vertical: 2.0),
                            margin: EdgeInsets.only(
                                bottom: isLast
                                    ? MediaQuery.of(context).padding.bottom + 20
                                    : 0),
                            child: Container(
                              decoration: BoxDecoration(
                                color: isSelected
                                    ? Theme.of(context)
                                        .colorScheme
                                        .primaryContainer
                                    : Theme.of(context)
                                        .colorScheme
                                        .surfaceContainerLowest,
                                borderRadius: isSelected
                                    ? BorderRadius.circular(50)
                                    : BorderRadius.only(
                                        topLeft: Radius.circular(0),
                                        topRight: Radius.circular(0),
                                        bottomLeft:
                                            Radius.circular(isLast ? 18 : 0),
                                        bottomRight:
                                            Radius.circular(isLast ? 18 : 0)),
                              ),
                              child: ListTile(
                                contentPadding:
                                    EdgeInsets.only(left: 3, right: 20),
                                minTileHeight: 68,
                                splashColor: Colors.transparent,
                                leading: isSelected
                                    ? Stack(
                                        alignment: Alignment.center,
                                        children: [
                                          SvgPicture.string(buildSvg(
                                              Theme.of(context)
                                                  .colorScheme
                                                  .primary)),
                                          Icon(Icons.check,
                                              color: Theme.of(context)
                                                  .colorScheme
                                                  .onPrimary),
                                        ],
                                      )
                                    : Stack(
                                        alignment: Alignment.center,
                                        children: [
                                          SvgPicture.string(buildSvg(
                                              Theme.of(context)
                                                  .colorScheme
                                                  .tertiary)),
                                          Icon(Icons.location_on,
                                              color: Theme.of(context)
                                                  .colorScheme
                                                  .onTertiary),
                                        ],
                                      ),
                                title: Text(
                                  loc.city,
                                  style: TextStyle(
                                      fontWeight: FontWeight.w500,
                                      color: isSelected
                                          ? Theme.of(context)
                                              .colorScheme
                                              .onPrimaryContainer
                                          : null),
                                ),
                                subtitle: Text(
                                  loc.country,
                                  style: TextStyle(
                                      color: isSelected
                                          ? Theme.of(context)
                                              .colorScheme
                                              .onPrimaryContainer
                                          : null),
                                ),
                                onTap: () {
                                  if (!isSelected) {
                                    setHomeLocation(context, loc);
                                  }
                                },
                              ),
                            ),
                          );
                        },
                        childCount: snapshot.data!.length + 1,
                      ),
                    ),
                ],
              );
            },
          );
        },
      ),
    );
  }
}

String buildSvg(Color color) {
  final hexColor =
      '#${color.value.toRadixString(16).padLeft(8, '0').substring(2)}';

  return '''<svg width="70" height="70" viewBox="0 0 70 70" fill="none" xmlns="http://www.w3.org/2000/svg">
  <path d="M30.7073 7.34003C30.807 7.24237 30.8568 7.19354 30.9 7.15287C33.2032 4.98414 36.7969 4.98414 39.1 7.15287C39.1432 7.19354 39.1931 7.24237 39.2927 7.34003C39.3525 7.39863 39.3824 7.42793 39.4109 7.45505C40.8846 8.86156 42.973 9.42113 44.9526 8.93995C44.9908 8.93067 45.0313 8.92025 45.1124 8.8994C45.2475 8.86467 45.3151 8.8473 45.3729 8.83367C48.4518 8.10707 51.5641 9.90393 52.4743 12.9337C52.4914 12.9905 52.5101 13.0577 52.5476 13.1921C52.5701 13.2728 52.5813 13.3131 52.5924 13.3508C53.1655 15.3058 54.6943 16.8345 56.6492 17.4076C56.6869 17.4187 56.7272 17.4299 56.8079 17.4524C56.9423 17.4899 57.0095 17.5087 57.0663 17.5257C60.0961 18.436 61.8929 21.5482 61.1663 24.6272C61.1527 24.6849 61.1353 24.7525 61.1006 24.8876C61.0798 24.9687 61.0694 25.0093 61.0601 25.0474C60.5789 27.027 61.1385 29.1154 62.545 30.5892C62.5721 30.6176 62.6014 30.6475 62.66 30.7073C62.7576 30.807 62.8065 30.8568 62.8472 30.9C65.0159 33.2032 65.0159 36.7969 62.8472 39.1C62.8065 39.1432 62.7576 39.1931 62.66 39.2927C62.6014 39.3525 62.5721 39.3824 62.545 39.4109C61.1385 40.8846 60.5789 42.973 61.0601 44.9526C61.0694 44.9908 61.0798 45.0313 61.1006 45.1124C61.1353 45.2475 61.1527 45.3151 61.1663 45.3729C61.8929 48.4518 60.0961 51.5641 57.0663 52.4743C57.0095 52.4914 56.9423 52.5101 56.8079 52.5476C56.7272 52.5701 56.6869 52.5813 56.6492 52.5924C54.6943 53.1655 53.1655 54.6943 52.5924 56.6492C52.5813 56.6869 52.5701 56.7272 52.5476 56.8079C52.5101 56.9423 52.4914 57.0095 52.4743 57.0663C51.5641 60.0961 48.4518 61.8929 45.3729 61.1663C45.3151 61.1527 45.2475 61.1353 45.1124 61.1006C45.0313 61.0798 44.9908 61.0694 44.9526 61.0601C42.973 60.5789 40.8846 61.1385 39.4109 62.545C39.3824 62.5721 39.3525 62.6014 39.2927 62.66C39.1931 62.7576 39.1432 62.8065 39.1 62.8472C36.7969 65.0159 33.2032 65.0159 30.9 62.8472C30.8568 62.8065 30.807 62.7576 30.7073 62.66C30.6475 62.6014 30.6176 62.5721 30.5892 62.545C29.1154 61.1385 27.027 60.5789 25.0474 61.0601C25.0093 61.0694 24.9687 61.0798 24.8876 61.1006C24.7525 61.1353 24.6849 61.1527 24.6272 61.1663C21.5482 61.8929 18.436 60.0961 17.5257 57.0663C17.5087 57.0095 17.4899 56.9423 17.4524 56.8079C17.4299 56.7272 17.4187 56.6869 17.4076 56.6492C16.8345 54.6943 15.3058 53.1655 13.3508 52.5924C13.3131 52.5813 13.2728 52.5701 13.1921 52.5476C13.0577 52.5101 12.9905 52.4914 12.9337 52.4743C9.90393 51.5641 8.10707 48.4518 8.83367 45.3729C8.8473 45.3151 8.86467 45.2475 8.8994 45.1124C8.92025 45.0313 8.93067 44.9908 8.93995 44.9526C9.42113 42.973 8.86156 40.8846 7.45505 39.4109C7.42793 39.3824 7.39863 39.3525 7.34003 39.2927C7.24237 39.1931 7.19354 39.1432 7.15287 39.1C4.98414 36.7969 4.98414 33.2032 7.15287 30.9C7.19354 30.8568 7.24237 30.807 7.34003 30.7073C7.39863 30.6475 7.42793 30.6176 7.45505 30.5892C8.86156 29.1154 9.42113 27.027 8.93995 25.0474C8.93067 25.0093 8.92025 24.9687 8.8994 24.8876C8.86467 24.7525 8.8473 24.6849 8.83367 24.6272C8.10707 21.5482 9.90393 18.436 12.9337 17.5257C12.9905 17.5087 13.0577 17.4899 13.1921 17.4524C13.2728 17.4299 13.3131 17.4187 13.3508 17.4076C15.3058 16.8345 16.8345 15.3058 17.4076 13.3508C17.4187 13.3131 17.4299 13.2728 17.4524 13.1921C17.4899 13.0577 17.5087 12.9905 17.5257 12.9337C18.436 9.90393 21.5482 8.10707 24.6272 8.83367C24.6849 8.8473 24.7525 8.86467 24.8876 8.8994C24.9687 8.92025 25.0093 8.93067 25.0474 8.93995C27.027 9.42113 29.1154 8.86156 30.5892 7.45505C30.6176 7.42793 30.6475 7.39863 30.7073 7.34003Z" fill="$hexColor"/>
  </svg>
''';
}
