
function handleStorageChange(event) {
    switch (event.key) {
        case 'SelectedTempUnit':
            HandleNoReloadSettings()
            break;
        case 'savedLocationsCustomName':
          HandleNoReloadSettings()
        document.getElementById("city-name").innerHTML = JSON.parse(localStorage.getItem("savedLocationsCustomName"))?.[localStorage.getItem('CurrentLocationName')] || localStorage.getItem('CurrentLocationName');

        document.getElementById("currentLocationName").textContent = JSON.parse(localStorage.getItem("savedLocationsCustomName"))?.[JSON.parse(localStorage.getItem('DefaultLocation')).name] || JSON.parse(localStorage.getItem('DefaultLocation')).name;

          break;
        case 'SelectedWindUnit':
           HandleNoReloadSettings()
            break;
        case 'selectedVisibilityUnit':
            HandleNoReloadSettings()
            break;
        case 'selectedTimeMode':
            HandleNoReloadSettings()
            break;
        case 'selectedAQItype':
            HandleNoReloadSettings()
            break;
        case 'selectedPrecipitationUnit':
            HandleNoReloadSettings()
            break;
        case 'UseBackgroundAnimations':
            RemoveDisplayedAnimations();
            HandleNoReloadSettings()
            break;
        case 'selectedMainWeatherProvider':
            setTimeout(() => {
                if(localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather' && !localStorage.getItem('ApiForAccu')){
                } else{
                    window.location.reload();
                    sendThemeToAndroid('overcast')
                }
            }, 1500);
            break;
        case 'ApiForAccu':
            setTimeout(() => {
           window.location.reload();
           sendThemeToAndroid('overcast')
           }, 1500);
            break;
        case 'selectedPressureUnit':
            HandleNoReloadSettings();
            break;
        case 'useWeatherAlerts':
            HandleNoReloadSettings();
            break;
        case 'UseFrogSummary':
        case 'useAutomaticUnitsSwitch':
            HandleNoReloadSettings();
            break;
        case 'AppLanguage':
            HandleNoReloadSettings();
            setTimeout(() =>{
            applyTranslations(localStorage.getItem('AppLanguageCode') || 'en');
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
            }, 500)

            break;
        default:
            console.log('Untracked key changed:', event.key);
    }
}

window.addEventListener('storage', handleStorageChange);

// apply when options are changed

let debounceTimer;

function HandleNoReloadSettings() {
    const renderLocation = localStorage.getItem('CurrentLocationName');

    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
        LoadLocationOnRequest(localStorage.getItem('currentLat'), localStorage.getItem('currentLong'), renderLocation);
        onAllLocationsLoaded();
    }, 500);

}


function RemoveDisplayedAnimations() {

    removeCloudsFull()
    removeDrizzle()
    removeClouds()
    removeRain()
    removeFog()
    removeSnow()
    removeStars()
    removeThunder()
    removeLeaves()

}


