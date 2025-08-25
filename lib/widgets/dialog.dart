import 'package:flutter/material.dart';

Future<void> showMatDialog({
  required BuildContext context,
  String title = 'Notice',
  Widget? content,
  IconData? icon,
  String confirmText = 'OK',
  VoidCallback? onConfirm,
  String? cancelText,
  VoidCallback? onCancel,
}) {
  return showDialog<void>(
    context: context,
    builder: (BuildContext context) {
      return AlertDialog.adaptive(
        title: Text(title),
        icon: icon == null ? null : Icon(icon),
        content: content,
        actions: <Widget>[
          if (cancelText != null)
            TextButton(
              child: Text(
                cancelText,
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
              ),
              onPressed: () {
                Navigator.of(context).pop();
                if (onCancel != null) onCancel();
              },
            ),
          TextButton(
            child: Text(confirmText,
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
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