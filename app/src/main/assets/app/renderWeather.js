const SelectedTempUnit = localStorage.getItem("SelectedTempUnit");
const SelectedWindUnit = localStorage.getItem("SelectedWindUnit");
const SelectedVisibiltyUnit = localStorage.getItem("selectedVisibilityUnit");
const SelectedPrecipitationUnit = localStorage.getItem(
  "selectedPrecipitationUnit"
);
const SelectedPressureUnit = localStorage.getItem("selectedPressureUnit");
const timeFormat = localStorage.getItem("selectedTimeMode");

let timezone = ''
let utcOffsetSeconds = ''
let summaryData = []

function HourlyWeather(data) {
  const forecastContainer = document.getElementById("forecast");
  const RainBarsContainer = document.querySelector("rainMeterBar");

  timezone = data.timezone
  utcOffsetSeconds = data.utc_offset_seconds

  const timezoneMain = data.timezone
   const utcOffsetSecondsMain = data.utc_offset_seconds

  forecastContainer.innerHTML = "";
  RainBarsContainer.innerHTML = "";

  let local

  if (
    localStorage.getItem("AppLanguageCode") === '' ||
    localStorage.getItem("AppLanguageCode") === 'nl' || // Dutch
    localStorage.getItem("AppLanguageCode") === 'fr' || // French
    localStorage.getItem("AppLanguageCode") === 'de' || // German
    localStorage.getItem("AppLanguageCode") === 'el' || // Greek
    localStorage.getItem("AppLanguageCode") === 'it' || // Italian
    localStorage.getItem("AppLanguageCode") === 'fa' || // Persian
    localStorage.getItem("AppLanguageCode") === 'pl' || // Polish
    localStorage.getItem("AppLanguageCode") === 'pt' || // Portuguese
    localStorage.getItem("AppLanguageCode") === 'ro' || // Romanian
    localStorage.getItem("AppLanguageCode") === 'ru' || // Russian
    localStorage.getItem("AppLanguageCode") === 'es' || // Spanish
    localStorage.getItem("AppLanguageCode") === 'tr' || // Turkish
    localStorage.getItem("AppLanguageCode") === 'uk' || // Ukrainian
    localStorage.getItem("AppLanguageCode") === 'sr' || // Serbian
    localStorage.getItem("AppLanguageCode") === 'az' || // Azerbaijani
    localStorage.getItem("AppLanguageCode") === 'sl' || // Slovenian
    localStorage.getItem("AppLanguageCode") === 'fi' || // Finnish
    localStorage.getItem("AppLanguageCode") === 'hu' ||  // Hungarian
    localStorage.getItem("AppLanguageCode") === 'cs'    // Czech

  ) {
    local = ','
  } else{
    local = '.'

  }


  if (
    !data ||
    !data.hourly ||
    !data.hourly.time ||
    !data.hourly.weather_code ||
    !data.hourly.temperature_2m
  ) {
    console.error("Hourly forecast data is missing or undefined");
    return;
  }
  const sunriseTimes = data.daily.sunrise.map((time) =>
    new Date(time).getTime()
  );
  const sunsetTimes = data.daily.sunset.map((time) => new Date(time).getTime());

  const rainThreshold = 0.5;

  let rainStopping = null;
  let rainComing = null;

  const currentRainTime = data.hourly.time[0];
  const currentRainAmount = data.hourly.precipitation[0];

  const isRainingNow = currentRainAmount > rainThreshold;


  function getTimeInISOFormat(timezone, utcOffsetSeconds) {
    const currentDate = new Date();

    const localDate = new Date(currentDate.getTime() + utcOffsetSeconds * 1000);

    const year = localDate.getUTCFullYear();
    const month = String(localDate.getUTCMonth() + 1).padStart(2, '0');
    const day = String(localDate.getUTCDate()).padStart(2, '0');
    const hours = String(localDate.getUTCHours()).padStart(2, '0');
    const minutes = String(localDate.getUTCMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
}


const currentTime = new Date(getTimeInISOFormat(timezoneMain, utcOffsetSecondsMain));

const currentHour = currentTime.getHours();

const filteredData = data.hourly.time
  .map((time, index) => {
    const forecastTime = new Date(time);
    return { time: forecastTime, index };
  })
  .filter(({ time }) => {
    if (time.getHours() === currentHour) {
      return true;
    }
    return time >= currentTime;
  });



  filteredData.forEach(({ time, index }, i) => {
    const forecastTime = time;

    let hours;
    let period;

    if (localStorage.getItem("selectedTimeMode") === "24 hour") {
      hours = new Date(time).getHours().toString().padStart(2, "0") + ":";
      period = new Date(time).getMinutes().toString().padStart(2, "0");
    } else {
      hours = new Date(time).getHours();
      period = hours >= 12 ? "PM" : "AM";
      hours = hours % 12 || 12;
    }

    const HourWeatherCode = data.hourly.weather_code[index];

    const rainMeterBarItem = document.createElement("rainMeterBarItem");

    UvIndex(data.hourly.uv_index[0]);

    const rainAmountALL = data.hourly.precipitation[index];

    if (isRainingNow) {
      if (!rainStopping && rainAmountALL <= rainThreshold && index > 0) {
        rainStopping = `${getTranslationByLang(
          localStorage.getItem("AppLanguageCode"),
          "rain_to_stop_at"
        )} ${hours}${period}`;
      }
    } else if (!isRainingNow && !rainComing && rainAmountALL > rainThreshold) {
      const currentTime = new Date(data.hourly.time[0]);
      const rainTime = new Date(time);

      const isTomorrow =
        rainTime.getDate() > currentTime.getDate() ||
        (rainTime.getDate() < currentTime.getDate() &&
          rainTime.getMonth() > currentTime.getMonth());

      rainComing = isTomorrow
        ? `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "rain_likely_tomorrow_around"
          )} ${hours}${period}`
        : `${getTranslationByLang(
            localStorage.getItem("AppLanguageCode"),
            "rain_likely_around"
          )} ${hours}${period}`;
    }

    let PrecAmount;

    if (localStorage.getItem("selectedPrecipitationUnit") === "in") {
      PrecAmount = mmToInches(data.hourly.precipitation[index]).toFixed(2).replace('.', local) + "";
    } else if (localStorage.getItem("selectedPrecipitationUnit") === "cm") {
      PrecAmount = (Math.round(data.hourly.precipitation[index]) / 10).toFixed(
        2
      ).replace('.', local);
    } else {
      PrecAmount = data.hourly.precipitation[index].toFixed(1).replace('.', local) + "";
    }

    const PrecProb = data.hourly.precipitation_probability[index];

    let dayIndex = -1;
    for (let i = 0; i < sunriseTimes.length; i++) {
      if (
        forecastTime >= sunriseTimes[i] &&
        forecastTime < (sunriseTimes[i + 1] || Infinity)
      ) {
        dayIndex = i;
        break;
      }
    }
    let icon;
    if (
      dayIndex !== -1 &&
      forecastTime >= sunriseTimes[dayIndex] &&
      forecastTime < sunsetTimes[dayIndex]
    ) {
      icon = GetWeatherIconDay(HourWeatherCode); // Day icon
    } else {
      icon = GetWeatherIconNight(HourWeatherCode); // Night icon
    }

    const maxRain = 2;
    const rainAmountPercent = data.hourly.precipitation[index]
      ? (data.hourly.precipitation[index] / maxRain) * 100
      : 0;

    let barColor;

    if (data.hourly.precipitation[index] < 0.5) {
      barColor = "var(--Primary-Container)";
    } else if (
      data.hourly.precipitation[index] > 0.5 &&
      data.hourly.precipitation[index] <= 1
    ) {
      barColor = "var(--Primary-Container)";
    } else if (data.hourly.precipitation[index] > 1) {
      barColor = "var(--Primary)";
    }

    let HourTemperature;

    if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
      HourTemperature = Math.round(
        celsiusToFahrenheit(data.hourly.temperature_2m[index])
      );
    } else {
      HourTemperature = Math.round(data.hourly.temperature_2m[index]);
    }

    const forecastItem = document.createElement("div");
    forecastItem.classList.add("forecast-item");
    forecastItem.id = "forecast24";

    forecastItem.innerHTML = `
                <p class="temp-24">${HourTemperature}°</p>
 ${
   i === 0
     ? `
        <svg height="33.0dip" width="33.0dip" viewBox="0 0 33.0 33.0"
            xmlns="http://www.w3.org/2000/svg" class="hourly_forecast_star">
            <path style="fill: var(--Inverse-Primary);" d="M20.926,1.495C27.8,-1.49 34.776,5.486 31.791,12.36L31.297,13.496C30.386,15.595 30.386,17.977 31.297,20.076L31.791,21.212C34.776,28.086 27.8,35.062 20.926,32.077L19.79,31.583C17.691,30.672 15.309,30.672 13.21,31.583L12.074,32.077C5.2,35.062 -1.776,28.086 1.209,21.212L1.703,20.076C2.614,17.977 2.614,15.595 1.703,13.496L1.209,12.36C-1.776,5.486 5.2,-1.49 12.074,1.495L13.21,1.989C15.309,2.9 17.691,2.9 19.79,1.989L20.926,1.495Z" />
        </svg>`
     : ""
 }
                <img id="icon-24" src="${icon}" class="icon-24">
                <p class="time-24">${hours}${period}</p>
                <p class="disc_sml-24"></p>
                <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>
            `;

    rainMeterBarItem.innerHTML = `
                <rainPerBar>
                  <rainPerBarProgress style="height: ${Math.round(
                    rainAmountPercent
                  )}%; background-color: ${barColor};"">
                </rainPerBarProgress>
                </rainPerBar>
                <p class="prec_amount_bar">${PrecAmount}</p>
                <p>${
                  data.hourly.precipitation_probability[index] != null
                    ? data.hourly.precipitation_probability[index] + "%"
                    : "--"
                }</p>
                 <span>${hours}${period}</span>


            `;

    forecastItem.addEventListener("click", () => {
      ShowSnack(
        `<span style="text-transform: capitalize;">${getWeatherLabelInLangNoAnim(
          HourWeatherCode,
          1,
          localStorage.getItem("AppLanguageCode")
        )}</span>`,
        2000,
        3,
        "none",
        " ",
        "no-up"
      );
    });

    RainBarsContainer.append(rainMeterBarItem);
    forecastContainer.appendChild(forecastItem);
  });

  if (isRainingNow) {
    if (rainStopping) {
      document.getElementById("rainStopingText").innerHTML = rainStopping;
      document.querySelector(".whenRainPill").hidden = false;
    } else {
      document.querySelector(".whenRainPill").hidden = false;
      document.getElementById("rainStopingText").innerHTML =
        getTranslationByLang(
          localStorage.getItem("AppLanguageCode"),
          "rain_expected_to_continue"
        );
    }
  } else if (rainComing) {
    document.getElementById("rainStopingText").innerHTML = rainComing;
    document.querySelector(".whenRainPill").hidden = false;
  } else {
    document.querySelector(".whenRainPill").hidden = true;
  }
}

