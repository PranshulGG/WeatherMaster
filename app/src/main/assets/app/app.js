const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
const SelectedWindUnit = localStorage.getItem('SelectedWindUnit');
const SelectedVisibiltyUnit = localStorage.getItem('selectedVisibilityUnit');
const SelectedPrecipitationUnit = localStorage.getItem('selectedPrecipitationUnit');
const SelectedPressureUnit = localStorage.getItem('selectedPressureUnit');
const DefaultLocation = JSON.parse(localStorage.getItem('DefaultLocation'));




function openSummery(){
    document.querySelector('.summeryText').classList.toggle('openSummery');

    if(document.querySelector('.summeryText').classList.contains('openSummery')){
        document.querySelector('#arrow_up_toggle').innerHTML = 'keyboard_arrow_up'

    } else{
        document.querySelector('#arrow_up_toggle').innerHTML = 'keyboard_arrow_down';

    }
}


let currentApiKeyIndex = 0;

let currentKeyMoonIndex = 0;
let currentAstronomyKeyIndex = 0;


function handleStorageChange(event) {
    if (event.key === 'SelectedTempUnit' ||
        event.key === 'SelectedWindUnit' ||
        event.key === 'selectedVisibilityUnit' ||
        event.key === 'selectedPrecipitationUnit' ||
        event.key === 'DefaultLocation'||
        event.key === 'selectedPressureUnit') {

            setTimeout(()=>{
                window.location.reload();
            }, 1500);

    }
}

window.addEventListener('storage', handleStorageChange);



let anim = null;

function ShowError() {

    document.querySelector('.no_internet_error').hidden = false;

    if (anim) {
        return;
    }

    var animationContainer = document.getElementById('error_img_cat');
    var animationData = 'icons/error-cat.json';

    anim = bodymovin.loadAnimation({
        container: animationContainer,
        renderer: 'svg',
        loop: true,
        autoplay: true,
        path: animationData
    });

}


let currentLocation = null;

function useAutoCurrentLocation(){
    showLoader();
    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition((position) => {
            currentLocation = {
                latitude: position.coords.latitude,
                longitude: position.coords.longitude
            };
            getWeatherByCoordinates(currentLocation.latitude, currentLocation.longitude);
                        setCurrentLocation(currentLocation.latitude, currentLocation.longitude);

        }, (error) => {
            console.error('Geolocation error:', error);
            document.getElementById('search-container').style.display = 'block';
            window.history.pushState({ SearchContainerOpen: true }, "");
        });
    } else {
        console.error('Geolocation is not available in this browser.');

    }
}


if(DefaultLocation){
if(DefaultLocation.name === 'CurrentDeviceLocation'){
    useAutoCurrentLocation()
    sendThemeToAndroid("ReqLocation")
    document.querySelector('.currentLocationdiv').hidden = false;
} else if(DefaultLocation.lat && DefaultLocation.lon){
    getWeather(' ', DefaultLocation.lat, DefaultLocation.lon)
    document.querySelector('.currentLocationdiv').hidden = true;
}
}
else{
    useAutoCurrentLocation()
    sendThemeToAndroid("ReqLocation")
    document.querySelector('.currentLocationdiv').hidden = false;
}





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
    const openMapPicker = document.getElementById('openMapPicker');

    cityopen.addEventListener("click", () => {
  let savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];


        searchContainer.style.display = 'block';
        window.history.pushState({ SearchContainerOpen: true }, "");
         document.querySelector('.header_hold').style.transform = 'scale(1.1)';
                document.querySelector('.header_hold').style.opacity = '0';

        setTimeout(() => {
                                    document.querySelector('.header_hold').style.transform = '';
                                    document.querySelector('.header_hold').style.opacity = '';


            if (savedLocations.length === 0) {
                cityInput.focus()


            } else {


            }

        }, 400);
    });

    closeButton.addEventListener('click', () => {

        window.history.back()
    });


    openMapPicker.addEventListener('click', () => {
        document.querySelector('.map_picker').hidden = false;
        window.history.pushState({ MapPickerContainerOpen: true }, "");

        removeMap()

                setTimeout(() => {
                    RenderSearhMap()
                }, 400);
    });


    cityInput.addEventListener('input', () => {
        const searchTerm = cityInput.value.trim();
        document.querySelector('.currentLocationdiv').hidden = true;
        document.querySelector('.savedLocations').hidden = true;

        if (searchTerm) {
            document.getElementById('cityLoader').hidden = false;
                        setTimeout(()=>{
                            getCitySuggestions(cityInput.value);
                        }, 500);
        } else {
            cityList.innerHTML = '';
            document.getElementById('cityLoader').hidden = true;
            document.querySelector('.currentLocationdiv').hidden = false;
            document.querySelector('.savedLocations').hidden = false;


                        setTimeout(()=>{
                        cityList.innerHTML = '';
                        }, 200)

                        setTimeout(()=>{
                            cityList.innerHTML = '';
                            }, 400)

                            setTimeout(()=>{
                                cityList.innerHTML = '';
                                }, 600)


            let savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];

            if (savedLocations.length === 0) {
                document.querySelector('.savedLocations').hidden = true;
            } else {
                document.querySelector('.savedLocations').hidden = false;

            }


        }
    });


    cityInput.addEventListener('keypress', (event) => {
        const searchTerm = cityInput.value.trim();
        document.querySelector('.currentLocationdiv').hidden = true;

        if (event.key === 'Enter') {
            if (searchTerm) {
                document.getElementById('cityLoader').hidden = false;
                            setTimeout(()=>{
                                getCitySuggestions(cityInput.value);
                            }, 500);
            } else {
                cityList.innerHTML = '';
                document.getElementById('cityLoader').hidden = true;
                document.querySelector('.currentLocationdiv').hidden = false;


            }
        }
    });


