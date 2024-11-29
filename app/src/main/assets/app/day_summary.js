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

    console.log(hourlyWeather, dailyWeather);

    let weatherComment = '';

    function describeWeather(condition) {
        const descriptions = {
            'Clear sky': ["a bright and sunny day", "clear and beautiful skies", "perfectly sunny weather"],
            'Mostly clear': ["mostly sunny with a few clouds", "sunshine with some clouds", "a mix of sun and clouds"],
            'Partly cloudy': ["partly cloudy skies", "some clouds floating across the sky", "a nice balance of clouds and sun"],
            'Overcast': ["cloudy and overcast conditions", "an overcast sky with no sunlight", "gray and cloudy weather"],
            'Fog': ["foggy weather", "dense fog reducing visibility", "a misty, foggy atmosphere"],
            'Drizzle': ["light drizzle", "gentle drizzle", "a soft, light rain"],
            'Freezing Drizzle': ["freezing drizzle", "icy drizzle", "freezing rain that might accumulate"],
            'Moderate rain': ["moderate rain showers", "steady rain throughout the day", "light to moderate rain"],
            'Heavy intensity rain': ["heavy rainfall", "intense rain showers", "a downpour with heavy rain"],
            'Freezing Rain': ["freezing rain", "rain turning to ice", "ice-covered conditions"],
            'Slight snow': ["light snowfall", "gentle snowflakes falling", "a light dusting of snow"],
            'Moderate snow': ["moderate snowfall", "steady snowfall", "a good amount of snow coming down"],
            'Heavy intensity snow': ["heavy snowfall", "a snowstorm in full swing", "intense snowfall that might accumulate quickly"],
            'Snow grains': ["occasional snow grains", "sporadic snow grains", "light snow grains drifting down"],
            'Rain showers': ["intermittent rain showers", "sporadic rain showers", "on and off rain throughout the day"],
            'Heavy rain showers': ["heavy rain showers", "frequent and intense rain showers", "a lot of rain throughout the day"],
            'Slight snow showers': ["light snow showers", "occasional snowflakes", "intermittent light snow"],
            'Heavy snow showers': ["heavy snow showers", "constant heavy snow", "snow showers with strong intensity"],
            'Thunderstorm': ["a thunderstorm", "a storm with lightning", "thunder and lightning throughout the day"],
            'Strong thunderstorm': ["strong thunderstorms", "severe thunderstorms", "intense thunderstorm activity"],
        };

        let randomDescriptions = descriptions[condition] || ["unpredictable weather"];
        return randomDescriptions[Math.floor(Math.random() * randomDescriptions.length)];
    }

    let hourlyDescription = describeWeather(hourlyWeather);
    let dailyDescription = describeWeather(dailyWeather);

    const weatherPhrases = [
        `Expect ${hourlyDescription} in the morning, followed by ${dailyDescription} later in the day.`,
        `In the morning, you'll experience ${hourlyDescription}, with ${dailyDescription} taking over as the day progresses.`,
        `The morning will bring ${hourlyDescription}, and then you can expect ${dailyDescription} as the day unfolds.`,
        `Start your day with ${hourlyDescription}, and then later on, look forward to ${dailyDescription}.`,
        `The weather will begin with ${hourlyDescription} in the morning, transitioning to ${dailyDescription} by afternoon.`
    ];

    weatherComment = weatherPhrases[Math.floor(Math.random() * weatherPhrases.length)];

    document.getElementById('weatherComments').innerHTML = `
        ${weatherComment}
        <space></space>
        <md-icon icon-outlined id="arrow_up_toggle">keyboard_arrow_down</md-icon>
    `;
}

