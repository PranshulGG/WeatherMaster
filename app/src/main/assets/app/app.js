const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
const SelectedWindUnit = localStorage.getItem('SelectedWindUnit');
const SelectedVisibiltyUnit = localStorage.getItem('selectedVisibilityUnit');



let currentLocation = null;

document.addEventListener('DOMContentLoaded', () => {
    showLoader();
    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition((position) => {
            currentLocation = {
                latitude: position.coords.latitude,
                longitude: position.coords.longitude
            };
            getWeatherByCoordinates(currentLocation.latitude, currentLocation.longitude);
        }, (error) => {
            console.error('Geolocation error:', error);

        });
    } else {
        console.error('Geolocation is not available in this browser.');

    }
});


const uvIndexData = [
    { description: "Very Low", style: "background-color: #4AC708;", img: 'uv-images/uv-0.png', detail: 'A UV index of 0 is considered satisfactory, indicating that there is little or no risk of harm from ultraviolet radiation.' },
    { description: "Low", style: "background-color: #4AC708;", img: 'uv-images/uv-1.png', detail: 'A UV index of 1 is considered satisfactory, indicating that there is little or no risk of harm from ultraviolet radiation.' },
    { description: "Acceptable", style: "background-color: #4AC708;", img: 'uv-images/uv-2.png', detail: 'With a UV index of 2, conditions are still generally low-risk, indicating that exposure to ultraviolet radiation poses a minimal threat.' },
    { description: "Fair", style: "background-color: #FFB800;", img: 'uv-images/uv-3.png', detail: 'At a index of 3, the risk of harm from ultraviolet radiation remains low. It is considered moderate, suggesting a slight increase in caution for prolonged sun exposure.' },
    { description: "Moderate", style: "background-color: #FFB800;", img: 'uv-images/uv-4.png', detail: 'index at 4 Moderate risk. Caution advised—consider sunscreen and limit sun exposure during peak hours.' },
    { description: "Balanced", style: "background-color: #FFB800;", img: 'uv-images/uv-5.png', detail: 'index of 5 Moderate risk. Use sunscreen, especially during midday hours, and take precautions for extended sun exposure.' },
    { description: "Elevated", style: "background-color: #ff8d00;", img: 'uv-images/uv-6.png', detail: 'index at 6 Moderate to high risk. Apply sunscreen, wear protective clothing, and limit sun exposure, especially during midday hours.' },
    { description: "Intense", style: "background-color: #ff8d00;", img: 'uv-images/uv-7.png', detail: 'index at 7 High risk. Use sunscreen, protective clothing, and limit sun exposure, particularly during midday hours, to reduce the risk of harm from intense ultraviolet radiation.' },
    { description: "Severe", style: "background-color: #ff3c00;", img: 'uv-images/uv-8.png', detail: 'index of 8 Very high risk. Take utmost precautions—apply sunscreen, wear protective clothing, and minimize sun exposure, especially during midday hours, to prevent harm from intense ultraviolet radiation.' },
    { description: "Critical", style: "background-color: #ff3c00;", img: 'uv-images/uv-9.png', detail: 'index at 9 Extremely high risk. Use high SPF sunscreen, wear protective clothing, and avoid prolonged sun exposure, especially midday, to minimize harm from intense ultraviolet radiation.' },
    { description: "Its Very High", style: "background-color: #ff3c00;", img: 'uv-images/uv-10.png', detail: 'index at 10 Very high risk. Maximize precautions—use high SPF sunscreen, wear protective clothing, and limit sun exposure, especially midday, to minimize harm from intense ultraviolet radiation.' },
    { description: "Extreme", style: "background-color: #9936D4;", img: 'uv-images/uv-11.png', detail: 'Index at 11 avoid every outdoor activies.' },
    { description: "Extreme+", style: "background-color: #FF0087;", img: 'uv-images/uv-12.png', detail: 'Index at 12 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' },
    { description: "Extreme++", style: "background-color: #FF0087;", img: 'uv-images/uv-13.png', detail: 'Index at 13 avoid every outdoor activies.' }

];


function handleGeolocationError(error) {
    console.error('Error getting geolocation:', error);
    displayErrorMessage('Error getting geolocation. Please enable location services.');
}


