function createWidget(
  condition,
  locationWeather,
  mainTemp,
  iconData,
  highLow,
  hour_0_temp,
  hour_0_icon,
  hour_0_time,
  hour_1_temp,
  hour_1_icon,
  hour_1_time,
  hour_2_temp,
  hour_2_icon,
  hour_2_time,
  hour_3_temp,
  hour_3_icon,
  hour_3_time
) {

  UpdateWidget1Interface.UpdateWidget1(  condition,
    locationWeather,
    mainTemp,
    iconData,
    highLow,
    hour_0_temp,
    hour_0_icon,
    hour_0_time,
    hour_1_temp,
    hour_1_icon,
    hour_1_time,
    hour_2_temp,
    hour_2_icon,
    hour_2_time,
    hour_3_temp,
    hour_3_icon,
    hour_3_time)
}

// widget data

function createWidgetData() {
  const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
  const timeFormat = localStorage.getItem('selectedTimeMode');
  const selectedProvider = localStorage.getItem('selectedMainWeatherProvider')

const DefaultLocation = JSON.parse(localStorage.getItem('DefaultLocation'));

    const currentLocationData = DefaultLocation.name

  let weatherData;

  if (
    selectedProvider === "Met norway" ||
    (localStorage.getItem("ApiForAccu") && selectedProvider === "Accuweather")
  ) {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataOpenMeteo_${currentLocationData}`)
    );
  } else if (selectedProvider === "meteoFrance") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataMeteoFrance_${currentLocationData}`)
    );
  } else if (selectedProvider === "dwdGermany") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataDWDGermany_${currentLocationData}`)
    );
  } else if (selectedProvider === "noaaUS") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataNOAAUS_${currentLocationData}`)
    );
  } else if (selectedProvider === "ecmwf") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataECMWF_${currentLocationData}`)
    );
  } else if (selectedProvider === "ukMetOffice") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataukMetOffice_${currentLocationData}`)
    );
  } else if (selectedProvider === "jmaJapan") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataJMAJapan_${currentLocationData}`)
    );
  } else if (selectedProvider === "gemCanada") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDatagemCanada_${currentLocationData}`)
    );
  } else if (selectedProvider === "bomAustralia") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDatabomAustralia_${currentLocationData}`)
    );
  } else if (selectedProvider === "cmaChina") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDatacmaChina_${currentLocationData}`)
    );
  } else if (selectedProvider === "knmiNetherlands") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataknmiNetherlands_${currentLocationData}`)
    );
  } else if (selectedProvider === "dmiDenmark") {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDatadmiDenmark_${currentLocationData}`)
    );
  } else {
    weatherData = JSON.parse(
      localStorage.getItem(`WeatherDataOpenMeteo_${currentLocationData}`)
    );
  }

    if(weatherData){

  let CurrentTemperature;

  if (SelectedTempUnit === "fahrenheit") {
    CurrentTemperature = Math.round(celsiusToFahrenheit(weatherData.current.temperature_2m)) + '°';
  } else {
    CurrentTemperature = Math.round(weatherData.current.temperature_2m) + '°';
  }

  // -----------

  let locationName;

  if (currentLocationData === "CurrentDeviceLocation") {
    locationName = "Device location";
  } else {
    locationName = currentLocationData;
  }

  // --------

  let conditionMain;

  conditionMain = getWeatherLabelWidgetLang(weatherData.current.weather_code, localStorage.getItem('AppLanguageCode'));

  // ----------

  let conditionMainIcon;

  conditionMainIcon = GetWeatherIconWidget(weatherData.current.weather_code, weatherData.current.is_day)

  // ------

  let TempMinCurrent
  let TempMaxCurrent
  let highLowTemp;

  if (SelectedTempUnit === 'fahrenheit') {
      TempMinCurrent = Math.round(celsiusToFahrenheit(weatherData.daily.temperature_2m_min[0]))
  } else {
      TempMinCurrent = Math.round(weatherData.daily.temperature_2m_min[0])
  }

  if (SelectedTempUnit === 'fahrenheit') {
      TempMaxCurrent = Math.round(celsiusToFahrenheit(weatherData.daily.temperature_2m_max[0]))
  } else {
      TempMaxCurrent = Math.round(weatherData.daily.temperature_2m_max[0])
  }

  highLowTemp = `High: ${TempMaxCurrent}° Low: ${TempMinCurrent}°`


// hour data

  let hour0Temp
  let hour0Icon
  let hour0Time

  let hour1Temp
  let hour1Icon
  let hour1Time

  let hour2Temp
  let hour2Icon
  let hour2Time

  let hour3Temp
  let hour3Icon
  let hour3Time

weatherData.hourly.time.forEach((time, index) => {

    const forecastTime = new Date(time).getTime();

    let hours;
    let period;

    if (timeFormat === '24 hour') {
        hours = new Date(time).getHours().toString().padStart(2, '0') + ':';
        period = new Date(time).getMinutes().toString().padStart(2, '0');
    } else {
        let hour = new Date(time).getHours();
        period = hour >= 12 ? "PM" : "AM";
        hour = hour % 12 || 12;
        hours = `${hour}`;
    }

    const sunriseTimes = weatherData.daily.sunrise.map(time => new Date(time).getTime());
    const sunsetTimes = weatherData.daily.sunset.map(time => new Date(time).getTime());

    let dayIndex = -1;
    for (let i = 0; i < sunriseTimes.length; i++) {
        if (forecastTime >= sunriseTimes[i] && forecastTime < (sunriseTimes[i + 1] || Infinity)) {
            dayIndex = i;
            break;
        }
    }

    const HourWeatherCodeHour0 = weatherData.hourly.weather_code[0]

    let iconHour0;
    if (dayIndex !== -1 && forecastTime >= sunriseTimes[dayIndex] && forecastTime < sunsetTimes[dayIndex]) {
      iconHour0 = GetWeatherIconDayWidget(HourWeatherCodeHour0);
    } else {
      iconHour0 = GetWeatherIconNightWidget(HourWeatherCodeHour0);
    }

    const HourWeatherCodeHour1 = weatherData.hourly.weather_code[1]

    let iconHour1;
    if (dayIndex !== -1 && forecastTime >= sunriseTimes[dayIndex] && forecastTime < sunsetTimes[dayIndex]) {
      iconHour1 = GetWeatherIconDayWidget(HourWeatherCodeHour1);
    } else {
      iconHour1 = GetWeatherIconNightWidget(HourWeatherCodeHour1);
    }

    const HourWeatherCodeHour2 = weatherData.hourly.weather_code[2]

    let iconHour2;
    if (dayIndex !== -1 && forecastTime >= sunriseTimes[dayIndex] && forecastTime < sunsetTimes[dayIndex]) {
      iconHour2 = GetWeatherIconDayWidget(HourWeatherCodeHour2);
    } else {
      iconHour2 = GetWeatherIconNightWidget(HourWeatherCodeHour2);
    }

    const HourWeatherCodeHour3 = weatherData.hourly.weather_code[3]

    let iconHour3;
    if (dayIndex !== -1 && forecastTime >= sunriseTimes[dayIndex] && forecastTime < sunsetTimes[dayIndex]) {
      iconHour3 = GetWeatherIconDayWidget(HourWeatherCodeHour3);
    } else {
      iconHour3 = GetWeatherIconNightWidget(HourWeatherCodeHour3);
    }

    let HourTemperatureHour0;

    if (SelectedTempUnit === 'fahrenheit') {
      HourTemperatureHour0 = Math.round(celsiusToFahrenheit(weatherData.hourly.temperature_2m[0])) + '°';

    } else {
      HourTemperatureHour0 = Math.round(weatherData.hourly.temperature_2m[0]) + '°';

    }

    let HourTemperatureHour1;

    if (SelectedTempUnit === 'fahrenheit') {
      HourTemperatureHour1 = Math.round(celsiusToFahrenheit(weatherData.hourly.temperature_2m[1])) + '°';

    } else {
      HourTemperatureHour1 = Math.round(weatherData.hourly.temperature_2m[1]) + '°';

    }

    let HourTemperatureHour2;

    if (SelectedTempUnit === 'fahrenheit') {
      HourTemperatureHour2 = Math.round(celsiusToFahrenheit(weatherData.hourly.temperature_2m[2])) + '°';

    } else {
      HourTemperatureHour2 = Math.round(weatherData.hourly.temperature_2m[2]) + '°';

    }

    let HourTemperatureHour3;

    if (SelectedTempUnit === 'fahrenheit') {
      HourTemperatureHour3 = Math.round(celsiusToFahrenheit(weatherData.hourly.temperature_2m[3])) + '°';

    } else {
      HourTemperatureHour3 = Math.round(weatherData.hourly.temperature_2m[3]) + '°';

    }


    const timeString = `${hours}${period}`;
    if (index === 0) {
    hour0Icon = iconHour0;
    hour0Temp = HourTemperatureHour0;
    hour0Time = timeString;

    } else if (index === 1) {
    hour1Icon = iconHour1;
    hour1Temp = HourTemperatureHour1;
    hour1Time = timeString;
    } else if (index === 2) {
    hour2Icon = iconHour2;
    hour2Temp = HourTemperatureHour2;
    hour2Time = timeString;
    } else if (index === 3) {
    hour3Icon = iconHour3;
    hour3Temp = HourTemperatureHour3;
    hour3Time = timeString;
    }

  })

  createWidget(conditionMain, locationName, CurrentTemperature, conditionMainIcon, highLowTemp, hour0Temp, hour0Icon, hour0Time, hour1Temp, hour1Icon, hour1Time, hour2Temp, hour2Icon, hour2Time, hour3Temp, hour3Icon, hour3Time)
}
}