let currentApiKeyGeoIndex = 0;

    async function getCitySuggestions(query) {
        const apiKeyGeo = apiKeysGeo[currentApiKeyGeoIndex];
        const url = `https://api.opencagedata.com/geocode/v1/json?q=${query}&key=${apiKeyGeo}&limit=5`;

        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            displaySuggestions(data.results);
        } catch (error) {
            console.error('Error fetching city suggestions:', error);

            if (currentApiKeyGeoIndex < apiKeysGeo.length - 1) {
                currentApiKeyGeoIndex++;
                console.log(`Switching to API key index ${currentApiKeyGeoIndex} for city suggestions`);
                getCitySuggestions(query);
            } else {
                console.error('All geo API keys failed. Unable to fetch data.');

            }
        }
    }
    function displaySuggestions(results) {

        const suggestionsContainer = document.getElementById('city-list');
        clearSuggestions()

        const displayedSuggestions = new Set();

        const savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];
        const savedLocationsSet = new Set(savedLocations.map(location => location.locationName));


        results.forEach(result => {
            const city = result.components.city || result.components.town || result.components.village || result.components.hamlet;
            const state = result.components.state || result.components.region;
            const country = result.components.country;
            const countryCode = result.components.country_code;

            const uniqueComponents = [city, state, country].filter((value, index, self) => value && self.indexOf(value) === index);
            const suggestionText = uniqueComponents.join(', ');


  if (!displayedSuggestions.has(suggestionText)) {
                        displayedSuggestions.add(suggestionText);


            const suggestionItem = document.createElement('div');

                            const clickLocation = document.createElement('savelocationtouch')


                            const saveBtnLocation = document.createElement('md-text-button');


                            saveBtnLocation.textContent = 'Save';

                            if (!savedLocationsSet.has(suggestionText)) {
                                saveBtnLocation.hidden = false;
                            } else {
                                saveBtnLocation.hidden = true;

                            }


                            const createGap = document.createElement('flex');

                        const countryIcon = document.createElement('span');
                        countryIcon.classList.add('fi', 'fis', `fi-${countryCode}`)


            suggestionItem.classList.add('suggestion-item');
            const suggestRipple = document.createElement('md-ripple');
            suggestRipple.style = '--md-ripple-pressed-opacity: 0.1;'
            suggestionItem.textContent = suggestionText;
            clickLocation.appendChild(suggestRipple)
            suggestionItem.setAttribute('data-lat', result.geometry.lat);
            suggestionItem.setAttribute('data-lon', result.geometry.lng);
            clickLocation.addEventListener('click', function () {
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

                            saveBtnLocation.addEventListener('click', () => {


                                saveLocationToContainer(suggestionText, result.geometry.lat, result.geometry.lng)



                                cityList.innerHTML = '';
                                cityInput.value = '';

                                setTimeout(() => {
                                    cityInput.dispatchEvent(new Event('input'));
                                }, 200);
                            });

                suggestionItem.appendChild(countryIcon)
                suggestionItem.appendChild(clickLocation)
                suggestionItem.appendChild(createGap)
                suggestionItem.appendChild(saveBtnLocation);
                suggestionsContainer.appendChild(suggestionItem);
            setTimeout(() => {
                document.getElementById('cityLoader').hidden = true;

            }, 400);
            }
        });
    }


    function clearSuggestions() {
        const suggestionsContainer = document.getElementById('city-list');
        while (suggestionsContainer.firstChild) {
            suggestionsContainer.removeChild(suggestionsContainer.firstChild);
        }
    }

});


function saveLocationToContainer(locationName, lat, lon) {
    // Save to local storage
    let savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];
    savedLocations.push({ locationName, lat, lon });
    localStorage.setItem('savedLocations', JSON.stringify(savedLocations));

    const savedLocationsHolder = document.querySelector('savedLocationsHolder');

    savedLocationsHolder.innerHTML = ''


    setTimeout(() => {
        loadSavedLocations()
    }, 200)

}


function loadSavedLocations() {
    const savedLocationsHolder = document.querySelector('savedLocationsHolder');
    const savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];



    if (savedLocations.length === 0) {
        document.querySelector('.savedLocations').hidden = true;
    } else {
        document.querySelector('.savedLocations').hidden = false;

    }

    savedLocations.forEach(location => {
        const savedLocationItem = document.createElement('savedLocationItem');

        savedLocationItem.setAttribute('lat', location.lat);
        savedLocationItem.setAttribute('lon', location.lon);

        const savedLocationItemLat = savedLocationItem.getAttribute('lat');
        const savedLocationItemLon = savedLocationItem.getAttribute('lon');


        const apiKeySaved = 'CREATE_KEY';
        const apiUrlSaved = `https://api.openweathermap.org/data/2.5/weather?lat=${savedLocationItemLat}&lon=${savedLocationItemLon}&appid=${apiKeySaved}&units=metric`;


        fetch(apiUrlSaved)
            .then(response => response.json())
            .then(data => {
                const currentTemp = Math.round(data.main.temp);
                const currentCondition = data.weather[0].description;
                const currentIcon = data.weather[0].icon;



                    const currentTempF = Math.round(currentTemp * 9 / 5 + 32);


                if (SelectedTempUnit === 'fahrenheit') {
                savedLocationItem.innerHTML = `
            <savedlocationimg>
                <img src="weather-icons/${currentIcon}.svg" alt="">
            </savedlocationimg>
            <div>
                <p>${location.locationName}</p>
                <span>${currentCondition} • ${currentTempF}°F</span>
            </div>
            <flex></flex>
            <md-icon-button class="delete-btn">
                <md-icon icon-filled>delete</md-icon>
            </md-icon-button>
        `;
                } else{
                    savedLocationItem.innerHTML = `
                    <savedlocationimg>
                        <img src="weather-icons/${currentIcon}.svg" alt="">
                    </savedlocationimg>
                    <div>
                        <p>${location.locationName}</p>
                        <span>${currentCondition} • ${currentTemp}°C</span>
                    </div>
                    <flex></flex>
                    <md-icon-button class="delete-btn">
                        <md-icon icon-filled>delete</md-icon>
                    </md-icon-button>
                `;
                }




                savedLocationItem.querySelector('.delete-btn').addEventListener('click', () => {
                    deleteLocation(location.locationName);
                    savedLocationItem.remove();
                });



                const savelocationtouch = document.createElement('savelocationtouch');
                const md_rippleSaveLocationTouch = document.createElement('md-ripple');
                savelocationtouch.appendChild(md_rippleSaveLocationTouch);

                savelocationtouch.addEventListener('click', () => {
                    getWeather(location.locationName, savedLocationItemLat, savedLocationItemLon)
            document.getElementById('city-name').innerHTML = '<md-circular-progress indeterminate style="--md-circular-progress-size: 30px;"></md-circular-progress>'
            document.getElementById('forecast').scrollLeft = 0;
            document.getElementById('weather_wrap').scrollTop = 0;
                    window.history.back();
                });











                savedLocationsHolder.append(savedLocationItem);
                savedLocationItem.appendChild(savelocationtouch);
            });
    });


}

document.addEventListener('DOMContentLoaded', (event) => {
    loadSavedLocations();
});

function deleteLocation(locationName) {
    let savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];

    savedLocations = savedLocations.filter(location => location.locationName !== locationName);

    localStorage.setItem('savedLocations', JSON.stringify(savedLocations));

    if (savedLocations.length === 0) {
        document.querySelector('.savedLocations').hidden = true;
    } else {
        document.querySelector('.savedLocations').hidden = false;

    }
}


