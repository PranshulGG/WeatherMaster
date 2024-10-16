let combinedData = {
    hourlyData: null,
    dailyData: null
};

function ReportFromhourly(data) {
    combinedData.hourlyData = data;
    
    if (combinedData.hourlyData && combinedData.dailyData) {
        GenerateSummary(combinedData);
    }
}

function ReportFromdaily(data) {
    combinedData.dailyData = data;
    
    if (combinedData.hourlyData && combinedData.dailyData) {
        GenerateSummary(combinedData);
    }
}

function GenerateSummary(data) {
    let hourlyWeather = getWeatherLabelInLangNoAnimText(data.hourlyData,1 , localStorage.getItem('AppLanguageCode'));
    let dailyWeather = getWeatherLabelInLangNoAnim(data.dailyData, 1, localStorage.getItem('AppLanguageCode'));

    let weatherComment = '';

    if (hourlyWeather === dailyWeather) {
        weatherComment = `Looks ${hourlyWeather} throughout the day.`;
    } else {
        weatherComment = `Looks ${hourlyWeather} and ${dailyWeather} throughout the day.`;
    }

    document.getElementById('weatherComments').innerHTML = `
        ${weatherComment}
        <space></space>
        <md-icon icon-outlined id="arrow_up_toggle">keyboard_arrow_down</md-icon>
    `;
}
