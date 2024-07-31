const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
const SelectedWindUnit = localStorage.getItem('SelectedWindUnit');



const apiOne = 'dd1571a8ad3fd44555e8a5d66db01929';

function getDailyForecast(latitude, longitude) {


        const apiUrl = `https://api.openweathermap.org/data/2.5/onecall?lat=${latitude}&lon=${longitude}&appid=${apiOne}&units=metric`;
    
        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                const dailyForecast = data.daily;
                displayDailyForecast(dailyForecast);
            })
            .catch(error => console.error('Error fetching daily forecast:', error));

}

function displayDailyForecast(dailyForecast) {
    const forecastContainer = document.getElementById('foreCastList');
    forecastContainer.innerHTML = '';

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    dailyForecast.forEach((forecast, index) => {

        if (index === 0) return; // Skip the current day

        const timestamp = new Date(forecast.dt * 1000);
        if (timestamp.getDate() === today.getDate()) return;

        const date = timestamp.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });


        const description = forecast.weather[0].description;
        let iconCode = forecast.weather[0].icon;
        const tempMax = Math.round(forecast.temp.max);
        const tempMIN = Math.round(forecast.temp.min);

        const tempMaxF = Math.round(tempMax * 9 / 5 + 32);

        const rainPercentage = Math.round(forecast.pop * 100)|| '0';
        const tempMINF = Math.round(tempMIN * 9 / 5 + 32);
        const rainMM = forecast.rain || '0.0';
        const pressure = forecast.pressure
        const humidity = forecast.humidity
        const cloudCover = forecast.clouds
        const dewPoint = Math.round(forecast.dew_point)
        const dewPointF = Math.round(dewPoint * 9 / 5 + 32);
        const windspeedKmh = Math.round(forecast.wind_speed);
        const windSpeedMph = Math.round(forecast.wind_speed * 2.23694);

                const uvIndex = Math.round(forecast.uvi)

                let UVindexText;

                if (uvIndex >= 0 && uvIndex <= 1) {
                    UVindexText = 'Minimal';
                } else if (uvIndex > 1 && uvIndex <= 2) {
                    UVindexText = 'Low';

                } else if (uvIndex > 2 && uvIndex <= 3) {
                    UVindexText = 'Low';

                } else if (uvIndex > 3 && uvIndex <= 4) {
                    UVindexText = 'Moderate';

                } else if (uvIndex > 4 && uvIndex <= 5) {
                    UVindexText = 'Moderate';

                } else if (uvIndex > 5 && uvIndex <= 6) {
                    UVindexText = 'Moderate';

                } else if (uvIndex > 6 && uvIndex <= 7) {
                    UVindexText = 'High';

                } else if (uvIndex > 7 && uvIndex <= 8) {
                    UVindexText = 'High';

                } else if (uvIndex > 8 && uvIndex <= 9) {
                    UVindexText = 'Very high';

                } else if (uvIndex > 9 && uvIndex <= 10) {
                    UVindexText = 'Very high';

                } else if (uvIndex > 10 && uvIndex <= 11) {
                    UVindexText = 'Very high';

                } else if (uvIndex > 11 && uvIndex <= 12) {
                    UVindexText = 'Extreme';

                } else if (uvIndex > 12 && uvIndex <= 13) {
                    UVindexText = 'Extreme';

                } else if (uvIndex > 13) {
                    UVindexText = 'Extreme';
                }

        iconCode = iconCode.replace('n', 'd');

        const forecastItem = document.createElement('div');
        forecastItem.classList.add('forecast-list-item');

        let colorStyle;

        if(iconCode === '01d'){
            colorStyle = '--weather-color: #375aa3;';
        } else if(iconCode === '02d' ){
            colorStyle = '--weather-color: #5b606b;';
        } else if(iconCode === '03d' || iconCode === '04d'){
            colorStyle = '--weather-color: #555b69;';
        } else if(iconCode === '09d' || iconCode === '10d'){
            colorStyle = '--weather-color: #33415e;';
        } else if(iconCode === '11d'){
            colorStyle = '--weather-color: #383147;'
        } else if(iconCode === '13d'){
            colorStyle = '--weather-color: #41465f;'
        } else if(iconCode === '50d'){
            colorStyle = '--weather-color: #352603;'
        }


        // create forecast-details

        forecastItem.style = colorStyle


       if(SelectedTempUnit === 'fahrenheit' && SelectedWindUnit === 'kilometer'){

               forecastItem.innerHTML = `
               <div class="daily_date_time">
                   <div>
                   <p>${date}</p>
                   <span>${description}</span></div>

                   <div>
                   <img src="../weather-icons/${iconCode}.svg">
                   </div>
               </div>

               <div class="temp_min_max">
                   <p>${tempMaxF}° <span>/ ${tempMINF}°</span></p>
               </div>

               <div class="daily_details">

                   <div class="details_daily_items">

                               <div class="daily_detail_item">
                                   <span>Precipitation chances</span>
                                     <i><img src="https://i.ibb.co/BLHjSpt/rain-per.png"></i>
                                   <p>${rainPercentage}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Precipitation amount</span>
                                    <i><img src="https://i.ibb.co/YTzNd3G/rain-amount.png"></i>
                                   <p>${rainMM} mm</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Wind speed</span>
                                     <i><img src="https://i.ibb.co/59Rv1BW/wind-speed.png"></i>
                                   <p>${windspeedKmh} km/h</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Pressure</span>
                                     <i><img src="https://i.ibb.co/C5Mpzz1/pressure.png"></i>
                                   <p>${pressure} hPa</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Cloud cover</span>
                                     <i><img src="https://i.ibb.co/Vw0BXpc/cloud-cover.png"></i>
                                   <p>${cloudCover}%</p>
                               </div>


                               <div class="daily_detail_item">
                                   <span>Humidity</span>
                                     <i><img src="https://i.ibb.co/84G9S6Z/humidity.png"></i>
                                   <p>${humidity}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Dew point</span>
                                     <i><img src="https://i.ibb.co/KrMZncB/dew-point.png"></i>
                                   <p>${dewPointF}°</p>
                               </div>

                                 <div class="daily_detail_item">
                                   <span>UV index</span>
                                     <i><img src="https://i.ibb.co/X2MtYGT/uv-index.png"></i>
                                   <p>${uvIndex} <text style="color: var(--On-Surface-Variant);font-size: 13px;">${UVindexText}</text></p>
                               </div>

                       </div>

               </div>

               `
               } else if(SelectedWindUnit === 'mile' && SelectedTempUnit === 'celsius'){
                   forecastItem.innerHTML = `
                   <div class="daily_date_time">
                       <div>
                       <p>${date}</p>
                       <span>${description}</span></div>

                       <div>
                       <img src="../weather-icons/${iconCode}.svg">
                       </div>
                   </div>

                   <div class="temp_min_max">
                       <p>${tempMax}° <span>/ ${tempMIN}°</span></p>
                   </div>

                   <div class="daily_details">

                       <div class="details_daily_items">

                               <div class="daily_detail_item">
                                   <span>Precipitation chances</span>
                                     <i><img src="https://i.ibb.co/BLHjSpt/rain-per.png"></i>
                                   <p>${rainPercentage}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Precipitation amount</span>
                                    <i><img src="https://i.ibb.co/YTzNd3G/rain-amount.png"></i>
                                   <p>${rainMM} mm</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Wind speed</span>
                                     <i><img src="https://i.ibb.co/59Rv1BW/wind-speed.png"></i>
                                   <p>${windSpeedMph} mph</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Pressure</span>
                                     <i><img src="https://i.ibb.co/C5Mpzz1/pressure.png"></i>
                                   <p>${pressure} hPa</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Cloud cover</span>
                                     <i><img src="https://i.ibb.co/Vw0BXpc/cloud-cover.png"></i>
                                   <p>${cloudCover}%</p>
                               </div>


                               <div class="daily_detail_item">
                                   <span>Humidity</span>
                                     <i><img src="https://i.ibb.co/84G9S6Z/humidity.png"></i>
                                   <p>${humidity}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Dew point</span>
                                     <i><img src="https://i.ibb.co/KrMZncB/dew-point.png"></i>
                                   <p>${dewPoint}°</p>
                               </div>

                                 <div class="daily_detail_item">
                                   <span>UV index</span>
                                     <i><img src="https://i.ibb.co/X2MtYGT/uv-index.png"></i>
                                   <p>${uvIndex} <text style="color: var(--On-Surface-Variant);font-size: 13px;">${UVindexText}</text></p>
                               </div>

                           </div>

                   </div>

                   `
               } else if(SelectedWindUnit === 'mile' && SelectedTempUnit === 'fahrenheit'){
                   forecastItem.innerHTML = `
                   <div class="daily_date_time">
                       <div>
                       <p>${date}</p>
                       <span>${description}</span></div>

                       <div>
                       <img src="../weather-icons/${iconCode}.svg">
                       </div>
                   </div>

                   <div class="temp_min_max">
                       <p>${tempMaxF}° <span>/ ${tempMINF}°</span></p>
                   </div>

                   <div class="daily_details">

                       <div class="details_daily_items">

                               <div class="daily_detail_item">
                                   <span>Precipitation chances</span>
                                     <i><img src="https://i.ibb.co/BLHjSpt/rain-per.png"></i>
                                   <p>${rainPercentage}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Precipitation amount</span>
                                    <i><img src="https://i.ibb.co/YTzNd3G/rain-amount.png"></i>
                                   <p>${rainMM} mm</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Wind speed</span>
                                     <i><img src="https://i.ibb.co/59Rv1BW/wind-speed.png"></i>
                                   <p>${windSpeedMph} mph</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Pressure</span>
                                     <i><img src="https://i.ibb.co/C5Mpzz1/pressure.png"></i>
                                   <p>${pressure} hPa</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Cloud cover</span>
                                     <i><img src="https://i.ibb.co/Vw0BXpc/cloud-cover.png"></i>
                                   <p>${cloudCover}%</p>
                               </div>


                               <div class="daily_detail_item">
                                   <span>Humidity</span>
                                     <i><img src="https://i.ibb.co/84G9S6Z/humidity.png"></i>
                                   <p>${humidity}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Dew point</span>
                                     <i><img src="https://i.ibb.co/KrMZncB/dew-point.png"></i>
                                   <p>${dewPointF}°</p>
                               </div>

                                 <div class="daily_detail_item">
                                   <span>UV index</span>
                                     <i><img src="https://i.ibb.co/X2MtYGT/uv-index.png"></i>
                                   <p>${uvIndex} <text style="color: var(--On-Surface-Variant);font-size: 13px;">${UVindexText}</text></p>
                               </div>

                           </div>

                   </div>

                   `
               } else{
                   forecastItem.innerHTML = `
                   <div class="daily_date_time">
                       <div>
                       <p>${date}</p>
                       <span>${description}</span></div>

                       <div>
                       <img src="../weather-icons/${iconCode}.svg">
                       </div>
                   </div>

                   <div class="temp_min_max">
                       <p>${tempMax}° <span>/ ${tempMIN}°</span></p>
                   </div>

                   <div class="daily_details">

                       <div class="details_daily_items">

                               <div class="daily_detail_item">
                                   <span>Precipitation chances</span>
                                     <i><img src="https://i.ibb.co/BLHjSpt/rain-per.png"></i>
                                   <p>${rainPercentage}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Precipitation amount</span>
                                    <i><img src="https://i.ibb.co/YTzNd3G/rain-amount.png"></i>
                                   <p>${rainMM} mm</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Wind speed</span>
                                     <i><img src="https://i.ibb.co/59Rv1BW/wind-speed.png"></i>
                                   <p>${windspeedKmh} km/h</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Pressure</span>
                                     <i><img src="https://i.ibb.co/C5Mpzz1/pressure.png"></i>
                                   <p>${pressure} hPa</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Cloud cover</span>
                                     <i><img src="https://i.ibb.co/Vw0BXpc/cloud-cover.png"></i>
                                   <p>${cloudCover}%</p>
                               </div>


                               <div class="daily_detail_item">
                                   <span>Humidity</span>
                                     <i><img src="https://i.ibb.co/84G9S6Z/humidity.png"></i>
                                   <p>${humidity}%</p>
                               </div>

                               <div class="daily_detail_item">
                                   <span>Dew point</span>
                                     <i><img src="https://i.ibb.co/KrMZncB/dew-point.png"></i>
                                   <p>${dewPoint}°</p>
                               </div>

                                 <div class="daily_detail_item">
                                   <span>UV index</span>
                                     <i><img src="https://i.ibb.co/X2MtYGT/uv-index.png"></i>
                                   <p>${uvIndex} <text style="color: var(--On-Surface-Variant);font-size: 13px;">${UVindexText}</text></p>
                               </div>

                           </div>

                   </div>

                   `
               }

        forecastContainer.appendChild(forecastItem);
    });
}



const latDif = localStorage.getItem('currentLat');
const longDif = localStorage.getItem('currentLong');


setTimeout(()=>{
    getDailyForecast(latDif, longDif)
}, 1500);
