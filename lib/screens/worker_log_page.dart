import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../utils/preferences_helper.dart';
import 'package:easy_localization/easy_localization.dart';

class WorkInfoWidget extends StatefulWidget {
  @override
  _WorkInfoWidgetState createState() => _WorkInfoWidgetState();
}

class _WorkInfoWidgetState extends State<WorkInfoWidget> {
  static const platform = MethodChannel('com.pranshulgg.workinfo/channel');

  String _workInfo = 'Loading...';
  Map<String, dynamic>? _workInfoMap;

  @override
  void initState() {
    super.initState();
    _getWorkInfo();
  }

  Future<void> _getWorkInfo() async {
    try {
      final Map<dynamic, dynamic> result =
          await platform.invokeMethod('getWorkInfoSummary', {
        'uniqueWorkName': 'weather_update_work',
        'intervalMinutes':
            PreferencesHelper.getInt("savedRefreshInterval") ?? 90,
      });

      setState(() {
        if (result.containsKey('error')) {
          _workInfo = result['error'];
        } else {
          _workInfoMap = Map<String, dynamic>.from(result);
        }
      });
    } on PlatformException catch (e) {
      setState(() {
        _workInfo = "Failed to get work info: '${e.message}'.";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            title: Text('scheduled_updates'.tr()),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,
          ),
          SliverToBoxAdapter(
            child: Container(
              decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surfaceContainerLow,
                  borderRadius: BorderRadius.circular(18)),
              margin: const EdgeInsets.all(10),
              padding: const EdgeInsets.all(16.0),
              child: _workInfoMap == null
                  ? Text(_workInfo)
                  : Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: _workInfoMap!.entries.map((entry) {
                        if (entry.value is List) {
                          return Padding(
                            padding: const EdgeInsets.symmetric(vertical: 8.0),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Divider(),
                                Text(
                                  entry.key,
                                  style: TextStyle(
                                      fontWeight: FontWeight.bold,
                                      fontSize: 16,
                                      color: Theme.of(context)
                                          .colorScheme
                                          .tertiary),
                                ),
                                ...List<Widget>.from(
                                  (entry.value as List).map((tag) => Padding(
                                        padding: const EdgeInsets.only(
                                            left: 16.0, top: 4.0),
                                        child: Text("• $tag"),
                                      )),
                                ),
                                Divider()
                              ],
                            ),
                          );
                        } else if (entry.value is bool) {
                          return SizedBox.shrink();
                        } else {
                          return Padding(
                            padding: const EdgeInsets.symmetric(vertical: 4.0),
                            child: RichText(
                              text: TextSpan(
                                style: TextStyle(
                                    color: entry.value.toString() == "CANCELLED"
                                        ? Theme.of(context).colorScheme.error
                                        : Theme.of(context)
                                            .colorScheme
                                            .onSurface,
                                    fontSize: 16),
                                children: [
                                  TextSpan(
                                    text: "${entry.key}: ",
                                    style: TextStyle(
                                        fontWeight: FontWeight.w500,
                                        color: Theme.of(context)
                                            .colorScheme
                                            .primary),
                                  ),
                                  TextSpan(text: entry.value.toString()),
                                ],
                              ),
                            ),
                          );
                        }
                      }).toList(),
                    ),
            ),
          ),
        ],
      ),
    );
  }
}
