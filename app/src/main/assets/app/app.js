
const DefaultLocation = JSON.parse(localStorage.getItem("DefaultLocation"));
const savedLocationsNameCustom = JSON.parse(localStorage.getItem("savedLocationsCustomName"));

let currentApiKeyIndex = 0;

let currentKeyMoonIndex = 0;
let currentAstronomyKeyIndex = 0;

function WaitBeforeRefresh() {
  ShowSnackMessage.ShowSnack(
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "Please_wait_before_refreshing_again."
    ),
    "long"
  );
}
function ShowError() {
  document.querySelector(".no_internet_error").hidden = false;
}

let currentLocation = null;

function useAutoCurrentLocation() {
  showLoader();
  if ("geolocation" in navigator) {
    navigator.geolocation.getCurrentPosition((position) => {
      currentLocation = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      };

      document.getElementById("city-name").innerHTML = getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "current_location"
      );
      document.getElementById("SelectedLocationText").innerHTML =
        getTranslationByLang(
          localStorage.getItem("AppLanguageCode"),
          "current_location"
        );
      localStorage.setItem("CurrentLocationName", "Current location");
      document.getElementById("currentLocationName").textContent =
        getTranslationByLang(
          localStorage.getItem("AppLanguageCode"),
          "current_location"
        );

      if (
        !DefaultLocation ||
        DefaultLocation.name === "CurrentDeviceLocation"
      ) {
        localStorage.setItem(
          "DefaultLocation",
          JSON.stringify({
            lat: currentLocation.latitude,
            lon: currentLocation.longitude,
            name: "CurrentDeviceLocation",
          })
        );
      }

      localStorage.setItem("deviceLat", currentLocation.latitude);
      localStorage.setItem("devicelon", currentLocation.longitude);


      setTimeout(() =>{
        DecodeWeather(
          currentLocation.latitude,
          currentLocation.longitude,
          "CurrentDeviceLocation"
        );
      }, 200);
    });
  }
}

if (navigator.onLine) {
  if (DefaultLocation) {
    if (DefaultLocation.name === "CurrentDeviceLocation") {
      const offlineData = localStorage.getItem("DefaultLocation");
      const parsedOfflineData = JSON.parse(offlineData);
      let weatherDataKey;
      if (
        localStorage.getItem("selectedMainWeatherProvider") === "Met norway"
      ) {
        weatherDataKey = `WeatherDataMetNorwayTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("ApiForAccu") &&
        localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
      ) {
        weatherDataKey = `WeatherDataAccuCurrentTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
      ) {
        weatherDataKey = `WeatherDataMeteoFranceTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
      ) {
        weatherDataKey = `WeatherDataDWDGermanyTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "noaaUS"
      ) {
        weatherDataKey = `WeatherDataNOAAUSTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "ecmwf"
      ) {
        weatherDataKey = `WeatherDataECMWFTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
      ) {
        weatherDataKey = `WeatherDataukMetOfficeTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
      ) {
        weatherDataKey = `WeatherDataJMAJapanTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
      ) {
        weatherDataKey = `WeatherDatagemCanadaTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
      ) {
        weatherDataKey = `WeatherDatabomAustraliaTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
      ) {
        weatherDataKey = `WeatherDatacmaChinaTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") ===
        "knmiNetherlands"
      ) {
        weatherDataKey = `WeatherDataknmiNetherlandsTimeStamp_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
      ) {
        weatherDataKey = `WeatherDatadmiDenmarkTimeStamp_${parsedOfflineData.name}`;
      } else {
        weatherDataKey = `WeatherDataOpenMeteoTimeStamp_${parsedOfflineData.name}`;
      }

      const weatherData = localStorage.getItem(weatherDataKey);


      const providedDate = new Date(weatherData);

      const currentDate = new Date();

      const timeDifference = currentDate - providedDate;

      const timeDifferenceInMinutes = timeDifference / (1000 * 60);

      if (timeDifferenceInMinutes < 40) {

        setTimeout(() => {
          ReturnHomeLocation();
        }, 1000);
          if (localStorage.getItem("selectedMainWeatherProvider") === "Met norway") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Met Norway (Global)`;
          } else if (
            localStorage.getItem("ApiForAccu") &&
            localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Accuweather (Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Météo-France (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} DWD (Europe, Global)`;
          } else if (localStorage.getItem("selectedMainWeatherProvider") === "noaaUS") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} NOAA (Americas, Global)`;
          } else if (localStorage.getItem("selectedMainWeatherProvider") === "ecmwf") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} ECMWF (Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} UK Met Office (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} JMA (Asia, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} GEM (Americas, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} BOM (Oceania, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} CMA (Asia, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "knmiEurope"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} KNMI (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "dmiEurope"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} DMI (Europe, Global)`;
          } else {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Open-Meteo (Global)`;
          }
      } else{
        useAutoCurrentLocation();
      }
      document.querySelector(".currentLocationdiv").hidden = false;
      document.getElementById("showDeviceLocation").hidden = false;
    } else if (DefaultLocation.lat && DefaultLocation.lon) {
      const offlineData = localStorage.getItem("DefaultLocation");
        const parsedOfflineData = JSON.parse(offlineData);
        let weatherDataKey;
        if (
          localStorage.getItem("selectedMainWeatherProvider") === "Met norway"
        ) {
          weatherDataKey = `WeatherDataMetNorwayTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("ApiForAccu") &&
          localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
        ) {
          weatherDataKey = `WeatherDataAccuCurrentTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
        ) {
          weatherDataKey = `WeatherDataMeteoFranceTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
        ) {
          weatherDataKey = `WeatherDataDWDGermanyTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "noaaUS"
        ) {
          weatherDataKey = `WeatherDataNOAAUSTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "ecmwf"
        ) {
          weatherDataKey = `WeatherDataECMWFTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
        ) {
          weatherDataKey = `WeatherDataukMetOfficeTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
        ) {
          weatherDataKey = `WeatherDataJMAJapanTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
        ) {
          weatherDataKey = `WeatherDatagemCanadaTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
        ) {
          weatherDataKey = `WeatherDatabomAustraliaTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
        ) {
          weatherDataKey = `WeatherDatacmaChinaTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") ===
          "knmiNetherlands"
        ) {
          weatherDataKey = `WeatherDataknmiNetherlandsTimeStamp_${parsedOfflineData.name}`;
        } else if (
          localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
        ) {
          weatherDataKey = `WeatherDatadmiDenmarkTimeStamp_${parsedOfflineData.name}`;
        } else {
          weatherDataKey = `WeatherDataOpenMeteoTimeStamp_${parsedOfflineData.name}`;
        }

        const weatherData = localStorage.getItem(weatherDataKey);


        const providedDate = new Date(weatherData);

        const currentDate = new Date();

        const timeDifference = currentDate - providedDate;

        const timeDifferenceInMinutes = timeDifference / (1000 * 60);

        if (timeDifferenceInMinutes < 40) {
        setTimeout(() => {
          ReturnHomeLocation();
        }, 1000);
          if (localStorage.getItem("selectedMainWeatherProvider") === "Met norway") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Met Norway (Global)`;
          } else if (
            localStorage.getItem("ApiForAccu") &&
            localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Accuweather (Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Météo-France (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} DWD (Europe, Global)`;
          } else if (localStorage.getItem("selectedMainWeatherProvider") === "noaaUS") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} NOAA (Americas, Global)`;
          } else if (localStorage.getItem("selectedMainWeatherProvider") === "ecmwf") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} ECMWF (Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} UK Met Office (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} JMA (Asia, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} GEM (Americas, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} BOM (Oceania, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} CMA (Asia, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "knmiEurope"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} KNMI (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "dmiEurope"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} DMI (Europe, Global)`;
          } else {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Open-Meteo (Global)`;
          }
        } else{
          DecodeWeather(
            DefaultLocation.lat,
            DefaultLocation.lon,
            DefaultLocation.name
          );

          document.getElementById("city-name").innerHTML = DefaultLocation.name;
          document.getElementById("SelectedLocationText").innerHTML =
            DefaultLocation.name;
          localStorage.setItem("CurrentLocationName", DefaultLocation.name);
          document.getElementById("currentLocationName").textContent =
            DefaultLocation.name;
          document.getElementById("showDeviceLocation").hidden = true;
        }




    }
  } else {
        document.querySelector('.start_up_screen').hidden = false
            sendThemeToAndroid("DisableSwipeRefresh");

  }
} else {
          if (localStorage.getItem("selectedMainWeatherProvider") === "Met norway") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Met Norway (Global)`;
          } else if (
            localStorage.getItem("ApiForAccu") &&
            localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Accuweather (Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Météo-France (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} DWD (Europe, Global)`;
          } else if (localStorage.getItem("selectedMainWeatherProvider") === "noaaUS") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} NOAA (Americas, Global)`;
          } else if (localStorage.getItem("selectedMainWeatherProvider") === "ecmwf") {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} ECMWF (Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} UK Met Office (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} JMA (Asia, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} GEM (Americas, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} BOM (Oceania, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} CMA (Asia, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "knmiEurope"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} KNMI (Europe, Global)`;
          } else if (
            localStorage.getItem("selectedMainWeatherProvider") === "dmiEurope"
          ) {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} DMI (Europe, Global)`;
          } else {
            document.querySelector(".data_provider_name_import").innerHTML =
              `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'data_by')} Open-Meteo (Global)`;
          }



  setTimeout(() => {
    ShowSnackMessage.ShowSnack(
      getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "network_unavailable"
      ),
      "long"
    );

  }, 2000);



}

