function GenerateRecommendation() {
    const currentLocationData = localStorage.getItem('CurrentLocationName');
    const airQualityData = JSON.parse(localStorage.getItem(`AirQuality_${currentLocationData}`));
    const selectedProvider = localStorage.getItem("selectedMainWeatherProvider");

    let weatherData;

    // Simplify weather data retrieval
    const providerMapping = {
        'Met norway': `WeatherDataMetNorway_${currentLocationData}`,
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
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_1")}</li>`);
        } else if (avgData.avgTemp >= -40 && avgData.avgTemp < -30) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_2")}</li>`);
        } else if (avgData.avgTemp >= -30 && avgData.avgTemp < -20) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_3")}</li>`);
        } else if (avgData.avgTemp >= -20 && avgData.avgTemp < -10) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_4")}</li>`);
        } else if (avgData.avgTemp >= -10 && avgData.avgTemp < 0) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_5")}</li>`);
        } else if (avgData.avgTemp >= 0 && avgData.avgTemp < 5) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_6")}</li>`);
        } else if (avgData.avgTemp >= 5 && avgData.avgTemp < 10) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_7")}</li>`);
        } else if (avgData.avgTemp >= 10 && avgData.avgTemp < 15) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_8")}</li>`);
        } else if (avgData.avgTemp >= 15 && avgData.avgTemp < 20) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_9")}</li>`);
        } else if (avgData.avgTemp >= 20 && avgData.avgTemp < 25) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_10")}</li>`);
        } else if (avgData.avgTemp >= 25 && avgData.avgTemp < 30) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_11")}</li>`);
        } else if (avgData.avgTemp >= 30 && avgData.avgTemp < 35) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_12")}</li>`);
        } else if (avgData.avgTemp >= 35) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_temp_13")}</li>`);
        }

        // Humidity-based recommendations
        if (avgData.avgHumidity > 80 && avgData.avgTemp > 20) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_humidity_1")}</li>`);
        } else if (avgData.avgHumidity < 30 && avgData.avgTemp < 10) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_humidity_2")}</li>`);
        }

        if (avgData.avgWindSpeed > 20) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_wind_1")}</li>`);
        }

    // UV index-based recommendations
    if (timeOfDay !== 'Night') {
        if (roundedUVIndex <= 1) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_uvIndex_1")}</li>`);
        } else if (roundedUVIndex >= 2 && roundedUVIndex <= 3) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_uvIndex_2")}</li>`);
        } else if (roundedUVIndex >= 4 && roundedUVIndex <= 5) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_uvIndex_3")}</li>`);
        } else if (roundedUVIndex >= 6 && roundedUVIndex <= 7) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_uvIndex_4")}</li>`);
        } else if (roundedUVIndex >= 8 && roundedUVIndex <= 10) {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_uvIndex_5")}</li>`);
        } else {
            clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_uvIndex_6")}</li>`);
        }
    }
    // Nighttime recommendations
    if (timeOfDay === 'Night') {
        clothingTips.push(`<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_uvIndex_7")}</li>`);
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
            return `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_weatherDescription_1")}</li>`;
        case 45:
        case 48:
            return `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_weatherDescription_2")}</li>`;
        case 51:
        case 53:
        case 55:
            return `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_weatherDescription_3")}</li>`;
        case 61:
        case 63:
        case 65:
            return `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_weatherDescription_4")}</li>`;
        case 71:
        case 73:
        case 75:
            return `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_weatherDescription_5")}</li>`;
        case 95:
        case 96:
        case 99:
            return `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_weatherDescription_6")}</li>`;
        default:
            return `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_weatherDescription_7")}</li>`;
    }
}

function getRandomItem(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}
function getAirQualitySuggestion(aqi) {
    if (aqi <= 50) {
        return getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_1")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_2")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_3")}</li>`
        ]);
    } else if (aqi <= 100) {
        return getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_4")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_5")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_6")}</li>`
        ]);
    } else if (aqi <= 150) {
        return getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_7")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_8")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_9")}</li>`
        ]);
    } else if (aqi <= 200) {
        return getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_10")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_11")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_12")}</li>`
        ]);
    } else {
        return getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_13")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_14")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_airQualityDescription_15")}</li>`
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
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_temp_1")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_temp_2")}</li>`
        ]);
    } else if (overallAvgTemp >= 10 && overallAvgTemp < 20) {
        dayTip += getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_temp_3")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_temp_4")}</li>`
        ]);
    } else {
        dayTip += getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_temp_5")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_temp_6")}</li>`
        ]);
    }

    // Wind Advice
    if (overallAvgWind > 15) {
        dayTip += getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_wind_1")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_wind_2")}</li>`
        ]);
    }


    // Humidity Advice
    if (overallAvgHumidity > 75) {
        dayTip += getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_humidity_1")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_humidity_2")}</li>`
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
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_aqi_1")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_aqi_2")}</li>`
        ]);
    } else if (overallAvgTemp <= 15 || aqi > 100) {
        dayTip += getRandomItem([
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_aqi_3")}</li>`,
            `<li>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "activity_suggestions_daytip_aqi_4")}</li>`
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