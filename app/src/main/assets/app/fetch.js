
function getCountryName(code) {
  return countryNames[code] || "Unknown Country";
}


async function DecodeWeather(lat, lon, suggestionText, refreshValue){
  const apiKey = 'API';
  const url = `https://api.timezonedb.com/v2.1/get-time-zone?key=${apiKey}&format=json&by=position&lat=${lat}&lng=${lon}`;

  try {
    const response = await fetch(url);
    const data = await response.json();

    if (data.status === 'OK') {
      FetchWeather(lat, lon, data.zoneName, suggestionText, refreshValue)
    } else {
      console.error('Error fetching timezone:', data);
          setTimeout(() =>{
          DecodeWeather(lat, lon, suggestionText, refreshValue)
          }, 2500)
      return null;
    }
  } catch (error) {
    console.error('Error:', error);
    setTimeout(() =>{
    DecodeWeather(lat, lon, suggestionText, refreshValue)
    }, 2500)
    return null;
  }
}


function FetchWeather(lat, lon, timezone, suggestionText, refreshValue) {


  fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m&hourly=wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,daylight_duration,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max&timezone=${timezone}&forecast_days=14&forecast_hours=24`)
    .then(response => response.json())
    .then(data => {



      // send hour

            if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {
              FetchWeatherMetNorway(lat, lon, suggestionText)

                       saveCache(lat, lon, timezone, suggestionText)
            localStorage.setItem(`WeatherDataOpenMeteo_${suggestionText}`, JSON.stringify(data, new Date().toISOString()))

document.querySelector('.data_provider_name_import').innerHTML = 'Data by Met norway';

            } else if(localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {
              FetchWeatherAccuweather(lat, lon, suggestionText)
                        saveCache(lat, lon, timezone, suggestionText)
            localStorage.setItem(`WeatherDataOpenMeteo_${suggestionText}`, JSON.stringify(data, new Date().toISOString()))

          document.querySelector('.data_provider_name_import').innerHTML = 'Data by Accuweather';
            }
             else {
             saveCache(lat, lon, timezone, suggestionText)
            localStorage.setItem(`WeatherDataOpenMeteo_${suggestionText}`, JSON.stringify(data))
            localStorage.setItem(`WeatherDataOpenMeteoTimeStamp_${suggestionText}`, new Date().toISOString())

            document.querySelector('.data_provider_name_import').innerHTML = 'Data by Open-Meteo';
            }





      FetchAirQuality(lat, lon, timezone, suggestionText)


      MoreDetails(lat, lon, suggestionText)


      function MoreDetails(latSum, lonSum, suggestionText) {
        fetch(`https://api.weatherapi.com/v1/forecast.json?key=API&q=${latSum},${lonSum}`)
            .then(response => response.json())
            .then(data => {


              localStorage.setItem(`MoreDetailsData_${suggestionText}`, JSON.stringify(data));



            });
          }

        astronomyData(lat, lon, suggestionText)


          function astronomyData(latSum, lonSum, suggestionText) {
            fetch(`https://api.weatherapi.com/v1/astronomy.json?key=API&q=${latSum},${lonSum}`)
                .then(response => response.json())
                .then(data => {


                  localStorage.setItem(`AstronomyData_${suggestionText}`, JSON.stringify(data));


                });
              }
      FetchAlert(lat, lon, suggestionText)

      function FetchAlert(lat, lon, suggestionText){
        fetch(`https://api.weatherapi.com/v1/alerts.json?key=API&q=${lat},${lon}`)
        .then(response => response.json())
        .then(data => {

          localStorage.setItem(`AlertData_${suggestionText}`, JSON.stringify(data));

        })
      }

      console.log(suggestionText)

IFSavedLocation(lat, lon,suggestionText)

    function IFSavedLocation(lat, lon,suggestionText){
        const SavedLocation = JSON.parse(localStorage.getItem('DefaultLocation'));
        const currentLocationName = localStorage.getItem('CurrentLocationName')

    if(suggestionText === SavedLocation.name || suggestionText === 'CurrentDeviceLocation' && suggestionText === currentLocationName || !refreshValue){
      setTimeout(()=>{
        ReturnHomeLocation()
       }, 1000);
    } else if (refreshValue){
                setTimeout(() =>{
                    LoadLocationOnRequest(lat, lon, suggestionText)
                     document.getElementById('last_updated').innerHTML = `Updated, just now`;
                     ShowSnackMessage.ShowSnack("Latest data fetched", "short");
                }, 1000)
                setTimeout(() =>{
                     document.getElementById('last_updated').innerHTML = `Updated, just now`;
                }, 2000)
    }
    }

document.querySelector('savedLocationsHolder').innerHTML = ''


setTimeout(() => {
    loadSavedLocations()
}, 1000)


hideLoader()
    }).catch(error =>{
document.querySelector('savedLocationsHolder').innerHTML = ''

setTimeout(() => {
    loadSavedLocations()
            ReturnHomeLocation()
}, 1000)
      document.getElementById('error_text_content').innerHTML = error

    })

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




function saveCache(lat, lon, timezone, suggestionText) {

  fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&hourly=relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&timezone=${timezone}&forecast_days=14`)
    .then(response => response.json())
    .then(data => {

      localStorage.setItem(`HourlyWeatherCache_${suggestionText}`, JSON.stringify(data.hourly));


    });

  }

  // fetch using met-norway







  function FetchWeatherMetNorway(lat, lon, suggestionText) {
    fetch(`https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=${lat}&lon=${lon}`)
      .then(response => response.json())
      .then(data => {
        console.log('Suggestion Text:', suggestionText);

        localStorage.setItem(`WeatherDataMetNorway_${suggestionText}`, JSON.stringify(data, new Date().toISOString()))
            localStorage.setItem(`WeatherDataMetNorwayTimeStamp_${suggestionText}`, new Date().toISOString())
        const dailyData = data.properties.timeseries.filter((entry, index) => index % 24 === 0);


        document.querySelector('savedLocationsHolder').innerHTML = ''


        setTimeout(() => {
            loadSavedLocations()
        }, 1000)

      });
  }





  // fetch ussing accuweather




  function FetchWeatherAccuweather(lat, lon, suggestionText) {
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
        FetchWeatherAccuweatherCurrent(locationKey, lat, lon, suggestionText)
        const hourlyForecastUrl = `https://dataservice.accuweather.com/forecasts/v1/hourly/12hour/${locationKey}?apikey=${apiKey}&metric=true`;

        return fetch(hourlyForecastUrl);
      })
      .then(response => response.json())
      .then(forecastData => {
        localStorage.setItem(`WeatherDataAccuHourly_${suggestionText}`, JSON.stringify(forecastData))

document.querySelector('savedLocationsHolder').innerHTML = ''


setTimeout(() => {
    loadSavedLocations()
}, 1000)

      })
      .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
      });

  }

  function FetchWeatherAccuweatherCurrent(location_key, lat, lon, suggestionText) {
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


document.querySelector('savedLocationsHolder').innerHTML = ''


setTimeout(() => {
    loadSavedLocations()
}, 1000)

      })
      .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
                  ToastAndroidShow.ShowToast('API Error, Please change provider', 'long');
      });
  }





