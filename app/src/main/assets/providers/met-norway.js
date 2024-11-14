
function renderCurrentDataMetNorway(data){
    const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');

    console.log(data)

    const weatherDetails = data.data.instant.details;

    const weather_icon = data.data.next_1_hours.summary.symbol_code; 

    let Temperature;


    if (SelectedTempUnit === 'fahrenheit') {
        Temperature = Math.round(celsiusToFahrenheit(weatherDetails.air_temperature));
    } else {
        Temperature = Math.round(weatherDetails.air_temperature);
    }

    

    animateTemp(Temperature)

    document.getElementById('weather-icon').src = getMetNorwayIcons(weather_icon)
    document.getElementById('froggie_imgs').src = getMetNorwayFrogy(weather_icon)
  
    sendThemeToAndroid(getMetNorwayTheme(weather_icon))
    document.documentElement.setAttribute('iconcodetheme', getMetNorwayTheme(weather_icon))
    document.getElementById('temPDiscCurrentLocation').innerHTML = `${Temperature}° • <span>${getMetNorwayWeatherLabelInLang(data.data.next_1_hours.summary.symbol_code,  localStorage.getItem('AppLanguageCode'))}</span>`
    document.getElementById('currentSearchImg').src = `${getMetNorwayIcons(weather_icon)}`;
    document.getElementById('description').innerHTML = getMetNorwayWeatherLabelInLang(data.data.next_1_hours.summary.symbol_code, localStorage.getItem('AppLanguageCode'))



}

function renderHourlyDataMetNorway(data) {
    console.log("Hourly weather data:", data);

    const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');


    const forecastContainer = document.getElementById('forecast');
    const RainBarsContainer = document.querySelector('rainMeterBar');

    forecastContainer.innerHTML = '';
    RainBarsContainer.innerHTML = '';

    if (!data || !Array.isArray(data)) {
        console.error("Hourly forecast data is missing or undefined");
        return;
    }





    data.forEach((hour, index) => {
        const time = hour.time;
        const forecastTime = new Date(time).getTime();

        let hours;
        let period;
        if (timeFormat === '24 hour') {
            hours = new Date(time).getHours().toString().padStart(2, '0') + ':';
            period = new Date(time).getMinutes().toString().padStart(2, '0');
        } else {
            hours = new Date(time).getHours();
            period = hours >= 12 ? "PM" : "AM";
            hours = hours % 12 || 12;
        }

        const HourWeatherCode = hour.data.next_1_hours.summary.symbol_code; 

        const rainMeterBarItem = document.createElement('rainMeterBarItem');


        let PrecAmount = `${hour.data.next_1_hours.details.precipitation_amount.toFixed(1)} mm`; 




        const maxRain = 2;
        const rainAmountPercent = (hour.data.next_1_hours.details.precipitation_amount / maxRain) * 100;

        let barColor;
        if (hour.data.next_1_hours.details.precipitation_amount < 0.5) {
            barColor = '#4c8df6';
        } else if (hour.data.next_1_hours.details.precipitation_amount > 0.5 && hour.data.next_1_hours.details.precipitation_amount <= 1) {
            barColor = 'orange';
        } else if (hour.data.next_1_hours.details.precipitation_amount > 1) {
            barColor = 'red';
        }

        let Visibility = Math.round(hour.data.instant.details.visibility / 1000);

        let HourTemperature;


        if (SelectedTempUnit === 'fahrenheit') {
            HourTemperature = Math.round(celsiusToFahrenheit(hour.data.instant.details.air_temperature));
        } else {
            HourTemperature = Math.round(hour.data.instant.details.air_temperature);
        }

        const forecastItem = document.createElement('div');
        forecastItem.classList.add('forecast-item');
        forecastItem.id = "forecast24";

        forecastItem.innerHTML = `
                <p class="temp-24">${HourTemperature}°</p>
                ${index === 0 ? `
        <svg height="33.0dip" width="33.0dip" viewBox="0 0 33.0 33.0"
            xmlns="http://www.w3.org/2000/svg" class="hourly_forecast_star">
            <path style="fill: var(--Inverse-Primary);" d="M20.926,1.495C27.8,-1.49 34.776,5.486 31.791,12.36L31.297,13.496C30.386,15.595 30.386,17.977 31.297,20.076L31.791,21.212C34.776,28.086 27.8,35.062 20.926,32.077L19.79,31.583C17.691,30.672 15.309,30.672 13.21,31.583L12.074,32.077C5.2,35.062 -1.776,28.086 1.209,21.212L1.703,20.076C2.614,17.977 2.614,15.595 1.703,13.496L1.209,12.36C-1.776,5.486 5.2,-1.49 12.074,1.495L13.21,1.989C15.309,2.9 17.691,2.9 19.79,1.989L20.926,1.495Z" />
        </svg>` : ''}
                <img id="icon-24" src="${getMetNorwayIcons(HourWeatherCode)}" class="icon-24">
                <p class="time-24">${hours}${period}</p>
            `;

        rainMeterBarItem.innerHTML = `
            <rainPerBar>
              <rainPerBarProgress style="height: ${Math.round(rainAmountPercent)}%; background-color: ${barColor};">
            </rainPerBarProgress>
            </rainPerBar>
            <p>${PrecAmount}</p>
            <span>${hours}${period}</span>
        `;


        RainBarsContainer.append(rainMeterBarItem);
        forecastContainer.appendChild(forecastItem);
    });


}