document.addEventListener('DOMContentLoaded', () => {
    const cityInput = document.getElementById('city-input');
    const cityList = document.getElementById('city-list');
    const cityopen = document.getElementById('city-open');
    const searchContainer = document.getElementById('search-container');
    const closeButton = document.querySelector('.close_search');


    cityopen.addEventListener("click", () => {
        searchContainer.style.display = 'block';
        window.history.pushState({ SearchContainerOpen: true }, "");

        setTimeout(() =>{
            cityInput.focus()
        }, 300);
    });

    closeButton.addEventListener('click', () => {

        window.history.back()
    });




    cityInput.addEventListener('input', () => {
        const searchTerm = cityInput.value.trim();
        document.querySelector('.currentLocationdiv').hidden = true;


        if (searchTerm) {
            document.getElementById('cityLoader').hidden = false;
            fetchCitySuggestions(searchTerm)
                .then(suggestions => displayCitySuggestions(suggestions, searchTerm));
        } else {
            cityList.innerHTML = '';
            document.getElementById('cityLoader').hidden = true;
            document.querySelector('.currentLocationdiv').hidden = false;


        }
    });

    cityList.addEventListener('click', (event) => {
        const selectedCity = event.target.textContent;
        const apiKey = '120d979ba5b2d0780f51872890f5ad0b';

        

        window.history.back()




        cityList.innerHTML = '';
        cityInput.value = '';
        document.getElementById('city-name').innerHTML = '<md-circular-progress indeterminate style="--md-circular-progress-size: 30px;"></md-circular-progress>'
        document.querySelector('.focus-input').blur();
        document.getElementById('forecast').scrollLeft = 0;
        document.getElementById('weather_wrap').scrollTop = 0;
        setTimeout(() =>{
            cityInput.dispatchEvent(new Event('input'));
        }, 200);

        showLoader()
        if (selectedCity.toLowerCase() === "delhi, india") {
            getWeather(selectedCity, 28.6139, 77.2090);
        } else {
            fetch(`https://api.openweathermap.org/geo/1.0/direct?q=${encodeURIComponent(selectedCity)}&limit=1&appid=${apiKey}`)
                .then(response => response.json())
                .then(data => {
                    if (data.length > 0) {
                        const cityCoords = data[0];
                        getWeather(selectedCity, cityCoords.lat, cityCoords.lon);
                    }
                })
                .catch(error => console.error('Error fetching coordinates:', error));
        }

    });
});


window.addEventListener('popstate', function (event) {
    document.getElementById('search-container').style.opacity = '0'

    setTimeout(() => {
        document.getElementById('search-container').style.display = 'none'
        document.getElementById('search-container').style.opacity = '1'

    }, 200);

});


function fetchCitySuggestions(searchTerm) {
    const query = encodeURIComponent(searchTerm);
    const apiKey = '120d979ba5b2d0780f51872890f5ad0b';

    if (searchTerm.toLowerCase() === "delhi") {
        return Promise.resolve(["Delhi, India"]);
    }

    return fetch(`https://api.openweathermap.org/geo/1.0/direct?q=${query}&limit=5&appid=${apiKey}`)
        .then(response => response.json())
        .then(data => data.map(city => {
            const cityName = city.name;
            const stateOrCountry = city.state || city.country;

            return `${cityName}, ${stateOrCountry}`;

        }))
        .catch(error => console.error('Error fetching city suggestions:', error));
}

function displayCitySuggestions(suggestions, searchTerm) {
    const cityList = document.getElementById('city-list');
    cityList.innerHTML = '';
    suggestions.forEach(suggestion => {

        if (suggestion.toLowerCase().includes(searchTerm.toLowerCase())) {
            const suggestionItem = document.createElement('div');
            const saveBTN = document.createElement('md-text-button');
                        const createSuggestRipple = document.createElement('md-ripple')
                        createSuggestRipple.style = '--md-ripple-pressed-opacity: 0.13;'
            // const suggestSpace = document.createElement('suggest-space');
            suggestionItem.classList.add("suggest");
            suggestionItem.textContent = suggestion;
            // suggestionItem.appendChild(suggestSpace)
            cityList.appendChild(suggestionItem);
            // suggestionItem.appendChild(saveBTN)
            // saveBTN.textContent = 'Save'
            suggestionItem.appendChild(createSuggestRipple)

            setTimeout(() => {
                document.getElementById('cityLoader').hidden = true;

            }, 400);

        }
    });
}



