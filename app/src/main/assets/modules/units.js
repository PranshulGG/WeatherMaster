function celsiusToFahrenheit(celsius) {
    return (celsius * 9 / 5) + 32;
}

function kmhToMph(kmh) {
    return kmh * 0.621371;
}

function kmToMiles(km) {
    return km * 0.621371;
}

function mmToInches(mm) {
    return mm * 0.0393701;
}

function hPaToInHg(hPa) {
    return hPa * 0.02953;
}

function hPaToMmHg(hPa) {
    return hPa * 0.750062;
}

function inchesToMm(inches) {
    return inches * 25.4;
}



// Weather Icons

function GetWeatherIcon(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {
            return 'weather-icons/clear_day.svg';
        } else if (iconCode === 1) {
            return 'weather-icons/mostly_clear_day.svg';
        } else if (iconCode === 2) {
            return 'weather-icons/partly_cloudy_day.svg';

        } else if (iconCode === 3) {
            return 'weather-icons/cloudy.svg';

        } else if (iconCode === 45 || iconCode === 48) {
            return 'weather-icons/haze_fog_dust_smoke.svg';

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'weather-icons/drizzle.svg';

        } else if (iconCode === 56 || iconCode === 57) {

            return 'weather-icons/mixed_rain_hail_sleet.svg';

        } else if (iconCode === 61 || iconCode === 63) {

            return 'weather-icons/showers_rain.svg';

        } else if (iconCode === 65) {

            return 'weather-icons/heavy_rain.svg';

        } else if (iconCode === 66 || iconCode === 67) {

            return 'weather-icons/sleet_hail.svg';

        } else if (iconCode === 71) {

            return 'weather-icons/scattered_snow_showers_day.svg';

        } else if (iconCode === 73) {

            return 'weather-icons/showers_snow.svg';

        } else if (iconCode === 75) {

            return 'weather-icons/heavy_snow.svg';

        } else if (iconCode === 77) {

            return 'weather-icons/flurries.svg';

        } else if (iconCode === 80 || iconCode === 81) {

            return 'weather-icons/showers_rain.svg';

        } else if (iconCode === 82) {

            return 'weather-icons/heavy_rain.svg';

        } else if (iconCode === 85) {

            return 'weather-icons/showers_snow.svg';

        } else if (iconCode === 86) {

            return 'weather-icons/heavy_snow.svg';

        } else if (iconCode === 95) {

            return 'weather-icons/isolated_thunderstorms.svg'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'weather-icons/strong_thunderstorms.svg'

        }
    } else {
        if (iconCode === 0) {

            return 'weather-icons/clear_night.svg';

        } else if (iconCode === 1) {

            return 'weather-icons/mostly_clear_night.svg';

        } else if (iconCode === 2) {

            return 'weather-icons/partly_cloudy_night.svg';

        } else if (iconCode === 3) {

            return 'weather-icons/cloudy.svg';

        } else if (iconCode === 45 || iconCode === 48) {

            return 'weather-icons/haze_fog_dust_smoke.svg';

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'weather-icons/drizzle.svg';

        } else if (iconCode === 56 || iconCode === 57) {

            return 'weather-icons/mixed_rain_hail_sleet.svg';

        } else if (iconCode === 61 || iconCode === 63) {

            return 'weather-icons/showers_rain.svg';

        } else if (iconCode === 65) {

            return 'weather-icons/heavy_rain.svg';

        } else if (iconCode === 66 || iconCode === 67) {

            return 'weather-icons/sleet_hail.svg';

        } else if (iconCode === 71) {

            return 'weather-icons/scattered_snow_showers_night.svg';

        } else if (iconCode === 73) {

            return 'weather-icons/showers_snow.svg';

        } else if (iconCode === 75) {

            return 'weather-icons/heavy_snow.svg';

        } else if (iconCode === 77) {

            return 'weather-icons/flurries.svg';

        } else if (iconCode === 80 || iconCode === 81) {

            return 'weather-icons/showers_rain.svg';

        } else if (iconCode === 82) {

            return 'weather-icons/heavy_rain.svg';

        } else if (iconCode === 85) {

            return 'weather-icons/showers_snow.svg';

        } else if (iconCode === 86) {

            return 'weather-icons/heavy_snow.svg';

        } else if (iconCode === 95) {

            return 'weather-icons/isolated_thunderstorms.svg'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'weather-icons/strong_thunderstorms.svg'

        }
    }




    return iconCode
}


