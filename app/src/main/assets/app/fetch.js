
function getCountryName(code) {
  return countryNames[code] || "Unknown Country";
}


async function DecodeWeather(lat, lon) {
  const apiKey = 'KEYS';
  const url = `https://api.timezonedb.com/v2.1/get-time-zone?key=${apiKey}&format=json&by=position&lat=${lat}&lng=${lon}`;

  try {
    const response = await fetch(url);
    const data = await response.json();

    if (data.status === 'OK') {
      FetchWeather(lat, lon, data.zoneName)
    } else {
      console.error('Error fetching timezone:', data);
      DecodeWeather(lat, lon)
      return null;
    }
  } catch (error) {
    console.error('Error:', error);
    DecodeWeather(lat, lon)
    return null;
  }
}


function FetchWeather(lat, lon, timezone) {

showLoader();

        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

  fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m&hourly=wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,daylight_duration,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max&timezone=${timezone}&forecast_days=14&forecast_hours=24`)
    .then(response => response.json())
    .then(data => {



      // send hour

            if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {
              FetchWeatherMetNorway(lat, lon)
              CurrentWeather(data.current, data.daily.sunrise[0], data.daily.sunset[0])
              localStorage.setItem('DailyWeatherCache', JSON.stringify(data.daily));
              UvIndex(data.hourly.uv_index[0])
              DailyWeather(data.daily)
                       saveCache(lat, lon, timezone)
           localStorage.setItem('CurrentHourlyCache', JSON.stringify(data.hourly))
document.querySelector('.data_provider_name_import').innerHTML = 'Data by Met norway';

            } else if(localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {
              FetchWeatherAccuweather(lat, lon)
              CurrentWeather(data.current, data.daily.sunrise[0], data.daily.sunset[0])
              localStorage.setItem('DailyWeatherCache', JSON.stringify(data.daily));
              UvIndex(data.hourly.uv_index[0])
              DailyWeather(data.daily)
                        saveCache(lat, lon, timezone)
         localStorage.setItem('CurrentHourlyCache', JSON.stringify(data.hourly))
          document.querySelector('.data_provider_name_import').innerHTML = 'Data by Accuweather';
            }
             else {
              DailyWeather(data.daily)
              HourlyWeather(data);
              localStorage.setItem('DailyWeatherCache', JSON.stringify(data.daily));
              localStorage.setItem('CurrentHourlyCache', JSON.stringify(data.hourly))
              saveCache(lat, lon, timezone)
              CurrentWeather(data.current, data.daily.sunrise[0], data.daily.sunset[0])
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by Open-Meteo';
            }




    const checkIFitsSavedLocation = JSON.parse(localStorage.getItem('DefaultLocation'));

      function isApproxEqual(val1, val2, epsilon = 0.0001) {
        return Math.abs(val1 - val2) < epsilon;
      }

      if (checkIFitsSavedLocation) {
        const savedLat = parseFloat(checkIFitsSavedLocation.lat);
        const savedLon = parseFloat(checkIFitsSavedLocation.lon);
        const savedName = checkIFitsSavedLocation.name;

        if ((savedLat !== undefined && savedLon !== undefined && isApproxEqual(lat, savedLat) && isApproxEqual(lon, savedLon)) ||
            (savedName === "CurrentDeviceLocation")) {
          cacheOfflineCurrentData(data, data.daily, data.current, data.daily.sunrise[0], data.daily.sunset[0]);
        }
      }



      FetchAirQuality(lat, lon, timezone)





MoreDetails(lat, lon)


      function MoreDetails(latSum, lonSum) {
        fetch(`https://api.weatherapi.com/v1/forecast.json?key=KEYS&q=${latSum},${lonSum}`)
            .then(response => response.json())
            .then(data => {
              MoreDetailsRender(data)

              const checkIFitsSavedLocation = JSON.parse(localStorage.getItem('DefaultLocation'));

              function isApproxEqual(val1, val2, epsilon = 0.0001) {
                return Math.abs(val1 - val2) < epsilon;
              }

              if (checkIFitsSavedLocation) {
                const savedLat = parseFloat(checkIFitsSavedLocation.lat);
                const savedLon = parseFloat(checkIFitsSavedLocation.lon);
                const savedName = checkIFitsSavedLocation.name;

                if ((savedLat !== undefined && savedLon !== undefined && isApproxEqual(lat, savedLat) && isApproxEqual(lon, savedLon)) ||
                    (savedName === "CurrentDeviceLocation")) {
            localStorage.setItem('OfflineMoreDetailsSummaryData', JSON.stringify(data));

                }
              }

            });
          }

        astronomyData(lat, lon)


          function astronomyData(latSum, lonSum) {
            fetch(`https://api.weatherapi.com/v1/astronomy.json?key=KEYS&q=${latSum},${lonSum}`)
                .then(response => response.json())
                .then(data => {

              astronomyDataRender(data)


                  const checkIFitsSavedLocation = JSON.parse(localStorage.getItem('DefaultLocation'));

                  function isApproxEqual(val1, val2, epsilon = 0.0001) {
                    return Math.abs(val1 - val2) < epsilon;
                  }

                  if (checkIFitsSavedLocation) {
                    const savedLat = parseFloat(checkIFitsSavedLocation.lat);
                    const savedLon = parseFloat(checkIFitsSavedLocation.lon);
                    const savedName = checkIFitsSavedLocation.name;

                    if ((savedLat !== undefined && savedLon !== undefined && isApproxEqual(lat, savedLat) && isApproxEqual(lon, savedLon)) ||
                        (savedName === "CurrentDeviceLocation")) {
                localStorage.setItem('OfflineastronomyData', JSON.stringify(data));

                    }
                  }
                });
              }
      FetchAlert(lat, lon)


