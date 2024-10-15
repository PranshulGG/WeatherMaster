

const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
const SelectedWindUnit = localStorage.getItem('SelectedWindUnit');
const SelectedVisibiltyUnit = localStorage.getItem('selectedVisibilityUnit');
const SelectedPrecipitationUnit = localStorage.getItem('selectedPrecipitationUnit');
const SelectedPressureUnit = localStorage.getItem('selectedPressureUnit');
const timeFormat = localStorage.getItem('selectedTimeMode');



function HourlyWeather(data) {
    const forecastContainer = document.getElementById('forecast');
    const RainBarsContainer = document.querySelector('rainMeterBar');

    forecastContainer.innerHTML = '';
    RainBarsContainer.innerHTML = '';
    if (!data || !data.hourly || !data.hourly.time || !data.hourly.weather_code || !data.hourly.temperature_2m) {
        console.error("Hourly forecast data is missing or undefined");
        return;
    }
    const sunriseTimes = data.daily.sunrise.map(time => new Date(time).getTime());
    const sunsetTimes = data.daily.sunset.map(time => new Date(time).getTime());


            const weatherCodeGroups = {
                "0": [0],
                "1-3": [1, 2, 3],
                "45-48": [45, 48],
                "51-55": [51, 53, 55],
                "56-57": [56, 57],
                "61-65": [61, 63, 65],
                "66-67": [66, 67],
                "71-75": [71, 73, 75],
                "77": [77],
                "80-82": [80, 81, 82],
                "85-86": [85, 86],
                "95": [95],
                "96-99": [96, 99]
            };

            let groupCounts = {};

            Object.keys(weatherCodeGroups).forEach(group => {
                groupCounts[group] = 0;
            });



    data.hourly.time.forEach((time, index) => {

        const forecastTime = new Date(time).getTime();

        let hours
        let period

        if (timeFormat === '24 hour') {
            hours = new Date(time).getHours().toString().padStart(2, '0') + ':';
            period = new Date(time).getMinutes().toString().padStart(2, '0');
        } else {
            hours = new Date(time).getHours();
            period = hours >= 12 ? "PM" : "AM";
            hours = hours % 12 || 12;
        }

        const HourWeatherCode = data.hourly.weather_code[index];

        const rainMeterBarItem = document.createElement('rainMeterBarItem');


        let PrecAmount;

        if (SelectedPrecipitationUnit === 'in') {
            PrecAmount = mmToInches(data.hourly.precipitation[index]).toFixed(2) + ' in';
        } else {
            PrecAmount = data.hourly.precipitation[index].toFixed(1) + ' mm';
        }

        const PrecProb = data.hourly.precipitation_probability[index]

        let dayIndex = -1;
        for (let i = 0; i < sunriseTimes.length; i++) {
            if (forecastTime >= sunriseTimes[i] && forecastTime < (sunriseTimes[i + 1] || Infinity)) {
                dayIndex = i;
                break;
            }
        }
        let icon;
        if (dayIndex !== -1 && forecastTime >= sunriseTimes[dayIndex] && forecastTime < sunsetTimes[dayIndex]) {
            icon = GetWeatherIconDay(HourWeatherCode);  // Day icon
        } else {
            icon = GetWeatherIconNight(HourWeatherCode);  // Night icon
        }

        const maxRain = 2;
        const rainAmountPercent = (data.hourly.precipitation[index] / maxRain) * 100;



        let barColor;

        if (data.hourly.precipitation[index] < 0.5) {
            barColor = '#4c8df6'
        } else if (data.hourly.precipitation[index] > 0.5 && data.hourly.precipitation[index] <= 1) {
            barColor = 'orange'
        } else if (data.hourly.precipitation[index] > 1) {
            barColor = 'red'
        }


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



        let HourTemperature;
        let DewPointTemp


        if (SelectedTempUnit === 'fahrenheit') {
            HourTemperature = Math.round(celsiusToFahrenheit(data.hourly.temperature_2m[index]));
            DewPointTemp = Math.round(celsiusToFahrenheit(data.hourly.dew_point_2m[0]))

        } else {
            HourTemperature = Math.round(data.hourly.temperature_2m[index]);
            DewPointTemp = Math.round(data.hourly.dew_point_2m[0])

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
                <img id="icon-24" src="${icon}" class="icon-24">
                <p class="time-24">${hours}${period}</p>
                <p class="disc_sml-24"></p>
                <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>
            `;


        rainMeterBarItem.innerHTML = `
                <rainPerBar>
                  <rainPerBarProgress style="height: ${Math.round(rainAmountPercent)}%; background-color: ${barColor};"">
                </rainPerBarProgress>
                </rainPerBar>
                <p>${PrecAmount}</p>
                 <p>${data.hourly.precipitation_probability[index]}%</p>
                 <span>${hours}${period}</span>


            `

            forecastItem.addEventListener('click', ()=>{
             ShowSnack(`<span style="text-transform: capitalize;">${getWeatherLabelInLangNoAnim(HourWeatherCode, 1,  localStorage.getItem('AppLanguageCode'))}</span>`, 2000, 3, 'none', ' ', 'no-up')


            });


        document.getElementById('dew_percentage').innerHTML = DewPointTemp + '°'

        RainBarsContainer.append(rainMeterBarItem)
        forecastContainer.appendChild(forecastItem);
    });

        const mostFrequentGroup = Object.keys(groupCounts).reduce((a, b) => groupCounts[a] > groupCounts[b] ? a : b);

        const selectedWeatherCode = weatherCodeGroups[mostFrequentGroup][0];


        ReportFromhourly(selectedWeatherCode)
}





// daily

function DailyWeather(dailyForecast) {

    const forecastContainer = document.getElementById('forecast-5day');
    forecastContainer.innerHTML = '';

    const today = new Date().toISOString().split('T')[0];


    const warmingComments = [
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'Warming expected over the next few days.'),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'Temperatures will rise soon, get ready for some heat!'),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), "Looks like it's going to get warmer this week."),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), "Prepare for warmer weather ahead."),
    ];

    const coolingComments = [
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'Cooling expected over the next few days.'),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'Temperatures are dropping soon, stay warm!'),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), "It's going to get cooler in the coming days."),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), "Expect a chilly breeze over the next days."),
    ];

    const stableComments = [
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'Stable temperatures expected in the next few days.'),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'No big temperature changes ahead, steady weather.'),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), "Temperatures are holding steady for now."),
        getTranslationByLang(localStorage.getItem('AppLanguageCode'), "Expect stable weather without much change."),
    ];

    function getRandomComment(commentsArray) {
        const randomIndex = Math.floor(Math.random() * commentsArray.length);
        return commentsArray[randomIndex];
    }

    const trendDaysArray = [2, 3, 6];
    let trendMessage = '';

    for (let trendDays of trendDaysArray) {
        if (dailyForecast.time.length >= trendDays) {
            let tempDifferenceSum = 0;

            for (let i = 0; i < trendDays - 1; i++) {
                const currentDayAvgTemp = (dailyForecast.temperature_2m_max[i] + dailyForecast.temperature_2m_min[i]) / 2;
                const nextDayAvgTemp = (dailyForecast.temperature_2m_max[i + 1] + dailyForecast.temperature_2m_min[i + 1]) / 2;
                tempDifferenceSum += nextDayAvgTemp - currentDayAvgTemp;
            }

            if (tempDifferenceSum > 0) {
                trendMessage = getRandomComment(warmingComments);
                document.getElementById('temp_insight_icon').innerHTML = 'trending_up';
            } else if (tempDifferenceSum < 0) {
                trendMessage = getRandomComment(coolingComments);
                document.getElementById('temp_insight_icon').innerHTML = 'trending_down';
            } else {
                trendMessage = getRandomComment(stableComments);
                document.getElementById('temp_insight_icon').innerHTML = 'thermostat';
            }

            break;
        }
    }

    document.getElementById('temp_insight').innerHTML = trendMessage;


    dailyForecast.time.forEach((time, index) => {
  const [year, month, day] = time.split('-').map(Number);
     const dateObj = new Date(year, month - 1, day);

     const today = new Date();
     const isSameDay = dateObj.getFullYear() === today.getFullYear() &&
                       dateObj.getMonth() === today.getMonth() &&
                       dateObj.getDate() === today.getDate();

     const weekday = isSameDay ? 'today' : dateObj.toLocaleDateString('en-US', { weekday: 'short' }).toLowerCase();
     const weekdayLang = getTranslationByLang(localStorage.getItem('AppLanguageCode'), weekday);


         const send1stDay = dailyForecast.weather_code[0]

         ReportFromdaily(send1stDay)

        const rainPercentage = dailyForecast.precipitation_probability_max[index];
        const DailyWeatherCode = dailyForecast.weather_code[index];

        let TempMin

        if (SelectedTempUnit === 'fahrenheit') {
            TempMin = Math.round(celsiusToFahrenheit(dailyForecast.temperature_2m_min[index]))
        } else {
            TempMin = Math.round(dailyForecast.temperature_2m_min[index])
        }

        let TempMax

        if (SelectedTempUnit === 'fahrenheit') {
            TempMax = Math.round(celsiusToFahrenheit(dailyForecast.temperature_2m_max[index]))
        } else {
            TempMax = Math.round(dailyForecast.temperature_2m_max[index])
        }

                let TempMinCurrent

                if (SelectedTempUnit === 'fahrenheit') {
                    TempMinCurrent = Math.round(celsiusToFahrenheit(dailyForecast.temperature_2m_min[0]))
                } else {
                    TempMinCurrent = Math.round(dailyForecast.temperature_2m_min[0])
                }

                let TempMaxCurrent

                if (SelectedTempUnit === 'fahrenheit') {
                    TempMaxCurrent = Math.round(celsiusToFahrenheit(dailyForecast.temperature_2m_max[0]))
                } else {
                    TempMaxCurrent = Math.round(dailyForecast.temperature_2m_max[0])
                }

                document.getElementById('high_temp').innerHTML = TempMaxCurrent + '°';
                document.getElementById('low_temp').innerHTML = TempMinCurrent + '°';


        const forecastItem = document.createElement('div');
        forecastItem.classList.add('forecast-item-forecast');

                forecastItem.setAttribute('onclick', `clickForecastItem(${index}); sendThemeToAndroid("Open8Forecast");`)


        forecastItem.innerHTML = `
        <p class="disc-5d">${TempMax}°<span> ${TempMin}°</span></p>

        <img id="icon-5d" src="${GetWeatherIcon(DailyWeatherCode, 1)}" alt="Weather Icon">
        <span class="daily_rain">${rainPercentage}%</span>
        <div class="d5-disc-text">
        <p class="time-5d">${weekdayLang}</p>
        </div>
      <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>
        `
        const daylightDurationInSeconds = dailyForecast.daylight_duration[0];
        const daylightHours = Math.floor(daylightDurationInSeconds / 3600);
        const daylightMinutes = Math.floor((daylightDurationInSeconds % 3600) / 60);


                document.getElementById('day_length_text').innerHTML  = `${daylightHours} hrs ${daylightMinutes} mins ${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'day_length')}`




        let TodaysPrecAmount;

        if (SelectedPrecipitationUnit === 'in') {
            TodaysPrecAmount = mmToInches(dailyForecast.precipitation_sum[0]).toFixed(2) + ' in';
        } else {
            TodaysPrecAmount = dailyForecast.precipitation_sum[0].toFixed(1) + ' mm';
        }

        document.getElementById('AmountRainMM').innerHTML = TodaysPrecAmount


        document.getElementById('RainHours').innerHTML = dailyForecast.precipitation_hours[0] + ' hrs'


        forecastContainer.appendChild(forecastItem);
    });



}


// current


function CurrentWeather(data, sunrise, sunset) {
    const CurrentCloudCover = data.cloud_cover;
    const CurrentHumidity = Math.round(data.relative_humidity_2m);
    const CurrentWeatherCode = data.weather_code;
    const CurrentWindDirection = data.wind_direction_10m
    const isDay = data.is_day



    let CurrentTemperature;
    let FeelsLikeTemp;

    if (SelectedTempUnit === 'fahrenheit') {
        CurrentTemperature = Math.round(celsiusToFahrenheit(data.temperature_2m))
        FeelsLikeTemp = Math.round(celsiusToFahrenheit(data.apparent_temperature))
    } else {
        CurrentTemperature = Math.round(data.temperature_2m)
        FeelsLikeTemp = Math.round(data.apparent_temperature)
    }

        if (CurrentTemperature < 10 && CurrentTemperature >= 0) {
            CurrentTemperature = '0' + CurrentTemperature;
        }


    let CurrentWindGust;

    if (SelectedWindUnit === 'mile') {
        CurrentWindGust = Math.round(kmhToMph(data.wind_gusts_10m)) + ' mph';
    } else if (SelectedWindUnit === 'M/s') {
        CurrentWindGust = (data.wind_gusts_10m / 3.6).toFixed(2) + ' m/s';
    } else {
        CurrentWindGust = Math.round(data.wind_gusts_10m) + ' km/h';
    }

    let CurrentWindSpeed;

    if (SelectedWindUnit === 'mile') {
        CurrentWindSpeed = Math.round(kmhToMph(data.wind_speed_10m)) + ' mph';
    } else if (SelectedWindUnit === 'M/s') {
        CurrentWindSpeed = (data.wind_speed_10m / 3.6).toFixed(2) + ' m/s';
    } else {
        CurrentWindSpeed = Math.round(data.wind_speed_10m) + ' km/h';
    }

    let CurrentPressure;
    let pressureMainUnit;

    if (SelectedPressureUnit === 'inHg') {
        CurrentPressure = hPaToInHg(data.pressure_msl).toFixed(2);
        pressureMainUnit = 'inHg';
    } else if (SelectedPressureUnit === 'mmHg') {
        CurrentPressure = hPaToMmHg(data.pressure_msl).toFixed(2);
        pressureMainUnit = 'mmHg';
    } else {
        CurrentPressure = data.pressure_msl;
        pressureMainUnit = 'hPa';
    }

    let CurrentPrecipitation;

    if (SelectedPrecipitationUnit === 'in') {
        CurrentPrecipitation = mmToInches(Math.round(data.precipitation));
    } else {
        CurrentPrecipitation = Math.round(data.precipitation);
    }

    // -------------------------------


    document.getElementById('temp').innerHTML = CurrentTemperature + '°';
    document.getElementById('feels_like_now').innerHTML = `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'feels_like')} ` + FeelsLikeTemp + '°';
    document.getElementById('weather-icon').src = GetWeatherIcon(CurrentWeatherCode, isDay);
    document.getElementById('weather-icon').alt = CurrentWeatherCode
    document.getElementById('description').innerHTML = getWeatherLabelInLang(CurrentWeatherCode, isDay,  localStorage.getItem('AppLanguageCode'));
    document.getElementById('wind-speed').innerHTML = CurrentWindSpeed
    document.getElementById('WindGust').innerHTML = CurrentWindGust
    document.getElementById('clouds').innerHTML = CurrentCloudCover + '%'
    document.getElementById('froggie_imgs').src = GetFroggieIcon(CurrentWeatherCode, isDay)
    document.documentElement.setAttribute('iconcodetheme', GetWeatherTheme(CurrentWeatherCode, isDay))
        sendThemeToAndroid(GetWeatherTheme(CurrentWeatherCode, 1))

        document.getElementById('temPDiscCurrentLocation').innerHTML = `${CurrentTemperature}° • <span>${getWeatherLabelInLang(CurrentWeatherCode, isDay,  localStorage.getItem('AppLanguageCode'))}</span>`

        document.getElementById('currentSearchImg').src = `${GetWeatherIcon(CurrentWeatherCode, isDay)}`;


    document.getElementById('humidity').innerHTML = CurrentHumidity + '%'

    document.getElementById('pressure_text_main').innerHTML = CurrentPressure;
    document.getElementById('pressureMainUnit').innerHTML = pressureMainUnit;

    document.getElementById('windDirectionArrow').style.transform = `rotate(${CurrentWindDirection}deg)`;

    function getWindDirection(degree) {
        if (degree >= 0 && degree <= 22.5 || degree > 337.5 && degree <= 360) return 'N';
        if (degree > 22.5 && degree <= 67.5) return 'NE';
        if (degree > 67.5 && degree <= 112.5) return 'E';
        if (degree > 112.5 && degree <= 157.5) return 'SE';
        if (degree > 157.5 && degree <= 202.5) return 'S';
        if (degree > 202.5 && degree <= 247.5) return 'SW';
        if (degree > 247.5 && degree <= 292.5) return 'W';
        if (degree > 292.5 && degree <= 337.5) return 'NW';
    }

    document.getElementById('directionWind').innerHTML = getWindDirection(CurrentWindDirection)


    const windspeedType = document.getElementById('windtype');

    if (data.wind_speed_10m < 1) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'calm');
    } else if (data.wind_speed_10m < 5) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'light_air');
    } else if (data.wind_speed_10m < 11) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'light_breeze');
    } else if (data.wind_speed_10m < 19) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'gentle_breeze');
    } else if (data.wind_speed_10m < 28) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'moderate_breeze');
    } else if (data.wind_speed_10m < 38) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'fresh_breeze');
    } else if (data.wind_speed_10m < 49) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'strong_breeze');
    } else if (data.wind_speed_10m < 61) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'high_wind');
    } else if (data.wind_speed_10m < 74) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'gale');
    } else if (data.wind_speed_10m < 88) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'strong_gale');
    } else if (data.wind_speed_10m < 102) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'storm');
    } else if (data.wind_speed_10m < 117) {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'violent_storm');
    } else {
        windspeedType.innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'hurricane');
    }


    if (data.pressure_msl < '980') {
        document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.LowPressure
    } else if (data.pressure_msl > '980' && data.pressure_msl <= '1005') {
        document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.LowMedPressure
    } else if (data.pressure_msl > '1005' && data.pressure_msl <= '1020') {
        document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.MediumPressure
    } else if (data.pressure_msl > '1020' && data.pressure_msl <= '1035') {
        document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.HighPressure
    } else if (data.pressure_msl > '1036') {
        document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.VeryHighPressure
    }

    setTimeout(() => {
        if (CurrentHumidity < 30) {
            document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_7;
        } else if (CurrentHumidity >= 30 && CurrentHumidity < 50) {
            document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_30;
        } else if (CurrentHumidity >= 50 && CurrentHumidity < 70) {
            document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_50;
        } else if (CurrentHumidity >= 70 && CurrentHumidity < 90) {
            document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_70;
        } else if (CurrentHumidity >= 90) {
            document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_90;
        } else {
            console.log('Error');
        }
    }, 300);


    const convertTo12Hour = (time) => {
        const date = new Date(time);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true });
    };


    const convertTo24Hour = (time) => {
        const date = new Date(time);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
    };


    function calculateTimeDifference(targetTime) {
        const now = new Date(data.time);
        const targetDate = new Date(targetTime); 
        
 
        const diffInMilliseconds = targetDate - now;
    

        return Math.round(diffInMilliseconds / 60000); 
    }
    
    const diffToSunrise = calculateTimeDifference(sunrise); 
    const diffToSunset = calculateTimeDifference(sunset); 
    

    


    if (timeFormat === '24 hour') {
        document.getElementById('sunrise').innerHTML = convertTo24Hour(sunrise)
        document.getElementById('sunset').innerHTML = convertTo24Hour(sunset)
    } else{
        document.getElementById('sunrise').innerHTML = convertTo12Hour(sunrise)
        document.getElementById('sunset').innerHTML = convertTo12Hour(sunset)
    }


    

    if (diffToSunrise <= 40 && diffToSunrise >= 0) {
        document.getElementById('sunrise_insight').hidden = false;
        document.getElementById('sunrise_insight').classList.add('insights_item')

                document.getElementById('scroll-indicators').innerHTML = ''
                setTimeout(()=>{
                    document.querySelector('.insights').scrollLeft = 0

                createScrollDots()
                }, 1500);

    } else{
        document.getElementById('sunrise_insight').hidden = true;
        document.getElementById('sunrise_insight').classList.remove('insights_item')

    }

    if (diffToSunset <= 40 && diffToSunset >= 0) {
        document.getElementById('sunset_insight').hidden = false;
        document.getElementById('sunset_insight').classList.add('insights_item')

        document.getElementById('scroll-indicators').innerHTML = ''
        setTimeout(()=>{
            document.querySelector('.insights').scrollLeft = 0

        createScrollDots()
        }, 1500);

    } else{
        document.getElementById('sunset_insight').hidden = true;
        document.getElementById('sunset_insight').classList.remove('insights_item')

    }


    const now = new Date(data.time);
    const lastUpdated = new Date(data.time);
    const minutesAgo = Math.floor((now - lastUpdated) / 60000);

document.getElementById('last_updated').innerHTML = ''

setTimeout(()=>{
    if (minutesAgo > 1) {
        document.getElementById('last_updated').innerHTML = `Updated ${minutesAgo} mins ago`;
    } else if (minutesAgo < 1) {
        document.getElementById('last_updated').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'updated_just_now');;
    } else {
        document.getElementById('last_updated').innerHTML = `Updated ${minutesAgo} min ago`;
    }
}, 300)




const calculateDaylightPercentage = (sunrise, sunset, nowTime) => {
    const now = new Date(nowTime);
    const sunriseTime = new Date(sunrise);
    const sunsetTime = new Date(sunset);

    if (now < sunriseTime) return 0;
    if (now > sunsetTime) return 100;

    const totalDaylight = sunsetTime - sunriseTime;

    const timeSinceSunrise = now - sunriseTime;

    return (timeSinceSunrise / totalDaylight) * 100;
};

const percentageOfDaylight = Math.round(calculateDaylightPercentage(sunrise, sunset, data.time));

    if (percentageOfDaylight > 1 && percentageOfDaylight <= 10) {
        moveSun(10)
    } else if (percentageOfDaylight > 10 && percentageOfDaylight <= 20) {
        moveSun(20)
    } else if (percentageOfDaylight > 20 && percentageOfDaylight <= 30) {
        moveSun(30)
    } else if (percentageOfDaylight > 30 && percentageOfDaylight <= 40) {
        moveSun(40)
    } else if (percentageOfDaylight > 40 && percentageOfDaylight <= 50) {
        moveSun(50)
    } else if (percentageOfDaylight > 50 && percentageOfDaylight <= 60) {
        moveSun(60)
    } else if (percentageOfDaylight > 60 && percentageOfDaylight <= 70) {
        moveSun(70)
    } else if (percentageOfDaylight > 70 && percentageOfDaylight <= 80) {
        moveSun(80)
    } else if (percentageOfDaylight > 80 && percentageOfDaylight <= 90) {
        moveSun(90)
    } else if (percentageOfDaylight > 90 && percentageOfDaylight <= 100) {
        moveSun(100)
    }

    document.getElementById('day_value').value = (percentageOfDaylight / 100).toFixed(2) ;

    const temperatureCLoths = Math.round(data.temperature_2m);

    function getClothingRecommendation(temp) {
        if (temp <= 0) {
            return "❄️ Freezing temperatures! Wear a heavy coat, gloves, a hat, and a scarf to stay warm.";
        } else if (temp <= 5) {
            return "🧥 Very cold! Wear a thick coat, a hat, and gloves to keep warm.";
        } else if (temp <= 10) {
            return "🧣 Cold weather. A coat and a sweater will keep you comfortable.";
        } else if (temp <= 15) {
            return "🧥 Cool temperatures. Wear a light jacket and long sleeves.";
        } else if (temp <= 20) {
            return "🌤️ Mild weather. A light jacket or a sweater should be enough.";
        } else if (temp <= 25) {
            return "👕 Warm day. A t-shirt and jeans or pants are suitable.";
        } else if (temp <= 30) {
            return "☀️ Hot! Opt for a t-shirt and light pants or shorts.";
        } else if (temp <= 35) {
            return "🌞 Very hot. Wear light, breathable clothing and stay hydrated.";
        } else if (temp <= 40) {
            return "🔥 Extreme heat! Wear very light clothing, stay hydrated, and avoid direct sun exposure.";
        } else if (temp <= 45) {
            return "⚠️ Dangerously hot! Wear minimal clothing, stay indoors if possible, and drink plenty of water.";
        } else {
            return "🚨 Extreme heat alert! Wear minimal clothing, stay indoors, and drink plenty of water to stay safe.";
        }
    }



    const recommendation = getClothingRecommendation(temperatureCLoths)

    document.getElementById('cloth_recommended').textContent = getTranslationByLang(localStorage.getItem('AppLanguageCode'), recommendation)

}



// air quality


function AirQuaility(data) {

    const aqi = data.current.us_aqi
    // const 


    let aqiCategory;

    if (aqi <= 50) {
        aqiCategory = 1;
    } else if (aqi <= 100) {
        aqiCategory = 2;
    } else if (aqi <= 150) {
        aqiCategory = 3;
    } else if (aqi <= 200) {
        aqiCategory = 4;
    } else {
        aqiCategory = 5;
    }

        document.getElementById('aqi-level-value').innerHTML = aqi

    document.getElementById('pm25_air').innerHTML = Math.round(data.current.pm2_5);
    document.getElementById('pm25_air_color').style.backgroundColor = getColor(Math.round(data.current.pm2_5), 'PM2.5');


    document.getElementById('pm10_air').innerHTML = Math.round(data.current.pm10);
    document.getElementById('pm10_air_color').style.backgroundColor = getColor(Math.round(data.current.pm10), 'PM10');


    document.getElementById('CO_air').innerHTML = Math.round(data.current.carbon_monoxide);
    document.getElementById('CO_air_color').style.backgroundColor = getColor(Math.round(data.current.carbon_monoxide), 'CO');


    document.getElementById('NO2_air').innerHTML = Math.round(data.current.nitrogen_dioxide);
    document.getElementById('NO2_air_color').style.backgroundColor = getColor(Math.round(data.current.nitrogen_dioxide), 'NO2');


    document.getElementById('SO2_air').innerHTML = Math.round(data.current.sulphur_dioxide);
    document.getElementById('SO2_air_color').style.backgroundColor = getColor(Math.round(data.current.sulphur_dioxide), 'SO2');

    document.getElementById('O3_air').innerHTML = Math.round(data.current.ozone);
    document.getElementById('O3_air_color').style.backgroundColor = getColor(Math.round(data.current.ozone), 'O3');



    const aqiData = aqiText[aqiCategory];

    const langCode = localStorage.getItem('AppLanguageCode');

    const levelTranslation = getTranslationByLang(langCode, aqiData.level);
    const messageTranslation = getTranslationByLang(langCode, aqiData.message);

    document.getElementById('aqi-level').textContent = levelTranslation;
    document.getElementById('detail_air').textContent = messageTranslation;


    const backgroundImage = {
        1: 'air-pop-imgs/good.png',
        2: 'air-pop-imgs/fair.png',
        3: 'air-pop-imgs/moderate.png',
        4: 'air-pop-imgs/poor.png',
        5: 'air-pop-imgs/very_poor.png'
    };

    const backgroundColor = {
        1: '#43b710',
        2: '#eaaf10',
        3: '#eb8a11',
        4: '#e83f0f',
        5: '#8e3acf'
    }


    document.getElementById('aqi_img').src = backgroundImage[aqiCategory];
    document.getElementById('aqi-level').style.backgroundColor = backgroundColor[aqiCategory];
}


function getColor(value, type) {
    switch (type) {
        case 'CO':
            if (value <= 4.4) return '#20fc03';
            if (value <= 9.0) return 'yellow';
            if (value <= 15.0) return 'orange';
            if (value <= 30.0) return '#fc606d';
            if (value <= 45.0) return '#9000ff';
            return 'maroon';
        case 'NH3':
            if (value <= 5) return '#20fc03';
            if (value <= 15) return 'yellow';
            if (value <= 25) return 'orange';
            if (value <= 35) return '#fc606d';
            if (value <= 50) return '#9000ff';
            return 'maroon';
        case 'CO':
            if (value <= 500) return '#20fc03';
            if (value <= 1000) return 'yellow';
            if (value <= 1500) return 'orange';
            if (value <= 2000) return '#fc606d';
            if (value <= 5000) return '#9000ff';
            return 'maroon';
        case 'NO2':
            if (value <= 40) return '#20fc03';
            if (value <= 100) return 'yellow';
            if (value <= 200) return 'orange';
            if (value <= 300) return '#fc606d';
            if (value <= 500) return '#9000ff';
            return 'maroon';
        case 'O3':
            if (value <= 100) return '#20fc03';
            if (value <= 180) return 'yellow';
            if (value <= 300) return 'orange';
            if (value <= 400) return '#fc606d';
            if (value <= 500) return '#9000ff';
            return 'maroon';
        case 'PM2.5':
            if (value <= 12) return '#20fc03';
            if (value <= 35) return 'yellow';
            if (value <= 55) return 'orange';
            if (value <= 150) return '#fc606d';
            if (value <= 250) return '#9000ff';
            return 'maroon';
        case 'PM10':
            if (value <= 20) return '#20fc03';
            if (value <= 50) return 'yellow';
            if (value <= 100) return 'orange';
            if (value <= 150) return '#fc606d';
            if (value <= 250) return '#9000ff';
            return 'maroon';
        case 'SO2':
            if (value <= 50) return '#20fc03';
            if (value <= 150) return 'yellow';
            if (value <= 250) return 'orange';
            if (value <= 500) return '#fc606d';
            if (value <= 1000) return '#9000ff';
            return 'maroon';
        default:
            return 'white';
    }
}


// uv index


function UvIndex(latitude, longitude) {
    const url = `https://currentuvindex.com/api/v1/uvi?latitude=${latitude}&longitude=${longitude}`;
    const option = { method: 'GET', headers: { Accept: 'application/json' } };

    fetch(url, option)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const now = data.now;
            const uvIndex = now.uvi;


            if (uvIndex >= 0 && uvIndex <= 1) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'minimal_risk');
                document.getElementById('uv-index').style = 'background-color: #43b710';
                document.getElementById('uv_img').src = 'uv-images/uv-0.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'uv_index_satisfactory')
            } else if (uvIndex > 1 && uvIndex <= 2) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'low_risk');
                document.getElementById('uv-index').style = 'background-color: #43b710';
                document.getElementById('uv_img').src = 'uv-images/uv-1.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'conditions_low_risk')
            } else if (uvIndex > 2 && uvIndex <= 3) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'low_risk');
                document.getElementById('uv-index').style = 'background-color: #43b710';
                document.getElementById('uv_img').src = 'uv-images/uv-2.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'low_exposure_level')
            } else if (uvIndex > 3 && uvIndex <= 4) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'moderate_risk');
                document.getElementById('uv-index').style = 'background-color: #eaaf10';
                document.getElementById('uv_img').src = 'uv-images/uv-3.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'moderate_risk_sun_exposure')
            } else if (uvIndex > 4 && uvIndex <= 5) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'moderate_risk');
                document.getElementById('uv-index').style = 'background-color: #eaaf10';
                document.getElementById('uv_img').src = 'uv-images/uv-4.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'moderate_risk_sun_exposure')
            } else if (uvIndex > 5 && uvIndex <= 6) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'moderate_risk');
                document.getElementById('uv-index').style = 'background-color: #eaaf10';
                document.getElementById('uv_img').src = 'uv-images/uv-5.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'moderate_risk_sun_exposure')
            } else if (uvIndex > 6 && uvIndex <= 7) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'high_risk');
                document.getElementById('uv-index').style = 'background-color: #eb8a11';
                document.getElementById('uv_img').src = 'uv-images/uv-6.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'high_risk_sun_exposure')
            } else if (uvIndex > 7 && uvIndex <= 8) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'high_risk');
                document.getElementById('uv-index').style = 'background-color: #eb8a11';
                document.getElementById('uv_img').src = 'uv-images/uv-7.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'high_risk_sun_exposure')
            } else if (uvIndex > 8 && uvIndex <= 9) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'very_high_risk');
                document.getElementById('uv-index').style = 'background-color: #e83f0f';
                document.getElementById('uv_img').src = 'uv-images/uv-8.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'very_high_risk_sun_exposure')
            } else if (uvIndex > 9 && uvIndex <= 10) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'very_high_risk');
                document.getElementById('uv-index').style = 'background-color: #e83f0f';
                document.getElementById('uv_img').src = 'uv-images/uv-9.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'very_high_risk_sun_exposure')
            } else if (uvIndex > 10 && uvIndex <= 11) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'very_high_risk');
                document.getElementById('uv-index').style = 'background-color: #e83f0f';
                document.getElementById('uv_img').src = 'uv-images/uv-10.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'very_high_risk_sun_exposure')
            } else if (uvIndex > 11 && uvIndex <= 12) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'extreme_risk');
                document.getElementById('uv-index').style = 'background-color: #8e3acf';
                document.getElementById('uv_img').src = 'uv-images/uv-11.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'extreme_risk_sun_exposure')
            } else if (uvIndex > 12 && uvIndex <= 13) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'extreme_risk');
                document.getElementById('uv-index').style = 'background-color: #ec0c8b';
                document.getElementById('uv_img').src = 'uv-images/uv-12.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'extreme_risk_sun_exposure')
            } else if (uvIndex > 13) {
                document.getElementById('uv-index').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'extreme_risk');
                document.getElementById('uv-index').style = 'background-color: #550ef9';
                document.getElementById('uv_img').src = 'uv-images/uv-13.png';
                document.getElementById('detail_uv').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'extreme_risk_sun_exposure')
            }


        })

}


