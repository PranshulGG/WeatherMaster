# Expressive Loading Indicator

> Archival notice: Refer to the implementation at [ksokolovskyi/material_loading_indicator](https://github.com/ksokolovskyi/material_loading_indicator).

[![Pub](https://img.shields.io/pub/v/expressive_loading_indicator.svg)](https://pub.dartlang.org/packages/expressive_loading_indicator)

Material 3 Expressive loading indicator ported to Flutter.

[![Expressive Loading Indicator Demo](https://github.com/user-attachments/assets/d5ee926f-7cae-4824-8a37-e844b7ebee4f)](https://github.com/user-attachments/assets/d5ee926f-7cae-4824-8a37-e844b7ebee4f)

## Package

[`expressive_loading_indicator` on pub.dev](https://pub.dev/packages/expressive_loading_indicator).

## Usage

```dart
import 'package:expressive_loading_indicator/expressive_loading_indicator.dart';
import 'package:material_new_shapes/material_new_shapes.dart';

ExpressiveLoadingIndicator(
  // Custom color
  color: Colors.purple,

  // Custom size constraints
  constraints: BoxConstraints(
    minWidth: 64.0,
    minHeight: 64.0,
    maxWidth: 64.0,
    maxHeight: 64.0,
  ),

  // Custom polygon shapes
  polygons: [
    MaterialShapes.softBurst,
    MaterialShapes.pentagon,
    MaterialShapes.pill,
  ],

  // Accessibility
  semanticsLabel: 'Loading',
  semanticsValue: 'In progress',
)
```

## TODO

- [ ] Add support for ContainedLoadingIndicator - A version that shows the loading indicator inside a container with a background
- [ ] Add support for DeterminateLoadingIndicator - A version that morphs depending on the progress value

## Credits

- [Kostia Sokolovskyi](https://github.com/ksokolovskyi) for their initial implementation of [material_shapes](https://github.com/ksokolovskyi/material_shapes).

## External Links

- [Loading Indicator](https://m3.material.io/components/loading-indicator/overview) on Material Design.
- [Kotlin source implementation](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/LoadingIndicator.kt) on Android Code Search.

## License & Attribution

This package is available under the [MIT License](LICENSE).

This package contains Dart ports of components from the Android Open Source Project (AOSP). The original Android components are licensed under the Apache License 2.0.
See [NOTICE](NOTICE) for complete attribution and license details.

## Contributing

Contributions are welcome! Please feel free to submit an Issue or Pull Request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/amazing-feature`)
3. Commit your Changes (`git commit -m 'Add some amazing feature'`)
4. Push to the Branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
