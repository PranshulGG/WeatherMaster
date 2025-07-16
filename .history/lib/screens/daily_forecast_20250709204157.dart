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
import '../widgets/currentConditions_card.dart';
import '../utils/preferences_helper.dart';
import 'package:flutter_compass/flutter_compass.dart';
import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;
import 'dart:math';

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

             final current = weather['current'];

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

                final selectedDay = DateTime.parse(dailyDates[selectedIndex]);

final List<int> selectedDayHourIndexes = [];

for (int i = 0; i < hourlyTime.length; i++) {
  final date = DateTime.parse(hourlyTime[i]);
  if (date.year == selectedDay.year &&
      date.month == selectedDay.month &&
      date.day == selectedDay.day) {
    selectedDayHourIndexes.add(i);
  }
}

final int firstIndex = selectedDayHourIndexes.isNotEmpty ? selectedDayHourIndexes.first : 0;

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
            Divider(),
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

          SizedBox(height: 10,),
          isSameDay(selectedDate!, DateTime.now().toUtc().add(Duration(seconds: int.parse(weather['utc_offset_seconds'].toString()))))?
                    SizedBox(
              height: 800,
              width: 380,
              child: ConditionsWidgets(
                selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLow.toARGB32(),
                currentHumidity: current['relative_humidity_2m'],
                currentDewPoint: hourly['dew_point_2m'][0].toDouble(),
                currentSunrise: daily['sunrise'][0],
                currentSunset: daily['sunset'][0],
                currentPressure: current['pressure_msl'],
                currentVisibility: hourly['visibility'][0],
                currentWindSpeed: current['wind_speed_10m'],
                currentWindDirc: current['wind_direction_10m'],
                timezone: weather['timezone'].toString(),
                utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                currentUvIndex: hourly['uv_index'][0],
                currentAQIUSA: weather['air_quality']['current']['us_aqi'],
                currentAQIEURO: weather['air_quality']['current']['european_aqi'],
                currentTotalPrec: daily['precipitation_sum'][0],
              ),
            )
              :
            SizedBox(
              height: 600,
              width: 380,
              child: ConditionsWidgetsForecast(
              selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLow.toARGB32(),
              currentSunrise: daily['sunrise']?[selectedIndex] ?? '',
              currentSunset: daily['sunset']?[selectedIndex] ?? '',
              currentPressure: hourly['pressure_msl']?[selectedIndex] ?? 0,
              currentVisibility: hourly['visibility']?[firstIndex] ?? 0,
              currentWindSpeed: daily['wind_speed_10m_max']?[selectedIndex] ?? 0,
              currentWindDirc: daily['wind_direction_10m']?[selectedIndex] ?? 0,
              timezone: weather['timezone'].toString(),
              utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
              currentUvIndex: daily['uv_index_max']?[selectedIndex] ?? 0,
              currentTotalPrec: daily['precipitation_sum']?[selectedIndex] ?? 0,
              currentTotalPrecProb: daily['precipitation_probability_max']?[selectedIndex] ?? 0,
            )
            ),
              SizedBox(height: MediaQuery.of(context).padding.bottom,)
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

String getCityFromPreferences() {
  final locationStr = PreferencesHelper.getString("currentLocation");
  final locationJson = jsonDecode(locationStr.toString());
  return locationJson['city'].toString();
}

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
        Padding(padding: EdgeInsets.only(left: 12, right: 12, top: 24),
        child: Text(formatDateDetailes(selectedDayData!['date']), style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 18, fontWeight: FontWeight.w700),)),
        Padding(padding: EdgeInsets.only(left: 12, right: 12, top: 3),
        child: Text(getCityFromPreferences(), style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 22, fontWeight: FontWeight.w500),)),
        Padding(padding: EdgeInsets.only(left: 12, right: 12),
        child: Row(children: [Text(context.watch<UnitSettingsNotifier>().tempUnit == 'Fahrenheit' ? "${UnitConverter.celsiusToFahrenheit(selectedDayData!['maxTemp']).round()}°/" : "${selectedDayData!['maxTemp'].round()}°/", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 56, fontWeight: FontWeight.w500)), 
        Text(context.watch<UnitSettingsNotifier>().tempUnit == 'Fahrenheit' ? "${UnitConverter.celsiusToFahrenheit(selectedDayData!['minTemp']).round()}°" : "${selectedDayData!['minTemp'].round()}°", style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 56, fontWeight: FontWeight.w500)),
        SizedBox(width: 10,),
        SvgPicture.asset(
            WeatherIconMapper.getIcon(selectedDayData!['weatherCode'], 1),
            width: 50,
          ),
        ],)),
    
      ],
    );
  }
}


String formatDateDetailes(String dateStr) {
  final date = DateTime.parse(dateStr);
  final formatter = DateFormat('MMMM, d');
  return formatter.format(date);
}

// widgets for forecast

class ConditionsWidgetsForecast extends StatelessWidget {
  final int selectedContainerBgIndex;
  final  String currentSunrise;
  final  String currentSunset;
  final  double currentPressure;
  final  double currentVisibility;
  final  double currentWindSpeed;
  final  int currentWindDirc;
  final  String timezone;
  final  String utcOffsetSeconds;
  final  double currentUvIndex;
  final  double currentTotalPrec;
  final int currentTotalPrecProb;

  const ConditionsWidgetsForecast({super.key, 
  required this.selectedContainerBgIndex, 
  required this.currentSunrise,
  required this.currentSunset,
  required this.currentPressure,
  required this.currentVisibility,
  required this.currentWindSpeed,
  required this.currentWindDirc,
  required this.timezone,
  required this.utcOffsetSeconds,
  required this.currentUvIndex,
  required this.currentTotalPrec,
  required this.currentTotalPrecProb
  });

