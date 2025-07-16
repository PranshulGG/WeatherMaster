import 'package:flutter/material.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/preferences_helper.dart';

class MeteoModelsPage extends StatefulWidget {
  const MeteoModelsPage({super.key});

  @override
  State<MeteoModelsPage> createState() => _MeteoModelsPageState();
}

class _MeteoModelsPageState extends State<MeteoModelsPage> {

final GlobalKey<ScaffoldMessengerState> _scaffoldMessengerKey = GlobalKey<ScaffoldMessengerState>();


final categorizedModels = {
    'Default': [
      {
        'key': 'best_match',
        'name': 'Best match',
        'desc': 'Combines the best weather models',
      },
    ],
    'Main': [
      {
        'key': 'ecmwf_ifs04',
        'name': 'ECMWF IFS 0.4°',
        'desc': 'Global forecast by ECMWF, slightly lower resolution, highly reliable worldwide.',
      },
      {
        'key': 'ecmwf_ifs025',
        'name': 'ECMWF IFS 0.25°',
        'desc': 'High-resolution version of ECMWF’s global model. Known for top accuracy globally.',
      },
      {
        'key': 'cma_grapes_global',
        'name': 'CMA GRAPES Global',
        'desc': 'Chinese national model from CMA. Good for Asia-Pacific, less accurate globally.',
      },
      {
        'key': 'bom_access_global',
        'name': 'BOM ACCESS Global',
        'desc': "Global model from Australia's Bureau of Meteorology. Best for Australia & Oceania.",
      },
      {
        'key': 'metno_nordic',
        'name': 'MET Norway Nordic',
        'desc': 'Regional model optimized for Norway and Scandinavia. Very accurate for the Nordic region.',
      },
    ],
      'GFS': [
      {
        'key': 'gfs_seamless',
        'name': 'GFS Seamless',
        'desc': 'NOAA’s blended high-res + global forecast. Balanced accuracy with faster updates.',
      },
      {
        'key': 'gfs_global',
        'name': 'GFS Global',
        'desc': 'NOAA’s global model. Fast, free, decent worldwide but less precise locally.',
      },
      {
        'key': 'gfs_hrrr',
        'name': 'GFS HRRR',
        'desc': 'Ultra high-res short-range US model. Best for hyperlocal US forecasts (0–18h).',
      },
      {
        'key': 'gfs_graphcast025',
        'name': 'GFS GraphCast',
        'desc': "AI-powered forecast by Google DeepMind. Experimental, smooth & cutting-edge globally.",
      },
    ],
      'JMA': [
      {
        'key': 'jma_seamless',
        'name': 'JMA Seamless',
        'desc': 'Blended Japan Meteorological Agency model. Accurate in Japan & East Asia.',
      },
      {
        'key': 'jma_msm',
        'name': 'JMA MSM',
        'desc': 'High-res short-range forecast for Japan. Great for 0–36h predictions in Japan.',
      },
      {
        'key': 'jma_gsm',
        'name': 'JMA GSM',
        'desc': 'Japan’s global forecast model. Lower res but reliable in Asia-Pacific.',
      },
    ],
    'DWD': [
      {
        'key': 'icon_seamless',
        'name': 'DWD ICON Seamless',
        'desc': 'German Weather Service’s blended ICON model. Good balance for Europe',
      },
      {
        'key': 'icon_global',
        'name': 'DWD ICON Global',
        'desc': 'Global version of ICON. Precise forecasts worldwide, especially Europe.',
      },
      {
        'key': 'icon_eu',
        'name': 'DWD ICON EU',
        'desc': 'High-res regional ICON for Europe. Best for central & western Europe.',
      },
      {
        'key': 'icon_d2',
        'name': 'DWD ICON D2',
        'desc': 'Ultra high-res short-term forecast for Germany. Ideal for next 24–48h in DE.',
      },
    ],
    'GEM': [
      {
        'key': 'gem_seamless',
        'name': 'GEM Seamless',
        'desc': 'Environment Canada’s blended forecast. Good across North America.',
      },
      {
        'key': 'gem_global',
        'name': 'GEM Global',
        'desc': 'Canada’s global model. Reliable for global forecasts, better in northern latitudes.',
      },
      {
        'key': 'gem_regional',
        'name': 'GEM Regional',
        'desc': 'Canadian regional model. High detail for local areas in Canada.',
      },
      {
        'key': 'gem_hrdps_continental',
        'name': 'GEM HRDPS Continental',
        'desc': 'Ultra high-res Canadian model. Best for pinpoint weather in Canada & nearby US.',
      },
    ],
    'Météo-France': [
      {
        'key': 'meteofrance_seamless',
        'name': 'Météo-France Seamless',
        'desc': 'Blended national model. Accurate in France & nearby countries.',
      },
      {
        'key': 'meteofrance_arpege_world',
        'name': 'Météo-France ARPEGE World',
        'desc': 'Global model from France. Decent worldwide, especially for French territories.',
      },
      {
        'key': 'meteofrance_arpege_europe',
        'name': 'Météo-France ARPEGE Europe',
        'desc': 'Europe-focused ARPEGE. More precise than global version.',
      },
      {
        'key': 'meteofrance_arome_france_hd',
        'name': 'Météo-France ARPEGE France HD',
        'desc': 'High-res national model. Best for local French forecasts.',
      },
    ],
    'ARPAE': [
      {
        'key': 'arpae_cosmo_seamless',
        'name': 'ARPAE Seamless',
        'desc': 'Italian meteorological blend. Optimized for regional accuracy.',
      },
      {
        'key': 'arpae_cosmo_2i',
        'name': 'ARPAE COSMO 2l',
        'desc': 'Very high-res Italian COSMO model. Great for hyperlocal Italy forecasts.',
      },
      {
        'key': 'arpae_cosmo_5m',
        'name': 'ARPAE COSMO 5M',
        'desc': 'COSMO model optimized for Italian mesoscale forecasting. Precise for 1–3 day outlooks.',
      },
    ],
  };

