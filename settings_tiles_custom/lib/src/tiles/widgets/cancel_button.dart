// ignore_for_file: public_member_api_docs private class

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

class CancelButton extends StatelessWidget {
  const CancelButton({super.key});

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: () => Navigator.pop(context),
      child: Text('cancel'.tr(),
          style: const TextStyle(fontVariations: [
            FontVariation('wght', 700),
            FontVariation('ROND', 100),
          ], fontSize: 16)),
    );
  }
}
