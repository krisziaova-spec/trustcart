# TrustCart Image Picker UI Hotfix

This hotfix updates the buyer image search flow:

- Removed visible native `Choose File / No file chosen` controls from the search UI.
- Removed the extra `Choose Photo` button from the image search panel.
- Clicking the camera / Image Search button now opens the browser file picker immediately.
- On mobile, the browser should offer available photo options such as photo library or camera capture depending on the device/browser.
- The image search details panel appears only after a photo is selected.
- The optional description and Find Similar Products button remain available after image selection.

No SQL rerun is required for this UI-only update.