// Weather label


function GetWeatherLabel(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()
            displayLeaves()
            }
            return 'clear_sky'

        } else if (iconCode === 1) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            displayClouds()
            removeRain()
            removeFog()
            removeSnow()
            displayLeaves()
            removeStars()
            removeThunder()}
            return 'mostly_clear'

        } else if (iconCode === 2) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeDrizzle()
            displayClouds()
            removeCloudsFull()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'partly_cloudy'

        } else if (iconCode === 3) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeDrizzle()
            removeClouds()
            displayCloudsFull()
            removeRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            displayFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            displayDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'drizzle'

        } else if (iconCode === 56 || iconCode === 57) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            displayDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'freezing_drizzle'

        } else if (iconCode === 61 || iconCode === 63) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'moderate_rain'

        } else if (iconCode === 65) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'heavy_intensity_rain'

        } else if (iconCode === 66 || iconCode === 67) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'freezing_rain'

        } else if (iconCode === 71) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'slight_snow'

        } else if (iconCode === 73) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'moderate_snow'

        } else if (iconCode === 75) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'heavy_intensity_snow'

        } else if (iconCode === 77) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'snow_grains'

        } else if (iconCode === 80 || iconCode === 81) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'rain_showers'

        } else if (iconCode === 82) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'heavy_rain_showers'

        } else if (iconCode === 85) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'slight_snow_showers'

        } else if (iconCode === 86) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            displaySnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'heavy_snow_showers'

        } else if (iconCode === 95) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            displayThunder()}
            return 'thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            displayThunder()}
            return 'strong_thunderstorm'

        }
    } else {
        if (iconCode === 0) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeThunder()
            displayStars()}
            return 'clear_sky'

        } else if (iconCode === 1) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
           removeCloudsFull()
            removeDrizzle()
            displayClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            displayStars()
            removeThunder()}
            return 'mostly_clear'

        } else if (iconCode === 2) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeDrizzle()
            displayClouds()
            removeCloudsFull()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'partly_cloudy'

        } else if (iconCode === 3) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeDrizzle()
            removeClouds()
            displayCloudsFull()
            removeRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeLeaves()
            removeRain()
            displayFog()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            displayDrizzle()
            removeClouds()
            removeLeaves()
            removeRain()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'drizzle'

        } else if (iconCode === 56 || iconCode === 57) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            displayDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'freezing_drizzle'

        } else if (iconCode === 61 || iconCode === 63) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'moderate_rain'

        } else if (iconCode === 65) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeLeaves()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'heavy_intensity_rain'

        } else if (iconCode === 66 || iconCode === 67) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeLeaves()
            displayRain()
            removeFog()
            removeSnow()
            removeStars()
            removeThunder()}
            return 'freezing_rain'

        } else if (iconCode === 71) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeLeaves()
            removeFog()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'slight_snow'

        } else if (iconCode === 73) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeLeaves()
            removeFog()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'moderate_snow'

        } else if (iconCode === 75) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            displaySnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'heavy_intensity_snow'

        } else if (iconCode === 77) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            displaySnow()
            removeStars()
            removeLeaves()
            removeThunder()}
            return 'snow_grains'

        } else if (iconCode === 80 || iconCode === 81) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'rain_showers'

        } else if (iconCode === 82) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeSnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'heavy_rain_showers'

        } else if (iconCode === 85) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            displaySnow()
            removeLeaves()
            removeStars()
            removeThunder()}
            return 'slight_snow_showers'

        } else if (iconCode === 86) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            removeRain()
            removeFog()
            removeLeaves()
            displaySnow()
            removeStars()
            removeThunder()}
            return 'heavy_snow_showers'

        } else if (iconCode === 95) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            displayThunder()}
            return 'thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {
            if(localStorage.getItem('UseBackgroundAnimations') && localStorage.getItem('UseBackgroundAnimations') === 'false'){
            }else{
            removeCloudsFull()
            removeDrizzle()
            removeClouds()
            displayRain()
            removeFog()
            removeLeaves()
            removeSnow()
            removeStars()
            displayThunder()}
            return 'strong_thunderstorm'

        }
    }




    return ''
}

