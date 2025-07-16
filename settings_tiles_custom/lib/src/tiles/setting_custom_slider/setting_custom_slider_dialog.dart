// ignore_for_file: public_member_api_docs private class

import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/widgets/cancel_button.dart';
import 'package:settings_tiles/src/tiles/widgets/ok_button.dart';

class SettingCustomSliderDialog extends StatefulWidget {
  const SettingCustomSliderDialog({
    required this.title,
    required this.label,
    required this.values,
    required this.initialValue,
    required this.onChanged,
    super.key,
  });

  final String title;

  final String Function(double)? label;
  final List<double> values;
  final double initialValue;

  final void Function(double)? onChanged;

  @override
  State<SettingCustomSliderDialog> createState() =>
      _SettingCustomSliderDialogState();
}

class _SettingCustomSliderDialogState extends State<SettingCustomSliderDialog> {
  /// The index of the current value.
  late int _index;

  /// The current value.
  double get _value => widget.values[_index];

  @override
  void initState() {
    super.initState();

    final valueIndex = widget.values.indexOf(widget.initialValue);

    assert(
      valueIndex != -1,
      'The initial value of the discrete slider is not allowed: '
      '${widget.initialValue} is not in ${widget.values}.',
    );

    _index = valueIndex;
  }

  void _onChanged(double value) {
    setState(() {
      _index = value.toInt();
    });

    widget.onChanged?.call(value);
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog.adaptive(
      title: Text(widget.title),
      content: SingleChildScrollView(
        child: Slider(
          value: _index.toDouble(),
          label: widget.label != null
              ? widget.label!(_value)
              : _value.toStringAsFixed(2),
          max: widget.values.length - 1,
          divisions: widget.values.length - 1,
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
