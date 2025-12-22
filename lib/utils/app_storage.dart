import 'package:hive_ce/hive_ce.dart';

abstract class HiveBoxNames {
  static const String weatherCache = 'weatherMasterCache';
  static const String changelogs = 'changelogs';
  static const String aiSummaryCache = 'ai_summary_cache';
  static const String weatherModelsCache = 'weatherModelsCache';
}

abstract class PrefKeys {
  static const String layoutConfig = 'layout_config';
  static const String homeLocation = 'homeLocation';
  static const String currentLocation = 'currentLocation';
  static const String savedLocations = 'saved_locations';
  static const String selectedViewLocation = 'selectedViewLocation';
  static const String locale = 'locale';
  static const String triggerFromWorker = 'triggerfromWorker';
  static const String lastUpdatedFromHome = 'lastUpdatedFromHome';
}

class HiveBoxes {
  static Future<Box> open(String name) async {
    if (Hive.isBoxOpen(name)) {
      return Hive.box(name);
    }
    return Hive.openBox(name);
  }

  static Future<Box> openWeatherCache() => open(HiveBoxNames.weatherCache);

  static Future<Box> openWeatherModelsCache() =>
      open(HiveBoxNames.weatherModelsCache);

  static Future<Box> openChangelogs() => open(HiveBoxNames.changelogs);

  static Future<Box> openAiSummaryCache() => open(HiveBoxNames.aiSummaryCache);
}
