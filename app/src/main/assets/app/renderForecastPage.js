const SelectedTempUnit = localStorage.getItem('SelectedTempUnit');
const SelectedWindUnit = localStorage.getItem('SelectedWindUnit');
const SelectedPrecipitationUnit = localStorage.getItem('selectedPrecipitationUnit');
const SelectedPressureUnit = localStorage.getItem('selectedPressureUnit');
const cachedData = JSON.parse(localStorage.getItem('DailyWeatherCache'));
const cachedDataHourly = JSON.parse(localStorage.getItem('HourlyWeatherCache'));
const timeFormat = localStorage.getItem('selectedTimeMode');


// icons

function GetWeatherIcon(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {
            return '../weather-icons/clear_day.svg';
        } else if (iconCode === 1) {
            return '../weather-icons/mostly_clear_day.svg';
        } else if (iconCode === 2) {
            return '../weather-icons/partly_cloudy_day.svg';

        } else if (iconCode === 3) {
            return '../weather-icons/cloudy.svg';

        } else if (iconCode === 45 || iconCode === 48) {
            return '../weather-icons/haze_fog_dust_smoke.svg';

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {

            return '../weather-icons/drizzle.svg';

        } else if (iconCode === 56 || iconCode === 57) {

            return '../weather-icons/mixed_rain_hail_sleet.svg';

        } else if (iconCode === 61 || iconCode === 63) {

            return '../weather-icons/showers_rain.svg';

        } else if (iconCode === 65) {

            return '../weather-icons/heavy_rain.svg';

        } else if (iconCode === 66 || iconCode === 67) {

            return '../weather-icons/sleet_hail.svg';

        } else if (iconCode === 71) {

            return '../weather-icons/scattered_snow_showers_day.svg';

        } else if (iconCode === 73) {

            return '../weather-icons/showers_snow.svg';

        } else if (iconCode === 75) {

            return '../weather-icons/heavy_snow.svg';

        } else if (iconCode === 77) {

            return '../weather-icons/flurries.svg';

        } else if (iconCode === 80 || iconCode === 81) {

            return '../weather-icons/showers_rain.svg';

        } else if (iconCode === 82) {

            return '../weather-icons/heavy_rain.svg';

        } else if (iconCode === 85) {

            return '../weather-icons/showers_snow.svg';

        } else if (iconCode === 86) {

            return '../weather-icons/heavy_snow.svg';

        } else if (iconCode === 95) {

            return '../weather-icons/isolated_thunderstorms.svg'

        } else if (iconCode === 96 || iconCode === 99) {

            return '../weather-icons/strong_thunderstorms.svg'

        }
    }

    return iconCode
}

// weather label

function GetWeatherLabel(iconCode, isDay) {


    if (isDay === 1) {
        if (iconCode === 0) {
            return 'clear_sky'

        } else if (iconCode === 1) {
            return 'mostly_clear'

        } else if (iconCode === 2) {
            return 'partly_cloudy'

        } else if (iconCode === 3) {
            return 'overcast'

        } else if (iconCode === 45 || iconCode === 48) {
            return 'fog'

        } else if (iconCode === 51 || iconCode === 53 || iconCode === 55) {
            return 'drizzle'

        } else if (iconCode === 56 || iconCode === 57) {
            return 'freezing_drizzle'

        } else if (iconCode === 61 || iconCode === 63) {
            return 'moderate_rain'

        } else if (iconCode === 65) {
            return 'heavy_intensity_rain'

        } else if (iconCode === 66 || iconCode === 67) {
            return 'freezing_rain'

        } else if (iconCode === 71) {
            return 'slight_snow'

        } else if (iconCode === 73) {
            return 'moderate_snow'

        } else if (iconCode === 75) {
            return 'heavy_intensity_snow'

        } else if (iconCode === 77) {
            return 'snow_grains'

        } else if (iconCode === 80 || iconCode === 81) {
            return 'rain_showers'

        } else if (iconCode === 82) {
            return 'heavy_rain_showers'

        } else if (iconCode === 85) {
            return 'slight_snow_showers'

        } else if (iconCode === 86) {
            return 'heavy_snow_showers'

        } else if (iconCode === 95) {
            return 'thunderstorm'

        } else if (iconCode === 96 || iconCode === 99) {
            return 'strong_thunderstorm'

        }
    }
}


function getWeatherLabelInLang(iconCode, isDay, langCode) {
    const translationKey = GetWeatherLabel(iconCode, isDay);

    const translatedLabel = getTranslationByLang(langCode, translationKey);

    return translatedLabel || 'Unknown weather';
}



