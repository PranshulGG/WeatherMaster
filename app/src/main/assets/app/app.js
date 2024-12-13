let currentApiKeyIndex = 0;

let currentKeyMoonIndex = 0;
let currentAstronomyKeyIndex = 0;

async function WaitBeforeRefresh() {
  ShowSnackMessage.ShowSnack(
    getTranslationByLang(
      await customStorage.getItem("AppLanguageCode"),
      "Please_wait_before_refreshing_again."
    ),
    "long"
  );
}

let anim = null;

function ShowError() {
  document.querySelector(".no_internet_error").hidden = false;

  if (anim) {
    return;
  }

  var animationContainer = document.getElementById("error_img_cat");
  var animationData = "icons/error-cat.json";

  anim = bodymovin.loadAnimation({
    container: animationContainer,
    renderer: "svg",
    loop: true,
    autoplay: true,
    path: animationData,
  });
}

let currentLocation = null;

async function useAutoCurrentLocation() {
  const DefaultLocation = JSON.parse(
    await customStorage.getItem("DefaultLocation")
  );

  showLoader();
  if ("geolocation" in navigator) {
    navigator.geolocation.getCurrentPosition(async (position) => {
      currentLocation = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      };

      document.getElementById("city-name").innerHTML = getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "current_location"
      );
      document.getElementById("SelectedLocationText").innerHTML =
        getTranslationByLang(
          await customStorage.getItem("AppLanguageCode"),
          "current_location"
        );
      await customStorage.setItem("CurrentLocationName", "Current location");
      document.getElementById("currentLocationName").textContent =
        getTranslationByLang(
          await customStorage.getItem("AppLanguageCode"),
          "current_location"
        );

      if (
        !DefaultLocation ||
        DefaultLocation.name === "CurrentDeviceLocation"
      ) {
        await customStorage.setItem(
          "DefaultLocation",
          JSON.stringify({
            lat: currentLocation.latitude,
            lon: currentLocation.longitude,
            name: "CurrentDeviceLocation",
          })
        );
      }

      await customStorage.setItem("deviceLat", currentLocation.latitude);
      await customStorage.setItem("devicelon", currentLocation.longitude);

      DecodeWeather(
        currentLocation.latitude,
        currentLocation.longitude,
        "CurrentDeviceLocation"
      );
    });
  }
}

