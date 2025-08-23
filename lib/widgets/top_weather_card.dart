import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../utils/icon_map.dart';
import '../utils/condition_label_map.dart';
import '../utils/preferences_helper.dart';
import '../utils/unit_converter.dart';
import '../notifiers/unit_settings_notifier.dart';
import 'package:provider/provider.dart';
import 'package:easy_localization/easy_localization.dart';

class WeatherTopCard extends StatefulWidget {
  final num currentTemp;
  final int currentWeatherIconCode;
  final int currentisDay;
  final num currentFeelsLike;
  final num currentMaxTemp;
  final num currentMinTemp;
  final String currentLastUpdated;

  const WeatherTopCard({
    super.key,
    required this.currentTemp,
    required this.currentWeatherIconCode,
    required this.currentisDay,
    required this.currentFeelsLike,
    required this.currentMaxTemp,
    required this.currentMinTemp,
    required this.currentLastUpdated,
  });

  @override
  State<WeatherTopCard> createState() => _WeatherTopCardState();
}

class _WeatherTopCardState extends State<WeatherTopCard> {
  final GlobalKey _labelKey = GlobalKey();
  double _labelHeight = 0;
  UnitSettingsNotifier? _notifier;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _updateLabelHeight();
    });
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    _notifier?.removeListener(_onSettingsChanged);

    _notifier = context.read<UnitSettingsNotifier>();
    _notifier?.addListener(_onSettingsChanged);
  }

  void _onSettingsChanged() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _updateLabelHeight();
    });
  }

  @override
  void dispose() {
    _notifier?.removeListener(_onSettingsChanged);
    super.dispose();
  }

  void _updateLabelHeight() {
    final context = _labelKey.currentContext;
    if (context != null) {
      final box = context.findRenderObject() as RenderBox;
      final height = box.size.height;
      setState(() {
        _labelHeight = height;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;
    final isShowFrog = context.read<UnitSettingsNotifier>().showFrog;
    num convert(num celsius) => tempUnit == "Fahrenheit"
        ? UnitConverter.celsiusToFahrenheit(celsius.toDouble()).round()
        : celsius.round();

    final convertedTemp = convert(widget.currentTemp);
    final convertedcurrentMaxTemp = convert(widget.currentMaxTemp);
    final convertedcurrentMinTemp = convert(widget.currentMinTemp);
    final convertedfeelsLikeTemp = convert(widget.currentFeelsLike);

    return isShowFrog
        ? Padding(
            padding: const EdgeInsets.fromLTRB(16, 5, 16, 10),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "now".tr(),
                      style: TextStyle(
                        color: Theme.of(context).colorScheme.secondary,
                        fontSize: 18,
                        fontVariations: [
                          FontVariation('wght', 450),
                        ],
                      ),
                    ),
                    Row(
                      children: [
                        PreferencesHelper.getBool("useTempAnimation") == false
                            ? Text("$convertedTemp°",
                                style: TextStyle(
                                    color: Theme.of(context).brightness ==
                                            Brightness.light
                                        ? Theme.of(context)
                                            .colorScheme
                                            .inverseSurface
                                        : Theme.of(context).colorScheme.primary,
                                    fontSize: 65,
                                    fontVariations: [
                                      FontVariation('wght', 500),
                                      FontVariation('ROND', 100),
                                    ],
                                    height: 1.3))
                            : AnimatedTemperature(
                                targetTemp: convertedTemp.toDouble()),
                        SvgPicture.asset(
                          WeatherIconMapper.getIcon(
                              widget.currentWeatherIconCode,
                              widget.currentisDay),
                          width: 50,
                        ),
                      ],
                    ),
                    Row(
                      children: [
                        Row(
                          children: [
                            Icon(Icons.arrow_upward,
                                size: 16,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onSurfaceVariant),
                            Text("$convertedcurrentMaxTemp°",
                                style: TextStyle(
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                    fontVariations: [
                                      FontVariation('wght', 450),
                                      FontVariation('ROND', 100),
                                    ],
                                    fontSize: 16))
                          ],
                        ),
                        Row(
                          children: [
                            Icon(
                              Icons.arrow_downward,
                              size: 16,
                              color: Theme.of(context)
                                  .colorScheme
                                  .onSurfaceVariant,
                            ),
                            Text("$convertedcurrentMinTemp°",
                                style: TextStyle(
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                    fontVariations: [
                                      FontVariation('wght', 450),
                                      FontVariation('ROND', 100),
                                    ],
                                    fontSize: 16))
                          ],
                        )
                      ],
                    )
                  ],
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    SizedBox(
                      height: 50,
                      width: 150,
                      child: Stack(
                        clipBehavior: Clip.none,
                        children: [
                          Positioned(
                            top: 0,
                            right: 0,
                            child: ConstrainedBox(
                              constraints: BoxConstraints(
                                maxWidth: 150,
                              ),
                              child: Text(
                                WeatherConditionMapper.getConditionLabel(
                                  widget.currentWeatherIconCode,
                                  widget.currentisDay,
                                ).tr(),
                                key: _labelKey,
                                style: TextStyle(
                                  color:
                                      Theme.of(context).colorScheme.onSurface,
                                  fontSize: 18,
                                  fontVariations: [
                                    FontVariation('wght', 500),
                                    FontVariation('ROND', 100),
                                  ],
                                ),
                                softWrap: true,
                                overflow: TextOverflow.ellipsis,
                                maxLines: 2,
                                textAlign: TextAlign.right,
                              ),
                            ),
                          ),
                          if (_labelHeight > 0)
                            Positioned(
                              top: _labelHeight,
                              right: 0,
                              child: ConstrainedBox(
                                constraints: BoxConstraints(
                                  maxWidth: 150,
                                ),
                                child: Text(
                                  "${'feels_like'.tr()}  ${widget.currentFeelsLike == 0000 ? '--' : '$convertedfeelsLikeTemp°'} ",
                                  style: TextStyle(
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                    fontSize: 15,
                                    fontVariations: [
                                      FontVariation('wght', 500),
                                    ],
                                  ),
                                  textAlign: TextAlign.right,
                                ),
                              ),
                            ),
                        ],
                      ),
                    ),
                    const SizedBox(
                      height: 60,
                    ),
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.update,
                          size: 15,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        ),
                        const SizedBox(
                          width: 3,
                        ),
                        Text(
                          widget.currentLastUpdated,
                          style: TextStyle(
                            color:
                                Theme.of(context).colorScheme.onSurfaceVariant,
                            fontSize: 14,
                            fontVariations: [
                              FontVariation('wght', 450),
                            ],
                            fontStyle: FontStyle.italic,
                          ),
                        )
                      ],
                    )
                  ],
                ),
              ],
            ),
          )
        : WeatherTopCardVertical(
            currentTemp: widget.currentTemp,
            currentWeatherIconCode: widget.currentWeatherIconCode,
            currentisDay: widget.currentisDay,
            currentFeelsLike: widget.currentFeelsLike,
            currentMaxTemp: widget.currentMaxTemp,
            currentMinTemp: widget.currentMinTemp,
            currentLastUpdated: widget.currentLastUpdated);
  }
}