// change ids same

const ConditionIcons = {

    WindSockIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <symbol id="Winda" viewBox="0 0 342 234"> <!-- blow-1 --> <path d="M264.16,21.29A40,40,0,1,1,293,89H9" fill="none" stroke-dasharray="148" stroke="#e2e8f0" stroke-linecap="round" stroke-miterlimit="10" stroke-width="18"> <animate attributeName="stroke-dashoffset" values="0; 2960" dur="6s" repeatCount="indefinite"/> </path> <!-- blow-2 --> <path d="M148.16,212.71A40,40,0,1,0,177,145H9" fill="none" stroke-dasharray="110" stroke="#e2e8f0" stroke-linecap="round" stroke-miterlimit="10" stroke-width="18"> <animate attributeName="stroke-dashoffset" values="0; 1540" dur="6s" repeatCount="indefinite"/> </path> </symbol> </defs> <use width="342" height="234" transform="translate(85 139)" xlink:href="#Winda"/> </svg>',

    PrecipitationAmountIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <linearGradient id="PrecipitationAmountIcona" x1="310.54" y1="152.47" x2="425.46" y2="351.53" gradientUnits="userSpaceOnUse"> <stop offset="0" stop-color="#d4d7dd"/> <stop offset="0.45" stop-color="#d4d7dd"/> <stop offset="1" stop-color="#bec1c6"/> </linearGradient> <clipPath id="PrecipitationAmountIconb"> <path fill="none"> <animate attributeName="d" values=" M168,252H344V380H168Z; M168,220H376V380H168Z; M168,252H344V380H168Z " dur="3s" calcMode="spline" keySplines=".42, 0, .58, 1; .42, 0, .58, 1" repeatCount="indefinite"/> </path> </clipPath> <symbol id="PrecipitationAmountIconc" viewBox="0 0 175 260.88"> <path d="M87.5,13.38c-48.7,72-80,117-80,160.75s35.79,79.25,80,79.25,80-35.47,80-79.25S136.2,85.35,87.5,13.38Z" fill="none" stroke="#2885c7" stroke-miterlimit="10" stroke-width="15"/> </symbol> </defs> <path d="M256,132c-48.7,72-80,117-80,160.75S211.79,372,256,372s80-35.47,80-79.25S304.7,204,256,132Z" fill="none" stroke="#e2e8f0" stroke-miterlimit="10" stroke-width="15"/> <path d="M352,132h32V372H352m8-120h24m-16,56h16M368,188h16" fill="none" stroke-linecap="round" stroke-linejoin="round" stroke-width="6" stroke="url(#PrecipitationAmountIcona)"/> <g clip-path="url(#PrecipitationAmountIconb)"> <use width="175" height="260.88" transform="translate(168.61 119.2)" xlink:href="#PrecipitationAmountIconc"/> </g> </svg>',

    PrecipitationChancesIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <clipPath id="PrecipitationChancesIcona"> <path d="M302,137c-48.7,72-80,117-80,160.75S257.79,377,302,377V512H0V0H302Z" fill="none"> <animateTransform attributeName="transform" additive="sum" type="scale" values="1 1; 1 .95; 1 1" dur="6s" calcMode="spline" keySplines=".42, 0, .58, 1; .42, 0, .58, 1" repeatCount="indefinite"/> </path> </clipPath> <symbol id="PrecipitationChancesIconb" viewBox="0 0 175 260.88"> <path d="M87.5,13.38c-48.7,72-80,117-80,160.75s35.79,79.25,80,79.25,80-35.47,80-79.25S136.2,85.35,87.5,13.38Z" fill="none" stroke="#2885c7" stroke-miterlimit="10" stroke-width="15"/> </symbol> </defs> <use width="175" height="260.88" transform="translate(214.5 123.62)" xlink:href="#PrecipitationChancesIconb"> <animateTransform attributeName="transform" additive="sum" type="scale" values="1 1; 1 .9; 1 1" dur="6s" calcMode="spline" keySplines=".42, 0, .58, 1; .42, 0, .58, 1" repeatCount="indefinite"/> </use> <g clip-path="url(#PrecipitationChancesIcona)"> <use width="175" height="260.88" transform="translate(122.5 123.62)" xlink:href="#PrecipitationChancesIconb"> <animateTransform attributeName="transform" additive="sum" type="scale" values="1 .9; 1 1; 1 .9" dur="6s" calcMode="spline" keySplines=".42, 0, .58, 1; .42, 0, .58, 1" repeatCount="indefinite"/> </use> </g> </svg>',

    PressureIcon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"> <!-- barometer --> <circle cx="256" cy="256" r="144" fill="none" stroke="#475569" stroke-miterlimit="10" stroke-width="12"/> <!-- values --> <path d="M256,200V152M364,256H316m-116,0H152m180-68-24,24m-104,0-24-24M308,300l24,24m-152,0,24-24" fill="none" stroke="#475569" stroke-linecap="round" stroke-linejoin="round" stroke-width="6"/> <!-- pointer-mount --> <circle cx="256" cy="256" r="24" fill="#ef4444"/> <!-- pointer --> <line x1="256" y1="284" x2="256" y2="164" fill="none" stroke="#ef4444" stroke-linecap="round" stroke-miterlimit="10" stroke-width="12"> <animateTransform attributeName="transform" dur="6s" values="-54 256 256; -15 256 256; -36 256 256; 36 256 256; 10 256 256; 115 256 256; -54 256 256" repeatCount="indefinite" type="rotate" calcMode="spline" keySplines=".42, 0, .58, 1; .42, 0, .58, 1; .42, 0, .58, 1; .42, 0, .58, 1; .42, 0, .58, 1; .42, 0, .58, 1" keyTimes="0; .17; .25; .42; .5; .67; 1"/> </line> </svg>',

    CloudCoverIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <symbol id="a" viewBox="0 0 359 231"> <path d="M295.5,223.5a56,56,0,0,0,0-112c-.85,0-1.68.09-2.53.13A83.9,83.9,0,0,0,140.1,47.42,55.91,55.91,0,0,0,55.5,95.5a56.56,56.56,0,0,0,.8,9.08A60,60,0,0,0,67.5,223.5" fill="none" stroke="#e2e8f0" stroke-linecap="round" stroke-linejoin="round" stroke-width="15"/> </symbol> </defs> <use width="359" height="231" transform="translate(76.5 140.5)" xlink:href="#a"> <animateTransform attributeName="transform" additive="sum" type="translate" values="-18 0; 18 0; -18 0" dur="6s" repeatCount="indefinite"/> </use> </svg>',

    HumidityIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <symbol id="HumidityIcona" viewBox="0 0 175 260.88"> <path d="M87.5,13.38c-48.7,72-80,117-80,160.75s35.79,79.25,80,79.25,80-35.47,80-79.25S136.2,85.35,87.5,13.38Z" fill="none" stroke="#2885c7" stroke-miterlimit="10" stroke-width="15"> <animateTransform attributeName="transform" additive="sum" type="scale" values="1 1; 1 .9; 1 1" dur="6s" calcMode="spline" keySplines=".42, 0, .58, 1; .42, 0, .58, 1" repeatCount="indefinite"/> </path> </symbol> </defs> <use width="175" height="260.88" transform="translate(168.4 123.18)" xlink:href="#HumidityIcona"/> <path d="M218.78,250.47q4.78-4.47,13.69-4.47t13.68,4.47q4.78,4.47,4.79,12.4v8q0,7.8-4.79,12.22t-13.68,4.41q-8.9,0-13.69-4.41T214,270.91v-8Q214,254.95,218.78,250.47ZM290,248.94a2.79,2.79,0,0,1-.55,2.61l-53,73.24a9.43,9.43,0,0,1-2.84,2.83,12.29,12.29,0,0,1-4.62.56h-4.34c-1.33,0-2.16-.37-2.5-1.13a2.76,2.76,0,0,1,.61-2.72l53-73.35a7,7,0,0,1,2.67-2.66,12.7,12.7,0,0,1,4.34-.51h4.89C288.91,247.81,289.69,248.18,290,248.94Zm-57.52,7.59q-7.68,0-7.68,6.9v6.79q0,6.9,7.68,6.91t7.68-6.91v-6.79Q240.15,256.53,232.47,256.53Zm33.38,36.39q4.78-4.47,13.69-4.47t13.68,4.47q4.78,4.47,4.78,12.4v8q0,7.81-4.78,12.23T279.54,330q-8.91,0-13.69-4.42t-4.79-12.23v-8Q261.06,297.39,265.85,292.92ZM279.54,299q-7.68,0-7.69,6.92v6.67q0,7,7.69,7t7.67-7v-6.67Q287.21,299,279.54,299Z" fill="var(--On-Surface)"/> </svg>',

    DewPointIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <clipPath id="a"> <path d="M333.92,392c-30.93,0-56-25.45-56-56.84a57,57,0,0,1,24-46.6V152.46a32.23,32.23,0,0,1,32-32.47H167.4V392Zm4-192h28m-28-32h28m-28,64h28" fill="none"/> </clipPath> <symbol id="b" viewBox="0 0 175 260.88"> <path d="M87.5,13.38c-48.7,72-80,117-80,160.75s35.79,79.25,80,79.25,80-35.47,80-79.25S136.2,85.35,87.5,13.38Z" fill="none" stroke="#2885c7" stroke-miterlimit="10" stroke-width="15"/> </symbol> <symbol id="d" viewBox="0 0 72 168"> <circle cx="36" cy="132" r="36" fill="#ef4444"/> <path d="M36,12V132" fill="none" stroke="#ef4444" stroke-linecap="round" stroke-miterlimit="10" stroke-width="24"> <animateTransform attributeName="transform" type="translate" values="0 0; 0 18; 0 0" dur="1s" calcMode="spline" keySplines=".42, 0, .58, 1; .42, 0, .58, 1" repeatCount="indefinite"/> </path> </symbol> <symbol id="e" viewBox="0 0 118 278"> <path d="M115,218.16C115,249.55,89.93,275,59,275S3,249.55,3,218.16a57,57,0,0,1,24-46.6V35.48a32,32,0,1,1,64,0V171.56A57,57,0,0,1,115,218.16ZM63,83H91M63,51H91M63,115H91" fill="none" stroke="#cbd5e1" stroke-linecap="round" stroke-linejoin="round" stroke-width="6"/> </symbol> <symbol id="c" viewBox="0 0 118 278"> <use width="72" height="168" transform="translate(23 87)" xlink:href="#d"/> <use width="118" height="278" xlink:href="#e"/> </symbol> </defs> <g clip-path="url(#a)"> <use width="175" height="260.88" transform="translate(168.43 123.18)" xlink:href="#b"/> </g> <use width="118" height="278" transform="translate(275 117)" xlink:href="#c"/> </svg>',

    SunriseIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <clipPath id="Sunrisea"> <path d="M512,306H304l-35.86-31.38a18.44,18.44,0,0,0-24.28,0L208,306H0V0H512Z" fill="none"/> </clipPath> <symbol id="Sunriseb" viewBox="0 0 375 375"> <!-- sun-core --> <circle cx="187.5" cy="187.5" r="84" fill="none" stroke="#fbbf24" stroke-miterlimit="10" stroke-width="15"/> <!-- sun-rays --> <path d="M187.5,57.16V7.5m0,360V317.84M279.67,95.33l35.11-35.11M60.22,314.78l35.11-35.11m0-184.34L60.22,60.22M314.78,314.78l-35.11-35.11M57.16,187.5H7.5m360,0H317.84" fill="none" stroke="#fbbf24" stroke-linecap="round" stroke-miterlimit="10" stroke-width="15"> <animateTransform attributeName="transform" additive="sum" type="rotate" values="0 187.5 187.5; 45 187.5 187.5" dur="6s" repeatCount="indefinite"/> </path> </symbol> </defs> <g clip-path="url(#Sunrisea)"> <use width="375" height="375" transform="translate(68.5 104.5)" xlink:href="#Sunriseb"/> </g> <polyline points="128 332 216 332 256 296 296 332 384 332" fill="none" stroke="var(--On-Surface-Variant)" stroke-linecap="round" stroke-linejoin="round" stroke-width="18"/> </svg>',

    SunsetIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <clipPath id="Sunseta"> <path d="M512,306H296.08a21.5,21.5,0,0,0-14.16,5.32L256,334l-25.92-22.68A21.5,21.5,0,0,0,215.92,306H0V0H512Z" fill="none"/> </clipPath> <symbol id="Sunsetb" viewBox="0 0 375 375"> <!-- sun-core --> <circle cx="187.5" cy="187.5" r="84" fill="none" stroke="#fbbf24" stroke-miterlimit="10" stroke-width="15"/> <!-- sun-rays --> <path d="M187.5,57.16V7.5m0,360V317.84M279.67,95.33l35.11-35.11M60.22,314.78l35.11-35.11m0-184.34L60.22,60.22M314.78,314.78l-35.11-35.11M57.16,187.5H7.5m360,0H317.84" fill="none" stroke="#fbbf24" stroke-linecap="round" stroke-miterlimit="10" stroke-width="15"> <animateTransform attributeName="transform" additive="sum" type="rotate" values="0 187.5 187.5; 45 187.5 187.5" dur="6s" repeatCount="indefinite"/> </path> </symbol> </defs> <g clip-path="url(#Sunseta)"> <use width="375" height="375" transform="translate(68.5 104.5)" xlink:href="#Sunsetb"/> </g> <polyline points="128 332 216 332 256 368 296 332 384 332" fill="none" stroke="var(--On-Surface-Variant)" stroke-linecap="round" stroke-linejoin="round" stroke-width="18"/> </svg>',

    UVindexIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <clipPath id="UVindexa"> <path d="M64,64H448V256H328a72,72,0,0,0-72,72V448H64Z" fill="none"/> </clipPath> <symbol id="UVindexb" viewBox="0 0 375 375"> <!-- sun-core --> <circle cx="187.5" cy="187.5" r="84" fill="none" stroke="#fbbf24" stroke-miterlimit="10" stroke-width="15"/> <!-- sun-rays --> <path d="M187.5,57.16V7.5m0,360V317.84M279.67,95.33l35.11-35.11M60.22,314.78l35.11-35.11m0-184.34L60.22,60.22M314.78,314.78l-35.11-35.11M57.16,187.5H7.5m360,0H317.84" fill="none" stroke="#fbbf24" stroke-linecap="round" stroke-miterlimit="10" stroke-width="15"> <animateTransform attributeName="transform" additive="sum" type="rotate" values="0 192 192; 45 192 192" dur="6s" repeatCount="indefinite"/> </path> </symbol> </defs> <g clip-path="url(#UVindexa)"> <use width="375" height="375" transform="translate(68.5 68.5)" xlink:href="#UVindexb"/> <path d="M254,338V328a74,74,0,0,1,74-74h10" fill="none" stroke="#fbbf24" stroke-miterlimit="10" stroke-width="15"/> </g> <path d="M337.71,388q-14.4,0-22.14-6.76t-7.74-19.39V316h18.32v45.94q0,11.07,11.65,11.07t11.56-11.07V316h18.32v45.85q0,12.73-7.74,19.44T337.71,388Z" fill="var(--On-Surface)"/> <path d="M413.43,344.51,421.56,316h18.61l-22.53,69.75H398.25L375.71,316h18.81l8.23,28.41,5.39,21.94Z" fill="var(--On-Surface)"/> </svg>',

    MoonRiseIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <clipPath id="Moonrisea"> <path d="M512,306H304l-35.86-31.38a18.44,18.44,0,0,0-24.28,0L208,306H0V0H512Z" fill="none"/> </clipPath> <symbol id="Moonriseb" viewBox="0 0 279 279"> <!-- moon --> <path d="M256.75,173.13c-74.12,0-134.21-59.28-134.21-132.42A130.48,130.48,0,0,1,127,7.5C59.79,14.75,7.5,70.87,7.5,139.08c0,73.13,60.09,132.42,134.21,132.42,62.48,0,114.83-42.18,129.79-99.21A135.56,135.56,0,0,1,256.75,173.13Z" fill="none" stroke="#72b9d5" stroke-linecap="round" stroke-linejoin="round" stroke-width="15"> <animateTransform attributeName="transform" additive="sum" type="rotate" values="-15 135 135; 9 135 135; -15 135 135" dur="6s" repeatCount="indefinite"/> </path> </symbol> </defs> <polyline points="128 332 216 332 256 296 296 332 384 332" fill="none" stroke="var(--On-Surface-Variant)" stroke-linecap="round" stroke-linejoin="round" stroke-width="18"/> <g clip-path="url(#Moonrisea)"> <use width="279" height="279" transform="translate(116.5 116.5)" xlink:href="#Moonriseb"/> </g> </svg>',

    MoonSetIcon: '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512"> <defs> <clipPath id="Moonseta"> <path d="M512,306H296.08a21.5,21.5,0,0,0-14.16,5.32L256,334l-25.92-22.68A21.5,21.5,0,0,0,215.92,306H0V0H512Z" fill="none"/> </clipPath> <symbol id="Moonsetb" viewBox="0 0 279 279" overflow="visible"> <!-- moon --> <path d="M256.75,173.13c-74.12,0-134.21-59.28-134.21-132.42A130.48,130.48,0,0,1,127,7.5C59.79,14.75,7.5,70.87,7.5,139.08c0,73.13,60.09,132.42,134.21,132.42,62.48,0,114.83-42.18,129.79-99.21A135.56,135.56,0,0,1,256.75,173.13Z" fill="none" stroke="#72b9d5" stroke-linecap="round" stroke-linejoin="round" stroke-width="15"> <animateTransform attributeName="transform" additive="sum" type="rotate" values="-15 135 135; 9 135 135; -15 135 135" dur="6s" repeatCount="indefinite"/> </path> </symbol> </defs> <polyline points="128 332 216 332 256 368 296 332 384 332" fill="none" stroke="var(--On-Surface-Variant)" stroke-linecap="round" stroke-linejoin="round" stroke-width="18"/> <g clip-path="url(#Moonseta)"> <use width="279" height="279" transform="translate(116.5 116.5)" xlink:href="#Moonsetb"/> </g> </svg>',
}