function GetWeatherIconWidget(iconCode, isDay) {
  if (isDay === 1) {
    if (iconCode === 0) {
      return "sunny";
    } else if (iconCode === 1) {
      return "mostly_sunny";
    } else if (iconCode === 2) {
      return "partly_cloudy";
    } else if (iconCode === 3) {
      return "cloudy";
    } else if (iconCode === 45 || iconCode === 48) {
      return "haze_fog_dust_smoke";
    } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
      return "drizzle";
    } else if (iconCode === 56 || iconCode === 57) {
      return "wintry_mix_rain_snow";
    } else if (iconCode === 61 || iconCode === 63) {
      return "showers_rain";
    } else if (iconCode === 65) {
      return "heavy_rain";
    } else if (iconCode === 66 || iconCode === 67) {
      return "sleet_hail";
    } else if (iconCode === 71) {
      return "snow_showers_snow";
    } else if (iconCode === 73) {
      return "snow_showers_snow";
    } else if (iconCode === 75) {
      return "heavy_snow";
    } else if (iconCode === 77) {
      return "flurries";
    } else if (iconCode === 80 || iconCode === 81) {
      return "showers_rain";
    } else if (iconCode === 82) {
      return "heavy_rain";
    } else if (iconCode === 85) {
      return "snow_showers_snow";
    } else if (iconCode === 86) {
      return "heavy_snow";
    } else if (iconCode === 95) {
      return "isolated_scattered_tstorms_day";
    } else if (iconCode === 96 || iconCode === 99) {
      return "strong_tstorms";
    }
  } else {
    if (iconCode === 0) {
      return "clear_night";
    } else if (iconCode === 1) {
      return "mostly_clear_night";
    } else if (iconCode === 2) {
      return "partly_cloudy_night";
    } else if (iconCode === 3) {
      return "cloudy";
    } else if (iconCode === 45 || iconCode === 48) {
      return "haze_fog_dust_smoke";
    } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
      return "drizzle";
    } else if (iconCode === 56 || iconCode === 57) {
      return "wintry_mix_rain_snow";
    } else if (iconCode === 61 || iconCode === 63) {
      return "showers_rain";
    } else if (iconCode === 65) {
      return "heavy_rain";
    } else if (iconCode === 66 || iconCode === 67) {
      return "sleet_hail";
    } else if (iconCode === 71) {
      return "snow_showers_snow";
    } else if (iconCode === 73) {
      return "snow_showers_snow";
    } else if (iconCode === 75) {
      return "heavy_snow";
    } else if (iconCode === 77) {
      return "flurries";
    } else if (iconCode === 80 || iconCode === 81) {
      return "showers_rain";
    } else if (iconCode === 82) {
      return "heavy_rain";
    } else if (iconCode === 85) {
      return "snow_showers_snow";
    } else if (iconCode === 86) {
      return "heavy_snow";
    } else if (iconCode === 95) {
      return "isolated_scattered_tstorms_night";
    } else if (iconCode === 96 || iconCode === 99) {
      return "strong_tstorms";
    }
  }

  return iconCode;
}

