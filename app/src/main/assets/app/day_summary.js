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
            let descriptions

            if(localStorage.getItem('UseFrogSummary') === 'true'){
                descriptions = {

                         ${loaddescriptions}

                }


            } else{
        descriptions = {
                         ${loaddescriptions}
        };
}
        let randomDescriptions = descriptions[condition] || ["unpredictable weather"];
        return randomDescriptions[Math.floor(Math.random() * randomDescriptions.length)];
    }

    let hourlyDescription = describeWeather(hourlyWeather);
    let dailyDescription = describeWeather(dailyWeather);

    let weatherPhrases

    if(localStorage.getItem('UseFrogSummary') === 'true'){
        weatherPhrases = [
                ${loadPhrases}
        ]
    } else{
     weatherPhrases = [
                ${loadPhrases}
    ];
    }
    weatherComment = weatherPhrases[Math.floor(Math.random() * weatherPhrases.length)];

    document.getElementById('weatherComments').innerHTML = `
        ${weatherComment}
    `;
}

