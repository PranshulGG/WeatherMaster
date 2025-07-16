import 'package:flutter/material.dart';

/// Setting tile icon.
class SettingTileIcon extends StatelessWidget {
  /// A styled icon to display at the start of a setting tile.
  const SettingTileIcon(this.icon, {super.key});

  /// The icon to display.
  final IconData icon;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 48,
      height: double.infinity,
      child: Icon(icon, size: 32),
    );
  }
}
