import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:hive/hive.dart';
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../utils/unit_converter.dart';
import 'package:provider/provider.dart';
import '../notifiers/unit_settings_notifier.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/icon_map.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../widgets/hourly_card.dart';


class DailyForecastPage extends StatefulWidget {
  final DateTime? initialSelectedDate;
    const DailyForecastPage({super.key, this.initialSelectedDate});

  @override
  State<DailyForecastPage> createState() => _DailyForecastPageState();

}

class _DailyForecastPageState extends State<DailyForecastPage> {
  late Future<Map<String, dynamic>?> weatherFuture = getWeatherFromCache();
  Map<String, dynamic>? weatherData;
  DateTime? selectedDate;

  @override
  void initState() {
    super.initState();
    selectedDate = widget.initialSelectedDate;
  }

  Future<Map<String, dynamic>?> getWeatherFromCache() async {

  final prefs = await SharedPreferences.getInstance();
  final jsonString = prefs.getString('currentLocation');
  if (jsonString != null) {
    final jsonMap = json.decode(jsonString);
    final box = await Hive.openBox('weatherMasterCache');
    final cached = box.get(jsonMap['cacheKey']);
    if (cached == null) return null;
    return json.decode(cached);
  }
}


  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("daily_forecast".tr()),
          titleSpacing: 0,
          scrolledUnderElevation: 0,
        ),
        body: SingleChildScrollView(
       child: FutureBuilder<Map<String, dynamic>?>(
        future: weatherFuture,
        builder: (context, snapshot) {
      if (snapshot.connectionState == ConnectionState.waiting) {
        return const SizedBox.shrink();
      }
    if (!snapshot.hasData) return const Text("No data");

    weatherData ??= snapshot.data!['data'];

             final weather = weatherData!;

            final daily = weather['daily'];
            final List<dynamic> dailyDates = daily['time'];       
            final List<dynamic> sunriseTimes = daily['sunrise'];   
            final List<dynamic> sunsetTimes = daily['sunset'];    
            final List<dynamic> dailyTempsMin = daily['temperature_2m_min'];
            final List<dynamic> dailyTempsMax = daily['temperature_2m_max'];
            final List<dynamic> dailyPrecProb = daily['precipitation_probability_max'];
            final List<dynamic> dailyWeatherCodes = daily['weather_code'];

            final hourly = weather['hourly'];
            final List<dynamic> hourlyTime = hourly['time'];
            final List<dynamic> hourlyTemps = hourly['temperature_2m'];
            final List<dynamic> hourlyWeatherCodes = hourly['weather_code'];
            final List<dynamic> hourlyPrecpProb = hourly['precipitation_probability'];

            final Map<String, (DateTime, DateTime)> daylightMap = {
              for (int i = 0; i < dailyDates.length; i++)
                dailyDates[i]: (DateTime.parse(sunriseTimes[i]), DateTime.parse(sunsetTimes[i])),
            };

            bool isHourDuringDaylightOptimized(DateTime hourTime) {
              final key = "${hourTime.year.toString().padLeft(4, '0')}-${hourTime.month.toString().padLeft(2, '0')}-${hourTime.day.toString().padLeft(2, '0')}";
              final times = daylightMap[key];
              if (times != null) {
                return hourTime.isAfter(times.$1) && hourTime.isBefore(times.$2);
              }
              return true; 
            }


            int selectedIndex = -1;
            for (int i = 0; i < dailyDates.length; i++) {
              final date = DateTime.parse(dailyDates[i]);
              if (isSameDay(date, selectedDate)) {
                selectedIndex = i;
                break;
              }
            }

            final selectedDayData = selectedIndex != -1
                ? {
                    'date': dailyDates[selectedIndex],
                    'minTemp': dailyTempsMin[selectedIndex],
                    'maxTemp': dailyTempsMax[selectedIndex],
                    'weatherCode': dailyWeatherCodes[selectedIndex],
                  }
                : null;

            return Column(
                children: [
              DailyForecastCard(dailyTime: dailyDates, 
                dailyTempsMin: dailyTempsMin, 
                dailyWeatherCodes: dailyWeatherCodes, 
                dailyTempsMax: dailyTempsMax,
                onDaySelected: (date) {
                setState(() {
                  selectedDate = date;
                });
              },
                timezone: weather['timezone'].toString(),
                utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                selectedDate: selectedDate,
            ),
            ForecastDetailsHeader(
                selectedDayData: selectedDayData,
            ),
            if (selectedDate != null) ...[
              const SizedBox(height: 12),
              isSameDay(selectedDate!, DateTime.now().toUtc().add(Duration(seconds: int.parse(weather['utc_offset_seconds'].toString()))))
              ? HourlyCard(
                hourlyTime: hourlyTime,
                hourlyTemps: hourlyTemps,
                hourlyWeatherCodes: hourlyWeatherCodes,
                isHourDuringDaylightOptimized: isHourDuringDaylightOptimized,
                selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLow.toARGB32(),
                timezone: weather['timezone'].toString(),
                utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                hourlyPrecpProb: hourlyPrecpProb,
            )
          : HourlyCardForecast(
            selectedDate: selectedDate!, 
            hourlyTime: hourlyTime,
            hourlyTemps: hourlyTemps,
            hourlyWeatherCodes: hourlyWeatherCodes,
            isHourDuringDaylightOptimized: isHourDuringDaylightOptimized,
            selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLow.toARGB32(),
            timezone: weather['timezone'].toString(),
            utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
            hourlyPrecpProb: hourlyPrecpProb,
            ),
          ],

          

            ],
            );
          }
        )
      )
    );
  }
}



