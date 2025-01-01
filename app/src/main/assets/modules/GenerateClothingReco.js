function GenerateRecommendation() {
    const currentLocationData = localStorage.getItem('CurrentLocationName');
    const airQualityData = JSON.parse(localStorage.getItem(`AirQuality_${currentLocationData}`));
    const selectedProvider = localStorage.getItem("selectedMainWeatherProvider");

    let weatherData;

    // Simplify weather data retrieval
    const providerMapping = {
        'Met norway': `WeatherDataOpenMeteo_${currentLocationData}`,
        'Accuweather': `WeatherDataOpenMeteo_${currentLocationData}`,
        'meteoFrance': `WeatherDataMeteoFrance_${currentLocationData}`,
        'dwdGermany': `WeatherDataDWDGermany_${currentLocationData}`,
        'noaaUS': `WeatherDataNOAAUS_${currentLocationData}`,
        'ecmwf': `WeatherDataECMWF_${currentLocationData}`,
        'ukMetOffice': `WeatherDataukMetOffice_${currentLocationData}`,
        'jmaJapan': `WeatherDataJMAJapan_${currentLocationData}`,
        'gemCanada': `WeatherDatagemCanada_${currentLocationData}`,
        'bomAustralia': `WeatherDatabomAustralia_${currentLocationData}`,
        'cmaChina': `WeatherDatacmaChina_${currentLocationData}`,
        'knmiNetherlands': `WeatherDataknmiNetherlands_${currentLocationData}`,
        'dmiDenmark': `WeatherDatadmiDenmark_${currentLocationData}`
    };

    weatherData = JSON.parse(localStorage.getItem(providerMapping[selectedProvider] || `WeatherDataOpenMeteo_${currentLocationData}`));

    const locationElement = document.getElementById('location_name');
    locationElement.innerHTML = currentLocationData === 'CurrentDeviceLocation'
        ? getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'current_location')
        : currentLocationData;

    const recommendationsContainer = document.getElementById('recommendations');
    const dayTipContainer = document.getElementById('day_tip');
    dayTipContainer.innerHTML = '';

    const hourlyData = weatherData.hourly;

    // Extract and calculate averages for different time periods
    const timePeriods = {
        Morning: extractTimePeriodData(hourlyData, 6, 9),
        Evening: extractTimePeriodData(hourlyData, 16, 19),
        Night: extractTimePeriodData(hourlyData, 21, 24)
    };

    const averages = Object.fromEntries(
        Object.entries(timePeriods).map(([key, data]) => [key, calculateAverage(data)])
    );

    const aqi = airQualityData ? airQualityData.current.us_aqi : null;

    // Generate recommendations for each period
    for (const [period, avgData] of Object.entries(averages)) {
        document.getElementById(period.toLowerCase()).innerHTML += generateClothingRecommendation(period, avgData, aqi);
    }

    // Air quality details
    const { carbon_monoxide, nitrogen_dioxide, ozone, pm2_5, pm10, sulphur_dioxide } = airQualityData.current || {};

    // Day Tip
    dayTipContainer.innerHTML =
        `<div class="data">
            <p class="label">Day tip</p>
            <div class="data_text">
                <ul>${generateDayTip(averages.Morning, averages.Evening, averages.Night, aqi)}</ul>
            </div>
        </div>`;
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
    const avgUVIndex = data.reduce((acc, item) => acc + item.uv_index, 0) / data.length;
    return { avgTemp, avgWindSpeed, avgHumidity, avgWeatherCode, avgUVIndex };
}

