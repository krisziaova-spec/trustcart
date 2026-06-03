# TrustCart Search Assist Update

Added voice search and prototype image-assisted search to the buyer homepage.

## What changed

- Added microphone buttons to the sticky header search and main product search form.
- Added an image search panel in the main search card.
- Added `/js/search-assist.js` for browser-side voice and image-search interactions.
- Expanded backend search to match product name, description, sustainability tag, alt text, origin, seller name, seller city, and seller province.
- Added `searchMode` and `imageQuery` request parameters to preserve whether a query came from text, voice, or image-assisted search.

## Current prototype behavior

Voice search uses the browser SpeechRecognition API, so it works best in Chrome/Edge and may not be supported in all browsers.

Image search is prototype-safe: the uploaded image is previewed in the browser and is not saved. Matching is based on the image file name plus the optional user prompt, mapped against the product catalogue search fields. For production-grade visual similarity, connect the image upload to an AI/vision or embedding service.
