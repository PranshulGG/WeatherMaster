function sendThemeToAndroid(status, nav, flag, flagAnimAndroid) {
    AndroidInterface.updateStatusBarColor(status, nav, flag, flagAnimAndroid);
};


let Themeflag = '1'

function checkThemeFlag(){
    if(localStorage.getItem('useLightTheme') && localStorage.getItem('useLightTheme') === 'true'){
        Themeflag = '1'
    } else{
        Themeflag = '0'
    } 
}

checkThemeFlag()

// -----------------

function navigateActivity(name) {
    OpenActivityInterface.OpenActivity(name);
};

// ----------

function ActivityBack(){
    BackActivityInterface.CloseActivity();
}

// dialog colors


const colorsDialogsOpenSurface = {
    clear_night_pageDialog : "",
    clear_night_pageDialog_dark : "#010209",
    clear_day_pageDialog : "",
    clear_day_pageDialog_dark : "#00060a",
    overcast_pageDialog: "",
    overcast_pageDialog_dark: "#070708",
    cloudy_pageDialog: "",
    cloudy_pageDialog_dark: "#05060a",
    rain_pageDialog: "",
    rain_pageDialog_dark: "#07080a",
    thunder_pageDialog: "",
    thunder_pageDialog_dark: "#08010a",
    fog_pageDialog: "",
    fog_pageDialog_dark: "#0a0501",
    snow_pageDialog: "",
    snow_pageDialog_dark: "#000505"
}

const colorsDialogsOpenContainer = {
    clear_night_pageDialog : "",
    clear_night_pageDialog_dark : "#080911",
    clear_day_pageDialog : "",
    clear_day_pageDialog_dark : "#00080e",
    overcast_pageDialog: "",
    overcast_pageDialog_dark: "#0a0a0a",
    cloudy_pageDialog: "",
    cloudy_pageDialog_dark: "#070a0d",
    rain_pageDialog: "",
    rain_pageDialog_dark: "#0a0a0c",
    thunder_pageDialog: "",
    thunder_pageDialog_dark: "#0c080c",
    fog_pageDialog: "",
    fog_pageDialog_dark: "#0c0805",
    snow_pageDialog: "",
    snow_pageDialog_dark: "#080808"
}



// get colors

function GetDialogOverlayContainerColor() {
    const theme = document.documentElement.getAttribute('Theme');
    if (!theme) return null; 

    if (localStorage.getItem('useLightTheme') === 'true') {
        return theme + 'Dialog';
    } else {
        return theme + 'Dialog_dark';
    }
}


