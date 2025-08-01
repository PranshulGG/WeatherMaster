import 'package:flutter/material.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import '../utils/preferences_helper.dart';
import 'package:flutter_markdown/flutter_markdown.dart';


class MeteoModelsPage extends StatefulWidget {
  const MeteoModelsPage({super.key});

  @override
  State<MeteoModelsPage> createState() => _MeteoModelsPageState();
}

class _MeteoModelsPageState extends State<MeteoModelsPage> {

final GlobalKey<ScaffoldMessengerState> _scaffoldMessengerKey = GlobalKey<ScaffoldMessengerState>();


final categorizedModels = {
    'default_text'.tr(): [
      {
        'key': 'best_match',
        'name': 'Best match',
        'desc': 'weather_model_info_1'.tr(),
      },
    ],
    'main_text'.tr(): [
      {
        'key': 'ecmwf_ifs04',
        'name': 'ECMWF IFS 0.4°',
        'desc': 'model_disc_2',
      },
      {
        'key': 'ecmwf_ifs025',
        'name': 'ECMWF IFS 0.25°',
        'desc': 'model_disc_3',
      },
      {
        'key': 'cma_grapes_global',
        'name': 'CMA GRAPES Global',
        'desc': 'model_disc_4',
      },
      {
        'key': 'bom_access_global',
        'name': 'BOM ACCESS Global',
        'desc': "model_disc_5",
      },
      {
        'key': 'metno_nordic',
        'name': 'MET Norway Nordic',
        'desc': 'model_disc_6',
      },
    ],
      'GFS': [
      {
        'key': 'gfs_seamless',
        'name': 'GFS Seamless',
        'desc': 'model_disc_7',
      },
      {
        'key': 'gfs_global',
        'name': 'GFS Global',
        'desc': 'model_disc_8',
      },
      {
        'key': 'gfs_hrrr',
        'name': 'GFS HRRR',
        'desc': 'model_disc_9',
      },
      {
        'key': 'gfs_graphcast025',
        'name': 'GFS GraphCast',
        'desc': "model_disc_10",
      },
    ],
      'JMA': [
      {
        'key': 'jma_seamless',
        'name': 'JMA Seamless',
        'desc': 'model_disc_11',
      },
      {
        'key': 'jma_msm',
        'name': 'JMA MSM',
        'desc': 'model_disc_12',
      },
      {
        'key': 'jma_gsm',
        'name': 'JMA GSM',
        'desc': 'model_disc_13',
      },
    ],
    'DWD': [
      {
        'key': 'icon_seamless',
        'name': 'DWD ICON Seamless',
        'desc': 'model_disc_14',
      },
      {
        'key': 'icon_global',
        'name': 'DWD ICON Global',
        'desc': 'model_disc_15',
      },
      {
        'key': 'icon_eu',
        'name': 'DWD ICON EU',
        'desc': 'model_disc_16',
      },
      {
        'key': 'icon_d2',
        'name': 'DWD ICON D2',
        'desc': 'model_disc_17',
      },
    ],
    'GEM': [
      {
        'key': 'gem_seamless',
        'name': 'GEM Seamless',
        'desc': 'model_disc_18',
      },
      {
        'key': 'gem_global',
        'name': 'GEM Global',
        'desc': 'model_disc_19',
      },
      {
        'key': 'gem_regional',
        'name': 'GEM Regional',
        'desc': 'model_disc_20',
      },
      {
        'key': 'gem_hrdps_continental',
        'name': 'GEM HRDPS Continental',
        'desc': 'model_disc_21',
      },
    ],
    'Météo-France': [
      {
        'key': 'meteofrance_seamless',
        'name': 'Météo-France Seamless',
        'desc': 'model_disc_22',
      },
      {
        'key': 'meteofrance_arpege_world',
        'name': 'Météo-France ARPEGE World',
        'desc': 'model_disc_23',
      },
      {
        'key': 'meteofrance_arpege_europe',
        'name': 'Météo-France ARPEGE Europe',
        'desc': 'model_disc_24',
      },
      {
        'key': 'meteofrance_arome_france_hd',
        'name': 'Météo-France ARPEGE France HD',
        'desc': 'model_disc_25',
      },
    ],
    'ARPAE': [
      {
        'key': 'italia_meteo_arpae_icon_2i',
        'name': 'ItaliaMeteo ARPAE ICON 2I',
        'desc': 'model_disc_27',
      },
    ],
    'KNMI': [
      {
        'key': 'knmi_seamless',
        'name': 'KNMI Seamless (with ECMWF)',
        'desc': 'Blended system integrating KNMI and ECMWF data for seamless global-to-local forecasts across scales.',
      },
      {
        'key': 'knmi_harmonie_arome_europe',
        'name': 'KNMI Harmonie Arome Europe',
        'desc': 'High-resolution convection-permitting model covering Europe. Ideal for regional weather events and mesoscale features.',
      },
      {
        'key': 'knmi_harmonie_arome_netherlands',
        'name': 'KNMI Harmonie Arome Netherlands',
        'desc': 'Ultra-high-resolution model focused on the Netherlands. Excellent for detailed local forecasts including precipitation and wind.',
      },
    ],

    'UKMO': [
      {
        'key': 'ukmo_seamless',
        'name': 'UK Met Office Seamless',
        'desc': 'model_disc_28',
      },
      {
        'key': 'ukmo_global_deterministic_10km',
        'name': 'UK Met Office Global 10km',
        'desc': 'model_disc_29',
      },
      {
        'key': 'ukmo_uk_deterministic_2km',
        'name': 'UK Met Office UK 2km',
        'desc': 'model_disc_30',
      },
    ],
  };


final Map<String, Map<String, String>> dialogContent = {
  'default_text'.tr(): {
  'content': '''
## Best match  
- **Where to use:** Anywhere  

- **Best for:** Users unsure what model to pick  

- **Notes:** Automatically selects the most appropriate model based on location and forecast range.
'''
  },
  'main_text'.tr(): {
  'content': '''
## ECMWF IFS 0.4°
- **Where to use:** Global  

- **Best for:** Long-range, general use, travelers, aviation  

- **Countries:** Long-range, general use, travelers, aviation  

- **Notes:** Very reliable but lower spatial resolution than the 0.25° version. Great fallback model.

---


## ECMWF IFS 0.25°
- **Where to use:** Global (especially Europe, Africa, Middle East)  

- **Best for:** High-accuracy global forecasts (3–10 days out) 

- **Countries:** Europe (top performer), global reliability  

- **Notes:** Arguably the most accurate public global model. Used by many national agencies. Slower updates than GFS.

---


## CMA GRAPES Global
- **Where to use:** China, SE Asia, East Asia  

- **Best for:** Regional use in Asia-Pacific 

- **Countries:** China, Taiwan, Vietnam, Korea 

- **Notes:** Lower global reliability. Performs best around East Asia.

---


## BOM ACCESS Global
- **Where to use:** Australia, New Zealand, Pacific islands  

- **Best for:** Marine and continental Australia forecasts 

- **Countries:** Australia, NZ, Papua New Guinea 

- **Notes:** Tailored for Southern Hemisphere. High-quality over AU/Oceania.

---


## MET Norway Nordic
- **Where to use:** Scandinavia  

- **Best for:** Local precision for Norway, Sweden, Finland 

- **Countries:** Norway (best), Sweden, Denmark 

- **Notes:** Uses MET Norway’s in-house regional models. Very accurate in Nordic terrain.
'''
  },

'GFS': {
  'content': '''
## GFS Seamless
- **Where to use:** Global  

- **Best for:** Fast updates + reasonable accuracy  

- **Countries:** US (especially), global  

- **Notes:** Combines GFS high-res short-term with long-range. Good balance between speed and reliability.

---


## GFS Global
- **Where to use:** Worldwide  

- **Best for:** Free access, rapid updates (4x/day)

- **Countries:** US (default), global fallback  

- **Notes:** Slightly less accurate than ECMWF, but faster and more frequently updated.

---


## GFS HRRR (High-Resolution Rapid Refresh)
- **Where to use:** United States only 

- **Best for:** Very short-term hyperlocal forecasting (0–18 hours) 

- **Countries:** US only 

- **Notes:** Super high-res (3km). Best for storms, hourly planning, airport ops.

---


## GFS GraphCast (AI)
- **Where to use:** Global 

- **Best for:** Smooth animations, AI enthusiasts 

- **Countries:** Global 

- **Notes:** DeepMind’s AI forecast. Still experimental. Very good for visual consistency but not battle-tested yet.

'''
  },

  'JMA': {
  'content': '''
## JMA Seamless
- **Where to use:** Japan, East Asia  

- **Best for:** General public, broad use  

- **Countries:** Japan, Korea, Taiwan, eastern China  

- **Notes:** National model with decent resolution. Blends short and long range.

---


## JMA MSM
- **Where to use:** Japan only  

- **Best for:** Precise forecasts up to 36h

- **Countries:** Japan  

- **Notes:** Very high resolution, excellent for metro areas like Tokyo or Osaka.

---


## JMA GSM
- **Where to use:** Asia-Pacific 

- **Best for:** Global coverage from Japan 

- **Countries:** Japan, Philippines, Taiwan 

- **Notes:** Not as sharp as ECMWF or GFS, but stable in East Asia.

'''
  },

  'DWD': {
  'content': '''
## ICON Seamless
- **Where to use:** Europe (broad), global  

- **Best for:** European-wide continuity  

- **Countries:** Germany, France, Central Europe  

- **Notes:** Transitions smoothly between resolutions. Good for large European areas.

---


## ICON Global
- **Where to use:** Worldwide

- **Best for:** Competitor to GFS/ECMWF globally

- **Countries:** Europe + Global  

- **Notes:** Strong model globally. Among best for Germany and Central Europe.

---


## ICON EU
- **Where to use:** Western and Central Europe 

- **Best for:** Medium-range precision in Europe 

- **Countries:** France, Germany, Poland, Italy 

- **Notes:** High-res model. Great for terrain-rich regions.

## ICON D2
- **Where to use:** Germany only 

- **Best for:** Ultra-local forecasting up to 48h 

- **Countries:** Germany 

- **Notes:** Very detailed — cities, valleys, small regions.

'''
  },

  'GEM': {
  'content': '''
## GEM Seamless
- **Where to use:** North America  

- **Best for:** Mixed-range use for US & Canada  

- **Countries:** Canada, US  

- **Notes:** Blended with HRDPS and regional models. Good balance.

---


## GEM Global
- **Where to use:** Northern Hemisphere

- **Best for:** Canadian territory, arctic routes

- **Countries:** Canada, Greenland, Alaska  

- **Notes:** Moderate global accuracy, optimized for polar areas.

---


## GEM Regional
- **Where to use:** Canada (regional) 

- **Best for:** Weather-sensitive sectors (aviation, snow, hydrology) 

- **Countries:** Canada 

- **Notes:** High-res version for populated areas like Toronto, Quebec, Vancouver.


## GEM HRDPS Continental
- **Where to use:** Canada + US border 

- **Best for:** Short-range events (snowstorms, rainfall) 

- **Countries:** Canada, North US 

- **Notes:** Very high-res (2.5km). Focused on precision forecasting.

'''
  },

  'Météo-France': {
  'content': '''
## Météo-France Seamless
- **Where to use:** France and surroundings  

- **Best for:** General use in France and nearby countries  

- **Countries:** France, Spain, Belgium  

- **Notes:** Blended for smoother transitions. High confidence over France.

---


## Météo-France ARPEGE World
- **Where to use:** French overseas territories

- **Best for:** French Guiana, La Réunion, Polynesia

- **Countries:** France + territories  

- **Notes:** Medium-res global forecast

---


## Météo-France ARPEGE Europe
- **Where to use:** France, Western Europe

- **Best for:** Continental Europe weather 

- **Countries:** France, Belgium, Italy 

- **Notes:** Higher resolution than global ARPEGE.


## Météo-France ARPEGE France HD
- **Where to use:** France only 

- **Best for:** Short-term, hyperlocal accuracy 

- **Countries:** France 

- **Notes:** Excellent detail — rain, fog, wind, storm.

'''
  },

  'ARPAE': {
  'content': '''
## ItaliaMeteo ARPAE ICON 2I
- **Where to use:** Italy only  

- **Best for:** Short-term high-res forecasting  

- **Countries:** Italy  

- **Notes:** Based on ICON with ARPAE tuning. Strong for mountainous terrain.

'''
  },

'UKMO': {
  'content': '''
## UKMO Seamless
- **Where to use:** UK + international routes  

- **Best for:** Combined global/local coverage  

- **Countries:** UK, global  

- **Notes:** Smooth model transitions. Good for flights, sea, and national forecasts.

---


## UKMO Global 10km
- **Where to use:** Worldwide

- **Best for:** Medium-range forecasts (3–7 days)

- **Countries:** UK, Commonwealth countries  

- **Notes:** Strong global model. Good tropical performance.

---


## UKMO UK 2km
- **Where to use:** UK only

- **Best for:** Precise local UK weather 

- **Countries:** England, Scotland, Wales, NI 

- **Notes:** One of the best models for dense urban UK settings.

'''
  },

  'KNMI': {
  'content': '''
## KNMI Seamless (with ECMWF)
- **Where to use:** Netherlands + Europe + global routes

- **Best for:** Smooth transition across forecast scales

- **Countries:** Netherlands, broader Europe, global

- **Notes:** Combines KNMI short-range and ECMWF global models. Useful for aviation, marine, and national-level planning.

---

## KNMI Harmonie Arome Europe
- **Where to use:** Continental Europe

- **Best for:** Mesoscale weather features (e.g. thunderstorms, wind gusts)

- **Countries:** European countries including Netherlands, Belgium, Germany, France

- **Notes:** Convection-permitting resolution. Very good for regional forecasting across European terrain.

---

## KNMI Harmonie Arome Netherlands
- **Where to use:** Netherlands (urban and coastal areas)

- **Best for:** High-resolution local forecasting (0–48 hours)

- **Countries:** Netherlands

- **Notes:** Ultra-fine resolution. Excellent for precipitation, wind, and local effects like sea breezes or fog.

'''
},


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
                  spacing: 3,
                  children: [
                    Padding(
                      padding: EdgeInsets.only(top: models[0]['key'] == 'best_match' ? 0 : 24, bottom: 0),
                      child: Row(
                      children: [
                       Text(
                        categoryName,
                        style: TextStyle(color: Theme.of(context).colorScheme.primary, fontWeight: FontWeight.w500, fontSize: 18),
                      ),
                      IconButton(onPressed: () {

                  if (dialogContent.containsKey(categoryName)) {
                    final modelInfo = dialogContent[categoryName]!;

                            Navigator.of(context).push( 
                                PageRouteBuilder(
                                  opaque: true,
                                  fullscreenDialog: true,
                                  reverseTransitionDuration: Duration(milliseconds: 200),
                                  pageBuilder: (context, animation, secondaryAnimation) {
                                return ShowModelsInfo(markdownData: modelInfo['content']!.trimLeft(), heading: categoryName ,);
                              },
                              transitionsBuilder: (context, animation, secondaryAnimation, child) {
                                return FadeTransition(
                                  opacity: animation,
                                  child: child,
                                  
                                );
                              }, 
                            ),
            
                          );
                        }
                    },
                    icon: Icon(Icons.info_outline))
                      ]
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
                        subtitle: Text(model['desc'].toString().tr(), style: TextStyle(
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
          PreferencesHelper.setBool("ModelChanged", true);
                 _scaffoldMessengerKey.currentState?.showSnackBar(
            SnackBar(content: Text('model_saved_snack'.tr())),
          );
       }, backgroundColor: Theme.of(context).colorScheme.tertiaryContainer, foregroundColor: Theme.of(context).colorScheme.onTertiaryContainer, child: Icon(Symbols.save)),
    ),
    );
  }
}


class ShowModelsInfo extends StatelessWidget {
  final markdownData;
  final heading;

