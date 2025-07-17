import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:hive/hive.dart';
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../utils/unit_converter.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/icon_map.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../widgets/hourly_card.dart';
import '../widgets/current_conditions_card.dart';
import '../utils/preferences_helper.dart';
import '../utils/condition_label_map.dart';

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
  } else{
    return null;
  }
}


  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Theme.of(context).colorScheme.surfaceContainerLow,
        appBar: AppBar(
          title: Text("daily_forecast".tr()),
          titleSpacing: 0,
          toolbarHeight: 65,
          scrolledUnderElevation: 1,
          backgroundColor: Theme.of(context).colorScheme.surfaceContainerLow,
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

      print(selectedDate);

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
                selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32(),
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
            selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32(),
            timezone: weather['timezone'].toString(),
            utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
            hourlyPrecpProb: hourlyPrecpProb,
            ),
          ],

          SizedBox(height: 10,),
          isSameDay(selectedDate!, DateTime.now().toUtc().add(Duration(seconds: int.parse(weather['utc_offset_seconds'].toString()))))?
                    SizedBox(
              child: ConditionsWidgets(
                selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32(),
                currentHumidity: current['relative_humidity_2m'] ?? 0.0000001,
                currentDewPoint: hourly['dew_point_2m'][0].toDouble() ?? 0.0000001,
                currentSunrise: daily['sunrise'][0] ?? 0.0000001,
                currentSunset: daily['sunset'][0] ?? 0.0000001,
                currentPressure: current['pressure_msl'] ?? 0.0000001,
                currentVisibility: hourly['visibility'][0] ?? 0.0000001,
                currentWindSpeed: current['wind_speed_10m'] ?? 0.0000001,
                currentWindDirc: current['wind_direction_10m'] ?? 0.0000001,
                timezone: weather['timezone'].toString(),
                utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
                currentUvIndex: daily['uv_index_max'][0] ?? 0.0000001,
                currentAQIUSA: weather['air_quality']['current']['us_aqi'] ?? 0.0000001,
                currentAQIEURO: weather['air_quality']['current']['european_aqi'] ?? 0.0000001,
                currentTotalPrec: daily['precipitation_sum'][0] ?? 0.0000001,
                currentDayLength: daily['daylight_duration'][0] ?? 0.0000001,
                isFromHome: false,
              ),
            )
              :
            SizedBox(
              child: ConditionsWidgetsForecast(
              selectedContainerBgIndex: Theme.of(context).colorScheme.surfaceContainerLowest.toARGB32(),
              currentSunrise: daily['sunrise']?[selectedIndex] ?? '',
              currentSunset: daily['sunset']?[selectedIndex] ?? '',
              currentPressure: hourly['pressure_msl']?[firstIndex] ?? 0.0000001,
              currentVisibility: hourly['visibility']?[firstIndex] ?? 0.0000001,
              currentWindSpeed: daily['wind_speed_10m_max']?[selectedIndex] ?? 0.0000001,
              currentWindDirc: hourly['wind_direction_10m']?[firstIndex] ?? 0.0000001,
              timezone: weather['timezone'].toString(),
              utcOffsetSeconds: weather['utc_offset_seconds'].toString(),
              currentUvIndex: daily['uv_index_max']?[selectedIndex] ?? 0.0000001,
              currentTotalPrec: daily['precipitation_sum']?[selectedIndex] ?? 0.0000001,
              currentTotalPrecProb: daily['precipitation_probability_max']?[selectedIndex] ?? 0.0000001,
            )
            ),
              SizedBox(height: MediaQuery.of(context).padding.bottom + 16,)
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


class DailyForecastCard extends StatefulWidget {
  final List<dynamic> dailyTime;
  final List<dynamic> dailyTempsMin;
  final List<dynamic> dailyWeatherCodes;
  final List<dynamic> dailyTempsMax;
  final String timezone;
  final String utcOffsetSeconds;
  final void Function(DateTime selectedDate)? onDaySelected;
  final DateTime? selectedDate;

