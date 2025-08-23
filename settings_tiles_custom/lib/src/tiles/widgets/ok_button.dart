// ignore_for_file: public_member_api_docs private class

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

class OkButton extends StatelessWidget {
  const OkButton({
    required this.onPressed,
    super.key,
  });

  final VoidCallback? onPressed;

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: onPressed,
      child: Text('ok'.tr(),
          style: const TextStyle(fontVariations: [
            FontVariation('wght', 700),
            FontVariation('ROND', 100),
          ], fontSize: 16)),
    );
  }
}