function MoreDetails(latSum, lonSum) {
    fetch(`https://api.weatherapi.com/v1/forecast.json?key=KEY&q=${latSum},${lonSum}`)
        .then(response => response.json())
        .then(data => {

            const mainData = data.forecast.forecastday[0].day



            const weatherCondition = mainData.condition.text;
            const precipitation = mainData.totalprecip_in;
            const humidity = mainData.avghumidity;

            let willRain


            if(mainData.daily_will_it_rain > 0){
             willRain = 'There is a chance of rain today! So, stay prepared just in case!'
            } else{
             willRain = 'No rain is expected today. It’s going to be a delightful day ahead! Enjoy! 😊'
            }

            let maxTemp

            if (SelectedTempUnit === 'fahrenheit') {
                maxTemp = Math.round(mainData.maxtemp_c * 9 / 5 + 32)
            } else {
                maxTemp = Math.round(mainData.maxtemp_c)
            }

            let minTemp

            if (SelectedTempUnit === 'fahrenheit') {
                minTemp = Math.round(mainData.mintemp_c * 9 / 5 + 32)
            } else {
                minTemp = Math.round(mainData.mintemp_c)
            }

                    let precipitationMessage;
        if (mainData.totalprecip_in > 0) {
            precipitationMessage = `Expect around ${Precipitation} of precipitation today 🌧️. Make sure to carry an umbrella! ☔`;
        } else {
            precipitationMessage = `No significant precipitation is expected today, so you can leave the umbrella at home! ☀️😊`;
        }

        let weatherReport = `
         <li style="padding-bottom: 5px;">${willRain}</li>
         <li style="padding-bottom: 5px;">Expect a high of ${maxTemp}° ☀️. As the sun sets 🌅, temperatures will drop to a cozy ${minTemp}°. A lovely evening awaits! 🌙</li>
         <li >${precipitationMessage}</li>


        `;
            let weatherTips = "";

            const hotWeatherTips = [
                "🔥 It's going to be hot today! Stay hydrated and wear light, breathable clothing.",
                "🥵 High temperatures ahead! Drink plenty of water and avoid the sun during peak hours.",
                "☀️ The heat is on! Wear a hat and stay in the shade as much as possible."
            ];
            
            const chillyWeatherTips = [
                "🧥 It's going to be a bit chilly. Consider wearing a jacket if you're heading out.",
                "🥶 Brr! It's cold today, so bundle up before heading out.",
                "🌬️ The temperature is lower today, keep yourself warm with layers."
            ];
            
            const rainTips = [
                "☔ There's a chance of rain. Don't forget to carry an umbrella or raincoat.",
                "🌧️ Wet weather ahead! Be sure to stay dry with waterproof gear.",
                "🌂 Expect rain showers today. Make sure to keep your umbrella handy."
            ];
            
            const sunnyTips = [
                "🌞 Enjoy the sunshine! Remember to apply sunscreen if you're spending time outdoors.",
                "😎 It's sunny out there! Perfect day for outdoor activities, but don't forget your sunglasses.",
                "☀️ Bright and sunny! Keep hydrated and protect yourself from the sun."
            ];
            
            const snowTips = [
                "❄️ Snow is expected, so dress warmly and drive carefully.",
                "🌨️ Heavy snow is on its way! Prepare for slippery roads and reduced visibility.",
                "⛄ Snowy day ahead! A great time for winter fun, but stay safe on the roads."
            ];

            const cloudyWeatherTips = [
                "☁️ It's a cloudy day. Might be a good idea to carry a light jacket just in case.",
                "🌥️ Overcast skies today. Perfect weather for a cozy indoor day or a walk in the park.",
                "🌫️ Cloudy conditions ahead. Visibility might be reduced, so drive carefully."
            ];

            const fogTips = [
                "🌫️ Foggy conditions ahead. Reduce your speed and use low-beam headlights when driving.",
                "👁️‍🗨️ Visibility will be reduced due to fog. Be cautious on the road.",
                "🚶‍♂️ Foggy weather today. If you're walking or biking, wear bright, reflective clothing."
            ];

            const windTips = [
                "💨 It's going to be windy today. Secure loose objects outdoors and be cautious when driving.",
                "🌬️ Strong winds ahead! Hold onto your hat and be aware of flying debris.",
                "🌀 Windy day ahead. If you're outdoors, take care of gusty conditions, especially near tall structures."
            ];

            const thunderstormTips = [
                "⛈️ Thunderstorms expected. Stay indoors and avoid being near tall objects or open fields.",
                "⚡ There's a risk of thunderstorms. Unplug sensitive electronics to avoid damage from lightning.",
                "🌩️ Stormy weather today! Avoid driving during heavy rain and stay safe indoors."
            ];
            
            if (maxTemp > 29) {
                weatherTips += hotWeatherTips[Math.floor(Math.random() * hotWeatherTips.length)] + " ";
            } else if (maxTemp < 19) {
                weatherTips += chillyWeatherTips[Math.floor(Math.random() * chillyWeatherTips.length)] + " ";
            }
            
            if (precipitation > 0) {
                weatherTips += rainTips[Math.floor(Math.random() * rainTips.length)] + " ";
            }
            
            if (weatherCondition.toLowerCase().includes("rain")) {
                weatherTips += "🚗 Be cautious of slippery roads if you're driving. ";
            } else if (weatherCondition.toLowerCase().includes("sunny")) {
                weatherTips += sunnyTips[Math.floor(Math.random() * sunnyTips.length)] + " ";
            } else if (weatherCondition.toLowerCase().includes("snow")) {
                weatherTips += snowTips[Math.floor(Math.random() * snowTips.length)] + " ";
            } else if (weatherCondition.toLowerCase().includes("cloudy") || weatherCondition.toLowerCase().includes("cloud") || weatherCondition.toLowerCase().includes("overcast")) {
                weatherTips += cloudyWeatherTips[Math.floor(Math.random() * cloudyWeatherTips.length)] + " ";
            } else if (weatherCondition.toLowerCase().includes("fog")) {
                weatherTips += fogTips[Math.floor(Math.random() * fogTips.length)] + " ";
            } else if (weatherCondition.toLowerCase().includes("wind")) {
                weatherTips += windTips[Math.floor(Math.random() * windTips.length)] + " ";
            } else if (weatherCondition.toLowerCase().includes("thunder")) {
                weatherTips += thunderstormTips[Math.floor(Math.random() * thunderstormTips.length)] + " ";
            }

            document.getElementById('day_tips').innerHTML = `<li>${weatherTips}</li>`
            document.getElementById('summeryDay').innerHTML = `<li>${weatherReport}</li>`



        })




}

