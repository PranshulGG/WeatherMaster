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
//    a summary
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

                    ${GenerateFroggySummaryEng}
                }


            } else{
        descriptions = {
            'Clear sky': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_clear_sky_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_clear_sky_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_clear_sky_3')}`],
            'Mostly clear': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_mostly_clear_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_mostly_clear_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_mostly_clear_3')}`],
            'Partly cloudy': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_partly_cloudy_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_partly_cloudy_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_partly_cloudy_3')}`],
            'Overcast': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_overcast_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_overcast_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_overcast_3')}`],
            'Fog': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_fog_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_fog_2')}`,`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_fog_3')}`],
            'Drizzle': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_drizzle_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_drizzle_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_drizzle_3')}`],
            'Freezing Drizzle': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_freezing_drizzle_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_freezing_drizzle_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_freezing_drizzle_3')}`],
            'Moderate rain': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_moderate_rain_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_moderate_rain_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_moderate_rain_3')}`],
            'Heavy intensity rain': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_intensity_rain_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_intensity_rain_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_intensity_rain_3')}`],
            'Freezing Rain': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_freezing_rain_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_freezing_rain_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_freezing_rain_3')}`],
            'Slight snow': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_slight_snow_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_slight_snow_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_slight_snow_3')}`],
            'Moderate snow': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_moderate_snow_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_moderate_snow_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_moderate_snow_3')}`],
            'Heavy intensity snow': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_intensity_snow_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_intensity_snow_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_intensity_snow_3')}`],
            'Snow grains': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_snow_grains_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_snow_grains_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_snow_grains_3')}`],
            'Rain showers': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_rain_showers_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_rain_showers_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_rain_showers_3')}`],
            'Heavy rain showers': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_rain_showers_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_rain_showers_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_rain_showers_3')}`],
            'Slight snow showers': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_slight_snow_showers_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_slight_snow_showers_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_slight_snow_showers_3')}`],
            'Heavy snow showers': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_snow_showers_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_snow_showers_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_heavy_snow_showers_3')}`],
            'Thunderstorm': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_thunderstorm_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_thunderstorm_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_thunderstorm_3')}`],
            'Strong thunderstorm': [`${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_strong_thunderstorm_1')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_strong_thunderstorm_2')}`, `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_strong_thunderstorm_3')}`],
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
                    ${GenerateFroggyPhraseEng}
        ]
    } else{
     weatherPhrases = [
        `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_1_part_1')} ${hourlyDescription} ${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_1_part_2')} ${dailyDescription} ${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_1_part_3')}`,
        `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_2_part_1')} ${hourlyDescription}${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_2_part_2')} ${dailyDescription} ${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_2_part_3')}`,
        `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_3_part_1')} ${hourlyDescription}${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_3_part_2')} ${dailyDescription} ${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_3_part_3')}`,
        `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_4_part_1')} ${hourlyDescription}${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_4_part_2')} ${dailyDescription}.`,
        `${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_5_part_1')} ${hourlyDescription} ${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_5_part_2')} ${dailyDescription} ${getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'summary_phrase_5_part_3')}`
    ];
    }
    weatherComment = weatherPhrases[Math.floor(Math.random() * weatherPhrases.length)];

    document.getElementById('weatherComments').innerHTML = `
        ${weatherComment}
    `;
}

