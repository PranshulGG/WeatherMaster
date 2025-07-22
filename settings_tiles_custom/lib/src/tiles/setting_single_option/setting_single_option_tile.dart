import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_single_option/setting_single_option_dialog.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';
import 'package:settings_tiles/src/types/multiple_options_details.dart';

/// Single option setting tile.
class SettingSingleOptionTile<T extends Object> extends SettingTile {
  /// A setting tile with a single option that can be selected.
  ///
  /// The title of the tile in the dialog is the value of the `toString()`
  /// method for each option. No subtitle is displayed. To specify them, use
  /// [SettingSingleOptionTile.detailed] instead.
  SettingSingleOptionTile({
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
    this.initialOption,
    this.onCanceled,
  }) : options = options
            .map(
              (option) =>
                  (value: option, title: option.toString(), subtitle: null),
            )
            .toList();

  /// A setting tile with a single option that can be selected.
  ///
  /// This constructor allows to specify the title and an optional subtitle for
  /// each option.
  const SettingSingleOptionTile.detailed({
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
    this.initialOption,
    this.onCanceled,
  });

  /// The title of the dialog.
  final String dialogTitle;

  /// The list of options.
  final List<MultipleOptionsDetails> options;

  /// The initial option that is selected.
  final T? initialOption;

  /// Called when the option is chosen.
  final void Function(T) onSubmitted;

  /// Called when the dialog is canceled.
  final VoidCallback? onCanceled;

  Future<void> _openDialog(BuildContext context) async {
    final result = await showAdaptiveDialog<T>(
      context: context,
      useRootNavigator: false,
      builder: (context) {
        return SettingSingleOptionDialog(
          title: dialogTitle,
          options: options,
          initialOption: initialOption,
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