function getWeatherLabelInLang(iconCode, isDay, langCode) {
    const translationKey = GetWeatherLabel(iconCode, isDay);

    const translatedLabel = getTranslationByLang(langCode, translationKey);

    return translatedLabel || 'Unknown weather';
}


// froggie


let mostlySunnyFrog = [];
let PartlyCloudyFrog = [];
let OvercastFrog = [];
let FogFrog = [];
let RainFrog = [];
let SnowFrog = [];
let ThunderStormFrog = [];
let ClearNightFrog = [];
let PartlyCloudyNightFrog = [];

if (navigator.onLine) {

    sunnyFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-creek-swimming.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-field-kite.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-orchard-picking.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/01-sunny/01-sunny-home-laundry_f.png?ref_type=heads"
    ];

    mostlySunnyFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-citypark-picnic.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-beach-sunscreen.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-home-laundry.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-beach-sandcastle.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/02-mostly-sunny/02-mostly-sunny-rooftop-pinacolada.png?ref_type=heads"
    ];

    PartlyCloudyFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-field-hiking_f.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-home-flowers.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-creek-feet.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-field-biking_c.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/03-partly-cloudy-day/03-partly-cloudy-day-citypark-ukelele.png?ref_type=heads"
    ];

    OvercastFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/09-cloudy/09-cloudy-home-flowers.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/09-cloudy/09-cloudy-orchard-watching.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/04-mostly-cloudy-day/04-mostly-cloudy-day-home-flowers.png?ref_type=heads"
    ];

    FogFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-bridge.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-fruit-stand.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-mountain.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-pier.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/26-haze-fog-dust-smoke/26-haze-fog-dust-smoke-field-lantern.png?ref_type=heads"
    ];

    RainFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/11-rain/11-rain-creek-leaf.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/11-rain/11-rain-home-inside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/04-mostly-cloudy-day/04-mostly-cloudy-day-orchard-treeswing_f.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/12-heavy-rain/12-heavy-rain-busstop-umbrella.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/12-heavy-rain/12-heavy-rain-cafe-sitting-singing.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/11-rain/11-shower-rain-field-leaf.png?ref_type=heads"
    ];

    SnowFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/15-snow-showers-snow/15-snow-showers-snow-citypark-snowman.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/15-snow-showers-snow/15-snow-showers-snow-home-shoveling.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/15-snow-showers-snow/15-snow-showers-snow-creek-iceskating.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/17-heavy-snow-blizzard/17-heavy-snow-blizzard-home-inside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/16-blowing-snow/16-blowing-snow-field-snowman.png?ref_type=heads"
    ];

    ThunderStormFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/22-iso-thunderstorms/22-iso-thunderstorms-home-inside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/22-iso-thunderstorms/22-iso-thunderstorms-cafe-looking-outside.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/22-iso-thunderstorms/22-iso-thunderstorms-busstop-newspaper.png?ref_type=heads"
    ];

    ClearNightFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-creek-stars.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-field-lanterns.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-orchard-fireflies.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-home-lounging.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/05-clear/05-clear-hills-telescope.png?ref_type=heads"
    ];

    PartlyCloudyNightFrog = [
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-creek-fireflies.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-field-fireflies.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-hills-smores.png?ref_type=heads",
        "https://gitlab.com/bignutty/google-weather-icons/-/raw/main/froggie/v2/mobile/07-partly-cloudy-night/07-partly-cloudy-night-orchard-eating.png?ref_type=heads"
    ];
} else {

    sunnyFrog = [
        'froggie/01d.png'
    ];

    mostlySunnyFrog = [
        'froggie/02d.png'
    ];

    PartlyCloudyFrog = [
        'froggie/03d.png'
    ];

    OvercastFrog = [
        'froggie/04d.png'
    ];

    FogFrog = [
        'froggie/50d.png'
    ];

    RainFrog = [
        'froggie/09d.png'
    ];

    SnowFrog = [
        'froggie/13d.png'
    ];

    ThunderStormFrog = [
        'froggie/11d.png'
    ];

    ClearNightFrog = [
        'froggie/01n.png'
    ];

    PartlyCloudyNightFrog = [
        'froggie/02n.png'
    ];

}




