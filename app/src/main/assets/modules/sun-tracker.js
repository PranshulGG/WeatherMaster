function moveSun(percent) {
    const circle = document.querySelector('.sun_tracker');

    if (percent === 10) {
      circle.style.transform = `translate(10px, -12px)`; 
    } else if (percent === 20) {
      circle.style.transform = `translate(20px, -20px)`; 
    } else if (percent === 30) {
        circle.style.transform = `translate(30px, -30px)`; 
    } else if (percent === 40) {
        circle.style.transform = `translate(40px, -40px)`; 
    } else if (percent === 50) {
        circle.style.transform = `translate(62px, -45px)`; 
    } else if (percent === 60) {
        circle.style.transform = `translate(80px, -40px)`; 
    } else if (percent === 70) {
        circle.style.transform = `translate(95px, -32px)`; 
    } else if (percent === 80) {
        circle.style.transform = `translate(110px, -20px)`; 
    } else if (percent === 90) {
        circle.style.transform = `translate(125px, -8px)`; 
    } else if (percent === 100) {
        circle.style.transform = `translate(133px, -0px)`; 
            
    } else {
      const block = document.querySelector('.block');
      const blockWidth = block.offsetWidth;
      const blockHeight = block.offsetHeight;
      
      const t = percent / 100;

      const x = t * blockWidth;
      const y = blockHeight * (1 - 4 * (t - 0.5) ** 2);

      circle.style.transform = `translate(${x}px, ${y - 15}px)`;
    }
  }


// toggle summary

async function openSummery(){
    document.querySelector('.summeryText').classList.toggle('openSummery');

    if(document.querySelector('.summeryText').classList.contains('openSummery')){
        document.querySelector('#arrow_up_toggle').innerHTML = 'keyboard_arrow_up'
        document.getElementById('lock_summary').style.width = '24px';


    } else{
        document.querySelector('#arrow_up_toggle').innerHTML = 'keyboard_arrow_down';
        document.getElementById('lock_summary').style.width = '0';



    }


}
// moon

function moveMoon(percent) {
    const circle = document.querySelector('.moon_tracker');

    if (percent === 10) {
      circle.style.transform = `translate(10px, -12px)`;
    } else if (percent === 20) {
      circle.style.transform = `translate(20px, -20px)`;
    } else if (percent === 30) {
        circle.style.transform = `translate(30px, -30px)`;
    } else if (percent === 40) {
        circle.style.transform = `translate(40px, -40px)`;
    } else if (percent === 50) {
        circle.style.transform = `translate(62px, -45px)`;
    } else if (percent === 60) {
        circle.style.transform = `translate(80px, -40px)`;
    } else if (percent === 70) {
        circle.style.transform = `translate(95px, -32px)`;
    } else if (percent === 80) {
        circle.style.transform = `translate(110px, -20px)`;
    } else if (percent === 90) {
        circle.style.transform = `translate(125px, -8px)`;
    } else if (percent === 100) {
        circle.style.transform = `translate(133px, -0px)`;

    } else {
      const block = document.querySelector('.block');
      const blockWidth = block.offsetWidth;
      const blockHeight = block.offsetHeight;

      const t = percent / 100;

      const x = t * blockWidth;
      const y = blockHeight * (1 - 4 * (t - 0.5) ** 2);

      circle.style.transform = `translate(${x}px, ${y - 15}px)`;
    }
  }

//   --------

async function lockSummaryToggle(){


 document.getElementById('lock_summary').classList.toggle('lockedSummaryClass')

 if(document.getElementById('lock_summary').classList.contains('lockedSummaryClass')){
    document.getElementById('lock_summary').innerHTML = 'lock'
    document.getElementById('lock_summary').style.color = 'var(--Secondary)'
    await customStorage.setItem('SummaryLocked', 'true');

 } else{

 document.getElementById('lock_summary').innerHTML = 'lock_open'
 document.getElementById('lock_summary').style.color = ''
 await customStorage.setItem('SummaryLocked', 'false');
 setTimeout(() =>{
    document.getElementById('lock_summary').style.width = '0';
    openSummery()
 }, 100)

 }



}

async function ChecklockSummaryToggle(){


    if(await customStorage.getItem('SummaryLocked') === 'true'){
        document.getElementById('lock_summary').innerHTML = 'lock'
        document.getElementById('lock_summary').style.color = 'var(--Secondary)'
        await customStorage.setItem('SummaryLocked', 'true');
        document.getElementById('lock_summary').style.width = '24px';
        document.getElementById('lock_summary').classList.add('lockedSummaryClass')

        openSummery()

    } else{
        await customStorage.setItem('SummaryLocked', 'false');
        document.getElementById('lock_summary').innerHTML = 'lock_open'
        document.getElementById('lock_summary').style.color = ''
        document.getElementById('lock_summary').style.width = '0';

    }


   }

   ChecklockSummaryToggle()