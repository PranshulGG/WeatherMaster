import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';

class WeatherConditionAnimationMapper {
  static Widget build({
    required int weatherCode,
    required int isDay,
    required BuildContext context,
    bool setFullDisplay = false,
  }) {
    String? animationUrl;
    num? topMain;
    String? secondaryAnimationUrl;
    double? diffHeight;
    bool isSunAnim = false;
    bool isNightBool = isDay == 0 ? true : false;

    switch (weatherCode) {
      case 0:
        animationUrl = isDay == 1
            ? 'assets/foreground-animations/sunny_foreground.json'
            : 'assets/foreground-animations/stars_foreground.json';
        secondaryAnimationUrl = isDay == 1
            ? 'assets/foreground-animations/sunny_background.json'
            : 'assets/foreground-animations/stars_foreground.json';
        topMain = 60;
        diffHeight = 400;
        isSunAnim = true;
        break;

      case 1:
      case 2:
        animationUrl = isDay == 1
            ? 'assets/foreground-animations/cloudy_foreground.json'
            : 'assets/foreground-animations/mostly_clear_night.json';
        secondaryAnimationUrl = isDay == 1
            ? setFullDisplay
                ? 'assets/foreground-animations/cloudy.json'
                : "assets/foreground-animations/cloudy_background.json"
            : 'assets/foreground-animations/mostly_clear_night.json';
        topMain = MediaQuery.of(context).padding.top + 40;
        diffHeight = isDay == 1 ? 400 : 450;
        break;

      case 3:
        animationUrl = setFullDisplay
            ? 'assets/foreground-animations/cloudy.json'
            : 'assets/foreground-animations/cloudy_background.json';
        secondaryAnimationUrl =
            'assets/foreground-animations/cloudy_background.json';
        topMain = setFullDisplay
            ? MediaQuery.of(context).padding.top + 100
            : MediaQuery.of(context).padding.top + 50;
        break;

      case 45:
      case 48:
        animationUrl = 'assets/foreground-animations/haze_foreground.json';
        topMain = 13;
        break;

      case 51:
      case 53:
      case 55:
      case 56:
      case 57:
        animationUrl = setFullDisplay
            ? 'assets/foreground-animations/showers.json'
            : 'assets/foreground-animations/rain_foreground.json';
        topMain = 15;
        break;

      case 61:
      case 63:
      case 65:
        animationUrl = isDay == 1
            ? setFullDisplay
                ? 'assets/foreground-animations/showers.json'
                : 'assets/foreground-animations/rain_foreground.json'
            : setFullDisplay
                ? 'assets/foreground-animations/showers.json'
                : 'assets/foreground-animations/rain_foreground.json';
        topMain = 15;
        break;

      case 66:
      case 67:
        animationUrl = setFullDisplay
            ? 'assets/foreground-animations/showers.json'
            : 'assets/foreground-animations/rain_foreground.json';
        topMain = 15;
        break;

      case 71:
      case 73:
      case 75:
        animationUrl = isDay == 1
            ? 'assets/foreground-animations/flurries_foreground.json'
            : 'assets/foreground-animations/flurries_foreground.json';
        break;

      case 77:
        animationUrl = 'assets/foreground-animations/flurries_foreground.json';
        break;

      case 80:
      case 81:
      case 82:
        animationUrl = setFullDisplay
            ? 'assets/foreground-animations/showers.json'
            : 'assets/foreground-animations/rain_foreground.json';
        topMain = 15;
        break;

      case 85:
      case 86:
        animationUrl =
            'assets/foreground-animations/snow_shower_foreground.json';
        break;

      case 95:
      case 96:
      case 99:
        animationUrl = setFullDisplay
            ? 'assets/foreground-animations/showers.json'
            : 'assets/foreground-animations/rain_foreground.json';
        secondaryAnimationUrl =
            'assets/foreground-animations/thunder_background.json';
        topMain = 10;
        break;

      default:
        animationUrl = null;
    }

    if (animationUrl == null) {
      return const SizedBox.shrink();
    }

    if (secondaryAnimationUrl != null) {
      return Positioned(
        top: isSunAnim
            ? -MediaQuery.of(context).padding.top + 10 - (topMain ?? 0)
            : -MediaQuery.of(context).padding.top - (topMain ?? 0),
        left: isSunAnim ? -MediaQuery.of(context).size.width * 0.12 : 0,
        right: 0,
        height: (isSunAnim == true && !isNightBool ||
                secondaryAnimationUrl.contains('cloudy_background.json'))
            ? 300
            : setFullDisplay
                ? MediaQuery.of(context).size.height
                : 300,
        child: Stack(
          clipBehavior: Clip.none,
          children: [
            Positioned.fill(
              child: RepaintBoundary(
                child: Lottie.asset(
                  animationUrl,
                  fit: BoxFit.cover,
                  repeat: true,
                  addRepaintBoundary: true,
                ),
              ),
            ),
            Positioned(
              top: (setFullDisplay &&
                      secondaryAnimationUrl
                          .contains('cloudy_background.json') &&
                      animationUrl.contains('cloudy_background.json'))
                  ? -MediaQuery.of(context).padding.top - (topMain ?? 0)
                  : -MediaQuery.of(context).padding.top,
              left: 0,
              right: 0,
              // height: diffHeight ?? 500,
              height: (isSunAnim == true && !isNightBool)
                  ? diffHeight ?? 500
                  : setFullDisplay
                      ? MediaQuery.of(context).size.height
                      : diffHeight ?? 500,
              child: RepaintBoundary(
                child: Lottie.asset(
                  secondaryAnimationUrl,
                  fit: BoxFit.cover,
                  repeat: true,
                  addRepaintBoundary: true,
                ),
              ),
            ),
          ],
        ),
      );
    }

    return Positioned(
      top: -MediaQuery.of(context).padding.top - (topMain ?? 0),
      left: 0,
      right: 0,
      height: (isSunAnim == true && !isNightBool)
          ? 300
          : setFullDisplay
              ? MediaQuery.of(context).size.height
              : 300,
      child: RepaintBoundary(
        child: Opacity(
          opacity: animationUrl.contains('haze_foreground.json') ? 0.4 : 1,
          child: Lottie.asset(
            animationUrl,
            fit: BoxFit.cover,
            repeat: true,
          ),
        ),
      ),
    );
  }
}
