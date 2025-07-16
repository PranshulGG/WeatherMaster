import 'package:settings_tiles/settings_tiles.dart'
    show SettingMultipleOptionsTile;
import 'package:settings_tiles/src/tiles/setting_multiple_options/setting_multiple_options_tile.dart'
    show SettingMultipleOptionsTile;

/// A record containing the `value`, the `title` and an optional `subtitle` of
/// an option of a [SettingMultipleOptionsTile].
typedef MultipleOptionsDetails = ({
  Object value,
  String title,
  String? subtitle,
});