function handleGeolocationError(error) {
  console.error("Error getting geolocation:", error);
  displayErrorMessage(
    "Error getting geolocation. Please enable location services."
  );
}

document.addEventListener("DOMContentLoaded", () => {
  const cityInput = document.getElementById("city-input");
  const cityopen = document.getElementById("city-open");
  const searchContainer = document.getElementById("search-container");
  const closeButton = document.querySelector(".close_search");
  const openMapPicker = document.getElementById("openMapPicker");

  cityopen.addEventListener("click", () => {
    document.querySelector(".view_device_location").hidden = true;
    sendThemeToAndroid("DisableSwipeRefresh");
    loadSavedLocations();
initializeDragAndDropLocations();

    let savedLocations =
      JSON.parse(localStorage.getItem("savedLocations")) || [];

    localStorage.setItem("DeviceOnline", "Yes");
    searchContainer.style.display = "block";
        document.querySelector(".header_hold").style.pointerEvents = 'none'
    window.history.pushState({ SearchContainerOpen: true }, "");

    setTimeout(() => {
document.querySelector(".close_search").style.pointerEvents = 'auto'

      if (savedLocations.length === 0) {


//        cityInput.focus();
      } else {
      }
    }, 400);
  });

  closeButton.addEventListener("click", () => {
    window.history.back();
  });

  openMapPicker.addEventListener("click", () => {
    document.querySelector(".map_picker").hidden = false;
    window.history.pushState({ MapPickerContainerOpen: true }, "");

    removeMap();

    setTimeout(() => {
      RenderSearhMap();
    }, 400);
  });

  let debounceTimeout;

  cityInput.addEventListener("input", () => {
    document.getElementById("cityLoader").hidden = true;
    document.querySelector(".currentLocationdiv").hidden = false;
    document.querySelector(".savedLocations").hidden = false;

    let savedLocations =
      JSON.parse(localStorage.getItem("savedLocations")) || [];

    if (savedLocations.length === 0) {
      document.querySelector(".savedLocations").hidden = true;
    } else {
      document.querySelector(".savedLocations").hidden = false;
    }
  });

  cityInput.addEventListener("keypress", (event) => {
    const searchTerm = cityInput.value.trim();
    document.querySelector(".currentLocationdiv").hidden = true;

    if (event.key === "Enter") {
      if (searchTerm) {
        document.querySelector(".currentLocationdiv").hidden = true;
        document.querySelector(".savedLocations").hidden = true;
        document.getElementById("cityLoader").hidden = false;
        setTimeout(() => {
          getCitySuggestions(cityInput.value);
        }, 500);
      } else {
        cityList.innerHTML = "";
        document.getElementById("cityLoader").hidden = true;
        document.querySelector(".currentLocationdiv").hidden = false;
      }
    }
  });

  async function getCitySuggestions(query) {
    const url = `https://geocoding-api.open-meteo.com/v1/search?name=${query}&count=5`;

    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      displaySuggestions(data.results);
    } catch (error) {
      console.error("Error fetching city suggestions:", error);
    }
  }

  function displaySuggestions(results) {
    const suggestionsContainer = document.getElementById("city-list");
    clearSuggestions();

    const displayedSuggestions = new Set();

    const savedLocations =
      JSON.parse(localStorage.getItem("savedLocations")) || [];
    const savedLocationsSet = new Set(
      savedLocations.map((location) => location.locationName)
    );

    results.forEach((result) => {
      const city = result.name;
      const state = result.admin1 || result.admin2 || result.admin3;
      const country = result.country;
      const countryCode = result.country_code.toLowerCase();

      const uniqueComponents = [city, state, country].filter(
        (value, index, self) => value && self.indexOf(value) === index
      );
      const suggestionText = uniqueComponents.join(", ");

      if (!displayedSuggestions.has(suggestionText)) {
        displayedSuggestions.add(suggestionText);

        const suggestionItem = document.createElement("div");
        const clickLocation = document.createElement("savelocationtouch");
        // const saveBtnLocation = document.createElement('md-text-button');
        // saveBtnLocation.textContent = 'Save';

        const createGap = document.createElement("flex");
        const countryIcon = document.createElement("span");
        countryIcon.classList.add("fi", "fis", `fi-${countryCode}`);

        suggestionItem.classList.add("suggestion-item");
        const suggestRipple = document.createElement("md-ripple");
        suggestRipple.style = "--md-ripple-pressed-opacity: 0.1;";
        suggestionItem.textContent = suggestionText;

        clickLocation.appendChild(suggestRipple);
        suggestionItem.setAttribute("data-lat", result.latitude);
        suggestionItem.setAttribute("data-lon", result.longitude);

        clickLocation.addEventListener("click", function () {
          DecodeWeather(result.latitude, result.longitude, suggestionText);
          cityList.innerHTML = "";
          cityInput.value = "";
          document.querySelector(".focus-input").blur();
          document.getElementById("forecast").scrollLeft = 0;
          document.getElementById("weather_wrap").scrollTop = 0;
          saveLocationToContainer(
            suggestionText,
            result.latitude,
            result.longitude
          );
          cityList.innerHTML = "";
          cityInput.value = "";

          setTimeout(() => {
            cityInput.dispatchEvent(new Event("input"));
          }, 200);
        });

        suggestionItem.appendChild(countryIcon);
        suggestionItem.appendChild(clickLocation);
        suggestionItem.appendChild(createGap);
        // suggestionItem.appendChild(saveBtnLocation);
        suggestionsContainer.appendChild(suggestionItem);

        setTimeout(() => {
          document.getElementById("cityLoader").hidden = true;
        }, 400);
      }
    });
  }

  function clearSuggestions() {
    const suggestionsContainer = document.getElementById("city-list");
    while (suggestionsContainer.firstChild) {
      suggestionsContainer.removeChild(suggestionsContainer.firstChild);
    }
  }
});

