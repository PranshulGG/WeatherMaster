import 'package:easy_localization/easy_localization.dart';
import '../utils/preferences_helper.dart';

List<Map<String, dynamic>> convertToListOfMaps(Map<String, dynamic> data) {
  final length = (data.values.first as List).length;
  return List.generate(length, (index) {
    return data.map((key, valueList) => MapEntry(key, valueList[index]));
  });
}




int getStartIndex(utc_offset_seconds, hourlyTime) {
    final offset = Duration(seconds: int.parse(utc_offset_seconds));
    final nowUtc = DateTime.now().toUtc();
    final nowLocal = nowUtc.add(offset);

    final timeUnit = PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';


    final roundedNow = DateTime(nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

    int startIndex = hourlyTime.indexWhere((timeStr) {
      final forecastLocal = DateTime.parse(timeStr); 
      return !forecastLocal.isBefore(roundedNow);
    });

    if (startIndex == -1) startIndex = 0;

    return startIndex;
}

  bool isPollenDataAvailable(List<double?> values) {
    return values.every((value) => value != null);
  }


String getDayLabel(DateTime date, int index, utcOffsetSeconds) {
      int offsetSeconds = int.parse(utcOffsetSeconds.toString());
    DateTime utcNow = DateTime.now().toUtc();
    DateTime now = utcNow.add(Duration(seconds: offsetSeconds));


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


  final today = DateTime(now.year, now.month, now.day);
  final target = DateTime(date.year, date.month, date.day);

  if (target == today) {
    return "Today";
  } else {
    return DateFormat('E').format(date); 
  }
}