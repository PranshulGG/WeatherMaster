
const DefaultLocation = JSON.parse(localStorage.getItem('DefaultLocation'));


let currentApiKeyIndex = 0;

let currentKeyMoonIndex = 0;
let currentAstronomyKeyIndex = 0;


function handleStorageChange(event) {
    if (event.key === 'SelectedTempUnit' ||
        event.key === 'SelectedWindUnit' ||
        event.key === 'selectedVisibilityUnit' ||
        event.key === 'selectedTimeMode'||
        event.key === 'selectedPrecipitationUnit' ||
        event.key === 'DefaultLocation'||
        event.key === 'UseBackgroundAnimations'||
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
            DecodeWeather(currentLocation.latitude, currentLocation.longitude);

            document.getElementById('city-name').innerHTML = 'Current location';
            document.getElementById('SelectedLocationText').innerHTML = 'Current location';
            localStorage.setItem('CurrentLocationName', 'Current location')
                  document.getElementById('currentLocationName').textContent = 'Current location';

    });
}
}


if(DefaultLocation){
if(DefaultLocation.name === 'CurrentDeviceLocation'){
    useAutoCurrentLocation()
    sendThemeToAndroid("ReqLocation")
    document.querySelector('.currentLocationdiv').hidden = false;
} else if(DefaultLocation.lat && DefaultLocation.lon){
    DecodeWeather(DefaultLocation.lat, DefaultLocation.lon)
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

    let debounceTimeout;

    cityInput.addEventListener('input', () => {
        const searchTerm = cityInput.value.trim();
        document.querySelector('.currentLocationdiv').hidden = true;
        document.querySelector('.savedLocations').hidden = true;

        clearTimeout(debounceTimeout);

        if (searchTerm && searchTerm.length > 2) {
            document.getElementById('cityLoader').hidden = false;

            debounceTimeout = setTimeout(() => {
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
                const saveBtnLocation = document.createElement('md-text-button');
                saveBtnLocation.textContent = 'Save';

                if (!savedLocationsSet.has(suggestionText)) {
                    saveBtnLocation.hidden = false;
                } else {
                    saveBtnLocation.hidden = true;
                }

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
                    DecodeWeather(result.latitude, result.longitude);
                    cityList.innerHTML = '';
                    cityInput.value = '';
                    document.getElementById('city-name').innerHTML = suggestionText;
                    document.querySelector('.focus-input').blur();
                    document.getElementById('forecast').scrollLeft = 0;
                    document.getElementById('weather_wrap').scrollTop = 0;
                    window.history.back();

                    setTimeout(() => {
                        cityInput.dispatchEvent(new Event('input'));
                    }, 200);
                });

                saveBtnLocation.addEventListener('click', () => {
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





                    savedLocationItem.innerHTML = `
                    <savedlocationimg>
                      <md-icon icon-outlined>my_location</md-icon>
                    </savedlocationimg>
                    <div>
                        <p>${location.locationName}</p>
                    </div>
                    <flex></flex>
                    <md-icon-button class="delete-btn">
                        <md-icon icon-filled>delete</md-icon>
                    </md-icon-button>
                `;





                savedLocationItem.querySelector('.delete-btn').addEventListener('click', () => {
                    deleteLocation(location.locationName);
                    savedLocationItem.remove();
                });



                const savelocationtouch = document.createElement('savelocationtouch');
                const md_rippleSaveLocationTouch = document.createElement('md-ripple');
                savelocationtouch.appendChild(md_rippleSaveLocationTouch);

                savelocationtouch.addEventListener('click', () => {
                    DecodeWeather(savedLocationItemLat, savedLocationItemLon)
            document.getElementById('city-name').innerHTML = location.locationName
            document.getElementById('forecast').scrollLeft = 0;
            document.getElementById('weather_wrap').scrollTop = 0;
                    window.history.back();
                });











                savedLocationsHolder.append(savedLocationItem);
                savedLocationItem.appendChild(savelocationtouch);
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





function refreshWeather(){
    const latSend = localStorage.getItem('currentLat')
    const longSend = localStorage.getItem('currentLong')

    DecodeWeather(latSend, longSend)

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

    map.on('click', function(e) {
        var lat = e.latlng.lat;
        var lon = e.latlng.lng;

        marker.setLatLng([lat, lon]);
        window.history.back();

        document.getElementById('search-container').style.opacity = '0';

        setTimeout(() => {
            window.history.back();
        }, 500);

        DecodeWeather(lat, lon);

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

        const currentVersion = 'v1.7.2';
            const githubRepo = 'PranshulGG/WeatherMaster';
            const releasesUrl = `https://api.github.com/repos/${githubRepo}/releases/latest`;

            try {
                const response = await fetch(releasesUrl);
                if (!response.ok) throw new Error('Network response was not ok.');

                const data = await response.json();
                const latestVersion = data.tag_name;



            if (latestVersion !== currentVersion) {


                           if(localStorage.getItem('HideNewUpdateToast') === 'true'){
                               document.querySelector('.new_ver_download').hidden = false;

                               setTimeout(()=>{
                                   document.querySelector('.new_ver_download').hidden = true;
                               }, 5000);
                           } else{
                               document.querySelector('.new_ver_download').hidden = false;
                           }


                } else {
                document.querySelector('.new_ver_download').hidden = true;
                    return
                }
            }catch (error) {
            }
        });





const scrollView = document.querySelector('.insights');


const scrollIndicators = document.getElementById('scroll-indicators');

function createScrollDots(){
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


scrollView.addEventListener('scroll', updateActiveIndicator);

document.addEventListener('DOMContentLoaded', ()=>{
createScrollDots()
updateActiveIndicator();

});



