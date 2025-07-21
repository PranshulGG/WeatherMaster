import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import '../utils/icon_map.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../screens/daily_forecast.dart';

class DailyCard extends StatelessWidget {
  final List<dynamic> dailyTime;
  final List<dynamic> dailyTempsMin;
  final List<dynamic> dailyWeatherCodes;
  final List<dynamic> dailyTempsMax;
  final List<dynamic> dailyPrecProb;
  final int selectedContainerBgIndex;


   DailyCard({super.key, 
   required this.dailyTime, 
   required this.dailyTempsMin, 
   required this.dailyWeatherCodes, 
   required this.dailyTempsMax, 
   required this.dailyPrecProb, 
   required this.selectedContainerBgIndex});

  @override
  Widget build(BuildContext context) {

    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;

    num convert(num celsius) => tempUnit == "Fahrenheit"
        ? UnitConverter.celsiusToFahrenheit(celsius.toDouble()).round()
        : celsius.round();

        final List<Map<String, dynamic>> validDailyData = [];

  for (int i = 0; i < dailyTime.length; i++) {
    if (i < dailyTempsMin.length &&
        i < dailyTempsMax.length &&
        i < dailyWeatherCodes.length &&
        i < dailyPrecProb.length &&
        dailyTime[i] != null &&
        dailyTempsMin[i] != null &&
        dailyTempsMax[i] != null &&
        dailyWeatherCodes[i] != null) {
      validDailyData.add({
        "time": dailyTime[i],
        "tempMin": dailyTempsMin[i],
        "tempMax": dailyTempsMax[i],
        "weatherCode": dailyWeatherCodes[i],
        "precipProb": (dailyPrecProb[i] as num?)?.toDouble() ?? 0.0000001,
      });
    }
  }


    return Container(
        decoration: BoxDecoration(
          color: Color(selectedContainerBgIndex),
          borderRadius: BorderRadius.circular(18),
          
        ),
        padding: EdgeInsets.only(top: 12, bottom: 10),
        margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
        child: Column(
        children: [
        Row(
          children: [
            SizedBox(width: 20,),
            Icon(Symbols.calendar_today, weight: 500, color: Theme.of(context).colorScheme.secondary, size: 20,),
            SizedBox(width: 5,),
           Text("daily_forecast".tr(), style: TextStyle(fontWeight: FontWeight.w500, color: Theme.of(context).colorScheme.secondary, fontSize: 15)),
          ]
        ),
        Divider(height: 20, color: Theme.of(context).colorScheme.outlineVariant,),
       SizedBox(
        height: 190,
        child: ListView.separated(
          scrollDirection: Axis.horizontal,
          physics:BouncingScrollPhysics() ,
          itemCount: validDailyData.length,
          separatorBuilder: (context, index) => const SizedBox(width: 5),
          itemBuilder: (context, index) {
              final item = validDailyData[index];
              final time = DateTime.parse(item["time"]);
              final tempMax = convert(item["tempMax"]);
              final tempMin = convert(item["tempMin"]);
              final code = item["weatherCode"];
              final precipProb = item["precipProb"];


          EdgeInsets itemMargin = EdgeInsets.only(
              left: index == 0 ? 10 : 0,
              right: index == dailyTime.length - 1 ? 10 : 0,

            );
            
            return GestureDetector(
            onTap: () {
              Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (_) => DailyForecastPage(
                    initialSelectedDate: time,
                  ),
                ),
              );
            },
           child:  Container(
              width: 68,
              margin: itemMargin,
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(99),
                color: Theme.of(context).brightness == Brightness.light ? Theme.of(context).colorScheme.surfaceContainer : Color.fromRGBO(0, 0, 0, 0.247)
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  SizedBox(height: 10,),
                  Column(
                    children: [
                  Text("${tempMax.round()}°", style: TextStyle(fontSize: 16, color: Theme.of(context).colorScheme.onSurface, fontWeight: FontWeight.w500)),
                  Text("${tempMin.round()}°", style: TextStyle(fontSize: 16, color: Theme.of(context).colorScheme.onSurfaceVariant, fontWeight: FontWeight.w500)),
                    ],
                  ),
                SvgPicture.asset(
                  WeatherIconMapper.getIcon(code, 1),
                  width: 35,
                ),
                  Column(
                children: [
                  Text(precipProb == 0.0000001 ? '--' : "${precipProb.round()}%", style: TextStyle(fontSize: 14, color: Theme.of(context).colorScheme.primary, fontWeight: FontWeight.w500)),
                  SizedBox(height: 5,),
                  Text(getLocalizedDateFormat(time, Localizations.localeOf(context)), style: const TextStyle(fontSize: 14), textAlign: TextAlign.center,),
                ]
              ),
                  SizedBox(height: 10,),
                ],
              ),
            ),
            );
          },
        ),
      ),
      ],
      ),
    );
  }
}

String getLocalizedDateFormat(DateTime time, Locale locale) {
  final lang = locale.languageCode;
  final country = locale.countryCode;

  if (lang == 'en' && country == 'US') {
    return DateFormat('MM/dd').format(time); 
  } else if (lang == 'ja') {
    return DateFormat('MM月dd日', 'ja').format(time); 
  } else if (lang == 'zh' && country == 'CN') {
    return DateFormat('MM月dd日', 'zh_CN').format(time); 
  } else if (lang == 'fa') {
    return DateFormat('yyyy/MM/dd', 'fa').format(time); 
  } else if (lang == 'de') {
    return DateFormat('dd.MM').format(time);
  }

  return DateFormat('dd/MM').format(time);
}