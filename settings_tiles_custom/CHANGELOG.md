# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 6.0.1

### Added

- Export the base `SettingsTile` class

## 6.0.0

### BREAKING CHANGES

- Reworked the tiles to be based on a `ListTile`, changing their appearance (use the `SettingSectionTitle`, `SettingTileIcon` and `SettingTileValue` widgets to have pre-styled widgets)
- Changed the `icon`, `title`, `value` and `description` parameters to take a `Widget`

### Added

- Added the `applicationName` and `applicationVersion` parameters to the `SettingAboutTile`

### Removed

- Removed the padding above a `SettingSection`

## 5.0.0

### BREAKING CHANGES

- The `divider` parameter of the `SettingSection` is `null` by default
- Removed the `TextStyle` and `Color` extensions from the package exports

## 4.1.0

- Add `SettingCustomSliderTile` that displays a slider with a set of custom values

## 4.0.0

### BREAKING CHANGES

- Update `SettingSingleOptionTile.detailed` so that the `options` parameter is a list of `MultipleOptionsDetails` record

## 3.0.0

### BREAKING CHANGES

- Update `SettingMultipleOptionsTile.detailed` so that the `options` parameter is a list of `MultipleOptionsDetails` record

### Added

- Minimum number of selected options for `SettingMultipleOptionsTile`

## 2.0.4

- Update example

## 2.0.3

### Changed

- Do not use the root navigator to show the dialogs

## 2.0.2

### Changed

- Reduce section padding

## 2.0.1

### Changed

- Reduce section padding

## 2.0.0

### BREAKING CHANGES

- Remove override widgets for `SettingSliderTile`, `SettingTextFieldTile` and `SettingColorTile`

### Added

- Allow to provide a title and a description to the options of `SettingSingleOptionTile` and `SettingMultipleOptionsTile`
- Allow to customize the label of the slider label of `SettingSliderTile`
- On changed callback for `SettingSliderTile`
- Use adaptive dialogs when applicable

### Changed

- Reduce the title size
- Improve the icon spacing

## 1.0.0

Initial release.