bool isSameDay(DateTime? a, DateTime? b) {
  if (a == null || b == null) return false;
  return a.year == b.year && a.month == b.month && a.day == b.day;
}


class DailyForecastCard extends StatelessWidget {
  final List<dynamic> dailyTime;
  final List<dynamic> dailyTempsMin;
  final List<dynamic> dailyWeatherCodes;
  final List<dynamic> dailyTempsMax;
  final String timezone;
  final String utcOffsetSeconds;
  final void Function(DateTime selectedDate)? onDaySelected;
  final DateTime? selectedDate;


  const DailyForecastCard({super.key, 
   required this.dailyTime, 
   required this.dailyTempsMin, 
   required this.dailyWeatherCodes, 
   required this.dailyTempsMax, 
   this.onDaySelected,
   required this.timezone,
   required this.utcOffsetSeconds,
   this.selectedDate,
  });

  @override
  Widget build(BuildContext context) {




    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;

    num convert(num celsius) => tempUnit == "Fahrenheit"
        ? UnitConverter.celsiusToFahrenheit(celsius.toDouble()).round()
        : celsius.round();

    return  Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(18),
        ),
        padding: EdgeInsets.only(top: 12, bottom: 10),
        child: Column(
        children: [

       SizedBox(
        height: 165,
        child: ListView.separated(
          scrollDirection: Axis.horizontal,
          physics:BouncingScrollPhysics() ,
          itemCount: dailyTime.length,
          separatorBuilder: (context, index) => const SizedBox(width: 5),
          itemBuilder: (context, index) {
            final time = DateTime.parse(dailyTime[index]);
            final tempMax = convert(dailyTempsMax[index].toDouble());
            final tempMin = convert(dailyTempsMin[index].toDouble());
            final code = dailyWeatherCodes[index];

              EdgeInsets itemMargin = EdgeInsets.only(
              left: index == 0 ? 10 : 0,
              right: index == dailyTime.length - 1 ? 10 : 0,

            );


            final isSelected = selectedDate != null &&
                              time.year == selectedDate!.year &&
                              time.month == selectedDate!.month &&
                              time.day == selectedDate!.day;


            
            return GestureDetector(
              onTap: () => onDaySelected?.call(time),
              child:   Container(
              width: 68,
              margin: itemMargin,
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(99),
                color: isSelected
                    ? Theme.of(context).colorScheme.primary
                    : Theme.of(context).colorScheme.surfaceContainerLow
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  SizedBox(height: 6,),
                  Column(
                    children: [
                  Text("${tempMax.round()}°", style: TextStyle(fontSize: 16, color: isSelected ? Theme.of(context).colorScheme.onPrimary : Theme.of(context).colorScheme.onSurface, fontWeight: FontWeight.w500)),
                  Text("${tempMin.round()}°", style: TextStyle(fontSize: 16, color: isSelected ? Theme.of(context).colorScheme.onPrimaryFixedVariant : Theme.of(context).colorScheme.onSurfaceVariant, fontWeight: FontWeight.w500)),
                    ],
                  ),
                SvgPicture.asset(
                  WeatherIconMapper.getIcon(code, 1),
                  width: 35,
                ),
                  Column(
                children: [
                  Text(
                _getDayLabel(time, index, utcOffsetSeconds),
                  style: TextStyle(fontSize: 15, fontWeight: FontWeight.w500, color: isSelected ? Theme.of(context).colorScheme.onPrimary : Theme.of(context).colorScheme.onSurface),
              ),
                ]
              ),
                  SizedBox(height: 6,),
                ],
                ),  
                ),  
              );
            },
          ),  
        ),  
        ]
      ),  
    );
  }
}


