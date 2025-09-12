// Port of Android's LoadingIndicator
// Source: androidx/compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/LoadingIndicator.kt
// Copyright (c) 2024 The Android Open Source Project
// Licensed under the Apache License, Version 2.0
//
// Dart port by Tamim Arafat
// Copyright (c) 2025 Tamim Arafat
// Licensed under MIT License

import 'dart:async';
import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:flutter/physics.dart';
import 'package:flutter/semantics.dart';
import 'package:material_new_shapes/material_new_shapes.dart';

/// A Material Design loading indicator.
///
/// This version of the loading indicator morphs between its [polygons] shapes.
/// ![Loading indicator image](https://developer.android.com/images/reference/androidx/compose/material3/loading-indicator.png)
class ExpressiveLoadingIndicator extends ProgressIndicator {
  /// A list of [RoundedPolygon]s for the sequence of shapes this loading indicator
  /// will morph between. The loading indicator expects at least two items in that list.
  final List<RoundedPolygon>? polygons;

  /// Defines minimum and maximum sizes for an [ExpressiveLoadingIndicator].
  /// If null, then the [ProgressIndicatorThemeData.constraints] will be used. Otherwise, defaults to a minimum width and height of 48 pixels.
  final BoxConstraints? constraints;

  final double activeSize;

  const ExpressiveLoadingIndicator({
    super.key,
    super.color,
    this.polygons,
    this.activeSize = 38.0,
    this.constraints,
    super.semanticsLabel,
    super.semanticsValue,
  }) : assert(polygons != null ? polygons.length > 1 : true);

  @override
  State<ExpressiveLoadingIndicator> createState() =>
      _ExpressiveLoadingIndicatorState();
}

