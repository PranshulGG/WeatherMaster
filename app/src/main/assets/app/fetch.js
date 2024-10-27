
function getCountryName(code) {
  return countryNames[code] || "Unknown Country";
}


async function DecodeWeather(lat, lon) {
  const apiKey = ' YOUR_KEY';
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


      HourlyWeather(data);


      // send daily


      DailyWeather(data.daily)

      // Cache weather for different pages

      localStorage.setItem('DailyWeatherCache', JSON.stringify(data.daily));
      localStorage.setItem('CurrentHourlyCache', JSON.stringify(data.hourly))


      // send current

      CurrentWeather(data.current, data.daily.sunrise[0], data.daily.sunset[0])

      saveCache(lat, lon, timezone)

      // send airquality

      FetchAirQuality(lat, lon, timezone)



      // send other data

      MoreDetails(lat, lon)
      astronomyData(lat, lon)
      FetchAlert(lat, lon)


      getOpenWeatherMainTemp()

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

    });

  }
  