class AnimatedTemperature extends StatelessWidget {
  final double targetTemp;

  const AnimatedTemperature({super.key, required this.targetTemp});

  @override
  Widget build(BuildContext context) {
    final isShowFrog = context.read<UnitSettingsNotifier>().showFrog;

    return TweenAnimationBuilder<double>(
      tween: Tween<double>(
        begin: (targetTemp * 0.50).clamp(0, double.infinity),
        end: targetTemp,
      ),
      duration: const Duration(milliseconds: 2000),
      curve: Curves.easeOut,
      builder: (context, value, child) {
        return Text(
          isShowFrog
              ? '${value.toStringAsFixed(0)}°'
              : value.toStringAsFixed(0),
          style: TextStyle(
            color: Theme.of(context).brightness == Brightness.light
                ? Theme.of(context).colorScheme.inverseSurface
                : Theme.of(context).colorScheme.primary,
            fontSize: isShowFrog ? 60 : 120,
            fontVariations: [
              FontVariation('wght', isShowFrog ? 500 : 600),
              FontVariation('ROND', 100),
            ],
            height: isShowFrog ? 1.3 : 1,
          ),
        );
      },
    );
  }
}

// vertical version

class WeatherTopCardVertical extends StatefulWidget {
  final num currentTemp;
  final int currentWeatherIconCode;
  final int currentisDay;
  final num currentFeelsLike;
  final num currentMaxTemp;
  final num currentMinTemp;
  final String currentLastUpdated;

