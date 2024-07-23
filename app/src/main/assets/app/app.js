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


const Uv_0 = 'A UV index is satisfactory, indicating that there is little or no risk of harm from ultraviolet radiation.';
const Uv_1 = 'Conditions are generally low-risk, indicating that exposure to ultraviolet radiation poses a minimal threat.';
const Uv_2 = 'Low exposure level with minimal risk of harm from UV radiation, suitable for most people.';
const Uv_3 = 'Moderate risk of harm from unprotected sun exposure, protective measures recommended.';
const Uv_4 = 'Moderate risk of harm from unprotected sun exposure, protective measures recommended.';
const Uv_5 = 'Moderate risk of harm from unprotected sun exposure, protective measures recommended.';
const Uv_6 = 'High risk of harm from unprotected sun exposure, protective measures required.';
const Uv_7 = 'High risk of harm from unprotected sun exposure, protective measures required.';
const Uv_8 = 'Very high risk of harm from unprotected sun exposure, extra precautions required.';
const Uv_9 = 'Very high risk of harm from unprotected sun exposure, extra precautions required.';
const Uv_10 = 'Very high risk of harm from unprotected sun exposure, extra precautions required.';
const Uv_11 = 'Extreme risk of harm from unprotected sun exposure, full protection necessary.';
const Uv_12 = 'Extreme risk of harm from unprotected sun exposure, full protection necessary.';
const Uv_13 = 'Extreme risk of harm from unprotected sun exposure, full protection necessary.';

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

        removeMap()

        setTimeout(() => {
            RenderSearhMap()
        }, 400);
    });

    closeButton.addEventListener('click', () => {

        window.history.back()
    });




    cityInput.addEventListener('input', () => {
        const searchTerm = cityInput.value.trim();
        document.querySelector('.currentLocationdiv').hidden = true;
        document.querySelector('.full_Wrap_map').hidden = true;

        if (searchTerm) {
            document.getElementById('cityLoader').hidden = false;
            getCitySuggestions(cityInput.value);
        } else {
            cityList.innerHTML = '';
            document.getElementById('cityLoader').hidden = true;
            document.querySelector('.currentLocationdiv').hidden = false;
            document.querySelector('.full_Wrap_map').hidden = false;

            setTimeout(() => {
                document.getElementById('city-input').dispatchEvent(new Event('input'));
            }, 300);

        }
    });


    cityInput.addEventListener('keypress', (event) => {
        const searchTerm = cityInput.value.trim();
        document.querySelector('.currentLocationdiv').hidden = true;

        if (event.key === 'Enter') {
            if (searchTerm) {
                document.getElementById('cityLoader').hidden = false;
                getCitySuggestions(cityInput.value);
            } else {
                cityList.innerHTML = '';
                document.getElementById('cityLoader').hidden = true;
                document.querySelector('.currentLocationdiv').hidden = false;


            }
        }
    });


    const apiKeyGeo = '7147cfac7299479da122684c73d9b80a';

    async function getCitySuggestions(query) {
        const response = await fetch(`https://api.opencagedata.com/geocode/v1/json?q=${query}&key=${apiKeyGeo}&limit=5`);
        const data = await response.json();
        displaySuggestions(data.results);
    }

    function displaySuggestions(results) {

        const suggestionsContainer = document.getElementById('city-list');
        clearSuggestions()

        results.forEach(result => {
            const city = result.components.city || result.components.town || result.components.village || result.components.hamlet;
            const state = result.components.state || result.components.region;
            const country = result.components.country;

            const uniqueComponents = [city, state, country].filter((value, index, self) => value && self.indexOf(value) === index);
            const suggestionText = uniqueComponents.join(', ');

            const suggestionItem = document.createElement('div');

            suggestionItem.classList.add('suggestion-item');
            const suggestRipple = document.createElement('md-ripple');
            suggestRipple.style = '--md-ripple-pressed-opacity: 0.1;'
            suggestionItem.textContent = suggestionText;
            suggestionItem.appendChild(suggestRipple)
            suggestionItem.setAttribute('data-lat', result.geometry.lat);
            suggestionItem.setAttribute('data-lon', result.geometry.lng);
            suggestionItem.addEventListener('click', function () {
                getWeather(city, result.geometry.lat, result.geometry.lng)

                cityList.innerHTML = '';
                cityInput.value = '';
                document.getElementById('city-name').innerHTML = '<md-circular-progress indeterminate style="--md-circular-progress-size: 30px;"></md-circular-progress>'
                document.querySelector('.focus-input').blur();
                document.getElementById('forecast').scrollLeft = 0;
                document.getElementById('weather_wrap').scrollTop = 0;
                window.history.back()

                setTimeout(() => {
                    cityInput.dispatchEvent(new Event('input'));
                }, 200);
            });
            suggestionsContainer.appendChild(suggestionItem);
            setTimeout(() => {
                document.getElementById('cityLoader').hidden = true;

            }, 400);
        });
    }


    function clearSuggestions() {
        const suggestionsContainer = document.getElementById('city-list');
        while (suggestionsContainer.firstChild) {
            suggestionsContainer.removeChild(suggestionsContainer.firstChild);
        }
    }

});



