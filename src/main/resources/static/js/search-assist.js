(function () {
  const smartSearch = document.getElementById('smartSearch');
  const productHints = [
    { pattern: /earbud|earphone|headphone|airpod/i, term: 'Wireless Earbuds' },
    { pattern: /power\s*bank|battery\s*pack|portable\s*charger/i, term: 'Power Bank' },
    { pattern: /speaker|bluetooth/i, term: 'Bluetooth Speaker' },
    { pattern: /charger|type\s*c|usb/i, term: 'Fast Charger' },
    { pattern: /phone\s*case|case/i, term: 'Phone Case' },
    { pattern: /tempered|glass|screen/i, term: 'Tempered Glass' },
    { pattern: /shirt|tshirt|t-shirt|top|cotton/i, term: 'Shirt' },
    { pattern: /pants|denim|jeans/i, term: 'Denim Pants' },
    { pattern: /tote|canvas|shopping\s*bag|bag/i, term: 'Tote Bag' },
    { pattern: /toothbrush|bamboo/i, term: 'Bamboo Toothbrush' },
    { pattern: /facial|wash|cleanser/i, term: 'Facial Wash' },
    { pattern: /sunscreen|spf/i, term: 'Sunscreen' },
    { pattern: /container|lunch\s*box|food\s*box/i, term: 'Food Container' },
    { pattern: /lamp|light/i, term: 'Desk Lamp' },
    { pattern: /organizer|storage|basket/i, term: 'Organizer' },
    { pattern: /adobo|meal|lunch|pancit|kakanin|sinigang/i, term: 'meal' },
    { pattern: /rice/i, term: 'Rice' },
    { pattern: /coffee|beans/i, term: 'Coffee' },
    { pattern: /sugar|muscovado/i, term: 'Muscovado Sugar' },
    { pattern: /band|resistance/i, term: 'Resistance Band' },
    { pattern: /bottle|water/i, term: 'Water Bottle' },
    { pattern: /wipes|baby/i, term: 'Baby Wipes' },
    { pattern: /puzzle|toy/i, term: 'Toy' },
    { pattern: /yoga|mat/i, term: 'Yoga Mat' },
    { pattern: /rope|jump/i, term: 'Jump Rope' },
    { pattern: /towel/i, term: 'Towel' },
    { pattern: /notebook|paper/i, term: 'Notebook' },
    { pattern: /pen|ballpen/i, term: 'Ballpen' },
    { pattern: /calculator/i, term: 'Calculator' },
    { pattern: /pet|shampoo|dog|cat/i, term: 'Pet' },
    { pattern: /straw/i, term: 'Metal Straw' },
    { pattern: /trash|compost/i, term: 'Compostable Trash Bags' },
    { pattern: /pouch|woven|handwoven/i, term: 'Pouch' },
    { pattern: /tablea|chocolate/i, term: 'Tablea Chocolate' },
    { pattern: /organic|vegetable|banana|coconut|malunggay/i, term: 'Organic' },
    { pattern: /soap|vinegar|sponge|baking\s*soda/i, term: 'organic cleaning' }
  ];

  function setStatus(form, message) {
    const status = form.querySelector('[data-assist-status]') || document.querySelector('[data-assist-status]');
    if (status) status.textContent = message;
  }

  function submitWithMode(form, term, mode) {
    const query = form.querySelector('.search-input');
    const modeInput = form.querySelector('.search-mode-input');
    const category = form.querySelector('select[name="category"]');
    if (query && term) query.value = term;
    if (modeInput) modeInput.value = mode;
    if (category && mode !== 'text') category.value = '';
    if (form.requestSubmit) form.requestSubmit();
    else form.submit();
  }

  function revealImagePanel() {
    if (!smartSearch) return;
    const panel = smartSearch.querySelector('[data-image-search-panel]');
    if (panel) panel.hidden = false;
    smartSearch.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  function hideImagePanelIfEmpty() {
    if (!smartSearch) return;
    const panel = smartSearch.querySelector('[data-image-search-panel]');
    const fileInput = smartSearch.querySelector('.image-search-file');
    if (panel && (!fileInput || !fileInput.files || !fileInput.files.length)) {
      panel.hidden = true;
    }
  }

  function openImagePanel(openFilePicker) {
    if (!smartSearch) return;
    const fileInput = smartSearch.querySelector('.image-search-file');
    if (openFilePicker && fileInput) {
      fileInput.click();
      return;
    }
    revealImagePanel();
  }

  function inferImageTerm(fileName, promptText) {
    const raw = [promptText || '', fileName || ''].join(' ').replace(/[_\-]+/g, ' ');
    const direct = raw.trim().replace(/\.[a-z0-9]+$/i, '').trim();
    for (const hint of productHints) {
      if (hint.pattern.test(raw)) return hint.term;
    }
    return direct || 'eco product';
  }

  function handleImageSearch(form) {
    const file = form.querySelector('.image-search-file')?.files?.[0];
    const prompt = form.querySelector('.image-search-prompt')?.value || '';
    const term = inferImageTerm(file ? file.name : '', prompt);
    const imageQueryInput = form.querySelector('.image-query-input');
    if (imageQueryInput) imageQueryInput.value = term;
    setStatus(form, 'Image-assisted search clue: ' + term);
    submitWithMode(form, term, 'image');
  }

  document.querySelectorAll('.image-search-toggle').forEach(function (button) {
    button.addEventListener('click', function () {
      openImagePanel(true);
    });
  });

  document.querySelectorAll('.image-search-submit').forEach(function (button) {
    button.addEventListener('click', function () {
      const form = button.closest('[data-search-form]');
      handleImageSearch(form);
    });
  });

  document.querySelectorAll('.image-search-file').forEach(function (input) {
    input.addEventListener('change', function () {
      const form = input.closest('[data-search-form]');
      const file = input.files && input.files[0];
      const preview = form.querySelector('.image-search-preview');
      const fileName = form.querySelector('[data-image-file-name]');
      if (file) {
        revealImagePanel();
      } else {
        hideImagePanelIfEmpty();
      }
      if (file && preview) {
        preview.src = URL.createObjectURL(file);
        preview.hidden = false;
      } else if (preview) {
        preview.hidden = true;
        preview.removeAttribute('src');
      }
      if (fileName) fileName.textContent = file ? file.name : '';
      setStatus(form, file ? 'Selected image: ' + file.name : 'No image selected.');
    });
  });
})();