function setCurrentLocation(lat, lon){
    const apiKeyCurrent = 'CREATE_KEY';
        const apiUrlCurrent = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKeyCurrent}&units=metric`;


        fetch(apiUrlCurrent)
            .then(response => response.json())
            .then(data => {
                const currentTemp = Math.round(data.main.temp);
                const currentCondition = data.weather[0].description;
                const currentIcon = data.weather[0].icon;
                const tempF = Math.round(currentTemp * 9 / 5 + 32);
                const countryName = data.sys.country;

            const countryNameText = getCountryName(countryName);


            document.getElementById('currentSearchImg').src = `weather-icons/${currentIcon}.svg`;


                if (SelectedTempUnit === 'fahrenheit') {
                document.getElementById('temPDiscCurrentLocation').innerHTML = `${tempF}°F • <span>${currentCondition}</span>`
                } else{
                    document.getElementById('temPDiscCurrentLocation').innerHTML = `${currentTemp}°C • <span>${currentCondition}</span>`

                }



let currentApiKeyCityNameIndex = 0;



                    fetchCityName(lat, lon, countryNameText)



                    function fetchCityName(cityLat, cityLon, countryNameText) {
                        const apiKeyCityName = apiKeysCityName[currentApiKeyCityNameIndex];
                        const urlcityName = `https://api.opencagedata.com/geocode/v1/json?q=${cityLat}+${cityLon}&key=${apiKeyCityName}`;

                        fetch(urlcityName)
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                return response.json();
                            })
                            .then(data => {
                                if (data.results.length > 0) {
                                    const components = data.results[0].components;
                                    const city = components.city || components.town || components.village;
                                    const stateMain = components.state;

                                    if(!city){
                                        document.getElementById('currentLocationName').textContent = `${stateMain}, ${countryNameText}`;
                                      } else if(!stateMain) {
                                    document.getElementById('currentLocationName').textContent = `${city}, ${countryNameText}`;
                                      } else{
                                        document.getElementById('currentLocationName').textContent = `${city}, ${stateMain}, ${countryNameText}`;
                                      }
                                } else {
                                    console.log('No results found');
                                }
                            })
                            .catch(error => {
                                console.error('Error fetching city name:', error);

                                if (currentApiKeyCityNameIndex < apiKeysCityName.length - 1) {
                                    currentApiKeyCityNameIndex++;
                                    console.log(`Switching to API key index ${currentApiKeyCityNameIndex} for city name`);
                                    fetchCityName(cityLat, cityLon, countryNameText);
                                } else {
                                    console.error('All city name API keys failed. Unable to fetch data.');

                                }
                            });
                    }

            });
}


window.addEventListener('popstate', function (event) {

    if (!document.querySelector('.map_picker').hidden) {
        document.querySelector('.map_picker').style.opacity = '0';
        document.querySelector('.map_picker').style.transform = 'scale(0.8)';


        setTimeout(() => {
            document.querySelector('.map_picker').hidden = true;
            document.querySelector('.map_picker').style.opacity = '';
            document.querySelector('.map_picker').style.transform = '';

        }, 300);
    } else {
        document.getElementById('search-container').style.opacity = '0'
        setTimeout(() => {
            document.getElementById('modal-content').scrollTop = 0;
            document.getElementById('city-input').value = ''
            document.getElementById('city-input').dispatchEvent(new Event('input'));
            document.getElementById('search-container').style.display = 'none'
            document.getElementById('search-container').style.opacity = '1'

        }, 300);
    }
});

function getCountryName(code) {
    return countryNames[code] || "Unknown Country";
}