  @override
  Widget build(BuildContext context) {

    // DateTime now = DateTime.now();
    int offsetSeconds = int.parse(utcOffsetSeconds);
    DateTime utcNow = DateTime.now().toUtc();
    DateTime now = utcNow.add(Duration(seconds: offsetSeconds));
    DateTime sunrise = DateTime.parse(currentSunrise);
    DateTime sunset = DateTime.parse(currentSunset);


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

    final sunriseFormat = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr' ? DateFormat.Hm().format(sunrise) : DateFormat.jm().format(sunrise);
    final sunsetFormat = context.watch<UnitSettingsNotifier>().timeUnit == '24 hr' ? DateFormat.Hm().format(sunset) : DateFormat.jm().format(sunset);

    final convertedPressure = context.watch<UnitSettingsNotifier>().pressureUnit == 'inHg'
        ? UnitConverter.hPaToInHg(currentPressure)
        : context.watch<UnitSettingsNotifier>().pressureUnit == 'mmHg'
            ? UnitConverter.hPaToMmHg(currentPressure)
            : currentPressure;

    final convertedPrecip = context.watch<UnitSettingsNotifier>().precipitationUnit == 'cm'
        ? UnitConverter.mmToCm(currentTotalPrec)
        : context.watch<UnitSettingsNotifier>().precipitationUnit == 'in'
            ? UnitConverter.mmToIn(currentTotalPrec)
            : currentTotalPrec;


    final convertedVisibility = context.watch<UnitSettingsNotifier>().visibilityUnit == 'Mile'
        ? UnitConverter.mToMiles(currentVisibility.toDouble()) : UnitConverter.mToKm(currentVisibility.toDouble());


    if (sunset.isBefore(sunrise)) {
      sunset = sunset.add(Duration(days: 1));
    }

    double percent = ((now.difference(sunrise).inSeconds) / (sunset.difference(sunrise).inSeconds)).clamp(0, 1);

        final convertedwindSpeed = context.watch<UnitSettingsNotifier>().windUnit == 'Mph'
        ? UnitConverter.kmhToMph(currentWindSpeed)
        : context.watch<UnitSettingsNotifier>().windUnit == 'M/s'
            ? UnitConverter.kmhToMs(currentWindSpeed)
            : currentWindSpeed.toStringAsFixed(0);

    return Container(
      margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
      child: Column(
          children: [
            Expanded(
              child: GridView.count(
                crossAxisCount: 2,
                  physics: const NeverScrollableScrollPhysics(),
                  shrinkWrap: true,
                  padding: EdgeInsets.only(top: 8.5),
                  crossAxisSpacing: 16,
                  mainAxisSpacing: 10,
                children: [
                  Container(
                    decoration: BoxDecoration(
                    color: Color(selectedContainerBgIndex),
                    borderRadius: BorderRadius.circular(20),
                  ),
                   child: Stack(   
                    children: [
                    ListTile(
                        leading: Icon(Symbols.wb_twilight, weight: 500, fill: 1, color: Theme.of(context).brightness == Brightness.dark ? Colors.white : Colors.black,),
                        horizontalTitleGap: 5,
                        contentPadding: EdgeInsets.only(left: 10, bottom: 0),
                        title: Text("sun_tile_page".tr(), style: TextStyle(color: Theme.of(context).brightness == Brightness.dark ? Colors.white : Colors.black), maxLines: 1, overflow: TextOverflow.ellipsis),
                      ),
                    Positioned(
                      bottom: -4,
                      left: 0,
                      right: 0,
                        child: SvgPicture.string(
                        buildsunPath(Theme.of(context).colorScheme.primary),
                      ),
                    ),
                Positioned(
                      left: 0,
                      bottom: -1,
                      right: 0,
                      child: Container(
                      height: 70,
                        decoration: BoxDecoration(
                            border: Border(
                            top: BorderSide(
                              color: Theme.of(context).colorScheme.outline, // Or any color you need
                              width: 1.5,
                            ),
                          ),
                          color: const Color.fromRGBO(0, 0, 0, 0.5),
                          borderRadius: BorderRadius.only(bottomLeft: Radius.circular(20), bottomRight: Radius.circular(20))
                        ),
                      ),
                    ),
                        Align(
                        alignment: Alignment.bottomCenter,
                         child: Column(
                          mainAxisSize: MainAxisSize.min,
                          crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                              spacing: 5,
                              mainAxisSize: MainAxisSize.min,
                                children: [
                                  Icon(Symbols.vertical_align_top, weight: 500, size: 17, color: Colors.white,),
                                  Text(sunriseFormat, style: TextStyle(color: Colors.white, fontWeight: FontWeight.w500, fontSize: 13,))
                                ],
                              ),
                              Row(
                              spacing: 5,
                              mainAxisSize: MainAxisSize.min,
                                children: [
                                  Icon(Symbols.vertical_align_bottom, weight: 500, size: 17, color: Colors.white,),
                                  Text(sunsetFormat, style: TextStyle(color: Colors.white, fontWeight: FontWeight.w500, fontSize: 13))
                                ],
                              ),
                              SizedBox(height: 10,)
                            ],
                          )
                        ),
                    ],
                  ),
                  ),
                

                Stack(
                  children: [
                    SvgPicture.string(buildPressueSvg(Theme.of(context).colorScheme.primary, Theme.of(context).colorScheme.surfaceContainerHigh, Color(selectedContainerBgIndex), currentPressure.round())),

                headerWidgetConditions(headerText: "pressure".tr(), headerIcon: Symbols.compress,),

                Align(
                  alignment: Alignment.center,
                  child: Text("${convertedPressure.round()}", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 34, fontWeight: FontWeight.w500),),
                ),
                Padding(
                  padding: EdgeInsets.only(bottom: 30),
               child: Align(
                  alignment: Alignment.bottomCenter,
                  child: Text(context.watch<UnitSettingsNotifier>().pressureUnit, style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 18),),
                ),
                ),
                ]
              ),

                  ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: Container(
                    height: 160,
                    decoration: BoxDecoration(
                      color: Color(selectedContainerBgIndex),
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Stack(
                      children: [
                        SvgPicture.string(
                          buildVisibilitySvg(Theme.of(context).colorScheme.tertiaryContainer),
                          fit: BoxFit.cover, 
                        ),
                headerWidgetConditions(headerText: "visibility".tr(), headerIcon: Symbols.visibility,),
          
                Align(
                  alignment: Alignment.center,
                  child: Text("${convertedVisibility.round()}", style: TextStyle(color: Theme.of(context).colorScheme.onTertiaryContainer, fontSize: 40, fontWeight: FontWeight.w500),),
                ),
                Padding(
                  padding: EdgeInsets.only(bottom: 30),
               child: Align(
                  alignment: Alignment.bottomCenter,
                  child: Text(context.watch<UnitSettingsNotifier>().visibilityUnit, style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 18),),
                ),
                ),
                ],
                ),
                ),
                ),
              ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: Container(
                    height: 160,
                    decoration: BoxDecoration(
                      color: Color(selectedContainerBgIndex),
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Stack(
                      children: [
              WindCompassWidget(currentWindDirc: currentWindDirc, backgroundColor: Color(selectedContainerBgIndex)),

                    headerWidgetConditions(headerText: "direction".tr(), headerIcon: Symbols.explore,),
                    Align(
                      alignment: Alignment.center,
                      child: Text(getCompassDirection(currentWindDirc), style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 34, fontWeight: FontWeight.w500),),
                    ),

                 Align(
                  alignment: Alignment.bottomCenter,
                  child:  Container(
                  margin: EdgeInsets.only(left: 20, right: 20),
                  height: 55,
                  child: Text("$convertedwindSpeed ${context.watch<UnitSettingsNotifier>().windUnit}", style: 
                  TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 16), maxLines: 1, overflow: TextOverflow.ellipsis, textAlign: TextAlign.center,),
                ),
                ),
              ]
                )      
                 )      
                ),      

                
                  ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: Container(
                    height: 160,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Stack(
                      children: [
                        SvgPicture.string(
                          buildUVSvg(Color(selectedContainerBgIndex), currentUvIndex.round()),
                          fit: BoxFit.cover, 
                        ),
                       headerWidgetConditions(headerText: "uv_index".tr(), headerIcon: Symbols.light_mode,),
          
                Align(
                  alignment: Alignment.center,
                  child: Text("${currentUvIndex.round()}", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 40, fontWeight: FontWeight.w500),),
                ),
                Padding(
                  padding: EdgeInsets.only(bottom: 40),
               child: Align(
                  alignment: Alignment.bottomCenter,
                  child: Text(getUvIndexType(currentUvIndex.round()), style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 15),),
                ),
                ),
                ],
                ),
                ),
                ),

                Container(
                    decoration: BoxDecoration(
                    color: Color(selectedContainerBgIndex),
                    borderRadius: BorderRadius.circular(20),
                  ),
                   child: Stack(   
                    children: [
                    ListTile(
                        leading: Icon(Symbols.rainy_heavy, weight: 500, fill: 1, color: Theme.of(context).brightness == Brightness.dark ? Colors.white : Colors.black,),
                        horizontalTitleGap: 5,
                        contentPadding: EdgeInsets.only(left: 10, bottom: 0),
                        title: Text("precipitation".tr(), style: TextStyle(color: Theme.of(context).brightness == Brightness.dark ? Colors.white : Colors.black), maxLines: 1, overflow: TextOverflow.ellipsis),
                      ),
                      Align(
                        alignment: Alignment.centerLeft,
                        child: Padding(
                        padding: EdgeInsets.only(left: 10, bottom: 12),
                       child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                         Text("${double.parse(convertedPrecip.toStringAsFixed(2))}", style: TextStyle(fontSize: 40, fontWeight: FontWeight.w500, color: Theme.of(context).colorScheme.onSurface),),
                         Padding(
                          padding: EdgeInsets.only(top: 15),
                          child: Text(context.watch<UnitSettingsNotifier>().precipitationUnit, style: TextStyle(fontSize: 20, color: Theme.of(context).colorScheme.secondary),),
                         ),
                          ],
                        ),
                      ),
                      ),
                      Align(
                      alignment: Alignment.bottomLeft,
                      child: Padding(
                        padding: EdgeInsets.only(left: 10, right: 10, bottom: 10),
                       child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                      SizedBox(
                        width: 100,
                         child: Text("Total rain for the day", style: TextStyle(height: 1.2, color: Theme.of(context).colorScheme.onSurfaceVariant))),
                          Text("$currentTotalPrecProb%", style: TextStyle(fontWeight: FontWeight.w700, color: Theme.of(context).colorScheme.primary),)
                        ],
                      )
                      )
                      ),
                    ],
                  ),
                  )
                ],
              ),
            ),
          ],
        ),
    );
  }
}


// svgs

