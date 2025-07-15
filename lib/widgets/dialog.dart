import 'package:flutter/material.dart';

Future<void> showMatDialog({
  required BuildContext context,
  String title = 'Notice',
  Widget? content,
  String confirmText = 'OK',
  VoidCallback? onConfirm,
  String? cancelText,
  VoidCallback? onCancel,
}) {
  return showDialog<void>(
    context: context,
    builder: (BuildContext context) {
      return AlertDialog(
        title: Text(title),
        content: content,
        actions: <Widget>[
          if (cancelText != null)
            TextButton(
              child: Text(cancelText),
              onPressed: () {
                Navigator.of(context).pop();
                if (onCancel != null) onCancel();
              },
            ),
          TextButton(
            child: Text(confirmText),
            onPressed: () {
              Navigator.of(context).pop();
              if (onConfirm != null) onConfirm();
            },
          ),
        ],
      );
    },
  );
}

    //       showMatDialog(
    //   context: context,
    //   title: 'Delete Item',
    //   message: 'Are you sure you want to delete this item?',
    //   confirmText: 'Delete',
    //   cancelText: 'Cancel',
    //   onConfirm: () {
    //     
    //   },
    //   onCancel: () {
    //     
    //   },
    // );