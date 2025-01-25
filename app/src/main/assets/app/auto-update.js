function updateOldData(latSend, longSend, CurrentLocationName){


    let weatherDataKey;
    if (
      localStorage.getItem("selectedMainWeatherProvider") === "Met norway"
    ) {
      weatherDataKey = `WeatherDataMetNorwayTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("ApiForAccu") &&
      localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
    ) {
      weatherDataKey = `WeatherDataAccuCurrentTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
    ) {
      weatherDataKey = `WeatherDataMeteoFranceTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
    ) {
      weatherDataKey = `WeatherDataDWDGermanyTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "noaaUS"
    ) {
      weatherDataKey = `WeatherDataNOAAUSTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ecmwf"
    ) {
      weatherDataKey = `WeatherDataECMWFTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
    ) {
      weatherDataKey = `WeatherDataukMetOfficeTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
    ) {
      weatherDataKey = `WeatherDataJMAJapanTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
    ) {
      weatherDataKey = `WeatherDatagemCanadaTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
    ) {
      weatherDataKey = `WeatherDatabomAustraliaTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
    ) {
      weatherDataKey = `WeatherDatacmaChinaTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") ===
      "knmiNetherlands"
    ) {
      weatherDataKey = `WeatherDataknmiNetherlandsTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
    ) {
      weatherDataKey = `WeatherDatadmiDenmarkTimeStamp_${CurrentLocationName}`;
    } else {
      weatherDataKey = `WeatherDataOpenMeteoTimeStamp_${CurrentLocationName}`;
    }

    const weatherData = localStorage.getItem(weatherDataKey);


    const providedDate = new Date(weatherData);

    const currentDate = new Date();

    const timeDifference = currentDate - providedDate;

    const timeDifferenceInMinutes = timeDifference / (1000 * 60);


    if (timeDifferenceInMinutes > 72) {
        document.querySelector('auto-update').hidden = false;
        DecodeWeather(
            latSend,
            longSend,
            CurrentLocationName,
            `Refreshed_${CurrentLocationName}`
          );
    }



}