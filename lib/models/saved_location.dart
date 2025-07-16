class SavedLocation {
  final String city;
  final String country;
  final double latitude;
  final double longitude;
  Map<String, dynamic>? weatherData;
  DateTime? lastUpdated;

  SavedLocation({
    required this.city,
    required this.country,
    required this.latitude,
    required this.longitude,
    this.weatherData,
    this.lastUpdated,
  });

  factory SavedLocation.fromJson(Map<String, dynamic> json) {
    return SavedLocation(
      city: json['city'],
      country: json['country'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      weatherData: json['weatherData'],
      lastUpdated: json['lastUpdated'] != null
          ? DateTime.tryParse(json['lastUpdated'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'city': city,
      'country': country,
      'latitude': latitude,
      'longitude': longitude,
      'weatherData': weatherData,
      'lastUpdated': lastUpdated?.toIso8601String(),
    };
  }
}