class _ExpressiveLoadingIndicatorState extends State<ExpressiveLoadingIndicator>
    with TickerProviderStateMixin {
  static final List<RoundedPolygon> _defaultPolygons = [
    MaterialShapes.softBurst,
    MaterialShapes.cookie9Sided,
    MaterialShapes.pentagon,
    MaterialShapes.pill,
    MaterialShapes.sunny,
    MaterialShapes.cookie4Sided,
    MaterialShapes.oval,
  ];

  static final BoxConstraints _defaultConstraints = BoxConstraints(
    minWidth: 48.0,
    minHeight: 48.0,
    maxWidth: 48.0,
    maxHeight: 48.0,
  ); // default from kotlin source

  late final List<RoundedPolygon> _polygons;

  static const int _globalRotationDurationMs = 4666;
  static const int _morphIntervalMs = 650;
  static const double _fullRotation = 360.0;

  static const double _quarterRotation = _fullRotation / 4;
  static const double _activeSize = 48; // based on source spec

  late final List<Morph> _morphSequence;

  late final AnimationController _morphController;
  late final AnimationController _globalRotationController;
  int _currentMorphIndex = 0;
  double _morphRotationTargetAngle = _quarterRotation;

  Timer? _morphTimer;

  final _morphAnimationSpec = SpringSimulation(
    SpringDescription.withDampingRatio(ratio: 0.6, stiffness: 200.0, mass: 1.0),
    0.0,
    1.0,
    5.0,
    snapToEnd: true,
  );

  late BoxConstraints _constraints;
  late Color _color;

  @override
  Widget build(BuildContext context) {
    final indicatorTheme = ProgressIndicatorTheme.of(context);
    _color =
        widget.color ??
        indicatorTheme.color ??
        Theme.of(context).colorScheme.primary;
    _constraints =
        widget.constraints ?? indicatorTheme.constraints ?? _defaultConstraints;

    final activeIndicatorScale =
        widget.activeSize /
        math.min(_constraints.maxWidth, _constraints.maxHeight);

    final shapesScaleFactor =
        _calculateScaleFactor(_polygons) * activeIndicatorScale;

    return Semantics.fromProperties(
      properties: SemanticsProperties(
        label: widget.semanticsLabel,
        value: widget.semanticsValue,
      ),
      child: RepaintBoundary(
        child: ConstrainedBox(
          constraints: _constraints,
          child: AspectRatio(
            aspectRatio: 1.0,
            child: AnimatedBuilder(
              animation: Listenable.merge([
                _morphController,
                _globalRotationController,
              ]),
              builder: (context, child) {
                final morphProgress = _morphController.value.clamp(0.0, 1.0);
                final globalRotationDegrees =
                    _globalRotationController.value * _fullRotation;

                // calculate total rotation (clockwise, matching Kotlin implementation)
                final totalRotationDegrees =
                    morphProgress * _quarterRotation +
                    _morphRotationTargetAngle +
                    globalRotationDegrees;

                final totalRotationRadians =
                    totalRotationDegrees * (math.pi / 180.0);

                return Transform.rotate(
                  angle: totalRotationRadians,
                  child: CustomPaint(
                    painter: _MorphPainter(
                      morph: _morphSequence[_currentMorphIndex],
                      progress: morphProgress,
                      color: _color,
                      scaleFactor: shapesScaleFactor,
                      repaint: Listenable.merge([
                        _morphController,
                        _globalRotationController,
                      ]),
                    ),
                    child: const SizedBox.expand(),
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _morphTimer?.cancel();
    _morphController.dispose();
    _globalRotationController.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();

    _polygons = widget.polygons ?? _defaultPolygons;

    _morphSequence = _createMorphSequence(_polygons, circularSequence: true);

    _morphController = AnimationController.unbounded(vsync: this);

    // continuous linear rotation
    _globalRotationController = AnimationController(
      duration: const Duration(milliseconds: _globalRotationDurationMs),
      vsync: this,
    );

    _startAnimations();
  }

  List<Morph> _createMorphSequence(
    List<RoundedPolygon> polygons, {
    required bool circularSequence,
  }) {
    final morphs = <Morph>[];

    for (int i = 0; i < polygons.length; i++) {
      if (i + 1 < polygons.length) {
        morphs.add(Morph(polygons[i], polygons[i + 1]));
      } else if (circularSequence) {
        // morph from last shape back to first shape
        morphs.add(Morph(polygons[i], polygons[0]));
      }
    }

    return morphs;
  }

  /// Calculates a scale factor that will be used when scaling the provided [RoundedPolygon]s into a
  /// specified sized container.
  ///
  /// Since the polygons may rotate, a simple [RoundedPolygon.calculateBounds] is not enough to
  /// determine the size the polygon will occupy as it rotates. Using the simple bounds calculation may
  /// result in a clipped shape.
  ///
  /// This function calculates and returns a scale factor by utilizing the
  /// [RoundedPolygon.calculateMaxBounds] and comparing its result to the
  /// [RoundedPolygon.calculateBounds]. The scale factor can later be used when calling [processPath].
  ///
  /// Port of Kotlin implementation.
  double _calculateScaleFactor(List<RoundedPolygon> polygons) {
    var scaleFactor = 1.0;

    for (final polygon in polygons) {
      final bounds = polygon.calculateBounds();
      final maxBounds = polygon.calculateMaxBounds();

      final boundsWidth = bounds[2] - bounds[0];
      final boundsHeight = bounds[3] - bounds[1];

      final maxBoundsWidth = maxBounds[2] - maxBounds[0];
      final maxBoundsHeight = maxBounds[3] - maxBounds[1];

      final scaleX = boundsWidth / maxBoundsWidth;
      final scaleY = boundsHeight / maxBoundsHeight;

      // We use max(scaleX, scaleY) to handle cases like a pill-shape that can throw off the
      // entire calculation.
      scaleFactor = math.min(scaleFactor, math.max(scaleX, scaleY));
    }

    return scaleFactor;
  }

  void _startAnimations() {
    // infinite global rotation
    _globalRotationController.repeat();

    // periodic morph cycle
    _morphTimer = Timer.periodic(
      const Duration(milliseconds: _morphIntervalMs),
      (_) => _startMorphCycle(),
    );

    _startMorphCycle();
  }

  void _startMorphCycle() {
    if (!mounted) return;

    // move to next morph in sequence
    _currentMorphIndex = (_currentMorphIndex + 1) % _morphSequence.length;

    // accumulate rotation target
    _morphRotationTargetAngle =
        (_morphRotationTargetAngle + _quarterRotation) % _fullRotation;

    // Reset and start morph animation
    _morphController
      ..value = 0.0
      ..animateWith(_morphAnimationSpec);
  }
}

class _MorphPainter extends CustomPainter {
  final Morph morph;
  final double progress;
  final Color color;

  /// A scale factor that will be taken into account uniformly when the [path] is
  /// scaled (i.e. the scaleX would be the [size] width x the scale factor, and the scaleY would be
  /// the [size] height x the scale factor)
  final double scaleFactor;

  _MorphPainter({
    required this.morph,
    required this.progress,
    required this.color,
    this.scaleFactor = 1.0,
    super.repaint,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final path = morph.toPath(progress: progress);
    final processedPath = _processPath(path, size);
    canvas.drawPath(
      processedPath,
      Paint()
        ..style = PaintingStyle.fill
        ..color = color,
    );
  }

  @override
  bool shouldRepaint(_MorphPainter oldDelegate) {
    return oldDelegate.morph != morph ||
        oldDelegate.progress != progress ||
        oldDelegate.color != color ||
        oldDelegate.scaleFactor != scaleFactor;
  }

  /// Process a given path to scale it and center it inside the given size.
  ///
  /// [path] takes a [Path] that was generated by a _normalized_ [Morph] or [RoundedPolygon].
  /// [size] takes a [Size] that the provided [path] is going to be scaled and centered into.
  Path _processPath(Path path, Size size) {
    // a [Matrix] that would be used to apply the scaling. Note that any provided
    // matrix will be reset in this function.
    final Matrix4 scaleMatrix = Matrix4.diagonal3Values(
      size.width * scaleFactor,
      size.height * scaleFactor,
      1,
    );
    final Path scaledPath = path.transform(scaleMatrix.storage);

    // Translate the path so that its center aligns with the center of the container.
    final Rect bounds = scaledPath.getBounds();
    final Offset translation =
        Offset(size.width / 2, size.height / 2) - bounds.center;
    final Path finalPath = scaledPath.shift(translation);

    return finalPath;
  }
}