// convert met-norway conditions


function converMetNorwayConditions(condition_text) {

    if (condition_text === 'clearsky_day') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()
            displayLeaves()
        }
        return 'clear_sky'

    } else if (condition_text === 'clearsky_night') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeThunder()
            displayStars()
        }
        return 'clear_sky'


    } else if (condition_text === 'fair_night') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            displayClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            displayStars()
            removeThunder()
        }
        return 'mostly_clear'

    } else if (condition_text === 'fair_day' || condition_text === 'fair_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            displayClouds()
            removeRain()
            removeFog()
            removeSnow()
            displayLeaves()
            removeStars()
            removeThunder()
        }
        return 'mostly_clear'

    } else if (condition_text === 'partlycloudy_day' || condition_text === 'partlycloudy_night' || condition_text === 'partlycloudy_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeDrizzle()
            displayClouds()
            removeCloudsFull()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()
        }
        return 'partly_cloudy'

    } else if (condition_text === 'cloudy') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeDrizzle()
            removeClouds()
            displayCloudsFull()
            removeRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()
        }
        return 'overcast'

    } else if (condition_text === 'fog') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            displayFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()
        }
        return 'fog'

    } else if (condition_text === 'lightrain' || condition_text === 'lightsleet') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            displayDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()
        }
        return 'drizzle'

    } else if (condition_text === 'heavysleet' || condition_text === '') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            displayDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()
        }
        return 'freezing_drizzle'

    } else if (condition_text === 'rain') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()
        }
        return 'moderate_rain'

    } else if (condition_text === 'heavyrain' || condition_text === 'lightrainandthunder') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()
        }
        return 'heavy_intensity_rain'

    } else if (condition_text === 'sleet' || condition_text === 'sleetshowersandthunder_day' || condition_text === 'sleetshowersandthunder_night' || condition_text === 'sleetshowersandthunder_polartwilight' || condition_text === 'lightssleetshowersandthunder_day' || condition_text === 'lightssleetshowersandthunder_night' || condition_text === 'lightssleetshowersandthunder_polartwilight' || condition_text === 'lightsleetandthunder' || condition_text === 'heavysleetshowers_day' || condition_text === 'heavysleetshowers_night' || condition_text === 'heavysleetshowers_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()
        }
        return 'freezing_rain'

    } else if (condition_text === 'snow' || condition_text === 'lightsnow' || condition_text === 'lightsnowshowers_day' || condition_text === 'lightsnowshowers_night' || condition_text === 'lightsnowshowers_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()
        }
        return 'slight_snow'

    } else if (condition_text === 'heavysnow' || condition_text === 'lightsnowandthunder') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()
        }
        return 'moderate_snow'

    } else if (condition_text === 'heavysnowandthunder' || condition_text === 'heavysnowshowers_day' || condition_text === 'heavysnowshowers_night' || condition_text === 'heavysnowshowers_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()
        }
        return 'heavy_intensity_snow'

    } else if (condition_text === 'sleetshowers_day' || condition_text === 'sleetshowers_night' || condition_text === 'sleetshowers_polartwilight' || condition_text === 'heavysleetshowersandthunder_day' || condition_text === 'heavysleetshowersandthunder_night' || condition_text === 'heavysleetshowersandthunder_polartwilight' || condition_text === 'heavysleetandthunder' || condition_text === 'lightsleetshowers_day' || condition_text === 'lightsleetshowers_night' || condition_text === 'lightsleetshowers_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()
        }
        return 'snow_grains'

    } else if (condition_text === 'rainshowers_day' || condition_text === 'rainshowers_night' || condition_text === 'rainshowers_polartwilight' || condition_text === 'lightrainshowersandthunder_day' || condition_text === 'lightrainshowersandthunder_night' || condition_text === 'lightrainshowersandthunder_polartwilight' || condition_text === 'lightrainshowers_day' || condition_text === 'lightrainshowers_night' || condition_text === 'lightrainshowers_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()
        }
        return 'rain_showers'

    } else if (condition_text === 'rainshowersandthunder_day' || condition_text === 'rainshowersandthunder_night' || condition_text === 'rainshowersandthunder_polartwilight' || condition_text === 'heavyrainshowersandthunder_day' || condition_text === 'heavyrainshowersandthunder_night' || condition_text === 'heavyrainshowersandthunder_polartwilight' || condition_text === 'heavyrainshowers_day' || condition_text === 'heavyrainshowers_night' || condition_text === 'heavyrainshowers_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()
        }
        return 'heavy_rain_showers'

    } else if (condition_text === 'snowshowers_day' || condition_text === 'snowshowers_night' || condition_text === 'snowshowers_polartwilight' || condition_text === 'lightssnowshowersandthunder_day' || condition_text === 'lightssnowshowersandthunder_night' || condition_text === 'lightssnowshowersandthunder_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()
        }
        return 'slight_snow_showers'

    } else if (condition_text === 'snowandthunder' || condition_text === 'snowshowersandthunder_day' || condition_text === 'snowshowersandthunder_night' || condition_text === 'snowshowersandthunder_polartwilight' || condition_text === 'heavysnowshowersandthunder_day' || condition_text === 'heavysnowshowersandthunder_night' || condition_text === 'heavysnowshowersandthunder_polartwilight') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()
        }
        return 'heavy_snow_showers'

    } else if (condition_text === 'heavyrainandthunder' || condition_text === 'rainandthunder' || condition_text === 'sleetandthunder') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            displayThunder()
        }
        return 'thunderstorm'

    } else if (condition_text === '' || condition_text === '') {
        if (localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false') {
        } else {
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            displayThunder()
        }
        return 'strong_thunderstorm'

    }

    return 'Not found'

}