function getWeather(city, latitude, longitude) {
    showLoader();
    const apiKey = 'CREATE_KEY';
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




                                        let pressureMain;
                                        let pressureMainUnit;

                                        if (SelectedPressureUnit === 'inHg') {
                                            pressureMain = (data.main.pressure * 0.02953).toFixed(2);
                                            pressureMainUnit = 'inHg';
                                        } else if (SelectedPressureUnit === 'mmHg') {
                                            pressureMain = (data.main.pressure * 0.750062).toFixed(2);
                                            pressureMainUnit = 'mmHg';
                                        } else {
                                            pressureMain = data.main.pressure;
                                            pressureMainUnit = 'hPa';
                                        }

            document.getElementById('pressure_text_main').innerHTML = pressureMain
            document.getElementById('pressureMainUnit').innerHTML = pressureMainUnit

            if (data.main.pressure < '980') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.LowPressure
            } else if (data.main.pressure > '980' && data.main.pressure <= '1005') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.LowMedPressure
            } else if (data.main.pressure > '1005' && data.main.pressure <= '1020') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.MediumPressure
            } else if (data.main.pressure > '1020' && data.main.pressure <= '1035') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.HighPressure
            } else if (data.main.pressure > '1036'){
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.VeryHighPressure
            }



            const windDirection = data.wind.deg;


            const directions = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
            const index = Math.round((windDirection % 360) / 45);
            document.getElementById('directionWind').textContent = directions[index]

            setTimeout(()=>{
            document.getElementById('windDirectionArrow').style.transform = `rotate(${windDirection}deg)`
            }, 300);

            const iconCode = data.weather[0].icon;

            const countryNameText = getCountryName(countryName);

                        const cityLat = data.coord.lat
                        const cityLon = data.coord.lon

                        let currentApiKeyCityNameIndex = 0;

                        fetchCityName(cityLat, cityLon, countryNameText)


                        function fetchCityName(cityLat, cityLon, countryNameText) {
                            const apiKeyCityName = apiKeysCityName[currentApiKeyCityNameIndex];
                            const urlcityName = `https://api.opencagedata.com/geocode/v1/json?q=${cityLat}+${cityLon}&key=${apiKeyCityName}`;

                            fetch(urlcityName)
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('Network response was not ok');
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    if (data.results.length > 0) {
                                        const components = data.results[0].components;
                                        const city = components.city || components.town || components.village;
                                        const stateMain = components.state;

                                        if (!city) {
                                            document.getElementById('city-name').innerHTML = `${stateMain}, ${countryNameText}`;
                                            document.getElementById('SelectedLocationText').innerHTML = `${stateMain}, ${countryNameText}`;
                                        } else if (!stateMain) {
                                            document.getElementById('city-name').innerHTML = `${city}, ${countryNameText}`;
                                            document.getElementById('SelectedLocationText').innerHTML = `${city}, ${countryNameText}`;
                                        } else {
                                            document.getElementById('city-name').innerHTML = `${city}, ${stateMain}, ${countryNameText}`;
                                            document.getElementById('SelectedLocationText').innerHTML = `${city}, ${stateMain}, ${countryNameText}`;
                                        }
                                    } else {
                                        console.log('No results found');
                                    }
                                })
                                .catch(error => {
                                    console.error('Error fetching city name:', error);

                                    if (currentApiKeyCityNameIndex < apiKeysCityName.length - 1) {
                                        currentApiKeyCityNameIndex++;
                                        console.log(`Switching to API key index ${currentApiKeyCityNameIndex} for city name`);
                                        fetchCityName(cityLat, cityLon, countryNameText);
                                    } else {
                                        console.error('All city name API keys failed. Unable to fetch data.');

                                    }
                                });
                        }

            if(SelectedTempUnit === 'fahrenheit'){
                document.getElementById('temp').innerHTML = `${tempF}°`;
 document.getElementById('feels_like_now').innerHTML = `Feels like ${Math.round(feelslikeF)}°`

            } else{
            document.getElementById('temp').innerHTML = `${temperature}°`;
        document.getElementById('feels_like_now').innerHTML = `Feels like ${Math.round(feelslike)}°`


            }




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

            getDailyForecast(latitude, longitude);



            const windSpeedMPS = data.wind.speed;
            const windSpeedMPH = (windSpeedMPS * 2.23694).toFixed(0);

            const windSpeedKPH = (windSpeedMPS * 3.6).toFixed(0);
            const timeZoneOffsetSeconds = data.timezone;
            const currentUTC = new Date().getTime();
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


            if (SelectedVisibiltyUnit === 'mileV') {
                document.getElementById('min-temp').innerHTML = `${visibilityInMiles} <span>mi</span>`;
            } else {
                document.getElementById('min-temp').innerHTML = `${(visibility / 1000).toFixed(0)} <span>km</span>`;

            }


            const diffToSunrise = (sunriseUTC - currentUTC) / (1000 * 60);
            const diffToSunset = (sunsetUTC - currentUTC) / (1000 * 60);

            if (diffToSunrise <= 40 && diffToSunrise >= 0) {
                console.log('Sunrise is happening in 40 minutes or less');
                document.getElementById('sunrise_insight').hidden = false;
                document.getElementById('sunrise_insight').classList.add('insights_item')

            } else{
                document.getElementById('sunrise_insight').hidden = true;
                document.getElementById('sunrise_insight').classList.remove('insights_item')

            }

            if (diffToSunset <= 40 && diffToSunset >= 0) {
                document.getElementById('sunset_insight').hidden = false;
                document.getElementById('sunset_insight').classList.add('insights_item')

            } else{
                document.getElementById('sunset_insight').hidden = true;
                document.getElementById('sunset_insight').classList.remove('insights_item')

            }


            document.getElementById('sunrise').textContent = sunrise;
            document.getElementById('sunset').textContent = sunset;
            document.getElementById('humidity').textContent = `${humidity}%`;
            document.getElementById('clouds').textContent = `${clouds}%`;


            if (humidity < '30') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_7;
            } else if (humidity > '30' && humidity <= '50') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_30;
            } else if (humidity > '50' && humidity <= '70') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_70;
            } else if (humidity > '70') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_90;
            }

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



            const air_url = `https://api.openweathermap.org/data/2.5/air_pollution?lat=${latitude}&lon=${longitude}&appid=${apiKey}`;

            fetch(air_url)
                .then(response => response.json())
                .then(air_data => {
                    let aqi = air_data.list[0].main.aqi;
                    document.getElementById('aqi-level').textContent = aqiText[aqi].level;
                    document.getElementById('detail_air').textContent = aqiText[aqi].message;


                                    const airQualityDetailsCompo = air_data.list[0].components;






                                    document.getElementById('pm25_air').innerHTML = Math.round(airQualityDetailsCompo.pm2_5);
                                    document.getElementById('pm25_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.pm10), 'PM2.5');


                                    document.getElementById('pm10_air').innerHTML = Math.round(airQualityDetailsCompo.pm10);
                                    document.getElementById('pm10_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.pm10), 'PM10');


                                    document.getElementById('NO_air').innerHTML = Math.round(airQualityDetailsCompo.no);
                                    document.getElementById('NO_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.no), 'NO');


                                    document.getElementById('NO2_air').innerHTML = Math.round(airQualityDetailsCompo.no2);
                                    document.getElementById('NO2_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.no2), 'NO2');


                                    document.getElementById('SO2_air').innerHTML = Math.round(airQualityDetailsCompo.so2);
                                    document.getElementById('SO2_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.so2), 'SO2');

                                    document.getElementById('O3_air').innerHTML = Math.round(airQualityDetailsCompo.o3);
                                    document.getElementById('O3_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.o3), 'O3');


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
            ShowError()

        });




    currentLocation = null;

}






function getCurrentLocationWeather() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(position => {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;
            getWeatherByCoordinates(latitude, longitude); // Call getWeatherByCoordinates with coordinates

                                    setCurrentLocation(latitude, longitude)

        }, handleGeolocationError);
    } else {
        console.error('Geolocation is not supported by this browser.');
    }
}




function getWeatherByCoordinates(latitude, longitude) {
    showLoader();
    const apiKey = 'CREATE_KEY';
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

                        let currentApiKeyCityNameIndex = 0;

                        fetchCityName(cityLat, cityLon, countryNameText)


                        function fetchCityName(cityLat, cityLon, countryNameText) {
                            const apiKeyCityName = apiKeysCityName[currentApiKeyCityNameIndex];
                            const urlcityName = `https://api.opencagedata.com/geocode/v1/json?q=${cityLat}+${cityLon}&key=${apiKeyCityName}`;

                            fetch(urlcityName)
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('Network response was not ok');
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    if (data.results.length > 0) {
                                        const components = data.results[0].components;
                                        const city = components.city || components.town || components.village;
                                        const stateMain = components.state;

                                        if (!city) {
                                            document.getElementById('city-name').innerHTML = `${stateMain}, ${countryNameText}`;
                                            document.getElementById('SelectedLocationText').innerHTML = `${stateMain}, ${countryNameText}`;
                                        } else if (!stateMain) {
                                            document.getElementById('city-name').innerHTML = `${city}, ${countryNameText}`;
                                            document.getElementById('SelectedLocationText').innerHTML = `${city}, ${countryNameText}`;
                                        } else {
                                            document.getElementById('city-name').innerHTML = `${city}, ${stateMain}, ${countryNameText}`;
                                            document.getElementById('SelectedLocationText').innerHTML = `${city}, ${stateMain}, ${countryNameText}`;
                                        }
                                    } else {
                                        console.log('No results found');
                                    }
                                })
                                .catch(error => {
                                    console.error('Error fetching city name:', error);

                                    if (currentApiKeyCityNameIndex < apiKeysCityName.length - 1) {
                                        currentApiKeyCityNameIndex++;
                                        console.log(`Switching to API key index ${currentApiKeyCityNameIndex} for city name`);
                                        fetchCityName(cityLat, cityLon, countryNameText);
                                    } else {
                                        console.error('All city name API keys failed. Unable to fetch data.');

                                    }
                                });
                        }




                                        let pressureMain;
                                        let pressureMainUnit;

                                        if (SelectedPressureUnit === 'inHg') {
                                            pressureMain = (data.main.pressure * 0.02953).toFixed(2);
                                            pressureMainUnit = 'inHg';
                                        } else if (SelectedPressureUnit === 'mmHg') {
                                            pressureMain = (data.main.pressure * 0.750062).toFixed(2);
                                            pressureMainUnit = 'mmHg';
                                        } else {
                                            pressureMain = data.main.pressure;
                                            pressureMainUnit = 'hPa';
                                        }

            document.getElementById('pressure_text_main').innerHTML = pressureMain
            document.getElementById('pressureMainUnit').innerHTML = pressureMainUnit

            if (data.main.pressure < '980') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.LowPressure
            } else if (data.main.pressure > '980' && data.main.pressure <= '1005') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.LowMedPressure
            } else if (data.main.pressure > '1005' && data.main.pressure <= '1020') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.MediumPressure
            } else if (data.main.pressure > '1020' && data.main.pressure <= '1035') {
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.HighPressure
            } else if (data.main.pressure > '1036'){
                document.getElementById('pressure_icon_svg').innerHTML = WidgetsPressure.VeryHighPressure
            }


                        const windDirection = data.wind.deg;

            const directions = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
            const index = Math.round((windDirection % 360) / 45);
            document.getElementById('directionWind').textContent = directions[index]

            setTimeout(()=>{
            document.getElementById('windDirectionArrow').style.transform = `rotate(${windDirection}deg)`
            }, 300);


            if(SelectedTempUnit === 'fahrenheit'){
                document.getElementById('temp').innerHTML = `${tempF}°`;
     document.getElementById('feels_like_now').innerHTML = `Feels like ${Math.round(feelslikeF)}°`
            } else{
            document.getElementById('temp').innerHTML = `${temperature}°`;

         document.getElementById('feels_like_now').innerHTML = `Feels like ${Math.round(feelslike)}°`

            }







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
            getDailyForecast(latitude, longitude);


            // Additional weather information
            const windSpeedMPS = data.wind.speed;
            const windSpeedMPH = (windSpeedMPS * 2.23694).toFixed(0);
            const windSpeedKPH = (windSpeedMPS * 3.6).toFixed(0); // Convert m/s to km/h
            const timeZoneOffsetSeconds = data.timezone;
            const currentUTC = new Date().getTime();
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

            if (SelectedVisibiltyUnit === 'mileV') {
                document.getElementById('min-temp').innerHTML = `${visibilityInMiles} <span>mi</span>`;
            } else {
                document.getElementById('min-temp').innerHTML = `${(visibility / 1000).toFixed(0)} <span>km</span>`;

            }



            const diffToSunrise = (sunriseUTC - currentUTC) / (1000 * 60);
            const diffToSunset = (sunsetUTC - currentUTC) / (1000 * 60);

            if (diffToSunrise <= 40 && diffToSunrise >= 0) {
                console.log('Sunrise is happening in 40 minutes or less');
                document.getElementById('sunrise_insight').hidden = false;
                document.getElementById('sunrise_insight').classList.add('insights_item')

            } else{
                document.getElementById('sunrise_insight').hidden = true;
                document.getElementById('sunrise_insight').classList.remove('insights_item')

            }

            if (diffToSunset <= 40 && diffToSunset >= 0) {
                document.getElementById('sunset_insight').hidden = false;
                document.getElementById('sunset_insight').classList.add('insights_item')

            } else{
                document.getElementById('sunset_insight').hidden = true;
                document.getElementById('sunset_insight').classList.remove('insights_item')

            }

            document.getElementById('sunrise').textContent = sunrise;
            document.getElementById('sunset').textContent = sunset;

            document.getElementById('humidity').textContent = ` ${humidity}% `;
            document.getElementById('clouds').textContent = `${clouds}%`;


            if (humidity < '30') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_7;
            } else if (humidity > '30' && humidity <= '50') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_30;
            } else if (humidity > '50' && humidity <= '70') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_70;
            } else if (humidity > '70') {
                document.getElementById('humidity_icon_svg').innerHTML = WidgetsHumidity.Humidity_90;
            }


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



            const air_url = `https://api.openweathermap.org/data/2.5/air_pollution?lat=${latitude}&lon=${longitude}&appid=${apiKey}`;

            fetch(air_url)
                .then(response => response.json())
                .then(air_data => {
                    let aqi = air_data.list[0].main.aqi;
                    document.getElementById('aqi-level').textContent = aqiText[aqi].level;
                    document.getElementById('detail_air').textContent = aqiText[aqi].message;

                                        const airQualityDetailsCompo = air_data.list[0].components;






                                        document.getElementById('pm25_air').innerHTML = Math.round(airQualityDetailsCompo.pm2_5);
                                        document.getElementById('pm25_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.pm10), 'PM2.5');


                                        document.getElementById('pm10_air').innerHTML = Math.round(airQualityDetailsCompo.pm10);
                                        document.getElementById('pm10_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.pm10), 'PM10');


                                        document.getElementById('NO_air').innerHTML = Math.round(airQualityDetailsCompo.no);
                                        document.getElementById('NO_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.no), 'NO');


                                        document.getElementById('NO2_air').innerHTML = Math.round(airQualityDetailsCompo.no2);
                                        document.getElementById('NO2_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.no2), 'NO2');


                                        document.getElementById('SO2_air').innerHTML = Math.round(airQualityDetailsCompo.so2);
                                        document.getElementById('SO2_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.so2), 'SO2');

                                        document.getElementById('O3_air').innerHTML = Math.round(airQualityDetailsCompo.o3);
                                        document.getElementById('O3_air_color').style.backgroundColor = getColor(Math.round(airQualityDetailsCompo.o3), 'O3');

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
                    ShowError()
                });

    currentLocation = {
        latitude,
        longitude
    };




}


function getColor(value, type) {
    switch(type) {
        case 'CO':
            if (value <= 4.4) return '#20fc03';
            if (value <= 9.0) return 'yellow';
            if (value <= 15.0) return 'orange';
            if (value <= 30.0) return '#fc606d';
            if (value <= 45.0) return '#9000ff';
            return 'maroon';
        case 'NH3':
            if (value <= 5) return '#20fc03';
            if (value <= 15) return 'yellow';
            if (value <= 25) return 'orange';
            if (value <= 35) return '#fc606d';
            if (value <= 50) return '#9000ff';
            return 'maroon';
        case 'NO':
            if (value <= 50) return '#20fc03';
            if (value <= 100) return 'yellow';
            if (value <= 150) return 'orange';
            if (value <= 200) return '#fc606d';
            if (value <= 300) return '#9000ff';
            return 'maroon';
        case 'NO2':
            if (value <= 40) return '#20fc03';
            if (value <= 100) return 'yellow';
            if (value <= 200) return 'orange';
            if (value <= 300) return '#fc606d';
            if (value <= 500) return '#9000ff';
            return 'maroon';
        case 'O3':
            if (value <= 100) return '#20fc03';
            if (value <= 180) return 'yellow';
            if (value <= 300) return 'orange';
            if (value <= 400) return '#fc606d';
            if (value <= 500) return '#9000ff';
            return 'maroon';
        case 'PM2.5':
            if (value <= 12) return '#20fc03';
            if (value <= 35) return 'yellow';
            if (value <= 55) return 'orange';
            if (value <= 150) return '#fc606d';
            if (value <= 250) return '#9000ff';
            return 'maroon';
        case 'PM10':
            if (value <= 20) return '#20fc03';
            if (value <= 50) return 'yellow';
            if (value <= 100) return 'orange';
            if (value <= 150) return '#fc606d';
            if (value <= 250) return '#9000ff';
            return 'maroon';
        case 'SO2':
            if (value <= 50) return '#20fc03';
            if (value <= 150) return 'yellow';
            if (value <= 250) return 'orange';
            if (value <= 500) return '#fc606d';
            if (value <= 1000) return '#9000ff';
            return 'maroon';
        default:
            return 'white';
    }
}



function updateSunTrackProgress(latitude, longitude) {
    const apiKey = 'CREATE_KEY';
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

    const apiKey = apiKeysDaily[currentApiKeyIndex];
    const apiUrl = `https://api.openweathermap.org/data/2.5/onecall?lat=${latitude}&lon=${longitude}&exclude=current,minutely,daily,alerts&appid=${apiKey}&units=metric`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const forecastData = data.hourly;
            display24HourForecast(forecastData);
        })
        .catch(error => {
            console.error('Error fetching daily forecast:', error);
            if (currentApiKeyIndex < apiKeysDaily.length - 1) {
                currentApiKeyIndex++;
                console.log(`Switching to API key index ${currentApiKeyIndex}`);
                get24HourForecast(latitude, longitude);
            } else {
                console.error('All API keys failed. Unable to fetch data.');
            }
        });
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
            let time;

                time = timestamp.toLocaleTimeString('en-US', { hour: 'numeric', hour12: true });
                time = time.replace('AM', ' AM').replace('PM', ' PM');




                const temperature = Math.round(forecast.temp);
                const tempF = Math.round(temperature * 9 / 5 + 32);




            const iconCode = forecast.weather[0].icon;
            const description = forecast.weather[0].description;
            const rainPercentage = forecast.pop * 100;
            const rainMeterBarItem = document.createElement('rainMeterBarItem');


            let rain1hr;



                        if (SelectedPrecipitationUnit === 'in') {
                            if (forecast.snow) {
                                rain1hr = forecast.snow ? (forecast.snow['1h'] * 0.0393701).toFixed(2) : 0;
                            } else {
                                rain1hr = forecast.rain ? (forecast.rain['1h'] * 0.0393701).toFixed(2) : 0;
                            }
                            rainUnit = 'in'
                        } else {
                            if (forecast.snow) {
                                rain1hr = forecast.snow ? forecast.snow['1h'] : 0;
                            } else {
                                rain1hr = forecast.rain ? forecast.rain['1h'] : 0;
                            }
                            rainUnit = 'mm'

                        }


                        let rain1hrBar;


                        if (forecast.snow) {
                            rain1hrBar = forecast.snow ? forecast.snow['1h'] : 0;
                        } else {
                            rain1hrBar = forecast.rain ? forecast.rain['1h'] : 0;
                        }






            const maxRain = 2;
            const rainAmountPercent = (rain1hrBar / maxRain) * 100;

            let barColor;

            if(rain1hrBar < 0.5 ){
                barColor = '#4c8df6'
            } else if(rain1hrBar > 0.5 && rain1hrBar <= 1){
                barColor = 'orange'
            } else if(rain1hrBar > 1){
                barColor = 'red'
            }



            const forecastItem = document.createElement('div');
            forecastItem.classList.add('forecast-item');
            forecastItem.id = "forecast24";

            if(SelectedTempUnit === 'fahrenheit'){
                forecastItem.innerHTML = `
                <p class="temp-24">${tempF}°</p>
                <img id="icon-24" src="weather-icons/${iconCode}.svg" alt="Weather Icon" class="icon-24">
                <p class="time-24">${time}</p>

                 <p class="disc_sml-24" >${description}</p>
                  <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>

            `;
            } else{
            forecastItem.innerHTML = `
            <p class="temp-24">${temperature}°</p>
            <img id="icon-24" src="weather-icons/${iconCode}.svg" alt="Weather Icon" class="icon-24">
            <p class="time-24">${time}</p>

             <p class="disc_sml-24" >${description}</p>
              <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>

        `;
            }

            rainMeterBarItem.innerHTML = `
                <rainPerBar>
                  <rainPerBarProgress style="height: ${Math.round(rainAmountPercent)}%; background-color: ${barColor};"">
                </rainPerBarProgress>
                </rainPerBar>
                <p>${rain1hr}${rainUnit}</p>
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

function getDailyForecast(latitude, longitude) {

    const apiKey = apiKeysDaily[currentApiKeyIndex];
    const apiUrl = `https://api.openweathermap.org/data/2.5/onecall?lat=${latitude}&lon=${longitude}&appid=${apiKey}&units=metric`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const dailyForecast = data.daily;
            displayDailyForecast(dailyForecast);
        })
        .catch(error => {
            console.error('Error fetching daily forecast:', error);
            if (currentApiKeyIndex < apiKeysDaily.length - 1) {
                currentApiKeyIndex++;
                console.log(`Switching to API key index ${currentApiKeyIndex}`);
                getDailyForecast(latitude, longitude);
            } else {
                console.error('All API keys failed. Unable to fetch data.');
            }
        });
}



