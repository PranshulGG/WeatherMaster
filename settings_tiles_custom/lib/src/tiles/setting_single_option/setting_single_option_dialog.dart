// ignore_for_file: public_member_api_docs private class

import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/widgets/cancel_button.dart';
import 'package:settings_tiles/src/tiles/widgets/ok_button.dart';
import 'package:settings_tiles/src/types/multiple_options_details.dart';

class SettingSingleOptionDialog<T extends Object> extends StatefulWidget {
  const SettingSingleOptionDialog({
    required this.title,
    required this.options,
    required this.initialOption,
    super.key,
  });

  final String title;

  final List<MultipleOptionsDetails> options;
  final T? initialOption;

  @override
  State<SettingSingleOptionDialog<T>> createState() =>
      _SettingSingleOptionDialogState<T>();
}

class _SettingSingleOptionDialogState<T extends Object>
    extends State<SettingSingleOptionDialog<T>> {
  late T? _selectedOption;

  @override
  void initState() {
    super.initState();

    _selectedOption = widget.initialOption;
  }

  bool _isSelected(T option) {
    return _selectedOption == option;
  }

  void _onChanged(T? option) {
    if (option == null) {
      return;
    }

    setState(() {
      _selectedOption = option;
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
            return RadioListTile<T>(
              dense: true,
              value: value,
              groupValue: _selectedOption,
              title: Text(title, style: const TextStyle(fontSize: 15)),
              subtitle: subtitle != null ? Text(subtitle) : null,
              selected: _isSelected(value),
              onChanged: (selected) => _onChanged(value),
            );
          }).toList(),
        ),
      ),
      actions: [
        const CancelButton(),
        OkButton(
          onPressed: () => Navigator.pop(context, _selectedOption),
        ),
      ],
    );
  }
}
