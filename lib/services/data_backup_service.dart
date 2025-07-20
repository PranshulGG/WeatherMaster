import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:hive/hive.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:file_picker/file_picker.dart';
import '../utils/snack_util.dart';
import 'package:restart_app/restart_app.dart';

class DataBackupService {
  static Future<void> exportData() async {
  final hiveBox = Hive.box('weatherMasterCache');
  final prefs = await SharedPreferences.getInstance();

  final sharedPrefsData = prefs.getKeys().fold<Map<String, dynamic>>({}, (map, key) {
    map[key] = prefs.get(key);
    return map;
  });

  final hiveData = Map<String, dynamic>.from(hiveBox.toMap());

  final allData = {
    'hive': hiveData,
    'sharedPreferences': sharedPrefsData,
  };

  final jsonString = jsonEncode(allData);
  final jsonBytes = utf8.encode(jsonString);

  try {
    final outputFilePath = await FilePicker.platform.saveFile(
      dialogTitle: 'Save backup file',
      fileName: 'WeatherMaster_data_export.json',
      type: FileType.custom,
      allowedExtensions: ['json'],
      bytes: jsonBytes, 
    );

    if (outputFilePath != null) {
    } else {
    }
  } catch (e) {
    return;
  }
}



  static Future<void> importAndReplaceAllData(BuildContext context) async {
  final prefs = await SharedPreferences.getInstance();
  final hiveBox = Hive.box('weatherMasterCache');

  try {
    final result = await FilePicker.platform.pickFiles(
      dialogTitle: 'Select backup file to import',
      type: FileType.custom,
      allowedExtensions: ['json'],
      withData: true,
    );

    if (result == null || result.files.isEmpty) {
      return;
    }

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('import_data'.tr()),
        content: Text(
          'import_data_sub'.tr(),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text('cancel'.tr()),
          ),
          FilledButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: Text('Import'),
          ),
        ],
      ),
    );

    if (confirmed != true) {
      return;
    }

    final pickedFile = result.files.single;
    final fileBytes = pickedFile.bytes;

    if (fileBytes == null) {
      return;
    }

    final jsonString = utf8.decode(fileBytes);
    final Map<String, dynamic> data = jsonDecode(jsonString);

    await prefs.clear();
    await hiveBox.clear();

    final sharedPrefsData = Map<String, dynamic>.from(data['sharedPreferences'] ?? {});
    for (final entry in sharedPrefsData.entries) {
      final key = entry.key;
      final value = entry.value;
      if (value is bool) {
        await prefs.setBool(key, value);
      } else if (value is int) {
        await prefs.setInt(key, value);
      } else if (value is double) {
        await prefs.setDouble(key, value);
      } else if (value is String) {
        await prefs.setString(key, value);
      } else if (value is List) {
        await prefs.setStringList(key, List<String>.from(value));
      }
    }

    final hiveData = Map<String, dynamic>.from(data['hive'] ?? {});
    for (final entry in hiveData.entries) {
      await hiveBox.put(entry.key, entry.value);
    }

    SnackUtil.showSnackBar(context: context, message: "Import complete. Restarting app");


    await Future.delayed(Duration(seconds: 10)).timeout(
      Duration(seconds: 2),
      onTimeout: () {
      Restart.restartApp();
      },
    );
  } catch (e) {
    SnackUtil.showSnackBar(context: context, message: "Error during import");
  }
}

}
