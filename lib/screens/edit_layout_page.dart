import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/layout_config.dart';
import 'dart:convert';
import '../notifiers/layout_provider.dart';
import 'package:provider/provider.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';

class EditLayoutPage extends StatefulWidget {
  const EditLayoutPage({super.key});

    @override
  State<EditLayoutPage> createState() => _EditLayoutPageState();
}

class _EditLayoutPageState extends State<EditLayoutPage> {
  List<LayoutBlockConfig> layoutConfig = [];

  @override
  void initState() {
    super.initState();
    loadLayoutConfig();
  }

Future<void> loadLayoutConfig() async {
  final prefs = await SharedPreferences.getInstance();
  final jsonStringList = prefs.getStringList('layout_config');

  if (jsonStringList != null) {
    layoutConfig = jsonStringList
        .map((json) => LayoutBlockConfig.fromJson(jsonDecode(json)))
        .toList();
  } else {
    layoutConfig = [
      LayoutBlockConfig(type: LayoutBlockType.rain),
      LayoutBlockConfig(type: LayoutBlockType.insights),
      LayoutBlockConfig(type: LayoutBlockType.summary),
      LayoutBlockConfig(type: LayoutBlockType.hourly),
      LayoutBlockConfig(type: LayoutBlockType.daily),
      LayoutBlockConfig(type: LayoutBlockType.conditions),
      LayoutBlockConfig(type: LayoutBlockType.pollen),
    ];
  }

  setState(() {});
}

Future<void> resetLayout() async {
  layoutConfig = [
    LayoutBlockConfig(type: LayoutBlockType.rain),
    LayoutBlockConfig(type: LayoutBlockType.insights),
    LayoutBlockConfig(type: LayoutBlockType.summary),
    LayoutBlockConfig(type: LayoutBlockType.hourly),
    LayoutBlockConfig(type: LayoutBlockType.daily),
    LayoutBlockConfig(type: LayoutBlockType.conditions),
    LayoutBlockConfig(type: LayoutBlockType.pollen),
  ];
  setState(() {});
}

  Future<void> saveLayoutOrder() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList(
  'layout_config',
  layoutConfig.map((e) => jsonEncode(e.toJson())).toList(),
);
  }


  @override
  Widget build(BuildContext context) {

 final layoutProvider = Provider.of<LayoutProvider>(context, listen: false);


    return Scaffold(
      appBar: AppBar(
        title: Text("arrange_items".tr()),
        titleSpacing: 0, scrolledUnderElevation: 0,
        toolbarHeight: 65,
      ),
      body: ReorderableListView(
  onReorder: (oldIndex, newIndex) {
    setState(() {
      if (newIndex > oldIndex) newIndex -= 1;
      final item = layoutConfig.removeAt(oldIndex);
      layoutConfig.insert(newIndex, item);
    });
  },

  proxyDecorator: (child, index, animation) {
    return Material(
      type: MaterialType.transparency,
      child: child,
    );
  },
  children: [
  ...layoutConfig.map((block) {
    return Container(
      key: ValueKey(block.type),
      margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 3),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainerLow,
        borderRadius: BorderRadius.circular(50), 
      ),
      
      child: ListTile(
      contentPadding: EdgeInsets.only(left: 16, right: 16),
      minTileHeight: 65,
      leading: CircleAvatar(foregroundColor: Theme.of(context).colorScheme.onTertiaryContainer, backgroundColor: Theme.of(context).colorScheme.tertiaryContainer, child: Icon(Symbols.drag_handle)),
      title: Text(getTitle(block.type.name.replaceAll(RegExp(r'([a-z])([A-Z])'), r'$1 $2')), style: TextStyle(fontWeight: FontWeight.w500, color: Theme.of(context).colorScheme.onSurface),),
      trailing: IconButton.filled(
      style: ButtonStyle(
        backgroundColor:block.isVisible ? WidgetStateProperty.all(Theme.of(context).colorScheme.surface) : WidgetStateProperty.all(Theme.of(context).colorScheme.errorContainer),
        iconSize: WidgetStateProperty.all(30),
        
      ),
      padding: EdgeInsets.all(10),
      icon: Icon(
        block.isVisible ? Icons.visibility : Icons.visibility_off,
        color: block.isVisible ? Theme.of(context).colorScheme.primary : Theme.of(context).colorScheme.onErrorContainer,
      ),
    onPressed: () {
    setState(() {
      final hiddenCount = layoutConfig.where((b) => !b.isVisible).length;

      if (block.isVisible) {
        if (hiddenCount < 3) {
          block.isVisible = false;
        } else {
          ScaffoldMessenger.of(context)
          ..hideCurrentSnackBar()  
          ..showSnackBar(
            SnackBar(
              content: Text('max_items_3'.tr()),
              duration: const Duration(seconds: 2),
            ),
          );
        }
      } else {
        block.isVisible = true;
      }
    });
  },
  ),
  ),

    );
  }).toList(),
   SizedBox(
    key: ValueKey('bottom_spacing'),
    height: MediaQuery.of(context).padding.bottom + 100
  ),
  ]
  ),
floatingActionButton: Row(
  spacing: 10,
  mainAxisSize: MainAxisSize.min,
  children: [
    FloatingActionButton(
      heroTag: 'btn2',
      onPressed: () async {
        resetLayout();
        await layoutProvider.saveLayout(layoutConfig);
      },
      child: Icon(Icons.refresh), 
    ),
    FloatingActionButton(
      heroTag: 'btn1',
      onPressed: () async {
        await layoutProvider.saveLayout(layoutConfig);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('layout_saved'.tr())),
        );
        Navigator.pop(context);
      },
      child: Icon(Icons.save),
    ),
  ],
),


       
    );
  }
}

String getTitle(String type){

  if(type == 'rain'){
    return 'rain_card'.tr();
  } else if (type == 'insights'){
    return 'insights'.tr();
  } else if (type == 'summary'){
    return 'summary_card'.tr();
  } else if (type == 'hourly'){
    return 'hourly_card'.tr();
  } else if (type == 'daily'){
    return 'daily_card'.tr();  
  } else if (type == 'conditions'){
    return 'current_conditions'.tr();  
  } else if (type == 'pollen'){
    return 'pollen_card'.tr();  
  } else{
    return 'Not found';
  }

}

