import 'package:flutter/material.dart';

/// Setting tile value.
class SettingTileValue extends StatelessWidget {
  /// A styled value to display in a setting tile between the title and the
  /// description.
  const SettingTileValue(this.value, {this.enabled = true, super.key});

  /// Whether the setting tile is enabled.
  final bool enabled;

  /// The value to display.
  final String value;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Text(
      value,
      style: theme.textTheme.bodyMedium
          ?.copyWith(color: enabled ? theme.colorScheme.tertiary : theme.disabledColor, fontWeight: FontWeight.w500),
    );
  }
}
