const img = document.getElementById('frame') as HTMLImageElement;
const fpsEl = document.getElementById('fps')!;
const resEl = document.getElementById('res')!;

// Replace with base64 processed frame. For quick demo, tiny transparent png used.
const sampleBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=";
img.src = sampleBase64;

img.onload = () => {
  resEl.textContent = `${img.naturalWidth}x${img.naturalHeight}`;
}

let last = performance.now();
let count = 0;
function tick() {
  const now = performance.now();
  count++;
  if (now - last >= 1000) {
    fpsEl.textContent = (count).toString();
    count = 0;
    last = now;
  }
  requestAnimationFrame(tick);
}
tick();