String buildHumidity(Color color, int humidityValue) {
  final hexColor = '#${color.value.toRadixString(16).padLeft(8, '0').substring(2)}';
 String returnSvg = '';

    if(humidityValue < 30){
     returnSvg = ''' <svg  viewBox="0 0 364.0 364.0" xmlns="http://www.w3.org/2000/svg"> <g> <clip-path d="M0,0h364v364h-364z" /> <path style="fill: $hexColor;" d="M9.4,331.51L7.79,332.45C-3.44,339.05 -10.34,351.11 -10.34,364.14L-10.34,511.68C-10.34,521.5 -2.38,529.46 7.43,529.46H393.8C403.61,529.46 411.57,521.5 411.57,511.68V353.86C411.57,339.13 395.57,329.97 382.86,337.43C376.91,340.93 369.52,340.93 363.56,337.43L353.48,331.51C341.98,324.75 327.73,324.75 316.24,331.51L315.12,332.17C303.63,338.92 289.38,338.92 277.89,332.17L276.76,331.51C265.27,324.75 251.02,324.75 239.53,331.51L238.41,332.17C226.92,338.92 212.67,338.92 201.17,332.17L200.05,331.51C188.56,324.75 174.31,324.75 162.82,331.51L161.7,332.17C150.21,338.92 135.96,338.92 124.46,332.17L123.34,331.51C111.85,324.75 97.6,324.75 86.11,331.51L84.99,332.17C73.49,338.92 59.25,338.92 47.75,332.17L46.63,331.51C35.14,324.75 20.89,324.75 9.4,331.51Z" /> </g> </svg>';
        ''';
   } else if (humidityValue >= 30 && humidityValue < 50){
     returnSvg = ''' <svg  viewBox="0 0 364.0 364.0" xmlns="http://www.w3.org/2000/svg"> <g> <clip-path d="M0,0h364v364h-364z" /> <path style="fill: $hexColor;" d="M9.4,250.85L7.79,251.79C-3.44,258.39 -10.34,270.45 -10.34,283.48L-10.34,431.02C-10.34,440.84 -2.39,448.8 7.43,448.8H393.8C403.61,448.8 411.57,440.84 411.57,431.02V273.2C411.57,258.47 395.56,249.31 382.86,256.77C376.9,260.27 369.52,260.27 363.56,256.77L353.47,250.85C341.98,244.09 327.73,244.09 316.24,250.85L315.12,251.51C303.63,258.26 289.38,258.26 277.88,251.51L276.76,250.85C265.27,244.09 251.02,244.09 239.53,250.85L238.41,251.51C226.91,258.26 212.67,258.26 201.17,251.51L200.05,250.85C188.56,244.09 174.31,244.09 162.82,250.85L161.7,251.51C150.2,258.26 135.96,258.26 124.46,251.51L123.34,250.85C111.85,244.09 97.6,244.09 86.11,250.85L84.99,251.51C73.49,258.26 59.24,258.26 47.75,251.51L46.63,250.85C35.14,244.09 20.89,244.09 9.4,250.85Z" /> </g> </svg>';
        ''';
   } else if (humidityValue >= 50 && humidityValue < 70){
     returnSvg = ''' <svg  viewBox="0 0 364.0 364.0" xmlns="http://www.w3.org/2000/svg"> <g> <clip-path d="M0,0h364v364h-364z" /> <path style="fill: $hexColor;" d="M9.4,178.46L7.79,179.41C-3.44,186.01 -10.34,198.06 -10.34,211.09L-10.34,358.64C-10.34,368.45 -2.39,376.41 7.43,376.41H393.8C403.61,376.41 411.57,368.45 411.57,358.64V200.82C411.57,186.08 395.56,176.93 382.86,184.39C376.9,187.89 369.52,187.89 363.56,184.39L353.47,178.46C341.98,171.71 327.73,171.71 316.24,178.46L315.12,179.12C303.63,185.87 289.38,185.87 277.88,179.12L276.76,178.46C265.27,171.71 251.02,171.71 239.53,178.46L238.41,179.12C226.91,185.87 212.67,185.87 201.17,179.12L200.05,178.46C188.56,171.71 174.31,171.71 162.82,178.46L161.7,179.12C150.2,185.87 135.96,185.87 124.46,179.12L123.34,178.46C111.85,171.71 97.6,171.71 86.11,178.46L84.99,179.12C73.49,185.87 59.24,185.87 47.75,179.12L46.63,178.46C35.14,171.71 20.89,171.71 9.4,178.46Z" /> </g> </svg>';
        ''';
   } else if (humidityValue >= 70 && humidityValue < 90){
     returnSvg = ''' <svg  viewBox="0 0 364.0 364.0" xmlns="http://www.w3.org/2000/svg"> <g> <clip-path d="M0,0h364v364h-364z" /> <path style="fill: $hexColor;" d="M9.4,87.46L7.79,88.41C-3.44,95.01 -10.34,107.06 -10.34,120.09L-10.34,349.33C-10.34,359.15 -2.39,367.1 7.43,367.1H393.8C403.61,367.1 411.57,359.15 411.57,349.33V109.82C411.57,95.08 395.56,85.93 382.86,93.39C376.9,96.89 369.52,96.89 363.56,93.39L353.47,87.46C341.98,80.71 327.73,80.71 316.24,87.46L315.12,88.12C303.63,94.87 289.38,94.87 277.88,88.12L276.76,87.46C265.27,80.71 251.02,80.71 239.53,87.46L238.41,88.12C226.91,94.87 212.67,94.87 201.17,88.12L200.05,87.46C188.56,80.71 174.31,80.71 162.82,87.46L161.7,88.12C150.2,94.87 135.96,94.87 124.46,88.12L123.34,87.46C111.85,80.71 97.6,80.71 86.11,87.46L84.99,88.12C73.49,94.87 59.24,94.87 47.75,88.12L46.63,87.46C35.14,80.71 20.89,80.71 9.4,87.46Z" /> </g> </svg>';
        ''';
   } else if (humidityValue >= 90){
     returnSvg = ''' <svg  viewBox="0 0 364.0 364.0" xmlns="http://www.w3.org/2000/svg"> <g> <clip-path d="M0,0h364v364h-364z" /> <path style="fill: $hexColor;" d="M9.4,31.62L7.79,32.56C-3.44,39.16 -10.34,51.22 -10.34,64.25L-10.34,350.36C-10.34,360.18 -2.39,368.14 7.43,368.14H393.8C403.61,368.14 411.57,360.18 411.57,350.36V53.97C411.57,39.24 395.56,30.08 382.86,37.55C376.9,41.05 369.52,41.05 363.56,37.55L353.47,31.62C341.98,24.87 327.73,24.87 316.24,31.62L315.12,32.28C303.63,39.03 289.38,39.03 277.88,32.28L276.76,31.62C265.27,24.87 251.02,24.87 239.53,31.62L238.41,32.28C226.91,39.03 212.67,39.03 201.17,32.28L200.05,31.62C188.56,24.87 174.31,24.87 162.82,31.62L161.7,32.28C150.2,39.03 135.96,39.03 124.46,32.28L123.34,31.62C111.85,24.87 97.6,24.87 86.11,31.62L84.99,32.28C73.49,39.03 59.24,39.03 47.75,32.28L46.63,31.62C35.14,24.87 20.89,24.87 9.4,31.62Z" /> </g> </svg>';
        ''';
   }

 return returnSvg;
}

String buildsunPath(Color color) {
  final hexColor = '#${color.value.toRadixString(16).padLeft(8, '0').substring(2)}';

  return  ''' <svg height="110.0dip" width="176.0dip" viewBox="0 0 176.0 110.0" xmlns="http://www.w3.org/2000/svg"> <path style="fill: $hexColor;" d="M176.5,54.44V89.86C176.5,100.91 167.52,109.86 156.44,109.86H20.06C8.98,109.86 0,100.91 0,89.86V52.57C7.79,51.81 15.41,48.77 21.78,43.46L62.06,9.91C76.95,-2.49 98.6,-2.49 113.49,9.91L156.22,45.51C162.18,50.47 169.23,53.45 176.49,54.44H176.5Z" fill-rule="evenodd" /> </svg>';
        ''';
}