function getDailyForecast() {
    if (cachedDataHourly && cachedDataHourly.time && cachedDataHourly.time.length > 0) {
        const dailyForecast = aggregateHourlyData(cachedDataHourly);
        displayDailyForecast(cachedData, dailyForecast);
    } else {
        console.error('Hourly data is not available.');
    }
}

function aggregateHourlyData(hourlyData) {
    const dailyData = {};
    hourlyData.time.forEach((time, index) => {
        const date = new Date(time).toISOString().split('T')[0];

        if (hourlyData.temperature_2m && hourlyData.temperature_2m[index] !== undefined) {
            if (!dailyData[date]) {
                dailyData[date] = {
                    humidity: 0,
                    cloudCover: 0,
                    dewPoint: 0,
                    pressure: 0,
                    count: 0
                };
            }

        }
        const dayData = dailyData[date];
        dayData.humidity += hourlyData.relative_humidity_2m[index];
        dayData.cloudCover += hourlyData.cloud_cover[index];
        dayData.dewPoint += hourlyData.dew_point_2m[index];
        dayData.pressure += hourlyData.pressure_msl[index];
        dayData.count++;
    });


    for (const date in dailyData) {
        dailyData[date].humidity = Math.round(dailyData[date].humidity / dailyData[date].count);
        dailyData[date].cloudCover = Math.round(dailyData[date].cloudCover / dailyData[date].count);
        dailyData[date].dewPoint = Math.round(dailyData[date].dewPoint / dailyData[date].count);
        dailyData[date].pressure = Math.round(dailyData[date].pressure / dailyData[date].count);
    }

    return dailyData;
}


