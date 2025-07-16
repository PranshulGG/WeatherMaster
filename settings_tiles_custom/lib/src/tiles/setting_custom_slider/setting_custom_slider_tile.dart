import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_custom_slider/setting_custom_slider_dialog.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Custom setting slider tile.
class SettingCustomSliderTile extends SettingTile {
  /// A setting tile with a slider to choose a value between a set of custom
  /// [values].
  const SettingCustomSliderTile({
    required this.dialogTitle,
    required this.values,
    required this.initialValue,
    required this.onSubmitted,
    super.key,
    super.visible,
    super.enabled,
    super.icon,
    super.title,
    super.value,
    super.description,
    super.trailing,
    this.label,
    this.onChanged,
    this.onCanceled,
  });

  /// The title of the dialog.
  final String dialogTitle;

  /// The label of the slider.
  ///
  /// The [value] is the current value of the slider.
  ///
  /// If `null`, the [value] of the slider is displayed directly with a 2 digits
  /// precision.
  final String Function(double value)? label;

  /// The list of discrete values allowed in the slider.
  final List<double> values;

  /// The initial value that the slider is set to.
  final double initialValue;

  /// Called when the slider value is changed.
  final void Function(double)? onChanged;

  /// Called when the slider value is chosen.
  final void Function(double) onSubmitted;

  /// Called when the dialog is canceled.
  final VoidCallback? onCanceled;

  Future<void> _openDialog(BuildContext context) async {
    final result = await showAdaptiveDialog<double>(
      context: context,
      useRootNavigator: false,
      builder: (context) {
        return SettingCustomSliderDialog(
          title: dialogTitle,
          label: label,
          values: values,
          initialValue: initialValue,
          onChanged: onChanged,
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