function GetFroggieIcon(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {
            return sunnyFrog[Math.floor(Math.random() * sunnyFrog.length)];
        } else if (iconCode === 1) {
            return mostlySunnyFrog[Math.floor(Math.random() * mostlySunnyFrog.length)];
        } else if (iconCode === 2) {
            return PartlyCloudyFrog[Math.floor(Math.random() * PartlyCloudyFrog.length)];

        } else if (iconCode === 3) {
            return OvercastFrog[Math.floor(Math.random() * OvercastFrog.length)];

        } else if (iconCode === 45 || iconCode === 48) {
            return FogFrog[Math.floor(Math.random() * FogFrog.length)];

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 56 || iconCode === 57) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 61 || iconCode === 63) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 65) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 66 || iconCode === 67) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 71) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 73) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 75) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 77) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 80 || iconCode === 81) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 82) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 85) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 86) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 95) {

            return ThunderStormFrog[Math.floor(Math.random() * ThunderStormFrog.length)];

        } else if (iconCode === 96 || iconCode === 99) {

            return ThunderStormFrog[Math.floor(Math.random() * ThunderStormFrog.length)];

        }
    } else {
        if (iconCode === 0) {
            return ClearNightFrog[Math.floor(Math.random() * ClearNightFrog.length)];
        } else if (iconCode === 1) {
            return ClearNightFrog[Math.floor(Math.random() * ClearNightFrog.length)];
        } else if (iconCode === 2) {
            return PartlyCloudyNightFrog[Math.floor(Math.random() * PartlyCloudyNightFrog.length)];

        } else if (iconCode === 3) {
            return OvercastFrog[Math.floor(Math.random() * OvercastFrog.length)];

        } else if (iconCode === 45 || iconCode === 48) {
            return FogFrog[Math.floor(Math.random() * FogFrog.length)];

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 56 || iconCode === 57) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 61 || iconCode === 63) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 65) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 66 || iconCode === 67) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 71) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 73) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 75) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 77) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 80 || iconCode === 81) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 82) {

            return RainFrog[Math.floor(Math.random() * RainFrog.length)];

        } else if (iconCode === 85) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 86) {

            return SnowFrog[Math.floor(Math.random() * SnowFrog.length)];

        } else if (iconCode === 95) {

            return ThunderStormFrog[Math.floor(Math.random() * ThunderStormFrog.length)];

        } else if (iconCode === 96 || iconCode === 99) {

            return ThunderStormFrog[Math.floor(Math.random() * ThunderStormFrog.length)];

        }
    }




    return froggie
}

// color theme