  const DailyForecastCard({
    super.key,
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
  State<DailyForecastCard> createState() => _DailyForecastCardState();
}

class _DailyForecastCardState extends State<DailyForecastCard> {
  final ScrollController _scrollController = ScrollController();
  bool _hasScrolled = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    if (!_hasScrolled && widget.selectedDate != null) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        final index = widget.dailyTime.indexWhere((time) {
          final date = DateTime.parse(time);
          final selected = widget.selectedDate!;
          return date.year == selected.year &&
              date.month == selected.month &&
              date.day == selected.day;
        });

        if (index != -1 && _scrollController.hasClients) {
            const double itemWidth = 68;
            const double spacing = 5;
            const double fullItemWidth = itemWidth + spacing;

            final screenWidth = MediaQuery.of(context).size.width;
            final centeredOffset = fullItemWidth * index - (screenWidth / 2 - itemWidth / 2);

            _scrollController.animateTo(
              centeredOffset.clamp(
                _scrollController.position.minScrollExtent,
                _scrollController.position.maxScrollExtent,
              ),
              duration: Duration(milliseconds: 300),
              curve: Curves.easeOut,
            );

            _hasScrolled = true;
          }
      });
    }
  }

  @override
  Widget build(BuildContext context) {



    final tempUnit = PreferencesHelper.getString("selectedTempUnit") ?? "Celsius";

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
          controller: _scrollController,
          scrollDirection: Axis.horizontal,
          physics:BouncingScrollPhysics() ,
          itemCount: widget.dailyTime.length,
          separatorBuilder: (context, index) => const SizedBox(width: 4.5),
          itemBuilder: (context, index) {
            final time = DateTime.parse(widget.dailyTime[index]);
            final tempMax = convert(widget.dailyTempsMax[index].toDouble());
            final tempMin = convert(widget.dailyTempsMin[index].toDouble());
            final code = widget.dailyWeatherCodes[index];

              EdgeInsets itemMargin = EdgeInsets.only(
              left: index == 0 ? 10 : 0,
              right: index == widget.dailyTime.length - 1 ? 10 : 0,

            );


            final isSelected = widget.selectedDate != null &&
                              time.year == widget.selectedDate!.year &&
                              time.month == widget.selectedDate!.month &&
                              time.day == widget.selectedDate!.day;


            
            return GestureDetector(
              onTap: () => widget.onDaySelected?.call(time),
              child: AnimatedContainer(
              duration: const Duration(milliseconds: 600), 
              
              curve: Curves.easeInOutQuint,
              width: 68,
              margin: itemMargin,
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                borderRadius: isSelected ? BorderRadius.circular(99) : index == 0
                 ? BorderRadius.only(topLeft: Radius.circular(18), bottomLeft: Radius.circular(18), topRight: Radius.circular(6), bottomRight: Radius.circular(6))
                 : index == widget.dailyTime.length - 1 ? BorderRadius.only(bottomRight: Radius.circular(18), topRight: Radius.circular(18), topLeft: Radius.circular(6), bottomLeft: Radius.circular(6)) : BorderRadius.circular(6),
                color: isSelected
                    ? Theme.of(context).colorScheme.primary
                    : Theme.of(context).colorScheme.surfaceContainerLowest
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  SizedBox(height: 6,),
                  Column(
                    children: [
                  Text("${tempMax.round()}°", style: TextStyle(fontSize: 16, color: isSelected ? Theme.of(context).colorScheme.onPrimary : Theme.of(context).colorScheme.onSurface, fontWeight: FontWeight.w500)),
                  Text("${tempMin.round()}°", style: TextStyle(fontSize: 16, color: isSelected ? Theme.of(context).colorScheme.onInverseSurface: Theme.of(context).colorScheme.onSurfaceVariant, fontWeight: FontWeight.w500)),
                    ],
                  ),
                SvgPicture.asset(
                  WeatherIconMapper.getIcon(code, 1),
                  width: 35,
                ),
                  Column(
                children: [
                  Text(
                _getDayLabel(time, index, widget.utcOffsetSeconds).toLowerCase().tr(),
                  style: TextStyle(fontSize: 15, fontWeight: FontWeight.w500, color: isSelected ? Theme.of(context).colorScheme.onPrimary : Theme.of(context).colorScheme.onSurface),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
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

    // final localSelectedDate = selectedDate.toUtc().add(offset);
    final localSelectedDate = selectedDate;

    final roundedSelectedDate = DateTime(localSelectedDate.year, localSelectedDate.month, localSelectedDate.day);

    int startIndex = hourlyTime.indexWhere((timeStr) {
      final forecastLocal = DateTime.parse(timeStr);
      return forecastLocal.isAtSameMomentAs(roundedSelectedDate) || forecastLocal.isAfter(roundedSelectedDate);
    });

    if (startIndex == -1) startIndex = 0;

    final timeUnit = PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';
    final tempUnit = PreferencesHelper.getString("selectedTempUnit") ?? "Celsius";



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

            final hour = timeUnit == '24 hr' ? "${roundedDisplayTime.hour.toString().padLeft(2, '0')}:00" : UnitConverter.formatTo12Hour(roundedDisplayTime);
            final temp = tempUnit == 'Fahrenheit' ? UnitConverter.celsiusToFahrenheit(hourlyTemps[dataIndex].toDouble()).round() : hourlyTemps[dataIndex].toDouble().round(); 
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
        )
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

    
    final tempUnit = PreferencesHelper.getString("selectedTempUnit") ?? "Celsius";


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
        child: Row(children: [Text(tempUnit == 'Fahrenheit' ? "${UnitConverter.celsiusToFahrenheit(selectedDayData!['maxTemp']).round()}°/" : "${selectedDayData!['maxTemp'].round()}°/", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 56, fontWeight: FontWeight.w500)), 
        Text(tempUnit == 'Fahrenheit' ? "${UnitConverter.celsiusToFahrenheit(selectedDayData!['minTemp']).round()}°" : "${selectedDayData!['minTemp'].round()}°", style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 56, fontWeight: FontWeight.w500)),
        SizedBox(width: 10,),
        SvgPicture.asset(
            WeatherIconMapper.getIcon(selectedDayData!['weatherCode'], 1),
            width: 50,
          ),
        ],)),
        Padding(padding: EdgeInsets.only(left: 12, right: 12, bottom: 10),
        child: Text(WeatherConditionMapper.getConditionLabel(selectedDayData!['weatherCode'], 1).tr(), style: TextStyle(color: Theme.of(context).colorScheme.primary, fontSize: 18),)
        )
    
      ],
    );
  }
}


