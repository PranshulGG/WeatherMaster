import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';

class _AnimationConfig {
  final String? animationUrl;
  final String? secondaryAnimationUrl;
  final num? topMain;
  final double? diffHeight;
  final bool isSunAnim;

  const _AnimationConfig({
    required this.animationUrl,
    required this.secondaryAnimationUrl,
    required this.topMain,
    required this.diffHeight,
    required this.isSunAnim,
  });
}

class WeatherConditionAnimationMapper {
  static const _foregroundAnimationsBasePath = 'assets/foreground-animations/';

  static const _sunnyForeground =
      '${_foregroundAnimationsBasePath}sunny_foreground.json';
  static const _starsForeground =
      '${_foregroundAnimationsBasePath}stars_foreground.json';
  static const _sunnyBackground =
      '${_foregroundAnimationsBasePath}sunny_background.json';
  static const _cloudyForeground =
      '${_foregroundAnimationsBasePath}cloudy_foreground.json';
  static const _cloudyFull = '${_foregroundAnimationsBasePath}cloudy.json';
  static const _cloudyBackground =
      '${_foregroundAnimationsBasePath}cloudy_background.json';
  static const _mostlyClearNight =
      '${_foregroundAnimationsBasePath}mostly_clear_night.json';
  static const _hazeForeground =
      '${_foregroundAnimationsBasePath}haze_foreground.json';
  static const _showers = '${_foregroundAnimationsBasePath}showers.json';
  static const _rainForeground =
      '${_foregroundAnimationsBasePath}rain_foreground.json';
  static const _flurriesForeground =
      '${_foregroundAnimationsBasePath}flurries_foreground.json';
  static const _snowShowerForeground =
      '${_foregroundAnimationsBasePath}snow_shower_foreground.json';
  static const _thunderBackground =
      '${_foregroundAnimationsBasePath}thunder_background.json';

  static const Set<int> _hazeCodes = {45, 48};
  static const Set<int> _rainCodes = {
    51,
    53,
    55,
    56,
    57,
    61,
    63,
    65,
    66,
    67,
    80,
    81,
    82,
  };
  static const Set<int> _snowCodes = {71, 73, 75, 77};
  static const Set<int> _snowShowerCodes = {85, 86};
  static const Set<int> _thunderCodes = {95, 96, 99};

  static _WeatherAnimType _resolveType(int weatherCode) {
    switch (weatherCode) {
      case 0:
        return _WeatherAnimType.clear;
      case 1:
      case 2:
        return _WeatherAnimType.partlyCloudy;
      case 3:
        return _WeatherAnimType.overcast;
      default:
        if (_hazeCodes.contains(weatherCode)) return _WeatherAnimType.haze;
        if (_thunderCodes.contains(weatherCode)) return _WeatherAnimType.thunder;
        if (_rainCodes.contains(weatherCode)) return _WeatherAnimType.rain;
        if (_snowCodes.contains(weatherCode)) return _WeatherAnimType.snow;
        if (_snowShowerCodes.contains(weatherCode)) {
          return _WeatherAnimType.snowShower;
        }
        return _WeatherAnimType.unknown;
    }
  }

  static Widget _lottieLayer(String asset, {double opacity = 1.0}) {
    final lottie = Lottie.asset(
      asset,
      fit: BoxFit.cover,
      repeat: true,
      backgroundLoading: true,
      renderCache: RenderCache.drawingCommands,
      frameRate: FrameRate.composition,
      addRepaintBoundary: false,
    );

    if (opacity == 1.0) return RepaintBoundary(child: lottie);
    return RepaintBoundary(child: Opacity(opacity: opacity, child: lottie));
  }

  static Widget build({
    required int weatherCode,
    required int isDay,
    required BuildContext context,
    bool setFullDisplay = false,
  }) {
    final media = MediaQuery.of(context);
    final isDayBool = isDay == 1;

    final config = _getConfig(
      weatherCode: weatherCode,
      isDay: isDayBool,
      paddingTop: media.padding.top,
      setFullDisplay: setFullDisplay,
    );

    final animationUrl = config.animationUrl;
    if (animationUrl == null) return const SizedBox.shrink();

    final secondaryAnimationUrl = config.secondaryAnimationUrl;
    if (secondaryAnimationUrl == null) {
      return _buildSingle(
        media: media,
        config: config,
        isNight: !isDayBool,
        setFullDisplay: setFullDisplay,
      );
    }

    return _buildWithSecondary(
      media: media,
      config: config,
      isNight: !isDayBool,
      setFullDisplay: setFullDisplay,
    );
  }

