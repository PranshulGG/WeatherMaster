import 'package:easy_localization/easy_localization.dart';
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

    if (!isShowFrog) {
      return const SizedBox.shrink();
    }

    final url = iconUrl!;

    if (url.startsWith('http')) {
      return Image.network(
        url,
        loadingBuilder: (context, child, loadingProgress) {
          if (loadingProgress == null) return child;
          return Text("loading_text".tr());
        },
      );
    }

    return Image.asset(url);
  }
}
