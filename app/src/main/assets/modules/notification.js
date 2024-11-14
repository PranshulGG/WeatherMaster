function createNotification(temperature, condition, locationWeather, uvindex, AQI_value, iconCodeCondition, ISDAY){
    if(localStorage.getItem('UseNotification') && localStorage.getItem('UseNotification') === 'true'){
        UpdateNotificationInterface.updateNotification(`${temperature}°`, condition, locationWeather, uvindex, AQI_value, iconCodeCondition, ISDAY);
    }
//    updated & fixed
}

function removeNotification(){
    if(localStorage.getItem('UseNotification') === 'true'){
    const OfflineDataTotal = JSON.parse(localStorage.getItem('OfflineData'))
    const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
    const NotificationlocationName = JSON.parse(localStorage.getItem('DefaultLocation'));
    const AirQuailityDataNotification = JSON.parse(localStorage.getItem('airQualityData'))

    let NotificationTemp;


    if (SelectedTempUnit === 'fahrenheit') {
      NotificationTemp = Math.round(celsiusToFahrenheit(OfflineDataTotal.dailyCurrent.temperature_2m))
    } else {
      NotificationTemp = Math.round(OfflineDataTotal.dailyCurrent.temperature_2m)
    }


    let location

    if (NotificationlocationName.name === 'CurrentDeviceLocation') {
      location = 'Device';
    } else {
      location = NotificationlocationName.name;

    }

              let TempMinNoti

              if (SelectedTempUnit === 'fahrenheit') {
                TempMinNoti = Math.round(celsiusToFahrenheit(OfflineDataTotal.dailyOffline.temperature_2m_min[0]))
              } else {
                TempMinNoti = Math.round(OfflineDataTotal.dailyOffline.temperature_2m_min[0])
              }

              let TempMaxNoti

              if (SelectedTempUnit === 'fahrenheit') {
                TempMaxNoti = Math.round(celsiusToFahrenheit(OfflineDataTotal.dailyOffline.temperature_2m_max[0]))
              } else {
                TempMaxNoti = Math.round(OfflineDataTotal.dailyOffline.temperature_2m_max[0])
              }

    const aqi = AirQuailityDataNotification.current.us_aqi

    

    let aqiCategoryLevel;

    if (aqi <= 50) {
      aqiCategoryLevel = 'Good';
    } else if (aqi <= 100) {
      aqiCategoryLevel = 'Fair';
    } else if (aqi <= 150) {
      aqiCategoryLevel = 'Moderate';
    } else if (aqi <= 200) {
      aqiCategoryLevel = 'Poor';
    } else {
      aqiCategoryLevel = 'Very poor';
    }


    createNotification(NotificationTemp, getWeatherLabelInLangNoAnim(OfflineDataTotal.dailyCurrent.weather_code, 1, 'en'), location, `Max: ${TempMaxNoti}° Min: ${TempMinNoti}°`, aqiCategoryLevel, OfflineDataTotal.dailyCurrent.weather_code, OfflineDataTotal.dailyCurrent.is_day)
    
    } else{
        UpdateNotificationInterface.destroyNotification();
    }
}

function checkNotificationSetting(event){
    if (event.key === 'UseNotification'){
            removeNotification()
    }
}


window.addEventListener('storage', checkNotificationSetting);
