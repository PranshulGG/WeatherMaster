function getCountryName(code) {
  return countryNames[code] || "Unknown Country";
}

let requestQueue = [];
let isProcessingQueue = false;

async function DecodeWeather(lat, lon, suggestionText, refreshValue) {
  return new Promise((resolve, reject) => {
    requestQueue.push({
      lat,
      lon,
      suggestionText,
      refreshValue,
      resolve,
      reject,
    });

    if (!isProcessingQueue) {
      processQueue();
    }
  });
}

function processQueue() {
  if (requestQueue.length === 0) {
    isProcessingQueue = false;
    return;
  }

  isProcessingQueue = true;
  const { lat, lon, suggestionText, refreshValue, resolve, reject } =
    requestQueue.shift();

  fetch(
    `https://api.timezonedb.com/v2.1/get-time-zone?key=KEY&format=json&by=position&lat=${lat}&lng=${lon}`
  )
    .then((response) => response.json())
    .then((data) => {
      if (data.status === "OK") {
        FetchWeather(lat, lon, data.zoneName, suggestionText, refreshValue);
        resolve(data);
      } else {
        console.error("Error fetching timezone:", data);
        setTimeout(() => {
          DecodeWeather(lat, lon, suggestionText, refreshValue);
        }, 2500);
        reject(new Error("Failed to fetch timezone"));
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      setTimeout(() => {
        DecodeWeather(lat, lon, suggestionText, refreshValue); // Retry request
      }, 2500);
      reject(error);
    })
    .finally(() => {
      // Process next request after 1 second
      setTimeout(processQueue, 1000);
    });
}

async function FetchWeather(lat, lon, timezone, suggestionText, refreshValue) {
  // send hour

  if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "Met norway"
  ) {
    FetchWeatherMetNorway(lat, lon, suggestionText, refreshValue);

    saveCache(lat, lon, timezone, suggestionText);
    FetchOpenMeteo(lat, lon, timezone, suggestionText);
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Met norway";
  } else if (
    (await customStorage.getItem("ApiForAccu")) &&
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "Accuweather"
  ) {
    FetchWeatherAccuweather(lat, lon, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    FetchOpenMeteo(lat, lon, timezone, suggestionText);

    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Accuweather";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "meteoFrance"
  ) {
    FetchMeteoFrance(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDataMeteoFranceTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );

    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Météo-France";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "dwdGermany"
  ) {
    FetchDWDGermany(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDataDWDGermanyTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );

    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by DWD Germany";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "noaaUS"
  ) {
    FetchNOAAUS(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDataNOAAUSTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by NOAA U.S.";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "ecmwf"
  ) {
    FetchECMWF(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDataECMWFTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by ECMWF";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "ukMetOffice"
  ) {
    FetchukMetOffice(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDataukMetOfficeTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by UK Met Office";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "jmaJapan"
  ) {
    FetchJMAJapan(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDataJMAJapanTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by JMA Japan";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "gemCanada"
  ) {
    FetchgemCanada(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDatagemCanadaTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by GEM Canada";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "bomAustralia"
  ) {
    FetchbomAustralia(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDatabomAustraliaTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by BOM Australia";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "cmaChina"
  ) {
    FetchcmaChina(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDatacmaChinaTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by CMA China";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "knmiNetherlands"
  ) {
    FetchknmiNetherlands(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDataknmiNetherlandsTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by KNMI Netherlands";
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "dmiDenmark"
  ) {
    FetchdmiDenmark(lat, lon, timezone, suggestionText, refreshValue);
    saveCache(lat, lon, timezone, suggestionText);
    await customStorage.setItem(
      `WeatherDatadmiDenmarkTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by DMI Denmark";
  } else {
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Open-meteo";
    FetchOpenMeteo(lat, lon, timezone, suggestionText, refreshValue);
  }

  FetchAirQuality(lat, lon, timezone, suggestionText);

  MoreDetails(lat, lon, suggestionText);

  async function MoreDetails(latSum, lonSum, suggestionText) {
    try {
      const response = await fetch(
        `https://api.weatherapi.com/v1/forecast.json?key=KEY&q=${latSum},${lonSum}`
      );
      const data = await response.json();
      await customStorage.setItem(
        `MoreDetailsData_${suggestionText}`,
        JSON.stringify(data)
      );
    } catch (error) {
      console.error("Error fetching or storing data:", error);
    }
  }

  astronomyData(lat, lon, suggestionText);

  async function astronomyData(latSum, lonSum, suggestionText) {
    try {
      const response = await fetch(
        `https://api.weatherapi.com/v1/astronomy.json?key=KEY&q=${latSum},${lonSum}`
      );
      const data = await response.json();
      await customStorage.setItem(
        `AstronomyData_${suggestionText}`,
        JSON.stringify(data)
      );
    } catch (error) {
      console.error("Error fetching or storing data:", error);
    }
  }

  FetchAlert(lat, lon, suggestionText);

  async function FetchAlert(lat, lon, suggestionText) {
    try {
      const response = await fetch(
        `https://api.weatherapi.com/v1/alerts.json?key=KEY&q=${lat},${lon}`
      );
      const data = await response.json();
      await customStorage.setItem(
        `AlertData_${suggestionText}`,
        JSON.stringify(data)
      );
    } catch (error) {
      console.error("Error fetching or storing data:", error);
    }
  }

  document.querySelector("savedLocationsHolder").innerHTML =
    '<empty_loader style="display: flex; align-items: center; justify-content: center;"><md-circular-progress indeterminate></md-circular-progress></empty_loader>';
}

// save open-meteo for invalid data

async function FetchOpenMeteo(
  lat,
  lon,
  timezone,
  suggestionText,
  refreshValue
) {
  const currentSelectedProvider = await customStorage.getItem(
    "selectedMainWeatherProvider"
  );
  const WeatherModel =
    (await customStorage.getItem("selectedWeatherModel")) || "best_match";

  if (
    currentSelectedProvider === "dwdGermany" ||
    currentSelectedProvider === "noaaUS" ||
    currentSelectedProvider === "meteoFrance" ||
    currentSelectedProvider === "ecmwf" ||
    currentSelectedProvider === "ukMetOffice" ||
    currentSelectedProvider === "jmaJapan" ||
    currentSelectedProvider === "gemCanada" ||
    currentSelectedProvider === "bomAustralia" ||
    currentSelectedProvider === "cmaChina" ||
    currentSelectedProvider === "knmiNetherlands" ||
    currentSelectedProvider === "dmiDenmark"
  ) {
  } else {
    try {
      const response = await fetch(
        `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m&hourly=wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,daylight_duration,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max&timezone=${timezone}&forecast_days=14&forecast_hours=24&models=${WeatherModel}`
      );
      const data = await response.json();

      saveCache(lat, lon, timezone, suggestionText);
      await customStorage.setItem(
        `WeatherDataOpenMeteo_${suggestionText}`,
        JSON.stringify(data, new Date().toISOString())
      );
      await customStorage.setItem(
        `WeatherDataOpenMeteoTimeStamp_${suggestionText}`,
        new Date().toISOString()
      );

      if (
        currentSelectedProvider === "dwdGermany" ||
        currentSelectedProvider === "noaaUS" ||
        currentSelectedProvider === "meteoFrance" ||
        currentSelectedProvider === "ecmwf" ||
        currentSelectedProvider === "ukMetOffice" ||
        currentSelectedProvider === "jmaJapan" ||
        currentSelectedProvider === "gemCanada" ||
        currentSelectedProvider === "bomAustralia" ||
        currentSelectedProvider === "cmaChina" ||
        currentSelectedProvider === "knmiNetherlands" ||
        currentSelectedProvider === "dmiDenmark" ||
        currentSelectedProvider === "Accuweather" ||
        currentSelectedProvider === "Met norway"
      ) {
      } else {
        renderLatestData(lat, lon, suggestionText, refreshValue);
      }
    } catch (error) {
      console.error("Error fetching or storing data:", error);
    }
  }
}

// get airquality

async function FetchAirQuality(lat, lon, timezone, suggestionText) {
  try {
    const response = await fetch(
      `https://air-quality-api.open-meteo.com/v1/air-quality?latitude=${lat}&longitude=${lon}&current=us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,alder_pollen,birch_pollen,grass_pollen,mugwort_pollen,olive_pollen,ragweed_pollen&timezone=${timezone}&forecast_hours=1`
    );
    const data = await response.json();

    await customStorage.setItem(
      `AirQuality_${suggestionText}`,
      JSON.stringify(data)
    );
  } catch (error) {
    console.error("Error fetching or storing data:", error);
  }
}

// selected location toggle

function seeSelectedLocation() {
  document.querySelector("selectLocationText").hidden = false;
  document.querySelector("selectLocationTextOverlay").hidden = false;

  document.querySelector(".header_hold").style.transform = "scale(1.1)";
  document.querySelector(".header_hold").style.opacity = "0";
}

function seeSelectedLocationClose() {
  document.querySelector("selectLocationTextOverlay").hidden = true;
  document.querySelector("selectLocationText").hidden = true;
  document.querySelector(".header_hold").style.transform = "";
  document.querySelector(".header_hold").style.opacity = "";
}

document
  .querySelector("selectLocationTextOverlay")
  .addEventListener("click", () => {
    seeSelectedLocationClose();
  });

async function saveCache(lat, lon, timezone, suggestionText) {
  try {
    const response = await fetch(
      `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&hourly=relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&timezone=${timezone}&forecast_days=14`
    );
    const data = await response.json();
    await customStorage.setItem(
      `HourlyWeatherCache_${suggestionText}`,
      JSON.stringify(data.hourly)
    );
  } catch (error) {
    console.error("Error fetching or storing data:", error);
  }
}

// fetch using met-norway

async function FetchWeatherMetNorway(lat, lon, suggestionText, refreshValue) {
  try {
    const response = await fetch(
      `https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}`
    );
    const data = await response.json();

    await customStorage.setItem(
      `WeatherDataMetNorway_${suggestionText}`,
      JSON.stringify(data, new Date().toISOString())
    );
    await customStorage.setItem(
      `WeatherDataMetNorwayTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );
    const dailyData = data.properties.timeseries.filter(
      (entry, index) => index % 24 === 0
    );

    renderLatestData(lat, lon, suggestionText, refreshValue);
  } catch (error) {
    console.error("Error fetching or storing data:", error);
  }
}

// fetch ussing accuweather

async function FetchWeatherAccuweather(lat, lon, suggestionText, refreshValue) {
  const apiKey = await customStorage.getItem("ApiForAccu");

  const geoPositionUrl = `https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=${apiKey}&q=${lat},${lon}`;

  try {
    const response = await fetch(geoPositionUrl);
    if (!response.ok) {
      throw new Error("Network response was not ok " + response.statusText);
    }
    const locationData = await response.json();
    const locationKey = locationData.Key;
    await FetchWeatherAccuweatherCurrent(
      locationKey,
      lat,
      lon,
      suggestionText,
      refreshValue
    );

    const hourlyForecastUrl = `https://dataservice.accuweather.com/forecasts/v1/hourly/12hour/${locationKey}?apikey=${apiKey}&metric=true`;
    const forecastResponse = await fetch(hourlyForecastUrl);
    const forecastData = await forecastResponse.json();

    await customStorage.setItem(
      `WeatherDataAccuHourly_${suggestionText}`,
      JSON.stringify(forecastData)
    );

    renderLatestData(lat, lon, suggestionText, refreshValue);
  } catch (error) {
    console.error("There was a problem with the fetch operation:", error);
  }
}

async function FetchWeatherAccuweatherCurrent(
  location_key,
  lat,
  lon,
  suggestionText,
  refreshValue
) {
  const locationKey = location_key;
  const apiKey = await customStorage.getItem("ApiForAccu");

  const weatherUrl = `https://dataservice.accuweather.com/currentconditions/v1/${locationKey}?apikey=${apiKey}`;

  try {
    const response = await fetch(weatherUrl);
    if (!response.ok) {
      throw new Error("Network response was not ok " + response.statusText);
    }
    const data = await response.json();

    await customStorage.setItem(
      `WeatherDataAccuCurrent_${suggestionText}`,
      JSON.stringify(data)
    );
    await customStorage.setItem(
      `WeatherDataAccuTimeStamp_${suggestionText}`,
      new Date().toISOString()
    );

    renderLatestData(lat, lon, suggestionText, refreshValue);
  } catch (error) {
    console.error("There was a problem with the fetch operation:", error);
    ShowSnackMessage.ShowSnack("API Error, Please change provider", "long");
  }
}

// refresh the data latest

async function renderLatestData(lat, lon, suggestionText, refreshValue) {
  console.log(suggestionText, refreshValue);

  const SavedLocation = JSON.parse(
    await customStorage.getItem("DefaultLocation")
  );
  const currentLocationName = await customStorage.getItem(
    "CurrentLocationName"
  );

  if (
    suggestionText === SavedLocation.name ||
    (suggestionText === "CurrentDeviceLocation" &&
      !refreshValue === "no_data_render" &&
      !refreshValue === `Refreshed_${suggestionText}`)
  ) {
    setTimeout(() => {
      ReturnHomeLocation();
      createWidgetData();
      AndroidInterface.updateStatusBarColor("StopRefreshingLoader");
      document.querySelector(".no_touch_screen").hidden = true;
      onAllLocationsLoaded();
      hideLoader();
      console.log("LOADED");
      createWidgetData();
    }, 300);
  } else if (refreshValue) {
    if (refreshValue === "no_data_render") {
      onAllLocationsLoaded();
    } else if (refreshValue === `Refreshed_${suggestionText}`) {
      setTimeout(async () => {
        AndroidInterface.updateStatusBarColor("StopRefreshingLoader");
        document.querySelector(".no_touch_screen").hidden = true;
        LoadLocationOnRequest(lat, lon, suggestionText);
        document.getElementById("last_updated").innerHTML = `Updated, just now`;
        ShowSnackMessage.ShowSnack(
          getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "latest_data_fetched"
          ),
          "short"
        );

        onAllLocationsLoaded();
        createWidgetData();
      }, 1500);
    }
  }
}