function getWeather(city, latitude, longitude) {
    showLoader();
    const apiKey = '120d979ba5b2d0780f51872890f5ad0b';
    const apiUrl = `https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&appid=${apiKey}&units=metric`;
    getMoonSetMoonRise(latitude, longitude)

    


    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const cityName = data.name;
            const countryName = data.sys.country;
            const temperature = Math.round(data.main.temp);
            const tempF = Math.round(temperature * 9 / 5 + 32);
            const visibility = data.visibility;
            const visibilityInMiles = (visibility * 0.000621371).toFixed(0);

            const humidity = data.main.humidity;
            const description = data.weather[0].description;
            const clouds = data.clouds.all
            const feelslike = data.main.feels_like;
            const feelslikeF = Math.round(feelslike * 9 / 5 + 32);

            const iconCode = data.weather[0].icon;
            document.getElementById('city-name').innerHTML = `${cityName}, ${countryName}`;

            if(SelectedTempUnit === 'fahrenheit'){
                document.getElementById('temp').innerHTML = `${tempF}<span>°F</span>`;
            document.getElementById('max-temp').innerHTML = `${Math.round(feelslikeF)}°F`;
         document.getElementById('temPDiscCurrentLocation').innerHTML = `${tempF}°F • <span>${description}</span>`

            } else{
            document.getElementById('temp').innerHTML = `${temperature}<span>°C</span>`;
            document.getElementById('max-temp').innerHTML = `${Math.round(feelslike)}°C`;
             document.getElementById('temPDiscCurrentLocation').innerHTML = `${temperature}°C • <span>${description}</span>`


            }

          document.getElementById('currentLocationName').textContent = `${cityName}, ${countryName}`;
             document.getElementById('currentSearchImg').src = `weather-icons/${iconCode}.svg`;

            document.getElementById('description').textContent = description;


            const weatherIcon = document.getElementById('weather-icon');
            weatherIcon.src = `weather-icons/${iconCode}.svg`;
                        document.getElementById('froggie_imgs').src = `froggie/${iconCode}.png`;
                        document.documentElement.setAttribute('iconCodeTheme', `${iconCode}`);
                        localStorage.setItem('weatherTHEME', iconCode)
                        sendThemeToAndroid(iconCode)

            weatherIcon.alt = "Weather Icon";

            const latitude = data.coord.lat;
            const longitude = data.coord.lon;

            get24HourForecast(latitude, longitude);

            get5DayForecast(latitude, longitude);

            const windSpeedMPS = data.wind.speed;
            const windSpeedMPH = (windSpeedMPS * 2.23694).toFixed(0);

            const windSpeedKPH = (windSpeedMPS * 3.6).toFixed(0); 
            const timeZoneOffsetSeconds = data.timezone;
            const sunriseUTC = new Date((data.sys.sunrise + timeZoneOffsetSeconds) * 1000);
            const sunsetUTC = new Date((data.sys.sunset + timeZoneOffsetSeconds) * 1000);
            const maxTemp = data.main.temp_max;
            const minTemp = data.main.temp_min;

            const options = { timeZone: 'UTC', hour: 'numeric', minute: 'numeric' };
            const sunrise = sunriseUTC.toLocaleTimeString('en-US', options);
            const sunset = sunsetUTC.toLocaleTimeString('en-US', options);

            if(SelectedWindUnit === 'mile'){
                document.getElementById('wind-speed').textContent = `${windSpeedMPH} mph`;
            } else{
                document.getElementById('wind-speed').textContent = `${windSpeedKPH} km/h`;

            }


            if(SelectedVisibiltyUnit === 'mileV'){
                document.getElementById('min-temp').innerHTML = `${visibilityInMiles} mile`;
            } else{
                document.getElementById('min-temp').innerHTML = `${visibility / 1000} km`;

            }

            document.getElementById('sunrise').textContent = sunrise;
            document.getElementById('sunset').textContent = sunset;
            document.getElementById('humidity').textContent = `${humidity}%`;
            document.getElementById('clouds').textContent = `${clouds}%`;



            const lastUpdatedTimestamp = data.dt;
            const lastUpdatedTime = new Date(lastUpdatedTimestamp * 1000).toLocaleTimeString();

            document.getElementById('updated').textContent = `Last Updated: ${lastUpdatedTime}`;


            function getClothingRecommendation(temp) {
                if (temp <= 0) {
                    return "Wear a heavy coat, gloves, a hat, and a scarf.";
                } else if (temp <= 5) {
                    return "Wear a thick coat, a hat, and gloves.";
                } else if (temp <= 10) {
                    return "Wear a coat and a sweater.";
                } else if (temp <= 15) {
                    return "Wear a light jacket and long sleeves.";
                } else if (temp <= 20) {
                    return "Wear a light jacket or a sweater.";
                } else if (temp <= 25) {
                    return "Wear a t-shirt and jeans or pants.";
                } else if (temp <= 30) {
                    return "Wear a t-shirt and light pants or shorts.";
                } else if (temp <= 35) {
                    return "Wear light, breathable clothing and stay hydrated.";
                } else if (temp <= 40) {
                    return "Wear very light clothing, stay hydrated, and avoid direct sun.";
                } else if (temp <= 45) {
                    return "Wear minimal clothing, stay indoors if possible, and stay hydrated.";
                } else {
                    return "Extreme heat! Wear minimal clothing, stay indoors, and drink plenty of water.";
                }
            }

            const recommendation = getClothingRecommendation(temperature);

            document.getElementById('cloth_recommended').textContent = recommendation


            // Fetch UV Index

            const air_url = `https://api.openweathermap.org/data/2.5/air_pollution?lat=${latitude}&lon=${longitude}&appid=${apiKey}`;

            fetch(air_url)
                .then(response => response.json())
                .then(air_data => {
                    let aqi = air_data.list[0].main.aqi;
                    document.getElementById('aqi-level').textContent = aqiText[aqi].level;
                    document.getElementById('detail_air').textContent = aqiText[aqi].message;


                    const backgroundImage = {
                        1: 'air-pop-imgs/good.png',
                        2: 'air-pop-imgs/fair.png',
                        3: 'air-pop-imgs/moderate.png',
                        4: 'air-pop-imgs/poor.png',
                        5: 'air-pop-imgs/very_poor.png'
                    };

                    const backgroundColor = {
                        1: 'rgb(11, 189, 5)',
                        2: 'rgb(218, 179, 8)',
                        3: 'rgb(255, 136, 0)',
                        4: 'rgb(255, 0, 0)',
                        5: 'rgb(183, 0, 255)'
                    }


                    document.getElementById('aqi_img').src = backgroundImage[aqi];
                    document.getElementById('aqi-level').style.backgroundColor = backgroundColor[aqi];
                });

            const api_uv = 'c3a14b64fd3eb5f994230183700f79d1'

            const uvUrl = `https://api.openweathermap.org/data/2.5/uvi?lat=${latitude}&lon=${longitude}&appid=${api_uv}`;
            fetch(uvUrl)
                .then(response => response.json())
                .then(uvData => {
                    const uvIndexValue = uvData.value;
                    const uvIndex = Math.round(uvIndexValue);

                    const uvIndexInfo = uvIndexData[uvIndex];

                    const uvIndexText = uvIndexInfo.description;


                    const uvIndexElement = document.getElementById('uv-index');
                    uvIndexElement.textContent = uvIndexText;
                    document.getElementById('uv-index').style = uvIndexInfo.style;
                    document.getElementById('uv_img').src = uvIndexInfo.img;
                    document.getElementById('detail_uv').innerHTML = uvIndexInfo.detail;

                    hideLoader();
                    localStorage.setItem('currentLat', latitude)
                    localStorage.setItem('currentLong', longitude)

                })
                .catch(error => console.error('Error fetching UV Index:', error));


        })
        .catch(error => {
            console.error('Error fetching current weather:', error);
            document.querySelector('.no_internet_error').hidden = false;
        });



    currentLocation = null;

} 