function GetWeatherTheme(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {
            return 'clear-day'

        } else if (iconCode === 1) {
            return 'cloudy'

        } else if (iconCode === 2) {
            return 'cloudy'

        } else if (iconCode === 3) {
            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {
            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
            return 'rain'

        } else if (iconCode === 56 || iconCode === 57) {
            return 'rain'

        } else if (iconCode === 61 || iconCode === 63) {
            return 'rain'

        } else if (iconCode === 65) {
            return 'rain'

        } else if (iconCode === 66 || iconCode === 67) {
            return 'rain'

        } else if (iconCode === 71) {
            return 'snow'

        } else if (iconCode === 73) {
            return 'snow'

        } else if (iconCode === 75) {
            return 'snow'

        } else if (iconCode === 77) {
            return 'snow'

        } else if (iconCode === 80 || iconCode === 81) {
            return 'rain'

        } else if (iconCode === 82) {
            return 'rain'

        } else if (iconCode === 85) {
            return 'snow'

        } else if (iconCode === 86) {
            return 'snow'

        } else if (iconCode === 95) {
            return 'thunder'

        } else if (iconCode === 96 || iconCode === 99) {
            return 'thunder'

        }
    } else {
        if (iconCode === 0) {
            return 'clear-night'

        } else if (iconCode === 1) {
            return 'cloudy'

        } else if (iconCode === 2) {
            return 'cloudy'

        } else if (iconCode === 3) {
            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {
            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
            return 'rain'

        } else if (iconCode === 56 || iconCode === 57) {
            return 'rain'

        } else if (iconCode === 61 || iconCode === 63) {
            return 'rain'

        } else if (iconCode === 65) {
            return 'rain'

        } else if (iconCode === 66 || iconCode === 67) {
            return 'rain'

        } else if (iconCode === 71) {
            return 'snow'

        } else if (iconCode === 73) {
            return 'snow'

        } else if (iconCode === 75) {
            return 'snow'

        } else if (iconCode === 77) {
            return 'snow'

        } else if (iconCode === 80 || iconCode === 81) {
            return 'rain'

        } else if (iconCode === 82) {
            return 'rain'

        } else if (iconCode === 85) {
            return 'snow'

        } else if (iconCode === 86) {
            return 'snow'

        } else if (iconCode === 95) {
            return 'thunder'

        } else if (iconCode === 96 || iconCode === 99) {
            return 'thunder'

        }
    }




    return WeatherTheme
}


// day night


function GetWeatherIconDay(iconCode) {


        if (iconCode === 0) {
            return 'weather-icons/clear_day.svg';
        } else if (iconCode === 1) {
            return 'weather-icons/mostly_clear_day.svg';
        } else if (iconCode === 2) {
            return 'weather-icons/partly_cloudy_day.svg';

        } else if (iconCode === 3) {
            return 'weather-icons/cloudy.svg';

        } else if (iconCode === 45 || iconCode === 48) {
            return 'weather-icons/haze_fog_dust_smoke.svg';

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'weather-icons/drizzle.svg';

        } else if (iconCode === 56 || iconCode === 57) {

            return 'weather-icons/mixed_rain_hail_sleet.svg';

        } else if (iconCode === 61 || iconCode === 63) {

            return 'weather-icons/showers_rain.svg';

        } else if (iconCode === 65) {

            return 'weather-icons/heavy_rain.svg';

        } else if (iconCode === 66 || iconCode === 67) {

            return 'weather-icons/sleet_hail.svg';

        } else if (iconCode === 71) {

            return 'weather-icons/scattered_snow_showers_day.svg';

        } else if (iconCode === 73) {

            return 'weather-icons/showers_snow.svg';

        } else if (iconCode === 75) {

            return 'weather-icons/heavy_snow.svg';

        } else if (iconCode === 77) {

            return 'weather-icons/flurries.svg';

        } else if (iconCode === 80 || iconCode === 81) {

            return 'weather-icons/showers_rain.svg';

        } else if (iconCode === 82) {

            return 'weather-icons/heavy_rain.svg';

        } else if (iconCode === 85) {

            return 'weather-icons/showers_snow.svg';

        } else if (iconCode === 86) {

            return 'weather-icons/heavy_snow.svg';

        } else if (iconCode === 95) {

            return 'weather-icons/isolated_thunderstorms.svg'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'weather-icons/strong_thunderstorms.svg'

        }
    

    return iconCodeDay
}



function GetWeatherIconNight(iconCode) {


    if (iconCode === 0) {

        return 'weather-icons/clear_night.svg';
    
    } else if (iconCode === 1) {
    
        return 'weather-icons/mostly_clear_night.svg';
    
    } else if (iconCode === 2) {
    
        return 'weather-icons/partly_cloudy_night.svg';
    
    } else if (iconCode === 3) {
    
        return 'weather-icons/cloudy.svg';
    
    } else if (iconCode === 45 || iconCode === 48) {
    
        return 'weather-icons/haze_fog_dust_smoke.svg';
    
    } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
    
        return 'weather-icons/drizzle.svg';
    
    } else if (iconCode === 56 || iconCode === 57) {
    
        return 'weather-icons/mixed_rain_hail_sleet.svg';
    
    } else if (iconCode === 61 || iconCode === 63) {
    
        return 'weather-icons/showers_rain.svg';
    
    } else if (iconCode === 65) {
    
        return 'weather-icons/heavy_rain.svg';
    
    } else if (iconCode === 66 || iconCode === 67) {
    
        return 'weather-icons/sleet_hail.svg';
    
    } else if (iconCode === 71) {
    
        return 'weather-icons/scattered_snow_showers_night.svg';
    
    } else if (iconCode === 73) {
    
        return 'weather-icons/showers_snow.svg';
    
    } else if (iconCode === 75) {
    
        return 'weather-icons/heavy_snow.svg';
    
    } else if (iconCode === 77) {
    
        return 'weather-icons/flurries.svg';
    
    } else if (iconCode === 80 || iconCode === 81) {
    
        return 'weather-icons/showers_rain.svg';
    
    } else if (iconCode === 82) {
    
        return 'weather-icons/heavy_rain.svg';
    
    } else if (iconCode === 85) {
    
        return 'weather-icons/showers_snow.svg';
    
    } else if (iconCode === 86) {
    
        return 'weather-icons/heavy_snow.svg';
    
    } else if (iconCode === 95) {
    
        return 'weather-icons/isolated_thunderstorms.svg'
    
    } else if (iconCode === 96 || iconCode === 99) {
    
        return 'weather-icons/strong_thunderstorms.svg'
    
    }
    
        return iconCodeNight
}






