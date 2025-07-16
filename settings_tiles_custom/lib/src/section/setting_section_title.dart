import 'package:flutter/material.dart';
import 'package:settings_tiles/settings_tiles.dart';

/// Setting section title.
class SettingSectionTitle extends StatelessWidget {
  /// A styled title of a [SettingSection].
  const SettingSectionTitle(this.title, {super.key});

  /// The title to display.
  final String title;

  @override
  Widget build(BuildContext context) {

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Text(
        title,
        style:
            TextStyle(
              fontWeight: FontWeight.w500,
              fontSize: 16,
              color: Theme.of(context).colorScheme.primary,
            ),
      ),
    );
  }
}
