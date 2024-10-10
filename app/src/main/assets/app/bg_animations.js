
function createClearSkyDay(){
  
}


function createRain() {
  const canvas = document.getElementById('bg_animation_rain');
  const ctx = canvas.getContext('2d');
  
  function resizeCanvas() {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
  }
  
  resizeCanvas();
  window.addEventListener('resize', resizeCanvas);
  
  let raindrops = [];
  let animationFrameId;

  class Raindrop {
      constructor(x, y, length, speed) {
          this.x = x;
          this.y = y;
          this.length = length;
          this.speed = speed;
      }

      fall() {
          this.y += this.speed;
          if (this.y > canvas.height) {
              this.y = 0;
              this.x = Math.random() * canvas.width;
          }
      }

      draw() {
          ctx.beginPath();
          ctx.moveTo(this.x, this.y);
          ctx.lineTo(this.x, this.y + this.length);
          ctx.strokeStyle = 'rgba(174,194,224,0.5)';
          ctx.lineWidth = 1;
          ctx.lineCap = 'round';
          ctx.stroke();
      }
  }

  function createRaindrops(count) {
      for (let i = 0; i < count; i++) {
          let x = Math.random() * canvas.width;
          let y = Math.random() * canvas.height;
          let length = Math.random() * 20 + 50;
          let speed = Math.random() * 10 + 15;
          raindrops.push(new Raindrop(x, y, length, speed));
      }
  }

  function animate() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      raindrops.forEach((raindrop) => {
          raindrop.fall();
          raindrop.draw();
      });
      animationFrameId = requestAnimationFrame(animate);
  }

  function clearCanvas() {
      cancelAnimationFrame(animationFrameId);
      raindrops = []; 
      ctx.clearRect(0, 0, canvas.width, canvas.height);
  }

  function startAnimationRain(){  
  createRaindrops(100);
  animate();
  }

  return { clearCanvas, startAnimationRain };
}



// --------------------------------

function createThunder() {
  const canvas = document.getElementById("bg_animation_thunder");
  const context = canvas.getContext('2d');
  const lightningStrikeOffset = 5;
  const lightningStrikeLength = 100;
  const lightningBoltLength = 5;
  const lightningThickness = 4;
  const canvasHeight = canvas.height;
  const canvasWidth = canvas.width;
  
  const createVector = function(x, y) { return { x, y } }
  
  const getRandomFloat = function(min, max) {
      const random = Math.random() * (max - min + 1) + min;
      return random;
  }
  
  const getRandomInteger = function(min, max) {
      return Math.floor(getRandomFloat(min, max)); 
  }
  
  const clearCanvasArea = function(x, y, height, width) {
      let rectX = x || 0;
      let rectY = y || 0;
      let rectHeight = height || canvasHeight;
      let rectWidth = width || canvasWidth;
      context.clearRect(rectX, rectY, rectWidth, rectHeight);
      context.beginPath();
  }
  
  const line = function(start, end, thickness, opacity) {
      context.beginPath();
      context.moveTo(start.x, start.y);
      context.lineTo(end.x, end.y);
      context.lineWidth = thickness;
      context.strokeStyle = `rgba(255, 255, 255, ${opacity})`;
      context.shadowBlur = 30;
      context.shadowColor = "white";
      context.stroke();
      context.closePath();
  }
  
  class Lightning {
      constructor(x1, y1, x2, y2, thickness, opacity) {
          this.start = createVector(x1, y1);
          this.end = createVector(x2, y2);
          this.thickness = thickness;
          this.opacity = opacity;
      }
      draw() {
          return line(this.start, this.end, this.thickness, this.opacity);
      }
  }
  
  const interval = 2000;
  let lightning = [];
  let animationFrameId;
  let intervalId;
  
  const createLightning = function() {
      lightning = [];
      let lightningX1 = getRandomInteger(2, canvasWidth - 2);
      let lightningX2 = getRandomInteger(lightningX1 - lightningStrikeOffset, lightningX1 + lightningStrikeOffset);
      lightning[0] = new Lightning(lightningX1, 0, lightningX2, lightningBoltLength, lightningThickness, 1);
      for (let l = 1; l < lightningStrikeLength; l++) {
          let lastBolt = lightning[l - 1];
          let lx1 = lastBolt.end.x;
          let lx2 = getRandomInteger(lx1 - lightningStrikeOffset, lx1 + lightningStrikeOffset);
          lightning.push(new Lightning(
              lx1, 
              lastBolt.end.y, 
              lx2, 
              lastBolt.end.y + lightningBoltLength, 
              lastBolt.thickness, 
              lastBolt.opacity
          ));
      }
  }
  
  const setup = function() {
      createLightning();
      for (let i = 0 ; i < lightning.length ; i++) {
          lightning[i].draw();
      }
  }
  
  const animate = function() {
      clearCanvasArea();
  
      for (let i = 0 ; i < lightning.length ; i++) {
          lightning[i].opacity -= 0.01;
          lightning[i].thickness -= 0.09;
          if (lightning[i].thickness <= 2) {
              lightning[i].end.y -= 0.09;
          }
          lightning[i].draw();
      }
  
      animationFrameId = requestAnimationFrame(animate);
  }
  
  function startAnimation(){  
  setup();
  animationFrameId = requestAnimationFrame(animate);
  intervalId = setInterval(function() {
      createLightning();
  }, interval);
  }
  function clearCanvas() {
      cancelAnimationFrame(animationFrameId);
      clearInterval(intervalId);
      lightning = [];
      clearCanvasArea();
      context.clearRect(0, 0, canvas.width, canvas.height);
  }

  return { clearCanvas, startAnimation };
}


