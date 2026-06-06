(function () {
  const fallbackSrc = '/img/products/placeholder-product.png';

  function isMissingImage(img) {
    const src = img.getAttribute('src');
    return !src || src.trim() === '' || src === window.location.href || src === window.location.pathname;
  }

  function setFallback(img) {
    if (!img || img.dataset.fallbackApplied === 'true') return;
    img.dataset.fallbackApplied = 'true';
    img.src = fallbackSrc;
  }

  function repairImage(img) {
    if (!img || img.tagName !== 'IMG') return;
    if (isMissingImage(img)) {
      setFallback(img);
      return;
    }
    if (img.complete && img.naturalWidth === 0) {
      setFallback(img);
    }
  }

  document.addEventListener('error', function (event) {
    if (event.target && event.target.tagName === 'IMG') {
      setFallback(event.target);
    }
  }, true);

  function scanImages() {
    document.querySelectorAll('img').forEach(repairImage);
  }

  document.addEventListener('DOMContentLoaded', scanImages);
  window.addEventListener('load', scanImages);
})();
