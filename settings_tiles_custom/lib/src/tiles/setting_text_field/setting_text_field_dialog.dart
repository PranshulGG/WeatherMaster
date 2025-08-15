// ignore_for_file: public_member_api_docs private class

import 'package:flutter/material.dart';
import 'package:settings_tiles/src/tiles/widgets/cancel_button.dart';
import 'package:settings_tiles/src/tiles/widgets/ok_button.dart';

class SettingTextFieldDialog extends StatefulWidget {
  const SettingTextFieldDialog({
    required this.title,
    required this.initialText,
    super.key,
  });

  final String title;

  final String? initialText;

  @override
  State<SettingTextFieldDialog> createState() => _SettingTextFieldDialogState();
}

class _SettingTextFieldDialogState extends State<SettingTextFieldDialog> {
  late TextEditingController _textEditingController;

  @override
  void initState() {
    super.initState();

    _textEditingController = TextEditingController(text: widget.initialText);
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog.adaptive(
      title: Text(widget.title),
      content: Padding(
        padding: const EdgeInsets.all(0),
        child: SingleChildScrollView(
          child: TextField(
            controller: _textEditingController,
            autofocus: true,
            decoration: InputDecoration(
              border: OutlineInputBorder(),
            ),
          ),
        ),
      ),
      actions: [
        const CancelButton(),
        OkButton(
          onPressed: () => Navigator.pop(context, _textEditingController.text),
        ),
      ],
    );
  }
}
