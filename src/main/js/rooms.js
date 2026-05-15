import { initRoomDeleteButtons } from './api/rooms-api.js';

document.addEventListener('DOMContentLoaded', () => {
  // Room list only needs the shared delete-button behavior
  initRoomDeleteButtons();
});