String buildPressueSvg(Color colorPrimary, Color colorHigh, Color colorSurface, pressueValue) {
  final hexColorPrimary = '#${colorPrimary.value.toRadixString(16).padLeft(8, '0').substring(2)}';
  final hexColorHigh = '#${colorHigh.value.toRadixString(16).padLeft(8, '0').substring(2)}';
  final hexColorSurface = '#${colorSurface.value.toRadixString(16).padLeft(8, '0').substring(2)}';

 String returnSvg = '';

  if(pressueValue < 980){
    returnSvg = ''' <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="$hexColorSurface" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="$hexColorHigh" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="$hexColorPrimary" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C20.29,132.41 14.2,121.31 10.88,109.26C10.29,107.13 11.66,104.99 13.82,104.51V104.51C15.97,104.03 18.1,105.39 18.7,107.52C21.67,118.08 27.02,127.83 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
     ''';
  } else if (pressueValue > 980 && pressueValue <= 1005){
    returnSvg = ''' <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="$hexColorSurface" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="$hexColorHigh" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="$hexColorPrimary" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C16.29,127.98 8.99,110.39 8.09,91.87C7.2,73.35 12.76,55.14 23.76,40.32C25.08,38.54 27.61,38.3 29.32,39.71V39.71C31.02,41.11 31.26,43.63 29.95,45.41C20.2,58.69 15.28,74.95 16.08,91.49C16.89,108.02 23.36,123.73 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
     ''';
  } else if (pressueValue > 1005 && pressueValue <= 1020){
    returnSvg = ''' <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="$hexColorSurface" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="$hexColorHigh" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="$hexColorPrimary" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C55.6,13.43 69.58,8.82 84,8.1C86.21,7.99 88,9.79 88,12V12C88,14.21 86.21,15.99 84,16.11C71.17,16.82 58.73,20.96 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
     ''';
  } else if (pressueValue > 1020 && pressueValue <= 1035){

  } else if (pressueValue > 1036){
    returnSvg = ''' <svg  android:autoMirrored="true" viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg"> <path fill="$hexColorSurface" d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" /> <path fill="$hexColorHigh" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C18.99,130.96 12.36,117.82 9.54,103.61C6.45,88.09 8.03,72 14.09,57.39C20.14,42.77 30.4,30.27 43.55,21.48C56.71,12.69 72.18,8 88,8C103.82,8 119.29,12.69 132.45,21.48C145.6,30.27 155.85,42.77 161.91,57.39C167.96,72 169.55,88.09 166.46,103.61C163.64,117.82 157.01,130.96 147.32,141.67C145.84,143.31 143.3,143.3 141.74,141.74V141.74C140.18,140.18 140.19,137.65 141.66,136.01C150.23,126.43 156.1,114.7 158.62,102.05C161.4,88.08 159.97,73.6 154.52,60.45C149.07,47.29 139.84,36.05 128,28.13C116.16,20.22 102.24,16 88,16C73.76,16 59.84,20.22 48,28.13C36.16,36.05 26.93,47.29 21.48,60.45C16.03,73.6 14.61,88.08 17.38,102.05C19.9,114.7 25.77,126.43 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> <path fill="$hexColorPrimary" d="M34.26,141.74C32.7,143.3 30.16,143.31 28.67,141.67C20.18,132.27 14.01,120.98 10.73,108.71C7.09,95.14 7.09,80.86 10.73,67.29C14.36,53.73 21.5,41.36 31.43,31.43C41.36,21.5 53.73,14.36 67.29,10.73C80.86,7.09 95.14,7.09 108.71,10.73C122.27,14.36 134.64,21.5 144.57,31.43C154.5,41.36 161.64,53.73 165.27,67.29C168.56,79.57 168.88,92.43 166.21,104.82C165.75,106.98 163.54,108.24 161.41,107.67V107.67C159.28,107.1 158.02,104.91 158.47,102.74C160.78,91.72 160.47,80.28 157.55,69.36C154.28,57.16 147.85,46.03 138.91,37.09C129.98,28.15 118.84,21.72 106.64,18.45C94.43,15.18 81.57,15.18 69.36,18.45C57.16,21.72 46.03,28.15 37.09,37.09C28.15,46.03 21.72,57.16 18.45,69.36C15.18,81.57 15.18,94.43 18.45,106.64C21.38,117.55 26.83,127.61 34.34,136.01C35.81,137.65 35.82,140.18 34.26,141.74V141.74Z" /> </svg>
     ''';
  }
  
  return returnSvg;
}



String buildVisibilitySvg(Color color) {
  final hexColor = '#${color.value.toRadixString(16).padLeft(8, '0').substring(2)}';

    return  ''' <svg height="180.0dip" width="180.0dip" viewBox="0 0 180.0 180.0"
                xmlns="http://www.w3.org/2000/svg">
              <path fill="$hexColor"
                    d="M89.07,16.84C94.2,12.37 102.04,13.19 106.12,18.63V18.63C109.08,22.57 114.2,24.24 118.91,22.78V22.78C125.41,20.79 132.24,24.73 133.75,31.35V31.35C134.85,36.16 138.85,39.76 143.75,40.36V40.36C150.5,41.17 155.13,47.55 153.82,54.22V54.22C152.88,59.06 155.06,63.98 159.3,66.51V66.51C165.13,70 166.77,77.71 162.86,83.27V83.27C160.02,87.31 160.02,92.69 162.86,96.73V96.73C166.77,102.29 165.13,110 159.3,113.49V113.49C155.06,116.02 152.88,120.94 153.82,125.78V125.78C155.13,132.45 150.5,138.83 143.75,139.65V139.65C138.85,140.24 134.85,143.84 133.75,148.65V148.65C132.24,155.27 125.41,159.21 118.91,157.21V157.21C114.2,155.76 109.08,157.43 106.12,161.37V161.37C102.04,166.81 94.2,167.63 89.07,163.16V163.16C85.36,159.92 80,159.36 75.69,161.76V161.76C69.75,165.06 62.26,162.63 59.4,156.46V156.46C57.32,151.99 52.66,149.3 47.74,149.74V149.74C40.97,150.34 35.12,145.07 35.01,138.27V138.27C34.93,133.34 31.77,128.98 27.1,127.38V127.38C20.67,125.18 17.46,117.98 20.13,111.73V111.73C22.07,107.19 20.95,101.93 17.33,98.57V98.57C12.35,93.94 12.35,86.06 17.33,81.43V81.43C20.95,78.07 22.07,72.81 20.13,68.27V68.27C17.46,62.02 20.67,54.82 27.1,52.62V52.62C31.77,51.02 34.93,46.66 35.01,41.73V41.73C35.12,34.94 40.97,29.66 47.74,30.26V30.26C52.66,30.7 57.32,28.01 59.4,23.54V23.54C62.26,17.37 69.75,14.94 75.69,18.24V18.24C80,20.64 85.36,20.08 89.07,16.84V16.84Z"
                    stroke-opacity="0.5" fill-opacity="0.5" />
              <path fill="$hexColor"
                    d="M85.25,16.99C90.13,12.26 98,12.67 102.36,17.88V17.88C105.52,21.67 110.72,23.06 115.36,21.37V21.37C121.74,19.03 128.76,22.61 130.63,29.15V29.15C131.98,33.89 136.16,37.28 141.08,37.61V37.61C147.86,38.07 152.82,44.2 151.87,50.93V50.93C151.17,55.81 153.61,60.61 157.97,62.92V62.92C163.98,66.1 166.02,73.71 162.41,79.47V79.47C159.79,83.65 160.07,89.02 163.11,92.91V92.91C167.31,98.26 166.07,106.04 160.43,109.83V109.83C156.34,112.58 154.41,117.61 155.61,122.39V122.39C157.27,128.99 152.97,135.6 146.28,136.76V136.76C141.42,137.61 137.61,141.42 136.76,146.28V146.28C135.6,152.98 128.98,157.27 122.39,155.61V155.61C117.61,154.41 112.58,156.34 109.83,160.43V160.43C106.04,166.08 98.25,167.31 92.9,163.11V163.11C89.02,160.07 83.65,159.79 79.47,162.41V162.41C73.71,166.02 66.1,163.98 62.92,157.97V157.97C60.61,153.61 55.81,151.17 50.93,151.87V151.87C44.2,152.82 38.07,147.86 37.61,141.08V141.08C37.28,136.16 33.89,131.98 29.14,130.63V130.63C22.61,128.76 19.03,121.74 21.36,115.36V115.36C23.06,110.72 21.67,105.53 17.88,102.36V102.36C12.67,98 12.25,90.13 16.99,85.25V85.25C20.42,81.71 21.26,76.39 19.09,71.96V71.96C16.1,65.86 18.92,58.5 25.23,55.96V55.96C29.81,54.12 32.74,49.61 32.56,44.68V44.68C32.31,37.88 37.88,32.31 44.68,32.56V32.56C49.61,32.74 54.12,29.81 55.96,25.23V25.23C58.49,18.93 65.85,16.1 71.96,19.09V19.09C76.39,21.26 81.7,20.42 85.25,16.99V16.99Z"
                    stroke-opacity="0.5" fill-opacity="0.5" />
              <path fill="$hexColor"
                    d="M81.43,17.33C86.06,12.35 93.94,12.35 98.57,17.33V17.33C101.93,20.95 107.19,22.07 111.73,20.13V20.13C117.98,17.46 125.18,20.67 127.38,27.1V27.1C128.98,31.77 133.34,34.93 138.27,35.01V35.01C145.07,35.12 150.34,40.97 149.74,47.75V47.75C149.3,52.66 151.99,57.32 156.46,59.4V59.4C162.63,62.26 165.06,69.75 161.76,75.69V75.69C159.36,80 159.92,85.36 163.16,89.07V89.07C167.63,94.2 166.81,102.04 161.37,106.12V106.12C157.43,109.08 155.76,114.2 157.21,118.91V118.91C159.21,125.41 155.27,132.24 148.65,133.75V133.75C143.84,134.85 140.24,138.85 139.64,143.75V143.75C138.83,150.5 132.45,155.13 125.78,153.82V153.82C120.94,152.88 116.02,155.06 113.49,159.3V159.3C110,165.13 102.29,166.77 96.73,162.86V162.86C92.69,160.02 87.31,160.02 83.27,162.86V162.86C77.71,166.77 70,165.13 66.51,159.3V159.3C63.98,155.06 59.06,152.88 54.22,153.82V153.82C47.55,155.13 41.17,150.5 40.36,143.75V143.75C39.76,138.85 36.16,134.85 31.35,133.75V133.75C24.73,132.24 20.79,125.41 22.78,118.91V118.91C24.24,114.2 22.57,109.08 18.63,106.12V106.12C13.19,102.04 12.37,94.2 16.84,89.07V89.07C20.08,85.36 20.64,80 18.24,75.69V75.69C14.94,69.75 17.37,62.26 23.54,59.4V59.4C28.01,57.32 30.7,52.66 30.26,47.75V47.75C29.66,40.97 34.94,35.12 41.73,35.01V35.01C46.66,34.93 51.02,31.77 52.62,27.1V27.1C54.82,20.67 62.02,17.46 68.27,20.13V20.13C72.81,22.07 78.07,20.95 81.43,17.33V17.33Z" />
          </svg>
      ''';
}


