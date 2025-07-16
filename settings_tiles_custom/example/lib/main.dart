import 'package:flutter/material.dart';
import 'package:settings_tiles/settings_tiles.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool toggled = true;
  bool checked = true;
  String selectedOption = 'Option 1';
  List<String> selectedOptions = ['Option 1', 'Option 3'];
  double sliderValue = 5.0;
  List<double> customSliderValues = [1, 7, 30];
  double customSliderValue = 7;
  Color color = Colors.blue;
  String text = 'Some text';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('settings_tiles example'),
        ),
        body: Builder(builder: (context) {
          return SafeArea(
            child: SingleChildScrollView(
              child: Center(
                child: Column(
                  children: [
                    SettingSection(
                      title: SettingSectionTitle('Section with all the tiles'),
                      tiles: [
                        const SettingTextTile(
                          icon: SettingTileIcon(Icons.abc),
                          title: Text('Text'),
                          description: Text('This is a text tile'),
                        ),
                        SettingActionTile(
                          icon: SettingTileIcon(Icons.touch_app),
                          title: Text('Action'),
                          description: Text('This is an action tile'),
                          onTap: () {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                content: Text('The action tile was tapped!'),
                                behavior: SnackBarBehavior.floating,
                              ),
                            );
                          },
                        ),
                        SettingSwitchTile(
                          icon: SettingTileIcon(Icons.toggle_on),
                          title: Text('Switch'),
                          description: Text('This is a switch tile'),
                          toggled: toggled,
                          onChanged: (value) {
                            setState(() {
                              toggled = value;
                            });
                          },
                        ),
                        SettingCheckboxTile(
                          icon: SettingTileIcon(Icons.check_box),
                          title: Text('Title'),
                          description: Text('This is a checkbox tile'),
                          checked: checked,
                          onChanged: (value) {
                            if (value == null) {
                              return;
                            }

                            setState(() {
                              checked = value;
                            });
                          },
                        ),
                        SettingSingleOptionTile(
                          icon: SettingTileIcon(Icons.radio_button_on),
                          title: Text('Single option'),
                          value: SettingTileValue(selectedOption),
                          description: Text('This is a single option tile'),
                          dialogTitle: 'Options',
                          options: const ['Option 1', 'Option 2', 'Option 3'],
                          initialOption: selectedOption,
                          onSubmitted: (value) {
                            setState(() {
                              selectedOption = value;
                            });
                          },
                        ),
                        SettingSingleOptionTile.detailed(
                          icon: SettingTileIcon(Icons.radio_button_on),
                          title: Text('Single option (detailed)'),
                          value: SettingTileValue(selectedOption),
                          description:
                              Text('This is a detailed single option tile'),
                          dialogTitle: 'Options',
                          options: const [
                            (
                              value: 'Option 1',
                              title: 'Option n°1',
                              subtitle: 'This is option n°1'
                            ),
                            (
                              value: 'Option 2',
                              title: 'Option n°2',
                              subtitle: 'This is option n°2'
                            ),
                            (
                              value: 'Option 3',
                              title: 'Option n°3',
                              subtitle: 'This is option n°3'
                            ),
                          ],
                          initialOption: selectedOption,
                          onSubmitted: (value) {
                            setState(() {
                              selectedOption = value;
                            });
                          },
                        ),
                        SettingMultipleOptionsTile(
                          icon: SettingTileIcon(Icons.checklist),
                          title: Text('Multiple options'),
                          value: SettingTileValue(selectedOptions.join(' | ')),
                          description: Text('This is a multiple options tile'),
                          dialogTitle: 'Options',
                          options: const ['Option 1', 'Option 2', 'Option 3'],
                          initialOptions: selectedOptions,
                          onSubmitted: (value) {
                            setState(() {
                              selectedOptions = value;
                            });
                          },
                        ),
                        SettingMultipleOptionsTile.detailed(
                          icon: SettingTileIcon(Icons.checklist),
                          title: Text('Multiple options (detailed)'),
                          value: SettingTileValue(selectedOptions.join(' | ')),
                          description:
                              Text('This is a detailed multiple options tile'),
                          dialogTitle: 'Options',
                          options: const [
                            (
                              value: 'Option 1',
                              title: 'Option n°1',
                              subtitle: 'This is option n°1'
                            ),
                            (
                              value: 'Option 2',
                              title: 'Option n°2',
                              subtitle: 'This is option n°2'
                            ),
                            (
                              value: 'Option 3',
                              title: 'Option n°3',
                              subtitle: 'This is option n°3'
                            ),
                          ],
                          initialOptions: selectedOptions,
                          onSubmitted: (value) {
                            setState(() {
                              selectedOptions = value;
                            });
                          },
                        ),
                        SettingTextFieldTile(
                          icon: SettingTileIcon(Icons.text_fields),
                          title: Text('Text field'),
                          value: SettingTileValue(text),
                          description: Text('This is a text field tile'),
                          dialogTitle: 'Text',
                          initialText: text,
                          onSubmitted: (value) {
                            setState(() {
                              text = value;
                            });
                          },
                        ),
                        SettingSliderTile(
                          icon: SettingTileIcon(Icons.linear_scale),
                          title: Text('Slider'),
                          value: SettingTileValue(sliderValue.toString()),
                          description: Text('This is a slider tile'),
                          dialogTitle: 'Slider',
                          min: 1.0,
                          max: 10.0,
                          divisions: 9,
                          initialValue: sliderValue,
                          onSubmitted: (value) {
                            setState(() {
                              sliderValue = value;
                            });
                          },
                        ),
                        SettingCustomSliderTile(
                          icon: SettingTileIcon(Icons.linear_scale),
                          title: Text('Custom slider'),
                          value: SettingTileValue(customSliderValue.toString()),
                          description: Text('This is a custom slider tile'),
                          dialogTitle: 'Custom slider',
                          values: customSliderValues,
                          initialValue: customSliderValue,
                          label: (value) {
                            switch (value) {
                              case 1:
                                return 'Day';
                              case 7:
                                return 'Week';
                              case 30:
                                return 'Month';
                              default:
                                return '';
                            }
                          },
                          onSubmitted: (value) {
                            setState(() {
                              customSliderValue = value;
                            });
                          },
                        ),
                        SettingColorTile(
                          icon: SettingTileIcon(Icons.color_lens),
                          title: Text('Color'),
                          description: Text('This is a color tile'),
                          dialogTitle: 'Color',
                          initialColor: color,
                          onSubmitted: (value) {
                            setState(() {
                              color = value;
                            });
                          },
                        ),
                        SettingAboutTile(
                          applicationIcon: Image.asset(
                            'assets/icon/icon.png',
                            fit: BoxFit.fitWidth,
                            height: 64,
                          ),
                          applicationName: 'Application',
                          applicationVersion: 'v1.0.0',
                        ),
                      ],
                    ),
                    SettingSection(
                      divider: Divider(height: 1),
                      title: SettingSectionTitle(
                          'Section with dividers and disabled tile'),
                      tiles: [
                        SettingActionTile(
                          enabled: false,
                          icon: SettingTileIcon(Icons.touch_app),
                          title: Text('Tile 2'),
                          description: Text('This is a disabled action tile'),
                          onTap: () {},
                        ),
                        const SettingTextTile(
                          icon: SettingTileIcon(Icons.abc),
                          title: Text('Tile 1'),
                          description: Text('This is a text tile'),
                        ),
                        const SettingTextTile(
                          icon: SettingTileIcon(Icons.abc),
                          title: Text('Tile 3'),
                          description: Text('This is a text tile'),
                        ),
                      ],
                    )
                  ],
                ),
              ),
            ),
          );
        }),
      ),
    );
  }
}