function astronomyData(latSum, lonSum) {
    fetch(`https://api.weatherapi.com/v1/astronomy.json?key=KEY&q=${latSum},${lonSum}`)
        .then(response => response.json())
        .then(data => {


            const MoonPhaseName = data.astronomy.astro.moon_phase
            const moonillumination = Math.round(data.astronomy.astro.moon_illumination)

            if (MoonPhaseName.includes("New Moon")) {
                document.querySelector('moonPhaseProgress').style.right = moonillumination + '%'
            } else if (MoonPhaseName.includes("Waxing Crescent")) {
                document.querySelector('moonPhaseProgress').style.right = moonillumination + '%'
            } else if (MoonPhaseName.includes("First Quarter")) {
                document.querySelector('moonPhaseProgress').style.right = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = '0%'
            } else if (MoonPhaseName.includes("Waxing Gibbous")) {
                document.querySelector('moonPhaseProgress').style.right = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = ''
            } else if (MoonPhaseName.includes("Full Moon")) {
                document.querySelector('moonPhaseProgress').style.left = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = ''
            } else if (MoonPhaseName.includes("Waning Gibbous")) {
                document.querySelector('moonPhaseProgress').style.left = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = ''
            } else if (MoonPhaseName.includes("Last Quarter")) {
                document.querySelector('moonPhaseProgress').style.left = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = '0%'
            } else if (MoonPhaseName.includes("Waning Crescent")) {
                document.querySelector('moonPhaseProgress').style.left = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = ''
            }

            document.getElementById('moonIlli').innerHTML = moonillumination +'%'


            document.getElementById('moonPhase_name').innerHTML = MoonPhaseName

            function convertTo24Hour(time) {
                const [timePart, modifier] = time.split(' ');
                let [hours, minutes] = timePart.split(':');

                if (hours === '12') {
                    hours = '00';
                }

                if (modifier === 'PM') {
                    hours = parseInt(hours, 10) + 12;
                }

                return `${hours}:${minutes}`;
            }



            if (timeFormat === '24 hour') {
                document.getElementById('moonriseTime').innerHTML = convertTo24Hour(data.astronomy.astro.moonrise);
                document.getElementById('moonSetTime').innerHTML = convertTo24Hour(data.astronomy.astro.moonset);
            } else{
                document.getElementById('moonriseTime').innerHTML = data.astronomy.astro.moonrise;
                document.getElementById('moonSetTime').innerHTML = data.astronomy.astro.moonset;
            }


        })

}