const aqiText = {
    1: {
        level: "Good",
        message: "Air quality is considered satisfactory, and air pollution poses little or no risk.",
    },
    2: {
        level: "Fair",
        message: "Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.",

    },
    3: {
        level: "Moderate",
        message: "Member of sensitive groups may experience health effects. The general public is not likely to be affected.",

    },
    4: {
        level: "Poor",
        message: "Everyone may begin to experience health effects; member of sensitive groups may experience more serious health effects.",

    },
    5: {
        level: "Very Poor",
        message: "Health warnings of emergency conditions. The entire population is more likely to be affected.",
    }
}





function getCurrentLocationWeather() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(position => {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;
            getWeatherByCoordinates(latitude, longitude); // Call getWeatherByCoordinates with coordinates
        }, handleGeolocationError);
    } else {
        console.error('Geolocation is not supported by this browser.');
    }
}




function getWeatherByCoordinates(latitude, longitude) {
    showLoader();
    const apiKey = '120d979ba5b2d0780f51872890f5ad0b';
    const apiUrl = `https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&appid=${apiKey}&units=metric`;
    getMoonSetMoonRise(latitude, longitude)


    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const cityName = data.name;
            const countryName = data.sys.country;
            const temperature = Math.round(data.main.temp);
            const tempF = Math.round(temperature * 9 / 5 + 32);

            const humidity = data.main.humidity;
            const clouds = data.clouds.all
            const iconCode = data.weather[0].icon;
            const description = data.weather[0].description;
            const feelslike = data.main.feels_like;
            const feelslikeF = Math.round(feelslike * 9 / 5 + 32);
            document.getElementById('city-name').innerHTML = `${cityName}, ${countryName}`;

            if(SelectedTempUnit === 'fahrenheit'){
                document.getElementById('temp').innerHTML = `${tempF}<span>°F</span>`;
            document.getElementById('max-temp').innerHTML = `${Math.round(feelslikeF)}°F`;
             document.getElementById('temPDiscCurrentLocation').innerHTML = `${tempF}°F • <span>${description}</span>`

            } else{
            document.getElementById('temp').innerHTML = `${temperature}<span>°C</span>`;
            document.getElementById('max-temp').innerHTML = `${Math.round(feelslike)}°C`;

            document.getElementById('temPDiscCurrentLocation').innerHTML = `${temperature}°C • <span>${description}</span>`

            }


              document.getElementById('currentLocationName').textContent = `${cityName}, ${countryName}`;
                 document.getElementById('currentSearchImg').src = `weather-icons/${iconCode}.svg`;


            document.getElementById('description').textContent = description;
            const visibility = data.visibility;
            const visibilityInMiles = (visibility * 0.000621371).toFixed(0);


            const weatherIcon = document.getElementById('weather-icon');
            weatherIcon.src = `weather-icons/${iconCode}.svg`
                        document.getElementById('froggie_imgs').src = `froggie/${iconCode}.png`;
                        document.documentElement.setAttribute('iconCodeTheme', `${iconCode}`);
                        localStorage.setItem('weatherTHEME', iconCode)
                        sendThemeToAndroid(iconCode)

            weatherIcon.title = `${iconCode}`;
            // Fetch 24-hour forecast
            get24HourForecast(latitude, longitude);

            // Fetch 5-day forecast
            get5DayForecast(latitude, longitude);

            // Additional weather information
            const windSpeedMPS = data.wind.speed;
            const windSpeedMPH = (windSpeedMPS * 2.23694).toFixed(0);
            const windSpeedKPH = (windSpeedMPS * 3.6).toFixed(0); // Convert m/s to km/h
            const timeZoneOffsetSeconds = data.timezone;
            const sunriseUTC = new Date((data.sys.sunrise + timeZoneOffsetSeconds) * 1000);
            const sunsetUTC = new Date((data.sys.sunset + timeZoneOffsetSeconds) * 1000);
            const maxTemp = data.main.temp_max;
            const minTemp = data.main.temp_min;

            const options = { timeZone: 'UTC', hour: 'numeric', minute: 'numeric' };
            const sunrise = sunriseUTC.toLocaleTimeString('en-US', options);
            const sunset = sunsetUTC.toLocaleTimeString('en-US', options);

            
            if(SelectedWindUnit === 'mile'){
                document.getElementById('wind-speed').textContent = `${windSpeedMPH} mph`;
            } else{
                document.getElementById('wind-speed').textContent = `${windSpeedKPH} km/h`;

            }

            if(SelectedVisibiltyUnit === 'mileV'){
                document.getElementById('min-temp').innerHTML = `${visibilityInMiles} mile`;

            } else{
                document.getElementById('min-temp').innerHTML = `${visibility / 1000} km`;
            }


            document.getElementById('sunrise').textContent = sunrise;
            document.getElementById('sunset').textContent = sunset;

            document.getElementById('humidity').textContent = ` ${humidity}% `;
            document.getElementById('clouds').textContent = `${clouds}%`;





            const lastUpdatedTimestamp = data.dt;
            const lastUpdatedTime = new Date(lastUpdatedTimestamp * 1000).toLocaleTimeString();

            document.getElementById('updated').textContent = `Last Updated: ${lastUpdatedTime}`;



                            function getClothingRecommendation(temp) {
                                if (temp <= 0) {
                                    return "Wear a heavy coat, gloves, a hat, and a scarf.";
                                } else if (temp <= 5) {
                                    return "Wear a thick coat, a hat, and gloves.";
                                } else if (temp <= 10) {
                                    return "Wear a coat and a sweater.";
                                } else if (temp <= 15) {
                                    return "Wear a light jacket and long sleeves.";
                                } else if (temp <= 20) {
                                    return "Wear a light jacket or a sweater.";
                                } else if (temp <= 25) {
                                    return "Wear a t-shirt and jeans or pants.";
                                } else if (temp <= 30) {
                                    return "Wear a t-shirt and light pants or shorts.";
                                } else if (temp <= 35) {
                                    return "Wear light, breathable clothing and stay hydrated.";
                                } else if (temp <= 40) {
                                    return "Wear very light clothing, stay hydrated, and avoid direct sun.";
                                } else if (temp <= 45) {
                                    return "Wear minimal clothing, stay indoors if possible, and stay hydrated.";
                                } else {
                                    return "Extreme heat! Wear minimal clothing, stay indoors, and drink plenty of water.";
                                }
                            }

                            const recommendation = getClothingRecommendation(temperature);

                            document.getElementById('cloth_recommended').textContent = recommendation



            const air_url = `https://api.openweathermap.org/data/2.5/air_pollution?lat=${latitude}&lon=${longitude}&appid=${apiKey}`;

            fetch(air_url)
                .then(response => response.json())
                .then(air_data => {
                    let aqi = air_data.list[0].main.aqi;
                    document.getElementById('aqi-level').textContent = aqiText[aqi].level;
                    document.getElementById('detail_air').textContent = aqiText[aqi].message;


                    const backgroundImage = {
                        1: 'air-pop-imgs/good.png',
                        2: 'air-pop-imgs/fair.png',
                        3: 'air-pop-imgs/moderate.png',
                        4: 'air-pop-imgs/poor.png',
                        5: 'air-pop-imgs/very_poor.png'
                    };

                    const backgroundColor = {
                        1: 'rgb(11, 189, 5)',
                        2: 'rgb(218, 179, 8)',
                        3: 'rgb(255, 136, 0)',
                        4: 'rgb(255, 0, 0)',
                        5: 'rgb(183, 0, 255)'
                    }


                    document.getElementById('aqi_img').src = backgroundImage[aqi];
                    document.getElementById('aqi-level').style.backgroundColor = backgroundColor[aqi];
                })
            const uvUrl = `https://api.openweathermap.org/data/2.5/uvi?lat=${latitude}&lon=${longitude}&appid=${apiKey}`;
            fetch(uvUrl)
                .then(response => response.json())
                .then(uvData => {
                    const uvIndexValue = uvData.value;
                    const uvIndex = Math.round(uvIndexValue);

                    const uvIndexInfo = uvIndexData[uvIndex];

                    const uvIndexText = uvIndexInfo.description;

                    // Set the text and class on the element
                    const uvIndexElement = document.getElementById('uv-index');
                    uvIndexElement.textContent = uvIndexText;
                    document.getElementById('uv-index').style = uvIndexInfo.style;
                    document.getElementById('uv_img').src = uvIndexInfo.img;
                    document.getElementById('detail_uv').innerHTML = uvIndexInfo.detail;

                    hideLoader();
                    localStorage.setItem('currentLat', latitude)
                    localStorage.setItem('currentLong', longitude)
                })
                .catch(error => console.error('Error fetching UV Index:', error));


        })
          .catch(error => {
                    console.error('Error fetching current weather:', error);
                    document.querySelector('.no_internet_error').hidden = false;
                });