const weatherCodeGroups = {
        "0": [0],
        "1": [1],
        "2": [2],
        "3": [3],
        "45": [45],
        "48": [48],
        "51": [51],
        "53": [53],
        "55": [55],
        "56": [56],
        "57": [57],
        "61": [61],
        "63": [63],
        "65": [65],
        "66": [66],
        "67": [67],
        "71": [71],
        "73": [73],
        "75": [75],
        "77": [77],
        "80": [80],
        "81": [81],
        "82": [82],
        "85": [85],
        "86": [86],
        "95": [95],
        "96": [96],
        "99": [99]
    };


    let groupCounts = {};
    Object.keys(weatherCodeGroups).forEach(group => {
        groupCounts[group] = 0;
    });

    data.hourly.weather_code.forEach((code) => {
        if (groupCounts[code] !== undefined) {
            groupCounts[code]++;
        }
    });

    const mostFrequentGroup = Object.keys(groupCounts).reduce((a, b) => groupCounts[a] > groupCounts[b] ? a : b);
    const selectedWeatherCode = mostFrequentGroup;


    ReportFromhourly(selectedWeatherCode);

            let Visibility;
            let VisibilityUnit;


            if (SelectedVisibiltyUnit === 'mileV') {
                Visibility = Math.round(data.hourly.visibility[0] / 1609.34);
                VisibilityUnit = 'miles'
            } else {
                Visibility = Math.round(data.hourly.visibility[0] / 1000);
                VisibilityUnit = 'km'

            }


            document.getElementById('unit_visibility').innerHTML = VisibilityUnit
    document.getElementById('min-temp').innerHTML = Visibility

                let DewPointTemp


                if (SelectedTempUnit === 'fahrenheit') {
                    DewPointTemp = Math.round(celsiusToFahrenheit(data.hourly.dew_point_2m[0]))

                } else {
                    DewPointTemp = Math.round(data.hourly.dew_point_2m[0])

                }

        document.getElementById('dew_percentage').innerHTML = DewPointTemp + '°'


      hideLoader()
    }).catch(error =>{
      ShowError()
      document.getElementById('error_text_content').innerHTML = error

    })

}


// get airquality

