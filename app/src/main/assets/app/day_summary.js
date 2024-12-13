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

                    'Clear sky': ["a shiny, froggy day full of sunshine 🐸🌞", "the sky is clear like a froggy pond in summer 🐸🌈", "perfectly sunny with no clouds, just like a frog's favorite weather 🐸☀️"],
                    'Mostly clear': ["mostly sunny with a few clouds for froggies to hop on 🐸☁️", "some sunshine peeking through, perfect for froggy adventures 🐸🌤", "a mix of sunshine and clouds, just like froggies love it 🐸⛅"],
                    'Partly cloudy': ["partly cloudy, just enough clouds to make it froggy and cool 🐸🌥", "frogs love a balance of sun and cloud, just like today 🐸🌤", "some clouds and sunshine, a froggy combo 🐸☁️☀️"],
                    'Overcast': ["cloudy and overcast, a cozy day for frogs 🐸🌫", "the sky is grey like a frog in a puddle 🐸☁️", "overcast with no sunshine, just how some frogs like it 🐸🌥"],
                    'Fog': ["foggy weather, like a frog lost in a misty swamp 🐸🌫", "dense fog that makes it hard to see the lily pads 🐸🌁", "a misty, foggy atmosphere, just like a frog's favorite hiding spot 🐸🌫"],
                    'Drizzle': ["light drizzle, the perfect kind of rain for a froggy splash 🐸💧", "a gentle drizzle, just enough to make the frogs happy 🐸🌧️", "soft, light rain—perfect for frogs hopping around 🐸🌦️"],
                    'Freezing Drizzle': ["icy drizzle that makes froggies hop for cover 🐸❄️", "freezing drizzle, the kind of weather froggies don’t like 🐸🥶", "a touch of freezing drizzle, froggies need to bundle up! 🐸🧊"],
                    'Moderate rain': ["moderate rain showers, a little too wet for some frogs 🐸🌧️", "steady rain, perfect for a froggy swim 🐸🌧️💦", "a nice light to moderate rain—just enough for froggies to splash around 🐸🌧"],
                    'Heavy intensity rain': ["heavy rainfall, even frogs are staying inside 🐸🌧️", "intense rain, time for froggies to huddle together 🐸💧💦", "a downpour that’ll leave the frogs drenched 🐸💦"],
                    'Freezing Rain': ["freezing rain, a froggy nightmare of ice and cold 🐸❄️🧊", "rain turning to ice, froggies better stay warm 🐸🥶", "ice-covered conditions—frogs don’t like this kind of chill 🐸❄️"],
                    'Slight snow': ["light snowfall, froggies hopping through tiny snowflakes 🐸❄️", "gentle snowflakes falling, just a dusting for froggies 🐸☃️", "a little snow, perfect for froggies to hop around in 🐸🌨️"],
                    'Moderate snow': ["moderate snowfall, froggies love a good snowstorm 🐸❄️", "steady snowfall, froggies hopping through the fluff 🐸🌨️", "a nice amount of snow, froggies will enjoy playing in it 🐸❄️"],
                    'Heavy intensity snow': ["heavy snowfall, froggies need to dig in for warmth 🐸🌨️❄️", "a snowstorm, froggies buried in snowflakes 🐸❄️❄️", "intense snowfall, froggies might need a warm pond 🐸❄️❄️"],
                    'Snow grains': ["occasional snow grains, froggies making footprints in the light snow 🐸❄️", "sporadic snow grains, froggies can hardly notice them 🐸❄️", "light snow grains drifting down, just enough for froggies to enjoy 🐸🌨️"],
                    'Rain showers': ["intermittent rain showers, froggies will need to hop in and out 🐸🌧️", "sporadic rain showers, but frogs don’t mind 🐸💧", "on and off rain, froggies love hopping between puddles 🐸🌧️"],
                    'Heavy rain showers': ["heavy rain showers, froggies are staying dry 🐸🌧️💦", "frequent, intense rain showers, the froggies are hopping fast 🐸💧", "lots of rain today, the frogs are loving it... but a little too wet 🐸🌧️🌊"],
                    'Slight snow showers': ["light snow showers, froggies hopping through the flakes 🐸❄️", "occasional snowflakes, froggies are enjoying the cool air 🐸❄️🌨", "intermittent light snow, froggies are catching snowflakes on their tongues 🐸❄️"],
                    'Heavy snow showers': ["heavy snow showers, froggies making big leaps through the snow 🐸❄️❄️", "constant heavy snow, froggies hopping through the winter wonderland 🐸❄️🌨️", "snow showers with strong intensity, froggies building snow forts 🐸❄️🌨️"],
                    'Thunderstorm': ["a thunderstorm, froggies are huddling under a big leaf 🐸⚡", "a storm with lightning, froggies are hiding from the thunder 🐸⚡🌩️", "thunder and lightning, froggies don't like the loud boom 🐸⚡🌩️"],
                    'Strong thunderstorm': ["strong thunderstorms, froggies are taking cover 🐸⚡🌪", "severe thunderstorms, froggies are staying under the big, safe lily pads 🐸⚡🌩️", "intense thunderstorm activity, froggies are in full retreat 🐸⚡🌩️"],

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
        `Froggy says: Expect ${hourlyDescription} in the morning, followed by ${dailyDescription} later in the day. Ribbit! 🐸`,
        `Hop into the day with ${hourlyDescription}, and then get ready for ${dailyDescription} later on. Frogs like variety! 🐸🌞`,
        `It's gonna be ${hourlyDescription} this morning, and then a little bit of ${dailyDescription} to keep things interesting. Ribbit! 🐸🎉`,
        `Frogs love a good weather change! Start with ${hourlyDescription}, and watch out for ${dailyDescription} as the day goes on. 🐸🌤`,
        `Grab your umbrella! Expect ${hourlyDescription} to start, then hop into ${dailyDescription} by the afternoon. Ribbit! 🐸🌧️`,
        `Ribbit! Froggies say: ${hourlyDescription} this morning, with ${dailyDescription} later. Time to hop around! 🐸🌞`,
        `Frogs are jumping in joy because it’s ${hourlyDescription}, and by the afternoon, you’ll have ${dailyDescription}. 🐸💨`,
        `It’s a froggy day out there! Start with ${hourlyDescription}, then get ready for ${dailyDescription}. 🐸☔`,
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