function displayDailyForecast(dailyForecast) {
    const forecastContainer = document.getElementById('forecast-5day');
    forecastContainer.innerHTML = '';

    const today = new Date();
    today.setHours(0, 0, 0, 0);

        const trendDaysArray = [2, 3, 6];
        let trendMessage = '';

        for (let trendDays of trendDaysArray) {
            if (dailyForecast.length >= trendDays) {
                let tempDifferenceSum = 0;

                for (let i = 0; i < trendDays - 1; i++) {
                    const currentDayAvgTemp = (dailyForecast[i].temp.max + dailyForecast[i].temp.min) / 2;
                    const nextDayAvgTemp = (dailyForecast[i + 1].temp.max + dailyForecast[i + 1].temp.min) / 2;
                    tempDifferenceSum += nextDayAvgTemp - currentDayAvgTemp;
                }

                if (tempDifferenceSum > 0) {
                    trendMessage = `Warming expected over the next ${trendDays} days`;
                    document.getElementById('temp_insight_icon').innerHTML = 'trending_up';
                } else if (tempDifferenceSum < 0) {
                    trendMessage = `Cooling expected over the next ${trendDays} days`;
                    document.getElementById('temp_insight_icon').innerHTML = 'trending_down';

                } else {
                    trendMessage = `Stable temperatures expected over the next ${trendDays} days`;
                    document.getElementById('temp_insight_icon').innerHTML = 'thermostat';


                }

                break;
            }
        }

        document.getElementById('temp_insight').innerHTML = trendMessage

    dailyForecast.forEach((forecast, index) => {


        const timestamp = new Date(forecast.dt * 1000);
        const isToday = index === 0;
        const date = isToday ? 'Today' : timestamp.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
        const dayName = isToday ? 'Today' : timestamp.toLocaleDateString('en-US', { weekday: 'short' });


        const description = forecast.weather[0].description;
        let iconCode = forecast.weather[0].icon;
        const temp = Math.round(forecast.temp.max);
        const tempMIN = Math.round(forecast.temp.min);

        const tempF = Math.round(temp * 9 / 5 + 32);

        const rainPercentage = Math.round(forecast.pop * 100);;


        const tempMINF = Math.round(tempMIN * 9 / 5 + 32);


        iconCode = iconCode.replace('n', 'd');

        const forecastItem = document.createElement('div');
        forecastItem.classList.add('forecast-item-forecast');

        if (SelectedTempUnit === 'fahrenheit') {
            forecastItem.innerHTML = `
            <p class="disc-5d">${tempF}°<span> ${tempMINF}°</span></p>

            <img id="icon-5d" src="weather-icons/${iconCode}.svg" alt="Weather Icon">
            <span class="daily_rain">${rainPercentage}%<i icon-outlined>water_drop</i></span>
            <div class="d5-disc-text">
            <p class="time-5d">${dayName}</p>
            </div>

            `


        ;
        } else {

            forecastItem.innerHTML =`
        <p class="disc-5d">${temp}°<span> ${tempMIN}°</span></p>

        <img id="icon-5d" src="weather-icons/${iconCode}.svg" alt="Weather Icon">
            <span class="daily_rain">${rainPercentage}% <i icon-outlined>water_drop</i></span>
        <div class="d5-disc-text">
        <p class="time-5d">${dayName}</p>
        </div>
`
    ;
        }



        forecastContainer.appendChild(forecastItem);
    });
}


