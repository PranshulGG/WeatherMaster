import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import '../utils/preferences_helper.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'package:weather_master_app/utils/open_links.dart';

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

  final roundedNow =
      DateTime(nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

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

final String currentVersion = 'v2.6.0';
final String githubRepo = 'PranshulGG/WeatherMaster';
bool isChecking = false;

Future<void> checkForUpdatesOnStart(BuildContext context) async {
  if (isChecking) return;
  isChecking = true;

  try {
    final prefs = await SharedPreferences.getInstance();
    final lastCheckMillis = prefs.getInt('lastUpdateCheck') ?? 0;
    final now = DateTime.now().millisecondsSinceEpoch;

    if (now - lastCheckMillis < 24 * 60 * 60 * 1000) {
      isChecking = false;
      return;
    }

    final releasesUrl = 'https://api.github.com/repos/$githubRepo/releases';
    final response = await http.get(Uri.parse(releasesUrl));

    if (response.statusCode != 200) {
      throw Exception('Failed to fetch releases');
    }

    final List<dynamic> releases = jsonDecode(response.body);
    final latestStable = releases.firstWhere(
      (release) => release['prerelease'] == false,
      orElse: () => null,
    );

    await Future.delayed(Duration(seconds: 2));

    if (latestStable != null && latestStable['tag_name'] != currentVersion) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                  '${"new_version_available!".tr()} • ${latestStable['tag_name']}'),
              GestureDetector(
                onTap: () {
                  openLink(
                      "https://github.com/PranshulGG/WeatherMaster/releases/latest");
                },
                child: Padding(
                  padding: const EdgeInsets.only(right: 5),
                  child: Text(
                    "View",
                    style: TextStyle(
                      fontWeight: FontWeight.w600,
                      fontSize: 16,
                      color: Theme.of(context).colorScheme.inversePrimary,
                    ),
                  ),
                ),
              ),
            ],
          ),
          duration: Duration(days: 1),
        ),
      );
    }

    await prefs.setInt('lastUpdateCheck', now);
  } catch (e) {
    print('Update check failed: $e');
  } finally {
    isChecking = false;
  }
}
