import 'package:flutter/material.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/condition_label_map.dart';

class PollenCard extends StatelessWidget {
  final Map<String, dynamic> pollenData;
  final int selectedContainerBgIndex;

  const PollenCard({
    super.key,
    required this.pollenData,
    required this.selectedContainerBgIndex,
  });

  @override
  Widget build(BuildContext context) {
    final double? alderPollen = pollenData['alder_pollen'];
    final double? birchPollen = pollenData['birch_pollen'];
    final double? grassPollen = pollenData['grass_pollen'];
    final double? mugwortPollen = pollenData['mugwort_pollen'];
    final double? olivePollen = pollenData['olive_pollen'];
    final double? ragweedPollen = pollenData['ragweed_pollen'];

    Widget buildPollenSection({
      required String title,
      required Icon titleIcon,
      required double? pollenSum,
    }) {
      if (pollenSum == null) return const SizedBox.shrink();

      final pollenLevel = getPollenLevel(pollenSum, context);

      return Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Text("Level: ${pollenLevel['level']}"),
              // const SizedBox(width: 8),
              // Text("Severity: ${pollenLevel['fraction']}"),
              // const SizedBox(width: 8),
              // Icon(pollenLevel['icon'], size: 20),
              Stack(
                clipBehavior: Clip.none,
                alignment: Alignment.center,
                children: [
                  Positioned(
                    child: SvgPicture.string(
                      pollenLevel['icon'],
                      width: 105,
                    ),
                  ),
                  Positioned(
                      child: Column(
                    children: [
                      titleIcon,
                      Text(title, style: TextStyle(fontSize: 13))
                    ],
                  )),
                  Positioned(
                    bottom: 6,
                    child: Text(
                      "${pollenLevel['fraction']}",
                      style: TextStyle(
                          fontWeight: FontWeight.w500,
                          color: Theme.of(context).colorScheme.primary),
                    ),
                  ),
                  Positioned(
                    bottom: -14,
                    child: Text(
                      "${pollenLevel['level']}",
                      style: TextStyle(
                          fontWeight: FontWeight.w500,
                          color: Theme.of(context).colorScheme.onSurface),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      );
    }

    return Container(
      decoration: BoxDecoration(
        color: Color(selectedContainerBgIndex),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.15),
            spreadRadius: 1,
            blurRadius: 1,
            offset: Offset(0, 1),
          ),
        ],
      ),
      padding: EdgeInsets.only(top: 15, bottom: 15),
      margin: EdgeInsets.fromLTRB(13, 0, 13, 0),
      child: Column(
        children: [
          Row(crossAxisAlignment: CrossAxisAlignment.center, children: [
            SizedBox(
              width: 20,
            ),
            Icon(
              Symbols.allergy,
              weight: 500,
              color: Theme.of(context).colorScheme.secondary,
              size: 21,
              fill: 1,
            ),
            SizedBox(
              width: 5,
            ),
            Text("pollen".tr(),
                style: TextStyle(
                    color: Theme.of(context).colorScheme.secondary,
                    fontWeight: FontWeight.w600,
                    fontSize: 16)),
          ]),
          Divider(
            height: 14,
            color: Colors.transparent,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (_isPollenDataAvailable(
                  [alderPollen, birchPollen, olivePollen]))
                buildPollenSection(
                  title: "tree".tr(),
                  titleIcon: Icon(Symbols.nature),
                  pollenSum: alderPollen! + birchPollen! + olivePollen!,
                ),
              if (_isPollenDataAvailable([grassPollen]))
                buildPollenSection(
                  title: "grass".tr(),
                  titleIcon: Icon(Symbols.grass),
                  pollenSum: grassPollen,
                ),
              if (_isPollenDataAvailable([mugwortPollen, ragweedPollen]))
                buildPollenSection(
                  title: "weed".tr(),
                  titleIcon: Icon(Symbols.psychiatry),
                  pollenSum: mugwortPollen! + ragweedPollen!,
                ),
              if (!_isPollenDataAvailable([
                alderPollen,
                birchPollen,
                olivePollen,
                grassPollen,
                mugwortPollen,
                ragweedPollen,
              ]))
                const Text(
                  "No pollen data available.",
                  style: TextStyle(color: Colors.grey),
                ),
            ],
          ),
          SizedBox(
            height: 10,
          )
        ],
      ),
    );
  }

  // Helper to calculate pollen level
  Map<String, dynamic> getPollenLevel(double count, context) {
    if (count < 20) {
      return {
        'fraction': "1/4",
        'level': "low_pollen".tr(),
        'icon': '''
            <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="transparent" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="#${Theme.of(context).colorScheme.surfaceContainerHighest.toARGB32().toRadixString(16).padLeft(8, '0').substring(2)}" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="#0ec70e" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C16.29,127.98 8.99,110.39 8.09,91.87C7.2,73.35 12.76,55.14 23.76,40.32C25.08,38.54 27.61,38.3 29.32,39.71V39.71C31.02,41.11 31.26,43.63 29.95,45.41C20.2,58.69 15.28,74.95 16.08,91.49C16.89,108.02 23.36,123.73 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
            ''',
      };
    } else if (count < 50) {
      return {
        'fraction': "2/4",
        'level': "medium_pollen".tr(),
        'icon': '''
            <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="transparent" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="#${Theme.of(context).colorScheme.surfaceContainerHighest.toARGB32().toRadixString(16).padLeft(8, '0').substring(2)}" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="#e0b700" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C55.6,13.43 69.58,8.82 84,8.1C86.21,7.99 88,9.79 88,12V12C88,14.21 86.21,15.99 84,16.11C71.17,16.82 58.73,20.96 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
            ''',
      };
    } else if (count < 100) {
      return {
        'fraction': "3/4",
        'level': "high_pollen".tr(),
        'icon': '''
            <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="#${Theme.of(context).colorScheme.surface.toARGB32().toRadixString(16).padLeft(8, '0').substring(2)}" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="transparent" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="orange" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C15.4,127 8,107.88 8,88C8,66.78 16.43,46.43 31.43,31.43C46.43,16.43 66.78,8 88,8C107.88,8 127,15.4 141.67,28.67C143.31,30.16 143.3,32.7 141.74,34.26V34.26C140.18,35.82 137.65,35.81 136.01,34.34C122.84,22.56 105.76,16 88,16C68.9,16 50.59,23.59 37.09,37.09C23.59,50.59 16,68.9 16,88C16,105.76 22.56,122.84 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
            ''',
      };
    } else {
      return {
        'fraction': "4/4",
        'level': "Severe".tr(),
        'icon': '''
            <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="transparent" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="#${Theme.of(context).colorScheme.surfaceContainerHighest.toARGB32().toRadixString(16).padLeft(8, '0').substring(2)}" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="red" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C20.18,132.27 14.01,120.98 10.73,108.71C7.09,95.14 7.09,80.86 10.73,67.29C14.36,53.73 21.5,41.36 31.43,31.43C41.36,21.5 53.73,14.36 67.29,10.73C80.86,7.09 95.14,7.09 108.71,10.73C122.27,14.36 134.64,21.5 144.57,31.43C154.5,41.36 161.64,53.73 165.27,67.29C168.56,79.57 168.88,92.43 166.21,104.82C165.75,106.98 163.54,108.24 161.41,107.67V107.67C159.28,107.1 158.02,104.91 158.47,102.74C160.78,91.72 160.47,80.28 157.55,69.36C154.28,57.16 147.85,46.03 138.91,37.09C129.98,28.15 118.84,21.72 106.64,18.45C94.43,15.18 81.57,15.18 69.36,18.45C57.16,21.72 46.03,28.15 37.09,37.09C28.15,46.03 21.72,57.16 18.45,69.36C15.18,81.57 15.18,94.43 18.45,106.64C21.38,117.55 26.83,127.61 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
            ''',
      };
    }
  }

  bool _isPollenDataAvailable(List<double?> values) {
    return values.every((value) => value != null);
  }
}
