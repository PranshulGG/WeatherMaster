function playEffect(button) {
    button.classList.add('touched');

    button.classList.remove('touchedfade');
    
}
function playEffect2(button) {



   setTimeout(()=>{
    button.classList.add('touchedfade');
         }, 200);

   setTimeout(()=>{
        button.classList.remove('touched');
         button.classList.remove('touchedfade');
         }, 300);
}