function showLoader() {
    const loaderContainer = document.getElementById('loader-container');
    loaderContainer.style.display = 'flex';
    loaderContainer.style.opacity = '1';
                document.querySelector('rainmeterbar').scrollLeft = 0

}

// Hide the loader
function hideLoader() {
    const loaderContainer = document.getElementById('loader-container');

    loaderContainer.style.opacity = '0';

    setTimeout(() => {
        loaderContainer.style.display = 'none';

    }, 300);


    let savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];

    if (savedLocations.length === 0) {
        document.querySelector('.savedLocations').hidden = true;
    } else {
        document.querySelector('.savedLocations').hidden = false;

    }


}





function refreshWeather(){
    const latSend = localStorage.getItem('currentLat')
    const longSend = localStorage.getItem('currentLong')

    getWeather(' ',latSend, longSend)

        setTimeout(()=>{
            document.querySelector('.refresh_weat').disabled = true;
        }, 200);

        setTimeout(()=>{
            document.querySelector('.refresh_weat').disabled = false;
        }, 300000);
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

                document.getElementById('search-container').style.opacity = '0'

                setTimeout(() => {
                    window.history.back();

                }, 500);

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




function switchApiKey() {
    currentKeyMoonIndex = (currentKeyMoonIndex + 1) % apiKeysVisual.length;
}

function Fetchmoonphases(lat, long) {

    const apiKey = apiKeysVisual[currentKeyMoonIndex];

    fetch(`https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${lat},${long}?unitGroup=metric&include=days&key=${apiKey}&contentType=json`)
      .then(response => {
        if (!response.ok) {
          switchApiKey();
          throw new Error('API limit reached or error occurred');
        }
        return response.json();
      })
      .then(data => {


const moon_phase_img = document.getElementById('moon_phase_img');
        const moonPhaseText = document.getElementById('moonPhaseText');
        const moonPhaseDaysLeft = document.getElementById('moonPhaseDaysLeft');
        const moonPhase = data.days[0].moonphase;

        let phaseName = '';
        let phaseImg = '';
        let daysLeft = 0;
        let nextPhaseName = '';




        if (moonPhase === 0) {
            phaseName = 'New Moon';
            phaseImg = 'moon_phases/moon_new.svg';
            daysLeft = Math.round((0.25 - moonPhase) * 29.53);
            nextPhaseName = 'Waxing Crescent';


        } else if (moonPhase > 0 && moonPhase < 0.25) {
            phaseName = 'Waxing Crescent';
            phaseImg = 'moon_phases/moon_waxing_crescent.svg';
            daysLeft = Math.round((0.25 - moonPhase) * 29.53);
            nextPhaseName = 'First Quarter';


        } else if (moonPhase === 0.25) {
            phaseName = 'First Quarter';
            phaseImg = 'moon_phases/moon_first_quarter.svg';
            daysLeft = Math.round((0.5 - moonPhase) * 29.53);
            nextPhaseName = 'Waxing Gibbous';


        } else if (moonPhase > 0.25 && moonPhase < 0.5) {
            phaseName = 'Waxing Gibbous';
            phaseImg = 'moon_phases/moon_waxing_gibbous.svg';
            daysLeft = Math.round((0.5 - moonPhase) * 29.53);
            nextPhaseName = 'Full Moon';



        } else if (moonPhase === 0.5) {
            phaseName = 'Full Moon';
            phaseImg = 'moon_phases/moon_full.svg';
            daysLeft = Math.round((0.75 - moonPhase) * 29.53);
            nextPhaseName = 'Waning Gibbous';


        } else if (moonPhase > 0.5 && moonPhase < 0.75) {
            phaseName = 'Waning Gibbous';
            phaseImg = 'moon_phases/moon_waning_gibbous.svg';
            daysLeft = Math.round((0.75 - moonPhase) * 29.53);
            nextPhaseName = 'Last Quarter';



        } else if (moonPhase === 0.75) {
            phaseName = 'Last Quarter';
            phaseImg = 'moon_phases/moon_last_quarter.svg';
            daysLeft = Math.round((1 - moonPhase) * 29.53);
            nextPhaseName = 'Waning Crescent';


        } else if (moonPhase > 0.75 && moonPhase < 1) {
            phaseName = 'Waning Crescent';
            phaseImg = 'moon_phases/moon_waning_crescent.svg';
            daysLeft = Math.round((1 - moonPhase) * 29.53);
            nextPhaseName = 'New Moon';



        }


                    const currentDateMoonPhase = Math.floor(new Date().getTime() / 1000);



                    fetch(`https://api.farmsense.net/v1/moonphases/?d=${currentDateMoonPhase}`)
                        .then(response => response.json())
                        .then(data => {
                            const  MoonIllumination = Math.round(data[0].Illumination * 100)


                            document.getElementById('MoonIllumination').innerHTML = `${MoonIllumination}%`

                            const moonPhaseProgress = document.querySelector('moonPhaseProgress');

                            if (MoonIllumination >= 50) {
                                moonPhaseProgress.style.width = `${MoonIllumination}%`;
                                moonPhaseProgress.style.right = '0';
                                moonPhaseProgress.style.left = 'auto';
                            } else {
                                moonPhaseProgress.style.width = `${MoonIllumination}%`;
                                moonPhaseProgress.style.left = '0';
                                moonPhaseProgress.style.right = 'auto';
                            }


                        })
                        .catch(error => {
                            console.error('Error fetching data:', error);
                        });



                document.getElementById('moonPhaseNext').innerHTML = nextPhaseName




        moonPhaseText.innerHTML = phaseName;
//        moon_phase_img.src = phaseImg;
                    if(daysLeft > 1){
                        moonPhaseDaysLeft.innerHTML = `${daysLeft} days`;
                    } else{
                        moonPhaseDaysLeft.innerHTML = `${daysLeft} day`;
                    }

            document.getElementById('weatherComments').innerHTML = data.days[0].description + '<space></space> <md-icon icon-outlined id="arrow_up_toggle">keyboard_arrow_down</md-icon>'





            if (SelectedWindUnit === 'mile') {
                const windGustInMph = Math.round((data.days[0].windgust * 0.621371)) || '0';
                document.getElementById('WindGust').textContent = windGustInMph + ' mph';
            } else {
                document.getElementById('WindGust').textContent = Math.round(data.days[0].windgust || '0')  + ' km/h';
            }



            if (SelectedPrecipitationUnit === 'in') {
                const precipInInches = (data.days[0].precip * 0.0393701).toFixed(2);
                document.getElementById('AmountRainMM').innerHTML = precipInInches + ' in';
            } else {
                document.getElementById('AmountRainMM').innerHTML = data.days[0].precip.toFixed(1) + ' mm'
            }


const precipitationtypeText = data.days[0].preciptype ? data.days[0].preciptype[0] : 'None';


                document.getElementById('precipitation_type').innerHTML = 'Type: ' + precipitationtypeText



                            document.getElementById('RainHours').innerHTML = Math.round(data.days[0].precipcover) + '%';





                                function celsiusToFahrenheit(celsius) {
                                    return Math.round((celsius * 9/5) + 32);
                                }

                                const today_max_temp_celsius = data.days[0].tempmax;
                                const today_min_temp_celsius = data.days[0].tempmin;

            const dewPointTodayC = Math.round(data.days[0].dew);
            const dewPointTodayF = Math.round(dewPointTodayC * 9 / 5 + 32);

                                if(SelectedTempUnit === 'fahrenheit'){
                                    document.getElementById('high_temp').innerHTML = celsiusToFahrenheit(today_max_temp_celsius) + '°';
                                    document.getElementById('low_temp').innerHTML = celsiusToFahrenheit(today_min_temp_celsius) + '°';
                                     document.getElementById('dew_percentage').innerHTML  = dewPointTodayF + '°';
                                } else{
                                    document.getElementById('high_temp').innerHTML = Math.round(today_max_temp_celsius) + '°';
                                    document.getElementById('low_temp').innerHTML = Math.round(today_min_temp_celsius) + '°';
                                     document.getElementById('dew_percentage').innerHTML  = dewPointTodayC + '°';

                                }

                                hideLoader()
    })

    .catch(error => {
        console.error(error);
        console.log('no moon');
        Fetchmoonphases(lat, long);
      });





      fetchAstronomyData(lat, long)

      function switchAstronomyApiKey() {
        currentAstronomyKeyIndex = (currentAstronomyKeyIndex + 1) % astronomyApiKeys.length;
      }

      function fetchAstronomyData(lat, long) {
        const apiKey = astronomyApiKeys[currentAstronomyKeyIndex];

        fetch(`https://api.ipgeolocation.io/astronomy?apiKey=${apiKey}&lat=${lat}&long=${long}`)
          .then(response => {
            if (!response.ok) {
              switchAstronomyApiKey();
              throw new Error('API limit reached or error occurred');
            }
            return response.json();
          })
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


//        document.getElementById('DayLengthText').innerHTML = formattedDayLength;

    })
    .catch(error => {
      console.error(error);
      fetchAstronomyData(lat, long);
    });


  fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${long}&daily=daylight_duration&timezone=auto`)
  .then(response => response.json())
  .then(data => {
        const DayLengthdata = data.daily.daylight_duration[0]

        const hours = Math.floor(DayLengthdata / 3600);
        const minutes = Math.floor((DayLengthdata % 3600) / 60);

        document.getElementById('DayLengthText').innerHTML = `${hours} hours ${minutes} mins`;


    })
}




const latAlerts = lat;
const lonAlerts = long;
let currentApiKeyAlertsIndex = 0;


          const latSum = lat;
          const lonSum = long;
                      summary(latSum, lonSum)


getWeatherAlerts(latAlerts, lonAlerts)


function getWeatherAlerts(latAlerts, lonAlerts) {
    const apiKeyAlerts = apiKeysAlerts[currentApiKeyAlertsIndex];
    const apiUrlAlerts = `https://api.openweathermap.org/data/2.5/onecall?lat=${latAlerts}&lon=${lonAlerts}&exclude=current,minutely,hourly,daily&appid=${apiKeyAlerts}`;

    fetch(apiUrlAlerts)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.alerts) {
                console.log(data.alerts);
                const alertEvent = data.alerts[0].event;
                document.getElementById('excessiveHeatText').innerHTML = 'Weather alerts' + '<text>Tap to see more...</text>';
                document.querySelector('.weatherCommentsDiv').classList.add('alertOpened');
                document.querySelector('.excessiveHeat').hidden = false;
            } else {
                console.log('No alerts');
                document.querySelector('.weatherCommentsDiv').classList.remove('alertOpened');
                document.querySelector('.excessiveHeat').hidden = true;
            }
        })
        .catch(error => {
            console.error('Error fetching weather alerts:', error);
            if (currentApiKeyAlertsIndex < apiKeysAlerts.length - 1) {
                currentApiKeyAlertsIndex++;
                console.log(`Switching to API key index ${currentApiKeyAlertsIndex} for alerts`);
                getWeatherAlerts(latAlerts, lonAlerts);
            } else {
                console.error('All alert API keys failed. Unable to fetch data.');
            }
        });
}

}