function FetchAirQuality(lat, lon, timezone) {

  fetch(`https://air-quality-api.open-meteo.com/v1/air-quality?latitude=${lat}&longitude=${lon}&current=us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,alder_pollen,birch_pollen,grass_pollen,mugwort_pollen,olive_pollen,ragweed_pollen&timezone=${timezone}&forecast_hours=1`)
    .then(response => response.json())
    .then(data => {

      AirQuaility(data)


    const checkIFitsSavedLocation = JSON.parse(localStorage.getItem('DefaultLocation'));

    function isApproxEqual(val1, val2, epsilon = 0.0001) {
      return Math.abs(val1 - val2) < epsilon;
    }

    if (checkIFitsSavedLocation) {
      const savedLat = parseFloat(checkIFitsSavedLocation.lat);
      const savedLon = parseFloat(checkIFitsSavedLocation.lon);
      const savedName = checkIFitsSavedLocation.name;

      if ((savedLat !== undefined && savedLon !== undefined && isApproxEqual(lat, savedLat) && isApproxEqual(lon, savedLon)) ||
          (savedName === "CurrentDeviceLocation")) {
            localStorage.setItem('airQualityData', JSON.stringify(data));

      }
    }

    });

}


// selected location toggle

function seeSelectedLocation(){
  document.querySelector('selectLocationText').hidden = false;
  document.querySelector('selectLocationTextOverlay').hidden = false;

  document.querySelector('.header_hold').style.transform = 'scale(1.1)';
  document.querySelector('.header_hold').style.opacity = '0';


}

function seeSelectedLocationClose(){
  document.querySelector('selectLocationTextOverlay').hidden = true;
  document.querySelector('selectLocationText').hidden = true;
  document.querySelector('.header_hold').style.transform = '';
  document.querySelector('.header_hold').style.opacity = '';
}

document.querySelector('selectLocationTextOverlay').addEventListener('click', ()=>{
  seeSelectedLocationClose()
});




function saveCache(lat, lon, timezone) {

  fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&hourly=relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&timezone=${timezone}&forecast_days=14`)
    .then(response => response.json())
    .then(data => {


      localStorage.setItem('HourlyWeatherCache', JSON.stringify(data.hourly));

            const checkIFitsSavedLocation = JSON.parse(localStorage.getItem('DefaultLocation'));

            function isApproxEqual(val1, val2, epsilon = 0.0001) {
              return Math.abs(val1 - val2) < epsilon;
            }

            if (checkIFitsSavedLocation) {
              const savedLat = parseFloat(checkIFitsSavedLocation.lat);
              const savedLon = parseFloat(checkIFitsSavedLocation.lon);
              const savedName = checkIFitsSavedLocation.name;

              if ((savedLat !== undefined && savedLon !== undefined && isApproxEqual(lat, savedLat) && isApproxEqual(lon, savedLon)) ||
                  (savedName === "CurrentDeviceLocation")) {
                    localStorage.setItem('OfflinesaveCache', JSON.stringify(data.hourly));

              }
            }

    });

  }

  // fetch using met-norway





  function FetchWeatherMetNorway(lat, lon) {
    fetch(`https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}`)
      .then(response => response.json())
      .then(data => {
        const currentData = data.properties.timeseries[0];
        const hourlyData = data.properties.timeseries.slice(0, 24);
        const dailyData = data.properties.timeseries.filter((entry, index) => index % 24 === 0);

        renderCurrentDataMetNorway(currentData);
        renderHourlyDataMetNorway(hourlyData);






      });
  }






  // fetch ussing accuweather




  function FetchWeatherAccuweather(lat, lon) {
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
        FetchWeatherAccuweatherCurrent(locationKey)
        const hourlyForecastUrl = `https://dataservice.accuweather.com/forecasts/v1/hourly/12hour/${locationKey}?apikey=${apiKey}&metric=true`;

        return fetch(hourlyForecastUrl);
      })
      .then(response => response.json())
      .then(forecastData => {
        DisplayHourlyAccuweatherData(forecastData)


      })
      .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
      });

  }

  function FetchWeatherAccuweatherCurrent(location_key) {
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
        DisplayCurrentAccuweatherData(data)

      })
      .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
                  ToastAndroidShow.ShowToast('API Error, switching to open-meteo', 'long');
                        localStorage.setItem('selectedMainWeatherProvider', 'open-meteo');
                        setTimeout(()=>{
                          DecodeWeather(lat, lon)
                        }, 200);
      });
  }

// offline caching

