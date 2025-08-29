class UnitConverter {
  static double celsiusToFahrenheit(double c) => (c * 9 / 5) + 32;
  static double fahrenheitToCelsius(double f) => (f - 32) * 5 / 9;

  /// Wind Speed
  static double kmhToMph(double kmh) => kmh * 0.621371;
  static double kmhToMs(double kmh) => kmh / 3.6;
  static double kmhToBeaufort(double kmh) {
    final thresholds = [1, 5, 11, 19, 28, 38, 49, 61, 74, 88, 102, 117];
    for (int i = 0; i < thresholds.length; i++) {
      if (kmh < thresholds[i]) return i.toDouble();
    }
    return 12;
  }

  static double kmhToKt(double kmh) => kmh / 1.852;
  static double ktToKmh(double kt) => kt * 1.852;

  static double mphToKmh(double mph) => mph / 0.621371;
  static double msToKmh(double ms) => ms * 3.6;

  /// Visibility
  static double kmToMiles(double km) => km * 0.621371;
  static double milesToKm(double miles) => miles / 0.621371;

  /// Precipitation
  static double mmToCm(double mm) => mm / 10;
  static double mmToIn(double mm) => mm * 0.0393701;
  static double cmToMm(double cm) => cm * 10;
  static double inToMm(double inch) => inch / 0.0393701;

  /// Pressure
  static double hPaToInHg(double hPa) => hPa * 0.0295299830714;
  static double hPaToMmHg(double hPa) => hPa * 0.750061561303;
  static double inHgTohPa(double inHg) => inHg / 0.0295299830714;
  static double mmHgTohPa(double mmHg) => mmHg / 0.750061561303;

// Time

  static String formatTo12Hour(DateTime time) {
    final hour = time.hour % 12 == 0 ? 12 : time.hour % 12;
    final period = time.hour >= 12 ? 'PM' : 'AM';
    return '$hour$period';
  }

  static String formatTo24Hour(DateTime time) {
    return '${time.hour.toString().padLeft(2, '0')}:00';
  }

  static String string24To12(String time24) {
    final time = DateTime.parse('2000-01-01 $time24:00');
    return formatTo12Hour(time);
  }

  /// Distance
  static double mToKm(double meters) => meters / 1000;
  static double mToMiles(double meters) => meters * 0.000621371;
}
