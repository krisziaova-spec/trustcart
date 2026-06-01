const photoInput = document.getElementById('photoInput');
const userPhoto = document.getElementById('userPhoto');
const overlay = document.getElementById('garmentOverlay');
const placeholder = document.getElementById('placeholder');
const scaleRange = document.getElementById('scaleRange');
const rotateRange = document.getElementById('rotateRange');
const resetBtn = document.getElementById('resetBtn');
let pos = {x: 50, y: 85};
let dragging = false;
let offset = {x: 0, y: 0};

function updateOverlay(){
  overlay.style.left = pos.x + '%';
  overlay.style.top = pos.y + 'px';
  overlay.style.width = scaleRange.value + 'px';
  overlay.style.transform = `translateX(-50%) rotate(${rotateRange.value}deg)`;
}
photoInput?.addEventListener('change', e => {
  const file = e.target.files[0];
  if(!file) return;
  const reader = new FileReader();
  reader.onload = ev => { userPhoto.src = ev.target.result; userPhoto.style.display='block'; placeholder.style.display='none'; };
  reader.readAsDataURL(file);
});
function selectGarment(btn){
  const item = btn.closest('.tryon-item');
  overlay.src = item.dataset.asset;
  overlay.style.display = 'block';
  pos = {x: 50, y: 100};
  scaleRange.value = item.dataset.asset.includes('pants') || item.dataset.asset.includes('skirt') ? 130 : 145;
  rotateRange.value = 0;
  updateOverlay();
  document.querySelectorAll('.tryon-item').forEach(x=>x.classList.remove('selected'));
  item.classList.add('selected');
}
function showGender(gender){
  document.querySelectorAll('.tab').forEach(t=>t.classList.remove('active'));
  event.target.classList.add('active');
  document.querySelectorAll('.tryon-item').forEach(item=>{ item.style.display = item.dataset.gender === gender ? 'grid' : 'none'; });
}
scaleRange?.addEventListener('input', updateOverlay);
rotateRange?.addEventListener('input', updateOverlay);
resetBtn?.addEventListener('click', () => { pos={x:50,y:100}; scaleRange.value=145; rotateRange.value=0; updateOverlay(); });
overlay?.addEventListener('pointerdown', e => { dragging=true; overlay.setPointerCapture(e.pointerId); offset.x=e.clientX; offset.y=e.clientY; });
overlay?.addEventListener('pointermove', e => {
  if(!dragging) return;
  const stage = document.getElementById('stage').getBoundingClientRect();
  pos.x = ((e.clientX - stage.left) / stage.width) * 100;
  pos.y = e.clientY - stage.top;
  updateOverlay();
});
overlay?.addEventListener('pointerup', () => dragging=false);
document.addEventListener('DOMContentLoaded', () => showGender('MEN'));