  static _AnimationConfig _getConfig({
    required int weatherCode,
    required bool isDay,
    required double paddingTop,
    required bool setFullDisplay,
  }) {
    final type = _resolveType(weatherCode);
    final cloudyAsset = setFullDisplay ? _cloudyFull : _cloudyBackground;

    switch (type) {
      case _WeatherAnimType.clear:
        return _AnimationConfig(
          animationUrl: isDay ? _sunnyForeground : _starsForeground,
          secondaryAnimationUrl: isDay ? _sunnyBackground : _starsForeground,
          topMain: 60,
          diffHeight: 400.0,
          isSunAnim: true,
        );

      case _WeatherAnimType.partlyCloudy:
        return _AnimationConfig(
          animationUrl: isDay ? _cloudyForeground : _mostlyClearNight,
          secondaryAnimationUrl: isDay ? cloudyAsset : _mostlyClearNight,
          topMain: paddingTop + 40,
          diffHeight: isDay ? 400.0 : 450.0,
          isSunAnim: false,
        );

      case _WeatherAnimType.overcast:
        return _AnimationConfig(
          animationUrl: cloudyAsset,
          secondaryAnimationUrl: _cloudyBackground,
          topMain: paddingTop + (setFullDisplay ? 100 : 50),
          diffHeight: null,
          isSunAnim: false,
        );

      case _WeatherAnimType.haze:
        return const _AnimationConfig(
          animationUrl: _hazeForeground,
          secondaryAnimationUrl: null,
          topMain: 13,
          diffHeight: null,
          isSunAnim: false,
        );

      case _WeatherAnimType.rain:
        return _AnimationConfig(
          animationUrl: setFullDisplay ? _showers : _rainForeground,
          secondaryAnimationUrl: null,
          topMain: 15,
          diffHeight: null,
          isSunAnim: false,
        );

      case _WeatherAnimType.snow:
        return const _AnimationConfig(
          animationUrl: _flurriesForeground,
          secondaryAnimationUrl: null,
          topMain: null,
          diffHeight: null,
          isSunAnim: false,
        );

      case _WeatherAnimType.snowShower:
        return const _AnimationConfig(
          animationUrl: _snowShowerForeground,
          secondaryAnimationUrl: null,
          topMain: null,
          diffHeight: null,
          isSunAnim: false,
        );

      case _WeatherAnimType.thunder:
        return _AnimationConfig(
          animationUrl: setFullDisplay ? _showers : _rainForeground,
          secondaryAnimationUrl: _thunderBackground,
          topMain: 10,
          diffHeight: null,
          isSunAnim: false,
        );

      case _WeatherAnimType.unknown:
        return const _AnimationConfig(
          animationUrl: null,
          secondaryAnimationUrl: null,
          topMain: null,
          diffHeight: null,
          isSunAnim: false,
        );
    }
  }

  static Widget _buildWithSecondary({
    required MediaQueryData media,
    required _AnimationConfig config,
    required bool isNight,
    required bool setFullDisplay,
  }) {
    final animationUrl = config.animationUrl!;
    final secondaryAnimationUrl = config.secondaryAnimationUrl!;
    final topMain = (config.topMain ?? 0).toDouble();

    final paddingTop = media.padding.top;
    final width = media.size.width;
    final heightScreen = media.size.height;

    final mainTop = config.isSunAnim
        ? -paddingTop + 10 - topMain
        : -paddingTop - topMain;

    final secondaryIsCloudyBackground = secondaryAnimationUrl == _cloudyBackground;

    final useFixedMainHeight =
        (config.isSunAnim && !isNight) || secondaryIsCloudyBackground;

    final mainHeight =
        (setFullDisplay && !useFixedMainHeight) ? heightScreen : 300.0;

    final needsCloudyOffset = setFullDisplay &&
        secondaryIsCloudyBackground &&
        animationUrl == _cloudyBackground;

    final secondaryTop = needsCloudyOffset ? -paddingTop - topMain : -paddingTop;

    final defaultSecondaryHeight = config.diffHeight ?? 500.0;
    final secondaryHeight = (setFullDisplay && !(config.isSunAnim && !isNight))
        ? heightScreen
        : defaultSecondaryHeight;

    return Positioned(
      top: mainTop,
      left: config.isSunAnim ? -width * 0.12 : 0,
      right: 0,
      height: mainHeight,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          Positioned.fill(child: _lottieLayer(animationUrl)),
          Positioned(
            top: secondaryTop,
            left: 0,
            right: 0,
            // height: diffHeight ?? 500,
            height: secondaryHeight,
            child: _lottieLayer(secondaryAnimationUrl),
          ),
        ],
      ),
    );
  }

  static Widget _buildSingle({
    required MediaQueryData media,
    required _AnimationConfig config,
    required bool isNight,
    required bool setFullDisplay,
  }) {
    final animationUrl = config.animationUrl!;
    final topMain = (config.topMain ?? 0).toDouble();

    final paddingTop = media.padding.top;
    final heightScreen = media.size.height;

    final height = (setFullDisplay && !(config.isSunAnim && !isNight))
        ? heightScreen
        : 300.0;

    final opacity = animationUrl == _hazeForeground ? 0.4 : 1.0;

    return Positioned(
      top: -paddingTop - topMain,
      left: 0,
      right: 0,
      height: height,
      child: _lottieLayer(animationUrl, opacity: opacity),
    );
  }
}

enum _WeatherAnimType {
  clear,
  partlyCloudy,
  overcast,
  haze,
  rain,
  snow,
  snowShower,
  thunder,
  unknown,
}
