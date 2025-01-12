
function handleStorageChange(event) {
    switch (event.key) {
        case 'SelectedTempUnit':
            HandleNoReloadSettings()
            if(localStorage.getItem("SelectedTempUnit") === 'fahrenheit'){
                 document.querySelector('now').innerHTML = `<span>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'now')}</span> - °F`

            } else{
                document.querySelector('now').innerHTML = `<span>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'now')}</span> - °C`
            }
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
            HandleNoReloadSettings();
            break;
        case 'AppLanguage':
            HandleNoReloadSettings();
            setTimeout(() =>{
            applyTranslations(localStorage.getItem('AppLanguageCode') || 'en');
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

                    if(localStorage.getItem("SelectedTempUnit") === 'fahrenheit'){
                 document.querySelector('now').innerHTML = `<span>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'now')}</span> - °F`

            } else{
                document.querySelector('now').innerHTML = `<span>${getTranslationByLang(localStorage.getItem("AppLanguageCode"), 'now')}</span> - °C`
            }
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


