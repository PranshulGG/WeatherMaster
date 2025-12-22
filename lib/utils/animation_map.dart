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
    if (weatherCode == 0) return _WeatherAnimType.clear;
    if (weatherCode == 1 || weatherCode == 2) return _WeatherAnimType.partlyCloudy;
    if (weatherCode == 3) return _WeatherAnimType.overcast;
    if (_hazeCodes.contains(weatherCode)) return _WeatherAnimType.haze;
    if (_thunderCodes.contains(weatherCode)) return _WeatherAnimType.thunder;
    if (_rainCodes.contains(weatherCode)) return _WeatherAnimType.rain;
    if (_snowCodes.contains(weatherCode)) return _WeatherAnimType.snow;
    if (_snowShowerCodes.contains(weatherCode)) return _WeatherAnimType.snowShower;
    return _WeatherAnimType.unknown;
  }

  static Widget build({
    required int weatherCode,
    required int isDay,
    required BuildContext context,
    bool setFullDisplay = false,
  }) {
    final config = _getConfig(
      weatherCode: weatherCode,
      isDay: isDay,
      context: context,
      setFullDisplay: setFullDisplay,
    );

    final animationUrl = config.animationUrl;
    if (animationUrl == null) {
      return const SizedBox.shrink();
    }

    final secondaryAnimationUrl = config.secondaryAnimationUrl;
    if (secondaryAnimationUrl != null) {
      return _buildWithSecondary(
        context: context,
        config: config,
        isDay: isDay,
        setFullDisplay: setFullDisplay,
      );
    }

    return _buildSingle(
      context: context,
      config: config,
      isDay: isDay,
      setFullDisplay: setFullDisplay,
    );
  }

  static _AnimationConfig _getConfig({
    required int weatherCode,
    required int isDay,
    required BuildContext context,
    required bool setFullDisplay,
  }) {
    final isDayBool = isDay == 1;
    final paddingTop = MediaQuery.of(context).padding.top;
    final type = _resolveType(weatherCode);

    final partlyCloudySecondaryAnimationUrl = isDayBool
        ? (setFullDisplay ? _cloudyFull : _cloudyBackground)
        : _mostlyClearNight;

    switch (type) {
      case _WeatherAnimType.clear:
        return _AnimationConfig(
          animationUrl: isDayBool ? _sunnyForeground : _starsForeground,
          secondaryAnimationUrl: isDayBool ? _sunnyBackground : _starsForeground,
          topMain: 60,
          diffHeight: 400,
          isSunAnim: true,
        );

      case _WeatherAnimType.partlyCloudy:
        return _AnimationConfig(
          animationUrl: isDayBool ? _cloudyForeground : _mostlyClearNight,
          secondaryAnimationUrl: partlyCloudySecondaryAnimationUrl,
          topMain: paddingTop + 40,
          diffHeight: isDayBool ? 400 : 450,
          isSunAnim: false,
        );

      case _WeatherAnimType.overcast:
        return _AnimationConfig(
          animationUrl: setFullDisplay ? _cloudyFull : _cloudyBackground,
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
    required BuildContext context,
    required _AnimationConfig config,
    required int isDay,
    required bool setFullDisplay,
  }) {
    final animationUrl = config.animationUrl!;
    final secondaryAnimationUrl = config.secondaryAnimationUrl!;
    final topMain = config.topMain;
    final diffHeight = config.diffHeight;
    final isSunAnim = config.isSunAnim;

    final isNight = isDay == 0;
    final paddingTop = MediaQuery.of(context).padding.top;
    final width = MediaQuery.of(context).size.width;
    final heightScreen = MediaQuery.of(context).size.height;

    final mainTop = isSunAnim
        ? -paddingTop + 10 - (topMain ?? 0)
        : -paddingTop - (topMain ?? 0);

    final useFixedMainHeight =
        (isSunAnim && !isNight) || secondaryAnimationUrl.contains(_cloudyBackground);

    final mainHeight = (setFullDisplay && !useFixedMainHeight) ? heightScreen : 300.0;

    final needsCloudyOffset = setFullDisplay &&
        secondaryAnimationUrl.contains(_cloudyBackground) &&
        animationUrl.contains(_cloudyBackground);

    final secondaryTop =
        needsCloudyOffset ? -paddingTop - (topMain ?? 0) : -paddingTop;

    final defaultSecondaryHeight = diffHeight ?? 500;
    final secondaryHeight = (setFullDisplay && !(isSunAnim && !isNight))
        ? heightScreen
        : defaultSecondaryHeight;

    return Positioned(
      top: mainTop,
      left: isSunAnim ? -width * 0.12 : 0,
      right: 0,
      height: mainHeight,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          Positioned.fill(
            child: RepaintBoundary(
              child: Lottie.asset(
                animationUrl,
                fit: BoxFit.cover,
                repeat: true,
                backgroundLoading: true,
                renderCache: RenderCache.raster,
                frameRate: FrameRate.composition,
                addRepaintBoundary: true,
              ),
            ),
          ),
          Positioned(
            top: secondaryTop,
            left: 0,
            right: 0,
            // height: diffHeight ?? 500,
            height: secondaryHeight,
            child: RepaintBoundary(
              child: Lottie.asset(
                secondaryAnimationUrl,
                fit: BoxFit.cover,
                repeat: true,
                backgroundLoading: true,
                renderCache: RenderCache.raster,
                frameRate: FrameRate.composition,
                addRepaintBoundary: true,
              ),
            ),
          ),
        ],
      ),
    );
  }

  static Widget _buildSingle({
    required BuildContext context,
    required _AnimationConfig config,
    required int isDay,
    required bool setFullDisplay,
  }) {
    final animationUrl = config.animationUrl!;
    final topMain = config.topMain;
    final isSunAnim = config.isSunAnim;

    final isNight = isDay == 0;
    final paddingTop = MediaQuery.of(context).padding.top;
    final heightScreen = MediaQuery.of(context).size.height;

    double height;
    if (isSunAnim && !isNight) {
      height = 300;
    } else if (setFullDisplay) {
      height = heightScreen;
    } else {
      height = 300;
    }

    final opacity = animationUrl.contains(_hazeForeground) ? 0.4 : 1.0;

    return Positioned(
      top: -paddingTop - (topMain ?? 0),
      left: 0,
      right: 0,
      height: height,
      child: RepaintBoundary(
        child: Opacity(
          opacity: opacity,
          child: Lottie.asset(
            animationUrl,
            fit: BoxFit.cover,
            repeat: true,
            backgroundLoading: true,
            renderCache: RenderCache.raster,
            frameRate: FrameRate.composition,
          ),
        ),
      ),
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
