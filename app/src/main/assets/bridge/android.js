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
    blue_materialDialog : "#646466",
    blue_materialDialog_dark : "#07080a",
    purple_materialDialog : "#666364",
    purple_materialDialog_dark : "#090709",
    orange_materialDialog: "#666362",
    orange_materialDialog_dark: "#0a0705",
    red_materialDialog: "#666362",
    red_materialDialog_dark: "#0a0706",
    pink_materialDialog: "#666363",
    pink_materialDialog_dark: "#0a0708",
    green_materialDialog: "#646460",
    green_materialDialog_dark: "#070806",
    yellow_materialDialog: "#66645f",
    yellow_materialDialog_dark: "#080804",
    mono_materialDialog: "#656363",
    mono_materialDialog_dark: "#080808"
}

const colorsDialogsOpenContainer = {
    blue_materialDialog : "#5f5f62",
    blue_materialDialog_dark : "#0c0d0e",
    purple_materialDialog : "#635e60",
    purple_materialDialog_dark : "#0e0c0e",
    orange_materialDialog: "#645e5a",
    orange_materialDialog_dark: "#0f0c09",
    red_materialDialog: "#655e5c",
    red_materialDialog_dark: "#100c0b",
    pink_materialDialog: "#645e5e",
    pink_materialDialog_dark: "#0f0c0c",
    green_materialDialog: "#5f605b",
    green_materialDialog_dark: "#0c0d0a",
    yellow_materialDialog: "#625f59",
    yellow_materialDialog_dark: "#0e0d09",
    mono_materialDialog: "#605f5e",
    mono_materialDialog_dark: "#0d0c0c"
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