// daily

function DailyWeather(dailyForecast) {

  let local

  if (
    localStorage.getItem("AppLanguageCode") === '' ||
    localStorage.getItem("AppLanguageCode") === 'nl' || // Dutch
    localStorage.getItem("AppLanguageCode") === 'fr' || // French
    localStorage.getItem("AppLanguageCode") === 'de' || // German
    localStorage.getItem("AppLanguageCode") === 'el' || // Greek
    localStorage.getItem("AppLanguageCode") === 'it' || // Italian
    localStorage.getItem("AppLanguageCode") === 'fa' || // Persian
    localStorage.getItem("AppLanguageCode") === 'pl' || // Polish
    localStorage.getItem("AppLanguageCode") === 'pt' || // Portuguese
    localStorage.getItem("AppLanguageCode") === 'ro' || // Romanian
    localStorage.getItem("AppLanguageCode") === 'ru' || // Russian
    localStorage.getItem("AppLanguageCode") === 'es' || // Spanish
    localStorage.getItem("AppLanguageCode") === 'tr' || // Turkish
    localStorage.getItem("AppLanguageCode") === 'uk' || // Ukrainian
    localStorage.getItem("AppLanguageCode") === 'sr' || // Serbian
    localStorage.getItem("AppLanguageCode") === 'az' || // Azerbaijani
    localStorage.getItem("AppLanguageCode") === 'sl' || // Slovenian
    localStorage.getItem("AppLanguageCode") === 'fi' || // Finnish
    localStorage.getItem("AppLanguageCode") === 'hu' ||  // Hungarian
    localStorage.getItem("AppLanguageCode") === 'cs'    // Czech
  ) {
    local = ','
  } else{
    local = '.'

  }

  setChart();

  const forecastContainer = document.getElementById("forecast-5day");
  forecastContainer.innerHTML = "";

  const today = new Date().toISOString().split("T")[0];

  const warmingComments = [
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "warming_temp_trend_1"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "warming_temp_trend_2"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "warming_temp_trend_3"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "warming_temp_trend_4"
    ),
  ];

  const coolingComments = [
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cooling_temp_trend_1"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cooling_temp_trend_2"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cooling_temp_trend_3"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cooling_temp_trend_4"
    ),
  ];

  const stableComments = [
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "stable_temp_trend_1"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "stable_temp_trend_1"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "stable_temp_trend_1"
    ),
    getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "stable_temp_trend_1"
    ),
  ];

  function getRandomComment(commentsArray) {
    const randomIndex = Math.floor(Math.random() * commentsArray.length);
    return commentsArray[randomIndex];
  }

  const trendDaysArray = [2, 3, 6];
  let trendMessage = "";

  for (let trendDays of trendDaysArray) {
    if (dailyForecast.time.length >= trendDays) {
      let tempDifferenceSum = 0;

      for (let i = 0; i < trendDays - 1; i++) {
        const currentDayAvgTemp =
          (dailyForecast.temperature_2m_max[i] +
            dailyForecast.temperature_2m_min[i]) /
          2;
        const nextDayAvgTemp =
          (dailyForecast.temperature_2m_max[i + 1] +
            dailyForecast.temperature_2m_min[i + 1]) /
          2;
        tempDifferenceSum += nextDayAvgTemp - currentDayAvgTemp;
      }

      if (tempDifferenceSum > 0) {
        trendMessage = getRandomComment(warmingComments);
        document.getElementById("temp_insight_icon").innerHTML = "trending_up";
      } else if (tempDifferenceSum < 0) {
        trendMessage = getRandomComment(coolingComments);
        document.getElementById("temp_insight_icon").innerHTML =
          "trending_down";
      } else {
        trendMessage = getRandomComment(stableComments);
        document.getElementById("temp_insight_icon").innerHTML = "thermostat";
      }

      break;
    }
  }

  document.getElementById("temp_insight").innerHTML = trendMessage;

  const weekDaysCache = [];
    summaryData = []


  dailyForecast.time.forEach((time, index) => {
    const [year, month, day] = time.split("-").map(Number);
    const dateObj = new Date(year, month - 1, day);

    const today = new Date();
    const isSameDay =
      dateObj.getFullYear() === today.getFullYear() &&
      dateObj.getMonth() === today.getMonth() &&
      dateObj.getDate() === today.getDate();

    const weekday = isSameDay
      ? "today"
      : dateObj.toLocaleDateString("en-US", { weekday: "short" }).toLowerCase();
    const weekdayLang = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      weekday
    );

    const send1stDay = dailyForecast.weather_code[0];

    ReportFromdaily(send1stDay);

    weekDaysCache.push(getTranslationByLang(localStorage.getItem("AppLanguageCode"), weekday));

    const rainPercentage = dailyForecast.precipitation_probability_max[index];
    const DailyWeatherCode = dailyForecast.weather_code[index];

    let TempMin;

    if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
      TempMin = Math.round(
        celsiusToFahrenheit(dailyForecast.temperature_2m_min[index])
      );
    } else {
      TempMin = Math.round(dailyForecast.temperature_2m_min[index]);
    }

    let TempMax;

    if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
      TempMax = Math.round(
        celsiusToFahrenheit(dailyForecast.temperature_2m_max[index])
      );
    } else {
      TempMax = Math.round(dailyForecast.temperature_2m_max[index]);
    }

    let TempMinCurrent;

    if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
      TempMinCurrent = Math.round(
        celsiusToFahrenheit(dailyForecast.temperature_2m_min[0])
      );
    } else {
      TempMinCurrent = Math.round(dailyForecast.temperature_2m_min[0]);
    }

    let TempMaxCurrent;

    if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
      TempMaxCurrent = Math.round(
        celsiusToFahrenheit(dailyForecast.temperature_2m_max[0])
      );
    } else {
      TempMaxCurrent = Math.round(dailyForecast.temperature_2m_max[0]);
    }

        summaryData.push({tempMax: TempMaxCurrent, tempMin: TempMinCurrent, uvIndexMax: dailyForecast.uv_index_max[0]});


    document.getElementById("high_temp").innerHTML = TempMaxCurrent + "°";
    document.getElementById("low_temp").innerHTML = TempMinCurrent + "°";

    const forecastItem = document.createElement("div");
    forecastItem.classList.add("forecast-item-forecast");

    forecastItem.setAttribute(
      "onclick",
      `clickForecastItem(${index}); sendThemeToAndroid("Open8Forecast");`
    );

    forecastItem.innerHTML = `
        <p class="disc-5d">${TempMax}°<span> ${TempMin}°</span></p>

        <img id="icon-5d" src="${GetWeatherIcon(
          DailyWeatherCode,
          1
        )}" alt="Weather Icon">
        <span class="daily_rain">${
          rainPercentage != null ? rainPercentage + "%" : "--"
        }</span>
        <div class="d5-disc-text">
        <p class="time-5d">${weekdayLang}</p>
        </div>
      <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>
        `;
    const daylightDurationInSeconds = dailyForecast.daylight_duration[0];
    const daylightHours = Math.floor(daylightDurationInSeconds / 3600);
    const daylightMinutes = Math.floor((daylightDurationInSeconds % 3600) / 60);

    document.getElementById(
      "day_length_text"
    ).innerHTML = `${daylightHours} hrs ${daylightMinutes} mins ${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "day_length"
    )}`;

    let TodaysPrecAmount;

    if (localStorage.getItem("selectedPrecipitationUnit") === "in") {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        TodaysPrecAmount = mmToInches(dailyForecast.precipitation_sum[0]).toFixed(2).replace('.', local) + " 英寸";
      } else{
      TodaysPrecAmount = mmToInches(dailyForecast.precipitation_sum[0]).toFixed(2).replace('.', local) + " in";
      }
    } else if (localStorage.getItem("selectedPrecipitationUnit") === "cm") {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        TodaysPrecAmount =
        (Math.round(dailyForecast.precipitation_sum[0]) / 10).toFixed(2).replace('.', local) +
        " 厘米";
      } else{
        TodaysPrecAmount =
          (Math.round(dailyForecast.precipitation_sum[0]) / 10).toFixed(2).replace('.', local) +
          " cm";
      }
    } else {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        TodaysPrecAmount = dailyForecast.precipitation_sum[0].toFixed(1).replace('.', local) + " 毫米";
      } else{
         TodaysPrecAmount = dailyForecast.precipitation_sum[0].toFixed(1).replace('.', local) + " mm";

      }
    }

    if (dailyForecast.precipitation_sum[0] <= 0) {
      document.querySelector("rainmeterbar").hidden = true;
      document.querySelector(".whenRainPill").hidden = true;
    } else {
      document.querySelector("rainmeterbar").hidden = false;
    }

    document.getElementById("AmountRainMM").innerHTML = TodaysPrecAmount;

    document.getElementById("RainHours").innerHTML =
      dailyForecast.precipitation_hours[0] + " hrs";

    forecastContainer.appendChild(forecastItem);
  });

  async function saveWeekcache(){
    await customStorage.setItem("forecastWeekdays", JSON.stringify(weekDaysCache));
  }

  saveWeekcache()
}