// -------------

function createClouds() {
  const canvas = document.getElementById('bg_animation_clouds');
  const ctx = canvas.getContext('2d');

  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  let cloudArray = [];
  const numberOfClouds = 6;
  const cloudSpeed = 0.08;
  let animationFrameId;

  function drawRoundedCloud(x, y, width, height, radius) {
      ctx.beginPath();
      ctx.moveTo(x + radius, y);
      ctx.arcTo(x + width, y, x + width, y + height, radius);
      ctx.arcTo(x + width, y + height, x, y + height, radius);
      ctx.arcTo(x, y + height, x, y, radius);
      ctx.arcTo(x, y, x + width, y, radius);
      ctx.closePath();
  }

  class Cloud {
      constructor() {
          this.width = Math.random() * 40 + 60;
          this.height = this.width / 0.7;
          this.x = Math.random() * canvas.width;
          this.y = Math.random() * canvas.height;
          this.speed = cloudSpeed + Math.random() * 0.5;
          this.borderRadius = Math.random() * 5 + 10;
      }

      draw() {
          ctx.shadowColor = 'rgba(255, 255, 255, 0.5)';
          ctx.shadowBlur = 20;
          ctx.shadowOffsetX = 10;
          ctx.shadowOffsetY = 10;
          drawRoundedCloud(this.x, this.y, this.width, this.height, this.borderRadius);
          ctx.fillStyle = 'rgba(255, 255, 255, 0.1)';
          ctx.fill();
          ctx.shadowColor = 'transparent';
      }

      update() {
          this.x += this.speed;
          if (this.x > canvas.width + this.width) {
              this.x = -this.width;
          }
          this.draw();
      }
  }

  function initClouds() {
      cloudArray = [];
      for (let i = 0; i < numberOfClouds; i++) {
          cloudArray.push(new Cloud());
      }
  }

  function animateClouds() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      cloudArray.forEach(cloud => cloud.update());
      animationFrameId = requestAnimationFrame(animateClouds);
  }

  window.addEventListener('resize', () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
      initClouds();
  });

  function startAnimation(){  
  initClouds();
  animateClouds();
  }
  function clearCanvas() {
      cancelAnimationFrame(animationFrameId); 
      cloudArray = []; 
      ctx.clearRect(0, 0, canvas.width, canvas.height); 
  }

  return { clearCanvas, startAnimation };
}


// ------------


