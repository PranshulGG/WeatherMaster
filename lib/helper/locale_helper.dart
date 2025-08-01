import 'package:flutter/material.dart';

String localizeWindUnit(String unit, Locale locale) {
  if (locale.languageCode == "zh") {
    switch (unit) {
      case 'Km/h':
        return '公里/时';
      case 'Mph':
        return '英里/时';
      case 'M/s':
        return '米/秒';
      case 'Bft':
      return '级';
    }
  }
  return unit; 
}

String localizeVisibilityUnit(String unit, Locale locale) {
  if (locale.languageCode == "zh") {
    switch (unit) {
      case 'Km':
        return '公里';
      case 'Mile':
        return '英里';
    }
  }
  return unit; 
}

String localizePressureUnit(String unit, Locale locale) {
  if (locale.languageCode == "zh") {
    switch (unit) {
      case 'hPa':
        return '百帕';
      case 'inHg':
        return '英寸汞柱';
      case 'mmHg':
        return '毫米汞柱';
    }
  }
  return unit; 
}

String localizePrecipUnit(String unit, Locale locale) {
  if (locale.languageCode == "zh") {
    switch (unit) {
      case 'mm':
        return '毫米';
      case 'cm':
        return '厘米';
      case 'in':
        return '英寸';
    }
  }
  return unit; 
}

String localizeTempUnit(String unit, Locale locale) {
  if (locale.languageCode == "zh") {
    switch (unit) {
      case 'Celsius':
        return '摄氏度';
      case 'Fahrenheit':
        return '华氏度';
    }
  }
  return unit; 
}

String localizeTimeFormat(String unit, Locale locale) {
  if (locale.languageCode == "zh") {
    switch (unit) {
      case '24 hr':
        return '24 小时';
      case '12 hr':
        return '12 小时';
    }
  }
  return unit; 
}