// current

function CurrentWeather(data, sunrise, sunset, lat, lon) {
  let local

  if (
    localStorage.getItem("AppLanguageCode") === '' ||
    localStorage.getItem("AppLanguageCode") === 'nl' || // Dutch
    localStorage.getItem("AppLanguageCode") === 'fr' || // French
    localStorage.getItem("AppLanguageCode") === 'de' || // German
    localStorage.getItem("AppLanguageCode") === 'el' || // Greek
    localStorage.getItem("AppLanguageCode") === 'it' || // Italian
    localStorage.getItem("AppLanguageCode") === 'fa' || // Persian
    localStorage.getItem("AppLanguageCode") === 'pl' || // Polish
    localStorage.getItem("AppLanguageCode") === 'pt' || // Portuguese
    localStorage.getItem("AppLanguageCode") === 'ro' || // Romanian
    localStorage.getItem("AppLanguageCode") === 'ru' || // Russian
    localStorage.getItem("AppLanguageCode") === 'es' || // Spanish
    localStorage.getItem("AppLanguageCode") === 'tr' || // Turkish
    localStorage.getItem("AppLanguageCode") === 'uk' || // Ukrainian
    localStorage.getItem("AppLanguageCode") === 'sr' || // Serbian
    localStorage.getItem("AppLanguageCode") === 'az' || // Azerbaijani
    localStorage.getItem("AppLanguageCode") === 'sl' || // Slovenian
    localStorage.getItem("AppLanguageCode") === 'fi' || // Finnish
    localStorage.getItem("AppLanguageCode") === 'hu' ||  // Hungarian
    localStorage.getItem("AppLanguageCode") === 'cs'    // Czech
  ) {
    local = ','
  } else{
    local = '.'

  }

  const CurrentCloudCover = data.cloud_cover;
  const CurrentHumidity = Math.round(data.relative_humidity_2m);
  const CurrentWeatherCode = data.weather_code;
  const CurrentWindDirection = data.wind_direction_10m;
  const isDay = data.is_day;

  let CurrentTemperature;
  let FeelsLikeTemp;

  if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
    CurrentTemperature = Math.round(celsiusToFahrenheit(data.temperature_2m));
    FeelsLikeTemp = Math.round(celsiusToFahrenheit(data.apparent_temperature));
  } else {
    CurrentTemperature = Math.round(data.temperature_2m);
    FeelsLikeTemp = Math.round(data.apparent_temperature);
  }

  if (CurrentTemperature < 10 && CurrentTemperature >= 0) {
    CurrentTemperature = "" + CurrentTemperature;
  }

  function getBeaufort(speedKmh) {
    const levels = [1, 5, 11, 19, 28, 38, 49, 61, 74, 88, 102, 117];
    return levels.findIndex(level => speedKmh <= level) + 1 || 12;
}

  let CurrentWindGust;

  if (localStorage.getItem("SelectedWindUnit") === "mile") {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        CurrentWindGust = Math.round(kmhToMph(data.wind_gusts_10m)) + " 英里/时";
      } else{
        CurrentWindGust = Math.round(kmhToMph(data.wind_gusts_10m)) + " mph";
      }
  } else if (localStorage.getItem("SelectedWindUnit") === "M/s") {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        CurrentWindGust = (data.wind_gusts_10m / 3.6).toFixed(1).replace('.', local) + " 米/秒";
      } else{
        CurrentWindGust = (data.wind_gusts_10m / 3.6).toFixed(1).replace('.', local) + " m/s";
      }
  } else if (localStorage.getItem("SelectedWindUnit") === "Beaufort") {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        CurrentWindGust = getBeaufort(data.wind_speed_10m) + " 蒲福风级";
      } else{
        CurrentWindGust = getBeaufort(data.wind_speed_10m) + " Bft";
      }
  } else {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        CurrentWindGust = Math.round(data.wind_gusts_10m) + " 公里/时";
      } else{
             CurrentWindGust = Math.round(data.wind_gusts_10m) + " km/h";
      }
  }

  let CurrentWindSpeed;

  if (localStorage.getItem("SelectedWindUnit") === "mile") {
    if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
      CurrentWindSpeed = Math.round(kmhToMph(data.wind_speed_10m)) + " 英里/时";
    } else{
      CurrentWindSpeed = Math.round(kmhToMph(data.wind_speed_10m)) + " mph";

    }
  } else if (localStorage.getItem("SelectedWindUnit") === "M/s") {
      if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
        CurrentWindSpeed = (data.wind_speed_10m / 3.6).toFixed(1).replace('.', local) + " 米/秒";
      } else{
        CurrentWindSpeed = (data.wind_speed_10m / 3.6).toFixed(1).replace('.', local) + " m/s";
      }
  } else if (localStorage.getItem("SelectedWindUnit") === "Beaufort") {
    if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
      CurrentWindSpeed = getBeaufort(data.wind_speed_10m) + " 蒲福风级";
    } else{
    CurrentWindSpeed = getBeaufort(data.wind_speed_10m) + " Bft";
    }
  } else {
  if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
    CurrentWindSpeed = Math.round(data.wind_speed_10m) + " 公里/时";
  } else{
    CurrentWindSpeed = Math.round(data.wind_speed_10m) + " km/h";
    }
  }

  let CurrentPressure;
  let pressureMainUnit;

  if (localStorage.getItem("selectedPressureUnit") === "inHg") {
    CurrentPressure = hPaToInHg(data.pressure_msl).toFixed(2).replace('.', local);
    if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
    pressureMainUnit = "英寸汞柱";
    } else{
    pressureMainUnit = "inHg";
    }
  } else if (localStorage.getItem("selectedPressureUnit") === "mmHg") {
    CurrentPressure = hPaToMmHg(data.pressure_msl).toFixed(2).replace('.', local);
    if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
    pressureMainUnit = "毫米汞柱";
    } else{
    pressureMainUnit = "mmHg";
    }
  } else {
    CurrentPressure = Math.round(data.pressure_msl);
    if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
    pressureMainUnit = "百帕";
    } else{
    pressureMainUnit = "hPa";
    }
  }

  localStorage.setItem("CurrentPressurePage", data.pressure_msl);

  let CurrentPrecipitation;

  if (localStorage.getItem("selectedPrecipitationUnit") === "in") {
    CurrentPrecipitation = mmToInches(Math.round(data.precipitation));
  } else if (localStorage.getItem("selectedPrecipitationUnit") === "cm") {
    CurrentPrecipitation = (Math.round(data.precipitation) / 10).toFixed(2).replace('.', local);
  } else {
    CurrentPrecipitation = Math.round(data.precipitation);
  }

  // -------------------------------
 if (
    localStorage.getItem("ApiForAccu") &&
    localStorage.getItem("selectedMainWeatherProvider") === "Accuweather"
  ) {
  } else {
    animateTemp(CurrentTemperature);

    document.getElementById("weather-icon").src = GetWeatherIcon(
      CurrentWeatherCode,
      isDay
    );
    document.getElementById("weather-icon").alt = CurrentWeatherCode;
    document.getElementById("description").innerHTML = getWeatherLabelInLang(
      CurrentWeatherCode,
      isDay,
      localStorage.getItem("AppLanguageCode")
    );
    document.getElementById("froggie_imgs").src = GetFroggieIcon(
      CurrentWeatherCode,
      isDay
    );
    document.documentElement.setAttribute(
      "iconcodetheme",
      GetWeatherTheme(CurrentWeatherCode, isDay)
    );
    sendThemeToAndroid(GetWeatherTheme(CurrentWeatherCode, 1));

    renderHomeLocationSearchData();

    function renderHomeLocationSearchData() {
      const checkIFitsSavedLocation = JSON.parse(
        localStorage.getItem("DefaultLocation")
      );

      function isApproxEqual(val1, val2, epsilon = 0.0001) {
        return Math.abs(val1 - val2) < epsilon;
      }

      if (checkIFitsSavedLocation) {
        const savedLat = parseFloat(checkIFitsSavedLocation.lat);
        const savedLon = parseFloat(checkIFitsSavedLocation.lon);
        const savedName = checkIFitsSavedLocation.name;

        if (
          savedLat !== undefined &&
          savedLon !== undefined &&
          isApproxEqual(lat, savedLat) &&
          isApproxEqual(lon, savedLon)
        ) {
          document.getElementById(
            "temPDiscCurrentLocation"
          ).innerHTML = `${getWeatherLabelInLang(
            CurrentWeatherCode,
            isDay,
            localStorage.getItem("AppLanguageCode")
          )}`;
          document.getElementById("currentSearchImg").src = `${GetWeatherIcon(
            CurrentWeatherCode,
            isDay
          )}`;
          document.querySelector(
            "mainCurrenttemp"
          ).innerHTML = `${CurrentTemperature}°`;
        }
      }
    }
  }

  document.getElementById("feels_like_now").innerHTML =
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "feels_like"
    )} ` +
    FeelsLikeTemp +
    "°";
  document.getElementById("wind-speed").innerHTML = CurrentWindSpeed;
  document.getElementById("WindGust").innerHTML = CurrentWindGust;
  document.getElementById("clouds").innerHTML = CurrentCloudCover + "%";

  document.getElementById("humidity").innerHTML = CurrentHumidity + "%";

  document.getElementById("pressure_text_main").innerHTML = CurrentPressure;
  document.getElementById("pressureMainUnit").innerHTML = pressureMainUnit;

  document.getElementById(
    "windDirectionArrow"
  ).style.transform = `rotate(${CurrentWindDirection}deg)`;

  function getWindDirection(degree) {
    if ((degree >= 0 && degree <= 22.5) || (degree > 337.5 && degree <= 360))
      return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_n");
    if (degree > 22.5 && degree <= 67.5) return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_ne");
    if (degree > 67.5 && degree <= 112.5) return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_e");
    if (degree > 112.5 && degree <= 157.5) return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_se");
    if (degree > 157.5 && degree <= 202.5) return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_s");
    if (degree > 202.5 && degree <= 247.5) return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_sw");
    if (degree > 247.5 && degree <= 292.5) return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_w");
    if (degree > 292.5 && degree <= 337.5) return getTranslationByLang(localStorage.getItem("AppLanguageCode"), "wind_dir_nw");
  }

  document.getElementById("directionWind").innerHTML =
    getWindDirection(CurrentWindDirection);

  const windspeedType = document.getElementById("windtype");

  if (data.wind_speed_10m < 1) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "calm"
    );
  } else if (data.wind_speed_10m < 5) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "light_air"
    );
  } else if (data.wind_speed_10m < 11) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "light_breeze"
    );
  } else if (data.wind_speed_10m < 19) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "gentle_breeze"
    );
  } else if (data.wind_speed_10m < 28) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "moderate_breeze"
    );
  } else if (data.wind_speed_10m < 38) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "fresh_breeze"
    );
  } else if (data.wind_speed_10m < 49) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "strong_breeze"
    );
  } else if (data.wind_speed_10m < 61) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "high_wind"
    );
  } else if (data.wind_speed_10m < 74) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "gale"
    );
  } else if (data.wind_speed_10m < 88) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "strong_gale"
    );
  } else if (data.wind_speed_10m < 102) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "storm"
    );
  } else if (data.wind_speed_10m < 117) {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "violent_storm"
    );
  } else {
    windspeedType.innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "hurricane"
    );
  }

  if (data.pressure_msl < "980") {
    document.getElementById("pressure_icon_svg").innerHTML =
      WidgetsPressure.LowPressure;
  } else if (data.pressure_msl > "980" && data.pressure_msl <= "1005") {
    document.getElementById("pressure_icon_svg").innerHTML =
      WidgetsPressure.LowMedPressure;
  } else if (data.pressure_msl > "1005" && data.pressure_msl <= "1020") {
    document.getElementById("pressure_icon_svg").innerHTML =
      WidgetsPressure.MediumPressure;
  } else if (data.pressure_msl > "1020" && data.pressure_msl <= "1035") {
    document.getElementById("pressure_icon_svg").innerHTML =
      WidgetsPressure.HighPressure;
  } else if (data.pressure_msl > "1036") {
    document.getElementById("pressure_icon_svg").innerHTML =
      WidgetsPressure.VeryHighPressure;
  }

  setTimeout(() => {
    if (CurrentHumidity < 30) {
      document.getElementById("humidity_icon_svg").innerHTML =
        WidgetsHumidity.Humidity_7;
    } else if (CurrentHumidity >= 30 && CurrentHumidity < 50) {
      document.getElementById("humidity_icon_svg").innerHTML =
        WidgetsHumidity.Humidity_30;
    } else if (CurrentHumidity >= 50 && CurrentHumidity < 70) {
      document.getElementById("humidity_icon_svg").innerHTML =
        WidgetsHumidity.Humidity_50;
    } else if (CurrentHumidity >= 70 && CurrentHumidity < 90) {
      document.getElementById("humidity_icon_svg").innerHTML =
        WidgetsHumidity.Humidity_70;
    } else if (CurrentHumidity >= 90) {
      document.getElementById("humidity_icon_svg").innerHTML =
        WidgetsHumidity.Humidity_90;
    } else {
      console.log("Error");
    }
  }, 300);

  const convertTo12Hour = (time) => {
    const date = new Date(time);
    return date.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    });
  };

  const convertTo24Hour = (time) => {
    const date = new Date(time);
    return date.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  };

  function calculateTimeDifference(targetTime) {
    const now = new Date(data.time);
    const targetDate = new Date(targetTime);

    const diffInMilliseconds = targetDate - now;

    return Math.round(diffInMilliseconds / 60000);
  }

  const diffToSunrise = calculateTimeDifference(sunrise);
  const diffToSunset = calculateTimeDifference(sunset);

  if (localStorage.getItem("selectedTimeMode") === "24 hour") {
    document.getElementById("sunrise").innerHTML = convertTo24Hour(sunrise);
    document.getElementById("sunset").innerHTML = convertTo24Hour(sunset);
  } else {
    document.getElementById("sunrise").innerHTML = convertTo12Hour(sunrise);
    document.getElementById("sunset").innerHTML = convertTo12Hour(sunset);
  }

  //    if (diffToSunrise <= 40 && diffToSunrise >= 0) {
  //        document.getElementById('sunrise_insight').hidden = false;
  //        document.getElementById('sunrise_insight').classList.add('insights_item')
  //
  //                document.getElementById('scroll-indicators').innerHTML = ''
  //                setTimeout(()=>{
  //                    document.querySelector('.insights').scrollLeft = 0
  //
  //                createScrollDots()
  //                }, 1500);
  //
  //    } else{
  //        document.getElementById('sunrise_insight').hidden = true;
  //        document.getElementById('sunrise_insight').classList.remove('insights_item')
  //
  //    }
  //
  //    if (diffToSunset <= 40 && diffToSunset >= 0) {
  //        document.getElementById('sunset_insight').hidden = false;
  //        document.getElementById('sunset_insight').classList.add('insights_item')
  //
  //        document.getElementById('scroll-indicators').innerHTML = ''
  //        setTimeout(()=>{
  //            document.querySelector('.insights').scrollLeft = 0
  //
  //        createScrollDots()
  //        }, 1500);
  //
  //    } else{
  //        document.getElementById('sunset_insight').hidden = true;
  //        document.getElementById('sunset_insight').classList.remove('insights_item')
  //
  //    }

  const now = new Date(data.time);
  const lastUpdated = new Date(data.time);
  const minutesAgo = Math.floor((now - lastUpdated) / 60000);

  //document.getElementById('last_updated').innerHTML = ''

  //setTimeout(()=>{
  //    if (minutesAgo > 1) {
  //        document.getElementById('last_updated').innerHTML = `Updated ${minutesAgo} mins ago`;
  //    } else if (minutesAgo < 1) {
  //        document.getElementById('last_updated').innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'updated_just_now');;
  //    } else {
  //        document.getElementById('last_updated').innerHTML = `Updated ${minutesAgo} min ago`;
  //    }
  //}, 300)
  //

function getTimeInISOFormat(utcOffsetSeconds) {
    const currentDate = new Date();
    const localDate = new Date(currentDate.getTime() + utcOffsetSeconds * 1000);


    const year = localDate.getUTCFullYear();
    const month = String(localDate.getUTCMonth() + 1).padStart(2, '0');
    const day = String(localDate.getUTCDate()).padStart(2, '0');
    const hours = String(localDate.getUTCHours()).padStart(2, '0');
    const minutes = String(localDate.getUTCMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

const calculateDaylightPercentage = (sunrise, sunset, utcOffsetSeconds) => {
    // Get the current time in the required format
    const nowString = getTimeInISOFormat(utcOffsetSeconds);
    const now = new Date(`${nowString}:00`);

    // Parse sunrise and sunset times
    const sunriseTime = new Date(`${sunrise}:00`);
    const sunsetTime = new Date(`${sunset}:00`);


    if (now < sunriseTime) {
        return 0;
    }
    if (now > sunsetTime) {
        return 100;
    }

    // Calculate daylight percentages
    const totalDaylight = sunsetTime - sunriseTime;
    const timeSinceSunrise = now - sunriseTime;


    const percentage = (timeSinceSunrise / totalDaylight) * 100;

    return percentage;
};

setTimeout(() =>{
const percentageOfDaylight = Math.round(
  calculateDaylightPercentage(sunrise, sunset, utcOffsetSeconds)
);

  if (percentageOfDaylight > 1 && percentageOfDaylight <= 10) {
    moveSun(10);
  } else if (percentageOfDaylight > 10 && percentageOfDaylight <= 20) {
    moveSun(20);
  } else if (percentageOfDaylight > 20 && percentageOfDaylight <= 30) {
    moveSun(30);
  } else if (percentageOfDaylight > 30 && percentageOfDaylight <= 40) {
    moveSun(40);
  } else if (percentageOfDaylight > 40 && percentageOfDaylight <= 50) {
    moveSun(50);
  } else if (percentageOfDaylight > 50 && percentageOfDaylight <= 60) {
    moveSun(60);
  } else if (percentageOfDaylight > 60 && percentageOfDaylight <= 70) {
    moveSun(70);
  } else if (percentageOfDaylight > 70 && percentageOfDaylight <= 80) {
    moveSun(80);
  } else if (percentageOfDaylight > 80 && percentageOfDaylight <= 90) {
    moveSun(90);
  } else if (percentageOfDaylight > 90 && percentageOfDaylight <= 100) {
    moveSun(100);
  }
  document.getElementById("day_value").value = (
    percentageOfDaylight / 100
  ).toFixed(2);

if(percentageOfDaylight < 100){
  document.getElementById('hourglass_top_icon_length').innerHTML = 'clear_day'
} else{
  document.getElementById('hourglass_top_icon_length').innerHTML = 'bedtime'
}
}, 500);

  const temperatureCLoths = Math.round(data.temperature_2m);

  function getClothingRecommendation(temp) {
    if (temp <= 0) {
      return "❄️ Freezing temperatures! Wear a heavy coat, gloves, a hat, and a scarf to stay warm.";
    } else if (temp <= 5) {
      return "🧥 Very cold! Wear a thick coat, a hat, and gloves to keep warm.";
    } else if (temp <= 10) {
      return "🧣 Cold weather. A coat and a sweater will keep you comfortable.";
    } else if (temp <= 15) {
      return "🧥 Cool temperatures. Wear a light jacket and long sleeves.";
    } else if (temp <= 20) {
      return "🌤️ Mild weather. A light jacket or a sweater should be enough.";
    } else if (temp <= 25) {
      return "👕 Pleasantly warm. A t-shirt and jeans or pants are suitable.";
    } else if (temp <= 30) {
      return "☀️ Hot! Opt for a t-shirt and light pants or shorts.";
    } else if (temp <= 35) {
      return "🌞 Very hot. Wear light, breathable clothing and stay hydrated.";
    } else if (temp <= 40) {
      return "🔥 Extreme heat! Wear very light clothing, stay hydrated, and avoid direct sun exposure.";
    } else if (temp <= 45) {
      return "⚠️ Dangerously hot! Wear minimal clothing, stay indoors if possible, and drink plenty of water.";
    } else {
      return "🚨 Extreme heat alert! Wear minimal clothing, stay indoors, and drink plenty of water to stay safe.";
    }
  }

  //    const recommendation = getClothingRecommendation(temperatureCLoths)

  //    document.getElementById('cloth_recommended').textContent = getTranslationByLang(localStorage.getItem('AppLanguageCode'), recommendation)
}

// air quality

function AirQuaility(data) {
  const aqi = data.current.us_aqi;
  const aqiEU = data.current.european_aqi || 'N/A';


  let aqiCategory;
  let percentage = 0;
  let percentageColor = ''

  if (localStorage.getItem('selectedAQItype') === 'eu_aqi') {
    if (aqiEU <= 25) {
      aqiCategory = 1;
      percentage = Math.min(5 + ((aqiEU / 25) * 16), 10);
      percentageColor = '#00e400'
    } else if (aqiEU <= 50) {
      aqiCategory = 2;
      percentage = Math.min(20 + (((aqiEU - 25) / 25) * 16), 42);
      percentageColor = '#ffff00'
    } else if (aqiEU <= 75) {
      aqiCategory = 3;
      percentage = Math.min(40 + (((aqiEU - 50) / 50) * 16), 50);
      percentageColor = '#ff7e00'

    } else if (aqiEU <= 100) {
      aqiCategory = 4;
      percentage = Math.min(58 + (((aqiEU - 75) / 50) * 16), 74);
      percentageColor = '#ff0000'
    } else {
      aqiCategory = 5;
      percentage = Math.min(74 + (((aqiEU - 100) / 100) * 16), 95);
      percentageColor = '#8f3f97'
    }
    document.getElementById("aqi-level-value").innerHTML = aqiEU;
  } else {
    if (aqi <= 50) {
      aqiCategory = 1;
      percentage = Math.min(5 + ((aqi / 50) * 16), 10);
      percentageColor = '#00e400';
    } else if (aqi <= 100) {
      aqiCategory = 2;
      percentage = Math.min(20 + (((aqi - 50) / 50) * 16), 42);
      percentageColor = '#ffff00';
    } else if (aqi <= 150) {
      aqiCategory = 3;
      percentage = Math.min(42 + (((aqi - 100) / 50) * 16), 50);
      percentageColor = '#ff0000';
    } else if (aqi <= 200) {
      aqiCategory = 4;
      percentage = Math.min(58 + (((aqi - 150) / 50) * 16), 74);
      percentageColor = '#ff0000';
    } else {
      aqiCategory = 5;
      percentage = Math.min(74 + (((aqi - 200) / 100) * 16), 95);
      percentageColor = '#8f3f97';
    }
    document.getElementById("aqi-level-value").innerHTML = aqi;
  }


  percentage = Math.min(95, Math.max(5, percentage.toFixed(2)));
document.querySelector('air_quality_bar_progress').style = `--percentageColor: ${percentageColor}; width: ${percentage}%;`


  document.getElementById("pm25_air").innerHTML = Math.round(
    data.current.pm2_5
  );
  document.getElementById("pm25_air_color").style.backgroundColor = getColor(
    Math.round(data.current.pm2_5),
    "PM2.5"
  );

  document.getElementById("pm10_air").innerHTML = Math.round(data.current.pm10);
  document.getElementById("pm10_air_color").style.backgroundColor = getColor(
    Math.round(data.current.pm10),
    "PM10"
  );

  document.getElementById("CO_air").innerHTML =
  (data.current.carbon_monoxide * 0.000799).toFixed(2);

  document.getElementById("CO_air_color").style.backgroundColor = getColor(
    (data.current.carbon_monoxide * 0.000799).toFixed(2),
    "CO"
  );

  document.getElementById("NO2_air").innerHTML = Math.round(
    data.current.nitrogen_dioxide
  );
  document.getElementById("NO2_air_color").style.backgroundColor = getColor(
    Math.round(data.current.nitrogen_dioxide),
    "NO2"
  );

  document.getElementById("SO2_air").innerHTML = Math.round(
    data.current.sulphur_dioxide
  );
  document.getElementById("SO2_air_color").style.backgroundColor = getColor(
    Math.round(data.current.sulphur_dioxide),
    "SO2"
  );

  document.getElementById("O3_air").innerHTML = Math.round(data.current.ozone);
  document.getElementById("O3_air_color").style.backgroundColor = getColor(
    Math.round(data.current.ozone),
    "O3"
  );

  const aqiData = aqiText[aqiCategory];

  const langCode = localStorage.getItem("AppLanguageCode");

  const levelTranslation = getTranslationByLang(langCode, aqiData.level);
  const messageTranslation = getTranslationByLang(langCode, aqiData.message);

  document.getElementById("aqi-level").textContent = levelTranslation;
  document.getElementById("detail_air").textContent = messageTranslation;

  const backgroundColor = {
    1: "#43b710",
    2: "#eaaf10",
    3: "#eb8a11",
    4: "#e83f0f",
    5: "#8e3acf",
  };

  document.getElementById("aqi-level").style.backgroundColor =
    backgroundColor[aqiCategory];

  const alder_pollen = data.current.alder_pollen;
  const birch_pollen = data.current.birch_pollen;
  const grass_pollen = data.current.grass_pollen;
  const mugwort_pollen = data.current.mugwort_pollen;
  const olive_pollen = data.current.olive_pollen;
  const ragweed_pollen = data.current.ragweed_pollen;

  function getPollenLevel(pollenCount) {
    if (pollenCount < 20) {
      return { fraction: "1/4", level: getTranslationByLang(localStorage.getItem("AppLanguageCode"), "low_pollen"), icon: WidgetsPollen.LowPollen };
    } else if (pollenCount < 50) {
      return {
        fraction: "2/4",
        level: getTranslationByLang(localStorage.getItem("AppLanguageCode"), "medium_pollen"),
        icon: WidgetsPollen.MediumPollen,
      };
    } else if (pollenCount < 100) {
      return { fraction: "3/4", level: getTranslationByLang(localStorage.getItem("AppLanguageCode"), "high_pollen"), icon: WidgetsPollen.HighPollen };
    } else {
      return {
        fraction: "4/4",
        level: getTranslationByLang(localStorage.getItem("AppLanguageCode"), "severe_pollen"),
        icon: WidgetsPollen.SeverePollen,
      };
    }
  }

  function isPollenDataAvailable(...pollenValues) {
    return pollenValues.every((value) => value !== null && value !== undefined);
  }

  if (isPollenDataAvailable(alder_pollen, birch_pollen, olive_pollen)) {
    const treePollen = alder_pollen + birch_pollen + olive_pollen;
    const treePollenLevel = getPollenLevel(treePollen);
    document.getElementById(
      "pollen_number_tree"
    ).innerHTML = `${treePollenLevel.fraction}`;
    document.getElementById(
      "pollen_data-text_tree"
    ).innerHTML = `${treePollenLevel.level}`;
    document.querySelector(
      ".Pollen_Icon_slot_tree"
    ).innerHTML = `${treePollenLevel.icon}`;
    document.querySelector(".pollen_data").hidden = false;
    document
      .getElementById("ifPollenIsThere")
      .classList.add("available_pollen");
  } else {
    document.querySelector(".pollen_data").hidden = true;
    document
      .getElementById("ifPollenIsThere")
      .classList.remove("available_pollen");
  }

  if (isPollenDataAvailable(grass_pollen)) {
    const grassPollenLevel = getPollenLevel(grass_pollen);
    document.getElementById(
      "pollen_number_grass"
    ).innerHTML = `${grassPollenLevel.fraction}`;
    document.getElementById(
      "pollen_data-text_grass"
    ).innerHTML = `${grassPollenLevel.level}`;
    document.querySelector(
      ".Pollen_Icon_slot_grass"
    ).innerHTML = `${grassPollenLevel.icon}`;
    document.querySelector(".pollen_data").hidden = false;
    document
      .getElementById("ifPollenIsThere")
      .classList.add("available_pollen");
  } else {
    document.querySelector(".pollen_data").hidden = true;
    document
      .getElementById("ifPollenIsThere")
      .classList.remove("available_pollen");
  }

  if (isPollenDataAvailable(mugwort_pollen, ragweed_pollen)) {
    const weedPollen = mugwort_pollen + ragweed_pollen;
    const weedPollenLevel = getPollenLevel(weedPollen);
    document.getElementById(
      "pollen_number_weed"
    ).innerHTML = `${weedPollenLevel.fraction}`;
    document.getElementById(
      "pollen_data-text_weed"
    ).innerHTML = `${weedPollenLevel.level}`;
    document.querySelector(
      ".Pollen_Icon_slot_weed"
    ).innerHTML = `${weedPollenLevel.icon}`;
    document.querySelector(".pollen_data").hidden = false;
    document
      .getElementById("ifPollenIsThere")
      .classList.add("available_pollen");
  } else {
    document.querySelector(".pollen_data").hidden = true;
    document
      .getElementById("ifPollenIsThere")
      .classList.remove("available_pollen");
  }
}

function getColor(value, type) {
  switch (type) {
    case "CO":
      if (value <= 4.4) return "#20fc03";
      if (value <= 9.0) return "yellow";
      if (value <= 15.0) return "orange";
      if (value <= 30.0) return "#fc606d";
      if (value <= 45.0) return "#9000ff";
      return "maroon";
    case "NH3":
      if (value <= 5) return "#20fc03";
      if (value <= 15) return "yellow";
      if (value <= 25) return "orange";
      if (value <= 35) return "#fc606d";
      if (value <= 50) return "#9000ff";
      return "maroon";
    case "CO":
      if (value <= 4.5) return "#20fc03";
      if (value <= 9.5) return "yellow";
      if (value <= 12.5) return "orange";
      if (value <= 15.5) return "#fc606d";
      if (value <= 30.5) return "#9000ff";
      if (value <= 50.5) return "maroon";
      return "maroon";
    case "NO2":
      if (value <= 40) return "#20fc03";
      if (value <= 100) return "yellow";
      if (value <= 200) return "orange";
      if (value <= 300) return "#fc606d";
      if (value <= 500) return "#9000ff";
      return "maroon";
    case "O3":
      if (value <= 100) return "#20fc03";
      if (value <= 180) return "yellow";
      if (value <= 300) return "orange";
      if (value <= 400) return "#fc606d";
      if (value <= 500) return "#9000ff";
      return "maroon";
    case "PM2.5":
      if (value <= 12) return "#20fc03";
      if (value <= 35) return "yellow";
      if (value <= 55) return "orange";
      if (value <= 150) return "#fc606d";
      if (value <= 250) return "#9000ff";
      return "maroon";
    case "PM10":
      if (value <= 20) return "#20fc03";
      if (value <= 50) return "yellow";
      if (value <= 100) return "orange";
      if (value <= 150) return "#fc606d";
      if (value <= 250) return "#9000ff";
      return "maroon";
    case "SO2":
      if (value <= 50) return "#20fc03";
      if (value <= 150) return "yellow";
      if (value <= 250) return "orange";
      if (value <= 500) return "#fc606d";
      if (value <= 1000) return "#9000ff";
      return "maroon";
    default:
      return "white";
  }
}

// uv index

function UvIndex(uvIndexValue) {
  const uvIndex = Math.round(uvIndexValue);

let uvPercentageProg = 10;
let uvColor = "#00e400";


    if (uvIndex === 0) {
      uvColor = "#00e400";
      uvPercentageProg = 8;
    } else if (uvIndex === 1) {
      uvColor = "#00e400";
      uvPercentageProg = 8;
    } else if (uvIndex === 2) {
      uvColor = "#00e400";
      uvPercentageProg = 8;
    } else if (uvIndex === 3) {
      uvColor = "#00e400";
      uvPercentageProg = 8;
    } else if (uvIndex === 4) {
      uvColor = "#00e400";
      uvPercentageProg = 8;
    } else if (uvIndex === 5) {
      uvColor = "#ffff00";
      uvPercentageProg = 30;
    } else if (uvIndex === 6) {
      uvColor = "#ffff00";
      uvPercentageProg = 30;
    } else if (uvIndex === 7) {
      uvColor = "#ff7e00";
      uvPercentageProg = 45;
    } else if (uvIndex === 8) {
      uvColor = "#ff0000";
      uvPercentageProg = 50;
    } else if (uvIndex === 9) {
      uvColor = "#ff0000";
      uvPercentageProg = 70;
    } else if (uvIndex === 10) {
      uvColor = "#ff0000";
      uvPercentageProg = 70;
    } else if (uvIndex === 11) {
      uvColor = "#ff0000";
      uvPercentageProg = 70;
    } else if (uvIndex === 12) {
      uvColor = "#8f3f97";
      uvPercentageProg = 80;
    } else if (uvIndex === 13) {
      uvColor = "#8f3f97";
      uvPercentageProg = 80;
    } else if (uvIndex >= 14) {
      uvColor = "#7e0023";
      uvPercentageProg = 95;
    }




document.querySelector('uv_index_bar_progress').style = `--percentageColorUV: ${uvColor}; width: ${uvPercentageProg}%;`



  if (uvIndex >= 0 && uvIndex <= 1) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "minimal_risk"
    );
    document.getElementById("uv-index").style = "background-color: #43b710";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "uv_index_satisfactory"
    );
    localStorage.setItem("CurrentUVIndexMain", "0");
    document.getElementById("uv-level-value").innerHTML = '0';
  } else if (uvIndex > 1 && uvIndex <= 2) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "low_risk"
    );
    document.getElementById("uv-index").style = "background-color: #43b710";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "conditions_low_risk"
    );
    localStorage.setItem("CurrentUVIndexMain", "1");
    document.getElementById("uv-level-value").innerHTML = '1';
  } else if (uvIndex > 2 && uvIndex <= 3) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "low_risk"
    );
    document.getElementById("uv-index").style = "background-color: #43b710";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "low_exposure_level"
    );
    localStorage.setItem("CurrentUVIndexMain", "2");
    document.getElementById("uv-level-value").innerHTML = '2';
  } else if (uvIndex > 3 && uvIndex <= 4) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "low_risk"
    );
    document.getElementById("uv-index").style = "background-color: #43b710";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "low_exposure_level"
    );
    localStorage.setItem("CurrentUVIndexMain", "3");
    document.getElementById("uv-level-value").innerHTML = '3';
  } else if (uvIndex > 4 && uvIndex <= 5) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "moderate_risk"
    );
    document.getElementById("uv-index").style = "background-color: #eaaf10";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "moderate_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "4");
    document.getElementById("uv-level-value").innerHTML = '4';
  } else if (uvIndex > 5 && uvIndex <= 6) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "moderate_risk"
    );
    document.getElementById("uv-index").style = "background-color: #eaaf10";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "moderate_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "5");
    document.getElementById("uv-level-value").innerHTML = '5';
  } else if (uvIndex > 6 && uvIndex <= 7) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "high_risk"
    );
    document.getElementById("uv-index").style = "background-color: #eb8a11";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "high_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "6");
    document.getElementById("uv-level-value").innerHTML = '6';
  } else if (uvIndex > 7 && uvIndex <= 8) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "high_risk"
    );
    document.getElementById("uv-index").style = "background-color: #eb8a11";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "high_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "7");
    document.getElementById("uv-level-value").innerHTML = '7';
  } else if (uvIndex > 8 && uvIndex <= 9) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_high_risk"
    );
    document.getElementById("uv-index").style = "background-color: #e83f0f";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_high_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "8");
    document.getElementById("uv-level-value").innerHTML = '8';
  } else if (uvIndex > 9 && uvIndex <= 10) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_high_risk"
    );
    document.getElementById("uv-index").style = "background-color: #e83f0f";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_high_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "9");
    document.getElementById("uv-level-value").innerHTML = '9';
  } else if (uvIndex > 10 && uvIndex <= 11) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_high_risk"
    );
    document.getElementById("uv-index").style = "background-color: #e83f0f";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_high_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "10");
    document.getElementById("uv-level-value").innerHTML = '10';
  } else if (uvIndex > 11 && uvIndex <= 12) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_risk"
    );
    document.getElementById("uv-index").style = "background-color: #8e3acf";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "11");
    document.getElementById("uv-level-value").innerHTML = '11';
  } else if (uvIndex > 12 && uvIndex <= 13) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_risk"
    );
    document.getElementById("uv-index").style = "background-color: #ec0c8b";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "12");
    document.getElementById("uv-level-value").innerHTML = '12';
  } else if (uvIndex > 13) {
    document.getElementById("uv-index").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_risk"
    );
    document.getElementById("uv-index").style = "background-color: #550ef9";
    document.getElementById("detail_uv").innerHTML = getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_risk_sun_exposure"
    );
    localStorage.setItem("CurrentUVIndexMain", "13+");
    document.getElementById("uv-level-value").innerHTML = '13+';
  }
}


let debounceMoreDetails

function MoreDetailsRender(data) {
  let local

  if (
    localStorage.getItem("AppLanguageCode") === '' ||
    localStorage.getItem("AppLanguageCode") === 'nl' || // Dutch
    localStorage.getItem("AppLanguageCode") === 'fr' || // French
    localStorage.getItem("AppLanguageCode") === 'de' || // German
    localStorage.getItem("AppLanguageCode") === 'el' || // Greek
    localStorage.getItem("AppLanguageCode") === 'it' || // Italian
    localStorage.getItem("AppLanguageCode") === 'fa' || // Persian
    localStorage.getItem("AppLanguageCode") === 'pl' || // Polish
    localStorage.getItem("AppLanguageCode") === 'pt' || // Portuguese
    localStorage.getItem("AppLanguageCode") === 'ro' || // Romanian
    localStorage.getItem("AppLanguageCode") === 'ru' || // Russian
    localStorage.getItem("AppLanguageCode") === 'es' || // Spanish
    localStorage.getItem("AppLanguageCode") === 'tr' || // Turkish
    localStorage.getItem("AppLanguageCode") === 'uk' || // Ukrainian
    localStorage.getItem("AppLanguageCode") === 'sr' || // Serbian
    localStorage.getItem("AppLanguageCode") === 'az' || // Azerbaijani
    localStorage.getItem("AppLanguageCode") === 'sl' || // Slovenian
    localStorage.getItem("AppLanguageCode") === 'fi' || // Finnish
    localStorage.getItem("AppLanguageCode") === 'hu' ||  // Hungarian
    localStorage.getItem("AppLanguageCode") === 'cs'    // Czech
  ) {
    local = ','
  } else{
    local = '.'

  }

  clearTimeout(debounceMoreDetails)
  debounceMoreDetails = setTimeout(() =>{
  const mainData = data.forecast.forecastday[0].day;

  const weatherCondition = mainData.condition.text;
  const precipitation = mainData.totalprecip_in;
  const humidity = mainData.avghumidity;

  let willRain;

  if (mainData.daily_will_it_rain > 0) {
    willRain =
      "There is a chance of rain today! So, stay prepared just in case!";
  } else {
    willRain =
      "No rain is expected today. It’s going to be a delightful day ahead! Enjoy! 😊";
  }

  let maxTemp;

  if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
    maxTemp = Math.round((mainData.maxtemp_c * 9) / 5 + 32);
  } else {
    maxTemp = Math.round(mainData.maxtemp_c);
  }

  let minTemp;

  if (localStorage.getItem("SelectedTempUnit") === "fahrenheit") {
    minTemp = Math.round((mainData.mintemp_c * 9) / 5 + 32);
  } else {
    minTemp = Math.round(mainData.mintemp_c);
  }

  let Precipitation;

  if (localStorage.getItem("selectedPrecipitationUnit") === "in") {
  if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
    Precipitation = mainData.totalprecip_in.toFixed(2).replace('.', local) + " 英寸";
  } else{
    Precipitation = mainData.totalprecip_in.toFixed(2).replace('.', local) + " in";
  }
  } else if (localStorage.getItem("selectedPrecipitationUnit") === "cm") {
    if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
      Precipitation = (mainData.totalprecip_in * 2.54).toFixed(2).replace('.', local) + " 厘米";
    } else{
    Precipitation = (mainData.totalprecip_in * 2.54).toFixed(2).replace('.', local) + " cm";
    }
  } else {
  if(localStorage.getItem('AppLanguage') === 'Chinese (Simplified)'){
    Precipitation = inchesToMm(mainData.totalprecip_in).toFixed(2).replace('.', local) + " 英寸";
  } else{
    Precipitation = inchesToMm(mainData.totalprecip_in).toFixed(2).replace('.', local) + " mm";
    }
  }
  let precipitationMessage;
  if (mainData.totalprecip_in > 0) {
    precipitationMessage = `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "rain_report_tipPart_1"
    )} ${Precipitation} ${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "rain_report_tipPart_2"
    )} `;
  } else {
    precipitationMessage = `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "Norain_report_tipPart"
    )}`;
  }

      const globalTempMax = summaryData[0].tempMax
      const globalTempMin = summaryData[0].tempMin
      const globalUVmax = summaryData[0].uvIndexMax.toFixed(2).replace('.', local)

  let weatherReport = `
         <li style="padding-bottom: 5px;">${getTranslationByLang(
           localStorage.getItem("AppLanguageCode"),
           "temp_report_tipPart_1"
         )} ${globalTempMax}° ${getTranslationByLang(
    localStorage.getItem("AppLanguageCode"),
    "temp_report_tipPart_2"
  )} ${globalUVmax} ${getTranslationByLang(
    localStorage.getItem("AppLanguageCode"),
    "temp_report_tipPart_3"
  )} ${globalTempMin}° ${getTranslationByLang(
    localStorage.getItem("AppLanguageCode"),
    "temp_report_tipPart_4"
  )} </li>
         <li >${precipitationMessage}</li>


        `;
  let weatherTips = "";

  const veryHotWeatherTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_hot_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_hot_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_hot_weather_tips_3"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_hot_weather_tips_4"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_hot_weather_tips_5"
    )}`,
  ];

  const hotWeatherTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "hot_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "hot_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "hot_weather_tips_3"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "hot_weather_tips_4"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "hot_weather_tips_5"
    )}`,
  ];

  const mildWeatherTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "mild_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "mild_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "mild_weather_tips_3"
    )}`,
  ];

  const chillyWeatherTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "chilly_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "chilly_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "chilly_weather_tips_3"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "chilly_weather_tips_4"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "chilly_weather_tips_5"
    )}`,
  ];

  const veryColdWeatherTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_cold_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_cold_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_cold_weather_tips_3"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "very_cold_weather_tips_4"
    )}`,
  ];

  const extremeColdWeatherTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_cold_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_cold_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_cold_weather_tips_3"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_cold_weather_tips_4"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "extreme_cold_weather_tips_5"
    )}`,
  ];

  const rainTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "rain_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "rain_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "rain_weather_tips_3"
    )}`,
  ];

  const sunnyTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "sunny_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "sunny_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "sunny_weather_tips_3"
    )}`,
  ];

  const snowTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "snow_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "snow_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "snow_weather_tips_3"
    )}`,
  ];

  const cloudyWeatherTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cloudy_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cloudy_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cloudy_weather_tips_3"
    )}`,
  ];

  const fogTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "fog_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "fog_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "fog_weather_tips_3"
    )}`,
  ];

  const windTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "wind_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "wind_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "wind_weather_tips_3"
    )}`,
  ];

  const thunderstormTips = [
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "thunder_weather_tips_1"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "thunder_weather_tips_2"
    )}`,
    `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "thunder_weather_tips_3"
    )}`,
  ];

  if (Math.round(mainData.maxtemp_c) > 35) {
    weatherTips +=
      veryHotWeatherTips[
        Math.floor(Math.random() * veryHotWeatherTips.length)
      ] + " ";
  } else if (Math.round(mainData.maxtemp_c) > 29) {
    weatherTips +=
      hotWeatherTips[Math.floor(Math.random() * hotWeatherTips.length)] + " ";
  } else if (
    Math.round(mainData.maxtemp_c) >= 19 &&
    Math.round(mainData.maxtemp_c) <= 29
  ) {
    weatherTips +=
      mildWeatherTips[Math.floor(Math.random() * mildWeatherTips.length)] + " ";
  } else if (Math.round(mainData.maxtemp_c) < -10) {
    weatherTips +=
      extremeColdWeatherTips[
        Math.floor(Math.random() * extremeColdWeatherTips.length)
      ] + " ";
  } else if (Math.round(mainData.maxtemp_c) < 0) {
    weatherTips +=
      veryColdWeatherTips[
        Math.floor(Math.random() * veryColdWeatherTips.length)
      ] + " ";
  } else if (Math.round(mainData.maxtemp_c) < 19) {
    weatherTips +=
      chillyWeatherTips[Math.floor(Math.random() * chillyWeatherTips.length)] +
      " ";
  }

  if (precipitation > 0) {
    weatherTips += rainTips[Math.floor(Math.random() * rainTips.length)] + " ";
  }

  if (weatherCondition.toLowerCase().includes("rain")) {
    weatherTips += `${getTranslationByLang(
      localStorage.getItem("AppLanguageCode"),
      "cautious_slippery_roads"
    )} `;
  } else if (weatherCondition.toLowerCase().includes("sunny")) {
    weatherTips +=
      sunnyTips[Math.floor(Math.random() * sunnyTips.length)] + " ";
  } else if (weatherCondition.toLowerCase().includes("snow")) {
    weatherTips += snowTips[Math.floor(Math.random() * snowTips.length)] + " ";
  } else if (
    weatherCondition.toLowerCase().includes("cloudy") ||
    weatherCondition.toLowerCase().includes("cloud") ||
    weatherCondition.toLowerCase().includes("overcast")
  ) {
    weatherTips +=
      cloudyWeatherTips[Math.floor(Math.random() * cloudyWeatherTips.length)] +
      " ";
  } else if (weatherCondition.toLowerCase().includes("fog")) {
    weatherTips += fogTips[Math.floor(Math.random() * fogTips.length)] + " ";
  } else if (weatherCondition.toLowerCase().includes("wind")) {
    weatherTips += windTips[Math.floor(Math.random() * windTips.length)] + " ";
  } else if (weatherCondition.toLowerCase().includes("thunder")) {
    weatherTips +=
      thunderstormTips[Math.floor(Math.random() * thunderstormTips.length)] +
      " ";
  }

  document.getElementById("day_tips").innerHTML = `<li>${weatherTips}</li>`;
  document.getElementById("summeryDay").innerHTML = `<li>${weatherReport}</li>`;
  }, 300);
}

