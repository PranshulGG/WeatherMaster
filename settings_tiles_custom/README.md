# settings_tiles

![Pub Version](https://img.shields.io/pub/v/settings_tiles)

A collection of settings tiles to easily create a settings screen.

## Installing

See the [installing instructions](https://pub.dev/packages/settings_tiles/install).

## Usage

### Section

Create a setting section that displays a list of setting tiles, optionally separated by a divider, with a title at the top:

```dart
SettingSection(
  title: SettingSectionTitle('A setting section'),
  tiles: [
    // A list of tiles
  ],
)
```

Use a `SettingSectionTitle` to get a pre-styled title.

To remove the dividers between the setting tiles, set the `divider` parameter to `null`.

### Tiles

Use a `SettingTileIcon` to get a pre-styled icon and a `SettingTileValue` to get a pre-styled value.

#### Text

A simple tile that only displays text and has no interactions:

```dart
const SettingTextTile(
  icon: SettingTileIcon(Icons.abc),
  title: Text('Text'),
  description: Text('This is a text tile'),
)
```

#### Action

A tile that calls an action when tapped:

```dart
SettingActionTile(
  icon: SettingTileIcon(Icons.touch_app),
  title: Text('Action'),
  description: Text('This is an action tile'),
  onTap: () {
    print('The action tile was tapped');
  },
),
```

#### Switch

A tile that displays a switch:

```dart
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
)
```

#### Checkbox

A tile that displays a checkbox:

```dart
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
)
```

#### Single option

A tile that shows a dialog with a single option to choose:

```dart
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
)
```

The `.detailed` named constructor allows you to specify the title and an optional subtitle for each option.
The list of options must be a list of `MultipleOptionsDetails`, which is a record containing the value, the title and the optional subtitle of the option:

```dart
SettingSingleOptionTile.detailed(
  icon: SettingTileIcon(Icons.radio_button_on),
  title: Text('Single option (detailed)'),
  value: SettingTileValue(selectedOption),
  description: Text('This is a detailed single option tile'),
  dialogTitle: 'Options',
  options: const [
    (value: 'Option 1', title: 'Option n°1', subtitle: 'This is option n°1'),
    (value: 'Option 2', title: 'Option n°2', subtitle: 'This is option n°2'),
    (value: 'Option 3', title: 'Option n°3', subtitle: 'This is option n°3'),
  ],
  initialOption: selectedOption,
  onSubmitted: (value) {
    setState(() {
      selectedOption = value;
    });
  },
),
```

#### Multiple options

A tile that shows a dialog with multiple options to choose from:

```dart
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
)
```

The `.detailed` named constructor allows you to specify the title and an optional subtitle for each option.
The list of options must be a list of `MultipleOptionsDetails`, which is a record containing the value, the title and the optional subtitle of the option:

```dart
SettingMultipleOptionsTile.detailed(
  icon: SettingTileIcon(Icons.checklist),
  title: Text('Multiple options (detailed)'),
  value: SettingTileValue(selectedOptions.join(' | ')),
  description: Text('This is a detailed multiple options tile'),
  dialogTitle: 'Options',
  options: const [
    (value: 'Option 1', title: 'Option n°1', subtitle: 'This is option n°1'),
    (value: 'Option 2', title: 'Option n°2', subtitle: 'This is option n°2'),
    (value: 'Option 3', title: 'Option n°3', subtitle: 'This is option n°3'),
  ],
  initialOptions: selectedOptions,
  onSubmitted: (value) {
    setState(() {
      selectedOptions = value;
    });
  },
),
``` 

#### Text field

A tile that shows a dialog with a text field:

```dart
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
)
```

#### Slider

A tile that shows a dialog with a slider:

```dart
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
)
```

#### Custom slider

A tile that shows a dialog with a custom slider, allowing you to specify the values and their labels:

```dart
SettingCustomSliderTile(
  icon: SettingTileIcon(Icons.linear_scale),
  title: Text('Custom slider'),
  value: SettingTileValue(customSliderValue.toString()),
  description: Text('This is a custom slider tile'),
  dialogTitle: 'Custom slider',
  values: [1, 7, 30],
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
        throw Exception();
    }
  },
  onSubmitted: (value) {
    setState(() {
      customSliderValue = value;
    });
  },
),
```

#### Color picker

A tile that shows a dialog with some color pickers and a preview of the picked color:

```dart
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
)
```

To change the available color pickers, set the `colorPickers` parameter. To enable all the color pickers, set it to `ColorPickerType.values`.

#### About

A tile that shows information about the application and opens Flutter's `AboutDialog` when tapped:

```dart
const SettingAboutTile(
  applicationIcon: Image.asset('assets/icon/icon.png'),
  applicationName: 'Application name',
  applicationVersion: 'v1.0.0',
)
```

## Example

See the [example app](https://pub.dev/packages/settings_tiles/example).
