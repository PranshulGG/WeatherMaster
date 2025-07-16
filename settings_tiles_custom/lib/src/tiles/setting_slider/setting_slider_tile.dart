import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_slider/setting_slider_dialog.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Slider setting tile.
class SettingSliderTile extends SettingTile {
  /// A setting tile with a slider to choose a value between a [min] value, a
  /// [max] value with a number of [divisions].
  const SettingSliderTile({
    required this.dialogTitle,
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
    this.min = 0.0,
    this.max = 1.0,
    this.divisions,
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

  /// The minimum value that can be selected in the slider.
  final double min;

  /// The maximum value that can be selected in the slider.
  final double max;

  /// The number of divisions of the slider between the [min] and the [max].
  final int? divisions;

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
        return SettingSliderDialog(
          title: dialogTitle,
          label: label,
          min: min,
          max: max,
          divisions: divisions,
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