// weather label no animation



function GetWeatherLabelNoAnim(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {

            return 'clear_sky'

        } else if (iconCode === 1) {

            return 'mostly_clear'

        } else if (iconCode === 2) {

            return 'partly_cloudy'

        } else if (iconCode === 3) {

            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {

            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'drizzle'

        } else if (iconCode === 56 || iconCode === 57) {

            return 'freezing_drizzle'

        } else if (iconCode === 61 || iconCode === 63) {

            return 'moderate_rain'

        } else if (iconCode === 65) {

            return 'heavy_intensity_rain'

        } else if (iconCode === 66 || iconCode === 67) {

            return 'freezing_rain'

        } else if (iconCode === 71) {

            return 'slight_snow'

        } else if (iconCode === 73) {

            return 'moderate_snow'

        } else if (iconCode === 75) {

            return 'heavy_intensity_snow'

        } else if (iconCode === 77) {

            return 'snow_grains'

        } else if (iconCode === 80 || iconCode === 81) {

            return 'rain_showers'

        } else if (iconCode === 82) {

            return 'heavy_rain_showers'

        } else if (iconCode === 85) {

            return 'slight_snow_showers'

        } else if (iconCode === 86) {

            return 'heavy_snow_showers'

        } else if (iconCode === 95) {

            return 'thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'strong_thunderstorm'

        }
    } else {
        if (iconCode === 0) {

            return 'clear_sky'

        } else if (iconCode === 1) {

            return 'mostly_clear'

        } else if (iconCode === 2) {

            return 'partly_cloudy'

        } else if (iconCode === 3) {

            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {

            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'drizzle'

        } else if (iconCode === 56 || iconCode === 57) {

            return 'freezing_drizzle'

        } else if (iconCode === 61 || iconCode === 63) {

            return 'moderate_rain'

        } else if (iconCode === 65) {

            return 'heavy_intensity_rain'

        } else if (iconCode === 66 || iconCode === 67) {

            return 'freezing_rain'

        } else if (iconCode === 71) {

            return 'slight_snow'

        } else if (iconCode === 73) {

            return 'moderate_snow'

        } else if (iconCode === 75) {

            return 'heavy_intensity_snow'

        } else if (iconCode === 77) {

            return 'snow_grains'

        } else if (iconCode === 80 || iconCode === 81) {

            return 'rain_showers'

        } else if (iconCode === 82) {

            return 'heavy_rain_showers'

        } else if (iconCode === 85) {

            return 'slight_snow_showers'

        } else if (iconCode === 86) {

            return 'heavy_snow_showers'

        } else if (iconCode === 95) {

            return 'thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'strong_thunderstorm'

        }
    }




    return ''
}

function getWeatherLabelInLangNoAnim(iconCode, isDay, langCode) {
    const translationKey = GetWeatherLabelNoAnim(iconCode, isDay);

    const translatedLabel = getTranslationByLang(langCode, translationKey);

    return translatedLabel || 'Unknown weather';
}

// ---------------------


function GetWeatherLabelNoAnimText(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === '0') {

            return 'clear_sky'

        } else if (iconCode === '1') {

            return 'mostly_clear'

        } else if (iconCode === '2') {

            return 'partly_cloudy'

        } else if (iconCode === '3') {

            return 'overcast'

        } else if (iconCode === '45' || iconCode === '48') {

            return 'fog'

        } else if (iconCode === '51' || iconCode === '53' || iconCode === '55') {

            return 'drizzle'

        } else if (iconCode === '56' || iconCode === '57') {

            return 'freezing_drizzle'

        } else if (iconCode === '61' || iconCode === '63') {

            return 'moderate_rain'

        } else if (iconCode === '65') {

            return 'heavy_intensity_rain'

        } else if (iconCode === '66' || iconCode === '67') {

            return 'freezing_rain'

        } else if (iconCode === '71') {

            return 'slight_snow'

        } else if (iconCode === '73') {

            return 'moderate_snow'

        } else if (iconCode === '75') {

            return 'heavy_intensity_snow'

        } else if (iconCode === '77') {

            return 'snow_grains'

        } else if (iconCode === '80' || iconCode === '81') {

            return 'rain_showers'

        } else if (iconCode === '82') {

            return 'heavy_rain_showers'

        } else if (iconCode === '85') {

            return 'slight_snow_showers'

        } else if (iconCode === '86') {

            return 'heavy_snow_showers'

        } else if (iconCode === '95') {

            return 'thunderstorm'

        } else if (iconCode === '96' || iconCode === '99') {

            return 'strong_thunderstorm'

        }
    } else {
        if (iconCode === 0) {

            return 'clear_sky'

        } else if (iconCode === 1) {

            return 'mostly_clear'

        } else if (iconCode === 2) {

            return 'partly_cloudy'

        } else if (iconCode === 3) {

            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {

            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'drizzle'

        } else if (iconCode === 56 || iconCode === 57) {

            return 'freezing_drizzle'

        } else if (iconCode === 61 || iconCode === 63) {

            return 'moderate_rain'

        } else if (iconCode === 65) {

            return 'heavy_intensity_rain'

        } else if (iconCode === 66 || iconCode === 67) {

            return 'freezing_rain'

        } else if (iconCode === 71) {

            return 'slight_snow'

        } else if (iconCode === 73) {

            return 'moderate_snow'

        } else if (iconCode === 75) {

            return 'heavy_intensity_snow'

        } else if (iconCode === 77) {

            return 'snow_grains'

        } else if (iconCode === 80 || iconCode === 81) {

            return 'rain_showers'

        } else if (iconCode === 82) {

            return 'heavy_rain_showers'

        } else if (iconCode === 85) {

            return 'slight_snow_showers'

        } else if (iconCode === 86) {

            return 'heavy_snow_showers'

        } else if (iconCode === 95) {

            return 'thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'strong_thunderstorm'

        }
    }




    return ''
}

