import 'package:flex_color_picker/flex_color_picker.dart';
import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/setting_color/color_preview.dart';
import 'package:settings_tiles/src/tiles/setting_tile.dart';

/// Color setting tile.
class SettingColorTile extends SettingTile {
  /// A setting tile with configurable color pickers and a current color
  /// preview.
  ///
  /// By default, the two color pickers available are [ColorPickerType.primary]
  /// that shows primary colors, and [ColorPickerType.accent] that shows accent
  /// colors. To enable all the color pickers, pass [ColorPickerType.values] to
  /// the [colorPickers] parameter.
  SettingColorTile({
    required this.dialogTitle,
    required this.onSubmitted,
    super.key,
    super.visible,
    super.enabled,
    super.icon,
    super.title,
    super.value,
    super.description,
    this.colorPickers = const [ColorPickerType.primary, ColorPickerType.accent],
    this.enableOpacity = true,
    this.initialColor = Colors.white,
    this.onCanceled,
  }) : super(
          trailing: ColorPreview(color: initialColor),
        );

  /// The title of the dialog.
  final String dialogTitle;

  /// The list of color pickers available.
  final List<ColorPickerType> colorPickers;

  /// Whether to enable the slider to change the opacity of the picked color.
  ///
  /// If set to `false`, the color picked will always have 100% opacity
  /// (meaning no transparency).
  final bool enableOpacity;

  /// The initial color that should be selected in the color pickers and should
  /// be previewed at the end of the tile.
  final Color initialColor;

  /// Called when the color is picked.
  final void Function(Color) onSubmitted;

  /// Called when the dialog is canceled.
  final VoidCallback? onCanceled;

  Future<void> _openDialog(BuildContext context) async {
    final colorPickersMap = {
      for (final colorPickerType in ColorPickerType.values)
        colorPickerType: colorPickers.contains(colorPickerType),
    };

    await ColorPicker(
      title: Padding(
        padding: const EdgeInsets.only(bottom: 8),
        child: Text(
          dialogTitle,
          style: Theme.of(context).textTheme.headlineMedium,
        ),
      ),
      pickersEnabled: colorPickersMap,
      enableOpacity: enableOpacity,
      color: initialColor,
      onColorChanged: onSubmitted,
    ).showPickerDialog(context);
  }

  @override
  Widget build(BuildContext context) {
    return tile(context, onTap: enabled ? () => _openDialog(context) : null);
  }
}
