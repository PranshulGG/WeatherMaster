
//E:\MYAPPS\WeatherMaster\app\release

const DefaultLocation = JSON.parse(localStorage.getItem('DefaultLocation'));

let currentApiKeyIndex = 0;

let currentKeyMoonIndex = 0;
let currentAstronomyKeyIndex = 0;

function WaitBeforeRefresh() {
    ShowSnackMessage.ShowSnack("Please wait before refreshing again.", "long");
}




function handleStorageChange(event) {
    if (event.key === 'SelectedTempUnit' ||
        event.key === 'SelectedWindUnit' ||
        event.key === 'selectedVisibilityUnit' ||
        event.key === 'selectedTimeMode' ||
        event.key === 'selectedPrecipitationUnit' ||
        event.key === 'DefaultLocation' ||
        event.key === 'UseBackgroundAnimations' ||
        event.key === 'selectedMainWeatherProvider' ||
        event.key === 'ApiForAccu' ||
        event.key === 'selectedPressureUnit') {

        setTimeout(() => {
            window.location.reload();

            sendThemeToAndroid('overcast')
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

function useAutoCurrentLocation() {
    showLoader();
    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition((position) => {
            currentLocation = {
                latitude: position.coords.latitude,
                longitude: position.coords.longitude
            };

            document.getElementById('city-name').innerHTML = 'Current location';
            document.getElementById('SelectedLocationText').innerHTML = 'Current location';
            localStorage.setItem('CurrentLocationName', 'Current location')
            document.getElementById('currentLocationName').textContent = 'Current location';

            if (!DefaultLocation || DefaultLocation.name === 'CurrentDeviceLocation') {
                localStorage.setItem('DefaultLocation', JSON.stringify({ lat: currentLocation.latitude, lon: currentLocation.longitude, name: 'CurrentDeviceLocation' }));
            }

            localStorage.setItem('deviceLat', currentLocation.latitude)
            localStorage.setItem('devicelon', currentLocation.longitude)

            DecodeWeather(currentLocation.latitude, currentLocation.longitude, 'CurrentDeviceLocation');
        });
    }
}

if (navigator.onLine) {
    if (DefaultLocation) {
        if (DefaultLocation.name === 'CurrentDeviceLocation') {
            useAutoCurrentLocation()
            sendThemeToAndroid("ReqLocation")
            document.querySelector('.currentLocationdiv').hidden = false;
        } else if (DefaultLocation.lat && DefaultLocation.lon) {
            DecodeWeather(DefaultLocation.lat, DefaultLocation.lon, DefaultLocation.name)

            document.getElementById('city-name').innerHTML = DefaultLocation.name;
            document.getElementById('SelectedLocationText').innerHTML = DefaultLocation.name;
            localStorage.setItem('CurrentLocationName', DefaultLocation.name)
            document.getElementById('currentLocationName').textContent = DefaultLocation.name
        }
    }
    else {
        useAutoCurrentLocation()
        sendThemeToAndroid("ReqLocation")
        document.querySelector('.currentLocationdiv').hidden = false;
    }
} else {

        if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by Met Norway (Global)';

        } else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by Accuweather (Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by Météo-France (Europe, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dwdEurope') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by DWD (Europe, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by NOAA (Americas, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by ECMWF (Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by UK Met Office (Europe, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by JMA (Asia, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by GEM (Americas, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by BOM (Oceania, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by CMA (Asia, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'knmiEurope') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by KNMI (Europe, Global)';

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dmiEurope') {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by DMI (Europe, Global)';

        } else {
            document.querySelector('.data_provider_name_import').innerHTML = 'Data by Open-Meteo (Global)';
        }


}







function handleGeolocationError(error) {
    console.error('Error getting geolocation:', error);
    displayErrorMessage('Error getting geolocation. Please enable location services.');
}


document.addEventListener('DOMContentLoaded', () => {



    const cityInput = document.getElementById('city-input');
    const cityopen = document.getElementById('city-open');
    const searchContainer = document.getElementById('search-container');
    const closeButton = document.querySelector('.close_search');
    const openMapPicker = document.getElementById('openMapPicker');

    cityopen.addEventListener("click", () => {

        sendThemeToAndroid("DisableSwipeRefresh")
        let savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];

        localStorage.setItem('DeviceOnline', 'Yes');
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

    let debounceTimeout;

    cityInput.addEventListener('input', () => {




        document.getElementById('cityLoader').hidden = true;
        document.querySelector('.currentLocationdiv').hidden = false;
        document.querySelector('.savedLocations').hidden = false;



        let savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];

        if (savedLocations.length === 0) {
            document.querySelector('.savedLocations').hidden = true;
        } else {
            document.querySelector('.savedLocations').hidden = false;

        }


    });


    cityInput.addEventListener('keypress', (event) => {
        const searchTerm = cityInput.value.trim();
        document.querySelector('.currentLocationdiv').hidden = true;

        if (event.key === 'Enter') {
            if (searchTerm) {
                document.querySelector('.currentLocationdiv').hidden = true;
                document.querySelector('.savedLocations').hidden = true;
                document.getElementById('cityLoader').hidden = false;
                setTimeout(() => {
                    getCitySuggestions(cityInput.value);
                }, 500);
            } else {
                cityList.innerHTML = '';
                document.getElementById('cityLoader').hidden = true;
                document.querySelector('.currentLocationdiv').hidden = false;


            }
        }
    });


    async function getCitySuggestions(query) {
        const url = `https://geocoding-api.open-meteo.com/v1/search?name=${query}&count=5`;

        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            displaySuggestions(data.results);
        } catch (error) {
            console.error('Error fetching city suggestions:', error);
        }
    }

    function displaySuggestions(results) {
        const suggestionsContainer = document.getElementById('city-list');
        clearSuggestions();

        const displayedSuggestions = new Set();

        const savedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];
        const savedLocationsSet = new Set(savedLocations.map(location => location.locationName));

        results.forEach(result => {
            const city = result.name;
            const state = result.admin1 || result.admin2 || result.admin3;
            const country = result.country;
            const countryCode = result.country_code.toLowerCase();

            const uniqueComponents = [city, state, country].filter((value, index, self) => value && self.indexOf(value) === index);
            const suggestionText = uniqueComponents.join(', ');

            if (!displayedSuggestions.has(suggestionText)) {
                displayedSuggestions.add(suggestionText);

                const suggestionItem = document.createElement('div');
                const clickLocation = document.createElement('savelocationtouch');
                // const saveBtnLocation = document.createElement('md-text-button');
                // saveBtnLocation.textContent = 'Save';



                const createGap = document.createElement('flex');
                const countryIcon = document.createElement('span');
                countryIcon.classList.add('fi', 'fis', `fi-${countryCode}`);

                suggestionItem.classList.add('suggestion-item');
                const suggestRipple = document.createElement('md-ripple');
                suggestRipple.style = '--md-ripple-pressed-opacity: 0.1;';
                suggestionItem.textContent = suggestionText;

                clickLocation.appendChild(suggestRipple);
                suggestionItem.setAttribute('data-lat', result.latitude);
                suggestionItem.setAttribute('data-lon', result.longitude);

                clickLocation.addEventListener('click', function () {
                    DecodeWeather(result.latitude, result.longitude, suggestionText);
                    cityList.innerHTML = '';
                    cityInput.value = '';
                    document.querySelector('.focus-input').blur();
                    document.getElementById('forecast').scrollLeft = 0;
                    document.getElementById('weather_wrap').scrollTop = 0;
                    saveLocationToContainer(suggestionText, result.latitude, result.longitude);
                    cityList.innerHTML = '';
                    cityInput.value = '';

                    setTimeout(() => {
                        cityInput.dispatchEvent(new Event('input'));
                    }, 200);
                });


                suggestionItem.appendChild(countryIcon);
                suggestionItem.appendChild(clickLocation);
                suggestionItem.appendChild(createGap);
                // suggestionItem.appendChild(saveBtnLocation);
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


function GetSavedSearchLocation() {
    const searchedItem = JSON.parse(localStorage.getItem(`SearchedItem`));

    if (searchedItem) {
        DecodeWeather(searchedItem.latitude, searchedItem.longitude, searchedItem.LocationName);
        saveLocationToContainer(searchedItem.LocationName, searchedItem.latitude, searchedItem.longitude)
    }

    ShowSnackMessage.ShowSnack("Loading location data", "short");

}

function handleStorageChangeSearch(event) {
    if (event.key === 'SearchedItem') {

        setTimeout(() => {
            GetSavedSearchLocation()
        }, 1000);

    }
}

window.addEventListener('storage', handleStorageChangeSearch);

function saveLocationToContainer(locationName, lat, lon) {
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

    CheckLocationDatas()

    savedLocationsHolder.innerHTML = ''


    if (savedLocations.length === 0) {
        document.querySelector('.savedLocations').hidden = true;
    } else {
        document.querySelector('.savedLocations').hidden = false;
    }

    const currentTime = new Date().getTime();

    savedLocations.forEach(location => {
        const savedLocationItem = document.createElement('savedLocationItem');
        savedLocationItem.setAttribute('lat', location.lat);
        savedLocationItem.setAttribute('lon', location.lon);
        savedLocationItem.setAttribute('locationLabel', location.locationName)
        const savedLocationItemLat = savedLocationItem.getAttribute('lat');
        const savedLocationItemLon = savedLocationItem.getAttribute('lon');

        let temp = '0'
        let icon = '../icons/error.png'
        let conditionlabel = 'Loading, please wait..'
        if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway' && JSON.parse(localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.properties.timeseries[0].data.instant.details.air_temperature));
                } else {
                    temp = Math.round(data.properties.timeseries[0].data.instant.details.air_temperature);
                }

                icon = getMetNorwayIcons(data.properties.timeseries[0].data.next_1_hours.summary.symbol_code)

                conditionlabel = getMetNorwayWeatherLabelInLangNoAnim(data.properties.timeseries[0].data.next_1_hours.summary.symbol_code, localStorage.getItem('AppLanguageCode'))
            }


        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway' && !JSON.parse(localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallMet_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = ''
                setTimeout(() => {
                    loadSavedLocations()
                }, 1000)
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather' && JSON.parse(localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(data[0].Temperature.Imperial.Value);

                } else {
                    temp = Math.round(data[0].Temperature.Metric.Value);

                }

                icon = GetWeatherIconAccu(data[0].WeatherIcon)
                conditionlabel = GetWeatherTextAccuNoAnim(data[0].WeatherIcon)
            }

        } else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather' && !JSON.parse(localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCall_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = ''
                setTimeout(() => {
                    loadSavedLocations()
                }, 1000)
                localStorage.setItem(lastCallTimeKey, currentTime);
            }

        }

        else if (localStorage.getItem('selectedMainWeatherProvider') === 'dwdGermany' && JSON.parse(localStorage.getItem(`WeatherDatadwdGermany_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDatadwdGermany_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m))
                } else {
                    temp = Math.round(data.current.temperature_2m)
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'))
            }

        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dwdGermany' && !JSON.parse(localStorage.getItem(`WeatherDatadwdGermany_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCalldwdGermany_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = ''
                setTimeout(() => {
                    loadSavedLocations()
                }, 1000)
                localStorage.setItem(lastCallTimeKey, currentTime);
            }

        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS' && JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS' && !JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallnoaaUS_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance' && JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance' && !JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallmeteoFrance_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf' && JSON.parse(localStorage.getItem(`WeatherDataECMWF_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataECMWF_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf' && !JSON.parse(localStorage.getItem(`WeatherDataECMWF_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallecmwf_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice' && JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice' && !JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallukMetOffice_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan' && JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan' && !JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCalljmaJapan_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }

        else if (localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada' && JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada' && !JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallgemCanada_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia' && JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia' && !JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallbomAustralia_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina' && JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina' && !JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallcmaChina_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'knmiNetherlands' && JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'knmiNetherlands' && !JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCallknmiNetherlands_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }
        else if (localStorage.getItem('selectedMainWeatherProvider') === 'dmiDenmark' && JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`))) {
            const data = JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
                } else {
                    temp = Math.round(data.current.temperature_2m);
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'));
            }
        } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dmiDenmark' && !JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`))) {
            const lastCallTimeKey = `DecodeWeatherLastCalldmiDenmark_${location.locationName}`;
            const currentTime = Date.now();
            const lastCallTime = localStorage.getItem(lastCallTimeKey);

            const timeLimit = 5 * 60 * 1000;

            if (!lastCallTime || currentTime - lastCallTime > timeLimit) {
                DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                savedLocationsHolder.innerHTML = '';
                setTimeout(() => {
                    loadSavedLocations();
                }, 1000);
                localStorage.setItem(lastCallTimeKey, currentTime);
            }
        }

        else {

            const data = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`));

            if (data) {
                if (SelectedTempUnit === 'fahrenheit') {
                    temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m))
                } else {
                    temp = Math.round(data.current.temperature_2m)
                }

                icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

                conditionlabel = getWeatherLabelInLangNoAnim(data.current.weather_code, data.current.is_day, localStorage.getItem('AppLanguageCode'))
            }

        }




        savedLocationItem.innerHTML = `
        <savedlocationimg>
            <img src='${icon}' alt="">
        </savedlocationimg>
        <div>
            <p>${location.locationName}</p>
            <span>${conditionlabel}</span>
            <mainCurrenttempSaved>${temp}°</mainCurrenttempSaved>
        </div>
        <md-icon-button class="delete-btn">
            <md-icon icon-filled>delete</md-icon>
        </md-icon-button>`;

        savedLocationItem.querySelector('.delete-btn').addEventListener('click', () => {
            deleteLocation(location.locationName);
            if (localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`WeatherDataOpenMeteo_${location.locationName}`)
                }, 400);
            }

            if (localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`WeatherDataMetNorway_${location.locationName}`)
                }, 400);
            }

            if (localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`WeatherDataAccuCurrent_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`WeatherDataAccuHourly_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`WeatherDataAccuHourly_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`WeatherDataMetNorwayTimeStamp_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`WeatherDataMetNorwayTimeStamp_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`WeatherDataAccuTimeStamp_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`WeatherDataAccuTimeStamp_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`WeatherDataOpenMeteoTimeStamp_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`WeatherDataOpenMeteoTimeStamp_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`AlertData_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`AlertData_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`AstronomyData_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`AstronomyData_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`MoreDetailsData_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`MoreDetailsData_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`DecodeWeatherLastCall_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`DecodeWeatherLastCall_${location.locationName}`)
                }, 400);
            }
            if (localStorage.getItem(`DecodeWeatherLastCallMet_${location.locationName}`)) {
                setTimeout(() => {
                    localStorage.removeItem(`DecodeWeatherLastCallMet_${location.locationName}`)
                }, 400);
            }
                        if (localStorage.getItem(`WeatherDatadwdGermany_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDatadwdGermany_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDataNOAAUS_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDataMeteoFrance_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDataECMWF_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDataECMWF_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDataukMetOffice_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDatajmaJapan_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDatajmaJapan_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDatagemCanada_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDatabomAustralia_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDatacmaChina_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDataknmiNetherlands_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDataknmiNetherlands_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`WeatherDatadmiDenmark_${location.locationName}`)
                            }, 400);
                        }
                        // last call

                        if (localStorage.getItem(`DecodeWeatherLastCalldwdGermany_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCalldwdGermany_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallnoaaUS_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallnoaaUS_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallmeteoFrance_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallmeteoFrance_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallecmwf_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallecmwf_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallukMetOffice_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallukMetOffice_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCalljmaJapan_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCalljmaJapan_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallgemCanada_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallgemCanada_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallbomAustralia_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallbomAustralia_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallcmaChina_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallcmaChina_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCallknmiNetherlands_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCallknmiNetherlands_${location.locationName}`)
                            }, 400);
                        }
                        if (localStorage.getItem(`DecodeWeatherLastCalldmiDenmark_${location.locationName}`)) {
                            setTimeout(() => {
                                localStorage.removeItem(`DecodeWeatherLastCalldmiDenmark_${location.locationName}`)
                            }, 400);
                        }
            savedLocationItem.remove();
        });

        const savelocationtouch = document.createElement('savelocationtouch');
        const md_rippleSaveLocationTouch = document.createElement('md-ripple');
        savelocationtouch.appendChild(md_rippleSaveLocationTouch);

        savelocationtouch.addEventListener('click', () => {

            if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`));
                const renderFromSavedDataMet = JSON.parse(localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataMetTimstamp = localStorage.getItem(`WeatherDataMetNorwayTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }

                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                const currentData = renderFromSavedDataMet.properties.timeseries[0];
                const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(0, 24);


                if (renderFromSavedDataMet) {
                    renderCurrentDataMetNorway(currentData, savedLocationItemLat, savedLocationItemLon);
                    renderHourlyDataMetNorway(hourlyData);
                } else {
                    DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                }

                createHourlyDataCount(renderFromSavedData)

                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0])
                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                UvIndex(renderFromSavedData.hourly.uv_index[0])
                DailyWeather(renderFromSavedData.daily)
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly))
                document.querySelector('.data_provider_name_import').innerHTML = 'Data by Met norway';

                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));

                AirQuaility(AirQuailityData)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }

                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
                }, 1000);


            } else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {

                const data = JSON.parse(localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`));
                const dataHourly = JSON.parse(localStorage.getItem(`WeatherDataAccuHourly_${location.locationName}`));
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const dataTimstamp = localStorage.getItem(`WeatherDataAccuTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }

                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)
                if (data) {
                    DisplayCurrentAccuweatherData(data, savedLocationItemLat, savedLocationItemLon)
                    DisplayHourlyAccuweatherData(dataHourly)
                } else {
                    DecodeWeather(savedLocationItemLat, savedLocationItemLon, location.locationName);
                }

                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));

                AirQuaility(AirQuailityData)

                createHourlyDataCount(renderFromSavedData)

                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0])
                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                UvIndex(renderFromSavedData.hourly.uv_index[0])
                DailyWeather(renderFromSavedData.daily)
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly))
                document.querySelector('.data_provider_name_import').innerHTML = 'Data by Accuweather';

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }

                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(dataTimstamp)}`;
                }, 1000);

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataMeteoFranceTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'dwdGermany'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataDWDGermany_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataDWDGermanyTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataNOAAUSTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataECMWF_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataECMWFTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataukMetOfficeTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataJMAJapanTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatagemCanadaTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatabomAustraliaTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatagemCanadaTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatacmaChinaTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'knmiNetherlands'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataknmiNetherlandsTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            } else if(localStorage.getItem('selectedMainWeatherProvider') === 'dmiDenmark'){
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatadmiDenmarkTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));



            } else {
                const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`));
                const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${location.locationName}`));
                const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${location.locationName}`));
                const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${location.locationName}`));
                const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${location.locationName}`))
                const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataOpenMeteoTimeStamp_${location.locationName}`)
                const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${location.locationName}`))

                if (SavedalertData) {
                    FetchAlertRender(SavedalertData)
                }
                function timeAgo(timestamp) {
                    const now = new Date();
                    const updatedTime = new Date(timestamp);
                    const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                    const units = [
                        { name: "day", seconds: 86400 },
                        { name: "hr.", seconds: 3600 },
                        { name: "min.", seconds: 60 },
                        { name: "sec.", seconds: 1 }
                    ];

                    for (let unit of units) {
                        const amount = Math.floor(diffInSeconds / unit.seconds);
                        if (amount >= 1) {
                            return `${amount} ${unit.name} ago`;
                        }
                    }

                    return "now";
                }


                setTimeout(() => {
                    document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
                }, 1000);


                localStorage.setItem('currentLong', savedLocationItemLon)
                localStorage.setItem('currentLat', savedLocationItemLat)

                astronomyDataRender(AstronomyData)
                MoreDetailsRender(MoreDetailsData)

                createHourlyDataCount(renderFromSavedData)
                AirQuaility(AirQuailityData)

                DailyWeather(renderFromSavedData.daily)
                HourlyWeather(renderFromSavedData)
                CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], savedLocationItemLat, savedLocationItemLon)

                localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
                localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
                localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

            }





            localStorage.setItem('CurrentLocationName', location.locationName)

            document.getElementById('city-name').innerHTML = location.locationName;
            document.getElementById('forecast').scrollLeft = 0;
            document.getElementById('weather_wrap').scrollTop = 0;
            window.history.back();
            hideLoader()

        });
        savedLocationItem.appendChild(savelocationtouch);




        savedLocationsHolder.append(savedLocationItem);
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
        document.getElementById('edit_saved_locations').selected = false;

    } else {
        document.querySelector('.savedLocations').hidden = false;

    }
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
            checkTopScroll()
            cityList.innerHTML = '';
            cityInput.value = '';

        }, 300);
    }
});

