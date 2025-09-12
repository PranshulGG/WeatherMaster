import 'package:flutter/material.dart';
import 'package:expressive_loading_indicator/expressive_loading_indicator.dart';
import 'package:material_new_shapes/material_new_shapes.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Expressive Loading Indicator Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Expressive Loading Indicator Demo'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'Default Loading Indicator:',
              style: TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 16),
            // Default loading indicator
            const ExpressiveLoadingIndicator(),

            const SizedBox(height: 32),
            const Text(
              'Custom Color Loading Indicator:',
              style: TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 16),
            // Customized with color
            const ExpressiveLoadingIndicator(color: Colors.orange),

            const SizedBox(height: 32),
            const Text(
              'Custom Size Loading Indicator:',
              style: TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 16),
            // Customized with size
            const ExpressiveLoadingIndicator(
              constraints: BoxConstraints(
                minWidth: 72.0,
                minHeight: 72.0,
                maxWidth: 72.0,
                maxHeight: 72.0,
              ),
            ),

            const SizedBox(height: 32),
            const Text(
              'Custom Shapes Loading Indicator:',
              style: TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 16),
            // Customized with shapes
            ExpressiveLoadingIndicator(
              polygons: [
                MaterialShapes.softBurst,
                MaterialShapes.pill,
                MaterialShapes.pentagon,
              ],
            ),
          ],
        ),
      ),
    );
  }
}
