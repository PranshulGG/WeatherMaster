const darkToggleSwitch = document.getElementById('dark_mode_switch');
const IsDarkOn = localStorage.getItem('ThemeMode');
const currentThemeType = localStorage.getItem('ColorScheme')
const themeNameText = document.getElementById('themeName');

darkToggleSwitch.addEventListener('change', () =>{
    if(darkToggleSwitch.selected){
        document.documentElement.setAttribute('data-theme', 'dark');
        localStorage.setItem('ThemeMode', 'dark')
        checkTHEME();
    } else{
        document.documentElement.setAttribute('data-theme', 'light');
        localStorage.setItem('ThemeMode', 'light')
        checkTHEME();
    }

    themeOverlaySwitch()
});

if(IsDarkOn === 'dark'){
    document.documentElement.setAttribute('data-theme', 'dark');
    darkToggleSwitch.selected = true;
} else{
    document.documentElement.setAttribute('data-theme', 'light');
    darkToggleSwitch.selected = false;
}

const themeOptions = document.querySelectorAll('input[name="themes"]');

const themeOptionsCheck = () => {
    const selectedThemevalue = document.querySelector('input[name="themes"]:checked');
        localStorage.setItem('ColorScheme', selectedThemevalue.value)
        document.documentElement.setAttribute('data-color-theme', selectedThemevalue.value)
        themeNameText.textContent = selectedThemevalue.value;
        checkTHEME();

    themeOverlaySwitch()

    }


themeOptions.forEach(radio => {
    radio.addEventListener('input', themeOptionsCheck);

});


if (currentThemeType) {
    const ThemeradioToCheck = document.querySelector(`input[name="themes"][value="${currentThemeType}"]`);
    document.documentElement.setAttribute('data-color-theme', currentThemeType)
    themeNameText.textContent = currentThemeType;
    
        ThemeradioToCheck.checked = true;

}



function sendThemeToAndroid(theme) {

    AndroidInterface.updateStatusBarColor(theme);
    
    
    };


    // for adjusting scroll
function themeOverlaySwitch(){
    document.querySelector('.theme_overlay').hidden = false;

    const loader = document.getElementById('theme_loader');
    let loaderValue = 0;

    function increaseLoader() {
        loaderValue += 0.3; 

        loaderValue = Math.min(loaderValue, 1);

        loader.setAttribute('value', loaderValue.toString());
    }

    const intervalId = setInterval(increaseLoader, 200);


    setTimeout(() =>{
        document.getElementById('headUser-1').scrollTop = 0;
    }, 800);


    setTimeout(() =>{
        document.querySelector('.theme_overlay').hidden = true;
        loader.setAttribute('value', '0');



        clearInterval(intervalId);
    }, 1000);
}