String buildWindSvg(Color color, Color colorSurface) {
  final hexColor = '#${color.value.toRadixString(16).padLeft(8, '0').substring(2)}';
  final hexColorSurface = '#${colorSurface.value.toRadixString(16).padLeft(8, '0').substring(2)}';


    return  ''' <svg viewBox="0 0 176.0 176.0" xmlns="http://www.w3.org/2000/svg">
            <g> <clip-path
                    d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" />
                <path fill="$hexColorSurface"
                      d="M88,0L88,0A88,88 0,0 1,176 88L176,88A88,88 0,0 1,88 176L88,176A88,88 0,0 1,0 88L0,88A88,88 0,0 1,88 0z" />
                <path fill="$hexColor"
                      d="M108.04,151.24C99.97,168.05 76.03,168.05 67.96,151.24L27.21,66.3C18.79,48.75 35.4,29.63 53.96,35.5L81.29,44.15C85.66,45.54 90.34,45.54 94.71,44.15L122.04,35.5C140.6,29.63 157.21,48.75 148.79,66.3L108.04,151.24Z" />
            </g>
        </svg>
      ''';
}


class headerWidgetConditions extends StatelessWidget {
  final String headerText;
  final IconData headerIcon;

  const headerWidgetConditions({super.key, required this.headerText, required this.headerIcon});

  @override
  Widget build(BuildContext context) {
  return  Positioned(
          left: 0,
          right: 0,
          top: 35,
        child: Center(
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                headerIcon,
                weight: 500,
                fill: 1,
                color: Theme.of(context).brightness == Brightness.dark
                    ? Colors.white
                    : Colors.black,
                size: 18,
              ),
              SizedBox(width: 3),
              Text(
                headerText,
                style: TextStyle(
                  color: Theme.of(context).brightness == Brightness.dark
                      ? Colors.white
                      : Colors.black,
                  fontSize: 14,
                  fontWeight: FontWeight.w500
                ),
                maxLines: 1, overflow: TextOverflow.ellipsis
              ),
            ],
          ),
        ),
      );
  }
}


String getCompassDirection(int degrees) {
  var directions = [
    'wind_dir_n'.tr(), 'wind_dir_ne'.tr(), 'wind_dir_e'.tr(), 'wind_dir_se'.tr(), 'wind_dir_s'.tr(), 'wind_dir_sw'.tr(), 'wind_dir_w'.tr(), 'wind_dir_nw'.tr()
  ];
  int index = ((degrees + 22.5) ~/ 45) % 8;
  return directions[index];
}

// wind dirc

class WindCompassWidget extends StatefulWidget {
  final int currentWindDirc;
  final Color backgroundColor;

  const WindCompassWidget({
    required this.currentWindDirc,
    required this.backgroundColor,
    super.key,
  });

  @override
  State<WindCompassWidget> createState() => _WindCompassWidgetState();
}

class _WindCompassWidgetState extends State<WindCompassWidget> {


  @override
  Widget build(BuildContext context) {

final useDeviceCompass = context.watch<UnitSettingsNotifier>().useDeviceCompass;


    if (useDeviceCompass  &&
        !kIsWeb &&
        (Platform.isAndroid || Platform.isIOS)) {
      return StreamBuilder<CompassEvent>(
        stream: FlutterCompass.events,
        builder: (context, snapshot) {
          final heading = snapshot.data?.heading;
          if (heading == null) return SizedBox();

          final rotation = (widget.currentWindDirc - heading) * (pi / 180);

          return Transform.rotate(
            angle: rotation,
            child: SvgPicture.string(
              buildWindSvg(
                Theme.of(context).colorScheme.primaryContainer,
                widget.backgroundColor,
              ),
            ),
          );
        },
      );
    } else {
      // Default non-rotating arrow
      return Transform.rotate(
        angle: widget.currentWindDirc * (pi / 180),
        child: SvgPicture.string(
          buildWindSvg(
            Theme.of(context).colorScheme.primaryContainer,
            widget.backgroundColor,
          ),
        ),
      );
    }
  }
}