function GetSavedSearchLocation() {
  const searchedItem = JSON.parse(localStorage.getItem(`SearchedItem`));

  if (searchedItem) {
    DecodeWeather(
      searchedItem.latitude,
      searchedItem.longitude,
      searchedItem.LocationName,
      "no_data_render"
    );
    saveLocationToContainer(
      searchedItem.LocationName,
      searchedItem.latitude,
      searchedItem.longitude
    );

        if(!DefaultLocation){
          if (JSON.parse(localStorage.getItem("savedLocations")).length > 0) {
            const makeFirstDefault = JSON.parse(localStorage.getItem("savedLocations"))[0];
            localStorage.setItem(
              "DefaultLocation",
              JSON.stringify({
                lat: makeFirstDefault.lat,
                lon: makeFirstDefault.lon,
                name: makeFirstDefault.locationName,
              })
            );
        sessionStorage.setItem('usedFirstAsDefault', 'true');

                document.querySelector('.start_up_screen').hidden = true;


          } else {
            console.log("No saved locations found.");
          }
        }
  }

  ShowSnackMessage.ShowSnack(
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "loading_location_data"
    ),
    "long"
  );
}

function handleStorageChangeSearch(event) {
  if (event.key === "SearchedItem") {
    setTimeout(() => {
      GetSavedSearchLocation();
    }, 1000);
    document.getElementById('addlocationFab').hidden = true;

  }
}

window.addEventListener("storage", handleStorageChangeSearch);

function saveLocationToContainer(locationName, lat, lon) {
  let savedLocations = JSON.parse(localStorage.getItem("savedLocations")) || [];
  savedLocations.push({ locationName, lat, lon });
  localStorage.setItem("savedLocations", JSON.stringify(savedLocations));

  const savedLocationsHolder = document.querySelector("savedLocationsHolder");

  //    savedLocationsHolder.innerHTML = ''
}