  const WeatherTopCardVertical({
    super.key,
    required this.currentTemp,
    required this.currentWeatherIconCode,
    required this.currentisDay,
    required this.currentFeelsLike,
    required this.currentMaxTemp,
    required this.currentMinTemp,
    required this.currentLastUpdated,
  });

  @override
  State<WeatherTopCardVertical> createState() => _WeatherTopCardStateVertical();
}

class _WeatherTopCardStateVertical extends State<WeatherTopCardVertical> {
  final GlobalKey _labelKey = GlobalKey();
  double _labelHeight = 0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _updateLabelHeight();
    });
  }

  void _updateLabelHeight() {
    final context = _labelKey.currentContext;
    if (context != null) {
      final box = context.findRenderObject() as RenderBox;
      final height = box.size.height;
      setState(() {
        _labelHeight = height;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final tempUnit = context.watch<UnitSettingsNotifier>().tempUnit;
    final isShowFrog = context.read<UnitSettingsNotifier>().showFrog;
    num convert(num celsius) => tempUnit == "Fahrenheit"
        ? UnitConverter.celsiusToFahrenheit(celsius.toDouble()).round()
        : celsius.round();

    final convertedTemp = convert(widget.currentTemp);
    final convertedcurrentMaxTemp = convert(widget.currentMaxTemp);
    final convertedcurrentMinTemp = convert(widget.currentMinTemp);
    final convertedfeelsLikeTemp = convert(widget.currentFeelsLike);

    final colorTheme = Theme.of(context).colorScheme;

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 10, 16, 8),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                WeatherConditionMapper.getConditionLabel(
                  widget.currentWeatherIconCode,
                  widget.currentisDay,
                ).tr(),
                style: TextStyle(
                    color: colorTheme.onSurface,
                    fontSize: 22,
                    fontVariations: FontVariationsMedium),
              ),
              SizedBox(
                height: 8,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  PreferencesHelper.getBool("useTempAnimation") == false
                      ? Text("$convertedTemp",
                          style: TextStyle(
                              color: Theme.of(context).brightness ==
                                      Brightness.light
                                  ? Theme.of(context).colorScheme.inverseSurface
                                  : Theme.of(context).colorScheme.primary,
                              fontSize: 120,
                              fontVariations: [
                                FontVariation('wght', 600),
                                FontVariation('ROND', 100),
                              ],
                              height: 1.3))
                      : AnimatedTemperature(
                          targetTemp: convertedTemp.toDouble()),
                  Padding(
                    padding: const EdgeInsets.only(top: 10),
                    child: SvgPicture.asset(
                      WeatherIconMapper.getIcon(
                          widget.currentWeatherIconCode, widget.currentisDay),
                      width: 60,
                    ),
                  ),
                ],
              ),
              SizedBox(
                height: 6,
              ),
              Text(
                "${'feels_like'.tr()}  ${widget.currentFeelsLike == 0000 ? '--' : '$convertedfeelsLikeTemp°'}",
                style: TextStyle(
                    color: colorTheme.onSurface,
                    fontSize: 20,
                    fontVariations: FontVariationsMedium),
              ),
              SizedBox(height: 7),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                      "${'low_text'.tr()}: $convertedcurrentMinTemp° • ${'high_text'.tr()}: $convertedcurrentMaxTemp°",
                      style: TextStyle(
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                          fontVariations: [
                            FontVariation('wght', 450),
                            FontVariation('ROND', 100),
                          ],
                          fontSize: 18)),
                ],
              )
            ],
          ),
        ],
      ),
    );
  }
}
