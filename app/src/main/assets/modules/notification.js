function createNotification(condition, locationWeather, uvindex, iconCodeCondition, ISDAY, chanceOfRain){
  if(localStorage.getItem('UseNotification') && localStorage.getItem('UseNotification') === 'true'){
      UpdateNotificationInterface.updateNotification(condition, locationWeather, uvindex, iconCodeCondition, ISDAY, chanceOfRain);
  }
}


function checkNotification(){
  if(localStorage.getItem('UseNotification') === 'true'){

     const DefaultLocation = JSON.parse(localStorage.getItem('DefaultLocation'));
     const notificationData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${DefaultLocation.name}`))

          let location

          if (DefaultLocation.name === 'CurrentDeviceLocation') {
            location = 'Device';
          } else {
            location = DefaultLocation.name;

          }

     let TempMinNoti

     if (SelectedTempUnit === 'fahrenheit') {
       TempMinNoti = Math.round(celsiusToFahrenheit(notificationData.daily.temperature_2m_min[0]))
     } else {
       TempMinNoti = Math.round(notificationData.daily.temperature_2m_min[0])
     }

     let TempMaxNoti

     if (SelectedTempUnit === 'fahrenheit') {
       TempMaxNoti = Math.round(celsiusToFahrenheit(notificationData.daily.temperature_2m_max[0]))
     } else {
       TempMaxNoti = Math.round(notificationData.daily.temperature_2m_max[0])
     }

      createNotification(getWeatherLabelInLangNoAnim(notificationData.daily.weather_code[0], 1, 'en'), location, `Max: ${TempMaxNoti}° Min: ${TempMinNoti}°`, notificationData.daily.weather_code[0], 1, `${notificationData.daily.precipitation_probability_max[0]}%`)

  } else{
      UpdateNotificationInterface.destroyNotification();
  }
}

function checkNotificationSetting(event){
  if (event.key === 'UseNotification'){
        checkNotification()
  }
}


window.addEventListener('storage', checkNotificationSetting);


document.addEventListener('DOMContentLoaded', () =>{
  checkNotification()
});