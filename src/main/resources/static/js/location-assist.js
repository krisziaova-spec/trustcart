(function () {
  const form = document.querySelector('[data-location-form]');
  if (!form) return;

  const preset = document.getElementById('locationPreset');
  const cityInput = document.getElementById('marketCity');
  const latInput = document.getElementById('latitude');
  const lonInput = document.getElementById('longitude');
  const radiusInput = document.getElementById('radiusKm');
  const currentBtn = document.getElementById('useCurrentLocation');
  const status = document.getElementById('locationAssistStatus');
  const map = document.getElementById('marketMap');
  const mapCity = document.getElementById('marketMapCity');
  const mapRadius = document.getElementById('marketMapRadius');

  function setStatus(message) {
    if (status) status.textContent = message;
  }

  function numberValue(input, fallback) {
    const parsed = parseFloat(input && input.value);
    return Number.isFinite(parsed) ? parsed : fallback;
  }

  function updateMap() {
    const lat = numberValue(latInput, 14.0683);
    const lon = numberValue(lonInput, 121.3256);
    const radius = numberValue(radiusInput, 5);
    const delta = Math.max(0.03, radius / 80);
    const bbox = [lon - delta, lat - delta, lon + delta, lat + delta].map(v => v.toFixed(4)).join('%2C');
    if (map) {
      map.src = `https://www.openstreetmap.org/export/embed.html?bbox=${bbox}&layer=mapnik&marker=${lat.toFixed(4)}%2C${lon.toFixed(4)}`;
      map.dataset.lat = lat.toFixed(4);
      map.dataset.lon = lon.toFixed(4);
    }
    if (mapCity) mapCity.textContent = cityInput.value || 'Selected search area';
    if (mapRadius) mapRadius.textContent = `${radius} km radius`;
  }

  function applyPreset() {
    if (!preset || !preset.value) return;
    const parts = preset.value.split('|');
    if (parts.length < 3) return;
    cityInput.value = parts[0];
    latInput.value = parts[1];
    lonInput.value = parts[2];
    setStatus('Saved city selected. Click Apply Search Area to filter sellers.');
    updateMap();
  }

  function setCurrentLocation(position, autoSubmit) {
    const coords = position.coords || {};
    if (!Number.isFinite(coords.latitude) || !Number.isFinite(coords.longitude)) return;
    cityInput.value = 'My current location';
    latInput.value = coords.latitude.toFixed(4);
    lonInput.value = coords.longitude.toFixed(4);
    if (preset) preset.value = '';
    setStatus('Current location detected. Click Apply Search Area to use it for nearby seller filtering.');
    updateMap();
    if (autoSubmit) form.submit();
  }

  function requestCurrentLocation(autoSubmit) {
    if (!navigator.geolocation) {
      setStatus('Current location is not supported by this browser. Please select a city manually.');
      return;
    }
    setStatus('Reading current location. Please allow browser permission when prompted.');
    navigator.geolocation.getCurrentPosition(
      position => setCurrentLocation(position, autoSubmit),
      () => setStatus('Location permission was not allowed. You can still select a city manually.'),
      { enableHighAccuracy: false, timeout: 8000, maximumAge: 300000 }
    );
  }

  if (preset) preset.addEventListener('change', applyPreset);
  if (currentBtn) currentBtn.addEventListener('click', () => requestCurrentLocation(false));
  [cityInput, latInput, lonInput, radiusInput].forEach(el => el && el.addEventListener('change', updateMap));

  updateMap();

  const shouldAutoRead = form.dataset.autoReadLocation === 'true';
  const alreadyAsked = localStorage.getItem('trustcartLocationPrompted') === 'true';
  if (shouldAutoRead && !alreadyAsked && navigator.geolocation) {
    localStorage.setItem('trustcartLocationPrompted', 'true');
    window.setTimeout(() => requestCurrentLocation(false), 700);
  }
})();
