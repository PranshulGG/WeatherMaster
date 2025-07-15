import 'package:material_color_utilities/material_color_utilities.dart';
import 'package:flutter/material.dart';

final CorePalette palette = CorePalette.of(Colors.blueAccent.toARGB32());
final CorePalette paletteWeather = CorePalette.of(const Color.fromARGB(255, 1, 46, 124).toARGB32());

ColorScheme customScheme = ColorScheme(
  brightness: Brightness.light,
  primary: Color(palette.primary.get(40)),
  onPrimary: Color(palette.primary.get(100)),
  primaryContainer: Color(palette.primary.get(90)),
  onPrimaryContainer: Color(palette.primary.get(10)),
  secondary: Color(palette.secondary.get(40)),
  onSecondary: Color(palette.secondary.get(100)),
  secondaryContainer: Color(palette.secondary.get(90)),
  onSecondaryContainer: Color(palette.secondary.get(10)),
  tertiary: Color(palette.tertiary.get(40)),
  onTertiary: Color(palette.tertiary.get(100)),
  tertiaryContainer: Color(palette.tertiary.get(90)),
  onTertiaryContainer: Color(palette.tertiary.get(10)),
  surface: Color(palette.neutral.get(98)),
  onSurface: Color(palette.neutral.get(10)),
  onSurfaceVariant: Color(palette.neutralVariant.get(30)),
  error: Color(palette.error.get(40)),
  onError: Color(palette.error.get(100)),
  errorContainer: Color(palette.error.get(90)),
  onErrorContainer: Color(palette.error.get(10)),
  inversePrimary: Color(palette.primary.get(80)),
  inverseSurface: Color(palette.neutral.get(20)),
  outline: Color(palette.neutralVariant.get(50)),
  outlineVariant: Color(palette.neutralVariant.get(80)),
  shadow: Color(palette.neutral.get(0)),
  surfaceContainerHigh: Color(palette.neutral.get(92)),
  surfaceContainerLow: Color(palette.neutral.get(96)),
  surfaceContainer: Color(palette.neutral.get(94)),
  surfaceContainerHighest: Color(palette.neutral.get(90)),
  surfaceContainerLowest: Color(palette.neutral.get(100))
);

ColorScheme customDarkScheme = ColorScheme(
  brightness: Brightness.dark,
  primary: Color(palette.primary.get(80)),
  onPrimary: Color(palette.primary.get(20)),
  primaryContainer: Color(palette.primary.get(30)),
  onPrimaryContainer: Color(palette.primary.get(90)),
  secondary: Color(palette.secondary.get(80)),
  onSecondary: Color(palette.secondary.get(20)),
  secondaryContainer: Color(palette.secondary.get(30)),
  onSecondaryContainer: Color(palette.secondary.get(90)),
  tertiary: Color(palette.tertiary.get(80)),
  onTertiary: Color(palette.tertiary.get(20)),
  tertiaryContainer: Color(palette.tertiary.get(30)),
  onTertiaryContainer: Color(palette.tertiary.get(90)),
  surface: Color(palette.neutral.get(6)),
  onSurface: Color(palette.neutral.get(90)),
  onSurfaceVariant: Color(palette.neutralVariant.get(80)),
  error: Color(palette.error.get(80)),
  onError: Color(palette.error.get(20)),
  errorContainer: Color(palette.error.get(30)),
  onErrorContainer: Color(palette.error.get(90)),
  inversePrimary: Color(palette.primary.get(40)),
  inverseSurface: Color(palette.neutral.get(90)),
  outline: Color(palette.neutralVariant.get(60)),
  outlineVariant: Color(palette.neutralVariant.get(30)),
  shadow: Color(palette.neutral.get(0)),
  surfaceContainerHigh: Color(palette.neutral.get(17)),
  surfaceContainerLow: Color(palette.neutral.get(10)),
  surfaceContainer: Color(palette.neutral.get(12)),
  surfaceContainerHighest: Color(palette.neutral.get(22)),
  surfaceContainerLowest: Color(palette.neutral.get(4))
);