function createStars() {
  const canvas = document.getElementById('bg_animation_stars');
  const ctx = canvas.getContext('2d');

  function adjustCanvasSize() {
      const devicePixelRatio = window.devicePixelRatio || 1;
      canvas.width = window.innerWidth * devicePixelRatio;
      canvas.height = window.innerHeight * devicePixelRatio;
      canvas.style.width = `${window.innerWidth}px`;
      canvas.style.height = `${window.innerHeight}px`;
      ctx.scale(devicePixelRatio, devicePixelRatio);
  }

  let starsArray = [];
  const numberOfStars = 150;
  const twinkleInterval = 10000;
  let animationFrameId;

  class Star {
      constructor(isGolden = false) {
          this.x = Math.random() * canvas.width / (window.devicePixelRatio || 1);
          this.y = Math.random() * canvas.height / (window.devicePixelRatio || 1);
          this.size = isGolden ? Math.random() * 2 + 4 : Math.random() * 2 + 1;
          this.color = isGolden ? 'gold' : '#fffbd4';
          this.opacity = Math.random();
          this.isGolden = isGolden;
          this.twinkleTime = Math.random() * twinkleInterval;
      }

      draw() {
          ctx.globalAlpha = this.opacity;
          ctx.beginPath();
          ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
          ctx.fillStyle = this.color;
          ctx.fill();
          ctx.globalAlpha = 1;
      }

      update(deltaTime) {
          this.twinkleTime -= deltaTime;
          if (this.twinkleTime <= 0) {
              this.opacity = Math.random();
              this.twinkleTime = Math.random() * twinkleInterval;
          }
          this.draw();
      }
  }

  function initStars() {
      starsArray = [];
      for (let i = 0; i < numberOfStars; i++) {
          if (i < 2) {
              starsArray.push(new Star(true));
          } else {
              starsArray.push(new Star());
          }
      }
  }

  let lastTime = 0;
  function animateStars(timestamp) {
      const deltaTime = timestamp - lastTime;
      lastTime = timestamp;

      ctx.clearRect(0, 0, canvas.width, canvas.height);
      starsArray.forEach(star => star.update(deltaTime));
      animationFrameId = requestAnimationFrame(animateStars);
  }

  window.addEventListener('resize', () => {
      adjustCanvasSize();
      initStars();
  });

  function startAnimation(){  
  adjustCanvasSize();
  initStars();
  animationFrameId = requestAnimationFrame(animateStars);
  }
  function clearCanvas() {
      cancelAnimationFrame(animationFrameId);
      starsArray = [];
      ctx.clearRect(0, 0, canvas.width, canvas.height);
  }

  return { clearCanvas, startAnimation };
}


// --------------------


function createFog() {
  const canvas = document.getElementById('bg_animation_fog');
  const ctx = canvas.getContext('2d');

  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  let cloudArray = [];
  const numberOfClouds = 5;
  const cloudSpeed = 0.01;
  let animationFrameId;

  function drawRoundedCloud(x, y, width, height, radius) {
      ctx.beginPath();
      ctx.moveTo(x + radius, y);
      ctx.arcTo(x + width, y, x + width, y + height, radius);
      ctx.arcTo(x + width, y + height, x, y + height, radius);
      ctx.arcTo(x, y + height, x, y, radius);
      ctx.arcTo(x, y, x + width, y, radius);
      ctx.closePath();
  }

  class Cloud {
      constructor() {
          this.width = Math.random() * 200 + 100;
          this.height = this.width / 2;
          this.x = Math.random() * canvas.width;
          this.y = Math.random() * canvas.height;
          this.speed = cloudSpeed + Math.random() * 0.5;
          this.borderRadius = Math.random() * 5 + 10;
      }

      draw() {
          ctx.shadowColor = '#99885f80';
          ctx.shadowBlur = 20;
          ctx.shadowOffsetX = 20;
          ctx.shadowOffsetY = 10;
          drawRoundedCloud(this.x, this.y, this.width, this.height, this.borderRadius);
          ctx.fillStyle = '#99885f1a';
          ctx.fill();
          ctx.shadowColor = 'transparent';
      }

      update() {
          this.x += this.speed;
          if (this.x > canvas.width + this.width) {
              this.x = -this.width;
          }
          this.draw();
      }
  }

  function initClouds() {
      cloudArray = [];
      for (let i = 0; i < numberOfClouds; i++) {
          cloudArray.push(new Cloud());
      }
  }

  function animateClouds() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      cloudArray.forEach(cloud => cloud.update());
      animationFrameId = requestAnimationFrame(animateClouds);
  }

  window.addEventListener('resize', () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
      initClouds();
  });

  function startAnimation(){  
  initClouds();
  animateClouds();
  }
  function clearCanvas() {
      cancelAnimationFrame(animationFrameId); 
      cloudArray = []; 
      ctx.clearRect(0, 0, canvas.width, canvas.height); 
  }

  return { clearCanvas, startAnimation };
}


