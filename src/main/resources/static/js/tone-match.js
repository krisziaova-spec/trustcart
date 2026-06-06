(function () {
  const uploadButton = document.getElementById('toneUploadBtn');
  const photoInput = document.getElementById('tonePhotoInput');
  const manualSelect = document.getElementById('toneManualSelect');
  const preview = document.getElementById('tonePreview');
  const placeholder = document.getElementById('tonePlaceholder');
  const result = document.getElementById('toneResult');
  const palette = document.getElementById('tonePalette');
  const shopLink = document.getElementById('toneShopLink');

  if (!uploadButton || !photoInput || !manualSelect || !result || !palette || !shopLink) return;

  const toneData = {
    'Spring Warm': {
      label: 'Spring Warm Tone',
      colors: ['#F7C59F', '#FF8A65', '#FFF1C1', '#F6E7C1', '#B7D88A'],
      names: 'peach, coral, ivory, light yellow, warm beige'
    },
    'Summer Cool': {
      label: 'Summer Cool Tone',
      colors: ['#D8B4FE', '#A7C7E7', '#F7B7C8', '#D1D5DB', '#F8FAFC'],
      names: 'lavender, powder blue, soft pink, light gray, pearl white'
    },
    'Autumn Warm': {
      label: 'Autumn Warm Tone',
      colors: ['#708238', '#C19A6B', '#C65D3A', '#D4A017', '#6B4423'],
      names: 'olive, camel, terracotta, mustard, warm brown'
    },
    'Winter Cool': {
      label: 'Winter Cool Tone',
      colors: ['#111827', '#FFFFFF', '#0F2A5F', '#4169E1', '#800020'],
      names: 'black, white, navy, royal blue, burgundy'
    }
  };

  const toneOrder = ['Spring Warm', 'Summer Cool', 'Autumn Warm', 'Winter Cool'];

  function pickDemoTone(fileName) {
    const source = (fileName || String(Date.now())).toLowerCase();
    let score = 0;
    for (let i = 0; i < source.length; i++) score += source.charCodeAt(i);
    return toneOrder[score % toneOrder.length];
  }

  function showTone(toneKey) {
    const data = toneData[toneKey];
    if (!data) return;
    result.innerHTML = '<strong>Your suggested tone: ' + data.label + '</strong><span>Recommended clothing colors: ' + data.names + '.</span>';
    palette.innerHTML = '';
    data.colors.forEach(function (color) {
      const swatch = document.createElement('span');
      swatch.className = 'tone-swatch';
      swatch.style.background = color;
      palette.appendChild(swatch);
    });
    shopLink.textContent = 'Shop ' + data.label + ' Clothes';
    shopLink.href = '/?category=FASHION&q=' + encodeURIComponent(toneKey);
  }

  uploadButton.addEventListener('click', function () {
    photoInput.click();
  });

  photoInput.addEventListener('change', function () {
    const file = photoInput.files && photoInput.files[0];
    if (!file) return;
    if (preview) {
      preview.src = URL.createObjectURL(file);
      preview.hidden = false;
    }
    if (placeholder) placeholder.hidden = true;
    showTone(pickDemoTone(file.name));
  });

  manualSelect.addEventListener('change', function () {
    if (manualSelect.value) showTone(manualSelect.value);
  });
})();