window.addEventListener('popstate', function (event) {
    document.getElementById('search-container').style.opacity = '0'

    setTimeout(() => {
        document.getElementById('city-input').value = ''
        document.getElementById('city-input').dispatchEvent(new Event('input'));
        document.getElementById('search-container').style.display = 'none'
        document.getElementById('search-container').style.opacity = '1'

    }, 300);

});


function fetchCitySuggestions(searchTerm) {
    const query = encodeURIComponent(searchTerm);
    const apiKey = '120d979ba5b2d0780f51872890f5ad0b';

    return fetch(`https://api.openweathermap.org/geo/1.0/direct?q=${query}&limit=5&appid=${apiKey}`)
        .then(response => response.json())
        .then(data => data.map(city => {
            const cityName = city.name;
            const stateOrCountry = city.state || city.country;

           return `<suggestTextContent>${cityName}<span>${stateOrCountry}</span></suggestTextContent>`;

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
            let formattedSuggestion = suggestion.replace(/<\/?suggestTextContent>/g, '');

                        formattedSuggestion = formattedSuggestion.replace(/<\/span>/g, '');
                        formattedSuggestion = formattedSuggestion.replace(/<span>/g, ', ');


                        suggestionItem.setAttribute('Location', formattedSuggestion)

                        suggestionItem.innerHTML = suggestion;
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

function getCountryName(code) {
    return countryNames[code] || "Unknown Country";
}


function getWeather(city, latitude, longitude) {
    showLoader();
    const apiKey = '120d979ba5b2d0780f51872890f5ad0b';
    const apiUrl = `https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&appid=${apiKey}&units=metric`;

    setTimeout(() => {
        updateSunTrackProgress(latitude, longitude);
        Fetchmoonphases(latitude, longitude)
    }, 500);


    localStorage.setItem('currentLong', longitude)
    localStorage.setItem('currentLat', latitude)


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

                        if (data.snow && data.snow['1h']) {
                            document.getElementById('SnowAmount').innerHTML = (`${data.snow['1h'].toFixed(1)} mm`)
                        } else{
                            document.getElementById('SnowAmount').innerHTML = '0.0 mm'
                        }

                        const pressureMain = data.main.pressure;

                        document.getElementById('pressure_text_main').innerHTML = pressureMain + '<span style="color: var(--On-Surface-Variant); font-size: 15px;"> hPa</span> '


                        const minPressure = 870;
            const maxPressure = 1080;
            const maxProgress = 0.8;

            let progressValue = ((pressureMain - minPressure) / (maxPressure - minPressure)) * maxProgress;

            progressValue = Math.max(0, Math.min(progressValue, maxProgress));

            document.querySelector('.pressure_progress_main').setAttribute('value', progressValue);

                        const windDirection = data.wind.deg;

                        setTimeout(() => {
                            document.querySelector('.direction').style.transform = `rotate(${windDirection}deg)`
                        }, 300);

                        const directions = ['North', 'North-East', 'East', 'South-East', 'South', 'South-West', 'West', 'North-West'];
                        const index = Math.round((windDirection % 360) / 45);
                        document.getElementById('directionWind').textContent = directions[index]

            const iconCode = data.weather[0].icon;

            const countryNameText = getCountryName(countryName);

                        const cityLat = data.coord.lat
                        const cityLon = data.coord.lon

            const apiKeyCityName = '7147cfac7299479da122684c73d9b80a';
            const urlcityName = `https://api.opencagedata.com/geocode/v1/json?q=${cityLat}+${cityLon}&key=${apiKeyCityName}`;

            fetch(urlcityName)
                .then(response => response.json())
                .then(data => {
                    if (data.results.length > 0) {
                        const components = data.results[0].components;
                        const city = components.city || components.town || components.village || 'Unknown';
                        document.getElementById('city-name').innerHTML = `${city}, ${countryNameText}`;
                        document.getElementById('currentLocationName').textContent = `${city}, ${countryNameText}`;
                    } else {
                        console.log('No results found')

                    }
                })

            if(SelectedTempUnit === 'fahrenheit'){
                document.getElementById('temp').innerHTML = `${tempF}<span>°F</span>`;
         document.getElementById('temPDiscCurrentLocation').innerHTML = `${tempF}°F • <span>${description}</span>`
             document.getElementById('willFeelLike').innerHTML = ` ${Math.round(feelslikeF + 3)}°F`

            } else{
            document.getElementById('temp').innerHTML = `${temperature}<span>°C</span>`;
             document.getElementById('temPDiscCurrentLocation').innerHTML = `${temperature}°C • <span>${description}</span>`

                 document.getElementById('willFeelLike').innerHTML = ` ${Math.round(feelslike + 3)}°C`

            }

                        if(temperature > 32){
                            document.querySelector('.excessiveHeat').hidden = false;
                        } else{
                            document.querySelector('.excessiveHeat').hidden = true;
                        }




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
                document.getElementById('min-temp').innerHTML = `${(visibility / 1000).toFixed(0)} km`;

            }

                        let visibilityInKm = visibility / 1000;
                        let maxVisibility = 10;

                        let visibilityPercentage = Math.min(visibilityInKm / maxVisibility, 1);

                        document.querySelector('.md-circle01').setAttribute('value', visibilityPercentage.toString());


            document.getElementById('sunrise').textContent = sunrise;
            document.getElementById('sunset').textContent = sunset;
            document.getElementById('humidity').textContent = `${humidity}%`;
            document.getElementById('clouds').textContent = `${clouds}%`;
            document.querySelector('humidityBarProgress').style.height = `${humidity}%`;


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

                        const windspeedType = document.getElementById('windtype');

                            if (windSpeedKPH < 1) {
                                windspeedType.innerHTML = "Calm";
                            } else if (windSpeedKPH < 5) {
                                windspeedType.innerHTML =  "Light air";
                            } else if (windSpeedKPH < 11) {
                                windspeedType.innerHTML =  "Light breeze";
                            } else if (windSpeedKPH < 19) {
                                windspeedType.innerHTML =  "Gentle breeze";
                            } else if (windSpeedKPH < 28) {
                                windspeedType.innerHTML =  "Moderate breeze";
                            } else if (windSpeedKPH < 38) {
                                windspeedType.innerHTML =  "Fresh breeze";
                            } else if (windSpeedKPH < 49) {
                                windspeedType.innerHTML =  "Strong breeze";
                            } else if (windSpeedKPH < 61) {
                                windspeedType.innerHTML =  "High wind";
                            } else if (windSpeedKPH < 74) {
                                windspeedType.innerHTML =  "Gale";
                            } else if (windSpeedKPH < 88) {
                                windspeedType.innerHTML =  "Strong gale";
                            } else if (windSpeedKPH < 102) {
                                windspeedType.innerHTML =  "Storm";
                            } else if (windSpeedKPH < 117) {
                                windspeedType.innerHTML =  "Violent storm";
                            } else {
                                windspeedType.innerHTML =  "Hurricane";
                            }

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
                        1: '#43b710',
                        2: '#eaaf10',
                        3: '#eb8a11',
                        4: '#e83f0f',
                        5: '#8e3acf'
                    }


                    document.getElementById('aqi_img').src = backgroundImage[aqi];
                    document.getElementById('aqi-level').style.backgroundColor = backgroundColor[aqi];
                });

            const url = `https://currentuvindex.com/api/v1/uvi?latitude=${latitude}&longitude=${longitude}`;
            const option = { method: 'GET', headers: { Accept: 'application/json' } };

            fetch(url, option)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    const now = data.now;
                    const uvIndex = now.uvi;


                    if (uvIndex >= 0 && uvIndex <= 1) {
                        document.getElementById('uv-index').innerHTML = 'Minimal risk';
                        document.getElementById('uv-index').style = 'background-color: #43b710';
                        document.getElementById('uv_img').src = 'uv-images/uv-0.png';
                        document.getElementById('detail_uv').innerHTML = Uv_0
                    } else if (uvIndex > 1 && uvIndex <= 2) {
                        document.getElementById('uv-index').innerHTML = 'Low risk';
                        document.getElementById('uv-index').style = 'background-color: #43b710';
                        document.getElementById('uv_img').src = 'uv-images/uv-1.png';
                        document.getElementById('detail_uv').innerHTML = Uv_1
                    } else if (uvIndex > 2 && uvIndex <= 3) {
                        document.getElementById('uv-index').innerHTML = 'Low risk';
                        document.getElementById('uv-index').style = 'background-color: #43b710';
                        document.getElementById('uv_img').src = 'uv-images/uv-2.png';
                        document.getElementById('detail_uv').innerHTML = Uv_2
                    } else if (uvIndex > 3 && uvIndex <= 4) {
                        document.getElementById('uv-index').innerHTML = 'Moderate risk';
                        document.getElementById('uv-index').style = 'background-color: #eaaf10';
                        document.getElementById('uv_img').src = 'uv-images/uv-3.png';
                        document.getElementById('detail_uv').innerHTML = Uv_3
                    } else if (uvIndex > 4 && uvIndex <= 5) {
                        document.getElementById('uv-index').innerHTML = 'Moderate risk';
                        document.getElementById('uv-index').style = 'background-color: #eaaf10';
                        document.getElementById('uv_img').src = 'uv-images/uv-4.png';
                        document.getElementById('detail_uv').innerHTML = Uv_4
                    } else if (uvIndex > 5 && uvIndex <= 6) {
                        document.getElementById('uv-index').innerHTML = 'Moderate risk';
                        document.getElementById('uv-index').style = 'background-color: #eaaf10';
                        document.getElementById('uv_img').src = 'uv-images/uv-5.png';
                        document.getElementById('detail_uv').innerHTML = Uv_5
                    } else if (uvIndex > 6 && uvIndex <= 7) {
                        document.getElementById('uv-index').innerHTML = 'High risk';
                        document.getElementById('uv-index').style = 'background-color: #eb8a11';
                        document.getElementById('uv_img').src = 'uv-images/uv-6.png';
                        document.getElementById('detail_uv').innerHTML = Uv_6
                    } else if (uvIndex > 7 && uvIndex <= 8) {
                        document.getElementById('uv-index').innerHTML = 'High risk';
                        document.getElementById('uv-index').style = 'background-color: #eb8a11';
                        document.getElementById('uv_img').src = 'uv-images/uv-7.png';
                        document.getElementById('detail_uv').innerHTML = Uv_7
                    } else if (uvIndex > 8 && uvIndex <= 9) {
                        document.getElementById('uv-index').innerHTML = 'Very high risk';
                        document.getElementById('uv-index').style = 'background-color: #e83f0f';
                        document.getElementById('uv_img').src = 'uv-images/uv-8.png';
                        document.getElementById('detail_uv').innerHTML = Uv_8
                    } else if (uvIndex > 9 && uvIndex <= 10) {
                        document.getElementById('uv-index').innerHTML = 'Very high risk';
                        document.getElementById('uv-index').style = 'background-color: #e83f0f';
                        document.getElementById('uv_img').src = 'uv-images/uv-9.png';
                        document.getElementById('detail_uv').innerHTML = Uv_9
                    } else if (uvIndex > 10 && uvIndex <= 11) {
                        document.getElementById('uv-index').innerHTML = 'Very high risk';
                        document.getElementById('uv-index').style = 'background-color: #e83f0f';
                        document.getElementById('uv_img').src = 'uv-images/uv-10.png';
                        document.getElementById('detail_uv').innerHTML = Uv_10
                    } else if (uvIndex > 11 && uvIndex <= 12) {
                        document.getElementById('uv-index').innerHTML = 'Extreme risk';
                        document.getElementById('uv-index').style = 'background-color: #8e3acf';
                        document.getElementById('uv_img').src = 'uv-images/uv-11.png';
                        document.getElementById('detail_uv').innerHTML = Uv_11
                    } else if (uvIndex > 12 && uvIndex <= 13) {
                        document.getElementById('uv-index').innerHTML = 'Extreme risk';
                        document.getElementById('uv-index').style = 'background-color: #ec0c8b';
                        document.getElementById('uv_img').src = 'uv-images/uv-12.png';
                        document.getElementById('detail_uv').innerHTML = Uv_12
                    } else if (uvIndex > 13) {
                        document.getElementById('uv-index').innerHTML = 'Extreme risk';
                        document.getElementById('uv-index').style = 'background-color: #550ef9';
                        document.getElementById('uv_img').src = 'uv-images/uv-13.png';
                        document.getElementById('detail_uv').innerHTML = Uv_13
                    }


                })



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


    setTimeout(() => {
                Fetchmoonphases(latitude, longitude)
        updateSunTrackProgress(latitude, longitude);
    }, 500);


        localStorage.setItem('currentLong', longitude)
        localStorage.setItem('currentLat', latitude)


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

            const countryNameText = getCountryName(countryName);

            const cityLat = data.coord.lat
            const cityLon = data.coord.lon

            const apiKeyCityName = '7147cfac7299479da122684c73d9b80a';
            const urlcityName = `https://api.opencagedata.com/geocode/v1/json?q=${cityLat}+${cityLon}&key=${apiKeyCityName}`;

            fetch(urlcityName)
                .then(response => response.json())
                .then(data => {
                    if (data.results.length > 0) {
                        const components = data.results[0].components;
                        const city = components.city || components.town || components.village || 'Unknown';
                        document.getElementById('city-name').innerHTML = `${city}, ${countryNameText}`;
                        document.getElementById('currentLocationName').textContent = `${city}, ${countryNameText}`;
                    } else {
                        console.log('No results found')

                    }
                })

                        if (data.snow && data.snow['1h']) {
                            document.getElementById('SnowAmount').innerHTML = (`${data.snow['1h'].toFixed(1)} mm`)
                        } else{
                            document.getElementById('SnowAmount').innerHTML = '0.0 mm'
                        }

                        const pressureMain = data.main.pressure;

                        document.getElementById('pressure_text_main').innerHTML = pressureMain + '<span style="color: var(--On-Surface-Variant); font-size: 15px;"> hPa</span> '


                        const minPressure = 870;
            const maxPressure = 1080;
            const maxProgress = 0.8;

            let progressValue = ((pressureMain - minPressure) / (maxPressure - minPressure)) * maxProgress;

            progressValue = Math.max(0, Math.min(progressValue, maxProgress));

            document.querySelector('.pressure_progress_main').setAttribute('value', progressValue);

                        const windDirection = data.wind.deg;

                        setTimeout(() => {
                            document.querySelector('.direction').style.transform = `rotate(${windDirection}deg)`
                        }, 300);

                        const directions = ['North', 'North-East', 'East', 'South-East', 'South', 'South-West', 'West', 'North-West'];
                        const index = Math.round((windDirection % 360) / 45);
                        document.getElementById('directionWind').textContent = directions[index]

            if(SelectedTempUnit === 'fahrenheit'){
                document.getElementById('temp').innerHTML = `${tempF}<span>°F</span>`;
             document.getElementById('temPDiscCurrentLocation').innerHTML = `${tempF}°F • <span>${description}</span>`
         document.getElementById('willFeelLike').innerHTML = ` ${Math.round(feelslikeF + 3)}°F`

            } else{
            document.getElementById('temp').innerHTML = `${temperature}<span>°C</span>`;

            document.getElementById('temPDiscCurrentLocation').innerHTML = `${temperature}°C • <span>${description}</span>`
             document.getElementById('willFeelLike').innerHTML = ` ${Math.round(feelslike + 3)}°C`

            }

                        if(temperature > 32){
                            document.querySelector('.excessiveHeat').hidden = false;
                        } else{
                            document.querySelector('.excessiveHeat').hidden = true;
                        }





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
                document.getElementById('min-temp').innerHTML = `${(visibility / 1000).toFixed(0)} km`;
            }



            let visibilityInKm = visibility / 1000;
            let maxVisibility = 10;

            let visibilityPercentage = Math.min(visibilityInKm / maxVisibility, 1);

            document.querySelector('.md-circle01').setAttribute('value', visibilityPercentage.toString());


            document.getElementById('sunrise').textContent = sunrise;
            document.getElementById('sunset').textContent = sunset;

            document.getElementById('humidity').textContent = ` ${humidity}% `;
            document.getElementById('clouds').textContent = `${clouds}%`;
            document.querySelector('humidityBarProgress').style.height = `${humidity}%`;



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



                    const windspeedType = document.getElementById('windtype');

                        if (windSpeedKPH < 1) {
                            windspeedType.innerHTML = "Calm";
                        } else if (windSpeedKPH < 5) {
                            windspeedType.innerHTML =  "Light air";
                        } else if (windSpeedKPH < 11) {
                            windspeedType.innerHTML =  "Light breeze";
                        } else if (windSpeedKPH < 19) {
                            windspeedType.innerHTML =  "Gentle breeze";
                        } else if (windSpeedKPH < 28) {
                            windspeedType.innerHTML =  "Moderate breeze";
                        } else if (windSpeedKPH < 38) {
                            windspeedType.innerHTML =  "Fresh breeze";
                        } else if (windSpeedKPH < 49) {
                            windspeedType.innerHTML =  "Strong breeze";
                        } else if (windSpeedKPH < 61) {
                            windspeedType.innerHTML =  "High wind";
                        } else if (windSpeedKPH < 74) {
                            windspeedType.innerHTML =  "Gale";
                        } else if (windSpeedKPH < 88) {
                            windspeedType.innerHTML =  "Strong gale";
                        } else if (windSpeedKPH < 102) {
                            windspeedType.innerHTML =  "Storm";
                        } else if (windSpeedKPH < 117) {
                            windspeedType.innerHTML =  "Violent storm";
                        } else {
                            windspeedType.innerHTML =  "Hurricane";
                        }


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
                        1: '#43b710',
                        2: '#eaaf10',
                        3: '#eb8a11',
                        4: '#e83f0f',
                        5: '#8e3acf'
                    }


                    document.getElementById('aqi_img').src = backgroundImage[aqi];
                    document.getElementById('aqi-level').style.backgroundColor = backgroundColor[aqi];
                })

            const url = `https://currentuvindex.com/api/v1/uvi?latitude=${latitude}&longitude=${longitude}`;
            const option = { method: 'GET', headers: { Accept: 'application/json' } };

            fetch(url, option)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    const now = data.now;
                    const uvIndex = now.uvi;


                    if (uvIndex >= 0 && uvIndex <= 1) {
                        document.getElementById('uv-index').innerHTML = 'Minimal risk';
                        document.getElementById('uv-index').style = 'background-color: #43b710';
                        document.getElementById('uv_img').src = 'uv-images/uv-0.png';
                        document.getElementById('detail_uv').innerHTML = Uv_0
                    } else if (uvIndex > 1 && uvIndex <= 2) {
                        document.getElementById('uv-index').innerHTML = 'Low risk';
                        document.getElementById('uv-index').style = 'background-color: #43b710';
                        document.getElementById('uv_img').src = 'uv-images/uv-1.png';
                        document.getElementById('detail_uv').innerHTML = Uv_1
                    } else if (uvIndex > 2 && uvIndex <= 3) {
                        document.getElementById('uv-index').innerHTML = 'Low risk';
                        document.getElementById('uv-index').style = 'background-color: #43b710';
                        document.getElementById('uv_img').src = 'uv-images/uv-2.png';
                        document.getElementById('detail_uv').innerHTML = Uv_2
                    } else if (uvIndex > 3 && uvIndex <= 4) {
                        document.getElementById('uv-index').innerHTML = 'Moderate risk';
                        document.getElementById('uv-index').style = 'background-color: #eaaf10';
                        document.getElementById('uv_img').src = 'uv-images/uv-3.png';
                        document.getElementById('detail_uv').innerHTML = Uv_3
                    } else if (uvIndex > 4 && uvIndex <= 5) {
                        document.getElementById('uv-index').innerHTML = 'Moderate risk';
                        document.getElementById('uv-index').style = 'background-color: #eaaf10';
                        document.getElementById('uv_img').src = 'uv-images/uv-4.png';
                        document.getElementById('detail_uv').innerHTML = Uv_4
                    } else if (uvIndex > 5 && uvIndex <= 6) {
                        document.getElementById('uv-index').innerHTML = 'Moderate risk';
                        document.getElementById('uv-index').style = 'background-color: #eaaf10';
                        document.getElementById('uv_img').src = 'uv-images/uv-5.png';
                        document.getElementById('detail_uv').innerHTML = Uv_5
                    } else if (uvIndex > 6 && uvIndex <= 7) {
                        document.getElementById('uv-index').innerHTML = 'High risk';
                        document.getElementById('uv-index').style = 'background-color: #eb8a11';
                        document.getElementById('uv_img').src = 'uv-images/uv-6.png';
                        document.getElementById('detail_uv').innerHTML = Uv_6
                    } else if (uvIndex > 7 && uvIndex <= 8) {
                        document.getElementById('uv-index').innerHTML = 'High risk';
                        document.getElementById('uv-index').style = 'background-color: #eb8a11';
                        document.getElementById('uv_img').src = 'uv-images/uv-7.png';
                        document.getElementById('detail_uv').innerHTML = Uv_7
                    } else if (uvIndex > 8 && uvIndex <= 9) {
                        document.getElementById('uv-index').innerHTML = 'Very high risk';
                        document.getElementById('uv-index').style = 'background-color: #e83f0f';
                        document.getElementById('uv_img').src = 'uv-images/uv-8.png';
                        document.getElementById('detail_uv').innerHTML = Uv_8
                    } else if (uvIndex > 9 && uvIndex <= 10) {
                        document.getElementById('uv-index').innerHTML = 'Very high risk';
                        document.getElementById('uv-index').style = 'background-color: #e83f0f';
                        document.getElementById('uv_img').src = 'uv-images/uv-9.png';
                        document.getElementById('detail_uv').innerHTML = Uv_9
                    } else if (uvIndex > 10 && uvIndex <= 11) {
                        document.getElementById('uv-index').innerHTML = 'Very high risk';
                        document.getElementById('uv-index').style = 'background-color: #e83f0f';
                        document.getElementById('uv_img').src = 'uv-images/uv-10.png';
                        document.getElementById('detail_uv').innerHTML = Uv_10
                    } else if (uvIndex > 11 && uvIndex <= 12) {
                        document.getElementById('uv-index').innerHTML = 'Extreme risk';
                        document.getElementById('uv-index').style = 'background-color: #8e3acf';
                        document.getElementById('uv_img').src = 'uv-images/uv-11.png';
                        document.getElementById('detail_uv').innerHTML = Uv_11
                    } else if (uvIndex > 12 && uvIndex <= 13) {
                        document.getElementById('uv-index').innerHTML = 'Extreme risk';
                        document.getElementById('uv-index').style = 'background-color: #ec0c8b';
                        document.getElementById('uv_img').src = 'uv-images/uv-12.png';
                        document.getElementById('detail_uv').innerHTML = Uv_12
                    } else if (uvIndex > 13) {
                        document.getElementById('uv-index').innerHTML = 'Extreme risk';
                        document.getElementById('uv-index').style = 'background-color: #550ef9';
                        document.getElementById('uv_img').src = 'uv-images/uv-13.png';
                        document.getElementById('detail_uv').innerHTML = Uv_13
                    }


                })



        })
          .catch(error => {
                    console.error('Error fetching current weather:', error);
                    document.querySelector('.no_internet_error').hidden = false;
                });

    currentLocation = {
        latitude,
        longitude
    };




}