n


    currentLocation = {
        latitude,
        longitude
    };

}


// Add this function to fetch the 24-hour forecast
function get24HourForecast(latitude, longitude) {
    const apiKey = '28fe7b5f9a78838c639143fc517e4343';
    const apiUrl = `https://api.openweathermap.org/data/2.5/onecall?lat=${latitude}&lon=${longitude}&exclude=current,minutely,daily,alerts&appid=${apiKey}&units=metric`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const forecastData = data.hourly;
            display24HourForecast(forecastData);
        })
        .catch(error => console.error('Error fetching 24-hour forecast:', error));
}


function display24HourForecast(forecastData) {
    const forecastContainer = document.getElementById('forecast');
    forecastContainer.innerHTML = ''; 

    if (forecastData && forecastData.length >= 24) {
        for (let i = 0; i < 24; i++) {
            const forecast = forecastData[i];
            const timestamp = new Date(forecast.dt * 1000);
            const time = timestamp.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit', hour12: true }).replace(/\s/g, '');


                const temperature = Math.round(forecast.temp);
                const tempF = Math.round(temperature * 9 / 5 + 32);

                


            const iconCode = forecast.weather[0].icon;
            const description = forecast.weather[0].description;
            const rainPercentage = forecast.pop * 100;


            const forecastItem = document.createElement('div');
            forecastItem.classList.add('forecast-item');
            forecastItem.id = "forecast24";

            if(SelectedTempUnit === 'fahrenheit'){
                forecastItem.innerHTML = `
                <p class="time-24">${time}</p>
                <img id="icon-24" src="weather-icons/${iconCode}.svg" alt="Weather Icon" class="icon-24">
                <p class="temp-24">${tempF}°F</p>
                 <p class="rain-24"><span icon-outlined>water_drop</span> ${Math.round(rainPercentage)}%</p>

                 <p class="disc_sml-24" >${description}</p>
            `;
            } else{
            forecastItem.innerHTML = `
            <p class="time-24">${time}</p>
            <img id="icon-24" src="weather-icons/${iconCode}.svg" alt="Weather Icon" class="icon-24">
            <p class="temp-24">${temperature}°C</p>
             <p class="rain-24"><span icon-outlined>water_drop</span> ${Math.round(rainPercentage)}%</p>

             <p class="disc_sml-24" >${description}</p>
        `;
            }

            

            forecastContainer.appendChild(forecastItem);

        }
    } else {
        console.error('Error fetching 24-hour forecast: Data is missing or insufficient');
                forecastContainer.innerHTML = `
                                    <div style="display: flex; align-items: center; justify-content: center; width: 100%;">
                                <md-circular-progress indeterminate ></md-circular-progress></div>`
    }
}

