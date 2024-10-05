// 3dot- menu
// const Menu = document.getElementById('menu');

// function toggleMenu(){
//     Menu.style.display = 'block'; 
//     Menu.style.transition = 'none';
//     Menu.style.opacity = '1';
// }

// window.addEventListener("click", function(event) {
//     if (event.target == Menu) {
//         Menu.style.opacity = '0';
//         Menu.style.transition = 'opacity 0.2s';
//         setTimeout(() =>{
//             Menu.style.display = "none";
//         }, 200);
      
//     }
//   });

//   const eq = document.querySelectorAll('.menu-btn');


//   eq.forEach(function(eq) {
//     eq.addEventListener('click', () => {
//         Menu.style.opacity = '0';
//         Menu.style.transition = 'opacity 0.2s';
//         setTimeout(() =>{
//             Menu.style.display = "none";
//         }, 200);
//       });



//   });

// removed menu


//   border-change 

const changeBorderButtons = document.querySelectorAll('.change-border');

changeBorderButtons.forEach(function(changeBorderButton) {
    changeBorderButton.addEventListener('touchstart', () => {
        const borderRadiusStart = changeBorderButton.dataset.borderStart;
        changeBorderButton.style.borderRadius = `${borderRadiusStart}px`;
    });

    changeBorderButton.addEventListener('touchend', () => {
        const borderRadiusEnd = changeBorderButton.dataset.borderEnd;
        changeBorderButton.style.borderRadius = `${borderRadiusEnd}px`;
    });
});

changeBorderButtons.forEach(function(changeBorderButtons) {
    changeBorderButtons.style.transition = 'all 0.25s ease-out';
});



// dialog


function openModal(modalId) {
    var modal = document.querySelector('[data-modal-id="' + modalId + '"]');
    modal.style.display = "flex";
    modal.style.opacity = "1";
}


function closeModal(modalId) {
    var modal = document.querySelector('[data-modal-id="' + modalId + '"]');

    modal.style.opacity = '0';

    setTimeout(() =>{
        modal.style.display = "none";
    }, 200);

}

window.onclick = function(event) {
    var modals = document.getElementsByClassName('modalRegular');
    for (var i = 0; i < modals.length; i++) {
        if (event.target === modals[i]) {
            modals[i].style.opacity = '0';
            
         
            (function(index) {
                setTimeout(function() {
                    modals[index].style.display = "none";
                }, 200);
            })(i);
        }
    }
};



document.addEventListener('click', function(event) {
    if (event.target.hasAttribute('data-modal-open')) {
        var modalId = event.target.getAttribute('data-modal-open');
        openModal(modalId);
    }

    if (event.target.hasAttribute('data-modal-close')) {
        var modalId = event.target.getAttribute('data-modal-close');
        closeModal(modalId);
    }
});



      //snack-bar

      function ShowSnack(message, timeout, bottom, type, inside, useValue) {

        var snackbarDiv = document.createElement("div");
        snackbarDiv.id = "snackbar";
        snackbarDiv.style.opacity = '0';
        snackbarDiv.style.display = 'flex';
        snackbarDiv.classList.add(type)

        if(useValue){
            snackbarDiv.setAttribute('value', useValue)
        }
    
        snackbarDiv.style.setProperty('--snackbar-bottom', bottom + '%');
    
        var snackbarDivP = document.createElement("p");
        snackbarDivP.innerHTML = message;
        snackbarDiv.appendChild(snackbarDivP);
    
    
        var existingSnackbar = document.getElementById("snackbar");
        if (existingSnackbar) {
    
            existingSnackbar.parentNode.removeChild(existingSnackbar);
        }
    
        var AppendinsideElement = document.getElementById(inside);

        if (AppendinsideElement) {
            AppendinsideElement.appendChild(snackbarDiv);

            if(snackbarDiv.getAttribute('value') === 'no-up'){
             
            } else{
                document.querySelector('.fab_container').classList.add('adjust');
            }

        } else {
            document.body.appendChild(snackbarDiv);
            console.log('body')
        }
    

        // document.getElementById(inside).appendChild(snackbarDiv);
    

        setTimeout(() => {
            snackbarDiv.style.transition = 'opacity 0.2s, bottom 0.3s ease-in';
            snackbarDiv.style.opacity = '1';
        }, 10);
    
    
        setTimeout(() => {
            snackbarDiv.style.bottom = bottom + '3%';
            snackbarDiv.style.opacity = '0';
   
    
            setTimeout(() => {
        
                snackbarDiv.parentNode.removeChild(snackbarDiv);
                if(snackbarDiv.getAttribute('value') === 'no-up'){
             
                } else{
                    document.querySelector('.fab_container').classList.remove('adjust');
                }

            }, 200);
        }, timeout);
    }
    
    
    
    function showSnackbarAction(options) {
        const existingSnackbar = document.querySelector('.snackbar');
    
        if (existingSnackbar) {
            existingSnackbar.remove();
        }
    
        const snackbar = document.createElement('div');
        snackbar.classList.add('snackbar');
        snackbar.style.setProperty('--snackbaraction-bottom', options.bottomPosition + '%');

    
        const snackbarContent = document.createElement('div');
        snackbarContent.classList.add('snackbar-content');
    
        const snackbarText = document.createElement('span');
        snackbarText.textContent = options.text || '';
    
        const actionButton = document.createElement('md-text-button');
        actionButton.style.zIndex = '999'
        actionButton.classList.add('snackbar-action' );

        actionButton.setAttribute('style', '--md-text-button-label-text-color: var(--Inverse-Primary); --md-text-button-pressed-state-layer-color: var(--Inverse-Primary); --md-text-button-hover-label-text-color: var(--Inverse-Primary); --md-text-button-pressed-label-text-color: var(--Inverse-Primary); --md-text-button-focus-label-text-color: var(--Inverse-Primary);')

        actionButton.textContent = options.actionText || 'Action';
        actionButton.setAttribute('data-modal-open', options.modalId || '');
        if (options.onActionClick) {
            if (typeof options.onActionClick === 'function') {
                actionButton.onclick = () => {
                    options.onActionClick(snackbar);
                    dismissSnackbar(snackbar);
                };
            } else if (typeof options.onActionClick === 'string') {
                actionButton.setAttribute('onclick', `${options.onActionClick}; dismissSnackbar(this.parentNode.parentNode)`);
            }
        }
        snackbarContent.appendChild(actionButton);
        snackbarContent.appendChild(snackbarText);
      
        snackbar.appendChild(snackbarContent);
    
        document.body.appendChild(snackbar);
    
        snackbar.style.display = 'flex';
    
        if (options.manualDismiss) {
            snackbar.addEventListener('click', () =>{snackbar.remove();})
            snackbar.dataset.manualDismiss = true;
        } else {
            
            setTimeout(() => {
                snackbar.style.opacity = 0;
    
                setTimeout(() => {
                    snackbar.remove();
                }, 300);
            }, options.timeout || 3000);
        }
    }
    
    function dismissSnackbar(snackbar) {
        if (snackbar.dataset.manualDismiss === 'true') {
            snackbar.style.opacity = 0;
    
            setTimeout(() => {
                snackbar.remove();
            }, 300);
        }
    }
      