function updateSunTrackProgress(latitude, longitude) {
    const apiKey = '120d979ba5b2d0780f51872890f5ad0b';
    const apiUrl = `https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&appid=${apiKey}&units=metric`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const sunriseUTC = new Date(data.sys.sunrise * 1000);
            const sunsetUTC = new Date(data.sys.sunset * 1000);
            const currentTime = new Date();

            const totalDaylight = sunsetUTC - sunriseUTC;

            const timeSinceSunrise = currentTime - sunriseUTC;

            let percentageOfDaylight = (timeSinceSunrise / totalDaylight) * 100;

            percentageOfDaylight = Math.min(Math.max(percentageOfDaylight, 0), 100);

            document.querySelector('suntrackprogress').style.width = `${percentageOfDaylight}%`;


        })
        .catch(error => {
            console.error('Error fetching sunrise/sunset data:', error);
        });
}




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
        const RainBarsContainer = document.querySelector('rainMeterBar');

    forecastContainer.innerHTML = '';
    RainBarsContainer.innerHTML = '';


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
            const rainMeterBarItem = document.createElement('rainMeterBarItem');


            const forecastItem = document.createElement('div');
            forecastItem.classList.add('forecast-item');
            forecastItem.id = "forecast24";

            if(SelectedTempUnit === 'fahrenheit'){
                forecastItem.innerHTML = `
                <p class="time-24">${time}</p>
                <img id="icon-24" src="weather-icons/${iconCode}.svg" alt="Weather Icon" class="icon-24">
                <p class="temp-24">${tempF}°F</p>

                 <p class="disc_sml-24" >${description}</p>
                  <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>

            `;
            } else{
            forecastItem.innerHTML = `
            <p class="time-24">${time}</p>
            <img id="icon-24" src="weather-icons/${iconCode}.svg" alt="Weather Icon" class="icon-24">
            <p class="temp-24">${temperature}°C</p>

             <p class="disc_sml-24" >${description}</p>
              <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>

        `;
            }

            rainMeterBarItem.innerHTML = `
                <rainPerBar>
                  <rainPerBarProgress style="height: ${Math.round(rainPercentage)}%;">
                </rainPerBarProgress>
                </rainPerBar>
                <p>${Math.round(rainPercentage)}%</p>
                 <span>${time}</span>


            `

            forecastItem.addEventListener('click', ()=>{
                ShowSnack(`<span style="text-transform: capitalize;">${description}</span>`, 2000, 3, 'none', ' ', 'no-up')

            });


            RainBarsContainer.append(rainMeterBarItem)
            

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



        const minTemp = -70;
        const maxTemp = 60;
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
                document.querySelector('rainmeterbar').scrollLeft = 0

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



