import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/saved_location.dart';
import '../services/fetch_data.dart';
import '../models/loading_me.dart';


enum GeoProvider { nominatim, geonames, openMeteo }

class SearchLocationsScreen extends StatefulWidget {
  const SearchLocationsScreen({super.key});

  @override
  State<SearchLocationsScreen> createState() => _SearchLocationsScreenState();
}

class _SearchLocationsScreenState extends State<SearchLocationsScreen> {

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
    await prefs.setString(
        'saved_locations',
        jsonEncode(current.map((e) => e.toJson()).toList()));
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


}


Future<void> loadSavedProvider() async {
  final prefs = await SharedPreferences.getInstance();
  final index = prefs.getInt('geo_provider_index') ?? 0;
  setState(() {
    selectedProvider = GeoProvider.values[index];
  });
}

Future<void> saveProvider(GeoProvider provider) async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.setInt('geo_provider_index', provider.index);
}


Future<void> searchLocation(String input) async {
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
          'country': address['country'] ?? '',
          'country_code': address['country_code'] ?? '',
          'lat': e['lat'] ?? '',
          'lon': e['lon'] ?? '',
        };
      }).toList(),

      GeoProvider.geonames => (data['geonames'] as List).map<Map<String, String>>((e) {
        return {
          'city': e['name'],
          'country': e['countryName'] ?? '',
          'country_code': e['countryCode'] ?? '',
          'lat': e['lat']?.toString() ?? '',
          'lon': e['lng']?.toString() ?? '', 
        };
      }).toList(),

      GeoProvider.openMeteo => (data['results'] as List).map<Map<String, String>>((e) {
        return {
          'city': e['name'],
          'country': e['country'] ?? '',
          'country_code': e['country_code'] ?? '',
          'lat': e['latitude']?.toString() ?? '',
          'lon': e['longitude']?.toString() ?? '',
        };
      }).toList(),
    };




    // Remove duplicates
    final unique = {
      for (var loc in locations)
        "${loc['city']},${loc['country']}": loc
    }.values.toList();

    setState(() => results = unique);
  } catch (e) {
    setState(() => results = []);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Failed to load.")),
    );
  }

  setState(() => isLoading = false);
}

void openProviderDialog() async {
  GeoProvider tempProvider = selectedProvider;

  final confirmed = await showDialog<bool>(
    context: context,
    builder: (_) {
      return AlertDialog(
        title: const Text("Choose Provider"),
        contentPadding: EdgeInsets.only(left: 0, right: 0, top: 16, bottom: 5),
        content: StatefulBuilder(
          builder: (context, setState) {
            return Column(
              mainAxisSize: MainAxisSize.min,
              children: GeoProvider.values.map((provider) {
                return RadioListTile<GeoProvider>(
                  dense: true,
                  title: Text(providerLabels[provider]!, style: TextStyle(fontSize: 15),),

                  value: provider,
                  groupValue: tempProvider,
                  onChanged: (val) => setState(() => tempProvider = val!),
                );
              }).toList(),
            );
          },
        ),
        actions: [
          TextButton(
            child: Text("Cancel", style: TextStyle(fontWeight: FontWeight.w500, fontSize: 16),),
            onPressed: () => Navigator.pop(context, false),
          ),
          TextButton(
            child: Text("Save", style: TextStyle(fontWeight: FontWeight.w500, fontSize: 16),),
            onPressed: () => Navigator.pop(context, true),
          ),
        ],
      );
    },
  );

  if (confirmed == true && tempProvider != selectedProvider) {
    setState(() => selectedProvider = tempProvider);
    saveProvider(tempProvider);
    if (query.isNotEmpty) searchLocation(query); // refresh search
  }
}


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: 
        TextField(
          style: TextStyle(fontSize: 16, color: Theme.of(context).colorScheme.onSurface),
          onChanged: (value) => query = value,
          onSubmitted: searchLocation,
          decoration: const InputDecoration(
            border: InputBorder.none,
            focusedBorder: InputBorder.none,
            enabledBorder: InputBorder.none,
            disabledBorder: InputBorder.none,
            hintText: "Search...",
          ), 
          ),
        titleSpacing: 0,     
        toolbarHeight: 65,
        scrolledUnderElevation: 0,
        bottom: PreferredSize(preferredSize:Size(0, 16) , child: Divider(thickness: 2, color: Theme.of(context).colorScheme.outline.withOpacity(0.8),)),

      ),

      body: isLoading
          ? const Center(child: LoaderWidget(size: 60, isContained: false,))
          : results.isEmpty
              ? const Center(child: Text("No results"))
              : ListView.builder(
                  itemCount: results.length,
                  itemBuilder: (context, index) {
                    final city = results[index]['city'] ?? '';
                    final country = results[index]['country'] ?? '';
                    final code = results[index]['country_code'] ?? '';
                    return ListTile(
                       leading: code.isNotEmpty
                    ? CircleAvatar(
                        radius: 18,
                        backgroundColor: Colors.transparent,
                        backgroundImage: NetworkImage("https://flagcdn.com/w80/${code.toLowerCase()}.png"),
                      )
                    : const Icon(Icons.location_on),
                      title: Text(city, style: TextStyle(fontWeight: FontWeight.w500),),
                      subtitle: country.isNotEmpty ? Text(country) : null,
                      onTap: () async {
                      showDialog(
                          context: context,
                          barrierDismissible: false, 
                          barrierColor: Theme.of(context).colorScheme.surface,
                          builder: (context) => const Center(child: LoaderWidget(size: 60, isContained: false,)),
                        );

                        final location = results[index];
                        final lat = double.tryParse(location['lat'] ?? '') ?? 0.0;
                        final lon = double.tryParse(location['lon'] ?? '') ?? 0.0;

                        final saved = SavedLocation(
                          latitude: lat,
                          longitude: lon,
                          city: location['city'] ?? '',
                          country: location['country'] ?? '',
                        );
                        
                        saveLocation(saved);
                    final cacheKey = "${saved.city}_${saved.country}".toLowerCase().replaceAll(' ', '_');
                    final weatherService = WeatherService();
                    await weatherService.fetchWeather(lat, lon, locationName: cacheKey);


                    final prefs = await SharedPreferences.getInstance();
                    final jsonString = prefs.getString('saved_locations');
                    if (jsonString != null) {
                      final List<dynamic> jsonList = jsonDecode(jsonString);
                      final locations = jsonList.map((json) => SavedLocation.fromJson(json)).toList();
                        if (locations.length == 1) {
                        
                            final loc = locations.first;

                          WidgetsBinding.instance.addPostFrameCallback((_) async {
                                final prefs = await SharedPreferences.getInstance();
                        final cacheKey = "${loc.city}_${loc.country}".toLowerCase().replaceAll(' ', '_');

                        await prefs.setString('homeLocation', jsonEncode({
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
                        }
                    );
                  },
        ),
        bottomNavigationBar: BottomAppBar(
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("Provider:", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 15),),
                 Text("${providerLabels[selectedProvider]}", style: TextStyle(color: Theme.of(context).colorScheme.secondary, fontWeight: FontWeight.w500, fontSize: 18)),
                ],
              ),
              FloatingActionButton(
                onPressed: openProviderDialog,
                child: Icon(Icons.tune),
              )
            ],
          ),
        ),
    );
  }
}


