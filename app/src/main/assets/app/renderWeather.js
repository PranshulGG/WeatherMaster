

const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
const SelectedWindUnit = localStorage.getItem('SelectedWindUnit');
const SelectedVisibiltyUnit = localStorage.getItem('selectedVisibilityUnit');
const SelectedPrecipitationUnit = localStorage.getItem('selectedPrecipitationUnit');
const SelectedPressureUnit = localStorage.getItem('selectedPressureUnit');



function HourlyWeather(data) {
    const forecastContainer = document.getElementById('forecast');
    const RainBarsContainer = document.querySelector('rainMeterBar');

    forecastContainer.innerHTML = '';
    RainBarsContainer.innerHTML = '';
    if (!data || !data.hourly || !data.hourly.time || !data.hourly.weather_code || !data.hourly.temperature_2m) {
        console.error("Hourly forecast data is missing or undefined");
        return;
    }
    const sunriseTime = new Date(data.daily.sunrise[0]);
    const sunsetTime = new Date(data.daily.sunset[0]);



    data.hourly.time.forEach((time, index) => {

        const forecastTime = new Date(time);
        let hours = forecastTime.getHours();
        const period = hours >= 12 ? "PM" : "AM";
        hours = hours % 12 || 12;

        const HourWeatherCode = data.hourly.weather_code[index];

        const rainMeterBarItem = document.createElement('rainMeterBarItem');


        let PrecAmount;

        if (SelectedPrecipitationUnit === 'in') {
            PrecAmount = mmToInches(data.hourly.precipitation[index].toFixed(2)) + ' in';
        } else {
            PrecAmount = data.hourly.precipitation[index].toFixed(1) + ' mm';
        }

        const PrecProb = data.hourly.precipitation_probability[index]

        let icon;

        if (forecastTime >= sunriseTime && forecastTime <= sunsetTime) {
            icon = GetWeatherIconDay(HourWeatherCode);
        } else {
            icon = GetWeatherIconNight(HourWeatherCode);
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


        if (SelectedVisibiltyUnit === 'mile') {
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
            HourTemperature = celsiusToFahrenheit(Math.round(data.hourly.temperature_2m[index]));
            DewPointTemp = celsiusToFahrenheit(Math.round(data.hourly.dew_point_2m[0]))

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
                <p class="time-24">${hours} ${period}</p>
                <p class="disc_sml-24"></p>
                <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>
            `;


        rainMeterBarItem.innerHTML = `
                <rainPerBar>
                  <rainPerBarProgress style="height: ${Math.round(rainAmountPercent)}%; background-color: ${barColor};"">
                </rainPerBarProgress>
                </rainPerBar>
                <p>${PrecAmount}</p>
                 <span>${hours} ${period}</span>


            `

            forecastItem.addEventListener('click', ()=>{
                ShowSnack(`<span style="text-transform: capitalize;">${GetWeatherLabel(HourWeatherCode, 1)}</span>`, 2000, 3, 'none', ' ', 'no-up')

            });


        document.getElementById('dew_percentage').innerHTML = DewPointTemp + '°'

        RainBarsContainer.append(rainMeterBarItem)
        forecastContainer.appendChild(forecastItem);
    });
}





// daily

function DailyWeather(dailyForecast) {

    const forecastContainer = document.getElementById('forecast-5day');
    forecastContainer.innerHTML = '';

    const today = new Date().toISOString().split('T')[0];


    const warmingComments = [
        "Warming expected over the next few days.",
        "Temperatures will rise soon, get ready for some heat!",
        "Looks like it's going to get warmer this week.",
        "Prepare for warmer weather ahead."
    ];

    const coolingComments = [
        "Cooling expected over the next few days.",
        "Temperatures are dropping soon, stay warm!",
        "It's going to get cooler in the coming days.",
        "Expect a chilly breeze over the next days."
    ];

    const stableComments = [
        "Stable temperatures expected in the next few days.",
        "No big temperature changes ahead, steady weather.",
        "Temperatures are holding steady for now.",
        "Expect stable weather without much change."
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
        const dateObj = new Date(time);

        const isToday = time === today;
        const weekday = isToday ? 'Today' : dateObj.toLocaleDateString('en-US', { weekday: 'short' });


        const rainPercentage = dailyForecast.precipitation_probability_max[index];
        const DailyWeatherCode = dailyForecast.weather_code[index];

        let TempMin

        if (SelectedTempUnit === 'fahrenheit') {
            TempMin = celsiusToFahrenheit(Math.round(dailyForecast.temperature_2m_min[index]))
        } else {
            TempMin = Math.round(dailyForecast.temperature_2m_min[index])
        }

        let TempMax

        if (SelectedTempUnit === 'fahrenheit') {
            TempMax = celsiusToFahrenheit(Math.round(dailyForecast.temperature_2m_max[index]))
        } else {
            TempMax = Math.round(dailyForecast.temperature_2m_max[index])
        }


        const forecastItem = document.createElement('div');
        forecastItem.classList.add('forecast-item-forecast');


        forecastItem.innerHTML = `
        <p class="disc-5d">${TempMax}°<span> ${TempMin}°</span></p>

        <img id="icon-5d" src="${GetWeatherIcon(DailyWeatherCode, 1)}" alt="Weather Icon">
        <span class="daily_rain">${rainPercentage}%</span>
        <div class="d5-disc-text">
        <p class="time-5d">${weekday}</p>
        </div>

        `
        const daylightDurationInSeconds = dailyForecast.daylight_duration[0];
        const daylightHours = Math.floor(daylightDurationInSeconds / 3600);
        const daylightMinutes = Math.floor((daylightDurationInSeconds % 3600) / 60);


        document.getElementById('weatherComments').innerHTML = `<md-icon icon-outlined style="font-size: 18px;">hourglass_top</md-icon> ${daylightHours} hrs ${daylightMinutes} mins day length <space></space> <md-icon icon-outlined id="arrow_up_toggle">keyboard_arrow_down</md-icon>`



        let TodaysPrecAmount;

        if (SelectedPrecipitationUnit === 'in') {
            TodaysPrecAmount = mmToInches(dailyForecast.precipitation_sum[0].toFixed(2)) + ' in';
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
        CurrentTemperature = celsiusToFahrenheit(Math.round(data.temperature_2m))
        FeelsLikeTemp = celsiusToFahrenheit(Math.round(data.apparent_temperature))
    } else {
        CurrentTemperature = Math.round(data.temperature_2m)
        FeelsLikeTemp = Math.round(data.apparent_temperature)
    }

        if (CurrentTemperature < 10 && CurrentTemperature >= 0) {
            CurrentTemperature = '0' + CurrentTemperature;
        }


    let CurrentWindGust;

    if (SelectedWindUnit === 'mile') {
        CurrentWindGust = kmhToMph(Math.round(data.wind_gusts_10m)) + ' mph';
    } else {
        CurrentWindGust = Math.round(data.wind_gusts_10m) + ' km/h';
    }

    let CurrentWindSpeed;

    if (SelectedWindUnit === 'mile') {
        CurrentWindSpeed = kmhToMph(Math.round(data.wind_speed_10m)) + ' mph';
    } else {
        CurrentWindSpeed = Math.round(data.wind_speed_10m) + ' km/h';
    }

    let CurrentPressure;
    let pressureMainUnit;

    if (SelectedPressureUnit === 'inHg') {
        CurrentPressure = hPaToInHg(Math.round(data.pressure_msl));
        pressureMainUnit = 'inHg';
    } else if (SelectedPressureUnit === 'mmHg') {
        CurrentPressure = hPaToMmHg(Math.round(data.pressure_msl));
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
    document.getElementById('feels_like_now').innerHTML = 'Feels like ' + FeelsLikeTemp + '°';
    document.getElementById('weather-icon').src = GetWeatherIcon(CurrentWeatherCode, isDay);
    document.getElementById('weather-icon').alt = CurrentWeatherCode
    document.getElementById('description').innerHTML = GetWeatherLabel(CurrentWeatherCode, isDay)
    document.getElementById('wind-speed').innerHTML = CurrentWindSpeed
    document.getElementById('WindGust').innerHTML = CurrentWindGust
    document.getElementById('clouds').innerHTML = CurrentCloudCover + '%'
    document.getElementById('froggie_imgs').src = GetFroggieIcon(CurrentWeatherCode, isDay)
    document.documentElement.setAttribute('iconcodetheme', GetWeatherTheme(CurrentWeatherCode, isDay))
        sendThemeToAndroid(GetWeatherTheme(CurrentWeatherCode, 1))

        document.getElementById('temPDiscCurrentLocation').innerHTML = `${CurrentTemperature}° • <span>${GetWeatherLabel(CurrentWeatherCode, isDay)}</span>`

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
        windspeedType.innerHTML = "Calm";
    } else if (data.wind_speed_10m < 5) {
        windspeedType.innerHTML = "Light air";
    } else if (data.wind_speed_10m < 11) {
        windspeedType.innerHTML = "Light breeze";
    } else if (data.wind_speed_10m < 19) {
        windspeedType.innerHTML = "Gentle breeze";
    } else if (data.wind_speed_10m < 28) {
        windspeedType.innerHTML = "Moderate breeze";
    } else if (data.wind_speed_10m < 38) {
        windspeedType.innerHTML = "Fresh breeze";
    } else if (data.wind_speed_10m < 49) {
        windspeedType.innerHTML = "Strong breeze";
    } else if (data.wind_speed_10m < 61) {
        windspeedType.innerHTML = "High wind";
    } else if (data.wind_speed_10m < 74) {
        windspeedType.innerHTML = "Gale";
    } else if (data.wind_speed_10m < 88) {
        windspeedType.innerHTML = "Strong gale";
    } else if (data.wind_speed_10m < 102) {
        windspeedType.innerHTML = "Storm";
    } else if (data.wind_speed_10m < 117) {
        windspeedType.innerHTML = "Violent storm";
    } else {
        windspeedType.innerHTML = "Hurricane";
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

    function calculateTimeDifference(targetTime) {
        const now = new Date();
        const targetDate = new Date(targetTime); 
        
 
        const diffInMilliseconds = targetDate - now;
    

        return Math.round(diffInMilliseconds / 60000); 
    }
    
    const diffToSunrise = calculateTimeDifference(sunrise); 
    const diffToSunset = calculateTimeDifference(sunset); 
    

    

    document.getElementById('sunrise').innerHTML = convertTo12Hour(sunrise)
    document.getElementById('sunset').innerHTML = convertTo12Hour(sunset)

    

    if (diffToSunrise <= 40 && diffToSunrise >= 0) {
        document.getElementById('sunrise_insight').hidden = false;
        document.getElementById('sunrise_insight').classList.add('insights_item')

    } else{
        document.getElementById('sunrise_insight').hidden = true;
        document.getElementById('sunrise_insight').classList.remove('insights_item')

    }

    if (diffToSunset <= 40 && diffToSunset >= 0) {
        document.getElementById('sunset_insight').hidden = false;
        document.getElementById('sunset_insight').classList.add('insights_item')

        setTimeout(()=>{
            document.querySelector('.insights').scrollLeft = 0
        }, 1500);

    } else{
        document.getElementById('sunset_insight').hidden = true;
        document.getElementById('sunset_insight').classList.remove('insights_item')

    }


    const now = new Date();
    const lastUpdated = new Date(data.time);
    const minutesAgo = Math.floor((now - lastUpdated) / 60000);

document.getElementById('last_updated').innerHTML = ''

setTimeout(()=>{
    if (minutesAgo > 1) {
        document.getElementById('last_updated').innerHTML = `Updated ${minutesAgo} mins ago`;
    } else if (minutesAgo < 1) {
        document.getElementById('last_updated').innerHTML = `Updated, just now`;
    } else {
        document.getElementById('last_updated').innerHTML = `Updated ${minutesAgo} min ago`;
    }
}, 300)




    const calculateDaylightPercentage = (sunrise, sunset) => {
        const now = new Date();
        const sunriseTime = new Date(sunrise);
        const sunsetTime = new Date(sunset);

        if (now.getHours() >= 0 && now.getHours() < 12) {
            return 0;
        }

        const totalDaylight = sunsetTime - sunriseTime;

        const timeSinceSunrise = now - sunriseTime;

        if (timeSinceSunrise < 0) return 0;
        if (timeSinceSunrise > totalDaylight) return 100;

        return (timeSinceSunrise / totalDaylight) * 100;
    };

    const percentageOfDaylight = Math.round(calculateDaylightPercentage(sunrise, sunset));

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



    const recommendation = getClothingRecommendation(temperatureCLoths);

    document.getElementById('cloth_recommended').textContent = recommendation

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



    document.getElementById('aqi-level').textContent = aqiText[aqiCategory].level;
    document.getElementById('detail_air').textContent = aqiText[aqiCategory].message;

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
                document.getElementById('uv-index').innerHTML = 'Minimal risk';
                document.getElementById('uv-index').style = 'background-color: #43b710';
                document.getElementById('uv_img').src = 'uv-images/uv-0.png';
                document.getElementById('detail_uv').innerHTML = Uv_0
            } else if (uvIndex > 1 && uvIndex <= 2) {
                document.getElementById('uv-index').innerHTML = 'Low risk';
                document.getElementById('uv-index').style = 'background-color: #43b710';
                document.getElementById('uv_img').src = 'uv-images/uv-1.png';
                document.getElementById('detail_uv').innerHTML = Uv_1
            } else if (uvIndex > 2 && uvIndex <= 3) {
                document.getElementById('uv-index').innerHTML = 'Low risk';
                document.getElementById('uv-index').style = 'background-color: #43b710';
                document.getElementById('uv_img').src = 'uv-images/uv-2.png';
                document.getElementById('detail_uv').innerHTML = Uv_2
            } else if (uvIndex > 3 && uvIndex <= 4) {
                document.getElementById('uv-index').innerHTML = 'Moderate risk';
                document.getElementById('uv-index').style = 'background-color: #eaaf10';
                document.getElementById('uv_img').src = 'uv-images/uv-3.png';
                document.getElementById('detail_uv').innerHTML = Uv_3
            } else if (uvIndex > 4 && uvIndex <= 5) {
                document.getElementById('uv-index').innerHTML = 'Moderate risk';
                document.getElementById('uv-index').style = 'background-color: #eaaf10';
                document.getElementById('uv_img').src = 'uv-images/uv-4.png';
                document.getElementById('detail_uv').innerHTML = Uv_4
            } else if (uvIndex > 5 && uvIndex <= 6) {
                document.getElementById('uv-index').innerHTML = 'Moderate risk';
                document.getElementById('uv-index').style = 'background-color: #eaaf10';
                document.getElementById('uv_img').src = 'uv-images/uv-5.png';
                document.getElementById('detail_uv').innerHTML = Uv_5
            } else if (uvIndex > 6 && uvIndex <= 7) {
                document.getElementById('uv-index').innerHTML = 'High risk';
                document.getElementById('uv-index').style = 'background-color: #eb8a11';
                document.getElementById('uv_img').src = 'uv-images/uv-6.png';
                document.getElementById('detail_uv').innerHTML = Uv_6
            } else if (uvIndex > 7 && uvIndex <= 8) {
                document.getElementById('uv-index').innerHTML = 'High risk';
                document.getElementById('uv-index').style = 'background-color: #eb8a11';
                document.getElementById('uv_img').src = 'uv-images/uv-7.png';
                document.getElementById('detail_uv').innerHTML = Uv_7
            } else if (uvIndex > 8 && uvIndex <= 9) {
                document.getElementById('uv-index').innerHTML = 'Very high risk';
                document.getElementById('uv-index').style = 'background-color: #e83f0f';
                document.getElementById('uv_img').src = 'uv-images/uv-8.png';
                document.getElementById('detail_uv').innerHTML = Uv_8
            } else if (uvIndex > 9 && uvIndex <= 10) {
                document.getElementById('uv-index').innerHTML = 'Very high risk';
                document.getElementById('uv-index').style = 'background-color: #e83f0f';
                document.getElementById('uv_img').src = 'uv-images/uv-9.png';
                document.getElementById('detail_uv').innerHTML = Uv_9
            } else if (uvIndex > 10 && uvIndex <= 11) {
                document.getElementById('uv-index').innerHTML = 'Very high risk';
                document.getElementById('uv-index').style = 'background-color: #e83f0f';
                document.getElementById('uv_img').src = 'uv-images/uv-10.png';
                document.getElementById('detail_uv').innerHTML = Uv_10
            } else if (uvIndex > 11 && uvIndex <= 12) {
                document.getElementById('uv-index').innerHTML = 'Extreme risk';
                document.getElementById('uv-index').style = 'background-color: #8e3acf';
                document.getElementById('uv_img').src = 'uv-images/uv-11.png';
                document.getElementById('detail_uv').innerHTML = Uv_11
            } else if (uvIndex > 12 && uvIndex <= 13) {
                document.getElementById('uv-index').innerHTML = 'Extreme risk';
                document.getElementById('uv-index').style = 'background-color: #ec0c8b';
                document.getElementById('uv_img').src = 'uv-images/uv-12.png';
                document.getElementById('detail_uv').innerHTML = Uv_12
            } else if (uvIndex > 13) {
                document.getElementById('uv-index').innerHTML = 'Extreme risk';
                document.getElementById('uv-index').style = 'background-color: #550ef9';
                document.getElementById('uv_img').src = 'uv-images/uv-13.png';
                document.getElementById('detail_uv').innerHTML = Uv_13
            }


        })

}


function MoreDetails(latSum, lonSum) {
    fetch(`https://api.weatherapi.com/v1/forecast.json?key=ef2cb48d90984d828a8140518240209&q=${latSum},${lonSum}`)
        .then(response => response.json())
        .then(data => {

            const mainData = data.forecast.forecastday[0].day



            const weatherCondition = mainData.condition.text;
            const precipitation = mainData.totalprecip_in;
            const humidity = mainData.avghumidity;


            let maxTemp

            if (SelectedTempUnit === 'fahrenheit') {
                const maxTemp = mainData.maxtemp_c;
                maxTemp = Math.round(maxTemp * 9 / 5 + 32)
            } else {
                maxTemp = Math.round(mainData.maxtemp_c)
            }

            let minTemp

            if (SelectedTempUnit === 'fahrenheit') {
                const minTemp = mainData.mintemp_c;
                minTemp = Math.round(minTemp * 9 / 5 + 32)
            } else {
                minTemp = Math.round(mainData.mintemp_c)
            }

            let Precipitation;

            if (SelectedPrecipitationUnit === 'in') {
                Precipitation = mainData.totalprecip_in.toFixed(2) + ' inches';
            } else {
                Precipitation = inchesToMm(mainData.totalprecip_in.toFixed(1)) + ' millimetres'
            }


            const weatherSummary = `${weatherCondition} expected for today. 🌡️ The daytime temperature will reach ${maxTemp}°, and it will dip to ${minTemp}° at night. 🌧️ We expect around ${Precipitation} of precipitation, with humidity levels around ${humidity}%.`;

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
            
            if (maxTemp > 29) {
                weatherTips += hotWeatherTips[Math.floor(Math.random() * hotWeatherTips.length)] + " ";
            } else if (maxTemp < 19) {
                weatherTips += chillyWeatherTips[Math.floor(Math.random() * chillyWeatherTips.length)] + " ";
            }
            
            if (precipitation > 0) {
                weatherTips += rainTips[Math.floor(Math.random() * rainTips.length)] + " ";
            }
            
            if (weatherCondition.includes("rain")) {
                weatherTips += "🚗 Be cautious of slippery roads if you're driving. ";
            } else if (weatherCondition.includes("sunny")) {
                weatherTips += sunnyTips[Math.floor(Math.random() * sunnyTips.length)] + " ";
            } else if (weatherCondition.includes("snow")) {
                weatherTips += snowTips[Math.floor(Math.random() * snowTips.length)] + " ";
            }
            

            document.getElementById('summeryDay').innerHTML = `<li>${weatherSummary}</li>`
            document.getElementById('day_tips').innerHTML = `<li>${weatherTips}</li>`



        })




}

function astronomyData(latSum, lonSum) {
    fetch(`https://api.weatherapi.com/v1/astronomy.json?key=ef2cb48d90984d828a8140518240209&q=${latSum},${lonSum}`)
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
            } else if (MoonPhaseName.includes("Waxing Gibbious")) {
                document.querySelector('moonPhaseProgress').style.left = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = ''
            } else if (MoonPhaseName.includes("Full Moon")) {
                document.querySelector('moonPhaseProgress').style.left = moonillumination + '%'
                document.querySelector('moonPhaseProgress').style.borderRadius = ''
            } else if (MoonPhaseName.includes("Waning Gibbious")) {
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

            document.getElementById('moonriseTime').innerHTML = data.astronomy.astro.moonrise
            document.getElementById('moonSetTime').innerHTML = data.astronomy.astro.moonset


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
          document.getElementById('excessiveHeatText').innerHTML = 'Weather alerts' + '<text>Tap to see more...</text>';
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