String formatDateDetailes(String dateStr) {
  final date = DateTime.parse(dateStr);

  const customMonths = {
    1: "month_jan",
    2: "month_feb",
    3: "month_mar",
    4: "month_apr",
    5: "month_may",
    6: "month_june",
    7: "month_july",
    8: "month_aug",
    9: "month_sep",
    10: "month_oct",
    11: "month_nov",
    12: "month_Dec",
  };

  final month = customMonths[date.month] ?? '';
  return "${month.tr()}, ${date.day}";
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

    final timeUnit = PreferencesHelper.getString("selectedTimeUnit") ?? '12 hr';
    final pressureUnit = PreferencesHelper.getString("selectedPressureUnit") ?? "hPa";
    final precipUnit = PreferencesHelper.getString("selectedPrecipitationUnit") ?? "mm";
    final visibilityUnit = PreferencesHelper.getString("selectedVisibilityUnit") ?? "Km";
    final windUnit = PreferencesHelper.getString("selectedWindUnit") ?? "Km/h";


    final sunriseFormat = timeUnit == '24 hr' ? DateFormat.Hm().format(sunrise) : DateFormat.jm().format(sunrise);
    final sunsetFormat = timeUnit == '24 hr' ? DateFormat.Hm().format(sunset) : DateFormat.jm().format(sunset);

    final convertedPressure = pressureUnit == 'inHg'
        ? UnitConverter.hPaToInHg(currentPressure)
        : pressureUnit == 'mmHg'
            ? UnitConverter.hPaToMmHg(currentPressure)
            : currentPressure;

    final convertedPrecip = precipUnit == 'cm'
        ? UnitConverter.mmToCm(currentTotalPrec)
        : precipUnit == 'in'
            ? UnitConverter.mmToIn(currentTotalPrec)
            : currentTotalPrec;


    final convertedVisibility = visibilityUnit == 'Mile'
        ? UnitConverter.mToMiles(currentVisibility.toDouble()) : UnitConverter.mToKm(currentVisibility.toDouble());


    if (sunset.isBefore(sunrise)) {
      sunset = sunset.add(Duration(days: 1));
    }


        final convertedwindSpeed = windUnit == 'Mph'
        ? UnitConverter.kmhToMph(currentWindSpeed)
        : windUnit == 'M/s'
            ? UnitConverter.kmhToMs(currentWindSpeed) : windUnit == 'Bft' ? UnitConverter.kmhToBeaufort(currentWindSpeed)
            : currentWindSpeed.toStringAsFixed(0);

    return Container(
      margin: EdgeInsets.fromLTRB(12, 0, 12, 0),
      child: Column(
          children: [
            Container(
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
                        bottom: 0,
                        left: 0,
                        right: 0,
                        child: SizedBox(
                          height: 110,
                          child: SvgPicture.string(
                            buildSunPathWithIcon(
                              pathColor: Theme.of(context).colorScheme.primary,
                              percent: 50,
                              outLineColor: Theme.of(context).colorScheme.onSurface,
                              showSun: false
                            ),
                            fit: BoxFit.fill,
                          ),
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
                AspectRatio(
                      aspectRatio: 1,
                      child:  SvgPicture.string(buildPressueSvg(Theme.of(context).colorScheme.primary, Theme.of(context).colorScheme.surfaceContainerHigh, Color(selectedContainerBgIndex), currentPressure.round()), fit: BoxFit.contain),
                  ),
                headerWidgetConditions(headerText: "pressure".tr(), headerIcon: Symbols.compress,),

                Align(
                  alignment: Alignment.center,
                  child: Text(currentPressure == 0.0000001 ? '--' : "${convertedPressure.round()}", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: MediaQuery.of(context).size.width * 0.1, fontWeight: FontWeight.w500),),
                ),
                Padding(
                  padding: EdgeInsets.only(bottom: 30),
               child: Align(
                  alignment: Alignment.bottomCenter,
                  child: Text(pressureUnit, style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 18),),
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
                    AspectRatio(
                      aspectRatio: 1,
                      child: 
                        SvgPicture.string(
                          buildVisibilitySvg(Theme.of(context).colorScheme.tertiaryContainer),
                          fit: BoxFit.contain, 
                        ),
                          ),
                headerWidgetConditions(headerText: "visibility".tr(), headerIcon: Symbols.visibility,),
          
                Align(
                  alignment: Alignment.center,
                  child: Text(currentVisibility == 0.0000001 ? '--' : "${convertedVisibility.round()}", style: TextStyle(color: Theme.of(context).colorScheme.onTertiaryContainer, fontSize: MediaQuery.of(context).size.width * 0.1, fontWeight: FontWeight.w500),),
                ),
                Padding(
                  padding: EdgeInsets.only(bottom: 30),
               child: Align(
                  alignment: Alignment.bottomCenter,
                  child: Text(visibilityUnit, style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: 18),),
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
                      child: Text(currentWindDirc == 0.0000001 ? '--' : getCompassDirection(currentWindDirc), style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: MediaQuery.of(context).size.width * 0.1, fontWeight: FontWeight.w500),),
                    ),

                 Align(
                  alignment: Alignment.bottomCenter,
                  child:  Container(
                  margin: EdgeInsets.only(left: 20, right: 20),
                  height: 55,
                  child: Text(currentWindSpeed == 0.0000001 ? '--' : "$convertedwindSpeed $windUnit", style: 
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
                    AspectRatio(
                      aspectRatio: 1,
                      child: 
                        SvgPicture.string(
                          buildUVSvg(Color(selectedContainerBgIndex), currentUvIndex.round()),
                          fit: BoxFit.contain, 
                        ),
                        ),
                       headerWidgetConditions(headerText: "uv_index".tr(), headerIcon: Symbols.light_mode,),
          
                Align(
                  alignment: Alignment.center,
                  child: Text(currentUvIndex == 0.0000001 ? '--' : "${currentUvIndex.round()}", style: TextStyle(color: Theme.of(context).colorScheme.onSurface, fontSize: MediaQuery.of(context).size.width * 0.1, fontWeight: FontWeight.w500),),
                ),
                Padding(
                  padding: EdgeInsets.only(bottom: MediaQuery.of(context).size.width * 0.11),
               child: Align(
                  alignment: Alignment.bottomCenter,
                  child: Text(currentUvIndex == 0.0000001 ? '--' : getUvIndexType(currentUvIndex.round()), style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 15),),
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
                         Text(currentTotalPrec == 0.0000001 ? '--' : "${double.parse(convertedPrecip.toStringAsFixed(2))}", style: TextStyle(fontSize: MediaQuery.of(context).size.width * 0.11, fontWeight: FontWeight.w500, color: Theme.of(context).colorScheme.onSurface),),
                         Padding(
                          padding: EdgeInsets.only(top: 15),
                          child: Text(precipUnit, style: TextStyle(fontSize: 20, color: Theme.of(context).colorScheme.secondary),),
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
                          Text(currentTotalPrecProb == 0.0000001 ? '--' : "$currentTotalPrecProb%", style: TextStyle(fontWeight: FontWeight.w700, color: Theme.of(context).colorScheme.primary),)
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