function cacheOfflineCurrentData(hourlyOffline, dailyOffline, dailyCurrent, dailySunrise, dailySunset){

  localStorage.setItem('OfflineData', JSON.stringify({ hourlyOffline: hourlyOffline, dailyOffline: dailyOffline, dailyCurrent: dailyCurrent, dailySunrise: dailySunrise, dailySunset: dailySunset }));

}


function renderOfflineData(){
  const OfflineDataTotal = JSON.parse(localStorage.getItem('OfflineData'))
  const AirQuailityDataOffline = JSON.parse(localStorage.getItem('airQualityData'))
  const OfflineMoreDetailsSummaryData = JSON.parse(localStorage.getItem('OfflineMoreDetailsSummaryData'));
  const OfflineastronomyData = JSON.parse(localStorage.getItem('OfflineastronomyData'));
  const OfflinesaveCache = JSON.parse(localStorage.getItem('OfflinesaveCache'));

  const offlineDatalocationName = JSON.parse(localStorage.getItem('DefaultLocation'));

  localStorage.setItem('DailyWeatherCache', JSON.stringify(OfflineDataTotal.dailyOffline));
  localStorage.setItem('CurrentHourlyCache', JSON.stringify(OfflineDataTotal.hourlyOffline.hourly));
  localStorage.setItem('HourlyWeatherCache', JSON.stringify(OfflinesaveCache));

  document.getElementById('city-name').innerHTML = offlineDatalocationName.name;
  document.getElementById('SelectedLocationText').innerHTML = offlineDatalocationName.name;
  document.getElementById('currentLocationName').textContent = offlineDatalocationName.name;

  HourlyWeather(OfflineDataTotal.hourlyOffline)
  DailyWeather(OfflineDataTotal.dailyOffline)
  CurrentWeather(OfflineDataTotal.dailyCurrent, OfflineDataTotal.dailySunrise, OfflineDataTotal.dailySunset)
  AirQuaility(AirQuailityDataOffline)
  MoreDetailsRender(OfflineMoreDetailsSummaryData)
  astronomyDataRender(OfflineastronomyData)


  let DewPointTemp


  if (SelectedTempUnit === 'fahrenheit') {
    DewPointTemp = Math.round(celsiusToFahrenheit(OfflineDataTotal.hourlyOffline.hourly.dew_point_2m[0]))

  } else {
    DewPointTemp = Math.round(OfflineDataTotal.hourlyOffline.hourly.dew_point_2m[0])

  }

  document.getElementById('dew_percentage').innerHTML = DewPointTemp + '°'


  const weatherCodeGroups = {
    "0": [0],
    "1": [1],
    "2": [2],
    "3": [3],
    "45": [45],
    "48": [48],
    "51": [51],
    "53": [53],
    "55": [55],
    "56": [56],
    "57": [57],
    "61": [61],
    "63": [63],
    "65": [65],
    "66": [66],
    "67": [67],
    "71": [71],
    "73": [73],
    "75": [75],
    "77": [77],
    "80": [80],
    "81": [81],
    "82": [82],
    "85": [85],
    "86": [86],
    "95": [95],
    "96": [96],
    "99": [99]
  };


  let groupCounts = {};
  Object.keys(weatherCodeGroups).forEach(group => {
    groupCounts[group] = 0;
  });

  OfflineDataTotal.hourlyOffline.hourly.weather_code.forEach((code) => {
    if (groupCounts[code] !== undefined) {
      groupCounts[code]++;
    }
  });

  const mostFrequentGroup = Object.keys(groupCounts).reduce((a, b) => groupCounts[a] > groupCounts[b] ? a : b);
  const selectedWeatherCode = mostFrequentGroup;


  ReportFromhourly(selectedWeatherCode);

  let Visibility;
  let VisibilityUnit;


  if (SelectedVisibiltyUnit === 'mileV') {
    Visibility = Math.round(OfflineDataTotal.hourlyOffline.hourly.visibility[0] / 1609.34);
    VisibilityUnit = 'miles'
  } else {
    Visibility = Math.round(OfflineDataTotal.hourlyOffline.hourly.visibility[0] / 1000);
    VisibilityUnit = 'km'

  }


  document.getElementById('unit_visibility').innerHTML = VisibilityUnit
  document.getElementById('min-temp').innerHTML = Visibility

   hideLoader()
}