String buildUVSvg(Color colorSurface, uvValue) {
  final hexColorSurface = '#${colorSurface.value.toRadixString(16).padLeft(8, '0').substring(2)}';

 String returnSvg = '';

  if(uvValue >= 0 && uvValue <= 3){
    returnSvg = '''<?xml version="1.0" encoding="utf-8"?>
    <svg height="176.0dip" width="176.0dip" android:autoMirrored="true" viewBox="0 0 176.0 176.0"
      xmlns:android="http://schemas.android.com/apk/res/android">
        <path fill="$hexColorSurface" d="M77.55,6.78C81.33,4.32 83.23,3.1 85.26,2.62C87.06,2.2 88.94,2.2 90.74,2.62C92.77,3.1 94.67,4.32 98.45,6.78L102.86,9.64C104.8,10.9 105.77,11.53 106.82,11.95C107.75,12.33 108.73,12.6 109.72,12.73C110.84,12.89 112,12.83 114.31,12.71L119.56,12.43C124.07,12.2 126.32,12.08 128.32,12.69C130.09,13.22 131.71,14.16 133.06,15.43C134.59,16.86 135.61,18.87 137.66,22.88L140.05,27.57C141.1,29.63 141.63,30.66 142.32,31.55C142.94,32.35 143.65,33.06 144.45,33.68C145.34,34.37 146.37,34.9 148.43,35.95L153.12,38.34C157.13,40.39 159.14,41.41 160.57,42.94C161.84,44.29 162.78,45.91 163.31,47.68C163.92,49.68 163.8,51.93 163.57,56.44L163.29,61.69C163.17,64 163.11,65.16 163.27,66.28C163.4,67.27 163.67,68.25 164.04,69.18C164.47,70.23 165.1,71.2 166.36,73.14L169.22,77.55C171.68,81.33 172.9,83.23 173.38,85.26C173.8,87.06 173.8,88.94 173.38,90.74C172.9,92.77 171.68,94.67 169.22,98.45L166.36,102.86C165.1,104.8 164.47,105.77 164.04,106.82C163.67,107.75 163.4,108.73 163.27,109.72C163.11,110.84 163.17,112 163.29,114.31L163.57,119.56C163.8,124.07 163.92,126.32 163.31,128.32C162.78,130.09 161.84,131.71 160.57,133.06C159.14,134.59 157.13,135.61 153.12,137.66L148.43,140.05C146.37,141.1 145.34,141.63 144.45,142.32C143.65,142.94 142.94,143.65 142.32,144.45C141.63,145.34 141.1,146.37 140.05,148.43L137.66,153.12C135.61,157.13 134.59,159.14 133.06,160.57C131.71,161.84 130.09,162.78 128.32,163.31C126.32,163.92 124.07,163.8 119.56,163.57L114.31,163.29C112,163.17 110.84,163.11 109.72,163.27C108.73,163.4 107.75,163.67 106.82,164.04C105.77,164.47 104.8,165.1 102.86,166.36L98.45,169.22C94.67,171.68 92.77,172.9 90.74,173.38C88.94,173.8 87.06,173.8 85.26,173.38C83.23,172.9 81.33,171.68 77.55,169.22L73.14,166.36C71.2,165.1 70.23,164.47 69.18,164.04C68.25,163.67 67.27,163.4 66.28,163.27C65.16,163.11 64,163.17 61.69,163.29L56.44,163.57C51.93,163.8 49.68,163.92 47.68,163.31C45.91,162.78 44.29,161.84 42.94,160.57C41.41,159.14 40.39,157.13 38.34,153.12L35.95,148.43C34.9,146.37 34.37,145.34 33.68,144.45C33.06,143.65 32.35,142.94 31.55,142.32C30.66,141.63 29.63,141.1 27.57,140.05L22.88,137.66C18.87,135.61 16.86,134.59 15.43,133.06C14.16,131.71 13.22,130.09 12.69,128.32C12.08,126.32 12.2,124.07 12.43,119.56L12.71,114.31C12.83,112 12.89,110.84 12.73,109.72C12.6,108.73 12.33,107.75 11.95,106.82C11.53,105.77 10.9,104.8 9.64,102.86L6.78,98.45C4.32,94.67 3.1,92.77 2.62,90.74C2.2,88.94 2.2,87.06 2.62,85.26C3.1,83.23 4.32,81.33 6.78,77.55L9.64,73.14C10.9,71.2 11.53,70.23 11.95,69.18C12.33,68.25 12.6,67.27 12.73,66.28C12.89,65.16 12.83,64 12.71,61.69L12.43,56.44C12.2,51.93 12.08,49.68 12.69,47.68C13.22,45.91 14.16,44.29 15.43,42.94C16.86,41.41 18.87,40.39 22.88,38.34L27.57,35.95C29.63,34.9 30.66,34.37 31.55,33.68C32.35,33.06 33.06,32.35 33.68,31.55C34.37,30.66 34.9,29.63 35.95,27.57L38.34,22.88C40.39,18.87 41.41,16.86 42.94,15.43C44.29,14.16 45.91,13.22 47.68,12.69C49.68,12.08 51.93,12.2 56.44,12.43L61.69,12.71C64,12.83 65.16,12.89 66.28,12.73C67.27,12.6 68.25,12.33 69.18,11.95C70.23,11.53 71.2,10.9 73.14,9.64L77.55,6.78Z" />
        <path fill="#fa903e" d="M88,155m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#af5cf7" d="M144,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#6dd58c" d="M31,121m8,-0a8,8 0,1 1,-16 -0a8,8 0,1 1,16 -0" />
        <path fill="#fcc934" d="M54,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#ee675c" d="M120,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
    </svg>

    ''';
  } else if (uvValue > 3 && uvValue <= 6){
    returnSvg = '''<?xml version="1.0" encoding="utf-8"?>
      <svg height="176.0dip" width="176.0dip" android:autoMirrored="true" viewBox="0 0 176.0 176.0"
        xmlns:android="http://schemas.android.com/apk/res/android">
          <path fill="$hexColorSurface" d="M77.55,6.78C81.33,4.32 83.23,3.1 85.26,2.62C87.06,2.2 88.94,2.2 90.74,2.62C92.77,3.1 94.67,4.32 98.45,6.78L102.86,9.64C104.8,10.9 105.77,11.53 106.82,11.95C107.75,12.33 108.73,12.6 109.72,12.73C110.84,12.89 112,12.83 114.31,12.71L119.56,12.43C124.07,12.2 126.32,12.08 128.32,12.69C130.09,13.22 131.71,14.16 133.06,15.43C134.59,16.86 135.61,18.87 137.66,22.88L140.05,27.57C141.1,29.63 141.63,30.66 142.32,31.55C142.94,32.35 143.65,33.06 144.45,33.68C145.34,34.37 146.37,34.9 148.43,35.95L153.12,38.34C157.13,40.39 159.14,41.41 160.57,42.94C161.84,44.29 162.78,45.91 163.31,47.68C163.92,49.68 163.8,51.93 163.57,56.44L163.29,61.69C163.17,64 163.11,65.16 163.27,66.28C163.4,67.27 163.67,68.25 164.04,69.18C164.47,70.23 165.1,71.2 166.36,73.14L169.22,77.55C171.68,81.33 172.9,83.23 173.38,85.26C173.8,87.06 173.8,88.94 173.38,90.74C172.9,92.77 171.68,94.67 169.22,98.45L166.36,102.86C165.1,104.8 164.47,105.77 164.04,106.82C163.67,107.75 163.4,108.73 163.27,109.72C163.11,110.84 163.17,112 163.29,114.31L163.57,119.56C163.8,124.07 163.92,126.32 163.31,128.32C162.78,130.09 161.84,131.71 160.57,133.06C159.14,134.59 157.13,135.61 153.12,137.66L148.43,140.05C146.37,141.1 145.34,141.63 144.45,142.32C143.65,142.94 142.94,143.65 142.32,144.45C141.63,145.34 141.1,146.37 140.05,148.43L137.66,153.12C135.61,157.13 134.59,159.14 133.06,160.57C131.71,161.84 130.09,162.78 128.32,163.31C126.32,163.92 124.07,163.8 119.56,163.57L114.31,163.29C112,163.17 110.84,163.11 109.72,163.27C108.73,163.4 107.75,163.67 106.82,164.04C105.77,164.47 104.8,165.1 102.86,166.36L98.45,169.22C94.67,171.68 92.77,172.9 90.74,173.38C88.94,173.8 87.06,173.8 85.26,173.38C83.23,172.9 81.33,171.68 77.55,169.22L73.14,166.36C71.2,165.1 70.23,164.47 69.18,164.04C68.25,163.67 67.27,163.4 66.28,163.27C65.16,163.11 64,163.17 61.69,163.29L56.44,163.57C51.93,163.8 49.68,163.92 47.68,163.31C45.91,162.78 44.29,161.84 42.94,160.57C41.41,159.14 40.39,157.13 38.34,153.12L35.95,148.43C34.9,146.37 34.37,145.34 33.68,144.45C33.06,143.65 32.35,142.94 31.55,142.32C30.66,141.63 29.63,141.1 27.57,140.05L22.88,137.66C18.87,135.61 16.86,134.59 15.43,133.06C14.16,131.71 13.22,130.09 12.69,128.32C12.08,126.32 12.2,124.07 12.43,119.56L12.71,114.31C12.83,112 12.89,110.84 12.73,109.72C12.6,108.73 12.33,107.75 11.95,106.82C11.53,105.77 10.9,104.8 9.64,102.86L6.78,98.45C4.32,94.67 3.1,92.77 2.62,90.74C2.2,88.94 2.2,87.06 2.62,85.26C3.1,83.23 4.32,81.33 6.78,77.55L9.64,73.14C10.9,71.2 11.53,70.23 11.95,69.18C12.33,68.25 12.6,67.27 12.73,66.28C12.89,65.16 12.83,64 12.71,61.69L12.43,56.44C12.2,51.93 12.08,49.68 12.69,47.68C13.22,45.91 14.16,44.29 15.43,42.94C16.86,41.41 18.87,40.39 22.88,38.34L27.57,35.95C29.63,34.9 30.66,34.37 31.55,33.68C32.35,33.06 33.06,32.35 33.68,31.55C34.37,30.66 34.9,29.63 35.95,27.57L38.34,22.88C40.39,18.87 41.41,16.86 42.94,15.43C44.29,14.16 45.91,13.22 47.68,12.69C49.68,12.08 51.93,12.2 56.44,12.43L61.69,12.71C64,12.83 65.16,12.89 66.28,12.73C67.27,12.6 68.25,12.33 69.18,11.95C70.23,11.53 71.2,10.9 73.14,9.64L77.55,6.78Z" />
          <path fill="#fa903e" d="M88,155m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
          <path fill="#af5cf7" d="M144,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
          <path fill="#6dd58c" d="M31,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
          <path fill="#ee675c" d="M120,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
          <path fill="#fcc934" d="M56,145m8,-0a8,8 0,1 1,-16 -0a8,8 0,1 1,16 -0" />
      </svg>
    ''';      
  } else if (uvValue > 6 && uvValue <= 8){
    returnSvg = '''<?xml version="1.0" encoding="utf-8"?>
      <svg height="176.0dip" width="176.0dip" android:autoMirrored="true" viewBox="0 0 176.0 176.0"
        xmlns:android="http://schemas.android.com/apk/res/android">
          <path fill="$hexColorSurface" d="M77.55,6.78C81.33,4.32 83.23,3.1 85.26,2.62C87.06,2.2 88.94,2.2 90.74,2.62C92.77,3.1 94.67,4.32 98.45,6.78L102.86,9.64C104.8,10.9 105.77,11.53 106.82,11.95C107.75,12.33 108.73,12.6 109.72,12.73C110.84,12.89 112,12.83 114.31,12.71L119.56,12.43C124.07,12.2 126.32,12.08 128.32,12.69C130.09,13.22 131.71,14.16 133.06,15.43C134.59,16.86 135.61,18.87 137.66,22.88L140.05,27.57C141.1,29.63 141.63,30.66 142.32,31.55C142.94,32.35 143.65,33.06 144.45,33.68C145.34,34.37 146.37,34.9 148.43,35.95L153.12,38.34C157.13,40.39 159.14,41.41 160.57,42.94C161.84,44.29 162.78,45.91 163.31,47.68C163.92,49.68 163.8,51.93 163.57,56.44L163.29,61.69C163.17,64 163.11,65.16 163.27,66.28C163.4,67.27 163.67,68.25 164.04,69.18C164.47,70.23 165.1,71.2 166.36,73.14L169.22,77.55C171.68,81.33 172.9,83.23 173.38,85.26C173.8,87.06 173.8,88.94 173.38,90.74C172.9,92.77 171.68,94.67 169.22,98.45L166.36,102.86C165.1,104.8 164.47,105.77 164.04,106.82C163.67,107.75 163.4,108.73 163.27,109.72C163.11,110.84 163.17,112 163.29,114.31L163.57,119.56C163.8,124.07 163.92,126.32 163.31,128.32C162.78,130.09 161.84,131.71 160.57,133.06C159.14,134.59 157.13,135.61 153.12,137.66L148.43,140.05C146.37,141.1 145.34,141.63 144.45,142.32C143.65,142.94 142.94,143.65 142.32,144.45C141.63,145.34 141.1,146.37 140.05,148.43L137.66,153.12C135.61,157.13 134.59,159.14 133.06,160.57C131.71,161.84 130.09,162.78 128.32,163.31C126.32,163.92 124.07,163.8 119.56,163.57L114.31,163.29C112,163.17 110.84,163.11 109.72,163.27C108.73,163.4 107.75,163.67 106.82,164.04C105.77,164.47 104.8,165.1 102.86,166.36L98.45,169.22C94.67,171.68 92.77,172.9 90.74,173.38C88.94,173.8 87.06,173.8 85.26,173.38C83.23,172.9 81.33,171.68 77.55,169.22L73.14,166.36C71.2,165.1 70.23,164.47 69.18,164.04C68.25,163.67 67.27,163.4 66.28,163.27C65.16,163.11 64,163.17 61.69,163.29L56.44,163.57C51.93,163.8 49.68,163.92 47.68,163.31C45.91,162.78 44.29,161.84 42.94,160.57C41.41,159.14 40.39,157.13 38.34,153.12L35.95,148.43C34.9,146.37 34.37,145.34 33.68,144.45C33.06,143.65 32.35,142.94 31.55,142.32C30.66,141.63 29.63,141.1 27.57,140.05L22.88,137.66C18.87,135.61 16.86,134.59 15.43,133.06C14.16,131.71 13.22,130.09 12.69,128.32C12.08,126.32 12.2,124.07 12.43,119.56L12.71,114.31C12.83,112 12.89,110.84 12.73,109.72C12.6,108.73 12.33,107.75 11.95,106.82C11.53,105.77 10.9,104.8 9.64,102.86L6.78,98.45C4.32,94.67 3.1,92.77 2.62,90.74C2.2,88.94 2.2,87.06 2.62,85.26C3.1,83.23 4.32,81.33 6.78,77.55L9.64,73.14C10.9,71.2 11.53,70.23 11.95,69.18C12.33,68.25 12.6,67.27 12.73,66.28C12.89,65.16 12.83,64 12.71,61.69L12.43,56.44C12.2,51.93 12.08,49.68 12.69,47.68C13.22,45.91 14.16,44.29 15.43,42.94C16.86,41.41 18.87,40.39 22.88,38.34L27.57,35.95C29.63,34.9 30.66,34.37 31.55,33.68C32.35,33.06 33.06,32.35 33.68,31.55C34.37,30.66 34.9,29.63 35.95,27.57L38.34,22.88C40.39,18.87 41.41,16.86 42.94,15.43C44.29,14.16 45.91,13.22 47.68,12.69C49.68,12.08 51.93,12.2 56.44,12.43L61.69,12.71C64,12.83 65.16,12.89 66.28,12.73C67.27,12.6 68.25,12.33 69.18,11.95C70.23,11.53 71.2,10.9 73.14,9.64L77.55,6.78Z" />
          <path fill="#fcc934" d="M54,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
          <path fill="#fa903e" d="M88,155m8,-0a8,8 0,1 1,-16 -0a8,8 0,1 1,16 -0" />
          <path fill="#af5cf7" d="M144,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
          <path fill="#6dd58c" d="M31,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
          <path fill="#ee675c" d="M120,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
      </svg>
    ''';       
  } else if (uvValue > 8 && uvValue <= 10){
    returnSvg = '''<?xml version="1.0" encoding="utf-8"?>
    <svg height="176.0dip" width="176.0dip" android:autoMirrored="true" viewBox="0 0 176.0 176.0"
      xmlns:android="http://schemas.android.com/apk/res/android">
        <path fill="$hexColorSurface" d="M77.55,6.78C81.33,4.32 83.23,3.1 85.26,2.62C87.06,2.2 88.94,2.2 90.74,2.62C92.77,3.1 94.67,4.32 98.45,6.78L102.86,9.64C104.8,10.9 105.77,11.53 106.82,11.95C107.75,12.33 108.73,12.6 109.72,12.73C110.84,12.89 112,12.83 114.31,12.71L119.56,12.43C124.07,12.2 126.32,12.08 128.32,12.69C130.09,13.22 131.71,14.16 133.06,15.43C134.59,16.86 135.61,18.87 137.66,22.88L140.05,27.57C141.1,29.63 141.63,30.66 142.32,31.55C142.94,32.35 143.65,33.06 144.45,33.68C145.34,34.37 146.37,34.9 148.43,35.95L153.12,38.34C157.13,40.39 159.14,41.41 160.57,42.94C161.84,44.29 162.78,45.91 163.31,47.68C163.92,49.68 163.8,51.93 163.57,56.44L163.29,61.69C163.17,64 163.11,65.16 163.27,66.28C163.4,67.27 163.67,68.25 164.04,69.18C164.47,70.23 165.1,71.2 166.36,73.14L169.22,77.55C171.68,81.33 172.9,83.23 173.38,85.26C173.8,87.06 173.8,88.94 173.38,90.74C172.9,92.77 171.68,94.67 169.22,98.45L166.36,102.86C165.1,104.8 164.47,105.77 164.04,106.82C163.67,107.75 163.4,108.73 163.27,109.72C163.11,110.84 163.17,112 163.29,114.31L163.57,119.56C163.8,124.07 163.92,126.32 163.31,128.32C162.78,130.09 161.84,131.71 160.57,133.06C159.14,134.59 157.13,135.61 153.12,137.66L148.43,140.05C146.37,141.1 145.34,141.63 144.45,142.32C143.65,142.94 142.94,143.65 142.32,144.45C141.63,145.34 141.1,146.37 140.05,148.43L137.66,153.12C135.61,157.13 134.59,159.14 133.06,160.57C131.71,161.84 130.09,162.78 128.32,163.31C126.32,163.92 124.07,163.8 119.56,163.57L114.31,163.29C112,163.17 110.84,163.11 109.72,163.27C108.73,163.4 107.75,163.67 106.82,164.04C105.77,164.47 104.8,165.1 102.86,166.36L98.45,169.22C94.67,171.68 92.77,172.9 90.74,173.38C88.94,173.8 87.06,173.8 85.26,173.38C83.23,172.9 81.33,171.68 77.55,169.22L73.14,166.36C71.2,165.1 70.23,164.47 69.18,164.04C68.25,163.67 67.27,163.4 66.28,163.27C65.16,163.11 64,163.17 61.69,163.29L56.44,163.57C51.93,163.8 49.68,163.92 47.68,163.31C45.91,162.78 44.29,161.84 42.94,160.57C41.41,159.14 40.39,157.13 38.34,153.12L35.95,148.43C34.9,146.37 34.37,145.34 33.68,144.45C33.06,143.65 32.35,142.94 31.55,142.32C30.66,141.63 29.63,141.1 27.57,140.05L22.88,137.66C18.87,135.61 16.86,134.59 15.43,133.06C14.16,131.71 13.22,130.09 12.69,128.32C12.08,126.32 12.2,124.07 12.43,119.56L12.71,114.31C12.83,112 12.89,110.84 12.73,109.72C12.6,108.73 12.33,107.75 11.95,106.82C11.53,105.77 10.9,104.8 9.64,102.86L6.78,98.45C4.32,94.67 3.1,92.77 2.62,90.74C2.2,88.94 2.2,87.06 2.62,85.26C3.1,83.23 4.32,81.33 6.78,77.55L9.64,73.14C10.9,71.2 11.53,70.23 11.95,69.18C12.33,68.25 12.6,67.27 12.73,66.28C12.89,65.16 12.83,64 12.71,61.69L12.43,56.44C12.2,51.93 12.08,49.68 12.69,47.68C13.22,45.91 14.16,44.29 15.43,42.94C16.86,41.41 18.87,40.39 22.88,38.34L27.57,35.95C29.63,34.9 30.66,34.37 31.55,33.68C32.35,33.06 33.06,32.35 33.68,31.55C34.37,30.66 34.9,29.63 35.95,27.57L38.34,22.88C40.39,18.87 41.41,16.86 42.94,15.43C44.29,14.16 45.91,13.22 47.68,12.69C49.68,12.08 51.93,12.2 56.44,12.43L61.69,12.71C64,12.83 65.16,12.89 66.28,12.73C67.27,12.6 68.25,12.33 69.18,11.95C70.23,11.53 71.2,10.9 73.14,9.64L77.55,6.78Z" />
        <path fill="#6dd58c" d="M31,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#fcc934" d="M54,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#fa903e" d="M88,155m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#af5cf7" d="M144,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#ee675c" d="M120,145m8,-0a8,8 0,1 1,-16 -0a8,8 0,1 1,16 -0" />
    </svg>
    ''';          
  } else if (uvValue > 10){
    returnSvg = '''<?xml version="1.0" encoding="utf-8"?>
    <svg height="176.0dip" width="176.0dip" android:autoMirrored="true" viewBox="0 0 176.0 176.0"
      xmlns:android="http://schemas.android.com/apk/res/android">
        <path fill="#$hexColorSurface" d="M77.55,6.78C81.33,4.32 83.23,3.1 85.26,2.62C87.06,2.2 88.94,2.2 90.74,2.62C92.77,3.1 94.67,4.32 98.45,6.78L102.86,9.64C104.8,10.9 105.77,11.53 106.82,11.95C107.75,12.33 108.73,12.6 109.72,12.73C110.84,12.89 112,12.83 114.31,12.71L119.56,12.43C124.07,12.2 126.32,12.08 128.32,12.69C130.09,13.22 131.71,14.16 133.06,15.43C134.59,16.86 135.61,18.87 137.66,22.88L140.05,27.57C141.1,29.63 141.63,30.66 142.32,31.55C142.94,32.35 143.65,33.06 144.45,33.68C145.34,34.37 146.37,34.9 148.43,35.95L153.12,38.34C157.13,40.39 159.14,41.41 160.57,42.94C161.84,44.29 162.78,45.91 163.31,47.68C163.92,49.68 163.8,51.93 163.57,56.44L163.29,61.69C163.17,64 163.11,65.16 163.27,66.28C163.4,67.27 163.67,68.25 164.04,69.18C164.47,70.23 165.1,71.2 166.36,73.14L169.22,77.55C171.68,81.33 172.9,83.23 173.38,85.26C173.8,87.06 173.8,88.94 173.38,90.74C172.9,92.77 171.68,94.67 169.22,98.45L166.36,102.86C165.1,104.8 164.47,105.77 164.04,106.82C163.67,107.75 163.4,108.73 163.27,109.72C163.11,110.84 163.17,112 163.29,114.31L163.57,119.56C163.8,124.07 163.92,126.32 163.31,128.32C162.78,130.09 161.84,131.71 160.57,133.06C159.14,134.59 157.13,135.61 153.12,137.66L148.43,140.05C146.37,141.1 145.34,141.63 144.45,142.32C143.65,142.94 142.94,143.65 142.32,144.45C141.63,145.34 141.1,146.37 140.05,148.43L137.66,153.12C135.61,157.13 134.59,159.14 133.06,160.57C131.71,161.84 130.09,162.78 128.32,163.31C126.32,163.92 124.07,163.8 119.56,163.57L114.31,163.29C112,163.17 110.84,163.11 109.72,163.27C108.73,163.4 107.75,163.67 106.82,164.04C105.77,164.47 104.8,165.1 102.86,166.36L98.45,169.22C94.67,171.68 92.77,172.9 90.74,173.38C88.94,173.8 87.06,173.8 85.26,173.38C83.23,172.9 81.33,171.68 77.55,169.22L73.14,166.36C71.2,165.1 70.23,164.47 69.18,164.04C68.25,163.67 67.27,163.4 66.28,163.27C65.16,163.11 64,163.17 61.69,163.29L56.44,163.57C51.93,163.8 49.68,163.92 47.68,163.31C45.91,162.78 44.29,161.84 42.94,160.57C41.41,159.14 40.39,157.13 38.34,153.12L35.95,148.43C34.9,146.37 34.37,145.34 33.68,144.45C33.06,143.65 32.35,142.94 31.55,142.32C30.66,141.63 29.63,141.1 27.57,140.05L22.88,137.66C18.87,135.61 16.86,134.59 15.43,133.06C14.16,131.71 13.22,130.09 12.69,128.32C12.08,126.32 12.2,124.07 12.43,119.56L12.71,114.31C12.83,112 12.89,110.84 12.73,109.72C12.6,108.73 12.33,107.75 11.95,106.82C11.53,105.77 10.9,104.8 9.64,102.86L6.78,98.45C4.32,94.67 3.1,92.77 2.62,90.74C2.2,88.94 2.2,87.06 2.62,85.26C3.1,83.23 4.32,81.33 6.78,77.55L9.64,73.14C10.9,71.2 11.53,70.23 11.95,69.18C12.33,68.25 12.6,67.27 12.73,66.28C12.89,65.16 12.83,64 12.71,61.69L12.43,56.44C12.2,51.93 12.08,49.68 12.69,47.68C13.22,45.91 14.16,44.29 15.43,42.94C16.86,41.41 18.87,40.39 22.88,38.34L27.57,35.95C29.63,34.9 30.66,34.37 31.55,33.68C32.35,33.06 33.06,32.35 33.68,31.55C34.37,30.66 34.9,29.63 35.95,27.57L38.34,22.88C40.39,18.87 41.41,16.86 42.94,15.43C44.29,14.16 45.91,13.22 47.68,12.69C49.68,12.08 51.93,12.2 56.44,12.43L61.69,12.71C64,12.83 65.16,12.89 66.28,12.73C67.27,12.6 68.25,12.33 69.18,11.95C70.23,11.53 71.2,10.9 73.14,9.64L77.55,6.78Z" />
        <path fill="#fcc934" d="M54,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#6dd58c" d="M31,121m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#ee675c" d="M120,145m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#fa903e" d="M88,155m6,-0a6,6 0,1 1,-12 -0a6,6 0,1 1,12 -0" stroke-opacity="0.15" fill-opacity="0.15" />
        <path fill="#af5cf7" d="M143,121m8,-0a8,8 0,1 1,-16 -0a8,8 0,1 1,16 -0" />
    </svg>
    ''';    
  }


 return returnSvg;
}

String getUvIndexType(int uvIndex) {
  if(uvIndex >= 0 && uvIndex <= 3){
    return "low_forecast_uv".tr();
  } else if (uvIndex > 3 && uvIndex <= 6){
    return "moderate_forecast_uv".tr();
  } else if (uvIndex > 6 && uvIndex <= 8){
    return "high_forecast_uv".tr();
  } else if (uvIndex > 8 && uvIndex <= 10){
    return "very_high_forecast_uv".tr();
  } else if (uvIndex > 10){
    return "extreme_forecast_uv".tr();
  } else{
    return "error";
  }
}

