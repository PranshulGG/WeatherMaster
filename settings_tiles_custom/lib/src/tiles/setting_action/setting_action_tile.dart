import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Action setting tile.
class SettingActionTile extends SettingTile {
  /// A setting tile that calls [onTap] when tapped.
  const SettingActionTile({
    required this.onTap,
    super.key,
    super.visible,
    super.enabled,
    super.icon,
    super.title,
    super.value,
    super.description,
    super.trailing,
  });

  /// Called when the tile is tapped.
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return tile(context, onTap: onTap);
  }
}