// -------------

function createSnow() {
  const canvas = document.getElementById('bg_animation_snow');
  const ctx = canvas.getContext('2d');

  function adjustCanvasSize() {
      const devicePixelRatio = window.devicePixelRatio || 1;
      canvas.width = window.innerWidth * devicePixelRatio;
      canvas.height = window.innerHeight * devicePixelRatio;
      canvas.style.width = `${window.innerWidth}px`;
      canvas.style.height = `${window.innerHeight}px`;
      ctx.scale(devicePixelRatio, devicePixelRatio);
  }

  let snowflakesArray = [];
  const numberOfSnowflakes = 150;
  let animationFrameId;

  class Snowflake {
      constructor() {
          this.x = Math.random() * canvas.width / (window.devicePixelRatio || 1);
          this.y = Math.random() * canvas.height / (window.devicePixelRatio || 1);
          this.size = Math.random() * 3 + 2;
          this.speedY = Math.random() * 1 + 0.5;
          this.speedX = Math.random() * 1 - 0.5;
          this.opacity = Math.random() * 0.8 + 0.2;
      }

      draw() {
          ctx.globalAlpha = this.opacity;
          ctx.beginPath();
          ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
          ctx.fillStyle = 'white';
          ctx.fill();
          ctx.globalAlpha = 1;
      }

      update() {
          this.y += this.speedY;
          this.x += this.speedX;

          if (this.y > canvas.height / (window.devicePixelRatio || 1)) {
              this.y = -this.size;
              this.x = Math.random() * canvas.width / (window.devicePixelRatio || 1);
          }

          if (this.x > canvas.width / (window.devicePixelRatio || 1)) {
              this.x = -this.size;
          } else if (this.x < -this.size) {
              this.x = canvas.width / (window.devicePixelRatio || 1);
          }

          this.draw();
      }
  }

  function initSnowflakes() {
      snowflakesArray = [];
      for (let i = 0; i < numberOfSnowflakes; i++) {
          snowflakesArray.push(new Snowflake());
      }
  }

  function animateSnowflakes() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      snowflakesArray.forEach(snowflake => snowflake.update());
      animationFrameId = requestAnimationFrame(animateSnowflakes);
  }

  window.addEventListener('resize', () => {
      adjustCanvasSize();
      initSnowflakes();
  });

  function startAnimation(){  
    adjustCanvasSize();
  initSnowflakes();
  animateSnowflakes();
  }
  
  function clearCanvas() {
      cancelAnimationFrame(animationFrameId);
      snowflakesArray = [];
      ctx.clearRect(0, 0, canvas.width, canvas.height);
  }

  return { clearCanvas, startAnimation };
}



const snowAnimation = createSnow();
const RainFallAnimation = createRain();
const cloudsAnimation = createClouds();
const thunderAnimation = createThunder();
const starsAnimation = createStars();
const fogAnimation = createFog();



// display animations


function displayRain(){
  RainFallAnimation.startAnimationRain()
}

function displaySnow(){
  snowAnimation.startAnimation()
}

function displayClouds(){
  cloudsAnimation.startAnimation()
}

function displayStars(){
  starsAnimation.startAnimation()
}

function displayFog(){
  fogAnimation.startAnimation()
}

function displayThunder(){
  thunderAnimation.startAnimation()
}

// remove animations


function removeRain(){
  RainFallAnimation.clearCanvas()
}

function removeSnow(){
  snowAnimation.clearCanvas()
}

function removeClouds(){
  cloudsAnimation.clearCanvas()
}

function removeStars(){
  starsAnimation.clearCanvas()
}

function removeFog(){
  fogAnimation.clearCanvas()
}

function removeThunder(){
  thunderAnimation.clearCanvas()
}