function showToast(description, time) {
    const toast = document.createElement('div');
    toast.classList.add('toast');
    toast.innerHTML = `${description} at ${time}`;


    const toastContainer = document.getElementById('toast-container');
    toastContainer.appendChild(toast);


    setTimeout(() => {
        toast.style.opacity = '0';

        setTimeout(() => {
            toastContainer.removeChild(toast);

        }, 500);
    }, 3000);
}
function get5DayForecast(latitude, longitude) {
    const apiKey = '28fe7b5f9a78838c639143fc517e4343';
    const apiUrl = `https://api.openweathermap.org/data/2.5/forecast?lat=${latitude}&lon=${longitude}&appid=${apiKey}&units=metric`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const forecastData = data.list;
            display5DayForecast(forecastData);
        })
        .catch(error => console.error('Error fetching 5-day forecast:', error));
}

function display5DayForecast(forecastData) {
    const forecast5dayContainer = document.getElementById('forecast-5day');
    forecast5dayContainer.innerHTML = '';

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    for (let i = 7; i < forecastData.length; i += 8) {
        const forecast = forecastData[i];
        const timestamp = new Date(forecast.dt * 1000);


        if (timestamp.getDate() === today.getDate()) {
            continue;
        }

        const date = timestamp.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

        const description = forecast.weather[0].description;
        let iconCode = forecast.weather[0].icon;
        const temp = Math.round(forecast.main.temp);

        const tempF = Math.round(temp * 9 / 5 + 32);




        iconCode = iconCode.replace('n', 'd');

        let tempColor;
        if (temp <= -50) {
            tempColor = 'blue';
        } else if (temp <= 0) {
            tempColor = 'lightblue'; 
        } else if (temp <= 25) {
            tempColor = 'rgb(9, 185, 9)'; 
        } else {
            tempColor = 'red'; 
        }


        const forecastItem = document.createElement('div');
        forecastItem.classList.add('forecast-item-forecast');


        if(SelectedTempUnit === 'fahrenheit'){
            forecastItem.innerHTML = `

            <img id="icon-5d" src="weather-icons/${iconCode}.svg" alt="Weather Icon">
            <p class="disc-5d">${tempF}°F</p>
            <div class="temp_progress_hold">
            <md-linear-progress value="0" id="temp-bar-${i}" style="--md-linear-progress-active-indicator-color: ${tempColor};"></md-linear-progress></div>
            <div class="d5-disc-text">${description}
            <p class="time-5d">${date}</p>
            </div>

        `;
        } else{

        forecastItem.innerHTML = `

        <img id="icon-5d" src="weather-icons/${iconCode}.svg" alt="Weather Icon">
        <p class="disc-5d">${temp}°C</p>
        <div class="temp_progress_hold">
        <md-linear-progress value="0" id="temp-bar-${i}" style="--md-linear-progress-active-indicator-color: ${tempColor};"></md-linear-progress></div>
        <div class="d5-disc-text">${description}
        <p class="time-5d">${date}</p>
        </div>

    `;
        }



        const minTemp = -100;
        const maxTemp = 50;
        const value = (temp - minTemp) / (maxTemp - minTemp);
        setTimeout(() => {
            document.getElementById(`temp-bar-${i}`).value = value;
        }, 0);


        forecast5dayContainer.appendChild(forecastItem);
        
    }
}



