import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/widgets/setting_tile_icon.dart';
import 'package:settings_tiles/src/tiles/widgets/setting_tile_value.dart';

/// A setting tile.
abstract class SettingTile extends StatelessWidget {
  /// A setting tile displays information about a setting and handles the
  /// necessary interactions to modify it.
  ///
  /// It can be hidden by setting [visible] to `false`, and disabled by setting
  /// [enabled] to `false`.
  const SettingTile({
    super.key,
    this.visible = true,
    this.enabled = true,
    this.icon,
    this.value,
    this.title,
    this.description,
    this.trailing,
  });

  /// Whether the tile is visible.
  final bool visible;

  /// Whether the tile is enabled.
  final bool enabled;

  /// The icon of the tile.
  ///
  /// Use the [SettingTileIcon] class to have the icon automatically styled.
  final Widget? icon;

  /// The title of the tile.
  final Widget? title;

  /// The current value of the setting.
  ///
  /// If set, it is displayed between the title and the description.
  ///
  /// Use the [SettingTileValue] class to have the text automatically styled.
  final Widget? value;

  /// The description of the title.
  final Widget? description;

  /// An optional widget to show at the end of the tile.
  final Widget? trailing;

  /// The tile to display.
  Widget tile(BuildContext context, {GestureTapCallback? onTap}) {
    if (!visible) {
      return const SizedBox.shrink();
    }

    return ListTile(
      contentPadding: const EdgeInsets.only(right: 16, left: 16),
      enabled: enabled,
      leading: icon,
      title: title,
      subtitle: description != null || value != null
          ? Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                if (value != null) value!,
                if (description != null) description!,
              ],
            )
          : null,
      trailing: trailing,
      onTap: onTap,
    );
  }
}
