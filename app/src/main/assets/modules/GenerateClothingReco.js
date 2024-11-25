function GenerateRecommendation() {
    const currentLocationData = localStorage.getItem('CurrentLocationName');
    const airQualityData = JSON.parse(localStorage.getItem(`AirQuality_${currentLocationData}`));
    const selectedProvider = localStorage.getItem("selectedMainWeatherProvider")

        let weatherData

        if (selectedProvider === 'Met norway' || localStorage.getItem('ApiForAccu') && selectedProvider === 'Accuweather') {
            weatherData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${currentLocationData}`));


       } else if (selectedProvider === 'meteoFrance') {
           weatherData = JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${currentLocationData}`));
       } else if (selectedProvider === 'dwdGermany') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDataDWDGermany_${currentLocationData}`));


       } else if (selectedProvider === 'noaaUS') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${currentLocationData}`));



       } else if (selectedProvider === 'ecmwf') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDataECMWF_${currentLocationData}`));


       } else if (selectedProvider === 'ukMetOffice') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${currentLocationData}`));



       } else if (selectedProvider === 'jmaJapan') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${currentLocationData}`));



       } else if (selectedProvider === 'gemCanada') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${currentLocationData}`));


       } else if (selectedProvider === 'bomAustralia') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${currentLocationData}`));


       } else if (selectedProvider === 'cmaChina') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${currentLocationData}`));



       } else if (selectedProvider === 'knmiNetherlands') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${currentLocationData}`));



       } else if (selectedProvider === 'dmiDenmark') {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${currentLocationData}`));


       } else {
        weatherData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${currentLocationData}`));

    }


    document.getElementById('location_name').innerHTML = currentLocationData


    const recommendationsContainer = document.getElementById('recommendations');
    const dayTipContainer = document.getElementById('day_tip');
    recommendationsContainer.innerHTML = '';
    dayTipContainer.innerHTML = '';

    const hourlyData = weatherData.hourly;

    const morningData = extractTimePeriodData(hourlyData, 6, 9);
    const eveningData = extractTimePeriodData(hourlyData, 16, 19);
    const nightData = extractTimePeriodData(hourlyData, 21, 24);

    const morningAvg = calculateAverage(morningData);
    const eveningAvg = calculateAverage(eveningData);
    const nightAvg = calculateAverage(nightData);

    const aqi = airQualityData ? airQualityData.current.us_aqi : null;

    recommendationsContainer.innerHTML += generateClothingRecommendation('Morning', morningAvg, aqi);
    recommendationsContainer.innerHTML += generateClothingRecommendation('Evening', eveningAvg, aqi);
    recommendationsContainer.innerHTML += generateClothingRecommendation('Night', nightAvg, aqi);

    const aqi_carbon_monoxide = airQualityData.current.carbon_monoxide
    const aqi_nitrogen_dioxide = airQualityData.current.nitrogen_dioxide
    const aqi_ozone = airQualityData.current.ozone
    const aqi_pm2_5 = airQualityData.current.pm2_5
    const aqi_pm10 = airQualityData.current.pm10
    const aqi_sulphur_dioxide = airQualityData.current.sulphur_dioxide



    generateAirQualityDetail(aqi_carbon_monoxide, aqi_nitrogen_dioxide, aqi_ozone, aqi_pm2_5, aqi_pm10, aqi_sulphur_dioxide)


    dayTipContainer.innerHTML = `
    <p class="label">Day tip</p>
    <div>
    ${generateDayTip(morningAvg, eveningAvg, nightAvg, aqi)}</div>
    `


}

function extractTimePeriodData(hourlyData, startHour, endHour) {
    return hourlyData.time
        .map((time, index) => {
            const hour = new Date(time).getHours();
            return (hour >= startHour && hour < endHour) ? index : -1;
        })
        .filter(index => index !== -1)
        .map(index => ({
            apparent_temperature: hourlyData.apparent_temperature[index],
            relative_humidity_2m: hourlyData.relative_humidity_2m[index],
            wind_speed_10m: hourlyData.wind_speed_10m[index],
            weather_code: hourlyData.weather_code[index],
            uv_index: hourlyData.uv_index[index],
            wind_direction_10m: hourlyData.wind_direction_10m[index],
            precipitation_probability: hourlyData.precipitation_probability[index]
        }));
}

function calculateAverage(data) {
    const avgTemp = data.reduce((acc, item) => acc + item.apparent_temperature, 0) / data.length;
    const avgWindSpeed = data.reduce((acc, item) => acc + item.wind_speed_10m, 0) / data.length;
    const avgHumidity = data.reduce((acc, item) => acc + item.relative_humidity_2m, 0) / data.length;
    const avgWeatherCode = data[0] ? data[0].weather_code : null;
    return { avgTemp, avgWindSpeed, avgHumidity, avgWeatherCode };
}

function generateClothingRecommendation(timeOfDay, avgData, aqi) {
    const clothingTips = [];
    let description = `Clothing recommendations for ${timeOfDay}:`;

function getRandomItem(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

if (avgData.avgTemp < 0) {
    clothingTips.push(getRandomItem([
        '<li>🧥 Wear a heavy winter coat or down jacket for extreme cold to keep yourself insulated and protected from the harsh weather.</li>',
        '<li>❄️ Layer up with thermal wear, including insulated gloves, a warm scarf, and a knitted hat to prevent heat loss from extremities.</li>',
        '<li>🧣 A thick wool scarf and insulated boots are essential for freezing temperatures, especially if you plan on spending time outside.</li>'
    ]));
} else if (avgData.avgTemp < 10) {
    clothingTips.push(getRandomItem([
        '<li>🧥 Wear a warm jacket or coat to stay insulated against the cold temperatures. A thicker, padded coat may be necessary for prolonged exposure.</li>',
        '<li>❄️ Consider wearing thermal wear (such as base layers) underneath your clothes for additional warmth, especially if you’ll be outside for extended periods.</li>',
        '<li>🧣 Don’t forget a scarf to keep your neck warm, and perhaps gloves and a hat to prevent heat loss from extremities.</li>'
    ]));
} else if (avgData.avgTemp >= 10 && avgData.avgTemp < 20) {
    clothingTips.push(getRandomItem([
        '<li>🧳 A light jacket or sweater should suffice for the cooler temperatures, but ensure it’s breathable to prevent overheating during the day.</li>',
        '<li>👖 Pair your jacket with comfortable jeans or pants. Consider layering with a long-sleeve shirt if you’re out in the evening when temperatures drop.</li>',
        '<li>🧢 Consider wearing a hat or light beanie to protect from the wind and keep the warmth in during cooler parts of the day.</li>'
    ]));
} else {
    clothingTips.push(getRandomItem([
        '<li>👕 Light clothing such as t-shirts or dresses is ideal for warmer conditions. Choose breathable fabrics like cotton to stay cool.</li>',
        '<li>🩳 Opt for shorts or breathable pants for maximum comfort. Lightweight materials like linen or cotton are great for hot days.</li>',
        '<li>🧴 Remember to apply sunscreen to avoid sunburn, especially if you’re outside for long periods during peak sunlight hours.</li>'
    ]));
}

if (avgData.avgWindSpeed > 20) {
    clothingTips.push(getRandomItem([
        '<li>🌬️ Wear wind-resistant clothing to protect yourself from strong gusts. A windbreaker or a jacket with a windproof layer would be ideal.</li>',
        '<li>💨 Wind can make cold temperatures feel even chillier, so make sure your outer layer is adequate to shield you from the wind chill.</li>'
    ]));
}

if (avgData.avgHumidity > 80) {
    clothingTips.push(getRandomItem([
        '<li>💦 Wear moisture-wicking clothes to keep sweat away from your skin, helping you stay dry and comfortable in humid conditions.</li>',
        '<li>🧴 A light, breathable fabric like moisture-wicking synthetics or merino wool is great for keeping cool and dry in high humidity.</li>',
        '<li>🌞 Keep hydrated and apply sunscreen to protect your skin from UV rays, which can be more intense in humid conditions.</li>'
    ]));
} else if (avgData.avgHumidity < 20) {
    clothingTips.push(getRandomItem([
        '<li>💧 Moisturize your skin regularly to prevent dryness, as low humidity can cause skin to lose moisture quickly.</li>',
        '<li>🧴 Consider using a hydrating face mist throughout the day to refresh your skin in dry conditions.</li>',
        '<li>👟 Wear lightweight, breathable fabrics to prevent overheating and discomfort, and avoid heavy clothing that can trap moisture.</li>'
    ]));
}


    clothingTips.push(getWeatherDescription(avgData.avgWeatherCode));

    clothingTips.push(getAirQualitySuggestion(aqi));

    return `
        <div class="data_${timeOfDay.toLowerCase()} data">
            <p class="label">${timeOfDay}</p>
            <div class="data_text">
                <ul>
                    ${clothingTips.join('')}
                </ul>
            </div>
        </div>
    `;


}

function getWeatherDescription(weatherCode) {
    switch (weatherCode) {
        case 0:
            return '<li>🌞 Clear sky - Perfect for outdoor activities!</li>';
        case 1:
        case 2:
        case 3:
            return '<li>🌤️ Mainly clear or partly cloudy - A light jacket is enough.</li>';
        case 45:
        case 48:
            return '<li>🌫️ Fog or rime fog - Visibility low, wear bright colors!</li>';
        case 51:
        case 53:
        case 55:
            return '<li>🌧️ Drizzle - Light rain, carry an umbrella or wear a raincoat.</li>';
        case 56:
        case 57:
            return '<li>❄️ Freezing drizzle - Wear a waterproof jacket.</li>';
        case 61:
        case 63:
        case 65:
            return '<li>🌧️ Rain - Moderate to heavy rain, wear waterproof clothing!</li>';
        case 66:
        case 67:
            return '<li>❄️ Freezing rain - Wear insulated clothing.</li>';
        case 71:
        case 73:
        case 75:
            return '<li>❄️ Snowfall - Heavy snow, wear a winter coat and gloves.</li>';
        case 77:
            return '<li>❄️ Snow grains - Wear warm clothing.</li>';
        case 80:
        case 81:
        case 82:
            return '<li>🌧️ Rain showers - Carry an umbrella.</li>';
        case 85:
        case 86:
            return '<li>❄️ Snow showers - Wear warm winter clothing.</li>';
        case 95:
            return '<li>⛈️ Thunderstorm - Stay indoors, avoid outdoor activities!</li>';
        case 96:
        case 99:
            return '<li>⛈️ Thunderstorm with hail - Stay indoors, hail is dangerous!</li>';
        default:
            return '<li>🌤️ Weather conditions unclear, dress according to the current temperature.</li>';
    }
}

function getRandomItem(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}
function getAirQualitySuggestion(aqi) {
    if (aqi <= 50) {
        return getRandomItem([
            '<li>👍 Air quality is good today. It\'s safe to be outdoors and breathe easy. 🌿 Perfect day for a walk in the park, jogging, or other outdoor activities!</li>',
            '<li>👌 The air is fresh, so it\'s a great time to enjoy outdoor activities like hiking or biking. Stay active and enjoy the clear skies!</li>',
            '<li>🌞 With great air quality, it\'s the perfect time to engage in outdoor sports or simply relax outside. Go for a jog or picnic!</li>'
        ]);
    } else if (aqi <= 100) {
        return getRandomItem([
            '<li>👌 Air quality is moderate. It\'s okay for most people, but sensitive individuals (children, elderly, or those with respiratory conditions) might want to limit prolonged outdoor exposure. 🌳 Consider taking it easy if you have allergies or asthma.</li>',
            '<li>🌿 Air quality is acceptable for most people, but be cautious if you have respiratory issues. Opt for light outdoor activities like walking or stretching.</li>',
            '<li>🌥️ It\'s a good day for outdoor activities if you\'re generally healthy. However, individuals with asthma or other respiratory concerns should limit time outdoors.</li>'
        ]);
    } else if (aqi <= 150) {
        return getRandomItem([
            '<li>⚠️ Air quality is unhealthy for sensitive groups. Those with respiratory or heart conditions should avoid strenuous outdoor activities. 🏠 If possible, stay indoors or wear a mask if you\'re heading outside.</li>',
            '<li>🚶‍♀️ Sensitive individuals should reduce outdoor exposure. It might be best to stay indoors or use a mask if you need to go outside. Consider limiting outdoor exercise.</li>',
            '<li>💨 If you have a pre-existing health condition, avoid outdoor activities and stay indoors to minimize exposure. Make sure to wear a mask if you must go outside.</li>'
        ]);
    } else if (aqi <= 200) {
        return getRandomItem([
            '<li>🚷 Air quality is unhealthy. Limit outdoor exposure as much as possible. 🏃‍♂️ For outdoor workers or athletes, consider rescheduling activities or using an N95 mask. 🚫 Avoid physical activities outdoors, especially for children and people with health concerns.</li>',
            '<li>⚠️ Due to unhealthy air quality, it\'s recommended to stay indoors. Reschedule outdoor activities or use an N95 mask if you need to go outside.</li>',
            '<li>💨 Limit outdoor exposure and take precautions if you must be outside. People with respiratory or heart conditions should stay indoors and avoid exertion.</li>'
        ]);
    } else {
        return getRandomItem([
            '<li>🚨 Hazardous air quality! It\'s best to stay indoors at all costs. 🌫️ If you must go outside, make sure to wear a high-quality face mask and minimize outdoor exposure. 🛑 People with heart or lung conditions should remain indoors and take necessary precautions.</li>',
            '<li>☠️ The air quality is hazardous, so stay indoors to protect your health. If you need to leave, wear a high-quality mask and limit exposure. People with pre-existing conditions should avoid outdoor activities.</li>',
            '<li>🚷 Extremely hazardous air quality! Stay indoors to avoid serious health risks. If you must go out, wear a respirator mask and minimize time outdoors. People with respiratory issues should not leave the house.</li>'
        ]);
    }
}



function generateDayTip(morningAvg, eveningAvg, nightAvg, aqi) {
    const overallAvgTemp = (morningAvg.avgTemp + eveningAvg.avgTemp + nightAvg.avgTemp) / 3;
    const overallAvgWind = (morningAvg.avgWindSpeed + eveningAvg.avgWindSpeed + nightAvg.avgWindSpeed) / 3;
    const overallAvgHumidity = (morningAvg.avgHumidity + eveningAvg.avgHumidity + nightAvg.avgHumidity) / 3;

    let dayTip = "<p class='highlight'>🌟 Daily Highlights:</p>";

    // Temperature Advice
    if (overallAvgTemp < 10) {
        dayTip += getRandomItem([
            "<p>🥶 Bundle up! Cold weather calls for insulated jackets and accessories like scarves and gloves.</p>",
            "<p>❄️ Expect chilly conditions throughout the day. Layer up with thermals for extra warmth.</p>"
        ]);
    } else if (overallAvgTemp >= 10 && overallAvgTemp < 20) {
        dayTip += getRandomItem([
            "<p>🍂 Mild and pleasant weather. A light jacket will keep you comfortable, especially in the evening.</p>",
            "<p>🌤️ A great day for a stroll or light outdoor activities. Don’t forget to carry a sweater for cooler moments!</p>"
        ]);
    } else {
        dayTip += getRandomItem([
            "<p>☀️ Warm and sunny. Opt for breathable fabrics and stay hydrated throughout the day.</p>",
            "<p>🌞 Enjoy the warmth, but remember to apply sunscreen if you're heading out during peak sunlight hours.</p>"
        ]);
    }

    // Wind Advice
    if (overallAvgWind > 15) {
        dayTip += getRandomItem([
            "<p>💨 Windy conditions today. Secure loose items and wear wind-resistant clothing if you're outside.</p>",
            "<p>🌬️ Gusty winds might make it feel cooler. A lightweight jacket can help protect against the breeze.</p>"
        ]);
    }

    // Humidity Advice
    if (overallAvgHumidity > 75) {
        dayTip += getRandomItem([
            "<p>💦 High humidity could make it feel warmer. Wear moisture-wicking clothing and stay hydrated.</p>",
            "<p>💧 The air is humid—light, breathable fabrics will help you stay comfortable.</p>"
        ]);
    } else if (overallAvgHumidity < 20) {
        dayTip += getRandomItem([
            "<p>🧴 The air is dry. Moisturize your skin and drink plenty of water to stay hydrated.</p>",
            "<p>💧 Low humidity may cause dry skin or irritation. Keep a water bottle handy and avoid overexposure.</p>"
        ]);
    }

    // Air Quality Advice
    dayTip += getAirQualitySuggestion(aqi);

    // Activity Suggestions
    if (overallAvgTemp > 15 && aqi <= 50) {
        dayTip += getRandomItem([
            "<p>🌳 Perfect weather for outdoor activities like jogging or cycling. Head to a park and enjoy the fresh air!</p>",
            "<p>🚶‍♂️ Consider a nature walk or picnic—today's conditions are great for some time outdoors.</p>"
        ]);
    } else if (overallAvgTemp <= 15 || aqi > 100) {
        dayTip += getRandomItem([
            "<p>🏠 It might be a good day to stay indoors and enjoy a warm beverage. Consider a cozy activity like reading.</p>",
            "<p>📺 Plan indoor activities, especially if you’re sensitive to air quality or cold weather.</p>"
        ]);
    }

    // Weather Breakdown

    return dayTip;
}

function generateAirQualityDetail(co, no2, o3, pm25, pm10, so2) {
    const airQualityDetailsContainer = document.getElementById('air_quality_details');
    airQualityDetailsContainer.innerHTML = `
        <p class="label">Air Quality Details</p>
        <div class="data_text">
            <ul>
                <li>💨 <strong>Carbon Monoxide (CO):</strong> ${co} μg/m³ - ${getCODescription(co)}</li>
                <li>🌆 <strong>Nitrogen Dioxide (NO₂):</strong> ${no2} μg/m³ - ${getNO2Description(no2)}</li>
                <li>☀️ <strong>Ozone (O₃):</strong> ${o3} μg/m³ - ${getOzoneDescription(o3)}</li>
                <li>🌀 <strong>PM2.5:</strong> ${pm25} μg/m³ - ${getPM25Description(pm25)}</li>
                <li>🌬️ <strong>PM10:</strong> ${pm10} μg/m³ - ${getPM10Description(pm10)}</li>
                <li>🌋 <strong>Sulphur Dioxide (SO₂):</strong> ${so2} μg/m³ - ${getSO2Description(so2)}</li>
            </ul>
        </div>
    `;
}

function getCODescription(value) {
    if (value <= 5) {
        return "Low risk. Safe levels of carbon monoxide for outdoor activities.";
    } else if (value <= 10) {
        return "Moderate risk. Sensitive groups may experience mild effects.";
    } else {
        return "High risk. Avoid prolonged exposure or heavy outdoor activity.";
    }
}

function getNO2Description(value) {
    if (value <= 40) {
        return "Low risk. Nitrogen dioxide levels are within safe limits.";
    } else if (value <= 100) {
        return "Moderate risk. May irritate sensitive individuals.";
    } else {
        return "High risk. Prolonged exposure can cause respiratory issues.";
    }
}

function getOzoneDescription(value) {
    if (value <= 50) {
        return "Low risk. Ozone levels are healthy for most people.";
    } else if (value <= 100) {
        return "Moderate risk. May cause mild irritation to sensitive groups.";
    } else {
        return "High risk. Limit outdoor activities to avoid breathing issues.";
    }
}

function getPM25Description(value) {
    if (value <= 12) {
        return "Good. Air quality is safe for everyone.";
    } else if (value <= 35) {
        return "Moderate. Sensitive groups may want to limit outdoor exposure.";
    } else {
        return "Unhealthy. Avoid prolonged outdoor activities.";
    }
}

function getPM10Description(value) {
    if (value <= 50) {
        return "Good. PM10 levels are safe for outdoor activities.";
    } else if (value <= 150) {
        return "Moderate. People with respiratory conditions should be cautious.";
    } else {
        return "Unhealthy. Consider staying indoors to avoid health risks.";
    }
}

function getSO2Description(value) {
    if (value <= 20) {
        return "Low risk. Sulphur dioxide levels are well within safe limits.";
    } else if (value <= 80) {
        return "Moderate risk. Sensitive individuals might feel irritation.";
    } else {
        return "High risk. Prolonged exposure can cause respiratory discomfort.";
    }
}

GenerateRecommendation()