// labels

function GetWeatherLabelWidget(iconCode, isDay) {
  if (isDay === 1) {
    if (iconCode === 0) {
      return "clear_sky";
    } else if (iconCode === 1) {
      return "mostly_sunny";
    } else if (iconCode === 2) {
      return "partly_cloudy";
    } else if (iconCode === 3) {
      return "overcast";
    } else if (iconCode === 45 || iconCode === 48) {
      return "fog";
    } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
      return "drizzle";
    } else if (iconCode === 56 || iconCode === 57) {
      return "freezing_drizzle";
    } else if (iconCode === 61 || iconCode === 63) {
      return "moderate_rain";
    } else if (iconCode === 65) {
      return "heavy_intensity_rain";
    } else if (iconCode === 66 || iconCode === 67) {
      return "freezing_rain";
    } else if (iconCode === 71) {
      return "slight_snow";
    } else if (iconCode === 73) {
      return "moderate_snow";
    } else if (iconCode === 75) {
      return "heavy_intensity_snow";
    } else if (iconCode === 77) {
      return "snow_grains";
    } else if (iconCode === 80 || iconCode === 81) {
      return "rain_showers";
    } else if (iconCode === 82) {
      return "heavy_rain_showers";
    } else if (iconCode === 85) {
      return "slight_snow_showers";
    } else if (iconCode === 86) {
      return "heavy_snow_showers";
    } else if (iconCode === 95) {
      return "thunderstorm";
    } else if (iconCode === 96 || iconCode === 99) {
      return "strong_thunderstorm";
    }
  } else {
    if (iconCode === 0) {
      return "clear_sky";
    } else if (iconCode === 1) {
      return "mostly_clear";
    } else if (iconCode === 2) {
      return "partly_cloudy";
    } else if (iconCode === 3) {
      return "overcast";
    } else if (iconCode === 45 || iconCode === 48) {
      return "fog";
    } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
      return "drizzle";
    } else if (iconCode === 56 || iconCode === 57) {
      return "freezing_drizzle";
    } else if (iconCode === 61 || iconCode === 63) {
      return "moderate_rain";
    } else if (iconCode === 65) {
      return "heavy_intensity_rain";
    } else if (iconCode === 66 || iconCode === 67) {
      return "freezing_rain";
    } else if (iconCode === 71) {
      return "slight_snow";
    } else if (iconCode === 73) {
      return "moderate_snow";
    } else if (iconCode === 75) {
      return "heavy_intensity_snow";
    } else if (iconCode === 77) {
      return "snow_grains";
    } else if (iconCode === 80 || iconCode === 81) {
      return "rain_showers";
    } else if (iconCode === 82) {
      return "heavy_rain_showers";
    } else if (iconCode === 85) {
      return "slight_snow_showers";
    } else if (iconCode === 86) {
      return "heavy_snow_showers";
    } else if (iconCode === 95) {
      return "thunderstorm";
    } else if (iconCode === 96 || iconCode === 99) {
      return "strong_thunderstorm";
    }
  }

  return "";
}