function getMetNorwayWeatherLabelInLang(condition_text, langCode) {
    const translationKey = converMetNorwayConditions(condition_text);

    const translatedLabel = getTranslationByLang(langCode, translationKey);

    return translatedLabel || 'Unknown weather';
}


function getMetNorwayIcons(data_condition){

    if (data_condition === 'clearsky_day' || data_condition === 'clearsky_polartwilight') {

        return 'weather-icons/clear_day.svg'

    } else if (data_condition === 'clearsky_night') {

        return 'weather-icons/clear_night.svg'

    } else if (data_condition === 'fair_day' || data_condition === 'fair_polartwilight') {

        return 'weather-icons/mostly_clear_day.svg'

    } else if (data_condition === 'fair_night') {

        return 'weather-icons/mostly_clear_night.svg'

    } else if (data_condition === 'partlycloudy_day' || data_condition === 'partlycloudy_polartwilight') {

        return 'weather-icons/partly_cloudy_day.svg'

    } else if (data_condition === 'partlycloudy_night') {

        return 'weather-icons/partly_cloudy_night.svg'

    } else if (data_condition === 'cloudy') {

        return 'weather-icons/cloudy.svg';

    } else if (data_condition === 'fog') {

        return 'weather-icons/haze_fog_dust_smoke.svg'

    } else if (data_condition === 'lightrain' || data_condition === 'lightsleet') {

        return 'weather-icons/drizzle.svg'

    } else if (data_condition === 'heavysleet' || data_condition === '') {

        return 'weather-icons/sleet_hail.svg'

    } else if (data_condition === 'rain') {

        return 'weather-icons/showers_rain.svg'

    } else if (data_condition === 'heavyrain' || data_condition === 'lightrainandthunder') {

        return 'weather-icons/heavy_rain.svg'

    } else if (data_condition === 'sleet' || data_condition === 'sleetshowersandthunder_day' || data_condition === 'sleetshowersandthunder_polartwilight' || data_condition === 'lightssleetshowersandthunder_day' || data_condition === 'lightssleetshowersandthunder_polartwilight' || data_condition === 'lightsleetandthunder' || data_condition === 'heavysleetshowers_day' || data_condition === 'heavysleetshowers_polartwilight') {

        return 'weather-icons/mixed_rain_hail_sleet.svg'

    } else if (data_condition === 'sleetshowersandthunder_night' || data_condition === 'lightssleetshowersandthunder_night' || data_condition === 'heavysleetshowers_night') {

        return 'weather-icons/mixed_rain_hail_sleet.svg'

    } else if (data_condition === 'snow' || data_condition === 'lightsnow' || data_condition === 'lightsnowshowers_day' || data_condition === 'lightsnowshowers_polartwilight') {

        return 'weather-icons/scattered_snow_showers_day.svg'

    } else if (data_condition === 'lightsnowshowers_night') {

        return 'weather-icons/scattered_snow_showers_night.svg'

    } else if (data_condition === 'heavysnow' || data_condition === 'lightsnowandthunder') {

        return 'weather-icons/heavy_snow.svg'

    } else if (data_condition === 'heavysnowandthunder' || data_condition === 'heavysnowshowers_day' || data_condition === 'heavysnowshowers_polartwilight') {

        return 'weather-icons/heavy_snow.svg'

    } else if (data_condition === 'heavysnowshowers_night') {

        return 'weather-icons/heavy_snow.svg'

    } else if (data_condition === 'sleetshowers_day' || data_condition === 'sleetshowers_polartwilight' || data_condition === 'heavysleetshowersandthunder_day' || data_condition === 'heavysleetshowersandthunder_polartwilight' || data_condition === 'heavysleetandthunder' || data_condition === 'lightsleetshowers_day' || data_condition === 'lightsleetshowers_polartwilight') {

        return 'weather-icons/sleet_hail.svg'

    } else if (data_condition === 'sleetshowers_night' || data_condition === 'heavysleetshowersandthunder_night' || data_condition === 'lightsleetshowers_night') {

        return 'weather-icons/sleet_hail.svg'

    } else if (data_condition === 'rainshowers_day' || data_condition === 'rainshowers_polartwilight' || data_condition === 'lightrainshowersandthunder_day' || data_condition === 'lightrainshowersandthunder_polartwilight' || data_condition === 'lightrainshowers_day' || data_condition === 'lightrainshowers_polartwilight') {

        return 'weather-icons/showers_rain.svg'

    } else if (data_condition === 'rainshowers_night' || data_condition === 'lightrainshowersandthunder_night' || data_condition === 'lightrainshowers_night') {

        return 'weather-icons/showers_rain.svg'

    } else if (data_condition === 'rainshowersandthunder_day' || data_condition === 'rainshowersandthunder_polartwilight' || data_condition === 'heavyrainshowersandthunder_day' || data_condition === 'heavyrainshowersandthunder_polartwilight' || data_condition === 'heavyrainshowers_day' || data_condition === 'heavyrainshowers_polartwilight') {

        return 'weather-icons/isolated_thunderstorms.svg'

    } else if (data_condition === 'rainshowersandthunder_night' || data_condition === 'heavyrainshowersandthunder_night' || data_condition === 'heavyrainshowers_night') {

        return 'weather-icons/isolated_thunderstorms.svg'

    } else if (data_condition === 'snowshowers_day' || data_condition === 'snowshowers_polartwilight' || data_condition === 'lightssnowshowersandthunder_day' || data_condition === 'lightssnowshowersandthunder_polartwilight') {

        return 'weather-icons/showers_snow.svg'

    } else if (data_condition === 'snowshowers_night' || data_condition === 'lightssnowshowersandthunder_night') {

        return 'weather-icons/showers_snow.svg'

    } else if (data_condition === 'snowandthunder' || data_condition === 'snowshowersandthunder_day' || data_condition === 'snowshowersandthunder_polartwilight' || data_condition === 'heavysnowshowersandthunder_day' || data_condition === 'heavysnowshowersandthunder_polartwilight') {

        return 'weather-icons/heavy_snow.svg'

    } else if (data_condition === 'snowshowersandthunder_night' || data_condition === 'heavysnowshowersandthunder_night') {

        return 'weather-icons/cloudy_with_snow.svg'

    } else if (data_condition === 'heavyrainandthunder' || data_condition === 'rainandthunder' || data_condition === 'sleetandthunder') {

        return 'weather-icons/strong_thunderstorms.svg'

    } else if (data_condition === '' || data_condition === '') {

        return ''

    }

    return 'weather-icons/not_available.svg'
}

