
function handleStorageChange(event) {
    switch (event.key) {
        case 'SelectedTempUnit':
            HandleNoReloadSettings()
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
        case 'selectedPrecipitationUnit':
            HandleNoReloadSettings()
            break;
        case 'DefaultLocation':
            setTimeout(() => {
           window.location.reload();
           sendThemeToAndroid('overcast')
           }, 1500);
            break;
        case 'UseBackgroundAnimations':
            RemoveDisplayedAnimations();
            HandleNoReloadSettings()
            break;
        case 'selectedMainWeatherProvider':
            setTimeout(() => {
           window.location.reload();
           sendThemeToAndroid('overcast')
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

function HandleNoReloadSettings() {

    const renderLocation = localStorage.getItem('CurrentLocationName')
        setTimeout(() =>{
            LoadLocationOnRequest(localStorage.getItem('currentLat'), localStorage.getItem('currentLong'), renderLocation)
           onAllLocationsLoaded()
        }, 300);
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


