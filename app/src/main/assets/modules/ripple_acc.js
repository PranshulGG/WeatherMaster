
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


  document.addEventListener('DOMContentLoaded', () =>{



    const customOpenAnimation = {
      dialog: [
          [
              // Dialog fades in
              [{ 'transform': 'scale(1.1)' }, { 'transform': 'scale(1)' }],
              { duration: 150, easing: 'ease-out' },
          ],
      ],
      scrim: [
          [
              // Scrim fades in
              [{ opacity: 0 }, { opacity: 0.6 }],
              { duration: 170, easing: 'ease-in' },
          ],
      ],
      container: [
          [
              // Container fades in
              [{ opacity: 0 }, { opacity: 1 }],
              { duration: 170, easing: 'ease-in' },
          ],
      ],
  };

  const customCloseAnimation = {
      dialog: [
          [
              // Dialog fades out
              [{ 'transform': 'scale(1)' }, { 'transform': 'scale(1.1)' }],
              { duration: 190, easing: 'ease-in' },
          ],
      ],
      scrim: [
          [
              // Scrim fades out
              [{ opacity: 0.6 }, { opacity: 0 }],
              { duration: 190, easing: 'ease-out' },
          ],
      ],
      container: [
          [
              [{ opacity: 1 }, { opacity: 0 }],
              { duration: 190, easing: 'ease-in' },
          ],
      ],
  };

  const dialogs = document.querySelectorAll('md-dialog');

  dialogs.forEach((dialog) => {
      dialog.getOpenAnimation = () => customOpenAnimation;
      dialog.getCloseAnimation = () => customCloseAnimation;
  });


  });


