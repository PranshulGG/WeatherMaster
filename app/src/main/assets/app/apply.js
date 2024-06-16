const themeType = localStorage.getItem('ThemeMode') || 'light';

if (!localStorage.getItem('ThemeMode')) {
    localStorage.setItem('ThemeMode', 'light');

}

const themeColorVariation = localStorage.getItem('ColorScheme') || 'Material You (Blue bright P40)' ;


if (!localStorage.getItem('ColorScheme')) {
    localStorage.setItem('ColorScheme', 'Material You (Blue bright P40)');

}

document.documentElement.setAttribute('data-theme', themeType);
document.documentElement.setAttribute('data-color-theme', themeColorVariation);
