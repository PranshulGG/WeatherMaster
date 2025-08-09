import 'package:flutter/material.dart';
import 'package:weather_master_app/utils/preferences_helper.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../services/notificationservice_native.dart';
import '../services/widget_service.dart';
import 'package:flutter/services.dart';
import '../screens/worker_log_page.dart';

class BackgroundUpdatesPage extends StatefulWidget {
  const BackgroundUpdatesPage({super.key});

  @override
  State<BackgroundUpdatesPage> createState() => _BackgroundUpdatesPageState();
}

class _BackgroundUpdatesPageState extends State<BackgroundUpdatesPage> {

  bool _permissionGranted = false;

  @override
  void initState() {
    super.initState();
    _loadPermission();
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            title: Text('Background updates'),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,            
          ),
           SliverToBoxAdapter(
            child: Column(
              children: [
                SettingSection(
                  styleTile: true,
                  errorTile: true,
                  tiles: [
                    SettingActionTile(
                     title: Text("Allow notification permission", style: TextStyle(color: Theme.of(context).colorScheme.onErrorContainer),), 
                     description: Text("Permission is required for background app updates"),
                     visible:  !_permissionGranted,
                      onTap: () async {
                        _RequestloadPermission();
                      },
                     )
                  ],
                ),
                SizedBox(height: 10,),
                SettingSection(
                  styleTile: true,
                  PrimarySwitch: true,
                  tiles: [
                    SettingSwitchTile(
                     title: Text("Updates in the background", style: TextStyle(color: Theme.of(context).colorScheme.onPrimaryContainer),), 
                     description: Text("Allow background activity. Turning it off may stop widget updates"),
                     toggled: PreferencesHelper.getBool("useBackgroundUpdates") ?? false,
                     enabled:  _permissionGranted,
                     onChanged: (value) {
                      PreferencesHelper.setBool("useBackgroundUpdates", value);
                      triggerBgUpdates(value);
                      setState(() {

                      });
                     }
                     )
                  ],
                ),
              SizedBox(height: 20,),
                  SettingSection(
                    styleTile: true,
                    title: SettingSectionTitle('additional'.tr(), noPadding: true,),
                    tiles: [
                  SettingTextTile(
                    fullempty: true,
                    title: BatteryOptWidget(), // 0%
                  ),      
                  SettingActionTile(
                  title: Text('Scheduled updates',),
                  trailing: Icon(Symbols.chevron_right),
                    onTap: () async {
                      Navigator.of(context).push(
                        MaterialPageRoute(builder: (_) => WorkInfoWidget()),
                      );
                    }, 
                ),
                ]
                )

              ]
            )
           )
        ]
      )
    );
  }
}


Future<void> triggerBgUpdates(value) async {
    if(value == false){
      stopWeatherService();
      PreferencesHelper.setBool("runningTaskBackground", false);
    } else{
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


class BatteryOptWidget extends StatefulWidget  {
  @override
  _BatteryOptWidgetState createState() => _BatteryOptWidgetState();
}

class _BatteryOptWidgetState extends State<BatteryOptWidget> with WidgetsBindingObserver {
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
    final isWhitelisted = await BatteryOptimization.isIgnoringBatteryOptimizations();
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
        ?  Padding(padding: EdgeInsets.only(left: 16, right: 16),
          child: Text("Battery optimization already disabled", style: TextStyle(color: Theme.of(context).colorScheme.tertiary)),
         )
        : SettingActionTile(
                  icon: null,
                  title: Text('Disable battery optimizations', style: TextStyle(color: Theme.of(context).colorScheme.error),),
                  description: Text('Disabling battery optimization is required for background updates to function properly', style: TextStyle(color: Theme.of(context).colorScheme.error)),
            onTap: () async {
              await BatteryOptimization.requestIgnoreBatteryOptimizations();
            },
        );
  }
}


  
