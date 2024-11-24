let combinedData = {
    hourlyData: null,
    dailyData: null
};

function ReportFromhourly(data) {
    combinedData.hourlyData = data;
    
        GenerateSummary(combinedData);
}

function ReportFromdaily(data) {
    combinedData.dailyData = data;
    
        GenerateSummary(combinedData);
}


function GenerateSummary(data) {
    let hourlyWeather = getWeatherLabelInLangNoAnimText(data.hourlyData, 1, 'en');
    let dailyWeather = getWeatherLabelInLangNoAnim(data.dailyData, 1, 'en');

    console.log(hourlyWeather, dailyWeather)

    let weatherComment = '';

    function describeWeather(condition) {
        const descriptions = {
            'Clear sky': "a bright and sunny day",
            'Mostly clear': "mostly sunny with a few clouds",
            'Partly cloudy': "partly cloudy skies",
            'Overcast': "cloudy and overcast conditions",
            'Fog': "foggy weather",
            'Drizzle': "light drizzle",
            'Freezing Drizzle': "freezing drizzle",
            'Moderate rain': "moderate rain showers",
            'Heavy intensity rain': "heavy rainfall",
            'Freezing Rain': "freezing rain",
            'Slight snow': "light snowfall",
            'Moderate snow': "moderate snowfall",
            'Heavy intensity snow': "heavy snowfall",
            'Snow grains': "occasional snow grains",
            'Rain showers': "intermittent rain showers",
            'Heavy rain showers': "heavy rain showers",
            'Slight snow showers': "light snow showers",
            'Heavy snow showers': "heavy snow showers",
            'Thunderstorm': "a thunderstorm",
            'Strong thunderstorm': "strong thunderstorms",
        };

        return descriptions[condition] || "unpredictable weather";
    }

    let hourlyDescription = describeWeather(hourlyWeather);
    let dailyDescription = describeWeather(dailyWeather);

    if (hourlyWeather === dailyWeather) {
        weatherComment = `The weather looks consistent with ${hourlyDescription} expected throughout the day.`;
    } else {
        weatherComment = `You can look forward to ${hourlyDescription} in the morning, with ${dailyDescription} later on.`;
    }

    document.getElementById('weatherComments').innerHTML = `
        ${weatherComment}
        <space></space>
        <md-icon icon-outlined id="arrow_up_toggle">keyboard_arrow_down</md-icon>
    `;
}