function getMetNorwayTheme(data_condition){

    if (data_condition === 'clearsky_day' || data_condition === 'clearsky_polartwilight') {

        return 'clear-day'

    } else if (data_condition === 'clearsky_night') {

        return 'clear-night'

    } else if (data_condition === 'fair_day' || data_condition === 'fair_polartwilight') {

        return 'cloudy'

    } else if (data_condition === 'fair_night') {

        return 'cloudy'

    } else if (data_condition === 'partlycloudy_day' || data_condition === 'partlycloudy_polartwilight') {

        return 'cloudy'

    } else if (data_condition === 'partlycloudy_night') {

        return 'cloudy'

    } else if (data_condition === 'cloudy') {

        return 'overcast';

    } else if (data_condition === 'fog') {

        return 'fog'

    } else if (data_condition === 'lightrain' || data_condition === 'lightsleet') {

        return 'rain'

    } else if (data_condition === 'heavysleet' || data_condition === '') {

        return 'snow'

    } else if (data_condition === 'rain') {

        return 'rain'

    } else if (data_condition === 'heavyrain' || data_condition === 'lightrainandthunder') {

        return 'rain'

    } else if (data_condition === 'sleet' || data_condition === 'sleetshowersandthunder_day' || data_condition === 'sleetshowersandthunder_polartwilight' || data_condition === 'lightssleetshowersandthunder_day' || data_condition === 'lightssleetshowersandthunder_polartwilight' || data_condition === 'lightsleetandthunder' || data_condition === 'heavysleetshowers_day' || data_condition === 'heavysleetshowers_polartwilight') {

        return 'snow'

    } else if (data_condition === 'sleetshowersandthunder_night' || data_condition === 'lightssleetshowersandthunder_night' || data_condition === 'heavysleetshowers_night') {

        return 'snow'

    } else if (data_condition === 'snow' || data_condition === 'lightsnow' || data_condition === 'lightsnowshowers_day' || data_condition === 'lightsnowshowers_polartwilight') {

        return 'snow'

    } else if (data_condition === 'lightsnowshowers_night') {

        return 'snow'

    } else if (data_condition === 'heavysnow' || data_condition === 'lightsnowandthunder') {

        return 'snow'

    } else if (data_condition === 'heavysnowandthunder' || data_condition === 'heavysnowshowers_day' || data_condition === 'heavysnowshowers_polartwilight') {

        return 'snow'

    } else if (data_condition === 'heavysnowshowers_night') {

        return 'snow'

    } else if (data_condition === 'sleetshowers_day' || data_condition === 'sleetshowers_polartwilight' || data_condition === 'heavysleetshowersandthunder_day' || data_condition === 'heavysleetshowersandthunder_polartwilight' || data_condition === 'heavysleetandthunder' || data_condition === 'lightsleetshowers_day' || data_condition === 'lightsleetshowers_polartwilight') {

        return 'snow'

    } else if (data_condition === 'sleetshowers_night' || data_condition === 'heavysleetshowersandthunder_night' || data_condition === 'lightsleetshowers_night') {

        return 'snow'

    } else if (data_condition === 'rainshowers_day' || data_condition === 'rainshowers_polartwilight' || data_condition === 'lightrainshowersandthunder_day' || data_condition === 'lightrainshowersandthunder_polartwilight' || data_condition === 'lightrainshowers_day' || data_condition === 'lightrainshowers_polartwilight') {

        return 'rain'

    } else if (data_condition === 'rainshowers_night' || data_condition === 'lightrainshowersandthunder_night' || data_condition === 'lightrainshowers_night') {

        return 'rain'

    } else if (data_condition === 'rainshowersandthunder_day' || data_condition === 'rainshowersandthunder_polartwilight' || data_condition === 'heavyrainshowersandthunder_day' || data_condition === 'heavyrainshowersandthunder_polartwilight' || data_condition === 'heavyrainshowers_day' || data_condition === 'heavyrainshowers_polartwilight') {

        return 'rain'

    } else if (data_condition === 'rainshowersandthunder_night' || data_condition === 'heavyrainshowersandthunder_night' || data_condition === 'heavyrainshowers_night') {

        return 'rain'

    } else if (data_condition === 'snowshowers_day' || data_condition === 'snowshowers_polartwilight' || data_condition === 'lightssnowshowersandthunder_day' || data_condition === 'lightssnowshowersandthunder_polartwilight') {

        return 'snow'

    } else if (data_condition === 'snowshowers_night' || data_condition === 'lightssnowshowersandthunder_night') {

        return 'snow'

    } else if (data_condition === 'snowandthunder' || data_condition === 'snowshowersandthunder_day' || data_condition === 'snowshowersandthunder_polartwilight' || data_condition === 'heavysnowshowersandthunder_day' || data_condition === 'heavysnowshowersandthunder_polartwilight') {

        return 'snow'

    } else if (data_condition === 'snowshowersandthunder_night' || data_condition === 'heavysnowshowersandthunder_night') {

        return 'rain'

    } else if (data_condition === 'heavyrainandthunder' || data_condition === 'rainandthunder' || data_condition === 'sleetandthunder') {

        return 'thunder'

    } else if (data_condition === '' || data_condition === '') {

        return ''

    }

    return 'weather-icons/not_available.svg'
}