  String? selectedModelKey = PreferencesHelper.getString("selectedWeatherModel") ?? 'best_match';
  
  @override
  Widget build(BuildContext context) {
    return ScaffoldMessenger(
    key: _scaffoldMessengerKey,
    child:  Scaffold(
    body: 
       CustomScrollView(
        slivers: [
          SliverAppBar.large(
            title: Text('weather_models'.tr()),
            titleSpacing: 0,
            backgroundColor: Theme.of(context).colorScheme.surface,
            scrolledUnderElevation: 1,
          ),

          SliverList(
            delegate: SliverChildBuilderDelegate(
              (context, index) {
                final categoryKeys = categorizedModels.keys.toList();
                final categoryName = categoryKeys[index];
                final models = categorizedModels[categoryName]!;

            


                return Padding(
                padding: EdgeInsets.only(left: 10, right: 10),
               child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  spacing: 5,
                  children: [
                    Padding(
                      padding: EdgeInsets.only(top: models[0]['key'] == 'best_match' ? 0 : 24, bottom: 5),
                      child: Text(
                        categoryName,
                        style: TextStyle(color: Theme.of(context).colorScheme.primary, fontWeight: FontWeight.w500, fontSize: 18),
                      ),
                    ),

                     ...models.asMap().entries.map((entry) {
                      final i = entry.key;
                      final model = entry.value;
                      final isSelected = model['key'] == selectedModelKey;

                      final isFirst = i == 0;
                      final isLast = i == models.length - 1;

                      return ListTile(
                        contentPadding: EdgeInsets.only(left : 16, right: 16),
                        horizontalTitleGap: 10,
                        shape: RoundedRectangleBorder(
                            borderRadius: model['key'] == 'best_match' ? BorderRadius.all(Radius.circular(16))
                            : isFirst ? BorderRadius.only(topLeft: Radius.circular(16), topRight: Radius.circular(16), bottomLeft: Radius.circular(6), bottomRight: Radius.circular(6))
                            : isLast ? BorderRadius.only(topLeft: Radius.circular(6), topRight: Radius.circular(6), bottomLeft: Radius.circular(16), bottomRight: Radius.circular(16)) : BorderRadius.all(Radius.circular(6))
                          ),
                        trailing: isSelected ? Icon(Symbols.check, size: 28,) : Icon(Symbols.nest_farsight_weather, size: 28),
                        title: Text(
                          model['name'].toString(),
                          style: TextStyle(
                            fontWeight: FontWeight.w500,
                            color: isSelected ? Theme.of(context).colorScheme.onPrimaryContainer : null,
                            fontSize: 15
                          ),
                        ),
                        subtitle: Text(model['desc'].toString(), style: TextStyle(
                          color: isSelected
                            ? Theme.of(context).colorScheme.onSurfaceVariant
                            : null,
                        ),),
                        tileColor: isSelected
                            ? Theme.of(context).colorScheme.primaryContainer
                            : Theme.of(context).colorScheme.surfaceContainerLowest,
                        onTap: () {
                          setState(() {
                            selectedModelKey = model['key'].toString();
                          });
                        },
                      );
                    }),

                  ],
                  )
                );
              },
              childCount: categorizedModels.length,
            ),
          ),

          SliverToBoxAdapter(
            child: SizedBox(height: MediaQuery.of(context).padding.bottom + 100),
          )
        ]
       ),

       floatingActionButton: FloatingActionButton(onPressed: () {
          PreferencesHelper.setString("selectedWeatherModel", selectedModelKey.toString());
                 _scaffoldMessengerKey.currentState?.showSnackBar(
            SnackBar(content: Text('Model saved. Please refresh the weather')),
          );
       }, backgroundColor: Theme.of(context).colorScheme.tertiaryContainer, foregroundColor: Theme.of(context).colorScheme.onTertiaryContainer, child: Icon(Symbols.save)),
    ),
    );
  }
}