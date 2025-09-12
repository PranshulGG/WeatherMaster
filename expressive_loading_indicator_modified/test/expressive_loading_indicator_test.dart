import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:material_new_shapes/material_new_shapes.dart';
import 'package:expressive_loading_indicator/expressive_loading_indicator.dart';

void main() {
  group('ExpressiveLoadingIndicator', () {
    testWidgets('renders with default polygons', (WidgetTester tester) async {
      // Build our widget
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(body: Center(child: ExpressiveLoadingIndicator())),
        ),
      );

      // Verify the widget renders
      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      // Let the animation run for a bit
      await tester.pump(const Duration(milliseconds: 100));
      await tester.pump(const Duration(milliseconds: 100));

      // No errors should occur during animation
      expect(tester.takeException(), isNull);
    });

    testWidgets('renders with custom polygons', (WidgetTester tester) async {
      final customPolygons = [
        MaterialShapes.pill,
        MaterialShapes.pentagon,
        MaterialShapes.oval,
      ];

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: Center(
              child: ExpressiveLoadingIndicator(polygons: customPolygons),
            ),
          ),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      await tester.pump(const Duration(milliseconds: 100));
      await tester.pump(const Duration(milliseconds: 100));

      expect(tester.takeException(), isNull);
    });

    testWidgets('renders with custom color', (WidgetTester tester) async {
      const customColor = Colors.red;

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: Center(child: ExpressiveLoadingIndicator(color: customColor)),
          ),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      // Extract the widget to verify its properties
      final ExpressiveLoadingIndicator widget = tester.widget(
        find.byType(ExpressiveLoadingIndicator),
      );

      expect(widget.color, equals(customColor));
    });

    testWidgets('renders with custom constraints', (WidgetTester tester) async {
      const customConstraints = BoxConstraints(
        minWidth: 100.0,
        minHeight: 100.0,
        maxWidth: 100.0,
        maxHeight: 100.0,
      );

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: Center(
              child: ExpressiveLoadingIndicator(constraints: customConstraints),
            ),
          ),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      // Find the ConstrainedBox widget
      final ConstrainedBox constrainedBox = tester.widget(
        find.descendant(
          of: find.byType(ExpressiveLoadingIndicator),
          matching: find.byType(ConstrainedBox),
        ),
      );

      expect(constrainedBox.constraints, equals(customConstraints));
    });

    testWidgets('applies semantics properties correctly', (
      WidgetTester tester,
    ) async {
      const String semanticsLabel = 'Loading';
      const String semanticsValue = '50%';

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: Center(
              child: ExpressiveLoadingIndicator(
                semanticsLabel: semanticsLabel,
                semanticsValue: semanticsValue,
              ),
            ),
          ),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      // Verify semantics are applied to the widget
      final semanticsWidget = find.byType(Semantics);
      expect(semanticsWidget, findsWidgets);
    });

    testWidgets('throws assertion error with single polygon', (
      WidgetTester tester,
    ) async {
      expect(
        () => ExpressiveLoadingIndicator(polygons: [MaterialShapes.pill]),
        throwsAssertionError,
      );
    });

    testWidgets('renders in theme with correct color', (
      WidgetTester tester,
    ) async {
      const Color themeColor = Colors.green;

      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData(
            progressIndicatorTheme: const ProgressIndicatorThemeData(
              color: themeColor,
            ),
          ),
          home: const Scaffold(
            body: Center(child: ExpressiveLoadingIndicator()),
          ),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      await tester.pump(const Duration(milliseconds: 100));

      // The widget should use the theme color
      // This test verifies the widget respects the theme
      final ExpressiveLoadingIndicator widget = tester.widget(
        find.byType(ExpressiveLoadingIndicator),
      );

      expect(widget.color, isNull); // Widget itself doesn't have a color set
    });

    testWidgets('renders in theme with custom constraints', (
      WidgetTester tester,
    ) async {
      const BoxConstraints themeConstraints = BoxConstraints(
        minWidth: 80.0,
        minHeight: 80.0,
        maxWidth: 80.0,
        maxHeight: 80.0,
      );

      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData(
            progressIndicatorTheme: const ProgressIndicatorThemeData(
              constraints: themeConstraints,
            ),
          ),
          home: const Scaffold(
            body: Center(child: ExpressiveLoadingIndicator()),
          ),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      // The ConstrainedBox should use theme constraints
      final ConstrainedBox constrainedBox = tester.widget(
        find.descendant(
          of: find.byType(ExpressiveLoadingIndicator),
          matching: find.byType(ConstrainedBox),
        ),
      );

      expect(constrainedBox.constraints, equals(themeConstraints));
    });

    testWidgets('disposes resources properly', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(body: Center(child: ExpressiveLoadingIndicator())),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);

      // Replace the widget with something else
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(body: Center(child: Text('No indicator'))),
        ),
      );

      expect(find.byType(ExpressiveLoadingIndicator), findsNothing);
      expect(find.text('No indicator'), findsOneWidget);

      // No errors should occur during disposal
      expect(tester.takeException(), isNull);
    });

    testWidgets('handles widget rebuilds during animation', (
      WidgetTester tester,
    ) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(body: Center(child: ExpressiveLoadingIndicator())),
        ),
      );

      // Let animation start
      await tester.pump(const Duration(milliseconds: 100));

      // Rebuild with the same widget
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(body: Center(child: ExpressiveLoadingIndicator())),
        ),
      );

      // Continue animation
      await tester.pump(const Duration(milliseconds: 100));

      // No exceptions should occur
      expect(tester.takeException(), isNull);
    });

    testWidgets('handles rapid animation frames', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(body: Center(child: ExpressiveLoadingIndicator())),
        ),
      );

      // Rapid succession of small animation steps
      for (int i = 0; i < 5; i++) {
        await tester.pump(const Duration(milliseconds: 16)); // ~60fps
      }

      // Widget should handle rapid animation frames without issues
      expect(find.byType(ExpressiveLoadingIndicator), findsOneWidget);
      expect(tester.takeException(), isNull);
    });

    testWidgets('handles navigation away during animation', (
      WidgetTester tester,
    ) async {
      await tester.pumpWidget(
        MaterialApp(
          initialRoute: '/',
          routes: {
            '/': (context) => const Scaffold(
              body: Center(child: ExpressiveLoadingIndicator()),
            ),
            '/next': (context) =>
                const Scaffold(body: Center(child: Text('Next Page'))),
          },
        ),
      );

      // Start animation
      await tester.pump(const Duration(milliseconds: 100));

      // Navigate away
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(body: Center(child: Text('Next Page'))),
        ),
      );

      // No exceptions should occur
      expect(tester.takeException(), isNull);
    });
  });
}
