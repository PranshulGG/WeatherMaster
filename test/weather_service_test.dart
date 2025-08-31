import 'dart:developer';

import 'package:flutter_test/flutter_test.dart';
import 'package:weather_master_app/services/fetch_data.dart';

void main() {
  final service = WeatherService();

  group('WeatherService unit tests', () {
    test('nullSafeValue returns correct default', () {
      expect(service.nullSafeValue<int>(null), 0);
      expect(service.nullSafeValue<double>(null), 0.0000001);
    });

    test('sanitizeCurrent fills missing fields', () {
      final current = {'temperature_2m': 25.0};
      final sanitized = service.sanitizeCurrent(current);
      expect(sanitized['temperature_2m'], 25.0);
      expect(sanitized['wind_speed_10m'], 0.0000001);
    });
  });
}