function getWeatherLabelWidgetLang(iconCode, isDay, langCode) {
  const translationKey = GetWeatherLabelWidget(iconCode, isDay);

  const translatedLabel = getTranslationByLang(langCode, translationKey);

  return translatedLabel || "Unknown weather";
}

// day icons



function GetWeatherIconDayWidget(iconCode) {


    if (iconCode === 0) {
      return "sunny";
    } else if (iconCode === 1) {
      return "mostly_sunny";
    } else if (iconCode === 2) {
      return "partly_cloudy";
    } else if (iconCode === 3) {
      return "cloudy";
    } else if (iconCode === 45 || iconCode === 48) {
      return "haze_fog_dust_smoke";
    } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
      return "drizzle";
    } else if (iconCode === 56 || iconCode === 57) {
      return "wintry_mix_rain_snow";
    } else if (iconCode === 61 || iconCode === 63) {
      return "showers_rain";
    } else if (iconCode === 65) {
      return "heavy_rain";
    } else if (iconCode === 66 || iconCode === 67) {
      return "sleet_hail";
    } else if (iconCode === 71) {
      return "snow_showers_snow";
    } else if (iconCode === 73) {
      return "snow_showers_snow";
    } else if (iconCode === 75) {
      return "heavy_snow";
    } else if (iconCode === 77) {
      return "flurries";
    } else if (iconCode === 80 || iconCode === 81) {
      return "showers_rain";
    } else if (iconCode === 82) {
      return "heavy_rain";
    } else if (iconCode === 85) {
      return "snow_showers_snow";
    } else if (iconCode === 86) {
      return "heavy_snow";
    } else if (iconCode === 95) {
      return "isolated_scattered_tstorms_day";
    } else if (iconCode === 96 || iconCode === 99) {
      return "strong_tstorms";
    }