  const ShowModelsInfo({super.key, required this.markdownData, required this.heading});

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            titleSpacing: 0,
            automaticallyImplyLeading: false,
            flexibleSpace: FlexibleSpaceBar(
              title: Row(
                spacing: 5,
                children: [
                  Text(heading.toString())
                ],
              ),
              expandedTitleScale: 1.3,
              titlePadding: EdgeInsets.all(16),
            ),
            backgroundColor: Theme.of(context).colorScheme.surface,
            expandedHeight: 100,
            scrolledUnderElevation: 1,
            actions: [
              IconButton(onPressed: () {Navigator.pop(context);}, icon: Icon(Symbols.close, weight: 600,)),
              SizedBox(width: 5)
            ],
          ),

           SliverToBoxAdapter(
            child: Column(
              children: [
                Padding(
                  padding: EdgeInsets.only(
                    top: 0,
                    bottom: MediaQuery.of(context).padding.bottom + 10,
                    left: 16,
                    right: 16,
                  ),
                  child: MarkdownBody(
                    data: markdownData,
                    styleSheet: MarkdownStyleSheet(
                      h2: TextStyle(color: Theme.of(context).colorScheme.primary, fontWeight: FontWeight.w500),
                     
                      horizontalRuleDecoration: BoxDecoration(
                          border: Border(
                            top: BorderSide(
                              color: Colors.transparent,
                              width: 2,
                            ),
                          )
                      ),
                    ),
                  ),
                )
              ],
            ),
          )

        ]
      )
    );
  }
}