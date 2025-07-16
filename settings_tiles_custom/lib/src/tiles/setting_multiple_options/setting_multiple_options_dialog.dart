// ignore_for_file: public_member_api_docs private class

import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/widgets/cancel_button.dart';
import 'package:settings_tiles/src/tiles/widgets/ok_button.dart';
import 'package:settings_tiles/src/types/multiple_options_details.dart';

class SettingMultipleOptionsDialog<T extends Object> extends StatefulWidget {
  const SettingMultipleOptionsDialog({
    required this.title,
    required this.options,
    required this.initialOptions,
    required this.minOptions,
    super.key,
  });

  final String title;

  final List<MultipleOptionsDetails> options;
  final List<T> initialOptions;
  final int minOptions;

  @override
  State<SettingMultipleOptionsDialog<T>> createState() =>
      _SettingMultipleOptionsDialogState<T>();
}

class _SettingMultipleOptionsDialogState<T extends Object>
    extends State<SettingMultipleOptionsDialog<T>> {
  late List<T> _selectedOptions;

  @override
  void initState() {
    super.initState();

    // Create a new list so it can be modified
    _selectedOptions = List.from(widget.initialOptions);
  }

  bool get canSubmit {
    return _selectedOptions.length >= widget.minOptions;
  }

  bool _isSelected(T option) {
    return _selectedOptions.contains(option);
  }

  void _onChanged(T option, bool? selected) {
    if (selected == null) {
      return;
    }

    setState(() {
      selected ? _selectedOptions.add(option) : _selectedOptions.remove(option);
    });
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog.adaptive(
      contentPadding: const EdgeInsets.symmetric(vertical: 16),
      title: Text(widget.title),
      content: SingleChildScrollView(
        child: ListBody(
          children: widget.options.map((option) {
            final (:value as T, :title, :subtitle) = option;

            return CheckboxListTile(
              value: _isSelected(value),
              title: Text(title),
              subtitle: subtitle != null ? Text(subtitle) : null,
              selected: _isSelected(value),
              onChanged: (selected) => _onChanged(value, selected),
            );
          }).toList(),
        ),
      ),
      actions: [
        const CancelButton(),
        OkButton(
          onPressed:
              canSubmit ? () => Navigator.pop(context, _selectedOptions) : null,
        ),
      ],
    );
  }
}
