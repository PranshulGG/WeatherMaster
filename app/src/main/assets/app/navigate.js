



window.transitionToPage = function(href) {

     document.querySelector('body').style.opacity = '0';

    setTimeout(function() { 
        window.location.href = href

    }, 500);
     // delay from 2000ms to 500ms
}


document.addEventListener('DOMContentLoaded', function(event) {

    document.querySelector('body').style.opacity = '1';

});
