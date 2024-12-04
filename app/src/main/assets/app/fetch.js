
function getCountryName(code) {
  return countryNames[code] || "Unknown Country";
}


let requestQueue = [];
let isProcessingQueue = false;

async function DecodeWeather(lat, lon, suggestionText, refreshValue) {
  return new Promise((resolve, reject) => {
    requestQueue.push({ lat, lon, suggestionText, refreshValue, resolve, reject });

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
  const { lat, lon, suggestionText, refreshValue, resolve, reject } = requestQueue.shift();

  fetch(`https://api.timezonedb.com/v2.1/get-time-zone?key=J8HTH6P4YTGO&format=json&by=position&lat=${lat}&lng=${lon}`)
    .then(response => response.json())
    .then(data => {
      if (data.status === 'OK') {
        FetchWeather(lat, lon, data.zoneName, suggestionText, refreshValue);
        resolve(data);
      } else {
        console.error('Error fetching timezone:', data);
        setTimeout(() => {
          DecodeWeather(lat, lon, suggestionText, refreshValue);
        }, 2500);
        reject(new Error('Failed to fetch timezone'));
      }
    })
    .catch(error => {
      console.error('Error:', error);
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



function FetchWeather(lat, lon, timezone, suggestionText, refreshValue) {



  // send hour

  if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {
    FetchWeatherMetNorway(lat, lon, suggestionText, refreshValue)

    saveCache(lat, lon, timezone, suggestionText)
    FetchOpenMeteo(lat, lon, timezone, suggestionText)
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by Met norway';

  } else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {
    FetchWeatherAccuweather(lat, lon, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    FetchOpenMeteo(lat, lon, timezone, suggestionText)

    document.querySelector('.data_provider_name_import').innerHTML = 'Data by Accuweather';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance') {
    FetchMeteoFrance(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDataMeteoFranceTimeStamp_${suggestionText}`, new Date().toISOString())

    document.querySelector('.data_provider_name_import').innerHTML = 'Data by Météo-France';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dwdGermany') {
    FetchDWDGermany(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDataDWDGermanyTimeStamp_${suggestionText}`, new Date().toISOString())

    document.querySelector('.data_provider_name_import').innerHTML = 'Data by DWD Germany';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS') {
    FetchNOAAUS(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDataNOAAUSTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by NOAA U.S.';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf') {
    FetchECMWF(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDataECMWFTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by ECMWF';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice') {
    FetchukMetOffice(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDataukMetOfficeTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by UK Met Office';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan') {
    FetchJMAJapan(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDataJMAJapanTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by JMA Japan';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada') {
    FetchgemCanada(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDatagemCanadaTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by GEM Canada';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia') {
    FetchbomAustralia(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDatabomAustraliaTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by BOM Australia';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina') {
    FetchcmaChina(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDatacmaChinaTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by CMA China';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'knmiNetherlands') {
    FetchknmiNetherlands(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDataknmiNetherlandsTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by KNMI Netherlands';
  } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dmiDenmark') {
    FetchdmiDenmark(lat, lon, timezone, suggestionText, refreshValue)
    saveCache(lat, lon, timezone, suggestionText)
    localStorage.setItem(`WeatherDatadmiDenmarkTimeStamp_${suggestionText}`, new Date().toISOString())
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by DMI Denmark';
  } else {
    document.querySelector('.data_provider_name_import').innerHTML = 'Data by Open-meteo';
    FetchOpenMeteo(lat, lon, timezone, suggestionText, refreshValue)
  }





  FetchAirQuality(lat, lon, timezone, suggestionText)


  MoreDetails(lat, lon, suggestionText)


  function MoreDetails(latSum, lonSum, suggestionText) {
    fetch(`https://api.weatherapi.com/v1/forecast.json?key=ef2cb48d90984d828a8140518240209&q=${latSum},${lonSum}`)
      .then(response => response.json())
      .then(data => {


        localStorage.setItem(`MoreDetailsData_${suggestionText}`, JSON.stringify(data));



      });
  }

  astronomyData(lat, lon, suggestionText)


  function astronomyData(latSum, lonSum, suggestionText) {
    fetch(`https://api.weatherapi.com/v1/astronomy.json?key=ef2cb48d90984d828a8140518240209&q=${latSum},${lonSum}`)
      .then(response => response.json())
      .then(data => {


        localStorage.setItem(`AstronomyData_${suggestionText}`, JSON.stringify(data));


      });
  }
  FetchAlert(lat, lon, suggestionText)

  function FetchAlert(lat, lon, suggestionText) {
    fetch(`https://api.weatherapi.com/v1/alerts.json?key=10baabdf43ea48d191075955241810&q=${lat},${lon}`)
      .then(response => response.json())
      .then(data => {

        localStorage.setItem(`AlertData_${suggestionText}`, JSON.stringify(data));

      })
  }

  console.log(suggestionText)


  document.querySelector('savedLocationsHolder').innerHTML = ''


  setTimeout(() => {
    loadSavedLocations()
  }, 1000)

}


// save open-meteo for invalid data



function FetchOpenMeteo(lat, lon, timezone, suggestionText, refreshValue) {

  const currentSelectedProvider = localStorage.getItem('selectedMainWeatherProvider');

  if (currentSelectedProvider === 'dwdGermany' || currentSelectedProvider === 'noaaUS' || currentSelectedProvider === 'meteoFrance' || currentSelectedProvider === 'ecmwf' || currentSelectedProvider === 'ukMetOffice' || currentSelectedProvider === 'jmaJapan' || currentSelectedProvider === 'gemCanada' || currentSelectedProvider === 'bomAustralia' || currentSelectedProvider === 'cmaChina' || currentSelectedProvider === 'knmiNetherlands' || currentSelectedProvider === 'dmiDenmark') {


  } else {
    fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m&hourly=wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,daylight_duration,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max&timezone=${timezone}&forecast_days=14&forecast_hours=24`)
      .then(response => response.json())
      .then(data => {

        saveCache(lat, lon, timezone, suggestionText)
        localStorage.setItem(`WeatherDataOpenMeteo_${suggestionText}`, JSON.stringify(data, new Date().toISOString()))
        localStorage.setItem(`WeatherDataOpenMeteoTimeStamp_${suggestionText}`, new Date().toISOString())

      })
      .catch(error => {
        console.error('There was an error fetching the weather data:', error);
      });

  }

 if (currentSelectedProvider === 'dwdGermany' || currentSelectedProvider === 'noaaUS' || currentSelectedProvider === 'meteoFrance' || currentSelectedProvider === 'ecmwf' || currentSelectedProvider === 'ukMetOffice' || currentSelectedProvider === 'jmaJapan' || currentSelectedProvider === 'gemCanada' || currentSelectedProvider === 'bomAustralia' || currentSelectedProvider === 'cmaChina' || currentSelectedProvider === 'knmiNetherlands' || currentSelectedProvider === 'dmiDenmark' || currentSelectedProvider === 'Accuweather' || currentSelectedProvider === 'Met norway'){

 } else{
 renderLatestData(lat, lon, suggestionText, refreshValue)
 }


}


// get airquality

function FetchAirQuality(lat, lon, timezone, suggestionText) {

  fetch(`https://air-quality-api.open-meteo.com/v1/air-quality?latitude=${lat}&longitude=${lon}&current=us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,alder_pollen,birch_pollen,grass_pollen,mugwort_pollen,olive_pollen,ragweed_pollen&timezone=${timezone}&forecast_hours=1`)
    .then(response => response.json())
    .then(data => {


      localStorage.setItem(`AirQuality_${suggestionText}`, JSON.stringify(data));


    });

}


// selected location toggle

function seeSelectedLocation() {
  document.querySelector('selectLocationText').hidden = false;
  document.querySelector('selectLocationTextOverlay').hidden = false;

  document.querySelector('.header_hold').style.transform = 'scale(1.1)';
  document.querySelector('.header_hold').style.opacity = '0';


}

function seeSelectedLocationClose() {
  document.querySelector('selectLocationTextOverlay').hidden = true;
  document.querySelector('selectLocationText').hidden = true;
  document.querySelector('.header_hold').style.transform = '';
  document.querySelector('.header_hold').style.opacity = '';
}

document.querySelector('selectLocationTextOverlay').addEventListener('click', () => {
  seeSelectedLocationClose()
});




function saveCache(lat, lon, timezone, suggestionText) {

  fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&hourly=relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&timezone=${timezone}&forecast_days=14`)
    .then(response => response.json())
    .then(data => {

      localStorage.setItem(`HourlyWeatherCache_${suggestionText}`, JSON.stringify(data.hourly));


    });

}

// fetch using met-norway







function FetchWeatherMetNorway(lat, lon, suggestionText, refreshValue) {
  fetch(`https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}`)
    .then(response => response.json())
    .then(data => {
      console.log('Suggestion Text:', suggestionText);

      localStorage.setItem(`WeatherDataMetNorway_${suggestionText}`, JSON.stringify(data, new Date().toISOString()))
      localStorage.setItem(`WeatherDataMetNorwayTimeStamp_${suggestionText}`, new Date().toISOString())
      const dailyData = data.properties.timeseries.filter((entry, index) => index % 24 === 0);


      renderLatestData(lat, lon, suggestionText, refreshValue)

    });
}





// fetch ussing accuweather




function FetchWeatherAccuweather(lat, lon, suggestionText, refreshValue) {
  const apiKey = localStorage.getItem('ApiForAccu');


  const geoPositionUrl = `https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=${apiKey}&q=${lat},${lon}`;

  fetch(geoPositionUrl)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok ' + response.statusText);
      }
      return response.json();
    })
    .then(locationData => {
      const locationKey = locationData.Key;
      FetchWeatherAccuweatherCurrent(locationKey, lat, lon, suggestionText, refreshValue)
      const hourlyForecastUrl = `https://dataservice.accuweather.com/forecasts/v1/hourly/12hour/${locationKey}?apikey=${apiKey}&metric=true`;

      return fetch(hourlyForecastUrl);
    })
    .then(response => response.json())
    .then(forecastData => {
      localStorage.setItem(`WeatherDataAccuHourly_${suggestionText}`, JSON.stringify(forecastData))

      renderLatestData(lat, lon, suggestionText, refreshValue)

    })
    .catch(error => {
      console.error('There was a problem with the fetch operation:', error);
    });

}

function FetchWeatherAccuweatherCurrent(location_key, lat, lon, suggestionText, refreshValue) {
  const locationKey = location_key;
  const apiKey = localStorage.getItem('ApiForAccu');

  const weatherUrl = `https://dataservice.accuweather.com/currentconditions/v1/${locationKey}?apikey=${apiKey}`;

  fetch(weatherUrl)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok ' + response.statusText);
      }
      return response.json();
    })
    .then(data => {
      localStorage.setItem(`WeatherDataAccuCurrent_${suggestionText}`, JSON.stringify(data))
      localStorage.setItem(`WeatherDataAccuTimeStamp_${suggestionText}`, new Date().toISOString())



      renderLatestData(lat, lon, suggestionText, refreshValue)

    })
    .catch(error => {
      console.error('There was a problem with the fetch operation:', error);
      ShowSnackMessage.ShowSnack("API Error, Please change provider", "long");
    });
}





// refresh the data latest


function renderLatestData(lat, lon, suggestionText, refreshValue) {

  console.log(suggestionText, refreshValue)

  const SavedLocation = JSON.parse(localStorage.getItem('DefaultLocation'));
  const currentLocationName = localStorage.getItem('CurrentLocationName')

  if (suggestionText === SavedLocation.name || suggestionText === 'CurrentDeviceLocation' && !refreshValue === 'no_data_render' && !refreshValue === `Refreshed_${suggestionText}`) {

    setTimeout(() => {
      ReturnHomeLocation()
      createWidgetData()
     AndroidInterface.updateStatusBarColor('StopRefreshingLoader');
      document.querySelector('.no_touch_screen').hidden = true;
       onAllLocationsLoaded()
    hideLoader()
      console.log('LOADED')
    }, 1500);

  } else if (refreshValue) {
    if (refreshValue === 'no_data_render') {

    } else if(refreshValue === `Refreshed_${suggestionText}`) {
      setTimeout(() => {
       AndroidInterface.updateStatusBarColor('StopRefreshingLoader');
        document.querySelector('.no_touch_screen').hidden = true;
        LoadLocationOnRequest(lat, lon, suggestionText)
        document.getElementById('last_updated').innerHTML = `Updated, just now`;
        ShowSnackMessage.ShowSnack("Latest data fetched", "short");
               onAllLocationsLoaded()
        createWidgetData()
      }, 1500)


    }

  }

}