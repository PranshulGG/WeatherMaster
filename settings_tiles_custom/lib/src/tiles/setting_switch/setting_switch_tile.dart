import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Switch setting tile.
class SettingSwitchTile extends SettingTile {
  /// A setting tile with a checkbox that can be toggled and untoggled.
  SettingSwitchTile({
    required this.toggled,
    required this.onChanged,
    super.key,
    super.visible,
    super.enabled,
    super.icon,
    super.title,
    super.value,
    super.description,
  }) : super(
          trailing: Switch(
            value: toggled,
            onChanged: enabled ? onChanged : null,
          ),
        );

  /// Whether the switch it toggled.
  final bool toggled;

  /// Called when the status of the switch is changed.
  ///
  /// If `null`, the tile will be disabled.
  // ignore: avoid_positional_boolean_parameters
  final void Function(bool toggled)? onChanged;

  @override
  Widget build(BuildContext context) {
    return tile(
      context,
      onTap: enabled && onChanged != null ? () => onChanged!(!toggled) : null,
    );
  }
}
