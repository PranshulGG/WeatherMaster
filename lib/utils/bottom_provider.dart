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
    decorationThickness: context.locale.languageCode == "en" ? 1.3 : 3,
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

void showAddBottomSheet(
    BuildContext context, String lat, String lon, city, country) {
  final colorTheme = Theme.of(context).colorScheme;

  showModalBottomSheet(
    context: context,
    isScrollControlled: true,
    showDragHandle: true,
    backgroundColor: colorTheme.surfaceContainerLow,
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(
        top: Radius.circular(28),
      ),
    ),
    builder: (context) => Padding(
      padding: EdgeInsets.only(
          left: 16,
          right: 16,
          bottom: MediaQuery.of(context).padding.bottom + 10),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          locationInfoItem(context, "City", city),
          Divider(),
          SizedBox(
            height: 5,
          ),
          locationInfoItem(context, "Country", country),
          Divider(),
          SizedBox(
            height: 5,
          ),
          locationInfoItem(context, "Latitude", lat.toString()),
          Divider(),
          SizedBox(
            height: 5,
          ),
          locationInfoItem(context, "Longitude", lon.toString()),
        ],
      ),
    ),
  );
}

Widget locationInfoItem(BuildContext context, label, String data) {
  final colorTheme = Theme.of(context).colorScheme;

  return Row(
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Text(
        label,
        style: TextStyle(fontSize: 17, color: colorTheme.onSurface),
      ),
      Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 3),
        decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(5),
            color: colorTheme.secondaryContainer),
        child: Text(data,
            style: TextStyle(
                fontSize: 14, color: colorTheme.onSecondaryContainer)),
      ),
    ],
  );
}