function getCountryName(code) {
    return countryNames[code] || "Unknown Country";
}





function getCurrentLocationWeather() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(position => {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;
            DecodeWeather(latitude, longitude); // Call getWeatherByCoordinates with coordinates


        }, handleGeolocationError);
    } else {
        console.error('Geolocation is not supported by this browser.');
    }
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





function refreshWeather() {
    if (navigator.onLine) {
        const latSend = localStorage.getItem('currentLat')
        const longSend = localStorage.getItem('currentLong')
        const CurrentLocationName = localStorage.getItem('CurrentLocationName');

        DecodeWeather(latSend, longSend, CurrentLocationName, `Refreshed_${CurrentLocationName}`)

        //        setTimeout(() =>{
        //        sendThemeToAndroid('StopRefresh')
        //        }, 1000)



    } else {

        return
    }
}

function sendThemeToAndroid(theme) {

    AndroidInterface.updateStatusBarColor(theme);
};
function Toast(toastText, time) {
    ToastAndroidShow.ShowToast(toastText, time);
}



// map

var map;

function darkModeTileLayer(urlTemplate) {
    const filterStyle = 'invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%)';

    return window.L.tileLayer(urlTemplate, {
    }).addTo(map).getContainer().style.filter = filterStyle;
}

function RenderSearhMap() {
    const latDif = localStorage.getItem('currentLat');
    const longDif = localStorage.getItem('currentLong');

    map = window.L.map('map', {
        center: [latDif, longDif],
        zoom: 8,
        zoomControl: false
    });

    darkModeTileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png');

    var marker = window.L.marker([latDif, longDif]).addTo(map);

    map.on('click', function (e) {
        var lat = e.latlng.lat;
        var lon = e.latlng.lng;

        marker.setLatLng([lat, lon]);
        window.history.back();

        document.getElementById('search-container').style.opacity = '0';

        setTimeout(() => {
            window.history.back();
        }, 500);

        DecodeWeather(lat, lon);


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








function checkNoInternet() {
    if (navigator.onLine) {
        localStorage.setItem('DeviceOnline', 'Yes');
    } else {
        const offlineData = localStorage.getItem('DefaultLocation');
        if (offlineData) {
            const parsedOfflineData = JSON.parse(offlineData);
            const weatherDataKey = `WeatherDataOpenMeteo_${parsedOfflineData.name}`;
            const weatherData = localStorage.getItem(weatherDataKey);

            if (weatherData) {
                setTimeout(() => {
                    ReturnHomeLocation()
                }, 1000);
            } else {
                ShowError();
            }
        } else {
            ShowError();
        }
    }
}

checkNoInternet();







document.addEventListener('DOMContentLoaded', async function () {

    const currentVersion = 'v1.8.7';
    const githubRepo = 'PranshulGG/WeatherMaster';
    const releasesUrl = `https://api.github.com/repos/${githubRepo}/releases/latest`;

    try {
        const response = await fetch(releasesUrl);
        if (!response.ok) throw new Error('Network response was not ok.');

        const data = await response.json();
        const latestVersion = data.tag_name;



        if (latestVersion !== currentVersion) {


            if (localStorage.getItem('HideNewUpdateToast') === 'true') {
                document.querySelector('.new_ver_download').hidden = false;

                setTimeout(() => {
                    document.querySelector('.new_ver_download').hidden = true;
                }, 5000);
            } else {
                document.querySelector('.new_ver_download').hidden = false;
            }


        } else {
            document.querySelector('.new_ver_download').hidden = true;
            return
        }
    } catch (error) {
    }
});





const scrollView = document.querySelector('.insights');


const scrollIndicators = document.getElementById('scroll-indicators');

function saveScrollPosition() {
    const scrollPosition = scrollView.scrollLeft;
    localStorage.setItem('scrollPosition', scrollPosition);
}

function restoreScrollPosition() {
    const savedScrollPosition = localStorage.getItem('scrollPosition');
    if (savedScrollPosition) {
        scrollView.scrollLeft = savedScrollPosition;
    }
}

function createScrollDots() {
    const sections = document.querySelectorAll('.insights_item');


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
}

const updateActiveIndicator = () => {
    const scrollPosition = Math.round(scrollView.scrollLeft / scrollView.offsetWidth);
    const dotsValue = document.querySelectorAll('.dotValue');
    dotsValue.forEach((dotValue, index) => {
        if (index === scrollPosition) {
            dotValue.style.transform = 'scale(1)';
        } else {
            dotValue.style.transform = 'scale(0)';

        }
    });

};

function debounce(func, delay) {
    let inDebounce;
    return function () {
        const context = this, args = arguments;
        clearTimeout(inDebounce);
        inDebounce = setTimeout(() => func.apply(context, args), delay);
    };
}

let isSwipeDisabledHori = false;

scrollView.addEventListener('touchstart', () => {
    if (!isSwipeDisabledHori) {
        sendThemeToAndroid("DisableSwipeRefresh");
        isSwipeDisabledHori = true;
    }
});

scrollView.addEventListener('touchend', () => {
    setTimeout(() => {
        checkTopScroll()
        isSwipeDisabledHori = false;
    }, 400);
});

let isSwipeDisabledHoriForecast = false;

document.getElementById('forecast').addEventListener('touchstart', () => {
    if (!isSwipeDisabledHoriForecast) {
        sendThemeToAndroid("DisableSwipeRefresh");
        isSwipeDisabledHoriForecast = true;
    }
});

document.getElementById('forecast').addEventListener('touchend', () => {
    setTimeout(() => {
        checkTopScroll()
        isSwipeDisabledHoriForecast = false;
    }, 400);
});




scrollView.addEventListener('scroll', debounce(saveScrollPosition, 200));
scrollView.addEventListener('scroll', updateActiveIndicator);

document.addEventListener('DOMContentLoaded', () => {
    createScrollDots()
    updateActiveIndicator();
    restoreScrollPosition();
});


document.getElementById('edit_saved_locations').addEventListener('click', () => {

    const allDeleteBtns = document.querySelectorAll('.delete-btn');

    const allTempsSaved = document.querySelectorAll('maincurrenttempsaved');

    if (document.getElementById('edit_saved_locations').selected) {

        allDeleteBtns.forEach((deletebtns) => {
            deletebtns.style.display = 'flex'
        })

        allTempsSaved.forEach((TempsSaved) => {
            TempsSaved.style.display = 'none'
        })

    } else {
        allDeleteBtns.forEach((deletebtns) => {
            deletebtns.style.display = 'none'
        })

        allTempsSaved.forEach((TempsSaved) => {
            TempsSaved.style.display = 'block'
        })
    }

});


function ReturnHomeLocation() {

    const Locations = JSON.parse(localStorage.getItem('DefaultLocation'));

    document.getElementById('currentLocationName').textContent = Locations.name

    if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`));
        const renderFromSavedDataMet = JSON.parse(localStorage.getItem(`WeatherDataMetNorway_${Locations.name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
        const renderFromSavedDataMetTimstamp = localStorage.getItem(`WeatherDataMetNorwayTimeStamp_${Locations.name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        } else {
            document.querySelector('.weatherCommentsDiv').classList.remove('alertOpened');
            document.querySelector('.excessiveHeat').hidden = true;
        }
        localStorage.setItem('currentLong', Locations.lon)
        localStorage.setItem('currentLat', Locations.lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        const currentData = renderFromSavedDataMet.properties.timeseries[0];
        const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(0, 24);

        renderCurrentDataMetNorway(currentData, Locations.lat, Locations.lon);
        renderHourlyDataMetNorway(hourlyData);
        createHourlyDataCount(renderFromSavedData)

        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0])
        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        UvIndex(renderFromSavedData.hourly.uv_index[0])
        DailyWeather(renderFromSavedData.daily)
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly))
        document.querySelector('.data_provider_name_import').innerHTML = 'Data by Met norway';

        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));

        AirQuaility(AirQuailityData)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }

        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
        }, 1000);




    } else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {

        const data = JSON.parse(localStorage.getItem(`WeatherDataAccuCurrent_${Locations.name}`));
        const dataHourly = JSON.parse(localStorage.getItem(`WeatherDataAccuHourly_${Locations.name}`));
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
        const dataTimstamp = localStorage.getItem(`WeatherDataAccuTimeStamp_${Locations.name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        } else {
            document.querySelector('.weatherCommentsDiv').classList.remove('alertOpened');
            document.querySelector('.excessiveHeat').hidden = true;
        }
        localStorage.setItem('currentLong', Locations.lon)
        localStorage.setItem('currentLat', Locations.lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)
        DisplayCurrentAccuweatherData(data, Locations.lat, Locations.lon)
        DisplayHourlyAccuweatherData(dataHourly)

        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));

        AirQuaility(AirQuailityData)

        createHourlyDataCount(renderFromSavedData)

        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0])
        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        UvIndex(renderFromSavedData.hourly.uv_index[0])
        DailyWeather(renderFromSavedData.daily)
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly))
        document.querySelector('.data_provider_name_import').innerHTML = 'Data by Accuweather';

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }

        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(dataTimstamp)}`;
        }, 1000);

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataMeteoFranceTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'dwdGermany'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataDWDGermany_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataDWDGermanyTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataNOAAUSTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataECMWF_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataECMWFTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataukMetOfficeTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataJMAJapanTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatagemCanadaTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatabomAustraliaTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatagemCanadaTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatacmaChinaTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'knmiNetherlands'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataknmiNetherlandsTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        } else if(localStorage.getItem('selectedMainWeatherProvider') === 'dmiDenmark'){
            const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${Locations.name}`));
            const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
            const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
            const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
            const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
            const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatadmiDenmarkTimeStamp_${Locations.name}`)
            const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

            if (SavedalertData) {
                FetchAlertRender(SavedalertData)
            }
            function timeAgo(timestamp) {
                const now = new Date();
                const updatedTime = new Date(timestamp);
                const diffInSeconds = Math.floor((now - updatedTime) / 1000);

                const units = [
                    { name: "day", seconds: 86400 },
                    { name: "hr.", seconds: 3600 },
                    { name: "min.", seconds: 60 },
                    { name: "sec.", seconds: 1 }
                ];

                for (let unit of units) {
                    const amount = Math.floor(diffInSeconds / unit.seconds);
                    if (amount >= 1) {
                        return `${amount} ${unit.name} ago`;
                    }
                }

                return "now";
            }


            setTimeout(() => {
                document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
            }, 1000);


            localStorage.setItem('currentLong', Locations.lon)
            localStorage.setItem('currentLat', Locations.lat)

            astronomyDataRender(AstronomyData)
            MoreDetailsRender(MoreDetailsData)

            createHourlyDataCount(renderFromSavedData)
            AirQuaility(AirQuailityData)

            DailyWeather(renderFromSavedData.daily)
            HourlyWeather(renderFromSavedData)
            CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

            localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
            localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
            localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));




    } else {
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${Locations.name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${Locations.name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${Locations.name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${Locations.name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataOpenMeteoTimeStamp_${Locations.name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${Locations.name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        } else {
            document.querySelector('.weatherCommentsDiv').classList.remove('alertOpened');
            document.querySelector('.excessiveHeat').hidden = true;
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', Locations.lon)
        localStorage.setItem('currentLat', Locations.lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], Locations.lat, Locations.lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    }




    if (Locations.name === 'CurrentDeviceLocation') {
        document.getElementById('city-name').innerHTML = 'Current location';
    } else {
        document.getElementById('city-name').innerHTML = Locations.name;
    }
    document.getElementById('SelectedLocationText').innerHTML = Locations.name;
    localStorage.setItem('CurrentLocationName', Locations.name)


    hideLoader()

}


if (localStorage.getItem('removedOldSavedLocations') && localStorage.getItem('removedOldSavedLocations') === 'removed') {
    localStorage.removeItem('savedLocations');

    setTimeout(() => {
        localStorage.getItem('removedOldSavedLocations', 'removed')
    }, 300);
} else {
    localStorage.getItem('removedOldSavedLocations', 'removed')

}


document.getElementById('open_temp_trend').addEventListener('click', () => {
    document.querySelector('.temp_trend_bars').classList.toggle('trends_opened')

    if (document.querySelector('.temp_trend_bars').classList.contains("trends_opened")) {
        document.querySelector('.trend_arrow_temp').innerHTML = 'keyboard_arrow_up'

    } else {
        document.querySelector('.trend_arrow_temp').innerHTML = 'keyboard_arrow_down'
    }

});

function setChart() {
    if (localStorage.getItem('useBarChart') && localStorage.getItem('useBarChart') === 'true') {
        createTempTrendsChartBar()
    } else {
        createTempTrendsChart()
    }
}

function handleStorageChangeChart(event) {
    if (event.key === 'useBarChart') {

        setTimeout(() => {
            setChart()
        }, 500);

    }
}

window.addEventListener('storage', handleStorageChangeChart);

function createHourlyDataCount(data) {


    const weatherCodeGroups = {
        "0": [0],
        "1": [1],
        "2": [2],
        "3": [3],
        "45": [45],
        "48": [48],
        "51": [51],
        "53": [53],
        "55": [55],
        "56": [56],
        "57": [57],
        "61": [61],
        "63": [63],
        "65": [65],
        "66": [66],
        "67": [67],
        "71": [71],
        "73": [73],
        "75": [75],
        "77": [77],
        "80": [80],
        "81": [81],
        "82": [82],
        "85": [85],
        "86": [86],
        "95": [95],
        "96": [96],
        "99": [99]
    };


    let groupCounts = {};
    Object.keys(weatherCodeGroups).forEach(group => {
        groupCounts[group] = 0;
    });

    data.hourly.weather_code.forEach((code) => {
        if (groupCounts[code] !== undefined) {
            groupCounts[code]++;
        }
    });

    const mostFrequentGroup = Object.keys(groupCounts).reduce((a, b) => groupCounts[a] > groupCounts[b] ? a : b);
    const selectedWeatherCode = mostFrequentGroup;


    ReportFromhourly(selectedWeatherCode);

    let Visibility;
    let VisibilityUnit;


    if (SelectedVisibiltyUnit === 'mileV') {
        Visibility = Math.round(data.hourly.visibility[0] / 1609.34);
        VisibilityUnit = 'miles'
    } else {
        Visibility = Math.round(data.hourly.visibility[0] / 1000);
        VisibilityUnit = 'km'

    }


    document.getElementById('unit_visibility').innerHTML = VisibilityUnit
    document.getElementById('min-temp').innerHTML = Visibility

    let DewPointTemp


    if (SelectedTempUnit === 'fahrenheit') {
        DewPointTemp = Math.round(celsiusToFahrenheit(data.hourly.dew_point_2m[0]))

    } else {
        DewPointTemp = Math.round(data.hourly.dew_point_2m[0])

    }

    document.getElementById('dew_percentage').innerHTML = DewPointTemp + '°'
}


// load location on request

function LoadLocationOnRequest(lat, lon, name) {


    if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {
        showLoader();
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${name}`));
        const renderFromSavedDataMet = JSON.parse(localStorage.getItem(`WeatherDataMetNorway_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataMetTimstamp = localStorage.getItem(`WeatherDataMetNorwayTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        const currentData = renderFromSavedDataMet.properties.timeseries[0];
        const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(0, 24);

        renderCurrentDataMetNorway(currentData, lat, lon);
        renderHourlyDataMetNorway(hourlyData);
        createHourlyDataCount(renderFromSavedData)

        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0])
        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        UvIndex(renderFromSavedData.hourly.uv_index[0])
        DailyWeather(renderFromSavedData.daily)
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly))
        document.querySelector('.data_provider_name_import').innerHTML = 'Data by Met norway';

        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));

        AirQuaility(AirQuailityData)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }

        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
        }, 1000);


        hideLoader()

    } else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {
        showLoader();
        const data = JSON.parse(localStorage.getItem(`WeatherDataAccuCurrent_${name}`));
        const dataHourly = JSON.parse(localStorage.getItem(`WeatherDataAccuHourly_${name}`));
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const dataTimstamp = localStorage.getItem(`WeatherDataAccuTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)
        DisplayCurrentAccuweatherData(data, lat, lon)
        DisplayHourlyAccuweatherData(dataHourly)

        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));

        AirQuaility(AirQuailityData)

        createHourlyDataCount(renderFromSavedData)

        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0])
        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        UvIndex(renderFromSavedData.hourly.uv_index[0])
        DailyWeather(renderFromSavedData.daily)
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly))
        document.querySelector('.data_provider_name_import').innerHTML = 'Data by Accuweather';

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }

        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(dataTimstamp)}`;
        }, 1000);

        hideLoader()

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataMeteoFranceTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'dwdGermany'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataDWDGermany_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataDWDGermanyTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataNOAAUSTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataECMWF_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataECMWFTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataukMetOfficeTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataJMAJapanTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);


        localStorage.setItem('currentLong', Locations.lon)
        localStorage.setItem('currentLat', Locations.lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatagemCanadaTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatabomAustraliaTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);


        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatagemCanadaTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);


        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatacmaChinaTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'knmiNetherlands'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataknmiNetherlandsTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

    } else if(localStorage.getItem('selectedMainWeatherProvider') === 'dmiDenmark'){
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDatadmiDenmarkTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));




    } else {
        showLoader();
        const renderFromSavedData = JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${name}`));
        const dataHourlyFull = JSON.parse(localStorage.getItem(`HourlyWeatherCache_${name}`));
        const AirQuailityData = JSON.parse(localStorage.getItem(`AirQuality_${name}`));
        const MoreDetailsData = JSON.parse(localStorage.getItem(`MoreDetailsData_${name}`));
        const AstronomyData = JSON.parse(localStorage.getItem(`AstronomyData_${name}`))
        const renderFromSavedDataTimstamp = localStorage.getItem(`WeatherDataOpenMeteoTimeStamp_${name}`)
        const SavedalertData = JSON.parse(localStorage.getItem(`AlertData_${name}`))

        if (SavedalertData) {
            FetchAlertRender(SavedalertData)
        }
        function timeAgo(timestamp) {
            const now = new Date();
            const updatedTime = new Date(timestamp);
            const diffInSeconds = Math.floor((now - updatedTime) / 1000);

            const units = [
                { name: "day", seconds: 86400 },
                { name: "hr.", seconds: 3600 },
                { name: "min.", seconds: 60 },
                { name: "sec.", seconds: 1 }
            ];

            for (let unit of units) {
                const amount = Math.floor(diffInSeconds / unit.seconds);
                if (amount >= 1) {
                    return `${amount} ${unit.name} ago`;
                }
            }

            return "now";
        }


        setTimeout(() => {
            document.getElementById('last_updated').innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);



        localStorage.setItem('currentLong', lon)
        localStorage.setItem('currentLat', lat)

        astronomyDataRender(AstronomyData)
        MoreDetailsRender(MoreDetailsData)

        createHourlyDataCount(renderFromSavedData)
        AirQuaility(AirQuailityData)

        DailyWeather(renderFromSavedData.daily)
        HourlyWeather(renderFromSavedData)
        CurrentWeather(renderFromSavedData.current, renderFromSavedData.daily.sunrise[0], renderFromSavedData.daily.sunset[0], lat, lon)

        localStorage.setItem('DailyWeatherCache', JSON.stringify(renderFromSavedData.daily));
        localStorage.setItem('CurrentHourlyCache', JSON.stringify(renderFromSavedData.hourly));
        localStorage.setItem('HourlyWeatherCache', JSON.stringify(dataHourlyFull));

        hideLoader()
    }
}

function checkTopScroll() {
    if (document.getElementById('weather_wrap').scrollTop === 0) {
        sendThemeToAndroid('EnableSwipeRefresh')
    } else {
        sendThemeToAndroid('DisableSwipeRefresh')
    }
}

// check if the location data is available for saved locations

function CheckLocationDatas(){
    const allSavedLocations = JSON.parse(localStorage.getItem('savedLocations')) || [];

    allSavedLocations.forEach((allSavedLocation) =>{
        if (localStorage.getItem('selectedMainWeatherProvider') === 'Met norway') {

            if(JSON.parse(localStorage.getItem(`WeatherDataMetNorway_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('ApiForAccu') && localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather') {
            if(JSON.parse(localStorage.getItem(`WeatherDataAccuCurrent_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }


          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'meteoFrance') {
            if(JSON.parse(localStorage.getItem(`WeatherDataMeteoFrance_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dwdGermany') {
            if(JSON.parse(localStorage.getItem(`WeatherDatadwdGermany_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'noaaUS') {
            if(JSON.parse(localStorage.getItem(`WeatherDataNOAAUS_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ecmwf') {
            if(JSON.parse(localStorage.getItem(`WeatherDataECMWF_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'ukMetOffice') {
            if(JSON.parse(localStorage.getItem(`WeatherDataukMetOffice_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'jmaJapan') {
            if(JSON.parse(localStorage.getItem(`WeatherDataJMAJapan_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'gemCanada') {
            if(JSON.parse(localStorage.getItem(`WeatherDatagemCanada_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }


          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'bomAustralia') {
            if(JSON.parse(localStorage.getItem(`WeatherDatabomAustralia_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'cmaChina') {
            if(JSON.parse(localStorage.getItem(`WeatherDatacmaChina_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'knmiNetherlands') {
            if(JSON.parse(localStorage.getItem(`WeatherDataknmiNetherlands_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else if (localStorage.getItem('selectedMainWeatherProvider') === 'dmiDenmark') {
            if(JSON.parse(localStorage.getItem(`WeatherDatadmiDenmark_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          } else {

            if(JSON.parse(localStorage.getItem(`WeatherDataOpenMeteo_${allSavedLocation.locationName}`))){
                document.getElementById('location_data_loader').hidden = true;
            } else{
                document.getElementById('location_data_loader').hidden = false;
            }

          }
    })

}

document.addEventListener('DOMContentLoaded', () =>{
    CheckLocationDatas()
});