function convertToAmPm(time) {
    let [hour, minute] = time.split(':');
    hour = parseInt(hour);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    hour = hour % 12;
    hour = hour ? hour : 12;
    return `${hour}:${minute} ${ampm}`;
}



function seeSelectedLocation(){
    document.querySelector('selectLocationText').hidden = false;
    document.querySelector('selectLocationTextOverlay').hidden = false;

    document.querySelector('.header_hold').style.transform = 'scale(1.1)';
    document.querySelector('.header_hold').style.opacity = '0';


}

function seeSelectedLocationClose(){
    document.querySelector('selectLocationTextOverlay').hidden = true;
    document.querySelector('selectLocationText').hidden = true;
    document.querySelector('.header_hold').style.transform = '';
    document.querySelector('.header_hold').style.opacity = '';
}

document.querySelector('selectLocationTextOverlay').addEventListener('click', ()=>{
    seeSelectedLocationClose()
});



function checkNoInternet(){
        if(navigator.onLine){
            document.querySelector('.no_internet_error').hidden = true;
            document.getElementById('error_img_cat').innerHTML = ''

        } else{
            ShowError()
        }
    }
    checkNoInternet()





    document.addEventListener('DOMContentLoaded', async function() {

        const currentVersion = 'v1.5.7';
            const githubRepo = 'PranshulGG/WeatherMaster';
            const releasesUrl = `https://api.github.com/repos/${githubRepo}/releases/latest`;

            try {
                const response = await fetch(releasesUrl);
                if (!response.ok) throw new Error('Network response was not ok.');

                const data = await response.json();
                const latestVersion = data.tag_name;



            if (latestVersion !== currentVersion) {


                document.querySelector('.new_ver_download').hidden = false;


                } else {
                document.querySelector('.new_ver_download').hidden = true;
                    return
                }
            }catch (error) {
            }
        });




const scrollView = document.querySelector('.insights');


const sections = document.querySelectorAll('.insights_item');
const scrollIndicators = document.getElementById('scroll-indicators');


sections.forEach((section, index) => {
  const dot = document.createElement('span');
  const dotValue = document.createElement('div');
  dot.classList.add('dot');
  dotValue.classList.add('dotValue')
  dot.onclick = () => {
    section.scrollIntoView({ behavior: 'smooth' });
  };
  scrollIndicators.appendChild(dot);
  dot.appendChild(dotValue)
});


const updateActiveIndicator = () => {
  const scrollPosition = Math.round(scrollView.scrollLeft / scrollView.offsetWidth);
  const dotsValue = document.querySelectorAll('.dotValue');
  dotsValue.forEach((dotValue, index) => {
    if (index === scrollPosition) {
    //   dot.style.backgroundColor = 'var(--Tertiary)';
    dotValue.style.transform = 'scale(1)';
    } else {
    //   dot.style.backgroundColor = 'var(--Surface-Container-Low)';
    dotValue.style.transform = 'scale(0)';

    }
  });
};


scrollView.addEventListener('scroll', updateActiveIndicator);

updateActiveIndicator();


