import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'open_links.dart';
import '../utils/condition_label_map.dart';

Widget homeBottomBar(BuildContext context, bool isLight) {
  final textColor = isLight ? Colors.black : Colors.white;
  final commonTextStyle = TextStyle(
    color: textColor,
    fontSize: 18,
    decoration: TextDecoration.underline,
    decorationThickness: context.locale.languageCode == "en" ? 2 : 3,
  );

  return RepaintBoundary(
    child: Padding(
      padding: EdgeInsets.only(
        bottom: MediaQuery.of(context).padding.bottom + 10,
        top: 16,
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          GestureDetector(
            onTap: () => openLink("https://open-meteo.com/"),
            child: Text("Open-Meteo", style: commonTextStyle),
          ),
          const SizedBox(width: 4),
          Text("•", style: TextStyle(color: textColor, fontSize: 18)),
          const SizedBox(width: 4),
          GestureDetector(
            onTap: () => openLink("https://www.weatherapi.com/"),
            child: Text("Weatherapi", style: commonTextStyle),
          ),
        ],
      ),
    ),
  );
}