String _getDayLabel(DateTime date, int index, utcOffsetSeconds) {
  // final now = DateTime.now();
      int offsetSeconds = int.parse(utcOffsetSeconds.toString());
    DateTime utcNow = DateTime.now().toUtc();
    DateTime now = utcNow.add(Duration(seconds: offsetSeconds));


    now = DateTime(
      now.year,
      now.month,
      now.day,
      now.hour,
      now.minute,
      now.second,
      now.millisecond,
      now.microsecond,
    );


  final today = DateTime(now.year, now.month, now.day);
  final target = DateTime(date.year, date.month, date.day);

  if (target == today) {
    return "Today";
  } else {
    return DateFormat('E').format(date); 
  }
}


class HourlyCardForecast extends StatelessWidget {
  final DateTime selectedDate;
  final List<dynamic> hourlyTime;
  final List<dynamic> hourlyTemps;
  final List<dynamic> hourlyWeatherCodes;
  final bool Function(DateTime) isHourDuringDaylightOptimized;
  final int selectedContainerBgIndex;
  final String timezone;
  final String utcOffsetSeconds;
  final List<dynamic> hourlyPrecpProb;


   

  const HourlyCardForecast({super.key, 
  required this.selectedDate,
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

    final localSelectedDate = selectedDate.toUtc().add(offset);

    final roundedSelectedDate = DateTime(localSelectedDate.year, localSelectedDate.month, localSelectedDate.day);

    int startIndex = hourlyTime.indexWhere((timeStr) {
      final forecastLocal = DateTime.parse(timeStr);
      return forecastLocal.isAtSameMomentAs(roundedSelectedDate) || forecastLocal.isAfter(roundedSelectedDate);
    });

    if (startIndex == -1) startIndex = 0;

    return Container(
        decoration: BoxDecoration(
          color: Color(selectedContainerBgIndex),
          borderRadius: BorderRadius.circular(18),
          // boxShadow: [
          //     BoxShadow(
          //     color: Colors.black.withOpacity(0.1),
          //     blurRadius: 3,
          //     offset: Offset(0, 1),
          //   ),
          //   BoxShadow(
          //     color: Colors.black.withOpacity(0.06),
          //     blurRadius: 6,
          //     offset: Offset(0, 2),
          //   ),
          // ]
        ),
        padding: EdgeInsets.only(top: 12, bottom: 10),
        margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
        child: Column(
        children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            SizedBox(width: 20,),
            Icon(Symbols.schedule, weight: 500, color: Theme.of(context).colorScheme.secondary, size: 20,),
            SizedBox(width: 5,),
           Text("hourly_forecast".tr(), style: TextStyle(fontWeight: FontWeight.w500, color: Theme.of(context).colorScheme.secondary, fontSize: 15)),
          ]
        ),
        Divider(height: 20, color: Theme.of(context).colorScheme.outlineVariant,),
       SizedBox(
        height: 98,
        child: ListView.builder(
          scrollDirection: Axis.horizontal,
          physics: BouncingScrollPhysics(),
          itemCount: startIndex,
          
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

            final hour = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr' ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00" : UnitConverter.formatTo12Hour(roundedDisplayTime);
            final temp = context.watch<UnitSettingsNotifier>().tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(hourlyTemps[dataIndex].toDouble()).round() : hourlyTemps[dataIndex].toDouble().round(); 
            final code = hourlyWeatherCodes[dataIndex];
            final precipProb = hourlyPrecpProb[dataIndex];


            final isDay = isHourDuringDaylightOptimized(roundedDisplayTime);
            
            return Container(
              width: 70,
              
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
                  Text(precipProb > 10 ? "${precipProb.round()}%" : "0%", style: TextStyle(fontSize: 12, color: Theme.of(context).colorScheme.primary, fontWeight: FontWeight.w700)),
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


// header


class ForecastDetailsHeader extends StatelessWidget {
  final Map<String, dynamic>? selectedDayData;

  const ForecastDetailsHeader({
    Key? key,
    required this.selectedDayData,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
  if (selectedDayData == null) {
      return const Text('No data selected.');
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Padding(padding: EdgeInsets.only(left: 12, right: 12),
        child: Text(formatDateDetailes(selectedDayData!['date']), style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 24, fontWeight: FontWeight.w500),)),
        Padding(padding: EdgeInsets.only(left: 12, right: 12),
        child: Row(children: [Text(selectedDayData!['maxTemp'], style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 24, fontWeight: FontWeight.w500))],)),
        // Text("Min Temp: ${selectedDayData!['minTemp']}°C"),
        // Text("Max Temp: ${selectedDayData!['maxTemp']}°C"),
        // Text("Weather Code: ${selectedDayData!['weatherCode']}"),
      ],
    );
  }
}

String formatDateDetailes(String dateStr) {
  final date = DateTime.parse(dateStr);
  final formatter = DateFormat('d MMMM');
  return formatter.format(date);
}