function astronomyDataRender(data) {
  if (data) {
    const MoonPhaseName = data.astronomy.astro.moon_phase;
    const moonillumination = Math.round(data.astronomy.astro.moon_illumination);

    if (MoonPhaseName.includes("New Moon")) {
      document.querySelector("moonPhaseProgress").style.right =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_new_moon');
    } else if (MoonPhaseName.includes("Waxing Crescent")) {
      document.querySelector("moonPhaseProgress").style.right =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_waxing_crescent');
    } else if (MoonPhaseName.includes("First Quarter")) {
      document.querySelector("moonPhaseProgress").style.right =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_first_quarter');
      document.querySelector("moonPhaseProgress").style.borderRadius = "0%";
    } else if (MoonPhaseName.includes("Waxing Gibbous")) {
      document.querySelector("moonPhaseProgress").style.right =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_waxing_gibbous');
      document.querySelector("moonPhaseProgress").style.borderRadius = "";
    } else if (MoonPhaseName.includes("Full Moon")) {
      document.querySelector("moonPhaseProgress").style.left =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_full_moon');
      document.querySelector("moonPhaseProgress").style.borderRadius = "";
    } else if (MoonPhaseName.includes("Waning Gibbous")) {
      document.querySelector("moonPhaseProgress").style.left =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_waning_gibbous');
      document.querySelector("moonPhaseProgress").style.borderRadius = "";
    } else if (MoonPhaseName.includes("Last Quarter")) {
      document.querySelector("moonPhaseProgress").style.left =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_last_quarter');
      document.querySelector("moonPhaseProgress").style.borderRadius = "0%";
    } else if (MoonPhaseName.includes("Waning Crescent")) {
      document.querySelector("moonPhaseProgress").style.left =
        moonillumination + "%";
        document.getElementById("moonPhase_name").innerHTML = getTranslationByLang(localStorage.getItem('AppLanguageCode'), 'phase_waning_crescent');
      document.querySelector("moonPhaseProgress").style.borderRadius = "";
    }

    document.getElementById("moonIlli").innerHTML = moonillumination + "%";


    function convertTo24Hour(time) {
      const [timePart, modifier] = time.split(" ");
      let [hours, minutes] = timePart.split(":");

      if (hours === "12") {
        hours = "00";
      }

      if (modifier === "PM") {
        hours = parseInt(hours, 10) + 12;
      }

      return `${hours}:${minutes}`;
    }

    if (localStorage.getItem("selectedTimeMode") === "24 hour") {
      document.getElementById("moonriseTime").innerHTML = convertTo24Hour(
        data.astronomy.astro.moonrise
      );
      document.getElementById("moonSetTime").innerHTML = convertTo24Hour(
        data.astronomy.astro.moonset
      );
    } else {
      document.getElementById("moonriseTime").innerHTML =
        data.astronomy.astro.moonrise;
      document.getElementById("moonSetTime").innerHTML =
        data.astronomy.astro.moonset;
    }
  }
setTimeout(() =>{

  function convertToISOFormat(timeStr) {
    const today = new Date();
    const [time, modifier] = timeStr.split(' ');
    let [hours, minutes] = time.split(':').map(Number);

    if (modifier === 'PM' && hours !== 12) {
        hours += 12;
    } else if (modifier === 'AM' && hours === 12) {
        hours = 0;
    }

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}T${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
}

function calculateMoonVisibilityPercentage(moonrise, moonset, utcOffsetSeconds) {
    const nowString = getTimeInISOFormat(utcOffsetSeconds);
    const now = new Date(`${nowString}:00`);

    const moonriseISO = convertToISOFormat(moonrise);
    const moonsetISO = convertToISOFormat(moonset);

    const moonriseTime = new Date(`${moonriseISO}:00`);
    const moonsetTime = new Date(`${moonsetISO}:00`);

    if (moonsetTime < moonriseTime) {
      moonsetTime.setDate(moonsetTime.getDate() + 1);
    }

    if (now < moonriseTime) {
        return 0;
    }
    if (now > moonsetTime) {
        return 100;
    }

    const totalNighttime = moonsetTime - moonriseTime;
    const timeSinceMoonrise = now - moonriseTime;

    const percentage = (timeSinceMoonrise / totalNighttime) * 100;

    return percentage;
}

function getTimeInISOFormat(utcOffsetSeconds) {
    const currentDate = new Date();
    const localDate = new Date(currentDate.getTime() + utcOffsetSeconds * 1000);

    const year = localDate.getUTCFullYear();
    const month = String(localDate.getUTCMonth() + 1).padStart(2, '0');
    const day = String(localDate.getUTCDate()).padStart(2, '0');
    const hours = String(localDate.getUTCHours()).padStart(2, '0');
    const minutes = String(localDate.getUTCMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

const moonrise = data.astronomy.astro.moonrise
const moonset =  data.astronomy.astro.moonset


const percentageOfMoonVisibility = Math.round(
    calculateMoonVisibilityPercentage(moonrise, moonset, utcOffsetSeconds)
);

if (percentageOfMoonVisibility > 1 && percentageOfMoonVisibility <= 10) {
    moveMoon(10);
} else if (percentageOfMoonVisibility > 10 && percentageOfMoonVisibility <= 20) {
    moveMoon(20);
} else if (percentageOfMoonVisibility > 20 && percentageOfMoonVisibility <= 30) {
    moveMoon(30);
} else if (percentageOfMoonVisibility > 30 && percentageOfMoonVisibility <= 40) {
    moveMoon(40);
} else if (percentageOfMoonVisibility > 40 && percentageOfMoonVisibility <= 50) {
    moveMoon(50);
} else if (percentageOfMoonVisibility > 50 && percentageOfMoonVisibility <= 60) {
    moveMoon(60);
} else if (percentageOfMoonVisibility > 60 && percentageOfMoonVisibility <= 70) {
    moveMoon(70);
} else if (percentageOfMoonVisibility > 70 && percentageOfMoonVisibility <= 80) {
    moveMoon(80);
} else if (percentageOfMoonVisibility > 80 && percentageOfMoonVisibility <= 90) {
    moveMoon(90);
} else if (percentageOfMoonVisibility > 90 && percentageOfMoonVisibility <= 100) {
    moveMoon(100);
}
}, 500);

}

function FetchAlertRender(data) {
  if (localStorage.getItem("useWeatherAlerts") === "false") {
    document.querySelector(".excessiveHeat").hidden = true;
  } else {
    if (data.alerts.alert && data.alerts.alert.length > 0) {
      document.querySelector(".excessiveHeat").hidden = false;
      localStorage.setItem("AlertCache", JSON.stringify(data.alerts.alert));
    } else {
      console.log("No alerts");
      document.querySelector(".excessiveHeat").hidden = true;
      localStorage.removeItem("AlertCache", JSON.stringify(data.alerts.alert));
    }
  }
}

function clickForecastItem(index) {
  localStorage.setItem("ClickedForecastItem", index);
}

const AppLanguageCodeValue = localStorage.getItem("AppLanguageCode");
if (AppLanguageCodeValue) {
  applyTranslations(AppLanguageCodeValue);
}

// ---------

function createTempTrendsChart() {
  const cachedCurrentDataAvg = JSON.parse(
    localStorage.getItem("DailyWeatherCache")
  );
  const tempTrendHolder = document.querySelector(".temp_trend_bars");

  tempTrendHolder.innerHTML = "";

  if (!cachedCurrentDataAvg || !cachedCurrentDataAvg.time) {
    console.error("Weather data is not available in the cache.");
    return;
  }

  const labels = cachedCurrentDataAvg.time;

  let Unit;

  let minTemps = cachedCurrentDataAvg.temperature_2m_min;
  let maxTemps = cachedCurrentDataAvg.temperature_2m_max;

  if (SelectedTempUnit === "fahrenheit") {
    minTemps = minTemps.map((temp) => Math.round(celsiusToFahrenheit(temp)));
    maxTemps = maxTemps.map((temp) => Math.round(celsiusToFahrenheit(temp)));
    Unit = "°F";
  } else {
    minTemps = minTemps.map((temp) => Math.round(temp));
    maxTemps = maxTemps.map((temp) => Math.round(temp));
    Unit = "°C";
  }

  const avgTemps = minTemps.map((min, index) => (min + maxTemps[index]) / 2);

  tempTrendHolder.innerHTML = "";
  const canvas = document.createElement("canvas");
  canvas.id = "tempTrendsChart";
  tempTrendHolder.appendChild(canvas);

  new Chart(canvas, {
    type: "line",
    data: {
      labels: labels,
      datasets: [
        {
          label: "Min",
          data: minTemps,
          borderColor: "blue",
          backgroundColor: "rgba(0, 0, 255, 0.1)",
          fill: true,
          tension: 0.3,
        },
        {
          label: "Max",
          data: maxTemps,
          borderColor: "red",
          backgroundColor: "rgba(255, 0, 0, 0.1)",
          fill: true,
          tension: 0.3,
        },
        {
          label: "Avg",
          data: avgTemps,
          borderColor: "green",
          backgroundColor: "rgba(0, 255, 0, 0.1)",
          fill: false,
          tension: 0.3,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          mode: "index",
          intersect: false,
        },
      },
      scales: {
        x: {
          display: false,
        },
        y: {
          title: {
            display: true,
            text: `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "temperature")} (${Unit})`,
          },
          beginAtZero: true,
        },
      },
    },
  });
}

// bar charts

function createTempTrendsChartBar() {
  const cachedCurrentDataAvg = JSON.parse(
    localStorage.getItem("DailyWeatherCache")
  );
  const tempTrendHolder = document.querySelector(".temp_trend_bars");

  tempTrendHolder.innerHTML = "";

  if (!cachedCurrentDataAvg || !cachedCurrentDataAvg.time) {
    console.error("Weather data is not available in the cache.");
    return;
  }

  const labels = cachedCurrentDataAvg.time;

  let Unit;

  let minTemps = cachedCurrentDataAvg.temperature_2m_min;
  let maxTemps = cachedCurrentDataAvg.temperature_2m_max;

  if (SelectedTempUnit === "fahrenheit") {
    minTemps = minTemps.map((temp) => Math.round(celsiusToFahrenheit(temp)));
    maxTemps = maxTemps.map((temp) => Math.round(celsiusToFahrenheit(temp)));
    Unit = "°F";
  } else {
    minTemps = minTemps.map((temp) => Math.round(temp));
    maxTemps = maxTemps.map((temp) => Math.round(temp));
    Unit = "°C";
  }

  const avgTemps = minTemps.map((min, index) => (min + maxTemps[index]) / 2);

  tempTrendHolder.innerHTML = "";
  const canvas = document.createElement("canvas");
  canvas.id = "tempTrendsChart";
  tempTrendHolder.appendChild(canvas);

  new Chart(canvas, {
    type: "bar",
    data: {
      labels: labels,
      datasets: [
        {
          label: "Min",
          data: minTemps,
          borderColor: "blue",
          backgroundColor: "rgba(0, 0, 255, 0.1)",
          borderRadius: 50,
          borderWidth: 1,
          fill: true,
        },
        {
          label: "Max",
          data: maxTemps,
          borderColor: "red",
          backgroundColor: "rgba(255, 0, 0, 0.1)",
          borderRadius: 50,
          borderWidth: 1,
          fill: true,
        },
        {
          label: "Avg",
          data: avgTemps,
          borderColor: "green",
          backgroundColor: "rgba(0, 255, 0, 0.1)",
          borderRadius: 50,
          borderWidth: 1,
          fill: true,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          mode: "index",
          intersect: false,
        },
      },
      scales: {
        x: {
          display: false,
        },
        y: {
          title: {
            display: true,
            text: `${getTranslationByLang(localStorage.getItem("AppLanguageCode"), "temperature")} (${Unit})`,
          },
          beginAtZero: true,
        },
      },
    },
  });
}
