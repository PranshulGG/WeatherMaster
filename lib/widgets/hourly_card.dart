import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/icon_map.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';


class HourlyCard extends StatelessWidget {
  final List<dynamic> hourlyTime;
  final List<dynamic> hourlyTemps;
  final List<dynamic> hourlyWeatherCodes;
  final bool Function(DateTime) isHourDuringDaylightOptimized;
  final int selectedContainerBgIndex;
  final String timezone;
  final String utcOffsetSeconds;
  final List<dynamic> hourlyPrecpProb;


   

  const HourlyCard({super.key, 
   required this.hourlyTime, 
   required this.hourlyTemps, 
   required this.hourlyWeatherCodes,
   required this.isHourDuringDaylightOptimized, 
   required this.selectedContainerBgIndex,
   required this.timezone,
   required this.utcOffsetSeconds,
   required this.hourlyPrecpProb
   });

  @override
  Widget build(BuildContext context) {

    final offset = Duration(seconds: int.parse(utcOffsetSeconds));
    final nowUtc = DateTime.now().toUtc();
    final nowLocal = nowUtc.add(offset);

    final timeUnit = context.watch<UnitSettingsNotifier>().timeUnit;
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;


    final roundedNow = DateTime(nowLocal.year, nowLocal.month, nowLocal.day, nowLocal.hour);

    int startIndex = hourlyTime.indexWhere((timeStr) {
      final forecastLocal = DateTime.parse(timeStr); 
      return !forecastLocal.isBefore(roundedNow);
    });

    if (startIndex == -1) startIndex = 0;
      final scale = MediaQuery.of(context).textScaler.scale(1.0);
      final extraHeight = (scale - 1.0) * 30;

    return Container(
        decoration: BoxDecoration(
          color: Color(selectedContainerBgIndex),
          borderRadius: BorderRadius.circular(18),
        ),
        padding: EdgeInsets.only(top: 12, bottom: 0),
        margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
        child: Column(
        children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            SizedBox(width: 20,),
            Icon(Symbols.schedule, weight: 500, color: Theme.of(context).colorScheme.secondary, size: 20,),
            SizedBox(width: 5,),
           Text("hourly_forecast".tr(), style: TextStyle(fontWeight: FontWeight.w500, color: Theme.of(context).colorScheme.secondary, fontSize: 15)),
          ]
        ),
        Divider(height: 20, color: Theme.of(context).colorScheme.outlineVariant,),
       SizedBox(
        
        height: 98 + extraHeight + 10, 
        child: ListView.builder(
          scrollDirection: Axis.horizontal,
          physics: BouncingScrollPhysics(),
          itemCount: startIndex != null ? (48 - startIndex).clamp(0, 48) : 0,
          
          itemBuilder: (context, index) {
            // final time = DateTime.parse(hourlyTime[index]);
               final dataIndex = startIndex + index;

                if (dataIndex >= hourlyTime.length) return const SizedBox();

                
                final forecastLocal = DateTime.parse(hourlyTime[dataIndex]);


                final roundedDisplayTime = DateTime(
                  forecastLocal.year,
                  forecastLocal.month,
                  forecastLocal.day,
                  forecastLocal.hour,
                );

            

            final hour = timeUnit == '24 hr' ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00" : UnitConverter.formatTo12Hour(roundedDisplayTime);
            final temp = tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(hourlyTemps[dataIndex].toDouble()).round() : hourlyTemps[dataIndex].toDouble().round(); 
            final code = hourlyWeatherCodes[dataIndex];
            final precipProb = hourlyPrecpProb[dataIndex] ?? 0.1111111;


            final isDay = isHourDuringDaylightOptimized(roundedDisplayTime);
            
            return Container(
              width: 65,
              
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  Text("${temp}°", style: TextStyle(fontSize: 16, color: Theme.of(context).colorScheme.onSurface, fontWeight: FontWeight.w500)),
                  const SizedBox(height: 10),
                SvgPicture.asset(
                  WeatherIconMapper.getIcon(code, isDay ? 1 : 0),
                  width: 26,
                ),
                  Text(precipProb == 0.1111111 ? '--%' : precipProb > 10 ? "${precipProb.round()}%" : "0%", style: TextStyle(fontSize: 12, color: Theme.of(context).colorScheme.primary, fontWeight: FontWeight.w700)),
                  
                  Text(hour, style: TextStyle(fontSize: 14, color: Theme.of(context).colorScheme.onSurfaceVariant, fontWeight: FontWeight.w500)),
                ],
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