// full-activity-large-header

const LargeActivityContents = document.querySelectorAll('.activity-large-content');
const HiddenLabels = document.querySelectorAll('.label-hidden-large');
const LargeLabelsMain = document.querySelectorAll('.label-large-main');
const LargeActivityLabels = document.querySelectorAll('.header-activity-large');
const speedFactor = 2;

LargeActivityContents.forEach((LargeActivityContent, index) => {
    let opacity = 0;
    const triggerHeight = 100;
    const triggerHeight2 = 100;


    let hasTriggered = false;
    let hasTriggered2 = false;


    LargeActivityContent.addEventListener('scroll', () => {
        const scrollPosition = LargeActivityContent.scrollTop;

        const t = Math.min(1, scrollPosition / triggerHeight);

        const lerp = (start, end, t) => (1 - t) * start + t * end;

        opacity = lerp(0, 1, t);

        const redOpacity = lerp(0, 1, opacity);
        const redOpacity2 = lerp(0, 0.6, opacity);

        HiddenLabels[index].style.opacity = opacity.toString();
        LargeLabelsMain[index].style.opacity = (1 - opacity).toString();
        LargeLabelsMain[index].style.backgroundColor = `rgba(var(--Top-bar), ${redOpacity2})`;

    
      
    });
});




// openActivity

const BackgroundApp = document.getElementById('App');

function openActivity(activityNumber, activityContent) {

    closeAllModals();
    const OpenModal = document.querySelector(`[data-modal-${activityNumber}]`);
    const OpenedContent = document.querySelector(`[activity-content-${activityContent}]`);

    BackgroundApp.style.transform = 'translateX(-30%)';
    BackgroundApp.style.opacity = '0';
    OpenModal.style.display = 'block';
    OpenModal.style.opacity = '1';
    OpenModal.style.transform = 'none';
    OpenedContent.scrollTop = 0;

}

function closeActivity(activityNumber) {

    const modal = document.querySelector(`[data-modal-${activityNumber}]`);

  
    modal.style.transform = 'translateX(30%)';
    modal.style.opacity = '0';
    BackgroundApp.style.transform = 'none';
    BackgroundApp.style.opacity = '1';


    setTimeout(() => {
        modal.style.display = 'none';
    }, 400);
}

function closeAllModals() {

    document.querySelectorAll('.full-activity-large').forEach(modal => {
        modal.style.display = 'none';
    });
}



//   buttons

const ripplebuttonclass = document.querySelectorAll('button-filled, button-outlined, button-tonal, text-button, fab, fab-small, fab-large, button-filled-i, button-outlined-i, button-tonal-i, text-button-i');


ripplebuttonclass.forEach(function(ripplebuttonclass) {
   
      ripplebuttonclass.classList.add('md-ripples');



  });


//   loader

const circleLoaders = document.querySelectorAll('circle-loader');

circleLoaders.forEach(circleLoader => {
  const size = circleLoader.getAttribute('data-size');
  const Swidth = circleLoader.getAttribute('data-thick');

  const svgElement = document.createElementNS("http://www.w3.org/2000/svg", "svg");
  svgElement.setAttribute('viewBox', '0 0 32 32');
  svgElement.setAttribute('height', size);
  svgElement.setAttribute('width', size);


  const circleElement = document.createElementNS("http://www.w3.org/2000/svg", "circle");
  circleElement.setAttribute('id', 'spinner');
  circleElement.setAttribute('cx', '16');
  circleElement.setAttribute('cy', '16');
  circleElement.setAttribute('r', '14');
  circleElement.setAttribute('fill', 'none');
  circleElement.setAttribute('stroke-width', Swidth)


  svgElement.appendChild(circleElement);

  circleLoader.appendChild(svgElement);
});





// Border-change usage ---

/**
 * Data-attributes
 * data-border-start="20" data-border-end="50"
 */