function getWeatherLabelInLangNoAnimText(iconCode, isDay, langCode) {
    const translationKey = GetWeatherLabelNoAnimText(iconCode, isDay);

    const translatedLabel = getTranslationByLang(langCode, translationKey);

    return translatedLabel || 'Unknown weather';
}

// -----------


function animateTemp(temp_value) {
    const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');

    let targetNum = temp_value;
    let currentNum = 0;
    let baseSpeed = 50;

    if (SelectedTempUnit === 'fahrenheit') {
        currentNum = 100

    } else {
        currentNum = 0

    }

    function animateNumber() {
        let interval = setInterval(() => {

            if (currentNum > targetNum) {
                currentNum--;
                document.getElementById('temp').innerHTML = currentNum + '°';

                if (currentNum <= targetNum + 5 && currentNum > targetNum) {
                    clearInterval(interval);

                    interval = setInterval(() => {
                        if (currentNum > targetNum) {
                            currentNum--;
                            document.getElementById('temp').innerHTML = currentNum + '°';
                        } else {
                            clearInterval(interval);
                        }
                    }, baseSpeed * 4);
                }
                // If the targetNum is positive, increment the currentNum
            } else if (currentNum < targetNum) {
                currentNum++;
                document.getElementById('temp').innerHTML = currentNum + '°';

                if (currentNum >= targetNum - 5 && currentNum < targetNum) {
                    clearInterval(interval);

                    interval = setInterval(() => {
                        if (currentNum < targetNum) {
                            currentNum++;
                            document.getElementById('temp').innerHTML = currentNum + '°';
                        } else {
                            clearInterval(interval);
                        }
                    }, baseSpeed * 4);
                }
            } else {
                clearInterval(interval);
            }
        }, baseSpeed);
    }

    animateNumber();
}