function displayDailyForecast(forecast, forecastDaily) {
    const forecastContainer = document.getElementById('foreCastList');
    const forecastDateHeader = document.createElement('forecastDateHeader');
    const forecastMainDetails = document.createElement('forecastMainDetails');

    forecastContainer.innerHTML = '';


    const today = new Date();
    const todayString = today.toISOString().split('T')[0];
    const sortedDates = Object.keys(forecastDaily)
        .filter(date => date >= todayString)
        .sort();


    if (sortedDates.length === 0) {
        forecastContainer.innerHTML = '<p>No upcoming forecast data available.</p>';
        return;
    }


    sortedDates.forEach((date, index) => {




        const dateObj = new Date(date + 'T00:00:00');

        const isToday = date === todayString;
        const weekday = isToday ? 'today' : dateObj.toLocaleDateString('en-US', { weekday: 'short' }).toLowerCase();


        const weekdayLang = getTranslationByLang(localStorage.getItem('AppLanguageCode'), weekday);


        const dailyData = forecastDaily[date];

        const formattedDate = new Intl.DateTimeFormat('en-US', { day: 'numeric', month: 'short' }).format(dateObj);

        const rainPercentage = Math.round(forecast.precipitation_probability_max[index]) || '0';

        const convertTo12Hour = (time) => {
            const date = new Date(time);
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true });
        };

        const convertTo24Hour = (time) => {
            const date = new Date(time);
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
        };

        let SunriseTime
        let SunsetTime

        if (timeFormat === '24 hour') {
            SunriseTime = convertTo24Hour(forecast.sunrise[index])
            SunsetTime = convertTo24Hour(forecast.sunset[index])
        } else {
            SunriseTime = convertTo12Hour(forecast.sunrise[index])
            SunsetTime = convertTo12Hour(forecast.sunset[index])
        }
        const DailyWeatherCode = forecast.weather_code[index];

        const cloudCover = dailyData.cloudCover

        const humidity = dailyData.humidity;


        let WindSpeed;

        if (SelectedWindUnit === 'mile') {
            WindSpeed = Math.round(forecast.wind_speed_10m_max[index] / 1.60934) + ' mph';
        } else if (SelectedWindUnit === 'M/s') {
            WindSpeed = (forecast.wind_speed_10m_max[index] / 3.6).toFixed(2) + ' m/s';
        } else {
            WindSpeed = Math.round(forecast.wind_speed_10m_max[index]) + ' km/h';
        }


        let TemperatureMax;

        if (SelectedTempUnit === 'fahrenheit') {
            TemperatureMax = Math.round(forecast.temperature_2m_max[index] * 9 / 5 + 32);
        } else {
            TemperatureMax = Math.round(forecast.temperature_2m_max[index]);
        }

        let TemperatureMin;

        if (SelectedTempUnit === 'fahrenheit') {
            TemperatureMin = Math.round(forecast.temperature_2m_min[index] * 9 / 5 + 32);
        } else {
            TemperatureMin = Math.round(forecast.temperature_2m_min[index]);
        }

        let dewPointTemp;


        if (SelectedTempUnit === 'fahrenheit') {
            dewPointTemp = Math.round(dailyData.dewPoint * 9 / 5 + 32);
        } else {
            dewPointTemp = Math.round(dailyData.dewPoint);
        }

        let rainAmount;

        if (SelectedPrecipitationUnit === 'in') {
            rainAmount = forecast.precipitation_sum[index] ? (forecast.precipitation_sum[index] * 0.0393701).toFixed(2) + 'in' : '0.00in';

        } else {
            rainAmount = forecast.precipitation_sum[index] ? forecast.precipitation_sum[index] + ' mm' : '0.0mm';
        }


        let pressureMain;
        let pressureMainUnit;

        if (SelectedPressureUnit === 'inHg') {
            pressureMain = (dailyData.pressure * 0.02953).toFixed(2);
            pressureMainUnit = 'inHg';
        } else if (SelectedPressureUnit === 'mmHg') {
            pressureMain = (dailyData.pressure * 0.750062).toFixed(2);
            pressureMainUnit = 'mmHg';
        } else {
            pressureMain = dailyData.pressure;
            pressureMainUnit = 'hPa';
        }

        const uvIndex = Math.round(forecast.uv_index_max[index])

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




        const forecastDateHeaderContent = document.createElement('div');
        forecastDateHeaderContent.classList.add('forecastDateHeaderContent')
        forecastDateHeaderContent.dataset.index = index;


        forecastDateHeaderContent.innerHTML = `

            <span>${weekdayLang}</span>
            <img src="${GetWeatherIcon(DailyWeatherCode, 1)}">
            <div>
            <p>${TemperatureMax}°<span>/${TemperatureMin}°</span></p>
            </div>

            <md-ripple style="--md-ripple-pressed-opacity: 0.1;"></md-ripple>

        `
        // ------------------------------------------------------





        forecastDateHeader.appendChild(forecastDateHeaderContent);
        forecastContainer.appendChild(forecastDateHeader)
        forecastContainer.appendChild(forecastMainDetails)



        function handleSelection(event) {
            document.querySelectorAll('.forecastDateHeaderContent').forEach(item => {
                item.classList.remove('selected');
            });

            event.currentTarget.classList.add('selected');

            forecastMainDetails.innerHTML = '';


            const forecastTempConditionMainContent = document.createElement('div');
            forecastTempConditionMainContent.classList.add('forecastTempConditionMainContent')

            const selectedIndex = event.currentTarget.dataset.index;
            const selectedForecast = forecast[selectedIndex];

            forecastTempConditionMainContent.innerHTML = `

            <div class="top-details">
                <p>${weekdayLang}, ${formattedDate}</p>
                <div>
                <tempLarge><p>${TemperatureMax}° </p> <span>/${TemperatureMin}°</span></tempLarge>
                <img src="${GetWeatherIcon(DailyWeatherCode, 1)}">
                </div>
                <weatherConditionText>${getWeatherLabelInLang(DailyWeatherCode, 1, localStorage.getItem('AppLanguageCode'))}</weatherConditionText>
            </div>


            <p class="daily-conditions-title" data-translate="daily_conditions">Daily conditions</p>
            <div class="daily-conditions">
            <div class="daily-conditions-wrap">

                <div>
                    <p data-translate="wind_speed">Wind speed</p>
                    <conditionIcon>
                    ${ConditionIcons.WindSockIcon}
                    </conditionIcon>
                    <span>${WindSpeed}</span>
                </div>


                <div>
                    <p data-translate="humidity">Humidity</p>
                    <conditionIcon>
                    ${ConditionIcons.HumidityIcon}
                    </conditionIcon>
                    <span>${humidity}%</span>
                </div>


                <div>
                <p data-translate="precipitation_chances">Precipitation chances</p>
                <conditionIcon>
                ${ConditionIcons.PrecipitationChancesIcon}
                </conditionIcon>
                <span>${rainPercentage}%</span>
                </div>

                <div>
                <p data-translate="precipitation_amount">Precipitation amount</p>
                <conditionIcon>
                ${ConditionIcons.PrecipitationAmountIcon}
                </conditionIcon>
                <span>${rainAmount}</span>
                </div>

                <div>
                <p data-translate="cloudiness">Cloud cover</p>
                <conditionIcon>
                ${ConditionIcons.CloudCoverIcon}
                </conditionIcon>
                <span>${cloudCover}%</span>
                </div>

                <div>
               <p data-translate="pressure">Pressure</p>
                <conditionIcon>
                ${ConditionIcons.PressureIcon}
                </conditionIcon>
                <span>${pressureMain} ${pressureMainUnit}</span>
                </div>

                <div>
                <p data-translate="dew_point">Dew point</p>
                <conditionIcon>
                ${ConditionIcons.DewPointIcon}
                </conditionIcon>
                <span>${dewPointTemp}°</span>
                </div>

                <div>
                <p data-translate="uv_index">UV index</p>
                <conditionIcon>
                ${ConditionIcons.UVindexIcon}
                </conditionIcon>
                <span style="align-items: center; gap: 5px;">${uvIndex} <text style="color: var(--On-Surface-Variant);font-size: 13px;"> ${UVindexText}</text></span>
                </div>

                </div>
                </div>

                 <p class="daily-conditions-title" style="margin-top:10px;"><span data-translate="sunrise">Sunrise</span> & <span data-translate="sunset">sunset</span></p>
                <div class="sunrise-sunset-forecast">

                <div class="sunrise-sunset-item">
                <p data-translate="sunrise">Sunrise</p>
                <div class="sunrise-sunset-img">
                ${ConditionIcons.SunriseIcon}
                </div>
                <span>${SunriseTime}</span>
                </div>

                <div class="sunrise-sunset-item">
                <p data-translate="sunset">Sunset</p>
                <div class="sunrise-sunset-img">
                ${ConditionIcons.SunsetIcon}
                </div>
                <span>${SunsetTime}</span>
                </div>

                </div>

            `

            if (!forecastMainDetails.contains(forecastTempConditionMainContent)) {
                forecastMainDetails.appendChild(forecastTempConditionMainContent);
            }

            const AppLanguageCodeValue = localStorage.getItem('AppLanguageCode');
            if (AppLanguageCodeValue) {
                applyTranslations(AppLanguageCodeValue);

            }


        }


        forecastDateHeaderContent.addEventListener('click', handleSelection);
        const clickedForecastItem = localStorage.getItem('ClickedForecastItem') || '0';
        const selectedForecastIndex = parseInt(clickedForecastItem, 10);


        if (selectedForecastIndex >= 0 && selectedForecastIndex <= 13 || index === 0) {
            if (index === selectedForecastIndex || index === 0) {
                forecastDateHeaderContent.classList.add('selected');
                handleSelection({ currentTarget: forecastDateHeaderContent });
                firstForecastIndex = index;

                setTimeout(() => {
                    forecastDateHeaderContent.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

                    setTimeout(() => {
                        if (clickedForecastItem >= 4) {
                            const forecastDateHeader = document.querySelector('forecastDateHeader');
                            forecastDateHeader.scrollBy({
                                top: 0,
                                left: 50,
                                behavior: 'smooth'
                            });
                        }

                    }, 600);
                }, 500);
            }
        }
    });
}



setTimeout(() => {
    getDailyForecast()
}, 1500);


function convertTo12HourFormat(unixTimestamp) {
    const date = new Date(unixTimestamp * 1000);
    let hours = date.getHours();
    const minutes = date.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    const minutesStr = minutes < 10 ? '0' + minutes : minutes;
    return `${hours}:${minutesStr} ${ampm}`;
}


setTimeout(() => {
    document.querySelector('.loader_holder').style.opacity = '0'
}, 2000);


setTimeout(() => {
    document.querySelector('.loader_holder').hidden = true
}, 2300);







