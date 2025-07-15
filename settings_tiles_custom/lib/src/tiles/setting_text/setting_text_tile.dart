import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Text setting tile.
class SettingTextTile extends SettingTile {
  /// A setting tile that only displays text with no interactions.
  const SettingTextTile({
    super.key,
    super.visible,
    super.icon,
    super.title,
    super.value,
    super.description,
    super.trailing,
  });

  @override
  Widget build(BuildContext context) {
    return tile(context);
  }
}