function showLoader() {
    const loaderContainer = document.getElementById('loader-container');
    loaderContainer.style.display = 'flex';
    loaderContainer.style.opacity = '1';
                document.getElementById('city-open').disabled = true;

}

// Hide the loader
function hideLoader() {
    const loaderContainer = document.getElementById('loader-container');

    loaderContainer.style.opacity = '0';

    setTimeout(() => {
        loaderContainer.style.display = 'none';
                document.getElementById('city-open').disabled = false;

    }, 300);


}



document.getElementById('forecast').addEventListener('scroll', function () {
    var items = document.querySelectorAll('.forecast .forecast-item');
    var scrollPosition = document.getElementById('forecast').scrollLeft;
    var windowWidth = document.getElementById('forecast').offsetWidth;

    items.forEach(function (item) {
        var itemOffset = item.offsetLeft - scrollPosition;
        var isVisible = (itemOffset >= 0 && itemOffset < windowWidth);
        if (isVisible) {
            item.style.scale = 1;

        } else {
            item.style.scale = 0.5;
        }
    });
});




function getMoonSetMoonRise(lat,long){
    fetch(`https://api.ipgeolocation.io/astronomy?apiKey=f22c81e7a0b5448e812ad5e0e1c25242&lat=${lat}&long=${long}`)
        .then(response => response.json())
        .then(data => {
            const moonrise = formatTimeMoonRiseMoonSet(data.moonrise);
            const moonset = formatTimeMoonRiseMoonSet(data.moonset);
            document.getElementById('moonrise').textContent = `${moonrise}`;
            document.getElementById('moonset').textContent = `${moonset}`;
        })
    }

function formatTimeMoonRiseMoonSet(time24) {
    let [hours, minutes] = time24.split(':').map(Number);

    minutes = Math.round(minutes);

    if (minutes === 60) {
        minutes = 0;
        hours += 1;
    }

    const period = hours >= 12 ? 'PM' : 'AM';

    hours = hours % 12 || 12;

    minutes = minutes.toString().padStart(2, '0');

    return `${hours}:${minutes} ${period}`;
}

function refreshWeather(){
    const latSend = localStorage.getItem('currentLat')
    const longSend = localStorage.getItem('currentLong')

    getWeather(' ',latSend, longSend)
}


function sendThemeToAndroid(theme) {

    AndroidInterface.updateStatusBarColor(theme);
  };
