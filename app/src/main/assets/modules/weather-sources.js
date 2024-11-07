
const selectMainWeatherProvider = document.getElementById('selectMainWeatherProvider');


selectMainWeatherProvider.addEventListener('input', () =>{
    localStorage.setItem('selectedMainWeatherProvider', selectMainWeatherProvider.value)

    if(selectMainWeatherProvider.value === 'Accuweather'){
      document.getElementById('accuweatherAPI_input_item').style.maxHeight = '200px';
      document.getElementById('accuweatherAPI_input_item').style.opacity = '1';


    } else{
      document.getElementById('accuweatherAPI_input_item').style.maxHeight = '0';
      document.getElementById('accuweatherAPI_input_item').style.opacity = '0';


    }
});


if(!localStorage.getItem('selectedMainWeatherProvider')){
localStorage.setItem('selectedMainWeatherProvider', 'open-meteo');
document.querySelector(`[value="open-meteo"]`).selected = true;

}

const getSelectedMainWeatherProvider = localStorage.getItem('selectedMainWeatherProvider')


if(localStorage.getItem('selectedMainWeatherProvider')){
  document.querySelector(`[value="${getSelectedMainWeatherProvider}"]`).selected = true;

  if(localStorage.getItem('selectedMainWeatherProvider') === 'Accuweather'){
    document.getElementById('accuweatherAPI_input_item').style.maxHeight = '200px';
    document.getElementById('accuweatherAPI_input_item').style.opacity = '1';

  } else{
    document.getElementById('accuweatherAPI_input_item').style.maxHeight = '0';
    document.getElementById('accuweatherAPI_input_item').style.opacity = '0';


  }
}


// -------------------------------------------------------



const selectMainAlertsProvider = document.getElementById('selectMainAlertsProvider');


selectMainAlertsProvider.addEventListener('input', () =>{
    localStorage.setItem('selectedMainAlertsProvider', selectMainAlertsProvider.value)

});


if(!localStorage.getItem('selectedMainAlertsProvider')){
localStorage.setItem('selectedMainAlertsProvider', 'weatherapi-alert');
document.querySelector(`[value="weatherapi-alert"]`).selected = true;

}

const getSelectedMainAlertsProvider = localStorage.getItem('selectedMainAlertsProvider')


if(localStorage.getItem('selectedMainAlertsProvider')){
  document.querySelector(`[value="${getSelectedMainAlertsProvider}"]`).selected = true;
}


// -------------------------------------------------------

const selectMainSearchProvider = document.getElementById('selectMainSearchProvider');


selectMainSearchProvider.addEventListener('input', () =>{
    localStorage.setItem('selectedMainSearchProvider', selectMainSearchProvider.value)

});


if(!localStorage.getItem('selectedMainSearchProvider')){
localStorage.setItem('selectedMainSearchProvider', 'open-meteo-search');
document.querySelector(`[value="open-meteo-search"]`).selected = true;

}

const getSelectedMainSearchProvider = localStorage.getItem('selectedMainSearchProvider')


if(localStorage.getItem('selectedMainSearchProvider')){
  document.querySelector(`[value="${getSelectedMainSearchProvider}"]`).selected = true;
}

// --------------------------------------------


document.getElementById('input_accuweather_field').addEventListener('input', ()=>{
  localStorage.setItem('ApiForAccuTemp', document.getElementById('input_accuweather_field').value)
});

const ApiForAccu = localStorage.getItem('ApiForAccu');

if(ApiForAccu){
  document.getElementById('input_accuweather_field').value = ApiForAccu;
} else{
  document.getElementById('input_accuweather_field').value = localStorage.getItem('ApiForAccuTemp');;

}


function verifyKeyAccu(){
  const locationKey = '188524';
  const apiKey = document.getElementById('input_accuweather_field').value;

  const weatherUrl = `https://dataservice.accuweather.com/currentconditions/v1/${locationKey}?apikey=${apiKey}`;

  fetch(weatherUrl)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok ' + response.statusText);
      }
      return response.json();
    })
    .then(data => {
      localStorage.setItem('ApiForAccu', document.getElementById('input_accuweather_field').value)
      localStorage.removeItem('ApiForAccuTemp')
       ToastAndroidShow.ShowToast('API was verified', 'long');

    })
    .catch(error => {
      console.error('There was a problem with the fetch operation:', error);
      localStorage.removeItem('ApiForAccuTemp')
      localStorage.setItem('selectedMainWeatherProvider', 'open-meteo');
       ToastAndroidShow.ShowToast('Wrong API', 'long');
    });
}