function generateClothingRecommendation(timeOfDay, avgData, aqi) {
    const clothingTips = [];
        const roundedUVIndex = Math.round(avgData.avgUVIndex);



        if (avgData.avgTemp < -40) {
            clothingTips.push('<li>🥶 Extreme cold: In temperatures below -40°C (-40°F), you need high-insulation gear. Wear a heavy-duty down parka with windproof outer layers, a balaclava, thermal base layers, thick wool socks, insulated boots, and a face mask. All exposed skin must be covered to avoid frostbite.</li>');
        } else if (avgData.avgTemp >= -40 && avgData.avgTemp < -30) {
            clothingTips.push('<li>🥶 Extreme cold: Layer with thermal base layers (top and bottom), a down or synthetic coat, thick wool socks, insulated boots, and a wool hat. Make sure to wear insulated gloves and a scarf to cover exposed skin.</li>');
        } else if (avgData.avgTemp >= -30 && avgData.avgTemp < -20) {
            clothingTips.push('<li>🥶 Very cold: A heavy-duty winter jacket is necessary. Wear thermal leggings, thick wool socks, and insulated boots. Insulated gloves and a wool hat will keep you warm. Layering is key in these frigid temperatures.</li>');
        } else if (avgData.avgTemp >= -20 && avgData.avgTemp < -10) {
            clothingTips.push('<li>🧥 Very cold: Layer with a heavy winter coat, sweater, and long underwear. Thermal gloves, insulated boots, and a scarf are essential. A wool hat or beanie will provide extra warmth for your head.</li>');
        } else if (avgData.avgTemp >= -10 && avgData.avgTemp < 0) {
            clothingTips.push('<li>🧥 Cold weather: Wear a thick winter coat or puffer jacket with a sweater underneath. Insulated gloves, wool socks, a scarf, and a hat are necessary for comfort in these temperatures.</li>');
        } else if (avgData.avgTemp >= 0 && avgData.avgTemp < 5) {
            clothingTips.push('<li>🧥 Cold weather: A heavy jacket or coat is recommended. Pair with thermal gloves, a wool scarf, and thick socks. Opt for boots to keep your feet warm and dry.</li>');
        } else if (avgData.avgTemp >= 5 && avgData.avgTemp < 10) {
            clothingTips.push('<li>🧥 Cool weather: Layer with a warm jacket or puffer coat. Consider wearing thermal socks and boots, as well as gloves for extra protection from the chill. A scarf will also add warmth around your neck.</li>');
        } else if (avgData.avgTemp >= 10 && avgData.avgTemp < 15) {
            clothingTips.push('<li>🧳 Mild weather: A light jacket or sweater should suffice. Pair with jeans or trousers, and consider a hat to protect against mild wind or sun exposure.</li>');
        } else if (avgData.avgTemp >= 15 && avgData.avgTemp < 20) {
            clothingTips.push('<li>🧳 Mild weather: Dress in layers with a light jacket or sweater. You can wear long sleeves, but consider switching to short sleeves if it feels warm. A scarf can help if the wind picks up.</li>');
        } else if (avgData.avgTemp >= 20 && avgData.avgTemp < 25) {
            clothingTips.push('<li>🌞 Warm weather: Opt for breathable fabrics like cotton or linen. A short-sleeve shirt, shorts, or a skirt will keep you comfortable. Don’t forget sunglasses and sunscreen to protect against UV rays.</li>');
        } else if (avgData.avgTemp >= 25 && avgData.avgTemp < 30) {
            clothingTips.push('<li>🌞 Hot weather: Wear lightweight, moisture-wicking clothing like cotton or linen. A wide-brimmed hat and sunglasses are essential to shield from the sun. Stay hydrated and wear sunscreen, especially if you’ll be outside for long periods.</li>');
        } else if (avgData.avgTemp >= 30 && avgData.avgTemp < 35) {
            clothingTips.push('<li>🔥 Hot weather: Dress in lightweight, breathable clothing. Lightweight, loose-fitting shirts and shorts or skirts will help you stay cool. Don’t forget to hydrate regularly and apply sunscreen to avoid sunburn.</li>');
        } else if (avgData.avgTemp >= 35) {
            clothingTips.push('<li>🔥 Extremely hot: Wear loose-fitting, moisture-wicking fabrics to stay cool. Avoid dark colors as they absorb heat. A wide-brimmed hat, sunglasses, and plenty of sunscreen are crucial. Stay in shaded areas as much as possible and drink water constantly.</li>');
        }


        // Humidity-based recommendations
        if (avgData.avgHumidity > 80 && avgData.avgTemp > 20) {
            clothingTips.push('<li>💦 High humidity: Wear moisture-wicking clothes and carry a towel to stay dry.</li>');
        } else if (avgData.avgHumidity < 30 && avgData.avgTemp < 10) {
            clothingTips.push('<li>💧 Low humidity: Use a hydrating moisturizer to avoid dry skin.</li>');
        }

        if (avgData.avgWindSpeed > 20) {
            clothingTips.push('<li>🌬️ Strong winds: Wear windproof clothing and secure loose items.</li>');
        }

    // UV index-based recommendations
    if (timeOfDay !== 'Night') {
        if (roundedUVIndex <= 1) {
            clothingTips.push('<li>🕶️ UV is very low. Sunglasses for comfort are sufficient.</li>');
        } else if (roundedUVIndex >= 2 && roundedUVIndex <= 3) {
            clothingTips.push('<li>🧴 UV is low. Wear sunglasses and apply SPF 15-30 sunscreen.</li>');
        } else if (roundedUVIndex >= 4 && roundedUVIndex <= 5) {
            clothingTips.push('<li>🧴 Moderate UV. Use SPF 30 sunscreen, wear sunglasses, and a hat.</li>');
        } else if (roundedUVIndex >= 6 && roundedUVIndex <= 7) {
            clothingTips.push('<li>🧴 High UV. SPF 50+, sunglasses, UV-blocking clothing, and stay in shade when possible.</li>');
        } else if (roundedUVIndex >= 8 && roundedUVIndex <= 10) {
            clothingTips.push('<li>⚠️ Very high UV! Avoid direct sun, use SPF 50+, UV-blocking clothing, and sunglasses.</li>');
        } else {
            clothingTips.push('<li>🚨 Extreme UV! Stay indoors if possible. SPF 50+, sunglasses, and protective clothing are mandatory.</li>');
        }
    }
    // Nighttime recommendations
    if (timeOfDay === 'Night') {
        clothingTips.push('<li>🌙 Opt for reflective clothing or accessories for better visibility in low light.</li>');
    }

    clothingTips.push(getWeatherDescription(avgData.avgWeatherCode));
    clothingTips.push(getAirQualitySuggestion(aqi));

    return `
        <div class="data_${timeOfDay.toLowerCase()} data">
            <p class="label">${timeOfDay}</p>
            <div class="data_text">
                <ul>${clothingTips.join('')}</ul>
            </div>
        </div>`;
}
function getWeatherDescription(weatherCode) {
    switch (weatherCode) {
        case 0:
        case 1:
        case 2:
        case 3:
            return '<li>🌤️ Clear to partly cloudy. Comfortable weather overall.</li>';
        case 45:
        case 48:
            return '<li>🌫️ Foggy conditions. Drive safely and use fog lights.</li>';
        case 51:
        case 53:
        case 55:
            return '<li>🌦️ Light drizzle expected. Carry a waterproof jacket or umbrella.</li>';
        case 61:
        case 63:
        case 65:
            return '<li>🌧️ Rainy weather. A raincoat or umbrella is essential.</li>';
        case 71:
        case 73:
        case 75:
            return '<li>❄️ Snowfall expected. Wear thermal clothing and snow boots.</li>';
        case 95:
        case 96:
        case 99:
            return '<li>⛈️ Thunderstorms likely. Stay indoors if possible.</li>';
        default:
            return '<li>🌍 Weather conditions are normal. Enjoy your day!</li>';
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

    let dayTip = "";

    // Temperature Advice
    if (overallAvgTemp < 10) {
        dayTip += getRandomItem([
            "<li>🥶 Bundle up! Cold weather calls for insulated jackets and accessories like scarves and gloves.</li>",
            "<li>❄️ Expect chilly conditions throughout the day. Layer up with thermals for extra warmth.</li>"
        ]);
    } else if (overallAvgTemp >= 10 && overallAvgTemp < 20) {
        dayTip += getRandomItem([
            "<li>🍂 Mild and pleasant weather. A light jacket will keep you comfortable, especially in the evening.</li>",
            "<li>🌤️ A great day for a stroll or light outdoor activities. Don’t forget to carry a sweater for cooler moments!</li>"
        ]);
    } else {
        dayTip += getRandomItem([
            "<li>☀️ Warm and sunny. Opt for breathable fabrics and stay hydrated throughout the day.</li>",
            "<li>🌞 Enjoy the warmth, but remember to apply sunscreen if you're heading out during peak sunlight hours.</li>"
        ]);
    }

    // Wind Advice
    if (overallAvgWind > 15) {
        dayTip += getRandomItem([
            "<li>💨 Windy conditions today. Secure loose items and wear wind-resistant clothing if you're outside.</li>",
            "<li>🌬️ Gusty winds might make it feel cooler. A lightweight jacket can help protect against the breeze.</li>"
        ]);
    }

    // Humidity Advice
    if (overallAvgHumidity > 75) {
        dayTip += getRandomItem([
            "<li>💦 High humidity could make it feel warmer. Wear moisture-wicking clothing and stay hydrated.</li>",
            "<li>💧 The air is humid—light, breathable fabrics will help you stay comfortable.</li>"
        ]);
    } else if (overallAvgHumidity < 20) {
        dayTip += getRandomItem([
            "<li>🧴 The air is dry. Moisturize your skin and drink plenty of water to stay hydrated.</li>",
            "<li>💧 Low humidity may cause dry skin or irritation. Keep a water bottle handy and avoid overexposure.</li>"
        ]);
    }

    // Air Quality Advice
    dayTip += getAirQualitySuggestion(aqi);

    // Activity Suggestions
    if (overallAvgTemp > 15 && aqi <= 50) {
        dayTip += getRandomItem([
            "<li>🌳 Perfect weather for outdoor activities like jogging or cycling. Head to a park and enjoy the fresh air!</li>",
            "<li>🚶‍♂️ Consider a nature walk or picnic—today's conditions are great for some time outdoors.</li>"
        ]);
    } else if (overallAvgTemp <= 15 || aqi > 100) {
        dayTip += getRandomItem([
            "<li>🏠 It might be a good day to stay indoors and enjoy a warm beverage. Consider a cozy activity like reading.</li>",
            "<li>📺 Plan indoor activities, especially if you’re sensitive to air quality or cold weather.</li>"
        ]);
    }

    // Weather Breakdown

    return dayTip;
}

function generateAirQualityDetail(co, no2, o3, pm25, pm10, so2) {
    const airQualityDetailsContainer = document.getElementById('air_quality_details');
    airQualityDetailsContainer.innerHTML = `
    <div class="data">
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


// tabs


document.getElementById('change_type_tabs').addEventListener('change', () => {
    if (event.target.activeTabIndex === 0) {
        document.getElementById('morning').hidden = false;
        document.getElementById('evening').hidden = true;
        document.getElementById('night').hidden = true;
        document.getElementById('day_tip').hidden = true;
        document.getElementById('air_quality_details').hidden = true;

    } else if (event.target.activeTabIndex === 1){
        document.getElementById('morning').hidden = true;
        document.getElementById('evening').hidden = false;
        document.getElementById('night').hidden = true;
        document.getElementById('day_tip').hidden = true;
        document.getElementById('air_quality_details').hidden = true;

    } else if (event.target.activeTabIndex === 2){
        document.getElementById('morning').hidden = true;
        document.getElementById('evening').hidden = true;
        document.getElementById('night').hidden = false;
        document.getElementById('day_tip').hidden = true;
        document.getElementById('air_quality_details').hidden = true;

    } else if (event.target.activeTabIndex === 3){
        document.getElementById('morning').hidden = true;
        document.getElementById('evening').hidden = true;
        document.getElementById('night').hidden = true;
        document.getElementById('day_tip').hidden = false;
        document.getElementById('air_quality_details').hidden = false;

    }
  });