// ignore_for_file: public_member_api_docs private class

import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/widgets/cancel_button.dart';
import 'package:settings_tiles/src/tiles/widgets/ok_button.dart';

class SettingSliderDialog extends StatefulWidget {
  const SettingSliderDialog({
    required this.title,
    required this.label,
    required this.min,
    required this.max,
    required this.divisions,
    required this.initialValue,
    required this.onChanged,
    super.key,
  });

  final String title;

  final String Function(double)? label;
  final double min;
  final double max;
  final int? divisions;
  final double initialValue;

  final void Function(double)? onChanged;

  @override
  State<SettingSliderDialog> createState() => _SettingSliderDialogState();
}

class _SettingSliderDialogState extends State<SettingSliderDialog> {
  late double _value;

  @override
  void initState() {
    super.initState();

    _value = widget.initialValue;
  }

  void _onChanged(double value) {
    setState(() {
      _value = value;
    });

    widget.onChanged?.call(value);
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog.adaptive(
      title: Text(widget.title),
      content: SingleChildScrollView(
        child: Slider(
          value: _value,
          label: widget.label != null
              ? widget.label!(_value)
              : _value.toStringAsFixed(2),
          min: widget.min,
          max: widget.max,
          divisions: widget.divisions,
          onChanged: _onChanged,
        ),
      ),
      actions: [
        const CancelButton(),
        OkButton(
          onPressed: () => Navigator.pop(context, _value),
        ),
      ],
    );
  }
}
