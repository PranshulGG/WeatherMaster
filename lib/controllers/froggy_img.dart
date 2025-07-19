import 'package:flutter/material.dart';
import '../notifiers/unit_settings_notifier.dart';
import 'package:provider/provider.dart';

class WeatherFrogIconWidget extends StatelessWidget {
  final String? iconUrl;

  const WeatherFrogIconWidget({super.key, required this.iconUrl});

  @override
  Widget build(BuildContext context) {
    if (iconUrl == null) {
      return const Text("");
    }
    
    final isShowFrog = context.read<UnitSettingsNotifier>().showFrog;

    return isShowFrog ? iconUrl!.startsWith('http')
        ? Image.network(
            iconUrl!,
            loadingBuilder: (context, child, loadingProgress) {
              if (loadingProgress == null) return child;
              return const Text("Loading...");
            },
          )
        : Image.asset(iconUrl!) : SizedBox.shrink() ;
  }
}