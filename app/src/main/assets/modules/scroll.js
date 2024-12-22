const weatherWrap = document.getElementById('weather_wrap');
const mainWeat = document.querySelector('.main-weat');
const maxHeight = 180;
const minHeight = 0;
const maxOpacity = 1;
const minOpacity = 0;

let ticking = false;

if(weatherWrap){
weatherWrap.addEventListener('scroll', function() {
    if (!ticking) {
        window.requestAnimationFrame(() => {
            const scrollPosition = weatherWrap.scrollTop;
            const newHeight = Math.max(minHeight, maxHeight - scrollPosition);
            const newOpacity = Math.max(minOpacity, maxOpacity - (scrollPosition / maxHeight));
//            mainWeat.style.opacity = `${newOpacity}`;
//            mainWeat.style.opacity = newOpacity;
                        document.querySelector('.froggie').style.opacity = newOpacity;

                                              if(scrollPosition > 100){
                                                    document.querySelector('.header-top').classList.add('use');

                                                } else{
                                                    document.querySelector('.header-top').classList.remove('use');

                                                }
            ticking = false;
        });
        ticking = true;
    }
});}


// -------


    async function loadSwipeGestures() {
    const weatherWrap = document.getElementById('weather_wrap');
    const scrollableContainers = document.querySelectorAll('.noSwipe');
    const savedLocationsFromStorage  = JSON.parse(localStorage.getItem("savedLocations")) || [];
    const Locations = JSON.parse(localStorage.getItem("DefaultLocation"));

        if(weatherWrap){
    const savedLocations = [...savedLocationsFromStorage];
    if (Locations?.name === "CurrentDeviceLocation") {
        savedLocations.unshift({ locationName: "CurrentDeviceLocation", lat: null, lon: null });
    }

    let currentIndex = 0;
    let startX = 0;
    let startY = 0;
    let isSwiping = false;
    const swipeThreshold = await customStorage.getItem('ThresholdSwipeValue') || 100;

    // Check if a touch event occurs inside a scrollable container
    function isInsideScrollableContainer(target) {
        return Array.from(scrollableContainers).some((container) =>
            container.contains(target)
        );
    }


    function isInsideScrollableContainerEnd(target) {
        const scrollableContainers = document.querySelectorAll('.noSwipe');

        return Array.from(scrollableContainers).some((container) =>
            container.contains(target)
        );
    }

        const indicatorContainer = document.getElementById('indicatorContainer');

        indicatorContainer.innerHTML = ''

        savedLocations.forEach((location, index) => {
            const ball = document.createElement('div');
            ball.classList.add('indicator-ball');
            ball.dataset.index = index;
            indicatorContainer.appendChild(ball);
        });



    let debounceTimer;
    let debounceTimer2;
    let debounceTimer3


    function updateIndicator() {
        const balls = document.querySelectorAll('.indicator-ball');
        balls.forEach(ball => ball.classList.remove('active'));
        if (balls[currentIndex]) {
            balls[currentIndex].classList.add('active');
        }

        document.querySelector('.floating_indications').hidden = false

        clearTimeout(debounceTimer2);

        debounceTimer2 = setTimeout(() => {
        document.querySelector('.floating_indications').style.bottom = '0'
        document.querySelector('.floating_indications').style.opacity = '0'

        }, 3000);

        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
        document.querySelector('.floating_indications').hidden = true
        document.querySelector('.floating_indications').style.bottom = ''
        document.querySelector('.floating_indications').style.opacity = ''
        }, 3200);

        clearTimeout(debounceTimer3);
        debounceTimer3 = setTimeout(() => {
            document.querySelector('swipeCooldown').hidden = true;
        }, 2500);
    }


    weatherWrap.addEventListener('touchstart', async (e) => {
            if(await customStorage.getItem('useGestureLocation') && await customStorage.getItem('useGestureLocation') === true){
        const touch = e.touches[0];

        // If touch starts inside a scrollable container, don't process swipe
        if (isInsideScrollableContainer(touch.target)) {
            isSwiping = false; // Prevent swipe detection
            return;
        }

        startX = touch.clientX;
        startY = touch.clientY;
        isSwiping = true; // Allow swipe detection
        }
    });

    weatherWrap.addEventListener('touchmove', async (e) => {
        if(await customStorage.getItem('useGestureLocation') && await customStorage.getItem('useGestureLocation') === true){
        if (!isSwiping) return; // Ignore if not swiping

        const touch = e.touches[0];
        const diffX = touch.clientX - startX;
        const diffY = touch.clientY - startY;

        // If vertical movement is greater, treat it as a scroll
        if (Math.abs(diffY) > Math.abs(diffX)) {
            isSwiping = false;
            return;
        }

        // If horizontal movement exceeds threshold, prevent scrolling
        if (Math.abs(diffX) > swipeThreshold) {
            e.preventDefault(); // Prevent default scrolling
        }
        }
    });

    weatherWrap.addEventListener('touchend', async (e) => {
        if(await customStorage.getItem('useGestureLocation') && await customStorage.getItem('useGestureLocation') === true){
        const touch = e.changedTouches[0];

                    if (isInsideScrollableContainerEnd(touch.target)) {
                        console.log("Touch end inside scrollable container, disabling swipe.");
                        isSwiping = false; // Prevent swipe detection
                        return;
                    }

        if (!isSwiping) return;

        const diffX = touch.clientX - startX;

        if (diffX > swipeThreshold) {
            let nextIndex = (currentIndex + 1) % savedLocations.length;

            // Ensure the next location is not the same as the current location
            while (savedLocations[nextIndex]?.locationName === savedLocations[currentIndex]?.locationName) {
                nextIndex = (nextIndex + 1) % savedLocations.length;
            }

            currentIndex = nextIndex;


            if(savedLocations[currentIndex]?.locationName === 'CurrentDeviceLocation'){
                ReturnHomeLocation()
                 document.getElementById("city-name").innerHTML = getTranslationByLang(localStorage.getItem("AppLanguageCode"), "current_location");
            } else{
                LoadLocationOnRequest(savedLocations[currentIndex]?.lat, savedLocations[currentIndex]?.lon, savedLocations[currentIndex]?.locationName)
            document.getElementById("city-name").innerHTML = savedLocations[currentIndex]?.locationName;

            }

          document.getElementById("forecast").scrollLeft = 0;
          document.getElementById("weather_wrap").scrollTop = 0;
                  localStorage.setItem("CurrentLocationName", savedLocations[currentIndex]?.locationName);

            updateIndicator()
        document.querySelector('swipeCooldown').hidden = false;

        }
        // Move to previous location on swipe left
        else if (diffX < -swipeThreshold) {
            let prevIndex = (currentIndex - 1 + savedLocations.length) % savedLocations.length;

            // Ensure the previous location is not the same as the current location
            while (savedLocations[prevIndex]?.locationName === savedLocations[currentIndex]?.locationName) {
                prevIndex = (prevIndex - 1 + savedLocations.length) % savedLocations.length;
            }

            currentIndex = prevIndex;


            if(savedLocations[currentIndex]?.locationName === 'CurrentDeviceLocation'){
                ReturnHomeLocation()
            document.getElementById("city-name").innerHTML = getTranslationByLang(localStorage.getItem("AppLanguageCode"), "current_location");

            } else{
                LoadLocationOnRequest(savedLocations[currentIndex]?.lat, savedLocations[currentIndex]?.lon, savedLocations[currentIndex]?.locationName)
            document.getElementById("city-name").innerHTML = savedLocations[currentIndex]?.locationName;

            }

            document.getElementById("forecast").scrollLeft = 0;
            document.getElementById("weather_wrap").scrollTop = 0;
              localStorage.setItem("CurrentLocationName", savedLocations[currentIndex]?.locationName);
            updateIndicator()
        document.querySelector('swipeCooldown').hidden = false;

        }




        isSwiping = false; // Reset
    }
    });
}
        }




document.addEventListener('DOMContentLoaded', async () => {
loadSwipeGestures()
})