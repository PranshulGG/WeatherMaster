import 'package:flutter/material.dart';

class SnackUtil {
  static void showSnackBar({
    required BuildContext context,
    required String message,
    Duration duration = const Duration(seconds: 3),
    String? actionLabel,
    VoidCallback? onActionPressed,
  }) {

    final snackBar = SnackBar(
      content: Text(
        message,
      ),
      duration: duration,
      action: (actionLabel != null && onActionPressed != null)
          ? SnackBarAction(
              label: actionLabel,
              onPressed: onActionPressed,
            )
          : null,
    );

    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(snackBar);
  }
}
