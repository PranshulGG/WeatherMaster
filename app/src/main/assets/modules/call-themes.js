



function checkSearchScroll(){
    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('search-blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('search-purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('search-yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('search-green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('search-red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('search-pink-dark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)') {
            sendThemeToAndroid('search-charcol-dark');
          } else if (currentColorTheme === 'Material You (Hint orange 33)') {
            sendThemeToAndroid('search-orange-dark');

        } else if (currentColorTheme === 'Material You (Blue bright P40)') {
            sendThemeToAndroid('search-blueBright-dark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('search-blue-dark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('search-blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('search-purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('search-yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('search-green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
            sendThemeToAndroid('search-red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('search-pink-light');

          } else if (currentColorTheme === 'Material You (Deep charcoal 83)') {
            sendThemeToAndroid('search-charcol-light');

          } else if (currentColorTheme === 'Material You (Hint orange 33)') {
            sendThemeToAndroid('search-orange-light');

        } else if (currentColorTheme === 'Material You (Blue bright P40)') {
            sendThemeToAndroid('search-blueBright-light');
        }
         else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('search-blue-light');
        }

    }
}

// scrollMain

function checkMainScroll(){
    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('scroll-blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('scroll-purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('scroll-yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('scroll-green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('scroll-red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('scroll-pink-dark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)') {
            sendThemeToAndroid('scroll-charcol-dark');

          } else if (currentColorTheme === 'Material You (Hint orange 33)') {
            sendThemeToAndroid('scroll-orange-dark');


        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('scroll-blue-dark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('scroll-blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('scroll-purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('scroll-yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('scroll-green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
            sendThemeToAndroid('scroll-red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('scroll-pink-light');

          } else if (currentColorTheme === 'Material You (Deep charcoal 83)') {
            sendThemeToAndroid('scroll-charcol-light');

          } else if (currentColorTheme === 'Material You (Hint orange 33)') {
            sendThemeToAndroid('scroll-orange-light');
        }
         else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('scroll-blue-light');
        }

    }
}



// ActivityColors

function ActivityColor(){
    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('Activitybluedark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('Activitypurpledark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('Activityyellowdark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('Activitygreendark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('Activityreddark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('ActivityPinkdark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('ActivityCharcoldark');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('ActivityOrangedark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('Activitybluedark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('Activitybluelight');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('Activitypurplelight');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('Activityyellowlight');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('Activitygreenlight');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
            sendThemeToAndroid('Activityredlight');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('ActivityPinklight');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('ActivityCharcollight');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('ActivityOrangelight');
        }
         else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('Activitybluelight');
        }

    }
}

// ActivityColorsScrolled

function ActivityColorScroll(){
    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('activityScroll-blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('activityScroll-purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('activityScroll-yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('activityScroll-green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('activityScroll-red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('activityScroll-pink-dark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('activityScroll-charcol-dark');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('activityScroll-orange-dark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('activityScroll-blue-dark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('activityScroll-blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('activityScroll-purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('activityScroll-yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('activityScroll-green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
            sendThemeToAndroid('activityScroll-red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('activityScroll-pink-light');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('activityScroll-charcol-light');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('activityScroll-orange-light');
        }
         else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('activityScroll-blue-light');
        }

    }
}


// dialog

function dialogcolor(){
    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('dialog-blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('dialog-purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('dialog-yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('dialog-green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('dialog-red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('dialog-pink-dark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('dialog-charcoal-dark');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('dialog-orange-dark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('dialog-blue-dark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('dialog-blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('dialog-purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('dialog-yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('dialog-green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('dialog-red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('dialog-pink-light');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('dialog-charcoal-light');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('dialog-orange-light');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('dialog-blue-light');
        }

    }
}


// dialogInverted

function dialogcolorInverted(){
    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('dialogInverted-blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('dialogInverted-purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('dialogInverted-yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('dialogInverted-green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('dialogInverted-red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('dialogInverted-pink-dark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('dialogInverted-charcoal-dark');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('dialogInverted-orange-dark');
        } else if (currentColorTheme === 'Material You (Blue bright P40)') {
            sendThemeToAndroid('dialogInverted-blue-dark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('dialogInverted-blue-dark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('dialogInverted-blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('dialogInverted-purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('dialogInverted-yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('dialogInverted-green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('dialogInverted-red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('dialogInverted-pink-light');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('dialogInverted-charcoal-light');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('dialogInverted-orange-light');

        } else if (currentColorTheme === 'Material You (Blue bright P40)') {
            sendThemeToAndroid('dialogInverted-blue-light');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('dialogInverted-blue-light');
        }

    }
}


function dialogcolorFull(){
    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('dialogActvity-blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('dialogActvity-purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('dialogActvity-yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('dialogActvity-green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('dialogActvity-red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('dialogActvity-pink-dark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('dialogActvity-charcoal-dark');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('dialogActvity-orange-dark');

        } else if (currentColorTheme === 'Material You (Blue bright P40)') {
            sendThemeToAndroid('dialogActvity-blue-dark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('dialogActvity-blue-dark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('dialogActvity-blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('dialogActvity-purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('dialogActvity-yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('dialogActvity-green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('dialogActvity-red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('dialogActvity-pink-light');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('dialogActvity-charcoal-light');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('dialogActvity-orange-light');

        } else if (currentColorTheme === 'Material You (Blue bright P40)') {
            sendThemeToAndroid('dialogActvity-blue-light');
        } 
        else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('dialogActvity-blue-light');
        }

    }
}

// main

function checkTHEME(){

    const currentTheme = localStorage.getItem('ThemeMode');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('pink-dark');
           } else if (currentColorTheme === 'Material You (Deep charcoal 83)') {
               sendThemeToAndroid('charcol-dark');

           } else if (currentColorTheme === 'Material You (Hint orange 33)') {
               sendThemeToAndroid('orange-dark');

            } else if (currentColorTheme === 'Material You (Blue bright P40)') {
                sendThemeToAndroid('blue-bright-dark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('blue-dark');

        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
            sendThemeToAndroid('red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('pink-light');
           } else if (currentColorTheme === 'Material You (Deep charcoal 83)') {
               sendThemeToAndroid('charcol-light');

           } else if (currentColorTheme === 'Material You (Hint orange 33)') {
               sendThemeToAndroid('orange-light');

            } else if (currentColorTheme === 'Material You (Blue bright P40)') {
                sendThemeToAndroid('blue-bright-light');
        }
        
         else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('blue-light');

        }

    }
}


function sendThemeToAndroid(theme) {

    AndroidInterface.updateStatusBarColor(theme);

    
};

// sheetColor

function sheetcolorInverted(){
    const currentTheme = localStorage.getItem('theme');
    const currentColorTheme = localStorage.getItem('ColorScheme');

    if (currentTheme === 'dark') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('sheetInverted-blue-dark');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('sheetInverted-purple-dark');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('sheetInverted-yellow-dark');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('sheetInverted-green-dark');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('sheetInverted-red-dark');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('sheetInverted-pink-dark');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('sheetInverted-charcoal-dark');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('sheetInverted-orange-dark');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('sheetInverted-blue-dark');
        }

    } else if (currentTheme === 'light') {

        if (currentColorTheme === 'Material You (Berry pop blue 44)') {
            sendThemeToAndroid('sheetInverted-blue-light');
        } else if (currentColorTheme === 'Material You (Raisin purple 100)') {
            sendThemeToAndroid('sheetInverted-purple-light');
        } else if (currentColorTheme === 'Material You (Olive pop green 49)') {
            sendThemeToAndroid('sheetInverted-yellow-light');

        } else if (currentColorTheme === 'Material You (Forest green 33)') {
            sendThemeToAndroid('sheetInverted-green-light');

        } else if (currentColorTheme === 'Material You (Chestnut cool red 122)') {
          sendThemeToAndroid('sheetInverted-red-light');
        } else if (currentColorTheme === 'Material You (Raspberry pink P99)') {
            sendThemeToAndroid('sheetInverted-pink-light');
          } else if (currentColorTheme === 'Material You (Deep charcoal 83)'){
            sendThemeToAndroid('sheetInverted-charcoal-light');

          } else if (currentColorTheme === 'Material You (Hint orange 33)'){
            sendThemeToAndroid('sheetInverted-orange-light');

        } else if (currentColorTheme === 'Material You (pop_blue_default)'){
            sendThemeToAndroid('sheetInverted-blue-light');
        }

    }
}

// cool