function refreshWeather(){
    const latSend = localStorage.getItem('currentLat')
    const longSend = localStorage.getItem('currentLong')

    getWeather(' ',latSend, longSend)
}


function sendThemeToAndroid(theme) {

    AndroidInterface.updateStatusBarColor(theme);
  };
function Toast(toastText, time){
    ToastAndroidShow.ShowToast(toastText, time);
}


// map

var map;

function RenderSearhMap() {
    const latDif = localStorage.getItem('currentLat');
    const longDif = localStorage.getItem('currentLong');

    map = window.L.map('map', {
        center: [latDif, longDif],
        zoom: 13,
        zoomControl: false

    });

    window.L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}.png', {}).addTo(map);

    var marker = window.L.marker([latDif, longDif]).addTo(map);

    map.on('click', function(e) {
        var lat = e.latlng.lat;
        var lon = e.latlng.lng;

        marker.setLatLng([lat, lon]);
        window.history.back();
        getWeather(' ', lat, lon);

        document.getElementById('city-name').innerHTML = '<md-circular-progress indeterminate style="--md-circular-progress-size: 30px;"></md-circular-progress>';
        document.getElementById('forecast').scrollLeft = 0;
        document.getElementById('weather_wrap').scrollTop = 0;
    });
}

function removeMap() {
    if (map) {
        map.remove();
        map = null;
    }
}

