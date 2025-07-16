import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_text_field/setting_text_field_dialog.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Text field setting tile.
class SettingTextFieldTile extends SettingTile {
  /// A setting tile with a text field to enter some text.
  const SettingTextFieldTile({
    required this.dialogTitle,
    required this.onSubmitted,
    super.key,
    super.visible,
    super.enabled,
    super.icon,
    super.title,
    super.value,
    super.description,
    super.trailing,
    this.initialText,
    this.onCanceled,
  });

  /// The title of the dialog.
  final String dialogTitle;

  /// The initial text displayed in the text field.
  final String? initialText;

  /// Called when the text is submitted.
  final void Function(String) onSubmitted;

  /// Called when the dialog is canceled.
  final void Function()? onCanceled;

  Future<void> _openDialog(BuildContext context) async {
    final result = await showAdaptiveDialog<String>(
      context: context,
      useRootNavigator: false,
      builder: (context) {
        return SettingTextFieldDialog(
          title: dialogTitle,
          initialText: initialText,
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
