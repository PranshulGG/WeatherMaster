import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_multiple_options/setting_multiple_options_dialog.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';
import 'package:settings_tiles/src/types/multiple_options_details.dart';

/// Multiple options setting tile.
class SettingMultipleOptionsTile<T extends Object> extends SettingTile {
  /// A setting tile with multiple options that can be checked.
  ///
  /// The title of the tile in the dialog is the value of the `toString()`
  /// method for each option. No subtitle is displayed. To specify them, use
  /// [SettingMultipleOptionsTile.detailed] instead.
  SettingMultipleOptionsTile({
    required this.dialogTitle,
    required List<T> options,
    required this.onSubmitted,
    super.key,
    super.visible,
    super.enabled,
    super.icon,
    super.title,
    super.value,
    super.description,
    super.trailing,
    this.initialOptions = const [],
    this.minOptions = 0,
    this.onCanceled,
  }) : options = options
            .map(
              (option) =>
                  (value: option, title: option.toString(), subtitle: null),
            )
            .toList();

  /// A setting tile with multiple options that can be checked.
  ///
  /// This constructor allows to specify the title and an optional subtitle for
  /// each option.
  const SettingMultipleOptionsTile.detailed({
    required this.dialogTitle,
    required this.options,
    required this.onSubmitted,
    super.key,
    super.visible,
    super.enabled,
    super.icon,
    super.title,
    super.value,
    super.description,
    super.trailing,
    this.initialOptions = const [],
    this.minOptions = 0,
    this.onCanceled,
  });

  /// The title of the dialog.
  final String dialogTitle;

  /// The list of options to choose from.
  final List<MultipleOptionsDetails> options;

  /// The initial options that are checked.
  final List<T> initialOptions;

  /// The minimal number of options that need to be selected.
  final int minOptions;

  /// Called when the options are chosen.
  final void Function(List<T>) onSubmitted;

  /// Called when the dialog is canceled.
  final VoidCallback? onCanceled;

  Future<void> _openDialog(BuildContext context) async {
    final result = await showAdaptiveDialog<List<T>>(
      context: context,
      useRootNavigator: false,
      builder: (context) {
        return SettingMultipleOptionsDialog(
          title: dialogTitle,
          options: options,
          initialOptions: initialOptions,
          minOptions: minOptions,
        );
      },
    );

    if (result == null) {
      if (onCanceled == null) {
        return;
      } else {
        onCanceled!();
      }
    } else {
      return onSubmitted(result);
    }
  }

  @override
  Widget build(BuildContext context) {
    return tile(context, onTap: enabled ? () => _openDialog(context) : null);
  }
}