async function loadSavedLocations() {
  const savedLocationsHolder = document.querySelector("savedLocationsHolder");
  savedLocationsHolder.innerHTML = "";

  const savedLocations =
    JSON.parse(localStorage.getItem("savedLocations")) || [];

  if (savedLocations.length === 0) {
    document.querySelector(".savedLocations").hidden = true;
  } else {
    document.querySelector(".savedLocations").hidden = false;
  }

  const currentTime = new Date().getTime();
  let dataIdCounter = 1;

      const uniqueLocations = {};

      const uniqueSavedLocations = savedLocations.filter(location => {
        if (!uniqueLocations[location.locationName]) {
          uniqueLocations[location.locationName] = true;
          return true;
        }
        return false;
      });

        localStorage.setItem("savedLocations", JSON.stringify(uniqueSavedLocations));

  for (const location of uniqueSavedLocations) {
    const savedLocationItem = document.createElement("savedLocationItem");
    savedLocationItem.setAttribute("data-id", dataIdCounter++);
    savedLocationItem.setAttribute("lat", location.lat);
    savedLocationItem.setAttribute("lon", location.lon);
    savedLocationItem.setAttribute("locationLabel", location.locationName);
    const savedLocationItemLat = savedLocationItem.getAttribute("lat");
    const savedLocationItemLon = savedLocationItem.getAttribute("lon");


    let temp = "0";
    let icon = "";
    let showReloadBtn = "hidden";
    let conditionlabel = "Refresh the location";
    if (
      localStorage.getItem("selectedMainWeatherProvider") === "Met norway" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "Met norway" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else if (
      localStorage.getItem("ApiForAccu") &&
      localStorage.getItem("selectedMainWeatherProvider") === "Accuweather" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(data[0].Temperature.Imperial.Value);
        } else {
          temp = Math.round(data[0].Temperature.Metric.Value);
        }

        icon = GetWeatherIconAccu(data[0].WeatherIcon);
        conditionlabel = GetWeatherTextAccuNoAnim(data[0].WeatherIcon);
        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("ApiForAccu") &&
      localStorage.getItem("selectedMainWeatherProvider") === "Accuweather" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataAccuCurrent_${location.locationName}`)
      )
    ) {
      showReloadBtn = "";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataDWDGermany_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataDWDGermany_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataDWDGermany_${location.locationName}`)
      )
    ) {
      showReloadBtn = "";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "noaaUS" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "noaaUS" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`)
      )
    ) {
      showReloadBtn = "";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataMeteoFrance_${location.locationName}`)
      )
    ) {
      showReloadBtn = "";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ecmwf" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ecmwf" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataukMetOffice_${location.locationName}`)
      )
    ) {
      showReloadBtn = "";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan" &&
      JSON.parse(
        localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "gemCanada" &&
      JSON.parse(
        localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "gemCanada" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia" &&
      JSON.parse(
        localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDatabomAustralia_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "cmaChina" &&
      JSON.parse(
        localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "cmaChina" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") ===
        "knmiNetherlands" &&
      JSON.parse(
        localStorage.getItem(
          `WeatherDataknmiNetherlands_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(
          `WeatherDataknmiNetherlands_${location.locationName}`
        )
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") ===
        "knmiNetherlands" &&
      !JSON.parse(
        localStorage.getItem(
          `WeatherDataknmiNetherlands_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = "";
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark" &&
      JSON.parse(
        localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark" &&
      !JSON.parse(
        localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else {
      const data = JSON.parse(
        localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`)
      );

      if (data) {
        if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          localStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      } else {
        showReloadBtn = " ";
      }
    }

    savedLocationItem.innerHTML = `
        <savedlocationimg>
            <img src='${icon}' alt="">
            <md-filled-icon-button class="refresh_saved_location" ${showReloadBtn}>
                <md-icon icon-filled>refresh</md-icon>
            </md-filled-icon-button>
        </savedlocationimg>
        <div>
            <p>${JSON.parse(localStorage.getItem("savedLocationsCustomName"))?.[location.locationName] ||location.locationName}</p>
            <span>${conditionlabel}</span>
            <mainCurrenttempSaved>${temp}°</mainCurrenttempSaved>
        </div>
        <md-icon-button class="delete-btn">
            <md-icon icon-filled>delete</md-icon>
        </md-icon-button>

        `;

    savedLocationItem
      .querySelector(".refresh_saved_location")
      .addEventListener("click", () => {
        if (navigator.onLine) {
          DecodeWeather(
            savedLocationItemLat,
            savedLocationItemLon,
            location.locationName,
            "no_data_render"
          );
          setTimeout(() => {
            document.querySelector("savedLocationsHolder").innerHTML = "";
          }, 200);
        } else {
          ShowSnackMessage.ShowSnack(
            getTranslationByLang(
              localStorage.getItem("AppLanguageCode"),
              "network_unavailable"
            ),
            "long"
          );
        }
      });

    savedLocationItem
      .querySelector(".delete-btn")
      .addEventListener("click", () => {
        const checkDefault = JSON.parse(
          localStorage.getItem("DefaultLocation")
        );

        if (location.locationName === checkDefault.name) {
            ShowSnackMessage.ShowSnack(
              getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "default_location_delete_snack"
              ),
              "long"
            );
          return;
        } else {
          deleteLocation(location.locationName);
          loadSwipeGestures()
          if (
            localStorage.getItem(
              `WeatherDataOpenMeteo_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataOpenMeteo_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataMetNorway_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataMetNorway_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataAccuCurrent_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataAccuCurrent_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataAccuHourly_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataAccuHourly_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataMetNorwayTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataMetNorwayTimeStamp_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataAccuTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataAccuTimeStamp_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataOpenMeteoTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataOpenMeteoTimeStamp_${location.locationName}`
              );
            }, 400);
          }
          if (localStorage.getItem(`AlertData_${location.locationName}`)) {
            setTimeout(() => {
              localStorage.removeItem(`AlertData_${location.locationName}`);
            }, 400);
          }
          if (localStorage.getItem(`AstronomyData_${location.locationName}`)) {
            setTimeout(() => {
              localStorage.removeItem(`AstronomyData_${location.locationName}`);
            }, 400);
          }
          if (
            localStorage.getItem(`MoreDetailsData_${location.locationName}`)
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `MoreDetailsData_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCall_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCall_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallMet_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallMet_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataDWDGermany_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataDWDGermany_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`)
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataNOAAUS_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataMeteoFrance_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataMeteoFrance_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataECMWF_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataukMetOffice_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataukMetOffice_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(`WeatherDatajmaJapan_${location.locationName}`)
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatajmaJapan_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDatagemCanada_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatagemCanada_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDatabomAustralia_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatabomAustralia_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`)
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatacmaChina_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDataknmiNetherlands_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataknmiNetherlands_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `WeatherDatadmiDenmark_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatadmiDenmark_${location.locationName}`
              );
            }, 400);
          }
          // last call

          if (
            localStorage.getItem(
              `DecodeWeatherLastCalldwdGermany_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCalldwdGermany_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallopenmeteo_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallopenmeteo_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallnoaaUS_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallnoaaUS_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallmeteoFrance_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallmeteoFrance_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallecmwf_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallecmwf_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallukMetOffice_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallukMetOffice_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCalljmaJapan_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCalljmaJapan_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallgemCanada_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallgemCanada_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallbomAustralia_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallbomAustralia_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallcmaChina_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallcmaChina_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCallknmiNetherlands_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCallknmiNetherlands_${location.locationName}`
              );
            }, 400);
          }
          if (
            localStorage.getItem(
              `DecodeWeatherLastCalldmiDenmark_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `DecodeWeatherLastCalldmiDenmark_${location.locationName}`
              );
            }, 400);
          }

          // remove any other data
          if (
            localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `HourlyWeatherCache_${location.locationName}`
              );
            }, 400);
          }

          if (localStorage.getItem(`AstronomyData_${location.locationName}`)) {
            setTimeout(() => {
              localStorage.removeItem(`AstronomyData_${location.locationName}`);
            }, 400);
          }

          if (
            localStorage.getItem(`MoreDetailsData_${location.locationName}`)
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `MoreDetailsData_${location.locationName}`
              );
            }, 400);
          }

          if (localStorage.getItem(`AlertData_${location.locationName}`)) {
            setTimeout(() => {
              localStorage.removeItem(`AlertData_${location.locationName}`);
            }, 400);
          }

          if (localStorage.getItem(`AirQuality_${location.locationName}`)) {
            setTimeout(() => {
              localStorage.removeItem(`AirQuality_${location.locationName}`);
            }, 400);
          }

          // time stamps

          if (
            localStorage.getItem(
              `WeatherDataMeteoFranceTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataMeteoFranceTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataDWDGermanyTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataDWDGermanyTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataNOAAUSTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataNOAAUSTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataECMWFTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataECMWFTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataukMetOfficeTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataukMetOfficeTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataJMAJapanTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataJMAJapanTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDatagemCanadaTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatagemCanadaTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDatabomAustraliaTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatabomAustraliaTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDatacmaChinaTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatacmaChinaTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDataknmiNetherlandsTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDataknmiNetherlandsTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            localStorage.getItem(
              `WeatherDatadmiDenmarkTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(() => {
              localStorage.removeItem(
                `WeatherDatadmiDenmarkTimeStamp_${location.locationName}`
              );
            }, 400);
          }
          savedLocationItem.remove();
        }
      });

    const savelocationtouch = document.createElement("savelocationtouch");
    const md_rippleSaveLocationTouch = document.createElement("md-ripple");
    savelocationtouch.appendChild(md_rippleSaveLocationTouch);

    savelocationtouch.addEventListener("click", () => {
      document.getElementById("search-container").style.display = "none";

      if (
        localStorage.getItem("selectedMainWeatherProvider") === "Met norway"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`)
        );
        const renderFromSavedDataMet = JSON.parse(
          localStorage.getItem(`WeatherDataMetNorway_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataMetTimstamp = localStorage.getItem(
          `WeatherDataMetNorwayTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

//        const currentData = renderFromSavedDataMet.properties.timeseries[0];
//        const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(
//          0,
//          24
//        );

        createHourlyDataCount(renderFromSavedData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        UvIndex(renderFromSavedData.hourly.uv_index[0]);
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        document.querySelector(".data_provider_name_import").innerHTML =
          "Data by Met norway";

        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );

        AirQuaility(AirQuailityData);

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );

        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
        }, 1000);

                document.getElementById('last_updated').addEventListener('click', () =>{
                  document.getElementById(
                    "last_updated"
                  ).innerHTML = `${getTranslationByLang(
                    localStorage.getItem("AppLanguageCode"),
                    "updated"
                  )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
                });

      } else if (
        localStorage.getItem("ApiForAccu") &&
        localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
      ) {
        const data = JSON.parse(
          localStorage.getItem(
            `WeatherDataAccuCurrent_${location.locationName}`
          )
        );
        const dataHourly = JSON.parse(
          localStorage.getItem(`WeatherDataAccuHourly_${location.locationName}`)
        );
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const dataTimstamp = localStorage.getItem(
          `WeatherDataAccuTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);
        if (data) {
          DisplayCurrentAccuweatherData(
            data,
            savedLocationItemLat,
            savedLocationItemLon
          );
        HourlyWeather(renderFromSavedData);
        } else {
//          DecodeWeather(
//            savedLocationItemLat,
//            savedLocationItemLon,
//            location.locationName
//          );
        }

        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );

        AirQuaility(AirQuailityData);

        createHourlyDataCount(renderFromSavedData);

        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0]
        );
        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        UvIndex(renderFromSavedData.hourly.uv_index[0]);
        DailyWeather(renderFromSavedData.daily);
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        document.querySelector(".data_provider_name_import").innerHTML =
          "Data by Accuweather";

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );

        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(dataTimstamp)}`;
        }, 1000);

            document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(dataTimstamp)}`;
            });

      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(
            `WeatherDataMeteoFrance_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataMeteoFranceTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        document.getElementById('last_updated').addEventListener('click', () =>{
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDataDWDGermany_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataDWDGermanyTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

            document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "noaaUS"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDataNOAAUS_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataNOAAUSTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

            document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "ecmwf"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataECMWFTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

                document.getElementById('last_updated').addEventListener('click', () =>{
                  document.getElementById(
                    "last_updated"
                  ).innerHTML = `${getTranslationByLang(
                    localStorage.getItem("AppLanguageCode"),
                    "updated"
                  )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
                });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(
            `WeatherDataukMetOffice_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataukMetOfficeTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        document.getElementById('last_updated').addEventListener('click', () =>{
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDataJMAJapan_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataJMAJapanTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        document.getElementById('last_updated').addEventListener('click', () =>{
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDatagemCanadaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

                document.getElementById('last_updated').addEventListener('click', () =>{
                  document.getElementById(
                    "last_updated"
                  ).innerHTML = `${getTranslationByLang(
                    localStorage.getItem("AppLanguageCode"),
                    "updated"
                  )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
                });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(
            `WeatherDatabomAustralia_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDatabomAustraliaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

                document.getElementById('last_updated').addEventListener('click', () =>{
                  document.getElementById(
                    "last_updated"
                  ).innerHTML = `${getTranslationByLang(
                    localStorage.getItem("AppLanguageCode"),
                    "updated"
                  )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
                });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDatagemCanada_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDatagemCanadaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

                document.getElementById('last_updated').addEventListener('click', () =>{
                  document.getElementById(
                    "last_updated"
                  ).innerHTML = `${getTranslationByLang(
                    localStorage.getItem("AppLanguageCode"),
                    "updated"
                  )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
                });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDatacmaChina_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDatacmaChinaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

                document.getElementById('last_updated').addEventListener('click', () =>{
                  document.getElementById(
                    "last_updated"
                  ).innerHTML = `${getTranslationByLang(
                    localStorage.getItem("AppLanguageCode"),
                    "updated"
                  )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
                });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") ===
        "knmiNetherlands"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(
            `WeatherDataknmiNetherlands_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataknmiNetherlandsTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

                document.getElementById('last_updated').addEventListener('click', () =>{
                  document.getElementById(
                    "last_updated"
                  ).innerHTML = `${getTranslationByLang(
                    localStorage.getItem("AppLanguageCode"),
                    "updated"
                  )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
                });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
      ) {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDatadmiDenmark_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDatadmiDenmarkTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        document.getElementById('last_updated').addEventListener('click', () =>{
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        });

        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else {
        const renderFromSavedData = JSON.parse(
          localStorage.getItem(`WeatherDataOpenMeteo_${location.locationName}`)
        );
        const dataHourlyFull = JSON.parse(
          localStorage.getItem(`HourlyWeatherCache_${location.locationName}`)
        );
        const AirQuailityData = JSON.parse(
          localStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          localStorage.getItem(`MoreDetailsData_${location.locationName}`)
        );
        const AstronomyData = JSON.parse(
          localStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = localStorage.getItem(
          `WeatherDataOpenMeteoTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          localStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }
        function timeAgo(timestamp) {
          const now = new Date();
          const updatedTime = new Date(timestamp);
          const diffInSeconds = Math.floor((now - updatedTime) / 1000);

          const units = [
            { name: "day", seconds: 86400 },
            { name: "hr.", seconds: 3600 },
            { name: "min.", seconds: 60 },
            { name: "sec.", seconds: 1 },
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
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        document.getElementById('last_updated').addEventListener('click', () =>{
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        });
        localStorage.setItem("currentLong", savedLocationItemLon);
        localStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        createHourlyDataCount(renderFromSavedData);
        AirQuaility(AirQuailityData);

        DailyWeather(renderFromSavedData.daily);
        HourlyWeather(renderFromSavedData);
        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0],
          savedLocationItemLat,
          savedLocationItemLon
        );

        localStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        localStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        localStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      }

      localStorage.setItem("CurrentLocationName", location.locationName);

      document.getElementById("city-name").innerHTML = JSON.parse(localStorage.getItem("savedLocationsCustomName"))?.[location.locationName] || location.locationName;
      document.getElementById("forecast").scrollLeft = 0;
      document.getElementById("weather_wrap").scrollTop = 0;
      window.history.back();
      hideLoader();

            if(localStorage.getItem('updateOldData') === 'true'){
              if (navigator.onLine) {
              setTimeout(() =>{
                updateOldData(savedLocationItemLat, savedLocationItemLon, location.locationName)
              }, 200);
              }
            }
    });
    savedLocationItem.appendChild(savelocationtouch);

    savedLocationsHolder.append(savedLocationItem);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  loadSavedLocations();
});


function deleteLocation(locationName) {
  let savedLocations = JSON.parse(localStorage.getItem("savedLocations")) || [];

  savedLocations = savedLocations.filter(
    (location) => location.locationName !== locationName
  );

  localStorage.setItem("savedLocations", JSON.stringify(savedLocations));

  if (savedLocations.length === 0) {
    document.querySelector(".savedLocations").hidden = true;
    document.getElementById("edit_saved_locations").selected = false;
  } else {
    document.querySelector(".savedLocations").hidden = false;
  }
}

window.addEventListener("popstate", function (event) {
  if (!document.querySelector(".map_picker").hidden) {
    document.querySelector(".map_picker").style.opacity = "0";
    document.querySelector(".map_picker").style.transform = "scale(0.8)";

    setTimeout(() => {
      document.querySelector(".map_picker").hidden = true;
      document.querySelector(".map_picker").style.opacity = "";
      document.querySelector(".map_picker").style.transform = "";
    }, 300);
  } else {
    document.getElementById("search-container").style.transform = "translateY(20%)";
    document.getElementById("search-container").style.opacity = "0";

    setTimeout(() => {
document.querySelector(".close_search").style.pointerEvents = 'none'

      document.getElementById("modal-content").scrollTop = 0;
      document.getElementById("search-container").style.display = "none";
      document.getElementById("search-container").style.opacity = "1";
          document.getElementById("search-container").style.transform = "";
    document.querySelector(".header_hold").style.pointerEvents = ''

      checkTopScroll();
    document.getElementById("edit_saved_locations").selected = false;

            if(document.getElementById('weather_wrap').scrollTop > 10){
              const allClassesAnimation = document.querySelectorAll('.behind_search_animation');
              const allClassesAnimationFog = document.querySelectorAll('.behind_search_animation_fog');

              if(allClassesAnimation){
                  allClassesAnimation.forEach((classAnimation) =>{
                      classAnimation.hidden = true
                  })
              }

              if(allClassesAnimationFog){
                  allClassesAnimationFog.forEach((classAnimationFog) =>{
                      classAnimationFog.hidden = true
                  })
              }

          } else{
              const allClassesAnimation = document.querySelectorAll('.behind_search_animation');
              const allClassesAnimationFog = document.querySelectorAll('.behind_search_animation_fog');

              if(allClassesAnimation){
                  allClassesAnimation.forEach((classAnimation) =>{
                      classAnimation.hidden = false
                  })
              }
              if(allClassesAnimationFog){
                  allClassesAnimationFog.forEach((classAnimationFog) =>{
                      classAnimationFog.hidden = false
                  })
              }
          }
    }, 300);
  }
});

function getCountryName(code) {
  return countryNames[code] || "Unknown Country";
}

function getCurrentLocationWeather() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
      const latitude = position.coords.latitude;
      const longitude = position.coords.longitude;
      DecodeWeather(latitude, longitude); // Call getWeatherByCoordinates with coordinates
    }, handleGeolocationError);
  } else {
    console.error("Geolocation is not supported by this browser.");
  }
}

let debounceTimeout;

function onAllLocationsLoaded() {
  clearTimeout(debounceTimeout);
  document.querySelector("savedLocationsHolder").innerHTML =
    '<empty_loader style="display: flex; align-items: center; justify-content: center;"><md-circular-progress indeterminate></md-circular-progress></empty_loader>';

  debounceTimeout = setTimeout(() => {
      localStorage.removeItem(`SearchedItem`);
    loadSavedLocations();
initializeDragAndDropLocations();

    document.getElementById('addlocationFab').hidden = false;
  }, 2500);
    if(sessionStorage.getItem('usedFirstAsDefault')){
      sessionStorage.removeItem('usedFirstAsDefault')
    }
}

function showLoader() {
  const loaderContainer = document.getElementById("loader-container");
  loaderContainer.style.display = "flex";
  loaderContainer.style.opacity = "1";
  document.querySelector("rainmeterbar").scrollLeft = 0;
}

// Hide the loader
function hideLoader() {
  const loaderContainer = document.getElementById("loader-container");

  loaderContainer.style.opacity = "0";

  setTimeout(() => {
    loaderContainer.style.display = "none";
  }, 300);

  let savedLocations = JSON.parse(localStorage.getItem("savedLocations")) || [];

  if (savedLocations.length === 0) {
    document.querySelector(".savedLocations").hidden = true;
  } else {
    document.querySelector(".savedLocations").hidden = false;
  }
}

function refreshWeather() {
  document.querySelector(".no_touch_screen").hidden = false;

  if (navigator.onLine) {
    const latSend = localStorage.getItem("currentLat");
    const longSend = localStorage.getItem("currentLong");
    const CurrentLocationName = localStorage.getItem("CurrentLocationName");

    let weatherDataKey;
    if (
      localStorage.getItem("selectedMainWeatherProvider") === "Met norway"
    ) {
      weatherDataKey = `WeatherDataMetNorwayTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("ApiForAccu") &&
      localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
    ) {
      weatherDataKey = `WeatherDataAccuCurrentTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
    ) {
      weatherDataKey = `WeatherDataMeteoFranceTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
    ) {
      weatherDataKey = `WeatherDataDWDGermanyTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "noaaUS"
    ) {
      weatherDataKey = `WeatherDataNOAAUSTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ecmwf"
    ) {
      weatherDataKey = `WeatherDataECMWFTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
    ) {
      weatherDataKey = `WeatherDataukMetOfficeTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
    ) {
      weatherDataKey = `WeatherDataJMAJapanTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
    ) {
      weatherDataKey = `WeatherDatagemCanadaTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
    ) {
      weatherDataKey = `WeatherDatabomAustraliaTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
    ) {
      weatherDataKey = `WeatherDatacmaChinaTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") ===
      "knmiNetherlands"
    ) {
      weatherDataKey = `WeatherDataknmiNetherlandsTimeStamp_${CurrentLocationName}`;
    } else if (
      localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
    ) {
      weatherDataKey = `WeatherDatadmiDenmarkTimeStamp_${CurrentLocationName}`;
    } else {
      weatherDataKey = `WeatherDataOpenMeteoTimeStamp_${CurrentLocationName}`;
    }

    const weatherData = localStorage.getItem(weatherDataKey);


    const providedDate = new Date(weatherData);

    const currentDate = new Date();

    const timeDifference = currentDate - providedDate;

    const timeDifferenceInMinutes = timeDifference / (1000 * 60);

    if (timeDifferenceInMinutes < 10) {

      setTimeout(() => {
        document.querySelector(".no_touch_screen").hidden = true;
          LoadLocationOnRequest(latSend, longSend, CurrentLocationName)
        sendThemeToAndroid("StopRefreshingLoader");
      }, 1000);
    } else{

    DecodeWeather(
      latSend,
      longSend,
      CurrentLocationName,
      `Refreshed_${CurrentLocationName}`
    );
    }


  } else {
    setTimeout(() => {
      document.querySelector(".no_touch_screen").hidden = true;
      ShowSnackMessage.ShowSnack(
        getTranslationByLang(
          localStorage.getItem("AppLanguageCode"),
          "network_unavailable"
        ),
        "long"
      );
      sendThemeToAndroid("StopRefreshingLoader");
    }, 1000);
    return;
  }
}

function sendThemeToAndroid(theme) {
  AndroidInterface.updateStatusBarColor(theme);
}
function Toast(toastText, time) {
  ToastAndroidShow.ShowToast(toastText, time);
}

// map

var map;

function darkModeTileLayer(urlTemplate) {
  const filterStyle =
    "invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%)";

  return (window.L.tileLayer(urlTemplate, {})
    .addTo(map)
    .getContainer().style.filter = filterStyle);
}

function RenderSearhMap() {
  const latDif = localStorage.getItem("currentLat");
  const longDif = localStorage.getItem("currentLong");

  map = window.L.map("map", {
    center: [latDif, longDif],
    zoom: 8,
    zoomControl: false,
  });

  darkModeTileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png");

  var marker = window.L.marker([latDif, longDif]).addTo(map);

  map.on("click", function (e) {
    var lat = e.latlng.lat;
    var lon = e.latlng.lng;

    marker.setLatLng([lat, lon]);
    window.history.back();

    document.getElementById("search-container").style.opacity = "0";

    setTimeout(() => {
      window.history.back();
    }, 500);

    DecodeWeather(lat, lon);

    document.getElementById("forecast").scrollLeft = 0;
    document.getElementById("weather_wrap").scrollTop = 0;
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
    localStorage.setItem("DeviceOnline", "Yes");
  } else {
    const offlineData = localStorage.getItem("DefaultLocation");
    if (offlineData) {
      const parsedOfflineData = JSON.parse(offlineData);
      let weatherDataKey;

      if (
        localStorage.getItem("selectedMainWeatherProvider") === "Met norway"
      ) {
        weatherDataKey = `WeatherDataMetNorway_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("ApiForAccu") &&
        localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
      ) {
        weatherDataKey = `WeatherDataAccuCurrent_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
      ) {
        weatherDataKey = `WeatherDataMeteoFrance_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
      ) {
        weatherDataKey = `WeatherDataDWDGermany_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "noaaUS"
      ) {
        weatherDataKey = `WeatherDataNOAAUS_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "ecmwf"
      ) {
        weatherDataKey = `WeatherDataECMWF_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
      ) {
        weatherDataKey = `WeatherDataukMetOffice_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
      ) {
        weatherDataKey = `WeatherDataJMAJapan_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
      ) {
        weatherDataKey = `WeatherDatagemCanada_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
      ) {
        weatherDataKey = `WeatherDatabomAustralia_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
      ) {
        weatherDataKey = `WeatherDatacmaChina_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") ===
        "knmiNetherlands"
      ) {
        weatherDataKey = `WeatherDataknmiNetherlands_${parsedOfflineData.name}`;
      } else if (
        localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
      ) {
        weatherDataKey = `WeatherDatadmiDenmark_${parsedOfflineData.name}`;
      } else {
        weatherDataKey = `WeatherDataOpenMeteo_${parsedOfflineData.name}`;
      }

      const weatherData = localStorage.getItem(weatherDataKey);

      if (weatherData) {
        setTimeout(() => {
          ReturnHomeLocation();
        }, 1000);
      } else {
        ShowError();
      }
    } else {
      ShowError();
    }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  checkNoInternet();
});


document.addEventListener("DOMContentLoaded", async function () {
  if (localStorage.getItem("HideNewUpdateToast") === "true") {
    return;
  }

  const currentVersion = "v1.19.0";
  const githubRepo = "PranshulGG/WeatherMaster";
  const releasesUrl = `https://api.github.com/repos/${githubRepo}/releases`;

  try {
    const response = await fetch(releasesUrl);
    if (!response.ok) throw new Error("Network response was not ok.");

    const data = await response.json();

    const stableRelease = data.find(release => !release.prerelease);

    if (stableRelease && stableRelease.tag_name !== currentVersion) {
      document.querySelector(".new_ver_download").hidden = false;
    } else {
      document.querySelector(".new_ver_download").hidden = true;
    }

  } catch (error) {
    console.error("Error checking for updates:", error);
  }
});


const scrollView = document.querySelector(".insights");

const scrollIndicators = document.getElementById("scroll-indicators");

function saveScrollPosition() {
  const scrollPosition = scrollView.scrollLeft;
  localStorage.setItem("scrollPosition", scrollPosition);
}

function restoreScrollPosition() {
  const savedScrollPosition = localStorage.getItem("scrollPosition");
  if (savedScrollPosition) {
    scrollView.scrollLeft = savedScrollPosition;
  }
}

function createScrollDots() {
  const sections = document.querySelectorAll(".insights_item");

  sections.forEach((section, index) => {
    const dot = document.createElement("span");
    const dotValue = document.createElement("div");
    dot.classList.add("dot");
    dotValue.classList.add("dotValue");
    dot.onclick = () => {
      section.scrollIntoView({ behavior: "smooth" });
    };
    scrollIndicators.appendChild(dot);
    dot.appendChild(dotValue);
  });
}

const updateActiveIndicator = () => {
  const scrollPosition = Math.round(
    scrollView.scrollLeft / scrollView.offsetWidth
  );
  const dotsValue = document.querySelectorAll(".dotValue");
  dotsValue.forEach((dotValue, index) => {
    if (index === scrollPosition) {
      dotValue.style.transform = "scale(1)";
    } else {
      dotValue.style.transform = "scale(0)";
    }
  });
};

const no_refresh_elements = document.querySelectorAll('.noSwipe');

no_refresh_elements.forEach((no_refresh_element) =>{
  no_refresh_element.addEventListener('touchstart', () =>{
    sendThemeToAndroid("DisableSwipeRefresh")
  });
  no_refresh_element.addEventListener('touchend', () =>{
//  clearTimeout(debounceTimeoutTouch1);
    checkTopScroll()
});

})

// ------------------------

const no_refresh_element2 = document.getElementById('sortableContainer');


no_refresh_element2.addEventListener('touchstart', () =>{
  sendThemeToAndroid("DisableSwipeRefresh")
});


no_refresh_element2.addEventListener('touchend', () =>{
  if (document.getElementById("weather_wrap").scrollTop === 0) {
    sendThemeToAndroid("EnableSwipeRefresh");
  } else {
    sendThemeToAndroid("DisableSwipeRefresh");
  }
});

function checkTopScroll() {
  if (document.getElementById("weather_wrap").scrollTop === 0) {
    sendThemeToAndroid("EnableSwipeRefresh");
  } else {
    sendThemeToAndroid("DisableSwipeRefresh");
  }
}

// Helper debounce function
function debounce(func, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => func.apply(this, args), delay);
  };
}

scrollView.addEventListener("scroll", debounce(saveScrollPosition, 200));
scrollView.addEventListener("scroll", updateActiveIndicator);

document.addEventListener("DOMContentLoaded", () => {
  createScrollDots();
  updateActiveIndicator();
  restoreScrollPosition();
});

document
  .getElementById("edit_saved_locations")
  .addEventListener("click", () => {
    const allDeleteBtns = document.querySelectorAll(".delete-btn");

    const allTempsSaved = document.querySelectorAll("maincurrenttempsaved");

    if (document.getElementById("edit_saved_locations").selected) {
      allDeleteBtns.forEach((deletebtns) => {
        deletebtns.style.display = "flex";
      });

      allTempsSaved.forEach((TempsSaved) => {
        TempsSaved.style.display = "none";
      });
    } else {
      allDeleteBtns.forEach((deletebtns) => {
        deletebtns.style.display = "none";
      });

      allTempsSaved.forEach((TempsSaved) => {
        TempsSaved.style.display = "block";
      });
    }
  });

function ReturnHomeLocation() {
  const Locations = JSON.parse(localStorage.getItem("DefaultLocation"));

  if (localStorage.getItem("selectedMainWeatherProvider") === "Met norway") {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataMetNorway_${Locations.name}`)
    );
    const renderFromSavedDataMet = JSON.parse(
      localStorage.getItem(`WeatherDataMetNorway_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataMetTimstamp = localStorage.getItem(
      `WeatherDataMetNorwayTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    } else {
      document
        .querySelector(".weatherCommentsDiv")
        .classList.remove("alertOpened");
      document.querySelector(".excessiveHeat").hidden = true;
    }
    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

//    const currentData = renderFromSavedDataMet.properties.timeseries[0];
//    const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(
//      0,
//      24
//    );

    createHourlyDataCount(renderFromSavedData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );
    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Met norway";

    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );

    AirQuaility(AirQuailityData);

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));

    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
            });

  } else if (
    localStorage.getItem("ApiForAccu") &&
    localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
  ) {
    const data = JSON.parse(
      localStorage.getItem(`WeatherDataAccuCurrent_${Locations.name}`)
    );
    const dataHourly = JSON.parse(
      localStorage.getItem(`WeatherDataAccuHourly_${Locations.name}`)
    );
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const dataTimstamp = localStorage.getItem(
      `WeatherDataAccuTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    } else {
      document
        .querySelector(".weatherCommentsDiv")
        .classList.remove("alertOpened");
      document.querySelector(".excessiveHeat").hidden = true;
    }
    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);
    DisplayCurrentAccuweatherData(data, Locations.lat, Locations.lon);
        HourlyWeather(renderFromSavedData);

    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );

    AirQuaility(AirQuailityData);

    createHourlyDataCount(renderFromSavedData);

    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0]
    );
    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    DailyWeather(renderFromSavedData.daily);
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Accuweather";

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));

    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(dataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(dataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(dataTimstamp)}`;
            });
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataMeteoFrance_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataMeteoFranceTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

document.getElementById('last_updated').addEventListener('click', () =>{
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataDWDGermany_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataDWDGermanyTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (localStorage.getItem("selectedMainWeatherProvider") === "noaaUS") {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataNOAAUS_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataNOAAUSTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (localStorage.getItem("selectedMainWeatherProvider") === "ecmwf") {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataECMWF_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataECMWFTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataukMetOffice_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataukMetOfficeTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataJMAJapan_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataJMAJapanTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatagemCanada_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatabomAustralia_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatabomAustraliaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatagemCanada_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatacmaChina_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatacmaChinaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "knmiNetherlands"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataknmiNetherlands_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataknmiNetherlandsTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatadmiDenmark_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatadmiDenmarkTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataOpenMeteoTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    } else {
      document
        .querySelector(".weatherCommentsDiv")
        .classList.remove("alertOpened");
      document.querySelector(".excessiveHeat").hidden = true;
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      Locations.lat,
      Locations.lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  }

  if (Locations.name === "CurrentDeviceLocation") {
    document.getElementById("city-name").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "current_location"
    );
    document.getElementById("currentLocationName").textContent =
      getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "current_location"
      );
  } else {
    document.getElementById("city-name").innerHTML = JSON.parse(localStorage.getItem("savedLocationsCustomName"))?.[Locations.name] || Locations.name;
    document.getElementById("currentLocationName").textContent = JSON.parse(localStorage.getItem("savedLocationsCustomName"))?.[Locations.name] || Locations.name;
  }
  document.getElementById("SelectedLocationText").innerHTML = Locations.name;
  localStorage.setItem("CurrentLocationName", Locations.name);

  hideLoader();
}

if (
  localStorage.getItem("removedOldSavedLocations") &&
  localStorage.getItem("removedOldSavedLocations") === "removed"
) {
  localStorage.removeItem("savedLocations");

  setTimeout(() => {
    localStorage.getItem("removedOldSavedLocations", "removed");
  }, 300);
} else {
  localStorage.getItem("removedOldSavedLocations", "removed");
}

document.getElementById("open_temp_trend").addEventListener("click", () => {
  document.querySelector(".temp_trend_bars").classList.toggle("trends_opened");

  if (
    document
      .querySelector(".temp_trend_bars")
      .classList.contains("trends_opened")
  ) {
    document.querySelector(".trend_arrow_temp").innerHTML = "keyboard_arrow_up";
  } else {
    document.querySelector(".trend_arrow_temp").innerHTML =
      "keyboard_arrow_down";
  }
});

function setChart() {
  if (
    localStorage.getItem("useBarChart") &&
    localStorage.getItem("useBarChart") === "true"
  ) {
    createTempTrendsChartBar();
  } else {
    createTempTrendsChart();
  }
}

function handleStorageChangeChart(event) {
  if (event.key === "useBarChart") {
    setTimeout(() => {
      setChart();
    }, 500);
  }
}

window.addEventListener("storage", handleStorageChangeChart);

function createHourlyDataCount(data) {
  const weatherCodeGroups = {
    0: [0],
    1: [1],
    2: [2],
    3: [3],
    45: [45],
    48: [48],
    51: [51],
    53: [53],
    55: [55],
    56: [56],
    57: [57],
    61: [61],
    63: [63],
    65: [65],
    66: [66],
    67: [67],
    71: [71],
    73: [73],
    75: [75],
    77: [77],
    80: [80],
    81: [81],
    82: [82],
    85: [85],
    86: [86],
    95: [95],
    96: [96],
    99: [99],
  };

  let groupCounts = {};
  Object.keys(weatherCodeGroups).forEach((group) => {
    groupCounts[group] = 0;
  });

  data.hourly.weather_code.forEach((code) => {
    if (groupCounts[code] !== undefined) {
      groupCounts[code]++;
    }
  });

  const mostFrequentGroup = Object.keys(groupCounts).reduce((a, b) =>
    groupCounts[a] > groupCounts[b] ? a : b
  );
  const selectedWeatherCode = mostFrequentGroup;

  ReportFromhourly(selectedWeatherCode);

  let Visibility;
  let VisibilityUnit;

  if (localStorage.getItem("selectedVisibilityUnit") === "mileV") {
    Visibility = Math.round(data.hourly.visibility[0] / 1609.34);
        if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        VisibilityUnit = "英里";
        } else{
    VisibilityUnit = "miles";
    }
  } else {
    Visibility = Math.round(data.hourly.visibility[0] / 1000);
    if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
    VisibilityUnit = "公里";
    } else{
    VisibilityUnit = "km";
    }
  }

  document.getElementById("unit_visibility").innerHTML = VisibilityUnit;
  document.getElementById("min-temp").innerHTML = Visibility;

  let DewPointTemp;

  if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
    DewPointTemp = Math.round(celsiusToFahrenheit(data.hourly.dew_point_2m[0]));
  } else {
    DewPointTemp = Math.round(data.hourly.dew_point_2m[0]);
  }

  document.getElementById("dew_percentage").innerHTML = DewPointTemp + "°";
}

// load location on request

function LoadLocationOnRequest(lat, lon, name) {
  if (localStorage.getItem("selectedMainWeatherProvider") === "Met norway") {
    showLoader();
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataMetNorway_${name}`)
    );
    const renderFromSavedDataMet = JSON.parse(
      localStorage.getItem(`WeatherDataMetNorway_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataMetTimstamp = localStorage.getItem(
      `WeatherDataMetNorwayTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

//    const currentData = renderFromSavedDataMet.properties.timeseries[0];
//    const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(
//      0,
//      24
//    );

    createHourlyDataCount(renderFromSavedData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Met norway";

    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );

    AirQuaility(AirQuailityData);

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));

    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    hideLoader();
  } else if (
    localStorage.getItem("ApiForAccu") &&
    localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
  ) {
    showLoader();
    const data = JSON.parse(
      localStorage.getItem(`WeatherDataAccuCurrent_${name}`)
    );
    const dataHourly = JSON.parse(
      localStorage.getItem(`WeatherDataAccuHourly_${name}`)
    );
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataOpenMeteo_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const dataTimstamp = localStorage.getItem(
      `WeatherDataAccuTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);
    DisplayCurrentAccuweatherData(data, lat, lon);
        HourlyWeather(renderFromSavedData);

    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );

    AirQuaility(AirQuailityData);

    createHourlyDataCount(renderFromSavedData);

    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0]
    );
    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    DailyWeather(renderFromSavedData.daily);
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Accuweather";

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));

    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    hideLoader();
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "meteoFrance"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataMeteoFrance_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataMeteoFranceTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "dwdGermany"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataDWDGermany_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataDWDGermanyTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (localStorage.getItem("selectedMainWeatherProvider") === "noaaUS") {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataNOAAUS_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataNOAAUSTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (localStorage.getItem("selectedMainWeatherProvider") === "ecmwf") {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataECMWF_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataECMWFTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "ukMetOffice"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataukMetOffice_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataukMetOfficeTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

document.getElementById('last_updated').addEventListener('click', () =>{
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "jmaJapan"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataJMAJapan_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataJMAJapanTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", Locations.lon);
    localStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatagemCanada_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "bomAustralia"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatabomAustralia_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatabomAustraliaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatagemCanada_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "cmaChina"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatacmaChina_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatacmaChinaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "knmiNetherlands"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataknmiNetherlands_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataknmiNetherlandsTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else if (
    localStorage.getItem("selectedMainWeatherProvider") === "dmiDenmark"
  ) {
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDatadmiDenmark_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDatadmiDenmarkTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(() => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));
  } else {
    showLoader();
    const renderFromSavedData = JSON.parse(
      localStorage.getItem(`WeatherDataOpenMeteo_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      localStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      localStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      localStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      localStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = localStorage.getItem(
      `WeatherDataOpenMeteoTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      localStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    function timeAgo(timestamp) {
      const now = new Date();
      const updatedTime = new Date(timestamp);
      const diffInSeconds = Math.floor((now - updatedTime) / 1000);

      const units = [
        { name: "day", seconds: 86400 },
        { name: "hr.", seconds: 3600 },
        { name: "min.", seconds: 60 },
        { name: "sec.", seconds: 1 },
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
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        localStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1000);

    document.getElementById('last_updated').addEventListener('click', () =>{
              document.getElementById(
                "last_updated"
              ).innerHTML = `${getTranslationByLang(
                localStorage.getItem("AppLanguageCode"),
                "updated"
              )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
            });

    localStorage.setItem("currentLong", lon);
    localStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    createHourlyDataCount(renderFromSavedData);
    AirQuaility(AirQuailityData);

    DailyWeather(renderFromSavedData.daily);
    HourlyWeather(renderFromSavedData);
    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0],
      lat,
      lon
    );

    localStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    localStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    localStorage.setItem("HourlyWeatherCache", JSON.stringify(dataHourlyFull));

    hideLoader();
  }

  if (name === "CurrentDeviceLocation") {
    document.getElementById("city-name").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "current_location"
    );
  }
}



