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
    this.styleTile = false,
    this.PrimarySwitch = false,
    this.errorTile = false,
  });

  /// The title of the setting section.
  final Widget? title;

  /// The list of setting tiles.
  final List<SettingTile> tiles;

  /// A divider displayed between the setting tiles.
  final Divider? divider;
  final bool styleTile;
  final bool PrimarySwitch;
  final bool errorTile;



Widget _wrapStyledTile(BuildContext context, Widget tile,
    {required bool isFirst, required bool isLast, required bool isOnly}) {
  final borderRadius = isOnly
      ? BorderRadius.circular(18)
      : isFirst
          ? const BorderRadius.only(
              topLeft: Radius.circular(18),
              topRight: Radius.circular(18),
              bottomLeft: Radius.circular(6),
              bottomRight: Radius.circular(6),
            )
          : isLast
              ? const BorderRadius.only(
                  topLeft: Radius.circular(6),
                  topRight: Radius.circular(6),
                  bottomLeft: Radius.circular(18),
                  bottomRight: Radius.circular(18),
                )
              : BorderRadius.circular(6);

  return Material(
    color: PrimarySwitch ? Theme.of(context).colorScheme.primaryContainer : errorTile ? Theme.of(context).colorScheme.errorContainer : Theme.of(context).colorScheme.surfaceContainerLowest,
    shape: RoundedRectangleBorder(borderRadius: borderRadius),
    clipBehavior: Clip.hardEdge,
    child: tile,
  );
}


  @override
  Widget build(BuildContext context) {
    return Padding(
  padding: EdgeInsets.fromLTRB(styleTile ? 10 : 0, 0, styleTile ? 10 : 0, 0),
  child: Column(
      spacing: styleTile ? 4 : 0,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (title != null) title!,
        for (final (index, tile) in tiles.indexed) ...[
          if (divider != null && index != 0) divider!,
      if (styleTile)
           _wrapStyledTile(
            context,
            tile,
            isFirst: index == 0,
            isLast: index == tiles.length - 1,
            isOnly: tiles.length == 1,
          )
        else
          tile,
        ],
      ],
      )
    );
  }
}
