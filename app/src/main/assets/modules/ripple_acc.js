
document.addEventListener('DOMContentLoaded', function () {
    var addripple = document.querySelectorAll('.ripple_btn');

    addripple.forEach(function (element) {
        var createRippleForOne = document.createElement('md-ripple');
        element.appendChild(createRippleForOne);
        element.style.position = 'relative';
    });
});

document.addEventListener('DOMContentLoaded', function () {
    var addrippleDark = document.querySelectorAll('.ripple_btn_dark');

    addrippleDark.forEach(function (elementDark) {
        var createRippleForOneDark = document.createElement('md-ripple');
        elementDark.appendChild(createRippleForOneDark);
        elementDark.style.position = 'relative';
        createRippleForOneDark.style = '--md-ripple-pressed-color: var(--Inverse-On-Surface);'
    });
});

document.addEventListener('DOMContentLoaded', function () {
    var addrippleDarkFixed = document.querySelectorAll('.ripple_btn_dark_fix');

    addrippleDarkFixed.forEach(function (elementDarkFixed) {
        var createRippleForOneDarkFixed = document.createElement('md-ripple');
        elementDarkFixed.appendChild(createRippleForOneDarkFixed);
        elementDarkFixed.style.position = 'relative';
        createRippleForOneDarkFixed.style = '--md-ripple-pressed-color: black';
    });
});



document.addEventListener('DOMContentLoaded', function () {
    var addripplelow = document.querySelectorAll('.ripple_btn_low');

    addripplelow.forEach(function (elementrippleLow) {
        var createRippleForOnelow = document.createElement('md-ripple');
        elementrippleLow.appendChild(createRippleForOnelow);
        elementrippleLow.style.position = 'relative';
        createRippleForOnelow.style = '--md-ripple-pressed-opacity: 0.13;'
    });
});

const useSysFontPages = localStorage.getItem('SelectedAPPfont');

if(useSysFontPages === 'roboto'){
    document.documentElement.setAttribute('sys-font', 'roboto');
} else{
    document.documentElement.setAttribute('sys-font', ' ');
}

const useFontSizePages = localStorage.getItem('SelectedAPPfontSize');



if (useFontSizePages === 'large_fontSize') {
    document.documentElement.setAttribute("sys-font-size", "large_fontSize");
  } else if (useFontSizePages === 'medium_fontSize') {
    document.documentElement.setAttribute("sys-font-size", "medium_fontSize");
  } else {
    document.documentElement.setAttribute("sys-font-size", "");
  }