// display device location

document.getElementById("showDeviceLocation").addEventListener("click", () => {
  setTimeout(() => {
    document.querySelector(".view_device_location").hidden = false;
  }, 200);
  if ("geolocation" in navigator) {
    navigator.geolocation.getCurrentPosition((position) => {
      currentLocation = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      };

      const url = `https://nominatim.openstreetmap.org/reverse?lat=${currentLocation.latitude}&lon=${currentLocation.longitude}&format=json`;

      fetch(url)
        .then((response) => {
          if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
          }
          return response.json();
        })
        .then((data) => {
          if (data.error) {
            document.getElementById("address").innerText = "No address found.";
          } else {
            const address = data.address;
            const shortAddress = `${
              address.city || address.town || address.village || ""
            }, ${address.state || ""}, ${address.country || ""}`;
            document.getElementById("device_address").innerHTML =
              shortAddress.trim();
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          document.getElementById("address").innerText =
            "Failed to retrieve address.";
        });
    });
  }
});

document
  .querySelector(".view_device_location")
  .addEventListener("click", () => {
    document.querySelector(".view_device_location").hidden = true;
  });



function startUpBackUp(importedData){
  try {
    const data = JSON.parse(importedData);

    if (typeof data !== "object" || data === null) {
      throw new Error("Invalid data format");
    }

    localStorage.clear();

    setTimeout(() => {
      for (const [key, value] of Object.entries(data)) {
        localStorage.setItem(key, value);
      }
      ShowSnackMessage.ShowSnack("Data imported successfully!", "short");
    }, 1000);
    setTimeout(() => {
      Android.reloadTheApp();
    }, 1500);
  } catch (error) {
    ShowSnackMessage.ShowSnack(
      "Error parsing the imported JSON data. Please ensure it is valid and correctly formatted.",
      "long"
    );
    console.error(error);
  }
}