function getMetNorwayFrogy(data_condition){

    if (data_condition === 'clearsky_day' || data_condition === 'clearsky_polartwilight') {

        return sunnyFrog[Math.floor(Math.random() * sunnyFrog.length)];

    } else if (data_condition === 'clearsky_night') {

        return ClearNightFrog[Math.floor(Math.random() * ClearNightFrog.length)];

    } else if (data_condition === 'fair_day' || data_condition === 'fair_polartwilight') {

        return mostlySunnyFrog[Math.floor(Math.random() * mostlySunnyFrog.length)];

    } else if (data_condition === 'fair_night') {

        return ClearNightFrog[Math.floor(Math.random() * ClearNightFrog.length)];

    } else if (data_condition === 'partlycloudy_day' || data_condition === 'partlycloudy_polartwilight') {

        return PartlyCloudyFrog[Math.floor(Math.random() * PartlyCloudyFrog.length)];

    } else if (data_condition === 'partlycloudy_night') {

        return PartlyCloudyNightFrog[Math.floor(Math.random() * PartlyCloudyNightFrog.length)];

    } else if (data_condition === 'cloudy') {

        return OvercastFrog[Math.floor(Math.random() * OvercastFrog.length)];

    } else if (data_condition === 'fog') {

        return FogFrog[Math.floor(Math.random() * FogFrog.length)];

    } else if (data_condition === 'lightrain' || data_condition === 'lightsleet') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'heavysleet' || data_condition === '') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'rain') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'heavyrain' || data_condition === 'lightrainandthunder') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'sleet' || data_condition === 'sleetshowersandthunder_day' || data_condition === 'sleetshowersandthunder_polartwilight' || data_condition === 'lightssleetshowersandthunder_day' || data_condition === 'lightssleetshowersandthunder_polartwilight' || data_condition === 'lightsleetandthunder' || data_condition === 'heavysleetshowers_day' || data_condition === 'heavysleetshowers_polartwilight') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'sleetshowersandthunder_night' || data_condition === 'lightssleetshowersandthunder_night' || data_condition === 'heavysleetshowers_night') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'snow' || data_condition === 'lightsnow' || data_condition === 'lightsnowshowers_day' || data_condition === 'lightsnowshowers_polartwilight') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'lightsnowshowers_night') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'heavysnow' || data_condition === 'lightsnowandthunder') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'heavysnowandthunder' || data_condition === 'heavysnowshowers_day' || data_condition === 'heavysnowshowers_polartwilight') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'heavysnowshowers_night') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'sleetshowers_day' || data_condition === 'sleetshowers_polartwilight' || data_condition === 'heavysleetshowersandthunder_day' || data_condition === 'heavysleetshowersandthunder_polartwilight' || data_condition === 'heavysleetandthunder' || data_condition === 'lightsleetshowers_day' || data_condition === 'lightsleetshowers_polartwilight') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'sleetshowers_night' || data_condition === 'heavysleetshowersandthunder_night' || data_condition === 'lightsleetshowers_night') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'rainshowers_day' || data_condition === 'rainshowers_polartwilight' || data_condition === 'lightrainshowersandthunder_day' || data_condition === 'lightrainshowersandthunder_polartwilight' || data_condition === 'lightrainshowers_day' || data_condition === 'lightrainshowers_polartwilight') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'rainshowers_night' || data_condition === 'lightrainshowersandthunder_night' || data_condition === 'lightrainshowers_night') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'rainshowersandthunder_day' || data_condition === 'rainshowersandthunder_polartwilight' || data_condition === 'heavyrainshowersandthunder_day' || data_condition === 'heavyrainshowersandthunder_polartwilight' || data_condition === 'heavyrainshowers_day' || data_condition === 'heavyrainshowers_polartwilight') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'rainshowersandthunder_night' || data_condition === 'heavyrainshowersandthunder_night' || data_condition === 'heavyrainshowers_night') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'snowshowers_day' || data_condition === 'snowshowers_polartwilight' || data_condition === 'lightssnowshowersandthunder_day' || data_condition === 'lightssnowshowersandthunder_polartwilight') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'snowshowers_night' || data_condition === 'lightssnowshowersandthunder_night') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'snowandthunder' || data_condition === 'snowshowersandthunder_day' || data_condition === 'snowshowersandthunder_polartwilight' || data_condition === 'heavysnowshowersandthunder_day' || data_condition === 'heavysnowshowersandthunder_polartwilight') {

        return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

    } else if (data_condition === 'snowshowersandthunder_night' || data_condition === 'heavysnowshowersandthunder_night') {

        return RainFrog[Math.floor(Math.random() * RainFrog.length)];

    } else if (data_condition === 'heavyrainandthunder' || data_condition === 'rainandthunder' || data_condition === 'sleetandthunder') {

        return ThunderStormFrog[Math.floor(Math.random() * ThunderStormFrog.length)];

    } else if (data_condition === '' || data_condition === '') {

        return ''

    }

    return 'weather-icons/not_available.svg'
}