// ignore_for_file: public_member_api_docs private class

import 'package:flutter/material.dart';

class CancelButton extends StatelessWidget {
  const CancelButton({super.key});

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: () => Navigator.pop(context),
      child:
          Text(Localizations.of<MaterialLocalizations>(context, MaterialLocalizations)?.cancelButtonLabel ?? 'Cancel',
              style: const TextStyle(fontVariations: [
                FontVariation('wght', 600),
                FontVariation('ROND', 100),
              ], fontSize: 16)),
    );
  }
}
