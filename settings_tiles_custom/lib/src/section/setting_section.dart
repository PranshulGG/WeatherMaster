import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Setting section.
class SettingSection extends StatelessWidget {
  /// A setting section with a [title] and a list of settings [tiles].
  ///
  /// An optional [divider] can be displayed between the tiles.
  const SettingSection({
    required this.tiles,
    super.key,
    this.title,
    this.divider,
  });

  /// The title of the setting section.
  final Widget? title;

  /// The list of setting tiles.
  final List<SettingTile> tiles;

  /// A divider displayed between the setting tiles.
  final Divider? divider;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (title != null) title!,
        for (final (index, tile) in tiles.indexed) ...[
          if (divider != null && index != 0) divider!,
          tile,
        ],
      ],
    );
  }
}