var livemap;

function liveMapRender(maptype){

    const latDif = localStorage.getItem('currentLat');
    const longDif = localStorage.getItem('currentLong');

    livemap = window.L.map('livemap', {
        center: [latDif, longDif],
        zoom: 4,
        minZoom: 3,
        zoomControl: false
    });


 window.L.marker([latDif, longDif]).addTo(livemap);


    const apiKey = '9458a8b672d3e5ed460b72e7637c6eeb';

    window.L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}.png', {}).addTo(livemap);


    window.L.tileLayer(`https://tile.openweathermap.org/map/${maptype}/{z}/{x}/{y}.png?appid=${apiKey}`).addTo(livemap);




}


function removeLiveMap() {
    if (livemap) {
        livemap.remove();
        livemap = null;
    }
}








function Open_RefreshLiveMap(type){
        removeLiveMap()

    setTimeout(()=>{
        liveMapRender(type)
    }, 500);

}




function toggleMapTypeChips(element) {
    if (element.selected) {
    var passChips = document.getElementsByName('MapType');
    passChips.forEach((passChip) => {
        if (passChip !== element) {
            passChip.selected = false;
        }

    });
    } else{
        element.selected = true;
    }
}


function openLivemap(){
    document.querySelector('.liveMapScreen').hidden = false;
    window.history.pushState({ LiveMapOpen: true }, "");

        var passChips = document.getElementsByName('MapType');
        passChips.forEach((passChip) => {
            passChip.selected = false;
        });
        document.querySelector('[label="Temperature"]').selected = true;
}