let currentApiKeyAlertsIndex = 0;

function FetchAlert(lat, lon){


const apiKeyAlerts = apiKeysAlerts[currentApiKeyAlertsIndex];
const apiUrlAlerts = `https://api.openweathermap.org/data/2.5/onecall?lat=${lat}&lon=${lon}&exclude=current,minutely,hourly,daily&appid=${apiKeyAlerts}`;

fetch(apiUrlAlerts)
  .then(response => {
      if (!response.ok) {
          throw new Error('Network response was not ok');
      }
      return response.json();
  })
  .then(data => {
      if (data.alerts) {
          console.log(data.alerts);
          const alertEvent = data.alerts[0].event;

          document.querySelector('.weatherCommentsDiv').classList.add('alertOpened');
          document.querySelector('.excessiveHeat').hidden = false;
      localStorage.setItem('AlertCache', JSON.stringify(data.alerts));

      } else {
          console.log('No alerts');
          document.querySelector('.weatherCommentsDiv').classList.remove('alertOpened');
          document.querySelector('.excessiveHeat').hidden = true;
      localStorage.removeItem('AlertCache', JSON.stringify(data.alerts));

      }
  })
  .catch(error => {
      console.error('Error fetching weather alerts:', error);
      if (currentApiKeyAlertsIndex < apiKeysAlerts.length - 1) {
          currentApiKeyAlertsIndex++;
          console.log(`Switching to API key index ${currentApiKeyAlertsIndex} for alerts`);
          FetchAlert(lat, lon);
      } else {
          console.error('All alert API keys failed. Unable to fetch data.');
      }
  });
}

