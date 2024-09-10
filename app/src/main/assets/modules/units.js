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
            return 'Clear sky'

        } else if (iconCode === 1) {
            return 'Mostly clear'

        } else if (iconCode === 2) {
            return 'Partly cloudy'

        } else if (iconCode === 3) {
            return 'Overcast'

        } else if (iconCode === 45 || iconCode === 48) {
            return 'Fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
            return 'Drizzle'

        } else if (iconCode === 56 || iconCode === 57) {
            return 'Freezing Drizzle'

        } else if (iconCode === 61 || iconCode === 63) {
            return 'Moderate rain'

        } else if (iconCode === 65) {
            return 'Heavy intensity rain'

        } else if (iconCode === 66 || iconCode === 67) {
            return 'Freezing Rain'

        } else if (iconCode === 71) {
            return 'Slight snow'

        } else if (iconCode === 73) {
            return 'Moderate snow'

        } else if (iconCode === 75) {
            return 'Heavy intensity snow'

        } else if (iconCode === 77) {
            return 'Snow grains'

        } else if (iconCode === 80 || iconCode === 81) {
            return 'Rain showers'

        } else if (iconCode === 82) {
            return 'Heavy rain showers'

        } else if (iconCode === 85) {
            return 'Slight snow showers'

        } else if (iconCode === 86) {
            return 'Heavy snow showers'

        } else if (iconCode === 95) {
            return 'Thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {
            return 'Strong thunderstorm'

        }
    } else {
        if (iconCode === 0) {
            return 'Clear sky'

        } else if (iconCode === 1) {
            return 'Mostly clear'

        } else if (iconCode === 2) {
            return 'Partly cloudy'

        } else if (iconCode === 3) {
            return 'Overcast'

        } else if (iconCode === 45 || iconCode === 48) {
            return 'Fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
            return 'Drizzle'

        } else if (iconCode === 56 || iconCode === 57) {
            return 'Freezing Drizzle'

        } else if (iconCode === 61 || iconCode === 63) {
            return 'Moderate rain'

        } else if (iconCode === 65) {
            return 'Heavy intensity rain'

        } else if (iconCode === 66 || iconCode === 67) {
            return 'Freezing Rain'

        } else if (iconCode === 71) {
            return 'Slight snow'

        } else if (iconCode === 73) {
            return 'Moderate snow'

        } else if (iconCode === 75) {
            return 'Heavy intensity snow'

        } else if (iconCode === 77) {
            return 'Snow grains'

        } else if (iconCode === 80 || iconCode === 81) {
            return 'Rain showers'

        } else if (iconCode === 82) {
            return 'Heavy rain showers'

        } else if (iconCode === 85) {
            return 'Slight snow showers'

        } else if (iconCode === 86) {
            return 'Heavy snow showers'

        } else if (iconCode === 95) {
            return 'Thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {
            return 'Strong thunderstorm'

        }
    }




    return WeatherLabel
}


// froggie


function GetFroggieIcon(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {
            return 'froggie/01d.png';
        } else if (iconCode === 1) {
            return 'froggie/02d.png';
        } else if (iconCode === 2) { 
            return 'froggie/02d.png';

        } else if (iconCode === 3) {
            return 'froggie/03d.png';

        } else if (iconCode === 45 || iconCode === 48) {
            return 'froggie/50d.png';

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'froggie/10d.png';

        } else if (iconCode === 56 || iconCode === 57) {

            return 'froggie/10d.png';

        } else if (iconCode === 61 || iconCode === 63) {

            return 'froggie/09d.png';

        } else if (iconCode === 65) {

            return 'froggie/09d.png';

        } else if (iconCode === 66 || iconCode === 67) {

            return 'froggie/hail.png';

        } else if (iconCode === 71) {

            return 'froggie/13d.png';

        } else if (iconCode === 73) {

            return 'froggie/flurries.png';

        } else if (iconCode === 75) {

            return 'froggie/13d.png';

        } else if (iconCode === 77) {

            return 'froggie/snowgrains.png';

        } else if (iconCode === 80 || iconCode === 81) {

            return 'froggie/heavy_rain.png';

        } else if (iconCode === 82) {

            return 'froggie/heavy_rain.png';

        } else if (iconCode === 85) {

            return 'froggie/13d.png';

        } else if (iconCode === 86) {

            return 'froggie/flurries.png';

        } else if (iconCode === 95) {

            return 'froggie/11d.png'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'froggie/strong_thunder.png'

        }
    } else {
        if (iconCode === 0) {
            return 'froggie/01n.png';
        } else if (iconCode === 1) {
            return 'froggie/02n.png';
        } else if (iconCode === 2) { 
            return 'froggie/02n.png';

        } else if (iconCode === 3) {
            return 'froggie/03n.png';

        } else if (iconCode === 45 || iconCode === 48) {
            return 'froggie/50n.png';

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return 'froggie/10n.png';

        } else if (iconCode === 56 || iconCode === 57) {

            return 'froggie/10n.png';

        } else if (iconCode === 61 || iconCode === 63) {

            return 'froggie/09n.png';

        } else if (iconCode === 65) {

            return 'froggie/09n.png';

        } else if (iconCode === 66 || iconCode === 67) {

            return 'froggie/hail.png';

        } else if (iconCode === 71) {

            return 'froggie/13n.png';

        } else if (iconCode === 73) {

            return 'froggie/flurries.png';

        } else if (iconCode === 75) {

            return 'froggie/13n.png';

        } else if (iconCode === 77) {

            return 'froggie/snowgrains.png';

        } else if (iconCode === 80 || iconCode === 81) {

            return 'froggie/heavy_rain.png';

        } else if (iconCode === 82) {

            return 'froggie/heavy_rain.png';

        } else if (iconCode === 85) {

            return 'froggie/13n.png';

        } else if (iconCode === 86) {

            return 'froggie/flurries.png';

        } else if (iconCode === 95) {

            return 'froggie/11n.png'

        } else if (iconCode === 96 || iconCode === 99) {

            return 'froggie/strong_thunder.png'

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






