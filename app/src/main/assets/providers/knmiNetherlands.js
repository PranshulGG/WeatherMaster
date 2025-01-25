// fetch using KNMI Netherlands


function FetchknmiNetherlands(lat, lon, timezone, suggestionText, refreshValue) {


    fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,is_day,apparent_temperature,pressure_msl,relative_humidity_2m,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m,wind_gusts_10m&hourly=wind_speed_10m,wind_direction_10m,relative_humidity_2m,pressure_msl,cloud_cover,temperature_2m,dew_point_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,visibility,uv_index&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,uv_index_max,precipitation_sum,daylight_duration,precipitation_probability_max,precipitation_hours,wind_speed_10m_max,wind_gusts_10m_max&timezone=${timezone}&models=knmi_seamless&forecast_days=7&forecast_hours=24`)
      .then(response => response.json())
      .then(data => {
  
        localStorage.setItem(`WeatherDataknmiNetherlands_${suggestionText}`, JSON.stringify(data, new Date().toISOString()))

  renderLatestData(lat, lon, suggestionText, refreshValue)
    })
    .catch(error => {
      console.error('There was an error fetching the weather data:', error);
            ShowSnackMessage.ShowSnack(
              getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "data_fetch_error"
              ),
              "long"
            );
     AndroidInterface.updateStatusBarColor('StopRefreshingLoader');
      document.querySelector('.no_touch_screen').hidden = true;
      return
    });
  }