async function LoadFromNetwork() {
  const DefaultLocation = JSON.parse(
    await customStorage.getItem("DefaultLocation")
  );

  if (navigator.onLine) {
    if (DefaultLocation) {
      if (DefaultLocation.name === "CurrentDeviceLocation") {
        useAutoCurrentLocation();
        sendThemeToAndroid("ReqLocation");
        document.querySelector(".currentLocationdiv").hidden = false;
        document.getElementById("showDeviceLocation").hidden = false;
      } else if (DefaultLocation.lat && DefaultLocation.lon) {
        DecodeWeather(
          DefaultLocation.lat,
          DefaultLocation.lon,
          DefaultLocation.name
        );

        document.getElementById("city-name").innerHTML = DefaultLocation.name;
        document.getElementById("SelectedLocationText").innerHTML =
          DefaultLocation.name;
        await customStorage.setItem(
          "CurrentLocationName",
          DefaultLocation.name
        );
        document.getElementById("currentLocationName").textContent =
          DefaultLocation.name;
        document.getElementById("showDeviceLocation").hidden = true;
      }
    } else {
      useAutoCurrentLocation();
      sendThemeToAndroid("ReqLocation");
      document.querySelector(".currentLocationdiv").hidden = false;
    }
  } else {
    if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "Met norway"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by Met Norway (Global)";
    } else if (
      (await customStorage.getItem("ApiForAccu")) &&
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "Accuweather"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by Accuweather (Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "meteoFrance"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by Météo-France (Europe, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "dwdGermany"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by DWD (Europe, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) === "noaaUS"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by NOAA (Americas, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) === "ecmwf"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by ECMWF (Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "ukMetOffice"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by UK Met Office (Europe, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "jmaJapan"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by JMA (Asia, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "gemCanada"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by GEM (Americas, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "bomAustralia"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by BOM (Oceania, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "cmaChina"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by CMA (Asia, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "knmiEurope"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by KNMI (Europe, Global)";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "dmiEurope"
    ) {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by DMI (Europe, Global)";
    } else {
      document.querySelector(".data_provider_name_import").innerHTML =
        "Data by Open-Meteo (Global)";
    }

    setTimeout(async () => {
      ShowSnackMessage.ShowSnack(
        await getTranslationByLang(
          await customStorage.getItem("AppLanguageCode"),
          "network_unavailable"
        ),
        "long"
      );
    }, 2000);
  }
}

function handleGeolocationError(error) {
  console.error("Error getting geolocation:", error);
  displayErrorMessage(
    "Error getting geolocation. Please enable location services."
  );
}

document.addEventListener("DOMContentLoaded", async () => {
  LoadFromNetwork();

  const cityInput = document.getElementById("city-input");
  const cityopen = document.getElementById("city-open");
  const searchContainer = document.getElementById("search-container");
  const closeButton = document.querySelector(".close_search");
  const openMapPicker = document.getElementById("openMapPicker");

  cityopen.addEventListener("click", async () => {
    document.querySelector(".view_device_location").hidden = true;
    sendThemeToAndroid("DisableSwipeRefresh");
    loadSavedLocations();

    let savedLocations =
      JSON.parse(await customStorage.getItem("savedLocations")) || [];

    await customStorage.setItem("DeviceOnline", "Yes");
    searchContainer.style.display = "block";
    window.history.pushState({ SearchContainerOpen: true }, "");
    document.querySelector(".header_hold").style.transform = "scale(1.1)";
    document.querySelector(".header_hold").style.opacity = "0";

    setTimeout(() => {
      document.querySelector(".header_hold").style.transform = "";
      document.querySelector(".header_hold").style.opacity = "";

      if (savedLocations.length === 0) {
        cityInput.focus();
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

  cityInput.addEventListener("input", async () => {
    document.getElementById("cityLoader").hidden = true;
    document.querySelector(".currentLocationdiv").hidden = false;
    document.querySelector(".savedLocations").hidden = false;

    let savedLocations =
      JSON.parse(await customStorage.getItem("savedLocations")) || [];

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

  async function displaySuggestions(results) {
    const suggestionsContainer = document.getElementById("city-list");
    clearSuggestions();

    const displayedSuggestions = new Set();

    const savedLocations =
      JSON.parse(await customStorage.getItem("savedLocations")) || [];
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

async function GetSavedSearchLocation() {
  const searchedItem = JSON.parse(await customStorage.getItem(`SearchedItem`));

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
  }

  ShowSnackMessage.ShowSnack(
    getTranslationByLang(
      await customStorage.getItem("AppLanguageCode"),
      "loading_location_data"
    ),
    "long"
  );
}


function handleStorageChangeSearch(event) {
    if (event.detail && event.detail.key === 'SearchedItem') {
        setTimeout(() => {
            GetSavedSearchLocation();
          }, 1000);
    }
}

window.addEventListener('indexedDBChange', handleStorageChangeSearch);

async function saveLocationToContainer(locationName, lat, lon) {
  let savedLocations =
    JSON.parse(await customStorage.getItem("savedLocations")) || [];
  savedLocations.push({ locationName, lat, lon });
  await customStorage.setItem("savedLocations", JSON.stringify(savedLocations));

  const savedLocationsHolder = document.querySelector("savedLocationsHolder");

  //    savedLocationsHolder.innerHTML = ''
}

async function loadSavedLocations() {
  const savedLocationsHolder = document.querySelector("savedLocationsHolder");
  const savedLocations =
    JSON.parse(await customStorage.getItem("savedLocations")) || [];

  if (savedLocations.length === 0) {
    document.querySelector(".savedLocations").hidden = true;
  } else {
    document.querySelector(".savedLocations").hidden = false;
  }

  const currentTime = new Date().getTime();
  savedLocationsHolder.innerHTML = "";

  for (const location of savedLocations) {
    const savedLocationItem = document.createElement("savedLocationItem");
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
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "Met norway" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataMetNorway_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataMetNorway_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(
            celsiusToFahrenheit(
              data.properties.timeseries[0].data.instant.details.air_temperature
            )
          );
        } else {
          temp = Math.round(
            data.properties.timeseries[0].data.instant.details.air_temperature
          );
        }

        icon = getMetNorwayIcons(
          data.properties.timeseries[0].data.next_1_hours.summary.symbol_code
        );

        conditionlabel = getMetNorwayWeatherLabelInLangNoAnim(
          data.properties.timeseries[0].data.next_1_hours.summary.symbol_code,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "Met norway" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataMetNorway_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = " ";
    } else if (
      (await customStorage.getItem("ApiForAccu")) &&
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "Accuweather" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataAccuCurrent_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataAccuCurrent_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(data[0].Temperature.Imperial.Value);
        } else {
          temp = Math.round(data[0].Temperature.Metric.Value);
        }

        icon = GetWeatherIconAccu(data[0].WeatherIcon);
        conditionlabel = GetWeatherTextAccuNoAnim(data[0].WeatherIcon);
        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("ApiForAccu")) &&
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "Accuweather" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataAccuCurrent_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = "";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dwdGermany" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataDWDGermany_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataDWDGermany_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dwdGermany" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataDWDGermany_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = "";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "noaaUS" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataNOAAUS_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataNOAAUS_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "noaaUS" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataNOAAUS_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = "";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "meteoFrance" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataMeteoFrance_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataMeteoFrance_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "meteoFrance" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataMeteoFrance_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = "";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "ecmwf" &&
      JSON.parse(
        await customStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "ecmwf" &&
      !JSON.parse(
        await customStorage.getItem(`WeatherDataECMWF_${location.locationName}`)
      )
    ) {
      showReloadBtn = " ";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "ukMetOffice" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataukMetOffice_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataukMetOffice_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "ukMetOffice" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataukMetOffice_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = "";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "jmaJapan" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataJMAJapan_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataJMAJapan_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "jmaJapan" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataJMAJapan_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = " ";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "gemCanada" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDatagemCanada_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDatagemCanada_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "gemCanada" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDatagemCanada_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = " ";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "bomAustralia" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDatabomAustralia_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDatabomAustralia_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "bomAustralia" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDatabomAustralia_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = " ";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "cmaChina" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDatacmaChina_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDatacmaChina_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "cmaChina" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDatacmaChina_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = " ";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "knmiNetherlands" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDataknmiNetherlands_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataknmiNetherlands_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "knmiNetherlands" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDataknmiNetherlands_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = "";
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dmiDenmark" &&
      JSON.parse(
        await customStorage.getItem(
          `WeatherDatadmiDenmark_${location.locationName}`
        )
      )
    ) {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDatadmiDenmark_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
        );

        showReloadBtn = "hidden";
      }
    } else if (
      (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dmiDenmark" &&
      !JSON.parse(
        await customStorage.getItem(
          `WeatherDatadmiDenmark_${location.locationName}`
        )
      )
    ) {
      showReloadBtn = " ";
    } else {
      const data = JSON.parse(
        await customStorage.getItem(
          `WeatherDataOpenMeteo_${location.locationName}`
        )
      );

      if (data) {
        if (
          (await customStorage.getItem("SelectedTempUnit")) === "fahrenheit"
        ) {
          temp = Math.round(celsiusToFahrenheit(data.current.temperature_2m));
        } else {
          temp = Math.round(data.current.temperature_2m);
        }

        icon = GetWeatherIcon(data.current.weather_code, data.current.is_day);

        conditionlabel = getWeatherLabelInLangNoAnim(
          data.current.weather_code,
          data.current.is_day,
          await customStorage.getItem("AppLanguageCode")
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
            <p>${location.locationName}</p>
            <span>${conditionlabel}</span>
            <mainCurrenttempSaved>${temp}°</mainCurrenttempSaved>
        </div>
        <md-icon-button class="delete-btn">
            <md-icon icon-filled>delete</md-icon>
        </md-icon-button>

        `;

    savedLocationItem
      .querySelector(".refresh_saved_location")
      .addEventListener("click", async () => {
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
              await customStorage.getItem("AppLanguageCode"),
              "network_unavailable"
            ),
            "long"
          );
        }
      });

    savedLocationItem
      .querySelector(".delete-btn")
      .addEventListener("click", async () => {
        const checkDefault = JSON.parse(
          await customStorage.getItem("DefaultLocation")
        );

        if (location.locationName === checkDefault.name) {
          ShowSnackMessage.ShowSnack(
            "You can't delete the default location",
            "long"
          );
          return;
        } else {
          deleteLocation(location.locationName);
          if (
            await customStorage.getItem(
              `WeatherDataOpenMeteo_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataOpenMeteo_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataMetNorway_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataMetNorway_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataAccuCurrent_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataAccuCurrent_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataAccuHourly_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataAccuHourly_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataMetNorwayTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataMetNorwayTimeStamp_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataAccuTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataAccuTimeStamp_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataOpenMeteoTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataOpenMeteoTimeStamp_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(`AlertData_${location.locationName}`)
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `AlertData_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `AstronomyData_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `AstronomyData_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `MoreDetailsData_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `MoreDetailsData_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCall_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCall_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallMet_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallMet_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataDWDGermany_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataDWDGermany_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataNOAAUS_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataNOAAUS_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataMeteoFrance_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataMeteoFrance_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataECMWF_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataECMWF_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataukMetOffice_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataukMetOffice_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDatajmaJapan_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatajmaJapan_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDatagemCanada_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatagemCanada_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDatabomAustralia_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatabomAustralia_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDatacmaChina_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatacmaChina_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDataknmiNetherlands_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataknmiNetherlands_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `WeatherDatadmiDenmark_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatadmiDenmark_${location.locationName}`
              );
            }, 400);
          }
          // last call

          if (
            await customStorage.getItem(
              `DecodeWeatherLastCalldwdGermany_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCalldwdGermany_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallopenmeteo_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallopenmeteo_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallnoaaUS_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallnoaaUS_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallmeteoFrance_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallmeteoFrance_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallecmwf_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallecmwf_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallukMetOffice_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallukMetOffice_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCalljmaJapan_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCalljmaJapan_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallgemCanada_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallgemCanada_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallbomAustralia_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallbomAustralia_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallcmaChina_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallcmaChina_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCallknmiNetherlands_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCallknmiNetherlands_${location.locationName}`
              );
            }, 400);
          }
          if (
            await customStorage.getItem(
              `DecodeWeatherLastCalldmiDenmark_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `DecodeWeatherLastCalldmiDenmark_${location.locationName}`
              );
            }, 400);
          }

          // remove any other data
          if (
            await customStorage.getItem(
              `HourlyWeatherCache_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `HourlyWeatherCache_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `AstronomyData_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `AstronomyData_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `MoreDetailsData_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `MoreDetailsData_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(`AlertData_${location.locationName}`)
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `AlertData_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(`AirQuality_${location.locationName}`)
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `AirQuality_${location.locationName}`
              );
            }, 400);
          }

          // time stamps

          if (
            await customStorage.getItem(
              `WeatherDataMeteoFranceTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataMeteoFranceTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataDWDGermanyTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataDWDGermanyTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataNOAAUSTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataNOAAUSTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataECMWFTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataECMWFTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataukMetOfficeTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataukMetOfficeTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataJMAJapanTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataJMAJapanTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDatagemCanadaTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatagemCanadaTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDatabomAustraliaTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatabomAustraliaTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDatacmaChinaTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDatacmaChinaTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDataknmiNetherlandsTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
                `WeatherDataknmiNetherlandsTimeStamp_${location.locationName}`
              );
            }, 400);
          }

          if (
            await customStorage.getItem(
              `WeatherDatadmiDenmarkTimeStamp_${location.locationName}`
            )
          ) {
            setTimeout(async () => {
              await customStorage.removeItem(
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

    savelocationtouch.addEventListener("click", async () => {
      if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "Met norway"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataOpenMeteo_${location.locationName}`
          )
        );
        const renderFromSavedDataMet = JSON.parse(
          await customStorage.getItem(
            `WeatherDataMetNorway_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataMetTimstamp = await customStorage.getItem(
          `WeatherDataMetNorwayTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);

        const currentData = renderFromSavedDataMet.properties.timeseries[0];
        const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(
          0,
          24
        );

        renderCurrentDataMetNorway(
          currentData,
          savedLocationItemLat,
          savedLocationItemLon
        );
        renderHourlyDataMetNorway(hourlyData);

        createHourlyDataCount(renderFromSavedData);

        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0]
        );
        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        UvIndex(renderFromSavedData.hourly.uv_index[0]);
        DailyWeather(renderFromSavedData.daily);
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        document.querySelector(".data_provider_name_import").innerHTML =
          "Data by Met norway";

        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );

        AirQuaility(AirQuailityData);

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
        }, 1000);
      } else if (
        (await customStorage.getItem("ApiForAccu")) &&
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
          "Accuweather"
      ) {
        const data = JSON.parse(
          await customStorage.getItem(
            `WeatherDataAccuCurrent_${location.locationName}`
          )
        );
        const dataHourly = JSON.parse(
          await customStorage.getItem(
            `WeatherDataAccuHourly_${location.locationName}`
          )
        );
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataOpenMeteo_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const dataTimstamp = await customStorage.getItem(
          `WeatherDataAccuTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
        );

        if (SavedalertData) {
          FetchAlertRender(SavedalertData);
        }

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

        astronomyDataRender(AstronomyData);
        MoreDetailsRender(MoreDetailsData);
        if (data) {
          DisplayCurrentAccuweatherData(
            data,
            savedLocationItemLat,
            savedLocationItemLon
          );
          DisplayHourlyAccuweatherData(dataHourly);
        } else {
          DecodeWeather(
            savedLocationItemLat,
            savedLocationItemLon,
            location.locationName
          );
        }

        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );

        AirQuaility(AirQuailityData);

        createHourlyDataCount(renderFromSavedData);

        CurrentWeather(
          renderFromSavedData.current,
          renderFromSavedData.daily.sunrise[0],
          renderFromSavedData.daily.sunset[0]
        );
        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        UvIndex(renderFromSavedData.hourly.uv_index[0]);
        DailyWeather(renderFromSavedData.daily);
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        document.querySelector(".data_provider_name_import").innerHTML =
          "Data by Accuweather";

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(dataTimstamp)}`;
        }, 1000);
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "meteoFrance"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataMeteoFrance_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataMeteoFranceTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dwdGermany"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataDWDGermany_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataDWDGermanyTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "noaaUS"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataNOAAUS_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataNOAAUSTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) === "ecmwf"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataECMWF_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataECMWFTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "ukMetOffice"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataukMetOffice_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataukMetOfficeTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "jmaJapan"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataJMAJapan_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataJMAJapanTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "gemCanada"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDatagemCanada_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDatagemCanadaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "bomAustralia"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDatabomAustralia_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDatabomAustraliaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "gemCanada"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDatagemCanada_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDatagemCanadaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "cmaChina"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDatacmaChina_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDatacmaChinaTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `Updated, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "knmiNetherlands"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataknmiNetherlands_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataknmiNetherlandsTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dmiDenmark"
      ) {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDatadmiDenmark_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDatadmiDenmarkTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      } else {
        const renderFromSavedData = JSON.parse(
          await customStorage.getItem(
            `WeatherDataOpenMeteo_${location.locationName}`
          )
        );
        const dataHourlyFull = JSON.parse(
          await customStorage.getItem(
            `HourlyWeatherCache_${location.locationName}`
          )
        );
        const AirQuailityData = JSON.parse(
          await customStorage.getItem(`AirQuality_${location.locationName}`)
        );
        const MoreDetailsData = JSON.parse(
          await customStorage.getItem(
            `MoreDetailsData_${location.locationName}`
          )
        );
        const AstronomyData = JSON.parse(
          await customStorage.getItem(`AstronomyData_${location.locationName}`)
        );
        const renderFromSavedDataTimstamp = await customStorage.getItem(
          `WeatherDataOpenMeteoTimeStamp_${location.locationName}`
        );
        const SavedalertData = JSON.parse(
          await customStorage.getItem(`AlertData_${location.locationName}`)
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

        setTimeout(async () => {
          document.getElementById(
            "last_updated"
          ).innerHTML = `${getTranslationByLang(
            await customStorage.getItem("AppLanguageCode"),
            "updated"
          )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
        }, 1000);

        await customStorage.setItem("currentLong", savedLocationItemLon);
        await customStorage.setItem("currentLat", savedLocationItemLat);

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

        await customStorage.setItem(
          "DailyWeatherCache",
          JSON.stringify(renderFromSavedData.daily)
        );
        await customStorage.setItem(
          "CurrentHourlyCache",
          JSON.stringify(renderFromSavedData.hourly)
        );
        await customStorage.setItem(
          "HourlyWeatherCache",
          JSON.stringify(dataHourlyFull)
        );
      }

      await customStorage.setItem("CurrentLocationName", location.locationName);

      document.getElementById("city-name").innerHTML = location.locationName;
      document.getElementById("forecast").scrollLeft = 0;
      document.getElementById("weather_wrap").scrollTop = 0;
      window.history.back();
      hideLoader();
    });
    savedLocationItem.appendChild(savelocationtouch);

    savedLocationsHolder.append(savedLocationItem);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  loadSavedLocations();
});

async function deleteLocation(locationName) {
  let savedLocations =
    JSON.parse(await customStorage.getItem("savedLocations")) || [];

  savedLocations = savedLocations.filter(
    (location) => location.locationName !== locationName
  );

  await customStorage.setItem("savedLocations", JSON.stringify(savedLocations));

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
    document.getElementById("search-container").style.opacity = "0";
    setTimeout(() => {
      document.getElementById("modal-content").scrollTop = 0;
      document.getElementById("search-container").style.display = "none";
      document.getElementById("search-container").style.opacity = "1";
      checkTopScroll();
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

async function onAllLocationsLoaded() {
  clearTimeout(debounceTimeout);
  document.querySelector("savedLocationsHolder").innerHTML =
    '<empty_loader style="display: flex; align-items: center; justify-content: center;"><md-circular-progress indeterminate></md-circular-progress></empty_loader>';
  debounceTimeout = setTimeout(async () => {
    if (JSON.parse(await customStorage.getItem(`SearchedItem`))) {
      await customStorage.removeItem(`SearchedItem`);
    }
    loadSavedLocations();
  }, 2500);
}

function showLoader() {
  const loaderContainer = document.getElementById("loader-container");
  loaderContainer.style.display = "flex";
  loaderContainer.style.opacity = "1";
  document.querySelector("rainmeterbar").scrollLeft = 0;
}

// Hide the loader
async function hideLoader() {
  const loaderContainer = document.getElementById("loader-container");

  loaderContainer.style.opacity = "0";

  setTimeout(() => {
    loaderContainer.style.display = "none";
  }, 300);

  let savedLocations =
    JSON.parse(await customStorage.getItem("savedLocations")) || [];

  if (savedLocations.length === 0) {
    document.querySelector(".savedLocations").hidden = true;
  } else {
    document.querySelector(".savedLocations").hidden = false;
  }
}

async function refreshWeather() {
  document.querySelector(".no_touch_screen").hidden = false;

  if (navigator.onLine) {
    const latSend = await customStorage.getItem("currentLat");
    const longSend = await customStorage.getItem("currentLong");
    const CurrentLocationName = await customStorage.getItem(
      "CurrentLocationName"
    );

    DecodeWeather(
      latSend,
      longSend,
      CurrentLocationName,
      `Refreshed_${CurrentLocationName}`
    );
  } else {
    setTimeout(async () => {
      document.querySelector(".no_touch_screen").hidden = true;
      ShowSnackMessage.ShowSnack(
        getTranslationByLang(
          await customStorage.getItem("AppLanguageCode"),
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

async function RenderSearhMap() {
  const latDif = await customStorage.getItem("currentLat");
  const longDif = await customStorage.getItem("currentLong");

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

async function checkNoInternet() {
  if (navigator.onLine) {
    await customStorage.setItem("DeviceOnline", "Yes");
  } else {
    const offlineData = await customStorage.getItem("DefaultLocation");
    if (offlineData) {
      const parsedOfflineData = JSON.parse(offlineData);
      let weatherDataKey;

      if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "Met norway"
      ) {
        weatherDataKey = `WeatherDataMetNorway_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("ApiForAccu")) &&
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
          "Accuweather"
      ) {
        weatherDataKey = `WeatherDataAccuCurrent_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "meteoFrance"
      ) {
        weatherDataKey = `WeatherDataMeteoFrance_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dwdGermany"
      ) {
        weatherDataKey = `WeatherDataDWDGermany_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "noaaUS"
      ) {
        weatherDataKey = `WeatherDataNOAAUS_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) === "ecmwf"
      ) {
        weatherDataKey = `WeatherDataECMWF_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "ukMetOffice"
      ) {
        weatherDataKey = `WeatherDataukMetOffice_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "jmaJapan"
      ) {
        weatherDataKey = `WeatherDataJMAJapan_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "gemCanada"
      ) {
        weatherDataKey = `WeatherDatagemCanada_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "bomAustralia"
      ) {
        weatherDataKey = `WeatherDatabomAustralia_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "cmaChina"
      ) {
        weatherDataKey = `WeatherDatacmaChina_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "knmiNetherlands"
      ) {
        weatherDataKey = `WeatherDataknmiNetherlands_${parsedOfflineData.name}`;
      } else if (
        (await customStorage.getItem("selectedMainWeatherProvider")) ===
        "dmiDenmark"
      ) {
        weatherDataKey = `WeatherDatadmiDenmark_${parsedOfflineData.name}`;
      } else {
        weatherDataKey = `WeatherDataOpenMeteo_${parsedOfflineData.name}`;
      }

      const weatherData = await customStorage.getItem(weatherDataKey);

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
  const currentVersion = "v1.9.8";
  const githubRepo = "PranshulGG/WeatherMaster";
  const releasesUrl = `https://api.github.com/repos/${githubRepo}/releases/latest`;

  try {
    const response = await fetch(releasesUrl);
    if (!response.ok) throw new Error("Network response was not ok.");

    const data = await response.json();
    const latestVersion = data.tag_name;

    if (latestVersion !== currentVersion) {
      if ((await customStorage.getItem("HideNewUpdateToast")) === "true") {
        document.querySelector(".new_ver_download").hidden = false;

        setTimeout(() => {
          document.querySelector(".new_ver_download").hidden = true;
        }, 5000);
      } else {
        document.querySelector(".new_ver_download").hidden = false;
      }
    } else {
      document.querySelector(".new_ver_download").hidden = true;
      return;
    }
  } catch (error) {}
});

const scrollView = document.querySelector(".insights");

const scrollIndicators = document.getElementById("scroll-indicators");

async function saveScrollPosition() {
  const scrollPosition = scrollView.scrollLeft;
  await customStorage.setItem("scrollPosition", scrollPosition);
}

async function restoreScrollPosition() {
  const savedScrollPosition = await customStorage.getItem("scrollPosition");
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

function debounce(func, delay) {
  let inDebounce;
  return function () {
    const context = this,
      args = arguments;
    clearTimeout(inDebounce);
    inDebounce = setTimeout(() => func.apply(context, args), delay);
  };
}

let isSwipeDisabledHori = false;
let isDebouncingScroll = false; // For debouncing touch events on `scrollView`

const debounceTouch = (func, delay) => {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => func(...args), delay);
  };
};

scrollView.addEventListener("touchstart", () => {
  if (!isSwipeDisabledHori && !isDebouncingScroll) {
    sendThemeToAndroid("DisableSwipeRefresh");
    isSwipeDisabledHori = true;
  }
});

scrollView.addEventListener("touchend", () => {
  if (!isDebouncingScroll) {
    isDebouncingScroll = true;
    debounceTouch(() => {
      checkTopScroll();
      isSwipeDisabledHori = false;
      isDebouncingScroll = false;
    }, 400)();
  }
});

let isSwipeDisabledHoriForecast = false;
let isDebouncingForecast = false; // For debouncing touch events on `forecast`

document.getElementById("forecast").addEventListener("touchstart", () => {
  if (!isSwipeDisabledHoriForecast && !isDebouncingForecast) {
    sendThemeToAndroid("DisableSwipeRefresh");
    isSwipeDisabledHoriForecast = true;
  }
});

document.getElementById("forecast").addEventListener("touchend", () => {
  if (!isDebouncingForecast) {
    isDebouncingForecast = true;
    debounceTouch(() => {
      checkTopScroll();
      isSwipeDisabledHoriForecast = false;
      isDebouncingForecast = false;
    }, 400)();
  }
});

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

async function ReturnHomeLocation() {
  const Locations = JSON.parse(await customStorage.getItem("DefaultLocation"));

  if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "Met norway"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`)
    );
    const renderFromSavedDataMet = JSON.parse(
      await customStorage.getItem(`WeatherDataMetNorway_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataMetTimstamp = await customStorage.getItem(
      `WeatherDataMetNorwayTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    } else {
      document
        .querySelector(".weatherCommentsDiv")
        .classList.remove("alertOpened");
      document.querySelector(".excessiveHeat").hidden = true;
    }
    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    const currentData = renderFromSavedDataMet.properties.timeseries[0];
    const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(
      0,
      24
    );

    renderCurrentDataMetNorway(currentData, Locations.lat, Locations.lon);
    renderHourlyDataMetNorway(hourlyData);
    createHourlyDataCount(renderFromSavedData);

    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0]
    );
    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    DailyWeather(renderFromSavedData.daily);
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Met norway";

    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );

    AirQuaility(AirQuailityData);

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataMetTimstamp)}`;
    }, 2400);
  } else if (
    (await customStorage.getItem("ApiForAccu")) &&
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "Accuweather"
  ) {
    const data = JSON.parse(
      await customStorage.getItem(`WeatherDataAccuCurrent_${Locations.name}`)
    );
    const dataHourly = JSON.parse(
      await customStorage.getItem(`WeatherDataAccuHourly_${Locations.name}`)
    );
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const dataTimstamp = await customStorage.getItem(
      `WeatherDataAccuTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    } else {
      document
        .querySelector(".weatherCommentsDiv")
        .classList.remove("alertOpened");
      document.querySelector(".excessiveHeat").hidden = true;
    }
    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);
    DisplayCurrentAccuweatherData(data, Locations.lat, Locations.lon);
    DisplayHourlyAccuweatherData(dataHourly);

    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );

    AirQuaility(AirQuailityData);

    createHourlyDataCount(renderFromSavedData);

    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0]
    );
    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    DailyWeather(renderFromSavedData.daily);
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Accuweather";

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(dataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(dataTimstamp)}`;
    }, 2400);
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "meteoFrance"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataMeteoFrance_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataMeteoFranceTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "dwdGermany"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataDWDGermany_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataDWDGermanyTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "noaaUS"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataNOAAUS_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataNOAAUSTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "ecmwf"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataECMWF_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataECMWFTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "ukMetOffice"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataukMetOffice_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataukMetOfficeTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "jmaJapan"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataJMAJapan_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataJMAJapanTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatagemCanada_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "bomAustralia"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatabomAustralia_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatabomAustraliaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatagemCanada_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "cmaChina"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatacmaChina_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatacmaChinaTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "knmiNetherlands"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(
        `WeatherDataknmiNetherlands_${Locations.name}`
      )
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataknmiNetherlandsTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "dmiDenmark"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatadmiDenmark_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatadmiDenmarkTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataOpenMeteo_${Locations.name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${Locations.name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${Locations.name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${Locations.name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${Locations.name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataOpenMeteoTimeStamp_${Locations.name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${Locations.name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  }

  if (Locations.name === "CurrentDeviceLocation") {
    document.getElementById("city-name").innerHTML = getTranslationByLang(
      await customStorage.getItem("AppLanguageCode"),
      "current_location"
    );
    document.getElementById("currentLocationName").textContent =
      getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "current_location"
      );
  } else {
    document.getElementById("city-name").innerHTML = Locations.name;
    document.getElementById("currentLocationName").textContent = Locations.name;
  }
  document.getElementById("SelectedLocationText").innerHTML = Locations.name;
  await customStorage.setItem("CurrentLocationName", Locations.name);

  hideLoader();
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

async function setChart() {
  if (
    (await customStorage.getItem("useBarChart")) &&
    (await customStorage.getItem("useBarChart")) === "true"
  ) {
    createTempTrendsChartBar();
  } else {
    createTempTrendsChart();
  }
}

function handleStorageChangeChart(event) {
  if (event.detail && event.detail.key === "useBarChart") {
    setTimeout(() => {
      setChart();
    }, 500);
  }
}

window.addEventListener("indexedDBChange", handleStorageChangeChart);

async function createHourlyDataCount(data) {
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

  if ((await customStorage.getItem("selectedVisibilityUnit")) === "mileV") {
    Visibility = Math.round(data.hourly.visibility[0] / 1609.34);
    VisibilityUnit = "miles";
  } else {
    Visibility = Math.round(data.hourly.visibility[0] / 1000);
    VisibilityUnit = "km";
  }

  document.getElementById("unit_visibility").innerHTML = VisibilityUnit;
  document.getElementById("min-temp").innerHTML = Visibility;

  let DewPointTemp;

  if ((await customStorage.getItem("SelectedTempUnit")) === "fahrenheit") {
    DewPointTemp = Math.round(celsiusToFahrenheit(data.hourly.dew_point_2m[0]));
  } else {
    DewPointTemp = Math.round(data.hourly.dew_point_2m[0]);
  }

  document.getElementById("dew_percentage").innerHTML = DewPointTemp + "°";
}

// load location on request

async function LoadLocationOnRequest(lat, lon, name) {
  if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "Met norway"
  ) {
    showLoader();
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataOpenMeteo_${name}`)
    );
    const renderFromSavedDataMet = JSON.parse(
      await customStorage.getItem(`WeatherDataMetNorway_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataMetTimstamp = await customStorage.getItem(
      `WeatherDataMetNorwayTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);

    const currentData = renderFromSavedDataMet.properties.timeseries[0];
    const hourlyData = renderFromSavedDataMet.properties.timeseries.slice(
      0,
      24
    );

    renderCurrentDataMetNorway(currentData, lat, lon);
    renderHourlyDataMetNorway(hourlyData);
    createHourlyDataCount(renderFromSavedData);

    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0]
    );
    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    DailyWeather(renderFromSavedData.daily);
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Met norway";

    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );

    AirQuaility(AirQuailityData);

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    hideLoader();
  } else if (
    (await customStorage.getItem("ApiForAccu")) &&
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
      "Accuweather"
  ) {
    showLoader();
    const data = JSON.parse(
      await customStorage.getItem(`WeatherDataAccuCurrent_${name}`)
    );
    const dataHourly = JSON.parse(
      await customStorage.getItem(`WeatherDataAccuHourly_${name}`)
    );
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataOpenMeteo_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const dataTimstamp = await customStorage.getItem(
      `WeatherDataAccuTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
    );

    if (SavedalertData) {
      FetchAlertRender(SavedalertData);
    }
    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

    astronomyDataRender(AstronomyData);
    MoreDetailsRender(MoreDetailsData);
    DisplayCurrentAccuweatherData(data, lat, lon);
    DisplayHourlyAccuweatherData(dataHourly);

    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );

    AirQuaility(AirQuailityData);

    createHourlyDataCount(renderFromSavedData);

    CurrentWeather(
      renderFromSavedData.current,
      renderFromSavedData.daily.sunrise[0],
      renderFromSavedData.daily.sunset[0]
    );
    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    UvIndex(renderFromSavedData.hourly.uv_index[0]);
    DailyWeather(renderFromSavedData.daily);
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    document.querySelector(".data_provider_name_import").innerHTML =
      "Data by Accuweather";

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    hideLoader();
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "meteoFrance"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataMeteoFrance_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataMeteoFranceTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "dwdGermany"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataDWDGermany_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataDWDGermanyTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "noaaUS"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataNOAAUS_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataNOAAUSTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "ecmwf"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataECMWF_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataECMWFTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "ukMetOffice"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataukMetOffice_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataukMetOfficeTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "jmaJapan"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataJMAJapan_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataJMAJapanTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", Locations.lon);
    await customStorage.setItem("currentLat", Locations.lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatagemCanada_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "bomAustralia"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatabomAustralia_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatabomAustraliaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "gemCanada"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatagemCanada_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatagemCanadaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) === "cmaChina"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatacmaChina_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatacmaChinaTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "knmiNetherlands"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataknmiNetherlands_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataknmiNetherlandsTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else if (
    (await customStorage.getItem("selectedMainWeatherProvider")) ===
    "dmiDenmark"
  ) {
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDatadmiDenmark_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDatadmiDenmarkTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1200);

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 2400);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );
  } else {
    showLoader();
    const renderFromSavedData = JSON.parse(
      await customStorage.getItem(`WeatherDataOpenMeteo_${name}`)
    );
    const dataHourlyFull = JSON.parse(
      await customStorage.getItem(`HourlyWeatherCache_${name}`)
    );
    const AirQuailityData = JSON.parse(
      await customStorage.getItem(`AirQuality_${name}`)
    );
    const MoreDetailsData = JSON.parse(
      await customStorage.getItem(`MoreDetailsData_${name}`)
    );
    const AstronomyData = JSON.parse(
      await customStorage.getItem(`AstronomyData_${name}`)
    );
    const renderFromSavedDataTimstamp = await customStorage.getItem(
      `WeatherDataOpenMeteoTimeStamp_${name}`
    );
    const SavedalertData = JSON.parse(
      await customStorage.getItem(`AlertData_${name}`)
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

    setTimeout(async () => {
      document.getElementById(
        "last_updated"
      ).innerHTML = `${getTranslationByLang(
        await customStorage.getItem("AppLanguageCode"),
        "updated"
      )}, ${timeAgo(renderFromSavedDataTimstamp)}`;
    }, 1000);

    await customStorage.setItem("currentLong", lon);
    await customStorage.setItem("currentLat", lat);

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

    await customStorage.setItem(
      "DailyWeatherCache",
      JSON.stringify(renderFromSavedData.daily)
    );
    await customStorage.setItem(
      "CurrentHourlyCache",
      JSON.stringify(renderFromSavedData.hourly)
    );
    await customStorage.setItem(
      "HourlyWeatherCache",
      JSON.stringify(dataHourlyFull)
    );

    hideLoader();
  }

  if (name === "CurrentDeviceLocation") {
    document.getElementById("city-name").innerHTML = getTranslationByLang(
      await customStorage.getItem("AppLanguageCode"),
      "current_location"
    );
  }
}

function checkTopScroll() {
  if (document.getElementById("weather_wrap").scrollTop === 0) {
    sendThemeToAndroid("EnableSwipeRefresh");
  } else {
    sendThemeToAndroid("DisableSwipeRefresh");
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

// Move app data to DataBase

async function migrateLocalStorageToIndexedDB() {
  const storage = new StorageDB();

  // Check if migration has already been completed
  const migrationFlag = await storage.getItem("migrationComplete");
  if (migrationFlag) {
    console.log("Migration already completed.");
    return;
  }

  const keys = Object.keys(localStorage);

  for (const key of keys) {
    const value = localStorage.getItem(key);
    // Save data to IndexedDB
    await storage.setItem(key, value);
  }

  localStorage.clear();

  // Set migration complete flag in IndexedDB
  await storage.setItem("migrationComplete", true);

  console.log("Migration complete!");
}

migrateLocalStorageToIndexedDB();