function closeLiveMap(){
    document.querySelector('.liveMapScreen').style.height = '0'
    document.querySelector('.liveMapScreen').style.opacity = '0'


    setTimeout(()=>{
        document.querySelector('.liveMapScreen').hidden = true;
    document.querySelector('.liveMapScreen').style.height = ''
    document.querySelector('.liveMapScreen').style.opacity = ''

    }, 350);

}


window.addEventListener('popstate', function (event) {
    if(!document.querySelector('.liveMapScreen').hidden){
        closeLiveMap()
    }

});

function refreshCurrentMap(){
if(document.querySelector('[label="Rain"]').selected){
    Open_RefreshLiveMap('rain')
} else if(document.querySelector('[label="Clouds"]').selected){
    Open_RefreshLiveMap('clouds')
} else if(document.querySelector('[label="Temperature"]').selected){
    Open_RefreshLiveMap('temp_new')

} else if(document.querySelector('[label="Snow"]').selected){
    Open_RefreshLiveMap('snow')

} else if(document.querySelector('[label="Wind"]').selected){
    Open_RefreshLiveMap('wind_new')

}

}


function Fetchmoonphases(lat, long){
    fetch(`https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${lat},${long}?unitGroup=metric&include=days&key=4KMES28T2K3PXBCXTNYZCZFW8&contentType=json`)
    .then(response => response.json())
    .then(data => {

const moon_phase_img = document.getElementById('moon_phase_img');
        const moonPhaseText = document.getElementById('moonPhaseText');
        const moonPhaseDaysLeft = document.getElementById('moonPhaseDaysLeft');
        const moonPhase = data.days[0].moonphase;

        let phaseName = '';
        let phaseImg = '';
        let daysLeft = 0;

        if (moonPhase === 0) {
            phaseName = 'New Moon';
            phaseImg = 'moon_phases/moon_new.svg';
            daysLeft = Math.round((0.25 - moonPhase) * 29.53);
        } else if (moonPhase > 0 && moonPhase < 0.25) {
            phaseName = 'Waxing Crescent';
            phaseImg = 'moon_phases/moon_waxing_crescent.svg';
            daysLeft = Math.round((0.25 - moonPhase) * 29.53);
        } else if (moonPhase === 0.25) {
            phaseName = 'First Quarter';
            phaseImg = 'moon_phases/moon_first_quarter.svg';
            daysLeft = Math.round((0.5 - moonPhase) * 29.53);
        } else if (moonPhase > 0.25 && moonPhase < 0.5) {
            phaseName = 'Waxing Gibbous';
            phaseImg = 'moon_phases/moon_waxing_gibbous.svg';
            daysLeft = Math.round((0.5 - moonPhase) * 29.53);
        } else if (moonPhase === 0.5) {
            phaseName = 'Full Moon';
            phaseImg = 'moon_phases/moon_full.svg';
            daysLeft = Math.round((0.75 - moonPhase) * 29.53);
        } else if (moonPhase > 0.5 && moonPhase < 0.75) {
            phaseName = 'Waning Gibbous';
            phaseImg = 'moon_phases/moon_waning_gibbous.svg';
            daysLeft = Math.round((0.75 - moonPhase) * 29.53);
        } else if (moonPhase === 0.75) {
            phaseName = 'Last Quarter';
            phaseImg = 'moon_phases/moon_last_quarter.svg';
            daysLeft = Math.round((1 - moonPhase) * 29.53);
        } else if (moonPhase > 0.75 && moonPhase < 1) {
            phaseName = 'Waning Crescent';
            phaseImg = 'moon_phases/moon_waning_crescent.svg';
            daysLeft = Math.round((1 - moonPhase) * 29.53);
        }

        moonPhaseText.innerHTML = phaseName;
        moon_phase_img.src = phaseImg;
        moonPhaseDaysLeft.innerHTML = `${daysLeft} days`;

            document.getElementById('weatherComments').innerHTML = data.days[0].description



        const severerisk = data.days[0].severerisk

        const riskFill = document.getElementById('riskFill');
        const riskDescription = document.getElementById('severeriskText')


        if (severerisk > 90) {
            riskFill.style.color = '#b30000';
            riskDescription.innerHTML = 'Extremely High Risk: Catastrophic weather conditions expected';
        } else if (severerisk > 75) {
            riskFill.style.color = '#ff0000';
            riskDescription.innerHTML = 'Very High Risk: Extreme weather conditions expected';
        } else if (severerisk > 50) {
            riskFill.style.color = '#ff4d4d';
            riskDescription.innerHTML = 'High Risk: Severe weather likely ';
        } else if (severerisk > 30) {
            riskFill.style.color = '#ff9999';
            riskDescription.innerHTML = 'Moderate Risk: Potential weather issues';
        } else if (severerisk > 10) {
            riskFill.style.color = '#ffff99';
            riskDescription.innerHTML = 'Low to Moderate Risk: Minor weather impacts';
        } else {
            riskFill.style.color = '#ccffcc';
            riskDescription.innerHTML = 'No weather risks';
        }


                document.getElementById('AmountRainMM').innerHTML = data.days[0].precip + ' mm'

                document.getElementById('RainCoverage').innerHTML = Math.round(data.days[0].precipcover) + '%';


                    hideLoader()

    })

    .catch(error => console.error(error));



    fetch(`https://api.ipgeolocation.io/astronomy?apiKey=63a7210d2b104646a1099d5ba223d221&lat=${lat}8&long=${long}`)
.then(response => response.json())
.then(data => {
    const moonRise = data.moonrise;
    const moonSet = data.moonset;

    const moonRiseAmPm = convertToAmPm(moonRise);
    const moonSetAmPm = convertToAmPm(moonSet);


    document.getElementById('moonriseText').innerHTML = moonRiseAmPm;
    document.getElementById('moonsetText').innerHTML = moonSetAmPm;


    const dayLength = data.day_length;
    const [hours, minutes, seconds] = dayLength.split(':');
    const formattedDayLength = `${parseInt(hours)} hours ${parseInt(minutes)} mins`;


        document.getElementById('DayLengthText').innerHTML = formattedDayLength;

})
.catch(error => console.error(error));


}

function convertToAmPm(time) {
    let [hour, minute] = time.split(':');
    hour = parseInt(hour);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    hour = hour % 12;
    hour = hour ? hour : 12;
    return `${hour}:${minute} ${ampm}`;
}

document.querySelector('rainMeterBar').addEventListener('scroll', function () {
    var items = document.querySelectorAll('rainmeterbaritem');
    var scrollPosition = document.querySelector('rainMeterBar').scrollLeft;
    var windowWidth = document.querySelector('rainMeterBar').offsetWidth;

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