function clickForecastItem(index){
    localStorage.setItem('ClickedForecastItem', index)
}

        const AppLanguageCodeValue = localStorage.getItem('AppLanguageCode');
        if (AppLanguageCodeValue) {
            applyTranslations(AppLanguageCodeValue);

        }

        // ---------

        function getOpenWeatherMainTemp(){

        const mainTempProviderGet = localStorage.getItem('CustomApiKey')
        const mainTempProviderValid = localStorage.getItem('ApiKeyValid')
        const mainTempProvider = localStorage.getItem('MainTempProvider');

        setTimeout(()=>{
            if(mainTempProviderValid && mainTempProviderValid === 'Yes'){
            if(mainTempProvider && mainTempProvider === 'OpenWeatherMap'){
                fetch(`https://api.openweathermap.org/data/2.5/weather?lat=${localStorage.getItem('currentLat')}&lon=${localStorage.getItem('currentLong')}&units=metric&appid=${mainTempProviderGet}`)
                    .then(response => response.json())
                    .then(data => {
                        let CurrentTemperature;

                        if (SelectedTempUnit === 'fahrenheit') {
                            CurrentTemperature = Math.round(celsiusToFahrenheit(data.main.temp))
                        } else {
                            CurrentTemperature = Math.round(data.main.temp)
                        }

                        if (CurrentTemperature < 10 && CurrentTemperature >= 0) {
                            CurrentTemperature = '0' + CurrentTemperature;
                        }

                        document.getElementById('temp').innerHTML = CurrentTemperature + '°';

                    })
            }
        }
        }, 300);
        }