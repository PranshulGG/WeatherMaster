import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';
import 'package:settings_tiles/src/tiles/widgets/setting_tile_icon.dart';

/// About setting tile.
class SettingAboutTile extends SettingTile {
  /// A setting tile that show the name and the version of the app,
  /// and opens the default Flutter's [AboutDialog] when tapped.
  SettingAboutTile({
    super.key,
    this.applicationName,
    this.applicationVersion,
    this.applicationIcon,
    this.applicationLegalese,
    this.dialogChildren,
  }) : super(
          title: applicationName != null ? Text(applicationName) : null,
          description:
              applicationVersion != null ? Text(applicationVersion) : null,
          icon: const SettingTileIcon(Icons.info),
        );

  /// The name of the application.
  final String? applicationName;

  /// The version of the application.
  final String? applicationVersion;

  /// The icon of the application displayed in Flutter's [AboutDialog]..
  final Widget? applicationIcon;

  /// The legalese of the application displayed in Flutter's [AboutDialog].
  final String? applicationLegalese;

  /// The list of widgets to display in Flutter's [AboutDialog]
  final List<Widget>? dialogChildren;

  void _showAboutDialog(BuildContext context) {
    showAboutDialog(
      context: context,
      applicationIcon: applicationIcon,
      applicationName: applicationName,
      applicationVersion: applicationVersion,
      applicationLegalese: applicationLegalese,
      children: dialogChildren,
    );
  }

  @override
  Widget build(BuildContext context) {
    return tile(context, onTap: () => _showAboutDialog(context));
  }
}
