import 'package:flutter/material.dart';
import 'package:weather_master_app/utils/preferences_helper.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../services/notificationservice_native.dart';
import '../services/widget_service.dart';
import 'package:flutter/services.dart';
import '../screens/worker_log_page.dart';
import '../utils/condition_label_map.dart';

class BackgroundUpdatesPage extends StatefulWidget {
  const BackgroundUpdatesPage({super.key});

  @override
  State<BackgroundUpdatesPage> createState() => _BackgroundUpdatesPageState();
}

class _BackgroundUpdatesPageState extends State<BackgroundUpdatesPage> {
  bool _permissionGranted = false;
  int refreshInterval = 90;

  @override
  void initState() {
    super.initState();
    _loadPermission();
    refreshInterval = PreferencesHelper.getInt("savedRefreshInterval") ?? 90;
  }

  Future<void> _loadPermission() async {
    final bool granted = await NotificationService.checkPermission();
    setState(() {
      _permissionGranted = granted;
    });
  }

  Future<void> _RequestloadPermission() async {
    final bool granted = await NotificationService.requestPermission();
    _loadPermission();
  }

  final optionsInterval = {
    30: "30 ${"minutes".tr()}",
    60: "1 ${"hours".tr()}",
    90: "1.5 ${"hours".tr()}",
    120: "2 ${"hours".tr()}",
    180: "3 ${"hours".tr()}",
    240: "4 ${"hours".tr()}",
    300: "5 ${"hours".tr()}",
    360: "6 ${"hours".tr()}",
  };

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Theme.of(context).colorScheme.surface,
        body: CustomScrollView(slivers: [
          SliverAppBar.large(
            title: Text('background_updates'.tr()),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,
          ),
          SliverToBoxAdapter(
              child: Column(children: [
            SettingSection(
              styleTile: true,
              errorTile: true,
              tiles: [
                SettingActionTile(
                  title: Text(
                    "allow_notification_permission".tr(),
                    style: TextStyle(
                        color: Theme.of(context).colorScheme.onErrorContainer),
                  ),
                  description: Text("allow_notification_permission_sub".tr()),
                  visible: !_permissionGranted,
                  onTap: () async {
                    _RequestloadPermission();
                  },
                )
              ],
            ),
            SizedBox(
              height: 10,
            ),
            SettingSection(
              styleTile: true,
              PrimarySwitch: true,
              tiles: [
                SettingSwitchTile(
                    title: Padding(
                      padding: EdgeInsets.only(top: 12, bottom: 12),
                      child: Text(
                        "updates_in_the_background".tr(),
                        style: TextStyle(
                            fontVariations: FontVariationsMedium,
                            color: Theme.of(context)
                                .colorScheme
                                .onPrimaryContainer),
                      ),
                    ),

                    // description: Text(
                    // "Allow background activity. Turning it off may stop widget updates"),
                    toggled:
                        PreferencesHelper.getBool("useBackgroundUpdates") ??
                            false,
                    enabled: _permissionGranted,
                    onChanged: (value) {
                      PreferencesHelper.setBool("useBackgroundUpdates", value);
                      triggerBgUpdates(value);
                      setState(() {});
                    })
              ],
            ),
            SizedBox(
              height: 20,
            ),
            SettingSection(
                styleTile: true,
                title: SettingSectionTitle(
                  'additional'.tr(),
                  noPadding: true,
                ),
                tiles: [
                  SettingSingleOptionTile(
                    title: Text('refresh_interval'.tr()),
                    value: SettingTileValue(optionsInterval[refreshInterval]!),
                    dialogTitle: 'refresh_interval'.tr(),
                    options: optionsInterval.values.toList(),
                    initialOption: optionsInterval[refreshInterval]!,
                    onSubmitted: (value) {
                      final selectedKey = optionsInterval.entries
                          .firstWhere((e) => e.value == value)
                          .key;
                      PreferencesHelper.setInt(
                          "savedRefreshInterval", selectedKey);
                      if (selectedKey != refreshInterval) {
                        startWeatherService();
                      }
                      setState(() {
                        refreshInterval = selectedKey;
                      });
                    },
                  ),
                  SettingTextTile(
                    fullempty: true,
                    title: BatteryOptWidget(), // 0%
                  ),
                  SettingActionTile(
                    title: Text(
                      'scheduled_updates'.tr(),
                    ),
                    trailing: Icon(Symbols.chevron_right),
                    onTap: () async {
                      Navigator.of(context).push(
                        MaterialPageRoute(builder: (_) => WorkInfoWidget()),
                      );
                    },
                  ),
                ])
          ]))
        ]));
  }
}

Future<void> triggerBgUpdates(value) async {
  if (value == false) {
    stopWeatherService();
    PreferencesHelper.setBool("runningTaskBackground", false);
  } else {
    startWeatherService();
    PreferencesHelper.setBool("runningTaskBackground", true);
  }
}

class BatteryOptimization {
  static const _channel = MethodChannel('com.pranshulgg.battery_optimization');

  /// Checks if the app is ignoring battery optimizations
  static Future<bool> isIgnoringBatteryOptimizations() async {
    try {
      final bool result =
          await _channel.invokeMethod('isIgnoringBatteryOptimizations');
      return result;
    } catch (_) {
      return false;
    }
  }

  /// Shows the system dialog to request battery optimization exemption
  static Future<void> requestIgnoreBatteryOptimizations() async {
    try {
      await _channel.invokeMethod('requestIgnoreBatteryOptimizations');
    } catch (e) {
      print("Error requesting battery optimization: $e");
    }
  }
}

class BatteryOptWidget extends StatefulWidget {
  @override
  _BatteryOptWidgetState createState() => _BatteryOptWidgetState();
}

class _BatteryOptWidgetState extends State<BatteryOptWidget>
    with WidgetsBindingObserver {
  bool? _isWhitelisted;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _checkBatteryStatus();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _checkBatteryStatus();
    }
  }

  Future<void> _checkBatteryStatus() async {
    final isWhitelisted =
        await BatteryOptimization.isIgnoringBatteryOptimizations();
    setState(() {
      _isWhitelisted = isWhitelisted;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isWhitelisted == null) {
      return const CircularProgressIndicator();
    }

    return _isWhitelisted!
        ? Padding(
            padding: EdgeInsets.only(left: 16, right: 16),
            child: Text("disable_battery_optimizations_done".tr(),
                style:
                    TextStyle(color: Theme.of(context).colorScheme.tertiary)),
          )
        : SettingActionTile(
            icon: null,
            title: Text(
              'disable_battery_optimizations'.tr(),
              style: TextStyle(color: Theme.of(context).colorScheme.error),
            ),
            description: Text('disable_battery_optimizations_sub'.tr(),
                style: TextStyle(color: Theme.of(context).colorScheme.error)),
            onTap: () async {
              await BatteryOptimization.requestIgnoreBatteryOptimizations();
            },
          );
  }
}