function handleTouchStartSavedLocations() {
      sendThemeToAndroid("DisableSwipeRefresh")

}

function handleTouchEndSavedLocations() {
  if (document.getElementById("weather_wrap").scrollTop === 0) {
        sendThemeToAndroid("EnableSwipeRefresh");
  } else {
      sendThemeToAndroid("DisableSwipeRefresh")
  }
}

const mediaQuery = window.matchMedia('(min-width: 700px)');


const handleWidthChange = (mq) => {
  if (mq.matches) {

    document.querySelector('.savedLocations').addEventListener('touchstart', handleTouchStartSavedLocations);
    document.querySelector('.savedLocations').addEventListener('touchend', handleTouchEndSavedLocations);

  } else {

    document.querySelector('.savedLocations').removeEventListener('touchstart', handleTouchStartSavedLocations);
    document.querySelector('.savedLocations').removeEventListener('touchend', handleTouchEndSavedLocations);

    document.getElementById('search-container').style.display = 'none'
  }
};

handleWidthChange(mediaQuery);

mediaQuery.addListener(handleWidthChange);

//-----------------------------

async function applySummaryConfig(){
  if(await customStorage.getItem('UseQuickSummary') === false){
    document.querySelector('.weatherCommentsDiv').hidden = true;
    document.querySelector('.high-all').classList.add('no_summary')

  } else{
    document.querySelector('.weatherCommentsDiv').hidden = false;
    document.querySelector('.high-all').classList.remove('no_summary')

  }
}

applySummaryConfig()


function handleStorageSummaryConfigEvent(event) {
  if (event.detail && event.detail.key === 'UseQuickSummary') {

      setTimeout(() => {
        applySummaryConfig()()
      }, 200);

  }
}

window.addEventListener('indexedDBChange', handleStorageSummaryConfigEvent);