return iconCodeDay
}

// night icons

function GetWeatherIconNightWidget(iconCode) {


    if (iconCode === 0) {
      return "clear_night";
    } else if (iconCode === 1) {
      return "mostly_clear_night";
    } else if (iconCode === 2) {
      return "partly_cloudy_night";
    } else if (iconCode === 3) {
      return "cloudy";
    } else if (iconCode === 45 || iconCode === 48) {
      return "haze_fog_dust_smoke";
    } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
      return "drizzle";
    } else if (iconCode === 56 || iconCode === 57) {
      return "wintry_mix_rain_snow";
    } else if (iconCode === 61 || iconCode === 63) {
      return "showers_rain";
    } else if (iconCode === 65) {
      return "heavy_rain";
    } else if (iconCode === 66 || iconCode === 67) {
      return "sleet_hail";
    } else if (iconCode === 71) {
      return "snow_showers_snow";
    } else if (iconCode === 73) {
      return "snow_showers_snow";
    } else if (iconCode === 75) {
      return "heavy_snow";
    } else if (iconCode === 77) {
      return "flurries";
    } else if (iconCode === 80 || iconCode === 81) {
      return "showers_rain";
    } else if (iconCode === 82) {
      return "heavy_rain";
    } else if (iconCode === 85) {
      return "snow_showers_snow";
    } else if (iconCode === 86) {
      return "heavy_snow";
    } else if (iconCode === 95) {
      return "isolated_scattered_tstorms_night";
    } else if (iconCode === 96 || iconCode === 99) {
      return "strong_tstorms";
    }

      return iconCodeNight
}


document.addEventListener('DOMContentLoaded', () =>{
  createWidgetData()
})
