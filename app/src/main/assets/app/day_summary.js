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
    document.getElementById('weatherComments').innerHTML = `Looks ${getWeatherLabelInLangNoAnim(data.hourlyData, 1, localStorage.getItem('AppLanguageCode'))} and ${getWeatherLabelInLangNoAnim(data.dailyData, 1, localStorage.getItem('AppLanguageCode'))} through out the day.
        <space></space>
        <md-icon icon-outlined id="arrow_up_toggle">keyboard_arrow_down</md-icon>
    `;
}
